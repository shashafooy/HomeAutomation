package com.example.homeautomation;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.io.Serializable;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class LightController implements Parcelable {
    private String lightName;
    private Boolean timerActive, lightActive;
    private Date timerOnTime;
    private Date timerOffTime;
    // may need hh instead of h, hh will cause leading 0 (07:00)
    public static DateFormat timeFormat = new SimpleDateFormat("h:mm a");
    private DatabaseReference lightReference;


//    public LightController(String lightName){
//        lightReference=myDatabase.getLightReference(lightName);
//        this.lightName=lightName;
//        lightReference.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                lightActive = (Boolean) dataSnapshot.child("Active").getValue();
//                timerActive = (Boolean) dataSnapshot.child("TimerActive").getValue();
//                try {
//                    timerOnTime = timeFormat.parse((String) dataSnapshot.child("TimerOn").getValue());
//                    timerOffTime = timeFormat.parse((String) dataSnapshot.child("TimerOff").getValue());
//                } catch (ParseException e) {
//                    e.printStackTrace();
//                }
//                notify();
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError databaseError) {
//
//            }
//        });
//
//    }

    public LightController(DatabaseReference lightRef){
        lightReference=lightRef;
        myDatabase.continuousReadData(lightReference, new myDatabase.OnGetDataListener() {
            @Override
            public void onSuccess(DataSnapshot dataSnapshot) {
                lightName = (String) dataSnapshot.child("Name").getValue();
                lightActive = (Boolean) dataSnapshot.child("Active").getValue();
                timerActive = (Boolean) dataSnapshot.child("TimerActive").getValue();
                try {
                    timerOnTime = timeFormat.parse((String) dataSnapshot.child("TimerOn").getValue());
                    timerOffTime = timeFormat.parse((String) dataSnapshot.child("TimerOff").getValue());
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onStart() {

            }

            @Override
            public void onFailure(DatabaseError databaseError) {

            }
        });
    }

    public LightController(LightController another){
        this.lightName = new String(another.getLightName());
        this.timerActive = another.getTimerActive();
        this.timerOnTime = another.getTimerOnTime();
        this.timerOffTime = another.getTimerOffTime();
        this.lightReference = another.lightReference;
    }

    public LightController(Parcel source){
        lightName = source.readString();
        timerActive = source.readByte() != 0;
        timerOnTime = new Date(source.readLong());
        timerOffTime = new Date(source.readLong());
        lightReference = myDatabase.getLightReference(lightName);

    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(final Parcel dest, int flags) {
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

    public void copyLightParameters(LightController light){
        lightActive = light.lightActive;
        lightName = light.lightName;
        timerActive = light.timerActive;
        timerOffTime = light.timerOffTime;
        timerOnTime = light.timerOnTime;
    }

    public void turnOn() {
        lightActive=true;
        lightReference.child("Active").setValue(true);
    }

    public void turnOff() {
        lightActive=false;
        lightReference.child("Active").setValue(false);
    }

    public void saveInfo(){
        lightReference.child("Active").setValue(lightActive);
        lightReference.child("Name").setValue(lightName);
        lightReference.child("TimerActive").setValue(timerActive);
        lightReference.child("TimerOff").setValue(timeFormat.format(timerOffTime));
        lightReference.child("TimerOn").setValue(timeFormat.format(timerOnTime));

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

    public DatabaseReference getLightReference() { return lightReference; }

    public Boolean getLightActive() { return lightActive; }

    public void setLightActive(Boolean lightActive) { this.lightActive = lightActive; }
}
