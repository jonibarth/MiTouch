package com.example.grupo110.mitouchmobile;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ExpandableListView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

public class CompartirActivity2 extends AppCompatActivity {

    int id_usuario=-1;
    String path=null;
    ExpandableListAdapter listAdapter;
    ExpandableListView expListView;
    List<String> listDataHeader;
    HashMap<String, List<String>> listDataChild;
    List<String> grupodeUsuario;
    String grupoUsuario=null;


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
        setContentView(R.layout.activity_compartir3);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        try{
            id_usuario = getIntent().getExtras().getInt("id");
            System.out.println("holi1");
            path = getIntent().getExtras().getString("url");
            System.out.println("holi2");}
        catch(Exception e){
            System.out.println("Error: "+e);
        }


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
                grupoUsuario = listDataChild.get(
                        listDataHeader.get(groupPosition)).get(
                        childPosition);
                Toast toast = Toast.makeText(getApplicationContext(),"soy: "+id_usuario +"usuario: "+grupoUsuario+"imagen: "+ path ,Toast.LENGTH_LONG);
                toast.show();

                try
                {
                    System.out.println("Crear archivo");
                    OutputStreamWriter fout=
                            new OutputStreamWriter(
                                    openFileOutput("prueba_int.txt", Context.MODE_PRIVATE));

                    fout.write("Texto de prueba.");
                    fout.close();
                    System.out.println("archivo Creado");
                }
                catch (Exception ex)
                {
                    Log.e("Ficheros", "Error al escribir fichero a memoria interna");
                }



                try
                {
                    BufferedReader fin =
                            new BufferedReader(
                                    new InputStreamReader(
                                            openFileInput("prueba_int.txt")));

                    String texto = fin.readLine();
                    System.out.println(texto);
                    fin.close();
                }
                catch (Exception ex)
                {
                    Log.e("Ficheros", "Error al leer fichero desde memoria interna");
                }
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
                    //System.out.println("Agregar usuario a la lista");
                    grupodeUsuario.add(resultSet.getString(4));
                }
            }

            listDataChild.put(grupo, grupodeUsuario);

        } catch (Exception e) {
            System.err.println("Error crear explist: " + e );
        }
    }

    private boolean existe(String[] archivos, String archbusca) {
        for (int f = 0; f < archivos.length; f++)
            if (archbusca.equals(archivos[f]))
                return true;
        return false;
    }

    public void IniciarPantalla() {
        Intent siguiente = new Intent(CompartirActivity2.this, MainMenuActivity.class);
        siguiente.putExtra("id",id_usuario);
        startActivity(siguiente);
        finish();
    }

    private Boolean buscarUsuario() {
        String comando = "";
        System.out.println("el usuario es" + id_usuario);
        comando = String.format("SELECT * FROM  \"MiTouch\".t_usuarios WHERE usu_id ="+ id_usuario +";");
        PostgrestBD baseDeDatos = new PostgrestBD();
        ResultSet resultSet = baseDeDatos.execute(comando);
        try{
            while (resultSet.next()) {
                System.out.println("usuario: " + resultSet.getInt("usu_id"));
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