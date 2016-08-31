package com.example.grupo110.mitouchmobile;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.sql.ResultSet;

public class RegistrarseActivity extends AppCompatActivity {
    private EditText EtxtNombreUsuario;
    private EditText EtxtNombreCompleto;
    private EditText EtxtGmail;
    private EditText EtxtPassword;
    private EditText EtxtRepiteContraseñaRegistro;
    private Button ButtonCancelar;
    private Button ButtonRegistrar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registrarse);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Button pasarALogin;
        pasarALogin =(Button)findViewById(R.id.button);
        pasarALogin.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                EditText email = (EditText) findViewById(R.id.email);
                Intent siguiente = new Intent(RegistrarseActivity.this, LoginActivity.class);
                startActivity(siguiente);
                finish();
            }
        });

    }
}
