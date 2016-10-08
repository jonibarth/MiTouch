package com.example.grupo110.mitouchmobile;


import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.Toast;


public class GaleriaActivity extends AppCompatActivity {
    private GridView gridView;
    private GridViewAdapter gridAdapter;
    private String nombre_usuario=null;
    public int id_usuario;
    List<String> listDataHeader;
    List<String> listIdHeader;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_galeria);
        id_usuario = getIntent().getExtras().getInt("id");

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(getResources().getDrawable(R.drawable.ic_previous));
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();

            }
        });

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent AgregarArchivoIntent = new Intent(GaleriaActivity.this, GalleryPage.class);
                AgregarArchivoIntent.putExtra("id",id_usuario);
                startActivity(AgregarArchivoIntent);
            }
        });


        gridView = (GridView) findViewById(R.id.gridView);
        CrearCarpetas();
        gridAdapter = new GridViewAdapter(this, R.layout.grid_item_layout, getData());
        gridView.setAdapter(gridAdapter);

        gridView.setOnItemClickListener(new OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
                ImageItem item = (ImageItem) parent.getItemAtPosition(position);
                //Create intent
                Intent siguiente = new Intent(GaleriaActivity.this, DetailsActivity.class);
                siguiente.putExtra("carpeta",listIdHeader.get(position));
                siguiente.putExtra("nombre_usuario",nombre_usuario);
                siguiente.putExtra("id",id_usuario);
                //Start details activity
                startActivity(siguiente);
            }
        });
    }

    private void CrearCarpetas() {
        listIdHeader = new ArrayList<>();
        listDataHeader = new ArrayList<>();
        listDataHeader.add("Carpeta Personal");
        listIdHeader.add("Carpeta Personal");
        String comando;
        comando = String.format("SELECT gru_id,gru_nombre " +
                "FROM \"MiTouch\".t_usuarios_grupo INNER JOIN \"MiTouch\".t_grupos ON ugru_id_grupo = gru_id " +
                "INNER JOIN \"MiTouch\".t_usuarios ON ugru_id_usuario = usu_id " +
                "WHERE  usu_id =" + id_usuario +";");

        PostgrestBD baseDeDatos = new PostgrestBD();
        ResultSet resultSet = baseDeDatos.execute(comando);
        try {
            while (resultSet.next()) {
                listDataHeader.add(resultSet.getString("gru_nombre"));
                listIdHeader.add("" +resultSet.getInt("gru_id"));
            }
        } catch (Exception e) {System.out.println("ERror Crear Carpetas: " + e);
        }
    }

    /**
     * Prepare some dummy data for gridview
     */
    private ArrayList<ImageItem> getData() {
        final ArrayList<ImageItem> imageItems = new ArrayList<>();
        for (int i = 0; i < listDataHeader.size(); i++) {
            System.out.println("carpetas:" +listDataHeader.get(i).toString());
            Bitmap item = BitmapFactory.decodeResource(getApplicationContext().getResources(),
                    R.drawable.folder);
            imageItems.add(new ImageItem(item, listDataHeader.get(i).toString() ));
        }
        return imageItems;
    }
}