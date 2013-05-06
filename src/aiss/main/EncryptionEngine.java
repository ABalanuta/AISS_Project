package aiss.main;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PublicKey;
import java.security.Security;
import java.security.Signature;
import java.security.SignatureException;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;

import javax.crypto.Cipher;
import javax.security.cert.CertificateException;
import javax.security.cert.X509Certificate;

import aiss.aesBox.AESboxJNI;
import aiss.aesJava.MyAES;
import aiss.tss.client.TSSClient;

import com.sun.xml.internal.ws.api.message.Message;

import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;
import sun.security.pkcs11.wrapper.PKCS11Exception;

public class EncryptionEngine implements Engine{

	private static final boolean DEBUG = true;
	private static final boolean AES_BOX_PRESENT = true;
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
		
		FileManager fm = new FileManager();
		BASE64Encoder base64encoder = new BASE64Encoder();
		Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());
		
		byte[] fileByteContent = null;
		byte[] cert = null;
		byte[] signature = null;
		byte[] timeStamp = null;
		
		
		// Adquire os conteudos da pacsta
		fileByteContent = fm.getFolderContentInZipByteArray();
		
		// If no file Found return 
		if(fileByteContent == null){
			return;
		}

		debug("ZIP ByteArray Size is: "+ fileByteContent.length);

		if(authentication){
			debug("Engine Authentication Service");

			try {
				cert = aiss.ccauthentication.Signature.obtainCert();

				//first hash/digest
				MessageDigest md1 = MessageDigest.getInstance("SHA-256");
				md1.update(fileByteContent);
				byte[] mdigest1 = md1.digest();

				//second hash/digest
				MessageDigest md2 = MessageDigest.getInstance("RIPEMD160","BC");
				md2.update(fileByteContent);
				byte[] mdigest2 = md2.digest();

				byte[] mdigestTotal = new byte[mdigest1.length + mdigest2.length];
				System.arraycopy(mdigest1,0,mdigestTotal,0,mdigest1.length);
				System.arraycopy(mdigest2,0,mdigestTotal,mdigest1.length,mdigest2.length);

				signature = aiss.ccauthentication.Signature.createSignature(mdigestTotal);

			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		
		
		if(timestamping){
			debug("Engine TimeStamping Service");
			
			byte[] textHash = TSSClient.byteDigestSHA256(fileByteContent);
			debug("Text SHA-256 Hash: " + base64encoder.encode(textHash));

			timeStamp = TSSClient.generateTimeStamp(textHash);
		}

		
		
		//Cifrar todos os Campos a enviar
		if(confidentiality){
			debug("Engine Confidentiality Service");
			
			fileByteContent = Cipher(fileByteContent);
			
//			if(authentication){
//				cert = Cipher(cert);
//				signature = Cipher(signature);
//			}
			
			if(timestamping){
				timeStamp = Cipher(timeStamp);
			}	
		}

		
		// Passar os campos para Base64
		String contentsBase64 = base64encoder.encode(fileByteContent);
		String certBase64 = "";
		String signatureBase64 = "";
		String timeStampSignBase64 = "";
		
		if(authentication){
			certBase64 = base64encoder.encode(cert);
			signatureBase64 = base64encoder.encode(signature);
		}
		
		if(timestamping){
			timeStampSignBase64 = base64encoder.encode(timeStamp);
		}
		
		// Escrever o conteudo para um ficheiro XML
		fm.writeXML(operations, contentsBase64, certBase64, signatureBase64,timeStampSignBase64);
	}
	
	// Classe que escolha um dos dois Devices da Cifra AES, utilizando a box
	// ou utilizando o modulo de software criado no laboratorio
	private byte[] Cipher(byte[] content){
		
		if(AES_BOX_PRESENT){
			debug("Using HARDWARE for AES Encryption");
			AESboxJNI aes = new AESboxJNI();
			return aes.CipherAll(AESboxJNI.ENCRYPT, content);
		}else{
			debug("Using JAVA for AES Encryption");
			// BACKUP MODE
			MyAES aes = new MyAES();
			return aes.CipherAll(MyAES.ENCRYPT, "AES_KEY_256.key", MyAES.CBC, content);
		}
	}
	
	
	private static void debug(String log){
		if(DEBUG){
			System.out.println(log);
		}
	}

}
