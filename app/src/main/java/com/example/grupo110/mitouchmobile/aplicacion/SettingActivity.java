package com.example.grupo110.mitouchmobile.aplicacion;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.format.DateFormat;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.ImageButton;
import android.widget.Toast;

import com.example.grupo110.mitouchmobile.R;
import com.example.grupo110.mitouchmobile.base_de_datos.PostgrestBD;
import com.example.grupo110.mitouchmobile.expandable_list.ExpandableListAdapter;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

public class SettingActivity extends AppCompatActivity {

    /*
    * Declaracion de variables
     */

    private final static int  USUARIOMINIMO = 6;
    private final static int  USUARIOMAXIMO = 15;
    Button cambiarContraseña;
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
    boolean solicitud_acceso=false;

    ExpandableListAdapter listAdapter;
    ExpandableListView expListView;
    List<String> listDataHeader;
    HashMap<String, List<String>> listDataChild;
    List<String> grupodeUsuario;
    List<String> grupodeUsuarioid;
    String grupoUsuario=null;




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
        toolbar.setNavigationIcon(getResources().getDrawable(R.drawable.ic_previous));
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();

            }
        });

        cambiarContraseña =(Button)findViewById(R.id.buttonAceptarCambios);
        cambiarContraseña.setOnClickListener(new View.OnClickListener() {
            @Override

            public void onClick(View v) {
                Intent mainIntent = new Intent(SettingActivity.this, CambiarPasswordActivity.class);
                mainIntent.putExtra("id",id_usuario);
                startActivity(mainIntent);
            }
        });


        ActualizarBasedeDatos =(Button)findViewById(R.id.buttonActualizarDatos);
        ActualizarBasedeDatos.setOnClickListener(new View.OnClickListener() {
            @Override

            public void onClick(View v) {
                if(solicitud_acceso)
                    CrearSolicitudAcceso();
                actualizarBasedeDatos();

            }
        });

        // get the listview
        expListView = (ExpandableListView) findViewById(R.id.lvExpNuevoGrupoSetting);
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
                grupodeUsuario = new ArrayList<>();
                BuscarGruposdeUsuario(grupodeUsuario,grupodeUsuarioid);
                expListView.collapseGroup(groupPosition);
                solicitud_acceso=true;
                return false;
            }
        });

    }

    private void actualizarBasedeDatos() {
        System.out.println("aca en actualizar");
        String comando;
        if((validarUsuario(editTextUsuario.getText().toString()))) {
            comando = "UPDATE \"MiTouch\".t_usuarios SET usu_nombre_usuario = '" + editTextUsuario.getText().toString() + "', usu_nombre_completo = '" + editTextNombreCompleto.getText().toString() + "', usu_mail = '" + editTextMail.getText().toString() + "'" + " WHERE usu_id = " + id_usuario + ";";
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

        comando = "SELECT * FROM  \"MiTouch\".t_usuarios WHERE usu_id ="+ id_usuario +";";

        PostgrestBD baseDeDatos = new PostgrestBD();
        ResultSet resultSet = baseDeDatos.execute(comando);
        try{
            while (resultSet.next()) {
                usu_nombre_usuario = resultSet.getString("usu_nombre_usuario");
                usu_nombre_completo = resultSet.getString("usu_nombre_completo");
                usu_mail = resultSet.getString("usu_mail");
            }
        }catch(Exception e){System.out.println("Error");}
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
        comando = "SELECT * FROM  \"MiTouch\".t_usuarios WHERE usu_nombre_usuario='" + usuario + "';";
        PostgrestBD baseDeDatos = new PostgrestBD();
        ResultSet resultSet = baseDeDatos.execute(comando);
        try {
            if (resultSet.next()){
                Toast toast = Toast.makeText(getApplicationContext(),"El nombre de usuario no esta disponible",Toast.LENGTH_LONG);
                toast.show();
                return false;
            }

        } catch (Exception e) {
            System.err.println("Error busqueda usuario en Registrar");
        }
        return true;
    }


    private void prepareListData() {
        System.out.println("preparando lista");
        listDataHeader = new ArrayList<>();
        listDataChild = new HashMap<>();
        // Adding child data
        listDataHeader.add("Solicitar Acceso a Grupo de usuario..");
        // Adding child data
        grupodeUsuario = new ArrayList<>();
        grupodeUsuarioid = new ArrayList<>();
        BuscarGruposdeUsuario(grupodeUsuario,grupodeUsuarioid);
    }


    private void BuscarGruposdeUsuario(List<String> grupodeUsuario,List<String> grupodeUsuarioid) {
        System.out.println("buscando grupos de usuario");
        String comando;
        comando = "SELECT gru_id,gru_nombre FROM  \"MiTouch\".t_grupos;";
        PostgrestBD baseDeDatos = new PostgrestBD();
        ResultSet resultSet = baseDeDatos.execute(comando);
        try {
            while (resultSet.next()) {
                grupodeUsuario.add(resultSet.getString("gru_nombre"));
                grupodeUsuarioid.add(resultSet.getArray("gru_id").toString());

                System.out.println("buscando grupos de usuario "+ resultSet.getString("gru_nombre"));

            }
            listDataChild.put(listDataHeader.get(0), grupodeUsuario); // Header, Child data
        } catch (Exception e) {
            System.err.println("Error busqyeda grupos de usuario: " + e );
        }

    }

    private void CrearSolicitudAcceso(){

        if((ValidarSolicitudGrupoDeUsuario()) && BuscarGruposdeUsuarioenArray()!=null )
        {
            String comando2;
            Date d = new Date();
            CharSequence diahora = DateFormat.format("yyyy-MM-dd H:mm:ss", d.getTime());
            System.out.println("Dia hora: " + diahora);

            PostgrestBD baseDeDatos = new PostgrestBD();
            comando2 = "INSERT INTO \"MiTouch\".t_solicitud_acceso (sol_id_usuario,sol_id_grupo,sol_fecha_hora,sol_fecha_hora_respuesta,sol_estado) VALUES (" + id_usuario + ",'" + BuscarGruposdeUsuarioenArray() + "','" + diahora + "',null,null);";
            baseDeDatos.execute(comando2);
            finish();
        }
    }

    private boolean ValidarSolicitudGrupoDeUsuario() {
        String comando;
        PostgrestBD baseDeDatos = new PostgrestBD();
        comando = "SELECT * FROM \"MiTouch\".t_solicitud_acceso WHERE sol_id_usuario="+id_usuario+" AND " +
                "sol_id_grupo="+BuscarGruposdeUsuarioenArray()+";";
        ResultSet resultSet = baseDeDatos.execute(comando);

        try {
            while (resultSet.next()) {
                if( resultSet.getInt("sol_estado") == 0 )
                {
                    Toast toast2 = Toast.makeText(getApplicationContext(), "El acceso esta pendiente", Toast.LENGTH_SHORT);
                    toast2.show();
                    return false;
                }else if( resultSet.getInt("sol_estado") == 1)
                {
                    Toast toast2 = Toast.makeText(getApplicationContext(), "Ya tiene acceso al grupo de usuario", Toast.LENGTH_SHORT);
                    toast2.show();
                    return false;
                }
            }
            listDataChild.put(listDataHeader.get(0), grupodeUsuario); // Header, Child data
        } catch (Exception e) {
            System.err.println("Error busqyeda grupos de usuario: " + e );
        }

        return true;
    }

    private String BuscarGruposdeUsuarioenArray() {
        try{
            return grupodeUsuarioid.get(grupodeUsuario.indexOf(grupoUsuario));
        }catch (Exception e){
            return null;
        }
    }


}
