package xdt.util;

import org.apache.commons.lang.StringUtils;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Calendar;


/**
 * 公共方法
 */
public class UtilMethod {
	
	/**
	 * 生成交易订单号
	 * @author wumeng
	 * @param orderType 业务编号
	 * @return 
	 */
	public  synchronized static String getOrderid(String orderType){
        String orderId = null;
        if(StringUtils.isNotBlank(orderType)){
            if(orderType.length() == 3){ //必须是三位字符串
                 String businessNumStr = orderType.substring(0,2);
                 if(StringUtils.isNumeric(businessNumStr)){ //判断前两位是数字
                	 //业务编号+时间戳
                     orderId = orderType + Calendar.getInstance().getTimeInMillis();
                 }
            }
        }
		return orderId;
	}
	
    /**
     * 生成MD5串
     * @author Jeff
     * @param str
     * @return
     */
    public static String getMd5Str(String str){
        String result ="";
        MessageDigest md = null;
        if(StringUtils.isNotBlank(str)){
            try {
                md = MessageDigest.getInstance("MD5");
                md.update(str.getBytes());
                byte byteArray[] = md.digest();

                StringBuffer md5StrBuff = new StringBuffer();
                   for (int i = 0; i < byteArray.length; i++) {
                         if (Integer.toHexString(0xFF & byteArray[i]).length() == 1) {
                             md5StrBuff.append("0").append(Integer.toHexString(0xFF & byteArray[i]));
                         }else {
                             md5StrBuff.append(Integer.toHexString(0xFF & byteArray[i]));
                         }
                   }
                result = md5StrBuff.toString();
            } catch (NoSuchAlgorithmException e) {
                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            }
        }
        return  result;
    }

}
