package com.example.grupo110.mitouchmobile;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.ContentUris;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.CalendarContract;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TimePicker;

import java.util.Calendar;

public class NewCalendarEventActivity extends Activity {

    private Calendar c;
    private int year;
    private int month;
    private int day;
    private int hourin;
    private int minutein;
    private int hourfin;
    private int minutefin;
    private EditText vdate    = null;
    private EditText vintime  = null;
    private EditText vfintime = null;
    private Button   vcanc    = null;
    private Button   vacep    = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_calendar_event);
        fsetlisteners();
    }

    protected void fsetlisteners(){

       vdate    = (EditText)findViewById(R.id.fecha_inicio);
       vfintime = (EditText)findViewById(R.id.hora_fin);
       vintime  = (EditText)findViewById(R.id.hora_inicio);
       vcanc    = (Button)findViewById(R.id.bCancelevento);
       vacep    = (Button)findViewById(R.id.bCrearEvento);
       final EditText vtitulo  = (EditText)findViewById(R.id.titulo);
       final EditText vdesc    = (EditText)findViewById(R.id.descripcion);
       final EditText vlugar   = (EditText)findViewById(R.id.lugar);
       final EditText vmail    = (EditText)findViewById(R.id.email_extra);


        vdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                c  = Calendar.getInstance();
                year  = c.get(Calendar.YEAR);
                month = c.get(Calendar.MONTH);
                day   = c.get(Calendar.DAY_OF_MONTH);

                new DatePickerDialog(NewCalendarEventActivity.this, date, c.get(Calendar.YEAR), c.get(Calendar.MONTH),
                        c.get(Calendar.DAY_OF_MONTH)).show();
            }
        });

        vintime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Calendar mcurrentTime = Calendar.getInstance();
                int hour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
                int minute = mcurrentTime.get(Calendar.MINUTE);

                new TimePickerDialog(NewCalendarEventActivity.this, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                        hourin   = selectedHour;
                        minutein = selectedMinute;
                        vintime.setText( selectedHour + ":" + selectedMinute);
                    }
                }, hour, minute, true).show();

            }
        });

        vfintime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Calendar mcurrentTime = Calendar.getInstance();
                int hour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
                int minute = mcurrentTime.get(Calendar.MINUTE);

                new TimePickerDialog(NewCalendarEventActivity.this, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                        hourfin   = selectedHour;
                        minutefin = selectedMinute;
                        vfintime.setText( selectedHour + ":" + selectedMinute);
                    }
                }, hour, minute, true).show();

            }
        });

        vacep.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar beginTime = Calendar.getInstance();
                beginTime.set(year,month,day,hourin,minutein);
                Calendar endTime = Calendar.getInstance();
                endTime.set(year, month, day, hourfin, minutefin);
                Intent intent = new Intent(Intent.ACTION_INSERT)
                        .setData(CalendarContract.Events.CONTENT_URI)
                        .putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME, beginTime.getTimeInMillis())
                        .putExtra(CalendarContract.EXTRA_EVENT_END_TIME, endTime.getTimeInMillis())
                        .putExtra(CalendarContract.Events.TITLE,vtitulo.getText())
                        .putExtra(CalendarContract.Events.DESCRIPTION, vdesc.getText())
                        .putExtra(CalendarContract.Events.EVENT_LOCATION, vlugar.getText())
                        .putExtra(Intent.EXTRA_EMAIL,vmail.getText());
                startActivity(intent);
            }
        });

        vcanc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

    }

    DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {

        @Override
        public void onDateSet(DatePicker view, int year1, int month1,
                              int day1) {
            year  = year1;
            month = month1;
            day   = day1;
            vdate.setText(day1+"/"+month1+"/"+year1);

        }

    };
}
