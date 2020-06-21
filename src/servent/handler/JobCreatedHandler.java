package servent.handler;

import app.AppConfig;
import app.fractals.FractalCreator;
import servent.message.JobCreatedMessage;
import servent.message.JobStartedMessage;
import servent.message.Message;
import servent.message.util.MessageUtil;

import java.awt.*;
import java.util.List;

public class JobCreatedHandler implements MessageHandler {
    private JobCreatedMessage message;
    private FractalCreator fractalCreator;

    public JobCreatedHandler(Message message, FractalCreator fractalCreator) {
        this.message = (JobCreatedMessage) message;
        this.fractalCreator = fractalCreator;
    }

    @Override
    public void run() {
        if (message.getSenderPort() != AppConfig.myServentInfo.getListenerPort()) {
            AppConfig.myFractalCreatorInfo = message.getFractalCreatorInfo();
            AppConfig.chordState.resetFractalsTable();
            AppConfig.chordState.updateFractalsTable();
            JobCreatedMessage jcm = new JobCreatedMessage(message.getSenderPort(), AppConfig.chordState.getNextNodePort());
            jcm.setFractalCreatorInfo(message.getFractalCreatorInfo());
            jcm.setReceiverIp(AppConfig.chordState.getNextNodeIp());
            MessageUtil.sendMessage(jcm);
        } else {
            AppConfig.chordState.resetFractalsTable();
            AppConfig.chordState.updateFractalsTable();
            if (!AppConfig.chordState.getFractalsIds().get(String.valueOf(AppConfig.myServentInfo.getChordId())).equals("idle")) {
                List<Point> staringPoints = fractalCreator.findStartingPointsForFractalId(AppConfig.chordState.getFractalsIds().get(String.valueOf(AppConfig.myServentInfo.getChordId())));
                fractalCreator.startWorking(staringPoints, null);
            }
            AppConfig.myFractalCreatorInfo.setWorking(true);
            JobStartedMessage message = new JobStartedMessage(AppConfig.myServentInfo.getListenerPort(), AppConfig.chordState.getNextNodePort());
            message.setReceiverIp(AppConfig.chordState.getNextNodeIp());
            MessageUtil.sendMessage(message);
        }

    }
}
