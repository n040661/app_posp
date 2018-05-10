package xdt.util.utils;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

public class AESUtil {


	/*<!-- JAVA AES加密 -->*/
	public static String encrypt(String data, String key, String iv) throws Exception
	{
	    try 
	    {
	        Cipher cipher = Cipher.getInstance("AES/CBC/NoPadding");
	        int blockSize = cipher.getBlockSize();
	        byte[] dataBytes = data.getBytes("UTF-8");
	        int plaintextLength = dataBytes.length;
	        if (plaintextLength % blockSize != 0) {
	            plaintextLength = plaintextLength + (blockSize - (plaintextLength % blockSize));
	        }
	        byte[] plaintext = new byte[plaintextLength];
	        System.arraycopy(dataBytes, 0, plaintext, 0, dataBytes.length);
	        SecretKeySpec keyspec = new SecretKeySpec(key.getBytes(), "AES");
	        IvParameterSpec ivspec = new IvParameterSpec(iv.getBytes());
	        cipher.init(Cipher.ENCRYPT_MODE, keyspec, ivspec);
	        byte[] encrypted = cipher.doFinal(plaintext);
	        return Base64.encode(encrypted);
	    }
	    catch (Exception e) {
	        e.printStackTrace();
	        return null;
	    }
	}


	/*<!-- JAVA AES解密 -->*/
	public static String desEncrypt(String data, String key, String iv) throws Exception
	{
	    try 
	    {
	        byte[] encrypted1 = Base64.decode(data);
	        Cipher cipher = Cipher.getInstance("AES/CBC/NoPadding");
	        SecretKeySpec keyspec = new SecretKeySpec(key.getBytes(), "AES");
	        IvParameterSpec ivspec = new IvParameterSpec(iv.getBytes());
	        cipher.init(Cipher.DECRYPT_MODE, keyspec, ivspec);
	        byte[] original = cipher.doFinal(encrypted1);
	        String originalString = new String(original, "UTF-8");
	        return originalString;
	    }
	    catch (Exception e) {
	        e.printStackTrace();
	        return null;
	    }
	}
}
