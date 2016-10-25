package com.example.grupo110.mitouchmobile;

import android.accounts.AccountManager;
import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.Toast;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.drive.DriveFile;
import com.google.android.gms.drive.DriveId;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.googleapis.extensions.android.gms.auth.UserRecoverableAuthIOException;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.DriveScopes;

import java.util.Collections;

public class DeleteFileDrive extends Activity {

    private int result = 0;
    GoogleAccountCredential account;
    String fileId = null;
    private String cuenta = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    try {
        fileId = getIntent().getExtras().getString("idDel");
        }catch (Exception e){
            e.printStackTrace();
        }

    account = GoogleAccountCredential.usingOAuth2(getApplicationContext(), Collections.singletonList(DriveScopes.DRIVE));
    startActivityForResult(account.newChooseAccountIntent(), 1000);

    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch(requestCode) {
            case 1000:
                if (resultCode == RESULT_OK && data != null &&
                        data.getExtras() != null) {
                    String accountName = data.getStringExtra(AccountManager.KEY_ACCOUNT_NAME);
                    if (accountName != null) {
                        cuenta = accountName;
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

                                        driveService.files().delete(fileId).execute();

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

                                if(result != 0){

                                    Toast.makeText(DeleteFileDrive.this, R.string.error_delete, Toast.LENGTH_LONG).show();

                                }else{

                                    Toast.makeText(DeleteFileDrive.this, R.string.exito_delete, Toast.LENGTH_LONG).show();
                                }
                            }

                        }.execute();

                    }
                }
        }
    }
}
