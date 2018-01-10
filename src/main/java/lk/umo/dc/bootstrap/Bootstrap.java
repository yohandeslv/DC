package lk.umo.dc.bootstrap;

import lk.umo.dc.bootstrap.response.RegResponse;
import lk.umo.dc.config.BootstrapServerConfig;
import lk.umo.dc.config.NodeContext;
import lk.umo.dc.connector.TcpCommunicator;
import lk.umo.dc.connector.UdpCommunicator;
import lk.umo.dc.domain.model.PeerNode;
import lk.umo.dc.messaging.Connect;
import lk.umo.dc.util.ErrorCodeResolver;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.util.Random;

/**
 * Created by dev on 11/17/16.
 */
public class Bootstrap {

    private static final Logger LOGGER = LogManager.getLogger(Bootstrap.class.getName());

    private static String UNREG_OK_KEYWORD = "UNROK";
    private static String REG_OK_KEYWORD = "REGOK";

    private static PeerNode child1 = null;
    private static PeerNode child2 = null;

    /**
     * Register the node with bootstrap server
     *
     * @return boolean success/fail
     */
    public static RegResponse register() throws IOException {
        LOGGER.info("Registering with Bootstrap server");
        UdpCommunicator udpCommunicator = new UdpCommunicator();
        RegResponse regResponse = null;
        try {
            String message = BootstrapMessageUtils.constructRegMessage();
            String response = udpCommunicator.sendMessage(BootstrapServerConfig.getHost(),BootstrapServerConfig.getPort(), message);
            System.out.println("response: "+response);
            regResponse = new RegResponse(response);
            if (regResponse.getStatusCode() < ErrorCodeResolver.OK_MIN_FAIL_RANGE) {
                //This means reg is success
                connectPeers(regResponse);

                regResponse.setRegOk(true);
                return regResponse;
            } else {
                LOGGER.error("Registration failed, responseCode: {}, responseMessage: {}",
                        regResponse.getStatusCode(), regResponse.getStatusMessage());
                regResponse.setRegOk(false);
            }

        } catch (IllegalArgumentException e) {
            LOGGER.error("IllegalArgumentException occurred when connecting Bootstrap Server", e);
            throw e;
        } catch (IOException e) {
            LOGGER.error("IOException occurred when connecting Bootstrap Server", e);
            throw e;
        }
        return regResponse;
    }

    private static void connectPeers(RegResponse regResponse) {
        NodeContext.removeChilden();
        int nodeCount = regResponse.getPeerNodes().size();
        if (nodeCount == 0) {
            //no peer nodes yet in the BS
            LOGGER.info("No peer nodes in the network yet");
        } else if (nodeCount == 1) {
            child1 = regResponse.getPeerNodes().get(0);
            LOGGER.info("1 node in the network");
            if (!Connect.connectWithPeer(child1)) {
                child1 = null;
            }
        } else if (nodeCount == 2) {
            child1 = regResponse.getPeerNodes().get(0);
            if(!Connect.connectWithPeer(child1))
                child1 = null;
            child2 = regResponse.getPeerNodes().get(1);
            if(!Connect.connectWithPeer(child2))
                child2 = null;
            LOGGER.info("2 nodes in the network");
        } else {
            LOGGER.info(nodeCount + " nodes in the network");

            child1 = connectRandomChild(regResponse, nodeCount);
            child2 = connectRandomChild(regResponse, nodeCount);

            nodeCount = regResponse.getPeerNodes().size();//re evaluate node count

            //retry
            if (child1 == null && !regResponse.getPeerNodes().isEmpty()) {
                child1 = connectRandomChild(regResponse, nodeCount);
            }

            //retry
            if (child2 == null && !regResponse.getPeerNodes().isEmpty()) {
                child2 = connectRandomChild(regResponse, nodeCount);
            }

            if (child1 != null && child2 != null && child1.getUsername().equals(child2.getUsername())) {
                //hopefully it won't reach here. if so retry one last time
                child2 = connectRandomChild(regResponse, nodeCount);
                if (child1.getUsername().equals(child2.getUsername())) {
                    child2 = null;
                }
            }

        }

        if (child1 != null)
            NodeContext.addChild(child1);
        if (child2 != null)
            NodeContext.addChild(child2);

        LOGGER.info("picked children: {}", NodeContext.getChildren());
    }

    /**
     * Connect with a random peer node as a parent
     *
     * @param regResponse reg response
     * @param nodeCount   number of nodes in the network
     */
    private static PeerNode connectRandomChild(RegResponse regResponse, int nodeCount) {
        PeerNode child = regResponse.getPeerNodes().get(new Random().nextInt(nodeCount - 1));
        if (Connect.connectWithPeer(child)) {
            regResponse.getPeerNodes().remove(child);//removing from te pool to avoid duplication
            return child;
        } else {
            child = regResponse.getPeerNodes().get(new Random().nextInt(nodeCount - 1));
            if (Connect.connectWithPeer(child)) {
                regResponse.getPeerNodes().remove(child);//removing from te pool to avoid duplication
                return child;
            } else {
                return null;
            }
        }
    }

    public static boolean unregister() {
        LOGGER.info("UnRegistering from Bootstrap server");

        UdpCommunicator udpCommunicator = new UdpCommunicator();
        try {
            String response = udpCommunicator.sendMessage(BootstrapServerConfig.getHost(),
                    BootstrapServerConfig.getPort(), BootstrapMessageUtils.constructUnRegMessage());
            
            if (response.contains("UNROK")) {
                NodeContext.removeChilden();
                return true;
            }

        } catch (IllegalArgumentException e) {
            LOGGER.error("IllegalArgumentException occurred when connecting Bootstrap Server", e);
        } catch (IOException e) {
            LOGGER.error("IOException occurred when connecting Bootstrap Server", e);
        }
        return false;
    }

}
