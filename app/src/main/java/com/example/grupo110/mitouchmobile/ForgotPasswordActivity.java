package com.example.grupo110.mitouchmobile;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class ForgotPasswordActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        Button pasarALogin;
        pasarALogin =(Button)findViewById(R.id.button);
        pasarALogin.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Toast toast= Toast.makeText (getApplicationContext(), "puto! te olvidaste la contraseña!!" , Toast.LENGTH_SHORT);
                toast.show();

                EditText email = (EditText) findViewById(R.id.email);
                Intent siguiente = new Intent(ForgotPasswordActivity.this, LoginActivity.class);
                startActivity(siguiente);
                finish();
            }
        });




    }

}
