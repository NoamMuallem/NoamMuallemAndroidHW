package com.example.androidhw.classes;

public class Card {
    private int number;
    private String shape;
    private String assetName;

    public Card(int number, String shape) {
        this.number = number;
        this.shape = shape;
        this.assetName = shape+number;
    }

    public int getNumber() {
        return number;
    }

    public String getAssetName() {
        return assetName;
    }
}
