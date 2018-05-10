package xdt.common.security;

import java.io.InputStream;

public class XDTHash {
	public static final int NONE = 0;
	public static final int MD5 = 1;
	public static final int SHA1 = 2;

	public static byte[] getHashByString(int type, String src) throws Exception {
		try {
			switch (type) {
			case 1:
				return XDTMD5.getHashByString(src);
			case 2:
				return XDTSHA1.getHashByString(src);
			}

			throw new Exception("MPCM020");
		} catch (Exception e) {
			throw new Exception(e.getMessage());
		}
	}

	public static byte[] getHashByInputStream(int type, InputStream src)
			throws Exception {
		try {
			switch (type) {
			case 1:
				return XDTMD5.getHashByInputStream(src);
			case 2:
				return XDTSHA1.getHashByInputStream(src);
			}

			throw new Exception("MPCM017");
		} catch (Exception e) {
			throw new Exception(e.getMessage());
		}
	}

	public static byte[] getHashByBytes(int type, byte[] src) throws Exception {
		try {
			switch (type) {
			case 1:
				return XDTMD5.getHashByBytes(src);
			case 2:
				return XDTSHA1.getHashByBytes(src);
			}

			throw new Exception("MPCM021");
		} catch (Exception e) {
			throw new Exception(e.getMessage());
		}
	}
}

