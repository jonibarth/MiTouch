package com.example.grupo110.mitouchmobile;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ExpandableListView;
import android.widget.Toast;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class PedirAccesoGruposActivity extends AppCompatActivity {

    ExpandableListAdapter listAdapter;
    ExpandableListView expListView;
    List<String> listDataHeader;
    HashMap<String, List<String>> listDataChild;
    List<String> grupodeUsuario;
    private int id_usuario;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pedir_acceso_grupos);

        id_usuario = getIntent().getExtras().getInt("id");

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
        // Listview Group expanded listener
        expListView.setOnGroupExpandListener(new ExpandableListView.OnGroupExpandListener() {

            @Override
            public void onGroupExpand(int groupPosition) {
                Toast.makeText(getApplicationContext(),
                        listDataHeader.get(groupPosition) + " Expanded",
                        Toast.LENGTH_SHORT).show();
            }
        });
        // Listview Group collasped listener
        expListView.setOnGroupCollapseListener(new ExpandableListView.OnGroupCollapseListener() {

            @Override
            public void onGroupCollapse(int groupPosition) {
                Toast.makeText(getApplicationContext(),
                        listDataHeader.get(groupPosition) + " Collapsed",
                        Toast.LENGTH_SHORT).show();

            }
        });
        // Listview on child click listener
        expListView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {

            @Override
            public boolean onChildClick(ExpandableListView parent, View v,
                                        int groupPosition, int childPosition, long id) {
                // TODO Auto-generated method stub
                Toast.makeText(
                        getApplicationContext(),
                        listDataHeader.get(groupPosition)
                                + " : "
                                + listDataChild.get(
                                listDataHeader.get(groupPosition)).get(
                                childPosition), Toast.LENGTH_SHORT)
                        .show();
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
        BuscarGruposdeUsuario(grupodeUsuario);
    }


    private void BuscarGruposdeUsuario(List<String> grupodeUsuario) {
        String comando;
        comando = String.format( "SELECT gru_nombre FROM  \"MiTouch\".t_grupos" + ";");

        PostgrestBD baseDeDatos = new PostgrestBD();
        ResultSet resultSet = baseDeDatos.execute(comando);
        try {
            while (resultSet.next()) {
                System.out.println("nombre Grupo de usuario: " + resultSet.getString("gru_nombre"));
                grupodeUsuario.add(resultSet.getString("gru_nombre"));
            }
            listDataChild.put(listDataHeader.get(0), grupodeUsuario); // Header, Child data
        } catch (Exception e) {
            System.err.println("Error busqyeda grupos de usuario");
        }

    }

    private void VerificarSiPuedeTenerAcceso(){
        String comando;
        String grupoUsuario=null;
        comando = String.format( "SELECT gru_id FROM  \"MiTouch\".t_solicitud_acceso WHERE ='" + grupoUsuario + "';");

        PostgrestBD baseDeDatos = new PostgrestBD();
        ResultSet resultSet = baseDeDatos.execute(comando);
        try {
            while (resultSet.next()) {
                System.out.println("Nombre de grupo: " + resultSet.getString("gru_nombre"));
            }
        } catch (Exception e) {
            System.err.println("Error busqyeda grupos de usuario");
        }

    }

}
