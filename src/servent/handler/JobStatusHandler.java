package servent.handler;

import app.AppConfig;
import app.fractals.FractalCreator;
import servent.message.Message;
import servent.message.JobStatusMessage;
import servent.message.util.MessageUtil;

public class JobStatusHandler implements MessageHandler {

    private JobStatusMessage message;
    private FractalCreator fractalCreator;

    public JobStatusHandler(Message message, FractalCreator fractalCreator) {
        this.message = (JobStatusMessage) message;
        this.fractalCreator = fractalCreator;
    }

    @Override
    public void run() {
        if (message.getJobName() == null || message.getJobName().equals(AppConfig.myFractalCreatorInfo.getJobName())) {
            String fractalId = AppConfig.chordState.getFractalsIds().get(String.valueOf(AppConfig.myServentInfo.getChordId()));
            if (!AppConfig.chordState.getFractalsIds().get(String.valueOf(AppConfig.myServentInfo.getChordId())).equals("idle")) {
                message.getResult().put(fractalId, fractalCreator.getStatus());
            }
            if (message.getSenderPort() != AppConfig.myServentInfo.getListenerPort()) {
                JobStatusMessage jsm = new JobStatusMessage(message.getSenderPort(), AppConfig.chordState.getNextNodePort());
                jsm.setResult(message.getResult());
                jsm.setReceiverIp(AppConfig.chordState.getNextNodeIp());
                MessageUtil.sendMessage(jsm);
            } else {
                AppConfig.timestampedStandardPrint("Status result (key -> fractalId, value -> point count) " + message.getResult());
            }
        }
    }
}
