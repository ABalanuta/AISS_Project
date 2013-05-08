#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include "aiss_aesBox_AESboxJNI.h"
#include "protocol.h"

#define ENC_MODE 'c'
#define DEC_MODE 'd'

JNIEXPORT jbyteArray JNICALL Java_aiss_aesBox_AESboxJNI_Encrypt(JNIEnv *env, jobject obj, jbyteArray array){

	u32 mode = ROUNDS_10 | ECB_FLAG |FIRST_FLAG| ENCRYPT_FLAG;

	/* Initialization*/
	init( mode );

	// In Array
	jbyte *inArr = env->GetByteArrayElements(array, NULL);
	jsize lengthOfArray = env->GetArrayLength(array);


	//Variaveis
	u32 fullPackets = lengthOfArray/MAX_DATA_IN;
	u32 remainBytes = lengthOfArray%MAX_DATA_IN;
	u8 buffer_out[MAX_DATA_OUT];
	u8 buffer_temp[lengthOfArray+AES_BLOCK_SIZE]; //Tamanho maximo do pacote a retornar
	u32 i, returnSize, totalSize = 0;
	jbyteArray outArray;

	//printf("Encryption\n");
	//printf("Number of Bytes:%d\n", lengthOfArray);
	//printf("Number of MAX_DATA_IN:%d\n", MAX_DATA_IN);
	//printf("Number of fullPackets:%u\n", fullPackets);
	//printf("Number of remainBytes:%u\n", remainBytes);


	// Se tiveremos mais que MAX_DATA_IN dividimos os dados
	// em pedaços do tamanho MAX_DATA_IN fazendo update() a cada um
	// fazemos doFinal aos remanescentes dados
	if(fullPackets > 0){
		for(i = 0; i < fullPackets; i++){

			//cipher
			update(&inArr[i * MAX_DATA_IN], MAX_DATA_IN, buffer_out, &returnSize);
			memcpy(&buffer_temp[totalSize], buffer_out, returnSize);
			totalSize += returnSize;
		}

		//Cifrar o ultimo Pacote
		doFinal(&inArr[fullPackets * MAX_DATA_IN], remainBytes, buffer_out, &returnSize);
		memcpy(&buffer_temp[totalSize], buffer_out, returnSize);
		totalSize += returnSize;

		//Criar array de retorno
		outArray = env->NewByteArray((jsize) totalSize);
		jsize start = (jsize) 0;
		jsize len = (jsize) totalSize;
		jbyte *buf = (jbyte*) buffer_temp;
		env->SetByteArrayRegion(outArray, start, len , buf);

	}else{

		//cipher
		doFinal(inArr, lengthOfArray, buffer_out, &returnSize);

		//Criar array de retorno
		outArray = env->NewByteArray((jsize) returnSize);
		jsize start = (jsize) 0;
		jsize len = (jsize) returnSize;
		jbyte *buf = (jbyte*) buffer_out;
		env->SetByteArrayRegion(outArray, start, len , buf);

	}

	//Limpar Bufers
	memset(&buffer_temp, 0, sizeof(buffer_temp));
	memset(&buffer_out, 0, sizeof(buffer_out));

	//release Array (memory leak problem)
	env->ReleaseByteArrayElements(array,inArr,JNI_ABORT);

	return outArray;
}

/*
 * Class:     AESboxJNI
 * Method:    Decrypt
 * Signature: ([B)[B
 */
JNIEXPORT jbyteArray JNICALL Java_aiss_aesBox_AESboxJNI_Decrypt(JNIEnv *env, jobject obj, jbyteArray array){

	u32 mode = ROUNDS_10 | ECB_FLAG |FIRST_FLAG| DECRYPT_FLAG;

	/* Initialization*/
	init( mode );

	// In Array and Size
	jbyte *inArr = env->GetByteArrayElements(array, NULL);
	jsize lengthOfArray = env->GetArrayLength(array);

	//Variaveis
	u32 fullPackets = lengthOfArray/MAX_DATA_IN;
	u32 remainBytes = lengthOfArray%MAX_DATA_IN;
	u8 buffer_out[MAX_DATA_OUT];
	u8 buffer_temp[lengthOfArray+AES_BLOCK_SIZE]; //Tamanho maximo do pacote a retornar
	u32 i, returnSize, totalSize = 0;
	jbyteArray outArray;

	//printf("Decryption\n");
	//printf("Number of Bytes:%d\n", lengthOfArray);
	//printf("Number of MAX_DATA_IN:%d\n", MAX_DATA_IN);
	//printf("Number of fullPackets:%u\n", fullPackets);
	//printf("Number of remainBytes:%u\n", remainBytes);

	// Se tiveremos mais que MAX_DATA_IN dividimos os dados
	// em pedaços do tamanho MAX_DATA_IN fazendo update() a cada um
	// fazemos doFinal aos remanescentes dados
	if(fullPackets > 0){
		for(i = 0; i < fullPackets; i++){

			//cipher
			update(&inArr[i * MAX_DATA_IN], MAX_DATA_IN, buffer_out, &returnSize);
			memcpy(&buffer_temp[totalSize], buffer_out, returnSize);
			totalSize += returnSize;

		}

		//Cifrar o ultimo Pacote
		doFinal(&inArr[fullPackets * MAX_DATA_IN], remainBytes, buffer_out, &returnSize);
		memcpy(&buffer_temp[totalSize], buffer_out, returnSize);
		totalSize += returnSize;

		//Cria Array de Retorno
		outArray = env->NewByteArray((jsize) totalSize);
		jsize start = (jsize) 0;
		jsize len = (jsize) totalSize;
		jbyte *buf = (jbyte*) buffer_temp;
		env->SetByteArrayRegion(outArray, start, len , buf);

	}else{

		//cipher
		doFinal(inArr, lengthOfArray, buffer_out, &returnSize);

		//Criar array de retorno
		outArray = env->NewByteArray((jsize) returnSize);
		jsize start = (jsize) 0;
		jsize len = (jsize) returnSize;
		jbyte *buf = (jbyte*) buffer_out;
		env->SetByteArrayRegion(outArray, start, len , buf);

	}

	//Limpar Bufers
	memset(&buffer_temp, 0, sizeof(buffer_temp));
	memset(&buffer_out, 0, sizeof(buffer_out));

	//release Array (memory leak problem)
	env->ReleaseByteArrayElements(array,inArr,JNI_ABORT);
	return outArray;
}
