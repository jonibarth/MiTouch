package com.example.grupo110.mitouchmobile;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.view.ContextThemeWrapper;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.Toast;


import com.google.api.client.util.Sleeper;

import java.io.File;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadFactory;

public class DetailsActivity extends AppCompatActivity {
    private GridView gridView;
    private GridViewAdapter gridAdapter;
    public int id_usuario; // Guardo el id del usuario que esta conectado
    public int posicionAprentada; // Cuando apreto sobre un archivo multimedia me devuelve una posicion, aca la guardo
    public int id_grupo=-1;  // Guardo el id de la carpeta en caso que la misma sea de un grupo
    public int id_carpeta=-1; // Guardo el id de la carpeta que voy a entrar o entre
    public String path="";
    public String carpeta; // Aca me viene el id de la carpeta que abri, cuando hago el intent
    public String nombre_carpeta=null; // guardo el nombre de la carpeta
    List<String> listArchivosCompletos; // Guardo el path de los archivos que hay en esa carpeta
    List<String> listIDArchivosCompletos; // Guardo el id de los archivos que hay en esa carpeta
    List<String> listExtenciones; // Guardo la extencion de los archivos que hay en esa carpeta
    List<String> listNombreArchivos; // Guardo el nomre de los archivos que hay en esa carpeta
    final String PATH_MOBILE = "/storage/sdcard0/MiTouchMultimedia";
    String nombreArchivo; // nombre del archivo que quiero abrir compartir o eliminar
    private ProgressDialog progress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {


        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detalles_galeria);
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
        carpeta = getIntent().getExtras().getString("carpeta");

        listArchivosCompletos = new ArrayList<>();
        listIDArchivosCompletos = new ArrayList<>();
        listExtenciones = new ArrayList<>();
        listNombreArchivos = new ArrayList<>();

        if(!carpeta.equals("Carpeta Personal")) {
            id_grupo = Integer.parseInt(carpeta);
            System.out.println("el id que voy a buscar es : " + id_grupo);
            buscarpathCarpetaGrupoUsuario();
            buscarArchivos();
            descomponerArchivos();
        }
        else {
            buscarPathCarpetaPersonal();
            System.out.println("carpeta Personal id: "+ id_carpeta);
            System.out.println("carpeta Personal path: "+ path);
            buscarArchivos();
            descomponerArchivos();
        }


        gridView = (GridView) findViewById(R.id.gridView);
        gridAdapter = new GridViewAdapter(this, R.layout.grid_item_layout, getData());
        gridView.setAdapter(gridAdapter);

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
                ImageItem item = (ImageItem) parent.getItemAtPosition(position);

                posicionAprentada = position;

                final AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(DetailsActivity.this, R.style.AlertDialogCustom));
                builder.setTitle("Acción a realizar")
                        .setItems(new String [] {"Abrir","Compartir","Eliminar","Cerrar"}, new DialogInterface.OnClickListener(){
                            public void onClick(DialogInterface dialog, int opt){
                                System.out.println("Opción elegida: "+ opt);
                                switch (opt){
                                    case 0:  System.out.println("Archivo que deseo abrir: "  +listArchivosCompletos.get(posicionAprentada) + listIDArchivosCompletos.get(posicionAprentada) );
                                        abrirArchivo(listIDArchivosCompletos.get(posicionAprentada));
                                        break;
                                    case 1:  System.out.println("Archivo que deseo compartir: " +  listArchivosCompletos.get(posicionAprentada));
                                        compartirArchivo(listIDArchivosCompletos.get(posicionAprentada));
                                        break;
                                    case 2:  System.out.println("Archivo que deseo eliminar: " +  listArchivosCompletos.get(posicionAprentada));
                                        borrarArchivo(listIDArchivosCompletos.get(posicionAprentada));
                                        finish();
                                        break;
                                    case 3:  System.out.println("Salir " );
                                        break;
                                }
                            }
                        });
                builder.show();
            }

        });

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent AgregarArchivoIntent = new Intent(DetailsActivity.this, GalleryPage.class);
                System.out.println(id_usuario +"---"+carpeta);
                AgregarArchivoIntent.putExtra("id",id_usuario);
                AgregarArchivoIntent.putExtra("carpeta",carpeta);
                startActivity(AgregarArchivoIntent);
            }
        });

    }

    private void compartirArchivo(String idCompartir) {
        String pathAbrir;
        pathAbrir =PATH_MOBILE+"/"+nombre_carpeta+"/"+listNombreArchivos.get(posicionAprentada);
        System.out.println("El archivo que quiero compartir es: " + PATH_MOBILE);
        System.out.println("El archivo que quiero compartir es: " + nombre_carpeta);
        System.out.println("El archivo que quiero compartir es: " + listNombreArchivos.get(posicionAprentada));
        System.out.println("El archivo que quiero compartir es: " + pathAbrir);

        Intent intent = new Intent(DetailsActivity.this,CompartirActivity.class);
        intent.putExtra("id",id_usuario);
        intent.putExtra("url",pathAbrir);
        startActivity(intent);


    }

    private void abrirArchivo(String idAbrir) {
        String pathAbrir;
        String auxiliar = listNombreArchivos.get(posicionAprentada);
        pathAbrir =PATH_MOBILE+"/"+nombre_carpeta+"/"+auxiliar;
        System.out.println(pathAbrir);
        if(ArchivoExiste()){
            Intent intent = new Intent();
            intent.setAction(Intent.ACTION_VIEW);

            if(esunaImagen(auxiliar.substring(auxiliar.lastIndexOf(".") + 1)))
                intent.setDataAndType(Uri.fromFile(new File(pathAbrir)), "image/*");
            else
                intent.setDataAndType(Uri.fromFile(new File(pathAbrir)), "video/*");
            startActivity(intent);
        }else
        {
            // nombre del archivo
            // carpeta del archivo
            progress = new ProgressDialog(this, R.style.MyTheme);
                new SFTClienteDownloadFile(progress, this, nombre_carpeta, auxiliar, getApplicationContext()).execute();
        }
    }

    private boolean ArchivoExiste() {

        String pathAbrir= PATH_MOBILE+"/"+nombre_carpeta+"/"+listNombreArchivos.get(posicionAprentada);
        System.out.println("El path a abrir es: " + pathAbrir);
        File file = new File(pathAbrir);
        if(file.exists())
            return true;
        else
            return false;
// Do something else.
    }

    // Este metodo sirve para descargar el archivo que se encuentra en el servidor!!!
    private void descargarArchivo() {
        System.out.println("Descargando archivo...");
    }


    private void borrarArchivo(String idEliminar) {

        // borro fila en t_carpeta_archivo que el cag_id_archivo sea = idEliminar
        String comando;
        comando = String.format("DELETE " +
                "FROM \"MiTouch\".t_carpeta_archivos_galeria " +
                "WHERE  cag_id_archivo =" + idEliminar +";");
        PostgrestBD baseDeDatos = new PostgrestBD();
        baseDeDatos.execute(comando);
        // borro fila en t_archivo archg_id que el archg_id sea = idEliminar
        comando = String.format("DELETE " +
                "FROM \"MiTouch\".t_archivo_galeria " +
                "WHERE  archg_id =" + idEliminar +";");
        baseDeDatos.execute(comando);
        // busco nombre archivo, borrar esta en la carpeta MiTouchMultimedia, lo borro

        nombreArchivo=listNombreArchivos.get(posicionAprentada);
        String pArchivo = PATH_MOBILE + "/" +nombre_carpeta+ "/" + nombreArchivo;
        try {
            File fichero = new File(pArchivo);
            if (!fichero.delete())
                throw new Exception("El fichero " + pArchivo
                        + " no puede ser borrado!");
        } catch (Exception e) {
            System.out.println("Error Borrar Archivo!: " +e);
        } // end try
        // end Eliminar
        System.out.println("Borrar Archivo Exitoso ");
        //borrar de la carpeta MiTouchMultimedia en el dispositivos android


        new SFTClienteDeleteFile(nombre_carpeta, nombreArchivo).execute();


    }

    private void descomponerArchivos() {
        String auxiliar="";
        for (int i=0; i <listArchivosCompletos.size();i++) {
            System.out.println("archivo: " + listArchivosCompletos.get(i));

            auxiliar=listArchivosCompletos.get(i);

            listExtenciones.add(auxiliar.substring(auxiliar.lastIndexOf(".") + 1));
            listNombreArchivos.add(auxiliar.substring(auxiliar.lastIndexOf("/") + 1));

        }

        for (int i=0; i <listArchivosCompletos.size();i++) {
            System.out.println("archivo: " + listArchivosCompletos.get(i));
            System.out.println("archivo: " + listExtenciones.get(i));
            System.out.println("archivo: " + listNombreArchivos.get(i));
        }
    }


    private void  buscarPathCarpetaPersonal() {
        String comando;
        comando = String.format("SELECT cg_id,cg_path,usu_nombre_usuario " +
                "FROM \"MiTouch\".t_carpetas_galeria INNER JOIN \"MiTouch\".t_usuarios ON cg_id = usu_id_galeria " +
                "WHERE  usu_id =" + id_usuario +";");

        PostgrestBD baseDeDatos = new PostgrestBD();
        ResultSet resultSet = baseDeDatos.execute(comando);
        try {
            while (resultSet.next()) {
                id_carpeta=resultSet.getInt("cg_id");
                System.out.println("id Carpeta: "+id_carpeta);
                path =resultSet.getString("cg_path");
                System.out.println("path carpeta: " + path);
                nombre_carpeta=resultSet.getString("usu_nombre_usuario");
            }
        }catch (Exception e) {System.out.println("Error Crear Carpetas: " + e);
        }
    }


    private void buscarArchivos() {
        String comando;
        comando = String.format("SELECT archg_path,archg_id " +
                "FROM \"MiTouch\".t_carpeta_archivos_galeria INNER JOIN \"MiTouch\".t_archivo_galeria ON archg_id=cag_id_archivo " +
                " WHERE  cag_id_carpeta =" + id_carpeta+";");

        PostgrestBD baseDeDatos = new PostgrestBD();
        ResultSet resultSet = baseDeDatos.execute(comando);
        try {
            while (resultSet.next()) {
                System.out.println("archivo leido: " + resultSet.getString("archg_path"));
                listArchivosCompletos.add(resultSet.getString("archg_path"));
                listIDArchivosCompletos.add(resultSet.getString("archg_id"));
            }
        }catch (Exception e) {System.out.println("Error Crear Carpetas: " + e);
        }
    }

    private void buscarpathCarpetaGrupoUsuario() {
        String comando;
        comando = String.format("SELECT cg_id,cg_path,gru_nombre " +
                "FROM \"MiTouch\".t_carpetas_galeria INNER JOIN \"MiTouch\".t_grupos ON cg_id = gru_id_galeria " +
                "WHERE  gru_id =" + id_grupo +";");

        PostgrestBD baseDeDatos = new PostgrestBD();
        ResultSet resultSet = baseDeDatos.execute(comando);
        try {
            while (resultSet.next()) {
                nombre_carpeta=resultSet.getString("gru_nombre");
                id_carpeta=resultSet.getInt("cg_id");
                System.out.println("id Carpeta: "+id_carpeta);
                path =resultSet.getString("cg_path");
                System.out.println("path carpeta: " + path);
            }
        }catch (Exception e) {System.out.println("Error Crear Carpetas: " + e);
        }
    }


    /**
     * Prepare some dummy data for gridview
     */
    private ArrayList<ImageItem> getData() {
        final ArrayList<ImageItem> imageItems = new ArrayList<>();

        for (int i = 0; i < listArchivosCompletos.size(); i++) {
            System.out.println("Archivos:" +listArchivosCompletos.get(i));

        if(esunaImagen(listExtenciones.get(i)))
        {
            Bitmap item = BitmapFactory.decodeResource(getApplicationContext().getResources(),
                    R.drawable.image_1);
            imageItems.add(new ImageItem(item, listNombreArchivos.get(i) ));
        }
            else
        if(esunVideo(listExtenciones.get(i)))
        {
            Bitmap item = BitmapFactory.decodeResource(getApplicationContext().getResources(),
                    R.drawable.image_2);
            imageItems.add(new ImageItem(item, listNombreArchivos.get(i) ));
        }
            else {System.out.println("error");}

        }
        return imageItems;
    }


    private boolean esunaImagen(String imagen){

        if(imagen.equals("jpg")||imagen.equals("jpeg")||imagen.equals("png")||imagen.equals("gif")||imagen.equals("bmp"))
            return true;
        return false;
    }

    private boolean esunVideo(String imagen){

        if(imagen.equals("mp4")||imagen.equals("avi")||imagen.equals("3gp"))
            return true;
        return false;
    }

}
