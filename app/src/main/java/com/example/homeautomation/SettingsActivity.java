package com.example.homeautomation;

import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.firebase.auth.FirebaseAuth;

import java.util.Timer;
import java.util.TimerTask;

public class SettingsActivity extends AppCompatActivity {
    private TextView systemIdView;
    private Button logoutBtn;
    private FirebaseAuth mAuth;
    private GoogleSignInClient mGoogleSignInClient;

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
            switch (menuItem.getItemId())
            {
                case R.id.action_lighting:
                    startActivityForResult(new Intent(SettingsActivity.this, LightingActivity.class),0);
//                    new Timer().schedule(new TimerTask() {
//                        @Override
//                        public void run() {
//                            finish();
//                        }
//                    },5000);
                    return true;
                case R.id.action_washer_dryer:finish();
                    startActivityForResult(new Intent(SettingsActivity.this, WasherDryerActivity.class),0);
//                    new Timer().schedule(new TimerTask() {
//                        @Override
//                        public void run() {
//                            finish();
//                        }
//                    },5000);
                    return true;
                case R.id.action_settings:
                    return true;
            }
            return false;
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_main);

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setSelectedItemId(R.id.action_settings);
        bottomNavigationView.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        //TODO change idView to EditText and add a save button to let the user change systems
        systemIdView = findViewById(R.id.systemID);
        //TODO get system ID from database
        systemIdView.setText("123456789");

        logoutBtn = findViewById(R.id.logout_btn);
        logoutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO disconnect from database
                //TODO figure out how to sign out of google
                Intent signoutIntent =new Intent(SettingsActivity.this,LoginActivity.class);

                setResult(Constants.LOGOUT_REQUEST);
                finish();
//                new Timer().schedule(new TimerTask() {
//                    @Override
//                    public void run() {
//                        finish();
//                    }
//                },5000);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==Constants.LOGOUT_REQUEST){
            setResult(Constants.LOGOUT_REQUEST);
            finish();
        }
    }

    //    @Override
//    public void onBackPressed(){
//        AlertDialog exitAlert = GlobalDialogBuilders.exitAppDialogBuilder(this).create();
//        exitAlert.show();
//    }
}
