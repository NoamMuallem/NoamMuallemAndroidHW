package com.example.androidhw.Activites;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.androidhw.R;
import com.example.androidhw.classes.Winner;
import com.example.androidhw.utils.MySignal;
import com.example.androidhw.utils.PermissionManager;
import com.example.androidhw.utils.SP;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Collections;

public class ActivityGameOver extends AppCompatActivity{

    //##############################################variables

    //data from previous intent
    public static final String WINNER = "WINNER"; //String

    //for permissions
    //private static final int LOCATION_REQUEST_CODE = 100;

    //views
    private Button game_over_btn_go_to_menu, game_over_btn_go_to_top_ten;
    private TextView game_over_lbl_msg;

    //winner from game activity
    private Winner winner;

    private ArrayList<Winner> ttArray;
    private Gson gson;

    //##############################################initialization

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_over);
        findViews();
        init();
        updateTopTen();
    }

    private void init() {
        //set up Gson
        gson = new Gson();
        //scores keys: 0-9 values are winners objects in json string format
        ttArray = new ArrayList<>();
        //get winner from privies activity
        winner = gson.fromJson(getIntent().getStringExtra(this.WINNER), Winner.class);
        if (winner.isDrew()) {
            game_over_lbl_msg.setText(winner.getName() + " are both the winners!");
        } else {
            game_over_lbl_msg.setText(winner.getName() + " is the winner!");
        }

        game_over_btn_go_to_top_ten.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //close activity, the menu activity and sign in/ log in activity is still opened
                startActivity(new Intent(ActivityGameOver.this, ActivityTopTen.class));
                finish();
            }
        });

        game_over_btn_go_to_menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //menu is already open, just finish this intent
                finish();
            }
        });


    }

    private void findViews() {
        game_over_btn_go_to_menu = findViewById(R.id.game_over_btn_go_to_menu);
        game_over_btn_go_to_top_ten = findViewById(R.id.game_over_btn_go_to_top_ten);
        game_over_lbl_msg = findViewById(R.id.game_over_lbl_msg);
    }

    //check if new winner can get in the top ten
    private void updateTopTen() {
        for (int i = 0; i < 10; i++) {
            String temp = SP.getInstance().getString(i + "", "");
            if (!temp.isEmpty()) {
                ttArray.add(gson.fromJson(temp, Winner.class));
            } else {
                break;
            }
        }
        //check to see if entered the top ten
        if (ttArray.size() < 10 || ttArray.get(9).getScore() <= winner.getScore()) {
            //get current location
            if (!PermissionManager.getInstance().checkLocationPermissions(this)) {
                PermissionManager.getInstance().requestLocationPermission(this);
            } else {
                grabLocation();
            }
        }
    }

    //inserting new winner to top ten
    private void insertNewWinner(){
        //entered to the top ten now ttArray contains 11 objects with indexes from 0 - 10
        ttArray.add(winner);
        //sort
        Collections.sort(ttArray);
        //remove redundant - only if size of ttArray is 11, else throw null pointer exception on remove(10)
        if (ttArray.size() == 11) {
            ttArray.remove(10);
        }
        //apply new changes
        for (int i = 0; i < ttArray.size(); i++) {
            SP.getInstance().putString(i + "", gson.toJson(ttArray.get(i), Winner.class));
        }
        MySignal.getInstance().MakeToastMsgLong("congratulations, new record!");
    }

    //when location permission window close (with approve or deny)
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
            if(PermissionManager.getInstance().getLocationRequestCode() == requestCode) {
                //location - check we have permissions
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    //permission granted
                    grabLocation();
                } else {
                    //permission denied
                    MySignal.getInstance().MakeToastMsgLong("please enable location permissions to enter to top ten table");
                }
            }
    }

    //take location data, *****also executing insertNewWinner***** (to avoid extra callbacks)
    private void grabLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        LocationServices.getFusedLocationProviderClient(this).getLastLocation()
                .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        // Got last known location. In some rare situations this can be null.
                        if (location != null) {
                            // Logic to handle location object
                            winner.setLat(location.getLatitude());
                            winner.setLon(location.getLongitude());
                            insertNewWinner();
                            return;
                        }else{
                            MySignal.getInstance().MakeToastMsgLong("could not get location");
                        }
                    }
                });
    }

}