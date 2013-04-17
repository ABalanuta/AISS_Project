package aiss.tss.client;

import java.io.FileInputStream;
import java.math.BigInteger;

import org.bouncycastle.asn1.ASN1InputStream;
import org.bouncycastle.cms.CMSSignedDataGenerator;
import org.bouncycastle.cms.SignerId;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.tsp.TimeStampRequest;
import org.bouncycastle.tsp.TimeStampResponse;
import org.bouncycastle.tsp.TimeStampToken;
import org.bouncycastle.tsp.TimeStampTokenInfo;
import org.bouncycastle.tsp.TSPException;

public class TSSclient {




	public static void main(String[] args){

		System.out.println("Hello Humans I am A Client");


		try{
			FileInputStream inreq=new FileInputStream("tsq"); // request       
			FileInputStream inresp=new FileInputStream("tsr");// response
			TimeStampRequest req = new TimeStampRequest (inreq);
			TimeStampResponse resp = new TimeStampResponse (inresp);
			resp.validate (req);  // if it fails a TSPException is raised
			System.out.println ("TimeStamp verified.");
			TimeStampToken  tsToken = resp.getTimeStampToken();
			TimeStampTokenInfo tsInfo= tsToken.getTimeStampInfo();
			SignerId signer_id = tsToken.getSID();
			BigInteger cert_serial_number = signer_id.getSerialNumber();
			System.out.println ("Generation time " + tsInfo.getGenTime());
			System.out.println ("Signer ID serial "+signer_id.getSerialNumber());
		//	System.out.println ("Signer ID issuer "+signer_id.getIssuerAsString());
		} catch(TSPException tsex){
			System.out.println(tsex.getMessage());
		} catch(Exception ex){
			ex.printStackTrace();
		}


	}


}
