/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package lk.umo.dc.util;

import java.math.BigInteger;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 *
 * @author yasitham
 */
public class Utils {
    
    public static String getIP(){
        
        String ip = "";
        try {
             InetAddress IP=InetAddress.getLocalHost();
             ip = IP.getHostAddress();  
          } catch (UnknownHostException e) {

          }
        return ip;
    }

    public static String md5(String s){
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] digest = md.digest(s.getBytes());
            BigInteger bigInt = new BigInteger(1,digest);
           return bigInt.toString(16);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return null;
    }
}
