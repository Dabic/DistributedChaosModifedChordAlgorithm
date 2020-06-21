package servent.message;

public class ReceivePointsQuitResponseMessage extends BasicMessage {
    private static final long serialVersionUID = 9186993556771561176L;

    public ReceivePointsQuitResponseMessage(int senderPort, int receiverPort) {
        super(MessageType.RECEIVE_POINTS_QUIT_RESPONSE, senderPort, receiverPort);
    }
}
