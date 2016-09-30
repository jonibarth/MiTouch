package com.example.grupo110.mitouchmobile;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.sql.ResultSet;
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
        System.out.println("OnCreate Login");
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
                        System.out.println(" id "+id_usuario);
                        System.out.println(" url "+url);

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
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(email.getWindowToken(), 0);
                }
            }
        });

        password.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(!hasFocus) {
                    //Lineas para ocultar el teclado virtual (Hide keyboard)
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(password.getWindowToken(), 0);
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
        Toast toast2 = Toast.makeText(getApplicationContext(), "Usuario y contraseña invalidos... Ingrese nuevamente ", Toast.LENGTH_SHORT);
        toast2.show();
    }

    public boolean login(String usuario, String password) {
        String comando = "";

        comando = String.format("SELECT * FROM  \"MiTouch\".t_usuarios WHERE usu_nombre_usuario ='"+ usuario +"' " +
                "AND usu_password = '"+ password +"';");
        PostgrestBD baseDeDatos = new PostgrestBD();
        ResultSet resultSet = baseDeDatos.execute(comando);
        try{
            while (resultSet.next()) {
                id_usuario = resultSet.getInt("usu_id");
                modificar_usu_ultimo_log_in(id_usuario);

                return true;
            }
        }catch(Exception e){}
        return false;
    }

    private void modificar_usu_ultimo_log_in(int id_usuario) {

        Calendar c = Calendar.getInstance();
        System.out.println("Current time => "+c.getTime());

        SimpleDateFormat diahora = new SimpleDateFormat("yyyyMMdd HH:mm:ss");



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
        Toast t = Toast.makeText(this, "Los datos fueron grabados",
                Toast.LENGTH_SHORT);
        t.show();
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
            System.out.println("que es todo:");
            System.out.println(todo);
        } catch (Exception e) {
            System.out.println("error en el try que esta dentro del if");
        }
    }


    public static void dumpIntent(Intent i){

        Bundle bundle = i.getExtras();
        if (bundle != null) {
            Set<String> keys = bundle.keySet();
            Iterator<String> it = keys.iterator();
           System.out.println("Dumping Intent start");
            while (it.hasNext()) {
                String key = it.next();
                System.out.println("[" + key + "=" + bundle.get(key)+"]");
            }
            System.out.println("Dumping Intent end");
        }
    }
}
