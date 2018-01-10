package lk.umo.dc.util;

import lk.umo.dc.domain.model.Operation;
/**
 * Created by dev on 11/11/16.
 */
public class MessageUtils {

    private static final int MESSAGE_LENGTH_TOKEN_LENGH = 4;

    //TODO implement
    public static String createMsg(Operation type, String ipAddress, int port, String name){
        String msg = "";
        int length;
        String strlen;
        String paddedlen;
        
        if(type == Operation.REG){
            msg = String.format("REG %s %d %s", ipAddress, port, name); 
        }
        else if(type == Operation.JOIN){
             msg = String.format("JOIN %s %d", ipAddress, port); 
        }
        else if(type == Operation.SER){
            msg = String.format("SER %s %d %s", ipAddress, port, name); 
        }
        
        length = msg.length() + 5;
        strlen = Integer.toString(length);
        paddedlen = "0000".substring(strlen.length()) + strlen;
        
        msg = paddedlen + " " + msg;
        return msg;
    }


    /**
     * Prepend message length to the message
     *
     * @param message message eg: REG 129.82.123.45 5001 1234abcd
     * @return formatted message eg: 0036 REG 129.82.123.45 5001 1234abcd
     */
    public static String prependLength(String message) throws IllegalArgumentException {
        message = ' ' + message;//prepend a space
        message = formatToFourDigitString(message.length() + MESSAGE_LENGTH_TOKEN_LENGH) + message;
        return message;
    }


    /**
     * Format an integer into a four digit string value
     *
     * @param number
     * @return formatted String
     */
    public static String formatToFourDigitString(int number) throws IllegalArgumentException {
        String strNumber = String.valueOf(number);
        if (strNumber.length() > MESSAGE_LENGTH_TOKEN_LENGH) {
            throw new IllegalArgumentException("Message is too long");
        } else {
            while (strNumber.length() != MESSAGE_LENGTH_TOKEN_LENGH) {
                strNumber = '0' + strNumber;
            }
        }
        return strNumber;
    }

}
