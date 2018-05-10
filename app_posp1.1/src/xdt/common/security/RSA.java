package xdt.common.security;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.GeneralSecurityException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Signature;
import java.security.SignatureException;
import java.security.interfaces.RSAKey;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

import javax.crypto.Cipher;

import org.apache.commons.codec.binary.Base64;

import xdt.util.Global;

public class RSA {
	
	
	
	public static final String RSA = "RSA/ECB/PKCS1Padding";
	
	 public static final String privatekeyFile = System.getProperty("user.dir")
			 +File.separator+"config"+File.separator+Global.getConfig("RASKeyFilePath")+File.separator+"prikey.pem";  
	    
	 public static final String publickeyFile =System.getProperty("user.dir")+
			 File.separator+"config"+File.separator+Global.getConfig("RASKeyFilePath")+File.separator+"pubkey.pem";
	 
	 static RSAPrivateKey privateKey;
	 static RSAPublicKey publicKey;
	 static {
		 try {
			privateKey = getPrivateKey(privatekeyFile);
			 publicKey = getPublicKey(publickeyFile);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (GeneralSecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	 }
	 
	 
	 
	 public static void main(String[] args) throws Exception {
//		 System.out.println(privatekeyFile);
		 String info ="<BODY><sys_order_id></sys_order_id><auth_code>281146214438429055</auth_code><ins_id_cd>63110000</ins_id_cd><mchnt_cd>631411645110000</mchnt_cd><ret_cd>A0</ret_cd><ret_msg>报文验签失败</ret_msg><buyer_user></buyer_user><pay_time></pay_time></BODY>";  
		    
//		 String info ="123";
		 //加密  
//		    byte[] bytes = encrypt(privateKey,info.getBytes("utf-8"));  
////		    //解密  
//		    bytes = decrypt(publicKey, bytes);  
//		    System.out.println(new String(bytes,"utf-8")); 
		    
		    String sign =signSHA1WithRSA(info, privateKey, "gbk");
		    System.out.println(sign);
		    String jj="";
//		    
		    boolean check = doCheckSHA1WithRSA(info, sign, publicKey, "gbk");
		    System.out.println(check);
//		    
	}
	 
	 public static String sign(String data){
		 String sign="";
			sign =signSHA1WithRSA(data, privateKey, "gbk");
		return sign;
	 }
	 
	 public static boolean doCheck(String data,String sign){
		 boolean check = doCheckSHA1WithRSA(data, sign, publicKey, "gbk");
		return check;
	 }
	 
	public static String signSHA1WithRSA(String content, RSAPrivateKey priKey, String encode) {
		String ret = "";
		java.security.Signature signature;
		try {
			signature = java.security.Signature.getInstance("SHA1WithRSA");
			 signature.initSign(priKey);  
		        signature.update( content.getBytes(encode));

		        byte[] signed = signature.sign();  
		        ret = Base64.encodeBase64String(signed);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}  
       
          
        return ret ;  
	} 
	
	 /** 
	    * RSA验签名检查 
	    * @param content 待签名数据 
	    * @param sign 签名值 
	    * @param publicKey 分配给开发商公钥 
	    * @param encode 字符集编码 
	    * @return 布尔值 
	    */  
	    public static boolean doCheckSHA1WithRSA(String content, String sign, PublicKey publicKey,String encode)  
	    {  
	        try   
	        {  
//	            KeyFactory keyFactory = KeyFactory.getInstance("RSA");  
//	            byte[] encodedKey = Base64.decodeBase64(publicKey);  
//	            PublicKey pubKey = keyFactory.generatePublic(new X509EncodedKeySpec(encodedKey));  
	  
	          
	            java.security.Signature signature = java.security.Signature.getInstance("SHA1WithRSA");  
	          
	            signature.initVerify(publicKey);  
	            signature.update( content.getBytes(encode) );  
	          
	            boolean bverify = signature.verify( Base64.decodeBase64(sign) );  
	            return bverify;  
	              
	        }   
	        catch (Exception e)   
	        {  
	            e.printStackTrace();  
	        }  
	          
	        return false;  
	    }  
	
	
	public static byte[] encrypt(RSAKey key, byte[] data)
		    throws Exception
		  {
		    try
		    {
		      Cipher cipher = Cipher.getInstance(RSA);
		      cipher.init(Cipher.ENCRYPT_MODE, (Key)key);
		      /*int step = key.getModulus().bitLength() / 8;
		      int n = data.length / step;
		      if (n > 0)
		      {
		        ByteArrayOutputStream baos = new ByteArrayOutputStream();
		        for (int i = 0; i < n; ++i) {
		          baos.write(cipher.doFinal(data, i * step, step));
		        }
		        if ((n = data.length % step) != 0) {
		          baos.write(cipher.doFinal(data, data.length - n, n));
		        }
		        return baos.toByteArray();
		      }*/
		      return cipher.doFinal(data);
		    }
		    catch (Exception e)
		    {
		    	e.printStackTrace();
		      throw new Exception("MPCM033");
		    }
		  }

		  public static byte[] decrypt(RSAKey key, byte[] raw)
		    throws Exception
		  {
		    try
		    {
		      Cipher cipher = Cipher.getInstance(RSA);
		      cipher.init(Cipher.DECRYPT_MODE, (Key)key);
		      /*int step = key.getModulus().bitLength() / 8;
		      int n = raw.length / step;
		      if (n > 0)
		      {
		        ByteArrayOutputStream baos = new ByteArrayOutputStream();
		        for (int i = 0; i < n; ++i) {
		          baos.write(cipher.doFinal(raw, i * step, step));
		        }
		        return baos.toByteArray();
		      }*/
		      return cipher.doFinal(raw);
		    }
		    catch (Exception e)
		    {
		      throw new Exception("MPCM033");
		    }
		  }

	public static String getKey(String filename) throws IOException {
		// Read key from file
		String strKeyPEM = "";
		BufferedReader br = new BufferedReader(new FileReader(filename));
		String line;
		while ((line = br.readLine()) != null) {
			strKeyPEM += line + "\n";
		}
		br.close();
		System.out.println("key####################");
		System.out.println(strKeyPEM);
		System.out.println("key####################");
		return strKeyPEM;
	}

	public static RSAPrivateKey getPrivateKey(String filename)
			throws IOException, GeneralSecurityException {
		System.out.println("文件路径"+filename);
		String privateKeyPEM = getKey(filename);
		return getPrivateKeyFromString(privateKeyPEM);
	}

	public static RSAPrivateKey getPrivateKeyFromString(String key)
			throws IOException, GeneralSecurityException {
		String privateKeyPEM = key;
//		privateKeyPEM = privateKeyPEM.replace("-----BEGIN PRIVATE KEY-----\n",
//				"");
//		privateKeyPEM = privateKeyPEM.replace("-----END PRIVATE KEY-----", "");
		byte[] encoded = Base64.decodeBase64(privateKeyPEM);
		KeyFactory kf = KeyFactory.getInstance("RSA");
		PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(encoded);
		RSAPrivateKey privKey = (RSAPrivateKey) kf.generatePrivate(keySpec);
		return privKey;
	}

	public static RSAPublicKey getPublicKey(String filename)
			throws IOException, GeneralSecurityException {
		String publicKeyPEM = getKey(filename);
		return getPublicKeyFromString(publicKeyPEM);
	}

	public static RSAPublicKey getPublicKeyFromString(String key)
			throws IOException, GeneralSecurityException {
		String publicKeyPEM = key;
		publicKeyPEM = publicKeyPEM.replace("-----BEGIN PUBLIC KEY-----\n", "");
		publicKeyPEM = publicKeyPEM.replace("-----END PUBLIC KEY-----", "");
		byte[] encoded = Base64.decodeBase64(publicKeyPEM);
		KeyFactory kf = KeyFactory.getInstance("RSA");
		RSAPublicKey pubKey = (RSAPublicKey) kf
				.generatePublic(new X509EncodedKeySpec (encoded));
		return pubKey;
	}

	public static String sign(PrivateKey privateKey, String message)
			throws NoSuchAlgorithmException, InvalidKeyException,
			SignatureException, UnsupportedEncodingException {
		Signature sign = Signature.getInstance("SHA1withRSA");
		sign.initSign(privateKey);
		sign.update(message.getBytes("UTF-8"));
		return new String(Base64.encodeBase64(sign.sign()), "UTF-8");
	}

	public static boolean verify(PublicKey publicKey, String message,
			String signature) throws SignatureException,
			NoSuchAlgorithmException, UnsupportedEncodingException,
			InvalidKeyException {
		Signature sign = Signature.getInstance("SHA1withRSA");
		sign.initVerify(publicKey);
		sign.update(message.getBytes("UTF-8"));
		return sign.verify(Base64.decodeBase64(signature.getBytes("UTF-8")));
	}

	public static String encrypt(String rawText, PublicKey publicKey)
			throws IOException, GeneralSecurityException {
		Cipher cipher = Cipher.getInstance("RSA");
		cipher.init(Cipher.ENCRYPT_MODE, publicKey);
		return Base64.encodeBase64String(cipher.doFinal(rawText
				.getBytes("UTF-8")));
	}

	public static String decrypt(String cipherText, PrivateKey privateKey)
			throws IOException, GeneralSecurityException {
		Cipher cipher = Cipher.getInstance("RSA");
		cipher.init(Cipher.DECRYPT_MODE, privateKey);
		return new String(cipher.doFinal(Base64.decodeBase64(cipherText)),
				"UTF-8");
	}
}
