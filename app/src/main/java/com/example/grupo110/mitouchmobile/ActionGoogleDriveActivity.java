package com.example.grupo110.mitouchmobile;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.ImageFormat;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.view.ContextThemeWrapper;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.ExpandableListView;

import com.example.grupo110.mitouchmobile.base_de_datos.PostgrestBD;

import java.sql.ResultSet;



public class ActionGoogleDriveActivity extends Activity {

    private String id, url, idDel;
    private DeleteFileDrive del;
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

                                case 0: url = getUrl(id);

                                        if(url != ""){

                                            Intent docIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                                            startActivity(docIntent);
                                        }

                                        break;

                                case 1: Intent newintent = new Intent(ActionGoogleDriveActivity.this, CompartirDrive.class);
                                        newintent.putExtra("id",getIntent().getExtras().getInt("user"));
                                        startActivityForResult(newintent,1);
                                        break;

                                case 2: if(idDel != null){

                                    Intent deleteIntent = new Intent(ActionGoogleDriveActivity.this,DeleteFileDrive.class);
                                    deleteIntent.putExtra("idDel",idDel);
                                    startActivity(deleteIntent);

                                    break;
                                }

                            }
                            //finish();
                        }

                    });

            builder.show();

        }

    }

    public String getUrl (String id) {
        String comando = "";
        String res = "";

        comando = String.format("SELECT * FROM  \"MiTouch\".t_archivos WHERE arch_id_google_drive ='"+ id +"';");
        PostgrestBD baseDeDatos = new PostgrestBD();
        ResultSet resultSet = baseDeDatos.execute(comando);
        try{
            while (resultSet.next()) {


                res = resultSet.getString("arch_url_acceso");
            }
        }catch(Exception e){res = "";}

        return res;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == 1) {
            if(resultCode == Activity.RESULT_OK){
                String mail = data.getStringExtra("mail");

                Intent shareIntent = new Intent(ActionGoogleDriveActivity.this,ShareDriveActivity.class);

                shareIntent.putExtra("mail",mail);
                shareIntent.putExtra("file",id);

                startActivity(shareIntent);

            }
            if (resultCode == Activity.RESULT_CANCELED) {
                //Write your code if there's no result
            }
        }
    }//onActivityResult

}
