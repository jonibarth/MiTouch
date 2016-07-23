package com.example.grupo110.mitouchmobile;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class LoginActivity extends AppCompatActivity {

    Button pasarAMenu;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        pasarAMenu =(Button)findViewById(R.id.email_sign_in_button);
        pasarAMenu.setOnClickListener(new View.OnClickListener() {
            @Override

 // Un harcode: si usuario: admin y password: admin entro al menu
            public void onClick(View v) {
                EditText email = (EditText) findViewById(R.id.email);
                EditText password = (EditText) findViewById(R.id.password);
                String emailHarcode = "admin";
                String passwordHarcode = "admin";
                if(email.getText().toString().equals(emailHarcode)&&password.getText().toString().equals(passwordHarcode))
                    IniciarPantalla();
                else {
                    ErrorLogueo(email.getText().toString(), password.getText().toString());
                    email.setText("");
                    password.setText("");
                    }
            }
        });
// Cuando apretas sobre el texto " me olvide la contraseña"
        TextView pasarAForgotPassword;
        pasarAForgotPassword =(TextView)findViewById(R.id.textViewForgotYourPassword);
        pasarAForgotPassword.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                EditText email = (EditText) findViewById(R.id.email);
                Intent siguiente = new Intent(LoginActivity.this, ForgotPasswordActivity.class);
                startActivity(siguiente);
                finish();
            }
        });


    }

    // Si el usuario y pass son validas!
    public void IniciarPantalla()
    {
        Intent siguiente = new Intent(LoginActivity.this, MenuActivity.class);
        startActivity(siguiente);
        finish();
    }

    // Si el usuario y pass son INvalidas!
    private void ErrorLogueo(String email, String password) {
        Toast toast= Toast.makeText (getApplicationContext(), "email ingresado: " + email +" password ingresada: " + password , Toast.LENGTH_SHORT);
        toast.show();
        Toast toast2= Toast.makeText (getApplicationContext(),"ingresar admin admin", Toast.LENGTH_SHORT);
        toast2.show();
    }
}
