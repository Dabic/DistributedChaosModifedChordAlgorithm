package servent.message;

import java.awt.*;
import java.util.List;

public class SendPointsMessage extends BasicMessage {
    private static final long serialVersionUID = 7506356856575327309L;

    private List<Point> startingPoints;
    private List<Point> tracePoints;
    private int nodeId;
    public SendPointsMessage(int senderPort, int receiverPort) {
        super(MessageType.SEND_POINTS, senderPort, receiverPort);
    }

    public List<Point> getStartingPoints() {
        return startingPoints;
    }

    public void setStartingPoints(List<Point> startingPoints) {
        this.startingPoints = startingPoints;
    }

    public List<Point> getTracePoints() {
        return tracePoints;
    }

    public void setTracePoints(List<Point> tracePoints) {
        this.tracePoints = tracePoints;
    }

    public int getNodeId() {
        return nodeId;
    }

    public void setNodeId(int nodeId) {
        this.nodeId = nodeId;
    }
}
