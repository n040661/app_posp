package xdt.quickpay.hf.util;

import java.io.ByteArrayOutputStream;
import java.security.Key;
import java.security.KeyFactory;
import java.security.PublicKey;
import java.security.Signature;
import java.security.spec.X509EncodedKeySpec;
import java.util.Map;

import javax.crypto.Cipher;

import xdt.quickpay.hf.comm.SampleConstant;
import xdt.util.RSAUtil;

/**
 * @author:Ivan
 * @version Revision 1.0.0
 * @see:
 * @创建日期：2016-12-11
 * @功能说明：密钥工具类
 * @begin
 * @修改记录:
 * @修改后版本          修改人      	修改内容
 * @2016-12-11  	         Ivan        	创建
 * @end
 */
public class PlatKeyGenerator {

	 /** *//** 
     * 加密算法RSA 
     */  
    public static final String KEY_ALGORITHM = "RSA";  
      
    /** *//** 
     * 签名算法 
     */  
    public static final String SIGNATURE_ALGORITHM = "MD5withRSA";  
  
    /** *//** 
     * 获取公钥的key 
     */  
    private static final String PUBLIC_KEY = "RSAPublicKey";  
      
    /** *//** 
     * RSA最大加密明文大小 
     */  
    private static final int MAX_ENCRYPT_BLOCK = 117;  
      
    /** *//** 
     * RSA最大解密密文大小 
     */  
    private static final int MAX_DECRYPT_BLOCK = 128;  
  
  
    /** *//** 
     * <p> 
     * 校验数字签名 
     * </p> 
     * @param data 已加密数据 
     * @param publicKey 公钥(BASE64编码) 
     * @param sign 数字签名 
     *  
     * @return 
     * @throws Exception 
     *  
     */  
    public static boolean verify(byte[] data, String publicKey, String sign)  
            throws Exception {  
        byte[] keyBytes = PlatBase64Utils.decode(publicKey);  
        X509EncodedKeySpec keySpec = new X509EncodedKeySpec(keyBytes);  
        KeyFactory keyFactory = KeyFactory.getInstance(KEY_ALGORITHM);  
        PublicKey publicK = keyFactory.generatePublic(keySpec);  
        Signature signature = Signature.getInstance(SIGNATURE_ALGORITHM);  
        signature.initVerify(publicK);  
        signature.update(data);  
        return signature.verify(PlatBase64Utils.decode(sign));  
    }  
  
    /** *//** 
     * <p> 
     * 公钥解密 
     * </p> 
     *  
     * @param encryptedData 已加密数据 
     * @param publicKey 公钥(BASE64编码) 
     * @return 
     * @throws Exception 
     */  
    public static byte[] decryptByPublicKey(byte[] encryptedData, String publicKey)  
            throws Exception {  
        byte[] keyBytes = PlatBase64Utils.decode(publicKey);  
        X509EncodedKeySpec x509KeySpec = new X509EncodedKeySpec(keyBytes);  
        KeyFactory keyFactory = KeyFactory.getInstance(KEY_ALGORITHM);  
        Key publicK = keyFactory.generatePublic(x509KeySpec); 
        System.out.println(keyFactory.getAlgorithm());
        Cipher cipher = Cipher.getInstance(keyFactory.getAlgorithm());  
        cipher.init(Cipher.DECRYPT_MODE, publicK);  
        int inputLen = encryptedData.length;  
        ByteArrayOutputStream out = new ByteArrayOutputStream();  
        int offSet = 0;  
        byte[] cache;  
        int i = 0;  
        // 对数据分段解密  
        while (inputLen - offSet > 0) {  
            if (inputLen - offSet > MAX_DECRYPT_BLOCK) {  
                cache = cipher.doFinal(encryptedData, offSet, MAX_DECRYPT_BLOCK);  
            } else {  
                cache = cipher.doFinal(encryptedData, offSet, inputLen - offSet);  
            }  
            out.write(cache, 0, cache.length);  
            i++;  
            offSet = i * MAX_DECRYPT_BLOCK;  
        }  
        byte[] decryptedData = out.toByteArray();  
        out.close();  
        return decryptedData;  
    }  
  
    /** *//** 
     * <p> 
     * 公钥加密 
     * </p> 
     *  
     * @param data 源数据 
     * @param publicKey 公钥(BASE64编码) 
     * @return 
     * @throws Exception 
     */  
    public static byte[] encryptByPublicKey(byte[] data, String publicKey)  
            throws Exception {  
        byte[] keyBytes = PlatBase64Utils.decode(publicKey);  
        X509EncodedKeySpec x509KeySpec = new X509EncodedKeySpec(keyBytes);  
        KeyFactory keyFactory = KeyFactory.getInstance(KEY_ALGORITHM);  
        Key publicK = keyFactory.generatePublic(x509KeySpec);  
        // 对数据加密  
        Cipher cipher = Cipher.getInstance(keyFactory.getAlgorithm());  
        cipher.init(Cipher.ENCRYPT_MODE, publicK);  
        int inputLen = data.length;  
        ByteArrayOutputStream out = new ByteArrayOutputStream();  
        int offSet = 0;  
        byte[] cache;  
        int i = 0;  
        // 对数据分段加密  
        while (inputLen - offSet > 0) {  
            if (inputLen - offSet > MAX_ENCRYPT_BLOCK) {  
                cache = cipher.doFinal(data, offSet, MAX_ENCRYPT_BLOCK);  
            } else {  
                cache = cipher.doFinal(data, offSet, inputLen - offSet);  
            }  
            out.write(cache, 0, cache.length);  
            i++;  
            offSet = i * MAX_ENCRYPT_BLOCK;  
        }  
        byte[] encryptedData = out.toByteArray();  
        out.close();  
        return encryptedData;  
    } 
    public static String bytesToString(byte[] encrytpByte) {
    	String result = "";
    	for (Byte bytes : encrytpByte) {
    	result += bytes.toString() + " ";
    	}
    	return result;
    	}
  
    /** *//** 
     * <p> 
     * 获取公钥 
     * </p> 
     *  
     * @param keyMap 密钥对 
     * @return 
     * @throws Exception 
     */  
    public static String getPublicKey(Map<String, Object> keyMap)  
            throws Exception {  
        Key key = (Key) keyMap.get(PUBLIC_KEY);  
        return PlatBase64Utils.encode(key.getEncoded());  
    } 
    public static void main(String[] args) throws Exception {
		
    	String key="MIICdwIBADANBgkqhkiG9w0BAQEFAASCAmEwggJdAgEAAoGBAMwjKPBL16tAwWNhiNsBtg2x704ZNY36KcQ1JEHSHlPC1MAByJLYwQNp3BYbgg54YO/Zn+pFbmT+uS2khmgga9lnSQSN1JLnQAQYj1m/nuo4HZqZ3lTfWagvLf6ksyfzKW8NdwIrrnY2jredz/ioZv5L/6vFr4g51tohhpT4m6hJAgMBAAECgYBG4Z6BcZQ9XorH20IN6ZYxQyt4zsANwGNoQZuJT1BAfVIqKXRfqOu8J7HH611JcPsXzVT9n4Ypg1kBRDN3TGqlv7e7pvewBF72Jz19hzbA6wcdh5rhVXlP6fPR+cWbwtU3wz30i7oPbp9HezfjSfcBra6KaGrOfhzDf8XogzUr0QJBAOcSd9bJ5vbojFpWJOpz+PnT0iH6fkWhGz9pjJgWu+BUrUwukQD6kqlGeSzr8sz0tneYJtR7a/JVYa1nPe6iGj8CQQDiKNT/iu44zT8+kKGK6oCLpC4XOi5YQy1d4hnJvzv2661dgCdCY04On8ndew6FbWEzkzZirXxAigwW9VpEL0t3AkBl5mIMyWreNb03VUfgUJZabcv8CgBHan2eoEhNBbGCgzUNJHfzq4yjs51abw7azYvt/54YE/mNm5OAqcfJBZl3AkEAnUtC4TyydOUjgJM9F44Du5uDxvnbM939Fpfj0bJktVazLB2usHi62GcAEj+9GMW7XhCcOcNpcMXSpMEed9/g/wJBALoLF7UfCNTLE+EmlpIcUR6eG8II3olUCmRV5j4eMt1cicKbJf6kmwPGVbluqdU0lBLdgIzRkJqhATGczmD/g+g=";
    	
    	String content="nb9ZuAnapd3CnoHEP6qJKRSHHAG0EfkLKjC4UjYPpGKNUwnDfMGmo7NpiFzDHxggOZEGQClvx9fKD2nzmjoJdDtvsEisVTFb/BEopqdJ7tR7HByPGz6FyHEKQuMelmazNRWLgfGknBUPXoxyHaUYbWo44VXReuVTZ1T1Wf3c5CA=";
    	
    	String[] strArr = content.split(" ");
    	int len = strArr.length;
    	byte[] clone = new byte[len];
    	for (int i = 0; i < len; i++) {
    	clone[i] = Byte.parseByte(strArr[i]);
    	System.out.println(clone[i]);
    	}
    	System.out.println("convert to String, then back to bytes again: " +clone);
    	byte[] aa=decryptByPublicKey(content.getBytes("UTF-8"),key);
//    	
    	String data = PlatBase64Utils.encode(aa);
//    	
//    	System.out.println(data);
    	//byte[] a = decryptByPublicKey(content.getBytes("utf-8"),key);
    	
//    	byte[] a=RSAUtil.decrypt(key, content);

		String Str = new String(data);
		System.out.println(PlatBase64Utils.decode(content));
    	
    	
	}
}
