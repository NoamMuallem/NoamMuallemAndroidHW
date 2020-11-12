package com.example.androidhw;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

public class ActivityGameOver extends AppCompatActivity {

    //data from previous intent
    public static final String PLAYER_ONE_NAME = "WINNER_NAME"; //String
    public static final String PLAYER_TWO_NAME = "IMAGE_URL_WINNER"; //String
    public static final String WINNER = "WINNER_NUM"; //int

    //views
    private Button game_over_btn_new_game;
    private TextView game_over_lbl_msg;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_over);
        findViews();
        init();
    }

    private void init() {
        //initialize with data from last intent
        int winner = getIntent().getIntExtra(WINNER,10);
        if(winner == 1){
                String name = getIntent().getStringExtra(PLAYER_ONE_NAME);
                game_over_lbl_msg.setText("Congratulations " + name + " You Are The Winner");
        }else if(winner == 2){
                String name = getIntent().getStringExtra(PLAYER_TWO_NAME);
                game_over_lbl_msg.setText("Congratulations " + name + " You Are The Winner");
        }else{
            String name1 = getIntent().getStringExtra(PLAYER_TWO_NAME);
            String name2 = getIntent().getStringExtra(PLAYER_ONE_NAME);
            game_over_lbl_msg.setText(name1 + " "+ name2 + " You Are In A Drew");
        }

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