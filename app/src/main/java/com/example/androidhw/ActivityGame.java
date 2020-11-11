package com.example.androidhw;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.androidhw.classes.CardGame;
import com.example.androidhw.classes.Deck;
import com.example.androidhw.classes.Player;

public class ActivityGame extends AppCompatActivity {

    private TextView game_lbl_score1, game_lbl_name1, game_lbl_score2, game_lbl_name2;
    private ImageView game_imv_player1_card, game_imv_player2_card;
    private ImageButton game_button_play_turn;
    private CardGame cardGame;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);
        findVies();
        init();
    }

    private void init() {
        //set new game
        cardGame = new CardGame();
        //set click listener to play button
        game_button_play_turn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(cardGame.getNumTurns() >= 26){
                    //game is over
                    Intent gameOverIntent = new Intent(ActivityGame.this, ActivityGameOver.class);
                    gameOverIntent.putExtra(ActivityGameOver.WINNER_NUM,cardGame.getWinner());
                    startActivity(gameOverIntent);
                    finish();
                }else{
                    cardGame.playATurn(game_imv_player1_card, game_lbl_score1, game_imv_player2_card, game_lbl_score2, ActivityGame.super.getBaseContext());
                }
            }
        });
    }

    private void findVies() {
        game_lbl_score1 = findViewById(R.id.game_lbl_score1);
        game_lbl_name1 = findViewById(R.id.game_lbl_name1);
        game_lbl_score2 = findViewById(R.id.game_lbl_score2);
        game_lbl_name2 = findViewById(R.id.game_lbl_name2);
        game_imv_player1_card = findViewById(R.id.game_imv_player1_card);
        game_imv_player2_card = findViewById(R.id.game_imv_player2_card);
        game_button_play_turn = findViewById(R.id.game_button_play_turn);
    }
}