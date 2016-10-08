package com.example.grupo110.mitouchmobile;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;

import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.googleapis.batch.BatchRequest;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
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

            String fileId = "1a4oIGyOYlkZFF9_3wqd7Vieb5B1SxnRMA2_l1nDhcRA";

            try{

            GoogleAccountCredential account;

                account = GoogleAccountCredential.usingOAuth2(getApplicationContext(),Collections.singletonList(DriveScopes.DRIVE));
                startActivityForResult(account.newChooseAccountIntent(),1);

            com.google.api.services.drive.Drive driveService =
                    new Drive.Builder(
                            AndroidHttp.newCompatibleTransport(),
                            JacksonFactory.getDefaultInstance(),
                            account
                    ).build();


                Permission permission = new Permission();
                permission.setEmailAddress("martindiazgrizzuti@gmail.com")
                .setType("user").setRole("owner");


                driveService.permissions().create(fileId,permission).execute().setDisplayName("Name");
                }catch (Exception e){
                    e.printStackTrace();
                }
                return null;

        }}.execute();
    }
}

