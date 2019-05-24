/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.usu.HomeAutomation;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseException;
import com.google.firebase.FirebaseOptions;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author aaron
 */
public class StartApplication {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        if(args.length!=1){ 
            System.out.println("invalid arguments, require system ID");
            System.exit(0);
        }
        System.out.println("Working Directory: " + System.getProperty("user.dir"));
        
        
        FileInputStream serviceAccount;
        try {
            serviceAccount = new FileInputStream(System.getProperty("user.dir") + "/homeautomation-serviceAccountKey.json");
            FirebaseOptions options = new FirebaseOptions.Builder()
                .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                .setDatabaseUrl("https://homeautomation-a3e9f.firebaseio.com/")
                .build();
        FirebaseApp.initializeApp(options);
        } catch (FileNotFoundException ex) {
            Logger.getLogger(StartApplication.class.getName()).log(Level.SEVERE, null, ex);
            System.exit(0);
        } catch (IOException ex) {
            Logger.getLogger(StartApplication.class.getName()).log(Level.SEVERE, null, ex);
            System.exit(0);
        }
        
//        Runnable myRunnable = () -> {
//            myDatabase.createDatabase(args[0]);            
//        };
        
        myDatabase.createDatabase(args[0]);
        try {
            Thread.sleep(2000);
        } catch (InterruptedException ex) {
            Logger.getLogger(StartApplication.class.getName()).log(Level.SEVERE, null, ex);
        }
        //myDatabase.PostWasherActive(true);
        RFSystem.InitRFSystem();
//        RFSystem.run();
        System.out.println("Waiting for data");
        while(true);
    }
    
}
