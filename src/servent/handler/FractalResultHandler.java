package servent.handler;

import app.AppConfig;
import app.fractals.FractalCreator;
import servent.message.FractalResultMessage;
import servent.message.FractalResultResponseMessage;
import servent.message.Message;
import servent.message.util.MessageUtil;

import java.util.ArrayList;

public class FractalResultHandler implements MessageHandler {

    private FractalResultMessage message;
    private FractalCreator fractalCreator;

    public FractalResultHandler(Message message, FractalCreator fractalCreator) {
        this.message = (FractalResultMessage) message;
        this.fractalCreator = fractalCreator;
    }

    @Override
    public void run() {
        if (Integer.parseInt(message.getNodeId()) != AppConfig.myServentInfo.getChordId()) {
            FractalResultMessage frm = new FractalResultMessage(AppConfig.myServentInfo.getListenerPort(),
                    AppConfig.chordState.getNextNodeForKey(Integer.parseInt(message.getNodeId())).getListenerPort());
            frm.setNodeId(message.getNodeId());
            frm.setJobName(message.getJobName());
            frm.setReceiverIp(AppConfig.chordState.getNextNodeForKey(Integer.parseInt(message.getNodeId())).getIpAddress());
            frm.setResponseNodeId(message.getResponseNodeId());
            MessageUtil.sendMessage(frm);
        } else {
            if (AppConfig.myFractalCreatorInfo.getJobName().equals(message.getJobName())) {
                FractalResultResponseMessage frrm = new FractalResultResponseMessage(AppConfig.myServentInfo.getListenerPort(),
                        AppConfig.chordState.getNextNodeForKey(Integer.parseInt(message.getResponseNodeId())).getListenerPort());
                frrm.setNodeId(message.getResponseNodeId());
                frrm.setReceiverIp(AppConfig.chordState.getNextNodeForKey(Integer.parseInt(message.getResponseNodeId())).getIpAddress());
                frrm.setPoints(fractalCreator.getMyTracePoints());
                MessageUtil.sendMessage(frrm);
            } else {
                FractalResultResponseMessage frrm = new FractalResultResponseMessage(AppConfig.myServentInfo.getListenerPort(),
                        AppConfig.chordState.getNextNodeForKey(Integer.parseInt(message.getResponseNodeId())).getListenerPort());
                frrm.setNodeId(message.getResponseNodeId());
                frrm.setPoints(new ArrayList<>());
                frrm.setReceiverIp(AppConfig.chordState.getNextNodeForKey(Integer.parseInt(message.getResponseNodeId())).getIpAddress());
                MessageUtil.sendMessage(frrm);
            }
        }
    }
}
