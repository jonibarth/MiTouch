package com.example.grupo110.mitouchmobile.galeria;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ExpandableListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.grupo110.mitouchmobile.comunicacion_servidor.SFTClienteUploadFileFromGalleriaMiTouch;
import com.example.grupo110.mitouchmobile.comunicacion_servidor.SFTPClienteUploadFileFromGaleriaDispositivo;
import com.example.grupo110.mitouchmobile.expandable_list.ExpandableListAdapter;
import com.example.grupo110.mitouchmobile.base_de_datos.PostgrestBD;
import com.example.grupo110.mitouchmobile.R;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

/*
    * http://blog.openalfa.com/como-cambiar-de-nombre-mover-o-copiar-un-fichero-en-javaç
    * http://es.stackoverflow.com/questions/4225/error-en-metodo-al-mover-archivos-de-un-directorio-a-otro
    * http://kodehelp.com/java-program-for-downloading-file-from-sftp-server/
 */
public class CompartirActivity extends AppCompatActivity {

    int id_usuario=-1;
    int id_carpetausuario;
    String usuario;
    String path=null;
    ExpandableListAdapter listAdapter;
    ExpandableListView expListView;
    List<String> listDataHeader;
    HashMap<String, List<String>> listDataChild;
    List<String> grupodeUsuario;
    HashMap<String, List<String>> listDataID;
    List<String> grupodeUsuarioID;

    String grupoUsuario=null;
    String id_carpeta=null;
    String id_archivo;

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
        dumpIntent(getIntent());
        try{
            id_usuario = getIntent().getExtras().getInt("id");
            path = getIntent().getExtras().getString("url");
            }
        catch(Exception e){
            System.out.println("Error: "+e);
        }
        if(!validarExtenciones(path))
        {
            Toast.makeText(this, "MiTouch no admite la archivo multimedia con la extensión", Toast.LENGTH_LONG).show();

            finish();
        }
        System.out.println("url: " + path);
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
                grupoUsuario = listDataChild.get(
                        listDataHeader.get(groupPosition)).get(
                        childPosition);
               // System.out.println("El grupo de uuario es: "+ grupoUsuario);

                id_carpeta=listDataID.get(
                        listDataHeader.get(groupPosition)).get(
                        childPosition);

                Toast toast;
                obtenerIdDelArchivo();
                if(!ArchivoExiteEnBD()){
                    crearArchivoMultimedia();
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

    private void crearArchivoMultimedia() {
        System.out.println(path);
        System.out.println(path.substring(path.lastIndexOf("/") + 1));

        ActualizarBaseDeDatos(); // Actualice la base de datos, debo tener el archivo creado

        /*
         * Lo que le pando es:
         * process dialog
         * la clase
         * el id del archivo
         * el nombre del archivo
         * contexto
         */



        System.out.println(id_archivo);
        System.out.println(id_carpeta);

        new SFTPClienteUploadFileFromGaleriaDispositivo(id_archivo, path).execute();//Voy a copiar el archivo al servidor

    }
    /*
    *Metodo para actualizar la tabla: t_archivo_galeria
    *
    * Metodo para actualizar la tabla: t_carpeta_archivos_galeria
     */
    private void ActualizarBaseDeDatos() {

        Calendar c = Calendar.getInstance();
        SimpleDateFormat df =new SimpleDateFormat("yyyyMMdd");
        String fecha = df.format(c.getTime());
        String comando;
        comando = "INSERT INTO \"MiTouch\".t_archivo_galeria (archg_path,archg_fecha_desde,archg_fecha_baja) VALUES ('"+path.substring(path.lastIndexOf("/") + 1)+"','"+fecha+"',"+null+") RETURNING archg_id;";
        System.out.println("el comando es: " + comando);
        PostgrestBD baseDeDatos = new PostgrestBD();
        ResultSet resultSet = baseDeDatos.execute(comando);
        try {
            while (resultSet.next()){
                id_archivo=resultSet.getArray(1).toString();
            }
        } catch (Exception e) {
            System.err.println("Error: " + e);
        }

        //Asociar Carpeta con el archivo
        comando = "INSERT INTO \"MiTouch\".t_carpeta_archivos_galeria (cag_id_carpeta,cag_id_archivo) VALUES ("+id_carpeta+","+id_archivo+");";
        System.out.println("el comando es: " + comando);
        baseDeDatos.execute(comando);
    }

    private boolean ArchivoExiteEnBD() {
        String comando;
        System.out.println("Estoy en archivo existe en bd");
        comando = "SELECT * " +
                "FROM \"MiTouch\".t_carpeta_archivos_galeria " +
                "WHERE  cag_id_archivo="+id_archivo+" " +
                "AND cag_id_carpeta ="+ id_carpeta+";";
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
                "WHERE  archg_path='"+path.substring(path.lastIndexOf("/") + 1)+"' " +
                "AND cag_id_carpeta ="+id_carpeta+";";

        PostgrestBD baseDeDatos = new PostgrestBD();
        ResultSet resultSet = baseDeDatos.execute(comando);
        try{
            if (resultSet.next()) {
                id_archivo = resultSet.getInt("archg_id")+"";
                return true;
            }
        }catch(Exception e){System.out.println("Error busqueda:" + e);}
        return false;
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





        comando = "SELECT ugru_id_grupo, gru_nombre ,ugru_id_usuario, usu_nombre_usuario, gru_id_galeria, usu_id_galeria " +
                "FROM tablaVista NATURAL JOIN \"MiTouch\".t_usuarios_grupo " +
                "INNER JOIN \"MiTouch\".t_usuarios ON ugru_id_usuario = usu_id " +
                "INNER JOIN \"MiTouch\".t_grupos ON ugru_id_grupo = gru_id " +
                //"WHERE  usu_id<>"+id_usuario+" "+
                "ORDER BY ugru_id_grupo, gru_nombre ,ugru_id_usuario, usu_nombre_usuario;";
        baseDeDatos = new PostgrestBD();
        resultSet = baseDeDatos.execute(comando);
        System.out.println("El id de la galeria personal es: " + id_carpetausuario);

        grupo="Carpeta Personal";

        listDataHeader.add(grupo);
        grupodeUsuario.add(usuario);
        grupodeUsuarioID.add(id_carpetausuario+"");
        listDataChild.put(grupo, grupodeUsuario);
        listDataID.put(grupo, grupodeUsuarioID);

        try {
            while (resultSet.next()) {
                if(aux != resultSet.getInt(1)){
                    if(aux == -1)
                    {
                        grupodeUsuario = new ArrayList<>();
                        grupodeUsuarioID = new ArrayList<>();
                        grupo=resultSet.getString(2);
                        listDataHeader.add(grupo);
                        grupodeUsuario.add(grupo);
                        grupodeUsuarioID.add(resultSet.getString("gru_id_galeria"));
                        if(id_carpetausuario != resultSet.getInt("usu_id_galeria")) {
                            grupodeUsuario.add(resultSet.getString("usu_nombre_usuario"));
                            grupodeUsuarioID.add(resultSet.getString("usu_id_galeria"));
                        }
                        aux = resultSet.getInt(1);
                    }
                    else
                    {
                        listDataChild.put(grupo, grupodeUsuario);
                        listDataID.put(grupo, grupodeUsuarioID);
                        grupodeUsuario = new ArrayList<>();
                        grupodeUsuarioID = new ArrayList<>();
                        grupo = resultSet.getString(2);
                        listDataHeader.add(grupo);
                        grupodeUsuario.add(grupo);
                        grupodeUsuarioID.add(resultSet.getString("gru_id_galeria"));
                        if(id_carpetausuario != resultSet.getInt("usu_id_galeria")) {
                            grupodeUsuario.add(resultSet.getString("usu_nombre_usuario"));
                            grupodeUsuarioID.add(resultSet.getString("usu_id_galeria"));
                        }
                        aux = resultSet.getInt(1);
                    }
                }
                else
                {
                    if(id_carpetausuario != resultSet.getInt("usu_id_galeria")) {
                        grupodeUsuario.add(resultSet.getString("usu_nombre_usuario"));
                        grupodeUsuarioID.add(resultSet.getString("usu_id_galeria"));
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

    private boolean validarExtenciones(String imgDecodableString) {

        if(     imgDecodableString.substring(imgDecodableString.lastIndexOf(".") + 1).equals("jpg") ||
                imgDecodableString.substring(imgDecodableString.lastIndexOf(".") + 1).equals("JPG") ||

                imgDecodableString.substring(imgDecodableString.lastIndexOf(".") + 1).equals("bmp") ||
                imgDecodableString.substring(imgDecodableString.lastIndexOf(".") + 1).equals("BMP") ||

                imgDecodableString.substring(imgDecodableString.lastIndexOf(".") + 1).equals("mp4") ||
                imgDecodableString.substring(imgDecodableString.lastIndexOf(".") + 1).equals("MP4") ||

                imgDecodableString.substring(imgDecodableString.lastIndexOf(".") + 1).equals("avi") ||
                imgDecodableString.substring(imgDecodableString.lastIndexOf(".") + 1).equals("AVI")
                )
            return true;

        return false;
    }

}