package xdt.util;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import sun.net.www.content.text.plain;

public class MD5 {
	public String str;

	public void md5s(String plainText) {
		try {
			MessageDigest md = MessageDigest.getInstance("MD5");
			md.update(plainText.getBytes());
			byte b[] = md.digest();

			int i;

			StringBuffer buf = new StringBuffer("");
			for (int offset = 0; offset < b.length; offset++) {
				i = b[offset];
				if (i < 0)
					i += 256;
				if (i < 16)
					buf.append("0");
				buf.append(Integer.toHexString(i));
			}
			str = buf.toString();
			System.out.println("result: " + buf.toString());// 32位的加密
			System.out.println("result: " + buf.toString().substring(8, 24));// 16位的加密
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();

		}
	}

	public static void main(String agrs[]) throws Exception {
		
		String aa="asffiajfsad.aso_5995jgtfji";
		
		System.out.println("aaaa"+MD5.encryption(aa));
	}
	public static String encryption(String plainText) throws Exception {
		StringBuffer buf=null;
		try {
			System.out.println("文本内容:"+plainText);
			MessageDigest md = MessageDigest.getInstance("MD5");
			System.out.println(plainText.getBytes("utf-8"));
			md.update(plainText.getBytes("utf-8"));
			//String num=new String(plainText.getBytes("ISO-8859-1"),"utf-8");
			//md.update(num.getBytes("utf-8"));
			byte b[] = md.digest();

			int i;

			buf = new StringBuffer("");
			for (int offset = 0; offset < b.length; offset++) {
				i = b[offset];
				if (i < 0)
					i += 256;
				if (i < 16)
					buf.append("0");
				buf.append(Integer.toHexString(i));
			}
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();

		}
		return buf.toString();
	}
	
}
