#ifndef WASHDRY_H
#define WASHDRY_H


#include <stdbool.h>
#define SIZE 256

extern bool washerBuffer[SIZE], dryerBuffer[SIZE];
//extern bool dryerBuffer[SIZE] = {0};
extern bool washerOn, dryerOn;
extern bool laundryStateChange;

/*********************
	Checks washer activity to determine 
	Machine is considered on if at least 20% of samples are on
	Updates washerOn var.
**********************/
void updateWasherDryerState(void);
/*****************************************
	Adds the given state to the dryer buffer
@param state: boolean value for if the sensor read a 1/0
*****************************************/
void updateDryer(bool state);
/*****************************************
	Adds the given state to the washer buffer
@param state: boolean value for if the sensor read a 1/0
*****************************************/
void updateWasher(bool state);

#endif
