package xdt.tools.rsa;

import java.security.KeyFactory;
import java.security.PublicKey;
import java.security.Signature;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

import xdt.tools.Base64;


/**
 * 描述：数字签名类
 *
 * @author 
 * 创建时间：2009-4-2
 */
public class Signatory {
  /**
   * 生成MD5WithRSA数字签名
   * @param priKey 私钥的Base64串
   * @param data 签名源串
   * @param encoding 源串所使用的字符集编码 UTF8,GBK等
   * @return null：失败 否则：数字签名串
   */
  public static String sign(String priKey, String data, String encoding){ 
    String text = null;
    if (data == null){
      System.out.println("生成数字签名错：签名源串为空");
      return null;
    }
    if (encoding == null || encoding.trim().equals("")){
      encoding = "UTF-8";
    }
    
    //获取签名对象
    try{
      byte[] plainText = data.getBytes(encoding);
      PKCS8EncodedKeySpec priPKCS8 = new PKCS8EncodedKeySpec(Base64.decode(priKey));
      KeyFactory keyf = KeyFactory.getInstance("RSA");
      Signature signer=Signature.getInstance("MD5WithRSA"); 
      
      signer.initSign(keyf.generatePrivate(priPKCS8));
      signer.update(plainText); 
      byte[] signature=signer.sign();
      
      text = Base64.encodeBytes(signature);
    }catch (Exception e){
      System.out.println("生成数字签名错:"+e.getMessage());
      return null;
    }
    
    return text;
  }
  
  /**
   * 
   * 功能：验证数字签名
   *
   * @param pubKey 验证签名公钥； Base64格式
   * @param data 源串
   * @param sign 签名串
   * @param encoding 源串所使用的字符集编码 UTF8,GBK等
   * @return true：验证通过
   */
  public static boolean verify(String pubKey, String data, String sign, String encoding){
    boolean b = false;
    if (pubKey == null || data == null || sign == null){
      System.out.println("验证数字签名错：传入的参数有空值");
      System.out.println("pubKey="+pubKey);
      System.out.println("data="+data);
      System.out.println("sign="+sign);
      return b;
    }
    
    if (encoding == null || encoding.trim().equals("")){
      encoding = "UTF-8";
    }
    try{
      //生成验证对象
      Signature signer=Signature.getInstance("MD5WithRSA"); 
      
      //生成公钥
      X509EncodedKeySpec keySpec = new X509EncodedKeySpec(Base64.decode(pubKey));
      KeyFactory keyFactory = KeyFactory.getInstance("RSA");
      PublicKey publicKey = keyFactory.generatePublic(keySpec);
      
      signer.initVerify(publicKey); 
      byte[] plainText = data.getBytes(encoding);
      signer.update(plainText); 
      
      //验证签名
      b = signer.verify(Base64.decode(sign));
    }catch (Exception e){
      System.out.println("验证数字签名错"+e.getMessage());
      return false;
    }
    return b;
  }

  /**
   * bytes转换成十六进制字符串
   */
  public static String byte2HexStr(byte[] b) {
    String hs="";
    String stmp="";
    for (int n=0;n<b.length;n++) {
      stmp=(Integer.toHexString(b[n] & 0XFF));
      if (stmp.length()==1) hs=hs+"0"+stmp;
      else hs=hs+stmp;
      //if (n<b.length-1)  hs=hs+":";
    }
    return hs.toUpperCase();
  }

  /**
   * 十六进制字符串换转成bytes
   */
  public static byte[] hexStr2Bytes(String src) {
    int m=0,n=0;
    int l=src.length()/2;
    byte[] ret = new byte[l];
    for (int i = 0; i < l; i++) {
      m=i*2+1;
      n=m+1;
      ret[i] = uniteBytes(src.substring(i*2, m),src.substring(m,n));
    }
    return ret;
  }

  public static byte uniteBytes(String src0, String src1) {
    byte b0 = Byte.decode("0x" + src0).byteValue();
    b0 = (byte) (b0 << 4);
    byte b1 = Byte.decode("0x" + src1).byteValue();
    byte ret = (byte) (b0 | b1);
    return ret;
  }
}
