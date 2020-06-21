package servent.message;

import java.awt.*;
import java.util.List;

public class SendPointsQuitMessage extends BasicMessage {
    private static final long serialVersionUID = 1948954767417498606L;
    private String nodeId;
    private String receiverId;

    public SendPointsQuitMessage(int senderPort, int receiverPort) {
        super(MessageType.SEND_POINTS_QUIT, senderPort, receiverPort);
    }

    public String getNodeId() {
        return nodeId;
    }

    public void setNodeId(String nodeId) {
        this.nodeId = nodeId;
    }

    public String getReceiverId() {
        return receiverId;
    }

    public void setReceiverId(String receiverId) {
        this.receiverId = receiverId;
    }
}
