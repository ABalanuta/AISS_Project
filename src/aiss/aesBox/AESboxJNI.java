package aiss.aesBox;

import java.io.File;
import java.util.Arrays;

public class AESboxJNI {

	static {

		String osName = System.getProperty("os.name");
		String libName = "libaesbox.so";
		String libDir = new File("").getAbsolutePath() + "/";

		//Fora do Eclipse (Jar)
		if(osName.contains("Mac")){
			libName = "libaesbox.jnilib";
		}
		try{			
			System.load(libDir+libName);
			// Dentro do Eclipse
		}catch(UnsatisfiedLinkError e){
			libDir += "/src/aiss/aesBox/";
			System.load(libDir+libName);
		}
	}


	// Type of Actions
	public static final int ENCRYPT = 1;
	public static final int DECRYPT = 2;


	private native byte[] Encrypt(byte[] text);

	private native byte[] Decrypt(byte[] text);


	public byte[] CipherAll(int Cipher,byte[] content){

		if(Cipher == ENCRYPT){
			return this.Encrypt(content);
		}
		else if(Cipher == DECRYPT){
			return this.Decrypt(content);
		}
		else{
			throw new RuntimeException("No such Cipther Mode !");
		}
	}


	public static void main(String[] args) {
		AESboxJNI box = new AESboxJNI();

		//Teste Exaustivo no caso sem padding
		for(long i = 1; i<10000; i += 1){
			byte[] plainText = String.format("%"+i+"s", "").replace(' ', 'W').getBytes();
			byte[] encText = box.Encrypt(plainText);

			// remover para testar com pading
			//if(encText.length != plainText.length){
			//	System.out.println("Error: length dont match at " + i);
			//	break;
			//}

			byte[] plainText2 = box.Decrypt(encText);
			
			
			if(!Arrays.equals(plainText,plainText2)){
				System.out.println("Error: Enc/Dec dont match at " + i);
				System.out.println(":"+plainText2 + ":" + plainText+":");
				break;
			}

			if(i%50 == 0){
				System.out.println(i+" ");
			}
		}
	}

}
