package com.example.grupo110.mitouchmobile;


import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.SQLException;
//import net.sourceforge.jtds.jdbc.Driver;


public class DBConnection {
    private static DBConnection instance = null;
    private static String URL = "jdbc:jtds:sqlserver://SERVER:1433/DATABASE";
    private static String USER = "USERNAME";
    private static String PASS = "PASSWORD";
    private static Connection connection = null;

    private DBConnection(){}

    public static DBConnection getInstance(){
        if(instance==null)
            instance=new DBConnection();
        return instance;
    }
    public Connection getConnection(){
        if(connection==null)
            connection=conectar();
        return connection;
    }
    private Connection conectar() {
        Connection conn = null;
        try {
           // (new Driver()).getClass();
            /*conseguí el error... se debe agregar al path la ubicación del .jar luego para las versiones 3.0
            o superior se debe usar los métodos asíncronos ya que android no lo permite hacer para evitar el
            cierre de las aplicaciones y por ultimo use la versión 1.2.7 del jtds ya que la 1.3.1 no me funciono. */
            conn = DriverManager.getConnection(URL, USER, PASS);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return conn;
    }
}
