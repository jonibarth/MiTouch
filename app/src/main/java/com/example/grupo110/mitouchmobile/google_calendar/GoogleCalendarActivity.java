package com.example.grupo110.mitouchmobile.google_calendar;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.ContentUris;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.CalendarContract;
import android.view.View;
import android.widget.DatePicker;
import android.widget.TextView;

import com.example.grupo110.mitouchmobile.R;

import java.util.Calendar;

public class GoogleCalendarActivity extends Activity {

    private int year;
    private int month;
    private int day;
    private Calendar c;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calendar_picker);
        setListeners();
    }

    protected void setListeners() {

        TextView vToday  = (TextView) findViewById(R.id.viewToday);
        TextView vDate   = (TextView) findViewById(R.id.viewDate);
        TextView vCreate = (TextView) findViewById(R.id.createDate);

        vToday.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                fShowTodayEvents();
            }});

        vDate.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){

                   c  = Calendar.getInstance();
                year  = c.get(Calendar.YEAR);
                month = c.get(Calendar.MONTH);
                day   = c.get(Calendar.DAY_OF_MONTH);

                new DatePickerDialog(GoogleCalendarActivity.this, date, c.get(Calendar.YEAR), c.get(Calendar.MONTH),
                        c.get(Calendar.DAY_OF_MONTH)).show();

            }

        });

        vCreate.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){

                Intent mainIntent = new Intent(GoogleCalendarActivity.this, NewCalendarEventActivity.class);
                startActivity(mainIntent);
                /*
                Calendar beginTime = Calendar.getInstance();
                Calendar endTime = Calendar.getInstance();
                Intent intent = new Intent(Intent.ACTION_INSERT)
                        .setData(CalendarContract.Events.CONTENT_URI)
                        .putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME, beginTime.getTimeInMillis())
                        .putExtra(CalendarContract.EXTRA_EVENT_END_TIME, endTime.getTimeInMillis());
                startActivity(intent);
                finish();
                */
            }

        });


    }

    protected void fShowTodayEvents(){

        long startMillis;

        startMillis = System.currentTimeMillis();

        Uri.Builder builder = CalendarContract.CONTENT_URI.buildUpon();
        builder.appendPath("time");
        ContentUris.appendId(builder, startMillis);
        Intent intent = new Intent(Intent.ACTION_VIEW)
                .setData(builder.build());
        startActivity(intent);

    }

    DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {

        @Override
        public void onDateSet(DatePicker view, int year1, int month1,
                              int day1) {

            c.set(year1,month1,day1);

            long startMillis;

            startMillis = c.getTimeInMillis();

            Uri.Builder builder = CalendarContract.CONTENT_URI.buildUpon();
            builder.appendPath("time");
            ContentUris.appendId(builder, startMillis);
            Intent intent = new Intent(Intent.ACTION_VIEW)
                    .setData(builder.build());
            startActivity(intent);

        }

    };

    }