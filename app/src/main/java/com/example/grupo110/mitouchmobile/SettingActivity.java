package com.example.grupo110.mitouchmobile;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.view.menu.MenuView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import java.sql.ResultSet;

public class SettingActivity extends AppCompatActivity {

    /*
    * Declaracion de variables
     */

    private final static int  USUARIOMINIMO = 6;
    private final static int  USUARIOMAXIMO = 15;
    Button cambiarContraseña;
    Button solicitarNuevoGrupodeUsuario;
    Button ActualizarBasedeDatos;
    EditText editTextUsuario;
    EditText editTextNombreCompleto;
    EditText editTextMail;
    ImageButton imagenEditUsuario;
    ImageButton imagenEditNombre;
    ImageButton imagenEditEmail;
    String usu_nombre_usuario;
    String usu_nombre_completo;
    String usu_mail;
    int id_usuario;

    /*
     * Toolbar con el boton para volver al menu anterior
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_settings, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_back:
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        id_usuario = getIntent().getExtras().getInt("id");
        LlenarCampos();
        AgregarImagenes();
        imagenEditUsuario.setOnClickListener(new View.OnClickListener() {
            @Override

            public void onClick(View v) {
                editTextUsuario.setKeyListener(new EditText(getApplicationContext()).getKeyListener());
            }
        });

        imagenEditNombre.setOnClickListener(new View.OnClickListener() {
            @Override

            public void onClick(View v) {
                editTextNombreCompleto.setKeyListener(new EditText(getApplicationContext()).getKeyListener());
            }
        });

        imagenEditEmail.setOnClickListener(new View.OnClickListener() {
            @Override

            public void onClick(View v) {
                editTextMail.setKeyListener(new EditText(getApplicationContext()).getKeyListener());
            }
        });


        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        cambiarContraseña =(Button)findViewById(R.id.buttonAceptarCambios);
        cambiarContraseña.setOnClickListener(new View.OnClickListener() {
            @Override

            public void onClick(View v) {
                Intent mainIntent = new Intent(SettingActivity.this, CambiarPasswordActivity.class);
                mainIntent.putExtra("id",id_usuario);
                startActivity(mainIntent);
            }
        });

        solicitarNuevoGrupodeUsuario =(Button)findViewById(R.id.buttonSolicitarAcceso);
        solicitarNuevoGrupodeUsuario.setOnClickListener(new View.OnClickListener() {
            @Override

            public void onClick(View v) {
                Intent mainIntent = new Intent(SettingActivity.this, PedirAccesoGruposActivity.class);
                mainIntent.putExtra("id",id_usuario);
                startActivity(mainIntent);
                finish();
            }
        });

        ActualizarBasedeDatos =(Button)findViewById(R.id.buttonActualizarDatos);
        ActualizarBasedeDatos.setOnClickListener(new View.OnClickListener() {
            @Override

            public void onClick(View v) {
                actualizarBasedeDatos();

            }
        });

    }

    private void actualizarBasedeDatos() {
        System.out.println("aca en actualizar");
        String comando;
        if((validarUsuario(editTextUsuario.getText().toString()))) {
            comando = String.format("UPDATE \"MiTouch\".t_usuarios SET usu_nombre_usuario = '" + editTextUsuario.getText().toString() + "', usu_nombre_completo = '" + editTextNombreCompleto.getText().toString() + "', usu_mail = '" + editTextMail.getText().toString() + "'" + " WHERE usu_id = " + id_usuario + ";");
            PostgrestBD baseDeDatos = new PostgrestBD();
            baseDeDatos.execute(comando);
            finish(); // No se si deberia quedar en el menu o no!!
        }

    }

    private void AgregarImagenes() {
        String uri = "@drawable/edit";  // where myresource (without the extension) is the file
        int imageResource = getResources().getIdentifier(uri, null, getPackageName());
        imagenEditUsuario= (ImageButton)findViewById(R.id.editarNombreUsuario);
        imagenEditNombre= (ImageButton)findViewById(R.id.editarNombreCompleto);
        imagenEditEmail= (ImageButton)findViewById(R.id.editaremailgmailSetting);

        Drawable res = getResources().getDrawable(imageResource);
        imagenEditUsuario.setImageDrawable(res);
        imagenEditNombre.setImageDrawable(res);
        imagenEditEmail.setImageDrawable(res);
    }

    public void LlenarCampos()
    {
        buscarDatos();
        editTextUsuario =(EditText)findViewById(R.id.nombreUsuarioSetting);
        editTextNombreCompleto =(EditText)findViewById(R.id.nombreCompletoSetting);
        editTextMail =(EditText)findViewById(R.id.emailgmailSettings);

        // No permitir edición
        editTextUsuario.setKeyListener(null);
        editTextNombreCompleto.setKeyListener(null);
        editTextMail.setKeyListener(null);

        // Buscar en base de datos datos del usuario:
        editTextUsuario.setText(usu_nombre_usuario);
        editTextNombreCompleto.setText(usu_nombre_completo);
        editTextMail.setText(usu_mail);

    }

    public int buscarDatos() {
        String comando;

        comando = String.format("SELECT * FROM  \"MiTouch\".t_usuarios WHERE usu_id ="+ id_usuario +";");

        PostgrestBD baseDeDatos = new PostgrestBD();
        ResultSet resultSet = baseDeDatos.execute(comando);
        try{
            while (resultSet.next()) {
                usu_nombre_usuario = resultSet.getString("usu_nombre_usuario");
                usu_nombre_completo = resultSet.getString("usu_nombre_completo");
                usu_mail = resultSet.getString("usu_mail");
            }
        }catch(Exception e){}
        return 0;
    }

    private boolean validarUsuario(String usuario) {
        System.out.println("aca en validar"+ usuario.length());

        if(USUARIOMINIMO>usuario.length())
        {
            Toast toast = Toast.makeText(getApplicationContext(),"la cantidad de caracteres debe ser mayor que 5",Toast.LENGTH_LONG);
            toast.show();
            return false;
        }
        if(usuario.length()> USUARIOMAXIMO){
            Toast toast = Toast.makeText(getApplicationContext(),"la cantidad de caracteres debe ser menor que 16",Toast.LENGTH_LONG);
            toast.show();
            return false;
        }
        String comando;
        comando = String.format( "SELECT * FROM  \"MiTouch\".t_usuarios WHERE usu_nombre_usuario='" + usuario + "';");
        PostgrestBD baseDeDatos = new PostgrestBD();
        ResultSet resultSet = baseDeDatos.execute(comando);
        try {
            while (resultSet.next()){
                Toast toast = Toast.makeText(getApplicationContext(),"El nombre de usuario no esta disponible",Toast.LENGTH_LONG);
                toast.show();
                return false;
            }

        } catch (Exception e) {
            System.err.println("Error busqueda usuario en Registrar");
        }
        return true;
    }
}
