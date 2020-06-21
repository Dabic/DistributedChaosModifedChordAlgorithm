package servent.handler;

import app.AppConfig;
import app.fractals.FractalCreator;
import servent.message.AddTransferPointsQuitMessage;
import servent.message.Message;
import servent.message.TransferPointsQuitMessage;
import servent.message.util.MessageUtil;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;


public class TransferPointsQuitHandler implements MessageHandler {
    private TransferPointsQuitMessage message;
    private FractalCreator fractalCreator;

    public TransferPointsQuitHandler(Message message, FractalCreator fractalCreator) {
        this.message = (TransferPointsQuitMessage) message;
        this.fractalCreator = fractalCreator;
    }

    @Override
    public void run() {
        if (Integer.parseInt(message.getSenderId()) != AppConfig.myServentInfo.getChordId()) {
            TransferPointsQuitMessage tpqm = new TransferPointsQuitMessage(message.getSenderPort(),
                    AppConfig.chordState.getNextNodeForKey(Integer.parseInt(message.getSenderId())).getListenerPort());
            tpqm.setPoints(message.getPoints());
            tpqm.setReceiverId(message.getReceiverId());
            tpqm.setSenderId(message.getSenderId());
            tpqm.setReceiverIp(AppConfig.chordState.getNextNodeForKey(Integer.parseInt(message.getSenderId())).getIpAddress());
            MessageUtil.sendMessage(tpqm);
        } else {
            List<Point> points = new ArrayList<>();
            points.addAll(fractalCreator.getMyTracePoints());
            points.addAll(fractalCreator.getCurrentWorkingPoints());
            fractalCreator.stop();
            AddTransferPointsQuitMessage atpqm = new AddTransferPointsQuitMessage(message.getSenderPort(),
                    AppConfig.chordState.getNextNodeForKey(Integer.parseInt(message.getReceiverId())).getListenerPort());
            atpqm.setNodeId(message.getReceiverId());
            atpqm.setPoints(points);
            atpqm.setReceiverIp(AppConfig.chordState.getNextNodeForKey(Integer.parseInt(message.getReceiverId())).getIpAddress());
            MessageUtil.sendMessage(atpqm);
        }
    }
}
