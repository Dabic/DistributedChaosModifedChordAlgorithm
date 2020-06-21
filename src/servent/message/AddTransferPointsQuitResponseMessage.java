package servent.message;

public class AddTransferPointsQuitResponseMessage extends BasicMessage {
    private static final long serialVersionUID = -575878128660334571L;

    public AddTransferPointsQuitResponseMessage(int senderPort, int receiverPort) {
        super(MessageType.ADD_TRANSFER_POINTS_QUIT_RESPONSE, senderPort, receiverPort);
    }
}
