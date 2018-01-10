package lk.umo.dc.connector;

import lk.umo.dc.config.NodeContext;
import lk.umo.dc.domain.model.PeerNode;
import lk.umo.dc.messaging.PeerMessageUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.util.List;

/**
 * Created by dev on 12/10/16.
 */
public class HeartBeatDetector extends Thread {

    private static final Logger LOGGER = LogManager.getLogger(HeartBeatDetector.class.getName());

    private static final int HEART_BEAT_INTERVAL = 30000;
    private static final String HEART_BEAT_RESPONSE_KEYWORD = "PINGOK";

    @Override
    public synchronized void start() {
        LOGGER.debug("Starting heartbeat...");
        super.start();
    }

    @Override
    public void run() {
        try {
            while (true) {
                Thread.sleep(HEART_BEAT_INTERVAL);
                List<PeerNode> children = NodeContext.getChildren();
                UdpCommunicator udpCommunicator = new UdpCommunicator();
                for (PeerNode child : children) {
                    String message = PeerMessageUtils.constructHeartBeatMessage();
                    try {
                        String response = udpCommunicator.sendMessage(child.getIp(), child.getPort(), message);
                        if (!response.contains(HEART_BEAT_RESPONSE_KEYWORD)) {
                            LOGGER.info("Heartbeat failed. removing child {}", child);
                            NodeContext.removeChild(child);
                        }
                    } catch (IOException e) {
                        //Could not connect to child, so will remove
                        LOGGER.info("Heartbeat failed. removing child {}", child);
                        NodeContext.removeChild(child);
                    }
                }
            }

        } catch (InterruptedException e) {
            LOGGER.error("HeartBeatDetector error", e);
        }
    }
}
