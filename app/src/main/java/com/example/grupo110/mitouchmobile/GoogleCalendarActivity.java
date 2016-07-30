package com.example.grupo110.mitouchmobile;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageButton;

public class GoogleCalendarActivity extends AppCompatActivity {
    ImageButton googleCalendar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_google_calendar);
        addImageButtons();
        setImageNextAndRepeat();


        googleCalendar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void setImageNextAndRepeat() {
        googleCalendar .setImageResource(R.drawable.hardcode_google_calendar);
    }

    private void addImageButtons() {
        googleCalendar= (ImageButton) findViewById(R.id.imageButtonGoogleCalendar);

    }

}
