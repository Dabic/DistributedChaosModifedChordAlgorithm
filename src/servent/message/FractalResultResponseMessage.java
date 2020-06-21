package servent.message;

import java.awt.*;
import java.util.List;

public class FractalResultResponseMessage extends BasicMessage {
    private static final long serialVersionUID = 8875025645529054343L;
    private List<Point> points;
    private String nodeId;
    public FractalResultResponseMessage(int senderPort, int receiverPort) {
        super(MessageType.FRACTAL_RESULT_RESPONSE, senderPort, receiverPort);
    }

    public void setPoints(List<Point> points) {
        this.points = points;
    }

    public List<Point> getPoints() {
        return points;
    }

    public String getNodeId() {
        return nodeId;
    }

    public void setNodeId(String nodeId) {
        this.nodeId = nodeId;
    }
}
