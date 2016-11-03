package com.example.grupo110.mitouchmobile.chat;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ExpandableListView;
import android.widget.Toast;

import com.example.grupo110.mitouchmobile.R;
import com.example.grupo110.mitouchmobile.aplicacion.MainMenuActivity;
import com.example.grupo110.mitouchmobile.aplicacion.SettingActivity;
import com.example.grupo110.mitouchmobile.base_de_datos.PostgrestBD;
import com.example.grupo110.mitouchmobile.expandable_list.ExpandableListAdapter;
import com.example.grupo110.mitouchmobile.google_calendar.GoogleCalendarActivity;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

public class SeleccionarChat extends AppCompatActivity {

    int id_usuario=-1;
    String usuario;
    ExpandableListAdapter listAdapter;
    ExpandableListView expListView;
    List<String> listDataHeader;
    HashMap<String, List<String>> listDataChild;
    List<String> grupodeUsuario;
    HashMap<String, List<String>> listDataID;
    List<String> grupodeUsuarioID;

    String grupoUsuario=null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_seleccionar_chat);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(getResources().getDrawable(R.drawable.ic_previous));
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        id_usuario = getIntent().getExtras().getInt("id");
        buscarUsuario();

        // get the listview
        expListView = (ExpandableListView) findViewById(R.id.lvExpCompartir);
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
                // v.setBackgroundColor(Color.GREEN);
                return false;
            }
        });

        // Listview Group collasped listener
        expListView.setOnGroupCollapseListener(new ExpandableListView.OnGroupCollapseListener() {

            @Override
            public void onGroupCollapse(int groupPosition) {

            }
        });

        // Listview on child click listener
        expListView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {

            @Override
            public boolean onChildClick(ExpandableListView parent, View v,
                                        int groupPosition, int childPosition, long id) {

                Intent mainIntent = new Intent(SeleccionarChat.this, ChatActivity.class);

                String id_usuario2=listDataID.get(
                        listDataHeader.get(groupPosition)).get(
                        childPosition);

                String usuario2=listDataChild.get(
                        listDataHeader.get(groupPosition)).get(
                        childPosition);


                mainIntent.putExtra("id",id_usuario);
                mainIntent.putExtra("id2",Integer.parseInt(id_usuario2));
                mainIntent.putExtra("idnombre",usuario);
                mainIntent.putExtra("idnombre2",usuario2);
                startActivity(mainIntent);
                finish();
                return false;
            }
        });
    }


    private void prepareListData() {
        listDataHeader = new ArrayList<>();
        listDataChild = new HashMap<>();
        listDataID =new HashMap<>();
        grupodeUsuario = new ArrayList<>();
        grupodeUsuarioID = new ArrayList<>();
        BuscarGruposdeUsuario(grupodeUsuario);
    }
    private void BuscarGruposdeUsuario(List<String> grupodeUsuario) {
        String comando,grupo=null;
        PostgrestBD baseDeDatos;
        ResultSet resultSet;
        int aux = -1;

        comando ="DROP VIEW tablaVista ;";
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
                if (!resultSet.getString("ugru_id_usuario").equals(id_usuario + "")) {
                    if (aux != resultSet.getInt(1)) {
                        if (aux == -1) {
                            try {
                                if (!resultSet.getString("ugru_id_usuario").equals(null)) {
                                    grupo = resultSet.getString("gru_nombre");
                                    listDataHeader.add(grupo);
                                    //grupodeUsuario.add(grupo);
                                    //grupodeUsuarioID.add(resultSet.getString("ugru_id_grupo"));
                                    if (!resultSet.getString("ugru_id_usuario").equals(id_usuario + "")) {
                                        grupodeUsuario.add(resultSet.getString("usu_nombre_usuario"));
                                        grupodeUsuarioID.add(resultSet.getString("ugru_id_usuario"));
                                    }
                                }
                            } catch (Exception e) {
                                System.out.println("Error try");
                            }
                            aux = resultSet.getInt(1);
                        } else {
                            listDataChild.put(grupo, grupodeUsuario);
                            listDataID.put(grupo, grupodeUsuarioID);
                            grupodeUsuario = new ArrayList<>();
                            grupodeUsuarioID = new ArrayList<>();

                            try {
                                if (!resultSet.getString("ugru_id_usuario").equals(null)) {
                                    grupo = resultSet.getString("gru_nombre");
                                    listDataHeader.add(grupo);
                                    //grupodeUsuario.add(grupo);
                                    //grupodeUsuarioID.add(resultSet.getString("ugru_id_grupo"));
                                    if (!resultSet.getString("ugru_id_usuario").equals(id_usuario + "")) {
                                        grupodeUsuario.add(resultSet.getString("usu_nombre_usuario"));
                                        grupodeUsuarioID.add(resultSet.getString("ugru_id_usuario"));
                                    }
                                }
                            } catch (Exception e) {
                                System.out.println("Error try");
                            }
                            aux = resultSet.getInt(1);
                        }
                    } else {
                        if (!resultSet.getString("ugru_id_usuario").equals(id_usuario + "")) {
                            grupodeUsuario.add(resultSet.getString("usu_nombre_usuario"));
                            grupodeUsuarioID.add(resultSet.getString("ugru_id_usuario"));
                        }
                    }
                }
            }

            listDataChild.put(grupo, grupodeUsuario);
            listDataID.put(grupo, grupodeUsuarioID);

        } catch (Exception e) {
            System.err.println("Error crear explist: " + e );
        }


    }
    @NonNull
    private Boolean buscarUsuario() {
        String comando = "";
        comando = "SELECT * FROM  \"MiTouch\".t_usuarios WHERE usu_id ="+ id_usuario +";";
        PostgrestBD baseDeDatos = new PostgrestBD();
        ResultSet resultSet = baseDeDatos.execute(comando);
        try{
            while (resultSet.next()) {
                usuario = resultSet.getString("usu_nombre_usuario");
                System.out.println("El usuario es: " + usuario);
                return true;
            }
        }catch(Exception e){System.out.println("Error busqueda");}
        return false;
    }


}