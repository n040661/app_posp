package xdt.common;

 import java.util.Locale;
 
 public class XDTHex
 {
   public static String encode(byte[] data)
     throws Exception
   {
     if (data == null)
     {
       throw new Exception("XDT014");
     }
 
     StringBuilder sb = new StringBuilder();
 
    for (int n = 0; n < data.length; ++n) {
      String stmp = Integer.toHexString(data[n] & 0xFF);
       if (stmp.length() == 1)
         sb.append("0");
       sb.append(stmp);
     }
     return sb.toString().toUpperCase(Locale.CHINA);
   }
 
   public static byte[] decode(String data)
     throws Exception
   {
    if (data == null)
     {
      throw new Exception("XDT015");
     }
 
     int n = data.length();
     if (n % 2 != 0) {
       data = "0" + data;
       ++n;
     }
     n /= 2;
    byte[] bytes = new byte[n];
     for (int i = 0; i < n; ++i) {
       bytes[i] = 
         (byte)Integer.parseInt(data.substring(i * 2, i * 2 + 2), 16);
     }
     return bytes;
   }
 }
