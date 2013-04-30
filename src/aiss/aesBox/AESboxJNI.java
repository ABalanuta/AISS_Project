import java.nio.ByteBuffer;

//package aiss.aesBox;

public class AESboxJNI {

	static {
		String osName = System.getProperty("os.name");
		String libName = "aesbox"; //Linux
		System.out.println(System.getProperty("java.library.path"));

		System.loadLibrary(libName);
	}
	
	private native byte[] Encrypt(byte[] text);

	private native byte[] Decrypt(byte[] text);

	public static void main(String[] args) {
		AESboxJNI box = new AESboxJNI();

		byte[] plainText = "Isto é um Teste".getBytes();
		byte[] encText = null;
		byte[] plainText2 = null;

		System.out.println("plainText IN:"+(new String(plainText)));
		encText = box.Encrypt(plainText);
		System.out.println("plainText OUT:"+(new String(plainText)));
		
		
		System.out.println("encText:"+(new String(encText)));
		plainText2 = box.Decrypt(encText);
		
		System.out.println("plainText2:"+(new String(plainText2)));
	}

}
