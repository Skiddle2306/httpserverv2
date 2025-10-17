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
    protected String response= "";
    String setCookie=null;
    private HashMap<String,String> data=new HashMap<>();
    public String requestParser(BufferedReader reader) throws IOException {
        this.reader=reader;
        String line = reader.readLine();
        if(line==null){
            return "HTTP/1.1 400 Bad Request";
        }
        System.out.println(line);
        if(!line.isEmpty()) {
            String[] firstLine = line.split(" ");
            System.out.println();
            headers.put("method", firstLine[0]);
            headers.put("path", firstLine[1]);
            headers.put("version", firstLine[2]);
            if(headers.get("method").equals("GET")){
                return getResponse();
            }else if(headers.get("method").equals("POST")){
                return postRequest();
            }else{
                return "HTTP/1.1 400 Bad Request";
            }
        }else {
            System.out.println("Cannot parse request");
            return "HTTP/1.1 400 Bad Request";
        }
    }
    private String path(HashMap<String,String> vars) throws IOException {
        String version=headers.get("version");
        String path = headers.get("path");
        String cookie;
        String sessionid;
        String user;
        String password;
        if(Main.allowedPathsBeforeLogin.contains(path)){
            System.out.println("yay");
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
                System.out.println("bruh");
                return "HTTP/1.1 400 Bad Request";
            }
            if(data.get("password").equals(password)){
                System.out.println("Login Successful");
                String sessionId= genSession();
                Main.setSession(sessionId,data.get("username"));
                setCookie=("\nSet-Cookie: sessionId=" + sessionId + "; Path=/; SameSite=Lax");
                headers.put("path", "/hello.html");
                path="/hello.html";
            }else{
                System.out.println("Login Failed");
                headers.put("path", "/login.html");
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
                System.out.println(vars.get("username"));
                dbConnection.addUser(user,password,email,age,profession);
                String sessionId= genSession();
                Main.setSession(sessionId,data.get("username"));
                setCookie=("\nSet-Cookie: sessionId=" + sessionId + "; Path=/; SameSite=Lax");
                headers.put("path", "/");
                path="/";
            }
        }
        if(headers.get("path").equals("/logout")){

        }
        if(headers.get("path").equals("/")){
            headers.put("path", "/hello.html");
            path="/hello.html";
        }

        System.out.println(headers.get("path"));


        response = response.concat(version);
        if(path == null){
            System.out.println("No path provided");
            response=response.concat(" 503");
        }else {
            try{
                Scanner sc = new Scanner(new File("/home/Ayush/IdeaProjects/httpServerv2/src/main/resources"+path));
                response = response.concat(" 200 OK");
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
                if(setCookie!=null){
                    response=response.concat(setCookie);
                }
                int bytes=builder.toString().getBytes().length;
                response=response.concat("\nContent-Length: "+bytes+"\n");
                response=response.concat("Content-Type: text/html; charset=UTF-8\n\n");
                response = response.concat(builder.toString());
            } catch (Exception e) {
                System.out.println(e.getMessage());
                System.out.println("Invalid path provided");
                response=response.concat(" 400");
            }
        }
        return response;
    }
    private void readHeaders() throws IOException {
        String line = " ";
        while (true) {
            line =reader.readLine();
            System.out.println(line);
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
            System.out.println(headers.get("Content-Type"));
            if(headers.get("Content-Type").equals("application/x-www-form-urlencoded")){
                System.out.println("works");
                int length=Integer.parseInt(headers.get("Content-Length"));
                System.out.println(length);
                char[] body=new char[length];
                reader.read(body,0,length);
                System.out.println(body);
                data=parseMethods.parseUrlencoded(new String(body));
            }
        }
    }
    private String getResponse() throws IOException {
        HashMap<String,String> vars =new HashMap<>();
        readHeaders();
        if(headers.isEmpty())
            return "HTTP/1.1 400 Bad Request";
        return path(vars);
    }
    private String postRequest() throws IOException {
        HashMap<String,String> vars =new HashMap<>();
        readHeaders();
        readBody();
        if(headers.isEmpty())
            return "HTTP/1.1 400 Bad Request";
        return path(vars);
    }
    private String genSession() throws IOException {
        return UUID.randomUUID().toString();
    }

}
