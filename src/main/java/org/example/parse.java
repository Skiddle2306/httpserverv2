package org.example;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Scanner;
import java.util.UUID;

public class parse {
    BufferedReader reader=null;
    private httpResponse response;
    String res=" ";
    private HashMap<String,String> headers=new HashMap<String,String>();
    private HashMap<String,String> data=new HashMap<String,String>();
    private HashMap<String,String> vars=new HashMap<String,String>();


    private String method;
    private String version;

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    private String path;
    private String username;
    private String password;
    int bytes;
    String body;

    HashSet<String> needAuth=new HashSet<>();

    public String parseRequest(BufferedReader reader) throws IOException {
        this.reader=reader;
        readFirstLine();
        if(res.equals("Done")){
            return response.getResponse();
        }
        readHeaders();
        if(res.equals("Done")){
            return response.getResponse();
        }
        readBody();
        checkLogin();
        needAuth = authenticationPaths.getData();
        if(needAuth.contains(path)){
            authenticationPaths auth=new authenticationPaths(path,data);
            return auth.authenticate();
        }

        {
            genFile(vars);
            if(bytes==0){
                response =new httpResponse("HTTP/1.1",404,"Not Found");
                return response.getResponse();
            }
            response =new httpResponse("HTTP/1.1",200,"OK");
            response.addBody(bytes,body);
        }
        return response.getResponse();

    }
    private void readFirstLine() throws IOException {
        String line = reader.readLine();
        if (line == null){
            response = new httpResponse("HTTP/1.1", 400, "Bad Request");
            res = "Done";
            return;
        }else if (!line.isEmpty()) {
            String[] firstLine = line.split(" ");
            if (firstLine.length != 3) {
                response = new httpResponse("HTTP/1.1", 400, "Bad Request");
                res= "Done";
            }
            headers.put("method", firstLine[0]);
            method=firstLine[0];
            headers.put("path", firstLine[1]);
            path=firstLine[1];
            headers.put("version", firstLine[2]);
            version=firstLine[2];
        }

    }
    private void readHeaders() throws IOException {
        String line;
        while (true) {
            line =reader.readLine();
            if (line.isEmpty()) {
                break;
            }
            String[] split = line.split(":", 2);
            if (split.length == 2) {
                headers.put(split[0].trim(), split[1].trim());
            }
        }
    }private void readBody() throws IOException {
        if(headers.containsKey("Content-Type")){
            if(headers.get("Content-Type").equals("application/x-www-form-urlencoded")){
                int length=Integer.parseInt(headers.get("Content-Length"));
                char[] body=new char[length];
                reader.read(body,0,length);
                data=parseMethods.parseUrlencoded(new String(body));
            }
        }
    }

    void checkLogin(){
        if(Main.allowedPathsBeforeLogin.contains(path)){
            //ignores if path does not require login
        }
        else if(headers.get("Cookie")!=null){
            String cookie =headers.get("Cookie");
            String sessionid=cookie.split("sessionId=")[1];
            HashMap<String,String> allSession=Main.getSession();
            boolean checkSession=false;
            for(String s:allSession.keySet()){
                if(sessionid.equals(s)){
                    checkSession=true;
                }
            }if(!checkSession){
                vars.put("<!-- error-message -->", "Please Login in again");
                path="/login.html";
            }
        }else{
            vars.put("<!-- error-message -->", "Please Login before using our site.");
            path="/login.html";
            //invalid cookie
        }
    }
    private void login() throws IOException {
        username=data.get("username");
        password=dbConnection.getPassword(username);

        if(username == null || path == null){
             response=new httpResponse("HTTP/1.1",400,"Bad Request");
            res="Done";
        }else if(data.get("password").equals(password)){
            System.out.println("Login Successful");
            String sessionId= genSession();
            Main.setSession(sessionId,data.get("username"));
            path="/hello.html";
            response=new httpResponse("HTTP/1.1",302,"Found");
            response.addHeader("Location",path);
            response.addCookie("sessionId",sessionId);
            return ;
        }else{
                System.out.println("Login Failed");
                path="/login.html";
                vars.put("<!-- error-message -->", "Invalid Username/Password");
                response=new httpResponse("HTTP/1.1",200,"OK");
                genFile(vars);
                response.addBody(bytes,body);
                res="Done";
                return;
        }

    }
    public static String genSession() throws IOException {
        return UUID.randomUUID().toString();
    }

    public  void genFile(HashMap<String,String> vars){
        try{
            System.out.println(path);
            Scanner sc = new Scanner(new File("/home/Ayush/IdeaProjects/httpServerv2/src/main/resources"+path));
            StringBuilder builder = new StringBuilder();
            while(sc.hasNextLine()){
                String line = sc.nextLine();
                for(String key : vars.keySet()){
                    if(line.contains(key)){
                        line = line.replace(key,vars.get(key));
                    }
                }
                builder.append(line);
            }
            bytes=builder.toString().getBytes().length;

            body= builder.toString();

        } catch (Exception e) {
            System.out.println(e.getMessage());
            System.out.println("Invalid path provided");
        }
    }
}
