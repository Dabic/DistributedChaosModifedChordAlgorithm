package servent.message;

import app.ServentInfo;

import java.util.Map;

public class AskForPointsMessage extends BasicMessage {

    private static final long serialVersionUID = 5990116169004932041L;

    private ServentInfo receiverInfo;
    private Map<String, String> fractals;

    public AskForPointsMessage(int senderPort, int receiverPort) {
        super(MessageType.ASK_FOR_POINTS, senderPort, receiverPort);
    }

    public void setFractals(Map<String, String> fractals) {
        this.fractals = fractals;
    }

    public Map<String, String> getFractals() {
        return fractals;
    }

    public ServentInfo getReceiverInfo() {
        return receiverInfo;
    }

    public void setReceiverInfo(ServentInfo receiverInfo) {
        this.receiverInfo = receiverInfo;
    }
}
