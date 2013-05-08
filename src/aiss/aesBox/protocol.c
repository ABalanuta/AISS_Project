#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include "protocol.h"

char init(u32 mode){

	//TODO

	return 'c';
}

char update(u8 * data_in, u32 size, u8 * data_out,u32 * size_out){


	//printf("$$Update Size: %d\n", size);

	u8 data_temp[size];

	memcpy(data_temp,data_in,size);

	u32 i;
	for(i = 0; i < size; i++){
		data_temp[i] ^= '1';
		//data_temp[i] ^= '1';
	}

	memcpy(data_out,data_temp,size);
	*size_out = size;

	return 'c';
}

char doFinal(u8 * data_in, u32 size,u8 * data_out, u32 *size_out){


	//printf("$$doFinal Size: %d\n", size);

	u8 data_temp[size];

	memcpy(data_temp,data_in,size);

	u32 i;
	for(i = 0; i < size; i++){
		data_temp[i] ^= '1';
		//data_temp[i] ^= '1';
	}

	memcpy(data_out,data_temp,size);
	*size_out = size;

	return 'c';
}


