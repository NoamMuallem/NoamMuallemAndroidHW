package com.example.androidhw.classes;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

public class Deck {

     private ArrayList<Card> cards;

    public Deck() {
        cards = new ArrayList<>();
        //start from 1 for the assets name
        for (int i = 1; i < 14; i++) {
            Card tempA = new Card(i,"a");
            Card tempB = new Card(i,"b");
            Card tempC = new Card(i,"c");
            Card tempD = new Card(i,"d");
        }
        //deck with 13 * 4 different cards
        //shuffle deck
        Collections.shuffle(cards);
    }

    public ArrayList<Card> getFirstHalf() {
        return (ArrayList<Card>) cards.subList(0,26);
    }

    public ArrayList<Card> getSecondHalf() {
        return (ArrayList<Card>) cards.subList(26,52);
    }


}
