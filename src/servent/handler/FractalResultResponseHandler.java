package servent.handler;

import app.AppConfig;
import app.fractals.FractalCreator;
import servent.message.FractalResultResponseMessage;
import servent.message.Message;
import servent.message.util.MessageUtil;

public class FractalResultResponseHandler implements MessageHandler {
    private FractalResultResponseMessage message;
    private FractalCreator fractalCreator;

    public FractalResultResponseHandler(Message message, FractalCreator fractalCreator) {
        this.message = (FractalResultResponseMessage) message;
        this.fractalCreator = fractalCreator;
    }

    @Override
    public void run() {
        if (Integer.parseInt(message.getNodeId()) != AppConfig.myServentInfo.getChordId()) {
            FractalResultResponseMessage frrm = new FractalResultResponseMessage(message.getSenderPort(),
                    AppConfig.chordState.getNextNodeForKey(Integer.parseInt(message.getNodeId())).getListenerPort());
            frrm.setPoints(message.getPoints());
            frrm.setReceiverIp(AppConfig.chordState.getNextNodeForKey(Integer.parseInt(message.getNodeId())).getIpAddress());
            frrm.setNodeId(message.getNodeId());
            MessageUtil.sendMessage(frrm);
        } else {
            fractalCreator.printResult(message.getPoints());
        }
    }
}
