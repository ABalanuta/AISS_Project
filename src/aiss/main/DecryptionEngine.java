package aiss.main;

import java.io.IOException;

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
		
		if(xml == null){
			debug("Cannot Continue: Aborting");
			return;
		}
		
		String operationsTAG = getXMLvalue(xml, "Operations");
		String base64Message = getXMLvalue(xml, "Message");
		String signatureBase64First = getXMLvalue(xml, "Signature_1");
		String signatureBase64Second = getXMLvalue(xml, "Signature_2");
		
		debug("Operation: " + operationsTAG);
		debug("Message: " + base64Message);
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
		
		fm.saveFilesFromZipByteArray(zipBytes);
		
		
		
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
