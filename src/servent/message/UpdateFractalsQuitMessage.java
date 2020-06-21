package servent.message;

import app.ServentInfo;

import java.util.List;
import java.util.Map;

public class UpdateFractalsQuitMessage extends BasicMessage {
    private static final long serialVersionUID = 7226954148270772844L;
    private Map<String, String> fractalTable;
    private List<ServentInfo> allNodes;
    private String id;

    public UpdateFractalsQuitMessage(int senderPort, int receiverPort) {
        super(MessageType.UPDATE_FRACTAL_QUIT, senderPort, receiverPort);
    }

    public Map<String, String> getFractalTable() {
        return fractalTable;
    }

    public void setFractalTable(Map<String, String> fractalTable) {
        this.fractalTable = fractalTable;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public List<ServentInfo> getAllNodes() {
        return allNodes;
    }

    public void setAllNodes(List<ServentInfo> allNodes) {
        this.allNodes = allNodes;
    }
}
