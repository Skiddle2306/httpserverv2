package org.example;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class dbConnection {
    private static String jdbcURL = "jdbc:postgresql://localhost:5432/httpserver";
    private static String username = "postgres";
    private static String password = "password";
    private static Connection connection;
    public static void getConnection() {
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
            System.out.println(statement);
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
    public static String getUsername(String user){
        try{
            String query = "SELECT username FROM userdetails WHERE username = ?";
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setString(1, user);  // Set the username parameter in the query
            // Execute the query and get the result
            System.out.println(statement);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                String retrievedUsername = resultSet.getString("username");  // Retrieve the password
                System.out.println("User exists");
                return retrievedUsername;
            } else {

                System.out.println("Username not found.");
                return null;
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    public static String getEmail(String email){
        try{
            String query = "SELECT email FROM userdetails WHERE email= ?";
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setString(1, email);  // Set the username parameter in the query
            // Execute the query and get the result
            System.out.println(statement);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                String retrievedEmail = resultSet.getString("email");  // Retrieve the password
                System.out.println("Email is in use");
                return retrievedEmail;
            } else {
                System.out.println("Email is not in use");
                return null;
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static void addUser(String user, String password, String email,String age,String profession){
        try{
            String query = "insert into userdetails (username,password,email,age,profession) values (?,?,?,?,?)";
            PreparedStatement statement = connection.prepareStatement(query);
            int count=1;
            statement.setString(count++, user);  // Set the username parameter in the query
            // Execute the query and get the result
            statement.setString(count++, password);
            statement.setString(count++, email);
            statement.setInt(count++, Integer.parseInt(age));
            statement.setString(count++, profession);
            System.out.println(statement);
            int queryResult = statement.executeUpdate();
            if (queryResult > 0) {
                System.out.println("User successfully added");
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
