package com.example.androidhw.Activites;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.example.androidhw.R;
import com.example.androidhw.classes.CardGame;
import com.example.androidhw.classes.Winner;
import com.example.androidhw.utils.MySignal;
import com.example.androidhw.utils.PermissionManager;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.google.gson.Gson;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;

public class ActivityGame extends AppCompatActivity {

    //progress dialog for image uploading
    ProgressDialog pd;

    //views
    private TextView game_lbl_score1, game_lbl_name1, game_lbl_score2, game_lbl_name2;
    private ImageView game_imv_player1_card, game_imv_player2_card;
    private ImageButton game_button_play_turn;
    private ProgressBar game_prb_progress;
    private RelativeLayout game_rel_background;

    //cardGame
    private CardGame cardGame;

    //timer
    private Timer timer;
    private final int DELAY = 200;
    //to indicate in what state we are (not to stop when not playing and get exception)
    private boolean playing;

    //********************************initialization
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);
        findViews();
        init();
    }

    @Override
    protected void onStop() {
        //don't stop game if game is playing (like when on pause and pick picture
        //will raise an exception if game on pause and going into stopGame())
        if (playing) {
            stopGame();
        }
        playing = false;
        game_button_play_turn.setImageResource(R.drawable.play);
        super.onStop();
    }

    private void findViews() {
        game_lbl_score1 = findViewById(R.id.game_lbl_score1);
        game_lbl_name1 = findViewById(R.id.game_lbl_name1);
        game_lbl_score2 = findViewById(R.id.game_lbl_score2);
        game_lbl_name2 = findViewById(R.id.game_lbl_name2);
        game_imv_player1_card = findViewById(R.id.game_imv_player1_card);
        game_imv_player2_card = findViewById(R.id.game_imv_player2_card);
        game_button_play_turn = findViewById(R.id.game_button_play_turn);
        game_prb_progress = findViewById(R.id.game_prb_progress);
        game_rel_background = findViewById(R.id.game_rel_background);
    }

    private void init() {
        playing = false;
        //set new game
        cardGame = new CardGame();
        //initialize p1/2imageUrl to "" so if its empty take default images
        //set click listener to play pause button
        game_button_play_turn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                playing = !playing;
                if (playing) {
                    game_button_play_turn.setImageResource(R.drawable.pause);
                    playGame();
                } else {
                    game_button_play_turn.setImageResource(R.drawable.play);
                    stopGame();
                }
            }
        });

        //progress dialog
        pd = new ProgressDialog(ActivityGame.this);

        //final copy of the activity to pass to click listener
        final AppCompatActivity activity = this;

        Glide.with(this).load(R.drawable.background).into(new SimpleTarget<Drawable>() {
            @Override
            public void onResourceReady(Drawable resource, Transition<? super Drawable> transition) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                    game_rel_background.setBackground(resource);
                }
            }
        });

        //play sound for game start
        MySignal.getInstance().play(R.raw.button_press);
    }

    //********************timer functions for game
    private void playGame() {
        timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        playATurn();
                    }
                });
            }
        }, 0, DELAY);
    }

    private void playATurn() {
        if (cardGame.getNumTurns() >= 26) {
            //vibrate
            MySignal.getInstance().vibrate();
            //game is over
            Intent gameOverIntent = new Intent(ActivityGame.this, ActivityGameOver.class);
            //stop timer
            timer.cancel();
            //create a winner object
            //score by the winner score, if its a drew by player 2 score
            int score = cardGame.getWinner() == 1 ? cardGame.getPlayer1score() : cardGame.getWinner() == 2 ? cardGame.getPlayer2score() : cardGame.getPlayer2score();
            //name contain winner's name or both names if its a drew
            String name = cardGame.getWinner() == 1 ? game_lbl_name1.getText().toString() : cardGame.getWinner() == 2 ? game_lbl_name2.getText().toString() : game_lbl_name1.getText().toString() + " and " + game_lbl_name2.getText().toString();
            Winner winner = new Winner(score, name, cardGame.getPlayer1score() == cardGame.getPlayer2score());
            //create a json string of winner object to send to winner intent
            Gson gson = new Gson();
            String winnerJson = gson.toJson(winner);
            gameOverIntent.putExtra(ActivityGameOver.WINNER, winnerJson);
            startActivity(gameOverIntent);
            finish();
        } else {
            /*
            views - for easy update from inside cardGame
            context - for finding resources by name and not id
             */
            cardGame.playATurn(game_imv_player1_card, game_lbl_score1,
                    game_imv_player2_card, game_lbl_score2, this);
            //update progress bar
            game_prb_progress.setProgress(game_prb_progress.getProgress() - 1);
        }
    }

    private void stopGame() {
        timer.cancel();
    }
}