package servent.handler;

import app.AppConfig;
import app.fractals.FractalCreator;
import app.fractals.FractalCreatorInfo;
import servent.message.JobStoppedMessage;
import servent.message.Message;
import servent.message.util.MessageUtil;

public class JobStoppedHandler implements MessageHandler {
    private Message message;
    private FractalCreator fractalCreator;

    public JobStoppedHandler(Message message, FractalCreator fractalCreator) {
        this.message = message;
        this.fractalCreator = fractalCreator;
    }

    @Override
    public void run() {
        if (message.getSenderPort() != AppConfig.myServentInfo.getListenerPort()) {
            fractalCreator.stopWorking();
            FractalCreatorInfo fractalCreatorInfo = new FractalCreatorInfo();
            fractalCreatorInfo.setWorking(false);
            fractalCreatorInfo.setPoints(AppConfig.defaultJob.getPoints());
            fractalCreatorInfo.setHeight(AppConfig.defaultJob.getHeight());
            fractalCreatorInfo.setWidth(AppConfig.defaultJob.getWidth());
            fractalCreatorInfo.setProportion(AppConfig.defaultJob.getProportion());
            fractalCreatorInfo.setJobName(AppConfig.defaultJob.getJobName());
            fractalCreatorInfo.setPointCount(AppConfig.defaultJob.getPointCount());
            AppConfig.myFractalCreatorInfo = fractalCreatorInfo;
            AppConfig.chordState.resetFractalsTable();
            AppConfig.chordState.updateFractalsTable();
            JobStoppedMessage jsm = new JobStoppedMessage(message.getSenderPort(), AppConfig.chordState.getNextNodePort());
            jsm.setReceiverIp(AppConfig.chordState.getNextNodeIp());
            MessageUtil.sendMessage(jsm);
        }
    }
}
