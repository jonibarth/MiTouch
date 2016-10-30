package com.example.grupo110.mitouchmobile.chat;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.example.grupo110.mitouchmobile.base_de_datos.PostgrestBD;

import java.io.BufferedWriter;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.sql.ResultSet;

import static java.lang.Integer.parseInt;

/**
 * Created by Jonathan on 22/09/2016.
 */
public class SocketCliente implements Runnable {
    private String ip;
    private int id_usuario;


    public SocketCliente(int id_usuario) {
        super();
        this.id_usuario = id_usuario;
        this.conectaChat();
    }

    @Override
    public void run() {
        try {
            System.out.println("Estoy en el run: " + Utils.getIPAddress(true).toString()); // IPv4
        }catch (Exception e){System.out.println("Estoy en el run1: ");}
        //System.out.println( "Estoy en el run: " + Utils.getMACAddress("wlan0").toString());
        //System.out.println( "Estoy en el run: " + Utils.getMACAddress("eth0").toString());

        buscarGruposdeUsuario();

        leerBaseDeDatos();
    }

    /*
    * Busco los grupos de usuario y sus usuarios
     */
    private void leerBaseDeDatos() {
    }

    /*
    * Este metodo de dice a que grupos pertenezco, valido que no me hayan quitado acceso!
     */
    private void buscarGruposdeUsuario() {
        System.out.println("buscarGruposdeUsuario ");
        String comando;
        PostgrestBD baseDeDatos = new PostgrestBD();
        comando = String.format("SELECT ugru_id_grupo FROM \"MiTouch\".t_usuarios_grupo WHERE ugru_id_usuario="+id_usuario+
                " AND ugru_hasta IS null" +" ;");
        ResultSet resultSet = baseDeDatos.execute(comando);

        try {
            while (resultSet.next()) {

                System.out.println(resultSet.getInt("ugru_id_grupo"));

                //comando = String.format("INSERT INTO \"MiTouch\".t_chat (sol_id_usuario,sol_id_grupo,sol_fecha_hora,sol_fecha_hora_respuesta,sol_estado) VALUES (" + id_usuario + ",'" + BuscarGruposdeUsuarioenArray() + "','" + diahora + "',null,null);");
                //ResultSet resultSet2 = baseDeDatos.execute(comando);
            }
        } catch (Exception e) {
            System.err.println("Error busqueda grupos de usuario: " + e );
        }
    }

    private void conectaChat(){
        Thread thread = new Thread(this);
        thread.start();
        try{
            thread.join();
        }catch (Exception e){
            e.printStackTrace();
            System.err.println("Error: clase conecta!");
        }
    }
}