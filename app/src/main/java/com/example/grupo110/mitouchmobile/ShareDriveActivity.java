package com.example.grupo110.mitouchmobile;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;

import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.googleapis.batch.BatchRequest;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.googleapis.extensions.android.gms.auth.UserRecoverableAuthIOException;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.DriveScopes;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.drive.model.Permission;

import java.util.Collections;



public class ShareDriveActivity extends Activity{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        new AsyncTask<Void, Void, Void>() {
            protected Void doInBackground(Void... params) {

            String fileId = null;

            try{
                fileId = getIntent().getExtras().getString("id");
            }catch (Exception e){
                e.printStackTrace();
            }

            if (fileId != null) {

                try {

                    GoogleAccountCredential account;

                    account = GoogleAccountCredential.usingOAuth2(getApplicationContext(), Collections.singletonList(DriveScopes.DRIVE));
                    account.setSelectedAccountName("grupo110unlam@gmail.com");

                    com.google.api.services.drive.Drive driveService =
                            new Drive.Builder(
                                    AndroidHttp.newCompatibleTransport(),
                                    JacksonFactory.getDefaultInstance(),
                                    account
                            ).setApplicationName("MiTouch").build();

                    Permission permission = new Permission();
                    permission.setEmailAddress("dismal.cj@gmail.com")
                            .setType("user").setRole("writer");


                    driveService.permissions().create(fileId, permission).setFields("id").execute();
                } catch (UserRecoverableAuthIOException e) {
                    startActivityForResult(e.getIntent(), 2);
                } catch (Exception e1) {
                    e1.printStackTrace();
                }

            }
                return null;

        }}.execute();
    }
}

