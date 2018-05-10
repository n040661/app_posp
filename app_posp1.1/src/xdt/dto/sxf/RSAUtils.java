package xdt.dto.sxf;

import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

import javax.crypto.Cipher;

public class RSAUtils {

	public static String ALGORITHM = "RSA";

	public static String SIGN_ALGORITHMS = "SHA1WithRSA";// 摘要加密算饭

	public static String CHAR_SET = "UTF-8";
	

	/**
	 * 数据签名
	 * 
	 * @param content
	 *            签名内容
	 * @param privateKey
	 *            私钥
	 * @return 返回签名数据
	 * @throws Exception
	 */
	public static String sign(String content, String privateKey)
			throws Exception {
		byte[] str=Base64Utils.decode(privateKey);
		PKCS8EncodedKeySpec priPKCS8 = new PKCS8EncodedKeySpec(str);
		KeyFactory keyf = KeyFactory.getInstance("RSA");
		PrivateKey priKey = keyf.generatePrivate(priPKCS8);

		java.security.Signature signature = java.security.Signature
				.getInstance(SIGN_ALGORITHMS);

		signature.initSign(priKey);
		signature.update(content.getBytes(CHAR_SET));

		byte[] signed = signature.sign();

		return Base64Utils.encode(signed);
	}

	/**
	 * 签名验证
	 * 
	 * @param content
	 * @param sign
	 * @param public_key
	 * @return
	 * @throws Exception
	 */
	public static boolean verify(String content, String sign, String public_key)
			throws Exception {
		KeyFactory keyFactory = KeyFactory.getInstance("RSA");
		byte[] encodedKey = Base64Utils.decode(public_key);
		PublicKey pubKey = keyFactory.generatePublic(new X509EncodedKeySpec(
				encodedKey));

		java.security.Signature signature = java.security.Signature
				.getInstance(SIGN_ALGORITHMS);

		signature.initVerify(pubKey);
		signature.update(content.getBytes(CHAR_SET));

		boolean bverify = signature.verify(Base64Utils.decode(sign));
		return bverify;

	}
	
	 /** 
     * 得到公钥 
     *  
     * @param key 
     *            密钥字符串（经过base64编码） 
     * @throws Exception 
     */  
    public static PublicKey getPublicKey(String key) throws Exception {  
        byte[] keyBytes;  
        keyBytes = Base64Utils.decode(key);  
        X509EncodedKeySpec keySpec = new X509EncodedKeySpec(keyBytes);  
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");  
        PublicKey publicKey = keyFactory.generatePublic(keySpec);  
        return publicKey;  
    }  
      
    /** 
     * 得到私钥 
     *  
     * @param key 
     *            密钥字符串（经过base64编码） 
     * @throws Exception 
     */  
    public static PrivateKey getPrivateKey(String key) throws Exception {  
        byte[] keyBytes;  
        keyBytes = Base64Utils.decode(key);
        PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(keyBytes);  
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");  
        PrivateKey privateKey = keyFactory.generatePrivate(keySpec);  
        return privateKey;  
    }  
  
    /** 
     * 使用公钥对明文进行加密 
     * @param plainText 明文 
     * @param publicKey 公钥 
     * @return 
     */  
    public static byte[] encryptByPublicKey(byte[] plainText,String publicKey) throws Exception{  
		Cipher cipher = Cipher.getInstance("RSA");
		cipher.init(Cipher.ENCRYPT_MODE, getPublicKey(publicKey));
		byte[] enBytes = cipher.doFinal(plainText);
		return enBytes;
    }     
      
      
    /** 
     * 使用keystore对密文进行解密 
     * @param enStr       密文 
     * @param privateKey  私钥
     * @return 
     */  
    public static byte[] decryptByPrivateKey(byte[] enStrbs,String privateKey)throws Exception{  
		Cipher cipher = Cipher.getInstance("RSA");
		cipher.init(Cipher.DECRYPT_MODE, getPrivateKey(privateKey));
		byte[] deBytes = cipher.doFinal(enStrbs);
		return deBytes;
    } 
    
    /** 
     * 使用公钥对明文进行加密 
     * @param plainText 明文 
     * @param publicKey 公钥 
     * @return 
     */  
    public static byte[] encryptByPrivateKey(byte[] plainText,String privateKey) throws Exception{  
		Cipher cipher = Cipher.getInstance("RSA");
		cipher.init(Cipher.ENCRYPT_MODE, getPrivateKey(privateKey));
		byte[] enBytes = cipher.doFinal(plainText);
		return enBytes;
    }     
      
      
    /** 
     * 使用keystore对密文进行解密 
     * @param enStr       密文 
     * @param privateKey  私钥
     * @return 
     */  
    public static byte[] decryptByPublicKey(byte[] enStrbs,String publicKey)throws Exception{  
		Cipher cipher = Cipher.getInstance("RSA");
		cipher.init(Cipher.DECRYPT_MODE, getPublicKey(publicKey));
		byte[] deBytes = cipher.doFinal(enStrbs);
		return deBytes;
    } 
}
