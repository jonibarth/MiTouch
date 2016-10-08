package com.example.grupo110.mitouchmobile;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.OutputStream;

/**
 * Created by Jonathan on 08/10/2016.
 */

public class SFTClienteDownloadFile extends AsyncTask<Void, Void, Void> {



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
    ProgressDialog progress;
    DetailsActivity detailsActivity;
    Context context;

    Session 	session 	= null;
    Channel 	channel 	= null;
    ChannelSftp channelSftp = null;


    public SFTClienteDownloadFile(ProgressDialog progress, DetailsActivity detailsActivity, String nombre_carpeta, String auxiliar, Context applicationContext) {
        this.progress = progress;
        this.detailsActivity = detailsActivity;
        this.nombre_carpeta = nombre_carpeta;
        this.archivoOriginal = auxiliar;
        this.context = applicationContext;
    }


    public void onPreExecute() {
        progress.show();
    }

    public void onPostExecute(Void unused) {
        progress.dismiss();
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
            channelSftp.cd(PATH_BASE_DE_DATOS+"/"+ archivoOriginal );
            byte[] buffer = new byte[1024];
            BufferedInputStream bis = new BufferedInputStream(channelSftp.get(archivoOriginal));
            File newFile = new File("C:/Test.java");
            OutputStream os = new FileOutputStream(newFile);
            BufferedOutputStream bos = new BufferedOutputStream(os);
            int readCount;
            while( (readCount = bis.read(buffer)) > 0) {
                System.out.println("Writing: " );
                bos.write(buffer, 0, readCount);
            }
            bis.close();
            bos.close();
        }catch(Exception ex){
            ex.printStackTrace();
        }
        return null;
    }
}
