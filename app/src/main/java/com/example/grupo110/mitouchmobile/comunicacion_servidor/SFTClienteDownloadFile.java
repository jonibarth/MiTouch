package com.example.grupo110.mitouchmobile.comunicacion_servidor;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

import com.example.grupo110.mitouchmobile.galeria.CompartirActivity;
import com.example.grupo110.mitouchmobile.galeria.DetailsActivity;
import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;

/**
 * Created by Jonathan on 08/10/2016.
 */

public class SFTClienteDownloadFile extends AsyncTask<Void, Void, Void> {



    //String SFTPHOST = "mitouch.hopto.org";
    String SFTPHOST = "192.168.2.104";
    int SFTPPORT = 22;
    String SFTPUSER = "toor";
    String SFTPPASS = "namekiano";
    final String PATH_BASE_DE_DATOS = "/home/toor/galerias";
    final String PATH_MOBILE = "/storage/sdcard0/MiTouchMultimedia";

    String archivoOriginal;
    String nombre_carpeta;
    private String nombreArchivoAbrir;
    private String idArchivoAbrir;

    ProgressDialog progress;
    DetailsActivity detailsActivity;

    Context context;

    Session 	session 	= null;
    Channel 	channel 	= null;
    ChannelSftp channelSftp = null;

/*
* Parametros de entrada:
* progress: ProgressDialog que se creo para que el usuario espere a que el archivo se descargue
* detailsActivity: la actividad que me envio aca
* nombre_carpeta: carpeta donde se encuentra el archivo a descargar, puede ser la personal o una compartida
* nombreArchivoAbrir: archivo que quiero descargar
* idArchivoAbrir: el id del archivo a descargar
* applicationContext
  */
    public SFTClienteDownloadFile(ProgressDialog progress, DetailsActivity detailsActivity, String nombre_carpeta, String nombreArchivoAbrir, String idArchivoAbrir, Context applicationContext) {
        this.progress = progress;
        this.detailsActivity = detailsActivity;
        this.nombre_carpeta = nombre_carpeta;
        this.nombreArchivoAbrir = nombreArchivoAbrir;
        this.idArchivoAbrir = idArchivoAbrir;
        this.context = applicationContext;
    }


    @Override
    public void onPreExecute() {
        progress.show();
    }
    @Override
    public void onPostExecute(Void unused) {
        progress.dismiss();
        Toast toast = Toast.makeText(context,"Ahora ya puede abrir el archivo",Toast.LENGTH_LONG);
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
            channelSftp.cd(PATH_BASE_DE_DATOS);
            byte[] buffer = new byte[4096];
            System.out.println("El archivo a abrir: " + nombreArchivoAbrir);
            System.out.println("El archivo a abrir: " + idArchivoAbrir+"."+nombreArchivoAbrir.substring(nombreArchivoAbrir.lastIndexOf(".") + 1));

            BufferedInputStream bis = new BufferedInputStream(channelSftp.get(idArchivoAbrir+"."+nombreArchivoAbrir.substring(nombreArchivoAbrir.lastIndexOf(".") + 1)));
            CrearDirectorio();
            File newFile = new File(PATH_MOBILE+"/"+nombre_carpeta +"/"+nombreArchivoAbrir);
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
    /*
   * Metodo utilizado para crear directorio en la carpeta mobile en caso de que no exista.
   * El direcorio es la carpeta general que contendra subcarpeta con el usuario logueado y carpetas compartida
    */
    public void CrearDirectorio(){
        System.out.println("Cree el directorio!");
        try
        {

            File mediaStorageDir = new File(Environment.getExternalStorageDirectory(), "MiTouchMultimedia/"+nombre_carpeta);

            if (!mediaStorageDir.exists()) {
                if (!mediaStorageDir.mkdirs()) {
                    Log.d("App", "failed to create directory");
                }else{Log.d("App", "failed to create directory 2");}

            }
        }
        catch (Exception ex)
        {
            Log.e("Ficheros", "Error al escribir fichero a memoria interna");
        }
    }
}
