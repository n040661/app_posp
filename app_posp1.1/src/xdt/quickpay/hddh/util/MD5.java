package xdt.quickpay.hddh.util;

import java.nio.charset.Charset;
import java.security.DigestException;
import java.security.MessageDigestSpi;

public final class MD5 extends MessageDigestSpi implements Cloneable {
	private byte digestBits[];

	private int state[];

	private long count;

	private byte buffer[];

	private int transformBuffer[];

	public MD5() {
		init();
	}

	@SuppressWarnings("unused")
	private MD5(MD5 md5) {
		this();
		state = (int[]) md5.state.clone();
		transformBuffer = (int[]) md5.transformBuffer.clone();
		buffer = (byte[]) md5.buffer.clone();
		digestBits = (byte[]) md5.digestBits.clone();
		count = md5.count;
	}

	private int FF(int i, int j, int k, int l, int i1, int j1, int k1) {
		i += (j & k | ~j & l) + i1 + k1;
		return (i << j1 | i >>> 32 - j1) + j;
	}

	private int GG(int i, int j, int k, int l, int i1, int j1, int k1) {
		i += (j & l | k & ~l) + i1 + k1;
		return (i << j1 | i >>> 32 - j1) + j;
	}

	private int HH(int i, int j, int k, int l, int i1, int j1, int k1) {
		i += (j ^ k ^ l) + i1 + k1;
		return (i << j1 | i >>> 32 - j1) + j;
	}

	private int II(int i, int j, int k, int l, int i1, int j1, int k1) {
		i += (k ^ (j | ~l)) + i1 + k1;
		return (i << j1 | i >>> 32 - j1) + j;
	}

	void transform(byte abyte0[], int i) {
		int ai[] = transformBuffer;
		int j = state[0];
		int k = state[1];
		int l = state[2];
		int i1 = state[3];
		for (int j1 = 0; j1 < 16; j1++) {
			ai[j1] = abyte0[j1 * 4 + i] & 0xff;
			for (int k1 = 1; k1 < 4; k1++) {
				ai[j1] += (abyte0[j1 * 4 + k1 + i] & 0xff) << k1 * 8;

			}
		}

		j = FF(j, k, l, i1, ai[0], 7, 0xd76aa478);
		i1 = FF(i1, j, k, l, ai[1], 12, 0xe8c7b756);
		l = FF(l, i1, j, k, ai[2], 17, 0x242070db);
		k = FF(k, l, i1, j, ai[3], 22, 0xc1bdceee);
		j = FF(j, k, l, i1, ai[4], 7, 0xf57c0faf);
		i1 = FF(i1, j, k, l, ai[5], 12, 0x4787c62a);
		l = FF(l, i1, j, k, ai[6], 17, 0xa8304613);
		k = FF(k, l, i1, j, ai[7], 22, 0xfd469501);
		j = FF(j, k, l, i1, ai[8], 7, 0x698098d8);
		i1 = FF(i1, j, k, l, ai[9], 12, 0x8b44f7af);
		l = FF(l, i1, j, k, ai[10], 17, -42063);
		k = FF(k, l, i1, j, ai[11], 22, 0x895cd7be);
		j = FF(j, k, l, i1, ai[12], 7, 0x6b901122);
		i1 = FF(i1, j, k, l, ai[13], 12, 0xfd987193);
		l = FF(l, i1, j, k, ai[14], 17, 0xa679438e);
		k = FF(k, l, i1, j, ai[15], 22, 0x49b40821);
		j = GG(j, k, l, i1, ai[1], 5, 0xf61e2562);
		i1 = GG(i1, j, k, l, ai[6], 9, 0xc040b340);
		l = GG(l, i1, j, k, ai[11], 14, 0x265e5a51);
		k = GG(k, l, i1, j, ai[0], 20, 0xe9b6c7aa);
		j = GG(j, k, l, i1, ai[5], 5, 0xd62f105d);
		i1 = GG(i1, j, k, l, ai[10], 9, 0x2441453);
		l = GG(l, i1, j, k, ai[15], 14, 0xd8a1e681);
		k = GG(k, l, i1, j, ai[4], 20, 0xe7d3fbc8);
		j = GG(j, k, l, i1, ai[9], 5, 0x21e1cde6);
		i1 = GG(i1, j, k, l, ai[14], 9, 0xc33707d6);
		l = GG(l, i1, j, k, ai[3], 14, 0xf4d50d87);
		k = GG(k, l, i1, j, ai[8], 20, 0x455a14ed);
		j = GG(j, k, l, i1, ai[13], 5, 0xa9e3e905);
		i1 = GG(i1, j, k, l, ai[2], 9, 0xfcefa3f8);
		l = GG(l, i1, j, k, ai[7], 14, 0x676f02d9);
		k = GG(k, l, i1, j, ai[12], 20, 0x8d2a4c8a);
		j = HH(j, k, l, i1, ai[5], 4, 0xfffa3942);
		i1 = HH(i1, j, k, l, ai[8], 11, 0x8771f681);
		l = HH(l, i1, j, k, ai[11], 16, 0x6d9d6122);
		k = HH(k, l, i1, j, ai[14], 23, 0xfde5380c);
		j = HH(j, k, l, i1, ai[1], 4, 0xa4beea44);
		i1 = HH(i1, j, k, l, ai[4], 11, 0x4bdecfa9);
		l = HH(l, i1, j, k, ai[7], 16, 0xf6bb4b60);
		k = HH(k, l, i1, j, ai[10], 23, 0xbebfbc70);
		j = HH(j, k, l, i1, ai[13], 4, 0x289b7ec6);
		i1 = HH(i1, j, k, l, ai[0], 11, 0xeaa127fa);
		l = HH(l, i1, j, k, ai[3], 16, 0xd4ef3085);
		k = HH(k, l, i1, j, ai[6], 23, 0x4881d05);
		j = HH(j, k, l, i1, ai[9], 4, 0xd9d4d039);
		i1 = HH(i1, j, k, l, ai[12], 11, 0xe6db99e5);
		l = HH(l, i1, j, k, ai[15], 16, 0x1fa27cf8);
		k = HH(k, l, i1, j, ai[2], 23, 0xc4ac5665);
		j = II(j, k, l, i1, ai[0], 6, 0xf4292244);
		i1 = II(i1, j, k, l, ai[7], 10, 0x432aff97);
		l = II(l, i1, j, k, ai[14], 15, 0xab9423a7);
		k = II(k, l, i1, j, ai[5], 21, 0xfc93a039);
		j = II(j, k, l, i1, ai[12], 6, 0x655b59c3);
		i1 = II(i1, j, k, l, ai[3], 10, 0x8f0ccc92);
		l = II(l, i1, j, k, ai[10], 15, 0xffeff47d);
		k = II(k, l, i1, j, ai[1], 21, 0x85845dd1);
		j = II(j, k, l, i1, ai[8], 6, 0x6fa87e4f);
		i1 = II(i1, j, k, l, ai[15], 10, 0xfe2ce6e0);
		l = II(l, i1, j, k, ai[6], 15, 0xa3014314);
		k = II(k, l, i1, j, ai[13], 21, 0x4e0811a1);
		j = II(j, k, l, i1, ai[4], 6, 0xf7537e82);
		i1 = II(i1, j, k, l, ai[11], 10, 0xbd3af235);
		l = II(l, i1, j, k, ai[2], 15, 0x2ad7d2bb);
		k = II(k, l, i1, j, ai[9], 21, 0xeb86d391);
		state[0] += j;
		state[1] += k;
		state[2] += l;
		state[3] += i1;
	}

	public void init() {
		state = new int[4];
		transformBuffer = new int[16];
		buffer = new byte[64];
		digestBits = new byte[16];
		count = 0L;
		state[0] = 0x67452301;
		state[1] = 0xefcdab89;
		state[2] = 0x98badcfe;
		state[3] = 0x10325476;
		for (int i = 0; i < digestBits.length; i++) {
			digestBits[i] = 0;

		}
	}

	public void engineReset() {
		init();
	}

	protected int engineGetDigestLength() {
		return 16;
	}

	public synchronized void engineUpdate(byte byte0) {
		int i = (int) (count >>> 3 & 63L);
		count += 8L;
		buffer[i] = byte0;
		if (i >= 63) {
			transform(buffer, 0);
		}
	}

	public synchronized void engineUpdate(byte abyte0[]) {
		engineUpdate(abyte0, 0, abyte0.length);
	}

	public synchronized void engineUpdate(byte abyte0[], int i, int j) {
		int k = i;
		while (j > 0) {
			int l = (int) (count >>> 3 & 63L);
			if (l == 0 && j > 64) {
				count += 512L;
				transform(abyte0, k);
				j -= 64;
				k += 64;
			} else {
				count += 8L;
				buffer[l] = abyte0[k];
				if (l >= 63) {
					transform(buffer, 0);
				}
				k++;
				j--;
			}
		}
	}

	private void finish() {
		byte abyte0[] = new byte[8];
		for (int i = 0; i < 8; i++) {
			abyte0[i] = (byte) (int) (count >>> i * 8 & 255L);

		}
		int k = (int) (count >> 3) & 0x3f;
		int l = k >= 56 ? 120 - k : 56 - k;
		byte abyte1[] = new byte[l];
		abyte1[0] = -128;
		engineUpdate(abyte1, 0, abyte1.length);
		engineUpdate(abyte0, 0, abyte0.length);
		for (int j = 0; j < 4; j++) {
			for (int i1 = 0; i1 < 4; i1++) {
				digestBits[j * 4 + i1] = (byte) (state[j] >>> i1 * 8 & 0xff);

			}
		}

	}

	public byte[] engineDigest() {
		finish();
		byte abyte0[] = new byte[16];
		System.arraycopy(digestBits, 0, abyte0, 0, 16);
		init();
		return abyte0;
	}

	public int engineDigest(byte abyte0[], int i, int j) throws DigestException {
		finish();
		if (j < 16) {
			throw new DigestException("partial digests not returned");
		}
		if (abyte0.length - i < 16) {
			throw new DigestException(
					"insufficient space in the output buffer to store the digest");
		} else {
			System.arraycopy(digestBits, 0, abyte0, i, 16);
			init();
			return 16;
		}
	}

	public Object clone() {
		MD5 md5 = null;
		try {
			md5 = (MD5) super.clone();
			md5.state = (int[]) state.clone();
			md5.transformBuffer = (int[]) transformBuffer.clone();
			md5.buffer = (byte[]) buffer.clone();
			md5.digestBits = (byte[]) digestBits.clone();
			md5.count = count;
			return md5;
		} catch (CloneNotSupportedException clonenotsupportedexception) {
			return md5;
		}
	}

	public static String printByteArray(byte[] b) {
		String hexString = "";
		for (int i = 0; i < b.length; i++) {
			hexString += byte2Hex(b[i]);
		}
		return hexString;
	}

	public static String byte2Hex(byte ib) {
		char[] Digit = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A',
				'B', 'C', 'D', 'E', 'F' };
		char[] ob = new char[2];
		ob[0] = Digit[(ib >>> 4) & 0X0F];
		ob[1] = Digit[ib & 0X0F];
		String s = new String(ob);
		return s;
	}

	@SuppressWarnings("static-access")
	public static String md5(String str) {
		MD5 md5 = new MD5();
		md5.engineUpdate(str.getBytes());
		return md5.printByteArray(md5.engineDigest()).toLowerCase();
	}

	public static String md5(String str,Charset charset) {
		MD5 md5 = new MD5();
		md5.engineUpdate(str.getBytes(charset));
		return md5.printByteArray(md5.engineDigest()).toLowerCase();
	}


	public static void main(String[] arg) throws Exception {
		System.out.println( "sss" );
	}
}
