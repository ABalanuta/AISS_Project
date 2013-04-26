package aiss.main;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.Signature;
import java.security.SignatureException;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

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
		byte[] pkey = null;
		boolean verifiedFirst = false;
		boolean verifiedSecond = false;

		if(xml == null){
			debug("Cannot Continue: Aborting");
			return;
		}

		String operationsTAG = getXMLvalue(xml, "Operations");
		String base64Message = getXMLvalue(xml, "Message");
		String publicKeyBase64 = getXMLvalue(xml, "Public_Key");
		String signatureBase64First = getXMLvalue(xml, "Signature_1");
		String signatureBase64Second = getXMLvalue(xml, "Signature_2");

		debug("Operation: " + operationsTAG);
		debug("Message: " + base64Message);
		debug("Public_Key: " + publicKeyBase64);
		debug("Signature_1: " + signatureBase64First);
		debug("Signature_2: " + signatureBase64Second);
		
		try {	
			zipBytes = base64decoder.decodeBuffer(base64Message);
			debug("ByteArray Size is: "+ zipBytes.length);
		} catch (IOException e) {
			System.out.println("Should not Happen");
			return;
		}

		// TODO message Validation
		MessageDigest msg;
		KeyFactory keyFactory;
		try {
			msg = MessageDigest.getInstance("SHA");
			msg.update(zipBytes);
			msg.digest();
			
			// prepares public key
			keyFactory = KeyFactory.getInstance("RSA");
			X509EncodedKeySpec publicKeySpec = new X509EncodedKeySpec(base64decoder.decodeBuffer(publicKeyBase64));
			PublicKey pubkey = keyFactory.generatePublic(publicKeySpec);
			
			//verifies the signature1

			Signature sig1 = Signature.getInstance("SHA256withRSA");
			sig1.initVerify(pubkey);

			//update signature1

			sig1.update(zipBytes);      
			verifiedFirst = sig1.verify(base64decoder.decodeBuffer(signatureBase64First));
			debug("Verified result: " + verifiedFirst);
					
//			//verifies the signature2
//			Signature sig2 = Signature.getInstance("RIPEMDwithRSA");
//			sig2.initVerify(pubkey);
//
//			//update signature2
//			sig2.update(zipBytes);      
//			verifiedFirst = sig2.verify(base64decoder.decodeBuffer(signatureBase64Second));
//			debug("Verified result: " + verifiedSecond);
			
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvalidKeyException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvalidKeySpecException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SignatureException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		debug("Verified result: " + verifiedFirst);
		
		if(verifiedFirst == true /* && verifiedSecond == true */){
			fm.saveFilesFromZipByteArray(zipBytes);
		} else if(verifiedFirst == false /* || verifiedSecond == false */){
			fm.saveFilesFromZipByteArray("Validation Failed! :(".getBytes());
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
