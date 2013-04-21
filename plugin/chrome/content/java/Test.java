import java.io.*;

public class Test{
	
  public static void main(String[] args) throws IOException{
	  File f;
	  f=new File("/tmp/myfile.txt");
	  if(!f.exists()){
		  f.createNewFile();
		  System.out.println("New file \"myfile.txt\" has been created to the current directory");
	  }
  }
}