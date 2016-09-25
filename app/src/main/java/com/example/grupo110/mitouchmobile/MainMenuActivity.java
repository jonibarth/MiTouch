package com.example.grupo110.mitouchmobile;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.view.menu.MenuView;
import android.text.format.DateFormat;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;

import java.sql.ResultSet;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

public class MainMenuActivity extends AppCompatActivity {

    ImageView vDrive;
    ImageView vGallery;
    ImageView vChrome;
    ImageView vCalend;
    ImageView vCalc;
    ImageView vChat;
    int id_usuario;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_logout:
                modificar_usu_ultimo_log_out(id_usuario);
                finish();
                return true;
            case R.id.action_settings:
                Intent mainIntent = new Intent(MainMenuActivity.this, SettingActivity.class);
                mainIntent.putExtra("id",id_usuario);
                startActivity(mainIntent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mainmenu);
        id_usuario = getIntent().getExtras().getInt("id");
        setListeners();
    }

    protected void setListeners() {

        ImageView vDrive = (ImageView) findViewById(R.id.viewDrive);
        ImageView vGallery = (ImageView) findViewById(R.id.viewGallery);
        ImageView vChrome = (ImageView) findViewById(R.id.viewChrome);
        ImageView vCalend = (ImageView) findViewById(R.id.viewCalendar);
        ImageView vCalc = (ImageView) findViewById(R.id.viewCalc);
        ImageView vChat = (ImageView) findViewById(R.id.viewChat);

        vDrive.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent mainIntent = new Intent(MainMenuActivity.this, DriveActivity.class);
                mainIntent.putExtra("id",id_usuario);
                startActivity(mainIntent);
            }
        });
        vGallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent mainIntent = new Intent(MainMenuActivity.this, GaleriaActivity.class);
                mainIntent.putExtra("id",id_usuario);
                startActivity(mainIntent);
            }
        });
        vCalend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent mainIntent = new Intent(MainMenuActivity.this, GoogleCalendarActivity.class);
                mainIntent.putExtra("id",id_usuario);
                startActivity(mainIntent);
            }
        });
        vChat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent mainIntent = new Intent(MainMenuActivity.this, ChatActivity.class);
                mainIntent.putExtra("id",id_usuario);
                startActivity(mainIntent);
            }
        });

        vCalc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ArrayList<HashMap<String, Object>> items = new ArrayList<HashMap<String, Object>>();
                final PackageManager pm = getPackageManager();
                List<PackageInfo> packs = pm.getInstalledPackages(0);
                for (PackageInfo pi : packs) {
                    if (pi.packageName.toString().toLowerCase().contains("calcul")) {
                        HashMap<String, Object> map = new HashMap<String, Object>();
                        map.put("appName", pi.applicationInfo.loadLabel(pm));
                        map.put("packageName", pi.packageName);
                        items.add(map);
                    }
                }
                if (items.size() >= 1) {
                    String packageName = (String) items.get(0).get("packageName");
                    Intent i = pm.getLaunchIntentForPackage(packageName);
                    if (i != null)
                        startActivity(i);
                } else {
                    // Application not found
                }
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

    private void modificar_usu_ultimo_log_out(int id_usuario) {
        Date d = new Date();
        SimpleDateFormat formatoHora = new SimpleDateFormat("HH:mm");
        SimpleDateFormat formatoFecha = new SimpleDateFormat("dd-MM-yyyy");
        String comando = "";

        comando = String.format("UPDATE \"MiTouch\".t_usuarios SET usu_ultimo_log_out ='"+formatoFecha.format(d)+" " + formatoHora.format(d)+"' WHERE usu_id ='"+ id_usuario +"';");
        PostgrestBD baseDeDatos = new PostgrestBD();
        ResultSet resultSet = baseDeDatos.execute(comando);
        return;
    }


}