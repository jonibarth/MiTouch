package com.example.grupo110.mitouchmobile;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageButton;

public class ChatActivity extends AppCompatActivity {
    ImageButton chat;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        addImageButtons();
        setImageNextAndRepeat();


        chat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void setImageNextAndRepeat() {
        chat .setImageResource(R.drawable.hardcode_chat);
    }

    private void addImageButtons() {
        chat= (ImageButton) findViewById(R.id.imageButtonChat);

    }

}