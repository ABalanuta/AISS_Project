package aiss.main;

import java.io.IOException;
import java.security.MessageDigest;
import java.security.PublicKey;
import java.security.Security;
import java.security.Signature;

import javax.security.cert.X509Certificate;

import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.w3c.dom.Document;
import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;
import aiss.aesJava.MyAES;
import aiss.tss.client.TSSClient;


public class DecryptionEngine implements Engine{

	private static final boolean DEBUG = true;

	@Override
	public void run() {
		// TODO Auto-generated method stub
		debug("Engine Run");

		FileManager fm = new FileManager();
		BASE64Decoder base64decoder = new BASE64Decoder();
		BASE64Encoder base64encoder = new BASE64Encoder();
		Document xml = fm.readEncryptedXml();
		byte[] zipBytes = null;
		boolean verified = false;
		final String PROVIDER = BouncyCastleProvider.PROVIDER_NAME;
		String logToClient = "";
		if(xml == null){
			debug("Cannot Continue: Aborting");
			return;
		}

		String operationsTAG = getXMLvalue(xml, "Operations");
		String base64Message = getXMLvalue(xml, "Message");
		String certBase64 = getXMLvalue(xml, "Certificate");
		String signatureBase64 = getXMLvalue(xml, "Signature");
		String timeStampSignBase64 = getXMLvalue(xml, "TimeStampSignature");
		//
		//		debug("Operation: " + operationsTAG);
		//		debug("Message: " + base64Message);
		//		debug("Certificate: " + certBase64);
		//		debug("Signature_1: " + signatureBase64First);
		//		debug("Signature_2: " + signatureBase64Second);
		//		debug("TimeStampSignature: " + timeStampSignBase64);

		Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());


		try {	
			zipBytes = base64decoder.decodeBuffer(base64Message);
			debug("ByteArray Size is: "+ zipBytes.length);
		} catch (IOException e) {
			System.out.println("Should not Happen");
			return;
		}
		
		// Se a messagem foi cifrada vamos descifra-la
		if (operationsTAG.contains("C")){
			debug("Engine Confidentiality Service");

			// BACKUP MODE
			MyAES aes = new MyAES();
			zipBytes = aes.CipherAll(MyAES.DECRYPT, "AES_KEY_256.key", MyAES.CBC, zipBytes);
			
			logToClient += "-----Conf<br>\n";
			logToClient += "Cifrado com AES<br>\n";
			logToClient += "-----<br>\n";

		}
		
		// Save files from zip to the folder
		fm.saveFilesFromZipByteArray(zipBytes);

		if(operationsTAG.contains("A")){
			debug("Engine Auth Service");

			// TODO message Validation
			X509Certificate certificate;
			String senderData = null;
			try {
				
				MessageDigest md1 = MessageDigest.getInstance("SHA-256");
				md1.update(zipBytes);
				byte[] mdigest1 = md1.digest();
				
				//second hash/digest
				MessageDigest md2 = MessageDigest.getInstance("RIPEMD160",PROVIDER);
				md2.update(zipBytes);
				byte[] mdigest2 = md2.digest();
				
				byte[] mdigestTotal = new byte[mdigest1.length + mdigest2.length];
				System.arraycopy(mdigest1,0,mdigestTotal,0,mdigest1.length);
				System.arraycopy(mdigest2,0,mdigestTotal,mdigest1.length,mdigest2.length);

				// prepares public key
				certificate = X509Certificate.getInstance(base64decoder.decodeBuffer(certBase64));
				PublicKey pubkey = certificate.getPublicKey();
				senderData = certificate.getSubjectDN().getName();

				//verifies the signature1

				Signature sig = Signature.getInstance("SHA1withRSA");
				sig.initVerify(pubkey);

				//update signature1

				sig.update(mdigestTotal);
				byte[] tmp = base64decoder.decodeBuffer(signatureBase64);
				verified = sig.verify(tmp);
				debug("Verified result: " + verified);

			} catch (Exception e) {
				e.printStackTrace();
			} 

			if(verified == true){
				logToClient += "-----Auth<br>\n";
				logToClient += "Validacao Efectuada c/ Sucesso <br>\n" + "Enviado por: \n\t" + senderData + "<br>\n";
				logToClient += "-----<br>\n";
			} else if(verified == false){
				logToClient += "-----Auth<br>\n";
				logToClient += "Validacao Nao Efectuada<br>\n";
				logToClient += "-----<br>\n";
			}
		} 

		

		if (operationsTAG.contains("T")){
			debug("Engine TimeStamping Service");

			byte[] signedTimeStamp = null;
			try {
				signedTimeStamp = base64decoder.decodeBuffer(timeStampSignBase64);
			} catch (IOException e) {
				e.printStackTrace();
			}

			byte[] zipHash = TSSClient.byteDigestSHA256(zipBytes);
			String timeStamp = TSSClient.getTimeStamp(zipHash, signedTimeStamp);
			if(timeStamp.contains("GMT")){
				logToClient += "-----TimeStamp<br>\n";
				logToClient += "TimeStamp is Valid! <br>\n" + "Time of Creation is " + timeStamp + "<br>\n";
				logToClient += "-----<br>\n";
			}else{
				logToClient += "-----TimeStamp<br>";
				logToClient += "TimeStamp is INVALID !!!";
				logToClient += "-----<br>";
			}
		}
		
		fm.appendToValidationFile(logToClient);
		
		
	}

	private String getXMLvalue(Document doc, String tag){

		try{

			return doc.getElementsByTagName(tag).item(0).getChildNodes().item(0).getTextContent();

		}catch(NullPointerException e){
			return null;
		}
	}


	private static void debug(String log){
		if(DEBUG){
			System.out.println(log);
		}
	}

}
