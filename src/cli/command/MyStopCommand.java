package cli.command;

import app.AppConfig;
import app.fractals.FractalCreator;
import app.fractals.FractalCreatorInfo;
import servent.message.JobStoppedMessage;
import servent.message.util.MessageUtil;

public class MyStopCommand implements CLICommand {

    private FractalCreator fractalCreator;

    public MyStopCommand(FractalCreator fractalCreator) {
        this.fractalCreator = fractalCreator;
    }

    @Override
    public String commandName() {
        return "stop_job";
    }

    @Override
    public void execute(String args) {
        if (args == null) {
            AppConfig.timestampedErrorPrint("Stop command error: You must specify a job name.");
            return;
        }
        String[] argsArray = args.split(" ");
        switch (argsArray.length) {
            case 1:
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
                try {
                    JobStoppedMessage jsm = new JobStoppedMessage(AppConfig.myServentInfo.getListenerPort(), AppConfig.chordState.getNextNodePort());
                    jsm.setReceiverIp(AppConfig.chordState.getNextNodeIp());
                    MessageUtil.sendMessage(jsm);
                } catch (NullPointerException ignore) {

                }

                break;
            default:
                AppConfig.timestampedErrorPrint("Invalid arguments for stop command.");
        }
    }
}
