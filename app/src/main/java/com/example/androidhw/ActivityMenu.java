package com.example.androidhw;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class ActivityMenu extends AppCompatActivity {

    FirebaseUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        //set up action bar
        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("Game Menu");

        user = FirebaseAuth.getInstance().getCurrentUser();

        Toast.makeText(ActivityMenu.this,user.getEmail(),Toast.LENGTH_SHORT).show();
    }
}