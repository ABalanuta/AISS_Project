package aiss.aesJava;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Random;
import java.util.concurrent.ExecutionException;

public class MyAES {

	//Debug Mode
	private static final boolean DEBUG = false;

	// Type of Actions
	public static final int ENCRYPT = 1;
	public static final int DECRYPT = 2;
	public static final int KEYGEN = 3;

	// Encryption Modes
	public static final int ECB = 10;
	public static final int CBC = 20;
	public static final int CTR = 30;

	// Read/Write Buffer Parameters
	public static final int BUFF_SIZE = 1024*256; //256KB


	private static int Cipher = -1; //cipher operation: 1-encryption, 2-decryption, 3-keyGeneration
	private static int CMode = -1; //block cipher modes. ECB, CBC or CTR

	private static String keyFilePath = null;	//FilePAth to the Key
	private static byte[] key; 					//Key
	private static String keySize = null; 		//128bits, 192bits, 256bits

	private static String inFilePath = null;
	private static String outFilePath = null;

	// Parameters for BenchMarking
	private static boolean benchmarking = false;
	private static long bytesProcessed = 0;
	private static long initTime;
	private static long endTime;



	/**
	 * Main Method
	 * @param args input parameters
	 * @throws Exception wrong paramaeters Introduction
	 */
	public static void main(String args[]) throws Exception{

		/**
		 * Arguments Reader and Clasification
		 */
		for (int i = 0 ; i < args.length ; i++){

			debug("Argument "+ i +" is "+args[i]);


			if(args[i].equals("-e")){Cipher = ENCRYPT;} 
			else if (args[i].equals("-d")){Cipher = DECRYPT;} 
			else if (args[i].equals("-g")){Cipher = KEYGEN; keySize = args[i+1];} 

			else if (args[i].equals("-f")){
				keyFilePath = System.getProperty("user.dir") + "/" + args[i+1];
				//loads the specified key for encryption or description
				if(Cipher != KEYGEN){
					loadKey();
				}
			}
			else if (args[i].equals("-k")){
				key = hexStringToByteArrayKEY(args[i+1]);
			} 
			else if (args[i].equals("-i")){
				inFilePath = System.getProperty("user.dir") + "/" + args[i+1];
			} 
			else if (args[i].equals("-o")){
				outFilePath = System.getProperty("user.dir") + "/" + args[i+1];
			} 
			else if (args[i].equals("-ecb")){CMode = ECB;} 
			else if (args[i].equals("-cbc")){CMode = CBC;} 
			else if (args[i].equals("-ctr")){CMode = CTR;} 

			else if (args[i].equals("-b")){
				benchmarking = true; initTime = System.currentTimeMillis();
			}
		}

		/**
		 * Operation Chooser
		 */
		if((Cipher == ENCRYPT || Cipher == DECRYPT) && 
				CMode == ECB || CMode == CBC || CMode == CTR){
			cipher();
		}

		else if(Cipher == KEYGEN){
			generateKey(Integer.parseInt(keySize));
		}

		else{
			System.out.println("How to Use:");
			System.out.println("	MyAES [-e/d] [-g KeySize] [-ecb/cbc/ctr] [-f Keyfile] [-k Key] [-i inputfile] [-o outputfile] [-b]");
			System.out.println("\n		[-e/d]		Seletcts mode -e for Encryption -d for Decription");
			System.out.println("		[-g KeySize] 	generates a a key, wich size is specified: 128, 192 or 256 (bits)");
			System.out.println("		[-ecb/cbc/ctr] 	Enciption/Decryption Mode");
			System.out.println("		[-f Keyfile] 	Specifies a file ether to save the generated key or to load it");
			System.out.println("		[-k Key] 	Specifies a key in Hexa Form");
			System.out.println("		[-i inputfile] 	Path to Input File");
			System.out.println("		[-o outputfile] Path to Output File	\n");
			System.out.println("		[-b] Benchmarks the algorithm bytes/s");
		}

		if(benchmarking){
			endTime = System.currentTimeMillis();
			System.out.println("BenchMark: "+((float)(bytesProcessed)+ " Bytes in " + ((float)(endTime - initTime)/1000)+" seconds"));
			System.out.println("BenchMark: "+((double)(bytesProcessed/1024)/((endTime - initTime))+" MBytes/second"));
		}

	}


	public byte[] CipherAll(int Cipher, String keyFileName, int mode, byte[] content){
		
		keyFilePath = System.getProperty("user.dir") + "/" + keyFileName;
		
		try {
			loadKey();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
			
		AES aes = new AES();

		//Inicialize AES Algorithm
		aes.init(Cipher, mode, key);
		debug("Inicialized");
		
		ByteArrayInputStream inFile = new ByteArrayInputStream(content);
		ByteArrayOutputStream outFile = new ByteArrayOutputStream();

		byte[] plainTextBuff = new byte[BUFF_SIZE];
		byte[] cipherTextBuff = new byte[BUFF_SIZE];
		int nReadBytes;

		try{
		
		while ((nReadBytes=inFile.read( plainTextBuff, 0, BUFF_SIZE )) != -1 ){

			debug("BytesRead "+nReadBytes);
			bytesProcessed+=nReadBytes;
			cipherTextBuff = aes.update(plainTextBuff, nReadBytes);
			outFile.write(cipherTextBuff);
		}

		//Finalizes the Algorithm
		cipherTextBuff = aes.doFinal();
		outFile.write(cipherTextBuff);

		// Closing Files
		outFile.flush();
		inFile.close();
		outFile.close();
		}catch(Exception e){
			e.getStackTrace();
		}
		
		return outFile.toByteArray();
	}
	
	
	
	
	
	
	
	
	/**
	 * Loads the key specified at the input cli
	 * @throws IOException no such file Exeption
	 */
	private static void loadKey() throws IOException{

		// loading the key if specified
		if(keyFilePath != null){
			byte[] buff = new byte[32];
			int readBytes = 0;
			BufferedInputStream inKey = new BufferedInputStream(new FileInputStream(keyFilePath));
			readBytes = inKey.read(buff);
			inKey.close();
			key = new byte[readBytes];
			for(int x = 0; x < readBytes; x++){
				key[x] = buff[x];
			}
			debug("Loaded a "+readBytes*8+"bits Key fom input file");
		}else{
			System.out.println("Invalid key Path");
		}
	}



	/**
	 * Generates keys of diferent dimentions and writes it to file if it is specied
	 * @param key size, 128,192 or 256 bits keys
	 * @throws IOException when no file to save the key was provided
	 */
	private static void generateKey(int keysize) throws IOException{

		byte[] key = new byte[keysize/8];
		new Random().nextBytes(key);

		System.out.println("Your " + keySize + " bits key is: " + getHexString(key));

		//Writes key to specified location
		if(keyFilePath != null){
			BufferedOutputStream outKey = new BufferedOutputStream(new FileOutputStream(keyFilePath));
			outKey.write(key);
			outKey.flush();
			outKey.close();
		}

	}

	/**
	 * The method that actualy ciphers the file that is read from the input,
	 * it iniciates the algorithm with the right settings does the ciphering/deciphering,
	 * and finalizes it correctly,invoking the doFinal functions that inserts/removes the padding.
	 * @throws Exception for IO Acces
	 */
	private static void cipher() throws Exception {

		if(key == null){
			System.out.println("No Key was Especified! Quiting...");
			return;
		}

		debug("Ciphering/Deciphering");

		AES aes = new AES();

		//Inicialize AES Algorithm
		aes.init(Cipher, CMode, key);
		debug("Inicialized");

		BufferedInputStream inFile = new BufferedInputStream(new FileInputStream(inFilePath) );
		BufferedOutputStream outFile = new BufferedOutputStream(new FileOutputStream(outFilePath) );

		byte[] plainTextBuff = new byte[BUFF_SIZE];
		byte[] cipherTextBuff = new byte[BUFF_SIZE];
		int nReadBytes;

		while ((nReadBytes=inFile.read( plainTextBuff, 0, BUFF_SIZE )) != -1 ){

			debug("BytesRead "+nReadBytes);
			bytesProcessed+=nReadBytes;
			cipherTextBuff = aes.update(plainTextBuff, nReadBytes);
			outFile.write(cipherTextBuff);
		}

		//Finalizes the Algorithm
		cipherTextBuff = aes.doFinal();
		outFile.write(cipherTextBuff);

		// Closing Files
		outFile.flush();
		inFile.close();
		outFile.close();
		return;
	}

	/**
	 * Debug Functions that facilitates Debugging
	 * @param String to Print
	 */
	private static void debug(String s){
		if(DEBUG){
			System.out.println(s);
		}
	}

	/**
	 * Method that Converts a ByteArray to Hexadecimal String
	 * @param b byteArray to transform
	 * @return returns a string representing the hexadecimal representation
	 */
	private static String getHexString(byte[] b){
		String result = "";
		for (int i=0; i < b.length; i++) {
			result +=
					Integer.toString( ( b[i] & 0xff ) + 0x100, 16).substring( 1 );
		}
		return result;
	}

	/**
	 * Transforms a String of Hexadecimal Information into a ByteArray that
	 * is later used as a key for ciphering
	 * @param s String to transform
	 * @return returns a ByteArray representing the hexadecimal String
	 */
	private static byte[] hexStringToByteArrayKEY(String s) {
		int len = s.length();
		byte[] data = new byte[len / 2];
		for (int i = 0; i < len; i += 2) {
			data[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4)
					+ Character.digit(s.charAt(i+1), 16));
		}
		debug("Loaded a "+data.length*8+"bits Key fom input");
		return data;
	}
}
