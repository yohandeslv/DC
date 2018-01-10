package lk.umo.dc.domain.model;

import java.util.Date;

public class Comment {
    private String commentId;
    private String fileHash;
    private String node;
    private String comment;
    private Date time;

    public Comment() {
    }

    public Comment(String commentId, String fileHash, String node, String comment, Date time) {
        this.commentId = commentId;
        this.fileHash = fileHash;
        this.node = node;
        this.comment = comment;
        this.time = time;
    }

    public String getCommentId() {
        return commentId;
    }

    public void setCommentId(String commentId) {
        this.commentId = commentId;
    }

    public String getFileHash() {
        return fileHash;
    }

    public void setFileHash(String fileHash) {
        this.fileHash = fileHash;
    }

    public String getNode() {
        return node;
    }

    public void setNode(String node) {
        this.node = node;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public Date getTime() {
        return time;
    }

    public void setTime(Date time) {
        this.time = time;
    }

    @Override
    public String toString() {
        return "Comment{" +
                "commentId='" + commentId + '\'' +
                ", fileHash='" + fileHash + '\'' +
                ", node='" + node + '\'' +
                ", comment='" + comment + '\'' +
                ", time=" + time +
                '}';
    }
}
