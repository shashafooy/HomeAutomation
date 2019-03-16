package com.example.homeautomation;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

public class LightingActivity extends AppCompatActivity
{
    private LightingAdapter lightingAdapter;
    private ListView lView;
    private AlertDialog.Builder exitDialogBuilder;
    private static int counter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.lighting_main);
        counter++; //number of instances of activity

        lightingAdapter = new LightingAdapter(this);

        lView = findViewById(R.id.lightingListView);
        lView.setAdapter(lightingAdapter);

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setSelectedItemId(R.id.action_lighting);
        bottomNavigationView.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
    }

    @Override
    public void onBackPressed(){
        if(counter-->1){
            super.onBackPressed();
        }
        //do nothing if this is the last activity
        //technically login_activity still exists, but do not want to go to that screen
//        AlertDialog exitAlert = GlobalDialogBuilders.exitAppDialogBuilder(this).create();
//        exitAlert.show();
    }


    @Override
    protected void onResume() {
        super.onResume();
        lightingAdapter.notifyDataSetChanged();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        if(requestCode == Constants.RequestLightCode){
            lightingAdapter.onActivityResult(requestCode,resultCode,data);
        }else if(requestCode == Constants.LOGOUT_REQUEST){
            setResult(resultCode); //logout
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
                    return true;
                case R.id.action_washer_dryer:
                    startActivityForResult(new Intent(LightingActivity.this, WasherDryerActivity.class),0);
//                    new Timer().schedule(new TimerTask() {
//                        @Override
//                        public void run() {
//                            finish();
//                        }
//                    },5000);
                    return true;
                case R.id.action_settings:
                    startActivityForResult(new Intent(LightingActivity.this, SettingsActivity.class),0);
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
