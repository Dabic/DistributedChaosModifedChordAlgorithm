package servent.message;

import app.ServentInfo;
import app.fractals.FractalCreatorInfo;

import java.util.Map;

public class WelcomeMessage extends BasicMessage {


	private static final long serialVersionUID = -5466047382594697074L;
	private Map<Integer, Integer> values;
	private boolean working = false;
	private ServentInfo successorInfo;
	private Map<String, String> fractalIds;
	private FractalCreatorInfo fractalCreatorInfo;
	private String senderIp;
	
	public WelcomeMessage(int senderPort, int receiverPort, Map<Integer, Integer> values) {
		super(MessageType.WELCOME, senderPort, receiverPort);
		this.values = values;
	}

	public String getSenderIp() {
		return senderIp;
	}

	public void setSenderIp(String senderIp) {
		this.senderIp = senderIp;
	}

	public void setWorking(boolean working) {
		this.working = working;
	}

	public boolean isWorking() {
		return working;
	}

	public Map<Integer, Integer> getValues() {
		return values;
	}

	public void setSuccessorInfo(ServentInfo successorInfo) {
		this.successorInfo = successorInfo;
	}

	public ServentInfo getSuccessorInfo() {
		return successorInfo;
	}

	public void setFractalIds(Map<String, String> fractalIds) {
		this.fractalIds = fractalIds;
	}

	public Map<String, String> getFractalIds() {
		return fractalIds;
	}

	public FractalCreatorInfo getFractalCreatorInfo() {
		return fractalCreatorInfo;
	}

	public void setFractalCreatorInfo(FractalCreatorInfo fractalCreatorInfo) {
		this.fractalCreatorInfo = fractalCreatorInfo;
	}
}
