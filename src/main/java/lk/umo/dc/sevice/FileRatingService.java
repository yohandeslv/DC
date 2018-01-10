package lk.umo.dc.sevice;

import lk.umo.dc.util.DBConnection;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

//TODO too lazy for dao layer do it later
public class FileRatingService {
    private static final Logger LOGGER = LogManager.getLogger(FileRatingService.class.getName());

    private static final String INSERT_FILE_RATING = "INSERT INTO file_rating (file_hash, node, rating) values (?, ?, ?)";
    private static final String SELECT_FILE_RATING = "SELECT * FROM file_rating WHERE file_hash = ?";

    public void saveFileRating(String fileHash, String node, int rating){
        try {
            Connection conn = DBConnection.getConnection();
            PreparedStatement pstmt = conn.prepareStatement(INSERT_FILE_RATING);
            pstmt.setString(1, fileHash);
            pstmt.setString(2, node);
            pstmt.setInt(3, rating);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            LOGGER.error("failed to insert file rating", e);
        }
    }

    /**
     * @param fileHash md5 hash of the file name
     * return int [rating, ratingsCount]
     * */
    public int[] getFileRating(String fileHash){
        int rating = 0;
        int count = 0;
        int ratingSum = 0;

        try {
            Connection conn = DBConnection.getConnection();
            PreparedStatement pstmt = conn.prepareStatement(SELECT_FILE_RATING);
            pstmt.setString(1, fileHash);
            ResultSet rs    = pstmt.executeQuery();

            // loop through the result set
            while (rs.next()) {
                count = count + 1;
                ratingSum = ratingSum +rs.getInt("rating");
                rating = ratingSum / (count);
            }
        } catch (SQLException e) {
            LOGGER.error("failed to get file rating", e);
        }
        return new int[] {rating, count};
    }
}
