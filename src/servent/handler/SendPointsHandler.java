package servent.handler;

import app.AppConfig;
import app.fractals.FractalCreator;
import servent.message.Message;
import servent.message.SendPointsMessage;
import servent.message.util.MessageUtil;

public class SendPointsHandler implements MessageHandler {

    private SendPointsMessage message;
    private FractalCreator fractalCreator;

    public SendPointsHandler(Message message, FractalCreator fractalCreator) {
        this.message = (SendPointsMessage)message;
        this.fractalCreator = fractalCreator;
    }

    @Override
    public void run() {
        if (message.getNodeId() != AppConfig.myServentInfo.getChordId()) {
            SendPointsMessage spm = new SendPointsMessage(
                    AppConfig.myServentInfo.getListenerPort(),
                    AppConfig.chordState.getNextNodeForKey(message.getNodeId()).getListenerPort());
            spm.setStartingPoints(message.getStartingPoints());
            spm.setTracePoints(message.getTracePoints());
            spm.setNodeId(message.getNodeId());
            spm.setReceiverIp(AppConfig.chordState.getNextNodeForKey(message.getNodeId()).getIpAddress());
            MessageUtil.sendMessage(spm);
        } else {
            fractalCreator.startWorking(message.getStartingPoints(), message.getTracePoints());
        }
    }
}
