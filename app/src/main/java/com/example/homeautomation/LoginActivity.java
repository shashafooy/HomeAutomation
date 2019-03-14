package com.example.homeautomation;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

import org.w3c.dom.Text;

import java.util.Timer;
import java.util.TimerTask;

//Login
public class LoginActivity extends AppCompatActivity {

    private TextView mTextMessage;
    private Button login;
    private EditText userNameEdit,passwordEdit;
    private TextView errorView;
    private FirebaseAuth mAuth;
    private GoogleSignInClient mGoogleSignInClient;
    private static final String TAG = "GoogleActivity";
    private static final int RC_SIGN_IN = 9001;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_main);
        FirebaseApp.initializeApp(this);


        // Configure Google Sign In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this,gso);

        mAuth = FirebaseAuth.getInstance();







        login = findViewById(R.id.loginBtn);
        userNameEdit=findViewById(R.id.username_edit);
        passwordEdit=findViewById(R.id.password_Edit);
        errorView=findViewById(R.id.error_TextView);
        errorView.setVisibility(View.INVISIBLE);

        //TODO make google sign in button
        //TODO make system id field if user isn't associated with a system
        login.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                //TODO check if username and password are in database
                signIn();
//                if(userNameEdit.getText().toString().equals("admin") &&
//                    passwordEdit.getText().toString().equals("admin")){
//                    //Toast.makeText(getApplicationContext(),"Logging In",Toast.LENGTH_SHORT).show();
//                    //TODO connect to server

//                    new Timer().schedule(new TimerTask() {
//                        @Override
//                        public void run() {
//                            finish();
//                        }
//                    },5000);
//                }else{
//                    errorView.setVisibility(View.VISIBLE);
//                }
            }
        });



    }

    @Override
    protected void onStart() {
        super.onStart();
        updateUI(mAuth.getCurrentUser());
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);
                firebaseAuthWithGoogle(account);
            } catch (ApiException e) {
                // Google Sign In failed, update UI appropriately
                Log.w(TAG, "Google sign in failed", e);
                // [START_EXCLUDE]
                updateUI(null);
                // [END_EXCLUDE]
            }
        }else if (requestCode == Constants.LOGOUT_REQUEST){
            mAuth.signOut();
            mGoogleSignInClient.revokeAccess();
            updateUI(null);
        }
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        Log.d(TAG, "firebaseAuthWithGoogle:" + acct.getId());

        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithCredential:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            continueToApp();
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithCredential:failure", task.getException());
                            Snackbar.make(findViewById(R.id.error_TextView), "Authentication Failed.", Snackbar.LENGTH_SHORT).show();
                            updateUI(null);
                        }

                        // ...
                    }
                });
    }

    private void continueToApp() {
        //TODO check if user is associated with system, if not, prompt for system id
        startActivityForResult(new Intent(LoginActivity.this,LightingActivity.class), 0);
    }

    private void updateUI(FirebaseUser user){
        if(user !=null){
            userNameEdit.setText(user.getDisplayName());
            user.getUid(); //TODO somehow associate this with home ID?
        }

    }

    private void signIn() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }
}
