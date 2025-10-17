package org.example;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Scanner;
import java.util.UUID;
public class parseRequest {
    protected HashMap<String,String> headers=new HashMap<>();
    private BufferedReader reader;
    private httpResponse res;
    String setCookie=null;
    String path;
    int bytes;
    String body;
    private HashMap<String,String> data=new HashMap<>();
            HashMap<String,String> vars =new HashMap<>();

    public String requestParser(BufferedReader reader) throws IOException {
        this.reader=reader;
        String line = reader.readLine();
        if(line==null){
            res=new httpResponse("HTTP/1.1",400,"Bad Request");
            return res.toString();
        }
        if(!line.isEmpty()) {
            String[] firstLine = line.split(" ");
            headers.put("method", firstLine[0]);
            headers.put("path", firstLine[1]);
            headers.put("version", firstLine[2]);
            if(headers.get("method").equals("GET")){
                return getResponse();
            }else if(headers.get("method").equals("POST")){
                return postRequest();
            }else{
                res=new httpResponse("HTTP/1.1",400,"Bad Request");
            return res.toString();
            }
        }else {
            res=new httpResponse("HTTP/1.1",400,"Bad Request");
            return res.toString();
        }
    }
    private String path() throws IOException {
        String version=headers.get("version");
        path = headers.get("path");
        String cookie;
        String sessionid;
        String user;
        String password;
        if(Main.allowedPathsBeforeLogin.contains(path)){

        }
        else if(headers.get("Cookie")!=null){
            cookie =headers.get("Cookie");
            sessionid=cookie.split("sessionId=")[1];
            HashMap<String,String> allSession=Main.getSession();
            boolean checkSession=false;
            for(String s:allSession.keySet()){
                if(sessionid.equals(s)){
                    checkSession=true;
                    user=allSession.get(s);
                }
            }if(!checkSession){
                vars.put("<!-- error-message -->", "Please Login in again");
                path="/login.html";
            }
        }else{
            vars.put("<!-- error-message -->", "Please Login before using our site.");
            path="/login.html";
        }

        if(headers.get("path").equals("/login")){
            user=data.get("username");
            password=dbConnection.getPassword(user);
            if(user == null || path == null){
                res=new httpResponse("HTTP/1.1",400,"Bad Request");
            return res.toString();
            }
            if(data.get("password").equals(password)){
                System.out.println("Login Successful");
                String sessionId= genSession();
                Main.setSession(sessionId,data.get("username"));
                headers.put("path", "/hello.html");
                path="/hello.html";
                res=new httpResponse("HTTP/1.1",302,"Found");
                res.addHeader("Location",path);
                res.addCookie("sessionId",sessionId);
                setCookie=("\nSet-Cookie: sessionId=" + sessionId + "; Path=/; SameSite=Lax");
                System.out.println(res.toString());
                return res.getResponse();
            }else{
                System.out.println("Login Failed");
                headers.put("path", "/login.html");
                path="/login.html";
                vars.put("<!-- error-message -->", "Invalid Username/Password");
            }
        }
        if(headers.get("path").equals("/register")){
            user=data.get("username");
            password=data.get("password");
            String email=data.get("email");
            String age=data.get("age");
            String profession=data.get("profession");
            if(dbConnection.getUsername(user)!=null){
                vars.put("<!-- error-message -->", "Username already exists");
                headers.put("path", "/register.html");
                path="/register.html";
            }else if(dbConnection.getEmail(email)!=null){
                vars.put("<!-- error-message -->", "Email is already in use");
                headers.put("path", "/register.html");
                path="/register.html";
            }else{
                dbConnection.addUser(user,password,email,age,profession);
                String sessionId= genSession();
                Main.setSession(sessionId,data.get("username"));
                headers.put("path", "/hello.html");
                path="/hello.html";
                res=new httpResponse(version,302,"Found");
                res.addHeader("Location",path);
                res.addCookie("sessionId",sessionId);
                return res.getResponse();
            }
        }
        if(headers.get("path").equals("/logout")){

        }
        if(headers.get("path").equals("/")){
            headers.put("path", "/hello.html");
            path="/hello.html";
        }



        if(path == null){
            res=new httpResponse("HTTP/1.1",404,"Not Found");
            return res.getResponse();
        }else {
            genFile();
            if(bytes==0){
                res=new httpResponse("HTTP/1.1",404,"Not Found");
                return res.getResponse();
            }
            res=new httpResponse("HTTP/1.1",200,"OK");
            res.addBody(bytes,"text/html",body);
        }
        return res.getResponse();
    }
    private void readHeaders() throws IOException {
        String line = " ";
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
    }
    private void readBody() throws IOException {
        if(headers.containsKey("Content-Type")){
            if(headers.get("Content-Type").equals("application/x-www-form-urlencoded")){
                int length=Integer.parseInt(headers.get("Content-Length"));
                char[] body=new char[length];
                reader.read(body,0,length);
                data=parseMethods.parseUrlencoded(new String(body));
            }
        }
    }
    private String getResponse() throws IOException {
        readHeaders();
        if(headers.isEmpty()) {
            res = new httpResponse("HTTP/1.1", 400, "Bad Request");
            return res.toString();
        }
        return path();
    }
    private String postRequest() throws IOException {
        HashMap<String,String> vars =new HashMap<>();
        readHeaders();
        readBody();
        if(headers.isEmpty()) {
            res=new httpResponse("HTTP/1.1",400,"Bad Request");
            return res.toString();
        }
        return path();
    }
    private String genSession() throws IOException {
        return UUID.randomUUID().toString();
    }

    public  void genFile(){
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
