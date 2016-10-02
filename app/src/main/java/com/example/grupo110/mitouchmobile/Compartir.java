package com.example.grupo110.mitouchmobile;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.sql.ResultSet;
import java.util.Iterator;
import java.util.Set;

public class Compartir extends AppCompatActivity {

    int id_usuario=-1;
    Uri uri;
    String todo = null;
    String url=null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.humo);
        dumpIntent(getIntent());

        try {
            // Si vengo de la galeria
            Uri uri = (Uri) getIntent().getExtras().get(Intent.EXTRA_STREAM);
            url = getRealPathFromURI(getApplicationContext(), uri);
            System.out.println("la url es: " + url);
        }catch (Exception e){
            try{
                // Si vengo del file manager
                Uri uri = (Uri) getIntent().getExtras().get(Intent.EXTRA_STREAM);
                url = uri.toString();
                url = url.replaceAll("file://","");
                System.out.println("la url es: " + url);
            }catch(Exception ex)
            {
                try{
                    System.out.print("sdfsdf");
                }catch (Exception ee ){System.out.println("Error: " + ee);}
                System.out.println("Error "+ ex);
            }

            System.out.print("salte el try!!! "+ url);
        }
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
                abrirIntent();
            } catch (Exception e) {
                System.out.println("error en el try que esta dentro del if");
            }
        else{
            Toast toast=Toast.makeText(getApplicationContext(),"El usuario no esta logueado", Toast.LENGTH_LONG);
            toast.show();
            Intent mainIntent = new Intent().setClass(Compartir.this, LoginActivity.class);
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
                Intent mainIntent = new Intent().setClass(Compartir.this, CompartirActivity.class);
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
            Intent mainIntent = new Intent().setClass(Compartir.this, LoginActivity.class);
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
        comando = String.format("SELECT * FROM  \"MiTouch\".t_usuarios WHERE usu_id ="+ id_usuario +";");
        PostgrestBD baseDeDatos = new PostgrestBD();
        ResultSet resultSet = baseDeDatos.execute(comando);
        try{
            while (resultSet.next()) {
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
// Metodo que transforma uri en path!!
    public String getRealPathFromURI(Context context, Uri contentUri) {
        Cursor cursor = null;
        try {
            String[] proj = { MediaStore.Images.Media.DATA };
            cursor = context.getContentResolver().query(contentUri,  proj, null, null, null);
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            return cursor.getString(column_index);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }



}