package aiss.aesBox;

import java.io.File;

public class AESboxJNI {

	static {

		String osName = System.getProperty("os.name");
		String libName = "libaesbox.so";
		String libDir = new File("").getAbsolutePath() + "/";

		//Fora do Eclipse (Jar)
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

		//byte[] plainText = new byte[1600*50*2];
		byte[] plainText = ("Isto e uma frase muito nice e quero desejar feliz natal ").getBytes();
//							"e boa pascoa e bom ano novo a todos os meus amigos, mas como" +
//							" esta frase esta Cifrada com AES ninguem vai Decobrir. Feliz" +
//							" Natal a todos. :) ").getBytes();
		byte[] encText;
		byte[] plainText2 = null;

		System.out.println("plainText IN: "+(new String(plainText)));
		encText = box.Encrypt(plainText);
		System.out.println("encText: "+(new String(encText)));

		plainText2 = box.Decrypt(encText);
		System.out.println("plainText2: "+(new String(plainText2)));
	}

}
