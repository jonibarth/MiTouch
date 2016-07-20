package com.example.grupo110.mitouchmobile;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MenuActivityGridView extends AppCompatActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu_activity_grid_view);

        GridView gridview = (GridView) findViewById(R.id.gridview);
        gridview.setAdapter(new ImageAdapter(this));

        gridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v,
                                    int position, long id) {

                // Si es la posicion 0: Google Drive
                //Si es la posicion 1: Galeria
                //Si es la posicion 2: Google Calendar
                //Si es la posicion 3: Chat
                //Si es la posicion 4: Calculadora
                if(position == 4) {
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
                //Si es la posicion 5: Configuracion
                if(position==5)
                {
                    Intent siguiente = new Intent(MenuActivityGridView.this, SettingActivity.class);
                    startActivity(siguiente);
                    finish();
                }
                //Si es la posicion 6: Navegador
                if(position==6)
                {
                    String link = "http://www.google.com.ar";
                    Intent intent = null;
                    intent = new Intent(intent.ACTION_VIEW, Uri.parse(link));
                    startActivity(intent);
                }
                //Si es la posicion 7: Salir
                if(position == 7)
                    finish();
                /* Boludes que venia por defecto
                Toast.makeText(MenuActivityGridView.this, "" + position,
                        Toast.LENGTH_SHORT).show();
                        */
            }
        });
    }
}
