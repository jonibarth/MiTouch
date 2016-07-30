package com.example.grupo110.mitouchmobile;

import android.content.ComponentName;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.View;
import android.widget.ImageView;

public class MainMenuActivity extends AppCompatActivity {

    ImageView vDrive;
    ImageView vGallery;
    ImageView vChrome;
    ImageView vCalend;
    ImageView vCalc;
    ImageView vChat;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mainmenu);

        setListeners();
    }

    protected void setListeners(){

        ImageView vDrive   = (ImageView) findViewById(R.id.viewDrive);
        ImageView vGallery = (ImageView) findViewById(R.id.viewGallery);
        ImageView vChrome  = (ImageView) findViewById(R.id.viewChrome);
        ImageView vCalend  = (ImageView) findViewById(R.id.viewCalendar);
        ImageView vCalc    = (ImageView) findViewById(R.id.viewCalc);
        ImageView vChat    = (ImageView) findViewById(R.id.viewChat);

        vDrive.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent mainIntent = new Intent(MainMenuActivity.this, GoogleDrive.class);
                startActivity(mainIntent);
            }
        });

        vGallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent mainIntent = new Intent(MainMenuActivity.this, GaleriaActivity.class);
                startActivity(mainIntent);
            }
        });


        vCalend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent mainIntent = new Intent(MainMenuActivity.this, GoogleCalendarActivity.class);
                startActivity(mainIntent);
            }
        });

        vChat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent mainIntent = new Intent(MainMenuActivity.this, ChatActivity.class);
                startActivity(mainIntent);
            }
        });

        vCalc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent mainIntent = new Intent();
                mainIntent.setAction(Intent.ACTION_MAIN);
                mainIntent.setComponent(new ComponentName(
                        "com.android.calculator2",
                        "com.android.calculator2.Calculator"));
                startActivity(mainIntent);
            }
        });

    vChrome.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent mainIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.google.com"));
            startActivity(mainIntent);
        }
    });
}
}