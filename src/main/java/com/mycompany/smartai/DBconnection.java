/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.smartai;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
public class DBconnection {
    private static final String URL = 
        "jdbc:sqlserver://localhost:1433;databaseName=SMARTAI_DB;encrypt=false";
    
    private static final String USER = "alisha";
    private static final String PASSWORD = "1234";
    

    // Method to get connection
    public static Connection getConnection() {
        try {
            // Load driver
            Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");

            // Create connection
            Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);

            System.out.println(" Connected to Database Successfully!");
            return conn;

        } catch (ClassNotFoundException e) {
            System.out.println("JDBC Driver not found!");
            e.printStackTrace();
        } catch (SQLException e) {
            System.out.println(" Database connection failed!");
            e.printStackTrace();
        }
        return null;
    }
    
    
}
