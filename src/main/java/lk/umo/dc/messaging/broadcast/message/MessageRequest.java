package lk.umo.dc.messaging.broadcast.message;

import lk.umo.dc.util.MessageUtils;

public class MessageRequest {

    private static final String DELIMITER = " ";

    private String id;
    private String data;
    private String operation;
    private int hops;
    private int ttl;
    private String predecessor;

    public MessageRequest(String id, String data, String operation, int hops, int ttl, String predecessor) {
        this.id = id;
        this.data = data;
        this.operation = operation;
        this.hops = hops;
        this.ttl = ttl;
        this.predecessor = predecessor;
    }

    public MessageRequest(String message) {
        //length CMNT messageId hops ttl predecessor data1 data2
        String chunks[] = message.split(DELIMITER);
        this.operation = chunks[1];
        this.id = chunks[2];
        this.hops = Integer.parseInt(chunks[3]);
        this.ttl = Integer.parseInt(chunks[4]);
        this.predecessor = chunks[5];
        this.data = message.substring(message.indexOf(predecessor) + predecessor.length() + 1, message.length());
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public String getOperation() {
        return operation;
    }

    public void setOperation(String operation) {
        this.operation = operation;
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

    public String getPredecessor() {
        return predecessor;
    }

    public void setPredecessor(String predecessor) {
        this.predecessor = predecessor;
    }

    @Override
    public String toString() {
        String message =   operation + DELIMITER +
                id + DELIMITER +
                hops + DELIMITER +
                ttl + DELIMITER +
                predecessor + DELIMITER +
                data;
        message = MessageUtils.prependLength(message);
        return message;
    }
}
