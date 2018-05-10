package xdt.dao;

import java.util.Map;

import xdt.model.MerchantMinel;
import xdt.model.WeChatPublicNo;

public interface IMerchantMineDao extends IBaseDao<MerchantMinel> {

	/**
	 * 商户在客户端查看绑定的卡信息 提现页面显示使用
	 * @param paramMap
	 * @return
	 */
	public Map<String, String> queryDrawMoneyAcc(Map<String, String> paramMap);
	
	/**
	 *  获取业务信息   最大值、最小值、费率
	 * @param paramMap
	 * @return
	 */
	public Map<String, String> queryBusinessInfo(Map<String, String> paramMap);
	
	
	
	
	/**
	 * 提款记录存入
	 * @param paramMap
	 * @return
	 */
	public Integer saveDrawMoneyAcc(MerchantMinel pmsMerchantCollectManager) ;
	/**
	 * 查询交易日期是否是节假日 （是否是可提款转账日期
	 * @param date
	 * @return
	 */
	public int queryFestival(String  date);
	/**
	 * 微信公众号信息获取
	 * wumeng  20150831
	 * @param getoAgentNo
	 */
	public WeChatPublicNo queryWechatPublicNo(String oAgentNo);
	
	/**
	 * 查询提现时间段  
	 * wumeng  2015-10-25
	 */
	public Map<String, String> queryTiXianTime();
	/**
	 * 查询刷卡交易时间段判断 
	 *   wm 2016-03-04  
	 */
	public Map<String, String> queryShuaKaAgent0Time();
}
