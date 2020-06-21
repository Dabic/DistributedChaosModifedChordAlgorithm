package servent.message;

import java.awt.*;
import java.util.List;

public class TransferPointsQuitMessage extends BasicMessage {
    private static final long serialVersionUID = -3258109110779433863L;
    private String senderId;
    private String receiverId;
    private List<Point> points;

    public TransferPointsQuitMessage(int senderPort, int receiverPort) {
        super(MessageType.TRANSFER_POINTS_QUIT, senderPort, receiverPort);
    }

    public String getSenderId() {
        return senderId;
    }

    public void setSenderId(String senderId) {
        this.senderId = senderId;
    }

    public String getReceiverId() {
        return receiverId;
    }

    public void setReceiverId(String receiverId) {
        this.receiverId = receiverId;
    }

    public List<Point> getPoints() {
        return points;
    }

    public void setPoints(List<Point> points) {
        this.points = points;
    }
}
