package com.example.homeautomation;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.util.Log;

import com.google.firebase.FirebaseException;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

@SuppressWarnings("ConstantConditions")
final class myDatabase {
    private static FirebaseDatabase database;
    private static String systemID;
    private static DatabaseReference baseReference, userReference;
    private static ArrayList<LightController> lights;
    private static ArrayList<DatabaseReference> lightReferences;
    private static boolean washerActive, dryerActive, washerTimerOn, dryerTimerOn;
    private static boolean systemValid;
    private volatile static boolean lightingInitialized, washerDryerInitialized;
    private static Context context;
    private static NotificationCompat.Builder builder;
    private static boolean created = false;


    public static void checkIfValid(final String systemId) {
        if(database==null) database = FirebaseDatabase.getInstance();
        readData(database.getReference().child("SystemIds"), new OnGetDataListener() {
            @Override
            public void onSuccess(DataSnapshot dataSnapshot) {
                systemValid=dataSnapshot.hasChild(systemId);
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

    @SuppressWarnings("EmptyMethod")
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

    public static void createDatabase(Context context, final String SystemID, DatabaseReference userRef) throws FirebaseException {
        if (created) return;
        created = true;

        myDatabase.context = context;

        userReference = userRef;
//        systemExists.add(false);
        systemID=SystemID;

        if(!systemValid) throw new FirebaseException("System ID does not exist in database");
        baseReference = database.getReference().child("Systems/" + SystemID);
        lightingInitialized = false;

        lights = new ArrayList<>();
        //get all light references
//        new SetLightsTask().execute();
        //initialize lights
        readData(baseReference.child("Lights"), new OnGetDataListener() {
            @Override
            public void onSuccess(DataSnapshot dataSnapshot) {
                for (DataSnapshot child :
                        dataSnapshot.getChildren()) {
                    lights.add(new LightController(child.getRef()));
                }
                lightingInitialized = true;
            }

            @Override
            public void onStart() {

            }

            @Override
            public void onFailure(DatabaseError databaseError) {

            }
        });

        //get washer/dryer timer on status
        readData(userReference, new OnGetDataListener() {
            @Override
            public void onSuccess(DataSnapshot dataSnapshot) {
                washerTimerOn = dataSnapshot.child("WasherTimerOn").getValue(Boolean.class);
                dryerTimerOn = dataSnapshot.child("DryerTimerOn").getValue(Boolean.class);
            }

            @Override
            public void onStart() {

            }

            @Override
            public void onFailure(DatabaseError databaseError) {

            }
        });

        //notification builder
        Intent intent = new Intent(myDatabase.context, WasherDryerActivity.class);
//        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(myDatabase.context, 0, intent, 0);

        builder = new NotificationCompat.Builder(myDatabase.context, "Channel_1")
                .setSmallIcon(R.drawable.ic_washer_dryer)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setContentText("Tap to dismiss")
                .setContentIntent(pendingIntent)
                .setAutoCancel(true);


        //add listeners for washer/dryer
        washerDryerInitialized = true;
        continuousReadData(baseReference.child("WasherDryer"), new OnGetDataListener() {
            @Override
            public void onSuccess(DataSnapshot dataSnapshot) {
                boolean newDryer, newWasher;
                newDryer = dataSnapshot.child("DryerActive").getValue(Boolean.class);
                newWasher = dataSnapshot.child("WasherActive").getValue(Boolean.class);
                //trigger notification if not initializing
                if (washerDryerInitialized) washerDryerInitialized = false;
                else {
                    //dryer finished
                    if ((!newDryer && dryerActive && dryerTimerOn) || (!newWasher && washerActive && washerTimerOn)) {
                        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(myDatabase.context);
                        builder.setContentTitle(!newDryer ? "Dryer Finished" : "Washer Finished");

                        int notificationId = !newDryer ? Constants.DRYER_NOTIFICATION_ID : Constants.WASHER_NOTIFICATION_ID;
                        notificationManager.cancel(notificationId);
                        notificationManager.notify(notificationId, builder.build());

                    }
                }
                dryerActive = newDryer;
                washerActive = newWasher;

            }

            @Override
            public void onStart() {

            }

            @Override
            public void onFailure(DatabaseError databaseError) {

            }
        });


    }


    public static boolean AllLightsInitialized() {

        for (LightController light :
                lights) {
            if (!light.initialized) return false;
        }
        return true;
    }

    //region Setters and Getters

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

    public static void setWasherTimerState(boolean washerTimerOn) {
        myDatabase.washerTimerOn = washerTimerOn;
        userReference.child("WasherTimerOn").setValue(washerTimerOn);
    }

    public static void setDryerTimerState(boolean dryerTimerOn) {
        myDatabase.dryerTimerOn = dryerTimerOn;
        userReference.child("DryerTimerOn").setValue(dryerTimerOn);
    }

    public static boolean isWasherTimerOn() {
        return washerTimerOn;
    }

    public static boolean isDryerTimerOn() {
        return dryerTimerOn;
    }

    //endregion


//    private static class SetLightsTask extends AsyncTask<Void,Integer,Void> {
//
//        @Override
//        protected Void doInBackground(Void... voids) {
//            readData(baseReference.child("Lights"), new OnGetDataListener() {
//                @Override
//                public void onSuccess(DataSnapshot dataSnapshot) {
//                    for (DataSnapshot child :
//                            dataSnapshot.getChildren()) {
//                        lights.add(new LightController(child.getRef()));
//                    }
//                    lightingInitialized=true;
//                }
//
//                @Override
//                public void onStart() {
//
//                }
//
//                @Override
//                public void onFailure(DatabaseError databaseError) {
//
//                }
//            });
//            return null;
//        }
//    }
}
