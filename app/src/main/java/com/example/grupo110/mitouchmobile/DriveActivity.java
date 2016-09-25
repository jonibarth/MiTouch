/**
 * Copyright 2013 Google Inc. All Rights Reserved.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software distributed under the
 * License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either
 * express or implied. See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.grupo110.mitouchmobile;

import android.content.Intent;
import android.content.IntentSender;
import android.content.IntentSender.SendIntentException;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.webkit.MimeTypeMap;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.drive.Drive;
import com.google.android.gms.drive.DriveFile;
import com.google.android.gms.drive.DriveFolder;
import com.google.android.gms.drive.DriveId;
import com.google.android.gms.drive.OpenFileActivityBuilder;

/**
 * An activity to illustrate how to pick a file with the
 * opener intent.
 */
public class DriveActivity extends BaseDriveActivity {

    private static final String TAG = "DriveActivity";

    private static final int REQUEST_CODE_OPENER = 1;
    private final  String ext = "CAESHDBCNmRxRVljM1hTMXdUMmQ1WTFWMFQwUnhTRWsYlkYgmorkvM1VKAA=";
    private DriveFile file;

    @Override
    public void onConnected(Bundle connectionHint) {

        super.onConnected(connectionHint);
        System.out.println("mimetype: " + ext);
        String mimeType = MimeTypeMap.getSingleton().getMimeTypeFromExtension(ext.toLowerCase());

        IntentSender intentSender = Drive.DriveApi
                .newOpenFileActivityBuilder()
                .setMimeType(new String[] {DriveFolder.MIME_TYPE, //mimeType
                        "text/html",
                        "text/plain",
                        "application/rtf",
                        "application/vnd.oasis.opendocument.text",
                        "application/pdf",
                        "application/vnd.openxmlformats-officedocument.wordprocessingml.document",
                        "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet",
                        "application/x-vnd.oasis.opendocument.spreadsheet",
                        "text/csv",
                        "application/vnd.openxmlformats-officedocument.presentationml.presentation",
                        "application/vnd.google-apps.document",
                        "image/png",
                        "image/jpeg",
                        "image/jpg"
                })
                .build(getGoogleApiClient());
        try {
            startIntentSenderForResult(
                    intentSender, REQUEST_CODE_OPENER, null, 0, 0, 0);
        } catch (SendIntentException e) {
            Log.w(TAG, "Unable to send int", e);
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
         switch (requestCode) {
            case REQUEST_CODE_OPENER:
                if (resultCode == RESULT_OK) {
                    DriveId driveId = (DriveId) data.getParcelableExtra(
                            OpenFileActivityBuilder.EXTRA_RESPONSE_DRIVE_ID);
                }
                    finish();
                break;
            default:
                super.onActivityResult(requestCode, resultCode, data);
        }
    }


}
