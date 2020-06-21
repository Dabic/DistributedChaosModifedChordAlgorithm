package servent.message;

import servent.message.BasicMessage;
import servent.message.MessageType;

public class IAmGoneMessage extends BasicMessage {
    private static final long serialVersionUID = 931550879449680867L;
    private String id;

    public IAmGoneMessage(int senderPort, int receiverPort) {
        super(MessageType.I_AM_GONE, senderPort, receiverPort);
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
