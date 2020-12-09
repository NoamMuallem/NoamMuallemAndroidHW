package com.example.androidhw.Activites;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.androidhw.R;
import com.example.androidhw.utils.MySignal;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class ActivitySignUp extends AppCompatActivity {

    //views
    private EditText sign_up_edt_email, sign_up_edt_password;
    private Button sign_up_btn_submit;
    private TextView sign_in_lbl_have_account;

    //progress dialog for loading
    private ProgressDialog pd;

    //firebase
    private FirebaseAuth mAuth;
    FirebaseDatabase database;
    DatabaseReference myRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        findViews();
        init();
    }

    private void findViews() {
        sign_up_edt_email = findViewById(R.id.sign_up_edt_email);
        sign_up_edt_password = findViewById(R.id.sign_up_edt_password);
        sign_up_btn_submit = findViewById(R.id.sign_up_btn_submit);
        sign_in_lbl_have_account = findViewById(R.id.sign_in_lbl_have_account);
    }

    private void init() {
        //set up action bar
        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("Sigh Up");

        //init firebase auth
        mAuth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        myRef = database.getReference("Users");

        //init progress bar
        pd = new ProgressDialog(this);

        //if user have an account redirect him to sign in activity
        sign_in_lbl_have_account.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ActivitySignUp.this, ActivitySignIn.class));
                finish();
            }
        });

        //sign up user with email and password with firebase
        sign_up_btn_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = sign_up_edt_email.getText().toString().trim();
                String password = sign_up_edt_password.getText().toString().trim();
                //validate
                if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
                    //set error and focus on edit email text
                    sign_up_edt_email.setError("Invalid Email Address");
                    sign_up_edt_email.setFocusable(true);
                }else if(password.length()<6){
                    //set error and focus on password
                    sign_up_edt_password.setError("Password length must be greater then 6");
                    sign_up_edt_password.setFocusable(true);
                }else{
                    registerUser(email, password);
                }
            }
        });
    }

    private void registerUser(String email, String password) {
        //show progress dialog
        pd.setMessage("Signing Up...");
        pd.show();
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            FirebaseUser user = mAuth.getCurrentUser();
                            //dismiss progress dialog
                            pd.dismiss();
                            //save user data
                            HashMap <Object, String> hashMap = new HashMap<>();
                            hashMap.put("uid",user.getUid());
                            hashMap.put("p1name","");
                            hashMap.put("p1image","");
                            hashMap.put("p2name","");
                            hashMap.put("p2image","");
                            myRef.child(mAuth.getUid()).setValue(hashMap);
                            //start game
                            startActivity(new Intent(ActivitySignUp.this, ActivityMenu.class));
                            finish();
                        } else {
                            //dismiss progress dialog
                            pd.dismiss();
                            // If sign in fails, display a message to the user.
                            MySignal.getInstance().MakeToastMsgLong("Authentication failed.");
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                //dismiss progress dialog
                pd.dismiss();
                //error, get error and display it
                MySignal.getInstance().MakeToastMsgShort(""+e.getMessage());
            }
        });
    }
}