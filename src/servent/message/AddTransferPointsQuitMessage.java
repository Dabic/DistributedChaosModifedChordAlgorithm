package servent.message;

import java.awt.*;
import java.util.List;

public class AddTransferPointsQuitMessage extends BasicMessage {
    private static final long serialVersionUID = 8442357748570429329L;
    private String nodeId;
    private List<Point> points;

    public AddTransferPointsQuitMessage(int senderPort, int receiverPort) {
        super(MessageType.ADD_TRANSFER_POINTS_QUIT, senderPort, receiverPort);
    }

    public String getNodeId() {
        return nodeId;
    }

    public void setNodeId(String nodeId) {
        this.nodeId = nodeId;
    }

    public List<Point> getPoints() {
        return points;
    }

    public void setPoints(List<Point> points) {
        this.points = points;
    }
}
