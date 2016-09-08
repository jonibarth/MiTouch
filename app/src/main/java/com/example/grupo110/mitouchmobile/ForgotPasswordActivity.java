package com.example.grupo110.mitouchmobile;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.sql.ResultSet;

public class ForgotPasswordActivity extends AppCompatActivity {
    private String nombreAdministrador = null;
    private String emailAdministrador = null;
    TextView texto;
    Button pasarALogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);
        validarUsuario();

        pasarALogin =(Button)findViewById(R.id.button);
        texto = (TextView)findViewById(R.id.textViewForgotPassword);
        texto.setText("Enviale un mail al administrador\nNombre: " + nombreAdministrador +"\nemail: " + emailAdministrador);
        texto.setTextSize(20);

        pasarALogin.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                Intent siguiente = new Intent(ForgotPasswordActivity.this, LoginActivity.class);
                startActivity(siguiente);
                finish();
            }
        });
    }

    private boolean validarUsuario() {
        String comando;
        comando = String.format( "SELECT * FROM  \"MiTouch\".t_usuarios WHERE usu_administrador=true;");
        PostgrestBD baseDeDatos = new PostgrestBD();
        ResultSet resultSet = baseDeDatos.execute(comando);
        try {
            while (resultSet.next()) {
                nombreAdministrador = resultSet.getString("usu_nombre_completo");
                emailAdministrador = resultSet.getString("usu_mail");
            }
        } catch (Exception e) {
            System.err.println("Error busqueda usuario en Registrar");
        }
        return false;
    }

}
