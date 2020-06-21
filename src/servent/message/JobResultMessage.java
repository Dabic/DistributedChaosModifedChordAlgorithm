package servent.message;

import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class JobResultMessage extends BasicMessage {
    private static final long serialVersionUID = 4995330721134950989L;
    private List<Point> points;
    private String jobName;
    public JobResultMessage(int senderPort, int receiverPort) {
        super(MessageType.JOB_RESULT, senderPort, receiverPort);
        points = new ArrayList<>();
    }

    public List<Point> getPoints() {
        return points;
    }

    public void setPoints(List<Point> points) {
        this.points = points;
    }

    public String getJobName() {
        return jobName;
    }

    public void setJobName(String jobName) {
        this.jobName = jobName;
    }
}
