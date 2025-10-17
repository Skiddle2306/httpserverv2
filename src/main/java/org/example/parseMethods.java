package org.example;
import java.net.URLEncoder;
import java.util.HashMap;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

public class parseMethods {
    public static HashMap<String,String> parseUrlencoded(String raw) throws UnsupportedEncodingException {
        HashMap<String,String> data=new HashMap<>();
        String[] parts=raw.split("&");
        for(String part:parts){
            String[] parts2=part.split("=");
            if(parts2.length==2) {
                data.put(URLDecoder.decode(parts2[0], "UTF-8"), URLDecoder.decode(parts2[1], "UTF-8"));
                System.out.print(URLDecoder.decode(parts2[0], "UTF-8"));
                System.out.println(" : " + URLDecoder.decode(parts2[1], "UTF-8"));
            }else {
                System.out.println(part);
            }
        }
        return data;
    }
}
