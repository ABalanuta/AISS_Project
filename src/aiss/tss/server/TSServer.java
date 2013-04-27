package aiss.tss.server;

import java.io.File;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.security.PrivateKey;

import aiss.tss.aux.KeyManager;


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
