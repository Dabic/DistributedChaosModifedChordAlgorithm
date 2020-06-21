package servent.handler;

import app.AppConfig;
import app.fractals.FractalCreator;
import servent.message.AddTransferPointsQuitMessage;
import servent.message.AddTransferPointsQuitResponseMessage;
import servent.message.Message;
import servent.message.util.MessageUtil;

public class AddTransferPointsQuitHandler implements MessageHandler {
    private AddTransferPointsQuitMessage message;
    private FractalCreator fractalCreator;

    public AddTransferPointsQuitHandler(Message message, FractalCreator fractalCreator) {
        this.message = (AddTransferPointsQuitMessage) message;
        this.fractalCreator = fractalCreator;
    }

    @Override
    public void run() {
        if (Integer.parseInt(message.getNodeId()) != AppConfig.myServentInfo.getChordId()) {
            AddTransferPointsQuitMessage atpqm = new AddTransferPointsQuitMessage(message.getSenderPort(),
                    AppConfig.chordState.getNextNodeForKey(Integer.parseInt(message.getNodeId())).getListenerPort());
            atpqm.setPoints(message.getPoints());
            atpqm.setNodeId(message.getNodeId());
            atpqm.setReceiverIp(AppConfig.chordState.getNextNodeForKey(Integer.parseInt(message.getNodeId())).getIpAddress());
            MessageUtil.sendMessage(atpqm);
        } else {
            fractalCreator.getTempPoints().addAll(message.getPoints());
            fractalCreator.stop();
            AddTransferPointsQuitResponseMessage atpqrm = new AddTransferPointsQuitResponseMessage(message.getSenderPort(), AppConfig.chordState.getNextNodePort());
            atpqrm.setReceiverIp(AppConfig.chordState.getNextNodeIp());
            MessageUtil.sendMessage(atpqrm);
        }
    }
}
