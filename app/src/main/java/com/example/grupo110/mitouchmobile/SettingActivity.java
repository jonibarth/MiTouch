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

public class SettingActivity extends AppCompatActivity {

  //  Button volverAMenuSinCambios;
    Button volveraMenuCambiosOk;
    EditText editTextNombre;
    EditText editTextApellido;
    EditText editTextEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
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
        editTextNombre =(EditText)findViewById(R.id.editTextNombre);
        editTextApellido =(EditText)findViewById(R.id.editTextApellido);
        editTextEmail =(EditText)findViewById(R.id.editTextEmail);

        // Fondo Gris a las cosas que no se pueden editar
        //editTextNombre.setBackgroundColor(Color.rgb(216,223,234));
        //editTextApellido.setBackgroundColor(Color.rgb(216,223,234));
      //  editTextEmail.setBackgroundColor(Color.rgb(216,223,234));

        // No permitir edición
        editTextNombre.setKeyListener(null);
        editTextApellido.setKeyListener(null);
        editTextEmail.setKeyListener(null);


        // Buscar en base de datos datos del usuario:
        editTextNombre.setText("Juan ");
        editTextApellido.setText("Perez ");
        editTextEmail.setText("juanchi_perez@gmail.com ");

    }
}
