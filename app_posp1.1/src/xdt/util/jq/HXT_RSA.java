package xdt.util.jq;

import java.security.interfaces.RSAPublicKey;

public class HXT_RSA {
	
	//恒信通
	public  static String pubKey ="MIGdMA0GCSqGSIb3DQEBAQUAA4GLADCBhwKBgQCV3HdksdXnlyrP+2yxODB9T0TU+NJGxGJT/uu61gXHCotLJeNYgwwZwiypCprK8uNk2b8oUbd/CwVaSXqtV8R8Eu5pyad+qK+jQAPoMFhSNlemUJbFm+r6eIRwLQvX2L8GMXWisrN8U1cxVCQgNLFsQTMp8W6dehNBSQaS9Yj5mwIBAw==";   

	  
	 public  static RSAPublicKey getPubKey() throws Exception {
		 
		 System.err.println("pubKey-->"+pubKey);
		 RSAPublicKey publicKey= RSAEncrypt.loadPublicKeyByStr(pubKey);
		 
		return publicKey;
		 
		 
	 }
	 
	 
	 
	 public static byte[] encrypt0(String data) throws Exception{
		
		 byte[] plainTextData=data.getBytes();
		
		 byte[] _byte=RSAEncrypt.encrypt(getPubKey(), plainTextData);
		
		 return  _byte;
	 }
	 
	 public static String encrypt3(String data) throws Exception{
	 		
		 byte[] plainTextData=data.getBytes();
		
		 byte[] _byte=RSAEncrypt.encrypt(getPubKey(), plainTextData);
		
		 String resultStr=new String(_byte);
		
		 return resultStr;
	 }
	 
     public static String encrypt(String data) throws Exception{
		 
    	 byte[] plainTextData=data.getBytes();
		
    	 byte[] _byte=RSAEncrypt.encrypt(getPubKey(), plainTextData);
		
    	 String resultStr=Base64.encode(_byte);
		
    	 return resultStr ;
	 }
	
	 

     
    
     
     
     
     
     
     
}
