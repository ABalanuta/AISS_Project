#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include "protocol.h"

char init(u32 mode){

	//TODO

	return 'c';
}

char update(u8 * data_in, u32 size, u8 * data_out,u32 * size_out){

	u8 data_temp[size];

	memcpy(data_temp,data_in,size);

	u32 i;
	for(i = 0; i < size; i++){
		//data_temp[i] ^= '1';
		//data_temp[i] ^= '1';
	}

	memcpy(data_out,data_temp,size);
	*size_out = size;

	return 'c';
}

char doFinal(u8 * data_in, u32 size,u8 * data_out, u32 *size_out){


	u8 data_temp[size];

	memcpy(data_temp,data_in,size);

	u32 i;
	for(i = 0; i < size; i++){
		//data_temp[i] ^= '1';
		//data_temp[i] ^= '1';
	}

	memcpy(data_out,data_temp,size);
	*size_out = size;

	//printf("\nDoFinalIN------------>>>>> from %d until %d \n", a, size);
	//printf("%s",data_in);
	//printf("\nDoFinalIN------------>>>>>\n");


	//printf("\nDoFinalOUT------------>>>>> from %d until %d \n", a, size);
	//printf("%s",data_out);
	//printf("\nDoFinalOUT------------>>>>>\n");

	//printf("\n");
	//memcpy(data_out,&data_in,size);

	return 'c';
}

