package xdt.pufa;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.security.Key;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Signature;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

import javax.crypto.Cipher;

import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;
public class Sign {
	public static String ALGORITHM_DSA="DSA";
	public static String ALGORITHM_RSA="RSA";
	
	public static byte[] base64Decoder(String data) throws IOException{
		BASE64Decoder base64Decoder = new BASE64Decoder();
		return base64Decoder.decodeBuffer(data);
	}
	
	public static String base64Encoder(byte[] data){
		BASE64Encoder base64Encoder=new BASE64Encoder();
		return base64Encoder.encodeBuffer(data);
	}


	
	/** 
	* @名称: readKeyFile 
	* @描述: 读取Key文件 
	* @param in
	* @return
	* @throws Exception
	* 
	*/
	public static String readKeyFile(InputStream in) throws Exception {
		BufferedReader br = new BufferedReader(new InputStreamReader(in));
		String readLine = null;
		StringBuilder sb = new StringBuilder();
		while ((readLine = br.readLine()) != null) {
			if (readLine.charAt(0) == '-') {
				continue;
			} else {
				sb.append(readLine);
				sb.append('\r');
			}
		}
		System.out.println(sb);
		return sb.toString(); 
	}
	
	public static PrivateKey loadPrivateKey(String privateKey,String algorithm) throws Exception{
		// 解密由base64编码的私钥   
        byte[] keyBytes = base64Decoder(privateKey);   
        // 构造PKCS8EncodedKeySpec对象   
        PKCS8EncodedKeySpec pkcs8KeySpec = new PKCS8EncodedKeySpec(keyBytes);   
        // KEY_ALGORITHM 指定的加密算法   
        KeyFactory keyFactory = KeyFactory.getInstance(algorithm);   
        // 取私钥匙对象   
        PrivateKey priKey = keyFactory.generatePrivate(pkcs8KeySpec);   
        return priKey;
	}
	
	public static PublicKey loadPublicKey(String publicKey,String algorithm) throws Exception {
		byte[] buffer = base64Decoder(publicKey);
		KeyFactory keyFactory = KeyFactory.getInstance(algorithm);
		X509EncodedKeySpec keySpec = new X509EncodedKeySpec(buffer);
		PublicKey pubKey = keyFactory.generatePublic(keySpec);
		return pubKey;
	}

	/** 
	* @名称: rsaEncrypt 
	* @描述: RSA加密 
	* @param publicKey
	* @param plainTextData
	* @return
	* @throws Exception
	* 
	*/
	public static byte[] rsaEncrypt(RSAPublicKey publicKey, byte[] plainTextData)
			throws Exception {
		if (publicKey == null) {
			return null;
		}
		Cipher cipher = null;
		cipher = Cipher.getInstance("RSA");
		cipher.init(Cipher.ENCRYPT_MODE, publicKey);
		byte[] output = cipher.doFinal(plainTextData);
		return output;
	}

	/** 
	* @名称: rsaDecrypt 
	* @描述: RSA解密 
	* @param privateKey
	* @param cipherData
	* @return
	* @throws Exception
	* 
	*/
	public static byte[] rsaDecrypt(RSAPrivateKey privateKey, byte[] cipherData)
			throws Exception {
		if (privateKey == null) {
			return null;
		}
		Cipher cipher = null;
		cipher = Cipher.getInstance("RSA");
		cipher.init(Cipher.DECRYPT_MODE, privateKey);
		byte[] output = cipher.doFinal(cipherData);
		return output;
	}

	/**
	 * @名称: genKeyPair
	 * @描述: 生成随机公钥私钥
	 * @return
	 * @throws Exception
	 * 
	 */
	public static Key[] genKeyPair() throws Exception {
		KeyPairGenerator keyPairGen = null;
		keyPairGen = KeyPairGenerator.getInstance("RSA");
		keyPairGen.initialize(1024);
		KeyPair keyPair = keyPairGen.generateKeyPair();
		RSAPrivateKey privateKey = (RSAPrivateKey) keyPair.getPrivate();
		RSAPublicKey publicKey = (RSAPublicKey) keyPair.getPublic();
		Key[] key = new Key[2];
		key[0] = privateKey;
		key[1] = publicKey;
		return key;
	}
	
	
	
	public static String sign(byte[] data,String privateKey) throws Exception{
		// 解密由base64编码的私钥   
        byte[] keyBytes = base64Decoder(privateKey);   
        // 构造PKCS8EncodedKeySpec对象   
        PKCS8EncodedKeySpec pkcs8KeySpec = new PKCS8EncodedKeySpec(keyBytes);   
        // KEY_ALGORITHM 指定的加密算法   
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");   
        // 取私钥匙对象   
        PrivateKey priKey = keyFactory.generatePrivate(pkcs8KeySpec);   
        // 用私钥对信息生成数字签名   
        Signature signature = Signature.getInstance("SHA1withRSA");   
        signature.initSign(priKey);   
        signature.update(data);   
        return base64Encoder(signature.sign()); 
	}
	
	public static boolean verify(byte[] data, String publicKey, String sign) throws Exception{
		// 解密由base64编码的公钥   
        byte[] keyBytes = base64Decoder(publicKey);   
        // 构造X509EncodedKeySpec对象   
        X509EncodedKeySpec keySpec = new X509EncodedKeySpec(keyBytes);   
        // KEY_ALGORITHM 指定的加密算法   
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");   
        // 取公钥匙对象   
        PublicKey pubKey = keyFactory.generatePublic(keySpec);   
        Signature signature = Signature.getInstance("SHA1withRSA");   
        signature.initVerify(pubKey);   
        signature.update(data);   
        // 验证签名是否正常   
        return signature.verify(base64Decoder(sign));  
	}

	public static void main(String[] args) throws Exception{
		//pkcs8 -topk8 -inform PEM -in pra.key -outform PEM -nocrypt -out pracs8.key
		String str="这是一个测试字符串asdfdsafsaf123123测试";
		InputStream is=new FileInputStream(Sign.class.getResource("pracs8.key").getPath());
    	String sign=sign(str.getBytes(),readKeyFile(is));
    	System.out.println(sign);
    	is=new FileInputStream(Sign.class.getResource("pub.key").getPath());
    	boolean result=verify(str.getBytes(), readKeyFile(is), sign);
    	System.out.println(result);
//    	is=new FileInputStream(Sign.class.getResource("").getPath());
//    	System.out.println(verify(str.getBytes(),readKeyFile(is),sign));
    }

}
