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


	u32 fullPackets = lengthOfArray/MAX_DATA_IN;
	u32 remainBytes = lengthOfArray - (fullPackets * MAX_DATA_IN);
	printf("Number of Bytes:%d\n", lengthOfArray);
	printf("Number of MAX_DATA_IN:%d\n", MAX_DATA_IN);
	printf("Number of fullPackets:%u\n", fullPackets);
	printf("Number of remainBytes:%u\n", remainBytes);

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

		jbyteArray outArray = env->NewByteArray((jsize) returnSize);
		jsize start = (jsize) 0;
		jsize len = (jsize) returnSize;
		jbyte *buf = (jbyte*) buffer_out;
		//printf("len: %u\n",len);
		env->SetByteArrayRegion(outArray, start, len , buf);
		return outArray;

		//release Array
		//env->ReleaseByteArrayElements(array, 0, 0);

	} else{
		// copy to buff
		memcpy(&buffer_in, inArr, lengthOfArray);

		//printf("\nBEFORE------------>>>>>\n");
		//printf("Size is %d and String is %s",returnSize, buffer_out);
		//printf("\nBEFORE------------>>>>>\n");

		//cipher
		doFinal(buffer_in, lengthOfArray, buffer_out, &returnSize);
		printf("DoFinal\n");

		//printf("\nAfter------------>>>>>\n");
		//printf("Size is %d and String is %s",returnSize, buffer_out);
		//printf("\nAfter------------>>>>>\n");

		//memcpy(outArr,"string qualquer",returnSize);
		//void SetByteArrayRegion(JNIEnv *env, jbyteArray array, jsize start, jsize len, jbyte *buf);

		jbyteArray outArray = env->NewByteArray((jsize) returnSize);
		jsize start = (jsize) 0;
		jsize len = (jsize) returnSize;
		jbyte *buf = (jbyte*) buffer_out;
		//printf("len: %u\n",len);
		env->SetByteArrayRegion(outArray, start, len , buf);
		return outArray;

	}

}

/*
 * Class:     AESboxJNI
 * Method:    Decrypt
 * Signature: ([B)[B
 */
JNIEXPORT jbyteArray JNICALL Java_AESboxJNI_Decrypt(JNIEnv *env, jobject obj, jbyteArray array){

	u32 mode = ROUNDS_10 | ECB_FLAG |FIRST_FLAG| DECRYPT_FLAG;

	/* Initialization*/

	init( mode );

	// In Array
	jbyte *inArr = env->GetByteArrayElements(array, NULL);
	jsize lengthOfArray = env->GetArrayLength(array);


	u32 fullPackets = lengthOfArray/MAX_DATA_IN;
	u32 remainBytes = lengthOfArray - (fullPackets * MAX_DATA_IN);
	printf("Number of Bytes:%d\n", lengthOfArray);
	printf("Number of MAX_DATA_IN:%d\n", MAX_DATA_IN);
	printf("Number of fullPackets:%u\n", fullPackets);
	printf("Number of remainBytes:%u\n", remainBytes);

	u8 buffer_in[MAX_DATA_IN];
	u8 buffer_out[MAX_DATA_OUT];
	u8 *buffer_temp;
	u32 i, returnSize;

	// Se tiveremos mais que MAX_DATA_IN
	if(fullPackets > 0){
		printf("BIG PACKET\n");
		for(i = 0; i < fullPackets; i++){

			// copy to buff
			memcpy(&buffer_in, &inArr[i * MAX_DATA_IN], MAX_DATA_IN);
			printf("1...\n");

			//cipther
			update(buffer_in, MAX_DATA_IN, buffer_out, &returnSize);
			printf("2...\n");
			// todo pensar

			printf("size_of buf: %d\n",sizeof(buffer_temp));
			realloc(buffer_temp, sizeof(buffer_temp) + returnSize);
			printf("size_of buf: %d\n",sizeof(buffer_temp));
			memcpy(&buffer_temp[sizeof(buffer_temp) + returnSize], buffer_out, returnSize);

		}

		memcpy(&buffer_in, &inArr[fullPackets * MAX_DATA_IN],remainBytes);
		doFinal(buffer_in, remainBytes, buffer_out, &returnSize);
		realloc(buffer_temp, sizeof(buffer_temp) + returnSize);
		memcpy(&buffer_temp[sizeof(buffer_temp) + returnSize], buffer_out, returnSize);

		jbyteArray outArray = env->NewByteArray((jsize) sizeof(buffer_temp));
		jsize start = (jsize) 0;
		jsize len = (jsize) sizeof(buffer_temp);
		jbyte *buf = (jbyte*) buffer_out;
		//printf("len: %u\n",len);
		env->SetByteArrayRegion(outArray, start, len , buf);
		return outArray;

		//release Array
		//env->ReleaseByteArrayElements(array, 0, 0);

	}else{
		printf("SMALL PACKET\n");
		// copy to buff
		memcpy(&buffer_in, inArr, lengthOfArray);

		//printf("\nBEFORE------------>>>>>\n");
		//printf("Size is %d and String is %s",returnSize, buffer_out);
		//printf("\nBEFORE------------>>>>>\n");

		//cipher
		doFinal(buffer_in, lengthOfArray, buffer_out, &returnSize);
		printf("DoFinal\n");

		//printf("\nAfter------------>>>>>\n");
		//printf("Size is %d and String is %s",returnSize, buffer_out);
		//printf("\nAfter------------>>>>>\n");

		//memcpy(outArr,"string qualquer",returnSize);
		//void SetByteArrayRegion(JNIEnv *env, jbyteArray array, jsize start, jsize len, jbyte *buf);

		jbyteArray outArray = env->NewByteArray((jsize) returnSize);
		jsize start = (jsize) 0;
		jsize len = (jsize) returnSize;
		jbyte *buf = (jbyte*) buffer_out;
		//printf("len: %u\n",len);
		env->SetByteArrayRegion(outArray, start, len , buf);
		return outArray;

	}
}
