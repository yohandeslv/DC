package lk.umo.dc.messaging;

import lk.umo.dc.connector.UdpCommunicator;
import lk.umo.dc.domain.model.PeerNode;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;

/**
 * Created by dev on 11/26/16.
 *
 * This class will create the connection with peer nodes
 */
public class Connect {

    private static final Logger LOGGER = LogManager.getLogger(Connect.class.getName());

    private static final String CONNECT_OK_KEYWORD = "CONNECTOK";
    private static final String DISCONNECT_OK_KEYWORD = "DISCONNECTOK";
    /**
     * Connect with a peer
     * */
    public static boolean connectWithPeer(PeerNode peerNode) {
        UdpCommunicator udpCommunicator = new UdpCommunicator();
        try {
            String connectResponse = udpCommunicator.sendMessage(peerNode.getIp(), peerNode.getPort(),
                    PeerMessageUtils.constructConnectMessage(peerNode));
            if (connectResponse.contains(CONNECT_OK_KEYWORD)) {
                return true;
            } else {
                LOGGER.error("Failed to connectWithPeer with peer: {}", peerNode);
            }
        } catch (IOException e) {
            LOGGER.error("Failed to connectWithPeer with peer: {}", peerNode, e);
        }
        return false;
    }

    /**
     * disconnect from peer node
     * */
    public static boolean disconnectFromPeer(PeerNode peerNode) {
        LOGGER.info("Disconnecting with node: {}", peerNode);
        UdpCommunicator udpCommunicator = new UdpCommunicator();
        try {
            String connectResponse = udpCommunicator.sendMessage(peerNode.getIp(), peerNode.getPort(),
                    PeerMessageUtils.constructDisconnectMessage(peerNode));
            if (connectResponse.contains(DISCONNECT_OK_KEYWORD)) {
                return true;
            } else {
                LOGGER.error("Failed to connectWithPeer with peer: {}", peerNode);
            }
        } catch (IOException e) {
            LOGGER.error("Failed to connectWithPeer with peer: {}", peerNode, e);
        }
        return false;
    }
}
