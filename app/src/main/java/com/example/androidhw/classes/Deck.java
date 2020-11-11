package com.example.androidhw.classes;

import android.util.Log;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

public class Deck {

     private ArrayList<Card> cards;

    public Deck() {
        this.cards = new ArrayList<>();
        //start from 2 for the assets name
        //cards start from 2 - 2
        //and ends with 14 - ace
        for (int i = 2; i < 15; i++) {
            this.cards.add(new Card(i,"a"));
            this.cards.add(new Card(i,"b"));
            this.cards.add(new Card(i,"c"));
            this.cards.add(new Card(i,"d"));
        }
        //deck with 13 * 4 different cards
        //shuffle deck
        Collections.shuffle(cards);
    }

    public ArrayList<Card> getFirstHalf() {
        return new ArrayList<>(this.cards.subList(0,26));
    }

    public ArrayList<Card> getSecondHalf() {
        return  new ArrayList (this.cards.subList(26,52));
    }

    public ArrayList<Card> getCards() {
        return cards;
    }
}
