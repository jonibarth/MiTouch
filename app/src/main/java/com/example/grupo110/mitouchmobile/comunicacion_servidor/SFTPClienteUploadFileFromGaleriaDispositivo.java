package com.example.grupo110.mitouchmobile.comunicacion_servidor;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.widget.Toast;

import com.example.grupo110.mitouchmobile.galeria.AddDesdeGaleria;
import com.example.grupo110.mitouchmobile.galeria.CompartirActivity;
import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;

import java.io.File;
import java.io.FileInputStream;

/**
 * Created by Jonathan on 10/10/2016.
 */

public class SFTPClienteUploadFileFromGaleriaDispositivo extends AsyncTask<Void, Void, Void> {


    //String SFTPHOST = "mitouch.hopto.org";
    String SFTPHOST = "192.168.1.33";
    int SFTPPORT = 22;
    String SFTPUSER = "toor";
    String SFTPPASS = "namekiano";
    final String PATH_BASE_DE_DATOS = "/home/toor/galerias";
    private String path_archivo;
    String nombre_archivo;
    String extension_archivo;
    String id_archivo;

    Session session 	= null;
    Channel channel 	= null;
    ChannelSftp channelSftp = null;


    public SFTPClienteUploadFileFromGaleriaDispositivo( String id_archivo, String path_archivo) {
        this.id_archivo = id_archivo;
        this.path_archivo = path_archivo;
    }


    @Override
    protected Void doInBackground(Void... params) {
        obtenerArchivo();
        obtenerExtension();
        try{
            JSch jsch = new JSch();
            session = jsch.getSession(SFTPUSER,SFTPHOST,SFTPPORT);
            session.setPassword(SFTPPASS);
            java.util.Properties config = new java.util.Properties();
            config.put("StrictHostKeyChecking", "no");
            session.setConfig(config);
            session.connect();
            channel = session.openChannel("sftp");
            channel.connect();
            channelSftp = (ChannelSftp)channel;
            channelSftp.cd(PATH_BASE_DE_DATOS);
            System.out.println("El archivo es: " + id_archivo+"."+extension_archivo);
            File f = new File(path_archivo);
            channelSftp.put(new FileInputStream(f), id_archivo+"."+extension_archivo);
        }catch(Exception ex){
            System.out.println("Error: " + ex);
            ex.printStackTrace();
        }
        return null;
    }


    @NonNull
    private void obtenerArchivo() {
        nombre_archivo = path_archivo.substring(path_archivo.lastIndexOf("/") + 1);
    }

    @NonNull
    private void obtenerExtension() {
        extension_archivo = path_archivo.substring(path_archivo.lastIndexOf(".") + 1);
    }

}
