package lk.umo.dc.util;

import lk.umo.dc.forum.ForumSetting;
import lk.umo.dc.config.NodeContext;
import lk.umo.dc.connector.UdpCommunicator;
import lk.umo.dc.domain.model.FileManager;
import lk.umo.dc.domain.model.Operation;
import lk.umo.dc.messaging.PeerMessageUtils;
import lk.umo.dc.messaging.broadcast.BroadCastMessenger;
import lk.umo.dc.messaging.broadcast.message.SearchRequest;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.util.List;

/**
 * Created by thilina on 12/2/16.
 */
public class SearchUtil {

    private static final Logger LOGGER = LogManager.getLogger(SearchUtil.class.getName());


    //break the search term into words
    private static final String SEARCH_DELIMITER = " ";

    public static final void search(String searchTerm, int ttl) {
        if (searchTerm.contains(SEARCH_DELIMITER)) {
            String[] kewords = searchTerm.split(SEARCH_DELIMITER);
            for (String keyword : kewords) {
                initiateSearch(keyword, ttl);

            }
        } else {
            initiateSearch(searchTerm, ttl);
        }

    }

    private static void initiateSearch(String searchTerm, int ttl) {
        LOGGER.debug("searching for {}", searchTerm);

        //do local search
        List<String> files =  FileManager.searchFile(searchTerm);

        if (!files.isEmpty()) {
            for (String file : files) {
                ForumSetting.addSearchResult(file, "local");
            }
        }

        //create a broadcast search request
        BroadCastMessenger broadCastMessenger = new BroadCastMessenger();
        SearchRequest searchRequest = new SearchRequest(searchTerm, String.valueOf(Operation.SER), NodeContext.getIp(),
                NodeContext.getPort(), 1, ttl, NodeContext.getUserName());
        broadCastMessenger.broadcast(searchRequest);

    }
    
    /**
     * Search local files and send the results back to the source node
     * */
    public static void searchReturnAndBroadcast(SearchRequest searchRequest) {
        String keyword = searchRequest.getMessage();
        List<String> files = FileManager.searchFile(keyword);

        if (!files.isEmpty()) {
            for (String filename: files) {
                String message = PeerMessageUtils.constructSearchResultRequest(filename, searchRequest.getId());
                try {
                    new UdpCommunicator().sendMessage(searchRequest.getSourceIp(), searchRequest.getSourcePort(), message);
                } catch (IOException e) {
                    LOGGER.error("Failed sending search result", e);
                }
            }
        }

        //broadcast search request to neighbors
        BroadCastMessenger broadCastMessenger = new BroadCastMessenger();
        broadCastMessenger.broadcast(searchRequest);
    }

}
