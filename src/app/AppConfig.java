package app;

import app.fractals.FractalCreatorInfo;

import java.awt.*;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.List;

/**
 * This class contains all the global application configuration stuff.
 * @author bmilojkovic
 *
 */
public class AppConfig {

	/**
	 * Convenience access for this servent's information
	 */
	public static ServentInfo myServentInfo;
	public static FractalCreatorInfo myFractalCreatorInfo;
	public static FractalCreatorInfo defaultJob;

	/**
	 * Print a message to stdout with a timestamp
	 * @param message message to print
	 */
	public static void timestampedStandardPrint(String message) {
		DateFormat timeFormat = new SimpleDateFormat("HH:mm:ss");
		Date now = new Date();
		
		System.out.println(timeFormat.format(now) + " - " + message);
	}
	
	/**
	 * Print a message to stderr with a timestamp
	 * @param message message to print
	 */
	public static void timestampedErrorPrint(String message) {
		DateFormat timeFormat = new SimpleDateFormat("HH:mm:ss");
		Date now = new Date();
		
		System.err.println(timeFormat.format(now) + " - " + message);
	}
	
	public static boolean INITIALIZED = false;
	public static int BOOTSTRAP_PORT;
	public static String BOOTSTRAP_IP;
	public static int SERVENT_COUNT;
	
	public static ChordState chordState;
	
	/**
	 * Reads a config file. Should be called once at start of app.
	 * The config file should be of the following format:
	 * <br/>
	 * <code><br/>
	 * servent_count=3 			- number of servents in the system <br/>
	 * chord_size=64			- maximum value for Chord keys <br/>
	 * bs.port=2000				- bootstrap server listener port <br/>
	 * servent0.port=1100 		- listener ports for each servent <br/>
	 * servent1.port=1200 <br/>
	 * servent2.port=1300 <br/>
	 * 
	 * </code>
	 * <br/>
	 * So in this case, we would have three servents, listening on ports:
	 * 1100, 1200, and 1300. A bootstrap server listening on port 2000, and Chord system with
	 * max 64 keys and 64 nodes.<br/>
	 * 
	 * @param configName name of configuration file
	 * @param serventId id of the servent, as used in the configuration file
	 */
	public static void readConfig(String configName, int serventId){
		Properties properties = new Properties();
		try {
			properties.load(new FileInputStream(new File(configName)));
			
		} catch (IOException e) {
			timestampedErrorPrint("Couldn't open properties file. Exiting...");
			System.exit(0);
		}
		
		try {
			BOOTSTRAP_PORT = Integer.parseInt(properties.getProperty("bs.port"));
		} catch (NumberFormatException e) {
			timestampedErrorPrint("Problem reading bootstrap_port. Exiting...");
			System.exit(0);
		}

		try {
			BOOTSTRAP_IP = properties.getProperty("bs.ip");
		} catch (Exception e) {
			timestampedErrorPrint("Problem reading bootstrap ip. Exiting...");
			System.exit(0);
		}
		
		try {
			SERVENT_COUNT = Integer.parseInt(properties.getProperty("servent_count"));
		} catch (NumberFormatException e) {
			timestampedErrorPrint("Problem reading servent_count. Exiting...");
			System.exit(0);
		}
		
		try {
			int chordSize = Integer.parseInt(properties.getProperty("chord_size"));
			
			ChordState.CHORD_SIZE = chordSize;
			chordState = new ChordState();
			
		} catch (NumberFormatException e) {
			timestampedErrorPrint("Problem reading chord_size. Must be a number that is a power of 2. Exiting...");
			System.exit(0);
		}
		
		String portProperty = "servent"+serventId+".port";
		
		int serventPort = -1;
		
		try {
			serventPort = Integer.parseInt(properties.getProperty(portProperty));
		} catch (NumberFormatException e) {
			timestampedErrorPrint("Problem reading " + portProperty + ". Exiting...");
			System.exit(0);
		}
		String ip = null;
		try {
			ip = InetAddress.getLocalHost().getHostAddress();
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}
		if (ip == null) {
			AppConfig.timestampedErrorPrint("Something went wrong while creating servent info.");
			System.exit(-1);
		}
		myServentInfo = new ServentInfo(ip, serventPort);
		myFractalCreatorInfo = new FractalCreatorInfo();
		defaultJob = new FractalCreatorInfo();
		try {
			String jobName = properties.getProperty("job.name");
			myFractalCreatorInfo.setJobName(jobName);
			defaultJob.setJobName(jobName);
		} catch (Exception e) {
			timestampedErrorPrint("Problem reading job name. Exiting...");
			System.exit(0);
		}

		try {
			int pointCount = Integer.parseInt(properties.getProperty("job.point_count"));
			if (pointCount < 3 || pointCount > 10) {
				timestampedErrorPrint("Problem reading job point count. Must be an integer between 3 and 10. Exiting...");
				System.exit(0);
			}
			myFractalCreatorInfo.setPointCount(pointCount);
			defaultJob.setPointCount(pointCount);
		} catch (NumberFormatException e) {
			timestampedErrorPrint("Problem reading job point count. Exiting...");
			System.exit(0);
		}

		try {
			double proportion = Double.parseDouble(properties.getProperty("job.proportion"));
			if (proportion < 0 || proportion > 1) {
				timestampedErrorPrint("Problem reading job proportion. Must be a decimal number between 0 and 1. Exiting...");
				System.exit(0);
			}
			myFractalCreatorInfo.setProportion(proportion);
			defaultJob.setProportion(proportion);
		} catch (NumberFormatException e) {
			timestampedErrorPrint("Problem reading job proportion. Exiting...");
			System.exit(0);
		}

		try {
			int width = Integer.parseInt(properties.getProperty("job.width"));
			myFractalCreatorInfo.setWidth(width);
			defaultJob.setWidth(width);
		} catch (NumberFormatException e) {
			timestampedErrorPrint("Problem reading job width. Exiting...");
			System.exit(0);
		}

		try {
			int height = Integer.parseInt(properties.getProperty("job.height"));
			myFractalCreatorInfo.setHeight(height);
			defaultJob.setHeight(height);
		} catch (NumberFormatException e) {
			timestampedErrorPrint("Problem reading job height. Exiting...");
			System.exit(0);
		}

		try {
			List<Point> points = new ArrayList<>();
			String[] coords = properties.getProperty("job.point_coords").split(",");
			if (coords.length < myFractalCreatorInfo.getPointCount() * 2 || coords.length > myFractalCreatorInfo.getPointCount() * 2) {
				timestampedErrorPrint("Problem reading job coords. Coords count must me point_count * 2. Exiting...");
				System.exit(0);
			}
			for (int i = 0; i < coords.length; i=i+2) {
				Point point = new Point(Integer.parseInt(coords[i]), Integer.parseInt(coords[i+1]));
				points.add(point);
			}
			myFractalCreatorInfo.setPoints(points);
			defaultJob.setPoints(points);
		} catch (NumberFormatException | IndexOutOfBoundsException e) {
			timestampedErrorPrint("Problem reading job coords. Exiting...");
			System.exit(0);
		}
		myFractalCreatorInfo.setWorking(false);
		defaultJob.setWorking(false);
	}
	
}
