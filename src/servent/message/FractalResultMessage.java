package servent.message;

public class FractalResultMessage extends BasicMessage {
    private static final long serialVersionUID = -3284004003616373474L;

    private String nodeId;
    private String responseNodeId;
    private String jobName;
    public FractalResultMessage(int senderPort, int receiverPort) {
        super(MessageType.FRACTAL_RESULT, senderPort, receiverPort);
    }

    public String getNodeId() {
        return nodeId;
    }

    public void setNodeId(String nodeId) {
        this.nodeId = nodeId;
    }

    public String getResponseNodeId() {
        return responseNodeId;
    }

    public void setResponseNodeId(String responseNodeId) {
        this.responseNodeId = responseNodeId;
    }

    public String getJobName() {
        return jobName;
    }

    public void setJobName(String jobName) {
        this.jobName = jobName;
    }
}
