package com.example.grupo110.mitouchmobile.chat;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.DataSetObserver;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AbsListView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.grupo110.mitouchmobile.R;
import com.example.grupo110.mitouchmobile.base_de_datos.PostgrestBD;
import com.example.grupo110.mitouchmobile.galeria.AddDesdeGaleria;
import com.example.grupo110.mitouchmobile.galeria.DetailsActivity;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.InputStreamReader;
import java.sql.ResultSet;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

public class ChatActivity extends AppCompatActivity {

    private EditText textoAEnviar;
    private TextView textoPantalla;
    private int id_usuario_origen;
    private int id_usuario_destino;
    private String usuario=null;
    //private boolean control = false;
    private String usuario_origen;
    private String usuario_destino;
    private String texto="";
    final String PATH_MOBILE = "/storage/sdcard0/MiTouchMultimedia/chat";
    private boolean done=false;
    TimerTask tt;
    private Handler mHandler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        id_usuario_origen = getIntent().getExtras().getInt("id");
        id_usuario_destino = getIntent().getExtras().getInt("id2");
        usuario_origen = getIntent().getExtras().getString("idnombre");
        usuario_destino = getIntent().getExtras().getString("idnombre2");

        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(getResources().getDrawable(R.drawable.ic_previous));
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();

            }
        });


        textoAEnviar = (EditText) findViewById(R.id.EditText01);

        textoPantalla = new TextView(getApplicationContext());
        textoPantalla = (TextView) findViewById(R.id.textViewChat);
        textoPantalla.setMovementMethod(new ScrollingMovementMethod());

        try {
            System.out.println(PATH_MOBILE + "/" + id_usuario_destino + ".txt");
            LeerFichero(PATH_MOBILE + "/" + id_usuario_destino + ".txt");
        }catch (Exception e ){}

        textoPantalla.setText(texto);
        metodo();

        textoAEnviar.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                done = true;
                textoAEnviar.setText("");
               //  control=true;
                }
        });

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    if(validacion(textoAEnviar.getText().toString())) {
                        done = true;
                        Date d = new Date();
                        SimpleDateFormat formatoHora = new SimpleDateFormat("HH:mm:ss");
                        SimpleDateFormat formatoFecha = new SimpleDateFormat("dd/MM/yyyy");

                        texto = "[" + formatoFecha.format(d)+" " + formatoHora.format(d)+"] "+ usuario_origen + ": " +textoAEnviar.getText().toString();
                        String textoText = textoPantalla.getText().toString();

                        //textoPantalla.setTextSize(20);
                        if(texto.length()<250) {
                            textoPantalla.setText(textoText + "\n" + texto);
                            EscribirFichero(texto);
                            textoAEnviar.clearFocus();
                            actualizarBaseDeDatos();
                        }
                        else
                        {
                            Toast toast = Toast.makeText( getApplicationContext(),"solo se admite menos de 255 caracterres",Toast.LENGTH_LONG);
                            toast.show();
                        }

                        //  control = false;
                    }
                    InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(textoAEnviar.getWindowToken(), 0);
                    done=false;
                    iniciarTarea();
                }catch (Exception e ){System.out.println("el usuario se arrepintio");}
            }
        });
    }

    private void metodo() {
        done=false;
        iniciarTarea();
    }


    private void actualizarBaseDeDatos() {
        PostgrestBD baseDeDatos = new PostgrestBD();
        String comando;

        // Cuando creo un usuario primero voy a crear la carpeta de la galeria y del drive!
        comando = "INSERT INTO \"MiTouch\".t_mensajes ( m_id_usuario_emisor, m_id_usuario_receptor,m_mensaje) VALUES (" +id_usuario_origen+ "," + id_usuario_destino + ",'" + texto +"');";
        baseDeDatos.execute(comando);

    }

    private boolean validacion(String cadena) {

        for(int i = 0; i < cadena.length(); ++i) {
            char caracter = cadena.charAt(i);

            if(!Character.isSpaceChar(caracter)) {
                return true;
            }

        }
        textoAEnviar.setText("");
        return false;
    }



    public void EscribirFichero(String mensaje) {
        System.out.println(mensaje);
        CrearDirectorio();
        String ruta = PATH_MOBILE+"/"+id_usuario_destino+".txt";
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

    public void CrearDirectorio() {
        try {

            File mediaStorageDir = new File(Environment.getExternalStorageDirectory(), "MiTouchMultimedia/chat");

            if (!mediaStorageDir.exists()) {
                if (!mediaStorageDir.mkdirs()) {
                    Log.d("App", "failed to create directory");
                } else {
                    Log.d("App", "failed to create directory 2");
                }

            }
        } catch (Exception ex) {
            Log.e("Ficheros", "Error al escribir fichero a memoria interna");
        }
    }


    public void LeerFichero(String ruta) {
        texto="";
        CrearDirectorio();
        String cadena;
        try {
            FileReader f = new FileReader(ruta);
            BufferedReader b = new BufferedReader(f);
            while ((cadena = b.readLine()) != null) {
                texto +=cadena+"\n";
            }
            b.close();
        }catch (Exception e){System.out.println("Error leer fichero");}

        System.out.println(texto);
    }

    private void iniciarTarea() {
        try {
            tt = new TimerTask() {

                @Override
                public void run() {
                    while (!done) {
                        try {
                            LeerFichero(PATH_MOBILE + "/" + id_usuario_destino + ".txt");
                            System.out.println("el texto es: " + texto);
                            mHandler.postDelayed(hMyTimeTask, 1000);
                            Thread.sleep(5000);
                            mHandler.removeCallbacks(hMyTimeTask);
                        }catch (Exception e ){System.out.println("Algo fallo");}
                    }
                }
            };
            new Timer().schedule(tt, 500);
        }catch (Exception e){System.out.println("hlasdasd");}

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        done=true;
        finish();
    }

    @Override
    protected void onStop() {
        super.onStop();
        done=true;
        finish();
    }

    private Runnable hMyTimeTask = new Runnable() {
        public void run() {
            textoPantalla.setText(texto);
        }
    };
}
