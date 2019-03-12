package com.example.homeautomation;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;

public class LightingActivity extends AppCompatActivity
{
    private LightingAdapter lightingAdapter;

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
            switch (menuItem.getItemId())
            {
                case R.id.action_lighting:
                    Toast.makeText(LightingActivity.this,"Lighting clicked",Toast.LENGTH_SHORT).show();
                    return true;
                case R.id.action_washer_dryer:
                    startActivity(new Intent(LightingActivity.this, WasherDryerActivity.class));
                    return true;
                case R.id.action_settings:
                    startActivity(new Intent(LightingActivity.this, SettingsActivity.class));
                    return true;
            }
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.lighting_main);

        ArrayList<String> list = new ArrayList<String>();
        //TODO populate list with lights
        for(int i=0; i<4; i++){
            list.add("light"+i);
        }

        lightingAdapter = new LightingAdapter(list,this);

        ListView lView = (ListView)findViewById(R.id.lightingListView);
        lView.setAdapter(lightingAdapter);


        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        bottomNavigationView.setSelectedItemId(R.id.action_lighting);

    }
    @Override
    protected void onResume() {
        super.onResume();
        lightingAdapter.notifyDataSetChanged();
    }
}
