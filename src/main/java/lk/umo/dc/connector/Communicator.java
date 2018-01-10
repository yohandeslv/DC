package lk.umo.dc.connector;

import java.io.IOException;
import java.net.SocketException;
import java.net.UnknownHostException;

/**
 * Created by dev on 11/10/16.
 */
public interface Communicator {

    /**
     * @param host ip address/ host name
     * @param port port
     * @param message message
     * @return response message
     * */
    String sendMessage(String host, int port, String message) throws IOException;
}
