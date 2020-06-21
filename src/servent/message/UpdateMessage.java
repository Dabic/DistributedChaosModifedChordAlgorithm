package servent.message;

public class UpdateMessage extends BasicMessage {

	private static final long serialVersionUID = 3586102505319194978L;
	private int newNodeId;
	private String senderIp;
	public UpdateMessage(int senderPort, int receiverPort, String text) {
		super(MessageType.UPDATE, senderPort, receiverPort, text);
	}

	public void setNewNodeId(int newNodeId) {
		this.newNodeId = newNodeId;
	}

	public int getNewNodeId() {
		return newNodeId;
	}

	public String getSenderIp() {
		return senderIp;
	}

	public void setSenderIp(String senderIp) {
		this.senderIp = senderIp;
	}
}
