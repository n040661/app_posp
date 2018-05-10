package xdt.dto.hlb;

import org.apache.commons.lang.ArrayUtils;
import sun.misc.BASE64Encoder;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.*;
import java.security.cert.Certificate;
import java.security.cert.CertificateFactory;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Arrays;
import java.util.Enumeration;

/**
 * 私钥签名，私钥签名（只有私钥能签），公钥验证签名，确认发起人是私钥持有人
 * 公钥加密，公钥加密只有私钥能解密
 *
 * @author datou
 */
public class RSA {

    /**
     * String to hold name of the encryption padding.
     */
    public static final String NOPADDING = "RSA/NONE/NoPadding";

    public static final String RSANONEPKCS1PADDING = "RSA/NONE/PKCS1Padding";

    public static final String RSAECBPKCS1PADDING = "RSA/ECB/PKCS1Padding";

    public static final String PROVIDER = "BC";

    static {
        Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());
    }

    /**
     * 验证签名
     *
     * @param data     数据
     * @param sign     签名
     * @param publicKey 公钥
     * @return
     */
    public static boolean verifySign(byte[] data, byte[] sign,
                                     PublicKey publicKey) {
        try {
            Signature signature = Signature
                    .getInstance("MD5withRSA");
            signature.initVerify(publicKey);
            signature.update(data);
            boolean result = signature.verify(sign);
            return result;
        } catch (Exception e) {

            throw new RuntimeException("verifySign fail!", e);
        }
    }

    /**
     * 验证签名
     *
     * @param data     数据
     * @param sign     签名
     * @param pubicKey 公钥
     * @return
     */
    public static boolean verifySign(String data, String sign,
                                     PublicKey pubicKey) {
        try {
            byte[] dataByte = data
                    .getBytes("UTF-8");
            byte[] signByte = Base64.decode(sign
                    .getBytes("UTF-8"));
            return verifySign(dataByte, signByte, pubicKey);
        } catch (UnsupportedEncodingException e) {

            throw new RuntimeException("verifySign fail! data[" + data + "] sign[" + sign + "]", e);
        }
    }

    /**
     * 签名
     *
     * @param data
     * @param key
     * @return
     */
    public static byte[] sign(byte[] data, PrivateKey key) {
        try {
            Signature signature = Signature
                    .getInstance("MD5withRSA");
            signature.initSign(key);
            signature.update(data);
            return signature.sign();
        } catch (Exception e) {
            throw new RuntimeException("sign fail!", e);
        }
    }

    /**
     * 签名
     *
     * @param data
     * @param key
     * @return
     */
    public static String sign(String data, PrivateKey key) {
        try {
            byte[] dataByte = data.getBytes("UTF-8");
            return new String(Base64.encode(sign(dataByte, key)));
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException("sign fail!", e);
        }
    }

    /**
     * 加密
     *
     * @param data
     * @param key
     * @return
     */
    public static byte[] encrypt(byte[] data, Key key, String padding) {
        try {
            final Cipher cipher = Cipher.getInstance(padding, PROVIDER);
            cipher.init(Cipher.ENCRYPT_MODE, key);
            return cipher.doFinal(data);
        } catch (Exception e) {

            throw new RuntimeException("encrypt fail!", e);
        }
    }

    /**
     * 加密
     *
     * @param data
     * @param key
     * @return
     */
    public static String encryptToBase64(String data, Key key, String padding) {
        try {
            return new String(Base64.encode(encrypt(
                    data.getBytes("UTF-8"),
                    key, padding)));
        } catch (Exception e) {
            throw new RuntimeException("encrypt fail!", e);
        }
    }

    /**
     * 解密
     *
     * @param data
     * @param key
     * @return
     */
    public static byte[] decrypt(byte[] data, Key key, String padding) {
        try {
            final Cipher cipher = Cipher.getInstance(padding, PROVIDER);
            cipher.init(Cipher.DECRYPT_MODE, key);
            return cipher.doFinal(data);
        } catch (Exception e) {
            throw new RuntimeException("encrypt fail!", e);
        }
    }

    /**
     * 解密
     *
     * @param data
     * @param key
     * @return
     */
    public static String decryptFromBase64(String data, Key key, String padding) {
        try {
            return new String(decrypt(Base64.decode(data.getBytes()), key, padding),
                    "UTF-8");
        } catch (Exception e) {
            throw new RuntimeException("encrypt fail!", e);
        }
    }

    public static void createKeyPairs(int size) throws Exception {
        // create the keys
        KeyPairGenerator generator = KeyPairGenerator.getInstance("RSA", PROVIDER);
        generator.initialize(size, new SecureRandom());
        KeyPair pair = generator.generateKeyPair();
        PublicKey pubKey = pair.getPublic();
        PrivateKey privKey = pair.getPrivate();
        byte[] pk = pubKey.getEncoded();
        byte[] privk = privKey.getEncoded();
        String strpk = new String(Base64.encodeBase64(pk));
        String strprivk = new String(Base64.encodeBase64(privk));
        System.out.println("公钥:" + Arrays.toString(pk));
        System.out.println("私钥:" + Arrays.toString(privk));
        System.out.println("公钥Base64编码:" + strpk);
        System.out.println("私钥Base64编码:" + strprivk);
    }

    public static PublicKey getPublicKey(String base64EncodePublicKey) throws Exception {
        KeyFactory keyf = KeyFactory.getInstance("RSA", PROVIDER);
        X509EncodedKeySpec pubX509 = new X509EncodedKeySpec(Base64.decodeBase64(base64EncodePublicKey.getBytes()));
        PublicKey pubkey = keyf.generatePublic(pubX509);
        return pubkey;
    }

    public static PrivateKey getPrivateKey(String base64EncodePrivateKey) throws Exception {
        KeyFactory keyf = KeyFactory.getInstance("RSA", PROVIDER);
        PKCS8EncodedKeySpec priPKCS8 = new PKCS8EncodedKeySpec(Base64.decodeBase64(base64EncodePrivateKey.getBytes()));
        PrivateKey privkey = keyf.generatePrivate(priPKCS8);
        return privkey;
    }


    public static byte[] encode(String encodeString, Key key, String padding) throws Exception {
        final Cipher cipher = Cipher.getInstance(padding, PROVIDER);
        cipher.init(Cipher.ENCRYPT_MODE, key);
        byte[] bytes = encodeString.getBytes("UTF-8");
        byte[] encodedByteArray = new byte[]{};
        for (int i = 0; i < bytes.length; i += 117) {
            byte[] subarray = ArrayUtils.subarray(bytes, i, i + 117);
            byte[] doFinal = cipher.doFinal(subarray);
            encodedByteArray = ArrayUtils.addAll(encodedByteArray, doFinal);
        }
        return encodedByteArray;
    }

    /**
     * 加密
     *
     * @param data
     * @param key
     * @return
     */
    public static String encodeToBase64(String data, Key key, String padding) {
        try {
            return new String(Base64.encode(encode(data,
                    key, padding)));
        } catch (Exception e) {
            throw new RuntimeException("encrypt fail!", e);
        }
    }

    public static String decode(byte[] decodeByteArray, Key key, String padding) throws NoSuchAlgorithmException, InvalidKeySpecException, NoSuchPaddingException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException, IOException, NoSuchProviderException {
        final Cipher cipher = Cipher.getInstance(padding, PROVIDER);
        cipher.init(Cipher.DECRYPT_MODE, key);
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < decodeByteArray.length; i += 128) {
            byte[] doFinal = cipher.doFinal(ArrayUtils.subarray(decodeByteArray, i, i + 128));
            sb.append(new String(doFinal));
        }
        return sb.toString();
    }

    /**
     * 解密
     *
     * @param data
     * @param key
     * @return
     */
    public static String decodeFromBase64(String data, Key key, String padding) {
        try {
            return new String(decode(Base64.decode(data.getBytes()), key, padding).getBytes(),
                    "UTF-8");
        } catch (Exception e) {
            throw new RuntimeException("encrypt fail!", e);
        }
    }

    /**
     * 得到密钥字符串（经过base64编码）
     *
     * @return
     */
    public static String getKeyString(Key key) throws Exception {
        byte[] keyBytes = key.getEncoded();
        String s = (new BASE64Encoder()).encode(keyBytes);
        return s;
    }

    public static String getKeyStringByCer(String path) throws Exception {
        CertificateFactory cff = CertificateFactory.getInstance("X.509");
        FileInputStream fis1 = new FileInputStream(path);
        Certificate cf = cff.generateCertificate(fis1);
        PublicKey pk1 = cf.getPublicKey();
        String key = getKeyString(pk1);
        System.out.println("public:\n" + key);
        return key;
    }

    public static String getKeyStringByPfx(String strPfx, String strPassword) {
        try {
            KeyStore ks = KeyStore.getInstance("PKCS12");
            FileInputStream fis = new FileInputStream(strPfx);
            // If the keystore password is empty(""), then we have to set
            // to null, otherwise it won't work!!!
            char[] nPassword = null;
            if ((strPassword == null) || strPassword.trim().equals("")) {
                nPassword = null;
            } else {
                nPassword = strPassword.toCharArray();
            }
            ks.load(fis, nPassword);
            fis.close();
            System.out.println("keystore type=" + ks.getType());
            // Now we loop all the aliases, we need the alias to get keys.
            // It seems that this value is the "Friendly name" field in the
            // detals tab <-- Certificate window <-- view <-- Certificate
            // Button <-- Content tab <-- Internet Options <-- Tools menu
            // In MS IE 6.
            Enumeration enumas = ks.aliases();
            String keyAlias = null;
            if (enumas.hasMoreElements())// we are readin just one certificate.
            {
                keyAlias = (String) enumas.nextElement();
                System.out.println("alias=[" + keyAlias + "]");
            }
            // Now once we know the alias, we could get the keys.
            System.out.println("is key entry=" + ks.isKeyEntry(keyAlias));
            PrivateKey prikey = (PrivateKey) ks.getKey(keyAlias, nPassword);
            Certificate cert = ks.getCertificate(keyAlias);
            PublicKey pubkey = cert.getPublicKey();

            String basePrikey = RSA.getKeyString(prikey);
            System.out.println("cert class = " + cert.getClass().getName());
            System.out.println("cert = " + cert);
            System.out.println("public key = " + pubkey);
            System.out.println("private key = " + prikey);
            System.out.println("pubkey key = " + RSA.getKeyString(pubkey));
            System.out.println("prikey key = " + RSA.getKeyString(prikey));
            System.out.println("pubkey key length = " + RSA.getKeyString(pubkey).length());
            System.out.println("prikey key length = " + RSA.getKeyString(prikey).length());
            return basePrikey;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 512位PKCS#8
     *
     * @param args
     * @throws Exception
     */
    public static void main(String[] args) throws Exception {
        //getKeyStringByCer("/usr/local/cjpayProductServerCertExpiryDate20170509.cer");
        //String privatekey = "MIIEvgIBADANBgkqhkiG9w0BAQEFAASCBKgwggSkAgEAAoIBAQDYNb+tFTFM+V4kzeeubf5iUPgKl50kxXuEJ57qrFJ0iC0eHpOlw3axqD+zjnWQ+/R1yWUl77SOqgvowmtbKz1qLgXyB57G2lAveL48tp0yyd+tfQMQkW1duT27lGp1bAj/lhwVulvUSzJPqlc4mBt0F+/jEGQ4VZRmR0NxhMbutj7pu61rawZDqS/FSrcnt872uHaUsF+b7c2JcOl7qyB3ySLcMjkJ6rAonYohMCfy0ZT9KruRJQMXtVAbuDLxS+BASDYeE1aOqbg+KPQRfCPdM6t0FX4Y0o6y6cwhd6GQ2OcM6o4lUoKIV6ggeZWG/CHwm85mQ5dbhb7JhMj80CMzAgMBAAECggEBANRC0E7IQ4JiPr67NvzUF63F9/3OIwR6rGxtyWJykvC80C6FrkZKwzhfCUrqTRltge4xLhA0aHq3DsIQPP1gUgbv7/5Q5NwscBRq4bkRPvshnWrhFCUr4MbmmWiSqIFm8t6ZLDFYp9afjGERxldVXX54EZZF/V4hBnPy+o9z3ylyC8skTpX91xTlx523UCGRPWG9cLID5OHrhnWfRybeO7ekGzTFSD7pMIZvQ24mrC6eTJZK6QmAMTB6UxEfyQ+scETO5RhwKOl1KngfQ7rjNSr8tfPcj7u5jdu9VbWYgrg/SKzQTkZCzoLeubwVmTdhMvKmN0KZx771sqVfCa21maECgYEA8+nUHAhJpP/iDJUyh0zksBRBlakI4/UR4/LoVzKQ2eQybXmKDFPSGRXse/YQUvFNirtWHYtxruRWmKxUOlbI8ByWo2sSEZZkQ/5/RLLa7GeH46B7iw19fE8kVv6m9uXHSP+DZqPZ+oyMGI1h9CFO9hnGuzR6ZMBtWBuPwAIX/z0CgYEA4ux8wKGMkPPUxmMLSb7nTjy1q9g8K2YdBWNDTed82H9wwTkVdkWRcDmS64PQEglaK3BCBdI5nN4xQ4p/xEaVVicKChl+1kFpErrHIRO0PEHj/X2+UVurGr19ayEimSBbkqP9s8Ilm867zVjgUbBPb5eEGwF2ZWvBzHp62x+90y8CgYB6gmakEdGTi2ZpdYKkjI2Mlw/98KHjhVMQEBB8w4wXtNNTQymAWZV8PhPCi2vVjReqZ7+wJTrbYhrQojXH+5D/cQyEViIELWp88FXSFpd5B+MsBI4o4IS3rDSPcWZBOlYnJxdDtWalvnQbXN/nM9eqKnYzzv7unewNxgIkqaiN7QKBgA3H5tub8/lplWZm5WyZF1r8/NtuylioXuLQ4Gl+wWo0cxiI6vC/0NFL7cw3uQ8fWkaBDsFjvvPz2nILmy7ESpVs0YNDgRxp5IOqrCUqGSv/pGTkmgY+1ulM0K0M2hkKigUhyGp+Q+Jub6FM4HKVjn1sai1prVMDdEWCuqtMBi8pAoGBAMAZp5m+dUkgG9nUk4Hek+zGhBTUe56NdgTRRX1m8LofW4G4wIG34nLNxEvGmklQjmgGfg1s/XhBQ+BrNUhD4fP+d5Q78qdNqQGzBamXHgjw3fGwUZJJqnatdTeQlf2hSwOSps6cwECezdB0jdxN8oiWCYFc22wP076jHOiMgU+J";
        String publickey = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQDonLTXehZeFwmsg+zs8AHrsgGPkGXLS7Lhf2RMOHmAHC/MsvnrfHfu0GD0FcX7NuXKBJK7KsA0MWlEkHezuoRkZS2xZ79qQzImSVE3POO4g1ZVTsW2Bl9LNN2mkTAsum6ik/vodYzftbS0tT73SvgHk54mAm8cWdiQOEVmdX0yhQIDAQAB";
//		System.err.println("-----------"+getKeyStringByCer("/usr/local/businessgate.cer"));
//		String padding = RSAECBPKCS1PADDING;
        //createKeyPairs(1024);
//		String publickey = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCN7fVkqM5hzcPg+xTKEOCsMQ2WQyqgaVbMd3atYN9zngW/biNqkt9vo5EWvoZ/WEByXPvanZRKWMPRFU39fxmtAxdl5TBoh+f4utd8TqdQ2c/uMRUzOecNXlvEZZNXN3Pgu/LLr1ss/H2ma+Lwkoq1WBkWzpY5BJqqhBKpQfWPRwIDAQAB";
//		System.out.println(publickey.length());
//		String privatekey = "MIICdQIBADANBgkqhkiG9w0BAQEFAASCAl8wggJbAgEAAoGBAI3t9WSozmHNw+D7FMoQ4KwxDZZDKqBpVsx3dq1g33OeBb9uI2qS32+jkRa+hn9YQHJc+9qdlEpYw9EVTf1/Ga0DF2XlMGiH5/i613xOp1DZz+4xFTM55w1eW8Rlk1c3c+C78suvWyz8faZr4vCSirVYGRbOljkEmqqEEqlB9Y9HAgMBAAECgYBREHDwpVn7ksMmZUJih3FL6FiSUpquNHxXHIS0UnhOWRt/UzD3Vhw4b3wXOYsogQeOSn2vSAHC1SBC86AwAcBvqXmKrmuCPoZX0MQkO4tVqfONgE4sq1jbuWHobwP14iZTGqdY69a13Vx6C4PFaOElOkzJbyDA4YkjTBvuR5IvAQJBAPCzgT4uhA73WMsntlI/YhKL7NteA/epUPJbL2UnY6Tf3vqSsvskU9ulgmJLUt1GBG7NFNYXQ6a6I/3GqOPdz7ECQQCW81Iv6Rpns5Ovz1zwpU/bZDwR2Mx+ARDv/DFPH3wJ4J+KJ1XLzEGn9+DLujAkSjrfb1OTNUK2723QOAPYX0R3AkAZwrxkUUok5+gC5h8nMk1mur8Aw1EdYmudZJDv/IU3khYlLyokosCu00nfBzy5HdM/oIdIWnkNEQLlGCvfJ1AhAkBcW2+zxKnVPThlXzh8PIkJsOBYAw/LIJfeXa1FvfvEh1zVyI9j7AtX2//8ZmTCL3Cp+JFoBjRVfVTxCvS/VDqtAkBSU/x90zjwVYDTYPUxyyLz9jYLVu3vHavaTRUTmMn4D4qHSmq3WahYOrrOpI80+Cwrf+bc08hKB+DgNBHpnE0E";
//		System.err.println(publickey.length());
//		System.err.println(privatekey.length());
        PublicKey publicKey = RSA.getPublicKey(publickey);
        //PrivateKey privateKey = RSA.getPrivateKey(privatekey);
        System.out.println("1");
//		String sign = sign("1234",privateKey);
//		System.out.println(sign);
        System.out.println(verifySign("#TransferSingle#M800029658#20161108105547054321#20161108105547054321#10#ICBC#6212261001064813709#test#B2C#PAYER#true", "RSRECZmnN4f1giR8EnUSaUY9dRf0RAVLWFAoL+IDenMd0QXU9YckvfrS5Xg/EhBPt8W11ee5cqmcIsjAshsV18gF4RcuHint4woMOBiA32tKCE7e4rkjD5/R2x4MRTUv+2eLSyjzOh4nNvFY184WcN7tJdo4tj0o6QVkUJRFqfo=", publicKey));
//		String tmp = "104AE7ACA503EF4347DEA3EEFB5DB979747815526A0DD54168BB4B7197F776064CD6724542AFBFDA7B4B6F849B6130407E3709C280AF736CAF324D1D52010BD852BBCC1F5B9C668C1533BB747C1258761B80CA74AB0F2BA7E518A8A4BC7F407B531E7B8EFC877385503D4BE888DF5A113B1C6DC27EB94EFA16E112F23046B4ECE87B30EA62463A9CB0D94805A36D6239B9E587F95FF3E8F540132FE5FA590F52FEC9EC4FBB35C2108CA06F1F51AD6680199B51F85641EBDE5A7DC6127ED12237A86B5E025E0648217B7B8426F23348AF47DEA3EEFB5DB979AE85F4D79480DA150EE81C08064A010DBB900498D56FD7B432046000D768C2297CC3DD0AD15040DFD4EFC9FFCD7728984A4FADD66C8023FC0CF3780EA41D4080A8BC59E35FACBC20598B60BD0DE0C5414F0F380AECE38A98707A545C29D0D88E49A080904EF26202871D4BC06A49F1CEA977C5BC4DBB02786892E78AC3851EA233EE8D325757D64204EB1F637D5A609BC38E3BA2442BEFDE4DB0FD720BBC2F44BE9E0451A4425007C3706E9BF85A1F3F498DC6FFDD3EC2126C7F0EC61787ECF427A085AC5E3AD59E7E3C204BE6B0E89B31131043C210BC0947B51CE07CB37714048DDE18516F7D293969D714523D1F8096E59114409341897AB95064FA860C1E97A316D3D6CCADC2B8FA844541C58F457BCFEAFA022DA453BBB3142AF113CDCD125D1DF096E77244B47C150F3EE5BCDE5931C58C38D9C53C2222C08B0EA5C136EA1A4F2E512FF283430901D4F4212E3EA86B5E025E064821D4A1AE4629EEDDAD6000CD2CBAAE5B630712D23618975E838F620A31B788672896CEAF520A39CAEF37986D3F3CB1352DFDB75D2AAEF197D9F0142CF0FDE71082048F6024013895F461EB07B8A4C4897F7C32F38140BC42309D4E41D2F8F8DA277540506387A01118914B7C6D87ED185BC23CCC73C83B58C477579A1ADB3832678F2611BC14D24876";
//		String s3 = RSA.decodeFromBase64(tmp, publicKey, padding);
//		System.out.println(s3+"=====s3=====");
//		System.out.println(s3.length()+"=====s3=====");
//		String plain = "123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567";
//		System.out.println(plain.getBytes().length);
//		byte[] encryptByte = RSA.encode(plain, privateKey,padding);
//		System.err.println(encryptByte.length+"=====encryptByte=====");
//		byte[] sign = RSA.sign(encryptByte, privateKey);
//		System.out.println(RSA.verifySign(encryptByte,sign,publicKey));
//		String s2 = RSA.decode(encryptByte, publicKey,padding);
//		System.out.println(s2+"=====s2=====");
//		System.out.println(s2.length()+"=====s2=====");
//		String encryptString = RSA.encodeToBase64(plain, privateKey, padding);
//		System.err.println(encryptString.length()+"=====encryptString====="+encryptString);


    }
}
