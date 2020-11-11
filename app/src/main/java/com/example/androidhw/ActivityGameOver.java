package com.example.androidhw;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class ActivityGameOver extends AppCompatActivity {

    public static final String WINNER_NUM = "WINNER_NUM";

    private Button game_over_btn_new_game;
    private TextView game_over_lbl_msg;
    private int winner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_over);
        findViews();
        init();
    }

    private void init() {
        //get winner from privies intent
        winner = getIntent().getIntExtra(WINNER_NUM,3);
        //inject winner in msg IF NEEDED (its a drew by default)
        //TODO:take a string from last intent as a msg containing the player name
        game_over_lbl_msg.setText("Congratulation " + winner + "You Wan!");
        //new game button click
        game_over_btn_new_game.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent gameIntent = new Intent(ActivityGameOver.this, ActivityGame.class);
                startActivity(gameIntent);
                finish();
            }
        });


    }

    private void findViews() {
        game_over_btn_new_game = findViewById(R.id.game_over_btn_new_game);
        game_over_lbl_msg = findViewById(R.id.game_over_lbl_msg);
    }
}