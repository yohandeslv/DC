package lk.umo.dc.bootstrap.response;

/**
 * Created by dev on 11/20/16.
 */
public abstract class BootstrapResponse {

    protected static final String MESSAGE_DELIMITER = " ";

    private String responseString;
    private int messageLength;
    private String keyword;
    private int statusCode;
    private String messageBody;

    public BootstrapResponse(String messageString) {
        this.responseString = messageString;

        if (messageString != null) {
            if (messageString.contains(MESSAGE_DELIMITER)) {
                String chunks[] = messageString.split(MESSAGE_DELIMITER);
                if (chunks.length >= 3) {
                    messageLength = Integer.valueOf(chunks[0]);
                    keyword = chunks[1];
                    statusCode = Integer.valueOf(chunks[2]);
                    if (chunks.length >= 4)
                        messageBody = responseString.substring(responseString.indexOf(chunks[3]));
                }
            }
        }
        //TODO handle negative path
    }

    public int getMessageLength() {
        return messageLength;
    }

    public String getKeyword() {
        return keyword;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public String getMessageBody() {
        return messageBody;
    }

    public abstract String getStatusMessage();
}
