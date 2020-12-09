package com.example.androidhw.Fragments;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.androidhw.R;
import com.example.androidhw.callbacks.LocationCallback;
import com.example.androidhw.classes.Winner;
import com.example.androidhw.utils.SP;
import com.google.gson.Gson;

import java.util.ArrayList;

import static android.content.Context.MODE_PRIVATE;

public class FragmentScores extends Fragment {

    private ArrayList<Winner> ttData;
    private String [] ttStringArray;
    private ListView scores_liv_scores_list;
    private LocationCallback locationCallback;
    private ArrayAdapter<String> adapter;

    public FragmentScores(LocationCallback locationCallback) {
        this.locationCallback = locationCallback;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        //set up Gson
        Gson gson = new Gson();
        //scores keys: 0-9 values are winners objects in json string format
        ttData = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            //get data from shared preferences
            String temp = SP.getInstance().getString(i+"","");
            if(!temp.isEmpty()){
                ttData.add(gson.fromJson(temp,Winner.class));
            }else{
                break;
            }
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_scores, container, false);
        findViews(view);
        init();
        return view;
    }

    private void init() {
        //convert data to string format and put in String[]
        ttStringArray = new String[ttData.size()];
        for (int i = 0; i < ttData.size(); i++) {
            ttStringArray[i] = (i+1) + ".    " + ttData.get(i).getName() + ": " + ttData.get(i).getScore();
        }
        adapter = new ArrayAdapter<String>(getContext(), R.layout.scores_text_view, R.id.listView_tev, ttStringArray);
        scores_liv_scores_list.setAdapter(adapter);


        scores_liv_scores_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                locationCallback.getLocation(ttData.get(position).getLon(),ttData.get(position).getLat(), ttData.get(position).getName());
            }
        });


    }

    private void findViews(View view) {
        scores_liv_scores_list = view.findViewById(R.id.scores_liv_scores_list);
    }
}