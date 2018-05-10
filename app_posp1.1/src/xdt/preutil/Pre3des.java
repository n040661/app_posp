package xdt.preutil;

import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.UUID;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;

public class Pre3des {
//	static String modeDes = "DESede/ECB/NoPadding";
	static String modeDes = "DES/ECB/PKCS5Padding";
	static String modeDesKey="DES";
	
	/**
	 * 获取3des密码
	 */
	public static byte[] getDesKey() {
		UUID uuid = UUID.randomUUID();
		String desKey = uuid.toString();
		desKey = desKey.replace("-", "");
		if (desKey.length()>=8) {
			desKey=desKey.substring(0, 8);
		}else {
			desKey="fjiejcid";
		}
		return desKey.getBytes();
	}
	
	/**
	 * 3des加密
	 * 
	 * @param ss
	 *            要加密的字符串
	 * @param deskey
	 * @return
	 * @throws NoSuchAlgorithmException
	 * @throws NoSuchPaddingException
	 * @throws InvalidKeyException
	 * @throws IllegalBlockSizeException
	 * @throws BadPaddingException
	 * @throws UnsupportedEncodingException
	 */
	public static byte[] DesEncode(byte[] ss, byte[] deskey)
			throws NoSuchAlgorithmException, NoSuchPaddingException,
			InvalidKeyException, IllegalBlockSizeException, BadPaddingException {
		//负责完成加密或解密工作
		Cipher c = Cipher.getInstance(modeDes);
		//格式化密钥
		SecretKeySpec k = new SecretKeySpec(deskey, modeDesKey);
		// 根据密钥，对Cipher对象进行初始化,ENCRYPT_MODE表示加密模式
		c.init(Cipher.ENCRYPT_MODE, k);
		// 加密，结果保存进enc
		byte[] enc = c.doFinal(ss);
		return enc;
	}

	/**
	 * 3des解密
	 * 
	 * @param ss
	 *            要解密的数据
	 * @param deskey
	 *            生成密钥用的数组
	 * @return
	 * @throws NoSuchAlgorithmException
	 * @throws NoSuchPaddingException
	 * @throws InvalidKeyException
	 * @throws IllegalBlockSizeException
	 * @throws BadPaddingException
	 */
	public static byte[] DesDecode(byte[] ss, byte[] deskey)
		throws NoSuchAlgorithmException, NoSuchPaddingException,
		InvalidKeyException, IllegalBlockSizeException, BadPaddingException {
		//负责完成加密或解密工作
		Cipher c = Cipher.getInstance(modeDes);
		//格式化密钥
		SecretKeySpec k = new SecretKeySpec(deskey, modeDesKey);
		// 根据密钥，对Cipher对象进行初始化,ENCRYPT_MODE表示加密模式
		c.init(Cipher.DECRYPT_MODE, k);
		// 解密，结果保存进dec
		byte[] dec = c.doFinal(ss);
		return dec;
	}
}
