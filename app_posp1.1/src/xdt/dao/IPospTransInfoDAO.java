package xdt.dao;

import java.util.List;

import xdt.model.PospTransInfo;

/**
 * 支付流水 User: Jeff Date: 15-5-22 Time: 下午2:23 To change this template use File |
 * Settings | File Templates.
 */
public interface IPospTransInfoDAO extends IBaseDao<PospTransInfo> {

	PospTransInfo searchLatest(PospTransInfo pospTransInfo);

	PospTransInfo searchByOrderId(String orderId);

	/**
	 * 根据上送订单id查询流水信息
	 * 
	 * @param transOrderId
	 *            上送订单id
	 * @return
	 */
	PospTransInfo searchBytransOrderId(String transOrderId);

	/**
	 * 修改流水表状态
	 */
	public int updetePospTransInfo(String orderid) throws Exception;

	/**
	 * * 查询交易ID序列
	 * 
	 * @return
	 * @throws Exception
	 */
	public int getNextTransid() throws Exception;
	
	public PospTransInfo selectSrc(PospTransInfo para);
	
	public PospTransInfo selectSrcJour(PospTransInfo para);
	
	public PospTransInfo selectJourByUniqueKey(String uniqueKey);
	
	public PospTransInfo searchByPospsn(String pospsn);
	
	public PospTransInfo selectBySysseqno(String sysseqno);
	
	/**
	 * 订单号
	 * @return
	 */
	public int getJourno();
	
	/**
	 * 根据订单号修改流水
	 * 
	 * @param pospTransInfo
	 * @return
	 * @throws Exception
	 */
	public int updateByOrderId(PospTransInfo pospTransInfo) throws Exception;


	/**
	 * 唯一修改流水
	 * 
	 * @param pospTransInfo
	 * @return
	 * @throws Exception
	 */
	public int updateByUniqueKey(PospTransInfo pospTransInfo) throws Exception;
	
	/**
	 * 畅捷根据上送订单id查询流水信息
	 * 
	 * @param transOrderId
	 *            上送订单id
	 * @return
	 */
	PospTransInfo searchBycjtOrderId(String OrderId);
	/**
	 * 查询首信易商户中所有初始化订单
	 * 
	 * @param transOrderId
	 *            上送订单id
	 * @return
	 */
	public List<PospTransInfo>  selectPay();
}
