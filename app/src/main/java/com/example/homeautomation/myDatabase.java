package com.example.homeautomation;

import android.renderscript.Sampler;
import android.support.annotation.NonNull;

import com.google.firebase.FirebaseException;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public final class myDatabase {
    private static FirebaseDatabase database;
    private static String systemID;
    private static DatabaseReference baseReference;
    private static ArrayList<LightController> lights;
    private static ArrayList<DatabaseReference> lightReferences;
    private static boolean washerActive,dryerActive;

    public interface OnGetDataListener{
        void onSuccess(DataSnapshot dataSnapshot);
        void onStart();
        void onFailure();
    }

    public static void readData(DatabaseReference ref, final OnGetDataListener listener){
        listener.onStart();
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                listener.onSuccess(dataSnapshot);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                listener.onFailure();
            }
        });
    }

    public static void continuousReadData(DatabaseReference ref, final OnGetDataListener listener){
        listener.onStart();
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                listener.onSuccess(dataSnapshot);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                listener.onFailure();
            }
        });
    }

    public static void createDatabase(final String SystemID) throws FirebaseException {
        final ArrayList<Boolean> systemExists = new ArrayList<>();
        //final boolean systemExists;
        systemID=SystemID;
        database = FirebaseDatabase.getInstance();
        baseReference = database.getReference().child("Systems");
        //check if database has systemID


        readData(baseReference, new OnGetDataListener() {
            @Override
            public void onSuccess(DataSnapshot dataSnapshot) {
                systemExists.add(dataSnapshot.hasChild(SystemID));
            }

            @Override
            public void onStart() {

            }

            @Override
            public void onFailure() {

            }
        });
//        baseReference.addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                systemExists.add(dataSnapshot.hasChild(SystemID));
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError databaseError) {
//
//            }
//        });
        if(!systemExists.get(0)) throw new FirebaseException("System ID does not exist in database");
        baseReference= baseReference.child(SystemID);

        lights = new ArrayList<>();
        lightReferences = new ArrayList<>();
        //get all light references
        readData(baseReference.child("Lights"), new OnGetDataListener() {
            @Override
            public void onSuccess(DataSnapshot dataSnapshot) {
                for (DataSnapshot child :
                        dataSnapshot.getChildren()) {
                    lightReferences.add(child.getRef());
                }
            }

            @Override
            public void onStart() {

            }

            @Override
            public void onFailure() {

            }
        });


//        baseReference.child("Lights").addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                for (DataSnapshot child :
//                        dataSnapshot.getChildren()) {
//                    lightReferences.add(child.getRef());
//                }
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError databaseError) {
//
//            }
//        });

        //add controller for each light
        for (DatabaseReference lightRef :
                lightReferences) {
            lights.add(new LightController(lightRef));
        }
        //add listeners for washer/dryer
        continuousReadData(baseReference.child("WasherDryer"), new OnGetDataListener() {
            @Override
            public void onSuccess(DataSnapshot dataSnapshot) {
                dryerActive=(Boolean) dataSnapshot.child("DryerActive").getValue();
                washerActive=(Boolean) dataSnapshot.child("WasherActive").getValue();
            }

            @Override
            public void onStart() {

            }

            @Override
            public void onFailure() {

            }
        });
//        baseReference.child("WasherDryer").addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                dryerActive=(Boolean) dataSnapshot.child("DryerActive").getValue();
//                washerActive=(Boolean) dataSnapshot.child("WasherActive").getValue();
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError databaseError) {
//
//            }
//        });

    }

    public static DatabaseReference getLightReference(final String lightName){
        for (LightController light :
                lights) {
            if(light.getLightName().equals(lightName)) return light.getLightReference();
        }
        return null;
    }

    public static LightController getLight(String lightName){
        for (LightController light :
                lights) {
            if(light.getLightName().equals(lightName)) return light;
        }
        return null;
    }

    public static ArrayList<LightController> getLightList(){ return lights; }

    public static boolean isWasherActive() {
        return washerActive;
    }

    public static void setWasherActive(boolean washerActive) {
        myDatabase.washerActive = washerActive;
        baseReference.child("WasherDryer/WasherActive").setValue(washerActive);
    }

    public static boolean isDryerActive() {
        return dryerActive;
    }

    public static void setDryerActive(boolean dryerActive) {
        myDatabase.dryerActive = dryerActive;
        baseReference.child("WasherDryer/DryerActive").setValue(dryerActive);
    }

    public static String getSystemID() {
        return systemID;
    }

    public DatabaseReference getWasherDryerReference(){
        return baseReference.child("WasherDryer");
    }
}
