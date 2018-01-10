package lk.umo.dc.messaging.broadcast;

import lk.umo.dc.forum.ForumSetting;
import lk.umo.dc.config.NodeContext;
import lk.umo.dc.domain.model.Comment;
import lk.umo.dc.messaging.broadcast.message.MessageRequest;
import lk.umo.dc.sevice.CommentService;
import lk.umo.dc.sevice.FileRatingService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Date;
import java.util.UUID;
import lk.umo.dc.sevice.CommentRatingService;

public class CommentBroadCastUtil {
    private static final Logger LOGGER = LogManager.getLogger(CommentBroadCastUtil.class.getName());
    private CommentService commentService = new CommentService();
    private FileRatingService fileRatingService = new FileRatingService();
    private CommentRatingService commentRatingService = new CommentRatingService();

    public void broadcastComment(final Comment comment){
        new Thread(){
            @Override
            public void run() {
                LOGGER.debug("broadcasting comment");
                MessageRequest request = new MessageRequest(UUID.randomUUID().toString(),
                        comment.getFileHash() + " " + comment.getCommentId() + " " + comment.getNode() + " " +comment.getTime().getTime() + " " + comment.getComment(),
                        "CMNT", 0, (int)ForumSetting.spTtl.getValue(), NodeContext.getUserName());
                BroadCastMessenger broadCastMessenger = new BroadCastMessenger();
                broadCastMessenger.broadcast(request);
            }
        }.start();
    }

    public void handleReceivedComment(final MessageRequest request) {
        new Thread() {
            @Override
            public void run() {
                Comment comment = new Comment();
                String chunks[] = request.getData().trim().split(" ");
                comment.setFileHash(chunks[0]);
                comment.setCommentId(chunks[1]);
                comment.setNode(chunks[2]);
                comment.setTime(new Date(Long.valueOf(chunks[3])));
                comment.setComment(request.getData().substring(request.getData().lastIndexOf(chunks[3]) + 2,
                        request.getData().length()));
                commentService.saveComment(comment);
                BroadCastMessenger broadCastMessenger = new BroadCastMessenger();
                broadCastMessenger.broadcast(request);
            }
        }.start();
    }


    public void broadcastFileRating(final String fileHash, final String node, final int rating){
        new Thread(){
            @Override
            public void run() {
                LOGGER.debug("broadcasting file rating");
                MessageRequest request = new MessageRequest(UUID.randomUUID().toString(),
                        fileHash + " " + node + " " + rating,
                        "FILRTNG", 0, (int)ForumSetting.spTtl.getValue(), NodeContext.getUserName());
                BroadCastMessenger broadCastMessenger = new BroadCastMessenger();
                broadCastMessenger.broadcast(request);
            }
        }.start();
    }

    public void handleReceivedFileRating(final MessageRequest request) {
        new Thread() {
            @Override
            public void run() {
                Comment comment = new Comment();
                String chunks[] = request.getData().split(" ");
                String fileHash = chunks[0];
                String node = chunks[1];
                int rating = Integer.valueOf(chunks[2]);
                fileRatingService.saveFileRating(fileHash, node, rating);
                BroadCastMessenger broadCastMessenger = new BroadCastMessenger();
                broadCastMessenger.broadcast(request);
            }
        }.start();
    }

    public void broadcastCommentRating(final String commentId, final String node, final int rating){
        new Thread(){
            @Override
            public void run() {
                LOGGER.debug("broadcasting comment rating");
                MessageRequest request = new MessageRequest(UUID.randomUUID().toString(),
                        commentId + " " + node + " " + rating,
                        "CMNTRTNG", 0, (int)ForumSetting.spTtl.getValue(), NodeContext.getUserName());
                BroadCastMessenger broadCastMessenger = new BroadCastMessenger();
                broadCastMessenger.broadcast(request);
            }
        }.start();
    }

    public void handleReceivedCommentRating(final MessageRequest request) {
        new Thread() {
            @Override
            public void run() {
                Comment comment = new Comment();
                String chunks[] = request.getData().split(" ");
                String commentId = chunks[0];
                String node = chunks[1];
                int rating = Integer.valueOf(chunks[2]);
                commentRatingService.saveCommentRating(commentId, node, rating);
                BroadCastMessenger broadCastMessenger = new BroadCastMessenger();
                broadCastMessenger.broadcast(request);
            }
        }.start();
    }
}
