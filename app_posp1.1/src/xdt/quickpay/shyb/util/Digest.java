package xdt.quickpay.shyb.util;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

/**
 * @author sailfish
 * @create 2017-08-23-下午5:11
 */
public class Digest {

    private static final String ENCODING = "UTF-8";
    /**
     * 使用Hmac进行签名
     * @param aValue  加密明文
     * @return
     */
    public static String hmacSign(String aValue) {
        try {
            byte[] input = aValue.getBytes();
            MessageDigest md = MessageDigest.getInstance("MD5");
            System.out.println(md.digest(input));
            return toHex(md.digest(input));
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     *
     * @param input
     * @return
     */
    public static String toHex(byte input[]) {
        if (input == null)
            return null;
        StringBuffer output = new StringBuffer(input.length * 2);
        for (int i = 0; i < input.length; i++) {
            int current = input[i] & 0xff;
            if (current < 16)
                output.append("0");
            output.append(Integer.toString(current, 16));
        }

        return output.toString();
    }

    /**
     * 有hmackey
     * @param aValue
     * @param aKey
     * @return
     */
    public static String hmacSign(String aValue, String aKey) {
        return hmacSign(aValue, aKey, ENCODING);
    }

    /**
     * 根据加密字符串和hmackey来进行加密
     * @param aValue  加密明文
     * @param aKey  hmackey
     * @param encoding 编码，为UTF-8
     * @return
     */
    public static String hmacSign(String aValue, String aKey, String encoding) {
        byte k_ipad[] = new byte[64];
        byte k_opad[] = new byte[64];
        byte keyb[];
        byte value[];
        try {
            keyb = aKey.getBytes(encoding);
            value = aValue.getBytes(encoding);
        } catch (UnsupportedEncodingException e) {
            keyb = aKey.getBytes();
            value = aValue.getBytes();
        }
        Arrays.fill(k_ipad, keyb.length, 64, (byte) 54);
        Arrays.fill(k_opad, keyb.length, 64, (byte) 92);
        for (int i = 0; i < keyb.length; i++) {
            k_ipad[i] = (byte) (keyb[i] ^ 0x36);
            k_opad[i] = (byte) (keyb[i] ^ 0x5c);
        }

        MessageDigest md = null;
        try {
            md = MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return null;
        }
        md.update(k_ipad);
        md.update(value);
        byte dg[] = md.digest();
        md.reset();
        md.update(k_opad);
        md.update(dg, 0, 16);
        dg = md.digest();
        return toHex(dg);
    }


    public static void main(String[] args) {
        String key = "D10001674445100124348800.14511aaa18612563245http://112.74.176.237:8063/qpapi/openUnionpayResultForYiMingShenZhou.actionhttp://112.74.176.237:8063/qpapi/openUnionpayResultForYiMingShenZhou.actionhttp://112.74.176.237:8063/qpapi/openUnionpayResultForYiMingShenZhou.action";
        String s = hmacSign(key);
        System.out.println(s); //f41a4496b2aefb50217d446347214c52
    }
}
