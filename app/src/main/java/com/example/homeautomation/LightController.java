package com.example.homeautomation;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class LightController implements Parcelable {
    private String lightName;
    private Boolean timerActive;
    private Date timerOnTime;
    private Date timerOffTime;
    // may need hh instead of h, hh will cause leading 0 (07:00)
    public DateFormat timeFormat = new SimpleDateFormat("h:mm a");


    public LightController(String lightName){
        this.lightName = lightName;
        //TODO look up light in database
        this.timerActive = false;
        try {
            this.timerOnTime = timeFormat.parse("12:00 am");
            this.timerOffTime = timeFormat.parse("12:00 pm");
        } catch (ParseException e) {
            e.printStackTrace();
        }

    }

    public LightController(LightController another){
        this.lightName = new String(another.getLightName());
        this.timerActive = another.getTimerActive();
        this.timerOnTime = another.getTimerOnTime();
        this.timerOffTime = another.getTimerOffTime();
    }

    public LightController(Parcel source){
        lightName = source.readString();
        timerActive = source.readByte() != 0;
        timerOnTime = new Date(source.readLong());
        timerOffTime = new Date(source.readLong());

    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(lightName);
        dest.writeByte((byte)(timerActive ? 1 : 0));
        dest.writeLong(timerOnTime.getTime());
        dest.writeLong(timerOffTime.getTime());
    }

    public static final Creator<LightController> CREATOR = new Creator<LightController>() {
        @Override
        public LightController[] newArray(int size) {
            return new LightController[size];
        }

        @Override
        public LightController createFromParcel(Parcel source) {
            return new LightController(source);
        }
    };

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
