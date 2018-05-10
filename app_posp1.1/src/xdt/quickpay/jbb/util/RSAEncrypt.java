package xdt.quickpay.jbb.util;

import java.io.BufferedReader;  
import java.io.BufferedWriter;  
import java.io.FileReader;  
import java.io.FileWriter;  
import java.io.IOException;  
import java.security.InvalidKeyException;  
import java.security.KeyFactory;  
import java.security.KeyPair;  
import java.security.KeyPairGenerator;  
import java.security.NoSuchAlgorithmException;  
import java.security.SecureRandom;  
  
import java.security.interfaces.RSAPrivateKey;  
import java.security.interfaces.RSAPublicKey;  
import java.security.spec.InvalidKeySpecException;  
import java.security.spec.PKCS8EncodedKeySpec;  
import java.security.spec.X509EncodedKeySpec;  
  



import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.crypto.BadPaddingException;  
import javax.crypto.Cipher;  
import javax.crypto.IllegalBlockSizeException;  
import javax.crypto.NoSuchPaddingException;    

import net.sf.json.JSONObject;

import org.apache.commons.lang.ArrayUtils;
  
public class RSAEncrypt {  
    /** 
     * 字节数据转字符串专用集合 
     */  
    private static final char[] HEX_CHAR = { '0', '1', '2', '3', '4', '5', '6',  
            '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f' };  
  
    /** 
     * 随机生成密钥对 
     */  
    public static void genKeyPair(String filePath) {  
        // KeyPairGenerator类用于生成公钥和私钥对，基于RSA算法生成对象  
        KeyPairGenerator keyPairGen = null;  
        try {  
            keyPairGen = KeyPairGenerator.getInstance("RSA");  
        } catch (NoSuchAlgorithmException e) {  
            // TODO Auto-generated catch block  
            e.printStackTrace();  
        }  
        // 初始化密钥对生成器，密钥大小为96-1024位  
        keyPairGen.initialize(1024,new SecureRandom());  
        // 生成一个密钥对，保存在keyPair中  
        KeyPair keyPair = keyPairGen.generateKeyPair();  
        // 得到私钥  
        RSAPrivateKey privateKey = (RSAPrivateKey) keyPair.getPrivate();  
        // 得到公钥  
        RSAPublicKey publicKey = (RSAPublicKey) keyPair.getPublic();  
        try {  
            // 得到公钥字符串  
            String publicKeyString = Base64.encode(publicKey.getEncoded());  
            // 得到私钥字符串  
            String privateKeyString = Base64.encode(privateKey.getEncoded());  
            
            
            System.out.println("公钥串：" + publicKeyString);
            
            System.out.println("私钥串：" + privateKeyString);
            // 将密钥对写入到文件  
            FileWriter pubfw = new FileWriter(filePath + "/publicKey.keystore");  
            FileWriter prifw = new FileWriter(filePath + "/privateKey.keystore");  
            BufferedWriter pubbw = new BufferedWriter(pubfw);  
            BufferedWriter pribw = new BufferedWriter(prifw);  
            pubbw.write(publicKeyString);  
            pribw.write(privateKeyString);  
            pubbw.flush();  
            pubbw.close();  
            pubfw.close();  
            pribw.flush();  
            pribw.close();  
            prifw.close();  
        } catch (Exception e) {  
            e.printStackTrace();  
        }  
    }  
  
    /** 
     * 从文件中输入流中加载公钥 
     *  
     * @param in 
     *            公钥输入流 
     * @throws Exception 
     *             加载公钥时产生的异常 
     */  
    public static String loadPublicKeyByFile(String path) throws Exception {  
        try {  
            BufferedReader br = new BufferedReader(new FileReader(path  
                    + "/publicKey.keystore"));  
            String readLine = null;  
            StringBuilder sb = new StringBuilder();  
            while ((readLine = br.readLine()) != null) {  
                sb.append(readLine);  
            }  
            br.close();  
            return sb.toString();  
        } catch (IOException e) {  
            throw new Exception("公钥数据流读取错误");  
        } catch (NullPointerException e) {  
            throw new Exception("公钥输入流为空");  
        }  
    }  
  
    /** 
     * 从字符串中加载公钥 
     *  
     * @param publicKeyStr 
     *            公钥数据字符串 
     * @throws Exception 
     *             加载公钥时产生的异常 
     */  
    public static RSAPublicKey loadPublicKeyByStr(String publicKeyStr)  
            throws Exception {  
        try {  
            byte[] buffer = Base64.decode(publicKeyStr);  
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");  
            X509EncodedKeySpec keySpec = new X509EncodedKeySpec(buffer);  
            return (RSAPublicKey) keyFactory.generatePublic(keySpec);  
        } catch (NoSuchAlgorithmException e) {  
            throw new Exception("无此算法");  
        } catch (InvalidKeySpecException e) {  
            throw new Exception("公钥非法");  
        } catch (NullPointerException e) {  
            throw new Exception("公钥数据为空");  
        }  
    }  
  
    /** 
     * 从文件中加载私钥 
     *  
     * @param keyFileName 
     *            私钥文件名 
     * @return 是否成功 
     * @throws Exception 
     */  
    public static String loadPrivateKeyByFile(String path) throws Exception {  
        try {  
            BufferedReader br = new BufferedReader(new FileReader(path  
                    + "/privateKey.keystore"));  
            String readLine = null;  
            StringBuilder sb = new StringBuilder();  
            while ((readLine = br.readLine()) != null) {  
                sb.append(readLine);  
            }  
            br.close();  
            return sb.toString();  
        } catch (IOException e) {  
            throw new Exception("私钥数据读取错误");  
        } catch (NullPointerException e) {  
            throw new Exception("私钥输入流为空");  
        }  
    }  
  
    public static RSAPrivateKey loadPrivateKeyByStr(String privateKeyStr)  
            throws Exception {  
        try {  
            byte[] buffer = Base64.decode(privateKeyStr);  
            PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(buffer);  
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");  
            return (RSAPrivateKey) keyFactory.generatePrivate(keySpec);  
        } catch (NoSuchAlgorithmException e) {  
            throw new Exception("无此算法");  
        } catch (InvalidKeySpecException e) {  
            throw new Exception("私钥非法");  
        } catch (NullPointerException e) {  
            throw new Exception("私钥数据为空");  
        }  
    }  
  
    /** 
     * 公钥加密过程 
     *  
     * @param publicKey 
     *            公钥 
     * @param plainTextData 
     *            明文数据 
     * @return 
     * @throws Exception 
     *             加密过程中的异常信息 
     */  
    public static byte[] encrypt(RSAPublicKey publicKey, byte[] plainTextData)  
            throws Exception {  
        if (publicKey == null) {  
            throw new Exception("加密公钥为空, 请设置");  
        }  
        Cipher cipher = null;  
        try {  
            // 使用默认RSA  
            cipher = Cipher.getInstance("RSA");  
            // cipher= Cipher.getInstance("RSA", new BouncyCastleProvider());  
            cipher.init(Cipher.ENCRYPT_MODE, publicKey);  
            byte[] output=null;
            
            StringBuilder sb = new StringBuilder();
			for (int i = 0; i < plainTextData.length; i += 100) {
				byte[] doFinal = cipher.doFinal(ArrayUtils.subarray(plainTextData, i,
						i + 100));
				sb.append(new String(doFinal));
				output = ArrayUtils.addAll(output, doFinal);
			}
            
            
            
//            byte[] output = cipher.doFinal(plainTextData);  
            return output;  
        } catch (NoSuchAlgorithmException e) {  
            throw new Exception("无此加密算法");  
        } catch (NoSuchPaddingException e) {  
            e.printStackTrace();  
            return null;  
        } catch (InvalidKeyException e) {  
            throw new Exception("加密公钥非法,请检查");  
        } catch (IllegalBlockSizeException e) {  
            throw new Exception("明文长度非法");  
        } catch (BadPaddingException e) {  
            throw new Exception("明文数据已损坏");  
        }  
    }  
  
    /** 
     * 私钥加密过程 
     *  
     * @param privateKey 
     *            私钥 
     * @param plainTextData 
     *            明文数据 
     * @return 
     * @throws Exception 
     *             加密过程中的异常信息 
     */  
    public static byte[] encrypt(RSAPrivateKey privateKey, byte[] plainTextData)  
            throws Exception {  
        if (privateKey == null) {  
            throw new Exception("加密私钥为空, 请设置");  
        }  
        Cipher cipher = null;  
        try {  
            // 使用默认RSA  
            cipher = Cipher.getInstance("RSA");  
            cipher.init(Cipher.ENCRYPT_MODE, privateKey);  
            byte[] output = cipher.doFinal(plainTextData);  
            return output;  
        } catch (NoSuchAlgorithmException e) {  
            throw new Exception("无此加密算法");  
        } catch (NoSuchPaddingException e) {  
            e.printStackTrace();  
            return null;  
        } catch (InvalidKeyException e) {  
            throw new Exception("加密私钥非法,请检查");  
        } catch (IllegalBlockSizeException e) {  
            throw new Exception("明文长度非法");  
        } catch (BadPaddingException e) {  
            throw new Exception("明文数据已损坏");  
        }  
    }  
  
    /** 
     * 私钥解密过程 
     *  
     * @param privateKey 
     *            私钥 
     * @param cipherData 
     *            密文数据 
     * @return 明文 
     * @throws Exception 
     *             解密过程中的异常信息 
     */  
    public static byte[] decrypt(RSAPrivateKey privateKey, byte[] cipherData)  
            throws Exception {  
        if (privateKey == null) {  
            throw new Exception("解密私钥为空, 请设置");  
        }  
        Cipher cipher = null;  
        try {  
            // 使用默认RSA  
            cipher = Cipher.getInstance("RSA");  
            // cipher= Cipher.getInstance("RSA", new BouncyCastleProvider());  
            cipher.init(Cipher.DECRYPT_MODE, privateKey);  
//            byte[] output = cipher.doFinal(cipherData);  
            
            byte[] output;
            
            StringBuilder sb = new StringBuilder();
			for (int i = 0; i < cipherData.length; i += 128) {
				byte[] doFinal = cipher.doFinal(ArrayUtils.subarray(cipherData, i,
						i + 128));
				sb.append(new String(doFinal,"UTF-8"));
			}
			output = sb.toString().getBytes("UTF-8");
            
            
            return output;  
        } catch (NoSuchAlgorithmException e) {  
            throw new Exception("无此解密算法");  
        } catch (NoSuchPaddingException e) {  
            e.printStackTrace();  
            return null;  
        } catch (InvalidKeyException e) {  
            throw new Exception("解密私钥非法,请检查");  
        } catch (IllegalBlockSizeException e) {  
            throw new Exception("密文长度非法");  
        } catch (BadPaddingException e) {  
            throw new Exception("密文数据已损坏");  
        }  
    }  
  
    
    
    
    
    /** 
     * 私钥解密过程 -New
     *  
     * @param privateKey 
     *            私钥 
     * @param cipherData 
     *            密文数据 
     * @return 明文 
     * @throws Exception 
     *             解密过程中的异常信息 
     */  
    public static byte[] decryptNew(RSAPrivateKey privateKey, byte[] cipherData)  
            throws Exception {  
        if (privateKey == null) {  
            throw new Exception("解密私钥为空, 请设置");  
        }  
        Cipher cipher = null;  
        try {  
            // 使用默认RSA  
            cipher = Cipher.getInstance("RSA");  
            // cipher= Cipher.getInstance("RSA", new BouncyCastleProvider());  
            cipher.init(Cipher.DECRYPT_MODE, privateKey);  
//            byte[] output = cipher.doFinal(cipherData);  
            
            List<Byte> list =new ArrayList<Byte>();
            
			for (int i = 0; i < cipherData.length; i += 128) {
				byte[] doFinal = cipher.doFinal(ArrayUtils.subarray(cipherData, i,
						i + 128));
				
				for(byte b : doFinal){
					list.add(b);
				}
			}
			byte[] array = new byte[list.size()];
            for(int i=0; i<list.size(); i++){
            	array[i] = list.get(i);
            }
            
            return array;  
        } catch (NoSuchAlgorithmException e) {  
            throw new Exception("无此解密算法");  
        } catch (NoSuchPaddingException e) {  
            e.printStackTrace();  
            return null;  
        } catch (InvalidKeyException e) {  
            throw new Exception("解密私钥非法,请检查");  
        } catch (IllegalBlockSizeException e) {  
            throw new Exception("密文长度非法");  
        } catch (BadPaddingException e) {  
            throw new Exception("密文数据已损坏");  
        }  
    }  
    
    
    /** 
     * 公钥解密过程 
     *  
     * @param publicKey 
     *            公钥 
     * @param cipherData 
     *            密文数据 
     * @return 明文 
     * @throws Exception 
     *             解密过程中的异常信息 
     */  
    public static byte[] decrypt(RSAPublicKey publicKey, byte[] cipherData)  
            throws Exception {  
        if (publicKey == null) {  
            throw new Exception("解密公钥为空, 请设置");  
        }  
        Cipher cipher = null;  
        try {  
            // 使用默认RSA  
            cipher = Cipher.getInstance("RSA");  
            // cipher= Cipher.getInstance("RSA", new BouncyCastleProvider());  
            cipher.init(Cipher.DECRYPT_MODE, publicKey);  
            byte[] output = cipher.doFinal(cipherData);  
            return output;  
        } catch (NoSuchAlgorithmException e) {  
            throw new Exception("无此解密算法");  
        } catch (NoSuchPaddingException e) {  
            e.printStackTrace();  
            return null;  
        } catch (InvalidKeyException e) {  
            throw new Exception("解密公钥非法,请检查");  
        } catch (IllegalBlockSizeException e) {  
            throw new Exception("密文长度非法");  
        } catch (BadPaddingException e) {  
            throw new Exception("密文数据已损坏");  
        }  
    }  
  
    /** 
     * 字节数据转十六进制字符串 
     *  
     * @param data 
     *            输入数据 
     * @return 十六进制内容 
     */  
    public static String byteArrayToString(byte[] data) {  
        StringBuilder stringBuilder = new StringBuilder();  
        for (int i = 0; i < data.length; i++) {  
            // 取出字节的高四位 作为索引得到相应的十六进制标识符 注意无符号右移  
            stringBuilder.append(HEX_CHAR[(data[i] & 0xf0) >>> 4]);  
            // 取出字节的低四位 作为索引得到相应的十六进制标识符  
            stringBuilder.append(HEX_CHAR[(data[i] & 0x0f)]);  
            if (i < data.length - 1) {  
                stringBuilder.append(' ');  
            }  
        }  
        return stringBuilder.toString();  
    }
    
    
	public static HashMap<String, String> toHashMap(String jsonStr) {
		HashMap<String, String> data = new HashMap<String, String>();
		// 将json字符串转换成jsonObject
		JSONObject jsonObject = JSONObject.fromObject(jsonStr);
		Iterator<?> it = jsonObject.keys();
		// 遍历jsonObject数据，添加到Map对象
		while (it.hasNext()) {
			String key = String.valueOf(it.next());
			String value = (String) jsonObject.get(key);
			data.put(key, value);
		}
		return data;
	}
    
	
	
	
	 /** 
     * 随机生成密钥对 
     */  
    public static Map<String, String> genKeyPairMap() {  
        // KeyPairGenerator类用于生成公钥和私钥对，基于RSA算法生成对象  
    	Map<String, String> keyPairMap = new HashMap<String, String>();
        KeyPairGenerator keyPairGen = null;  
        try {  
            keyPairGen = KeyPairGenerator.getInstance("RSA");  
        } catch (NoSuchAlgorithmException e) {  
            // TODO Auto-generated catch block  
            e.printStackTrace();  
        }  
        // 初始化密钥对生成器，密钥大小为96-1024位  
        keyPairGen.initialize(1024,new SecureRandom());  
        // 生成一个密钥对，保存在keyPair中  
        KeyPair keyPair = keyPairGen.generateKeyPair();  
        // 得到私钥  
        RSAPrivateKey privateKey = (RSAPrivateKey) keyPair.getPrivate();  
        // 得到公钥  
        RSAPublicKey publicKey = (RSAPublicKey) keyPair.getPublic();  
        try {  
            // 得到公钥字符串  
            String publicKeyString = Base64.encode(publicKey.getEncoded());  
            // 得到私钥字符串  
            String privateKeyString = Base64.encode(privateKey.getEncoded());  
            
            
            System.out.println("公钥串：" + publicKeyString);
            
            System.out.println("私钥串：" + privateKeyString);
            
            keyPairMap.put("publicKey", publicKeyString);
            keyPairMap.put("privateKey", privateKeyString);
        } catch (Exception e) {  
            e.printStackTrace();  
        }  
        return keyPairMap;
    }  
    
    
    public static void main(String[] args) throws Exception{
//    	String filePath = "E:/keystore";
//    	String content = "6216261000000000018";
//    	System.out.println("原内容为：" + content);
//    	RSAPublicKey publicKey = loadPublicKeyByStr("MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQDQZY3hBd4Wft356cWFDONlWsfMmJcW0F9ZMrJw/MIqoKFLV6Q+lIgiiFbO1Q2jV7vTbphz1JCredBtsJjJo6PNe9EyH+BthMggh4SMvqncbn6mvpfKVw9OI7bmvLumQ29kx7nMRRGeClknnxTxvdR76bBWCy8B+o1xTvvRTVqIfQIDAQAB");
//    	RSAPrivateKey privateKey = loadPrivateKeyByStr("MIICeAIBADANBgkqhkiG9w0BAQEFAASCAmIwggJeAgEAAoGBANBljeEF3hZ+3fnpxYUM42Vax8yYlxbQX1kysnD8wiqgoUtXpD6UiCKIVs7VDaNXu9NumHPUkKt50G2wmMmjo8170TIf4G2EyCCHhIy+qdxufqa+l8pXD04jtua8u6ZDb2THucxFEZ4KWSefFPG91HvpsFYLLwH6jXFO+9FNWoh9AgMBAAECgYEAjA5EbR/xY3L+IjCfY80Gop6BOxquKFIAUsVEsbTdlaKA5E7RfpF6cKLqnHUJhma3P+DLKsWIOQZUMvQFqzaEJaXcLflhHqwGTE7xAPBJn1hKBzcnkJUtSfIitmpYKvXny5CeMpFiC1aPekhkqW9EAX1rhKhS1ClalhNECArRRUECQQDuVSyeZQ57xBfo1QQRXFYvEIMu7zEd2OhSvor9LgFIQJqKlDuBtjqhV3Y8Ed38yhKEcx0JzVaCQ30tBSzrPEYxAkEA39hJMpp+1sTmMsqSuARwCiun1cLiC+gnFRR2kkdEoxmIKzMuto4MpbvjjBBtyrcqGQR1vQbbST2DUWh6k6R4DQJBAOZCahI6Bk0y/fqX2w8+y5E6/dLZivWQO5y9tKC40spQiD58yiUevvRkALo2dOcm/87xPEvdE8hUldpqFqTKnfECQQDLPgWQJcj+1j6xZU78OENGaMb2BwHZj8tUj4TtYjpG4NrLZqiGZsnC/asotI4Lnm1h07PXks8uW+TM2NFbFt1NAkA3KATJQ33KZJDYO+Ft34sotoFU/BWx3pjJe/eku//XhRxFQpvbqrfoNQfE7YFwk/f3SHyskT5cbAVg/UatfSUe");
//    	
//    	byte[] encryptStrByPub = encrypt(publicKey, content.getBytes("UTF-8"));
//    	String encryptResult = Base64.encode(encryptStrByPub);
//    	System.out.println("加密串为：" + encryptResult);
    	
    	
    	String mengshenStr ="eH1BMfVgS00dPxH5rCQgBQOB3uDuE2m6VD9xwFobf6LayQcdBcbQgxj4hdqLrt+Rns8BJdLOEPPCJQLRXOIGlDzdsFRmWtY3w8oN2qXX/P8d5YILuQUJmUW3445gvXVbLMtIclf77XzxDZpWe24c4EBtDKNF/BoQC3YCx/l1guE=";
    	
    	RSAPrivateKey privateKey1 = RSAEncrypt.loadPrivateKeyByStr("MIICdgIBADANBgkqhkiG9w0BAQEFAASCAmAwggJcAgEAAoGBAKo8ZfKmC9p7CzdUCwkg5pqtcK92MPsHgr/ivrJFYYJmcb7P37xOnGCyORy62/FQEHPwg5l54oLk2kVkRt9V3nT1oPMu3rXb3KA8gtLdeok1ghcG1KjecEUiNY9KoycR4sXKi1AK5nTl5HEDyJiDzrCbKmvNEnVQYlSWx0LgofanAgMBAAECgYAayeBdG8YXvm8YaNBHwnando2Z/uZcSmVH3t2VXhenFonltf6GOnSfPsx/+eOsiKXeKMX8u9JqOKskalBAytlzmmnV6zT3Fqhli5TRstCFu9QsEZYXiQky7i4E5/gw3dj09/H+Dedbm4G6RSFh/GziI0JSEpoZowUX2iSI7X81MQJBAOAI6ZikULTvb3sqRg5srd7DHYndYD71QMGzciMAgfV/bKnp5zH9vubpNOsIfY12t1mjWCmb5Gpt3pLsgrLeP48CQQDChm3+tg5hTFtggI7NwgJArj8SuCfad3feguGayXjmbVs9ZsCy2FcAaa2Q+h0mxtHCTFZQrIzZYFhYfc+99UtpAkA/PqTbP3+9JF6/UVbHmayX11MOaV1TSJ8IT6Un9mu5p0hAy1RGC5EJ1Mnok9QFzH7EIlWwIV8hIHUV2wimgBFHAkEAlnfxwaMFwJGV5THMRLCbmXivZUV53m2nFUFcTaUmledlOjuvNyhNNA6KqaguwH3uZ0MxSToPwV3bOKgo96E0cQJAbo/6tICko7dcaKvz6FdMEfh4+2/XSb/rP2sS3MkSW//dqr3nfrCYiOFIq3VOE0ZEjCgGp3zAiST5GWB6hSktGw==");
		// 解密卡号
		//String realPayAcctNo = RsaDescryptUtil.decryptByPrivateKey(privateKey1, mengshenStr);
    	
    	//System.out.println("rsaAcctNo:" + realPayAcctNo);
    	
    	
//    	byte[] decryptStr = decrypt(privateKey, Base64.decode(encryptResult));
//    	String decryptResult = new String(decryptStr, "UTF-8");
//    	System.out.println("解密结果为：" + decryptResult);
    	
    }
}  