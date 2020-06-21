package servent.handler;

import app.AppConfig;
import app.fractals.FractalCreator;
import servent.message.FractalStatusMessage;
import servent.message.FractalStatusResponseMessage;
import servent.message.Message;
import servent.message.util.MessageUtil;

public class FractalStatusHandler implements MessageHandler {

    private FractalStatusMessage message;
    private FractalCreator fractalCreator;

    public FractalStatusHandler(Message message, FractalCreator fractalCreator) {
        this.message = (FractalStatusMessage) message;
        this.fractalCreator = fractalCreator;
    }

    @Override
    public void run() {
        if (Integer.parseInt(message.getNodeId()) != AppConfig.myServentInfo.getChordId()) {
            FractalStatusMessage fsm = new FractalStatusMessage(message.getSenderPort(), AppConfig.chordState.getNextNodeForKey(Integer.parseInt(message.getNodeId())).getListenerPort());
            fsm.setNodeId(message.getNodeId());
            fsm.setFractalId(message.getFractalId());
            fsm.setRequestNodeId(message.getRequestNodeId());
            fsm.setJobName(message.getJobName());
            fsm.setReceiverIp(AppConfig.chordState.getNextNodeForKey(Integer.parseInt(message.getNodeId())).getIpAddress());
            MessageUtil.sendMessage(fsm);
        } else {
            if (message.getJobName().equals(AppConfig.myFractalCreatorInfo.getJobName())) {
                FractalStatusResponseMessage fsrm = new FractalStatusResponseMessage(
                        AppConfig.myServentInfo.getListenerPort(),
                        AppConfig.chordState.getNextNodeForKey(Integer.parseInt(message.getRequestNodeId())).getListenerPort());
                fsrm.setNodeId(message.getRequestNodeId());
                fsrm.setFractalId(message.getFractalId());
                fsrm.setReceiverIp( AppConfig.chordState.getNextNodeForKey(Integer.parseInt(message.getRequestNodeId())).getIpAddress());
                fsrm.setPointCount(fractalCreator.getStatus());
                MessageUtil.sendMessage(fsrm);
            }
        }
    }
}
