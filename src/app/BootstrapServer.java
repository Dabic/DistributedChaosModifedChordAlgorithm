package app;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.*;

public class BootstrapServer {

    private volatile boolean working = true;
    private Map<Integer, Integer> activeServents;
    private Map<Integer, String> idsAndIps;

    private class CLIWorker implements Runnable {
        @Override
        public void run() {
            Scanner sc = new Scanner(System.in);

            String line;
            while (true) {
                line = sc.nextLine();

                if (line.equals("stop")) {
                    working = false;
                    break;
                }
            }

            sc.close();
        }
    }

    public BootstrapServer() {
        activeServents = new HashMap<>();
        idsAndIps = new HashMap<>();
    }

    public void doBootstrap(int bsPort) {
        Thread cliThread = new Thread(new CLIWorker());
        cliThread.start();

        ServerSocket listenerSocket = null;
        try {
            listenerSocket = new ServerSocket(bsPort);
            listenerSocket.setSoTimeout(1000);
        } catch (IOException e1) {
            AppConfig.timestampedErrorPrint("Problem while opening listener socket.");
            System.exit(0);
        }

        Random rand = new Random(System.currentTimeMillis());

        while (working) {
            try {
                Socket newServentSocket = listenerSocket.accept();

                /*
                 * Handling these messages is intentionally sequential, to avoid problems with
                 * concurrent initial starts.
                 *
                 * In practice, we would have an always-active backbone of servents to avoid this problem.
                 */

                Scanner socketScanner = new Scanner(newServentSocket.getInputStream());
                String message = socketScanner.nextLine();

                /*
                 * New servent has hailed us. He is sending us his own listener port.
                 * He wants to get a listener port from a random active servent,
                 * or -1 if he is the first one.
                 */
                if (message.equals("Hail")) {
                    int newServentPort = socketScanner.nextInt();
                    String serventIp = socketScanner.next();
                    boolean hasPort = false;
                    for (int i = 0; i < activeServents.size(); i++) {
                        if (activeServents.get(i) == newServentPort) {
                            hasPort = true;
                            break;
                        }
                    }

                    PrintWriter socketWriter = new PrintWriter(newServentSocket.getOutputStream());
                    if (hasPort) {
                        socketWriter.write("-5\n");
                        socketWriter.flush();
                        newServentSocket.close();
                    } else {

                        if (activeServents.size() == 0) {

                            socketWriter.write(-1 + "\n" + -1 + "\n" + 0 + "\n");
                            activeServents.put(0, newServentPort); //first one doesn't need to confirm
                            idsAndIps.put(0, serventIp);
                        } else {
                            System.out.println("got " + activeServents.get(activeServents.size() - 1) + " " +
                                    idsAndIps.get(activeServents.size() - 1) + " " +
                                    activeServents.size() + " ");
                            socketWriter.write(
                                    activeServents.get(activeServents.size() - 1) + "\n" +
                                            idsAndIps.get(activeServents.size() - 1) + "\n" +
                                            activeServents.size() + "\n");
                        }

                        socketWriter.flush();
                        newServentSocket.close();
                    }
                } else if (message.equals("New")) {
                    /**
                     * When a servent is confirmed not to be a collider, we add him to the list.
                     */
                    int newServentPort = socketScanner.nextInt();
                    int serventId = Integer.parseInt(socketScanner.next());
                    String ip = socketScanner.next();

                    activeServents.put(serventId, newServentPort);
                    idsAndIps.put(serventId, ip);
                    newServentSocket.close();
                } else if (message.equals("Quit")) {
                    PrintWriter socketWriter = new PrintWriter(newServentSocket.getOutputStream());
                    try {
                        int id = Integer.parseInt(socketScanner.nextLine());
                        Map<Integer, Integer> newActiveServents = new HashMap<>();
                        Map<Integer, Integer> newActiveServents2 = new HashMap<>();
                        activeServents.forEach((key, value) -> {
                            if (key != id) {
                                newActiveServents.put(key, value);
                            }
                        });
                        newActiveServents.forEach((key, value) -> {
                            if (key > id) {
                                newActiveServents2.put(key-1, value);
                            } else {
                                newActiveServents2.put(key, value);
                            }
                        });
                        Map<Integer, String> newIps = new HashMap<>();
                        Map<Integer, String> newIps2 = new HashMap<>();
                        idsAndIps.forEach((key, value) -> {
                            if (key != id) {
                                newIps.put(key, value);
                            }
                        });
                        newIps.forEach((key, value) -> {
                            if (key > id) {
                                newIps2.put(key-1 , value);
                            } else {
                                newIps2.put(key, value);
                            }
                        });
                        idsAndIps = newIps2;
                        activeServents = newActiveServents2;
                        AppConfig.timestampedStandardPrint("Got quit message for id " + id + " " + activeServents + " " + idsAndIps);
                        socketWriter.write("Ok\n");
                        socketWriter.flush();
                        newServentSocket.close();
                    } catch (NumberFormatException e) {
                        AppConfig.timestampedErrorPrint("Error reading message");
                        socketWriter.write("Not\n");
                        socketWriter.flush();
                        newServentSocket.close();
                    }
                }

            } catch (SocketTimeoutException e) {

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Expects one command line argument - the port to listen on.
     */
    public static void main(String[] args) {
        if (args.length != 1) {
            AppConfig.timestampedErrorPrint("Bootstrap started without port argument.");
        }

        int bsPort = 0;
        try {
            bsPort = Integer.parseInt(args[0]);
        } catch (NumberFormatException e) {
            AppConfig.timestampedErrorPrint("Bootstrap port not valid: " + args[0]);
            System.exit(0);
        }

        AppConfig.timestampedStandardPrint("Bootstrap server started on port: " + bsPort);

        BootstrapServer bs = new BootstrapServer();
        bs.doBootstrap(bsPort);
    }
}
