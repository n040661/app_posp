package xdt.dto.hm;

import xdt.dto.BaseUtil;

public class HMUtil {

	/*public static final String openApiUrl="https://api.3035pay.com/pay/unionpay/quickcredit/back";
	public static final String payUrl="https://api.3035pay.com/pay/unionpay/entrust/back";
	public static final String selectUrl="https://api.3035pay.com/pay/unionpay/query/entrust";
	public static final String merchantid = "M2017071113922991567";
	public static final String aeskey = "3B65571F4EB0F92E";
	public static final String shakey = "F839220E463C03DE93C1CFC2F16DC370";
	*/
	
	//生产参数
	public static final String url="https://api.3035pay.com";
	public static final String openApiUrl1="https://api.3035pay.com/pay/unionpay/quickcredit/back";
	public static final String openApiUrl="https://api.3035pay.com/pay/unionpay/quickdebit/back";
	public static final String quickPayXinUrl="https://api.3035pay.com/pay/unionpay/quick/credit";//标准快捷信用卡路径
	public static final String quickPayJieUrl="https://api.3035pay.com/pay/unionpay/quick/debit";//标准快捷借记卡路径
	public static final String payUrl="https://api.3035pay.com/pay/unionpay/entrust/back";//https://api.3035pay.com/pay/unionpay/entrsut/t1
	public static final String payUrlT1="https://api.3035pay.com/pay/unionpay/entrsut/t1";//https://api.3035pay.com/pay/unionpay/entrsut/t1
	//https://api.3035pay.com/pay/unionpay/entrust/back
	public static final String selectUrl="https://api.3035pay.com/pay/unionpay/query/entrust";
	public static final String merchantid = "M2017112113602199513";
	public static final String aeskey = "AE06E74551E94285";
	public static final String shakey = "BD48155A12409387AE31F91F9CDE19D8";
	public static final String backurl = BaseUtil.url+"/HMController/notifyUrl.action";
	public static final String quickUrl = BaseUtil.url+"/QuickPayController/notifyUrl.action";
	
	
}
