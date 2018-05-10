package xdt.quickpay.wzf;
import java.io.IOException;
import java.io.PrintStream;
import java.security.SecureRandom;
import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;
import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

public class DesUtil
{
  private static final String DES = "DES";

  public static void main(String[] args)
    throws Exception
  {
    String data = "123 456";
    String key = "wang!@#$%";
    System.err.println(encrypt(data, key, "UTF-8"));
  }

  public static String encrypt(String data, String key, String charset)
    throws Exception
  {
    if (("".equalsIgnoreCase(charset)) || (charset == null)) {
      charset = "UTF-8";
    }
    byte[] bt = encrypt(data.getBytes(charset), key.getBytes());
    String strs = new BASE64Encoder().encode(bt);
    return strs;
  }

  public static String decrypt(String data, String key, String charset) throws Exception
  {
    if (data == null)
      return null;
    if (("".equalsIgnoreCase(charset)) || (charset == null)) {
      charset = "UTF-8";
    }
    BASE64Decoder decoder = new BASE64Decoder();

      byte[] buf = decoder.decodeBuffer(data);
      byte[] bt = decrypt(buf, key.getBytes());
      return new String(bt, charset); 
    
  }

  private static byte[] encrypt(byte[] data, byte[] key) throws Exception
  {
    SecureRandom sr = new SecureRandom();
 
      DESKeySpec dks = new DESKeySpec(key);

      SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DES");
      SecretKey securekey = keyFactory.generateSecret(dks);

      Cipher cipher = Cipher.getInstance("DES");

      cipher.init(1, securekey, sr);
      return cipher.doFinal(data); 
    
  }

  private static byte[] decrypt(byte[] data, byte[] key)
    throws Exception
  {
    SecureRandom sr = new SecureRandom();

    DESKeySpec dks = new DESKeySpec(key);

    SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DES");
    SecretKey securekey = keyFactory.generateSecret(dks);

    Cipher cipher = Cipher.getInstance("DES");

    cipher.init(2, securekey, sr);

    return cipher.doFinal(data);
  }
}