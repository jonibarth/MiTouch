package com.example.grupo110.mitouchmobile;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageButton;

public class GaleriaActivity extends AppCompatActivity {
    ImageButton galeria;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_galeria);
        addImageButtons();
        setImageNextAndRepeat();


        galeria.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent siguiente = new Intent(GaleriaActivity.this, MenuActivity.class);
                startActivity(siguiente);
                finish();
            }
        });
    }

    private void setImageNextAndRepeat() {
        galeria .setImageResource(R.drawable.hardcode_galeria);
    }

    private void addImageButtons() {
        galeria= (ImageButton) findViewById(R.id.imageButtonGaleria);

    }

}
