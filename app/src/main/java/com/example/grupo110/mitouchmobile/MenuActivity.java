package com.example.grupo110.mitouchmobile;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MenuActivity extends AppCompatActivity {

    ImageButton salirDeAplicacion;
    ImageButton iniciarNavegador;
    ImageButton iniciarCalculadora;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

// Salir de la aplicación
    salirDeAplicacion =(ImageButton)findViewById(R.id.imageButtonLogOff);
    salirDeAplicacion.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            finish();
        }
    });

// Iniciar Navegador del Dispositivo Mobile
    iniciarNavegador =(ImageButton)findViewById(R.id.imageButtonNavegador);
    iniciarNavegador.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            String link = "http://www.google.com.ar";
            Intent intent = null;
            intent = new Intent(intent.ACTION_VIEW,Uri.parse(link));
            startActivity(intent);

        }
    });
// Iniciar Calculadora del dispositivo Mobile
    iniciarCalculadora =(ImageButton)findViewById(R.id.imageButtonCalculadora);
    iniciarCalculadora.setOnClickListener(new View.OnClickListener() {
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

}

}
