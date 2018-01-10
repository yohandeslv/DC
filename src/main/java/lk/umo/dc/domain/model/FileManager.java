/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package lk.umo.dc.domain.model;

import java.util.*;

/**
 *
 * @author yasitham
 */
public class FileManager {
    
    private static List<String> fileStorage = Collections.synchronizedList(new ArrayList<String>());

    public static void addFile(String fileName){
        fileStorage.add(fileName);
    }
    
    public static void removeFile(String fileName){
        fileStorage.remove(fileName);
    }
    
    public static List<String> searchFile(String keyword){
        List<String> searchResult = new ArrayList<>();
        Iterator<String> itr = fileStorage.iterator();
         
        for (String key : keyword.split(" ")){
            while(itr.hasNext()){
                String file = itr.next();
                if(file.toLowerCase().contains(key.toLowerCase())){
                    if(!searchResult.contains(file)){
                        searchResult.add(file);
                    }
                }
            }
        }
        return searchResult;
    }

    public static void clear() {
        fileStorage.clear();
    }
}
