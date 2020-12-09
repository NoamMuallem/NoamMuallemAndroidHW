package com.example.androidhw.Activites;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.example.androidhw.R;

public class ActivityMain extends AppCompatActivity {

    private Button main_btn_sign_in, main_btn_sign_up;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        findViews();
        init();
    }

    private void init() {
        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("Noam Muallem HW");
        main_btn_sign_in.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ActivityMain.this, ActivitySignIn.class));
            }
        });

        main_btn_sign_up.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ActivityMain.this, ActivitySignUp.class));
            }
        });
    }

    private void findViews() {
        main_btn_sign_in = findViewById(R.id.main_btn_sign_in);
        main_btn_sign_up = findViewById(R.id.main_btn_sign_up);
    }
}