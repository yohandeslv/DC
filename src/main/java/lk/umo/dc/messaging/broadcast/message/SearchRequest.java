package lk.umo.dc.messaging.broadcast.message;

import lk.umo.dc.util.MessageUtils;

import java.util.UUID;

/**
 * Created by dev on 11/26/16.
 */
public class SearchRequest {

    private static final String SEARCH_MESSAGE_FORMAT = "%s %s %d %s %d %d %s %s";
    private static final String DELIMITER = " ";

    private String id;
    private String message;
    private String operation;
    private String sourceIp;
    private int sourcePort;
    private int hops;
    private int ttl;
    private String predecessor;


    public SearchRequest(String id, String message, String operation, String sourceIp, int sourcePort, int hops, int ttl, String predecessor) {
        this.id = id;
        this.message = message;
        this.operation = operation;
        this.sourceIp = sourceIp;
        this.sourcePort = sourcePort;
        this.hops = hops;
        this.ttl = ttl;
        this.predecessor = predecessor;
    }

    public SearchRequest(String message, String operation, String sourceIp, int sourcePort, int hops, int ttl, String predecessor) {
        this.id = UUID.randomUUID().toString();
        this.message = message;
        this.operation = operation;
        this.sourceIp = sourceIp;
        this.sourcePort = sourcePort;
        this.hops = hops;
        this.ttl = ttl;
        this.predecessor = predecessor;
    }

    public SearchRequest(String message) {
        //length SER sourceip sourceport message hops ttl messageId
        String chunks[] = message.split(DELIMITER);
        this.operation = chunks[1];
        this.sourceIp = chunks[2];
        this.sourcePort = Integer.parseInt(chunks[3]);
        this.message = chunks[4];
        this.hops = Integer.parseInt(chunks[5]);
        this.ttl = Integer.parseInt(chunks[6]);
        this.id = chunks[7];
        this.predecessor = chunks[8];
    }


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getOperation() {
        return operation;
    }

    public void setOperation(String operation) {
        this.operation = operation;
    }

    public String getPredecessor() {
        return predecessor;
    }

    public void setPredecessor(String predecessor) {
        this.predecessor = predecessor;
    }

    public String getSourceIp() {
        return sourceIp;
    }

    public void setSourceIp(String sourceIp) {
        this.sourceIp = sourceIp;
    }

    public int getSourcePort() {
        return sourcePort;
    }

    public void setSourcePort(int sourcePort) {
        this.sourcePort = sourcePort;
    }

    public int getHops() {
        return hops;
    }

    public void setHops(int hops) {
        this.hops = hops;
    }

    public int getTtl() {
        return ttl;
    }

    public void setTtl(int ttl) {
        this.ttl = ttl;
    }

    @Override
    public String toString() {
        //length SER sourceip sourceport message hops ttl messageId
        String message = String.format(SEARCH_MESSAGE_FORMAT, this.operation, this.sourceIp,
                this.sourcePort, this.message, this.hops, this.ttl, this.id, this.predecessor);
        message = MessageUtils.prependLength(message);
        return message;
    }
}
