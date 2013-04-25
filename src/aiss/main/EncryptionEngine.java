package aiss.main;

import java.io.IOException;

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
		String contentsBase64 = null;
		String signatureBase64First = null; //SHA_256
		String signatureBase64Second = null;	//RIPMD_160
		String timeStampBase64 = null;
		String timeStampsignBase64 = null;;
		
		FileManager fm = new FileManager();
		BASE64Encoder base64encoder = new BASE64Encoder();
		
		byte[] fileByteContent = fm.getFolderContentInZipByteArray();
		debug("ByteArray Size is: "+ fileByteContent.length);
		
		// 
		if(fileByteContent == null){
			return;
		}
		
	
		debug("Engine auth");
		if(authentication){
			// TODO gerar em funcao do cartao de cidadao
			// Temp
			try {
				signatureBase64First = aiss.ccauthentication.Main.createSignature(fileByteContent,"CKM_SHA256_RSA_PKCS");
			} catch (PKCS11Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			signatureBase64First = base64encoder.encode(new byte[16]);
			try {
				signatureBase64Second = aiss.ccauthentication.Main.createSignature(fileByteContent, "CKM_RIPEMD160_RSA_PKCS");
			} catch (PKCS11Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			signatureBase64Second = base64encoder.encode(new byte[16]);
		}
		
		debug("Engine conf");
		if(confidentiality){
			// TODO Chamar invocação para cifrar o conteudo
			// CipherHandeler ch = new CiptherHandeler("AES...");
			//fileByteContent = ch.encrypt(fileByteContent);

		}
		
		debug("Engine time");
		if(timestamping){
			// TODO TSS
		}
		
		
		debug("Engine encode base 64");
		// passar o conteudo para Base64
		contentsBase64 = base64encoder.encode(fileByteContent);
		
		debug("Engine xml");
		// Gravar o conteudo
		fm.writeXML(operations, contentsBase64, signatureBase64First, signatureBase64Second);
		
	}
	
	private static void debug(String log){
		if(DEBUG){
			System.out.println(log);
		}
	}

}
