package xdt.quickpay.yy.util;


import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

@SuppressWarnings("restriction")
public class AesEncryptUtil {

	 public static String aesEncrypt(String str, String key) throws Exception {
	        if (str == null || key == null) return null;
	        Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
	        cipher.init(Cipher.ENCRYPT_MODE, new SecretKeySpec(key.getBytes("utf-8"), "AES"));
	        byte[] bytes = cipher.doFinal(str.getBytes("utf-8"));
	        return new BASE64Encoder().encode(bytes);
    }

    public static String aesDecrypt(String str, String key) throws Exception {
        if (str == null || key == null) return null;
        Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
        cipher.init(Cipher.DECRYPT_MODE, new SecretKeySpec(key.getBytes("utf-8"), "AES"));
        byte[] bytes = new BASE64Decoder().decodeBuffer(str);
        bytes = cipher.doFinal(bytes);
        return new String(bytes, "utf-8");
    }

    public static void main(String[] args) throws Exception{
        String signKey = EmaxPlusUtil.getEmaxPlusPrivateKey("6252802887672730625", "9001000023");
        String cardNum = "411325198701136515";

        System.out.println(signKey);
        System.out.println(signKey.substring(0, 16));
        String s = AesEncryptUtil.aesEncrypt(cardNum, signKey.substring(0, 16));
        System.out.println(s);
    }
}
