package xdt.dto.hfb;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESedeKeySpec;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.codec.binary.Base64;

/**
 * Title:        DESede算法 
 * Description:  主要用于兼容.net算法，要求私钥key必须为24位，IVPS值必须为8位长度
 *
 * @author Houjianchun
 * @version 2015-4-2 10:28:26
 */
public class Desede
{
    private static final String alg = "DESede";
    private static final String transformation = "DESede/CBC/PKCS5Padding";

    /**
     * DESede加密
     *
     * @param src  待加密数据
     * @param key  加密私钥，长度必须是8的倍数
     * @param ivps IvParameterSpec
     * @return 加密后的字节数组，一般结合Base64编码使用
     * @throws Exception 异常
     */
    public static byte[] encode(byte[] src, String key, byte[] ivps) throws Exception
    {
        return encode(src, key, ivps, transformation);
    }

    /**
     * DESede加密
     *
     * @param src            待加密数据
     * @param key            加密私钥，长度必须是8的倍数
     * @param ivps           IvParameterSpec
     * @param transformation String
     * @return 加密后的字节数组，一般结合Base64编码使用
     * @throws Exception 异常
     */
    public static byte[] encode(byte[] src, String key, byte[] ivps, String transformation) throws Exception
    {
        try
        {
            // 为 CBC 模式创建一个用于初始化的 vector 对象
            IvParameterSpec IvParameters = new IvParameterSpec(ivps);
            // 从原始密匙数据创建DESKeySpec对象
            DESedeKeySpec dks = new DESedeKeySpec(key.getBytes("UTF-8"));
            // 创建一个密匙工厂，然后用它把KeySpec转换成一个SecretKey对象
            SecretKeyFactory keyFactory = SecretKeyFactory.getInstance(alg);
            SecretKey securekey = keyFactory.generateSecret(dks);
            // Cipher对象实际完成加密操作
            Cipher cipher = Cipher.getInstance(transformation);
            // 用密匙初始化Cipher对象
            cipher.init(Cipher.ENCRYPT_MODE, securekey, IvParameters);
            // 现在，获取数据并加密
            // 正式执行加密操作
            return cipher.doFinal(src);
        } catch (Exception e)
        {
            throw new Exception(e);
        }
    }

    /**
     *
     *
     */
    public static String encodeECB(String src, String key) throws Exception
    {
        try
        {
            SecretKey deskey = new SecretKeySpec(key.getBytes("UTF-8"), "DESede");
            Cipher cipher = Cipher.getInstance("DESede/ECB/PKCS5Padding");
            cipher.init(Cipher.ENCRYPT_MODE, deskey);

            byte[] cipherInfo = cipher.doFinal(src.getBytes("UTF-8"));
            System.out.println(Hex.encode(cipherInfo).length());
            System.out.println(Base64.encodeBase64String(cipherInfo).length());
            return Hex.encode(cipherInfo);
        } catch (Exception e)
        {
            throw new Exception(e);
        }
    }

    /**
     * DESede解密
     *
     * @param src  待解密字符串
     * @param key  解密私钥，长度必须是8的倍数
     * @param ivps IvParameterSpec
     * @return 解密后的字节数组
     * @throws Exception 异常
     */
    public static byte[] decode(byte[] src, String key, byte[] ivps) throws Exception
    {
        return decode(src, key, ivps, transformation);
    }

    /**
     * DESede解密
     *
     * @param src            待解密字符串
     * @param key            解密私钥，长度必须是8的倍数
     * @param ivps           IvParameterSpec
     * @param transformation String
     * @return 解密后的字节数组
     * @throws Exception 异常
     */
    public static byte[] decode(byte[] src, String key, byte[] ivps, String transformation) throws Exception
    {
        try
        {
            // 为 CBC 模式创建一个用于初始化的 vector 对象
            IvParameterSpec IvParameters = new IvParameterSpec(ivps);
            // 从原始密匙数据创建一个DESKeySpec对象
            DESedeKeySpec dks = new DESedeKeySpec(key.getBytes("UTF-8"));
            // 创建一个密匙工厂，然后用它把KeySpec对象转换成一个SecretKey对象
            SecretKeyFactory keyFactory = SecretKeyFactory.getInstance(alg);
            SecretKey securekey = keyFactory.generateSecret(dks);
            // Cipher对象实际完成解密操作
            Cipher cipher = Cipher.getInstance(transformation);
            // 用密匙初始化Cipher对象
            cipher.init(Cipher.DECRYPT_MODE, securekey, IvParameters);
            // 现在，获取数据并解密
            // 正式执行解密操作
            return cipher.doFinal(src);
        } catch (Exception e)
        {
            throw new Exception(e);
        }
    }


    public static String decodeECB(String src, String key) throws Exception
    {
        try
        {
            SecretKey deskey = new SecretKeySpec(key.getBytes("UTF-8"), "DESede");
            Cipher cipher = Cipher.getInstance("DESede/ECB/PKCS5Padding");
            cipher.init(Cipher.DECRYPT_MODE, deskey);

            byte[] decodeRes = cipher.doFinal(Hex.decode(src));
            return new String(decodeRes, "UTF-8");
        } catch (Exception e)
        {
            throw new Exception(e);
        }
    }

    public static void main(String[] args) throws Exception
    {
//        System.out.println(Base64.encode(Desede.encode("1qaz2ws".getBytes(), "01234567890123456789012345678912", "23456789".getBytes())));
//        System.out.println(new String(Desede.decode(Base64.decode("uqtJiz1JMw4="), "111111112222222233333333", "12345678".getBytes())));

        String data = "59a2bf6c502a2cd9076611e1863bf0b23b8241ff29b81113467aa40a1ff2ffdcb56f4d7a7b969925f587ead7844a004b57f3b35ab7d2939e";
        String keys = "b74cfc9d50b422926b625462";

//        System.out.println(Base64.encode("1234567890123456".getBytes()));
//        System.out.println(keys.length() + "   " + keys.getBytes().length);
//        String key = Base64.encode(keys.getBytes()).substring(0,24);
//        System.out.println(Base64.encode(keys.getBytes()));
//        System.out.println(Base64.encode(key.getBytes()).length());

//        byte[] iv = keys.substring(0,8).getBytes();
//        System.out.println(Hex.encode(Desede.encode(data.getBytes(), keys, iv)));


//        System.out.println(Hex.encode(encodeECB(data.getBytes(), keys.getBytes())));
//        System.out.println(Base64.encode(encodeECB(data.getBytes("UTF-8"), keys.getBytes())));

        System.out.println(encodeECB("黎涛^6225880123456789^421023199909091010^13999999999", keys));


        System.out.println(decodeECB(data, keys));

//        byte[] iv = new byte[8];
//        for(int i=0; i<8; i++){
//            iv[i] = ivs[i];
//        }


//        System.out.println(new String(Desede.decode(Hex.decode("c51f305b92c2d73f"), "653352243ccc2acfd043d384247c99e1", "653352243ccc2acfd043d384247c99e1".substring(0,8).getBytes())));

    }
}
