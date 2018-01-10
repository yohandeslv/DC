package lk.umo.dc.connector;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

/**
 * Created by dev on 11/10/16.
 * <p>
 * This class is to send messages to bootstrap server
 */
public class TcpCommunicator implements Communicator {

    /**
     * @param host    ip address/ host name
     * @param port    port
     * @param message message
     * @return response message
     */
    @Override
    public String sendMessage(String host, int port, String message) throws IOException {
        Socket clientSocket = new Socket(host, port);
        DataOutputStream outToServer = new DataOutputStream(clientSocket.getOutputStream());

        outToServer.write(message.getBytes());
        
        BufferedReader inFromServer = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
        String response = inFromServer.readLine();

        outToServer.flush();
        outToServer.close();
        clientSocket.close();
        return response;
    }
}
