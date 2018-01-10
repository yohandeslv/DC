package lk.umo.dc.bootstrap.response;

import lk.umo.dc.domain.model.PeerNode;
import lk.umo.dc.util.ErrorCodeResolver;
import java.net.InetAddress;
import java.net.UnknownHostException;

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;


public class RegResponse extends BootstrapResponse {

    private static final String empty = new String();
    private List<PeerNode> peerNodes = new ArrayList<>();
    private boolean regOk;

    public RegResponse(String messageString) throws UnknownHostException {       
        super(messageString);
        String[] tokens = messageString.split(" ");
        
        //InetAddress ip = InetAddress.getLocalHost();
        System.out.println("messageString: "+messageString+"="+tokens[1]+", "+tokens[2]+", "+tokens[3]);
        if (this.getMessageBody() != null && !empty.equals(this.getStatusMessage())) {
            if (this.getMessageBody().contains(" ")) {
                StringTokenizer tokenizer = new StringTokenizer(this.getMessageBody(), " ");
                
                //while (tokenizer.hasMoreTokens()) {
                    //PeerNode peerNode = new PeerNode(tokenizer.nextToken(), Integer.parseInt(tokenizer.nextToken()), "a89");
                    PeerNode peerNode = new PeerNode(tokens[3], Integer.parseInt(tokens[4]), "a89");
                    peerNodes.add(peerNode);
                //}
            }
        }
    }

    @Override
    public String getStatusMessage() {
        return ErrorCodeResolver.getRegStatusMessage(this.getStatusCode());
    }

    public List<PeerNode> getPeerNodes() {
        return peerNodes;
    }

    public boolean isRegOk() {
        return regOk;
    }

    public void setRegOk(boolean regOk) {
        this.regOk = regOk;
    }
}
