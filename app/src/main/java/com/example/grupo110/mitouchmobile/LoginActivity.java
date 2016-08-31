package com.example.grupo110.mitouchmobile;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.preference.PreferenceScreen;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.sql.ResultSet;

public class LoginActivity extends AppCompatActivity {

    Button pasarAMenu;
    int id_usuario;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);


        pasarAMenu = (Button) findViewById(R.id.email_sign_in_button);
        pasarAMenu.setOnClickListener(new View.OnClickListener() {
            @Override

            // Un harcode: si usuario: admin y password: admin entro al menu
            public void onClick(View v) {
                EditText email = (EditText) findViewById(R.id.email);
                EditText password = (EditText) findViewById(R.id.password);
                if (login(email.getText().toString(), password.getText().toString()) == 1)
                //if (1 == 1)
                    IniciarPantalla();
                else {
                    ErrorLogueo(email.getText().toString(), password.getText().toString());
                    email.setText("");
                    password.setText("");
                    // Pierdo el foco!
                    password.clearFocus();
                    email.clearFocus();
                }
            }
        });
// Cuando apretas sobre el texto " me olvide la contraseña"
        TextView pasarAForgotPassword;
        pasarAForgotPassword = (TextView) findViewById(R.id.textViewForgotYourPassword);
        pasarAForgotPassword.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                EditText email = (EditText) findViewById(R.id.email);
                Intent siguiente = new Intent(LoginActivity.this, ForgotPasswordActivity.class);
                startActivity(siguiente);
                finish();
            }
        });

        // Cuando apretas sobre el texto " Registrarse"
        TextView PasaraRegistrarse;
        PasaraRegistrarse = (TextView) findViewById(R.id.textViewRegistrarse);
        PasaraRegistrarse.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent siguiente = new Intent(LoginActivity.this, RegistrarseActivity.class);
                startActivity(siguiente);
                finish();
            }
        });

    }

    // Si el usuario y pass son validas!
    public void IniciarPantalla() {
        Intent siguiente = new Intent(LoginActivity.this, MainMenuActivity.class);
        siguiente.putExtra("id",id_usuario);
        startActivity(siguiente);
        finish();
    }

    // Si el usuario y pass son INvalidas!
    private void ErrorLogueo(String email, String password) {
        Toast toast2 = Toast.makeText(getApplicationContext(), "Usuario y contraseña invalidos... Ingrese nuevamente ", Toast.LENGTH_SHORT);
        toast2.show();
    }

    public int login(String usuario, String password) {
        String comando = "";

        comando = String.format("SELECT * FROM  \"MiTouch\".t_usuarios WHERE usu_nombre_usuario ='"+ usuario +"' " +
                "AND usu_password = '"+ password +"';");
        PostgrestBD baseDeDatos = new PostgrestBD();
        ResultSet resultSet = baseDeDatos.execute(comando);
        try{
            while (resultSet.next()) {
                id_usuario = resultSet.getInt("usu_id");
                return 1;
            }
        }catch(Exception e){}
        return 0;
    }
}
