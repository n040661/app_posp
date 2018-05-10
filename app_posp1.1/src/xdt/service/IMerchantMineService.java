package xdt.service;


import xdt.dto.DrawMoneyAccRequestAndResponseDTO;
import xdt.model.PmsAppTransInfo;
import xdt.model.SessionInfo;


/**
 * 商户个人账户信息  service
 * wumeng 20150525
 */
public interface IMerchantMineService {

	
	/**
	 * 查看商户在客户端显示图片、手机号、账户金额     个人信息
	 * wumeng  20150525
	 * @param param
	 * @param sessionInfo
	 */
	public String queryMineAcc(String param, SessionInfo  sessionInfo)throws Exception;
	
	/**
	 * 商户在客户端查询绑定的卡信息   提现页面显示使用
	 * wumeng  20150525
	 * @param param
	 * @param sessionInfo
	 */
	public String queryDrawMoneyAcc(String param,SessionInfo  sessionInfo)throws Exception;
	/**
	 * 商户把钱款现到绑定的卡上操作  第一步  生成订单  添加流水   提款数据添加
	 * wumeng  20150525
	 * @param sessionInfo
	 */
	public DrawMoneyAccRequestAndResponseDTO insertDrawMoneyAcc(String param, SessionInfo sessionInfo)throws Exception;
	/**
	 * 商户把钱款现到绑定的卡上操作  第二步确认订单并支付
	 * wumeng  20150515
	 * @param mercid
	 * @param pmsAppTransInfo
	 */
	public String confirmDrawMoneyAcc(String mercid,PmsAppTransInfo  pmsAppTransInfo)throws Exception;
	
	
	/**
	 * 获取业务信息   最大值、最小值、费率
	 * wumeng  20150626
	 * @param param
	 * @param response
	 * @param session
	 * @param mercinfo
	 */
	public String queryBusinessInfo(String param,SessionInfo  sessionInfo)throws Exception;
	/**
	 * 微信公众号信息获取
	 * wumeng  20150831
	 * @param param
	 * @param response
	 * @param session
	 */
	public String queryWechatPublicNo(String param, SessionInfo sessionInfo)throws Exception;
	
}
