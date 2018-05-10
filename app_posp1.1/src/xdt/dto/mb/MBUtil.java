package xdt.dto.mb;

import xdt.dto.BaseUtil;

public class MBUtil {

	//public static final String payUrl="http://115.182.202.23:8880/ks_smpay/netsm/pay.sm";//测试地址
	public static final String payUrl="http://newpay.kspay.net:8181/ks_smpay/netsm/pay.sm";//生产地址
	public static final String quick ="http://newpay.kspay.net:8080/ks_onlpay/gateways/trans";//快捷支付生产环境
	public static final String payPUrl="http://hyapi.kspay.net:8190/ks_dfpay/mopay/pay";
	public static final String notifyUrl=BaseUtil.url+"/MBController/notifyUrl.action";//
	public static final String unionPaynotifyUrl=BaseUtil.url+"/MBController/unionPayNotifyUrl.action";
	public static final String returnUrl=BaseUtil.url+"/MBController/returnUrl.action";//
	public static final String versionId="001";//
	public static final String businessType1="1100";//
	public static final String businessType2="1200";//
	public static final String businessType3="470000";
	public static final String transChanlName="0008";//
	public static final String codeU="UTF-8";//
	public static final String codeG="GBK";//
	public static final String signType="MD5";//
}
