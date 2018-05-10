package xdt.dto.yf;

import java.io.FileInputStream;
import java.io.InputStream;
import java.security.Key;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Security;
import java.security.cert.Certificate;
import java.security.cert.CertificateFactory;
import java.util.Enumeration;

import javax.crypto.Cipher;

import org.apache.commons.codec.binary.Base64;

public class RSAComUtil {

    /**
     * 加密算法
     */
    public static final String RSA_ALGORITHM = "RSA/ECB/PKCS1Padding";

    /**
     * 签名算法
     */
    public static final String SIGN_ALGORITHMS = "SHA1WithRSA";

    static {
        Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());
    }




    public static String decrypt(PrivateKey priKey, String encryptedString, String charset) throws Exception {

        Key key = priKey;
        Cipher cipher = Cipher.getInstance(RSA_ALGORITHM);
        cipher.init(Cipher.DECRYPT_MODE, key);
        byte[] b1 = Base64.decodeBase64(encryptedString.getBytes());
        byte[] b = cipher.doFinal(b1);
        return new String(b);

    }


    public static String encrypt(PublicKey pubKey, String srcData, String charset) throws Exception {
        Key key = pubKey;
        Cipher cipher = Cipher.getInstance(RSA_ALGORITHM);
        cipher.init(Cipher.ENCRYPT_MODE, key);
        byte[] b = srcData.getBytes();
        byte[] b1 = cipher.doFinal(b);
        return new String(Base64.encodeBase64(b1),charset);
    }


    public static String doSignature(String content, PrivateKey priKey, String encode) throws Exception {
        java.security.Signature signature = java.security.Signature.getInstance(SIGN_ALGORITHMS);
        signature.initSign(priKey);
        signature.update(content.getBytes(encode));
        byte[] signed = signature.sign();
        return new String(Base64.encodeBase64(signed), encode);
    }

    public static boolean verifySignature(String content, String sign, PublicKey pubKey, String encode)
            throws Exception {
        java.security.Signature signature = java.security.Signature.getInstance(SIGN_ALGORITHMS);
        signature.initVerify(pubKey);
        signature.update(content.getBytes(encode));
        boolean bverify = signature.verify(Base64.decodeBase64(sign));
        return bverify;
    }

    /**
     * 通过PFX文件获得私钥
     *
     * @param //文件路径
     * @param //PFX密码
     * @return PrivateKey
     * @throws Exception
     */
    public static PrivateKey getPvkformPfx(String strPfx, String strPassword) throws Exception {
        PrivateKey prikey = null;
        char[] nPassword = null;
        if ((strPassword == null) || strPassword.trim().equals("")) {
            nPassword = null;
        } else {
            nPassword = strPassword.toCharArray();
        }
        KeyStore ks = getKsformPfx(strPfx, strPassword);
        String keyAlias = getAlsformPfx(strPfx, strPassword);
        prikey = (PrivateKey) ks.getKey(keyAlias, nPassword);
        return prikey;
    }
    
    /**
     * 通过PFX文件获得私钥
     *
     * @param //文件路径
     * @param //PFX密码
     * @return PrivateKey
     * @throws Exception
     */
    public static PrivateKey getPvkformPfxByInputStream(InputStream pfxStream, String strPassword) throws Exception {
        PrivateKey prikey = null;
        char[] nPassword = null;
        if ((strPassword == null) || strPassword.trim().equals("")) {
            nPassword = null;
        } else {
            nPassword = strPassword.toCharArray();
        }
        KeyStore ks = getKsformPfxByInputStream(pfxStream, strPassword);
        String keyAlias = getAlsformPfxByKeyStore(ks);
        prikey = (PrivateKey) ks.getKey(keyAlias, nPassword);
        return prikey;
    }

    /**
     * 通过PFX文件获得KEYSTORE
     *
     * @param //文件路径
     * @param //PFX密码
     * @return KeyStore
     */
    public static KeyStore getKsformPfx(String strPfx, String strPassword) throws Exception {
        FileInputStream fis = null;
        KeyStore ks = KeyStore.getInstance("PKCS12", "BC");
        fis = new FileInputStream(strPfx);
        char[] nPassword = null;
        if ((strPassword == null) || strPassword.trim().equals("")) {
            nPassword = null;
        } else {
            nPassword = strPassword.toCharArray();
        }
        
        ks.load(fis, nPassword);
        if (null != fis) {
            fis.close();
        }
        return ks;

    }
    
    /**
     * 通过PFX文件获得KEYSTORE
     *
     * @param //文件路径
     * @param //PFX密码
     * @return KeyStore
     */
    public static KeyStore getKsformPfxByInputStream(InputStream pfxStream, String strPassword) throws Exception {
        KeyStore ks = KeyStore.getInstance("PKCS12");
        char[] nPassword = null;
        if ((strPassword == null) || strPassword.trim().equals("")) {
            nPassword = null;
        } else {
            nPassword = strPassword.toCharArray();
        }
        ks.load(pfxStream, nPassword);
        return ks;

    }

    /**
     * 通过PFX文件获得别名
     *
     * @param //文件路径
     * @param //PFX密码
     * @return 别名
     */
    public static String getAlsformPfx(String strPfx, String strPassword) throws Exception {
        String keyAlias = null;
        KeyStore ks = getKsformPfx(strPfx, strPassword);
        Enumeration<String> enumas = ks.aliases();
        keyAlias = null;
        if (enumas.hasMoreElements()) {
            keyAlias = (String) enumas.nextElement();
        }
        return keyAlias;
    }
    
    /**
     * 通过PFX文件获得别名
     *
     * @param //文件路径
     * @param //PFX密码
     * @return 别名
     */
    public static String getAlsformPfxByKeyStore(KeyStore keystore) throws Exception {
        String keyAlias = null;
        Enumeration<String> enumas = keystore.aliases();
        keyAlias = null;
        if (enumas.hasMoreElements()) {
            keyAlias = (String) enumas.nextElement();
        }
        return keyAlias;
    }

    public static PublicKey getPubKeyFromCRT(String crtFileName) throws Exception {
        InputStream is = new FileInputStream(crtFileName);
        CertificateFactory cf = CertificateFactory.getInstance("x509");
        Certificate cerCert = cf.generateCertificate(is);
        return cerCert.getPublicKey();
    }
    
    public static PublicKey getPubKeyFromCRTInputStream(InputStream cerStream) throws Exception {
        CertificateFactory cf = CertificateFactory.getInstance("x509");
        Certificate cerCert = cf.generateCertificate(cerStream);
        return cerCert.getPublicKey();
    }

}
