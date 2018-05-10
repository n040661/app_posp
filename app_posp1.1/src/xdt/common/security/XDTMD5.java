 package xdt.common.security;
 
 import java.io.IOException;
 import java.io.InputStream;
 import java.io.UnsupportedEncodingException;
import java.security.InvalidParameterException;
 import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
 
 public class XDTMD5
 {
   private static final int READBUF_SIZE = 1024;
   private static MessageDigest messageDigest = null;
 
   static {
     try {
       messageDigest = MessageDigest.getInstance("MD5");
     }
     catch (NoSuchAlgorithmException e) {
       e.printStackTrace();
     }
   }
 
   public static byte[] getHashByString(String data)
     throws InvalidParameterException
   {
     if (data == null)
     {
       throw new InvalidParameterException("XDT005");
     }
     try
     {
       return getHashByBytes(data.getBytes("UTF-8"));
     }
     catch (UnsupportedEncodingException e) {
       e.printStackTrace();
     }return null;
   }
 
   public static synchronized byte[] getHashByBytes(byte[] data)
     throws InvalidParameterException
   {
     if (data == null)
     {
       throw new InvalidParameterException("XDT006");
     }
     try
     {
       messageDigest.reset();
       messageDigest.update(data);
      return messageDigest.digest(); } catch (Exception e) {
     }
    return "".getBytes();
   }
 
   public static byte[] getHashByInputStream(InputStream is)
     throws InvalidParameterException, IOException
   {
     if (is == null)
     {
       throw new InvalidParameterException("XDT008");
     }
 
     try
     {
       byte[] buf = new byte[1024];
       messageDigest.reset();
       int size;
       while ((size = is.read(buf)) != -1)
       {
        messageDigest.update(buf, 0, size);
       }
       return messageDigest.digest();
     }
     catch (IOException e) {
       throw new IOException("XDT008");
     }
   }
 }
