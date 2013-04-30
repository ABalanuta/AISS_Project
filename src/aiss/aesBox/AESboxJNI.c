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
	u8 buffer_temp[MAX_DATA_OUT];
	u32 i, returnSize, totalSize = 0;

	// Se tiveremos mais que MAX_DATA_IN
	if(fullPackets > 0){
		for(i = 0; i < fullPackets; i++){

			// copy to buff
			memcpy(&buffer_in, &inArr[i * MAX_DATA_IN], MAX_DATA_IN);

			//cipher
			update(buffer_in, MAX_DATA_IN, buffer_out, &returnSize);
			if(returnSize > MAX_DATA_IN){
				totalSize += MAX_DATA_IN ;
			} else {
				totalSize += returnSize;
			}
			memcpy(&buffer_temp, buffer_out, returnSize);
			memset(&buffer_out, 0, sizeof(buffer_out));

		}

		memcpy(&buffer_in, &inArr[fullPackets * MAX_DATA_IN],remainBytes);
		doFinal(buffer_in, remainBytes, buffer_out, &returnSize);
		memcpy(&buffer_temp[totalSize], buffer_out, returnSize);
		if(returnSize > MAX_DATA_IN){
			totalSize += MAX_DATA_IN ;
		} else {
			totalSize += returnSize;
		}
		jbyteArray outArray = env->NewByteArray((jsize) totalSize);
		jsize start = (jsize) 0;
		jsize len = (jsize) totalSize;
		jbyte *buf = (jbyte*) buffer_temp;
		env->SetByteArrayRegion(outArray, start, len , buf);
		memset(&buffer_in, 0, sizeof(buffer_in));
		memset(&buffer_temp, 0, sizeof(buffer_temp));
		memset(&buffer_out, 0, sizeof(buffer_out));
		return outArray;

		//release Array
		//env->ReleaseByteArrayElements(array, 0, 0);

	}else{
		// copy to buff
		memcpy(&buffer_in, inArr, lengthOfArray);

		//cipher
		doFinal(buffer_in, lengthOfArray, buffer_out, &returnSize);
		printf("DoFinal\n");

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
	u8 buffer_temp[MAX_DATA_OUT];
	u32 i, returnSize, totalSize = 0;

	// Se tiveremos mais que MAX_DATA_IN
	if(fullPackets > 0){
		for(i = 0; i < fullPackets; i++){
			// copy to buff
			memcpy(&buffer_in, &inArr[i * MAX_DATA_IN], MAX_DATA_IN);
			//cipher
			update(buffer_in, MAX_DATA_IN, buffer_out, &returnSize);
			if(returnSize > MAX_DATA_IN){
				totalSize += MAX_DATA_IN;
			} else {
				totalSize += returnSize;
			}
			memcpy(&buffer_temp, buffer_out, returnSize);
			memset(&buffer_out, 0, sizeof(buffer_out));

		}

		memcpy(&buffer_in, &inArr[fullPackets * MAX_DATA_IN],remainBytes);
		doFinal(buffer_in, remainBytes, buffer_out, &returnSize);
		memcpy(&buffer_temp[totalSize], buffer_out, returnSize);
		if(returnSize > MAX_DATA_IN){
			totalSize += MAX_DATA_IN ;
		} else {
			totalSize += returnSize;
		}
		jbyteArray outArray = env->NewByteArray((jsize) totalSize);
		jsize start = (jsize) 0;
		jsize len = (jsize) totalSize;
		jbyte *buf = (jbyte*) buffer_temp;
		env->SetByteArrayRegion(outArray, start, len , buf);
		return outArray;

		//release Array
		//env->ReleaseByteArrayElements(array, 0, 0);

	}else{
		// copy to buff
		memcpy(&buffer_in, inArr, lengthOfArray);

		//cipher
		doFinal(buffer_in, lengthOfArray, buffer_out, &returnSize);
		printf("DoFinal\n");

		jbyteArray outArray = env->NewByteArray((jsize) returnSize);
		jsize start = (jsize) 0;
		jsize len = (jsize) returnSize;
		jbyte *buf = (jbyte*) buffer_out;
		//printf("len: %u\n",len);
		env->SetByteArrayRegion(outArray, start, len , buf);
		return outArray;

	}
}
