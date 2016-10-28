package com.example.grupo110.mitouchmobile.galeria;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ExpandableListView;
import android.widget.Toast;

import com.example.grupo110.mitouchmobile.R;
import com.example.grupo110.mitouchmobile.base_de_datos.PostgrestBD;
import com.example.grupo110.mitouchmobile.expandable_list.ExpandableListAdapter;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

/**
 * Created by Jonathan on 10/10/2016.
 */

public class CompartirDesdePopUp extends AppCompatActivity {

    int id_usuario=-1;
    int id_carpetausuario;
    String usuario;
    String nombreArchivo;
    ExpandableListAdapter listAdapter;
    ExpandableListView expListView;
    List<String> listDataHeader;
    HashMap<String, List<String>> listDataChild;
    List<String> grupodeUsuario;
    HashMap<String, List<String>> listDataID;
    List<String> grupodeUsuarioID;
    HashMap<String, List<String>> listDataEscribir;
    List<String> grupodeUsuarioEscribir;

    String grupoUsuario=null;


    static Context context;
    private ProgressDialog progress;

    // Este es el id del archivo que cree!!
    int id_archivo;
    // id de la carpeta la cual va a compartir
    private int idCarpetaOrigen;
    // id de la carpeta la cual va a se le va a compartir el archivo
    private String id_carpetaDestino;

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
        progress = new ProgressDialog(this, R.style.MyTheme);
        progress.setMessage("Descargando..");
        dumpIntent(getIntent());
        try{
            id_usuario = getIntent().getExtras().getInt("id");
            nombreArchivo = getIntent().getExtras().getString("url");
            idCarpetaOrigen = getIntent().getExtras().getInt("idCarpeta");
        }
        catch(Exception e){
            System.out.println("Error: "+e);
        }
        // get the listview
        expListView = (ExpandableListView) findViewById(R.id.lvExpCompartir);
        // preparing list data
        prepareListData();
        context = getApplication();
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
                id_carpetaDestino=listDataID.get(
                        listDataHeader.get(groupPosition)).get(
                        childPosition);
                Toast toast;
                progress.show();
                System.out.println("id_usuario "+id_usuario);
                System.out.println("nombreArchivo "+nombreArchivo);
                System.out.println("idCarpetaOrigen "+idCarpetaOrigen);

                obtenerIdDelArchivo();

                if(!ArchivoExiteEnBD()){
                    ActualizarBaseDeDatos();
                    toast= Toast.makeText(getApplicationContext(),"El archivo fue copiado con exito", Toast.LENGTH_LONG);
                    toast.show();
                    finish();
                }
                else
                {
                    toast= Toast.makeText(getApplicationContext(),"El archivo ya existe", Toast.LENGTH_LONG);
                    toast.show();
                }

                return false;
            }
        });
    }

    private boolean ArchivoExiteEnBD() {
        String comando;
        System.out.println("Estoy en archivo existe en bd");
        comando = "SELECT * " +
                "FROM \"MiTouch\".t_carpeta_archivos_galeria " +
                "WHERE  cag_id_archivo="+id_archivo+" " +
                "AND cag_id_carpeta ="+ Integer.parseInt(id_carpetaDestino)+";";
        System.out.println(comando);
        PostgrestBD baseDeDatos = new PostgrestBD();
        ResultSet resultSet = baseDeDatos.execute(comando);
        try{
            if (resultSet.next()) {
                System.out.println("True");
                return true;
            }
        }catch(Exception e){System.out.println("Error busqueda:" + e);}
        System.out.println("false");
        return false;
    }


    private boolean obtenerIdDelArchivo() {
        System.out.println("Estoy en obtenerIdDelArchivo");
        String comando;

        comando = "SELECT archg_id " +
                "FROM \"MiTouch\".t_archivo_galeria " +
                "INNER JOIN \"MiTouch\".t_carpeta_archivos_galeria ON archg_id = cag_id_archivo " +
                "WHERE  archg_path='"+nombreArchivo+"' " +
                "AND cag_id_carpeta ="+idCarpetaOrigen+";";

        PostgrestBD baseDeDatos = new PostgrestBD();
        ResultSet resultSet = baseDeDatos.execute(comando);
        try{
            if (resultSet.next()) {
                id_archivo = resultSet.getInt("archg_id");
                return true;
            }
        }catch(Exception e){System.out.println("Error busqueda:" + e);}
        return false;
    }
    private void ActualizarBaseDeDatos() {
        System.out.println("Estoy en actualizar bd");
        String comando;
        PostgrestBD baseDeDatos = new PostgrestBD();
        //Asociar Carpeta con el archivo
        comando = "INSERT INTO \"MiTouch\".t_carpeta_archivos_galeria (cag_id_carpeta,cag_id_archivo) VALUES ("+Integer.parseInt(id_carpetaDestino)+","+id_archivo+");";
        baseDeDatos.execute(comando);
    }
    private void prepareListData() {
        listDataHeader = new ArrayList<>();
        listDataChild = new HashMap<>();
        listDataID =new HashMap<>();
        listDataEscribir = new HashMap<>();
        grupodeUsuario = new ArrayList<>();
        grupodeUsuarioID = new ArrayList<>();
        grupodeUsuarioEscribir = new ArrayList<>();
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
/*
        comando = "SELECT * FROM tablaVista;";
        baseDeDatos = new PostgrestBD();
        resultSet = baseDeDatos.execute(comando);
        try {
            while (resultSet.next())
                System.out.println(resultSet.getInt("ugru_id_grupo"));
        } catch (SQLException e) {
            System.out.println("Error select vista" + e);
        }

        System.out.println("pase la vista");
*/

        buscarUsuario();
        System.out.println("El id de la galeria personal es: " + id_carpetausuario);
        System.out.println("El id del usuario es " + id_usuario);

        grupo="Carpeta Personal";

        listDataHeader.add(grupo);
        grupodeUsuario.add(usuario);
        grupodeUsuarioID.add(id_carpetausuario+"");
        grupodeUsuarioEscribir.add("true");

        listDataChild.put(grupo, grupodeUsuario);
        listDataID.put(grupo, grupodeUsuarioID);
        listDataEscribir.put(grupo, grupodeUsuarioEscribir);



        comando = "SELECT ugru_id_grupo, gru_nombre ,ugru_id_usuario, usu_nombre_usuario, gru_id_galeria, usu_id_galeria " +
                "FROM tablaVista NATURAL JOIN \"MiTouch\".t_usuarios_grupo " +
                "INNER JOIN \"MiTouch\".t_grupos ON ugru_id_grupo = gru_id " +

                "INNER JOIN \"MiTouch\".t_usuarios ON ugru_id_usuario = usu_id " +

                //"WHERE  usu_id<>"+id_usuario+" "+

                "ORDER BY ugru_id_grupo, gru_nombre ,ugru_id_usuario, usu_nombre_usuario,gru_id_galeria, usu_id_galeria;";

        baseDeDatos = new PostgrestBD();
        resultSet = baseDeDatos.execute(comando);

        try {
            while (resultSet.next()) {
                if(aux != resultSet.getInt(1)){
                    if(aux == -1)
                    {
                        grupodeUsuario = new ArrayList<>();
                        grupodeUsuarioID = new ArrayList<>();
                        grupodeUsuarioEscribir = new ArrayList<>();
                        grupo=resultSet.getString(2);
                        listDataHeader.add(grupo);
                        grupodeUsuario.add(grupo);
                        grupodeUsuarioID.add(resultSet.getString("gru_id_galeria"));
                        grupodeUsuarioEscribir.add("true");
                        if(id_carpetausuario != resultSet.getInt("usu_id_galeria")) {
                            grupodeUsuario.add(resultSet.getString("usu_nombre_usuario"));
                            grupodeUsuarioID.add(resultSet.getString("usu_id_galeria"));

                            if (resultSet.getString("ugru_id_usuario").equals(id_usuario))
                                grupodeUsuarioEscribir.add("true");
                            else
                                grupodeUsuarioEscribir.add("false");
                        }
                        aux = resultSet.getInt(1);
                    }
                    else
                    {
                        listDataChild.put(grupo, grupodeUsuario);
                        listDataID.put(grupo, grupodeUsuarioID);
                        listDataEscribir.put(grupo, grupodeUsuarioEscribir);
                        grupodeUsuario = new ArrayList<>();
                        grupodeUsuarioID = new ArrayList<>();
                        grupodeUsuarioEscribir = new ArrayList<>();
                        grupo = resultSet.getString(2);
                        listDataHeader.add(grupo);
                        grupodeUsuario.add(grupo);
                        grupodeUsuarioID.add(resultSet.getString("gru_id_galeria"));
                        grupodeUsuarioEscribir.add("true");
                        if(id_carpetausuario != resultSet.getInt("usu_id_galeria")) {
                            grupodeUsuario.add(resultSet.getString("usu_nombre_usuario"));
                            grupodeUsuarioID.add(resultSet.getString("usu_id_galeria"));

                            if (resultSet.getString("ugru_id_usuario").equals(id_usuario))
                                grupodeUsuarioEscribir.add("true");
                            else
                                grupodeUsuarioEscribir.add("false");
                        }
                        aux = resultSet.getInt(1);
                    }
                }
                else
                {
                    if(id_carpetausuario != resultSet.getInt("usu_id_galeria")) {
                        grupodeUsuario.add(resultSet.getString("usu_nombre_usuario"));
                        grupodeUsuarioID.add(resultSet.getString("usu_id_galeria"));

                        if (resultSet.getString("ugru_id_usuario").equals(id_usuario))
                            grupodeUsuarioEscribir.add("true");
                        else
                            grupodeUsuarioEscribir.add("false");
                    }
                }
            }

            listDataChild.put(grupo, grupodeUsuario);
            listDataID.put(grupo, grupodeUsuarioID);
            listDataEscribir.put(grupo, grupodeUsuarioEscribir);

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
                id_carpetausuario = resultSet.getInt("usu_id_galeria");
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