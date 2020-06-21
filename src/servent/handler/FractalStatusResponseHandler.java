package servent.handler;

import app.AppConfig;
import servent.message.FractalStatusResponseMessage;
import servent.message.Message;
import servent.message.util.MessageUtil;

public class FractalStatusResponseHandler implements MessageHandler {
    private FractalStatusResponseMessage message;

    public FractalStatusResponseHandler(Message message) {
        this.message = (FractalStatusResponseMessage) message;
    }

    @Override
    public void run() {
        if (Integer.parseInt(message.getNodeId()) != AppConfig.myServentInfo.getChordId()) {
            FractalStatusResponseMessage fsrm = new FractalStatusResponseMessage(
                    message.getSenderPort(),
                    AppConfig.chordState.getNextNodeForKey(Integer.parseInt(message.getNodeId())).getListenerPort());
            fsrm.setNodeId(message.getNodeId());
            fsrm.setFractalId(message.getFractalId());
            fsrm.setPointCount(message.getPointCount());
            fsrm.setReceiverIp(AppConfig.chordState.getNextNodeForKey(Integer.parseInt(message.getNodeId())).getIpAddress());
            MessageUtil.sendMessage(fsrm);
        } else {
            AppConfig.timestampedStandardPrint("Status result for fractalId " + message.getFractalId() + " drew " + message.getPointCount() + " points.");
        }
    }
}
