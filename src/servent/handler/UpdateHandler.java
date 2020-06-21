package servent.handler;

import java.util.ArrayList;
import java.util.List;

import app.AppConfig;
import app.ServentInfo;
import servent.message.Message;
import servent.message.MessageType;
import servent.message.UpdateFractalTableMessage;
import servent.message.UpdateMessage;
import servent.message.util.MessageUtil;

public class UpdateHandler implements MessageHandler {

	private Message clientMessage;
	
	public UpdateHandler(Message clientMessage) {
		this.clientMessage = clientMessage;
	}
	
	@Override
	public void run() {
		if (clientMessage.getMessageType() == MessageType.UPDATE) {
			if (clientMessage.getSenderPort() != AppConfig.myServentInfo.getListenerPort()) {
				ServentInfo newNodInfo = new ServentInfo(((UpdateMessage)clientMessage).getSenderIp(), clientMessage.getSenderPort());
				newNodInfo.setChordId(((UpdateMessage)clientMessage).getNewNodeId());
				List<ServentInfo> newNodes = new ArrayList<>();
				newNodes.add(newNodInfo);
				AppConfig.chordState.addNodes(newNodes);
				String newMessageText = "";
				if (clientMessage.getMessageText().equals("")) {
					newMessageText = AppConfig.myServentInfo.getChordId() + " " + AppConfig.myServentInfo.getListenerPort() + " " + AppConfig.myServentInfo.getIpAddress();
				} else {
					newMessageText = clientMessage.getMessageText() + "," + AppConfig.myServentInfo.getChordId() + " " + AppConfig.myServentInfo.getListenerPort() + " " + AppConfig.myServentInfo.getIpAddress();
				}
				UpdateMessage nextUpdate = new UpdateMessage(clientMessage.getSenderPort(), AppConfig.chordState.getNextNodePort(),
						newMessageText);
				nextUpdate.setNewNodeId(((UpdateMessage)clientMessage).getNewNodeId());
				nextUpdate.setReceiverIp(AppConfig.chordState.getNextNodeIp());
				nextUpdate.setSenderIp(((UpdateMessage)clientMessage).getSenderIp());
				MessageUtil.sendMessage(nextUpdate);
			} else {
				String messageText = clientMessage.getMessageText();
				String[] idsWithPortsAndIps = messageText.split(",");
				
				List<ServentInfo> allNodes = new ArrayList<>();
				for (String idPorts : idsWithPortsAndIps) {
					ServentInfo newInfo = new ServentInfo(idPorts.split(" ")[2], Integer.parseInt(idPorts.split(" ")[1]));
					newInfo.setChordId(Integer.parseInt(idPorts.split(" ")[0]));
					allNodes.add(newInfo);
				}
				AppConfig.chordState.addNodes(allNodes);
				AppConfig.chordState.updateFractalsTable();
				UpdateFractalTableMessage uftm = new UpdateFractalTableMessage(AppConfig.myServentInfo.getListenerPort(), AppConfig.chordState.getNextNodePort());
				uftm.setFractalIds(AppConfig.chordState.getFractalsIds());
				uftm.setReceiverIp(AppConfig.chordState.getNextNodeIp());
				MessageUtil.sendMessage(uftm);
			}
		} else {
			AppConfig.timestampedErrorPrint("Update message handler got message that is not UPDATE");
		}
	}

}
