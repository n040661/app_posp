package xdt.dao;

import java.util.Map;

import xdt.dao.IBaseDao;
import xdt.model.PmsMerchantInfo;

public interface IPmsMerchantInfoDao extends IBaseDao<PmsMerchantInfo> {
	
	/**
	 * 根据实体更新商户信息
	 * @param pmsMerchantInfo
	 * @return
	 * @throws Exception
	 */
	public int merchantUpdateByPmsMerchantInfo(PmsMerchantInfo pmsMerchantInfo) throws Exception;

	
	
	
	/**
	 * 根据商户用户名（手机号）查询商户信息
	 * wumeng  20150525
	 */
	public Map<String, String> queryMercuryInfo(String  mobilePhone)throws Exception;

    /**
     * 根据商户号查询商户
     * @param mercId
     * @return
     * @throws Exception
     */
    public PmsMerchantInfo selectMercByMercId(String mercId) throws Exception;

    /**
	 * 根据商户编号查询商户是否正式商户  60 正式商户
	 * wumeng  20150525
	 */
	public String queryMercuryStatus(String  mercid)throws Exception;
	
	/**
	 * 查询支付宝和微信是不是绑定了路由
	 * wumeng  20150730
	 */
	public int getChannelCount(Map<String,String> map)throws Exception;
	
	/**
	 * 根据手机号查询商户是否存在
	 *    wm 2016-02-22
	 */
	public PmsMerchantInfo selectMerchantInfoByPhone(String account)throws Exception;
	
	/**
	 * 根据商户编号查询商户信息
	 *    wm 2016-02-22
	 */
	public PmsMerchantInfo selectMerchantInfoByMercid(String  mercid)throws Exception;



	
	/**
	 * 查询商户清分费率
	 *    wm 2016-05-11
	 */
	public Map<String,String> queryMersettleRateType(String mercid)throws Exception;

	

	/**
	 * 查询商户当前所属代理，刷卡费率和清算费率
	 *    wm 2016-05-18
	 */
	public Map<String, String> queryAgentShuaKaRateAndSettleRate(String mercid)throws Exception;
	
	public int merchantUpdateByPmsMerchantInfos(PmsMerchantInfo pmsMerchantInfo)throws Exception ;
	
	/**
	 * 根据实体更新商户信息
	 * @param pmsMerchantInfo
	 * @return
	 * @throws Exception
	 */
	public int UpdatePmsMerchantInfo(PmsMerchantInfo pmsMerchantInfo) throws Exception;
	
	/**
	 * 代付更新金额D0
	 */
	public int updataPay(Map<String, String> map);
	/**
	 * 代付更新金额T1
	 */
	public int updataPayT1(Map<String, String> map);
	
	/**
	 * 入金更新金额D0
	 */
	public int updataD0(Map<String, String> map);
	/**
	 * 入金更新金额T1
	 */
	public int updataT1(Map<String, String> map);
}
