package org.example;
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
import java.util.Scanner;
public class Main {
    private static String jdbcURL = "jdbc:postgresql://localhost:5432/httpserver";
    private static String username = "postgres";
    private static String password = "password";
    private static HashMap<String,String> session=new HashMap<>();//session id and username
    private static Connection connection;
    public static void main(String[] args){
        try{
            dbConnection();
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
    }private static void dbConnection(){
        try {
            // Load the PostgreSQL JDBC driver
            Class.forName("org.postgresql.Driver");
            // Establish the connection
            connection = DriverManager.getConnection(
                    jdbcURL, username, password);
            System.out.println(
                    "Connected to PostgreSQL database!");
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
    public static String getPassword(String user){
        try{
            String query = "SELECT password FROM userdetails WHERE username = ?";
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setString(1, user);  // Set the username parameter in the query
            // Execute the query and get the result
            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                String retrievedPassword = resultSet.getString("password");  // Retrieve the password
                System.out.println("Password for user " + user + " is: " + retrievedPassword);
                return retrievedPassword;
            } else {
                System.out.println("Username not found.");
                return null;
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}