package com.example.grupo110.mitouchmobile.aplicacion;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.grupo110.mitouchmobile.R;
import com.example.grupo110.mitouchmobile.base_de_datos.PostgrestBD;
import com.example.grupo110.mitouchmobile.envioEmail.ForgotPasswordActivity;
import com.example.grupo110.mitouchmobile.galeria.CompartirActivity;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.sql.Date;
import java.sql.ResultSet;
import java.sql.Time;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Iterator;
import java.util.Set;

public class LoginActivity extends AppCompatActivity {

    Button pasarAMenu;
    int id_usuario;
    String url=null;
    EditText email;
    EditText password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        email = (EditText) findViewById(R.id.email);
        password = (EditText) findViewById(R.id.password);
        pasarAMenu = (Button) findViewById(R.id.email_sign_in_button);

        pasarAMenu.setOnClickListener(new View.OnClickListener() {
            @Override

            // Un harcode: si usuario: admin y password: admin entro al menu
            public void onClick(View v) {

                if (login(email.getText().toString(), password.getText().toString())) {
                    System.out.println("El id del usuario es: " +id_usuario);
                    try{
                    url = getIntent().getExtras().getString("url");}catch(Exception e){}
                    grabar();
                    leer();
                    // En caso que quiero compartir una imagen desde la galeria y no estoy logueado, voy a recibir la url.
                    if(url==null)
                        IniciarPantalla();
                    else
                    {
                        Intent siguiente = new Intent(LoginActivity.this, CompartirActivity.class);
                        siguiente.putExtra("id",id_usuario);
                        siguiente.putExtra("url",url);
                        startActivity(siguiente);
                        finish();
                    }
                }
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

        email.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(!hasFocus) {
                    //Lineas para ocultar el teclado virtual (Hide keyboard)
                    //InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    //imm.hideSoftInputFromWindow(email.getWindowToken(), 0);
                }
            }
        });

        password.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(!hasFocus) {
                    //Lineas para ocultar el teclado virtual (Hide keyboard)
                    //InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    //imm.hideSoftInputFromWindow(password.getWindowToken(), 0);
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
                Intent siguiente = new Intent(LoginActivity.this, RegistrarActivity.class);
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
        Toast toast2 = Toast.makeText(getApplicationContext(), "Usuario y contraseña invalido", Toast.LENGTH_SHORT);
        toast2.show();
    }

    public boolean login(String usuario, String password) {
        String comando;

        comando = "SELECT * " +
                "FROM  \"MiTouch\".t_usuarios " +
                "WHERE usu_nombre_usuario ='"+ usuario +"' " +
                "AND usu_password = '"+ password +"' " +
                ";";

        PostgrestBD baseDeDatos = new PostgrestBD();
        ResultSet resultSet = baseDeDatos.execute(comando);
        try{
            while (resultSet.next()) {
                /*
                if(resultSet.getTimestamp("usu_ultimo_log_in")==null ){
                    id_usuario = resultSet.getInt("usu_id");
                    modificar_usu_ultimo_log_in(id_usuario);
                    return true;
                }else

                    if(validarFecha(resultSet.getTimestamp("usu_ultimo_log_in"),resultSet.getTimestamp("usu_ultimo_log_out"))){
                        id_usuario = resultSet.getInt("usu_id");
                        modificar_usu_ultimo_log_in(id_usuario);
                        return true;
                    }else
                        {
                        Toast toast2 = Toast.makeText(getApplicationContext(), "Usuario ya esta logueado ", Toast.LENGTH_SHORT);
                        toast2.show();
                        return false;
                    }*/

                id_usuario = resultSet.getInt("usu_id");
                modificar_usu_ultimo_log_in(id_usuario);
                return true;

            }
        }catch(Exception e){
            Toast toast2 = Toast.makeText(getApplicationContext(), "Error Base de datos! ", Toast.LENGTH_SHORT);
            toast2.show();
            return false;
        }
        Toast toast2 = Toast.makeText(getApplicationContext(), "Usuario no se encuentra registrado ", Toast.LENGTH_SHORT);
        toast2.show();
        return false;
    }

    private boolean validarFecha(Timestamp usu_ultimo_log_in, Timestamp usu_ultimo_log_out) {
        if(usu_ultimo_log_out.getTime() - usu_ultimo_log_in.getTime() > 0)
            return true;
        else
            return false;
    }


    private void modificar_usu_ultimo_log_in(int id_usuario) {

        Calendar c = Calendar.getInstance();
        SimpleDateFormat df = new SimpleDateFormat("yyyyMMdd HH:mm:ss");
        String diahora = df.format(c.getTime());
        String comando = "";
        comando = String.format("UPDATE \"MiTouch\".t_usuarios SET usu_ultimo_log_in ='"+diahora+"' WHERE usu_id ='"+ id_usuario +"';");
        PostgrestBD baseDeDatos = new PostgrestBD();
        baseDeDatos.execute(comando);
    }

    public void grabar() {
        try {
            OutputStreamWriter archivo = new OutputStreamWriter(openFileOutput(
                    "notas.txt", LoginActivity.MODE_PRIVATE));
            archivo.write(String.valueOf(id_usuario));
            archivo.flush();
            archivo.close();
        } catch (Exception e) {System.out.println("Error grabar archivo");
        }
    }

    private void leer() {
        try {
            InputStreamReader archivo = new InputStreamReader(
                    openFileInput("notas.txt"));
            BufferedReader br = new BufferedReader(archivo);
            String linea = br.readLine();
            String todo = "";
            while (linea != null) {
                todo = todo + linea + "\n";
                linea = br.readLine();
            }
            br.close();
            archivo.close();
        } catch (Exception e) {
            System.out.println("error en el try que esta dentro del if");
        }
    }
}
