package servent.message;

import java.io.Serializable;

public class JobStartedMessage extends BasicMessage {

    private static final long serialVersionUID = 3773005594639934082L;

    public JobStartedMessage(int senderPort, int receiverPort) {
        super(MessageType.JOB_STARTED, senderPort, receiverPort);
    }
}
