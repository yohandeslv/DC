package lk.umo.dc.config;

import lk.umo.dc.domain.model.PeerNode;
import lk.umo.dc.util.PeerNodeObserver;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by dev on 11/17/16.
 */
public class NodeContext {
    private static String ip;
    private static int port;
    private static String userName;
    //thread safe list
    private static List<PeerNode> peerNodes = Collections.synchronizedList(new ArrayList<PeerNode>());

    private static boolean online = false;

    //list of observers
    private static List<PeerNodeObserver> observers = Collections.synchronizedList(new ArrayList<PeerNodeObserver>());

    public static String getIp() {
        return ip;
    }

    public static void setIp(String ip) {
        NodeContext.ip = ip;
    }
    
    public static int getPort() {
        return port;
    }

    public static void setPort(int port) {
        NodeContext.port = port;
    }

    public static String getUserName() {
        return userName;
    }

    public static void setUserName(String userName) {
        NodeContext.userName = userName;
    }


    /**
     * Add a peerNode as a child
     */
    public static void addChild(PeerNode peerNode) {
        peerNode.setRelationship(PeerNode.Type.CHILD);
        peerNodes.add(peerNode);
        notifyObservers();
    }

    /**
     * Get child nodes
     *
     * @return children
     */
    public static List<PeerNode> getChildren() {
        List<PeerNode> children = new ArrayList<>();
        for (PeerNode peerNode : peerNodes) {
            if (peerNode.getRelationship().equals(PeerNode.Type.CHILD)) {
                children.add(peerNode);
            }
        }
        return children;
    }

    /**
     * Add a peerNode as a parent
     */
    public static void addParent(PeerNode peerNode) {
        peerNode.setRelationship(PeerNode.Type.PARENT);
        peerNodes.add(peerNode);
    }


    /**
     * Get parent nodes
     *
     * @return parents
     */
    public static List<PeerNode> getParents() {
        List<PeerNode> parents = new ArrayList<>();
        for (PeerNode peerNode : peerNodes) {
            if (peerNode.getRelationship().equals(PeerNode.Type.PARENT)) {
                parents.add(peerNode);
            }
        }
        return parents;
    }

    /**
     * Remove child node
     *
     */
    public static void removeChilden() {
        List<PeerNode> children = NodeContext.getChildren();
        for (PeerNode node : children) {
            peerNodes.remove(node);
        }
        notifyObservers();
    }

    /**
     * Remove child node
     *
     * @param child node
     */
    public static void removeChild(PeerNode child) {
        List<PeerNode> children = NodeContext.getChildren();
        for (PeerNode node : children) {
            if (child.getUsername().trim().equals(node.getUsername().trim())) {
                peerNodes.remove(node);
            }
        }
        notifyObservers();
    }

    private static void notifyObservers() {
        for (PeerNodeObserver observer : observers) {
            observer.onChildChanged(getChildren());
        }

    }

    /**
     * Remove parent node
     *
     * @param parent parent node
     */
    public static void removeParent(PeerNode parent) {
        List<PeerNode> parents = NodeContext.getParents();
        for (PeerNode node : parents) {
            if (parent.getUsername().trim().equals(node.getUsername().trim())) {
                peerNodes.remove(node);
            }
        }
    }

    /**
     * add to list of observers, so it will notify observer when a child change
     * */
    public static void tellMeWhenChildChanged(PeerNodeObserver observer){
        observers.add(observer);
    }

    public static boolean isOnline() {
        return online;
    }

    public static void setOnline(boolean online) {
        NodeContext.online = online;
    }
}
