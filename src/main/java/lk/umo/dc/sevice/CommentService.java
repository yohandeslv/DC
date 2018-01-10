package lk.umo.dc.sevice;

import lk.umo.dc.domain.model.Comment;
import lk.umo.dc.util.DBConnection;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

//TODO too lazy for dao layer do it later
public class CommentService {
    private static final Logger LOGGER = LogManager.getLogger(CommentService.class.getName());

    private static final String INSERT_COMMENT = "INSERT INTO comment (comment_id, file_hash, node, comment, comment_date) values (?, ?, ?, ?, ?)";
    private static final String SELECT_COMMENTS = "SELECT * FROM comment WHERE file_hash = ? ORDER BY comment_date";

    public void saveComment(Comment comment){
        try {
            Connection conn = DBConnection.getConnection();
            PreparedStatement pstmt = conn.prepareStatement(INSERT_COMMENT);
            pstmt.setString(1, comment.getCommentId());
            pstmt.setString(2, comment.getFileHash());
            pstmt.setString(3, comment.getNode());
            pstmt.setString(4, comment.getComment());
            pstmt.setLong(5, comment.getTime().getTime());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            LOGGER.error("failed to insert comment", e);
        }
    }

    /**
     * @param fileHash md5 hash of the file name
     * */
    public List<Comment> getComments(String fileHash){
        List <Comment> commentList = new ArrayList<>();
        try {
            Connection conn = DBConnection.getConnection();
            PreparedStatement pstmt = conn.prepareStatement(SELECT_COMMENTS);
            pstmt.setString(1, fileHash);
            ResultSet rs    = pstmt.executeQuery();

            // loop through the result set
            while (rs.next()) {
                Comment comment = new Comment(rs.getString("comment_id"),
                        rs.getString("file_hash"),
                        rs.getString("node"),
                        rs.getString("comment"),
                        new Date(rs.getLong("comment_date")));
                commentList.add(comment);
            }
        } catch (SQLException e) {
            LOGGER.error("failed to get comments", e);
        }
        return commentList;
    }
}
