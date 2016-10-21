package com.example.grupo110.mitouchmobile.base_de_datos;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;


/**
 * Created by Jonathan on 25/08/2016.
 */


// http://www.androidhive.info/2012/05/how-to-connect-android-with-php-mysql/
    // http://in1.php.net/pg_connect
    // http://www.javamexico.org/foros/java_enterprise/error_conexion_remota_postgresql_solucionado

public class PostgrestBD  implements Runnable {

    private Connection conn;
    /* Usuario Base de Datos: MiTouch password grupo 110*/

    private String host = "mitouch.hopto.org";
    private String db = "postgres";
    private int port = 5432;
    private String user = "postgres";
    private String pass = "mitouch";
/*
    private String host = "192.168.2.104";
    private String db = "postgres";
    private int port = 5432;
    private String user = "postgres";
    private String pass = "namekiano";


    private String host = "10.0.2.2";
    private String db = "MiTouch";
    private int port = 5432;
    private String user = "postgres";
    private String pass = "admin";
*/
    private String url = "jdbc:postgresql://%s:%d/%s";
    public PostgrestBD() {
        super();
        this.url = String.format(this.url,this.host, this.port, this.db);
        this.conecta();
        this.disconecta();
    }

    @Override
    public void run() {
        try{
            Class.forName("org.postgresql.Driver");
            this.conn = DriverManager.getConnection(this.url,this.user,this.pass);
            System.out.println(" ************* Postgresql Conectada! ************* ");
        }catch (Exception e){
            e.printStackTrace();
            System.err.println(" ************* Error: Cant connect Postgresql! ************* ");

        }
    }

    private void conecta(){
        Thread thread = new Thread(this);
        thread.start();
        try{
            thread.join();
        }catch (Exception e){
            e.printStackTrace();
            System.err.println("Error: clase conecta!");
        }
    }

    private void disconecta(){
        if (this.conn!= null){
            try{
                this.conn.close();
            }catch (Exception e){
                e.printStackTrace();
                System.err.println("Error: Cant desconnect!");

            }finally {
                this.conn = null;
            }
        }
    }

    public ResultSet execute(String query){
        this.conecta();
        ResultSet resultSet = null;
        try {
            resultSet = new ExecuteDB(this.conn, query).execute().get();
        }catch (Exception e){
            e.printStackTrace();
            System.err.println("Error: ejection Query!");
        }
        return resultSet;
    }
}