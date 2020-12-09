package com.example.androidhw.Activites;

import android.app.Application;

import com.example.androidhw.utils.MySignal;
import com.example.androidhw.utils.SP;

//this class will be instantiated when app starts
//it will initialize all utils and pass context to them
public class App extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        SP.init(this);
        MySignal.init(this);
    }

}
