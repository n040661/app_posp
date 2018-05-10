package xdt.common.security;
import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESedeKeySpec;
import javax.crypto.spec.IvParameterSpec;

import org.apache.log4j.Logger;

public class XDT3Des {
	private static Logger  logger = Logger.getLogger(XDT3Des.class);
	private static final String ALGORITHM = "DESede/ECB/PKCS5Padding";
	private static final String ALGORITHM_CBC = "DESede/CBC/PKCS5Padding";

	public static byte[] encrypt(byte src[], String key1, String key2,
			String key3)  throws Exception {
		if (src == null || src.length <= 0 || key1 == null || key1.length() < 1
				|| key2 == null || key2.length() < 1 || key3 == null
				|| key3.length() < 1)
		{
			 throw new Exception("MPSE003");
		}
			if (key1.length() < 8) {
				StringBuffer sb = new StringBuffer();
				for (int i = 0; i < 8 - key1.length(); i++)
					sb.append("0");

				key1 = (new StringBuilder(String.valueOf(key1))).append(
						sb.toString()).toString();
			}
		if (key2.length() < 8) {
			StringBuffer sb = new StringBuffer();
			for (int i = 0; i < 8 - key2.length(); i++)
				sb.append("0");

			key2 = (new StringBuilder(String.valueOf(key2))).append(
					sb.toString()).toString();
		}
		if (key3.length() < 8) {
			StringBuffer sb = new StringBuffer();
			for (int i = 0; i < 8 - key3.length(); i++)
				sb.append("0");

			key3 = (new StringBuilder(String.valueOf(key3))).append(
					sb.toString()).toString();
		}
		String key = key1 + key2 + key3;
		src = encrypt3DES(src, key);
		return src;
	}

	public static byte[] encrypt3DES(byte[] src, String key) {
		try {
			DESedeKeySpec dks = new DESedeKeySpec(key.getBytes("UTF-8"));
			SecretKeyFactory keyFactory = SecretKeyFactory
					.getInstance("DESede");
			SecretKey desKey = keyFactory.generateSecret(dks);

			Cipher cipher = Cipher.getInstance(ALGORITHM);
			cipher.init(Cipher.ENCRYPT_MODE, desKey);
			return cipher.doFinal(src);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return null;
	}

	public static byte[] decrypt(byte src[], String key1, String key2,
			String key3)
	 throws Exception
	{
		if (src == null || src.length < 1 || key1 == null || key1.length() < 1
				|| key2 == null || key2.length() < 1 || key3 == null
				|| key3.length() < 1)
		{
			 throw new Exception("MPSE004");
		}
			if (key1.length() < 8) {
				StringBuffer sb = new StringBuffer();
				for (int i = 0; i < 8 - key1.length(); i++)
					sb.append("0");

				key1 = (new StringBuilder(String.valueOf(key1))).append(
						sb.toString()).toString();
			}
		if (key2.length() < 8) {
			StringBuffer sb = new StringBuffer();
			for (int i = 0; i < 8 - key2.length(); i++)
				sb.append("0");

			key2 = (new StringBuilder(String.valueOf(key2))).append(
					sb.toString()).toString();
		}
		if (key3.length() < 8) {
			StringBuffer sb = new StringBuffer();
			for (int i = 0; i < 8 - key3.length(); i++)
				sb.append("0");

			key3 = (new StringBuilder(String.valueOf(key3))).append(
					sb.toString()).toString();
		}
		String key = key1 + key2 + key3;
		src = decrypt3DES(src, key);
		return src;
	}
	public static byte[] decrypt3DES(byte[] src, String key) {
		try {
			DESedeKeySpec dks = new DESedeKeySpec(key.getBytes("UTF-8"));
			SecretKeyFactory keyFactory = SecretKeyFactory
					.getInstance("DESede");
			SecretKey desKey = keyFactory.generateSecret(dks);

			Cipher cipher = Cipher.getInstance(ALGORITHM);
			cipher.init(Cipher.DECRYPT_MODE, desKey);
			return cipher.doFinal(src);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return null;
	}
	
	public static byte[] encrypt_cbc(byte[] src, byte[] key, byte[] iv) {
		try {
			DESedeKeySpec dks = new DESedeKeySpec(key);
			SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DESede");
			SecretKey desKey = keyFactory.generateSecret(dks);

			Cipher cipher = Cipher.getInstance(ALGORITHM_CBC);
			IvParameterSpec ivp = new IvParameterSpec(iv);
			cipher.init(Cipher.ENCRYPT_MODE, desKey, ivp);
			return cipher.doFinal(src);
		} catch (Exception e) {
			System.out.println(e);
		}

		return null;
	}
	
	/**
	 * 函数功能: 对数捄1�7ES规1�7
	 * @param src 待解密内宄1�7
	 * @param key 密钥
	 * @param iv 
	 * @return 明文
	 * @throws AppException 
	 */
	public static byte[] decrypt_cbc(byte[] src, byte[] key, byte[] iv){
		try {
			DESedeKeySpec dks = new DESedeKeySpec(key);
			SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DESede");
			SecretKey desKey = keyFactory.generateSecret(dks);

			Cipher cipher = Cipher.getInstance(ALGORITHM_CBC);
			IvParameterSpec ivp = new IvParameterSpec(iv);
			cipher.init(Cipher.DECRYPT_MODE, desKey,ivp);
			return cipher.doFinal(src);
		} catch (Exception e) {
			logger.error("CB3Des.java error:"+e.getMessage());
		}
		return null;
	}
	
	public static void main(String[] args) throws Exception{
		/*String key = "567890125678901223456789";
		String key1 = "56789012";
		String key2 = "56789012";
		String key3 = "23456789";
		//String key = "MIIEvwIBADANBgkqhkiG9w0BAQEFAASCBKkwggSlAgEAAoIBAQDGLrZ0lKmJWfnaByA8w04kFmw5rsRqaGGSFWHTyxM4sM4XASXUH0NUH/fXQIRRWkLAsKoiRQnSfB7BVpzXoZPAaHng9P0xPgx+ElKxKT5D0+Hanv3o8SYSRRImcjSBDIch788c/dvVH5264RBgi3qsDJe/lTiIBvTe3XeKrgcSVzueOOliTNHYaXlnfiXiDHs1k3EZ3f5ic1SsbT2NYmfdN++T0zI84CXwMLGIP15SQCkhWBD7jxcP3Bs3Lj+FHKrVjPk4ApdjfhY2KIz0TnFFESJ66xNwA7WfUDgvBff/f2qPjvs5zFLyYZT9l6oq246X8kzQ/fQT2eQM7xgl9LNZAgMBAAECggEAXzpk+8HPcStl7kkMSYZDTv3jRRhgUw0TDUHu/vkOpGf4r38isTnQfWTbD3h13C118+cv6UWpQZXYpWg79zUxrPvGzLyLyRSaph2h1HUP8UGnvgg106/EbnT4Erc/2V6utDTqHXEyJ8eIYBrzZqC+8lses7U9M8e79DcQ0PdseHfPTd0fRJBIdP+VONQ9CF9pHEUJIN+YfCJywSxXJwo0KQN5N3AxAz1bjXYugTVQ7UYCs3yNuQQaKPd/IoNRqF1zb2k4wqMApll0vi/vwpFpecxkvwASRqxI4dkfk4qSN5ibhpgC7uBLcuWFp7+9nGMIU/8x6daVb2qx9AI+j5GLMQKBgQDv4LtOeNipmOlBVf1hYNmMc+rngcSvWrGb7ra6/CqGmeJVQZtXSmBBzxg/rEzYRPJaRlIzBG3fvf+uE7jcElaFumg9LPmbvkBLmGOOM1afxfE8V/niL0WDgVx7cYBzpVNKXGur0U1EUkG2OgFRi17ErUwve/NfNrCv9phfaWCjhQKBgQDTgJUZBkBba3SoSC9i3/eA5DkU2ZMVM1rQS3RqhptMuw7DHV/W88fvStNMEivZtLQAcsuFdPsg/O3IFLuRQF0AiqGwUEx4XMi0TeKq40OhxIif2Oj4ruHjFTJg1UYxEO3Bujn0Q2iX3OZA14Z3exZaiWbgn0xrJPERTerO/wbGxQKBgQDN46uBSDJXJ1sV7DOmMFWpuxjlijmCdSq692K6DZSGoXBl0wD9JGaINbPPexkeC9ekanVFOsj/4PmitWTW55qB/6RW1dKMvfg7qlnRBPmV+3T6YVymqgJVRGRXw+0H1eAulDMKb4SNXrjuKs1U63X1A/W/T/ltQOjYr6TLSAxdbQKBgQDEuzX0sPvsDWfVoE9ziSCPo7n0yu0lKz+twIHpFIwgM17n3srnBptS2ibw+3YOayeDw0PMP6PYa2xCUAaodVYMyubbOdXnn2aRUGEm0351Wno7NWh+6Sz0LSNPLxK7USPWr4J40rrXh4IljlRljvib/nLTPVm30ATsM63WHSIQgQKBgQDO6gyDrce3WU7Mh4S8RNMZTGkA6Op0fsPX76szJDM5g8J0ZpXcn61Zd+55xI0h2tXc5tTlhxR26GW800J+vYecz1UqdoULWi4dk1iUNdVlldegIOWD1D2iLputdvVlUAE8oCeIBAfL1SVvd0nS2SUDrZR+8siyjmB0IThkBoFpZw==";
		String business = "liuliehui-刘列辉A1B2C3D4E5F6G3D4E5F6G3D4E5F6G3D4E5F6G";
		System.out.println("start business:" + business);
		byte[] encrypt = null;
		byte[] decrypt = null;
		try {
			encrypt = XDTConverter.bytesToBase64(encrypt(business.getBytes("utf-8"),key1,key2,key3)).getBytes("utf-8");
			//encrypt = encrypt3DES(business.getBytes("utf-8"),key);
			System.out.println("start encrypt business:" + new String(encrypt,"utf-8"));
			decrypt = decrypt(XDTConverter.base64ToBytes(new String(encrypt,"utf-8")),key1,key2,key3);
			//decrypt = decrypt3DES(encrypt,key);
			System.out.println("start decrypt business:" + new String(decrypt,"utf-8"));
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}*/
		
	}
	
	
}
