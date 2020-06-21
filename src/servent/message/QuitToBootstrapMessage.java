package servent.message;

import java.awt.*;

public class QuitToBootstrapMessage extends BasicMessage {
    private static final long serialVersionUID = -6549898624843683691L;


    public QuitToBootstrapMessage(MessageType type, int senderPort, int receiverPort) {
        super(type, senderPort, receiverPort);
    }
}
