package org.example;

import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;

public class authenticationPaths {
    HashMap<String,String> vars=new HashMap<>();
    HashMap<String,String> data=new HashMap<>();
    String path;
    httpResponse response;
    authenticationPaths(String path,HashMap<String,String> data){
        this.data=data;
        this.path=path;
    }
    public static HashSet<String> getData(){
        HashSet<String> set=new HashSet<>();
        set.add("/login");
                set.add("/register");
        return set;
    }
    public String authenticate() throws IOException {
        switch(path){
            case "/login":{
                return login();
            }
            case "/register":{
                return register();
            }
            default:{
                response=new httpResponse("HTTP/1.1",503,"Internal Server Error");
                return response.getResponse();
            }
        }
    }
    private String login() throws IOException {
        String user=data.get("username");
        String password=dbConnection.getPassword(user);
        if(user == null || path == null){
            response =new httpResponse("HTTP/1.1",400,"Bad Request");
            path="login.html";
            parse p = new parse();
            p.setPath(path);
            p.genFile(vars);
            response.addBody(p.bytes,p.body);
            return response.getResponse();
        }
        if(data.get("password").equals(password)){
            System.out.println("Login Successful");
            String sessionId= parse.genSession();
            Main.setSession(sessionId,data.get("username"));
            path="/hello.html";
            response =new httpResponse("HTTP/1.1",302,"Found");
            response.addHeader("Location",path);
            response.addCookie("sessionId",sessionId);
            System.out.println(response.toString());
            return response.getResponse();
        }else{
            System.out.println("Login Failed");
            path="/login.html";
            vars.put("<!-- error-message -->", "Invalid Username/Password");
            parse p = new parse();
                        p.setPath(path);

            p.genFile(vars);
            response=new httpResponse("HTTP/1.1",401,"Unauthorized");
            response.addBody(p.bytes,p.body);
            return response.getResponse();
        }
    }
    private String register() throws IOException {
        String user=data.get("username");
           String password=data.get("password");
            String email=data.get("email");
            String age=data.get("age");
            String profession=data.get("profession");
            if(dbConnection.getUsername(user)!=null){
                vars.put("<!-- error-message -->", "Username already exists");
                path="/register.html";
                parse p = new parse();
                            p.setPath(path);

                p.genFile(vars);
                response = new httpResponse("HTTP/1.1",409,"Conflict");
                response.addBody(p.bytes,p.body);
                return response.getResponse();
            }else if(dbConnection.getEmail(email)!=null){
                vars.put("<!-- error-message -->", "Email is already in use");
                path="/register.html";
                parse p = new parse();
                            p.setPath(path);

                p.genFile(vars);
                response = new httpResponse("HTTP/1.1",409,"Conflict");
                response.addBody(p.bytes,p.body);
                return response.getResponse();
            }else{
                dbConnection.addUser(user,password,email,age,profession);
                String sessionId= parse.genSession();
                Main.setSession(sessionId,data.get("username"));
                path="/hello.html";
                response =new httpResponse("HTTP/1.1",302,"Found");
                response.addHeader("Location",path);
                response.addCookie("sessionId",sessionId);
                return response.getResponse();
            }
    }
}
