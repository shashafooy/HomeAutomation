package com.example.homeautomation;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.widget.ListView;

public class LightingActivity extends AppCompatActivity
{
    private LightingAdapter lightingAdapter;
    private static int counter;
    private final Handler handler = new Handler();
    private boolean initialized;
    private BottomNavigationView bottomNavigationView;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.lighting_main);
        counter++; //number of instances of activity
        initialized = false;//myDatabase.AllLightsInitialized();

        lightingAdapter = new LightingAdapter(this);

        ListView lView = findViewById(R.id.lightingListView);
        lView.setAdapter(lightingAdapter);

        AutoRefresh();

//        final Runnable timedTask = new Runnable(){
//
//            @Override
//            public void run() {
//                lightingAdapter.notifyDataSetChanged();
//                if(!myDatabase.AllLightsInitialized()){
//                    handler.postDelayed(timedTask, 3000);
//                }
//            }
//        };
//
//        if(!myDatabase.AllLightsInitialized()) handler.postDelayed(timedTask, 2000);


        bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setSelectedItemId(R.id.action_lighting);
        bottomNavigationView.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
    }


    private void AutoRefresh() {
        //update every 10 seconds, unless not initialized, then update every second
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                lightingAdapter.notifyDataSetChanged();
                if (!initialized) {
                    initialized = myDatabase.AllLightsInitialized();
                    AutoRefresh();
                }
            }
        }, initialized ? 10000 : 1000);
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
        bottomNavigationView.setSelectedItemId(R.id.action_lighting);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        if(requestCode == Constants.RequestLightCode){
            lightingAdapter.onActivityResult(requestCode);
        }else if(requestCode == Constants.LOGOUT_REQUEST){
            setResult(resultCode); //logout
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
                    return true;
                case R.id.action_washer_dryer:
                    startActivityForResult(new Intent(LightingActivity.this, WasherDryerActivity.class),0);
                    return true;
                case R.id.action_settings:
                    startActivityForResult(new Intent(LightingActivity.this, SettingsActivity.class),0);
                    return true;
            }
            return false;
        }
    };

}
