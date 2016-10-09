package com.example.grupo110.mitouchmobile.comunicacion.servidor;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.widget.Toast;

import com.example.grupo110.mitouchmobile.CompartirActivity;
import com.example.grupo110.mitouchmobile.GalleryPage;
import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;

import java.io.File;
import java.io.FileInputStream;

/**
 * Created by Jonathan on 08/10/2016.
 */

public class SFTClienteUploadNoTengoArchivoLocal extends AsyncTask<Void, Void, Void> {

    String SFTPHOST = "mitouch.hopto.org";
    int SFTPPORT = 22;
    String SFTPUSER = "toor";
    String SFTPPASS = "namekiano";
    final String PATH_BASE_DE_DATOS = "/home/toor/galerias";
    final String PATH_MOBILE = "/storage/sdcard0/MiTouchMultimedia";
    int id_usuario;
    String archivoOriginal;
    String nombre_carpeta_origen;
    String nombre_carpeta_destino;

    Session session 	= null;
    Channel channel 	= null;
    ChannelSftp channelSftp = null;

    ProgressDialog progress;
    CompartirActivity compartirActivity;
    GalleryPage galleryPage;
    Context context;




    public SFTClienteUploadNoTengoArchivoLocal(ProgressDialog progress, CompartirActivity compartirActivity, String nombre_carpeta_destino, String archivoOriginal, String nombre_carpeta_origen, Context applicationContext) {
        this.progress = progress;
        this.compartirActivity = compartirActivity;
        this.nombre_carpeta_origen = nombre_carpeta_origen;
        this.archivoOriginal = archivoOriginal;
        this.nombre_carpeta_destino = nombre_carpeta_destino;
        this.context = applicationContext;
        System.out.println("nombre_carpeta origen" +" " + nombre_carpeta_origen);
        System.out.println("archivoOriginal" +" " + archivoOriginal);
        System.out.println("nombre_carpeta destino" +" " + nombre_carpeta_destino);
    }


    @Override
    public void onPreExecute() {
        progress.show();
    }
    @Override
    public void onPostExecute(Void unused) {
        progress.dismiss();
        Toast toast = Toast.makeText(context,"El archivo fue compartido con exito..",Toast.LENGTH_LONG);
        toast.show();
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
            channelSftp.cd(PATH_BASE_DE_DATOS+"/"+ nombre_carpeta_destino);
            File f = new File(PATH_BASE_DE_DATOS+"/"+ nombre_carpeta_origen+"/"+archivoOriginal);
            channelSftp.put(new FileInputStream(f), f.getName());
        }catch(Exception ex){
            System.out.println("Error: " + ex);
            ex.printStackTrace();
        }
        return null;
    }

}
