package cli.command;

import app.AppConfig;
import app.fractals.FractalCreator;
import app.fractals.FractalCreatorInfo;
import servent.message.JobCreatedMessage;
import servent.message.JobStartedMessage;
import servent.message.util.MessageUtil;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class MyStartCommand implements CLICommand {
    private final FractalCreator fractalCreator;

    public MyStartCommand(FractalCreator fractalCreator) {
        this.fractalCreator = fractalCreator;
    }

    @Override
    public String commandName() {
        return "start_job";
    }

    @Override
    public void execute(String args) {
        if (AppConfig.myFractalCreatorInfo.isWorking()) {
            AppConfig.timestampedErrorPrint("Couldn't start job. Job " + AppConfig.myFractalCreatorInfo.getJobName() + " is already working.");
            return;
        }
        if (args == null) {
            System.out.println(AppConfig.chordState.getFractalsIds());
            // fractalCreator.startWorking();
            // 0,0,500,0,0,500,500,500
            FractalCreatorInfo fractalCreatorInfo = createJobFromCLI();
            if (fractalCreatorInfo != null) {
                try {
                    AppConfig.myFractalCreatorInfo = fractalCreatorInfo;
                    JobCreatedMessage jcm = new JobCreatedMessage(AppConfig.myServentInfo.getListenerPort(), AppConfig.chordState.getNextNodePort());
                    jcm.setReceiverIp(AppConfig.chordState.getNextNodeIp());
                    jcm.setFractalCreatorInfo(fractalCreatorInfo);
                    MessageUtil.sendMessage(jcm);
                } catch (NullPointerException e) {
                    List<Point> staringPoints = fractalCreator.findStartingPointsForFractalId(AppConfig.chordState.getFractalsIds().get(String.valueOf(AppConfig.myServentInfo.getChordId())));
                    System.out.println(AppConfig.chordState.getFractalsIds());
                    fractalCreator.startWorking(staringPoints, null);
                    AppConfig.myFractalCreatorInfo.setWorking(true);
                }
            }
        } else {
            String[] argsArray = args.split(" ");
            if (argsArray.length == 1) {
                if (argsArray[0].equals(AppConfig.myFractalCreatorInfo.getJobName())) {
                    AppConfig.myFractalCreatorInfo.setWorking(true);
                    try {
                        if (!AppConfig.chordState.getFractalsIds().get(String.valueOf(AppConfig.myServentInfo.getChordId())).equals("idle")) {
                            List<Point> staringPoints = fractalCreator.findStartingPointsForFractalId(AppConfig.chordState.getFractalsIds().get(String.valueOf(AppConfig.myServentInfo.getChordId())));
                            fractalCreator.startWorking(staringPoints, null);
                            AppConfig.myFractalCreatorInfo.setWorking(true);
                        }
                        JobStartedMessage message = new JobStartedMessage(AppConfig.myServentInfo.getListenerPort(), AppConfig.chordState.getNextNodePort());
                        message.setReceiverIp(AppConfig.chordState.getNextNodeIp());
                        MessageUtil.sendMessage(message);
                    } catch (NullPointerException ignore) {
                    }
                } else {
                    AppConfig.timestampedErrorPrint("Start command error: Job " + argsArray[0] + " could not be found.");
                }
            } else {
                AppConfig.timestampedErrorPrint("Invalid arguments for start command.");
            }
        }
    }

    public FractalCreatorInfo createJobFromCLI() {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Enter job name: ");
        String jobName = scanner.nextLine();

        System.out.print("Enter job point count (3-10): ");
        String pointCount = scanner.nextLine();
        try {
            int countTest = Integer.parseInt(pointCount);
            if (countTest < 3 || countTest > 10) {
                AppConfig.timestampedErrorPrint("Point count must be a number from 3 to 10.");
                return null;
            }
        } catch (NumberFormatException e) {
            AppConfig.timestampedErrorPrint("Could not parse point count value.");
            return null;
        }

        System.out.print("Enter job proportion (0-1, decimal): ");
        String proportion = scanner.nextLine();
        try {
            double proportionTest = Double.parseDouble(proportion);
            if (proportionTest >= 1 || proportionTest <= 0) {
                AppConfig.timestampedErrorPrint("Proportion must be decimal (between 0 and 1).");
                return null;
            }
        } catch (NumberFormatException e) {
            AppConfig.timestampedErrorPrint("Could not parse proportion value.");
            return null;
        }

        System.out.print("Enter job width: ");
        String width = scanner.nextLine();
        try {
            int widthTest = Integer.parseInt(width);
        } catch (NumberFormatException e) {
            AppConfig.timestampedErrorPrint("Could not parse job width.");
            return null;
        }

        System.out.print("Enter job height: ");
        String height = scanner.nextLine();
        try {
            int heightTest = Integer.parseInt(height);
        } catch (NumberFormatException e) {
            AppConfig.timestampedErrorPrint("Could not parse job height.");
            return null;
        }

        System.out.print("Enter job starting points coordinates (ex. 500,0,0,500,1000,500): ");
        String coords = scanner.nextLine();
        String[] coordsArray = coords.split(",");
        if (coordsArray.length < Integer.parseInt(pointCount) * 2) {
            AppConfig.timestampedErrorPrint("Number of coordinates must be equal to point count*2.");
            return null;
        }
        List<Point> points = new ArrayList<>();
        for (int i = 0; i < coordsArray.length; i = i + 2) {
            try {
                points.add(new Point(Integer.parseInt(coordsArray[i]), Integer.parseInt(coordsArray[i + 1])));
            } catch (NumberFormatException e) {
                AppConfig.timestampedErrorPrint("Could not parse coordinates. Error at coordinate pair (" + coordsArray[i] + ", " + coordsArray[i + 1] + ").");
                return null;
            }
        }
        FractalCreatorInfo fractalCreatorInfo = new FractalCreatorInfo();
        fractalCreatorInfo.setPoints(points);
        fractalCreatorInfo.setPointCount(Integer.parseInt(pointCount));
        fractalCreatorInfo.setHeight(Integer.parseInt(height));
        fractalCreatorInfo.setWidth(Integer.parseInt(width));
        fractalCreatorInfo.setProportion(Double.parseDouble(proportion));
        fractalCreatorInfo.setJobName(jobName);
        return fractalCreatorInfo;
    }
}
