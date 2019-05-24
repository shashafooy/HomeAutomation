/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.usu.HomeAutomation;

import com.google.firebase.FirebaseException;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;
import java.lang.Integer;

/**
 *
 * @author aaron
 */
public class myDatabase {
    private static String systemId;
    private static DatabaseReference baseReference;
    private static ArrayList<Boolean> lightStatusList;
    private static ArrayList<DatabaseReference> lightReferences;
    private static boolean washerActive, dryerActive;
    private static boolean systemValid;
    private volatile static boolean lightingInitialized, washerDryerInitialized;
    private static boolean created = false;

    public static void setSystemId(String systemId) {
        myDatabase.systemId = systemId;
    }
    
    public interface OnGetDataListener{
        void onSuccess(DataSnapshot dataSnapshot);
        void onStart();
        void onFailure(DatabaseError databaseError);
    }
    
    public static void readData(DatabaseReference ref, final OnGetDataListener listener){
        listener.onStart();
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                listener.onSuccess(dataSnapshot);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                listener.onFailure(databaseError);
            }
        });
    }
    
    public static void continuousReadData(DatabaseReference ref, final OnGetDataListener listener){
    listener.onStart();
    ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                listener.onSuccess(dataSnapshot);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                listener.onFailure(databaseError);
            }
        });
    }
    
    public static void createDatabase(final String SystemID) {
        if (created) return;
        created = true;
        
        systemId=SystemID;

        baseReference = FirebaseDatabase.getInstance().getReference().child("Systems/" + SystemID);

        lightStatusList = new ArrayList<>();
        continuousReadData(baseReference.child("Lights"), new OnGetDataListener() {
            @Override
            public void onSuccess(DataSnapshot dataSnapshot) {
                int i=0;
                for (DataSnapshot child :
                        dataSnapshot.getChildren()) {
                    Boolean lightStatus = child.child("Active").getValue(Boolean.class);
                    if(!lightingInitialized){
                        lightStatusList.add(lightStatus);
                        SetLightStatus(i++,lightStatus);
                    }else if(Boolean.logicalXor(lightStatusList.get(i), lightStatus)){
                        lightStatusList.set(i, lightStatus);
                        SetLightStatus(i++, lightStatus);
                    }
                }
                lightingInitialized = true;
                System.out.println("Change in light");
            }

            @Override
            public void onStart() {

            }

            @Override
            public void onFailure(DatabaseError databaseError) {

            }
        });
        
        readData(baseReference.child("WasherDryer"), new OnGetDataListener(){
            @Override
            public void onSuccess(DataSnapshot dataSnapshot) {
                washerActive = dataSnapshot.child("WasherActive").getValue(Boolean.class);
                dryerActive = dataSnapshot.child("DryerActive").getValue(Boolean.class);
            }

            @Override
            public void onStart() {
            }

            @Override
            public void onFailure(DatabaseError databaseError) {
            }
            
        });
    }
    
    /**
     *Formats a byte to send for a given light and on/off state\n
     * format: (device type(1), id(6), status(1))\n
     * device type for lights are 1     * 
     * @param id 1-64 used for bits (0111 1110)   
     * @param status true for on, false for off
     */
    public static void SetLightStatus(int id, boolean status) {
        //TODO format data and broadcast
        id = id & 0x3F; //1-64 
        char bits = (char)(id<<1);
        bits = (char) (bits | (char)((status?0x1:0x0) | 0x80));
        RFSystem.sendByte(bits);
    }  
    
    public static boolean isWasherActive() {
        return washerActive;
    }

    public static void PostWasherActive(boolean washerActive) {
        myDatabase.washerActive = washerActive;
        baseReference.child("WasherDryer/WasherActive").setValueAsync(washerActive);
    }

    public static boolean isDryerActive() {
        return dryerActive;
    }

    public static void PostDryerActive(boolean dryerActive) {
        myDatabase.dryerActive = dryerActive;
        baseReference.child("WasherDryer/DryerActive").setValueAsync(dryerActive);
    }

    public static String getSystemID() {
        return systemId;
    }

    public DatabaseReference getWasherDryerReference(){
        return baseReference.child("WasherDryer");
    }
    
    
    
}
