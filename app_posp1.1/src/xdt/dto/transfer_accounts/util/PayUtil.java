package xdt.dto.transfer_accounts.util;

import xdt.dto.BaseUtil;

/** 
* @author 作者 E-mail: 
* @version 创建时间：2018年4月27日 下午4:38:45 
* 类说明 
*/
public class PayUtil {
	public static final String payUrl=BaseUtil.url+"/totalPayController/ysbNotifyUrl.action";//代付异步
	public static final String jmPayUrl=BaseUtil.url+"/totalPayController/jmNotifyUrl.action";//金米代付异步
	public static final String hjNotifyUrl=BaseUtil.url+"/totalPayController/hjNotifyUrl.action";//汇聚代付异步
	
}
