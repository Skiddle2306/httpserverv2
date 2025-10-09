package org.example;
import java.util.HashMap;

public class parseMethods {
    public static HashMap<String,String> parseUrlencoded(String raw){
        HashMap<String,String> data=new HashMap<>();
        String[] parts=raw.split("&");
        for(String part:parts){
            String[] parts2=part.split("=");
            if(parts2.length==2) {
                data.put(parts2[0], parts2[1]);
                System.out.print(parts2[0]);
                System.out.println(" : " + parts2[1]);
            }else {
                System.out.println(part);
            }
        }
        return data;
    }
}
