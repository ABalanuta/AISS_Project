package aiss.aesJava;
import java.util.Arrays;

public class AES {

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

	// Cipher Mode and Action
	private static int Cipher = -1; //cipher operation: 1-encryption, 2-decryption, 3-keyGeneration
	private static int CMode = -1; //block cipher modes. ECB, CBC or CTR

	// The last block ramains in memory until doFinal method is called
	private byte[] finalBlock = new byte[0];

	//AES block size in bits
	public static final int BLOCK_BITS  = 128;

	//ES block size in bytes
	public static final int BLOCK_SIZE  = (BLOCK_BITS >>> 3);

	//IV variable for the CBC Mode
	private static byte[] iv = new byte[] {0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,
		0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00};

	//Variables for the CTR Mode
	private static byte[] nounce = new byte[] {0x01,0x02,0x03,0x04,0x05,0x06,0x07,0x08};
	private static byte[] counter = new byte[] {0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00};
	private static long counterN = 0;

	//Substitution table (S-box).
	private static final String SS =
			"\u637C\u777B\uF26B\u6FC5\u3001\u672B\uFED7\uAB76" +
					"\uCA82\uC97D\uFA59\u47F0\uADD4\uA2AF\u9CA4\u72C0" +
					"\uB7FD\u9326\u363F\uF7CC\u34A5\uE5F1\u71D8\u3115" +
					"\u04C7\u23C3\u1896\u059A\u0712\u80E2\uEB27\uB275" +
					"\u0983\u2C1A\u1B6E\u5AA0\u523B\uD6B3\u29E3\u2F84" +
					"\u53D1\u00ED\u20FC\uB15B\u6ACB\uBE39\u4A4C\u58CF" +
					"\uD0EF\uAAFB\u434D\u3385\u45F9\u027F\u503C\u9FA8" +
					"\u51A3\u408F\u929D\u38F5\uBCB6\uDA21\u10FF\uF3D2" +
					"\uCD0C\u13EC\u5F97\u4417\uC4A7\u7E3D\u645D\u1973" +
					"\u6081\u4FDC\u222A\u9088\u46EE\uB814\uDE5E\u0BDB" +
					"\uE032\u3A0A\u4906\u245C\uC2D3\uAC62\u9195\uE479" +
					"\uE7C8\u376D\u8DD5\u4EA9\u6C56\uF4EA\u657A\uAE08" +
					"\uBA78\u252E\u1CA6\uB4C6\uE8DD\u741F\u4BBD\u8B8A" +
					"\u703E\uB566\u4803\uF60E\u6135\u57B9\u86C1\u1D9E" +
					"\uE1F8\u9811\u69D9\u8E94\u9B1E\u87E9\uCE55\u28DF" +
					"\u8CA1\u890D\uBFE6\u4268\u4199\u2D0F\uB054\uBB16";

	private static final byte[] Se = new byte[256];
	private static final byte[] Sd = new byte[256];

	private static final int[] 	
			Te0 = new int[256], Te1 = new int[256],
			Te2 = new int[256], Te3 = new int[256];

	private static final int[]	
			Td0 = new int[256], Td1 = new int[256],
			Td2 = new int[256], Td3 = new int[256];

	//Round constants
	private static final int[] rcon = new int[10]; /* for 128-bit blocks, Rijndael never uses more than 10 rcon values */

	//Number of rounds (depends on key size).
	private int Nr = 0;
	private int Nk = 0;
	private int Nw = 0;

	//Encryption key schedule
	private int rek[] = null;

	// Decryption key schedule
	private int rdk[] = null;

	static {

		int ROOT = 0x11B;
		int s1, s2, s3, i1, i2, i4, i8, i9, ib, id, ie, t;
		for (i1 = 0; i1 < 256; i1++) {
			char c = SS.charAt(i1 >>> 1);
			s1 = (byte)((i1 & 1) == 0 ? c >>> 8 : c) & 0xff;
			s2 = s1 << 1;
			if (s2 >= 0x100) {
				s2 ^= ROOT;
			}
			s3 = s2 ^ s1;
			i2 = i1 << 1;
			if (i2 >= 0x100) {
				i2 ^= ROOT;
			}
			i4 = i2 << 1;
			if (i4 >= 0x100) {
				i4 ^= ROOT;
			}
			i8 = i4 << 1;
			if (i8 >= 0x100) {
				i8 ^= ROOT;
			}
			i9 = i8 ^ i1;
			ib = i9 ^ i2;
			id = i9 ^ i4;
			ie = i8 ^ i4 ^ i2;

			Se[i1] = (byte)s1;
			Te0[i1] = t = (s2 << 24) | (s1 << 16) | (s1 << 8) | s3;
			Te1[i1] = (t >>>  8) | (t  << 24);
			Te2[i1] = (t >>> 16) | (t  << 16);
			Te3[i1] = (t >>> 24) | (t  <<  8);

			Sd[s1] = (byte)i1;
			Td0[s1] = t = (ie << 24) | (i9 << 16) | (id << 8) | ib;
			Td1[s1] = (t >>>  8) | (t  << 24);
			Td2[s1] = (t >>> 16) | (t  << 16);
			Td3[s1] = (t >>> 24) | (t  <<  8);
		}

		//round constants
		int r = 1;
		rcon[0] = r << 24;
		for (int i = 1; i < 10; i++) {
			r <<= 1;
			if (r >= 0x100) {
				r ^= ROOT;
			}
			rcon[i] = r << 24;
		}
	}

	/**
	 * Expand a cipher key into a full encryption key schedule.
	 * @param   cipherKey   the cipher key (128, 192, or 256 bits).
	 */
	private void expandKey(byte[] cipherKey) {
		int temp, r = 0;
		for (int i = 0, k = 0; i < Nk; i++, k += 4) {
			rek[i] =
					((cipherKey[k    ]       ) << 24) |
					((cipherKey[k + 1] & 0xff) << 16) |
					((cipherKey[k + 2] & 0xff) <<  8) |
					((cipherKey[k + 3] & 0xff));
		}
		for (int i = Nk, n = 0; i < Nw; i++, n--) {
			temp = rek[i - 1];
			if (n == 0) {
				n = Nk;
				temp =
						((Se[(temp >>> 16) & 0xff]       ) << 24) |
						((Se[(temp >>>  8) & 0xff] & 0xff) << 16) |
						((Se[(temp       ) & 0xff] & 0xff) <<  8) |
						((Se[(temp >>> 24)       ] & 0xff));
				temp ^= rcon[r++];
			} else if (Nk == 8 && n == 4) {
				temp =
						((Se[(temp >>> 24)       ]       ) << 24) |
						((Se[(temp >>> 16) & 0xff] & 0xff) << 16) |
						((Se[(temp >>>  8) & 0xff] & 0xff) <<  8) |
						((Se[(temp       ) & 0xff] & 0xff));
			}
			rek[i] = rek[i - Nk] ^ temp;
		}
		temp = 0;
	}


	/**
	 * Compute the decryption schedule from the encryption schedule .
	 */
	private void invertKey() {
		int d = 0, e = 4*Nr, w;
		/*
		 * apply the inverse MixColumn transform to all round keys
		 * but the first and the last:
		 */
		rdk[d    ] = rek[e    ];
		rdk[d + 1] = rek[e + 1];
		rdk[d + 2] = rek[e + 2];
		rdk[d + 3] = rek[e + 3];
		d += 4;
		e -= 4;
		for (int r = 1; r < Nr; r++) {
			w = rek[e    ];
			rdk[d    ] =
					Td0[Se[(w >>> 24)       ] & 0xff] ^
					Td1[Se[(w >>> 16) & 0xff] & 0xff] ^
					Td2[Se[(w >>>  8) & 0xff] & 0xff] ^
					Td3[Se[(w       ) & 0xff] & 0xff];
			w = rek[e + 1];
			rdk[d + 1] =
					Td0[Se[(w >>> 24)       ] & 0xff] ^
					Td1[Se[(w >>> 16) & 0xff] & 0xff] ^
					Td2[Se[(w >>>  8) & 0xff] & 0xff] ^
					Td3[Se[(w       ) & 0xff] & 0xff];
			w = rek[e + 2];
			rdk[d + 2] =
					Td0[Se[(w >>> 24)       ] & 0xff] ^
					Td1[Se[(w >>> 16) & 0xff] & 0xff] ^
					Td2[Se[(w >>>  8) & 0xff] & 0xff] ^
					Td3[Se[(w       ) & 0xff] & 0xff];
			w = rek[e + 3];
			rdk[d + 3] =
					Td0[Se[(w >>> 24)       ] & 0xff] ^
					Td1[Se[(w >>> 16) & 0xff] & 0xff] ^
					Td2[Se[(w >>>  8) & 0xff] & 0xff] ^
					Td3[Se[(w       ) & 0xff] & 0xff];
			d += 4;
			e -= 4;
		}
		rdk[d    ] = rek[e    ];
		rdk[d + 1] = rek[e + 1];
		rdk[d + 2] = rek[e + 2];
		rdk[d + 3] = rek[e + 3];
	}

	/**
	 * Xor Function Used by the CBC and CTR Modes
	 */
	private static byte[] xor_func(byte[] a, byte[] b) {
		byte[] out = new byte[a.length];
		for (int i = 0; i < a.length; i++) {
			out[i] = (byte) (a[i] ^ b[i]);
		}
		return out;
	}

	/**
	 * Inicializes the AES Algorithm, which include s the key expansion.
	 * @param	Cipher		
	 * @param   cipherKey   the cipher key (128, 192, or 256 bits).
	 * @param   direction   cipher direction (DIR_ENCRYPT, DIR_DECRYPT, or DIR_BOTH).
	 */
	public void init(int Cipher, int CMode, byte[] cipherKey)
			throws RuntimeException {

		this.Cipher = Cipher;
		this.CMode = CMode;
		int keyBits = cipherKey.length * 8;

		// check key size:
		if (keyBits != 128 && keyBits != 192 && keyBits != 256) {
			throw new RuntimeException("Invalid AES key size (" + keyBits + " bits)");
		}

		Nk = keyBits >>> 5;
		Nr = Nk + 6;
		Nw = 4*(Nr + 1);
		rek = new int[Nw];
		rdk = new int[Nw];
		expandKey(cipherKey);

		if (Cipher == DECRYPT) {
			invertKey();
		}
	}

	/**
	 * Function that receives an array of bytes to encrypt/dectipt,
	 * and returnes with the encrypted/decrypted array.
	 */
	public byte[] update(byte[] text, int inputlen){

		byte[] returnArray = new byte[inputlen];

		debug("Update: TextLen: "+ text.length + " InputLen: " + inputlen);

		if(inputlen != text.length){

			int blocksToProcess;

			//When is to Decrypt we allway save the last block
			if(Cipher == DECRYPT){
				blocksToProcess = (inputlen/BLOCK_SIZE) - 1;
			}else{
				blocksToProcess = inputlen/BLOCK_SIZE;
			}

			int newInputLen = blocksToProcess * BLOCK_SIZE;
			returnArray = new byte[newInputLen];

			finalBlock = new byte[inputlen-newInputLen];
			finalBlock = Arrays.copyOfRange(text, newInputLen, inputlen);
			debug("Update: " + (inputlen-newInputLen) + " bytes were saved in memory.");
			inputlen = newInputLen;
		}


		if(Cipher == ENCRYPT){

			for(int i = 0; i < inputlen; i += BLOCK_SIZE){

				if(CMode == ECB){
					System.arraycopy(encrypt(Arrays.copyOfRange(text, i, i+BLOCK_SIZE)), 0, returnArray, i, BLOCK_SIZE);
				}

				else if(CMode == CBC){
					byte[] xor = xor_func(Arrays.copyOfRange(text, i, i+BLOCK_SIZE), iv);	//xor with iv
					System.arraycopy(encrypt(xor), 0, returnArray, i, BLOCK_SIZE);	//cipher
					System.arraycopy(returnArray, i, iv, 0, BLOCK_SIZE);	//copy new iv
				}

				else if(CMode == CTR){
					byte[] ctr = encrypt(concat(nounce, counter)); //encrypts the nounce and counter
					System.arraycopy(xor_func(ctr, Arrays.copyOfRange(text, i, i+BLOCK_SIZE)), 0, returnArray, i, BLOCK_SIZE);	// copy of plaintext		
					addCounter();
				}
			}
			return returnArray;
		}


		else if(Cipher == DECRYPT){

			for(int i = 0; i < inputlen; i += BLOCK_SIZE){

				if(CMode == ECB){
					System.arraycopy(decrypt(Arrays.copyOfRange(text, i, i+BLOCK_SIZE)), 0, returnArray, i, BLOCK_SIZE);
				}

				else if(CMode == CBC){
					byte[] xor = xor_func(decrypt(Arrays.copyOfRange(text, i, i+BLOCK_SIZE)), iv);	//xor with iv
					System.arraycopy(xor, 0, returnArray, i, BLOCK_SIZE);	// copy of plaintext
					iv = Arrays.copyOfRange(text, i, i+BLOCK_SIZE);	//new iv
				}
				else if(CMode == CTR){
					byte[] ctr = encrypt(concat(nounce, counter)); //encrypts the nounce and counter
					System.arraycopy(xor_func(ctr, Arrays.copyOfRange(text, i, i+BLOCK_SIZE)), 0, returnArray, i, BLOCK_SIZE);	// copy of ciphertext		
					addCounter();
				}
			}
			return returnArray;
		}

		else{throw new RuntimeException("No Action is Possible");}
	}



	/**
	 * When all the block is to be Processed we will call this method that redirects
	 * to the update method with the correct inputlen parameter
	 */
	public byte[] update(byte[] plaintext){
		return update(plaintext, plaintext.length);
	}


	/**
	 * Does the processing of the last BLock, Adding or removing the padding
	 */
	public byte[] doFinal(byte[] lastBlock, int intputlen){

		byte[] returnBlock = new byte[BLOCK_SIZE];

		if(Cipher == ENCRYPT){

			if(intputlen == 0){
				debug("doFinal: Filling last block with padding and Cipthering it");
				returnBlock = doPadding(returnBlock, 0, BLOCK_SIZE);
			}else{
				System.arraycopy(lastBlock, 0, returnBlock, 0, intputlen);
				doPadding(returnBlock, intputlen, BLOCK_SIZE-intputlen);
			}

			return update(returnBlock);
		}

		else if(Cipher == DECRYPT){
			return undoPadding(lastBlock); 
		}

		finalize();
		return lastBlock;
	}

	/**
	 * Calls the doFinal Function with the apropriate
	 * Lenght of the array parameters
	 */
	public byte[] doFinal(byte[] lastBlock){
		return doFinal(lastBlock, lastBlock.length);
	}

	/**
	 * Calls the doFinal Function with the saved in memory Block
	 */
	public byte[] doFinal(){
		return doFinal(finalBlock, finalBlock.length);
	}


	/**
	 * Auxiliary Function that adds an integer to the counter
	 * representend in bytes
	 */
	private void addCounter(){
		counterN++;
		counter = longToByteArray(counterN);
	}

	/**
	 * Transforms a long type number into a byteArray representation of it
	 */
	private static final byte[] longToByteArray(long value) {
		return new byte[] {
				(byte)(value >>> 56),
				(byte)(value >>> 48),
				(byte)(value >>> 40),
				(byte)(value >>> 32),
				(byte)(value >>> 24),
				(byte)(value >>> 16),
				(byte)(value >>> 8),
				(byte)value};
	}


	/**
	 * Concatenates two ByteArrays, used by the CTR Mode
	 */
	private byte[] concat(byte[]...arrays)
	{
		// Determine the length of the result array
		int totalLength = 0;
		for (int i = 0; i < arrays.length; i++)
		{
			totalLength += arrays[i].length;
		}

		// create the result array
		byte[] result = new byte[totalLength];

		// copy the source arrays into the result array
		int currentIndex = 0;
		for (int i = 0; i < arrays.length; i++)
		{
			System.arraycopy(arrays[i], 0, result, currentIndex, arrays[i].length);
			currentIndex += arrays[i].length;
		}

		return result;
	}

	/**
	 * Adds Padding to fill an 128 bits block using PKCS7 Scheme.
	 */
	private byte[] doPadding(byte[] input, int from, int num){

		for(int x = from; x < input.length; x++){
			input[x] = (byte) num;
		}
		debug("doPadding: Added "+ num +" bytes of padding.");
		return input;
	}

	/**
	 * Removes the PKCS7 Padding
	 */
	private byte[] undoPadding(byte[] lastBlock) {

		byte[] padded = update(lastBlock);
		debug("undoPadding: Removed "+ (int)padded[padded.length-1] +" bytes of padding.");
		return Arrays.copyOf(padded, BLOCK_SIZE-(int)padded[padded.length-1]);
	}


	/**
	 * Encrypt exactly one block (BLOCK_SIZE bytes) of plaintext.
	 *
	 * @param   pt          plaintext block.
	 * @param   ct          ciphertext block.
	 */
	private byte[] encrypt(byte[] pt) {

		byte[] ct = new byte[pt.length];

		/*
		 * map byte array block to cipher state
		 * and add initial round key:
		 */
		int k = 0, v;
		int t0   = ((pt[ 0]       ) << 24 |
				(pt[ 1] & 0xff) << 16 |
				(pt[ 2] & 0xff) <<  8 |
				(pt[ 3] & 0xff)        ) ^ rek[0];
		int t1   = ((pt[ 4]       ) << 24 |
				(pt[ 5] & 0xff) << 16 |
				(pt[ 6] & 0xff) <<  8 |
				(pt[ 7] & 0xff)        ) ^ rek[1];
		int t2   = ((pt[ 8]       ) << 24 |
				(pt[ 9] & 0xff) << 16 |
				(pt[10] & 0xff) <<  8 |
				(pt[11] & 0xff)        ) ^ rek[2];
		int t3   = ((pt[12]       ) << 24 |
				(pt[13] & 0xff) << 16 |
				(pt[14] & 0xff) <<  8 |
				(pt[15] & 0xff)        ) ^ rek[3];
		/*
		 * Nr - 1 full rounds:
		 */
		for (int r = 1; r < Nr; r++) {
			k += 4;
			int a0 =
					Te0[(t0 >>> 24)       ] ^
					Te1[(t1 >>> 16) & 0xff] ^
					Te2[(t2 >>>  8) & 0xff] ^
					Te3[(t3       ) & 0xff] ^
					rek[k    ];
			int a1 =
					Te0[(t1 >>> 24)       ] ^
					Te1[(t2 >>> 16) & 0xff] ^
					Te2[(t3 >>>  8) & 0xff] ^
					Te3[(t0       ) & 0xff] ^
					rek[k + 1];
			int a2 =
					Te0[(t2 >>> 24)       ] ^
					Te1[(t3 >>> 16) & 0xff] ^
					Te2[(t0 >>>  8) & 0xff] ^
					Te3[(t1       ) & 0xff] ^
					rek[k + 2];
			int a3 =
					Te0[(t3 >>> 24)       ] ^
					Te1[(t0 >>> 16) & 0xff] ^
					Te2[(t1 >>>  8) & 0xff] ^
					Te3[(t2       ) & 0xff] ^
					rek[k + 3];
			t0 = a0; t1 = a1; t2 = a2; t3 = a3;
		}
		/*
		 * last round lacks MixColumn:
		 */
		k += 4;

		v = rek[k    ];
		ct[ 0] = (byte)(Se[(t0 >>> 24)       ] ^ (v >>> 24));
		ct[ 1] = (byte)(Se[(t1 >>> 16) & 0xff] ^ (v >>> 16));
		ct[ 2] = (byte)(Se[(t2 >>>  8) & 0xff] ^ (v >>>  8));
		ct[ 3] = (byte)(Se[(t3       ) & 0xff] ^ (v       ));

		v = rek[k + 1];
		ct[ 4] = (byte)(Se[(t1 >>> 24)       ] ^ (v >>> 24));
		ct[ 5] = (byte)(Se[(t2 >>> 16) & 0xff] ^ (v >>> 16));
		ct[ 6] = (byte)(Se[(t3 >>>  8) & 0xff] ^ (v >>>  8));
		ct[ 7] = (byte)(Se[(t0       ) & 0xff] ^ (v       ));

		v = rek[k + 2];
		ct[ 8] = (byte)(Se[(t2 >>> 24)       ] ^ (v >>> 24));
		ct[ 9] = (byte)(Se[(t3 >>> 16) & 0xff] ^ (v >>> 16));
		ct[10] = (byte)(Se[(t0 >>>  8) & 0xff] ^ (v >>>  8));
		ct[11] = (byte)(Se[(t1       ) & 0xff] ^ (v       ));

		v = rek[k + 3];
		ct[12] = (byte)(Se[(t3 >>> 24)       ] ^ (v >>> 24));
		ct[13] = (byte)(Se[(t0 >>> 16) & 0xff] ^ (v >>> 16));
		ct[14] = (byte)(Se[(t1 >>>  8) & 0xff] ^ (v >>>  8));
		ct[15] = (byte)(Se[(t2       ) & 0xff] ^ (v       ));

		return ct;
	}


	/**
	 * Decrypt exactly one block (BLOCK_SIZE bytes) of ciphertext.
	 *
	 * @param   ct          ciphertext block.
	 * @param   pt          plaintext block.
	 */
	private byte[] decrypt(byte[] ct ) {

		byte[] pt = new byte[ct.length];
		/*
		 * map byte array block to cipher state
		 * and add initial round key:
		 */
		int k = 0, v;
		int t0 =   ((ct[ 0]       ) << 24 |
				(ct[ 1] & 0xff) << 16 |
				(ct[ 2] & 0xff) <<  8 |
				(ct[ 3] & 0xff)        ) ^ rdk[0];
		int t1 =   ((ct[ 4]       ) << 24 |
				(ct[ 5] & 0xff) << 16 |
				(ct[ 6] & 0xff) <<  8 |
				(ct[ 7] & 0xff)        ) ^ rdk[1];
		int t2 =   ((ct[ 8]       ) << 24 |
				(ct[ 9] & 0xff) << 16 |
				(ct[10] & 0xff) <<  8 |
				(ct[11] & 0xff)        ) ^ rdk[2];
		int t3 =   ((ct[12]       ) << 24 |
				(ct[13] & 0xff) << 16 |
				(ct[14] & 0xff) <<  8 |
				(ct[15] & 0xff)        ) ^ rdk[3];
		/*
		 * Nr - 1 full rounds:
		 */
		for (int r = 1; r < Nr; r++) {
			k += 4;
			int a0 =
					Td0[(t0 >>> 24)       ] ^
					Td1[(t3 >>> 16) & 0xff] ^
					Td2[(t2 >>>  8) & 0xff] ^
					Td3[(t1       ) & 0xff] ^
					rdk[k    ];
			int a1 =
					Td0[(t1 >>> 24)       ] ^
					Td1[(t0 >>> 16) & 0xff] ^
					Td2[(t3 >>>  8) & 0xff] ^
					Td3[(t2       ) & 0xff] ^
					rdk[k + 1];
			int a2 =
					Td0[(t2 >>> 24)       ] ^
					Td1[(t1 >>> 16) & 0xff] ^
					Td2[(t0 >>>  8) & 0xff] ^
					Td3[(t3       ) & 0xff] ^
					rdk[k + 2];
			int a3 =
					Td0[(t3 >>> 24)       ] ^
					Td1[(t2 >>> 16) & 0xff] ^
					Td2[(t1 >>>  8) & 0xff] ^
					Td3[(t0       ) & 0xff] ^
					rdk[k + 3];
			t0 = a0; t1 = a1; t2 = a2; t3 = a3;
		}
		/*
		 * last round lacks MixColumn:
		 */
		k += 4;

		v = rdk[k    ];
		pt[ 0] = (byte)(Sd[(t0 >>> 24)       ] ^ (v >>> 24));
		pt[ 1] = (byte)(Sd[(t3 >>> 16) & 0xff] ^ (v >>> 16));
		pt[ 2] = (byte)(Sd[(t2 >>>  8) & 0xff] ^ (v >>>  8));
		pt[ 3] = (byte)(Sd[(t1       ) & 0xff] ^ (v       ));

		v = rdk[k + 1];
		pt[ 4] = (byte)(Sd[(t1 >>> 24)       ] ^ (v >>> 24));
		pt[ 5] = (byte)(Sd[(t0 >>> 16) & 0xff] ^ (v >>> 16));
		pt[ 6] = (byte)(Sd[(t3 >>>  8) & 0xff] ^ (v >>>  8));
		pt[ 7] = (byte)(Sd[(t2       ) & 0xff] ^ (v       ));

		v = rdk[k + 2];
		pt[ 8] = (byte)(Sd[(t2 >>> 24)       ] ^ (v >>> 24));
		pt[ 9] = (byte)(Sd[(t1 >>> 16) & 0xff] ^ (v >>> 16));
		pt[10] = (byte)(Sd[(t0 >>>  8) & 0xff] ^ (v >>>  8));
		pt[11] = (byte)(Sd[(t3       ) & 0xff] ^ (v       ));

		v = rdk[k + 3];
		pt[12] = (byte)(Sd[(t3 >>> 24)       ] ^ (v >>> 24));
		pt[13] = (byte)(Sd[(t2 >>> 16) & 0xff] ^ (v >>> 16));
		pt[14] = (byte)(Sd[(t1 >>>  8) & 0xff] ^ (v >>>  8));
		pt[15] = (byte)(Sd[(t0       ) & 0xff] ^ (v       ));

		return pt;
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
	 * Destroy all sensitive information in this object.
	 */
	protected final void finalize() {
		if (rek != null) {
			for (int i = 0; i < rek.length; i++) {
				rek[i] = 0;
			}
			rek = null;
		}
		if (rdk != null) {
			for (int i = 0; i < rdk.length; i++) {
				rdk[i] = 0;
			}
			rdk = null;
		}
	}
}

