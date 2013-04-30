#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include "AESboxJNI.h"
#include "protocol.h"

#define ENC_MODE 'c'
#define DEC_MODE 'd'

JNIEXPORT jbyteArray JNICALL Java_AESboxJNI_Encrypt(JNIEnv *env, jobject obj, jbyteArray array){

	u32 mode = ROUNDS_10 | ECB_FLAG |FIRST_FLAG| ENCRYPT_FLAG;

	/* Initialization*/

	init( mode );

	// In Array
	jbyte *inArr = env->GetByteArrayElements(array, NULL);
	jsize lengthOfArray = env->GetArrayLength(array);
	// Out Array
	jbyteArray outArr;

	u32 fullPackets = lengthOfArray/MAX_DATA_IN;
	u32 remainBytes = lengthOfArray - (fullPackets * MAX_DATA_IN);
	printf("Number of Bytes:%d\n", lengthOfArray);
	printf("Number of MAX_DATA_IN:%d\n", MAX_DATA_IN);
	printf("Number of fullPackets:%u\n", fullPackets);
	printf("Number of remainBytes:%u\n", remainBytes);

	u32 a = 0;

	u8 buffer_in[MAX_DATA_IN];
	u8 buffer_out[MAX_DATA_OUT];
	u32 i, returnSize;

	// Se tiveremos mais que MAX_DATA_IN
	if(fullPackets > 0){


		for(i = 0; i < fullPackets; i++){

			// copy to buff
			memcpy(&buffer_in, &inArr[i * MAX_DATA_IN], MAX_DATA_IN);

			//cipther
			update(buffer_in, MAX_DATA_IN, buffer_out, &returnSize);
			// todo pensar
		}
	}

	// Senao fazemos doFinal
	else{
		// copy to buff
		memcpy(&buffer_in, inArr, lengthOfArray);

		//cipther
		doFinal(buffer_in, lengthOfArray, buffer_out, &returnSize);


	//!! Olha, faz sentido n estarmos a receber nada -> n mexemos em nada do buffer_out e tb do returnSize

		// Out Array
		
		//memcpy(outArr,"string qualquer",returnSize);
		//void SetByteArrayRegion(JNIEnv *env, jbyteArray array, jsize start, jsize len, jbyte *buf);

		outArr = env->NewByteArray((jsize) returnSize);
		jsize start = (jsize) 0;
		jsize len = (jsize) returnSize;
		jbyte *buf = (jbyte*) buffer_out;
		printf("len: %u\n",len);
		env->SetByteArrayRegion(outArr, start, len , buf);
		//jsize lengthOfArray1 = env->GetArrayLength(sizeof(buf));
		
		for(a=0; a < (u32) len; a++){
			//printf("%u", lengthOfArray1);
			printf("%u%c", a, outArr[a]);
		}
		//return outArr;

	}

	//todo crear jbyteArray
	//todo clean inputs
	//todo return


	//update((u8*)inArr, (u32) lengthOfArray, (u8*)&outArr, (u32*)&lengthOfArray);
	//printf("%s\n", (char*) inarr);

	//printf("\n---------\n");
	//u32 inSize = 32;
	//u32 outSize = 32;
	//update((u8 *) inarr, inSize, NULL, &outSize);

	//printf("\nInit Response is :%i\n", init(a));

	//release Array
	//env->ReleaseByteArrayElements(array, 0, 0);

	return outArr;
}

/*
 * Class:     AESboxJNI
 * Method:    Decrypt
 * Signature: ([B)[B
 */
JNIEXPORT jbyteArray JNICALL Java_AESboxJNI_Decrypt(JNIEnv *env, jobject obj, jbyteArray array){

	return array;
}
