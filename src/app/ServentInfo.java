package app;

import java.io.Serializable;

/**
 * This is an immutable class that holds all the information for a servent.
 *
 * @author bmilojkovic
 */
public class ServentInfo implements Serializable {

	private static final long serialVersionUID = 5304170042791281555L;
	private final String ipAddress;
	private final int listenerPort;
	private int chordId;
	private int originalId;
	
	public ServentInfo(String ipAddress, int listenerPort) {
		this.ipAddress = ipAddress;
		this.listenerPort = listenerPort;
	}

	public String getIpAddress() {
		return ipAddress;
	}

	public int getListenerPort() {
		return listenerPort;
	}

	public int getChordId() {
		return chordId;
	}

	public void setChordId(int chordId) {
		this.chordId = chordId;
	}

	@Override
	public String toString() {
		return "[" + chordId + "|" + ipAddress + "|" + listenerPort + "]";
	}

	public int getOriginalId() {
		return originalId;
	}

	public void setOriginalId(int originalId) {
		this.originalId = originalId;
	}
}
