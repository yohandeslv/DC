package lk.umo.dc.util;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by dev on 11/19/16.
 */
public class ErrorCodeResolver {

    public static final int OK_SUCCESS = 0;
    public static final int OK_MIN_FAIL_RANGE = 9996;

    public static Map<Integer, String> ERROR_CODES_REG = new HashMap<>();
    public static Map<Integer, String> ERROR_CODES_UNREG = new HashMap<>();

    static {
        ERROR_CODES_REG.put(OK_SUCCESS, "Success");
        ERROR_CODES_REG.put(1, "Success and returned 1 node");
        ERROR_CODES_REG.put(2, "Success and returned 2 nodes");
        ERROR_CODES_REG.put(9999, "Failed, there is some error in the command");
        ERROR_CODES_REG.put(9998, "Failed, already registered to you, unregister first");
        ERROR_CODES_REG.put(9997, "Failed, registered to another user, try a different IP and port");
        ERROR_CODES_REG.put(OK_MIN_FAIL_RANGE, "Failed,  can’t register. BS full");

        ERROR_CODES_UNREG.put(OK_SUCCESS, "Success");
        ERROR_CODES_UNREG.put(1, "Error while unregistering. IP and port may not be in the registry or command is incorrect.");


    }

    /**
     * Retrieve error message for the status code
     *
     * @param code status code
     * @return String status message
     */
    public static String getRegStatusMessage(int code) {
        return ERROR_CODES_REG.get(code);
    }

    /**
     * Retrieve error message for the status code
     *
     * @param code status code
     * @return String status message
     */
    public static String getUnRegStatusMessage(int code) {
        return ERROR_CODES_UNREG.get(code);
    }
}
