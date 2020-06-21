package cli.command;

import app.AppConfig;
import app.fractals.FractalCreator;
import servent.message.FractalResultMessage;
import servent.message.JobResultMessage;
import servent.message.util.MessageUtil;

import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class MyResultCommand implements CLICommand {

    private FractalCreator fractalCreator;

    public MyResultCommand(FractalCreator fractalCreator) {
        this.fractalCreator = fractalCreator;
    }

    @Override
    public String commandName() {
        return "result";
    }

    @Override
    public void execute(String args) {
        if (args == null) {
            AppConfig.timestampedErrorPrint("Invalid arguments for result command.");
        }
        else {
            String[] argsArray = args.split(" ");
            switch (argsArray.length) {
                case 1:
                    if (!argsArray[0].equals(AppConfig.myFractalCreatorInfo.getJobName())) {
                        AppConfig.timestampedErrorPrint("Result command error: Job " + argsArray[0] + " not found.");
                        return;
                    }
                    if (AppConfig.chordState.getFractalsIds().size() == 1) {
                        fractalCreator.printResult(fractalCreator.getMyTracePoints());
                        return;
                    }
                    try {
                        JobResultMessage jrm = new JobResultMessage(AppConfig.myServentInfo.getListenerPort(), AppConfig.chordState.getNextNodePort());
                        jrm.setJobName(argsArray[0]);
                        jrm.setReceiverIp(AppConfig.chordState.getNextNodeIp());
                        MessageUtil.sendMessage(jrm);
                    } catch (Exception e) {
                        if (AppConfig.myFractalCreatorInfo.getJobName().equals(argsArray[0])) {
                            List<Point> points = new ArrayList<>();
                            points.addAll(fractalCreator.getMyTracePoints());
                            points.addAll(fractalCreator.getCurrentWorkingPoints());
                            fractalCreator.printResult(points);
                        }
                    }
                    break;
                case 2:
                    if (!argsArray[0].equals(AppConfig.myFractalCreatorInfo.getJobName())) {
                        AppConfig.timestampedErrorPrint("Result command error: Job " + argsArray[0] + " not found.");
                        return;
                    }
                    String fractalId = argsArray[1];
                    AtomicInteger nodeToAsk = new AtomicInteger(-1);
                    AppConfig.chordState.getFractalsIds().entrySet().forEach(entry -> {
                        if (entry.getValue().equals(fractalId)) {
                            nodeToAsk.set(Integer.parseInt(entry.getKey()));
                        }
                    });
                    if (nodeToAsk.get() == -1) {
                        AppConfig.timestampedErrorPrint("Result command error: Could not find node with given fractalId " + fractalId);
                    } else {
                        if (nodeToAsk.get() != AppConfig.myServentInfo.getChordId()) {
                            FractalResultMessage frm = new FractalResultMessage(AppConfig.myServentInfo.getListenerPort(), AppConfig.chordState.getNextNodeForKey(nodeToAsk.get()).getListenerPort());
                            frm.setJobName(argsArray[0]);
                            frm.setReceiverIp(AppConfig.chordState.getNextNodeForKey(nodeToAsk.get()).getIpAddress());
                            frm.setResponseNodeId(String.valueOf(AppConfig.myServentInfo.getChordId()));
                            frm.setNodeId(String.valueOf(nodeToAsk.get()));
                            MessageUtil.sendMessage(frm);
                        } else {
                            fractalCreator.printResult(fractalCreator.getMyTracePoints());
                        }
                    }
                    break;
                default:
                    AppConfig.timestampedErrorPrint("Invalid arguments for result command.");
            }
        }
    }
}
