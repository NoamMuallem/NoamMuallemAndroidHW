package com.example.androidhw.utils;

import android.content.Context;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.widget.Toast;

public class MySignal {

    private static MySignal instance;
    private Context context;

    public static MySignal getInstance() {
        return instance;
    }

    private MySignal(Context context) {
        //if we tak context a reference to context in some activity still exists
        //so to allow java garbage collector to delete unheeded activities
        //we take the application context from the context passed to here
        this.context = context.getApplicationContext();
    }

    public static void init(Context context) {
        //to allow init only for the first time - singleton design pattern
        //not because there is one instance but because we wont to be able to
        //instantiate only one and to use only that one instance
        if (instance == null) {
            //if we tak context a reference to context in some activity still exists
            //so to allow java garbage collector to delete unheeded activities
            //we take the application context from the context passed to here
            instance = new MySignal(context);
        }
    }

    public void vibrate() {
        /*
        to be used with (in manifest):
        <uses-permission android:name="android.permission.VIBRATE" />*/
        Vibrator v = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            v.vibrate(VibrationEffect.createOneShot(500, VibrationEffect.DEFAULT_AMPLITUDE));
        } else {
            //deprecated in API 26
            v.vibrate(500);
        }
    }

    public void play(int id){
        MediaPlayer mp = MediaPlayer.create(context, id);
        mp.start();
    }

    public void shortToast(String msg){
        Toast.makeText(context,msg,Toast.LENGTH_SHORT).show();
    }

    public void longToast(String msg){
        Toast.makeText(context,msg,Toast.LENGTH_LONG).show();
    }
}