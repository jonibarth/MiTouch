package com.example.grupo110.mitouchmobile.comunicacion.servidor;

import android.os.AsyncTask;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;

import java.io.File;
import java.io.FileInputStream;


/**
 * Created by Jonathan on 08/10/2016.
 */
public class SFTClienteDeleteFile extends AsyncTask<Void, Void, Void> {

    String SFTPHOST = "mitouch.hopto.org";
    int SFTPPORT = 22;
    String SFTPUSER = "toor";
    String SFTPPASS = "namekiano";
    final String PATH_BASE_DE_DATOS = "/home/toor/galerias";
    String archivoOriginal;
    String nombre_carpeta;

    Session 	session 	= null;
    Channel 	channel 	= null;
    ChannelSftp channelSftp = null;

/*
* Parametros de entrada:
* nombre del archivo que quiero borrar
* Carpeta donde se encuentra el archivo que quiero borrar
  */
    public SFTClienteDeleteFile(String nombre_carpeta, String nombreArchivo) {
        this.nombre_carpeta = nombre_carpeta;
        this.archivoOriginal = nombreArchivo;
    }

    @Override
    protected Void doInBackground(Void... params) {
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
            channelSftp.cd(PATH_BASE_DE_DATOS+"/"+nombre_carpeta);
            channelSftp.rm(archivoOriginal);
        }catch(Exception ex){
            ex.printStackTrace();
        }
        return null;
    }
}
