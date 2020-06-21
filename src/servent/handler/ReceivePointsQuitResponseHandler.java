package servent.handler;

import app.AppConfig;
import servent.message.Message;
import servent.message.ReceivePointsQuitResponseMessage;
import servent.message.util.MessageUtil;

public class ReceivePointsQuitResponseHandler implements MessageHandler {
    private ReceivePointsQuitResponseMessage message;

    public ReceivePointsQuitResponseHandler(Message message) {
        this.message = (ReceivePointsQuitResponseMessage) message;
    }

    @Override
    public void run() {
        if (message.getSenderPort() != AppConfig.myServentInfo.getListenerPort()) {
            ReceivePointsQuitResponseMessage rpqrm = new ReceivePointsQuitResponseMessage(message.getSenderPort(), AppConfig.chordState.getNextNodePort());
            rpqrm.setReceiverIp(AppConfig.chordState.getNextNodeIp());
            MessageUtil.sendMessage(rpqrm);
        } else {
            AppConfig.myFractalCreatorInfo.incrementMergeCount();
        }
    }
}
