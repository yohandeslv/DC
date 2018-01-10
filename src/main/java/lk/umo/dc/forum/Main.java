package lk.umo.dc.forum;

import lk.umo.dc.config.NodeContext;
import lk.umo.dc.connector.HeartBeatDetector;
import lk.umo.dc.listner.Listener;
import lk.umo.dc.listner.UdpListener;
import lk.umo.dc.util.DBConnection;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Main {

    private static final Logger LOGGER = LogManager.getLogger(Main.class.getName());

    public static void main(String[] args) {
        System.setProperty("log4j.configurationFile", "file:");

        ForumSetting form = new ForumSetting();
        form.setVisible(true);
        LOGGER.debug("Initializing...");


        new HeartBeatDetector().start();
        DBConnection.createDatabase();
    }

    private static void startListeners() {

        Listener udpListener = UdpListener.getInstance();
        udpListener.initListener(NodeContext.getPort());
    }
}
