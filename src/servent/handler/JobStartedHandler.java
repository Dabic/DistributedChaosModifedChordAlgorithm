package servent.handler;

import app.AppConfig;
import app.fractals.FractalCreator;
import servent.message.JobStartedMessage;
import servent.message.Message;
import servent.message.util.MessageUtil;

import java.awt.*;
import java.util.List;

public class JobStartedHandler implements MessageHandler {
    private final Message message;
    private final FractalCreator fractalCreator;

    public JobStartedHandler(Message message, FractalCreator fractalCreator) {
        this.message = message;
        this.fractalCreator = fractalCreator;
    }
    @Override
    public void run() {
        if (message.getSenderPort() != AppConfig.myServentInfo.getListenerPort()) {
            JobStartedMessage msg = new JobStartedMessage(message.getSenderPort(), AppConfig.chordState.getNextNodePort());
            msg.setReceiverIp(AppConfig.chordState.getNextNodeIp());
            MessageUtil.sendMessage(msg);
            AppConfig.myFractalCreatorInfo.setWorking(true);
            if (!AppConfig.chordState.getFractalsIds().get(String.valueOf(AppConfig.myServentInfo.getChordId())).equals("idle")) {
                List<Point> staringPoints = fractalCreator.findStartingPointsForFractalId(
                        AppConfig.chordState.getFractalsIds().get(String.valueOf(AppConfig.myServentInfo.getChordId())));
                fractalCreator.startWorking(staringPoints, null);
                AppConfig.myFractalCreatorInfo.setWorking(true);
            }
        }
    }
}
