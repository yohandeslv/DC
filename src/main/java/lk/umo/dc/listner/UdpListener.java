package lk.umo.dc.listner;

import lk.umo.dc.config.NodeContext;
import lk.umo.dc.util.MessageResolver;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.net.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by dev on 11/10/16.
 *
 * This class is acting as a UDP server to receive messages from neighbors
 */
public class UdpListener extends Listener {

    private static final Logger LOGGER = LogManager.getLogger(UdpListener.class.getName());


    private static final int THREAD_POOL_SIZE = 10;

    private static Listener instance;

    private int port;

    private static Runnable serverTask;
    private static Thread serverThread;

    //Singleton
    private UdpListener() {
    }

    @Override
    public void initListener(final int port) {
        LOGGER.info("Starting UDP Listener...");

        final ExecutorService clientProcessingPool = Executors.newFixedThreadPool(THREAD_POOL_SIZE);

        if (serverTask == null) {
            serverTask = new Runnable() {
                @Override
                public void run() {
                    try {
                        DatagramSocket datagramSocket = new DatagramSocket(port);

                        while (true) {
                            byte[] receiveData = new byte[4096];
                            byte[] sendData = new byte[4096];
                            DatagramPacket datagramPacket = new DatagramPacket(receiveData, receiveData.length);
                            datagramSocket.receive(datagramPacket);
                            clientProcessingPool.submit(new ClientTask(datagramSocket, datagramPacket));
                        }
                        //TODO graceful shutdown
                    } catch (SocketException e) {
                        LOGGER.error("SocketException in server thread", e);
                    } catch (IOException e) {
                        LOGGER.error("IOException in server thread", e);
                    }
                }
            };
        }

        if (serverThread == null) {
            LOGGER.info("Starting UDP Listener...");
            serverThread = new Thread(serverTask);
            serverThread.start();
            LOGGER.info("UDP Listener started on port : {}", port);
        } else {
            if (!serverThread.isAlive()) {
                LOGGER.info("Starting UDP Listener...");
                serverThread.start();
                LOGGER.info("UDP Listener started on port : {}", port);
            }
        }

    }

    @Override
    public String processMessage(String message) {
        return MessageResolver.resolvePeerMessage(message.trim());
    }

    /**
     * Get current instance
     *
     * @return UdpListener instance
     * */
    public static Listener getInstance() {
        if (instance == null) {
            instance = new UdpListener();
        }
        return instance;
    }


    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }


    //client task executor
    private class ClientTask implements Runnable {
        private final DatagramSocket clientSocket;
        private final DatagramPacket datagramPacket;
        private byte[] receiveData = new byte[4096];
        private byte[] sendData = new byte[4096];

        private ClientTask(DatagramSocket clientSocket, DatagramPacket datagramPacket) {
            this.clientSocket = clientSocket;
            this.datagramPacket = datagramPacket;
        }

        @Override
        public void run() {
            try {
                LOGGER.debug("UDP client connected!");
                String receivedText = new String(datagramPacket.getData());
                LOGGER.debug("received: {}", receivedText);

                InetAddress senderAddress = datagramPacket.getAddress();
                int senderPort = datagramPacket.getPort();

                String response = processMessage(receivedText);
                DatagramPacket sendPacket =
                        new DatagramPacket(sendData, sendData.length, senderAddress, senderPort);
                sendPacket.setData(response.getBytes());
                clientSocket.send(sendPacket);
            } catch (IOException e) {
                LOGGER.error("IOException in client task", e);
            }
        }
    }
}
