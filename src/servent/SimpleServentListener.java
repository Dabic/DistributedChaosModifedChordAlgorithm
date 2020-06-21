package servent;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import app.AppConfig;
import app.Cancellable;
import app.fractals.FractalCreator;
import servent.handler.*;
import servent.message.Message;
import servent.message.util.MessageUtil;

public class SimpleServentListener implements Runnable, Cancellable {

	private volatile boolean working = true;

	private final FractalCreator fractalCreator;
	
	public SimpleServentListener(FractalCreator fractalCreator) {
		this.fractalCreator = fractalCreator;
	}

	/*
	 * Thread pool for executing the handlers. Each client will get it's own handler thread.
	 */
	private final ExecutorService threadPool = Executors.newWorkStealingPool();
	
	@Override
	public void run() {
		ServerSocket listenerSocket = null;
		try {
			listenerSocket = new ServerSocket(AppConfig.myServentInfo.getListenerPort(), 100);
			/*
			 * If there is no connection after 1s, wake up and see if we should terminate.
			 */
			listenerSocket.setSoTimeout(1000);
		} catch (IOException e) {
			AppConfig.timestampedErrorPrint("Couldn't open listener socket on: " + AppConfig.myServentInfo.getListenerPort());
			System.exit(0);
		}
		
		
		while (working) {
			try {
				Message clientMessage;
				
				Socket clientSocket = listenerSocket.accept();
				
				//GOT A MESSAGE! <3
				clientMessage = MessageUtil.readMessage(clientSocket);
				
				MessageHandler messageHandler = new NullHandler(clientMessage);
				
				/*
				 * Each message type has it's own handler.
				 * If we can get away with stateless handlers, we will,
				 * because that way is much simpler and less error prone.
				 */
				switch (clientMessage.getMessageType()) {
					case NEW_NODE:
						messageHandler = new NewNodeHandler(clientMessage, fractalCreator);
						break;
					case WELCOME:
						messageHandler = new WelcomeHandler(clientMessage, fractalCreator);
						break;
					case SORRY:
						messageHandler = new SorryHandler(clientMessage);
						break;
					case UPDATE:
						messageHandler = new UpdateHandler(clientMessage);
						break;
					case PUT:
						messageHandler = new PutHandler(clientMessage);
						break;
					case ASK_GET:
						messageHandler = new AskGetHandler(clientMessage);
						break;
					case TELL_GET:
						messageHandler = new TellGetHandler(clientMessage);
						break;
					case POISON:
						break;
					case JOB_STARTED:
						messageHandler = new JobStartedHandler(clientMessage, fractalCreator);
						break;
					case UPDATE_FRACTAL_TABLE:
						messageHandler = new UpdateFractalTableHandler(clientMessage);
						break;
					case ASK_FOR_POINTS:
						messageHandler = new AskForPointsHandler(clientMessage, fractalCreator);
						break;
					case SEND_POINTS:
						messageHandler = new SendPointsHandler(clientMessage, fractalCreator);
						break;
					case JOB_STATUS:
						messageHandler = new JobStatusHandler(clientMessage, fractalCreator);
						break;
					case FRACTAL_STATUS:
						messageHandler = new FractalStatusHandler(clientMessage, fractalCreator);
						break;
					case FRACTAL_STATUS_RESPONSE:
						messageHandler = new FractalStatusResponseHandler(clientMessage);
						break;
					case JOB_RESULT:
						messageHandler = new JobResultHandler(clientMessage, fractalCreator);
						break;
					case FRACTAL_RESULT:
						messageHandler = new FractalResultHandler(clientMessage, fractalCreator);
						break;
					case FRACTAL_RESULT_RESPONSE:
						messageHandler = new FractalResultResponseHandler(clientMessage, fractalCreator);
						break;
					case JOB_CREATED:
						messageHandler = new JobCreatedHandler(clientMessage, fractalCreator);
						break;
					case JOB_STOPPED:
						messageHandler = new JobStoppedHandler(clientMessage, fractalCreator);
						break;
					case SEND_POINTS_QUIT:
						messageHandler = new SendPointsQuitHandler(clientMessage, fractalCreator);
						break;
					case RECEIVE_POINTS_QUIT:
						messageHandler = new ReceivePointsQuitHandler(clientMessage, fractalCreator);
						break;
					case RECEIVE_POINTS_QUIT_RESPONSE:
						messageHandler = new ReceivePointsQuitResponseHandler(clientMessage);
						break;
					case TRANSFER_POINTS_QUIT:
						messageHandler = new TransferPointsQuitHandler(clientMessage, fractalCreator);
						break;
					case ADD_TRANSFER_POINTS_QUIT:
						messageHandler = new AddTransferPointsQuitHandler(clientMessage, fractalCreator);
						break;
					case ADD_TRANSFER_POINTS_QUIT_RESPONSE:
						messageHandler = new AddTransferPointsQuitResponseHandler(clientMessage);
						break;
					case UPDATE_FRACTAL_QUIT:
						messageHandler = new UpdateFractalsQuitHandler(clientMessage, fractalCreator);
						break;
					case I_AM_GONE:
						messageHandler = new IAmGoneHandler(clientMessage, fractalCreator);
						break;
				}
				
				threadPool.submit(messageHandler);
			} catch (SocketTimeoutException timeoutEx) {
				//Uncomment the next line to see that we are waking up every second.
//				AppConfig.timedStandardPrint("Waiting...");
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	public void stop() {
		this.working = false;
	}

}
