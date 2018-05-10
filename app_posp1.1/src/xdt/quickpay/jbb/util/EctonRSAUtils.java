package xdt.quickpay.jbb.util;

import org.apache.commons.codec.binary.Base64;

import javax.crypto.Cipher;
import java.math.BigInteger;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.RSAPrivateKeySpec;
import java.security.spec.RSAPublicKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.HashMap;

public class EctonRSAUtils {  
	//密文长度等于密钥长度.当然这是不分片情况下的.分片后,密文长度=密钥长度*片数
    /** 
     * 生成公钥和私钥 
     */  
    public static HashMap<String, Object> getKeys() throws NoSuchAlgorithmException{  
        HashMap<String, Object> map = new HashMap<String, Object>();  
        KeyPairGenerator keyPairGen = KeyPairGenerator.getInstance("RSA");
        keyPairGen.initialize(512);  //TODO (1024)  密文长度等于密钥长度   ，密钥长度可修改
        KeyPair keyPair = keyPairGen.generateKeyPair();  
        RSAPublicKey publicKey = (RSAPublicKey) keyPair.getPublic();  
        RSAPrivateKey privateKey = (RSAPrivateKey) keyPair.getPrivate();  
        map.put("public", publicKey);  
        map.put("private", privateKey);  
                
        return map;  
    }  
    /** 
     * 使用模和指数生成RSA公钥 
     * 注意：【此代码用了默认补位方式，为RSA/None/PKCS1Padding，不同JDK默认的补位方式可能不同，如Android默认是RSA 
     * /None/NoPadding】 
     *  
     * @param modulus 
     *            模 
     * @param exponent 
     *            指数 
     * @return 
     */  
    public static RSAPublicKey getPublicKey(String modulus, String exponent) {  
        try {  
            BigInteger b1 = new BigInteger(modulus);  
            BigInteger b2 = new BigInteger(exponent);  
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            RSAPublicKeySpec keySpec = new RSAPublicKeySpec(b1, b2);  
            return (RSAPublicKey) keyFactory.generatePublic(keySpec);  
        } catch (Exception e) {  
            e.printStackTrace();  
            return null;  
        }  
    }  
  
    /** 
     * 使用模和指数生成RSA私钥 
     * 注意：【此代码用了默认补位方式，为RSA/None/PKCS1Padding，不同JDK默认的补位方式可能不同，如Android默认是RSA 
     * /None/NoPadding】 
     *  
     * @param modulus 
     *            模 
     * @param exponent 
     *            指数 
     * @return 
     */  
    public static RSAPrivateKey getPrivateKey(String modulus, String exponent) {  
        try {  
            BigInteger b1 = new BigInteger(modulus);  
            BigInteger b2 = new BigInteger(exponent);  
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            RSAPrivateKeySpec keySpec = new RSAPrivateKeySpec(b1, b2);  
            return (RSAPrivateKey) keyFactory.generatePrivate(keySpec);  
        } catch (Exception e) {  
            e.printStackTrace();  
            return null;  
        }  
    }  
  
    /** 
     * 公钥加密 
     *  
     * @param data 
     * @param publicKey 
     * @return 
     * @throws Exception 
     */  
    public static String encryptByPublicKey(String data, RSAPublicKey publicKey)  
            throws Exception {  
        Cipher cipher = Cipher.getInstance("RSA");
        cipher.init(Cipher.ENCRYPT_MODE, publicKey);  
        // 模长  
        int key_len = publicKey.getModulus().bitLength() / 8;  
        // 加密数据长度 <= 模长-11  
        String[] datas = splitString(data, key_len - 11);  
        String mi = "";  
        //如果明文长度大于模长-11则要分组加密  
        for (String s : datas) {  
            mi += bcd2Str(cipher.doFinal(s.getBytes()));  
        }  
        return mi;  
    }  
  
    /** 
     * 私钥解密 
     *  
     * @param data 
     * @param privateKey 
     * @return 
     * @throws Exception 
     */  
    public static String decryptByPrivateKey(String data, RSAPrivateKey privateKey)  
            throws Exception {  
        Cipher cipher = Cipher.getInstance("RSA");
        cipher.init(Cipher.DECRYPT_MODE, privateKey);  
        //模长  
        int key_len = privateKey.getModulus().bitLength() / 8;  
        byte[] bytes = data.getBytes();  
        byte[] bcd = ASCII_To_BCD(bytes, bytes.length);  
    //    System.err.println("bcd.length:"+bcd.length);  
        //如果密文长度大于模长则要分组解密  
        String ming = "";  
        byte[][] arrays = splitArray(bcd, key_len);  
        for(byte[] arr : arrays){  
            ming += new String(cipher.doFinal(arr));  
        }  
        return ming;  
    }  
    /** 
     * ASCII码转BCD码 
     *  
     */  
    public static byte[] ASCII_To_BCD(byte[] ascii, int asc_len) {  
        byte[] bcd = new byte[asc_len / 2];  
        int j = 0;  
        for (int i = 0; i < (asc_len + 1) / 2; i++) {  
            bcd[i] = asc_to_bcd(ascii[j++]);  
            bcd[i] = (byte) (((j >= asc_len) ? 0x00 : asc_to_bcd(ascii[j++])) + (bcd[i] << 4));  
        }  
        return bcd;  
    }  
    public static byte asc_to_bcd(byte asc) {  
        byte bcd;  
  
        if ((asc >= '0') && (asc <= '9'))  
            bcd = (byte) (asc - '0');  
        else if ((asc >= 'A') && (asc <= 'F'))  
            bcd = (byte) (asc - 'A' + 10);  
        else if ((asc >= 'a') && (asc <= 'f'))  
            bcd = (byte) (asc - 'a' + 10);  
        else  
            bcd = (byte) (asc - 48);  
        return bcd;  
    }  
    /** 
     * BCD转字符串 
     */  
    public static String bcd2Str(byte[] bytes) {  
        char temp[] = new char[bytes.length * 2], val;  
  
        for (int i = 0; i < bytes.length; i++) {  
            val = (char) (((bytes[i] & 0xf0) >> 4) & 0x0f);  
            temp[i * 2] = (char) (val > 9 ? val + 'A' - 10 : val + '0');  
  
            val = (char) (bytes[i] & 0x0f);  
            temp[i * 2 + 1] = (char) (val > 9 ? val + 'A' - 10 : val + '0');  
        }  
        return new String(temp);  
    }  
    /** 
     * 拆分字符串 
     */  
    public static String[] splitString(String string, int len) {  
        int x = string.length() / len;  
        int y = string.length() % len;  
        int z = 0;  
        if (y != 0) {  
            z = 1;  
        }  
        String[] strings = new String[x + z];  
        String str = "";  
        for (int i=0; i<x+z; i++) {  
            if (i==x+z-1 && y!=0) {  
                str = string.substring(i*len, i*len+y);  
            }else{  
                str = string.substring(i*len, i*len+len);  
            }  
            strings[i] = str;  
        }  
        return strings;  
    }  
    /** 
     *拆分数组  
     */  
    public static byte[][] splitArray(byte[] data,int len){  
        int x = data.length / len;  
        int y = data.length % len;  
        int z = 0;  
        if(y!=0){  
            z = 1;  
        }  
        byte[][] arrays = new byte[x+z][];  
        byte[] arr;  
        for(int i=0; i<x+z; i++){  
            arr = new byte[len];  
            if(i==x+z-1 && y!=0){  
                System.arraycopy(data, i*len, arr, 0, y);  
            }else{  
                System.arraycopy(data, i*len, arr, 0, len);  
            }  
            arrays[i] = arr;  
        }  
        return arrays;  
    }  
    
    public static void main(String[] args) throws Exception {  
        HashMap<String, Object> map = EctonRSAUtils.getKeys();  
        //生成公钥和私钥  
        RSAPublicKey publicKey = (RSAPublicKey) map.get("public");  
        RSAPrivateKey privateKey = (RSAPrivateKey) map.get("private");  
          
        //模  
        String modulus = publicKey.getModulus().toString();  
        //模  
        String modulus2 = privateKey.getModulus().toString(); 
        if(modulus.equals(modulus2)){
        	System.out.println("公钥模==私钥模");
        }
        //公钥指数  
        String public_exponent = publicKey.getPublicExponent().toString();  
        //私钥指数  
        String private_exponent = privateKey.getPrivateExponent().toString();
        
        //明文  
        String ming = "33345678";
        System.out.println("明文  :"+ming);
        //使用模和指数生成公钥和私钥  
        RSAPublicKey pubKey = EctonRSAUtils.getPublicKey(modulus, public_exponent);  
        RSAPrivateKey priKey = EctonRSAUtils.getPrivateKey(modulus, private_exponent);
        
        System.out.println("公钥：\n"+Base64.encodeBase64String(publicKey.getEncoded()));  
        System.out.println("私钥：\n"+Base64.encodeBase64String(privateKey.getEncoded())); 
        
        //加密后的密文  
        String enMing = EctonRSAUtils.encryptByPublicKey(ming, pubKey);  
        System.out.println("密文:"+enMing);
        System.out.println("密文长度:"+enMing.length());
        //解密后的明文  
        String deMing = EctonRSAUtils.decryptByPrivateKey(enMing, priKey);  
        System.out.println("密文解密成明文  :"+deMing);
    }  
    
    
    
   
    // 通过公钥byte[]将公钥还原，适用于RSA算法
    public   static   PublicKey getPublicKey( byte [] keyBytes)  throws
    NoSuchAlgorithmException,InvalidKeySpecException {
    	X509EncodedKeySpec keySpec =  new   X509EncodedKeySpec(keyBytes);
    	KeyFactory keyFactory = KeyFactory.getInstance( "RSA" );
    	PublicKey publicKey = keyFactory.generatePublic(keySpec);
    	return   publicKey;
    }
    // 通过私钥byte[]将私钥还原，适用于RSA算法
    public   static   PrivateKey getPrivateKey( byte [] keyBytes)  throws
    NoSuchAlgorithmException,InvalidKeySpecException {
    	PKCS8EncodedKeySpec keySpec =  new   PKCS8EncodedKeySpec(keyBytes);
    	KeyFactory keyFactory = KeyFactory.getInstance( "RSA" );
    	PrivateKey privateKey = keyFactory.generatePrivate(keySpec);
    	return   privateKey;
    }
    
    //公钥加密
    public static String encryptStr(String ming ,String key) throws Exception {
    	byte [] keyBytes = Base64.decodeBase64(key);
    	RSAPublicKey publicKey = (RSAPublicKey) getPublicKey(keyBytes);
        //模  
        String modulus = publicKey.getModulus().toString();  
        //公钥指数  
        String public_exponent = publicKey.getPublicExponent().toString();  
        //使用模和指数生成公钥和私钥  
        RSAPublicKey pubKey = EctonRSAUtils.getPublicKey(modulus, public_exponent);  
        String mi = EctonRSAUtils.encryptByPublicKey(ming, pubKey);  
		return mi; 	
    }
    
    //私钥解密
    public static String decryptStr(String mi ,String key) throws Exception{
    	byte [] keyBytes = Base64.decodeBase64(key);
    	RSAPrivateKey privateKey = (RSAPrivateKey) getPrivateKey(keyBytes);
        //模  
        String modulus = privateKey.getModulus().toString();  
        //私钥指数  
        String private_exponent = privateKey.getPrivateExponent().toString();
    	
        RSAPrivateKey priKey = EctonRSAUtils.getPrivateKey(modulus, private_exponent);
        //解密后的明文  
        String ming = EctonRSAUtils.decryptByPrivateKey(mi, priKey);  
		return ming;
    	
    }
    
    //随机生成str类型的一对工作密钥
    public static HashMap<String, Object> getStrKeys() throws NoSuchAlgorithmException{
        HashMap<String, Object> map = EctonRSAUtils.getKeys();  
        //生成公钥和私钥  
        RSAPublicKey publicKey = (RSAPublicKey) map.get("public");  
        RSAPrivateKey privateKey = (RSAPrivateKey) map.get("private");
        String pubKeyStr = Base64.encodeBase64String(publicKey.getEncoded());
        String priKeyStr = Base64.encodeBase64String(privateKey.getEncoded());
        map.clear();
        map.put("public", pubKeyStr);  
        map.put("private", priKeyStr);  
        return map;
    }
    
}
