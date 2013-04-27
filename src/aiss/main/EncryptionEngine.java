package aiss.main;

import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.Signature;
import java.security.SignatureException;
import java.security.spec.InvalidKeySpecException;

import javax.crypto.Cipher;

import aiss.tss.client.TSSClient;

import com.sun.xml.internal.ws.api.message.Message;

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
		String certBase64 = "";
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
				certBase64 = base64encoder.encode(aiss.ccauthentication.Signature.obtainCert());
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

			BASE64Encoder encoder = new BASE64Encoder();
			byte[] textHash = null;

			try {
				
				MessageDigest timeSig = MessageDigest.getInstance("SHA-256");
				timeSig.update(fileByteContent);
				textHash = timeSig.digest();
				
			} catch (Exception e) {
				e.printStackTrace();
			}

			debug("Text SHA-256 Hash: " + encoder.encode(textHash));
			
			byte[] timeStamp = TSSClient.generateTimeStamp(textHash);
			timeStampSignBase64 = encoder.encode(timeStamp);
		}


		// Passar o conteudo para Base64
		contentsBase64 = base64encoder.encode(fileByteContent);

		// Escrever o conteudo para um ficheiro XML
		fm.writeXML(operations, contentsBase64, certBase64, signatureBase64First,
				signatureBase64Second,timeStampSignBase64);
	}

	private static void debug(String log){
		if(DEBUG){
			System.out.println(log);
		}
	}

}
