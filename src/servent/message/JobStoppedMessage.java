package servent.message;

public class JobStoppedMessage extends BasicMessage {
    private static final long serialVersionUID = -7737089816104252071L;

    public JobStoppedMessage(int senderPort, int receiverPort) {
        super(MessageType.JOB_STOPPED, senderPort, receiverPort);
    }
}
