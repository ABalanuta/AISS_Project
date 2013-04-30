#include <jni.h>
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
	jbyteArray cenas = (jbyteArray)env->GetByteArrayElements(array, NULL);
	jsize lengthOfArray = env->GetArrayLength(array);




	// Out Array
	jbyteArray outArr = env->NewByteArray(0);



	u32 fullPackets = lengthOfArray/MAX_DATA_IN;
	u32 remainBytes = lengthOfArray - (fullPackets * MAX_DATA_IN);
	printf("Number of Bytes:%d\n", lengthOfArray);
	printf("Number of MAX_DATA_IN:%d\n", MAX_DATA_IN);
	printf("Number of fullPackets:%u\n", fullPackets);
	printf("Number of remainBytes:%u\n", remainBytes);

	printf("before------------>>>>>\n");
	u32 a = 0;

	printf("UPDATE------------>>>>> from %d until %d \n", a, lengthOfArray);

	for(a=0; a < (u32)lengthOfArray; a++){
	//	printf("%d%c\n", a,(char)cenas[a]);
	}
	printf("before------------>>>>>\n");

	return cenas;

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

	// Senáo fazemos doFinal
	else{

		// copy to buff
		memcpy(&buffer_in, inArr, lengthOfArray);

		//cipther
		doFinal(buffer_in, lengthOfArray, buffer_out, &returnSize);

		//memcpy(outArr,"string qualquer",returnSize);
		env->SetByteArrayRegion(outArr, 0, returnSize , (jbyte*) buffer_out);
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

	// release Array
	//(*env)->ReleaseByteArrayElements(env, array, bufferPtr, 0);

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
