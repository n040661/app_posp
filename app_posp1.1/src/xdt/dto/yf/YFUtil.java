package xdt.dto.yf;

import xdt.dto.BaseUtil;

/** 
* @author 作者 E-mail: 
* @version 创建时间：2018年3月28日 下午3:09:04 
* 类说明 
*/
public class YFUtil {

	public static final String notifyUrl=BaseUtil.url+"/gateWay/yf_notifyUrl.action";//支付异步
	
	public static final String tkNotifyUrl=BaseUtil.url+"/gateWay/yftk_notifyUrl.action";//退款异步
	
	public static final String reistUrl=BaseUtil.url+"/gateWay/yf_returnUrl.action";//支付同步
	
	public static final String payUrl=BaseUtil.url+"/totalPayController/pay_notifyUrl.action";//代付异步
	
	public static final String payOneUrl=BaseUtil.url+"/totalPayController/yfReturnUrl.action";//代付异步单笔
	
	
	public final static String postFileDownUrl = "http://www.yfpayment.com/batchpay/download.do";//下载代付文件用的
	
	public final static String postDisburseResultQueryUrl = "http://www.yfpayment.com/batchpay/payquery.do";// 代付结果查询（查询某一批次的代付）
	
	public final static String postRefundChequeResultDownUrl = "http://www.yfpayment.com/batchpay/refundfetch.do";//退票结果下载
	
}
