package com.example.androidhw.Activites;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.widget.FrameLayout;

import com.example.androidhw.Fragments.FragmentMaps;
import com.example.androidhw.Fragments.FragmentScores;
import com.example.androidhw.R;
import com.example.androidhw.callbacks.LocationCallback;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;

public class ActivityTopTen extends AppCompatActivity implements LocationCallback {

    private FrameLayout tt_frl_scores, tt_frl_map;
    private FragmentMaps fragmentMaps;
    private FragmentScores fragmentScores;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_top_ten);
        findViews();

        fragmentMaps = new FragmentMaps();
        getSupportFragmentManager().beginTransaction().add(R.id.tt_frl_map,fragmentMaps).commit();


        fragmentScores = new FragmentScores(this);
        getSupportFragmentManager().beginTransaction().add(R.id.tt_frl_scores,fragmentScores).commit();
    }

    private void findViews() {
        tt_frl_scores = findViewById(R.id.tt_frl_scores);
        tt_frl_map = findViewById(R.id.tt_frl_map);
    }

    @Override
    public void getLocation(double lon, double lat, String name) {
        //clear privies markers
        fragmentMaps.getGoogleMap().clear();
        //create new location and marker
        LatLng location = new LatLng(lat, lon);
        fragmentMaps.getGoogleMap().addMarker(new MarkerOptions().position(location).title(name));
        //view and zoom changes
        fragmentMaps.getGoogleMap().moveCamera(CameraUpdateFactory.newLatLng(location));
        fragmentMaps.getGoogleMap().setMinZoomPreference((float)250.50);
    }
}