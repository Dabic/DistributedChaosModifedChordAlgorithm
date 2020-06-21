package servent.handler;

import app.AppConfig;
import app.fractals.FractalCreator;
import servent.message.JobResultMessage;
import servent.message.Message;
import servent.message.util.MessageUtil;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class JobResultHandler implements MessageHandler {

    private JobResultMessage message;
    private FractalCreator fractalCreator;

    public JobResultHandler(Message message, FractalCreator fractalCreator) {
        this.message = (JobResultMessage) message;
        this.fractalCreator = fractalCreator;
    }

    @Override
    public void run() {
        if (message.getSenderPort() != AppConfig.myServentInfo.getListenerPort()) {
            if (message.getJobName().equals(AppConfig.myFractalCreatorInfo.getJobName())) {
                List<Point> allPoints = new ArrayList<>();
                allPoints.addAll(message.getPoints());
                allPoints.addAll(fractalCreator.getMyTracePoints());
                JobResultMessage jrm = new JobResultMessage(message.getSenderPort(), AppConfig.chordState.getNextNodePort());
                jrm.setPoints(allPoints);
                jrm.setJobName(message.getJobName());
                jrm.setReceiverIp(AppConfig.chordState.getNextNodeIp());
                MessageUtil.sendMessage(jrm);
            } else {
                JobResultMessage jrm = new JobResultMessage(message.getSenderPort(), AppConfig.chordState.getNextNodePort());
                jrm.setPoints(message.getPoints());
                jrm.setReceiverIp(AppConfig.chordState.getNextNodeIp());
                jrm.setJobName(message.getJobName());
                MessageUtil.sendMessage(jrm);
            }
        } else {
            List<Point> allPoints = new ArrayList<>();
            allPoints.addAll(message.getPoints());
            allPoints.addAll(fractalCreator.getMyTracePoints());
            fractalCreator.printResult(allPoints);
        }
    }
}
