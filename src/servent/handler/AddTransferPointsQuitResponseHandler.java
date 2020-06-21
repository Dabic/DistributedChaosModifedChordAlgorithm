package servent.handler;

import app.AppConfig;
import servent.message.AddTransferPointsQuitResponseMessage;
import servent.message.Message;
import servent.message.util.MessageUtil;

public class AddTransferPointsQuitResponseHandler implements MessageHandler {
    private AddTransferPointsQuitResponseMessage message;

    public AddTransferPointsQuitResponseHandler(Message message) {
        this.message = (AddTransferPointsQuitResponseMessage) message;
    }

    @Override
    public void run() {
        if (message.getSenderPort() != AppConfig.myServentInfo.getListenerPort()) {
            AddTransferPointsQuitResponseMessage atpqrm = new AddTransferPointsQuitResponseMessage(message.getSenderPort(), AppConfig.chordState.getNextNodePort());
            atpqrm.setReceiverIp(AppConfig.chordState.getNextNodeIp());
            MessageUtil.sendMessage(atpqrm);
        } else {
            AppConfig.myFractalCreatorInfo.incrementRegularCount();
        }
    }
}
