package com.example.grupo110.mitouchmobile.aplicacion;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.grupo110.mitouchmobile.R;
import com.example.grupo110.mitouchmobile.base_de_datos.PostgrestBD;

import java.sql.ResultSet;

public class CambiarPasswordActivity extends AppCompatActivity {

    private Button volveraMenuCambiosOk;
    private EditText passwordVieja;
    private EditText passwordNueva;
    private EditText passwordNuevaRepite;
    private int id_usuario;
    private String password;
    private final static int  LARGO_CONTRASEÑA = 10;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cambiar_password);
        id_usuario = getIntent().getExtras().getInt("id");


        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(getResources().getDrawable(R.drawable.ic_previous));
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();

            }
        });

        passwordVieja =(EditText)findViewById(R.id.editTextPasswordVieja);
        passwordNueva =(EditText)findViewById(R.id.editTextPasswordNueva);
        passwordNuevaRepite =(EditText)findViewById(R.id.editTextPasswordNuevaRepite);
        volveraMenuCambiosOk = (Button) findViewById(R.id.buttonAceptar);

        volveraMenuCambiosOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GrabarCambios();
            }
        });
    }

    /*
    * Si la contraseña vieja es la misma que esta en la base de datos +
    * las contraseñas son validas e iguales guardo los cambios
    *
    * Si la contraseña vieja es la misma que esta en la base de datos +
    * las contraseñas no son validas limpio los campos
    *
     */

    private void GrabarCambios() {
        boolean usuarioContraseñaCoinciden;
        usuarioContraseñaCoinciden = buscarBaseDeDatosUsuarioContraseña();
        if(usuarioContraseñaCoinciden == true)
        {
            if(passwordNueva.getText().toString().equals(passwordNuevaRepite.getText().toString())) {
                if(validarContraseña(passwordNueva.getText().toString())==true) {
                    ActualizarUsuario(passwordNueva.getText().toString());
                    Toast toast2 = Toast.makeText(getApplicationContext(), "Contraseña Actualizada", Toast.LENGTH_SHORT);
                    toast2.show();
                    finish();
                }
                else
                {
                    limpiarCampos();
                }
            }
            else
            {
                limpiarCampos();
                Toast toast2 = Toast.makeText(getApplicationContext(), "Ambas contraseñas no coinciden ", Toast.LENGTH_SHORT);
                toast2.show();
            }

        }else
        {
            limpiarCampos();
            Toast toast2 = Toast.makeText(getApplicationContext(), "El usuario y la contraseña no coinciden ", Toast.LENGTH_SHORT);
            toast2.show();
        }
    }
    /*
    * Valido que la contraseña que ingresa el usuario sea la misma que se encuentra en la base de datos
    */
    private boolean buscarBaseDeDatosUsuarioContraseña() {
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
    /*
    * Actualizar la contraseña en la Base de Datos
    */
    private boolean ActualizarUsuario(String nuevaContraseña) {
        String comando;

        comando = String.format("UPDATE \"MiTouch\".t_usuarios SET usu_password = '"+nuevaContraseña+"' WHERE usu_id = " + id_usuario + ";");
        System.out.println(comando);
        PostgrestBD baseDeDatos = new PostgrestBD();
        ResultSet resultSet = baseDeDatos.execute(comando);
        return false;
    }
    /*
   * Metodo para validar la contraseña que se ingresa
   * Valido que no contenga simbolos, que no tenga espacios y que tenga por lo menos una letra y por lo menos un numero,
   * Valido El largo de la contraseña
    */
    private boolean validarContraseña(String cadena)
    {
        boolean numero= false;
        boolean letra=false;
        Toast toast;
        if(cadena.length() < LARGO_CONTRASEÑA)
        {
            toast = Toast.makeText(getApplicationContext(), "La cantidad minima de caracteres es 10 ", Toast.LENGTH_SHORT);
            toast.show();
            return false;
        }
        for(int i = 0; i < cadena.length(); ++i) {
            char caracter = cadena.charAt(i);

            if(!Character.isLetterOrDigit(caracter)) {
                toast = Toast.makeText(getApplicationContext(), "La contraseña no puede tener simbolos y contener espacios", Toast.LENGTH_SHORT);
                toast.show();
                return false;
            }else
            if(Character.isDigit(caracter))
                numero = true;
            else
                letra = true;
        }

        if(numero == true && letra == true)
        return true;
        toast = Toast.makeText(getApplicationContext(), "La contraseña debe tener por lo menos un digito y una letra", Toast.LENGTH_SHORT);
        toast.show();
        return false;
    }

    public void limpiarCampos(){
        passwordVieja.setText("");
        passwordNueva.setText("");
        passwordNuevaRepite.setText("");
        passwordVieja.clearFocus();
        passwordNueva.clearFocus();
        passwordNuevaRepite.clearFocus();
    }
}