package com.example.grupo110.mitouchmobile;

/*
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import net.sourceforge.jtds.jdbc.Driver;

public class DBConnection {
    private static DBConnection instance = null;
    private static String URL = "jdbc:jtds:sqlserver://192.168.1.36/MiTouch";
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
            conn = DriverManager.getConnection(URL, USER, PASS);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return conn;
    }
}
*/