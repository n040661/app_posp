 package xdt.common.security;
 
 
 public class XDTConverter
 {
   /*public static String bytesToBCD(byte[] src)
     throws Exception
   {
     return CBBCD.encode(src);
   }
 
   public static byte[] BCDToBytes(String src)
     throws Exception
   {
    return CBBCD.decode(src);
   }*/
 
   public static String bytesToBase64(byte[] src)
     throws Exception
   {
    return new String(XDTBase64.encode(src));
   }
 
   public static byte[] base64ToBytes(String src)
     throws Exception
   {
     return XDTBase64.decode(src.getBytes());
   }
 
   public static String bytesToHex(byte[] src)
     throws Exception
   {
    return XDTHex.encode(src);
   }
 
   public static byte[] hexToBytes(String src)
     throws Exception
   {
    return XDTHex.decode(src);
   }
 }
