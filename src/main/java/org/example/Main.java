package org.example;
import javax.swing.plaf.nimbus.State;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Scanner;
public class Main {
    private static HashMap<String,String> session=new HashMap<>();//session id and username
    private static Connection connection;
    public static HashSet<String> allowedPathsBeforeLogin=new HashSet<>();
    public static void main(String[] args){
        setAllowedPathsBeforeLogin();
        try{
            dbConnection.getConnection();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        Scanner input;
        ServerSocket serverSocket;
        try{
            serverSocket = new ServerSocket(8080);
            System.out.println("Server started on port 8080");
        } catch (IOException e) {
            System.out.println(e.getMessage());
            throw new RuntimeException(e);
        }
        while(true){
            try{
                Socket client = serverSocket.accept();
                System.out.println("Client accepted");
                input = new Scanner(client.getInputStream());
                BufferedReader reader = new BufferedReader(new InputStreamReader(client.getInputStream()));
                parseRequest request=new parseRequest();
                String response = request.requestParser(reader);
                PrintWriter out = new PrintWriter(client.getOutputStream(),true);
                System.out.println(response);
                out.println(response);
                input.close();
                out.close();
                client.close();
            }catch (IOException e){
                System.out.println(e.getMessage());
                }
        }
    }
    public static HashMap<String, String> getSession() {
        return session;
    }
    public static void setSession(String ses,String username) {
        session.put(ses,username);
        System.out.println(ses);
        System.out.println(username);
    }

    private static void setAllowedPathsBeforeLogin(){
        allowedPathsBeforeLogin.add("/login");
        allowedPathsBeforeLogin.add("/register");
        allowedPathsBeforeLogin.add("/login.html");
        allowedPathsBeforeLogin.add("/register.html");
    }
}