package com.example.grupo110.mitouchmobile;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.Toast;

import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.googleapis.batch.BatchRequest;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.googleapis.extensions.android.gms.auth.UserRecoverableAuthIOException;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.DriveScopes;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.drive.model.Permission;

import com.example.grupo110.mitouchmobile.base_de_datos.PostgrestBD;

import java.sql.ResultSet;
import java.util.Collections;



public class ShareDriveActivity extends Activity{

    private int result = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        new AsyncTask<Void, Void, Void>() {
            protected Void doInBackground(Void... params) {

            GoogleAccountCredential account;

            String fileId = null;
            String mail   = null;
            String grupo  = null;
            String list   = null;
            String array[];
            int    user   = 0;

            try{
                fileId = getIntent().getExtras().getString("file");
                mail   = getIntent().getExtras().getString("mail");
                grupo  = getIntent().getExtras().getString("grupo");
                list   = getIntent().getExtras().getString("list");
                user   = getIntent().getExtras().getInt("user");

            }catch (Exception e){
                e.printStackTrace();
            }

            if (fileId != null && mail != null) {

                try {

                    account = GoogleAccountCredential.usingOAuth2(getApplicationContext(), Collections.singletonList(DriveScopes.DRIVE));
                    account.setSelectedAccountName("grupo110unlam@gmail.com");

                    Drive driveService =
                            new Drive.Builder(
                                    AndroidHttp.newCompatibleTransport(),
                                    JacksonFactory.getDefaultInstance(),
                                    account
                            ).setApplicationName("MiTouch").build();

                    Permission permission = new Permission();
                    permission.setEmailAddress(mail)
                            .setType("user").setRole("writer");


                    driveService.permissions().create(fileId, permission).setFields("id").execute();


                    finish();

                } catch (UserRecoverableAuthIOException e) {
                    startActivityForResult(e.getIntent(), 2);
                } catch (Exception e1) {
                    e1.printStackTrace();
                    result = 1;
                    finish();
                }

            }else{

                if(fileId != null && grupo != null && user != 0 && list != null){

                    array = list.split("\\$");

                    try {
                        account = GoogleAccountCredential.usingOAuth2(getApplicationContext(), Collections.singletonList(DriveScopes.DRIVE));
                        account.setSelectedAccountName("grupo110unlam@gmail.com");

                        Drive driveService =
                                new Drive.Builder(
                                        AndroidHttp.newCompatibleTransport(),
                                        JacksonFactory.getDefaultInstance(),
                                        account
                                ).setApplicationName("MiTouch").build();


                        for (int i = 0; i < array.length; i++) {

                            Permission permission = new Permission();
                            permission.setEmailAddress(array[i]).setType("user").setRole("writer");

                            driveService.permissions().create(fileId, permission).setFields("id").execute();

                        }

                        finish();
                    }catch (UserRecoverableAuthIOException e) {
                            startActivityForResult(e.getIntent(), 2);
                        } catch (Exception e1) {
                            e1.printStackTrace();

                        result = 1;
                        finish();
                    }
                }

            }
                return null;

        }

            protected void onPostExecute(Void param){

                if(result != 0){

                    Toast.makeText(ShareDriveActivity.this, R.string.error_share, Toast.LENGTH_LONG).show();

                }else{

                    Toast.makeText(ShareDriveActivity.this, R.string.exito_share, Toast.LENGTH_LONG).show();
                }

            }

        }.execute();
    }
}

