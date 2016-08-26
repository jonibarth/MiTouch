package com.example.grupo110.mitouchmobile;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import java.sql.ResultSet;
import java.util.Date;

public class SettingActivity extends AppCompatActivity {

  //  Button volverAMenuSinCambios;
    Button volveraMenuCambiosOk;
    EditText editTextUsuario;
    EditText editTextNombreCompleto;
    EditText editTextContraseña;

    String usu_nombre_usuario;
    String usu_nombre_completo;
    String usu_contraseña;
    int id_usuario;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        id_usuario = getIntent().getExtras().getInt("id");
        LlenarCampos();

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PantallaMenuSinCambios();
            }
        });

        volveraMenuCambiosOk =(Button)findViewById(R.id.buttonAceptarCambios);
        volveraMenuCambiosOk.setOnClickListener(new View.OnClickListener() {
            @Override

            public void onClick(View v) {
                PantallaMenuConCambios();
            }
        });

    }

    public void PantallaMenuSinCambios()
    {
        finish();
    }

    public void PantallaMenuConCambios()
    {
        finish();
    }

    public void LlenarCampos()
    {
        buscarDatos();
        editTextUsuario =(EditText)findViewById(R.id.editTextUsuario);
        editTextNombreCompleto =(EditText)findViewById(R.id.editTextNombreCompleto);
        editTextContraseña =(EditText)findViewById(R.id.editTextContraseña);

        // Fondo Gris a las cosas que no se pueden editar
        //editTextNombre.setBackgroundColor(Color.rgb(216,223,234));
        //editTextApellido.setBackgroundColor(Color.rgb(216,223,234));
      //  editTextEmail.setBackgroundColor(Color.rgb(216,223,234));

        // No permitir edición
        editTextUsuario.setKeyListener(null);
        editTextNombreCompleto.setKeyListener(null);
        editTextContraseña.setKeyListener(null);


        // Buscar en base de datos datos del usuario:
        editTextUsuario.setText(usu_nombre_usuario);
        editTextNombreCompleto.setText(usu_nombre_completo);
        editTextContraseña.setText(usu_contraseña);

    }

    public int buscarDatos() {
        String comando;

        comando = String.format("SELECT * FROM  \"MiTouch\".t_usuarios WHERE usu_id ="+ id_usuario +";");

        PostgrestBD baseDeDatos = new PostgrestBD();
        ResultSet resultSet = baseDeDatos.execute(comando);
        try{
            while (resultSet.next()) {
                usu_nombre_usuario = resultSet.getString("usu_nombre_usuario");
                usu_nombre_completo = resultSet.getString("usu_nombre_completo");
                usu_contraseña = resultSet.getString("usu_password");
            }
        }catch(Exception e){}
        return 0;
    }


}
