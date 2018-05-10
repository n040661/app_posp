package xdt.quickpay.qianlong.util;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;
import java.util.ResourceBundle;

import org.apache.log4j.Logger;


public class SdkUtil {
	
	private  final static Logger logger = Logger.getLogger(SdkUtil.class);
	
	private final static String FILE = "common";
	
	private static final char[] bcdLookup = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e','f' };

	/**
	 * 计算手续费
	 * @param amount
	 * @param feeRate
	 * @return
	 */
	public static Integer caclFee(int amount,double feeRate){
		double fee = amount*feeRate;
		BigDecimal bigDecimal = new BigDecimal(fee);
		int intFee;
		if(bigDecimal.intValue()<1)
		{
			intFee=1;
		}else
		{
		  intFee =bigDecimal.setScale(1, BigDecimal.ROUND_HALF_UP).intValue();
		}
		return intFee;
	}
	
	public static String fen2yuan(Integer amount){
		try {  
		     if(!amount.toString().matches("\\-?[0-9]+"));  
		 }catch (Exception e) {  
		     e.printStackTrace();  
		 }  
		 int flag = 0;  
		 String amString = amount.toString();      
		       if(amString.charAt(0)=='-'){  
		         flag = 1;      
		           amString = amString.substring(1);      
		       }      
		       StringBuffer result = new StringBuffer();      
		       if(amString.length()==1){      
		           result.append("0.0").append(amString);      
		       }else if(amString.length() == 2){      
		           result.append("0.").append(amString);      
		       }else{      
		            String intString = amString.substring(0,amString.length()-2);      
		            for(int i=1; i<=intString.length();i++){  
		                result.append(intString.substring(intString.length()-i,intString.length()-i+1));      
		            }      
		            result.reverse().append(".").append(amString.substring(amString.length()-2));      
		     }      
		     if(flag == 1){  
		      return "-"+result.toString();      
		     }else{      
		         return result.toString();      
		     }      
	}
	
	public static String getStringValue(String key){
		ResourceBundle resource = ResourceBundle.getBundle(FILE);
		return resource.getString(key);
	}
	
	public static Integer getIntValue(String key){
		return Integer.valueOf(getStringValue(key));
	}
	
	
	public static final String bytesToHexStr(byte[] bcd) {
		StringBuffer s = new StringBuffer(bcd.length * 2);

		for (int i = 0; i < bcd.length; i++) {
			s.append(bcdLookup[(bcd[i] >>> 4 & 0xF)]);
			s.append(bcdLookup[(bcd[i] & 0xF)]);
		}

		return s.toString();
	}

	public static final byte[] hexStrToBytes(String s) {
		byte[] bytes = new byte[s.length() / 2];

		for (int i = 0; i < bytes.length; i++) {
			bytes[i] = ((byte) Integer.parseInt(s.substring(2 * i, 2 * i + 2), 16));
		}

		return bytes;
	}
	
	public static String formatDate(Date date,String pattern){
		SimpleDateFormat sdf = new SimpleDateFormat(pattern);
		return sdf.format(date);
	}
	
	
	public static String substringByByte(String src, final int len) {
		if (src.length() < (len / 2))
			return src;

		try {
			byte[] bs = src.getBytes("GBK");

			//字符长度小于给定长度
			if (bs.length <= len) {
				return src;
			}

			//如果没有双字节字
			if (bs.length == src.length()) {
				return src.substring(0, len);
			}

			//处理双字节情况
			StringBuilder sb = new StringBuilder();
			int size = 0;
			int cnt = 0;
			for (Character ch : src.toCharArray()) {
				cnt = Character.toString(ch).getBytes("GBK").length;
				size += cnt;
				if (size <= len) {
					sb.append(ch);
				}
			}
			return sb.toString();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
	
	public static void main(String[] args) {
		System.out.println(caclFee(10000, 0.006d));
	}
	
	/**
	 * 获取随机字符串
	 * @return
	 */
	public static String getNonceStr() {
		String chars = "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";
		StringBuffer sb = new StringBuffer();
		Random random = new Random();
		for (int i = 0; i < 32; i++) {
			sb.append(chars.charAt(random.nextInt(chars.length())));
		}
		return sb.toString();
	}
	/**
	 * 生成机构订单号
	 * 
	 * @return 返回订单号
	 */
	public static String randomOrder() {

		SimpleDateFormat fmt = new SimpleDateFormat("yyyyMMddHHmmssSSS");

		StringBuffer sb = new StringBuffer();

		sb.append("QL");

		sb.append(fmt.format(new Date()));

		return sb.toString();
	}
}
