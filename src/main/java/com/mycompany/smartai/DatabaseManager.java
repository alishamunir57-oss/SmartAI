/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.smartai;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
public class DatabaseManager {
    public static void initializeDatabase() {
    try (Connection conn = DBconnection.getConnection();
         Statement stmt = conn.createStatement()) {

        String createUsers = "IF NOT EXISTS (SELECT * FROM sysobjects WHERE name='Users') " +
                "CREATE TABLE Users (" +
                "user_id INT PRIMARY KEY IDENTITY(1,1)," +
                "username VARCHAR(50) UNIQUE," +
                "email VARCHAR(100)," +
                "password VARCHAR(100))";
        String createChats = "IF NOT EXISTS (SELECT * FROM sysobjects WHERE name='Chats') " +
                "CREATE TABLE Chats (" +
                "chat_id INT PRIMARY KEY IDENTITY(1,1)," +
                "user_id INT," +
                "chat_title VARCHAR(100)," +
                "created_at DATETIME DEFAULT GETDATE()," +
                "FOREIGN KEY (user_id) REFERENCES Users(user_id))";
        String createMessages = "IF NOT EXISTS (SELECT * FROM sysobjects WHERE name='Messages') " +
                "CREATE TABLE Messages (" +
                "message_id INT PRIMARY KEY IDENTITY(1,1)," +
                "chat_id INT," +
                "message TEXT," +
                "response TEXT," +
                "timestamp DATETIME DEFAULT GETDATE()," +
                "FOREIGN KEY (chat_id) REFERENCES Chats(chat_id))";

       
        stmt.execute(createUsers);
        stmt.execute(createChats);
        stmt.execute(createMessages);
        System.out.println("Tables checked/created");

    } catch (Exception e) {
        e.printStackTrace();
    }
}
    public static boolean registerUser(String username, String email, String password) {
        String sql = "INSERT INTO Users (username, email, password) VALUES (?, ?, ?)";

        try (Connection conn = DBconnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, username);
            ps.setString(2, email);
            ps.setString(3, password);

            int rows = ps.executeUpdate();
            return rows > 0;

        } catch (SQLException e) {
            System.out.println(" Registration failed");
            e.printStackTrace();
            return false;
        }
    }

   
    public static boolean loginUser(String username, String password) {
        String sql = "SELECT * FROM Users WHERE username=? AND password=?";

        try (Connection conn = DBconnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, username);
            ps.setString(2, password);

            ResultSet rs = ps.executeQuery();

            return rs.next(); 

        } catch (SQLException e) {
            System.out.println("Login failed");
            e.printStackTrace();
            return false;
        }
    }

    public static int getUserId(String username) {
        String sql = "SELECT user_id FROM Users WHERE username=?";

        try (Connection conn = DBconnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, username);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return rs.getInt("user_id");
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }
    


public static int createChat(int userId, String title) {
    String sql = "INSERT INTO Chats (user_id, chat_title) VALUES (?, ?)";

    try (Connection conn = DBconnection.getConnection();
         PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

        ps.setInt(1, userId);
        ps.setString(2, title);
        ps.executeUpdate();

        ResultSet rs = ps.getGeneratedKeys();
        if (rs.next()) {
            return rs.getInt(1); // chat_id
        }

    } catch (Exception e) {
        e.printStackTrace();
    }
    return -1;
}
public static void saveMessage(int chatId, String message, String response) {
    String sql = "INSERT INTO Messages (chat_id, message, response) VALUES (?, ?, ?)";

    try (Connection conn = DBconnection.getConnection();
         PreparedStatement ps = conn.prepareStatement(sql)) {

        ps.setInt(1, chatId);
        ps.setString(2, message);
        ps.setString(3, response);

        ps.executeUpdate();

    } catch (Exception e) {
        e.printStackTrace();
    }
}
public static List<String> getMessages(int chatId) {
    List<String> list = new ArrayList<>();

    String sql = "SELECT message, response FROM Messages WHERE chat_id=? ORDER BY timestamp";

    try (Connection conn = DBconnection.getConnection();
         PreparedStatement ps = conn.prepareStatement(sql)) {

        ps.setInt(1, chatId);
        ResultSet rs = ps.executeQuery();

        while (rs.next()) {
            list.add("You: " + rs.getString("message"));
            list.add("AI: " + rs.getString("response"));
        }

    } catch (Exception e) {
        e.printStackTrace();
    }

    return list;
}
public static List<String[]> getChats(int userId) {
    List<String[]> chats = new ArrayList<>();

    String sql = "SELECT chat_id, chat_title FROM Chats WHERE user_id=? ORDER BY created_at";

    try (Connection conn = DBconnection.getConnection();
         PreparedStatement ps = conn.prepareStatement(sql)) {

        ps.setInt(1, userId);
        ResultSet rs = ps.executeQuery();

        while (rs.next()) {
            String chatId = String.valueOf(rs.getInt("chat_id"));
            String title = rs.getString("chat_title");
            chats.add(new String[]{chatId, title});
        }

    } catch (Exception e) {
        e.printStackTrace();
    }
    return chats;
}
public static void deleteChat(int chatId) {

    String deleteMessages =
            "DELETE FROM Messages WHERE chat_id=?";

    String deleteChat =
            "DELETE FROM Chats WHERE chat_id=?";

    try (Connection conn = DBconnection.getConnection()) {

        // delete messages first
        PreparedStatement ps1 =
                conn.prepareStatement(deleteMessages);

        ps1.setInt(1, chatId);

        ps1.executeUpdate();

        // then delete chat
        PreparedStatement ps2 =
                conn.prepareStatement(deleteChat);

        ps2.setInt(1, chatId);

        ps2.executeUpdate();

    } catch (Exception e) {

        e.printStackTrace();
    }
}
}


