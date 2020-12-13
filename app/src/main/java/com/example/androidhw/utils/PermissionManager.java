package com.example.androidhw.utils;

import android.Manifest;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.example.androidhw.Activites.ActivityGame;
import com.example.androidhw.Activites.ActivityGameOver;

import static androidx.core.app.ActivityCompat.requestPermissions;

public class PermissionManager {
    ////////////////////////variables
    private static PermissionManager instance;
    private Context context;
    //permission constance - flags to indicate what the user chose to edit so i can generalize the function use
    private static final int CAMERA_REQUEST_CODE = 100;
    private static final int STORAGE_REQUEST_CODE = 200;
    private static final int IMAGE_PICK_GALLERY_CODE = 300;
    private static final int IMAGE_PICK_CAMERA_CODE = 400;
    private static final int LOCATION_REQUEST_CODE = 500;
    //uri for picked image
    private Uri image_uri;

    ////////////////////singleton
    public static PermissionManager getInstance() {
        return instance;
    }

    private PermissionManager(Context context) {
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
            instance = new PermissionManager(context);
        }
    }

    ///////////////////image permissions///////////////////////////////////////////////////////////////////////
    //check if storage permissions is enabled or not
    public boolean checkStoragePermissions() {
        boolean result = ContextCompat.checkSelfPermission(this.context, Manifest.permission.WRITE_EXTERNAL_STORAGE) == (PackageManager.PERMISSION_GRANTED);
        return result;
    }

    //request on runtime storage permission
    public void requestStoragePermission(AppCompatActivity activity) {
        /* to be used with (in manifest):
        <uses-permission android:name="android.permission.CAMERA" />
        <uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE" />*/
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermissions(activity ,new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE}, STORAGE_REQUEST_CODE);
        }
    }

    //check if camera permissions is enabled or not
    public boolean checkCameraPermissions() {
        boolean result = ContextCompat.checkSelfPermission(this.context, Manifest.permission.CAMERA) == (PackageManager.PERMISSION_GRANTED);
        boolean result1 = ContextCompat.checkSelfPermission(this.context, Manifest.permission.WRITE_EXTERNAL_STORAGE) == (PackageManager.PERMISSION_GRANTED);
        return result && result1;
    }

    //request on runtime camera permission
    /* to be used with (in manifest):
        <uses-permission android:name="android.permission.CAMERA" />*/
    public void requestCameraPermission(AppCompatActivity activity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermissions(activity, new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE}, CAMERA_REQUEST_CODE);
        }
    }

    ///////////////image tools - intent for picking image and dialog to choose gallery or camera
    //intent for pick image from gallery
    public void pickFromGallery(AppCompatActivity activity) {
        Intent galleryIntent = new Intent(Intent.ACTION_PICK);
        galleryIntent.setType("image/*");
        //when activity finish return this code - start onRequestPermissionsResult will start
        activity.startActivityForResult(galleryIntent, IMAGE_PICK_GALLERY_CODE);
    }

    //intent for picking image from camera
    public void pickFromCamera(AppCompatActivity activity) {
        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.TITLE, "Temp Pic");
        values.put(MediaStore.Images.Media.DESCRIPTION, "Temp Description");
        //put image uri
        this.image_uri = activity.getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);

        //intent to start camera
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, image_uri);
        //when activity finish return this code - so onRequestPermissionsResult will start
        activity.startActivityForResult(cameraIntent, IMAGE_PICK_CAMERA_CODE);
    }

    //show dialog to pick an image - gallery or camera
    public void showImagePicDialog(AppCompatActivity activity) {
        //options to show in dialog
        String options[] = {"Camera", "Gallery"};
        //alert dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        //set title
        builder.setTitle("Choose Image Source");
        //final copy of the permission manager
        final PermissionManager temp = this;
        //set items to dialog
        builder.setItems(options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //handle dialog items click
                switch (which) {
                    case 0: {
                        //Take a photo
                        if (!checkCameraPermissions()) {
                            temp.requestCameraPermission(activity);
                        } else {
                            temp.pickFromCamera(activity);
                        }
                    }
                    break;
                    case 1: {
                        //Choose from gallery
                        if (!checkStoragePermissions()) {
                            temp.requestStoragePermission(activity);
                        } else {
                            temp.pickFromGallery(activity);
                        }
                    }
                    break;

                }
            }
        });
        //create dialog
        builder.create().show();
    }

    /////////////////////location permissions///////////////////////////////////////////////////////////
    //check if location permissions is enabled or not
    public boolean checkLocationPermissions(AppCompatActivity activity) {
        boolean result = ContextCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_FINE_LOCATION) == (PackageManager.PERMISSION_GRANTED);
        return result;
    }

    //request on runtime location permission
    /*
    to be used with (in manifest):
    <uses-permission android:name="android.permission.ACCESS_FINE_LOCATION" />*/
    public void requestLocationPermission(AppCompatActivity activity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            activity.requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_REQUEST_CODE);
        }
    }

    /////////////////getters and setters
    public static int getCameraRequestCode() {
        return CAMERA_REQUEST_CODE;
    }

    public static int getStorageRequestCode() {
        return STORAGE_REQUEST_CODE;
    }

    public static int getImagePickGalleryCode() {
        return IMAGE_PICK_GALLERY_CODE;
    }

    public void setImage_uri(Uri image_uri) {
        this.image_uri = image_uri;
    }

    public static int getImagePickCameraCode() {
        return IMAGE_PICK_CAMERA_CODE;
    }

    public static int getLocationRequestCode() {
        return LOCATION_REQUEST_CODE;
    }

    public Uri getImage_uri() {
        return image_uri;
    }
}
