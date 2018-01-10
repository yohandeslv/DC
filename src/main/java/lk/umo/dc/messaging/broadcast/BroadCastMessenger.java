package lk.umo.dc.messaging.broadcast;

import lk.umo.dc.config.NodeContext;
import lk.umo.dc.connector.UdpCommunicator;
import lk.umo.dc.domain.model.Comment;
import lk.umo.dc.domain.model.PeerNode;
import lk.umo.dc.messaging.broadcast.message.MessageRequest;
import lk.umo.dc.messaging.broadcast.message.SearchRequest;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;

/**
 * Created by dev on 11/26/16.
 */
public class BroadCastMessenger {


    private static final Logger LOGGER = LogManager.getLogger(BroadCastMessenger.class.getName());

    /**
     * Broadcast message to network
     *
     * @param searchRequest broadcast message
     * @return response
     */
    public void broadcast(SearchRequest searchRequest) {
        String predecessorNode = searchRequest.getPredecessor().trim();
        if (MessageCache.isInCache(searchRequest.getId())) {
            //Do nothing
            LOGGER.debug("Discarding message {}, already in cache", searchRequest.getId());
        } else {
            MessageCache.addCache(searchRequest.getId());
            for (PeerNode parent : NodeContext.getParents()) {
                //avoid sending back to predecessor
                String parentNode = parent.getUsername().trim();
                if (!parentNode.equals(predecessorNode)) {
                    try {
                        searchRequest.setPredecessor(NodeContext.getUserName());
                        searchRequest.setHops(searchRequest.getHops() + 1);
                        new UdpCommunicator().sendMessage(parent.getIp(), parent.getPort(), searchRequest.toString());
                    } catch (IOException e) {
                        LOGGER.error("Could not send message to parent {}", parent);
                    }
                }
            }
            for (PeerNode child : NodeContext.getChildren()) {
                String childNode = child.getUsername().trim();
                //avoid sending back to predecessor
                if (!childNode.equalsIgnoreCase(predecessorNode)) {
                    try {
                        searchRequest.setHops(searchRequest.getHops() + 1);
                        searchRequest.setPredecessor(NodeContext.getUserName());
                        new UdpCommunicator().sendMessage(child.getIp(), child.getPort(), searchRequest.toString());
                    } catch (IOException e) {
                        LOGGER.error("Could not send message to child {}", child);
                    }
                }
            }

        }
    }

    /**
     * Broadcast message to network
     *
     * @param messageRequest broadcast message
     * @return response
     */
    public void broadcast(MessageRequest messageRequest) {
        String predecessorNode = messageRequest.getPredecessor().trim();
        if (MessageCache.isInCache(messageRequest.getId())) {
            //Do nothing
            LOGGER.debug("Discarding message {}, already in cache", messageRequest.getId());
        } else {
            MessageCache.addCache(messageRequest.getId());
            for (PeerNode parent : NodeContext.getParents()) {
                //avoid sending back to predecessor
                String parentNode = parent.getUsername().trim();
                if (!parentNode.equals(predecessorNode)) {
                    try {
                        messageRequest.setPredecessor(NodeContext.getUserName());
                        messageRequest.setHops(messageRequest.getHops() + 1);
                        new UdpCommunicator().sendMessage(parent.getIp(), parent.getPort(), messageRequest.toString());
                    } catch (IOException e) {
                        LOGGER.error("Could not send message to parent {}", parent);
                    }
                }
            }
            for (PeerNode child : NodeContext.getChildren()) {
                String childNode = child.getUsername().trim();
                //avoid sending back to predecessor
                if (!childNode.equalsIgnoreCase(predecessorNode)) {
                    try {
                        messageRequest.setHops(messageRequest.getHops() + 1);
                        messageRequest.setPredecessor(NodeContext.getUserName());
                        new UdpCommunicator().sendMessage(child.getIp(), child.getPort(), messageRequest.toString());
                    } catch (IOException e) {
                        LOGGER.error("Could not send message to child {}", child);
                    }
                }
            }
        }
    }

}
