package servent.handler;

import app.AppConfig;
import app.fractals.FractalCreator;
import servent.message.Message;
import servent.message.ReceivePointsQuitMessage;
import servent.message.SendPointsQuitMessage;
import servent.message.util.MessageUtil;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class SendPointsQuitHandler implements MessageHandler {
    private SendPointsQuitMessage message;
    private FractalCreator fractalCreator;

    public SendPointsQuitHandler(Message message, FractalCreator fractalCreator) {
        this.message = (SendPointsQuitMessage) message;
        this.fractalCreator = fractalCreator;
    }

    @Override
    public void run() {
        if (Integer.parseInt(message.getNodeId()) != AppConfig.myServentInfo.getChordId()) {
            SendPointsQuitMessage spqm = new SendPointsQuitMessage(message.getSenderPort(),
                    AppConfig.chordState.getNextNodeForKey(Integer.parseInt(message.getNodeId())).getListenerPort());
            spqm.setNodeId(message.getNodeId());
            spqm.setReceiverIp(AppConfig.chordState.getNextNodeForKey(Integer.parseInt(message.getNodeId())).getIpAddress());
            spqm.setReceiverId(message.getReceiverId());
            MessageUtil.sendMessage(spqm);
        } else {
            List<Point> points = new ArrayList<>();
            points.addAll(fractalCreator.getMyTracePoints());
            points.addAll(fractalCreator.getCurrentWorkingPoints());
            points.addAll(fractalCreator.getTempPoints());
            fractalCreator.stopWorking();
            ReceivePointsQuitMessage rpqm = new ReceivePointsQuitMessage(message.getSenderPort(),
                    AppConfig.chordState.getNextNodeForKey(Integer.parseInt(message.getReceiverId())).getListenerPort());
            rpqm.setNodeId(message.getReceiverId());
            rpqm.setPoints(points);
            rpqm.setReceiverIp(AppConfig.chordState.getNextNodeForKey(Integer.parseInt(message.getReceiverId())).getIpAddress());
            MessageUtil.sendMessage(rpqm);
        }
    }
}
