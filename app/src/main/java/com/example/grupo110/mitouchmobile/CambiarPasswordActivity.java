package com.example.grupo110.mitouchmobile;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.sql.ResultSet;

public class CambiarPasswordActivity extends AppCompatActivity {

    private Button volverAMenuSinCambios;
    private Button volveraMenuCambiosOk;
    private EditText passwordVieja;
    private EditText passwordNueva;
    private EditText passwordNuevaRepite;
    private int id_usuario;
    private String password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cambiar_password);
        id_usuario = getIntent().getExtras().getInt("id");

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        passwordVieja =(EditText)findViewById(R.id.editTextPasswordVieja);
        passwordNueva =(EditText)findViewById(R.id.editTextPasswordNueva);
        passwordNuevaRepite =(EditText)findViewById(R.id.editTextPasswordNuevaRepite);
        volverAMenuSinCambios = (Button) findViewById(R.id.buttonCancelar);
        volveraMenuCambiosOk = (Button) findViewById(R.id.buttonAceptar);

        volveraMenuCambiosOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GrabarCambios();
            }
        });
        volverAMenuSinCambios.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void GrabarCambios() {
        boolean usuarioOk;

        usuarioOk = buscarUsuario();
        if(usuarioOk == true)
        {
            if(passwordNueva.getText().toString().equals(passwordNuevaRepite.getText().toString()) && !passwordNueva.getText().toString().equals("")) {
                ActualizarUsuario(passwordNueva.getText().toString());
                Toast toast2 = Toast.makeText(getApplicationContext(), "Contraseña Actualizada ", Toast.LENGTH_SHORT);
                toast2.show();
                finish();
            }
            else
            {
                passwordVieja.setText("");
                passwordNueva.setText("");
                passwordNuevaRepite.setText("");
                passwordVieja.clearFocus();
                passwordNueva.clearFocus();
                passwordNuevaRepite.clearFocus();
                Toast toast2 = Toast.makeText(getApplicationContext(), "Ambas contraseñas no coinciden ", Toast.LENGTH_SHORT);
                toast2.show();
            }

        }else
        {
            passwordVieja.setText("");
            passwordNueva.setText("");
            passwordNuevaRepite.setText("");
            passwordVieja.clearFocus();
            passwordNueva.clearFocus();
            passwordNuevaRepite.clearFocus();
            Toast toast2 = Toast.makeText(getApplicationContext(), "el usuario y la contraseña no coinciden ", Toast.LENGTH_SHORT);
            toast2.show();
        }
    }
    private boolean buscarUsuario() {
        password = passwordVieja.getText().toString();
        String comando;
        comando = String.format("SELECT * FROM  \"MiTouch\".t_usuarios WHERE usu_id =" + id_usuario +
                "AND usu_password = '" + password + "';");
        PostgrestBD baseDeDatos = new PostgrestBD();
        ResultSet resultSet = baseDeDatos.execute(comando);
        try {
            while (resultSet.next()) {
                System.out.println("base de datos actualizada");
                return true;
            }
        } catch (Exception e) {
        }
        return false;
    }

    private boolean ActualizarUsuario(String nuevaContraseña) {
        String comando;

        comando = String.format("UPDATE \"MiTouch\".t_usuarios SET usu_password = '"+nuevaContraseña+"' WHERE usu_id = " + id_usuario + ";");
        System.out.println(comando);

        PostgrestBD baseDeDatos = new PostgrestBD();
        ResultSet resultSet = baseDeDatos.execute(comando);
        /*try {
            if (resultSet.next()) {
                return true;
            }
        } catch (Exception e) {
        }*/
        return false;
    }

}