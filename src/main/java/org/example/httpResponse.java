package org.example;
import java.util.HashMap;
public class httpResponse {
    String version;
    int status;
    String message;
    private HashMap<String,String> headers=new HashMap<>();
    private HashMap<String,String> cookies=new HashMap<>();
    private StringBuilder response=new StringBuilder();
    int bytes;
    String type;
    String body;
    httpResponse(String version,int status){
        this.status=status;
        this.type=version;
    }
    httpResponse(String version,int status,String message){
        this.version=version;
        this.status=status;
        this.message=message;
    }
    public void addHeader(String key,String value){
        headers.put(key,value);
    }
    public void addCookie(String key,String value){
        cookies.put(key,value);
    }
    public void addBody(int bytes,String type,String body){
        this.bytes=bytes;
        this.type=type;
        this.body=body;
    }
    public String getResponse(){
        response.append(version);
        response.append(" ");
        response.append(status);
        response.append(" ");
        if(message==null){
            if(status==200){
                response.append("OK");
            }
            if(status==302){
                response.append("Found");
            }
            if(status==404){
                response.append("Not Found");
            }
            if(status==401){
                response.append("Unauthorized");
            }
            if(status==403){
                response.append("Forbidden");
            }
            if(status==500){
                response.append("Internal Server Error");
            }
            if(status==503){
                response.append("Service Unavailable");
            }
        }else{
            response.append(message);
            response.append("\r\n");
        }
        for(String key : headers.keySet()){
            response.append(key+": "+headers.get(key) + "\r\n");
        }
        for(String key : cookies.keySet()){
            response.append("Set-Cookie: "+key+ "=" + cookies.get(key) + "; Path=/; SameSite=Lax\r\n");
        }
        if(bytes>0){
            response.append("Content-Length: " + bytes + "\r\n");
//            response.append("Content-Type: " + type + "; charset=UTF-8\r\n");
            response.append("\r\n");
            response.append(body);
        }
        System.out.println(response.toString());
        return response.toString();
    }
}
