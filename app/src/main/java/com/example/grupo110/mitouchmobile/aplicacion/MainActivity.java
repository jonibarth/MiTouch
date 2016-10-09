package com.example.grupo110.mitouchmobile.aplicacion;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.Window;
import android.widget.ImageView;
import android.widget.VideoView;

import com.example.grupo110.mitouchmobile.R;
import com.example.grupo110.mitouchmobile.base_de_datos.PostgrestBD;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.sql.ResultSet;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends Activity {

    // Set the duration of the splash screen
    private static final long SPLASH_SCREEN_DELAY = 5000;
    private VideoView videoView;
    private ImageView imageView;
    int id_usuario=-1;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Set portrait orientation
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        // Hide title bar
        requestWindowFeature(Window.FEATURE_NO_TITLE);

        setContentView(R.layout.activity_welcome);
        try {
            videoView = (VideoView) findViewById(R.id.VideoView);
            Uri path = Uri.parse("android.resource://com.example.grupo110.mitouchmobile/"
            + R.raw.video1);
            videoView.setVideoURI(path);
            videoView.start();
        }catch (Exception e){
            String uri = "@drawable/splash_screen";  // where myresource (without the extension) is the file
            int imageResource = getResources().getIdentifier(uri, null, getPackageName());
            ImageView imagen= (ImageView)findViewById(R.id.ImageView);
            Drawable res = getResources().getDrawable(imageResource);
            imagen.setImageDrawable(res);
        }


        TimerTask task = new TimerTask() {
            @Override
            public void run() {

                // Start the next activity

                // Si en archivo id_usuario que esta en raw esta en nulo voy al login
                // si el archivo id_usuario que esta en el raw hay un numero, significa que hay un usuario conectado!
                // Busco que ese usuario exista, sino que entre en el login
                String texto = "null";
                String[] archivos = fileList();

                if (existe(archivos, "notas.txt"))
                    try {
                        InputStreamReader archivo = new InputStreamReader(
                                openFileInput("notas.txt"));
                        BufferedReader br = new BufferedReader(archivo);
                        String linea = br.readLine();
                        String todo = "";
                        todo =linea;
                        br.close();
                        archivo.close();
                        texto =todo;
                    } catch (Exception e) {
                        System.out.println("error en el try que esta dentro del if");
                    }
                else{
                    Intent mainIntent = new Intent().setClass(
                            MainActivity.this, LoginActivity.class);
                    startActivity(mainIntent);
                    finish();
                }

                texto.trim();
                if(!texto.equals("null")) {
                    id_usuario = Integer.parseInt(texto);
                    if (buscarUsuario()) {
                        IniciarPantalla();
                    }
                }
                else
                {
                    Intent mainIntent = new Intent().setClass(MainActivity.this, LoginActivity.class);
                    startActivity(mainIntent);
                    finish();
                }
            }
        };

        // Simulate a long loading process on application startup.
        Timer timer = new Timer();
        timer.schedule(task, SPLASH_SCREEN_DELAY);
    }

    private Boolean buscarUsuario() {
        String comando = "";
        comando = String.format("SELECT * FROM  \"MiTouch\".t_usuarios WHERE usu_id ="+ id_usuario +";");
        PostgrestBD baseDeDatos = new PostgrestBD();
        ResultSet resultSet = baseDeDatos.execute(comando);
        try{
            while (resultSet.next()) {
                System.out.println("usuario: " + resultSet.getInt("usu_id"));
                return true;
            }
        }catch(Exception e){System.out.println("Error busqueda");}
        return false;
    }

    public void IniciarPantalla() {
        Intent siguiente = new Intent(MainActivity.this, MainMenuActivity.class);
        siguiente.putExtra("id",id_usuario);
        startActivity(siguiente);
        finish();
    }
    private boolean existe(String[] archivos, String archbusca) {
        for (int f = 0; f < archivos.length; f++)
            if (archbusca.equals(archivos[f]))
                return true;
        return false;
    }
}