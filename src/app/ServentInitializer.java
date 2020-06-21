package app;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;

import servent.message.NewNodeMessage;
import servent.message.util.MessageUtil;

public class ServentInitializer implements Runnable {

	private String getSomeServentPort() {
		int bsPort = AppConfig.BOOTSTRAP_PORT;
		
		int retVal = -2;
		int id = 0;
		String result = "";
		String ip = null;
		
		try {
			Socket bsSocket = new Socket(AppConfig.BOOTSTRAP_IP, bsPort);
			
			PrintWriter bsWriter = new PrintWriter(bsSocket.getOutputStream());
			bsWriter.write("Hail\n" + AppConfig.myServentInfo.getListenerPort() + "\n" + AppConfig.myServentInfo.getIpAddress() + "\n");
			bsWriter.flush();
			
			Scanner bsScanner = new Scanner(bsSocket.getInputStream());
			retVal = bsScanner.nextInt();
			if (retVal == -5) {
				AppConfig.timestampedStandardPrint("Bootstrap says that I must change my port. Exiting...");
				System.exit(0);
			}
			ip = bsScanner.next();
			id = Integer.parseInt(bsScanner.next());
			bsSocket.close();
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		result = retVal + "," + id + "," + ip;
		return result;
	}
	//update, welcome, new node
	@Override
	public void run() {
		String[] someServentPortAndId = getSomeServentPort().split(",");
		int port = Integer.parseInt(someServentPortAndId[0]);
		int id = Integer.parseInt(someServentPortAndId[1]);
		if (port == -2) {
			AppConfig.timestampedErrorPrint("Error in contacting bootstrap. Exiting...");
			System.exit(0);
		}
		if (port == -1) { //bootstrap gave us -1 -> we are first
			AppConfig.timestampedStandardPrint("First node in Chord system.");
			AppConfig.myServentInfo.setOriginalId(0);
		} else { //bootstrap gave us something else - let that node tell our successor that we are here
			String ip = someServentPortAndId[2];
			System.out.println("Servent initialiser new node reciver ip " + ip);
			AppConfig.myServentInfo.setChordId(id);
			AppConfig.myServentInfo.setOriginalId(id);
			NewNodeMessage nnm = new NewNodeMessage(AppConfig.myServentInfo.getListenerPort(), port);
			nnm.setSenderIp(AppConfig.myServentInfo.getIpAddress());
			nnm.setReceiverIp(ip);
			nnm.setId(id);
			MessageUtil.sendMessage(nnm);
		}
	}

}
