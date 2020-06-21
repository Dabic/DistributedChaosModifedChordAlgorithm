package cli.command;

import app.AppConfig;
import app.fractals.FractalCreator;
import servent.message.FractalStatusMessage;
import servent.message.JobStatusMessage;
import servent.message.util.MessageUtil;

import java.util.concurrent.atomic.AtomicInteger;

public class MyStatusCommand implements CLICommand {

    private FractalCreator fractalCreator;

    public MyStatusCommand(FractalCreator fractalCreator) {
        this.fractalCreator = fractalCreator;
    }

    @Override
    public String commandName() {
        return "status";
    }

    @Override
    public void execute(String args) {
        if (args == null) {
            try {
                if (AppConfig.chordState.getFractalsIds().size() == 1) {
                    AppConfig.timestampedStandardPrint("Status result: " + fractalCreator.getStatus());
                } else {
                    JobStatusMessage jsm = new JobStatusMessage(AppConfig.myServentInfo.getListenerPort(), AppConfig.chordState.getNextNodePort());
                    jsm.setReceiverIp(AppConfig.chordState.getNextNodeIp());
                    MessageUtil.sendMessage(jsm);
                }
            } catch (NullPointerException e) {
                AppConfig.timestampedStandardPrint("Status result: " + fractalCreator.getStatus());
            }
        } else {
            String[] argsArray = args.split(" ");
            switch (argsArray.length) {
                case 1:
                    if (!args.equals(AppConfig.myFractalCreatorInfo.getJobName())) {
                        AppConfig.timestampedErrorPrint("Status command error: Job " + argsArray[0] + " not found.");
                        return;
                    }
                    if (AppConfig.chordState.getFractalsIds().size() == 1) {
                        AppConfig.timestampedStandardPrint("Status result: " + fractalCreator.getStatus());
                        return;
                    }
                   try {
                       JobStatusMessage jsm = new JobStatusMessage(AppConfig.myServentInfo.getListenerPort(), AppConfig.chordState.getNextNodePort());
                       jsm.setJobName(args);
                       jsm.setReceiverIp(AppConfig.chordState.getNextNodeIp());
                       MessageUtil.sendMessage(jsm);
                       break;
                   } catch (NullPointerException e) {
                       AppConfig.timestampedStandardPrint("Status result: " + fractalCreator.getStatus());
                       return;
                   }
                case 2:
                    if (!args.split(" ")[0].equals(AppConfig.myFractalCreatorInfo.getJobName())) {
                        AppConfig.timestampedErrorPrint("Status command error: Job " + argsArray[0] + " not found.");
                        return;
                    }
                    if (AppConfig.chordState.getFractalsIds().size() == 1) {
                        AppConfig.timestampedStandardPrint("Status result: " + fractalCreator.getStatus());
                        return;
                    }
                    String fractalId = args.split(" ")[1];
                    if (AppConfig.chordState.getFractalsIds().get(String.valueOf(AppConfig.myServentInfo.getChordId())).equals(fractalId)) {
                        AppConfig.timestampedStandardPrint("Status result: " + fractalCreator.getStatus());
                        return;
                    }
                    AtomicInteger nodeToAsk = new AtomicInteger(-1);
                    AppConfig.chordState.getFractalsIds().entrySet().forEach(entry -> {
                        if (entry.getValue().equals(fractalId)) {
                            nodeToAsk.set(Integer.parseInt(entry.getKey()));
                        }
                    });
                    if (nodeToAsk.get() == -1) {
                        AppConfig.timestampedErrorPrint("Status command error: Could not find node with given fractalId " + fractalId);
                    } else {
                        FractalStatusMessage fsm = new FractalStatusMessage(AppConfig.myServentInfo.getListenerPort(), AppConfig.chordState.getNextNodeForKey(nodeToAsk.get()).getListenerPort());
                        fsm.setFractalId(fractalId);
                        fsm.setReceiverIp(AppConfig.chordState.getNextNodeForKey(nodeToAsk.get()).getIpAddress());
                        fsm.setNodeId(String.valueOf(nodeToAsk.get()));
                        fsm.setRequestNodeId(String.valueOf(AppConfig.myServentInfo.getChordId()));
                        fsm.setJobName(args.split(" ")[0]);
                        MessageUtil.sendMessage(fsm);
                    }

                    AppConfig.timestampedErrorPrint("Status " + args);
                    break;
                default:
                    AppConfig.timestampedErrorPrint("Invalid arguments for status command.");
            }
        }
    }

}
