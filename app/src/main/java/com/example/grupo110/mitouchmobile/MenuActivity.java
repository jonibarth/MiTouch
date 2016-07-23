package com.example.grupo110.mitouchmobile;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageButton;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MenuActivity extends AppCompatActivity {
    ImageButton iB_topleft;
    ImageButton iB_topright;
    ImageButton iB_bottomleft;
    ImageButton iB_bottomright;
    ImageButton iB_next;
    ImageButton iB_repeat;
    ImageButton iB_midleft;
    ImageButton iB_midright;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);
        addImageButtons();
        setImageNextAndRepeat();

        iB_topleft.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Abrir Google Drive
            }
        });
        iB_topright.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Abrir Galeria
            }
        });
        iB_repeat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Abrir Calendario
            }
        });
        iB_next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Abrir Chat
            }
        });
        iB_midleft.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ArrayList<HashMap<String,Object>> items =new ArrayList<HashMap<String,Object>>();
                final PackageManager pm = getPackageManager();
                List<PackageInfo> packs = pm.getInstalledPackages(0);
                for (PackageInfo pi : packs) {
                    if( pi.packageName.toString().toLowerCase().contains("calcul")){
                        HashMap<String, Object> map = new HashMap<String, Object>();
                        map.put("appName", pi.applicationInfo.loadLabel(pm));
                        map.put("packageName", pi.packageName);
                        items.add(map);
                    }
                }
                if(items.size()>=1){
                    String packageName = (String) items.get(0).get("packageName");
                    Intent i = pm.getLaunchIntentForPackage(packageName);
                    if (i != null)
                        startActivity(i);
                }
                else{
                    // Application not found
                }
            }
        });
        iB_midright.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String link = "http://www.google.com.ar";
                Intent intent = null;
                intent = new Intent(intent.ACTION_VIEW, Uri.parse(link));
                startActivity(intent);
            }
        });
        iB_bottomleft.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent siguiente = new Intent(MenuActivity.this, SettingActivity.class);
                startActivity(siguiente);
                finish();
            }
        });
        iB_bottomright.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

    }

    public void addImageButtons()
    {
        iB_topleft = (ImageButton) findViewById(R.id.button_topleft);
        iB_topright = (ImageButton) findViewById(R.id.button_topright);
        iB_bottomleft = (ImageButton) findViewById(R.id.button_bottomleft);
        iB_bottomright = (ImageButton) findViewById(R.id.button_bottomright);
        iB_next = (ImageButton) findViewById(R.id.button_next);
        iB_repeat = (ImageButton) findViewById(R.id.button_repeat);
        iB_midleft = (ImageButton) findViewById(R.id.button_midleft);
        iB_midright = (ImageButton) findViewById(R.id.button_midright);
    }

    public void setImageNextAndRepeat()
    {
        iB_topleft .setImageResource(R.drawable.ic_googledrive);
        iB_topright.setImageResource(R.drawable.ic_picturesfolder);

        iB_next.setImageResource(R.drawable.ic_chat);
        iB_repeat.setImageResource(R.drawable.ic_googlecalendar);

        iB_midleft.setImageResource(R.drawable.ic_calculator);
        iB_midright.setImageResource(R.drawable.ic_googlechrome);

        iB_bottomleft.setImageResource(R.drawable.ic_services);
        iB_bottomright.setImageResource(R.drawable.ic_power_logoff);



    }


}
