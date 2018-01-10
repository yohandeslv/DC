package lk.umo.dc.domain.model;

/**
 * Created by thilina on 12/2/16.
 */
public class SearchResult {
    private String filename;
    private PeerNode peerNode;

    public SearchResult(String filename, PeerNode peerNode) {
        this.filename = filename;
        this.peerNode = peerNode;
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public PeerNode getPeerNode() {
        return peerNode;
    }

    public void setPeerNode(PeerNode peerNode) {
        this.peerNode = peerNode;
    }
}
