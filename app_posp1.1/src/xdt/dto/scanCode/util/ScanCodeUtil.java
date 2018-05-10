package xdt.dto.scanCode.util;

import xdt.dto.BaseUtil;

/** 
* @author 作者 E-mail: 
* @version 创建时间：2018年4月25日 下午4:46:11 
* 类说明 
*/
public class ScanCodeUtil {

	public static final String jpNotifyUrl=BaseUtil.url+"/ScanCodeController/jpNotifyUrl.action";//九派扫码异步
	public static final String hjNotifyUrl=BaseUtil.url+"/ScanCodeController/hjNotifyUrl.action";//汇聚扫码异步
	public static final String hjReturnUrl=BaseUtil.url+"/ScanCodeController/hjReturnUrl.action";//汇聚扫码同步
	public static final String jsdsNotifyUrl=BaseUtil.url+"/ScanCodeController/jsdsNotifyUrl.action";//江苏电商扫码异步
	public static final String ylNotifyUrl=BaseUtil.url+"/ScanCodeController/YLNotifyUrl.action";//漪雷扫码支付异步
	public static final String ylReturnUrl=BaseUtil.url+"/ScanCodeController/YLReturnUrl.action";//漪雷扫码支付同步
	public static final String ysbNotifyUrl=BaseUtil.url+"/ScanCodeController/ysbNotifyUrl.action";//银生宝扫码异步
	public static final String ylcsNotifyUrl=BaseUtil.url+"/ScanCodeController/ylcsNotifyUrl.action";//漪雷乘势扫码异步
	public static final String jhjNotifyUrl=BaseUtil.url+"/ScanCodeController/jhjNotifyUrl.action";//漪雷乘势扫码异步
	
	
}
