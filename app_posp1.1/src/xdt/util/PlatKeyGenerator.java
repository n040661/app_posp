package xdt.util;

import java.io.ByteArrayOutputStream;
import java.security.Key;
import java.security.KeyFactory;
import java.security.PublicKey;
import java.security.Signature;
import java.security.spec.X509EncodedKeySpec;
import java.util.Map;
import javax.crypto.Cipher;

public class PlatKeyGenerator
{
  public static final String KEY_ALGORITHM = "RSA";
  public static final String SIGNATURE_ALGORITHM = "MD5withRSA";
  private static final String PUBLIC_KEY = "RSAPublicKey";
  private static final int MAX_ENCRYPT_BLOCK = 117;
  private static final int MAX_DECRYPT_BLOCK = 128;

  public static boolean verify(byte[] data, String publicKey, String sign)
    throws Exception
  {
    byte[] keyBytes = PlatBase64Utils.decode(publicKey);
    X509EncodedKeySpec keySpec = new X509EncodedKeySpec(keyBytes);
    KeyFactory keyFactory = KeyFactory.getInstance("RSA");
    PublicKey publicK = keyFactory.generatePublic(keySpec);
    Signature signature = Signature.getInstance("MD5withRSA");
    signature.initVerify(publicK);
    signature.update(data);
    return signature.verify(PlatBase64Utils.decode(sign));
  }

  public static byte[] decryptByPublicKey(byte[] encryptedData, String publicKey)
    throws Exception
  {
    byte[] keyBytes = PlatBase64Utils.decode(publicKey);
    X509EncodedKeySpec x509KeySpec = new X509EncodedKeySpec(keyBytes);
    KeyFactory keyFactory = KeyFactory.getInstance("RSA");
    Key publicK = keyFactory.generatePublic(x509KeySpec);
    Cipher cipher = Cipher.getInstance(keyFactory.getAlgorithm());
    cipher.init(2, publicK);
    int inputLen = encryptedData.length;
    ByteArrayOutputStream out = new ByteArrayOutputStream();
    int offSet = 0;

    int i = 0;

    while (inputLen - offSet > 0)
    {
      byte[] cache;
      if (inputLen - offSet > 128)
        cache = cipher.doFinal(encryptedData, offSet, 128);
      else {
        cache = cipher.doFinal(encryptedData, offSet, inputLen - offSet);
      }
      out.write(cache, 0, cache.length);
      i++;
      offSet = i * 128;
    }
    byte[] decryptedData = out.toByteArray();
    out.close();
    return decryptedData;
  }

  public static byte[] encryptByPublicKey(byte[] data, String publicKey)
    throws Exception
  {
    byte[] keyBytes = PlatBase64Utils.decode(publicKey);
    X509EncodedKeySpec x509KeySpec = new X509EncodedKeySpec(keyBytes);
    KeyFactory keyFactory = KeyFactory.getInstance("RSA");
    Key publicK = keyFactory.generatePublic(x509KeySpec);

    Cipher cipher = Cipher.getInstance(keyFactory.getAlgorithm());
    cipher.init(1, publicK);
    int inputLen = data.length;
    ByteArrayOutputStream out = new ByteArrayOutputStream();
    int offSet = 0;

    int i = 0;

    while (inputLen - offSet > 0)
    {
      byte[] cache;
      if (inputLen - offSet > 117)
        cache = cipher.doFinal(data, offSet, 117);
      else {
        cache = cipher.doFinal(data, offSet, inputLen - offSet);
      }
      out.write(cache, 0, cache.length);
      i++;
      offSet = i * 117;
    }
    byte[] encryptedData = out.toByteArray();
    out.close();
    return encryptedData;
  }

  public static String getPublicKey(Map<String, Object> keyMap)
    throws Exception
  {
    Key key = (Key)keyMap.get("RSAPublicKey");
    return PlatBase64Utils.encode(key.getEncoded());
  }
}