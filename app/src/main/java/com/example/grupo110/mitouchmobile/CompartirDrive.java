package com.example.grupo110.mitouchmobile;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ExpandableListView;

import com.example.grupo110.mitouchmobile.base_de_datos.PostgrestBD;
import com.example.grupo110.mitouchmobile.expandable_list.ExpandableListAdapter;


import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

/**
 * Created by Jonathan on 10/10/2016.
 */
/*
*
* Intent mainIntent = new Intent().setClass(Claseorigen.this, CompartirDrive.class);
* mainIntent.putExtra("id",id_usuario);
* startActivity(mainIntent);
*  finish();
*
*
*
 */

public class CompartirDrive extends AppCompatActivity {

    int id_usuario=-1;
    String email_usuario;
    String nombre_usuario;
    ExpandableListAdapter listAdapter;
    ExpandableListView expListView;

    List<String> listDataHeader;

    HashMap<String, List<String>> listDataChild;
    List<String> grupodeUsuario;

    HashMap<String, List<String>> listDataID;
    List<String> grupodeUsuarioID;

    HashMap<String, List<String>> listDataEmailUsuario;
    List<String> grupodeUsuarioEmail;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_compartir3);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(getResources().getDrawable(R.drawable.ic_previous));
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();

            }
        });
        /*
        Clase que me dice que es lo que estoy mandando
         */
        dumpIntent(getIntent());

        /*
        *   Guardo el id del usuario que esta logueado
         */
        id_usuario = getIntent().getExtras().getInt("id");
        /*
        * Clase utilizada para buscar el nombre del usuario logueado
        * Clase utilizada para buscar el mail del usuario logueado
         */
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
                return false;
            }
        });

        // Listview on child click listener
        expListView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {

            @Override
            public boolean onChildClick(ExpandableListView parent, View v,
                                        int groupPosition, int childPosition, long id) {

                // Cuando hago click sobre un usuario!!!

                System.out.println("Nombre del usuario "+listDataChild.get(
                        listDataHeader.get(groupPosition)).get(
                        childPosition));
                System.out.println("ID del usuario "+ listDataID.get(
                        listDataHeader.get(groupPosition)).get(
                        childPosition));
                System.out.println("Email del usuario "+listDataEmailUsuario.get(
                        listDataHeader.get(groupPosition)).get(
                        childPosition));
                return false;
            }
        });
    }

    private void prepareListData() {
        listDataHeader = new ArrayList<>();

        grupodeUsuario = new ArrayList<>();
        grupodeUsuarioID = new ArrayList<>();
        grupodeUsuarioEmail = new ArrayList<>();

        listDataChild = new HashMap<>();
        listDataID =new HashMap<>();
        listDataEmailUsuario = new HashMap<>();

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

        comando = "SELECT ugru_id_grupo, gru_nombre ,ugru_id_usuario, usu_nombre_usuario, usu_mail, gru_id_carpeta " +
                "FROM tablaVista NATURAL JOIN \"MiTouch\".t_usuarios_grupo " +
                "INNER JOIN \"MiTouch\".t_usuarios ON ugru_id_usuario = usu_id " +
                "INNER JOIN \"MiTouch\".t_grupos ON ugru_id_grupo = gru_id " +
                "WHERE  usu_id<>"+id_usuario+" "+
                "ORDER BY ugru_id_grupo, gru_nombre ,ugru_id_usuario, usu_nombre_usuario, usu_mail, gru_id_carpeta;";
        baseDeDatos = new PostgrestBD();
        resultSet = baseDeDatos.execute(comando);

        grupo="Carpeta Personal";

        listDataHeader.add(grupo);
        grupodeUsuario.add(nombre_usuario);
        grupodeUsuarioID.add(id_usuario+"");
        grupodeUsuarioEmail.add(email_usuario);

        listDataChild.put(grupo, grupodeUsuario);
        listDataID.put(grupo, grupodeUsuarioID);
        listDataEmailUsuario.put(grupo, grupodeUsuarioEmail);

        grupodeUsuario = new ArrayList<>();
        grupodeUsuarioID = new ArrayList<>();
        grupodeUsuarioEmail = new ArrayList<>();


        try {
            while (resultSet.next()) {
                if(aux != resultSet.getInt(1)){
                    System.out.println("Aux: " + aux + " != " +resultSet.getInt(1));
                    if(aux == -1)
                    {

                        grupo=resultSet.getString("gru_nombre");
                        listDataHeader.add(grupo);
                        // Si queres borrar del child el grupo de usuario borra estas 3
                        grupodeUsuario.add(grupo);
                        grupodeUsuarioID.add(resultSet.getString("ugru_id_grupo"));
                        grupodeUsuarioEmail.add(resultSet.getString("gru_id_carpeta"));

                        System.out.println("Grupo nombre: "+resultSet.getString("gru_nombre"));
                        System.out.println("Grupo id : "+resultSet.getString("ugru_id_grupo"));

                        // Corresponde al usuario
                        grupodeUsuario.add(resultSet.getString("usu_nombre_usuario"));
                        grupodeUsuarioID.add(resultSet.getString("ugru_id_usuario"));
                        grupodeUsuarioEmail.add(resultSet.getString("usu_mail"));

                        System.out.println("usuario nombre: "+resultSet.getString("usu_nombre_usuario"));
                        System.out.println("usuario id: "+resultSet.getString("ugru_id_usuario"));
                        System.out.println("usuario mail: "+resultSet.getString("usu_mail"));

                        aux = resultSet.getInt(1);

                    }
                    else
                    {

                        listDataChild.put(grupo, grupodeUsuario);
                        listDataID.put(grupo, grupodeUsuarioID);
                        listDataEmailUsuario.put(grupo, grupodeUsuarioEmail);

                        grupodeUsuario = new ArrayList<>();
                        grupodeUsuarioID = new ArrayList<>();
                        grupodeUsuarioEmail = new ArrayList<>();


                        grupo=resultSet.getString("gru_nombre");
                        listDataHeader.add(grupo);
                        // Si queres borrar el grupo borra estas 3 lineas
                        grupodeUsuario.add(grupo);
                        grupodeUsuarioID.add(resultSet.getString("ugru_id_grupo"));
                        // En la bd no esta esto asi que lo comento
                        grupodeUsuarioEmail.add(resultSet.getString("gru_id_carpeta"));

                        System.out.println("Grupo nombre: "+resultSet.getString("gru_nombre"));
                        System.out.println("Grupo id : "+resultSet.getString("ugru_id_grupo"));

                        //usuario
                        grupodeUsuario.add(resultSet.getString("usu_nombre_usuario"));
                        grupodeUsuarioID.add(resultSet.getString("ugru_id_usuario"));
                        grupodeUsuarioEmail.add(resultSet.getString("usu_mail"));

                        System.out.println("usuario nombre: "+resultSet.getString("usu_nombre_usuario"));
                        System.out.println("usuario id: "+resultSet.getString("ugru_id_usuario"));
                        System.out.println("usuario mail: "+resultSet.getString("usu_mail"));


                        aux = resultSet.getInt(1);

                    }
                }
                else
                {
                    grupodeUsuario.add(resultSet.getString("usu_nombre_usuario"));
                    grupodeUsuarioID.add(resultSet.getString("ugru_id_usuario"));
                    grupodeUsuarioEmail.add(resultSet.getString("usu_mail"));

                    System.out.println("usuario nombre: "+resultSet.getString("usu_nombre_usuario"));
                    System.out.println("usuario id: "+resultSet.getString("ugru_id_usuario"));
                    System.out.println("usuario mail: "+resultSet.getString("usu_nombre_usuario"));
                }
            }

            listDataChild.put(grupo, grupodeUsuario);
            listDataID.put(grupo, grupodeUsuarioID);
            listDataEmailUsuario.put(grupo, grupodeUsuarioEmail);

        } catch (Exception e) {
            System.err.println("Error crear explist: " + e );
        }
    }
    @NonNull
    private Boolean buscarUsuario() {
        String comando;
        comando = "SELECT * FROM  \"MiTouch\".t_usuarios WHERE usu_id ="+ id_usuario +";";
        PostgrestBD baseDeDatos = new PostgrestBD();
        ResultSet resultSet = baseDeDatos.execute(comando);
        try{
            while (resultSet.next()) {
                nombre_usuario = resultSet.getString("usu_nombre_usuario");
                email_usuario = resultSet.getString("usu_mail");
                return true;
            }
        }catch(Exception e){System.out.println("Error busqueda");}
        return false;
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