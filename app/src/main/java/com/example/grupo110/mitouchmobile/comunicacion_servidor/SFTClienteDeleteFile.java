package com.example.grupo110.mitouchmobile.comunicacion_servidor;

import android.os.AsyncTask;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;


/**
 * Created by Jonathan on 08/10/2016.
 */
public class SFTClienteDeleteFile extends AsyncTask<Void, Void, Void> {

    //String SFTPHOST = "mitouch.hopto.org";
    String SFTPHOST = "192.168.0.105";
    int SFTPPORT = 22;
    String SFTPUSER = "toor";
    String SFTPPASS = "namekiano";
    final String PATH_BASE_DE_DATOS = "/home/toor/galerias";
    String idEliminar;
    String extension;

    Session 	session 	= null;
    Channel 	channel 	= null;
    ChannelSftp channelSftp = null;

/*
* Parametros de entrada:
* id del archivo que quiero borrar
* extension del archivo a borrar
  */



    public SFTClienteDeleteFile(String idEliminar, String extension) {
        this.idEliminar = idEliminar;
        this.extension = extension;
    }

    @Override
    protected Void doInBackground(Void... params) {
        System.out.println("Archivo a borrar:" + idEliminar+"."+extension);
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
            channelSftp.rm(idEliminar+"."+extension);
        }catch(Exception ex){
            ex.printStackTrace();
        }
        return null;
    }
}
