package servent.message;

import servent.message.BasicMessage;
import servent.message.MessageType;

import java.awt.*;
import java.util.List;

public class ReceivePointsQuitMessage extends BasicMessage {
    private static final long serialVersionUID = 3493584426743176814L;
    private String nodeId;
    private List<Point> points;

    public ReceivePointsQuitMessage(int senderPort, int receiverPort) {
        super(MessageType.RECEIVE_POINTS_QUIT, senderPort, receiverPort);
    }

    public List<Point> getPoints() {
        return points;
    }

    public void setPoints(List<Point> points) {
        this.points = points;
    }

    public String getNodeId() {
        return nodeId;
    }

    public void setNodeId(String nodeId) {
        this.nodeId = nodeId;
    }
}
