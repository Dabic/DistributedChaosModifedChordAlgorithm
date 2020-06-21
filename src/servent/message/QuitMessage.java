package servent.message;

public class QuitMessage extends BasicMessage {
    private static final long serialVersionUID = 2980425443525829935L;

    private int nodeId;


    public QuitMessage(int senderPort, int receiverPort) {
        super(MessageType.QUIT, senderPort, receiverPort);
    }
}
