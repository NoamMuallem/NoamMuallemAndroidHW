package com.example.androidhw.classes;

import android.content.Context;
import android.content.res.Resources;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;


public class CardGame {
    private Player player1, player2;
    private Deck deck;
    private int numTurns;

    public CardGame() {
        //create a new deck
        this.deck = new Deck();
        //initialize players decks
        this.player1 = new Player(this.deck.getFirstHalf());
        this.player2 = new Player(this.deck.getSecondHalf());
        //initialize turns, gameOver and winner
        int numTurns = 0;
    }

    public void playATurn(ImageView card1image, TextView player1Score, ImageView card2image, TextView player2Score, Context context){
        //get cards from decks
        Card player1card = player1.drew();
        Card player2card = player2.drew();
        //get resources by name
        Resources resources = context.getResources();
        final int player1cardId = resources.getIdentifier(player1card.getAssetName(), "drawable", context.getPackageName());
        final int player2cardId = resources.getIdentifier(player2card.getAssetName(), "drawable", context.getPackageName());
        //set new image assets
        card1image.setImageResource(player1cardId);
        card2image.setImageResource(player2cardId);
        //update user score and update score textView
        if(player1card.getNumber()>player2card.getNumber()){
            this.player1.wins();
            player1Score.setText(this.player1.getScore()+"");
        }else if(player1card.getNumber()<player2card.getNumber()){
            this.player2.wins();
            player2Score.setText(this.player2.getScore()+"");
        }else{
            this.player1.wins();
            player1Score.setText(this.player1.getScore()+"");
            this.player2.wins();
            player2Score.setText(this.player2.getScore()+"");
        }
        this.numTurns++;
    }

    public int getNumTurns() {
        return numTurns;
    }

    public int getWinner() {
        if(this.player1.getScore()>this.player2.getScore()){
            return 1;
        }else if(this.player1.getScore()<this.player2.getScore()){
            return 2;
        }else{
            return 0;
        }
    }

    public int getPlayer1score(){
        return this.player1.getScore();
    }

    public int getPlayer2score(){
        return this.player2.getScore();
    }
}
