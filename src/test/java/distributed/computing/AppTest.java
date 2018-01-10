package distributed.computing;

import lk.umo.dc.domain.model.Comment;
import lk.umo.dc.sevice.CommentRatingService;
import lk.umo.dc.sevice.CommentService;
import lk.umo.dc.sevice.FileRatingService;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import java.util.Date;
import java.util.UUID;

/**
 * Unit test for simple Main.
 */
public class AppTest 
    extends TestCase
{
    /**
     * Create the test case
     *
     * @param testName name of the test case
     */
    public AppTest( String testName )
    {
        super( testName );
    }

    /**
     * @return the suite of tests being tested
     */
    public static Test suite()
    {
        return new TestSuite( AppTest.class );
    }

    /**
     * Rigourous Test :-)
     */
    public void testApp()
    {
        assertTrue( true );
    }


    public static void main(String[] args) {
        CommentRatingService commentService = new CommentRatingService();

        commentService.saveCommentRating("123", "node1", 5);
        commentService.saveCommentRating("123", "node2", 1);

        System.out.println(commentService.getCommentRating("123")[0]);
        System.out.println(commentService.getCommentRating("123")[1]);
    }
}
