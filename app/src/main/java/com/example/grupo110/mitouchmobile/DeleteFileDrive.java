package com.example.grupo110.mitouchmobile;

import android.os.AsyncTask;
import android.os.Bundle;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.drive.DriveFile;
import com.google.android.gms.drive.DriveId;

public class DeleteFileDrive extends BaseDriveActivity{

    DriveFile sumFile;
    com.google.android.gms.common.api.Status deleteStatus;
    GoogleApiClient client;
    @Override
    public void onConnected(Bundle connectionHint) {

         super.onConnected(connectionHint);
         String idDel;

        try{

         idDel = getIntent().getExtras().getString("idDel");
         client = getGoogleApiClient();
         DriveId fileId = DriveId.decodeFromString(idDel);
         sumFile = fileId.asDriveFile();

            new AsyncTask<Void,Void,Void>(){
                @Override
                protected Void doInBackground(Void... params) {
                try {
                    deleteStatus = sumFile.trash(client).await();

                    if (deleteStatus.isSuccess()) {
                        System.out.println("Documento borrado con éxito");}
                        else{
                            System.out.println("No se pudo borrar el documento");
                        }

                }catch(Exception e1){
                    e1.printStackTrace();
                }
                    return null;
         }
            }.execute();
            showMessage("Archivo borrado correctamente");
            finish();
     }catch(Exception e){

         e.printStackTrace();
     }
    }

}
