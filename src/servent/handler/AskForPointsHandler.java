package servent.handler;

import app.AppConfig;
import app.fractals.FractalCreator;
import servent.message.AskForPointsMessage;
import servent.message.Message;
import servent.message.SendPointsMessage;
import servent.message.util.MessageUtil;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class AskForPointsHandler implements MessageHandler {

    private final AskForPointsMessage afpm;
    private final FractalCreator fractalCreator;

    public AskForPointsHandler(Message message, FractalCreator fractalCreator) {
        afpm = (AskForPointsMessage)message;
        this.fractalCreator = fractalCreator;
    }

    @Override
    public void run() {
        if (afpm.getReceiverInfo().getChordId() != AppConfig.myServentInfo.getChordId()) {
            AskForPointsMessage msg = new AskForPointsMessage(afpm.getSenderPort(), AppConfig.chordState.getNextNodeForKey(afpm.getReceiverInfo().getChordId()).getListenerPort());
            msg.setFractals(afpm.getFractals());
            msg.setReceiverIp(AppConfig.chordState.getNextNodeForKey(afpm.getReceiverInfo().getChordId()).getIpAddress());
            msg.setReceiverInfo(afpm.getReceiverInfo());
            MessageUtil.sendMessage(msg);
        } else {
            List<Point> myStartingPoints = fractalCreator.findStartingPointsForFractalId(
                    AppConfig.chordState.getFractalsIds().get(String.valueOf(AppConfig.myServentInfo.getChordId())));
            fractalCreator.setCurrentWorkingPoints(myStartingPoints);
            for (Map.Entry<String, String> entry : afpm.getFractals().entrySet()) {
                List<Point> startingPoints = fractalCreator.findStartingPointsForFractalId(entry.getValue());
                List<Point> tracePoints = fractalCreator.findAllTracePoints(fractalCreator.getMyTracePoints(), startingPoints);

                SendPointsMessage spm = new SendPointsMessage(AppConfig.myServentInfo.getListenerPort(),
                        AppConfig.chordState.getNextNodeForKey(Integer.parseInt(entry.getKey())).getListenerPort());
                spm.setNodeId(Integer.parseInt(entry.getKey()));
                spm.setReceiverIp(AppConfig.chordState.getNextNodeForKey(Integer.parseInt(entry.getKey())).getIpAddress());
                spm.setStartingPoints(startingPoints);
                spm.setTracePoints(tracePoints);
                MessageUtil.sendMessage(spm);
            }
            fractalCreator.deletePoints();
        }
    }
}
