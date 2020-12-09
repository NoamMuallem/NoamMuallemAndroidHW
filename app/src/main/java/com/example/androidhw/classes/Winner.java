package com.example.androidhw.classes;

public class Winner implements Comparable<Winner> {
    int score;
    String name;
    boolean drew;
    double lon, lat;

    public Winner(int score, String name, boolean drew) {
        this.score = score;
        this.name = name;
        this.drew = drew;
    }

    public int getScore() {
        return score;
    }

    public String getName() {
        return name;
    }

    public boolean isDrew() {
        return drew;
    }

    public double getLon() {
        return lon;
    }

    public void setLon(double lon) {
        this.lon = lon;
    }

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    @Override
    public int compareTo(Winner o) {
        if(this.score>o.getScore()){
            return -1;
        }else if(this.score<o.getScore()){
            return 1;
        }
        return 0;
    }
}
