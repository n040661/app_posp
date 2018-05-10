package xdt.quickpay.shyb.util;

import java.io.UnsupportedEncodingException;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

/**
 * @author sailfish
 * @create 2017-08-23-下午5:26
 */
public class AESUtil {

    public static final String CHAR_ENCODING = "UTF-8";
    public static final String AES_ALGORITHM = "AES";
//    public static final String AES_ALGORITHM = "AES/CBC/PKCS5Padding";


    public static void main(String[] args) {
        try {

            String data = encrypt("6121223701EFABB3D42062A93DB22D072430AB89ECA32E415DC0F0E8FD5F5F29F4BDF156F9B2EB30C94EEE6ABFFFB6485E1176B69EA62EDCC3941B654621284A687CD6B203285EC171D52E74460984EF1AB301B34A77EE6574030A4994FBB7FC", "f5puddau2es5uia5sbhyhkk2inc0pasdzk5v4b29xnsho7gqru993qouffwz");
            System.out.println(data);  //C749E16A7D2DAE81C7A53A87643691D337CAB948B043DB7AB9E9A35299827847

            String key = "f5puddau2es5uia5sbhyhkk2inc0pasdzk5v4b29xnsho7gqru993qouffwz";
            String decrypt = decrypt("6121223701EFABB3D42062A93DB22D072430AB89ECA32E415DC0F0E8FD5F5F29F4BDF156F9B2EB30C94EEE6ABFFFB6485E1176B69EA62EDCC3941B654621284A687CD6B203285EC171D52E74460984EF1AB301B34A77EE6574030A4994FBB7FC", key);

            System.out.println(decrypt);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * 加密
     *
     * @param data 待加密内容
     * @param key  加密秘钥
     * @return 十六进制字符串
     */
    public static String encrypt(String data, String key) {
        // CheckUtils.notEmpty(data, "data");
        // CheckUtils.notEmpty(key, "key");
        if (key.length() < 16) {
            throw new RuntimeException(
                    "Invalid AES key length (must be 16 bytes)");
        } else if (key.length() > 16) {
            key = key.substring(0, 16);
        }
        try {
            Cipher cipher = Cipher.getInstance(AES_ALGORITHM);// 创建密码器
            byte[] byteContent = data.getBytes(CHAR_ENCODING);
            cipher.init(Cipher.ENCRYPT_MODE, genKey(key));// 初始化
            byte[] result = cipher.doFinal(byteContent);
            return parseByte2HexStr(result); // 加密
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;

    }

    /**
     * 将二进制转换成16进制
     */
    private static String parseByte2HexStr(byte buf[]) {
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < buf.length; i++) {
            String hex = Integer.toHexString(buf[i] & 0xFF);
            if (hex.length() == 1) {
                hex = '0' + hex;
            }
            sb.append(hex.toUpperCase());
        }
        return sb.toString();
    }

    /**
     * 解密
     *
     * @param data 待解密内容(十六进制字符串)
     * @param key  加密秘钥
     */
    public static String decrypt(String data, String key) {
        // CheckUtils.notEmpty(data, "data");
        // CheckUtils.notEmpty(key, "key");
        if (key.length() < 16) {
            throw new RuntimeException(
                    "Invalid AES key length (must be 16 bytes)");
        } else if (key.length() > 16) {
            key = key.substring(0, 16);
        }
        try {
            byte[] decryptFrom = parseHexStr2Byte(data);
//            Cipher cipher = Cipher.getInstance(AES_ALGORITHM);// 创建密码器
            Cipher cipher = Cipher.getInstance("AES");// 创建密码器
            cipher.init(Cipher.DECRYPT_MODE, genKey(key));// 初始化
            //javax.crypto.BadPaddingException: Given final block not properly padded
            byte[] result = cipher.doFinal(decryptFrom);
            return new String(result, "utf-8"); // 加密
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 将16进制转换为二进制
     */
    private static byte[] parseHexStr2Byte(String hexStr) {
        if (hexStr.length() < 1)
            return null;
        byte[] result = new byte[hexStr.length() / 2];
        for (int i = 0; i < hexStr.length() / 2; i++) {
            int high = Integer.parseInt(hexStr.substring(i * 2, i * 2 + 1), 16);
            int low = Integer.parseInt(hexStr.substring(i * 2 + 1, i * 2 + 2),
                    16);
            result[i] = (byte) (high * 16 + low);
        }
        return result;
    }

    private static SecretKeySpec genKey(String key) {
        SecretKeySpec secretKey;
        try {
            secretKey = new SecretKeySpec(key.getBytes(CHAR_ENCODING),
                    AES_ALGORITHM);
            byte[] enCodeFormat = secretKey.getEncoded();
            SecretKeySpec seckey = new SecretKeySpec(enCodeFormat,
                    AES_ALGORITHM);
            return seckey;
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException("genKey fail!", e);
        }
    }
}
