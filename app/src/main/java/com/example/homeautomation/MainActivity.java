package com.example.homeautomation;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    private TextView mTextMessage;

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
            switch (menuItem.getItemId())
            {
                case R.id.action_lighting:
//                    Toast.makeText(MainActivity.this,"Lighting Clicked",Toast.LENGTH_SHORT).show();
                    //TODO go to lighting page
                    return true;
                case R.id.action_washer_dryer:
//                    Toast.makeText(MainActivity.this,"Washer/Dryer Clicked",Toast.LENGTH_SHORT).show();
                    //TODO go to washer page
                    return true;
                case R.id.action_settings:
//                    Toast.makeText(MainActivity.this,"Settings Clicked",Toast.LENGTH_SHORT).show();
                    //TODO go to settings page
                    return true;
            }
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.lighting_main);

        mTextMessage = (TextView) findViewById(R.id.message);
        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.bottom_navigation);

        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
    }

}
