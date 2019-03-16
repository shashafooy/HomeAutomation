package com.example.homeautomation;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

//Login
public class LoginActivity extends AppCompatActivity {

    private Button continueBtn,signoutBtn;
    private EditText userNameEdit;
    private TextView errorView, noIDView, accountNameView;
    private FirebaseAuth mAuth;
    private FirebaseDatabase database;
    private GoogleSignInClient mGoogleSignInClient;
    private SignInButton googleSignInBtn;
    private ProgressBar progressBar;
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
        database = FirebaseDatabase.getInstance();






        progressBar = findViewById(R.id.progressBar);
        progressBar.setVisibility(View.GONE);
        continueBtn = findViewById(R.id.continueBtn);
        continueBtn.setEnabled(false);
        continueBtn.setBackgroundColor(ContextCompat.getColor(this,R.color.white_90));
        userNameEdit=findViewById(R.id.username_edit);
        googleSignInBtn=findViewById(R.id.google_button);
        errorView=findViewById(R.id.error_TextView);
        errorView.setVisibility(View.GONE);
        noIDView=findViewById(R.id.noID_TextView);
        accountNameView=findViewById(R.id.googleUser_view);
        signoutBtn=findViewById(R.id.google_signout);



        //TODO make system id field if user isn't associated with a system
        continueBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                //TODO check if username and password are in database
                if(userNameEdit.getText().toString().equals("admin")){
                    startActivityForResult(new Intent(LoginActivity.this,LightingActivity.class),0);
                }else{
                    progressBar.setVisibility(View.VISIBLE);
                    try {
                        myDatabase.createDatabase(userNameEdit.getText().toString());
                        continueToApp();
                    } catch (FirebaseException e) {
                        e.printStackTrace();
                    }
                }
            }
        });

        googleSignInBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signIn();
            }
        });

        signoutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mGoogleSignInClient.signOut();
                mAuth.signOut();
                updateUI(null);
            }
        });
        userNameEdit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                continueBtn.setEnabled(true);
                continueBtn.setBackgroundColor(ContextCompat.getColor(LoginActivity.this,R.color.white_50));
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
                fireBaseAuthWithGoogle(account);
            } catch (ApiException e) {
                // Google Sign In failed, update UI appropriately
                Log.w(TAG, "Google sign in failed", e);
                // [START_EXCLUDE]
                updateUI(null);
                // [END_EXCLUDE]
            }
        }else if (requestCode == Constants.LOGOUT_REQUEST){
            mAuth.signOut();
            mGoogleSignInClient.signOut();
            updateUI(null);
        }
    }

    private void fireBaseAuthWithGoogle(GoogleSignInAccount acct) {
        Log.d(TAG, "fireBaseAuthWithGoogle:" + acct.getId());

        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithCredential:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            updateUI(user);
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
        progressBar.setVisibility(View.GONE);
        startActivityForResult(new Intent(LoginActivity.this,LightingActivity.class), 0);
    }

    private void updateUI(FirebaseUser user){
        if(user !=null){
            DatabaseReference userRef = database.getReference("Users/" + user.getUid());
            userRef.child("SystemID").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    String systemID = dataSnapshot.getValue(Integer.class).toString();
                    userNameEdit.setText(systemID);
                    if(systemID.isEmpty()){
                        noIDView.setVisibility(View.VISIBLE);
                    }else {
                        noIDView.setVisibility(View.GONE);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Log.d("Database", "error finding data, " + databaseError.getMessage());
                }
            });
            googleSignInBtn.setVisibility(View.GONE);
            accountNameView.setVisibility(View.VISIBLE);
            accountNameView.setText(user.getDisplayName());
            signoutBtn.setVisibility(View.VISIBLE);
        }else{
            userNameEdit.setText(null);
            googleSignInBtn.setVisibility(View.VISIBLE);
            accountNameView.setVisibility(View.GONE);
            signoutBtn.setVisibility(View.GONE);
        }

    }

    private void signIn() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }
}
