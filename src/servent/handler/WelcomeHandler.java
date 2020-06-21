package servent.handler;

import app.AppConfig;
import app.fractals.FractalCreator;
import servent.message.Message;
import servent.message.MessageType;
import servent.message.UpdateMessage;
import servent.message.WelcomeMessage;
import servent.message.util.MessageUtil;

public class WelcomeHandler implements MessageHandler {

	private Message clientMessage;
	private FractalCreator fractalCreator;
	
	public WelcomeHandler(Message clientMessage, FractalCreator fractalCreator) {
		this.clientMessage = clientMessage;
		this.fractalCreator = fractalCreator;
	}
	
	@Override
	public void run() {
		if (clientMessage.getMessageType() == MessageType.WELCOME) {
			WelcomeMessage welcomeMsg = (WelcomeMessage)clientMessage;
			
			AppConfig.chordState.init(welcomeMsg);
			if (welcomeMsg.isWorking()) {
				AppConfig.myFractalCreatorInfo.setWorking(true);
			}
			AppConfig.myFractalCreatorInfo = welcomeMsg.getFractalCreatorInfo();
			UpdateMessage um = new UpdateMessage(AppConfig.myServentInfo.getListenerPort(), AppConfig.chordState.getNextNodePort(), "");
			um.setReceiverIp(AppConfig.chordState.getNextNodeIp());
			um.setSenderIp(AppConfig.myServentInfo.getIpAddress());
			um.setNewNodeId(AppConfig.myServentInfo.getChordId());
			MessageUtil.sendMessage(um);
			
		} else {
			AppConfig.timestampedErrorPrint("Welcome handler got a message that is not WELCOME");
		}

	}

}
