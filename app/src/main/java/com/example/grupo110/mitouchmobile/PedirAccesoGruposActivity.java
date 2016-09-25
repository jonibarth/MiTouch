package com.example.grupo110.mitouchmobile;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.format.DateFormat;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.Toast;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

public class PedirAccesoGruposActivity extends AppCompatActivity {

    ExpandableListAdapter listAdapter;
    ExpandableListView expListView;
    List<String> listDataHeader;
    HashMap<String, List<String>> listDataChild;
    List<String> grupodeUsuario;
    List<String> grupodeUsuarioid;
    private int id_usuario;
    String grupoUsuario=null;
    Button aceptarGrupodeUsuario;


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
        setContentView(R.layout.activity_pedir_acceso_grupos);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        id_usuario = getIntent().getExtras().getInt("id");
        aceptarGrupodeUsuario = (Button) findViewById(R.id.buttonregistrarUsuarioNuevoGrupo);

        aceptarGrupodeUsuario.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CrearSolicitudAcceso();
            }
        });

        // get the listview
        expListView = (ExpandableListView) findViewById(R.id.lvExpNuevoGrupo);
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

    private void CrearSolicitudAcceso(){

        if((ValidarSolicitudGrupoDeUsuario()) && BuscarGruposdeUsuarioenArray()!=null )
        {
            String comando2;
            Date d = new Date();
            CharSequence diahora = DateFormat.format("yyyy-MM-dd H:mm:ss", d.getTime());
            System.out.println("Dia hora: " + diahora);

            PostgrestBD baseDeDatos = new PostgrestBD();
            comando2 = String.format("INSERT INTO \"MiTouch\".t_solicitud_acceso (sol_id_usuario,sol_id_grupo,sol_fecha_hora,sol_fecha_hora_respuesta,sol_estado) VALUES (" + id_usuario + ",'" + BuscarGruposdeUsuarioenArray() + "','" + diahora + "',null,null);");
            ResultSet resultSet2 = baseDeDatos.execute(comando2);
            finish();
        }
    }

    private boolean ValidarSolicitudGrupoDeUsuario() {
        String comando;
        PostgrestBD baseDeDatos = new PostgrestBD();
        comando = String.format("SELECT * FROM \"MiTouch\".t_solicitud_acceso WHERE sol_id_usuario="+id_usuario+" AND " +
                "sol_id_grupo="+BuscarGruposdeUsuarioenArray()+";");
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
