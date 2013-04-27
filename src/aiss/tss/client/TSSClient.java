package aiss.tss.client;

import java.awt.print.Printable;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.util.TimeZone;

import javax.crypto.Cipher;
import javax.crypto.NoSuchPaddingException;

import aiss.tss.aux.KeyManager;

import sun.awt.windows.ThemeReader;
import sun.misc.BASE64Encoder;

public class TSSClient {

	private static final String PUBLIC_KEY_NAME = "pub.key";
	
	public static boolean validateTimeStamp(byte[] hash, byte[] timeBlob){

		KeyManager km = new KeyManager();
		PublicKey pubKey = km.loadPublicKey(PUBLIC_KEY_NAME);
		
		byte[] decTimeAndHash = null;
		Cipher cipher;
		try {
			
			cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
			cipher.init(Cipher.DECRYPT_MODE, pubKey);
			decTimeAndHash = cipher.doFinal(timeBlob);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		String hashAndTime = new String(decTimeAndHash);
		String time = hashAndTime.substring(hash.length, hashAndTime.length());
		System.out.println(hashAndTime);
		System.out.println(time);
		
		return true;
	}
	
	public static String getTimeStamp(byte[] timeBlob){
		
		
		// TODO
		return null;
	}
	
	
	// Metodo que permite obter um hash e data de criação, assinado
	// pelo servidor de TimeStamp 
	public static byte[] generateTimeStamp(byte[] messageHash){
		
		// Cria Canal de comunicação
		TSSClientConnectionHandeler ch = new TSSClientConnectionHandeler();
		ch.start();
		
		KeyManager km = new KeyManager();
		PublicKey pubKey = km.loadPublicKey(PUBLIC_KEY_NAME);
		byte[] encHmac = null;
		
		// Cifra o hash com a chave publica do SErvidor
		try {
			
			Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
			cipher.init(Cipher.ENCRYPT_MODE, pubKey);
			encHmac = cipher.doFinal(messageHash);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		// Espera ate a conecção esabelecer
		while(!ch.isConnected()){
			
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		
		// Envia Hash to be time stamped
		ch.send(encHmac);

		
		// Espera pela resposta
		while(!ch.recevedObjects()){
			
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			
		}
		
		// Devolve a resposta
		byte[] encTime = (byte[]) ch.receve().get(0);
		return encTime;
	}
	
}


