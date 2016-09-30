package com.example.grupo110.mitouchmobile;

import android.content.Context;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.View;
import android.widget.ExpandableListView;
import android.widget.Toast;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.sql.ResultSet;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

public class CompartirActivity extends AppCompatActivity {

    int id_usuario=-1;
    String path=null;
    String archivoOriginal=null;
    ExpandableListAdapter listAdapter;
    ExpandableListView expListView;
    List<String> listDataHeader;
    List<String> listIDHeader;
    HashMap<String, List<String>> listDataChild;
    List<String> grupodeUsuario;
    String grupoUsuario=null;
    String id_carpeta=null;
    final String PATH_BASE_DE_DATOS = "C:\\Program Files\\MiTouch";
    final String PATH_MOBILE = "/storage/sdcard0/MiTouchMultimedia";
    static Context context;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_compartir3);
        Toast toast;
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(getResources().getDrawable(R.drawable.ic_previous));
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();

            }
        });
        try{
            id_usuario = getIntent().getExtras().getInt("id");
            path = getIntent().getExtras().getString("url");
            }
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
                context = getApplicationContext();
                //Toast toast = Toast.makeText(getApplicationContext(),"soy: "+id_usuario +"usuario: "+grupoUsuario+"imagen: "+ path ,Toast.LENGTH_LONG);
                //toast.show();
                id_carpeta=listIDHeader.get(childPosition);

                archivoOriginal = obtenerArchivo();
                String directorio = obtenerDirectorio();

                //System.out.println("path original: " + path);
                //System.out.println("directorio original: " + directorio);
                //System.out.println("Image name: " + archivoOriginal);
                //System.out.println("path destino: " + PATH_MOBILE);
                //System.out.println("soy: "+id_usuario);
                //System.out.println("usuario: "+grupoUsuario);
                Toast toast;

                if(!ArchivoExiteEnBD()){
                    //System.out.println("copiando archivo en la base de datos...");
                    CrearDirectorio();
                    copyFileOrDirectory(path,PATH_MOBILE+"/"+grupoUsuario);
                    ActualizarBaseDeDatos();
                    toast= Toast.makeText(getApplicationContext(),"El archivo fue copiado con exito", Toast.LENGTH_LONG);
                    toast.show();
                    finish();
                }
                else
                {
                    //System.out.println("el archivo existe en la base de datos");
                    toast= Toast.makeText(getApplicationContext(),"El archivo ya existe", Toast.LENGTH_LONG);
                    toast.show();
                }

                return false;
            }
        });
    }
    private boolean ArchivoExiteEnBD() {
        String pathAVerificar =PATH_BASE_DE_DATOS+"\\"+grupoUsuario+"\\"+archivoOriginal;
        String comando = "";

        //System.out.println("El path es: "+ pathAVerificar);
        comando = "SELECT archg_path FROM \"MiTouch\".t_archivo_galeria WHERE archg_path ='"+pathAVerificar+"';";
        PostgrestBD baseDeDatos = new PostgrestBD();
        ResultSet resultSet = baseDeDatos.execute(comando);
        try{
            while (resultSet.next()) {
                //System.out.println("eee: " + resultSet.getString(1));
                return true;
            }
        }catch(Exception e){System.out.println("Error busqueda:" + e);}

        return false;
    }
    // http://blog.openalfa.com/como-cambiar-de-nombre-mover-o-copiar-un-fichero-en-javaç
    // http://es.stackoverflow.com/questions/4225/error-en-metodo-al-mover-archivos-de-un-directorio-a-otro
    private void CopiarAInternalStorage(){

        // obtengo el nombre del archivo que queiro copiar

        //obtengo el directorio del archivo que quiero copiar

    }
    private void ActualizarBaseDeDatos() {
        // Crear Registro en la tabla de archivos
        String id_archivo=null;
        String path= PATH_BASE_DE_DATOS+"\\"+grupoUsuario+"\\" + archivoOriginal;
        Calendar c = Calendar.getInstance();
        //System.out.println("Current time => "+c.getTime());

        SimpleDateFormat df = new SimpleDateFormat("HH:mm:ss");
        String fecha = df.format(c.getTime());

        String comando;
        comando = "INSERT INTO \"MiTouch\".t_archivo_galeria (archg_path,archg_fecha_desde,archg_fecha_baja) VALUES ('"+path+"','"+fecha+"',"+null+") RETURNING archg_id;";
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
        baseDeDatos.execute(comando);
    }
    @NonNull
    private String obtenerArchivo() {
        return path.substring(path.lastIndexOf("/") + 1);
    }
    private String obtenerDirectorio(){
        String directorio="";
        String[] item =path.split("/");;

        for(int i = 1 ; i <item.length-1;i++){
            directorio=directorio+"/"+ item[i];
        }
        directorio=directorio+"/";
        return directorio;

    }
    private void prepareListData() {
        listDataHeader = new ArrayList<>();
        listIDHeader = new ArrayList<>();
        listDataChild = new HashMap<>();
        grupodeUsuario = new ArrayList<>();
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
                        listIDHeader.add(resultSet.getString(5));
                        listIDHeader.add(resultSet.getString(6));
                        grupodeUsuario.add(grupo);
                        grupodeUsuario.add(resultSet.getString(4));
                        aux=resultSet.getInt(1);
                    }
                    else
                    {
                        listDataChild.put(grupo, grupodeUsuario);
                        grupodeUsuario = new ArrayList<>();

                        grupo=resultSet.getString(2);
                        listDataHeader.add(grupo);
                        grupodeUsuario.add(grupo);
                        grupodeUsuario.add(resultSet.getString(4));
                        aux=resultSet.getInt(1);
                        listIDHeader.add(resultSet.getString(5));
                        listIDHeader.add(resultSet.getString(6));
                    }
                }
                else
                {
                    //System.out.println("Agregar usuario a la lista");
                    grupodeUsuario.add(resultSet.getString(4));
                    listIDHeader.add(resultSet.getString(6));
                }
            }

            listDataChild.put(grupo, grupodeUsuario);

        } catch (Exception e) {
            System.err.println("Error crear explist: " + e );
        }
    }
    @NonNull
    private Boolean buscarUsuario() {
        String comando = "";
        //System.out.println("el usuario es" + id_usuario);
        comando = "SELECT * FROM  \"MiTouch\".t_usuarios WHERE usu_id ="+ id_usuario +";";
        PostgrestBD baseDeDatos = new PostgrestBD();
        ResultSet resultSet = baseDeDatos.execute(comando);
        try{
            while (resultSet.next()) {
                //System.out.println("usuario: " + resultSet.getInt("usu_id"));
                return true;
            }
        }catch(Exception e){System.out.println("Error busqueda");}
        return false;
    }
    public void CrearDirectorio(){
        try
        {

            File mediaStorageDir = new File(Environment.getExternalStorageDirectory(), "MiTouchMultimedia");

            if (!mediaStorageDir.exists()) {
                if (!mediaStorageDir.mkdirs()) {
                    Log.d("App", "failed to create directory");
                }else{Log.d("App", "failed to create directory 2");}

            }
        }
        catch (Exception ex)
        {
            Log.e("Ficheros", "Error al escribir fichero a memoria interna");
        }
    }
    public static void copyFileOrDirectory(String srcDir, String dstDir) {
        try {
            File src = new File(srcDir);
            File dst = new File(dstDir, src.getName());

            //System.out.println(src.toString());
            //System.out.println(dstDir.toString());
            //System.out.println("so vo imagen ?" +src.getName());

            if (src.isDirectory()) {

                String files[] = src.list();
                int filesLength = files.length;
                for (int i = 0; i < filesLength; i++) {
                    String src1 = (new File(src, files[i]).getPath());
                    String dst1 = dst.getPath();
                    copyFileOrDirectory(src1, dst1);

                }
            } else {
                    copyFile(src, dst);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public static void copyFile(File sourceFile, File destFile) throws IOException {
        if (!destFile.getParentFile().exists())
            destFile.getParentFile().mkdirs();

        if (!destFile.exists()) {
            destFile.createNewFile();
        }

        FileChannel source = null;
        FileChannel destination = null;

        try {
            source = new FileInputStream(sourceFile).getChannel();
            destination = new FileOutputStream(destFile).getChannel();
            destination.transferFrom(source, 0, source.size());
        } finally {
            if (source != null) {
                source.close();
            }
            if (destination != null) {
                destination.close();
            }
        }
    }


}