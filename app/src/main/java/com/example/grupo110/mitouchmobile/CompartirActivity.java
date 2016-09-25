package com.example.grupo110.mitouchmobile;


import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ExpandableListView;
import android.widget.Toast;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CompartirActivity extends AppCompatActivity {

    int id_usuario=18;
    ExpandableListAdapter listAdapter;
    ExpandableListView expListView;
    List<String> listDataHeader;
    HashMap<String, List<String>> listDataChild;
    List<String> grupodeUsuario;
    List<String> grupodeUsuarioid;
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
        setContentView(R.layout.activity_compartir);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        //id_usuario = getIntent().getExtras().getInt("id");
        System.out.println( getIntent().getExtras().get(Intent.EXTRA_STREAM));

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
                Toast toast = Toast.makeText(getApplicationContext(),grupoUsuario,Toast.LENGTH_LONG);
                toast.show();
                return false;
            }
        });
    }


    private void prepareListData() {
        listDataHeader = new ArrayList<String>();
        listDataChild = new HashMap<String, List<String>>();
        grupodeUsuario = new ArrayList<String>();
        BuscarGruposdeUsuario(grupodeUsuario);
    }


    private void BuscarGruposdeUsuario(List<String> grupodeUsuario) {
        String comando,grupo=null;
        PostgrestBD baseDeDatos;
        ResultSet resultSet;
        int aux = -1;

        comando = String.format( "DROP VIEW tablaVista ;");
        baseDeDatos = new PostgrestBD();
        try{
            baseDeDatos.execute(comando);
        }catch (Exception e){System.out.println("Error drop vista: "+ e );}


        comando = "CREATE VIEW tablaVista AS " +
                "SELECT ugru_id_grupo " +
                "FROM \"MiTouch\".t_usuarios_grupo " +
                "WHERE ugru_id_usuario='"+id_usuario+"';";
        baseDeDatos = new PostgrestBD();
        try{
            baseDeDatos.execute(comando);
        }catch (Exception e){System.out.println("Error creacion vista: "+ e );}

        comando = "SELECT ugru_id_grupo, gru_nombre ,ugru_id_usuario, usu_nombre_usuario " +
                "FROM tablaVista NATURAL JOIN \"MiTouch\".t_usuarios_grupo " +
                "INNER JOIN \"MiTouch\".t_usuarios ON ugru_id_usuario = usu_id " +
                "INNER JOIN \"MiTouch\".t_grupos ON ugru_id_grupo = gru_id " +
                "ORDER BY ugru_id_grupo, gru_nombre ,ugru_id_usuario, usu_nombre_usuario;";
        baseDeDatos = new PostgrestBD();
        resultSet = baseDeDatos.execute(comando);
        try {
            while (resultSet.next()) {
                if(aux != resultSet.getInt(1)){
                    if(aux == -1)
                    {//Lo hago solo para el primer registro!!
                        grupo=resultSet.getString(2);
                        listDataHeader.add(grupo);
                        grupodeUsuario.add(grupo);
                        grupodeUsuario.add(resultSet.getString(4));
                        aux=resultSet.getInt(1);
                    }
                    else
                    {
                        listDataChild.put(grupo, grupodeUsuario);
                        grupodeUsuario = new ArrayList<String>();

                        grupo=resultSet.getString(2);
                        listDataHeader.add(grupo);
                        grupodeUsuario.add(grupo);
                        grupodeUsuario.add(resultSet.getString(4));
                        aux=resultSet.getInt(1);
                    }
                }
                else
                {
                    System.out.println("Agregar usuario a la lista");
                    grupodeUsuario.add(resultSet.getString(4));
                }
            }

            listDataChild.put(grupo, grupodeUsuario);

        } catch (Exception e) {
            System.err.println("Error crear explist: " + e );
        }

        int size=listDataHeader.size();
        for(int x=0;x<listDataHeader.size();x++) {
            System.out.println("listDataHeader "+listDataHeader.get(x));
        }

        for (Map.Entry<String, List<String>> entry : listDataChild.entrySet()) {
            System.out.println("clave=" + entry.getKey() + ", valor=" + entry.getValue());
        }



    }


}