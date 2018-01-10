package lk.umo.dc.sevice;

import lk.umo.dc.util.DBConnection;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

//TODO too lazy for dao layer do it later
public class CommentRatingService {
    private static final Logger LOGGER = LogManager.getLogger(CommentRatingService.class.getName());

    private static final String INSERT_COMENT_RATING = "INSERT INTO comment_rating (comment_id, node, rating) values (?, ?, ?)";
    private static final String SELECT_FILE_RATING = "SELECT * FROM comment_rating WHERE comment_id = ?";

    public void saveCommentRating(String commentId, String node, int rating){
        try {
            Connection conn = DBConnection.getConnection();
            PreparedStatement pstmt = conn.prepareStatement(INSERT_COMENT_RATING);
            pstmt.setString(1, commentId);
            pstmt.setString(2, node);
            pstmt.setInt(3, rating);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            LOGGER.error("failed to insert comment rating", e);
        }
    }

    /**
     * @param commentId unique identifier for the comment
     * return int [rating, ratingsCount]
     * */
    public int[] getCommentRating(String commentId){
        int rating = 0;
        int count = 0;
        int ratingSum = 0;

        try {
            Connection conn = DBConnection.getConnection();
            PreparedStatement pstmt = conn.prepareStatement(SELECT_FILE_RATING);
            pstmt.setString(1, commentId);
            ResultSet rs    = pstmt.executeQuery();

            // loop through the result set
            while (rs.next()) {
                count = count + 1;
                ratingSum = ratingSum +rs.getInt("rating");
                rating = ratingSum / (count);
            }
        } catch (SQLException e) {
            LOGGER.error("failed to get comment rating", e);
        }
        return new int[] {rating, count};
    }
}
