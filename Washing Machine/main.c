#include <stdbool.h>
#include "TM4C123.h"                    // Device header
#include "WasherDryer.h"

#define RCGC2_PA 0x00000001
#define RCGC2_PB 0x00000002
#define RCGC2_PC 0x00000004
#define UNLOCK 0x4c4f434b
#define WASHER_DRYER
#define LIGHTING
#define LIGHT1_ID 0x82		//0b1xxx xxxVal
#define Ts 100
#define RF_PIN 0
#define LIGHT1_PIN 1
#define WASHER_PIN 3
#define DRYER_PIN 2

#define SETBIT(source,value)  source |=value;
#define CLRBIT(source,value) source &= ~value;

void init_Ports(void);
void init_SSI(void);
void computeTouch(unsigned int, unsigned int);
void sendByte(int byte);
void sleep(unsigned int time);
void outputOne(void);
void outputZero(void);

bool lightOn;

void init_SysClk() {
	
	//set sysclk to 80MHz

	SYSCTL->RCC = (SYSCTL->RCC & ~0x7c0) + 0x540; //set XTAL to 0x15 16MHz
	SYSCTL->RCC2 |= 0x80000000; //use RCC2
	SYSCTL->RCC2 |= 0x1 << 11; //use bypass pll
	SYSCTL->RCC2 &= ~0x70; //clear OSCSRC2, use main oscillator
	SYSCTL->RCC2 &= ~0x2000; //clear	PWRDN2
	SYSCTL->RCC2 |= 0x40000000; //DIV400 = 1
	SYSCTL->RCC2 = (SYSCTL->RCC2 & ~0x1fc00000) + (0x4<<22);
	while((SYSCTL->RIS & 0x40) == 0); //wait for PLLRIS bit
	SYSCTL->RCC2 &= ~(0x1 << 11); //clear bypass pll
}


/*******************************
	Sets up Timer0 A and B as 32 bit periodic 
	TimerA functions as an interrupt every 0.5 seconds for Laundry device
********************************/
void init_WTimer(){
	
	//TODO set up timerB for communication delays
	
	SYSCTL->RCGCWTIMER = 0x1; //use GPWTimer 0
#ifndef LIGHTING
	NVIC_EnableIRQ(WTIMER0A_IRQn);
#endif
	
	//Wtimer0 for delay() function
	WTIMER0->CTL = 0x0; //disable timer
	WTIMER0->CFG = 0x4; //32 bit wide
	WTIMER0->TBMR = 0x1; //B is one - shot
#ifndef LIGHTING
	WTIMER0->TAMR = 0x2; //A is Periodic
	WTIMER0->TAILR= 0x2625A00;	//load A with 80MHz*0.5 seconds = 40,000,000
	WTIMER0->IMR = 0x1;	//Timer0A timeout interrupt 
	WTIMER0->CTL=0x1; //start timerA
#endif
	//use WTIMER0->CTL=0x1<<8; to enable timerB
}


/*************************
	initializes the GPIO pins
for Laundry:
	sets B(2,3) as input
	sets B(0) as output
for Lighting:
	sets B(0) as input
	sets B(1) as output
	set B(0) as interrupt

*************************/
void init_Ports(void){
	SYSCTL->RCGC2 |=RCGC2_PB;
	
	GPIOB->AFSEL = 0x00; //disable AF
#ifdef LIGHTING
	GPIOB->DIR = 0x1 << LIGHT1_PIN; //pin 0 input, pin 1 output
	
	NVIC_EnableIRQ(GPIOB_IRQn); //port B interrupt
	GPIOB->IS= 0x0; //interrupt on edge
	GPIOB->IBE = 0x0; //disable both edges
	GPIOB->IEV = 0x1 << RF_PIN; //rising edge
	GPIOB->IM = 0x1 << RF_PIN; //enable interrupt for pin 0
	
	GPIOB->DEN = 0x3;	//enable pins 0,1
#else
	GPIOB->DIR = 0x1 << RF_PIN; //pin 2,3 input, pin 0 output
	GPIOB->DEN = 0xD;
	
#endif
}

#ifdef LIGHTING
/*******************************
	GPIOB interrupt
	handles interrupts from the reciever
********************************/
void GPIOB_Handler(void){
	char byte = 0x00;
	int i;
	if(GPIOB->MIS & (0x1 << RF_PIN)){				//pin 2 interrupt
		GPIOB->IM = 0x0; //mask pin 2
		for(i=0; i<(int)(Ts*3.5); i+=Ts/10){
			if(((GPIOB->DATA >> RF_PIN) & 0x1)==0){
				
				GPIOB->ICR = 0x4;		//acknowledge interrupt
				GPIOB->IM |=0x8;		//enable pin 2 interrupt
				return;				//noise, not a valid signal
			}
			sleep(Ts/10);
		}
		while(true){
			if(((GPIOB->DATA >> RF_PIN) & 0x1)==0){
				sleep(3*Ts/2);
				for(i=7; i>=0; i--){
					byte |= ((GPIOB->DATA >>  RF_PIN) & 0x1) << i;
					sleep(Ts);
				}
				break;
			}				
		}
		
		//TODO determine if signal is for me
		switch(byte & 0xFE){				//check first 7 bits is equivalent to my ID
			case LIGHT1_ID:
				lightOn = byte & 0x1;		//check bit 0 for on/off status
				if(!lightOn) GPIOB->DATA |= 0x1 << LIGHT1_PIN;
				else GPIOB->DATA &= ~(0x1 << LIGHT1_PIN);
				break;
			default: 
				break;			
		}
		
		GPIOB->ICR = 0x1 << RF_PIN; //acknowledge pin 0 interrupt
	}
	GPIOB->IM |= 0x1 << RF_PIN; //enable pin 0 interrupt
}

#else
/****************************
TODO: create timer interrupt 
Check vibration sensor value
****************************/
void WTIMER0A_Handler(void){
	int byte;
	WTIMER0->IMR = 0x0;
	//get washer sensor data
	updateWasher((bool)(GPIOB->DATA & (0x1 << WASHER_PIN)));	//TODO change 0x1 to whatever pin the washer sensor is
	//get dryer sensor data
	updateDryer((bool)(GPIOB->DATA & (0x1 << DRYER_PIN)));		//TODO change 0x2 to whatever pin the dryer sensor is
	//acknowledge interrupt
	updateWasherDryerState();

	if(laundryStateChange){
		//washer code 0b11 msb
		byte=0x00;
		if(washerOn) byte = byte | 0x2;
		if(dryerOn) byte = byte | 0x1;
		sendByte(byte);
	}	
	
	WTIMER0->ICR=0x1;		//acknowledge timer interrupt
	WTIMER0->IMR = 0x1;
}

void sendByte(int byte){	
	
	
	int i;
	//wake up command
	GPIOB->DATA |= (0x1 << RF_PIN);			//TODO change shift value to output pin
	sleep(4*Ts);
	GPIOB->DATA&= ~(0x1 << RF_PIN);
	sleep(Ts/2);
	
	//send data
	for(i=7;i >=0; i--){
		if(byte>>i==1){
			outputOne();
		}else{
			outputZero();
		}
	}
	GPIOB->DATA &= ~0x1;
	
	
}

void outputOne(void){
	GPIOB->DATA |=(0x1 << RF_PIN);
	sleep(Ts/2);
	GPIOB->DATA &= ~(0x1 << RF_PIN);
	sleep(Ts/2);
}

void outputZero(void){	
	GPIOB->DATA &= ~(0x1 << RF_PIN);
	sleep(Ts/2);
	GPIOB->DATA |=(0x1 << RF_PIN);
	sleep(Ts/2);
}
#endif

//waits the specified time in milliseconds using WTIMER0B
void sleep(unsigned int time){
//	CLRBIT(WTIMER0->CTL, 0x1<<8);
	WTIMER0->CTL &=~(0x1<<8);
	time = time * 80000;
	WTIMER0->TBILR = time * 80000;
	WTIMER0->CTL |=0x1<<8;
	while(!((WTIMER0->RIS>>8) & 0x1));			//wait for timer timeout
	
}

int main(void)
{
	int i;
	lightOn=true;
	
	
	for(i=0; i<SIZE; i++){
		washerBuffer[i]=0;
		dryerBuffer[i]=0;
	}
	
	init_SysClk();
	init_WTimer();
	init_Ports();
//	init_SSI();

	
	while(1);
}
