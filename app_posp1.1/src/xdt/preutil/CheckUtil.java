package xdt.preutil;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.util.Arrays;
import org.apache.log4j.Logger;

public class CheckUtil {
	
	 private static   Logger logger=Logger.getLogger(CheckUtil.class);
	
	
	/**
	 * 计算数组的签名，传入参数为数组，算法如下：
	 * 1.对数组按KEY进行升序排序
	 * 2. 将数组拼接成字符串，以key=value&key=value的形式进行拼接
	 * 3. md5加密
	 * @param ary  生成签名的数组
	 * @return 返回生成签名
	 */
	public static String   makeSign(String[] ary ,String key){
		Arrays.sort(ary,String.CASE_INSENSITIVE_ORDER);   
		//对参数数组进行按key升序排列,然后拼接，最后调用5签名方法
		StringBuffer sb = new StringBuffer();
		for(int i = 0; i < ary.length; i++){ 
			//判断属性的值是否为空,值为空的不用参与拼接
			if(!"".equals(ary[i])&&ary[i]!=null){
				sb. append(ary[i]+"&");	
			}
				
		}
		String newStrTemp = sb.toString()+"key="+key.trim();
		//根据sign_method使用MD5签名
		String sign= md5Digest(newStrTemp); 
		logger.info("str待签名串: " + newStrTemp + ";签名串 sign=" + sign);
		return sign;
	}
	
	/**
	 * MD5值计算<p>
	 * MD5的算法在RFC1321 中定义:
	 * 在RFC 1321中，给出了Test suite用来检验你的实现是否正确：
	 * MD5 ("") = d41d8cd98f00b204e9800998ecf8427e
	 * MD5 ("a") = 0cc175b9c0f1b6a831c399e269772661
	 * MD5 ("abc") = 900150983cd24fb0d6963f7d28e17f72
	 * MD5 ("message digest") = f96b697d7cb7938d525a2f31aaf161d0
	 * MD5 ("abcdefghijklmnopqrstuvwxyz") = c3fcd3d76192e4007dfb496cca67e13b
	 *
	 * @param res 源字符串
	 * @return md5值
	 */
	public final static String md5Digest(String res) {
		if(res ==null||"".equals(res)){
			return null;
		}
		char hexDigits[] = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f' };
		byte[] strTemp;
		try {
			strTemp = res.getBytes("gbk");
		} catch (UnsupportedEncodingException e1) {
			return null;
		}
		try {
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
			String dd = new String(str);
			return dd;
		} catch (Exception e) {
			return null;
		}
	}

	/**
	 * 字符串编码 utf-8
	 * @param  str 待转码字符串
	 * @return result 转码后的字符串
	 */
	public static String encoder(String str){
		String result =null;
		try {
		    result = new String(str.toString().getBytes("utf-8"),"utf-8");
		} catch (UnsupportedEncodingException e) {
			
			e.printStackTrace();
		}
		return result;
	}
	
	
}
