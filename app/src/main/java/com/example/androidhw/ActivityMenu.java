package com.example.androidhw;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class ActivityMenu extends AppCompatActivity {

    Button menu_btn_start;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        //set up action bar
        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("Game Menu");

        findViews();
        init();

    }

    private void init() {
        menu_btn_start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ActivityMenu.this, ActivityGame.class));
            }
        });
    }

    private void findViews() {
        menu_btn_start = findViewById(R.id.menu_btn_start);
    }
}