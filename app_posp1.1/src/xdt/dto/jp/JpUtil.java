package xdt.dto.jp;

import xdt.dto.BaseUtil;

/** 
* @author 作者 E-mail: 
* @version 创建时间：2017年12月8日 下午5:27:30 
* 类说明 
*/
public class JpUtil {

	public static final String notifyUrl=BaseUtil.url+"/JPController/notifyUrl.action";//支付异步
	public static final String returnUrl=BaseUtil.url+"/JPController/returnUrl.action";//支付同步
	public static final String responseUrl=BaseUtil.url+"/JPController/responseUrl.action";//代付异步
	
	
	public static final String service="rpmBankPayment";
	public static final String pay="singleTransfer";
	public static final String payUrlTest="http://43.227.141.32/paygateway/mpsGate/mpsTransaction";//代付测试环境
	public static final String payUrl="https://jd.kingpass.cn/paygateway/mpsGate/mpsTransaction";//代付测试环境
	
	public static final String cardUrlTest="http://43.227.141.32/paygateway/paygateway/bankPayment";//支付测试环境
	public static final String cardUrl="https://jd.kingpass.cn/paygateway/paygateway/bankPayment";//支付生产环境
	
	public static final String scanCodeTest="http://43.227.141.32/paygateway/mpsGate/mpsTransaction";//支付测试环境
	public static final String scanCode="https://jd.kingpass.cn/paygateway/";//支付测试环境
	
}
