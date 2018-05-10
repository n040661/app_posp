package xdt.common.security;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.InputStream;
import java.security.KeyFactory;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import xdt.preutil.FileUtil;



public class KeyStore {

	
	public static final String PUFARASPRIVATEKey = "pfRasPrivateKey";
	public static final String PUFARASPUBLICKey = "pfRasPublicKey";
	private static Map<String,String> encryptKeyMap=new HashMap<String, String>();
	private static Map<String,RSAPublicKey> publicKeyMap=new HashMap<String, RSAPublicKey>();
	private static Map<String,RSAPrivateKey> privateKeyMap=new HashMap<String, RSAPrivateKey>();
	
//    private static final String privatekeyFile = "pri_pkcs8.pem";
	 private static final String privatekeyFile = System.getProperty("user.dir")+File.separator+"config"+File.separator+"key"+File.separator+"pri_pkcs8.pem";  
    
    private static final String publickeyFile =System.getProperty("user.dir")+File.separator+"config"+File.separator+"key"+File.separator+"pubkey.pem";
	
	static{
		try {
			InputStream is=null;
			try {
				File file = new File(privatekeyFile);
				String key = FileUtil.readTextFile(file);
				
				File file2 = new File(publickeyFile);
				String key2 = FileUtil.readTextFile(file2);
				privateKeyMap.put(PUFARASPRIVATEKey, productPrivateKey(key));
				publicKeyMap.put(PUFARASPUBLICKey, productPublicKey(key2));
			} catch (Exception e) {
				e.printStackTrace();
			}finally{
				XDTStreamOperator.close(is);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public  static RSAPublicKey productPublicKey(String publicKey) throws Exception{  
	    byte[] keyBytes = XDTConverter.base64ToBytes(publicKey);  
	    X509EncodedKeySpec spec = new X509EncodedKeySpec(keyBytes);  
	    KeyFactory keyFactory = KeyFactory.getInstance("RSA");  
	    return (RSAPublicKey) keyFactory.generatePublic(spec);  
	}  
	  
	public  static RSAPrivateKey productPrivateKey(String privateKey) throws Exception{  
	    byte[] keyBytes = XDTConverter.base64ToBytes(privateKey);  
	    PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(keyBytes);  
	    KeyFactory keyFactory = KeyFactory.getInstance("RSA");  
	    return (RSAPrivateKey) keyFactory.generatePrivate(spec);  
	} 
	
	public static RSAPublicKey getPublicKey(String key){
		return publicKeyMap.get(key);
	}

	public static RSAPrivateKey getPrivateKey(String key){
		RSAPrivateKey RSAPrivateKey= privateKeyMap.get(key);
		if(RSAPrivateKey == null) {
			//当取的O单私钥为空时，取默认私钥
			RSAPrivateKey = privateKeyMap.get(PUFARASPRIVATEKey);
		}
		return RSAPrivateKey;
	}

	public static String getEncryptKey(String key){
		return encryptKeyMap.get(key);
	}
	
	public static void main(String[] args){
		String str = "\"business\":\"{\"cr\":\"49BA6CF385BD4AA3952BDD3BFA35D7F9\"}";
		//String str = "X1IdkZKV6352QssPcdQFYhF0lBXOSml3fXdP4GotSjhIukTl60RydSqdofdWr8EAZvQfznApj/3s8OLeWjo3HneDCeloP1Fq4n/6vrTCb9NzBUsVGA5lEBEbcUvy84HBAjKLd4F1ueFiGKLyHCy6axwM1apSZRfD9YDGMtC1qL8=";
		System.out.println("--------1------------str:"+str);
	    byte[] strByte;
	    String businessStr="";
		try {
			//XDTKeyStore.productPrivateKey(priKey);
			//XDTKeyStore.productPublicKey(pubKey1);
			/*strByte = XDTRSA.encrypt(privateKeyMap.get(XDTRASPRIVATEKey), str.trim().getBytes("utf-8"));
			strByte = XDTRSA.decrypt(publicKeyMap.get(XDTRASPUBLICKey), strByte);*/
			//String businessStr=XDTConverter.bytesToBase64(XDTRSA.encrypt(privateKeyMap.get(XDTRASPRIVATEKey),str.getBytes("utf-8")));
			//String businessStr=XDTConverter.bytesToBase64(str.getBytes("utf-8"));
			//businessStr = new String(XDTConverter.base64ToBytes("5L2g5aW977yM5pyL5Y+L"));
			strByte = XDTRSA.encrypt(privateKeyMap.get(PUFARASPRIVATEKey),"你好".getBytes("utf-8"));
			businessStr = new String(strByte);
			strByte = XDTRSA.decrypt(publicKeyMap.get(PUFARASPUBLICKey), strByte);
			//strByte=XDTRSA.decrypt(publicKeyMap.get(XDTRASPUBLICKey),XDTConverter.base64ToBytes(str));
			//strByte=XDTRSA.decrypt(privateKeyMap.get(XDTRASPRIVATEKey),XDTConverter.base64ToBytes(str));
			//strByte=XDTRSA.decrypt(privateKeyMap.get(XDTRASPRIVATEKey),str.getBytes());
			//strByte=XDT3Des.decrypt3DES(publicKeyMap.get(XDTRASPUBLICKey),XDTConverter.base64ToBytes(businessStr));
			
			//strByte = XDTRSA.encrypt(privateKeyMap.get(XDTRASPRIVATEKey), str.trim().getBytes("utf-8"));
			//strByte = XDTRSA.decrypt(publicKeyMap.get(XDTRASPUBLICKey), strByte);
			System.out.println("-------2------------str:"+businessStr.trim());
		} catch (Exception e) {
			e.printStackTrace();
		}  
	    
	} 
}
