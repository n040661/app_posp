package xdt.quickpay.hengfeng.util;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * @ClassName: MD5
 * @Description: MD5 加密
 * @author LiShiwen
 * @date 2016年6月22日 上午9:40:51
 *
 */
public class MD5 {

	public String md5s(String plainText) {
		String result="";
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
			result = buf.toString();
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();

		}
		return result;
	}

	public static void main(String agrs[]) {
		MD5 md51 = new MD5();
		String s=md51.md5s("MAP=1495782988886?1?6216912301931252?骆剑峰?福建省?泉州市?中国民生银行股份有限公司泉州分行?305397023008??1&BATCH_NO=B1495782988886&MERCHANT_ID=100350521598217a187a9d94eb1ad5ea426f34c85d8");// 加密md5s(String values)
		System.out.println(s);
	}
}