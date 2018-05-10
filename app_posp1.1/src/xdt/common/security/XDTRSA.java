package xdt.common.security;

import java.io.InputStream;
import java.math.BigInteger;
import java.security.Key;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.Signature;
import java.security.interfaces.RSAKey;
import java.security.interfaces.RSAPrivateCrtKey;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.RSAPrivateKeySpec;
import java.security.spec.RSAPublicKeySpec;
import java.security.spec.X509EncodedKeySpec;

import javax.crypto.Cipher;

public class XDTRSA {
	// public static final String RSA = "RSA/ECB/NoPadding";
	private static final String RSA = "RSA/ECB/PKCS1Padding";
	public static final String MD5withRSA = "MD5withRSA";
	public static final String SHA1withRSA = "SHA1withRSA";
	private static final String PROVIDER_NAME = "BC";

	public static KeyPair generateKeyPair(int keySize) throws Exception {
		try {
			KeyPairGenerator keyPairGen = KeyPairGenerator.getInstance(RSA);
			keyPairGen.initialize(keySize, new SecureRandom());
			KeyPair keyPair = keyPairGen.generateKeyPair();
			return keyPair;
		} catch (Exception e) {
			throw new Exception("MPCM033");
		}
	}

	public static RSAPublicKey generateRSAPublicKey(byte[] modulus,
			byte[] publicExponent) throws Exception {
		try {
			KeyFactory keyFac = KeyFactory.getInstance(RSA);
			RSAPublicKeySpec pubKeySpec = new RSAPublicKeySpec(new BigInteger(
					modulus), new BigInteger(publicExponent));

			return (RSAPublicKey) keyFac.generatePublic(pubKeySpec);
		} catch (Exception e) {
			throw new Exception("RSAPublicKey error");
		}
	}

	// xdtKeyStore
	public static RSAPublicKey getRSAPublicKey(String urlB64Key)
			throws Exception {
		try {
			KeyFactory keyFac = KeyFactory.getInstance("RSA");
			X509EncodedKeySpec pubKeySpec = new X509EncodedKeySpec(
					XDTBase64.decode(urlB64Key.getBytes()));
			return (RSAPublicKey) keyFac.generatePublic(pubKeySpec);
		} catch (Exception e) {
			throw new Exception("RSAPublicKey error");
		}
	}

	public static RSAPublicKey getRSAPublicKey(RSAPrivateKey prk)
			throws Exception {
		return generateRSAPublicKey(prk.getModulus().toByteArray(),
				((RSAPrivateCrtKey) prk).getPublicExponent().toByteArray());
	}

	public static RSAPrivateKey generateRSAPrivateKey(byte[] modulus,
			byte[] privateExponent) throws Exception {
		try {
			KeyFactory keyFac = KeyFactory.getInstance(RSA);
			RSAPrivateKeySpec priKeySpec = new RSAPrivateKeySpec(
					new BigInteger(modulus), new BigInteger(privateExponent));
			return (RSAPrivateKey) keyFac.generatePrivate(priKeySpec);
		} catch (Exception e) {
			throw new Exception("MPCM033");
		}
	}

	// xdtKeyStore
	public static RSAPrivateKey getRSAPrivateKey(String strB64Key)
			throws Exception {
		try {
			KeyFactory keyFac = KeyFactory.getInstance("RSA");

			PKCS8EncodedKeySpec priKeySpec = new PKCS8EncodedKeySpec(
					XDTBase64.decode(strB64Key.getBytes()));

			return (RSAPrivateKey) keyFac.generatePrivate(priKeySpec);
		} catch (Exception e) {
			throw new Exception("MPCM033");
		}
	}

	public static byte[] encrypt(RSAKey key, byte[] data) throws Exception {
		try {
			Cipher cipher = Cipher.getInstance(RSA);
			cipher.init(Cipher.ENCRYPT_MODE, (Key) key);
			/*
			 * int step = key.getModulus().bitLength() / 8; int n = data.length
			 * / step; if (n > 0) { ByteArrayOutputStream baos = new
			 * ByteArrayOutputStream(); for (int i = 0; i < n; ++i) {
			 * baos.write(cipher.doFinal(data, i * step, step)); } if ((n =
			 * data.length % step) != 0) { baos.write(cipher.doFinal(data,
			 * data.length - n, n)); } return baos.toByteArray(); }
			 */
			return cipher.doFinal(data);
		} catch (Exception e) {
			throw new Exception("MPCM033");
		}
	}

	public static byte[] decrypt(RSAKey key, byte[] raw) throws Exception {
		try {
			Cipher cipher = Cipher.getInstance(RSA);
			cipher.init(Cipher.DECRYPT_MODE, (Key) key);
			/*
			 * int step = key.getModulus().bitLength() / 8; int n = raw.length /
			 * step; if (n > 0) { ByteArrayOutputStream baos = new
			 * ByteArrayOutputStream(); for (int i = 0; i < n; ++i) {
			 * baos.write(cipher.doFinal(raw, i * step, step)); } return
			 * baos.toByteArray(); }
			 */
			return cipher.doFinal(raw);
		} catch (Exception e) {
			throw new Exception("MPCM033");
		}
	}

	public static byte[] signature(String signtype, byte[] data, PrivateKey sk)
			throws Exception {
		try {
			Signature s = Signature.getInstance(signtype);
			s.initSign(sk);
			s.update(data);
			return s.sign();
		} catch (Exception e) {
			throw new Exception("MPCM033");
		}
	}

	public static byte[] signatureIs(String signtype, InputStream is,
			PrivateKey sk) throws Exception {
		try {
			Signature s = Signature.getInstance(signtype);
			s.initSign(sk);
			byte[] buf = new byte[1024];
			int size;
			while ((size = is.read(buf)) != -1) {
				s.update(buf, 0, size);
			}
			return s.sign();
		} catch (Exception e) {
			throw new Exception("MPCM033");
		}
	}

	public static boolean verify(String signtype, byte[] data, byte[] sign,
			PublicKey pk) {
		try {
			Signature v = Signature.getInstance(signtype);
			v.initVerify(pk);
			v.update(data);
			return v.verify(sign);
		} catch (Exception e) {
		}
		return false;
	}

	public static boolean verifyIs(String signtype, InputStream is,
			byte[] sign, PublicKey pk) {
		try {
			Signature v = Signature.getInstance(signtype);
			v.initVerify(pk);

			byte[] buf = new byte[1024];
			int size;
			while ((size = is.read(buf)) != -1) {
				v.update(buf, 0, size);
			}

			return v.verify(sign);
		} catch (Exception e) {
		}
		return false;
	}
}