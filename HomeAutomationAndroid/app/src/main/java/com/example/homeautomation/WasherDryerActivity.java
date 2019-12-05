package com.example.homeautomation;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

public class WasherDryerActivity extends AppCompatActivity {
    private TextView washerStatusView, dryerStatusView;
    private ImageButton washerTimerOnBtn, washerTimerOffBtn, dryerTimerOnBtn, dryerTimerOffBtn;
    private BottomNavigationView bottomNavigationView;
    private final Handler handler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.washer_main);

        bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setSelectedItemId(R.id.action_washer_dryer);
        bottomNavigationView.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        washerStatusView = findViewById(R.id.washerStatus);
        dryerStatusView = findViewById(R.id.dryerStatus);
        washerTimerOnBtn = findViewById(R.id.washerTimerOnBtn);
        washerTimerOffBtn = findViewById(R.id.washerTimerOffBtn);
        dryerTimerOnBtn = findViewById(R.id.dryerTimerOnBtn);
        dryerTimerOffBtn = findViewById(R.id.dryerTimerOffBtn);

        setWasherStatus(myDatabase.isWasherActive());
        setDryerStatus(myDatabase.isDryerActive());

        washerTimer(myDatabase.isWasherTimerOn());
        dryerTimer(myDatabase.isDryerTimerOn());

        washerTimerOnBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                washerTimer(false);
            }
        });
        washerTimerOffBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                washerTimer(true);
            }
        });

        dryerTimerOnBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dryerTimer(false);
            }
        });
        dryerTimerOffBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dryerTimer(true);
            }
        });

        AutoRefresh();

    }

    private void AutoRefresh() {
        //update every 10 seconds, unless not initialized, then update every second
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                setWasherStatus(myDatabase.isWasherActive());
                setDryerStatus(myDatabase.isDryerActive());

                washerTimer(myDatabase.isWasherTimerOn());
                dryerTimer(myDatabase.isDryerTimerOn());
                AutoRefresh();
            }
        }, 5000);
    }

    private void dryerTimer(boolean on) {
        dryerTimerOnBtn.setEnabled(on);
        dryerTimerOnBtn.setVisibility(on ? View.VISIBLE : View.INVISIBLE);
        dryerTimerOffBtn.setEnabled(!on);
        dryerTimerOffBtn.setVisibility(on ? View.INVISIBLE : View.VISIBLE);
        myDatabase.setDryerTimerState(on);
    }

    private void washerTimer(boolean on) {
        washerTimerOnBtn.setEnabled(on);
        washerTimerOnBtn.setVisibility(on ? View.VISIBLE : View.INVISIBLE);
        washerTimerOffBtn.setEnabled(!on);
        washerTimerOffBtn.setVisibility(on ? View.INVISIBLE : View.VISIBLE);
        myDatabase.setWasherTimerState(on);
    }

    @Override
    protected void onResume() {
        super.onResume();
        bottomNavigationView.setSelectedItemId(R.id.action_washer_dryer);

    }

    private void setDryerStatus(boolean active) {
        dryerStatusView.setText(active ? "Active" : "Idle");
        dryerStatusView.setTextColor(active ? Color.GREEN : Color.RED);
    }

    private void setWasherStatus(boolean active) {
        washerStatusView.setText(active ? "Active" : "Idle");
        washerStatusView.setTextColor(active ? Color.GREEN : Color.RED);
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

    private final BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
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
