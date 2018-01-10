package lk.umo.dc.config;

/**
 * Created by dev on 11/17/16.
 */
public class BootstrapServerConfig {
    private static String host;
    private static int port;

    public BootstrapServerConfig() {
    }

    public static String getHost() {
        return host;
    }

    public static void setHost(String host) {
        BootstrapServerConfig.host = host;
    }

    public static int getPort() {
        return port;
    }

    public static void setPort(int port) {
        BootstrapServerConfig.port = port;
    }
}
