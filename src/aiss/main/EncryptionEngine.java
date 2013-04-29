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

import aiss.aesJava.MyAES;
import aiss.tss.client.TSSClient;

import com.sun.xml.internal.ws.api.message.Message;

import sun.misc.BASE64Decoder;
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
		String signatureBase64 = "";
		String certBase64 = "";
		String timeStampSignBase64 = "";

		FileManager fm = new FileManager();
		BASE64Encoder base64encoder = new BASE64Encoder();

		byte[] fileByteContent = fm.getFolderContentInZipByteArray();
		Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());

		// If no file Found return 
		if(fileByteContent == null){
			return;
		}

		debug("ZIP ByteArray Size is: "+ fileByteContent.length);

		if(authentication){
			debug("Engine Authentication Service");

			try {
				certBase64 = base64encoder.encode(aiss.ccauthentication.Signature.obtainCert());

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

				signatureBase64 = base64encoder.encode(aiss.ccauthentication.Signature.createSignature(mdigestTotal));

			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		if(timestamping){
			debug("Engine TimeStamping Service");

			BASE64Encoder encoder = new BASE64Encoder();
			byte[] textHash = TSSClient.byteDigestSHA256(fileByteContent);
			debug("Text SHA-256 Hash: " + encoder.encode(textHash));

			byte[] timeStamp = TSSClient.generateTimeStamp(textHash);
			timeStampSignBase64 = encoder.encode(timeStamp);
		}

		if(confidentiality){
			debug("Engine Confidentiality Service");

			// BACKUP MODE
			MyAES aes = new MyAES();
			fileByteContent = aes.CipherAll(MyAES.ENCRYPT, "AES_KEY_256.key", MyAES.CBC, fileByteContent);
			
		}

		// Passar o conteudo para Base64
		contentsBase64 = base64encoder.encode(fileByteContent);

		// Escrever o conteudo para um ficheiro XML
		fm.writeXML(operations, contentsBase64, certBase64, signatureBase64,timeStampSignBase64);
	}

	private static void debug(String log){
		if(DEBUG){
			System.out.println(log);
		}
	}

}
