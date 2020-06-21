package app;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.*;

import servent.message.AskForPointsMessage;
import servent.message.AskGetMessage;
import servent.message.PutMessage;
import servent.message.WelcomeMessage;
import servent.message.util.MessageUtil;

/**
 * This class implements all the logic required for Chord to function.
 * It has a static method <code>chordHash</code> which will calculate our chord ids.
 * It also has a static attribute <code>CHORD_SIZE</code> that tells us what the maximum
 * key is in our system.
 * <p>
 * Other public attributes and methods:
 * <ul>
 *   <li><code>chordLevel</code> - log_2(CHORD_SIZE) - size of <code>successorTable</code></li>
 *   <li><code>successorTable</code> - a map of shortcuts in the system.</li>
 *   <li><code>predecessorInfo</code> - who is our predecessor.</li>
 *   <li><code>valueMap</code> - DHT values stored on this node.</li>
 *   <li><code>init()</code> - should be invoked when we get the WELCOME message.</li>
 *   <li><code>isCollision(int chordId)</code> - checks if a servent with that Chord ID is already active.</li>
 *   <li><code>isKeyMine(int key)</code> - checks if we have a key locally.</li>
 *   <li><code>getNextNodeForKey(int key)</code> - if next node has this key, then return it, otherwise returns the nearest predecessor for this key from my successor table.</li>
 *   <li><code>addNodes(List<ServentInfo> nodes)</code> - updates the successor table.</li>
 *   <li><code>putValue(int key, int value)</code> - stores the value locally or sends it on further in the system.</li>
 *   <li><code>getValue(int key)</code> - gets the value locally, or sends a message to get it from somewhere else.</li>
 * </ul>
 *
 * @author bmilojkovic
 */
public class ChordState {

    public static int CHORD_SIZE;

    public static int chordHash(int value) {
        return 61 * value % CHORD_SIZE;
    }

    private int chordLevel; //log_2(CHORD_SIZE)

    private ServentInfo[] successorTable;
    private ServentInfo predecessorInfo;

    //we DO NOT use this to send messages, but only to construct the successor table
    private List<ServentInfo> allNodeInfo;

    private Map<Integer, Integer> valueMap;

    private Map<String, String> fractalsIds;

    public ChordState() {
        this.chordLevel = (int) (Math.log(AppConfig.SERVENT_COUNT) / Math.log(2));
        fractalsIds = new HashMap<>();
        fractalsIds.put("0", "0");

        successorTable = new ServentInfo[1];
        successorTable[0] = null;

        predecessorInfo = null;
        valueMap = new HashMap<>();
        allNodeInfo = new ArrayList<>();
    }

    /**
     * This should be called once after we get <code>WELCOME</code> message.
     * It sets up our initial value map and our first successor so we can send <code>UPDATE</code>.
     * It also lets bootstrap know that we did not collide.
     */
    public void init(WelcomeMessage welcomeMsg) {
        //set a temporary pointer to next node, for sending of update message
        successorTable[0] = welcomeMsg.getSuccessorInfo();
        fractalsIds = welcomeMsg.getFractalIds();


        //tell bootstrap this node is not a collider
        try {
            Socket bsSocket = new Socket(AppConfig.BOOTSTRAP_IP, AppConfig.BOOTSTRAP_PORT);

            PrintWriter bsWriter = new PrintWriter(bsSocket.getOutputStream());
            bsWriter.write("New\n" + AppConfig.myServentInfo.getListenerPort() + "\n" + AppConfig.myServentInfo.getChordId() + "\n" + AppConfig.myServentInfo.getIpAddress() + "\n");

            bsWriter.flush();
            bsSocket.close();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public int getChordLevel() {
        return chordLevel;
    }

    public ServentInfo[] getSuccessorTable() {
        return successorTable;
    }

    public int getNextNodePort() {
        return successorTable[0].getListenerPort();
    }

    public String getNextNodeIp() {
        return successorTable[0].getIpAddress();
    }

    public ServentInfo getPredecessor() {
        return predecessorInfo;
    }

    public void setPredecessor(ServentInfo newNodeInfo) {
        this.predecessorInfo = newNodeInfo;
    }

    public Map<Integer, Integer> getValueMap() {
        return valueMap;
    }

    public void setValueMap(Map<Integer, Integer> valueMap) {
        this.valueMap = valueMap;
    }

    public boolean isCollision(int chordId) {
        if (chordId == AppConfig.myServentInfo.getChordId()) {
            return true;
        }
        for (ServentInfo serventInfo : allNodeInfo) {
            if (serventInfo.getChordId() == chordId) {
                return true;
            }
        }
        return false;
    }

    /**
     * Returns true if we are the owner of the specified key.
     */
    public boolean isKeyMine(int key) {
        if (predecessorInfo == null) {
            return true;
        }

        int predecessorChordId = predecessorInfo.getChordId();
        int myChordId = AppConfig.myServentInfo.getChordId();

        if (predecessorChordId < myChordId) { //no overflow
            if (key <= myChordId && key > predecessorChordId) {
                return true;
            }
        } else { //overflow
            if (key <= myChordId || key > predecessorChordId) {
                return true;
            }
        }

        return false;
    }

    /**
     * Main chord operation - find the nearest node to hop to to find a specific key.
     * We have to take a value that is smaller than required to make sure we don't overshoot.
     * We can only be certain we have found the required node when it is our first next node.
     */
    public ServentInfo getNextNodeForKey(int key) {
        if (AppConfig.myServentInfo.getChordId() == key) {
            return AppConfig.myServentInfo;
        }

        int previousId = successorTable[0].getChordId();

        if (previousId == key) {
            return successorTable[0];
        }


        for (int i = 1; i < successorTable.length; i++) {
            if (successorTable[i] == null) {
                AppConfig.timestampedErrorPrint("Couldn't find successor for " + key);
                break;
            }

            int successorId = successorTable[i].getChordId();

            if (successorId == key) {
                return successorTable[i];
            }

            if (AppConfig.myServentInfo.getChordId() < key) {

                if (successorId >= key) {

                    return successorTable[i - 1];

                }
                if (key > previousId && successorId < previousId) { //overflow
                    return successorTable[i - 1];
                }

            } else {

                if (successorId < AppConfig.myServentInfo.getChordId() && successorId >= key) {
                    return successorTable[i - 1];
                }

                if (key > previousId && successorId < previousId) { //overflow
                    return successorTable[i - 1];
                }

            }

            previousId = successorId;
        }
        //if we have only one node in all slots in the table, we might get here
        //then we can return any item
        return successorTable[0];
    }

    private void updateSuccessorTable() {
        //first node after me has to be successorTable[0]
        if (allNodeInfo.size() == 0) {
            successorTable = new ServentInfo[1];
            successorTable[0] = null;
            return;
        }
        int currentNodeIndex = 0;
        ServentInfo currentNode = allNodeInfo.get(currentNodeIndex);
        int currentIncrement = 2;
        chordLevel = (int) (Math.log(allNodeInfo.size() + 1) / Math.log(2));
        if (chordLevel == 0) {
            successorTable = new ServentInfo[1];
        } else {
            successorTable = new ServentInfo[chordLevel];
        }
        successorTable[0] = currentNode;
        for (int i = 1; i < chordLevel; i++, currentIncrement *= 2) {
            successorTable[i] = allNodeInfo.get(currentIncrement - 1);
        }
    }

    public void resetFractalsTable() {
        Map<String, String> newFractalsTable = new HashMap<>();
        fractalsIds.forEach((key, value) -> {
            if (key.equals("0")) {
                newFractalsTable.put(key, "0");
            } else {
                newFractalsTable.put(key, "idle");
            }
        });
        fractalsIds = newFractalsTable;
    }

    public void updateFractalsTable() {
        if (AppConfig.myServentInfo.getChordId() != 0) {
            fractalsIds.put(String.valueOf(AppConfig.myServentInfo.getChordId()), "idle");
        }
        calculateFractals(fractalsIds, AppConfig.myFractalCreatorInfo.getPointCount());
        System.out.println("fractalIds " + fractalsIds);
    }

    public Map<String, String> createNewFractalTable(Map<String, String> table) {
        calculateFractals(table, AppConfig.myFractalCreatorInfo.getPointCount());
        return table;
    }

    public static void calculateFractals(Map<String, String> map, int pointCount) {
        int idleCount = 0;
        while (true) {
            idleCount = 0;
            for (Map.Entry<String, String> entry : map.entrySet()) {
                if (entry.getValue().equals("idle")) {
                    idleCount++;
                }
            }
            if (idleCount < pointCount-1) {
                break;
            } else {
                int counter = 0;
                String node = findMinPrefix(map);
                for (Map.Entry<String, String> entry1 : map.entrySet()) {
                    if (entry1.getValue().equals("idle")) {
                        entry1.setValue(node.split(" ")[1] + (counter+1));
                        counter++;
                    }
                    if (counter == pointCount-1)
                        break;
                }
                map.put(node.split(" ")[0], node.split(" ")[1]+0);
            }

        }
    }

    public static String findMinPrefix(Map<String, String> map) {
        int min = Integer.MAX_VALUE;
        String fractalId = "0";
        String nodeId = "0";
        for (Map.Entry<String, String> entry : map.entrySet()) {
            if (entry.getValue().length() < min) {
                min = entry.getValue().length();
                fractalId = entry.getValue();
                nodeId = entry.getKey();
            }
        }
        return nodeId + " " +fractalId;
    }

    public void removeFractalIdForKey(String deleteKey) {
        Map<String, String> newFractalIds = new HashMap<>();
        fractalsIds.forEach((key, value) -> {
            if (!key.equals(deleteKey)) {
                newFractalIds.put(key, value);
            }
        });
        fractalsIds = newFractalIds;
    }

    public void emptyFractalTable() {
        Map<String, String> newFractalIds = new HashMap<>();
        newFractalIds.put("0", "0");
        fractalsIds = newFractalIds;
    }

    public void askForPoints() {
        if (AppConfig.myFractalCreatorInfo.isWorking() && !fractalsIds.get(String.valueOf(AppConfig.myServentInfo.getChordId())).equals("idle")) {
            String myFractalId = fractalsIds.get(String.valueOf(AppConfig.myServentInfo.getChordId()));
            List<String> nodes = new ArrayList<>();
            Map<String, String> nodeMap = new HashMap<>();
            for (Map.Entry<String, String> entry : fractalsIds.entrySet()) {
                if (Integer.parseInt(entry.getKey()) != AppConfig.myServentInfo.getChordId()
                        && entry.getValue().startsWith(myFractalId.substring(0, myFractalId.length() - 1))) {
                    nodes.add(entry.getKey() + " " + entry.getValue());
                    nodeMap.put(entry.getKey(), entry.getValue());
                }
            }
            ServentInfo serventInfo = findNodeToAsk(nodeMap);
            Map<String, String> other = new HashMap<>();
            for (Map.Entry<String, String> entry : nodeMap.entrySet()) {
                if (!entry.getKey().equals(String.valueOf(serventInfo.getChordId()))) {
                    other.put(entry.getKey(), entry.getValue());
                }
            }
            other.put(String.valueOf(AppConfig.myServentInfo.getChordId()), fractalsIds.get(String.valueOf(AppConfig.myServentInfo.getChordId())));
            AskForPointsMessage afpm = new AskForPointsMessage(AppConfig.myServentInfo.getListenerPort(), getNextNodeForKey(serventInfo.getChordId()).getListenerPort());
            afpm.setReceiverInfo(serventInfo);
            afpm.setFractals(other);
            afpm.setReceiverIp(getNextNodeForKey(serventInfo.getChordId()).getIpAddress());
            MessageUtil.sendMessage(afpm);
        }
    }

    public ServentInfo findNodeToAsk(Map<String, String> map) {
        int min = Integer.MAX_VALUE;
        for (Map.Entry<String, String> entry : map.entrySet()) {
            int id = Integer.parseInt(entry.getKey());
            if (id < min) {
                min = id;
            }
        }
        ServentInfo servent = null;
        for (ServentInfo serventInfo : allNodeInfo) {
            if (serventInfo.getChordId() == min) {
                servent = serventInfo;
                break;
            }
        }
        return servent;
    }

    /**
     * This method constructs an ordered list of all nodes. They are ordered by chordId, starting from this node.
     * Once the list is created, we invoke <code>updateSuccessorTable()</code> to do the rest of the work.
     */
    public void addNodes(List<ServentInfo> newNodes) {
        allNodeInfo.addAll(newNodes);
        allNodeInfo.sort(new Comparator<ServentInfo>() {

            @Override
            public int compare(ServentInfo o1, ServentInfo o2) {
                return o1.getChordId() - o2.getChordId();
            }

        });
        List<ServentInfo> newList = new ArrayList<>();
        List<ServentInfo> newList2 = new ArrayList<>();

        int myId = AppConfig.myServentInfo.getChordId();
        for (ServentInfo serventInfo : allNodeInfo) {
            if (serventInfo.getChordId() < myId) {
                newList2.add(serventInfo);
            } else {
                newList.add(serventInfo);
            }
        }

        allNodeInfo.clear();
        allNodeInfo.addAll(newList);
        allNodeInfo.addAll(newList2);

        updateSuccessorTable();
    }

    /**
     * The Chord put operation. Stores locally if key is ours, otherwise sends it on.
     */
    public void putValue(int key, int value) {
        if (isKeyMine(key)) {
            valueMap.put(key, value);
        } else {
            ServentInfo nextNode = getNextNodeForKey(key);
            PutMessage pm = new PutMessage(AppConfig.myServentInfo.getListenerPort(), nextNode.getListenerPort(), key, value);
            MessageUtil.sendMessage(pm);
        }
    }

    /**
     * The chord get operation. Gets the value locally if key is ours, otherwise asks someone else to give us the value.
     *
     * @return <ul>
     * <li>The value, if we have it</li>
     * <li>-1 if we own the key, but there is nothing there</li>
     * <li>-2 if we asked someone else</li>
     * </ul>
     */
    public int getValue(int key) {
        if (isKeyMine(key)) {
            if (valueMap.containsKey(key)) {
                return valueMap.get(key);
            } else {
                return -1;
            }
        }

        ServentInfo nextNode = getNextNodeForKey(key);
        AskGetMessage agm = new AskGetMessage(AppConfig.myServentInfo.getListenerPort(), nextNode.getListenerPort(), String.valueOf(key));
        MessageUtil.sendMessage(agm);

        return -2;
    }

    public List<ServentInfo> getAllNodeInfo() {
        return allNodeInfo;
    }

    public Map<String, String> getFractalsIds() {
        return fractalsIds;
    }

    public void setFractalsIds(Map<String, String> fractalsIds) {
        this.fractalsIds = fractalsIds;
    }

    public void clearAllNodes() {
        allNodeInfo.clear();
    }
}
