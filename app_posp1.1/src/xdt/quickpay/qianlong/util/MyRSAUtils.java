package xdt.quickpay.qianlong.util;

import java.io.ByteArrayOutputStream;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Signature;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

import javax.crypto.Cipher;

import org.springframework.util.Base64Utils;



/**
 * 
 * @author zj
 * 
 * @date:2016年4月8日上午11:51:36
 */
public final class MyRSAUtils {
	
	private static String RSA = "RSA";
	
	public static final String  MD5_SIGN_ALGORITHM = "MD5withRSA";
	
	public static final String  SHA1_SIGN_ALGORITHM = "SHA1withRSA";

	/**
	 * RSA签名
	 * @param privateKey:私钥
	 * @param plainText:待签名明文串
	 * @param algorithm:签名算法,默认MD5withRSA
	 * @return
	 */
	public static String sign(String  privateKey, String plainText, String algorithm) {
		try {
			byte[] keyBytes = Base64Utils.decodeFromString(privateKey);
			PKCS8EncodedKeySpec priPKCS8 = new PKCS8EncodedKeySpec(keyBytes);
			KeyFactory keyf = KeyFactory.getInstance(RSA);
			PrivateKey prikey = keyf.generatePrivate(priPKCS8);
			Signature signet = java.security.Signature.getInstance(algorithm);
			signet.initSign(prikey);
			signet.update(plainText.getBytes("utf-8"));
			return Base64Utils.encodeToString(signet.sign());
		} catch (java.lang.Exception e) {
			System.out.println("签名失败");
			e.printStackTrace();
		}
		return null;
	}
	
	/**
	 * RSA公钥验证
	 * @param publickey:公钥
	 * @param hexSigned：签名信息
	 * @param plainText：待签名明文
	 * @param algorithm:签名算法,默认MD5withRSA
	 * @return
	 */
	public static boolean verifySignature(String publickey, String hexSigned, String plainText,String algorithm) {
		try {
			PublicKey publicKey = loadPublicKey(publickey);
			Signature signetCheck = Signature.getInstance(algorithm);
			signetCheck.initVerify(publicKey);
			signetCheck.update(plainText.getBytes("utf-8"));
			if (signetCheck.verify(Base64Utils.decodeFromString(hexSigned))) {
				return true;
			}
			return false;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}
	

	public static PublicKey loadPublicKey(String publicKeyStr) throws Exception {
		try {
			byte[] buffer = Base64Utils.decodeFromString(publicKeyStr);
			KeyFactory keyFactory = KeyFactory.getInstance(RSA);
			X509EncodedKeySpec keySpec = new X509EncodedKeySpec(buffer);
			return (RSAPublicKey) keyFactory.generatePublic(keySpec);
		} catch (NoSuchAlgorithmException e) {
			throw new Exception("无此算法");
		} catch (InvalidKeySpecException e) {
			throw new Exception("公钥非法");
		} catch (NullPointerException e) {
			throw new Exception("公钥数据为空");
		}
	}

	public static PrivateKey loadPrivateKey(String privateKeyStr)
			throws Exception {
		try {
			byte[] buffer = Base64Utils.decodeFromString(privateKeyStr);
			// X509EncodedKeySpec keySpec = new X509EncodedKeySpec(buffer);
			PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(buffer);
			KeyFactory keyFactory = KeyFactory.getInstance(RSA);
			return (RSAPrivateKey) keyFactory.generatePrivate(keySpec);
		} catch (NoSuchAlgorithmException e) {
			throw new Exception("无此算法");
		} catch (InvalidKeySpecException e) {
			throw new Exception("私钥非法");
		} catch (NullPointerException e) {
			throw new Exception("私钥数据为空");
		}
	}
	
	
	public static void main(String[] args) {
		String testPrivateKey = "MIICdQIBADANBgkqhkiG9w0BAQEFAASCAl8wggJbAgEAAoGBAJepaH5kmNQYiF44cGj2bIwnSPuCgVmbLfHQwJq1BQhmlBRPDJZKpVap974E7VrcvQweGiBlHKiWHpPlWUBuSvP6RDiSe1X6LBezWsa9ht0y3HXTh2QY/z1ql79uSmpCcnSoMwjjCDQ0UojexGQotZ58t1SViDp3PQHTMq+p7WV/AgMBAAECgYBmkKHjQGL+pOysU5zpzyVPj03TEcoqcECdFaD7v0n9CWpBZchMZ4TzXBT4Q9rFJR0QcdcXa2n7NTLOsD8wzKYQ02cnJhE3ag7m2X+YoxNHgCX9s0/Z12vEkpXMl3r6p4wRe5o3x4vZ/Ql/NPp68d3nxzbVhpODKvAMeLlJgek3eQJBAPHDI6jh3mXwYrDpz1nTVTAuUfUblLOrEk/ZlPfiWi4JV1/zkd4dayWbIPuI+TtB2b3SjFwcj1tTMRXMmtK4NlsCQQCgl+TU2Y4n77ZS/FGCWXZN7vcsDEd+2X8vLSPUe78tbCcESelc9SxjhHoerlIp5ML0VID8ae5orPrQWHdH1h6tAkAoTx/v9ZDuMDFUS/eNg4XtnZt+oZWei0RnWeDTn8LncBlIX0UP4pnN0FqkdccQUT9rveI3LW/dizZg6xdZUOuHAkB5rj2TzXf56Ja0UDyOUKzkJhGgjXlKsw+8Ni603EKh7xWtGTe52IcWrJU/VAQrfAr/yFunZbhgFdlBLhbLFoFNAkArJYl8O9sBxQiYuSwpeLgsHBpYuWiKBcuDAvTs59mfIOuKJ1i5ekvCD7kOCZHIpe6ozfZnG2NUhIrSBp6qNY5V";
		String testPublicKey = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCXqWh+ZJjUGIheOHBo9myMJ0j7goFZmy3x0MCatQUIZpQUTwyWSqVWqfe+BO1a3L0MHhogZRyolh6T5VlAbkrz+kQ4kntV+iwXs1rGvYbdMtx104dkGP89ape/bkpqQnJ0qDMI4wg0NFKI3sRkKLWefLdUlYg6dz0B0zKvqe1lfwIDAQAB";
		String sign = sign(testPrivateKey, "orgId=000024&orgOrderNo=ql20161213154724742", "MD5withRSA");
		System.out.println(sign);
		System.out.println(verifySignature(testPublicKey, sign, "orgId=000024&orgOrderNo=ql20161213154724742",MyRSAUtils.MD5_SIGN_ALGORITHM));
	}
}
