package xdt.quickpay.clearQuickPay.util;

import java.security.MessageDigest;

/*
 * Created on 2002-4-27
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */

/**
 * @author ANDYLIU
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
/************************************************
 MD5 绠楁硶鐨凧ava Bean
 @author:Topcat Tuppin
 Last Modified:10,Mar,2001
 *************************************************/

/*******************************************************************************
 * md5 绫诲疄鐜颁簡RSA Data Security, Inc.鍦ㄦ彁浜ょ粰IETF 鐨凴FC1321涓殑MD5 message-digest 绠楁硶銆�1锟�7
 ******************************************************************************/

public class MD5 {

	public static MD5 instance = new MD5();

	public static MD5 getInstance() {
		return instance;
	}

	public String getMD5ofStr(String s) {
		char hexDigits[] = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
				'a', 'b', 'c', 'd', 'e', 'f' };
		try {
			byte[] strTemp = s.getBytes("UTF-8");
			MessageDigest mdTemp = MessageDigest.getInstance("MD5");
			mdTemp.update(strTemp);
			byte[] md = mdTemp.digest();
			int j = md.length;
			char str[] = new char[j * 2];
			int k = 0;
			for (int i = 0; i < j; i++) {
				byte byte0 = md[i];
				str[k++] = hexDigits[byte0 >>> 4 & 0xf];
				str[k++] = hexDigits[byte0 & 0xf];
			}
			return new String(str);
		} catch (Exception e) {
			return null;
		}

	}
}