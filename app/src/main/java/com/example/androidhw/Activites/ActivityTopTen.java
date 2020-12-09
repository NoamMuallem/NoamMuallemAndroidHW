package com.example.androidhw.Activites;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.widget.FrameLayout;

import com.example.androidhw.Fragments.FragmentMap;
import com.example.androidhw.Fragments.FragmentScores;
import com.example.androidhw.R;
import com.example.androidhw.callbacks.LocationCallback;

public class ActivityTopTen extends AppCompatActivity implements LocationCallback {

    private FrameLayout tt_frl_scores, tt_frl_map;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_top_ten);
        findViews();

        FragmentScores fragmentScores = new FragmentScores(this);
        getSupportFragmentManager().beginTransaction().add(R.id.tt_frl_scores,fragmentScores).commit();
    }

    private void findViews() {
        tt_frl_scores = findViewById(R.id.tt_frl_scores);
        tt_frl_map = findViewById(R.id.tt_frl_map);
    }

    @Override
    public void getLocation(double lon, double lat, String name) {
        Log.d("pttt","from topTenActivity: " + lon + " "+ lat);
    }
}