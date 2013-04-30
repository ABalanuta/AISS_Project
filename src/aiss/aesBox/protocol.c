#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include "protocol.h"

char init(u32 mode){

	//TODO

	return 'c';
}

char	update(u8 * data_in, u32 size, u8 * data_out,u32 * size_out){

	//printf("Size %i", size);
	//printf("Array is: %s\n", data_in);
	u32 a = 0;

	printf("UPDATE------------>>>>> from %d until %d \n", a, size);

	for(a=0; a < size; a++){
		printf("%d%c\n", a,data_in[a]);
	}
	//printf("UPDATE------------>>>>>:%s\n", data_in);

	return 'c';
}

char  doFinal(u8 * data_in, u32 size,u8 * data_out,u32 *size_out){
	//TODO
	return 'c';
}
