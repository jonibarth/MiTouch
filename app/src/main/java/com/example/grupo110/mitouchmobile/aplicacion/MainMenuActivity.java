package com.example.grupo110.mitouchmobile.aplicacion;

import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;

import com.example.grupo110.mitouchmobile.DriveActivity;
import com.example.grupo110.mitouchmobile.R;
import com.example.grupo110.mitouchmobile.base_de_datos.PostgrestBD;
import com.example.grupo110.mitouchmobile.chat.ConsultaSql;
import com.example.grupo110.mitouchmobile.chat.SeleccionarChat;
import com.example.grupo110.mitouchmobile.galeria.GaleriaActivity;
import com.example.grupo110.mitouchmobile.google_calendar.GoogleCalendarActivity;

import java.io.File;
import java.io.OutputStreamWriter;
import java.sql.ResultSet;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class MainMenuActivity extends AppCompatActivity {

    ImageView vDrive;
    ImageView vGallery;
    ImageView vChrome;
    ImageView vCalend;
    ImageView vCalc;
    ImageView vChat;
    int id_usuario;
    String email;//Mail para compartir en Drive

    boolean done = false;
    final String PATH_MOBILE = "/storage/sdcard0/MiTouchMultimedia/chat";
    Context context;


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
                grabar();
                Intent mainIntent = new Intent(MainMenuActivity.this, LoginActivity.class);
                startActivity(mainIntent);
                finish();
                return true;
            case R.id.action_settings:
                mainIntent = new Intent(MainMenuActivity.this, SettingActivity.class);
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
        context = this;
        setListeners();

    }

    protected void setListeners() {
        deleteWithChildren(PATH_MOBILE);
        TimerTask tt = new TimerTask() {

            @Override
            public void run() {
                while(!done) {
                    try {
                        ConsultaSql consultaSql = new ConsultaSql(context,id_usuario);
                        Thread.sleep(5000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        };
        new Timer().schedule(tt, 500);


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
                Intent mainIntent = new Intent(MainMenuActivity.this, SeleccionarChat.class);
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
        Calendar c = Calendar.getInstance();
        System.out.println("Current time => "+c.getTime());

        SimpleDateFormat df = new SimpleDateFormat("yyyyMMdd HH:mm:ss");
        String diahora = df.format(c.getTime());

        String comando = "";

        comando = String.format("UPDATE \"MiTouch\".t_usuarios SET usu_ultimo_log_out ='"+diahora+"' WHERE usu_id ='"+ id_usuario +"';");
        PostgrestBD baseDeDatos = new PostgrestBD();
        ResultSet resultSet = baseDeDatos.execute(comando);
        return;
    }

    public void grabar() {
        try {
            OutputStreamWriter archivo = new OutputStreamWriter(openFileOutput(
                    "notas.txt", MODE_PRIVATE));
            archivo.write("null");
            archivo.flush();
            archivo.close();
        } catch (Exception e) {System.out.println("Error grabar archivo");
        }
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        done = true;
    }


    /**
     * Deletes the given path and, if it is a directory, deletes all its children.
     */
    public boolean deleteWithChildren(String path) {
        File file = new File(path);
        if (!file.exists()) {
            return true;
        }
        if (!file.isDirectory()) {
            return file.delete();
        }
        return this.deleteChildren(file) && file.delete();
    }

    private boolean deleteChildren(File dir) {
        File[] children = dir.listFiles();
        boolean childrenDeleted = true;
        for (int i = 0; children != null && i < children.length; i++) {
            File child = children[i];
            if (child.isDirectory()) {
                childrenDeleted = this.deleteChildren(child) && childrenDeleted;
            }
            if (child.exists()) {
                childrenDeleted = child.delete() && childrenDeleted;
            }
        }
        return childrenDeleted;
    }


    public void f_enviar_notificacion(String title, String text){
        int mId = 1;
        NotificationManager mNotificationManager;
        mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        Uri defaultSoundUri= RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.ic_chat_green)
                .setContentTitle(title)
                .setContentText(text)
                .setSound(defaultSoundUri);

        mNotificationManager.notify(mId, mBuilder.build());
    }
}
