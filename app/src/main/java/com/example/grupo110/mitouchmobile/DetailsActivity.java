package com.example.grupo110.mitouchmobile;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.view.ContextThemeWrapper;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.Toast;

import java.io.File;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DetailsActivity extends AppCompatActivity {
    private GridView gridView;
    private GridViewAdapter gridAdapter;
    public int id_usuario;
    public int id_grupo=-1;
    public int id_carpeta=-1;
    public String path="";
    public String carpeta;
    List<String> listArchivosCompletos;
    List<String> listArchivos;
    List<String> listExtenciones;
    List<String> listRuta;

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
        System.out.println("usuario: " + id_usuario);
        System.out.println("Carpeta: " + carpeta);

        listArchivosCompletos = new ArrayList<>();
        listArchivos = new ArrayList<>();
        listRuta = new ArrayList<>();
        listExtenciones = new ArrayList<>();

        if(!carpeta.equals("Carpeta Personal")) {
            id_grupo = Integer.parseInt(carpeta);
            buscarpathCarpetaGrupoUsuario();
            buscarArchivos();
            descomponerArchivos();
        }
        else {
            buscarPathCarpetaPersonal();
            System.out.println("carpeta Personal id: "+ id_carpeta);
            System.out.println("carpeta Personal path: "+ path);
            buscarArchivos();
            for (int i=0; i <listArchivos.size();i++)
                System.out.println("archivo: " +listArchivos.get(i));
            descomponerArchivos();

        }


        gridView = (GridView) findViewById(R.id.gridView);
        gridAdapter = new GridViewAdapter(this, R.layout.grid_item_layout, getData());
        gridView.setAdapter(gridAdapter);

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
                ImageItem item = (ImageItem) parent.getItemAtPosition(position);
                System.out.println("Archivo que deseo abrir: " + listArchivosCompletos.get(position));


                AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(DetailsActivity.this, R.style.AlertDialogCustom));
                builder.setTitle("Acción a realizar")
                        .setItems(new String [] {"Abrir","Compartir","Eliminar","Cerrar"}, new DialogInterface.OnClickListener(){
                            public void onClick(DialogInterface dialog, int opt){
                                System.out.println("Opción elegida: "+ opt);
                                switch (opt){
                                    case 0:  System.out.println("Archivo que deseo abrir: " );
                                        break;
                                    case 1:  System.out.println("Archivo que deseo compartir: " );
                                        break;
                                    case 2:  System.out.println("Archivo que deseo eliminar: " );
                                        borrarArchivo();
                                        break;
                                    case 3:  System.out.println("Salir " );
                                        break;
                                }
                            }
                        });

                builder.show();
            }
        });
    }

    private void borrarArchivo() {
        // Borrar de la base de datos, Tabla t_archivos_galeria y t_carpetas_archivos_galeria


        //borrar de la carpeta MiTouchMultimedia en el dispositivos android




    }

    private void descomponerArchivos() {
        String auxiliar="";
        for (int i=0; i <listArchivosCompletos.size();i++) {
            System.out.println("archivo: " + listArchivosCompletos.get(i));
            auxiliar=listArchivosCompletos.get(i);
            listExtenciones.add(auxiliar.substring(auxiliar.lastIndexOf(".") + 1));
        }

        for (int i=0; i <listArchivosCompletos.size();i++) {
            System.out.println("archivo: " + listArchivosCompletos.get(i));
            System.out.println("archivo: " + listExtenciones.get(i));
        }
    }


    private void  buscarPathCarpetaPersonal() {
        String comando;
        comando = String.format("SELECT cg_id,cg_path " +
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
            }
        }catch (Exception e) {System.out.println("Error Crear Carpetas: " + e);
        }
    }


    private void buscarArchivos() {
        String comando;
        comando = String.format("SELECT archg_path " +
                "FROM \"MiTouch\".t_carpeta_archivos_galeria INNER JOIN \"MiTouch\".t_archivo_galeria ON archg_id=cag_id_archivo " +
                " WHERE  cag_id_carpeta =" + id_carpeta+";");

        PostgrestBD baseDeDatos = new PostgrestBD();
        ResultSet resultSet = baseDeDatos.execute(comando);
        try {
            while (resultSet.next()) {
                listArchivosCompletos.add(resultSet.getString("archg_path"));
            }
        }catch (Exception e) {System.out.println("Error Crear Carpetas: " + e);
        }
    }

    private void buscarpathCarpetaGrupoUsuario() {
        String comando;
        comando = String.format("SELECT cg_id,cg_path " +
                "FROM \"MiTouch\".t_carpetas_galeria INNER JOIN \"MiTouch\".t_grupos ON cg_id = gru_id_galeria " +
                "WHERE  gru_id =" + id_grupo +";");

        PostgrestBD baseDeDatos = new PostgrestBD();
        ResultSet resultSet = baseDeDatos.execute(comando);
        try {
            while (resultSet.next()) {
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
            imageItems.add(new ImageItem(item, listArchivosCompletos.get(i) ));
        }
            else
        if(esunVideo(listExtenciones.get(i)))
        {
            Bitmap item = BitmapFactory.decodeResource(getApplicationContext().getResources(),
                    R.drawable.image_2);
            imageItems.add(new ImageItem(item, listArchivosCompletos.get(i) ));
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
