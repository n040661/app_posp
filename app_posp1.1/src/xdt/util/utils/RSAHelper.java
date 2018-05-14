package xdt.util.utils;

import java.security.MessageDigest;
import javax.crypto.KeyGenerator;
import javax.crypto.Mac;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

public class RSAHelper
{
  private static final String KEY_SHA = "SHA";
  private static final String KEY_MD5 = "MD5";
  private static String hexString = "0123456789ABCDEF";
  public static final String KEY_MAC = "HmacMD5";

  public static byte[] decryptBASE64(String key)
    throws Exception
  {
    return new BASE64Decoder().decodeBuffer(key);
  }

  public static String encryptBASE64(byte[] key)
  {
    return new BASE64Encoder().encodeBuffer(key);
  }

  public static byte[] encryptMD5(byte[] data)
    throws Exception
  {
    MessageDigest md5 = MessageDigest.getInstance("MD5");
    md5.update(data);
    return md5.digest();
  }

  public static byte[] encryptSHA(byte[] data)
    throws Exception
  {
    MessageDigest sha = MessageDigest.getInstance("SHA");
    sha.update(data);
    return sha.digest();
  }

  public static String initMacKey()
    throws Exception
  {
    KeyGenerator keyGenerator = KeyGenerator.getInstance("HmacMD5");
    SecretKey secretKey = keyGenerator.generateKey();
    return encryptBASE64(secretKey.getEncoded());
  }

  public static byte[] encryptHMAC(byte[] data, String key)
    throws Exception
  {
    SecretKey secretKey = new SecretKeySpec(decryptBASE64(key), "HmacMD5");
    Mac mac = Mac.getInstance(secretKey.getAlgorithm());
    mac.init(secretKey);

    return mac.doFinal(data);
  }
}