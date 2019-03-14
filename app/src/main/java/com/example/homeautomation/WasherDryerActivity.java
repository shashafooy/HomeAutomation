package com.example.homeautomation;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

public class WasherDryerActivity extends AppCompatActivity {
    private TextView washerStatusView, dryerStatusView;
    private ImageButton washerTimerOnBtn, washerTimerOffBtn, dryerTimerOnBtn, dryerTimerOffBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.washer_main);

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setSelectedItemId(R.id.action_washer_dryer);
        bottomNavigationView.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        
        //TODO get database for washer and dryer
        washerStatusView = findViewById(R.id.washerStatus);
        dryerStatusView = findViewById(R.id.dryerStatus);
        washerTimerOnBtn = findViewById(R.id.washerTimerOnBtn);
        washerTimerOffBtn = findViewById(R.id.washerTimerOffBtn);
        dryerTimerOnBtn = findViewById(R.id.dryerTimerOnBtn);
        dryerTimerOffBtn = findViewById(R.id.dryerTimerOffBtn);

        washerTimerOnBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toggleButton(washerTimerOffBtn,washerTimerOnBtn);
                //TODO disable washer timer
            }
        });
        washerTimerOffBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toggleButton(washerTimerOnBtn,washerTimerOffBtn);
                //TODO enable washer timer
            }
        });

        dryerTimerOnBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toggleButton(dryerTimerOffBtn,dryerTimerOnBtn);
                //TODO disable dryer timer
            }
        });
        dryerTimerOffBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toggleButton(dryerTimerOnBtn,dryerTimerOffBtn);
                //TODO enable dryer timer
            }
        });



    }

    private void toggleButton(ImageButton enable, ImageButton disable){
        enable.setEnabled(true);
        enable.setVisibility(Button.VISIBLE);
        disable.setEnabled(false);
        disable.setVisibility(Button.INVISIBLE);
    }

    private TextView setIdle(TextView view){
        view.setText("Idle");
        view.setTextColor(Color.RED);
        return view;
    }

    private TextView setActive(TextView view){
        view.setText("Active");
        view.setTextColor(Color.GREEN);
        return view;
    }

//    @Override
//    public void onBackPressed(){
//        AlertDialog exitAlert = GlobalDialogBuilders.exitAppDialogBuilder(this).create();
//        exitAlert.show();
//    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==Constants.LOGOUT_REQUEST){
            setResult(requestCode); //logout
            finish();
        }
    }

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
            switch (menuItem.getItemId())
            {
                case R.id.action_lighting:
                    startActivityForResult(new Intent(WasherDryerActivity.this, LightingActivity.class),0);
//                    new Timer().schedule(new TimerTask() {
//                        @Override
//                        public void run() {
//                            finish();
//                        }
//                    },5000);
                    return true;
                case R.id.action_washer_dryer:
                    return true;
                case R.id.action_settings:
                    startActivityForResult(new Intent(WasherDryerActivity.this, SettingsActivity.class),0);
//                    new Timer().schedule(new TimerTask() {
//                        @Override
//                        public void run() {
//                            finish();
//                        }
//                    },5000);
                    return true;
            }
            return false;
        }
    };
}
