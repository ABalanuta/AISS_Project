#include <jni.h>
#include <stdio.h>
#include "AESboxJNI.h"
#include "protocol.h"

JNIEXPORT jbyteArray JNICALL Java_AESboxJNI_Encrypt(JNIEnv *env, jobject obj, jbyteArray array){

	// In Array
	jbyte *inArr = env->GetByteArrayElements(array, NULL);

	jsize lengthOfArray = env->GetArrayLength(array);

	// Out Array
	jbyteArray outArr = env->NewByteArray(lengthOfArray);

	update((u8*)inArr, (u32) lengthOfArray, (u8*)&outArr, (u32*)&lengthOfArray);
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
