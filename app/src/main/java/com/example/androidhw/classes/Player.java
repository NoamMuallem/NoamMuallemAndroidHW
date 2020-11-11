package com.example.androidhw.classes;

import android.util.Log;

import java.util.ArrayList;

public class Player {
    //player score
    private int score;
    //player deck of cards
    private ArrayList<Card> deck;
    //the index of the next available card in deck
    private int counter;
    //player name
    private String name;

    public Player(String name) {
        this.name = name;
    }

    public Player(ArrayList<Card> deck) {
        this.deck = deck;
        this.score = 0;
        this.counter = 0;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public ArrayList<Card> getDeck() {
        return deck;
    }

    public void setDeck(ArrayList<Card> deck) {
        this.deck = deck;
    }

    public Card drew(){
        Card temp = deck.get(this.counter);
        this.counter++;
        return temp;
    }

    public void wins(){
        this.score++;
    }
}
