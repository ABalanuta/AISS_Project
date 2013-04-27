package aiss.aes;

public class AESCall {

	private native void aesCall(String[] args);

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		new AESCall().aesCall(args);
	}
	static{
		System.loadLibrary("protocol");
	}

}
