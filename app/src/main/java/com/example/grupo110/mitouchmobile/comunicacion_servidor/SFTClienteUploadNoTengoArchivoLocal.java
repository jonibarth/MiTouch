package com.example.grupo110.mitouchmobile.comunicacion_servidor;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;

import com.example.grupo110.mitouchmobile.galeria.CompartirActivity;
import com.example.grupo110.mitouchmobile.galeria.AddDesdeGaleria;
import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Created by Jonathan on 08/10/2016.
 */

public class SFTClienteUploadNoTengoArchivoLocal extends AsyncTask<Void, Void, Void> {

    private String SFTPHOST = "mitouch.hopto.org";
    private int SFTPPORT = 22;
    private String SFTPUSER = "toor";
    private String SFTPPASS = "namekiano";
    private final String PATH_BASE_DE_DATOS = "/home/toor/galerias";
    private final String PATH_MOBILE = "/storage/sdcard0/MiTouchMultimedia";
    private int id_usuario;
    private String archivoOriginal;
    private String nombre_carpeta_origen;
    private String nombre_carpeta_destino;

    Session sessionRead 	= null;
    Channel channelRead 	= null;
    ChannelSftp channelSftpRead = null;

    Session sessionWrite 	= null;
    Channel channelWrite 	= null;
    ChannelSftp channelSftpWrite = null;

    ProgressDialog progress;
    CompartirActivity compartirActivity;
    AddDesdeGaleria addDesdeGaleria;
    Context context;

/*
* nombre_carpeta_destino = es el grupo de usuario a donde le voy a compartir la carpeta
* archivoOriginal = es el nombre del archivo a compartir
* nombre_carpeta_origen = es la carpeta donde se encuentra el archivo a compartir
 */


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

/*
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
*/


    // no se si va a funcionar.. http://stackoverflow.com/questions/32742066/using-sftp-in-java-how-do-i-transfer-a-file-from-one-folder-to-another
    @Override
    protected Void doInBackground(Void... params) {
        try{
            JSch jsch = new JSch();
            java.util.Properties config = new java.util.Properties();
            config.put("StrictHostKeyChecking", "no");




            sessionRead = jsch.getSession(SFTPUSER,SFTPHOST,SFTPPORT);
            sessionRead.setPassword(SFTPPASS);
            sessionRead.setConfig(config);
            sessionRead.connect();

            cp(sessionRead);
/*
            sessionWrite = jsch.getSession(SFTPUSER,SFTPHOST,SFTPPORT);
            sessionWrite.setPassword(SFTPPASS);
            sessionWrite.setConfig(config);
            sessionWrite.connect();

            channelRead = sessionRead.openChannel("sftp");
            channelRead.connect();
            channelSftpRead = (ChannelSftp)channelRead;

            channelWrite = sessionWrite.openChannel("sftp");
            channelWrite.connect();
            channelSftpWrite = (ChannelSftp)channelWrite;


            PipedInputStream pin = new PipedInputStream(2048);
            PipedOutputStream pout = new PipedOutputStream(pin);


            System.out.println("leo de:" +PATH_BASE_DE_DATOS+"/"+nombre_carpeta_origen+"/"+archivoOriginal);

            System.out.println("Escribo en:" +PATH_BASE_DE_DATOS+"/"+nombre_carpeta_destino+"/"+archivoOriginal);

            channelSftpRead.get(PATH_BASE_DE_DATOS+"/"+nombre_carpeta_origen+"/"+archivoOriginal, pout);
            channelSftpWrite.put(pin, PATH_BASE_DE_DATOS+"/"+nombre_carpeta_destino+"/"+archivoOriginal);
*/




/*
            channelRead.disconnect();
            channelWrite.disconnect();
*/
            sessionRead.disconnect();
            //sessionWrite.disconnect();

        }catch(Exception ex){
            System.out.println("Error: " + ex);
            ex.printStackTrace();
        }
        System.out.println("FIN");
        return null;
    }






    public void cp (Session session) throws Exception {
        if (!session.isConnected()) {
            System.out.println ("Session is not connected");
            throw new Exception("Session is not connected...");
        }
        Channel upChannel = null;
        Channel downChannel = null;
        ChannelSftp uploadChannel = null;
        ChannelSftp downloadChannel = null;
        try {
            upChannel = session.openChannel("sftp");
            downChannel = session.openChannel("sftp");
            upChannel.connect();
            downChannel.connect();
            uploadChannel = (ChannelSftp) upChannel;
            downloadChannel = (ChannelSftp) downChannel;

            // Primero voy a crear el directorio donde se alojara el archivo
            try{
                uploadChannel.cd(PATH_BASE_DE_DATOS+"/"+ nombre_carpeta_destino);}
            catch (Exception e){
                uploadChannel.mkdir(PATH_BASE_DE_DATOS+"/"+ nombre_carpeta_destino);
                uploadChannel.cd(PATH_BASE_DE_DATOS+"/"+ nombre_carpeta_destino);
            }
            System.out.println("el nombre del archivo origen es: " + PATH_BASE_DE_DATOS+"/"+nombre_carpeta_origen +"/"+archivoOriginal);
            File newFile = new File(PATH_BASE_DE_DATOS+"/"+nombre_carpeta_origen +"/"+archivoOriginal);

            System.out.println("el nombre del archivo es: " + newFile.getName());

            uploadChannel.put(new FileInputStream(newFile),  newFile.getName());
            /*
            InputStream inputStream = uploadChannel.get(PATH_BASE_DE_DATOS+"/"+nombre_carpeta_origen+"/"+archivoOriginal);
            downloadChannel.put(inputStream, PATH_BASE_DE_DATOS+"/"+nombre_carpeta_destino+"/"+archivoOriginal);
            */
        } catch (JSchException e) {
            System.out.println("Auth failure");
            throw new Exception(e);
        } finally {
            if (upChannel == null || downChannel == null) {
                System.out.println("Channel is null ...");
            }else if (uploadChannel != null && !uploadChannel.isClosed()){
                uploadChannel.exit();
                downloadChannel.exit();
                uploadChannel.disconnect();
                downloadChannel.disconnect();
            }else if (!upChannel.isClosed()) {
                upChannel.disconnect();
                downChannel.disconnect();
            }
            session.disconnect();
        }
    }





}
