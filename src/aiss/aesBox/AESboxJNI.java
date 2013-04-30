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

			//byte[] plainText = new byte[1600*50*2];
			byte[] plainText = "Isto e uma testes Espetacular".getBytes();
			byte[] encText;
			byte[] plainText2 = null;

			System.out.println("plainText IN:"+(new String(plainText)));
			encText = box.Encrypt(plainText);

				if(encText == null){
					System.out.println("IS NULL\n");
				}else{
					System.out.println("NOT NULL\n");
					System.out.println(encText.length);
				}


			//System.out.println("encText:"+(new String(encText)));
			//plainText2 = box.Decrypt(encText);

			//	System.out.println("plainText2:"+(new String(plainText2)));
		}

	}
