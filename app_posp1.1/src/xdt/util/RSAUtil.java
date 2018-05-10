package xdt.util;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

/**
 * RSA helper 2.0,it can encrypt and decrypt any length character string.<br>
 * use public key encrypt,use private decrypt.<br>
 * use private key sign,use public key verify.<br>
 * input data was encoded by BASE64.<br>
 * output data use BASE64 encode.<br>
 * 
 * @author never
 * 
 */
public class RSAUtil
{

    private static final Integer KEY_SIZE = 1024;

    private static final Integer MAX_ENCRYPT_SIZE = 117;

    private static final Integer MAX_DECRYPT_SIZE = 128;

    public static void generateKeyPair(String path) throws IOException
    {
        KeyPairGenerator keyPairGenerator = null;
        try
        {
            keyPairGenerator = KeyPairGenerator.getInstance("RSA");
        }
        catch (NoSuchAlgorithmException e)
        {
            e.printStackTrace();
        }

        keyPairGenerator.initialize(KEY_SIZE);

        KeyPair keyPair = keyPairGenerator.genKeyPair();
        PublicKey publicKey = keyPair.getPublic();
        PrivateKey privateKey = keyPair.getPrivate();

        byte[] publicKeyBytes = publicKey.getEncoded();
        byte[] privateKeyBytes = privateKey.getEncoded();

        String publicKeyString = base64Encode(publicKeyBytes);
        String privateKeyString = base64Encode(privateKeyBytes);

        byte[] publicKeyBase64Bytes = publicKeyString.getBytes();
        byte[] privateKeyBase64Bytes = privateKeyString.getBytes();

        File publicKeyFile = new File(path + "public.key");
        File privateKeyFile = new File(path + "private.key");

        FileOutputStream publicKeyFos = new FileOutputStream(publicKeyFile);
        FileOutputStream privateKeyFos = new FileOutputStream(privateKeyFile);

        publicKeyFos.write(publicKeyBase64Bytes);
        privateKeyFos.write(privateKeyBase64Bytes);

        publicKeyFos.flush();
        publicKeyFos.close();
        privateKeyFos.flush();
        privateKeyFos.close();

    }

    /**
     * encrypt<br>
     * result use BASE64 encode
     * 
     * @param publicKeyStr
     * @param needEncrypt
     * @return
     */
    public static byte[] encrypt(String publicKeyStr, byte[] needEncrypt)
    {
        byte[] encrypt = null;
        try
        {
            PublicKey publicKey = getPublicKey(publicKeyStr);
            encrypt = encrypt(publicKey, needEncrypt);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return encrypt;
    }

    /**
     * 
     * decrypt<br>
     * input was BASE64 encode
     * 
     * @param privateKeyStr
     * @param needDecrypt
     * @return
     */
    public static byte[] decrypt(String privateKeyStr, String needStr)
    {
        byte[] decrypt = null;
        
        try
        {
        	byte[] needDecrypt=base64Decode(needStr);
            PrivateKey privateKey = getPrivateKey(privateKeyStr);
            decrypt = decrypt(privateKey, needDecrypt);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return decrypt;
    }
    
    /**
     * 
     * decrypt<br>
     * input was BASE64 encode
     * 
     * @param privateKeyStr
     * @param needDecrypt
     * @return
     */
    public static byte[] decryptPub(String privateKeyStr, String needStr)
    {
        byte[] decrypt = null;
        
        try
        {
        	byte[] needDecrypt=base64Decode(needStr);
        	PublicKey publicKey = getPublicKey(privateKeyStr);
            decrypt = decrypt(publicKey, needDecrypt);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return decrypt;
    }

    /**
     * sign
     * 
     * @param privateKeyStr
     * @param needSign
     * @return
     */
    public static byte[] sign(String privateKeyStr, byte[] needSign)
    {
        byte[] encrypt = null;
        try
        {
            PrivateKey privateKey = getPrivateKey(privateKeyStr);
            encrypt = encrypt(privateKey, needSign);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return encrypt;
    }

    /**
     * verify
     * 
     * @param publicKeyStr
     * @param needVerify
     * @return
     */
    public static byte[] verify(String publicKeyStr, byte[] needVerify)
    {
        byte[] decrypt = null;
        try
        {
            PublicKey publicKey = getPublicKey(publicKeyStr);
            decrypt = decrypt(publicKey, needVerify);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return decrypt;
    }

    private static byte[] encrypt(Key key, byte[] needEncryptBytes)
            throws InvalidKeyException, IllegalBlockSizeException, BadPaddingException, NoSuchAlgorithmException,
            NoSuchPaddingException, IOException
    {
        if (needEncryptBytes == null)
        {
            return null;
        }

        Cipher cipher = Cipher.getInstance("RSA");

        // encrypt
        cipher.init(Cipher.ENCRYPT_MODE, key);

        ByteArrayInputStream iis = new ByteArrayInputStream(needEncryptBytes);
        ByteArrayOutputStream oos = new ByteArrayOutputStream();
        int restLength = needEncryptBytes.length;
        while (restLength > 0)
        {
            int readLength = restLength < MAX_ENCRYPT_SIZE ? restLength : MAX_ENCRYPT_SIZE;
            restLength = restLength - readLength;

            byte[] readBytes = new byte[readLength];
            iis.read(readBytes);

            byte[] append = cipher.doFinal(readBytes);
            oos.write(append);
        }
        byte[] encryptedBytes = oos.toByteArray();

        return encryptedBytes;
    }

    private static byte[] decrypt(Key key, byte[] needDecryptBytes) throws IOException, InvalidKeyException,
            IllegalBlockSizeException, BadPaddingException, NoSuchAlgorithmException, NoSuchPaddingException
    {
        if (needDecryptBytes == null)
        {
            return null;
        }
        Cipher cipher = Cipher.getInstance("RSA");

        // decrypt
        cipher.init(Cipher.DECRYPT_MODE, key);

        ByteArrayInputStream iis = new ByteArrayInputStream(needDecryptBytes);
        ByteArrayOutputStream oos = new ByteArrayOutputStream();
        int restLength = needDecryptBytes.length;
        while (restLength > 0)
        {
            int readLength = restLength < MAX_DECRYPT_SIZE ? restLength : MAX_DECRYPT_SIZE;
            restLength = restLength - readLength;

            byte[] readBytes = new byte[readLength];
            iis.read(readBytes);

            byte[] append = cipher.doFinal(readBytes);
            oos.write(append);
        }
        byte[] decryptedBytes = oos.toByteArray();

        return decryptedBytes;
    }

    private static PublicKey getPublicKey(String publicKeyStr)
            throws IOException, InvalidKeySpecException, NoSuchAlgorithmException
    {
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");

        byte[] publicKeyBytes = base64Decode(publicKeyStr);
        KeySpec keySpec = new X509EncodedKeySpec(publicKeyBytes);
        PublicKey publicKey = keyFactory.generatePublic(keySpec);

        return publicKey;
    }

    private static PrivateKey getPrivateKey(String privateKeyStr)
            throws IOException, InvalidKeySpecException, NoSuchAlgorithmException
    {
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");

        byte[] privateKeyBytes = base64Decode(privateKeyStr);
        KeySpec keySpec = new PKCS8EncodedKeySpec(privateKeyBytes);
        PrivateKey privateKey = keyFactory.generatePrivate(keySpec);

        return privateKey;
    }

    /**
     * BASE64 encode
     * 
     * @param needEncode
     * @return
     */
    public static String base64Encode(byte[] needEncode)
    {
        String encoded = null;
        if (needEncode != null)
        {
            encoded = new BASE64Encoder().encode(needEncode);
        }
        return encoded;
    }

    /**
     * BASE64 decode
     * 
     * @param needDecode
     * @return
     * @throws IOException
     */
    public static byte[] base64Decode(String needDecode) throws IOException
    {
        byte[] decoded = null;
        if (needDecode != null)
        {
            decoded = new BASE64Decoder().decodeBuffer(needDecode);
        }
        return decoded;
    }
    
    public static String readFile(String filePath, String charSet) throws Exception {
        FileInputStream fileInputStream = new FileInputStream(filePath);
        try {
            FileChannel fileChannel = fileInputStream.getChannel();
            ByteBuffer byteBuffer = ByteBuffer.allocate((int) fileChannel.size());
            fileChannel.read(byteBuffer);
            byteBuffer.flip();
            return new String(byteBuffer.array(), charSet);
        } finally {
            fileInputStream.close();
        }

    }

}
