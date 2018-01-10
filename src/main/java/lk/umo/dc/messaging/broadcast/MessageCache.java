package lk.umo.dc.messaging.broadcast;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by dev on 11/26/16.
 */
public class MessageCache extends Thread{
    private static final Logger LOGGER = LogManager.getLogger(MessageCache.class.getName());

    public static final int CACHE_TTL = 600; //10 minutes ; 600 seconds

    private static Map<String, Integer> messageCache = new ConcurrentHashMap<>();//thread safe map

    private static MessageCache messageCacheInstance;

    //Singleton
    private MessageCache() {
    }

    /**
     * Check a message id in cache
     * */
    public static boolean isInCache(String id) {
        return messageCache.containsKey(id);
    }

    public static void addCache(String id) {
        messageCache.put(id, CACHE_TTL);
    }

    /**
     * Init caching scheduler
     * */
    public static void initCachingScheduler() {
        if  (!getInstance().isAlive()) {
            LOGGER.info("Starting message cache scheduler");
            getInstance().start();
        }
    }

    @Override
    public void run() {
        try {
            while (true) {
                Thread.sleep(5000);
//                LOGGER.debug("Decrementing cache");
                for (String id : messageCache.keySet()) {
                    if (messageCache.get(id) - 5 <= 0) {
                        LOGGER.debug("invalidating cache: {}", id);;
                        messageCache.remove(id);
                    } else {
                        messageCache.put(id, messageCache.get(id) - 1);
                    }
                }
            }
        } catch (InterruptedException e) {
            LOGGER.error("InterruptedException in caching ", e);
        }
    }

    /**
     * Get instance of messageCache
     *
     * @return MessageCache instance
     * */
    public static MessageCache getInstance() {
        if (messageCacheInstance == null) {
            messageCacheInstance = new MessageCache();
        }
        return messageCacheInstance;
    }
}
