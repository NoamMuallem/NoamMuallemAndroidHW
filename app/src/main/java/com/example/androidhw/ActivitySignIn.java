package com.example.androidhw;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

/*
 * to enable google sign in:
 *  get SHA1 from android studio
 * 1. open Gradle tab on the top right in android studio
 * 2. go to android signinReport
 *  copy SHA1 to Firebase console.
 *
 * 4. in firebase go to authentication
 * 5. in sign in methods click "Google"
 * 6. click on 'project settings' (blue and underlined,
 * in the paragraph above the enable toggle)
 * 7. scroll down and choose add fingerprint
 * 8. past the SHA1 copped from signinReport
 * Done!
 * */


public class ActivitySignIn extends AppCompatActivity {

    //views
    private EditText sign_in_edt_email, sign_in_edt_password;
    private Button sign_in_btn_submit;
    private TextView sign_in_lbl_password_recovery, sign_in_lbl_no_account;
    SignInButton sign_in_btn_google_login;

    //progress dialog for loading
    private ProgressDialog pd;

    //firebase auth
    private FirebaseAuth mAuth;

    //for sign in with google
    GoogleSignInOptions gso;
    private static final int RC_SIGN_IN = 100;
    GoogleSignInClient mGoogleSignInClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);
        findViews();
        init();
    }

    private void findViews() {
        sign_in_edt_email = findViewById(R.id.sign_in_edt_email);
        sign_in_edt_password = findViewById(R.id.sign_in_edt_password);
        sign_in_btn_submit = findViewById(R.id.sign_in_btn_submit);
        sign_in_lbl_password_recovery = findViewById(R.id.sign_in_lbl_password_recovery);
        sign_in_btn_google_login = findViewById(R.id.sign_in_btn_google_login);
        sign_in_lbl_no_account = findViewById(R.id.sign_in_lbl_no_account);
    }

    //add listeners set up and firebase
    private void init() {
        //set up action bar
        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("Sigh In");

        //if user don't have an account redirect him to the correct activity
        sign_in_lbl_no_account.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ActivitySignIn.this, ActivitySignUp.class));
                finish();
            }
        });

        //initialize the FirebaseAuth instance
        mAuth = FirebaseAuth.getInstance();

        //init progress bar
        pd = new ProgressDialog(this);

        //check valid inputs and sign user in
        sign_in_btn_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //data inputs
                String email = sign_in_edt_email.getText().toString().trim();
                String password = sign_in_edt_password.getText().toString().trim();
                //validation
                if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
                    //email address not valid
                    sign_in_edt_email.setError("Email address not valid");
                    sign_in_edt_email.setFocusable(true);
                }else if(password.length()<6){
                    //password to short
                    sign_in_edt_password.setError("Password to short");
                    sign_in_edt_password.setFocusable(true);
                }else{
                    loginUser(email, password);
                }
            }
        });

        //password recovery
        sign_in_lbl_password_recovery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showRecoveryPasswordDialog();
            }
        });

        // Configure Google Sign In
        gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        sign_in_btn_google_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //begin google sign in
                Intent signInIntent = mGoogleSignInClient.getSignInIntent();
                startActivityForResult(signInIntent, RC_SIGN_IN);
            }
        });
    }

    //when "choose google account to sign in with" dialog is closed
    //this method runs
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);
                firebaseAuthWithGoogle(account.getIdToken());
            } catch (ApiException e) {
                // Google Sign In failed, update UI appropriately
                Toast.makeText(ActivitySignIn.this,"Google Sign In failed",Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void firebaseAuthWithGoogle(String idToken) {
        //show progress dialog
        pd.setMessage("Signing In...");
        pd.show();
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            FirebaseUser user = mAuth.getCurrentUser();
                            pd.dismiss();
                            startActivity(new Intent(ActivitySignIn.this, ActivityMenu.class));
                            finish();
                        } else {
                            // If sign in fails, display a message to the user.
                            pd.dismiss();
                            Toast.makeText(ActivitySignIn.this,"Authentication Failed",Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    //popup dialog to enter mail for password recovery
    private void showRecoveryPasswordDialog() {
        //alert dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Recover Password");

        //set linear layout
        LinearLayout linearLayout = new LinearLayout(this);

        //views to set in dialog
        EditText dialog_edt_email = new EditText(this);
        dialog_edt_email.setHint("Email");
        dialog_edt_email.setInputType(InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
        dialog_edt_email.setMinEms(16);

        //add dialog_edt_email to linear layout
        linearLayout.addView(dialog_edt_email);
        linearLayout.setPadding(10,10,10,10);

        //add linear layout to dialog
        builder.setView(linearLayout);

        //button recover
        builder.setPositiveButton("Recover", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //input data
                String email = dialog_edt_email.getText().toString().trim();
                if(Patterns.EMAIL_ADDRESS.matcher(email).matches()){
                    beginRecovery(email);
                }
            }
        });

        //button cancel
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //dismiss dialog
                dialog.dismiss();
            }
        });

        //show dialog
        builder.create().show();
    }

    //the actual password recovery method
    private void beginRecovery(String email) {
        //show progress dialog
        pd.setMessage("Sending recovery Email...");
        pd.show();
        mAuth.sendPasswordResetEmail(email)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                pd.dismiss();
                if(task.isSuccessful()){
                    Toast.makeText(ActivitySignIn.this,"Email sent",Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(ActivitySignIn.this,"Failed to send Email",Toast.LENGTH_SHORT).show();
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                pd.dismiss();
                //get and show proper error massage
                Toast.makeText(ActivitySignIn.this,""+e.getMessage(),Toast.LENGTH_SHORT).show();
            }
        });

    }

    //login user with email and password
    private void loginUser(String email, String password) {
        //show progress dialog
        pd.setMessage("Signing In...");
        pd.show();
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            FirebaseUser user = mAuth.getCurrentUser();
                            pd.dismiss();
                            startActivity(new Intent(ActivitySignIn.this, ActivityMenu.class));
                            finish();
                        } else {
                            // If sign in fails, display a message to the user.
                            pd.dismiss();
                            Toast.makeText(ActivitySignIn.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                }).addOnFailureListener(new OnFailureListener(){
            @Override
            public void onFailure(@NonNull Exception e) {
                //error, dismiss progress dialog and get and show the error massage
                pd.dismiss();
                Toast.makeText(ActivitySignIn.this,""+e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}