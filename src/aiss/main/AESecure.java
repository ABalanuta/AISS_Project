package aiss.main;

import java.io.BufferedWriter;
import java.io.FileWriter;

public class AESecure {

	public static void main(String args[]){

		if(args.length < 4){
			System.out.println("Not Enougth Arguments");
			return;
		}

		for(int i = 0; i<args.length; i++){
			System.out.println(i+" "+ args[i]);
		}


		System.out.println("##");

		String text = args[0];
		//String auth = args[1];
		//String conf = args[2];
		String savePath = args[3];

		//Authenticate text

		System.out.println("##");
		
		if(savePath.contains("\"")){
			System.out.println("Sanitizing Path");
			savePath = savePath.split("\"")[1];
		}
		
		System.out.println("##");
		String strToReturn = "<text>"+text+"<text>";
		
		System.out.println("##");
		
		//Generate Signature
		strToReturn += "<sign>"+text.getBytes().hashCode()+"<sign>";
		
		
		System.out.println("##");
		
		//Save To File
		try{
			// Create file
			//savePath = savePath.substring(0, savePath.length()); // removes ""
			
			FileWriter fstream = new FileWriter(savePath + "toSend.txt");
			
			System.out.println("Path: " + savePath + "toSend.txt");
			
			BufferedWriter out = new BufferedWriter(fstream);
			out.write(strToReturn);
			//Close the output stream
			out.close();
		}catch (Exception e){//Catch exception if any
			System.err.println("Error: " + e.getMessage());
		}
	}
}
