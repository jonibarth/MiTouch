package com.example.grupo110.mitouchmobile;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.view.ContextThemeWrapper;
import android.widget.Toast;

import com.example.grupo110.mitouchmobile.base_de_datos.PostgrestBD;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.googleapis.extensions.android.gms.auth.UserRecoverableAuthIOException;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.DriveScopes;
import com.google.api.services.drive.model.File;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Collections;


public class ActionGoogleDriveActivity extends Activity {

    private String id, url, idDel;
    private int user;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if(getIntent().getExtras() != null){

            id = getIntent().getExtras().getString("id");
            idDel = getIntent().getExtras().getString("idDel");

            AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(ActionGoogleDriveActivity.this, R.style.AlertDialogCustom));
            builder.setTitle("Acción a realizar")
                    .setItems(new String [] {"Abrir","Compartir","Eliminar"}, new DialogInterface.OnClickListener(){

                        public void onClick(DialogInterface dialog, int opt){

                            switch (opt){

                                case 0: Intent docIntent = new Intent(ActionGoogleDriveActivity.this, OpenDriveFileActivity.class);
                                        docIntent.putExtra("id",id);
                                        startActivity(docIntent);
                                        finish();


                                        break;

                                case 1: Intent newintent = new Intent(ActionGoogleDriveActivity.this, CompartirDrive.class);

                                        user = getIntent().getExtras().getInt("user");

                                        newintent.putExtra("id",user);
                                        startActivityForResult(newintent,1);

                                        break;

                                case 2: if(idDel != null){

                                    Intent deleteIntent = new Intent(ActionGoogleDriveActivity.this,DeleteFileDrive.class);
                                    deleteIntent.putExtra("idDel",id);
                                    startActivity(deleteIntent);
                                    finish();
                                    break;
                                }

                            }
                        }

                    });

            builder.show();

        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == 1) {
            if(resultCode == Activity.RESULT_OK){
                String mail  = data.getStringExtra("mail");
                String grupo = data.getStringExtra("grupo");
                String list  = "";

                Intent shareIntent = new Intent(ActionGoogleDriveActivity.this,ShareDriveActivity.class);

                shareIntent.putExtra("mail",mail);
                shareIntent.putExtra("grupo",grupo);
                shareIntent.putExtra("file",id);
                shareIntent.putExtra("user",user);

                String comando = null;
                PostgrestBD baseDeDatos;
                ResultSet resultSet;

                if(grupo != null){

                comando = "SELECT ugru_id_usuario, usu_mail " +
                        "FROM \"MiTouch\".t_usuarios_grupo " +
                        "INNER JOIN \"MiTouch\".t_usuarios ON ugru_id_usuario = usu_id " +
                        "WHERE ugru_id_grupo = "+ grupo +
                        " AND usu_id <> "+ Integer.toString(user)+";";

                baseDeDatos = new PostgrestBD();
                try{
                    resultSet = baseDeDatos.execute(comando);

                    try{
                        while (resultSet.next()) {

                            System.out.println(resultSet.getString("usu_mail"));
                            list = list + resultSet.getString("usu_mail") + "$";
                        }
                    }catch(Exception e){e.printStackTrace();}
                }catch (Exception e){ e.printStackTrace();}

                }

                shareIntent.putExtra("list",list);

                startActivity(shareIntent);
                finish();
            }
            if (resultCode == Activity.RESULT_CANCELED) {

            }
        }
    }//onActivityResult

}
