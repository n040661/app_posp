package xdt.quickpay.payeasy.comm;

import xdt.util.Global;

/**
 * @ClassName: Constant
 * @Description:首信易支付常量
 * @author YanChao.Shang
 * @date 2017年4月1日 上午9:58:28
 *
 */

public class Constant {
	
	//标准支付接口
	public  static final String STANDARD_URL=Global.getConfig("PayEasyStandard");
	
	//直连支付接口
	private static final String DIRECT_URL=Global.getConfig("PayEasyDirect");
	
	//会员支付接口
	private static final String MEMBER_URL=Global.getConfig("PayEasyMember");
	
	// 单笔对账url
	private static final String SELECT_URL = Global.getConfig("PayEasySelect");

}
