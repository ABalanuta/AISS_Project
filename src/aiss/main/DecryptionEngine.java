package aiss.main;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PublicKey;
import java.security.Security;
import java.security.Signature;
import java.security.SignatureException;
import javax.security.cert.CertificateException;
import javax.security.cert.X509Certificate;
import org.w3c.dom.Document;
import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

import org.bouncycastle.jce.provider.BouncyCastleProvider;


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
		boolean verifiedFirst = false;
		boolean verifiedSecond = false;
		final String PROVIDER = BouncyCastleProvider.PROVIDER_NAME;

		if(xml == null){
			debug("Cannot Continue: Aborting");
			return;
		}

		String operationsTAG = getXMLvalue(xml, "Operations");
		String base64Message = getXMLvalue(xml, "Message");
		String certBase64 = getXMLvalue(xml, "Certificate");
		String signatureBase64First = getXMLvalue(xml, "Signature_1");
		String signatureBase64Second = getXMLvalue(xml, "Signature_2");

		debug("Operation: " + operationsTAG);
		debug("Message: " + base64Message);
		debug("Certificate: " + certBase64);
		debug("Signature_1: " + signatureBase64First);
		debug("Signature_2: " + signatureBase64Second);

		Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());


		try {	
			zipBytes = base64decoder.decodeBuffer(base64Message);
			debug("ByteArray Size is: "+ zipBytes.length);
		} catch (IOException e) {
			System.out.println("Should not Happen");
			return;
		}

		if(operationsTAG.contains("A")){

			// TODO message Validation
			MessageDigest msg;
			X509Certificate certificate;
			String[] senderName = null;
			String[] senderNationality = null;

			try {
				msg = MessageDigest.getInstance("SHA");
				msg.update(zipBytes);
				msg.digest();

				// prepares public key
				certificate = X509Certificate.getInstance(base64decoder.decodeBuffer(certBase64));
				PublicKey pubkey = certificate.getPublicKey();

				String[] senderData = certificate.getSubjectDN().getName().split(",");
				senderName = senderData[0].split("=");
				senderNationality = senderData[4].split("=");

				//verifies the signature1

				Signature sig1 = Signature.getInstance("SHA256withRSA");
				sig1.initVerify(pubkey);

				//update signature1

				sig1.update(zipBytes);      
				verifiedFirst = sig1.verify(base64decoder.decodeBuffer(signatureBase64First));
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
				fm.saveFilesFromZipByteArray(zipBytes);
				fm.createValidationFile("Validacao Efectuada c/ Sucesso \n" + "Enviado por: " + senderName[1] + "\nNacionalidade: " + senderNationality[1] + "\n");
			} else if(verifiedFirst == false || verifiedSecond == false){
				fm.saveFilesFromZipByteArray("Validation Failed! :(".getBytes());
				fm.createValidationFile("Validacao Nao Efectuada\n");
			}
		} 

		if (operationsTAG.contains("C")){
			//TODO Stuff
		} 

		if (operationsTAG.contains("T")){
			//TODO Stuff
		}
	}
	private String getXMLvalue(Document doc, String tag){
		return doc.getElementsByTagName(tag).item(0).getChildNodes().item(0).getTextContent();
	}

	private static void debug(String log){
		if(DEBUG){
			System.out.println(log);
		}
	}

}
