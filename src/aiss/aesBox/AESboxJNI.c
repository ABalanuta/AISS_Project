#include <jni.h>
#include <stdio.h>
#include "AESboxJNI.h"
#include "protocol.h"

#define ENC_MODE 'c'
#define DEC_MODE 'd'

JNIEXPORT jbyteArray JNICALL Java_AESboxJNI_Encrypt(JNIEnv *env, jobject obj, jbyteArray array){

	u32 data_len,n;
	u8 *data_in;

	u8 buffer_in[MAX_DATA_IN];
	u8 buffer_out[MAX_DATA_OUT];
	u32 i,m,j;

	u32 mode = ROUNDS_10 | ECB_FLAG |FIRST_FLAG| ENCRYPT_FLAG;

	/* Initialization*/

	init( mode );

	// In Array
	jbyte *inArr = env->GetByteArrayElements(array, NULL);

	jsize lengthOfArray = env->GetArrayLength(array);

	// Out Array
	//jbyteArray outArr = env->NewByteArray(lengthOfArray);

	u32 fullPackets = lengthOfArray/MAX_DATA_IN;
	u32 remainBytes = lengthOfArray - (fullPackets * MAX_DATA_IN);
	printf("Number of Bytes:%d\n", lengthOfArray);
	printf("Number of MAX_DATA_IN:%d\n", MAX_DATA_IN);
	printf("Number of fullPackets:%u\n", fullPackets);
	printf("Number of remainBytes:%u\n", remainBytes);

	int i = 0;
	for(i = 0; i < fullPackets; i++){

		// todo alocar espaço para o retorno
		// todo update
		// todo pensar

	}

	//todo doUpdate

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

	return array;
}

/*
 * Class:     AESboxJNI
 * Method:    Decrypt
 * Signature: ([B)[B
 */
JNIEXPORT jbyteArray JNICALL Java_AESboxJNI_Decrypt(JNIEnv *env, jobject obj, jbyteArray array){

	return array;
}
