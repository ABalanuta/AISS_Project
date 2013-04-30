#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include "protocol.h"


char init(u32 mode){

	//TODO

	return 'c';
}

char  update(u8 * data_in, u32 size, u8 * data_out,u32 * size_out){

	//printf("Size %i", size);
	//printf("Array is: %s\n", data_in);
	printf("UPDATE------------>>>>>:%s\n", data_in);

	return 'c';
}

//char overloaded  doFinal(u8 * data_in, u32 size,u8 * data_out,u32 *size_out){
	//TODO
//	return 'c';
//}
