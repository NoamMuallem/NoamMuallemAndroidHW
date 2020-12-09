package com.example.androidhw.Activites;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.example.androidhw.R;

public class ActivityMenu extends AppCompatActivity {

    private Button menu_btn_new_game, menu_btn_top_ten;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        findViews();
        viewsInit();
    }

    private void viewsInit() {
        menu_btn_new_game.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ActivityMenu.this, ActivityGame.class));
            }
        });

        menu_btn_top_ten.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ActivityMenu.this, ActivityTopTen.class));
            }
        });
    }

    private void findViews() {
        menu_btn_new_game = findViewById(R.id.menu_btn_new_game);
        menu_btn_top_ten = findViewById(R.id.menu_btn_top_ten);
    }
}