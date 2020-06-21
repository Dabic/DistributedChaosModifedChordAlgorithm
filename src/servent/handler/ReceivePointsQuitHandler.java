package servent.handler;

import app.AppConfig;
import app.fractals.FractalCreator;
import servent.message.Message;
import servent.message.ReceivePointsQuitMessage;
import servent.message.ReceivePointsQuitResponseMessage;
import servent.message.util.MessageUtil;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class ReceivePointsQuitHandler implements MessageHandler {
    private ReceivePointsQuitMessage message;
    private FractalCreator fractalCreator;

    public ReceivePointsQuitHandler(Message message, FractalCreator fractalCreator) {
        this.message = (ReceivePointsQuitMessage) message;
        this.fractalCreator = fractalCreator;
    }

    @Override
    public void run() {
        if (Integer.parseInt(message.getNodeId()) != AppConfig.myServentInfo.getChordId()) {
            ReceivePointsQuitMessage rpqm = new ReceivePointsQuitMessage(message.getSenderPort(),
                    AppConfig.chordState.getNextNodeForKey(Integer.parseInt(message.getNodeId())).getListenerPort());
            rpqm.setPoints(message.getPoints());
            rpqm.setNodeId(message.getNodeId());
            rpqm.setReceiverIp(AppConfig.chordState.getNextNodeForKey(Integer.parseInt(message.getNodeId())).getIpAddress());
            MessageUtil.sendMessage(rpqm);
        } else {
            // String fractalId = AppConfig.chordState.getFractalsIds().get(String.valueOf(AppConfig.myServentInfo.getChordId()));
            // List<Point> startingPoints = fractalCreator.findStartingPointsForFractalId(fractalId.substring(0, fractalId.length() - 1));
            //List<Point> allPoints = new ArrayList<>();
//            allPoints.addAll(fractalCreator.getCurrentWorkingPoints());
//            allPoints.addAll(fractalCreator.getMyTracePoints());
            //fractalCreator.startWorking(startingPoints, allPoints);
            fractalCreator.getTempPoints().addAll(message.getPoints());
            fractalCreator.getTempPoints().addAll(fractalCreator.getMyTracePoints());
            fractalCreator.getTempPoints().addAll(fractalCreator.getCurrentWorkingPoints());
            fractalCreator.stop();


            ReceivePointsQuitResponseMessage rpqrm = new ReceivePointsQuitResponseMessage(message.getSenderPort(), AppConfig.chordState.getNextNodePort());
            rpqrm.setReceiverIp(AppConfig.chordState.getNextNodeIp());
            MessageUtil.sendMessage(rpqrm);
        }
    }
}
