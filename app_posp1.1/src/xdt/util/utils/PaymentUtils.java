package xdt.util.utils;

import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

import javax.crypto.Cipher;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang.ArrayUtils;

/**
 * Description: 商户加密 解密 签名 验签 工具类
 */
public class PaymentUtils {
	private static final String KEY_ALGORITHM = "RSA";
	private static final int KEY_SIZE = 2048;
	private static final int ENCRYPT_BlOCK_SIZE = KEY_SIZE / 8 - 11;
	private static final int DECRYPT_BLOCK_SIZE = KEY_SIZE / 8;
	public static final String SIGN_ALGORITHMS = "SHA1WithRSA";

	/**
	 * 内容加密
	 * 
	 * @param encryptStr
	 * @param publicKey
	 * @return
	 * @throws Exception
	 */
	public static String encrypt(String content, String publicKey) throws Exception {
		byte[] publicKeyBytes = Base64.decodeBase64(publicKey);
		byte[] encryptBytes = content.getBytes("UTF-8");

		if (encryptBytes.length <= ENCRYPT_BlOCK_SIZE) {
			return Base64.encodeBase64String(encrypt(encryptBytes, publicKeyBytes));
		} else {
			byte[] buffer = null;
			byte[] blockBytes = new byte[ENCRYPT_BlOCK_SIZE];

			int index = ((encryptBytes.length - 1) / ENCRYPT_BlOCK_SIZE) + 1;

			for (int i = 0; i < index; i++) {
				if (i == (index - 1)) {
					blockBytes = new byte[ENCRYPT_BlOCK_SIZE];
				}
				int startIndex = i * ENCRYPT_BlOCK_SIZE;
				int endIndex = startIndex + ENCRYPT_BlOCK_SIZE;
				blockBytes = ArrayUtils.subarray(encryptBytes, startIndex, endIndex);
				if (buffer == null) {
					buffer = encrypt(blockBytes, publicKeyBytes);
				} else {
					buffer = ArrayUtils.addAll(buffer, encrypt(blockBytes, publicKeyBytes));
				}

			}
			return Base64.encodeBase64String(buffer);
		}
	}

	/**
	 * 内容解密
	 * 
	 * @param decryptStr
	 * @param privateKey
	 * @return
	 * @throws Exception
	 */
	public static String decrypt(String content, String privateKey) throws Exception {
		byte[] privateKeyBytes = Base64.decodeBase64(privateKey);

		byte[] decryptBytes = Base64.decodeBase64(content);

		if (decryptBytes.length <= DECRYPT_BLOCK_SIZE) {
			return new String(decrypt(decryptBytes, privateKeyBytes), "UTF-8");
		} else {
			byte[] buffer = null;

			int index = ((decryptBytes.length - 1) / DECRYPT_BLOCK_SIZE) + 1;
			byte[] blockBytes = new byte[DECRYPT_BLOCK_SIZE];
			for (int i = 0; i < index; i++) {
				if (i == index - 1) {
					blockBytes = new byte[DECRYPT_BLOCK_SIZE];
				}
				int startIndex = i * DECRYPT_BLOCK_SIZE;
				int endIndex = startIndex + DECRYPT_BLOCK_SIZE;
				blockBytes = ArrayUtils.subarray(decryptBytes, startIndex,
						endIndex > decryptBytes.length ? decryptBytes.length : endIndex);
				if (buffer == null) {
					buffer = decrypt(blockBytes, privateKeyBytes);
				} else {
					buffer = ArrayUtils.addAll(buffer, decrypt(blockBytes, privateKeyBytes));
				}
			}
			return new String(buffer, "UTF-8");
		}
	}

	/**
	 * 
	 * Description: 签名生成
	 * 
	 * @author hao_jp@suixingpay.com
	 * @param @param content
	 * @param @param privateKey
	 * @param @return
	 * @return String
	 * @throws
	 */
	public static String sign(String content, String privateKey) {
		try {
			PKCS8EncodedKeySpec priPKCS8 = new PKCS8EncodedKeySpec(Base64.decodeBase64(privateKey));
			KeyFactory keyf = KeyFactory.getInstance("RSA");
			PrivateKey priKey = keyf.generatePrivate(priPKCS8);
			java.security.Signature signature = java.security.Signature
					.getInstance(SIGN_ALGORITHMS);
			signature.initSign(priKey);
			signature.update(content.getBytes());
			byte[] signed = signature.sign();
			return Base64.encodeBase64String(signed);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	/**
	 * 
	 * Description: 签名验签
	 * @author hao_jp@suixingpay.com
	 * @param  @param content
	 * @param  @param sign
	 * @param  @param publicKey
	 * @param  @return
	 * @return boolean
	 * @throws
	 */
	public static boolean doCheck(String content, String sign, String publicKey)
	{
		try 
		{
			KeyFactory keyFactory = KeyFactory.getInstance("RSA");
	        byte[] encodedKey = Base64.decodeBase64(publicKey);
	        PublicKey pubKey = keyFactory.generatePublic(new X509EncodedKeySpec(encodedKey));

		
			java.security.Signature signature = java.security.Signature
			.getInstance(SIGN_ALGORITHMS);
		
			signature.initVerify(pubKey);
			signature.update( content.getBytes() );
		
			boolean bverify = signature.verify( Base64.decodeBase64(sign) );
			return bverify;
			
		} 
		catch (Exception e) 
		{
			e.printStackTrace();
		}
		
		return false;
	}
	/**
	 * 
	 * Description: 获取公钥私钥(公钥,私钥)
	 * @author hao_jp@suixingpay.com
	 * @param  @return
	 * @param  @throws Exception
	 * @return 公钥,私钥
	 * @throws
	 */
	public static String getPublicAndPrivateKey() throws Exception {
		KeyPairGenerator kpGenerator = KeyPairGenerator.getInstance(KEY_ALGORITHM);
		kpGenerator.initialize(KEY_SIZE, new SecureRandom());
		KeyPair keyPair = kpGenerator.generateKeyPair();
		String publicKey=Base64.encodeBase64String(keyPair.getPublic().getEncoded());
		String privateKey=Base64.encodeBase64String(keyPair.getPrivate().getEncoded());
		return publicKey+","+privateKey;
	}
	
	public static byte[] encrypt(byte[] encryptBytes, byte[] publicKeyBytes) throws Exception {
		PublicKey publicKey = PaymentUtils.codeToPublicKey(publicKeyBytes);

		Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
		cipher.init(Cipher.ENCRYPT_MODE, publicKey);
		byte[] enBytes = cipher.doFinal(encryptBytes);
		return enBytes;
	}

	public static PublicKey codeToPublicKey(byte[] publicKey) throws Exception {
		X509EncodedKeySpec keySpec = new X509EncodedKeySpec(publicKey);
		KeyFactory keyFactory = KeyFactory.getInstance(KEY_ALGORITHM);
		return keyFactory.generatePublic(keySpec);
	}

	public static byte[] decrypt(byte[] decrypt, byte[] privateKeyBytes) throws Exception {
		PrivateKey privateKey = PaymentUtils.codeToPrivateKey(privateKeyBytes);

		Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
		cipher.init(Cipher.DECRYPT_MODE, privateKey);
		byte[] resultBytes = cipher.doFinal(decrypt);
		return resultBytes;
	}

	public static PrivateKey codeToPrivateKey(byte[] privateKey) throws Exception {
		// PKCS#8：描述私有密钥信息格式，该信息包括公开密钥算法的私有密钥以及可选的属性集等。
		PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(privateKey);
		KeyFactory keyFactory = KeyFactory.getInstance(KEY_ALGORITHM);
		PrivateKey keyPrivate = keyFactory.generatePrivate(keySpec);
		return keyPrivate;
	}
}
