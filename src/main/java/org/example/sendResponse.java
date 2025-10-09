package org.example;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Scanner;
import java.sql.*;

public class sendResponse {
    public static void main(String[] args){
        String jdbcURL = "jdbc:postgresql://localhost:5432/httpserver";
        String username = "postgres";
        String password = "password";

        try {
            // Load the PostgreSQL JDBC driver
            Class.forName("org.postgresql.Driver");

            // Establish the connection
            Connection connection = DriverManager.getConnection(
                    jdbcURL, username, password);
            System.out.println(
                    "Connected to PostgreSQL database!");
            String user="dumb";
            String query = "SELECT password FROM userdetails WHERE username = ?";
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setString(1, user);  // Set the username parameter in the query
            // Execute the query and get the result
            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                String retrievedPassword = resultSet.getString("password");  // Retrieve the password
                System.out.println("Password for user " + user + " is: " + retrievedPassword);
            } else {
                System.out.println("Username not found.");
            }
            resultSet.close();
            statement.close();
            connection.close();
            System.out.println("Connection closed.");
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
}
