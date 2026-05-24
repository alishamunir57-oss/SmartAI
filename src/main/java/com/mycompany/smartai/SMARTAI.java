/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 */

package com.mycompany.smartai;

/**
 *
 * @author HTC
 */
public class SMARTAI {

    public static void main(String[] args) {
       
        DatabaseManager.initializeDatabase();
       java.awt.EventQueue.invokeLater(() -> {
            new LoginFrame().setVisible(true);
        }); 
    }
}
