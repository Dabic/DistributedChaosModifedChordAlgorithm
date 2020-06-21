package servent.handler;

import app.AppConfig;
import app.fractals.FractalCreator;
import servent.message.IAmGoneMessage;
import servent.message.Message;
import servent.message.util.MessageUtil;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class IAmGoneHandler implements MessageHandler {
    private IAmGoneMessage message;
    private FractalCreator fractalCreator;

    public IAmGoneHandler(Message message, FractalCreator fractalCreator) {
        this.message = (IAmGoneMessage) message;
        this.fractalCreator = fractalCreator;
    }

    @Override
    public void run() {
        if (message.getId() != null) {
            if (Integer.parseInt(message.getId()) != AppConfig.myServentInfo.getChordId()) {
                if (!AppConfig.chordState.getFractalsIds().get(String.valueOf(AppConfig.myServentInfo.getChordId())).equals("idle") && AppConfig.myFractalCreatorInfo.isWorking()) {
                    List<Point> points = new ArrayList<>();
                    points.addAll(fractalCreator.getCurrentWorkingPoints());
                    points.addAll(fractalCreator.getMyTracePoints());
                    points.addAll(fractalCreator.getTempPoints());
                    String fractalId = AppConfig.chordState.getFractalsIds().get(String.valueOf(AppConfig.myServentInfo.getChordId()));
                    List<Point> startingPoints = fractalCreator.findStartingPointsForFractalId(fractalId);
                    if (startingPoints.size() == 0) {
                        startingPoints = new ArrayList<>(AppConfig.myFractalCreatorInfo.getPoints());
                    }
                    fractalCreator.startWorking(startingPoints, points);
                    fractalCreator.setTempPoints(new ArrayList<>());

                }
                IAmGoneMessage igm = new IAmGoneMessage(AppConfig.myServentInfo.getListenerPort(), AppConfig.chordState.getNextNodePort());
                igm.setReceiverIp(AppConfig.chordState.getNextNodeIp());
                igm.setId(message.getId());
                MessageUtil.sendMessage(igm);
            } else {
                AppConfig.timestampedStandardPrint("Active");
            }
        } else {
            if (!AppConfig.chordState.getFractalsIds().get(String.valueOf(AppConfig.myServentInfo.getChordId())).equals("idle") && AppConfig.myFractalCreatorInfo.isWorking()) {
                List<Point> points = new ArrayList<>();
                points.addAll(fractalCreator.getCurrentWorkingPoints());
                points.addAll(fractalCreator.getMyTracePoints());
                points.addAll(fractalCreator.getTempPoints());
                String fractalId = AppConfig.chordState.getFractalsIds().get(String.valueOf(AppConfig.myServentInfo.getChordId()));
                List<Point> startingPoints = fractalCreator.findStartingPointsForFractalId(fractalId);
                if (startingPoints.size() == 0) {
                    startingPoints = new ArrayList<>(AppConfig.myFractalCreatorInfo.getPoints());
                }
                fractalCreator.startWorking(startingPoints, points);
                fractalCreator.setTempPoints(new ArrayList<>());
            }
            if (AppConfig.chordState.getFractalsIds().size() > 1) {
                IAmGoneMessage igm = new IAmGoneMessage(AppConfig.myServentInfo.getListenerPort(), AppConfig.chordState.getNextNodePort());
                igm.setReceiverIp(AppConfig.chordState.getNextNodeIp());
                igm.setId(String.valueOf(AppConfig.myServentInfo.getChordId()));
                MessageUtil.sendMessage(igm);
            } else {
                AppConfig.timestampedStandardPrint("Active");
            }
        }
    }
}
