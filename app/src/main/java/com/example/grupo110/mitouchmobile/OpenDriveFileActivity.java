package com.example.grupo110.mitouchmobile;


import android.accounts.AccountManager;
import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.Toast;

import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.googleapis.extensions.android.gms.auth.UserRecoverableAuthIOException;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.DriveScopes;
import com.google.api.services.drive.model.File;

import java.util.Collections;

public class OpenDriveFileActivity extends Activity {

    private int result = 0;
    GoogleAccountCredential account;
    String fileId = null;
    String url = null;
    private String cuenta = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            fileId = getIntent().getExtras().getString("id");
        }catch (Exception e){
            e.printStackTrace();
        }

        account = GoogleAccountCredential.usingOAuth2(getApplicationContext(), Collections.singletonList(DriveScopes.DRIVE));

        cuenta = "grupo110unlam@gmail.com";

        if (cuenta != null) {
            new AsyncTask<Void, Void, Void>(){
                protected Void doInBackground(Void... params) {

                    if(fileId != null){
                        try {

                            account.setSelectedAccountName(cuenta);

                            Drive driveService =
                                    new Drive.Builder(
                                            AndroidHttp.newCompatibleTransport(),
                                            JacksonFactory.getDefaultInstance(),
                                            account
                                    ).setApplicationName("MiTouch").build();

                            File file = driveService.files().get(fileId).execute();

                            String mime = file.getMimeType();

                            switch (mime){

                                case "application/vnd.google-apps.spreadsheet":
                                    url = "https://docs.google.com/spreadsheets/d/" + fileId;
                                    break;
                                case "application/vnd.google-apps.presentation":
                                    url = "https://docs.google.com/presentation/d/" + fileId;
                                    break;
                                case "application/vnd.google-apps.document":
                                    url = "https://docs.google.com/document/d/" + fileId;
                                    break;
                                default: url = null;
                                    break;
                            }

                            if(url != null) {

                                Intent docIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                                startActivity(docIntent);
                                finish();
                            }else{
                                result = 1;
                            }

                        }catch (UserRecoverableAuthIOException e) {
                            startActivityForResult(e.getIntent(), 2);
                        } catch (Exception e1) {
                            e1.printStackTrace();
                            result = 1;
                            finish();
                        }
                    }

                    return null;
                }

                protected void onPostExecute(Void param){
                    if (result == 1){
                        Toast.makeText(OpenDriveFileActivity.this,"No se pudo abrir el archivo",Toast.LENGTH_LONG);
                    }

                }

            }.execute();

        }
    }

  }

