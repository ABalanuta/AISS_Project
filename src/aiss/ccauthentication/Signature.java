package aiss.ccauthentication;

import java.io.IOException;
import java.lang.reflect.Method;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;

import javax.security.cert.CertificateEncodingException;
import javax.security.cert.CertificateException;
import javax.security.cert.X509Certificate;

import pteidlib.PTEID_Certif;
import pteidlib.PteidException;
import pteidlib.pteid;

import sun.security.pkcs11.wrapper.CK_ATTRIBUTE;
import sun.security.pkcs11.wrapper.CK_C_INITIALIZE_ARGS;
import sun.security.pkcs11.wrapper.CK_MECHANISM;
import sun.security.pkcs11.wrapper.CK_SESSION_INFO;
import sun.security.pkcs11.wrapper.PKCS11;
import sun.security.pkcs11.wrapper.PKCS11Constants;
import sun.security.pkcs11.wrapper.PKCS11Exception;

public class Signature {
	
	static
	{
		String osName = System.getProperty("os.name");
		if(osName.equals("Linux")){
			System.out.println("its Linux");
			System.setProperty("java.library.path","/usr/local/lib/pteid_jni/");
		}
		
		System.out.println(System.getProperty("java.library.path"));
		
		try{
			System.loadLibrary("pteidlibj");
		}catch (UnsatisfiedLinkError e){
			System.err.println("Native code library failed to load.\n" + e);
			System.exit(1);
		}
	}


	public static byte[][] createSignature(byte[] nounce) throws PKCS11Exception, IOException{

		PKCS11 pkcs11;
		String osName = System.getProperty("os.name");
		String javaVersion = System.getProperty("java.version");
		long p11_session = 0;
		byte[][] signature = new byte[2][];

		try{
			String libName = "libpteidpkcs11.so";
			if (-1 != osName.indexOf("Windows"))
				libName = "pteidpkcs11.dll";
			else if (-1 != osName.indexOf("Mac"))
				libName = "pteidpkcs11.dylib";
			Class pkcs11Class = Class.forName("sun.security.pkcs11.wrapper.PKCS11");

			if (javaVersion.startsWith("1.5.")){
				Method getInstanceMethode = pkcs11Class.getDeclaredMethod("getInstance", new Class[] { String.class, CK_C_INITIALIZE_ARGS.class, boolean.class });
				pkcs11 = (PKCS11)getInstanceMethode.invoke(null, new Object[] { libName, null, false });
			} else {
				Method getInstanceMethode = pkcs11Class.getDeclaredMethod("getInstance", new Class[] { String.class, String.class, CK_C_INITIALIZE_ARGS.class, boolean.class });
				pkcs11 = (PKCS11)getInstanceMethode.invoke(null, new Object[] { libName, "C_GetFunctionList", null, false });
			}

			//Open the PKCS11 session

			p11_session = pkcs11.C_OpenSession(0, PKCS11Constants.CKF_SERIAL_SESSION, null, null);

			// Token login 
			pkcs11.C_Login(p11_session, 1, null);
			CK_SESSION_INFO info = pkcs11.C_GetSessionInfo(p11_session);

			// Get available keys 
			CK_ATTRIBUTE[] attributes = new CK_ATTRIBUTE[1];
			attributes[0] = new CK_ATTRIBUTE();
			attributes[0].type = PKCS11Constants.CKA_CLASS;
			attributes[0].pValue = new Long(PKCS11Constants.CKO_PRIVATE_KEY);

			pkcs11.C_FindObjectsInit(p11_session, attributes);
			long[] keyHandles = pkcs11.C_FindObjects(p11_session, 5);

			// points to auth_key 
			long signatureKey = keyHandles[0];		//test with other keys to see what you get

			pkcs11.C_FindObjectsFinal(p11_session);

			//byte[] data = nounce.getBytes();

			// initialize the signature method 
			CK_MECHANISM mechanism = new CK_MECHANISM();

			//first signature
			mechanism.mechanism = PKCS11Constants.CKM_SHA256_RSA_PKCS;
			mechanism.pParameter = null;
			pkcs11.C_SignInit(p11_session, mechanism, signatureKey);
			signature[0] = pkcs11.C_Sign(p11_session, nounce);

			//second signature
			mechanism.mechanism = PKCS11Constants.CKM_RIPEMD160_RSA_PKCS;
			mechanism.pParameter = null;
			pkcs11.C_SignInit(p11_session, mechanism, signatureKey);
			signature[1] = pkcs11.C_Sign(p11_session, nounce);

		}  catch (Exception e){
			e.printStackTrace();
		}
		return signature;
	}

	public static byte[] obtainCert() throws IOException, InvalidKeySpecException{

		byte[] certAuth = null;
		X509Certificate cert = null;
		byte[] result = null;

		try {
			pteid.Init("");
		} catch (PteidException e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
		}

		PTEID_Certif[] certs;
		try {
			certs = pteid.GetCertificates();
			for (int i = 0; i < certs.length; i++) {
				if(certs[i].certifLabel.equals("CITIZEN AUTHENTICATION CERTIFICATE")){
					certAuth = certs[i].certif;
					cert = X509Certificate.getInstance(certAuth);
				}
			}
		} catch (PteidException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (CertificateException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try {
			result = cert.getEncoded();
		} catch (CertificateEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return result;
	}

}
