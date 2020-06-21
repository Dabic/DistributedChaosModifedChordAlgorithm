package cli.command;

import app.AppConfig;
import app.ServentInfo;
import servent.message.SendPointsQuitMessage;
import servent.message.TransferPointsQuitMessage;
import servent.message.UpdateFractalsQuitMessage;
import servent.message.util.MessageUtil;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;

public class MyQuitCommand implements CLICommand {
    @Override
    public String commandName() {
        return "quit";
    }

    @Override
    public void execute(String args) {
        if (args != null) {
            AppConfig.timestampedErrorPrint("Invalid arguments for result command.");
        } else {
            try {
                Socket bsSocket = new Socket(AppConfig.BOOTSTRAP_IP, AppConfig.BOOTSTRAP_PORT);
                PrintWriter bsWriter = new PrintWriter(bsSocket.getOutputStream());
                bsWriter.write("Quit\n" + AppConfig.myServentInfo.getChordId() + "\n");
                bsWriter.flush();
                Scanner bsScanner = new Scanner(bsSocket.getInputStream());
                String msg = bsScanner.nextLine();
                if (msg.equals("Ok")) {
                    String fractalId = AppConfig.chordState.getFractalsIds().get(String.valueOf(AppConfig.myServentInfo.getChordId()));
                    if (AppConfig.chordState.getFractalsIds().size() == 1) {
                        System.exit(0);
                        return;
                    }
                    if (fractalId.equals("idle") || !AppConfig.myFractalCreatorInfo.isWorking()) {
                        int counter = 0;
                        Map<String, String> oldFractalTable = AppConfig.chordState.getFractalsIds();
                        Map<String, String> newFractalTable = new HashMap<>();
                        for (Map.Entry<String, String> entry : oldFractalTable.entrySet()) {
                            if (Integer.parseInt(entry.getKey()) != AppConfig.myServentInfo.getChordId()) {
                                newFractalTable.put(String.valueOf(counter++), "idle");
                            }
                        }
                        newFractalTable.put("0", "0");
                        System.out.println(AppConfig.chordState.createNewFractalTable(newFractalTable));
                        forIdle(newFractalTable);
                    } else {
                        if (AppConfig.myFractalCreatorInfo.isWorking()) {
                            Map<String, String> oldFractalTable = AppConfig.chordState.getFractalsIds();
                            Map<String, String> newFractalTable = new HashMap<>();
                            Map<String, String> temp = new HashMap<>();
                            List<ServentInfo> allNodes = AppConfig.chordState.getAllNodeInfo();
                            int counter = 0;
                            for (Map.Entry<String, String> entry : oldFractalTable.entrySet()) {
                                if (Integer.parseInt(entry.getKey()) != AppConfig.myServentInfo.getChordId()) {
                                    newFractalTable.put(String.valueOf(counter++), "idle");
                                }
                            }
                            newFractalTable.put("0", "0");
                            boolean doit = true;
                            for (Map.Entry<String, String> entry : oldFractalTable.entrySet()) {
                                if (Integer.parseInt(entry.getKey()) != AppConfig.myServentInfo.getChordId()) {
                                    if (doit) {
                                        temp.put(entry.getKey(), "0");
                                        doit = false;
                                    } else {
                                        temp.put(entry.getKey(), "idle");
                                    }
                                }
                            };
                            AppConfig.chordState.createNewFractalTable(temp);
                            System.out.println(AppConfig.chordState.createNewFractalTable(newFractalTable));
                            findAllDifferences(oldFractalTable, newFractalTable, temp);
                        }
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            // kazem bootstrapu da sam otisao
            // on me izbaci iz liste
            // treba da kazem svima da promene svoj allNodes, da smanje idijeve oni koji su posle mene
            // i da promene fraktalnu tabelu i da ja vidim kome da posaljem svoje podatke i kome da neko jos drugi posalje svoje podatke i da postane idle
            // zaista izadjem iz sistema
            AppConfig.timestampedErrorPrint("quiting");
        }
    }
    public static void findAllDifferences(Map<String, String> oldMap, Map<String, String> newMap, Map<String, String> differenceMap) {
        StringBuilder res = new StringBuilder();
        Map<String, String> result = new HashMap<>();
        Map<String, String> result2 = new HashMap<>();
        Map<String, String> result3 = new HashMap<>();
        Map<String, String> others = new HashMap<>();
        for (Map.Entry<String, String> oldEntry : oldMap.entrySet()) {
            String id = null;
            for (Map.Entry<String, String> newEntry : differenceMap.entrySet()) {
                if (oldEntry.getValue().equals(newEntry.getValue())) {
                    id = newEntry.getKey();
                    break;
                }
            }
            if (id != null) {
                res.append(oldEntry.getKey()).append(" je radio ").append(oldEntry.getValue()).append(", a sada to treba da radi ").append(id).append("\n");
                result.put(oldEntry.getKey(), id);
            } else {
                others.put(oldEntry.getKey(), oldEntry.getValue());
            }
        }
        int id = Integer.MAX_VALUE;
        for (Map.Entry<String, String> entry : others.entrySet()) {
            if (Integer.parseInt(entry.getKey()) < id) {
                if (newMap.get(entry.getKey()) != null) {
                    id = Integer.parseInt(entry.getKey());
                }
            }
        }
        for (Map.Entry<String, String> entry : others.entrySet()) {
            if (Integer.parseInt(entry.getKey()) != id) {
                result2.put(entry.getKey(), String.valueOf(id));
            }
        }

        for (Map.Entry<String, String> entry : result.entrySet()) {
            if (!entry.getValue().equals(entry.getKey())) {
                result3.put(entry.getKey(), entry.getValue());
            }
        }

        for (Map.Entry<String, String> entry : result3.entrySet()) {
            TransferPointsQuitMessage tpqm = new TransferPointsQuitMessage(AppConfig.myServentInfo.getListenerPort(),
                    AppConfig.chordState.getNextNodeForKey(Integer.parseInt(entry.getKey())).getListenerPort());
            tpqm.setSenderId(entry.getKey());
            tpqm.setReceiverId(entry.getValue());
            tpqm.setReceiverIp(AppConfig.chordState.getNextNodeForKey(Integer.parseInt(entry.getKey())).getIpAddress());
            MessageUtil.sendMessage(tpqm);
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        while (AppConfig.myFractalCreatorInfo.getRegularCount() != result3.size()) {

        }
        if (AppConfig.chordState.getFractalsIds().size() == 2 && AppConfig.myFractalCreatorInfo.getPointCount() == 3) {
            String otherId = null;

            for (Map.Entry<String, String> entry : oldMap.entrySet()) {
                if (Integer.parseInt(entry.getKey()) != AppConfig.myServentInfo.getChordId()) {
                    otherId = entry.getKey();
                }
            }
            SendPointsQuitMessage spqm = new SendPointsQuitMessage(AppConfig.myServentInfo.getListenerPort(),
                    AppConfig.chordState.getNextNodeForKey(Integer.parseInt(otherId)).getListenerPort());
            spqm.setNodeId(String.valueOf(AppConfig.myServentInfo.getChordId()));
            spqm.setReceiverIp(AppConfig.chordState.getNextNodeForKey(Integer.parseInt(otherId)).getIpAddress());
            spqm.setReceiverId(otherId);
            MessageUtil.sendMessage(spqm);
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        } else {
            for (Map.Entry<String, String> entry : result2.entrySet()) {
                SendPointsQuitMessage spqm = new SendPointsQuitMessage(AppConfig.myServentInfo.getListenerPort(),
                        AppConfig.chordState.getNextNodeForKey(Integer.parseInt(entry.getKey())).getListenerPort());
                spqm.setNodeId(entry.getKey());
                spqm.setReceiverIp(AppConfig.chordState.getNextNodeForKey(Integer.parseInt(entry.getKey())).getIpAddress());
                spqm.setReceiverId(entry.getValue());
                MessageUtil.sendMessage(spqm);
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }

        while (AppConfig.myFractalCreatorInfo.getMergeCount() != result2.size()) {
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        List<ServentInfo> allNodes = new ArrayList<>();
        for (ServentInfo serventInfo : AppConfig.chordState.getAllNodeInfo()) {
            if (serventInfo.getChordId() != AppConfig.myServentInfo.getChordId()) {
                ServentInfo info = serventInfo;
                if (info.getChordId() > AppConfig.myServentInfo.getChordId()) {
                    info.setChordId(info.getChordId() - 1);
                }
                allNodes.add(info);
            }
        }
        UpdateFractalsQuitMessage ufqm = new UpdateFractalsQuitMessage(AppConfig.myServentInfo.getListenerPort(), AppConfig.chordState.getNextNodePort());
        ufqm.setId(String.valueOf(AppConfig.myServentInfo.getChordId()));
        ufqm.setFractalTable(newMap);
        ufqm.setAllNodes(allNodes);
        ufqm.setReceiverIp(AppConfig.chordState.getNextNodeIp());
        MessageUtil.sendMessage(ufqm);
    }

    public void forIdle(Map<String, String> newMap) {
        List<ServentInfo> allNodes = new ArrayList<>();
        for (ServentInfo serventInfo : AppConfig.chordState.getAllNodeInfo()) {
            if (serventInfo.getChordId() != AppConfig.myServentInfo.getChordId()) {
                ServentInfo info = serventInfo;
                if (info.getChordId() > AppConfig.myServentInfo.getChordId()) {
                    info.setChordId(info.getChordId() - 1);
                }
                allNodes.add(info);
            }
        }
        UpdateFractalsQuitMessage ufqm = new UpdateFractalsQuitMessage(AppConfig.myServentInfo.getListenerPort(), AppConfig.chordState.getNextNodePort());
        ufqm.setReceiverIp(AppConfig.chordState.getNextNodeIp());
        ufqm.setId(String.valueOf(AppConfig.myServentInfo.getChordId()));
        ufqm.setFractalTable(newMap);
        ufqm.setAllNodes(allNodes);
        MessageUtil.sendMessage(ufqm);
    }
}
