package com.example.grupo110.mitouchmobile;


import android.app.Activity;
import android.content.Intent;
import android.database.DataSetObserver;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import java.sql.ResultSet;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ChatActivity extends AppCompatActivity {

    private Button botonChat;
    private EditText textoAEnviar;
    private TextView textoPantalla;
    private int id_usuario;
    private String usuario=null;
    private boolean control = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        id_usuario = getIntent().getExtras().getInt("id");
        buscarBaseDeDatosUsuario();

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(getResources().getDrawable(R.drawable.ic_previous));
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();

            }
        });
        botonChat = (Button) findViewById(R.id.chatButton);
        textoAEnviar = (EditText) findViewById(R.id.EditText01);
        textoPantalla = (TextView) findViewById(R.id.textViewChat);


        textoAEnviar.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                textoAEnviar.setText("");
                 control=true;
                }
        });

        botonChat.setOnClickListener(new View.OnClickListener() {
            @Override

            public void onClick(View v) {
                if(validacion()) {

                    String texto = textoAEnviar.getText().toString();
                    String textoText = textoPantalla.getText().toString();
                    Date d = new Date();
                    SimpleDateFormat formatoHora = new SimpleDateFormat("HH:mm");
                    SimpleDateFormat formatoFecha = new SimpleDateFormat("dd-MM");
                    textoPantalla.setTextSize(20);
                    textoPantalla.setText(textoText + "\n" + formatoFecha.format(d)+" " + formatoHora.format(d) + " " +usuario + ": " + texto);
                    textoAEnviar.clearFocus();
                    control = false;
                }
                }
        });
    }

    private boolean validacion() {
        // Validar que el mensaje no contenga espacios tabs etc!
        if(control )
            return true;
        return false;
    }
    private boolean buscarBaseDeDatosUsuario() {
        String comando;
        comando = String.format("SELECT * FROM  \"MiTouch\".t_usuarios WHERE usu_id =" + id_usuario +";");
        PostgrestBD baseDeDatos = new PostgrestBD();
        ResultSet resultSet = baseDeDatos.execute(comando);
        try {
            while (resultSet.next()) {
                usuario = resultSet.getString("usu_nombre_usuario");
            }
        } catch (Exception e) {
        }
        return false;
    }


}
