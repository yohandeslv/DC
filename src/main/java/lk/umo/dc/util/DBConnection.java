package lk.umo.dc.util;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class DBConnection {

    private static Connection connection;
    private static final Logger LOGGER = LogManager.getLogger(DBConnection.class.getName());

    private static final String FILE_RATING_TABLE = "CREATE TABLE IF NOT EXISTS file_rating(" +
            "file_hash text NOT NULL," +
            "node text NOT NULL," +
            "rating integer NOT NULL);";

    private static final String COMMENT_TABLE = "CREATE TABLE IF NOT EXISTS comment(" +
            "comment_id text PRIMARY KEY," +
            "file_hash text NOT NULL," +
            "node text NOT NULL," +
            "comment text," +
            "comment_date integer NOT NULL);";

    private static final String COMMENT_RATING_TABLE = "CREATE TABLE IF NOT EXISTS comment_rating(" +
            "comment_id text NOT NULL," +
            "node text NOT NULL," +
            "rating integer NOT NULL);";

    static {
        LOGGER.debug("Creating db connection");
        connection = connect();
    }

    private static Connection connect() {
        // SQLite connection string
        String url = "jdbc:sqlite:.file_data";
        Connection conn = null;
        try {
            conn = DriverManager.getConnection(url);
        } catch (SQLException e) {
            LOGGER.error("DB Connection failed", e);
        }
        return conn;
    }

    public static void createDatabase(){
         try {
             LOGGER.debug("Initializing database...");
             connection = getConnection();
             Statement stmt = connection.createStatement();
             // create tables
             stmt.execute(FILE_RATING_TABLE);
             stmt.execute(COMMENT_TABLE);
             stmt.execute(COMMENT_RATING_TABLE);

         } catch (SQLException e) {
            e.printStackTrace();
        }


    }

    public static Connection getConnection() {
        try {
            if (connection == null || connection.isClosed()) {
                connection = connect();
            }
        } catch (SQLException e) {
            LOGGER.error("Database connection failed!", e);
        }
        return connection;
    }
}
