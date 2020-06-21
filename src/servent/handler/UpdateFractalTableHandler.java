package servent.handler;

import app.AppConfig;
import servent.message.Message;
import servent.message.UpdateFractalTableMessage;
import servent.message.util.MessageUtil;

public class UpdateFractalTableHandler implements MessageHandler {
    private final UpdateFractalTableMessage message;

    public UpdateFractalTableHandler(Message message) {
        this.message = (UpdateFractalTableMessage)message;
    }

    @Override
    public void run() {
        if (message.getSenderPort() != AppConfig.myServentInfo.getListenerPort()) {
            AppConfig.chordState.setFractalsIds(message.getFractalIds());
            UpdateFractalTableMessage uftm = new UpdateFractalTableMessage(message.getSenderPort(), AppConfig.chordState.getNextNodePort());
            uftm.setFractalIds(message.getFractalIds());
            uftm.setReceiverIp(AppConfig.chordState.getNextNodeIp());
            MessageUtil.sendMessage(uftm);
        }
        else {
            AppConfig.chordState.askForPoints();
        }
    }
}
