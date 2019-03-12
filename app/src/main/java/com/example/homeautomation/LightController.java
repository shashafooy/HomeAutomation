package com.example.homeautomation;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class LightController{
    private String lightName;
    private Boolean timerActive;
    private Date timerOnTime;
    private Date timerOffTime;
    public DateFormat timeFormat = new SimpleDateFormat("hh:mm a");
    public LightController(String lightName){
        this.lightName = lightName;
        //TODO look up light in database
    }

    public LightController(LightController another){
        this.lightName = new String(another.getLightName());
        this.timerActive = another.getTimerActive();
        this.timerOnTime = another.getTimerOnTime();
        this.timerOffTime = another.getTimerOffTime();
    }


    public void turnOn() {
        //TODO update database to turn on light
    }

    public void turnOff() {
        //TODO update database to turn off light
    }

    public void saveInfo(){
        //TODO push data to database

    }

    public Boolean getTimerActive() {
        return timerActive;
    }

    public void setTimerActive(Boolean timerActive) {
        this.timerActive = timerActive;
    }

    public String getLightName() {
        return lightName;
    }

    public void setLightName(String lightName) {
        this.lightName = lightName;
    }

    public Date getTimerOnTime() {
        return timerOnTime;
    }

    public void setTimerOnTime(Date timerOnTime) {
        this.timerOnTime = timerOnTime;
    }

    public Date getTimerOffTime() {
        return timerOffTime;
    }

    public void setTimerOffTime(Date timerOffTime) {
        this.timerOffTime = timerOffTime;
    }
}
