package com.example.grupo110.mitouchmobile;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageButton;

public class GoogleDrive extends AppCompatActivity {
        ImageButton googleDrive;

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_google_drive);
            addImageButtons();
            setImageNextAndRepeat();


            googleDrive.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent siguiente = new Intent(GoogleDrive.this, MainMenuActivity.class);
                    startActivity(siguiente);
                    finish();
                }
            });
        }

        private void setImageNextAndRepeat() {
            googleDrive .setImageResource(R.drawable.hardcode_google_drive);
        }

        private void addImageButtons() {
            googleDrive= (ImageButton) findViewById(R.id.imageButtonGoogleDrive);

        }

}

