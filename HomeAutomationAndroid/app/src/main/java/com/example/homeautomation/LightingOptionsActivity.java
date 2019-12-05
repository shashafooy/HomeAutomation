package com.example.homeautomation;

import android.app.Activity;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;

import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;

public class LightingOptionsActivity extends AppCompatActivity {
    private LightController light, newLightInstance;
    private EditText startTime;
    private EditText endTime;
    private boolean infoEdited;
    private int month, lightPos;
    private AlertDialog.Builder exitDialogBuilder;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.lighting_options);
        infoEdited = false;
        month = Calendar.getInstance().get(Calendar.MONTH);
        String name = getIntent().getStringExtra(Constants.LightKey);
        for (LightController light :
                myDatabase.getLightList()) {
            if (light.getLightName().equals(name)) {
                this.light = light;
                lightPos = myDatabase.getLightList().indexOf(light);
                break;
            }
        }
        newLightInstance = new LightController(light);

        TextView lightNameView = findViewById(R.id.lightingOptionsTitle);
        lightNameView.setText(light.getLightName());

        EditText newNameEdit = findViewById(R.id.LightName_edit);
        newNameEdit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                infoEdited = true;
                newLightInstance.setLightName(s.toString());
            }
        });

        Switch timerSwitch = findViewById(R.id.timerSwitch);
        timerSwitch.setChecked(light.getTimerActive());
        timerSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                infoEdited=true;
                newLightInstance.setTimerActive(isChecked);
            }
        });

        startTime = findViewById(R.id.timeStart_Edit);
        startTime.setText(LightController.timeFormat.format(light.getTimerOnTime()));
        startTime.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                infoEdited = true;
                try {
                    newLightInstance.setTimerOnTime(LightController.timeFormat.parse(s.toString()));
                    startTime.setError(null);
                } catch (ParseException e) {
                    startTime.setError("Invalid format, please enter \"hh:mm am/pm\"");
                }
            }
        });

        endTime = findViewById(R.id.timeEnd_Edit);
        endTime.setText(LightController.timeFormat.format(light.getTimerOffTime()));
        endTime.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                infoEdited = true;
                try {
                    newLightInstance.setTimerOffTime(LightController.timeFormat.parse(s.toString()));
                    endTime.setError(null);
                } catch (ParseException e) {
                    endTime.setError("Invalid format, please enter \"hh:mm am/pm\"");
                }
            }
        });

        Button sunriseBtn = findViewById(R.id.startDefaultBtn);
        sunriseBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    startTime.setText(LightController.timeFormat.format(getSunriseTime(month)));
                } catch (ParseException e) {
                    startTime.setText("Error");
                }
            }
        });

        Button sunsetBtn = findViewById(R.id.endDefaultBtn);
        sunsetBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    endTime.setText(LightController.timeFormat.format(getSunsetTime(month)));
                } catch (ParseException e) {
                    startTime.setText("Error");
                }
            }
        });

        Button saveBtn = findViewById(R.id.saveBtn);
        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                myDatabase.getLightList().get(lightPos).copyLightParameters(newLightInstance);
                myDatabase.getLightList().get(lightPos).saveInfo();
                //setResult(Activity.RESULT_OK, new Intent().putExtra(Constants.LightKey,light).putExtra(Constants.OldLightKey, newLightInstance));
                setResult(Activity.RESULT_OK);
                finish();
            }
        });

        exitDialogBuilder =  new AlertDialog.Builder(this)
                .setTitle("Exit Options")
                .setMessage("Are you sure you want to leave without saving?")
                .setCancelable(true)
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    //exit without saving
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //light = newLightInstance;
                        //setResult(Activity.RESULT_OK, new Intent().putExtra(Constants.LightKey,light).putExtra(Constants.OldLightKey,newLightInstance.getLightName()));
                        finish();

                    }
                })
                .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                })
                .setIcon(android.R.drawable.ic_dialog_alert);
    }





    @Override
    public void onBackPressed(){
        //super.onBackPressed();
        if(infoEdited){
            AlertDialog exitAlert = exitDialogBuilder.create();
            exitAlert.show();
        }else {
            finish();
        }
    }

    private Date getSunriseTime(int month) throws ParseException {
        switch(month){
            case Calendar.JANUARY:
                return LightController.timeFormat.parse("7:00 am");
            case Calendar.FEBRUARY:
                return LightController.timeFormat.parse("6:45 am");
            case Calendar.MARCH:
                return LightController.timeFormat.parse("7:00 am");
            case Calendar.APRIL:
                return LightController.timeFormat.parse("6:27 am");
            case Calendar.MAY:
                return LightController.timeFormat.parse("5:40 am");
            case Calendar.JUNE:
                return LightController.timeFormat.parse("5:00 am");
            case Calendar.JULY:
                return LightController.timeFormat.parse("5:15 am");
            case Calendar.AUGUST:
                return LightController.timeFormat.parse("5:35 am");
            case Calendar.SEPTEMBER:
                return LightController.timeFormat.parse("6:15 am");
            case Calendar.OCTOBER:
                return LightController.timeFormat.parse("6:45 am");
            case Calendar.NOVEMBER:
                return LightController.timeFormat.parse("6:15 am");
            case Calendar.DECEMBER:
                return LightController.timeFormat.parse("6:45 am");

        }
        return null;
    }

    private Date getSunsetTime(int month) throws ParseException {
        switch(month){
            case Calendar.JANUARY:
                return LightController.timeFormat.parse("6:00 pm");
            case Calendar.FEBRUARY:
                return LightController.timeFormat.parse("6:30 pm");
            case Calendar.MARCH:
                return LightController.timeFormat.parse("8:15 pm");
            case Calendar.APRIL:
                return LightController.timeFormat.parse("8:45 pm");
            case Calendar.MAY:
                return LightController.timeFormat.parse("9:15 pm");
            case Calendar.JUNE:
                return LightController.timeFormat.parse("9:45 pm");
            case Calendar.JULY:
                return LightController.timeFormat.parse("9:35 pm");
            case Calendar.AUGUST:
                return LightController.timeFormat.parse("9:20 pm");
            case Calendar.SEPTEMBER:
                return LightController.timeFormat.parse("8:15 pm");
            case Calendar.OCTOBER:
                return LightController.timeFormat.parse("7:30 pm");
            case Calendar.NOVEMBER:
                return LightController.timeFormat.parse("5:50 pm");
            case Calendar.DECEMBER:
                return LightController.timeFormat.parse("5:45 pm");

        }
        return null;
    }
}
