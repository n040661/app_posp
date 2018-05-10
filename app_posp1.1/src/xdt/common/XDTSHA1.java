package xdt.common;

import java.io.IOException;
import java.io.InputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class XDTSHA1 {
	private static final int READBUF_SIZE = 1024;
	private static MessageDigest messageDigest = null;

	static {
		try {
			messageDigest = MessageDigest.getInstance("SHA1");
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
	}

	public static byte[] getHashByString(String data) throws Exception {
		if (data == null) {
			throw new Exception("MPCM010");
		}
		try {
			return getHashByBytes(data.getBytes("UTF-8"));
		} catch (Exception e) {
		}
		return "".getBytes();
	}

	public static synchronized byte[] getHashByBytes(byte[] data)
			throws Exception {
		if (data == null) {
			throw new Exception("MPCM011");
		}
		try {
			messageDigest.reset();
			messageDigest.update(data);
			return messageDigest.digest();
		} catch (Exception e) {
		}
		return "".getBytes();
	}

	public static byte[] getHashByInputStream(InputStream is) throws Exception,
			IOException {
		if (is == null) {
			throw new Exception("MPCM012");
		}

		try {
			byte[] buf = new byte[1024];
			messageDigest.reset();
			int size;
			while ((size = is.read(buf)) != -1) {
				messageDigest.update(buf, 0, size);
			}
			return messageDigest.digest();
		} catch (IOException e) {
			throw new IOException("MPCM013");
		}
	}
}
