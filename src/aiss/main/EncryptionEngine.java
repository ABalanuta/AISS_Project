package aiss.main;

import java.io.IOException;
import java.security.spec.InvalidKeySpecException;

import javax.crypto.Cipher;

import sun.misc.BASE64Encoder;
import sun.security.pkcs11.wrapper.PKCS11Exception;

public class EncryptionEngine implements Engine{

	private static final boolean DEBUG = true;
	private Boolean authentication = null;
	private Boolean confidentiality = null;
	private Boolean timestamping = null;
	private String operations = "";

	public EncryptionEngine(boolean authentication, boolean confidentiality, boolean timestamping){
		this.authentication = authentication;
		this.confidentiality = confidentiality;
		this.timestamping = timestamping;

		if(authentication){
			operations += "A";
		}
		if(confidentiality){
			operations += "C";
		}
		if(timestamping){
			operations += "T";
		}

	}


	@Override
	public void run() {
		debug("Engine Run");
		String contentsBase64 = "";
		String signatureBase64First = ""; 	//SHA_256
		String signatureBase64Second = "";	//RIPMD_160
		String publicKeyBase64 = "";
		String timeStampBase64 = "";
		String timeStampSignBase64 = "";

		FileManager fm = new FileManager();
		BASE64Encoder base64encoder = new BASE64Encoder();

		byte[] fileByteContent = fm.getFolderContentInZipByteArray();

		debug("ZIP ByteArray Size is: "+ fileByteContent.length);

		// If no file Found return 
		if(fileByteContent == null){
			return;
		}


		if(authentication){
			debug("Engine Authentication Service");

			try {
				publicKeyBase64 = base64encoder.encode(aiss.ccauthentication.Signature.obtainPKey());
				byte[][] signatures = aiss.ccauthentication.Signature.createSignature(fileByteContent);
				signatureBase64First = base64encoder.encode(signatures[0]);
				signatureBase64Second = base64encoder.encode(signatures[1]);
			} catch (PKCS11Exception e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			} catch (InvalidKeySpecException e) {
				e.printStackTrace();
			}
		}


		if(confidentiality){
			debug("Engine Confidentiality Service");

			// TODO Chamar invocaca7o para cifrar o conteudo
			// CipherHandeler ch = new CipherHandeler("AES...");
			//fileByteContent = ch.encrypt(fileByteContent);
		}



		if(timestamping){
			debug("Engine TimeStamping Service");
			
			timeStampBase64 = "12H";
			timeStampSignBase64 = "";
		}


		// Passar o conteudo para Base64
		contentsBase64 = base64encoder.encode(fileByteContent);

		// Escrever o conteudo para um ficheiro XML
		fm.writeXML(operations, contentsBase64, publicKeyBase64, signatureBase64First,
				signatureBase64Second,timeStampBase64,timeStampSignBase64);
	}

	private static void debug(String log){
		if(DEBUG){
			System.out.println(log);
		}
	}

}
