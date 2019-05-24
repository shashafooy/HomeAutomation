#include "WasherDryer.h"

bool washerBuffer[SIZE] = {0};
bool dryerBuffer[SIZE]={0};

int indexW,indexD;
bool washerOn = false, dryerOn = false;
bool laundryStateChange = false;

void updateWasherDryerState(void){
	int i;
	int cntW=0, cntD=0;
	bool temp;
	for(i=0;i<SIZE;i++){
		cntW+=washerBuffer[i];
		cntD+=dryerBuffer[i];
	}
	//on: greater than 20% activity
	//off: less than 20% activity	
	temp = ((float)(cntW)/SIZE)>0.20f;
	laundryStateChange = washerOn!=temp;
	washerOn=temp;
	
	temp = ((float)(cntD)/SIZE)>0.20f;
	laundryStateChange |= (dryerOn != temp);
	dryerOn=temp;
}

void updateDryer(bool state){
	dryerBuffer[indexD]=state;
	indexD=(indexD+1)%SIZE;
}

void updateWasher(bool state){
	washerBuffer[indexW]=state;
	indexW=(indexW+1)%SIZE;
}
