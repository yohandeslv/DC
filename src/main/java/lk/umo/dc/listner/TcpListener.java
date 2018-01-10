package lk.umo.dc.listner;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import sun.rmi.runtime.Log;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by dev on 11/12/16.
 * //TODO remove this class
 *
 * This class is acting as a TCP server to receive messages from Bootstrap server
 */
public class TcpListener extends Listener {

    private static final Logger LOGGER = LogManager.getLogger(TcpListener.class.getName());

    private static final int THREAD_POOL_SIZE = 10;
    private static Listener instance;
    private static Runnable serverTask;
    private static Thread serverThread;

    //Singleton
    private TcpListener() {
    }

    @Override
    public void initListener(final int port) {

        final ExecutorService clientProcessingPool = Executors.newFixedThreadPool(THREAD_POOL_SIZE);

        if (serverTask == null) {
            serverTask = new Runnable() {
                @Override
                public void run() {
                    try {
                        ServerSocket serverSocket = new ServerSocket(port);
                        while (true) {
                            Socket clientSocket = serverSocket.accept();
                            clientProcessingPool.submit(new ClientTask(clientSocket));
                        }

                        //TODO graceful shutdown
                    } catch (IOException e) {
                        LOGGER.error("IOException in server thread", e);
                    }
                }
            };
        }

        if (serverThread == null) {
            LOGGER.info("Starting TCP Listener...");
            serverThread = new Thread(serverTask);
            serverThread.start();
        } else {
            if (!serverThread.isAlive()) {
                LOGGER.info("Starting TCP Listener...");
                serverThread.start();
            }
        }


        LOGGER.info("TCP Listener started on port : {}", port);
    }

    @Override
    public String processMessage(String message) {
        //TODO implement
        return "test";
    }

    /**
     * Get current instance
     *
     * @return TcpListener instance
     * */
    public static Listener getInstance() {
        if (instance == null) {
            instance = new TcpListener();
        }
        return instance;
    }

    private class ClientTask implements Runnable {
        private final Socket clientSocket;

        private ClientTask(Socket clientSocket) {
            this.clientSocket = clientSocket;
        }

        @Override
        public void run() {
            try {
                LOGGER.debug("TCP client connected!");
                BufferedReader inFromClient =
                        new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                DataOutputStream outToClient = new DataOutputStream(clientSocket.getOutputStream());
                String receivedText = inFromClient.readLine();
                LOGGER.debug("received: {}", receivedText);
                String response = processMessage(receivedText);
                outToClient.writeBytes(response);

                clientSocket.close();
            } catch (IOException e) {
                LOGGER.error("IOException in client task", e);
            }
        }
    }
}
