package aiss.main;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
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

import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class FileManager {

	private static final String INFILENAME = "input.zip";
	private static final String OUTFILENAME = "text.out";
	private byte[] zipBytes = null;

	public static void main(String args[]){

		//FileManager fm = new FileManager();
		//System.out.println("FileManager test");
		//fm.writeXML("text", "sign1", "sign2");

	}



	public FileManager(){
	}



	@SuppressWarnings("resource")
	public byte[] getFolderContentInZipByteArray() {

		ZipManager zm = new ZipManager();
		zm.zipInputFolderTo(INFILENAME);

		RandomAccessFile in;

		try {
			in = new RandomAccessFile(new File(INFILENAME), "r");
			in.readFully(zipBytes);

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return zipBytes;
	}

	public void writeXML(String xml, String sign1, String sign2){

		try {

			DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder docBuilder = docFactory.newDocumentBuilder();

			// root elements
			Document doc = docBuilder.newDocument();
			Element rootElement = doc.createElement("AESecure_XML_Message");
			doc.appendChild(rootElement);

			// message in Base64
			Element message = doc.createElement("Message");
			message.appendChild(doc.createTextNode(xml));
			rootElement.appendChild(message);

			// signature 1 in Base64
			Element signature1 = doc.createElement("Signature_1");
			signature1.appendChild(doc.createTextNode(sign1));
			rootElement.appendChild(signature1);

			// signature 2 in Base64
			Element signature2 = doc.createElement("Signature_2");
			signature2.appendChild(doc.createTextNode(sign2));
			rootElement.appendChild(signature2);

			// write the content into xml file
			TransformerFactory transformerFactory = TransformerFactory.newInstance();
			Transformer transformer = transformerFactory.newTransformer();
			DOMSource source = new DOMSource(doc);
			StreamResult result = new StreamResult(new File(OUTFILENAME));
			transformer.transform(source, result);



		} catch (ParserConfigurationException pce) {
			pce.printStackTrace();
		} catch (TransformerException tfe) {
			tfe.printStackTrace();
		}
	}



}
