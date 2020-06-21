package servent.message;

public class NewNodeMessage extends BasicMessage {

	private static final long serialVersionUID = 3899837286642127636L;

	private int id;
	private String senderIp;
	public NewNodeMessage(int senderPort, int receiverPort) {
		super(MessageType.NEW_NODE, senderPort, receiverPort);
	}

	public static long getSerialVersionUID() {
		return serialVersionUID;
	}

	public String getSenderIp() {
		return senderIp;
	}

	public void setSenderIp(String senderIp) {
		this.senderIp = senderIp;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}
}
