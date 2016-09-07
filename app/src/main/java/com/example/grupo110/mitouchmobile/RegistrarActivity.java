package com.example.grupo110.mitouchmobile;


import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.format.DateFormat;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.Toast;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

public class RegistrarActivity extends AppCompatActivity {

    ExpandableListAdapter listAdapter;
    ExpandableListView expListView;

    List<String> listDataHeader;
    HashMap<String, List<String>> listDataChild;
    List<String> grupodeUsuario;
    List<String> grupodeUsuarioid;
    EditText nombreUsuario;
    EditText nombreCompleto;
    EditText direccionEmail;
    EditText contraseña;
    EditText repetirContraseña;
    String grupoUsuario=null;

    Button botonRegistrar;
    private final static int  LARGO_CONTRASEÑA = 10;
    private final static int  USUARIOMINIMO = 6;
    private final static int  USUARIOMAXIMO = 15;

    private boolean nombreValido = false;
    private boolean contraseñaValida = false;
    private boolean contraseñaRepiteValida = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registrar);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        nombreUsuario = (EditText) findViewById(R.id.nombreUsuarioRegistrar);
        nombreCompleto = (EditText) findViewById(R.id.nombreCompletoRegistrar);
        direccionEmail = (EditText) findViewById(R.id.emailgmailRegistrar);
        contraseña = (EditText) findViewById(R.id.passwordRegistrar);
        repetirContraseña = (EditText) findViewById(R.id.repitepasswordRegistrar);
        botonRegistrar = (Button) findViewById(R.id.buttonregistrarUsuario);

        nombreUsuario.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(!hasFocus){
                    boolean respuesta = validarUsuario(nombreUsuario.getText().toString());
                    insertarImagenNombreUsuario(respuesta);
                    //Lineas para ocultar el teclado virtual (Hide keyboard)
                    InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(repetirContraseña.getWindowToken(), 0);
                }

            }
        });
        nombreCompleto.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(!hasFocus) {
                    //Lineas para ocultar el teclado virtual (Hide keyboard)
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(repetirContraseña.getWindowToken(), 0);
                }
            }
        });
        direccionEmail.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(!hasFocus) {
                    //Lineas para ocultar el teclado virtual (Hide keyboard)
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(repetirContraseña.getWindowToken(), 0);
                }
            }
        });
        contraseña.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(!hasFocus){
                    boolean respuesta = validarContraseña(contraseña.getText().toString());
                    insertarImagenContraseña(respuesta);
                    //Lineas para ocultar el teclado virtual (Hide keyboard)
                    InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(repetirContraseña.getWindowToken(), 0);
                }

            }
        });
        repetirContraseña.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(!hasFocus){
                    if(repetirContraseña.getText().toString().equals(contraseña.getText().toString()))
                        insertarImagenRepiteContraseña(true);
                    else
                        insertarImagenRepiteContraseña(false);
                    //Lineas para ocultar el teclado virtual (Hide keyboard)
                    InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(repetirContraseña.getWindowToken(), 0);
                }

            }
        });

        botonRegistrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                nombreUsuario.clearFocus();
                nombreCompleto.clearFocus();
                contraseña.clearFocus();
                repetirContraseña.clearFocus();
                if((nombreValido) && (contraseñaRepiteValida) && (contraseñaValida) && (grupoUsuario!=null)) {
                //    if((nombreValido) && (contraseñaRepiteValida) && (contraseñaValida)) {
                    CrearUsuario();
                    Toast toast2 = Toast.makeText(getApplicationContext(),"Usuario Registrado",Toast.LENGTH_LONG);
                    toast2.show();
                    Intent siguiente = new Intent(RegistrarActivity.this, LoginActivity.class);
                    startActivity(siguiente);
                    finish();
                }
                else
                {
                    Toast toast = Toast.makeText(getApplicationContext(),"Datos invalidos",Toast.LENGTH_LONG);
                    toast.show();
                }

            }
        });

        // get the listview
        expListView = (ExpandableListView) findViewById(R.id.lvExp);
        // preparing list data
        prepareListData();

        listAdapter = new ExpandableListAdapter(this, listDataHeader, listDataChild);
        // setting list adapter
        expListView.setAdapter(listAdapter);
        // Listview Group click listener
        expListView.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {

            @Override
            public boolean onGroupClick(ExpandableListView parent, View v,
                                        int groupPosition, long id) {
                // Toast.makeText(getApplicationContext(),
                // "Group Clicked " + listDataHeader.get(groupPosition),
                // Toast.LENGTH_SHORT).show();
                return false;
            }
        });

        // Listview on child click listener
        expListView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {

            @Override
            public boolean onChildClick(ExpandableListView parent, View v,
                                        int groupPosition, int childPosition, long id) {
                grupoUsuario = listDataChild.get(
                        listDataHeader.get(groupPosition)).get(
                        childPosition);
                listDataHeader.clear();
                listDataHeader.add( grupoUsuario);
                grupodeUsuario = new ArrayList<String>();
                BuscarGruposdeUsuario(grupodeUsuario,grupodeUsuarioid);
                expListView.collapseGroup(groupPosition);
                return false;
            }
        });
    }
    /*
     * Preparing the list data
     */

    private void prepareListData() {
        listDataHeader = new ArrayList<String>();
        listDataChild = new HashMap<String, List<String>>();
        // Adding child data
        listDataHeader.add("Grupo de Usuario");
        // Adding child data
        grupodeUsuario = new ArrayList<String>();
        grupodeUsuarioid = new ArrayList<String>();
        BuscarGruposdeUsuario(grupodeUsuario,grupodeUsuarioid);
    }


    private void BuscarGruposdeUsuario(List<String> grupodeUsuario,List<String> grupodeUsuarioid) {
        String comando;
        comando = String.format( "SELECT * FROM  \"MiTouch\".t_grupos" + ";");
        PostgrestBD baseDeDatos = new PostgrestBD();
        ResultSet resultSet = baseDeDatos.execute(comando);
        try {
            while (resultSet.next()) {
                grupodeUsuario.add(resultSet.getString("gru_nombre"));
                grupodeUsuarioid.add(resultSet.getArray("gru_id").toString());
            }
            listDataChild.put(listDataHeader.get(0), grupodeUsuario); // Header, Child data
        } catch (Exception e) {
            System.err.println("Error busqyeda grupos de usuario: " + e );
        }

    }

    private void CrearUsuario(){
        String comando,comando2;
        Date d = new Date();
        CharSequence diahora  = DateFormat.format("yyyy-MM-dd H:mm:ss", d.getTime());
        System.out.println("Dia hora: " + diahora);
        CharSequence dia  = DateFormat.format("yyyy-MM-dd", d.getTime());
        System.out.println("dia: " + dia);
        String id_usuadioGenerado=null;

        comando = String.format("INSERT INTO \"MiTouch\".t_usuarios ( usu_fecha_alta, usu_path_galeria, usu_id_carpeta, usu_ultimo_log_in,usu_ultimo_log_out, usu_password, usu_nombre_usuario, usu_nombre_completo,usu_fecha_baja, usu_administrador, usu_mail) VALUES ('"+ dia +"', 'path6', 'path6', null ,null, '"+contraseña.getText().toString() +"', '"+nombreUsuario.getText().toString() +"', '"+nombreCompleto.getText().toString()+"',null, true, '"+direccionEmail.getText().toString()+"@gmail.com') RETURNING usu_id;");
        PostgrestBD baseDeDatos = new PostgrestBD();
        // Inserto usuario en la tabla usuarios
        // Busco el ID del usuario que genere para insertarlo en la tabla de solicitud de acceso!
        ResultSet resultSet = baseDeDatos.execute(comando);
        try {
            while (resultSet.next()){
                id_usuadioGenerado=resultSet.getArray(1).toString();
            }
        } catch (Exception e) {
            System.err.println("Error: " + e);
        }

        comando2 = String.format("INSERT INTO \"MiTouch\".t_solicitud_acceso (sol_id_usuario,sol_id_grupo,sol_fecha_hora,sol_fecha_hora_respuesta,sol_estado) VALUES ("+id_usuadioGenerado+",'"+BuscarGruposdeUsuarioenArray()+"','"+ diahora +"',null,null);");
        ResultSet resultSet2 = baseDeDatos.execute(comando2);
    }

    private String BuscarGruposdeUsuarioenArray() {
        return grupodeUsuarioid.get(grupodeUsuario.indexOf(grupoUsuario));
    }

    private boolean validarUsuario(String usuario) {
        if(USUARIOMINIMO>usuario.length())
        {
            Toast toast = Toast.makeText(getApplicationContext(),"la cantidad de caracteres debe ser mayor que 5",Toast.LENGTH_LONG);
            toast.show();
            return true;
        }
        if(usuario.length()> USUARIOMAXIMO){
            Toast toast = Toast.makeText(getApplicationContext(),"la cantidad de caracteres debe ser menor que 16",Toast.LENGTH_LONG);
            toast.show();
            return true;
        }
        String comando;
        comando = String.format( "SELECT * FROM  \"MiTouch\".t_usuarios WHERE usu_nombre_usuario='" + usuario + "';");
        PostgrestBD baseDeDatos = new PostgrestBD();
        ResultSet resultSet = baseDeDatos.execute(comando);
        try {
            while (resultSet.next())
                return true;
        } catch (Exception e) {
            System.err.println("Error busqueda usuario en Registrar");
        }
        return false;
    }

    private void insertarImagenNombreUsuario(boolean respuesta) {

        if(respuesta == true)
        {
            String uri = "@drawable/wrong";  // where myresource (without the extension) is the file
            int imageResource = getResources().getIdentifier(uri, null, getPackageName());
            ImageView imagen= (ImageView)findViewById(R.id.ImagenViewNombreUsuarioRegistro);
            Drawable res = getResources().getDrawable(imageResource);
            imagen.setImageDrawable(res);
        }
        else
        {
            String uri = "@drawable/right";  // where myresource (without the extension) is the file
            int imageResource = getResources().getIdentifier(uri, null, getPackageName());
            ImageView imagen= (ImageView)findViewById(R.id.ImagenViewNombreUsuarioRegistro);
            Drawable res = getResources().getDrawable(imageResource);
            imagen.setImageDrawable(res);
            nombreValido = true;
        }
    }

    private void insertarImagenContraseña(boolean respuesta) {

        if(respuesta == false)
        {
            String uri = "@drawable/wrong";  // where myresource (without the extension) is the file
            int imageResource = getResources().getIdentifier(uri, null, getPackageName());
            ImageView imagen= (ImageView)findViewById(R.id.ImagenViewContraseña);
            Drawable res = getResources().getDrawable(imageResource);
            imagen.setImageDrawable(res);
        }
        else
        {
            String uri = "@drawable/right";  // where myresource (without the extension) is the file
            int imageResource = getResources().getIdentifier(uri, null, getPackageName());
            ImageView imagen= (ImageView)findViewById(R.id.ImagenViewContraseña);
            Drawable res = getResources().getDrawable(imageResource);
            imagen.setImageDrawable(res);
            contraseñaValida = true;
        }
    }

    private void insertarImagenRepiteContraseña(boolean respuesta) {

        if(respuesta == false)
        {
            String uri = "@drawable/wrong";  // where myresource (without the extension) is the file
            int imageResource = getResources().getIdentifier(uri, null, getPackageName());
            ImageView imagen= (ImageView)findViewById(R.id.ImagenViewNombreRepiteContraseña);
            Drawable res = getResources().getDrawable(imageResource);
            imagen.setImageDrawable(res);
        }
        else
        {
            String uri = "@drawable/right";  // where myresource (without the extension) is the file
            int imageResource = getResources().getIdentifier(uri, null, getPackageName());
            ImageView imagen= (ImageView)findViewById(R.id.ImagenViewNombreRepiteContraseña);
            Drawable res = getResources().getDrawable(imageResource);
            imagen.setImageDrawable(res);
            contraseñaRepiteValida = true;

        }
    }

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
        //Valido que no contenga simbolos, que no tenga espacios y que tenga por lo menos una letra y por lo menos un numero,
        for(int i = 0; i < cadena.length(); ++i) {
            char caracter = cadena.charAt(i);
// No acepto simbolos ni vacios!
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
}
/*
// Listview Group expanded listener
expListView.setOnGroupExpandListener(new ExpandableListView.OnGroupExpandListener() {

@Override
public void onGroupExpand(int groupPosition) {
        Toast.makeText(getApplicationContext(),
        listDataHeader.get(groupPosition) + " Expanded",
        Toast.LENGTH_SHORT).show();
        //botonRegistrar.setVisibility(View.INVISIBLE);

        }
        });
        // Listview Group collasped listener
        expListView.setOnGroupCollapseListener(new ExpandableListView.OnGroupCollapseListener() {

@Override
public void onGroupCollapse(int groupPosition) {
        Toast.makeText(getApplicationContext(),
        listDataHeader.get(groupPosition) + " Collapsed",
        Toast.LENGTH_SHORT).show();
        // botonRegistrar.setVisibility(View.VISIBLE);

        }
        });
*/
