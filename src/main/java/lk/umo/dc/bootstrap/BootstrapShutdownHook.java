package lk.umo.dc.bootstrap;

import lk.umo.dc.config.NodeContext;
import lk.umo.dc.domain.model.PeerNode;
import lk.umo.dc.messaging.Connect;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.naming.CompositeName;
import java.util.List;

/**
 * Created by thilina on 11/25/16.
 *
 * Shutdown hook for client app
 */
public class BootstrapShutdownHook extends Thread {

    private static final Logger LOGGER = LogManager.getLogger(BootstrapShutdownHook.class.getName());


    @Override
    public void run() {
        LOGGER.info("Shutting down...");
        if (NodeContext.isOnline()) {
            Bootstrap.unregister();
        }
        List<PeerNode> children = NodeContext.getChildren();
        for (PeerNode child : children) {
            Connect.disconnectFromPeer(child);
        }
    }
}
