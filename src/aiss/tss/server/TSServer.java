package aiss.tss.server;


import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.security.Key;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.sql.Timestamp;
import java.util.ArrayList;

import org.apache.commons.io.IOUtils;


public class TSServer {

	private static final boolean DEBUG = true;
	private static final String PRIVATE_KEY_NAME = "priv.key";
	private static final String PUBLIC_KEY_NAME = "pub.key";	


	public static void main(String[] args){

		System.out.println("Hello Humans I am A TSS Server");
		
		PrivateKey privKey = GenerateOrLoadKeys();

		ConnectionInicializer conHandler = new ConnectionInicializer(privKey);
		conHandler.start();
	}



	private static PrivateKey GenerateOrLoadKeys(){

		KeyManager km = new KeyManager();
		File privFile = new File(PRIVATE_KEY_NAME);

		if(privFile.exists()){
			debug("Loading Existing Private Key");
			return km.loadPrivateKey(PRIVATE_KEY_NAME);

		}else{
			km.GenerateNewKeys(PRIVATE_KEY_NAME, PUBLIC_KEY_NAME);
			return km.loadPrivateKey(PRIVATE_KEY_NAME);
		}

	}

	private static void debug(String log){
		if(DEBUG){
			System.out.println(log);
		}
	}
}


class ConnectionInicializer extends Thread{

	public static final int SERVERPORT = 4444;
	private boolean running = false;
	private PrivateKey privateKey = null;
	private ServerSocket serverSocket = null;

	public ConnectionInicializer(PrivateKey privKey) {
		this.privateKey = privKey;
	}

	@Override
	public void run() {

		super.run();
		this.running = true;

		try {
			serverSocket = new ServerSocket(SERVERPORT);
			System.out.println("Server: Listening...");

			while(running){

				// Cria um socket novo por cada pedido
				Socket sock = serverSocket.accept();
				System.out.println("New Client Request");

				ServerConnectionHandler ch = new ServerConnectionHandler(sock, privateKey);
				ch.start();
				System.out.println("Listening Again...");
			}
		} catch (Exception e) {
			System.out.println("S: Error");
			e.printStackTrace();
		} finally{ try { serverSocket.close(); } catch (IOException e) { e.printStackTrace(); } }

	}

}
