/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package srexwingsapp.conexion;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Conexion {

    private static final String URL  = "jdbc:mysql://localhost:3306/srexwings_db?useSSL=false&serverTimezone=UTC";
    private static final String USER = "root";       // o el usuario que uses
    private static final String PASS = "Am3lc010";   // ← tu contraseña

    public static Connection getConexion() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASS);
    }
}
