package com.example.homeautomation;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class SettingsActivity extends AppCompatActivity {
    private BottomNavigationView bottomNavigationView;

    private final BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
            switch (menuItem.getItemId())
            {
                case R.id.action_lighting:
                    startActivityForResult(new Intent(SettingsActivity.this, LightingActivity.class),0);
                    return true;
                case R.id.action_washer_dryer:finish();
                    startActivityForResult(new Intent(SettingsActivity.this, WasherDryerActivity.class),0);
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

        bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setSelectedItemId(R.id.action_settings);
        bottomNavigationView.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        TextView systemIdView = findViewById(R.id.systemID);
        systemIdView.setText(myDatabase.getSystemID());

        Button logoutBtn = findViewById(R.id.logout_btn);
        logoutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent signoutIntent =new Intent(SettingsActivity.this,LoginActivity.class);
                signoutIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                setResult(Constants.LOGOUT_REQUEST);
                startActivity(signoutIntent);
                finish();
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

    @Override
    protected void onResume() {
        super.onResume();
        bottomNavigationView.setSelectedItemId(R.id.action_settings);

    }

    //    @Override
//    public void onBackPressed(){
//        AlertDialog exitAlert = GlobalDialogBuilders.exitAppDialogBuilder(this).create();
//        exitAlert.show();
//    }
}
