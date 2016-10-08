package com.example.grupo110.mitouchmobile;

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

public class SFTClienteUploadFile extends AsyncTask<Void, Void, Void> {

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

    Session 	session 	= null;
    Channel 	channel 	= null;
    ChannelSftp channelSftp = null;


    public SFTClienteUploadFile(String nombre_carpeta, String imgDecodableString) {
        this.nombre_carpeta=nombre_carpeta;
        this.archivoOriginal=imgDecodableString;
        System.out.println(nombre_carpeta +" " + archivoOriginal);
    }


    @Override
    protected Void doInBackground(Void... params) {
        try{
            JSch jsch = new JSch();
            session = jsch.getSession(SFTPUSER,SFTPHOST,SFTPPORT);
            session.setPassword(SFTPPASS);
            session.connect();
            channel = session.openChannel("sftp");
            channel.connect();
            channelSftp = (ChannelSftp)channel;
            channelSftp.cd(PATH_BASE_DE_DATOS+"/"+ archivoOriginal);
            File f = new File(archivoOriginal);
            channelSftp.put(new FileInputStream(f), f.getName());
        }catch(Exception ex){
            ex.printStackTrace();
        }
    return null;
    }

}
