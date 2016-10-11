package com.example.grupo110.mitouchmobile.galeria;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ExpandableListView;
import android.widget.Toast;

import com.example.grupo110.mitouchmobile.expandable_list.ExpandableListAdapter;
import com.example.grupo110.mitouchmobile.base_de_datos.PostgrestBD;
import com.example.grupo110.mitouchmobile.R;
import com.example.grupo110.mitouchmobile.comunicacion_servidor.SFTClienteUploadNoTengoArchivoLocal;
import com.example.grupo110.mitouchmobile.comunicacion_servidor.SFTClienteUploadFileFromGallery;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.sql.ResultSet;
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
    String archivoOriginal=null;
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
    String id_carpeta=null;
    final String PATH_BASE_DE_DATOS = "/home/toor/galerias";
    final String PATH_MOBILE = "/storage/sdcard0/MiTouchMultimedia";
    static Context context;
    private ProgressDialog progress;

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
               // System.out.println("El grupo de uuario es: "+ grupoUsuario);

                id_carpeta=listDataID.get(
                        listDataHeader.get(groupPosition)).get(
                        childPosition);

                System.out.println("id_carpeta " +id_carpeta);

                archivoOriginal = obtenerArchivo();

                String directorio = obtenerDirectorio();
                Toast toast;
                if(!ArchivoExiteEnBD()){
                    String PuedoEscribir = listDataEscribir.get(listDataHeader.get(groupPosition)).get(childPosition);
                    crearArchivo(PuedoEscribir);
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

    private void crearArchivo(String puedoEscribir) {
        ActualizarBaseDeDatos();
        System.out.println("el grupo de usuario es: " + grupoUsuario);
        System.out.println("el path es: " + path);
        System.out.println("A que grupo de usuario le voy a compartir: " + grupoUsuario);
        System.out.println("Que archivo le voy a compartir: " + path);


        if(!ArchivoExiste()) {
/*
* Me falta hacer esta parte!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
 */
            /*progress = new ProgressDialog(this, R.style.MyTheme);
            progress.setMessage("Descargando..");
            String[] separated = path.split("/");
             // this will contain "Fruit"
            System.out.println("Grupo de usuario que soy! " + separated[4]);
            new SFTClienteDownloadFile(progress, this, separated[4], path.substring(path.lastIndexOf("/") + 1), getApplicationContext()).execute();
*/
            String[] separated = path.split("/");
            new SFTClienteUploadNoTengoArchivoLocal(progress, this, grupoUsuario, path.substring(path.lastIndexOf("/") + 1), separated[4] ,getApplicationContext()).execute();
        }
        else {

            progress = new ProgressDialog(this, R.style.MyTheme);
            progress.setMessage("Compartiendo..");
            new SFTClienteUploadFileFromGallery(progress, this, grupoUsuario, path, getApplicationContext()).execute();
            if (puedoEscribir.equals("true")) {
                CrearDirectorio();
                copyFileOrDirectory(path, PATH_MOBILE + "/" + grupoUsuario);
            }
        }


    }

    private boolean ArchivoExiste() {
        String pathAbrir = PATH_MOBILE + "/" + grupoUsuario + "/" + path.substring(path.lastIndexOf("/") + 1);
        String[] separated = path.split("/");
        System.out.println("El path a abrir es: " + separated[4]);
        System.out.println("El path a abrir es: " + path.substring(path.lastIndexOf("/") + 1));
        File file = new File(pathAbrir);
        File file2 = new File(path);
        if (file.exists() || file2.exists())
            return true;
        else
            return false;
    }
    private boolean ArchivoExiteEnBD() {
        String pathAVerificar =PATH_BASE_DE_DATOS+"/"+grupoUsuario+"/"+archivoOriginal;
        String comando;
        comando = "SELECT archg_path FROM \"MiTouch\".t_archivo_galeria WHERE archg_path ='"+pathAVerificar+"' OR archg_fecha_baja IS NOT NULL;";
        PostgrestBD baseDeDatos = new PostgrestBD();
        ResultSet resultSet = baseDeDatos.execute(comando);
        try{
            if (resultSet.next())
                return true;
        }catch(Exception e){System.out.println("Error busqueda:" + e);}
        return false;
    }
    private void ActualizarBaseDeDatos() {
        // Crear Registro en la tabla de archivos
        String id_archivo=null;
        String path= PATH_BASE_DE_DATOS+"/"+grupoUsuario+"/" + archivoOriginal;
        Calendar c = Calendar.getInstance();
        SimpleDateFormat df = new SimpleDateFormat("HH:mm:ss");
        String fecha = df.format(c.getTime());
        String comando;
        comando = "INSERT INTO \"MiTouch\".t_archivo_galeria (archg_path,archg_fecha_desde,archg_fecha_baja) VALUES ('"+path+"','"+fecha+"',"+null+") RETURNING archg_id;";
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
    @NonNull
    private String obtenerArchivo() {
        return path.substring(path.lastIndexOf("/") + 1);
    }
    private String obtenerDirectorio(){
        String directorio="";
        String[] item =path.split("/");
        for(int i = 1 ; i <item.length-1;i++){
            directorio=directorio+"/"+ item[i];
        }
        directorio=directorio+"/";
        return directorio;
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

        comando = "SELECT ugru_id_grupo, gru_nombre ,ugru_id_usuario, usu_nombre_usuario, gru_id_galeria, usu_id_galeria " +
                "FROM tablaVista NATURAL JOIN \"MiTouch\".t_usuarios_grupo " +
                "INNER JOIN \"MiTouch\".t_usuarios ON ugru_id_usuario = usu_id " +
                "INNER JOIN \"MiTouch\".t_grupos ON ugru_id_grupo = gru_id " +
                "WHERE  usu_id<>"+id_usuario+" "+
                "ORDER BY ugru_id_grupo, gru_nombre ,ugru_id_usuario, usu_nombre_usuario;";
        baseDeDatos = new PostgrestBD();
        resultSet = baseDeDatos.execute(comando);
        System.out.println("El id de la galeria personal es: " + id_carpetausuario);

        grupo="Carpeta Personal";

        listDataHeader.add(grupo);
        grupodeUsuario.add(usuario);
        grupodeUsuarioID.add(id_carpetausuario+"");
        grupodeUsuarioEscribir.add("true");

        listDataChild.put(grupo, grupodeUsuario);
        listDataID.put(grupo, grupodeUsuarioID);
        listDataEscribir.put(grupo, grupodeUsuarioEscribir);
        grupodeUsuario = new ArrayList<>();
        grupodeUsuarioID = new ArrayList<>();
        grupodeUsuarioEscribir = new ArrayList<>();


        try {
            while (resultSet.next()) {
                if(aux != resultSet.getInt(1)){
                    System.out.println("Aux: " + aux + " != " +resultSet.getInt(1));
                    if(aux == -1)
                    {
                        System.out.println("Aux: " + aux + " != " +resultSet.getInt(1) + " --> Entro al if");
                            grupo=resultSet.getString(2);
                            listDataHeader.add(grupo);

                            grupodeUsuario.add(grupo);
                            grupodeUsuarioID.add(resultSet.getString("gru_id_galeria"));
                            grupodeUsuarioEscribir.add("true");


                            grupodeUsuario.add(resultSet.getString("usu_nombre_usuario"));
                            grupodeUsuarioID.add(resultSet.getString("usu_id_galeria"));
                        if (resultSet.getString("ugru_id_usuario").equals(id_usuario))
                            grupodeUsuarioEscribir.add("true");
                        else
                            grupodeUsuarioEscribir.add("false");


                        System.out.println("inicio tercer subconjunto");
                        System.out.println("listDataHeader: " + grupo);
                        System.out.println("grupodeUsuario: " + resultSet.getString("usu_nombre_usuario"));
                        System.out.println("grupodeUsuarioID: " + resultSet.getString("usu_id_galeria"));
                        System.out.println("fin tercer subconjunto");

                        aux = resultSet.getInt(1);

                    }
                    else
                    {
                        System.out.println("Aux: " + aux + " != " +resultSet.getInt(1) + " --> Entro al else");

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


                        grupodeUsuario.add(resultSet.getString(4));
                        grupodeUsuarioID.add(resultSet.getString("usu_id_galeria"));
                        if(resultSet.getInt("ugru_id_usuario")==id_usuario)
                            grupodeUsuarioEscribir.add("true");
                        else
                            grupodeUsuarioEscribir.add("false");

                        aux = resultSet.getInt(1);

                    }
                }
                else
                {
                        grupodeUsuario.add(resultSet.getString("usu_nombre_usuario"));
                        grupodeUsuarioID.add(resultSet.getString(6));
                        if (resultSet.getString("ugru_id_usuario").equals(id_usuario))
                            grupodeUsuarioEscribir.add("true");
                        else
                            grupodeUsuarioEscribir.add("false");

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
    public void CrearDirectorio(){
        System.out.println("Cree el directorio!");
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
        System.out.println("Cree el archivo: copyFileOrDirectory!");
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
        System.out.println("Cree el archivo: copyFile!");
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