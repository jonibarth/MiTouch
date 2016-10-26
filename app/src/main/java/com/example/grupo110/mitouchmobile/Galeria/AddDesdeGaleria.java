package com.example.grupo110.mitouchmobile.galeria;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.util.Log;
import android.widget.Toast;

import com.example.grupo110.mitouchmobile.base_de_datos.PostgrestBD;
import com.example.grupo110.mitouchmobile.R;
import com.example.grupo110.mitouchmobile.comunicacion_servidor.SFTClienteUploadFileFromGalleriaMiTouch;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.sql.ResultSet;
import java.text.SimpleDateFormat;
import java.util.Calendar;


public class AddDesdeGaleria extends Activity{

    private static final int SELECT_PICTURE = 1;
    String imgDecodableString;
    int id_usuario;
    int id_carpeta;
    String id_archivo;
    String archivoOriginal;
    String nombre_carpeta;
    final String PATH_BASE_DE_DATOS = "/home/toor/galerias";
    final String PATH_MOBILE = "/storage/sdcard0/MiTouchMultimedia";
    private String carpeta;
    private ProgressDialog progress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        /*
        * Le asigno un layout para poder poner el progress dialog
         */
        setContentView(R.layout.humo);
        /*
        * Lo que recibo es el id del usuario que esta conectado
        * Lo que recibo es el id de la carpeta, en caso de ser una carpeta compartida,
        * En caso de ser la carpeta personal, recibo Carpeta Personal
         */

        id_usuario = getIntent().getExtras().getInt("id");
        carpeta = getIntent().getExtras().getString("carpeta");

        System.out.println("onCreate Add desde galeria: id: " +id_usuario + "carpeta: " + carpeta);

        /*
        *
        * En caso de que sea la carpeta personal, parte else..
        * lo que necesito es el nombre de la persona y el id de la carpeta compartida
        *
        * En caso que sea una carpeta compartida, ya tengo el id pero necesito el nombre
        *
         */
        if(!carpeta.equals("Carpeta Personal"))
        {
            id_carpeta = Integer.parseInt(carpeta);
            obtenerCarpetaCompartida();
        }
        else {
            obtenerCarpeta();
        }

        System.out.println("El nombre de la carpeta es: " + nombre_carpeta);
        System.out.println("El id de la carpeta es: " + id_carpeta);

        /*
        * Lanzo la galeria para que el usuario pueda seleccionar un archivo multimedia.
        * Cantidad maxima de archivo es 1, segun SELECT_PICTURE
         */
        try {

            Intent galleryIntent = new Intent(Intent.ACTION_PICK,
                    android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(galleryIntent, SELECT_PICTURE);
        }catch (Exception e){
            Intent AgregarArchivoIntent = new Intent(AddDesdeGaleria.this, DetailsActivity.class);
            AgregarArchivoIntent.putExtra("id",id_usuario);
            AgregarArchivoIntent.putExtra("carpeta",carpeta);
            startActivity(AgregarArchivoIntent);
            finish();
        }

    }

    /*
    * Metodo utilizado para obtener el nombre de la carpeta compartida.
     */
    private void obtenerCarpetaCompartida() {
        String comando;
        comando = String.format("SELECT gru_nombre " +
                "FROM \"MiTouch\".t_grupos  " +
                "WHERE  gru_id_galeria =" + id_carpeta +";");

        PostgrestBD baseDeDatos = new PostgrestBD();
        ResultSet resultSet = baseDeDatos.execute(comando);
        try {
            while (resultSet.next()) {
                nombre_carpeta=resultSet.getString("gru_nombre");
                System.out.println("El nombre de la carpeta es: " + nombre_carpeta);
            }
        }catch (Exception e) {System.out.println("Error Crear Carpetas: " + e);
        }
    }
    /*
    * Metodo utilizado para obtener el id de la carpeta personal
    * Metodo utilizado para obtener el nombre de la carpeta personal( es el nombre de usuario)
     */
    private void obtenerCarpeta() {
        String comando;
        PostgrestBD baseDeDatos;
        ResultSet resultSet;

        comando = "SELECT usu_id_galeria, usu_nombre_usuario " +
                "FROM \"MiTouch\".t_usuarios " +
                "WHERE usu_id='"+id_usuario+"';";
        baseDeDatos = new PostgrestBD();
        resultSet = baseDeDatos.execute(comando);
        try {
            while (resultSet.next()) {
                id_carpeta = resultSet.getInt("usu_id_galeria");
                nombre_carpeta = resultSet.getString("usu_nombre_usuario");

            }
        }catch (Exception e){System.out.println("Error obtener carpeta: "+e);}
    }
    /*
    * Este metodo me devuelve el path de la imagen que seleccione desde la galeria de android
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        try {
            // When an Image is picked
            if (requestCode == SELECT_PICTURE && resultCode == RESULT_OK
                    && null != data) {
                // Get the Image from data

                Uri selectedImage = data.getData();
                String[] filePathColumn = { MediaStore.Images.Media.DATA };

                // Get the cursor
                Cursor cursor = getContentResolver().query(selectedImage,
                        filePathColumn, null, null, null);
                // Move to first row
                cursor.moveToFirst();
                int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                imgDecodableString = cursor.getString(columnIndex);

                System.out.println("file: " + imgDecodableString);
                if(validarExtenciones(imgDecodableString)){
                    cursor.close();
                    crearArchivoMultimedia();
                }
                else
                {
                    Toast.makeText(this, "MiTouch no admite la archivo multimedia con la extensión",
                            Toast.LENGTH_LONG).show();
                }
            } else {
                Toast.makeText(this, "You haven't picked Image",
                        Toast.LENGTH_LONG).show();
            }
        } catch (Exception e) {
            Toast.makeText(this, "Something went wrong", Toast.LENGTH_LONG)
                    .show();
        }
        Intent AgregarArchivoIntent = new Intent(AddDesdeGaleria.this, DetailsActivity.class);

        System.out.println("id: add desde galeria " + id_usuario);
        System.out.println("Carpeta: add desde galeria " + carpeta);
        AgregarArchivoIntent.putExtra("id",id_usuario);
        AgregarArchivoIntent.putExtra("carpeta",carpeta);
        startActivity(AgregarArchivoIntent);
        finish();

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

    /*
    * Metodo que me crea en caso de que no exista, el archivo, tanto en el dispositivo mobile
    * como en el servidor.
    * Este metodo lanza al de actualizar base de datos. (Se podria lanzar al reves)
     */
    private void crearArchivoMultimedia() {
        obtenerArchivo();
        ActualizarBaseDeDatos(); // Actualice la base de datos, debo tener el archivo creado
        CrearDirectorio(); // Crear directorio en caso de que no exista
        copyFileOrDirectory(imgDecodableString, PATH_MOBILE + "/" + nombre_carpeta);//Voy a copiar el archivo a la carpeta de MiTouch

        progress = new ProgressDialog(this, R.style.MyTheme);
        progress.setMessage("Cargando..");
        /*
         * Lo que le pando es:
         * process dialog
         * la clase
         * el id del archivo
         * el nombre del archivo
         * contexto
         */
        new SFTClienteUploadFileFromGalleriaMiTouch(progress, this, id_archivo, imgDecodableString, getApplicationContext()).execute();//Voy a copiar el archivo al servidor

    }

    /*
    *Metodo para actualizar la tabla: t_archivo_galeria
    *
    * Metodo para actualizar la tabla: t_carpeta_archivos_galeria
     */
    private void ActualizarBaseDeDatos() {

        String path= archivoOriginal;
        System.out.println("el path es: " + path);
        Calendar c = Calendar.getInstance();
        SimpleDateFormat df = new SimpleDateFormat("yyyyMMdd");
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
        System.out.println("el comando es: " + comando);
        baseDeDatos.execute(comando);
    }
    /*
    * Metodo utilizado para obtener el nombre del archivo que selccione junto con su extencion
    * Ejemplo: imagen.jpg
     */
    @NonNull
    private void obtenerArchivo() {
        archivoOriginal = imgDecodableString.substring(imgDecodableString.lastIndexOf("/") + 1);
    }
    /*
    * Metodo utilizado para crear directorio en la carpeta mobile en caso de que no exista.
    * El direcorio es la carpeta general que contendra subcarpeta con el usuario logueado y carpetas compartida
     */
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
    /*
    * Metodo utilizado para ubicar el directorio donde se debe crear el archivo
     */
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
    /*
    * Metodo utilizado para la creacion del archivo dentro del dispositivo mobile
     */
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
