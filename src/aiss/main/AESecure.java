package aiss.main;

import java.io.BufferedWriter;
import java.io.FileWriter;

public class AESecure {

	private static final boolean DEBUG = true;
	private static int mode = -1;
	private static Engine engine = null;

	public static void main(String args[]){
	
		if(args.length != 2){
			System.out.println("Invalid number of Arguments");
			return;
		}

		try{
			mode = Integer.parseInt(args[0]);
			
		}catch(Exception e){
			System.out.println("Invalid Argument 1: Must be an Integer");
			return;
		}

		try{
			if(Integer.parseInt(args[1]) == 0){
				EncryptionEngine.AES_BOX_PRESENT = false;
				DecryptionEngine.AES_BOX_PRESENT = false;
			}else{
				EncryptionEngine.AES_BOX_PRESENT = true;
				EncryptionEngine.AES_BOX_PRESENT = true;
			}
			
		}catch(Exception e){
			System.out.println("Invalid Argument 2: Must be an Integer");
			return;
		}
		

		if(mode == 0){
			debug("Decryption Mode:");
			//TODO
			engine = new DecryptionEngine();
		}
		else if (mode == 1){
			debug("Encryption Mode: Auth Only");
			engine = new EncryptionEngine(true, false, false);
		}
		else if (mode == 2){
			debug("Encryption Mode: Conf Only");
			engine = new EncryptionEngine(false, true, false);
		}
		else if (mode == 3){
			debug("Encryption Mode: Auth + Conf");
			engine = new EncryptionEngine(true, true, false);
		}
		else if (mode == 4){
			debug("Encryption Mode: TimeStamp");
			engine = new EncryptionEngine(false, false, true);
		}
		else if (mode == 5){
			debug("Encryption Mode: Auth  + TimeStamp");
			engine = new EncryptionEngine(true, false, true);
		}
		else if (mode == 6){
			debug("Encryption Mode: Conf  + TimeStamp");
			engine = new EncryptionEngine(false, true, true);
		}
		else if (mode == 7){
			debug("Encryption Mode: Auth + Conf  + TimeStamp");
			engine = new EncryptionEngine(true, true, true);
		}
		else{
			System.out.println("Invalid Mode");
			return;
		}

		
		// Starts the Engine
		engine.run();




		return;

		//		for(int i = 0; i<args.length; i++){
		//			System.out.println(i+" "+ args[i]);
		//		}
		//
		//
		//		System.out.println("##");
		//
		//		String text = args[0];
		//		//String auth = args[1];
		//		//String conf = args[2];
		//		String savePath = args[3];
		//
		//		//Authenticate text
		//
		//		System.out.println("##");
		//		
		//		if(savePath.contains("\"")){
		//			System.out.println("Sanitizing Path");
		//			savePath = savePath.split("\"")[1];
		//		}
		//		
		//		System.out.println("##");
		//		String strToReturn = "<text>"+text+"<text>";
		//		
		//		System.out.println("##");
		//		
		//		//Generate Signature
		//		strToReturn += "<sign>"+text.getBytes().hashCode()+"<sign>";
		//		
		//		
		//		System.out.println("##");
		//		
		//		//Save To File
		//		try{
		//			// Create file
		//			//savePath = savePath.substring(0, savePath.length()); // removes ""
		//			
		//			FileWriter fstream = new FileWriter(savePath + "toSend.txt");
		//			
		//			System.out.println("Path: " + savePath + "toSend.txt");
		//			
		//			BufferedWriter out = new BufferedWriter(fstream);
		//			out.write(strToReturn);
		//			//Close the output stream
		//			out.close();
		//		}catch (Exception e){//Catch exception if any
		//			System.err.println("Error: " + e.getMessage());
		//		}
	}

	private static void debug(String log){
		if(DEBUG){
			System.out.println(log);
		}
	}

}
