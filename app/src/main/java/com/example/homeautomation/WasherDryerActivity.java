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

public class WasherDryerActivity extends AppCompatActivity {
    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
            switch (menuItem.getItemId())
            {
                case R.id.action_lighting:
                    Toast.makeText(WasherDryerActivity.this,"Lighting clicked",Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(WasherDryerActivity.this, LightingActivity.class));
                    return true;
                case R.id.action_washer_dryer:
                    Toast.makeText(WasherDryerActivity.this,"washer clicked",Toast.LENGTH_SHORT).show();
                    return true;
                case R.id.action_settings:
                    Toast.makeText(WasherDryerActivity.this,"Settings clicked",Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(WasherDryerActivity.this, SettingsActivity.class));
                    return true;
            }
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.washer_main);

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        bottomNavigationView.setSelectedItemId(R.id.action_washer_dryer);

    }

}
