/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package pkg3rd_year_s2_f;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 *
 * @author Admin
 */
public class DBConnection {

    private static final String URL =
            "jdbc:mysql://localhost:3306/guesthouse_db";

    private static final String USER =
            "root";

    private static final String PASSWORD =
            "REMOVED";

    public static Connection getConnection() {

        Connection conn = null;

        try {

            Class.forName("com.mysql.cj.jdbc.Driver");

            conn = DriverManager.getConnection(
                    URL,
                    USER,
                    PASSWORD
            );

        } catch (ClassNotFoundException | SQLException e) {

            System.out.println(
                    "Database Connection Error : "
                    + e.getMessage()
            );

        }

        return conn;
    }
}