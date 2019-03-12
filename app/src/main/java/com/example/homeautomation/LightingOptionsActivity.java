package com.example.homeautomation;

import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

public class LightingOptionsActivity extends AppCompatActivity {
    LightController light, oldLightInstance;
    TextView lightName;
    Switch timerSwitch;
    EditText startTime, endTime;
    Button saveBtn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.lighting_options);

        light = new LightController(getIntent().getExtras().getString(Constants.LightKey));
        oldLightInstance = new LightController(light);

        lightName = findViewById(R.id.lightingOptionsTitle);
        lightName.setText(light.getLightName());

        timerSwitch = findViewById(R.id.timerSwitch);
        timerSwitch.setChecked(light.getTimerActive());
        timerSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                light.setTimerActive(isChecked);
            }
        });

        startTime.setText(light.timeFormat.format(light.getTimerOnTime()));
        endTime.setText(light.timeFormat.format(light.getTimerOffTime()));

        saveBtn = findViewById(R.id.save);
        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });





    }
}
