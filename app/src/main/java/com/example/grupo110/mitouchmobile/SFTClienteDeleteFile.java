package com.example.grupo110.mitouchmobile;

import android.os.AsyncTask;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;


/**
 * Created by Jonathan on 08/10/2016.
 */
public class SFTClienteDeleteFile extends AsyncTask<Void, Void, Void> {

    String SFTPHOST = "mitouch.hopto.org";
    int SFTPPORT = 22;
    String SFTPUSER = "toor";
    String SFTPPASS = "namekiano";
    final String PATH_BASE_DE_DATOS = "/home/toor/galerias";
    final String PATH_MOBILE = "/storage/sdcard0/MiTouchMultimedia";
    int id_usuario;
    int id_carpeta;
    String archivoOriginal;
    String nombre_carpeta;


    public SFTClienteDeleteFile(String nombre_carpeta, String nombreArchivo) {
        this.nombre_carpeta = nombre_carpeta;
        this.archivoOriginal = nombreArchivo;
    }

    @Override
    protected Void doInBackground(Void... params) {
        Session session = null;
        Channel channel = null;
        ChannelSftp channelSftp = null;
        System.out.println("preparing the host information for sftp.");
        try {
            JSch jsch = new JSch();
            session = jsch.getSession(SFTPUSER, SFTPHOST, SFTPPORT);
            session.setPassword(SFTPPASS);
            java.util.Properties config = new java.util.Properties();
            config.put("StrictHostKeyChecking", "no");
            session.setConfig(config);
            session.connect();
            System.out.println("Host connected.");
            channel = session.openChannel("sftp");
            channel.connect();
            System.out.println("sftp channel opened and connected.");
            channelSftp = (ChannelSftp) channel;
            channelSftp.cd(PATH_BASE_DE_DATOS+"/"+nombre_carpeta);
            channelSftp.rm(archivoOriginal);
        } catch (Exception ex) {
            System.out.println("Exception found while tranfer the response.");
        }
        finally{

            channelSftp.exit();
            System.out.println("sftp Channel exited.");
            channel.disconnect();
            System.out.println("Channel disconnected.");
            session.disconnect();
            System.out.println("Host Session disconnected.");
        }
        return null;
    }
}
