package servent.message;

import java.util.HashMap;
import java.util.Map;

public class JobStatusMessage extends BasicMessage {

    private static final long serialVersionUID = -7460652546326242557L;

    private String jobName;
    private Map<String, Integer> result;

    public JobStatusMessage(int senderPort, int receiverPort) {
        super(MessageType.JOB_STATUS, senderPort, receiverPort);
        result = new HashMap<>();
    }

    public String getJobName() {
        return jobName;
    }

    public void setJobName(String jobName) {
        this.jobName = jobName;
    }

    public Map<String, Integer> getResult() {
        return result;
    }

    public void setResult(Map<String, Integer> result) {
        this.result = result;
    }
}
