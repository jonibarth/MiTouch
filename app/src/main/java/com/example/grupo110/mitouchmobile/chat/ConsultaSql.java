package com.example.grupo110.mitouchmobile.chat;

import android.app.NotificationManager;
import android.content.Context;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Environment;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.ArrayAdapter;

import com.example.grupo110.mitouchmobile.R;
import com.example.grupo110.mitouchmobile.base_de_datos.PostgrestBD;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.sql.ResultSet;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


/**
 * Created by Jonathan on 01/11/2016.
 */

public class ConsultaSql {


    private final Context context;
    private final int id_usuario;
    private int id_origen;
    private int aux=-1;
    String mensaje="";
    private String usuario;
    private String usuario_origen;
    final String PATH_MOBILE = "/storage/sdcard0/MiTouchMultimedia/chat";
    private List<String> idNotificados;

    public ConsultaSql(Context context, int id_usuario) {
        this.context = context;
        this.id_usuario = id_usuario;
        usuario = buscarUsuario(id_usuario);
        Haymensajes();
    }

    private void Haymensajes() {
        String comando;
        idNotificados = new ArrayList<>();
        comando = String.format("SELECT * FROM  \"MiTouch\".t_mensajes WHERE m_id_usuario_receptor ="+ id_usuario +";");

        Date d = new Date();
        SimpleDateFormat formatoHora = new SimpleDateFormat("HH:mm");
        SimpleDateFormat formatoFecha = new SimpleDateFormat("dd-MM");

        PostgrestBD baseDeDatos = new PostgrestBD();
        ResultSet resultSet = baseDeDatos.execute(comando);
        System.out.println(comando);
        try {
            while (resultSet.next()) {
                id_origen = resultSet.getInt("m_id_usuario_emisor");
                usuario_origen = buscarUsuario(id_origen);
                if (!buscarEnLista(id_origen)) {
                    f_enviar_notificacion("Mitouch", "nuevo mensaje de: " + usuario_origen);
                    aux = 1;
                }
                EscribirFichero(formatoFecha.format(d) + " " + formatoHora.format(d) + " " +usuario_origen+": "+ resultSet.getString("m_mensaje"));
                eliminarMensaje(resultSet.getInt("m_id"));
            }
        } catch (Exception e) {
            System.out.println("Error recibir notificaciones;");
        }
        System.out.println(mensaje);

    }

    private boolean buscarEnLista(int id_origen) {
        for(int i=0; i<idNotificados.size();i++) {
            System.out.println("holi es el id: " + idNotificados.get(i));
            if (idNotificados.get(i).equals(id_origen + ""))
                return true;
        }

        idNotificados.add(id_origen + "");
        return false;

    }

    private void eliminarMensaje(int id) {
        String comando = String.format("DELETE FROM  \"MiTouch\".t_mensajes WHERE m_id  ="+ id +";");
        PostgrestBD baseDeDatos = new PostgrestBD();
        baseDeDatos.execute(comando);
    }

    public void f_enviar_notificacion(String title, String text){
        int mId = 1;
        NotificationManager mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        Uri defaultSoundUri= RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context)
                .setSmallIcon(R.drawable.ic_chat_green)
                .setContentTitle(title)
                .setContentText(text)
                .setSound(defaultSoundUri);
        mNotificationManager.notify(mId, mBuilder.build());
        mNotificationManager.cancelAll();

    }

    private String buscarUsuario(int id_a_buscar) {
        String comando = "";
        comando = String.format("SELECT * FROM  \"MiTouch\".t_usuarios WHERE usu_id ="+ id_a_buscar +";");
        PostgrestBD baseDeDatos = new PostgrestBD();
        ResultSet resultSet = baseDeDatos.execute(comando);
        try{
            while (resultSet.next()) {
                return resultSet.getString("usu_nombre_usuario");
            }
        }catch(Exception e){System.out.println("Error busqueda");}
        return null;
    }


    public void EscribirFichero(String mensaje) {
        System.out.println(mensaje);
        CrearDirectorio();
        String ruta = PATH_MOBILE+"/"+id_origen+".txt";
        File archivo = new File(ruta);
        BufferedWriter bw;
        FileWriter TextOut;
        try {
            if (archivo.exists()) {
                TextOut = new FileWriter(archivo, true);
                TextOut.write(mensaje+"\r\n");
                TextOut.close();
            } else {

                bw = new BufferedWriter(new FileWriter(archivo));
                bw.write(mensaje+"\r\n");
                bw.close();
            }


        }catch (Exception e){System.out.println("Error grabar archivo");}
    }

    public void CrearDirectorio(){
        System.out.println("Cree el directorio!");
        try
        {

            File mediaStorageDir = new File(Environment.getExternalStorageDirectory(), "MiTouchMultimedia/chat");

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
