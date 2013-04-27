package aiss.aes;

public class AESCall {

	native void aes(String args[]);
	
	static{
		System.loadLibrary("aiss_aes_AESCall");
	}

	/**
	 * @param args
	 */
	public static void main(String argv[]) {
		// TODO Auto-generated method stub
		new AESCall().aes(argv);
	}


}
