package servent.message;

public class FractalStatusMessage extends BasicMessage {
    private static final long serialVersionUID = -5131607435617382359L;

    private String fractalId;
    private int pointCount;
    private String nodeId;
    private String requestNodeId;
    private String jobName;

    public FractalStatusMessage(int senderPort, int receiverPort) {
        super(MessageType.FRACTAL_STATUS, senderPort, receiverPort);
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

    public void setNodeId(String nodeId) {
        this.nodeId = nodeId;
    }

    public String getNodeId() {
        return nodeId;
    }

    public String getRequestNodeId() {
        return requestNodeId;
    }

    public void setRequestNodeId(String requestNodeId) {
        this.requestNodeId = requestNodeId;
    }

    public String getJobName() {
        return jobName;
    }

    public void setJobName(String jobName) {
        this.jobName = jobName;
    }
}
