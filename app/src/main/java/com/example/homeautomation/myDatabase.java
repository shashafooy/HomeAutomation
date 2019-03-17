package com.example.homeautomation;

import android.provider.ContactsContract;
import android.renderscript.Sampler;
import android.support.annotation.NonNull;
import android.util.Log;

import com.google.firebase.FirebaseException;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Semaphore;

public final class myDatabase {
    private static FirebaseDatabase database;
    private static String systemID;
    private static DatabaseReference baseReference, systemReference;
    private static ArrayList<LightController> lights;
    private static ArrayList<DatabaseReference> lightReferences;
    private static boolean washerActive,dryerActive;
    private static boolean systemValid;


    public static void checkIfValid(final String systemId) {
        if(database==null) database = FirebaseDatabase.getInstance();
        readData(database.getReference().child("SystemIds"), new OnGetDataListener() {
            @Override
            public void onSuccess(DataSnapshot dataSnapshot) {
                //systemValid=dataSnapshot.hasChild(systemId);
                DataSnapshot child = dataSnapshot.child(systemId);
                systemValid=dataSnapshot.hasChild(systemId);
//                semaphore.release();
//                systemExists.add(dataSnapshot.hasChild(SystemID));
//                done.countDown();
            }

            @Override
            public void onStart() {

            }

            @Override
            public void onFailure(DatabaseError databaseError) {
                Log.d("myDatabase", "onFailure: Could not access database");
                systemValid=false;
//                semaphore.release();
//                systemExists.add(false);
            }
        });
    }

    public static boolean isSystemValid() { return systemValid;}

    public interface OnGetDataListener{
        void onSuccess(DataSnapshot dataSnapshot);
        void onStart();
        void onFailure(DatabaseError databaseError);
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
                listener.onFailure(databaseError);
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
                listener.onFailure(databaseError);
            }
        });
    }

    public static DatabaseReference getBaseReference() { return baseReference;}

    public static void createDatabase(final String SystemID) throws FirebaseException {
        final ArrayList<Boolean> systemExists = new ArrayList<>();
//        systemExists.add(false);
        systemID=SystemID;

        if(!systemValid) throw new FirebaseException("System ID does not exist in database");
        baseReference = database.getReference().child("Systems/" + SystemID);

        lights = new ArrayList<>();
//        lightReferences = new ArrayList<>();
        //get all light references
        //TODO run this as Task(?) and have main thread wait until data is initialized
        readData(baseReference.child("Lights"), new OnGetDataListener() {
            @Override
            public void onSuccess(DataSnapshot dataSnapshot) {
                for (DataSnapshot child :
                        dataSnapshot.getChildren()) {
                    lights.add(new LightController(child.getRef()));
                }
            }

            @Override
            public void onStart() {

            }

            @Override
            public void onFailure(DatabaseError databaseError) {

            }
        });

        //add listeners for washer/dryer
        continuousReadData(baseReference.child("WasherDryer"), new OnGetDataListener() {
            @Override
            public void onSuccess(DataSnapshot dataSnapshot) {
                dryerActive= dataSnapshot.child("DryerActive").getValue(Boolean.class);
                washerActive= dataSnapshot.child("WasherActive").getValue(Boolean.class);
            }

            @Override
            public void onStart() {

            }

            @Override
            public void onFailure(DatabaseError databaseError) {

            }
        });
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
