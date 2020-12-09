package com.example.androidhw.Activites;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.example.androidhw.R;
import com.example.androidhw.classes.CardGame;
import com.example.androidhw.classes.Player;
import com.example.androidhw.classes.Winner;
import com.example.androidhw.utils.MySignal;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.google.gson.Gson;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;

public class ActivityGame extends AppCompatActivity {

    //the only relevant lines for the CORE HW are:
    // 60-66, 93(99-110), 113-137, all other code is firebase related

    //variable to indicate what we are editing in firebase
    private int playerNameEdit; //which player change name
    private int playerImageEdit;//which player change image

    //progress dialog for image uploading loading
    ProgressDialog pd;

    //views
    private TextView game_lbl_score1, game_lbl_name1, game_lbl_score2, game_lbl_name2;
    private ImageView game_imv_player1_card, game_imv_player2_card, game_imv_p1_avatar, game_imv_p2_avatar;
    private ImageButton game_button_play_turn;
    private FloatingActionButton profile_fab_edit_profile;
    private ProgressBar game_prb_progress;
    private RelativeLayout game_rel_background;
    //cardGame
    private CardGame cardGame;
    //for timer
    private Timer timer;
    private final int DELAY = 1000;
    private boolean playing;

    //firebase
    private FirebaseAuth firebaseAuth;
    private FirebaseUser firebaseUser;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;
    //storage
    private StorageReference storageReference;

    //permission constance - flags to indicate what the user chose to edit so i can generalize the function use
    private static final int CAMERA_REQUEST_CODE = 100;
    private static final int STORAGE_REQUEST_CODE = 200;
    private static final int IMAGE_PICK_GALLERY_CODE = 300;
    private static final int IMAGE_PICK_CAMERA_CODE = 400;
    //arrays of permissions to be requested
    private String cameraPermissions[];
    private String storagePermissions[];
    //uri for picked image
    private Uri image_uri;

    //********************************initialization
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);
        findViews();
        init();
        firebaseInit();
        //set assets from firebase
        updateViewsWithProfileData();
    }

    private void findViews() {
        game_lbl_score1 = findViewById(R.id.game_lbl_score1);
        game_lbl_name1 = findViewById(R.id.game_lbl_name1);
        game_lbl_score2 = findViewById(R.id.game_lbl_score2);
        game_lbl_name2 = findViewById(R.id.game_lbl_name2);
        game_imv_player1_card = findViewById(R.id.game_imv_player1_card);
        game_imv_player2_card = findViewById(R.id.game_imv_player2_card);
        game_button_play_turn = findViewById(R.id.game_button_play_turn);
        profile_fab_edit_profile = findViewById(R.id.profile_fab_edit_profile);
        game_imv_p1_avatar = findViewById(R.id.game_imv_p1_avatar);
        game_imv_p2_avatar = findViewById(R.id.game_imv_p2_avatar);
        game_prb_progress = findViewById(R.id.game_prb_progress);
        game_rel_background = findViewById(R.id.game_rel_background);
    }

    private void init() {
        playing = false;
        //set new game
        cardGame = new CardGame();
        //initialize p1/2imageUrl to "" so if its empty take default images
        //set click listener to play button
        game_button_play_turn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                playing = !playing;
                //TODO:start timer
                if(playing){
                    game_button_play_turn.setImageResource(R.drawable.pause);
                    playGame();
                }else{
                    game_button_play_turn.setImageResource(R.drawable.play);
                    stopGame();
                }
            }
        });

        //init permissions
        cameraPermissions = new String[]{Manifest.permission.CAMERA,Manifest.permission.WRITE_EXTERNAL_STORAGE};
        storagePermissions = new String[]{Manifest.permission.CAMERA,Manifest.permission.WRITE_EXTERNAL_STORAGE};

        //progress dialog
        pd = new ProgressDialog(ActivityGame.this);

        //set edit floating bubble listener
        profile_fab_edit_profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //options to show in dialog
                String options[] = {"edit left picture", "edit right image","Edit left name","Edit right name"};
                //creating a dialog and build it
                AlertDialog.Builder builder = new AlertDialog.Builder(ActivityGame.this);
                //set title
                builder.setTitle("Edit Players Data");
                //set items to dialog
                builder.setItems(options, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //handle dialog items click
                        switch(which){
                            case 0: {
                                //edit profile picture clicked
                                playerImageEdit = 1; //indicate profile picture change
                                pd.setMessage("Updating Left Image");
                                showImagePicDialog();
                            }
                            break;
                            case 1: {
                                playerImageEdit = 2; //indicate the player that changing change
                                //edit cover photo clicked
                                pd.setMessage("Updating Right Image");
                                showImagePicDialog();
                            }
                            break;
                            case 2: {
                                playerNameEdit = 1;
                                //edit profile name clicked
                                pd.setMessage("Updating Left Name");
                                showNameUpdateDialogAndUpload();
                            }
                            break;
                            case 3: {
                                playerNameEdit = 2;
                                //edit phone clicked
                                pd.setMessage("Updating Right Name");
                                showNameUpdateDialogAndUpload();
                            }
                            break;
                        }
                    }
                });
                //create dialog
                builder.create().show();
            }
        });

        Glide.with(this).load(R.drawable.background).into(new SimpleTarget<Drawable>() {
            @Override
            public void onResourceReady(Drawable resource, Transition<? super Drawable> transition) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                    game_rel_background.setBackground(resource);
                }
            }
        });

        //play sound for game start
        MySignal.getInstance().play(R.raw.button_press);
    }

    private void firebaseInit() {
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        firebaseDatabase = FirebaseDatabase.getInstance();
        //where in bucket users are stored
        databaseReference = firebaseDatabase.getReference("Users");
        storageReference = FirebaseStorage.getInstance().getReference();//firebase storage reference
    }

    @Override
    protected void onStop() {
        //don't stop game if game is playing (like when on pause and pick picture
        //will raise an exception if game on pause and going into stopGame())
        if(playing){
            stopGame();
        }
        playing = false;
        game_button_play_turn.setImageResource(R.drawable.play);
        super.onStop();
    }

    //********************timer functions for game
    private void playGame() {
        timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        playATurn();
                    }
                });
            }
        },0,DELAY);

    }

    private void playATurn(){
        if(cardGame.getNumTurns() >= 26){
            //vibrate
            MySignal.getInstance().vibrate();
            //game is over
            Intent gameOverIntent = new Intent(ActivityGame.this, ActivityGameOver.class);
            //stop timer
            timer.cancel();
            //create a winner object
            //score by the winner score, if its a drew by player 2 score
            int score = cardGame.getWinner()==1?cardGame.getPlayer1score():cardGame.getWinner()==2?cardGame.getPlayer2score():cardGame.getPlayer2score();
            //name contain winner's name or both names if its a drew
            String name = cardGame.getWinner()==1?game_lbl_name1.getText().toString():cardGame.getWinner()==2?game_lbl_name2.getText().toString():game_lbl_name1.getText().toString() + " and " + game_lbl_name2.getText().toString();
            Winner winner  = new Winner(score, name, cardGame.getPlayer1score()==cardGame.getPlayer2score());
            //create a json string of winner object to send to winner intent
            Gson gson = new Gson();
            String winnerJson = gson.toJson(winner);
            gameOverIntent.putExtra(ActivityGameOver.WINNER,winnerJson);
            startActivity(gameOverIntent);
            finish();
        }else{
            /*
            views - for easy update from inside cardGame
            context - for finding resources by name and not id
             */
            cardGame.playATurn(game_imv_player1_card, game_lbl_score1, game_imv_player2_card, game_lbl_score2, ActivityGame.super.getBaseContext());
            //update progress bar
            game_prb_progress.setProgress(game_prb_progress.getProgress()-1);
        }
    }

    private void stopGame() {
        timer.cancel();
    }

    //*********************************permissions
    //check if storage permissions is enabled or not
    private boolean checkStoragePermissions(){
        boolean result = ContextCompat.checkSelfPermission(ActivityGame.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == (PackageManager.PERMISSION_GRANTED);
        return result;
    }

    //request on runtime storage permission
    private void requestStoragePermission(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermissions(storagePermissions, STORAGE_REQUEST_CODE);
        }
    }

    //check if camera permissions is enabled or not
    private boolean checkCameraPermissions(){
        boolean result = ContextCompat.checkSelfPermission(ActivityGame.this, Manifest.permission.CAMERA) == (PackageManager.PERMISSION_GRANTED);
        boolean result1 = ContextCompat.checkSelfPermission(ActivityGame.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == (PackageManager.PERMISSION_GRANTED);
        return result && result1;
    }

    //request on runtime camera permission
    private void requestCameraPermission(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermissions(cameraPermissions, CAMERA_REQUEST_CODE);
        }
    }

    //*********************************choosing image from camera or storage
    //show dialog to pick an image - gallery or camera
    private void showImagePicDialog() {
        //options to show in dialog
        String options[] = {"Camera","Gallery"};
        //alert dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(ActivityGame.this);
        //set title
        builder.setTitle("Choose Image Source");
        //set items to dialog
        builder.setItems(options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //handle dialog items click
                switch(which){
                    case 0: {
                        //Take a photo
                        if (!checkCameraPermissions()) {
                            requestCameraPermission();
                        } else {
                            pickFromCamera();
                        }
                    }
                    break;
                    case 1: {
                        //Choose from gallery
                        if (!checkStoragePermissions()) {
                            requestStoragePermission();
                        } else {
                            pickFromGallery();
                        }
                    }
                    break;

                }
            }
        });
        //create dialog
        builder.create().show();
    }

    //intent for pick image from gallery
    private void pickFromGallery() {
        Intent galleryIntent = new Intent(Intent.ACTION_PICK);
        galleryIntent.setType("image/*");
        //when activity finish return this code - so when onRequestPermissionsResult will start when
        //activity is done we will know from when it opened and if we take an image from camera or storage
        startActivityForResult(galleryIntent, IMAGE_PICK_GALLERY_CODE);
    }

    //intent for picking image from camera
    private void pickFromCamera() {
        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.TITLE, "Temp Pic");
        values.put(MediaStore.Images.Media.DESCRIPTION, "Temp Description");
        //put image uri
        image_uri = ActivityGame.this.getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);

        //intent to start camera
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, image_uri);
        //when activity finish return this code - so when onRequestPermissionsResult will start when
        //activity is done we will know from when it opened and if we take an image from camera or storage
        startActivityForResult(cameraIntent, IMAGE_PICK_CAMERA_CODE);
    }

    //this methods runs when permission dialog closes with granted or denial access - only for the first times
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch(requestCode){
            case CAMERA_REQUEST_CODE:{
                //picking from camera - check we have permissions
                if(grantResults.length > 0){
                    boolean cameraAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    boolean writeStorageAccepted = grantResults[1] == PackageManager.PERMISSION_GRANTED;
                    if(cameraAccepted && writeStorageAccepted){
                        //permission granted
                        pickFromCamera();
                    }else{
                        //permission denied
                        MySignal.getInstance().MakeToastMsgLong("please enable camera & storage permissions");
                    }
                }
            }
            break;
            case STORAGE_REQUEST_CODE:{
                //picking from gallery - check we have permissions
                if(grantResults.length > 0){
                    boolean writeStorageAccepted = grantResults[1] == PackageManager.PERMISSION_GRANTED;
                    if(writeStorageAccepted){
                        //permission granted
                        pickFromGallery();
                    }else{
                        //permission denied
                        MySignal.getInstance().MakeToastMsgShort("please enable storage permissions");
                    }
                }
            }
            break;
        }
    }

    //this method will run after picking image from gallery or camera
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == RESULT_OK){
            if(requestCode == IMAGE_PICK_GALLERY_CODE){
                //image picked from gallery - get uri of image
                image_uri = data.getData();
                uploadImage(image_uri);
            }
            if(requestCode == IMAGE_PICK_CAMERA_CODE){
                //image picked from camera - get uri of image
                uploadImage(image_uri);
            }
        }
    }

    //*********************************firebase related functionality
    //fetching info of current user by id and update views
    private void updateViewsWithProfileData() {
        //using orderByChild query to get user that have uid that machs the current user uid
        Query query = databaseReference.orderByChild("uid").equalTo(firebaseUser.getUid());
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                //checks until required data is back
                for(DataSnapshot ds : snapshot.getChildren()){
                    //get data
                    String p1image = "" + ds.child("p1image").getValue();
                    String p2image = "" + ds.child("p2image").getValue();
                    String p1name = "" + ds.child("p1name").getValue();
                    String p2name = "" + ds.child("p2name").getValue();

                    //set data
                    if(!TextUtils.isEmpty(p1name)){
                        game_lbl_name1.setText(p1name);
                    }
                    if(!TextUtils.isEmpty(p2name)){
                        game_lbl_name2.setText(p2name);
                    }

                    if(!TextUtils.isEmpty(p1image)){
                        //to set profile image
                        try{
                            //if image is received
                            Picasso.get().load(p1image).into(game_imv_p1_avatar);
                        }catch(Exception e){
                            //if there are any exceptions show default pic
                            Picasso.get().load(R.drawable.smile).into(game_imv_p1_avatar);
                        }
                    }
                    if(!TextUtils.isEmpty(p2image)){
                        //to set profile image
                        try{
                            //if image is received
                            Picasso.get().load(p2image).into(game_imv_p2_avatar);
                        }catch(Exception e){
                            //if there are any exceptions show default pic
                            Picasso.get().load(R.drawable.excuse).into(game_imv_p2_avatar);
                        }
                    }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void uploadImage(Uri image_uri) {
        //show progress
        pd.show();

        //path and name of image that will be stored in firebase
        //examples for imagePathAndName:
        //left_052982309840
        //right_052982309840
        String imagePathAndName = "Users_photoes_" + "_" + FirebaseAuth.getInstance().getUid();
        //creating new cluster in storage with the specified path
        StorageReference sr2 = storageReference.child(imagePathAndName).child(imagePathAndName+playerImageEdit);
        //saving the image on to it
        sr2.putFile(image_uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                //image was uploaded to storage - now get url and store it in Users database
                Task<Uri> uriTask = taskSnapshot.getStorage().getDownloadUrl();
                while(!uriTask.isSuccessful());
                Uri uri = uriTask.getResult();

                //check if image was uploaded or not and that a url was received
                if(uriTask.isSuccessful()){
                    //image upload - add / update image in Users database
                    HashMap<String, Object> result = new HashMap<>();
                    //first parameter is profileOrCoverPhoto that can be "image" or "cover"
                    //which are keys in Users database and url of image will be saved in one of theme
                    //second parameter is the url string
                    result.put("p"+playerImageEdit+"image"+"",uri.toString());
                    //the first reference that points to Users - get user that matches firebaseUser uid
                    databaseReference.child(FirebaseAuth.getInstance().getUid()).updateChildren(result).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            //url of image stored in Users realtime database successfully - dismiss progress bar
                            pd.dismiss();
                            MySignal.getInstance().MakeToastMsgShort("image updated...");
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            //error adding url to realtime database for user - dismiss progressbar
                            pd.dismiss();
                            MySignal.getInstance().MakeToastMsgShort("error updating image...");

                        }
                    });

                }else{
                    //error in uploading to storage
                    pd.dismiss();
                    MySignal.getInstance().MakeToastMsgShort("error in image uploading");
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                //there was an error(s), get and show error, dismiss progress dialog
                pd.dismiss();
                MySignal.getInstance().MakeToastMsgLong(e.getMessage());

            }
        });
    }

    private void showNameUpdateDialogAndUpload() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(ActivityGame.this);
        alertDialog.setTitle("Update Name");
        LinearLayout linearLayout = new LinearLayout(ActivityGame.this);
        linearLayout.setOrientation(LinearLayout.VERTICAL);
        linearLayout.setPadding(10,10,10,10);
        //add edit text
        EditText editText = new EditText(ActivityGame.this);
        editText.setHint("Enter Name");
        linearLayout.addView(editText);
        alertDialog.setView(linearLayout);

        //add button in dialog
        alertDialog.setPositiveButton("Update", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //input text from edit text
                String value = editText.getText().toString().trim();
                //validate that a user has entered something
                if(!TextUtils.isEmpty(value)){
                    pd.show();
                    HashMap<String, Object> result = new HashMap<>();
                    result.put("p"+playerNameEdit+"name", value);
                    databaseReference.child(FirebaseAuth.getInstance().getUid()).updateChildren(result).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            pd.dismiss();
                            MySignal.getInstance().MakeToastMsgShort("Updated...");
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            pd.dismiss();
                            MySignal.getInstance().MakeToastMsgLong(""+e.getMessage());
                        }
                    });
                }else{
                    MySignal.getInstance().MakeToastMsgShort("Please Enter Name");
                }
            }
        });
        alertDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        //create and show dialog
        alertDialog.create().show();
    }
}