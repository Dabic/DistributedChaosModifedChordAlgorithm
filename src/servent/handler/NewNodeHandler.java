package servent.handler;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import app.AppConfig;
import app.ServentInfo;
import app.fractals.FractalCreator;
import app.fractals.FractalCreatorInfo;
import servent.message.Message;
import servent.message.MessageType;
import servent.message.NewNodeMessage;
import servent.message.SorryMessage;
import servent.message.WelcomeMessage;
import servent.message.util.MessageUtil;

public class NewNodeHandler implements MessageHandler {

	private Message clientMessage;
	private FractalCreator fractalCreator;
	
	public NewNodeHandler(Message clientMessage, FractalCreator fractalCreator) {
		this.clientMessage = clientMessage;
		this.fractalCreator = fractalCreator;
	}
	
	@Override
	public void run() {
		if (clientMessage.getMessageType() == MessageType.NEW_NODE) {
			int newNodePort = clientMessage.getSenderPort();
			String ip = ((NewNodeMessage)clientMessage).getSenderIp();
			ServentInfo newNodeInfo = new ServentInfo(ip, newNodePort);
			newNodeInfo.setChordId(((NewNodeMessage)clientMessage).getId());
			ServentInfo mySuccessor = AppConfig.chordState.getSuccessorTable()[0];

			WelcomeMessage wm = new WelcomeMessage(AppConfig.myServentInfo.getListenerPort(), newNodePort, null);
			if (mySuccessor != null) {
				wm.setSuccessorInfo(mySuccessor);
			} else {
				wm.setSuccessorInfo(AppConfig.myServentInfo);
			}
			wm.setSenderIp(AppConfig.myServentInfo.getIpAddress());
			wm.setReceiverIp(((NewNodeMessage) clientMessage).getSenderIp());
			wm.setFractalIds(AppConfig.chordState.getFractalsIds());
			wm.setWorking(AppConfig.myFractalCreatorInfo.isWorking());
			FractalCreatorInfo fractalCreatorInfo = new FractalCreatorInfo();
			fractalCreatorInfo.setPoints(AppConfig.myFractalCreatorInfo.getPoints());
			fractalCreatorInfo.setPointCount(AppConfig.myFractalCreatorInfo.getPointCount());
			fractalCreatorInfo.setHeight(AppConfig.myFractalCreatorInfo.getHeight());
			fractalCreatorInfo.setWidth(AppConfig.myFractalCreatorInfo.getWidth());
			fractalCreatorInfo.setProportion(AppConfig.myFractalCreatorInfo.getProportion());
			fractalCreatorInfo.setJobName(AppConfig.myFractalCreatorInfo.getJobName());
			fractalCreatorInfo.setWorking(AppConfig.myFractalCreatorInfo.isWorking());
			wm.setFractalCreatorInfo(fractalCreatorInfo);
			MessageUtil.sendMessage(wm);
			
		} else {
			AppConfig.timestampedErrorPrint("NEW_NODE handler got something that is not new node message.");
		}

	}

}
