package xdt.dto.hm;

import org.apache.commons.codec.binary.Base64;

public class Base64Method {

	public static String EncryptBase64(String a_strString) throws Exception {
		Base64 base64 = new Base64();
		String base64str = new String(base64.encode(a_strString
				.getBytes("utf-8")), "utf-8");
		base64str = base64str.replace("\n", "").replace("\r", "")
				.replace('+', '-').replace('/', '_');
		return base64str;
	}

	public static String EncryptBase64(byte[] bytes) throws Exception {
		Base64 base64 = new Base64();
		String base64str = new String(base64.encode(bytes), "utf-8");
		base64str = base64str.replace("\n", "").replace("\r", "")
				.replace('+', '-').replace('/', '_');
		return base64str;
	}

	public static String DecryptBase64(String a_strString) throws Exception {
		Base64 base64 = new Base64();
		byte[] bytes = base64.decode(a_strString.replace('-', '+')
				.replace('_', '/').getBytes("utf-8"));
		String str = new String(bytes, "utf-8");
		return str;
	}

	public static byte[] DecryptBase64ForByte(String a_strString)
			throws Exception {
		Base64 base64 = new Base64();
		byte[] bytes = base64.decode(a_strString.replace('-', '+')
				.replace('_', '/').getBytes("utf-8"));
		return bytes;
	}
}
