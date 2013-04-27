package aiss.tss.aux;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

import org.apache.commons.io.IOUtils;

public class KeyManager {

	private static final boolean DEBUG = true;

	public void GenerateNewKeys(String pathPrivate, String pathPublic){

		debug("WARNING Generating new Key Pair");
		debug("		Old Keys Are Now Obsolete");

		KeyPairGenerator keyGen = null;
		try {
			keyGen = KeyPairGenerator.getInstance("RSA");
		} catch (NoSuchAlgorithmException e) {
			debug("Should never happen because RSA Algorithm exists");
		}

		keyGen.initialize(2048);
		KeyPair keyPair = keyGen.generateKeyPair();
		PrivateKey privateKey = keyPair.getPrivate();
		PublicKey  publicKey  = keyPair.getPublic();

		writePublicKey(pathPublic, publicKey);
		writePrivateKey(pathPrivate, privateKey);

		debug("Keys Saved");
	}


	public  void writePublicKey(String path, PublicKey key){
		try {

			X509EncodedKeySpec x509ks = new X509EncodedKeySpec(key.getEncoded());
			FileOutputStream fos = new FileOutputStream(path);
			fos.write(x509ks.getEncoded());
			fos.flush();
			fos.close();

			System.out.println("Public Key Saved");
		}catch(Exception e){
			e.printStackTrace();
		}
	}

	public  void writePrivateKey(String path, PrivateKey key){
		try {

			PKCS8EncodedKeySpec pkcsKeySpec = new PKCS8EncodedKeySpec(key.getEncoded());
			FileOutputStream fos = new FileOutputStream(path);
			fos.write(pkcsKeySpec.getEncoded());
			fos.flush();
			fos.close();

			System.out.println("Private Key Saved");
		}catch(Exception e){
			e.printStackTrace();
		}
	}

	public  PublicKey loadPublicKey(String path){

		try {
			byte[] encodedKey = IOUtils.toByteArray(new FileInputStream(path));
			KeyFactory keyFactory = KeyFactory.getInstance("RSA");
			//KeyFactory keyFactory = KeyFactory.getInstance("RSA", p);
			X509EncodedKeySpec pkSpec = new X509EncodedKeySpec(encodedKey);
			return keyFactory.generatePublic(pkSpec);

		} catch (Exception e) {
			debug("Public Key File Not Found, or error loading key");
			return null;
		}

	}


	public  PrivateKey loadPrivateKey(String path){

		try {
			byte[] encodedKey = IOUtils.toByteArray(new FileInputStream(path));
			KeyFactory keyFactory = KeyFactory.getInstance("RSA");
			//KeyFactory keyFactory = KeyFactory.getInstance("RSA", p);
			PKCS8EncodedKeySpec privKeySpec = new PKCS8EncodedKeySpec(encodedKey);
			return keyFactory.generatePrivate(privKeySpec);

		} catch (Exception e) {
			debug("Private Key File Not Found, or error loading key");
			return null;
		}

	}

	private  void debug(String log){
		if(DEBUG){
			System.out.println(log);
		}
	}


}
