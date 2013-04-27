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
import aiss.tss.client.TSSClient;


public class DecryptionEngine implements Engine{

	private static final boolean DEBUG = true;

	@Override
	public void run() {
		// TODO Auto-generated method stub
		debug("Engine Run");

		FileManager fm = new FileManager();
		BASE64Decoder base64decoder = new BASE64Decoder();
		Document xml = fm.readEncryptedXml();
		byte[] zipBytes = null;
		boolean verifiedFirst = false;
		boolean verifiedSecond = false;
		final String PROVIDER = BouncyCastleProvider.PROVIDER_NAME;
		String logToClient = "";
		if(xml == null){
			debug("Cannot Continue: Aborting");
			return;
		}

		String operationsTAG = getXMLvalue(xml, "Operations");
		String base64Message = getXMLvalue(xml, "Message");
		String certBase64 = getXMLvalue(xml, "Certificate");
		String signatureBase64First = getXMLvalue(xml, "Signature_1");
		String signatureBase64Second = getXMLvalue(xml, "Signature_2");
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

		fm.saveFilesFromZipByteArray(zipBytes);

		if(operationsTAG.contains("A")){
			debug("Engine Auth Service");

			// TODO message Validation
			MessageDigest msg;
			X509Certificate certificate;
			String senderData = null;
			try {
				msg = MessageDigest.getInstance("SHA");
				msg.update(zipBytes);
				msg.digest();

				// prepares public key
				certificate = X509Certificate.getInstance(base64decoder.decodeBuffer(certBase64));
				PublicKey pubkey = certificate.getPublicKey();
				System.out.println("Version is: " + certificate.getVersion());
				senderData = certificate.getSubjectDN().getName();

				//verifies the signature1

				Signature sig1 = Signature.getInstance("SHA256withRSA");
				sig1.initVerify(pubkey);

				//update signature1

				sig1.update(zipBytes);
				byte[] tmp = base64decoder.decodeBuffer(signatureBase64First);
				verifiedFirst = sig1.verify(tmp);
				debug("VerifiedFirst result: " + verifiedFirst);

				//verifies the signature2
				Signature sig2 = Signature.getInstance("RIPEMD160WithRSAEncryption", PROVIDER);
				sig2.initVerify(pubkey);

				//update signature2
				sig2.update(zipBytes);     

				verifiedSecond = sig2.verify(base64decoder.decodeBuffer(signatureBase64Second));
				debug("VerifiedSecond result: " + verifiedSecond);

			} catch (Exception e) {
				e.printStackTrace();
			} 

			if(verifiedFirst == true && verifiedSecond == true){
				logToClient += "-----Auth\n";
				logToClient += "Validacao Efectuada c/ Sucesso \n" + "Enviado por: \n\t" + senderData + "\n";
				logToClient += "-----\n";
			} else if(verifiedFirst == false || verifiedSecond == false){
				logToClient += "-----Auth\n";
				logToClient += "Validacao Nao Efectuada\n";
				logToClient += "-----\n";
			}
		} 

		if (operationsTAG.contains("C")){
			//TODO Stuff
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
				logToClient += "-----TimeStamp\n";
				logToClient += "TimeStamp is Valid! \n" + "Time of Creation is " + timeStamp + "\n";
				logToClient += "-----\n";
			}else{
				logToClient += "-----TimeStamp\n";
				logToClient += "TimeStamp is INVALID !!!";
				logToClient += "-----\n";
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
