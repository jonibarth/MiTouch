package com.example.grupo110.mitouchmobile;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.sql.ResultSet;
import java.util.Iterator;
import java.util.Set;

public class Compartir2 extends AppCompatActivity {

    int id_usuario=-1;
    Uri uri;
    String todo = null;
    String url=null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.humo);
        //dumpIntent(getIntent());

        Uri uri = (Uri) getIntent().getExtras().get(Intent.EXTRA_STREAM);
        url = uri.toString();
        System.out.println("uri: "+uri);
        System.out.println("texto: "+url);

        String[] archivos = fileList();
        if (existe(archivos, "notas.txt"))
            try {
                InputStreamReader archivo = new InputStreamReader(
                        openFileInput("notas.txt"));
                BufferedReader br = new BufferedReader(archivo);
                String linea = br.readLine();
                todo =linea;
                br.close();
                archivo.close();

                System.out.println("usuario que encontre: " + todo);
                abrirIntent();
            } catch (Exception e) {
                System.out.println("error en el try que esta dentro del if");
            }
        else{
            Toast toast=Toast.makeText(getApplicationContext(),"El usuario no esta logueado", Toast.LENGTH_LONG);
            toast.show();
            Intent mainIntent = new Intent().setClass(Compartir2.this, LoginActivity.class);
            mainIntent.putExtra("url",url);
            startActivity(mainIntent);
            finish();
        }
    }

    private void abrirIntent() {

        if(!todo.equals("null")) {
            todo.trim();
            id_usuario = Integer.parseInt(todo);
            if (buscarUsuario()) {
                Intent mainIntent = new Intent().setClass(Compartir2.this, CompartirActivity2.class);
                mainIntent.putExtra("id",id_usuario);
                mainIntent.putExtra("url",url);
                startActivity(mainIntent);
                finish();
            }
            else
            {
                Toast toast=Toast.makeText(getApplicationContext(),"Error con el usuario", Toast.LENGTH_LONG);
                toast.show();
                grabar();
            }
        }
        else
        {
            Toast toast=Toast.makeText(getApplicationContext(),"El usuario no esta logueado", Toast.LENGTH_LONG);
            toast.show();
            Intent mainIntent = new Intent().setClass(Compartir2.this, LoginActivity.class);
            mainIntent.putExtra("url",url);
            startActivity(mainIntent);
            finish();
        }
    }

    private boolean existe(String[] archivos, String archbusca) {
        for (int f = 0; f < archivos.length; f++)
            if (archbusca.equals(archivos[f]))
                return true;
        return false;
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

    public void grabar() {
        try {
            OutputStreamWriter archivo = new OutputStreamWriter(openFileOutput(
                    "notas.txt", LoginActivity.MODE_PRIVATE));
            archivo.write("null");
            archivo.flush();
            archivo.close();
        } catch (Exception e) {System.out.println("Error grabar archivo");
        }
        Toast t = Toast.makeText(this, "Los datos fueron grabados",
                Toast.LENGTH_SHORT);
        t.show();
    }
}