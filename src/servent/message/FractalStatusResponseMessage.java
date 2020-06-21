package servent.message;

public class FractalStatusResponseMessage extends BasicMessage {
    private static final long serialVersionUID = 7203373923594355242L;
    private String fractalId;
    private int pointCount;
    private String nodeId;
    public FractalStatusResponseMessage(int senderPort, int receiverPort) {
        super(MessageType.FRACTAL_STATUS_RESPONSE, senderPort, receiverPort);
    }

    public String getFractalId() {
        return fractalId;
    }

    public void setFractalId(String fractalId) {
        this.fractalId = fractalId;
    }

    public int getPointCount() {
        return pointCount;
    }

    public void setPointCount(int pointCount) {
        this.pointCount = pointCount;
    }

    public String getNodeId() {
        return nodeId;
    }

    public void setNodeId(String nodeId) {
        this.nodeId = nodeId;
    }
}
