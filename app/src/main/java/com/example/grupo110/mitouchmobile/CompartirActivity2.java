package com.example.grupo110.mitouchmobile;

import android.os.Bundle;
import android.os.Environment;
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

public class CompartirActivity2 extends AppCompatActivity {

    int id_usuario=-1;
    String path=null;
    ExpandableListAdapter listAdapter;
    ExpandableListView expListView;
    List<String> listDataHeader;
    List<String> listIDHeader;
    HashMap<String, List<String>> listDataChild;
    List<String> grupodeUsuario;
    String grupoUsuario=null;
    final String PATH_BASE_DE_DATOS = "C:\\Program Files\\MiTouch";
    final String PATH_MOBILE = "/storage/sdcard0/MiTouchMultimedia";


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
                Toast toast = Toast.makeText(getApplicationContext(),"soy: "+id_usuario +"usuario: "+grupoUsuario+"imagen: "+ path ,Toast.LENGTH_LONG);
                toast.show();
                CrearDirectorio();
                CopiarAInternalStorage();

                return false;
            }
        });
    }

    // http://blog.openalfa.com/como-cambiar-de-nombre-mover-o-copiar-un-fichero-en-javaç
    // http://es.stackoverflow.com/questions/4225/error-en-metodo-al-mover-archivos-de-un-directorio-a-otro
    private void CopiarAInternalStorage(){

        // obtengo el nombre del archivo que queiro copiar
        String archivoOriginal = obtenerArchivo();
        // obtengo el directorio del archivo que quiero copiar
        String directorio = obtenerDirectorio();
        System.out.println("path original: " + path);
        System.out.println("directorio original: " + directorio);
        System.out.println("Image name: " + archivoOriginal);
        System.out.println("path destino: ");
        copyFileOrDirectory(path,PATH_MOBILE);
        ActualizarBaseDeDatos();

    }

    private void ActualizarBaseDeDatos() {

        // Crear Registro en la tabla de archivos
        String path= PATH_BASE_DE_DATOS+"\\"+grupoUsuario+"\\";
        Calendar c = Calendar.getInstance();
        System.out.println("Current time => "+c.getTime());

        SimpleDateFormat df = new SimpleDateFormat("HH:mm:ss");
        String fecha = df.format(c.getTime());

        String comando;
        comando = "INSERT INTO \"MiTouch\".t_archivo_galeria (archg_path,archg_fecha_desde,archg_fecha_baja) VALUES ('"+path+"','"+fecha+"',"+null+");";
        PostgrestBD baseDeDatos = new PostgrestBD();
        baseDeDatos.execute(comando);
        System.out.println("Archivo Creado");

        // Asociar Carpeta con el archivo
        //comando = "INSERT INTO \"MiTouch\".t_carpeta_archivos_galeria (cag_id_carpeta,cag_id_archivo) VALUES ('"+path+"','"+path+"','"+fecha+"',"+null+");";
        //baseDeDatos.execute(comando);
        //System.out.println("archivo asociado a carpeta");
    }

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
                        listIDHeader.add(grupo);
                        listIDHeader.add(resultSet.getString(4));
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
                        listIDHeader.add(grupo);
                        listIDHeader.add(resultSet.getString(4));
                    }
                }
                else
                {
                    //System.out.println("Agregar usuario a la lista");
                    grupodeUsuario.add(resultSet.getString(4));
                    listIDHeader.add(resultSet.getString(4));
                }
            }

            listDataChild.put(grupo, grupodeUsuario);

        } catch (Exception e) {
            System.err.println("Error crear explist: " + e );
        }
    }

    private Boolean buscarUsuario() {
        String comando = "";
        System.out.println("el usuario es" + id_usuario);
        comando = "SELECT * FROM  \"MiTouch\".t_usuarios WHERE usu_id ="+ id_usuario +";";
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