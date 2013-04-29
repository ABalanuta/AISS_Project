package aiss.main;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.RandomAccessFile;

import java.io.File;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import java.io.File;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import org.zeroturnaround.zip.ZipUtil;

public class FileManager {

	private static final boolean DEBUG = false;
	private static final String INZIPFILENAME = "input.zip";
	private static final String TMPFILENAME = "text.in";

	private static final String OUTPUT_ZIP_FILE = new File("").getAbsolutePath() + "/in.zip";
	private static final String SOURCE_FOLDER = new File("").getAbsolutePath() + "/in";
	private static final String VALIDATION_FILE = new File("").getAbsolutePath() + "/validationLog.txt";
	private byte[] zipBytes = null;

	public static void main(String args[]){

		//FileManager fm = new FileManager();
		//System.out.println("FileManager test");
		//fm.writeXML("ACT", "text", "pkey" ,"sign1", "sign2");

	}


	public void saveFilesFromZipByteArray(byte[] zipBytes){

		File zip = new File(OUTPUT_ZIP_FILE);
		File folder = new File(SOURCE_FOLDER);

		debug(zip.getAbsolutePath());
		debug(folder.getAbsolutePath());
		ByteArrayInputStream bis = new ByteArrayInputStream(zipBytes);
		ZipUtil.unpack(bis, folder);
	}

	public void appendToValidationFile(String log){

		System.out.println("Append to "+ VALIDATION_FILE);

		File logFile = new File(VALIDATION_FILE);
		try{
			PrintWriter out = new PrintWriter(logFile);
			out.write("----------AESecure:----------\n" + log + "----------AESecure:----------\n");
			out.close();
		}catch(IOException e){
			System.out.println("COULD NOT LOG!!");
		}

	}

	@SuppressWarnings("resource")
	public byte[] getFolderContentInZipByteArray() {

		File zip = new File(OUTPUT_ZIP_FILE);
		File folder = new File(SOURCE_FOLDER);
		debug(zip.getAbsolutePath());
		debug(folder.getAbsolutePath());
		ZipUtil.pack(folder, zip);

		debug(zip.getAbsolutePath());

		try {

			FileInputStream in = new FileInputStream(zip);
			zipBytes = new byte[(int) zip.length()];
			in.read(zipBytes);

			//			in = new RandomAccessFile(new File(INFILENAME), "r");
			//			zipBytes = new byte[(int) in.length()];
			//			for(int i=0; in.readBoolean() == false; i++){
			//				zipBytes[i] = in.readByte();
			//			}
			//in.write(zipBytes);
			//in.readFully(zipBytes);

		} catch (FileNotFoundException e) {
			System.out.println("Zip file Not Found");
			return null;
		} catch (IOException e) {
			System.out.println("Should not Happen");
			return null;
		} catch (NullPointerException e) {
			System.out.println("Should not Happen");
			return null;
		}



		// Delete Zip File
		zip.delete();
		debug("Zip Deleted");


		return zipBytes;
	}

	public void writeXML(String opr, String msg, String cert , String sign, String timeSsign){

		try {

			DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder docBuilder = docFactory.newDocumentBuilder();

			// root elements
			Document doc = docBuilder.newDocument();
			Element rootElement = doc.createElement("AESecure_XML_Message");
			doc.appendChild(rootElement);

			// operations aplied to Message
			Element operations = doc.createElement("Operations");
			operations.appendChild(doc.createTextNode(opr));
			rootElement.appendChild(operations);

			// message in Base64
			Element message = doc.createElement("Message");
			message.appendChild(doc.createTextNode(msg));
			rootElement.appendChild(message);

			// PubKey in Base64
			Element certificate = doc.createElement("Certificate");
			certificate.appendChild(doc.createTextNode(cert));
			rootElement.appendChild(certificate);

			// signature 1 in Base64
			Element signature = doc.createElement("Signature");
			signature.appendChild(doc.createTextNode(sign));
			rootElement.appendChild(signature);

			// TimeStamp Signature in Base64
			Element timeStampSign = doc.createElement("TimeStampSignature");
			timeStampSign.appendChild(doc.createTextNode(timeSsign));
			rootElement.appendChild(timeStampSign);

			// write the content into xml file
			TransformerFactory transformerFactory = TransformerFactory.newInstance();
			Transformer transformer = transformerFactory.newTransformer();
			DOMSource source = new DOMSource(doc);
			String path = new File("").getAbsolutePath() + "/" + TMPFILENAME;
			StreamResult result = new StreamResult(new File(path));
			transformer.transform(source, result);

			debug(path);

		} catch (ParserConfigurationException pce) {
			pce.printStackTrace();
		} catch (TransformerException tfe) {
			tfe.printStackTrace();
		}
	}





	private static void debug(String log){
		if(DEBUG){
			System.out.println(log);
		}
	}



	public Document readEncryptedXml(){

		String path = new File("").getAbsolutePath() + "/" + TMPFILENAME;

		DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
		Document doc = null;

		try {

			DocumentBuilder docBuilder = docBuilderFactory.newDocumentBuilder();
			doc = docBuilder.parse(new File(path));

		} catch (SAXException e) {
			System.out.println("Shuld not happen SAX");
			e.printStackTrace();
			return null;
		} catch (IOException e) {
			System.out.println("File Not Found");
			return null;
		} catch (ParserConfigurationException e) {
			System.out.println("Shuld not happen Parser");
			return null;
		}

		doc.getDocumentElement().normalize();
		return doc;
	}

}
