/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.usu.HomeAutomation;
import com.pi4j.io.gpio.*;
import com.pi4j.io.gpio.event.GpioPinDigitalStateChangeEvent;
import com.pi4j.io.gpio.event.GpioPinListenerDigital;
import com.pi4j.io.gpio.Pin;
import com.pi4j.io.gpio.PinEdge;
import com.pi4j.io.gpio.PinState;
import java.util.ArrayDeque;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author aaron
 */
public class RFSystem {
    private static GpioController gpio;
    private static GpioPinDigitalInput dataIn;
    private static GpioPinDigitalOutput dataOut;
    private static ConcurrentLinkedDeque<Byte> buffer;         //use pop to obtain and remove first byte
    private static int Ts=50;                             //Symbol time in milliseconds
    
    
    
    
    public static void InitRFSystem(){
        gpio=GpioFactory.getInstance();
        dataIn = gpio.provisionDigitalInputPin(RaspiPin.GPIO_05); //TODO verify pin number connections
        dataIn.setShutdownOptions(true);
        dataOut = gpio.provisionDigitalOutputPin(RaspiPin.GPIO_04);
        buffer = new ConcurrentLinkedDeque<Byte>();
    }
    
    //pin needs to alternate or output will fluctuate
    //to send 0, send low Ts/2, high Ts/2
    //to send 1, send high Ts/2, low Ts/2
    //read every Ts, should be the first pulse
    public static void run(){
        RFThread myThread = new RFThread();
        myThread.start();
    }
      
    public static void sendByte(char value) {
//        buffer.add((byte)value);
        buffer.addLast((byte) value);
    }
    
    
    private static class RFThread extends Thread{
        @SuppressWarnings("SleepWhileInLoop")
        @Override
        public void run(){
            dataIn.addListener(IncomingSignalListener);
            while(true){
               // System.out.println("checking buffer");

                if(!buffer.isEmpty()){
                    System.out.println("Sending to light");
                    try {
                        //deactivate reciever
                        dataIn.removeListener(IncomingSignalListener);
                        byte data=buffer.pop();
                        //send wake up command
                        dataOut.high();
                        Thread.sleep(4*Ts);               //hold high for 4Ts
                        dataOut.low();
                        Thread.sleep(Ts/2);
                        
                        //send data
                        for(int i=0; i<8; i++){
                            int bit=(data >> i) & 1;
                            if(bit != 0){
                                OutputOne();
                            }else{
                                OutputZero();
                            }
                        }
                        dataOut.low();
                        //reactivate reciever
                        dataIn.addListener(IncomingSignalListener);

                    } catch (InterruptedException ex) {
                        Logger.getLogger(RFSystem.class.getName()).log(Level.SEVERE, null, ex);
                    }

                }             
                
            }
            
        }
        //Listener to obtain a byte from any incoming signals
        private final static GpioPinListenerDigital IncomingSignalListener = new GpioPinListenerDigital(){
                @Override
                public void handleGpioPinDigitalStateChangeEvent(GpioPinDigitalStateChangeEvent event) {
                    PinEdge pinEdge= event.getEdge();
                    //TODO check if I need to start reading in signal
                    //Start pulse is 4Ts long, verify 3.5*Ts of that is high
                    //3.5 is to account for system lag in processing
                    System.out.println("recieving data");

                    boolean good = true;
                    for(int i=0; i<(int)(Ts*3.5); i+=Ts/10){    
                        try {
                            if(dataIn.isLow()){
                                good = false;
                                break;
                            }
                            Thread.sleep(Ts/10);
                        } catch (InterruptedException ex) {
                            Logger.getLogger(RFSystem.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    }                    
                    if(!good) return;
                    byte incomingByte = 0;
                    boolean washerOn = false, dryerOn =false;
                    while(true){ //wait for start of low pulse
                        //wait for low pulse to occur
                        if(dataIn.isLow()){ try {                            
                            //low lasts for Ts/2
                            //wait an additional Ts/4 to make sure we grab the right data
                            Thread.sleep(3*Ts/2);
                            for(int i=7; i>=0; i--){
                                incomingByte = (byte) (incomingByte | (dataIn.isHigh()?1:0 << i));
                                Thread.sleep(Ts);
                            }
                            
                            //obtained byte
                            break;                            
                            
                            } catch (InterruptedException ex) {
                                Logger.getLogger(RFSystem.class.getName()).log(Level.SEVERE, null, ex);
                            }                            
                        }                        
                    }
                    //check if washer/dryer
                    //0*****(washer)(dryer) format
                    if(((incomingByte >> 7) & 1) == 0){
                        System.out.println("Recieved Laundry Transmission");

                        washerOn = ((incomingByte >> 1) & 1) == 1;
                        dryerOn = (incomingByte & 1) == 1;
                        //post status if there is a change
                        if(washerOn!=myDatabase.isWasherActive()) {
                            myDatabase.PostWasherActive(washerOn);
                        }
                        if(dryerOn!=myDatabase.isDryerActive()){
                            myDatabase.PostDryerActive(dryerOn);
                        }                                
                    }
                    
                }
            };
        
        private static void OutputZero(){
            try {
                dataOut.low();
                Thread.sleep(Ts/2);
                dataOut.high();
                Thread.sleep(Ts/2);
            } catch (InterruptedException ex) {
                Logger.getLogger(RFSystem.class.getName()).log(Level.SEVERE, null, ex);
            }
            
            
        }
        private static void OutputOne(){
            try {
                dataOut.high();                
                Thread.sleep(Ts/2);
                dataOut.low();
                Thread.sleep(Ts/2);            
            } catch (InterruptedException ex) {
                Logger.getLogger(RFSystem.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
    
    
    
}
