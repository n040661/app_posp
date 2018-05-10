package xdt.dao;

import xdt.dao.IBaseDao;
import xdt.model.PmsAppTransInfo;
import xdt.model.TransLatestData;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public interface IPmsAppTransInfoDao extends IBaseDao<PmsAppTransInfo> {

	/**
	 * 根据交易号检索订单信息
	 * @param orderId
	 * @return
	 * @throws Exception
	 */
	public PmsAppTransInfo searchOrderInfo(String orderId) throws Exception;
	
	/**
	 * 更新第三方响应码
	 * @param map
	 * @return
	 * @throws Exception
	 */
	public int updateResponseCode(HashMap<String, String> map)throws Exception;
	/**
	 * 更新讯联响应码和批次号
	 * @param map
	 * @return
	 * @throws Exception
	 */
	public int updateXLResponseCode(HashMap<String, String> map)throws Exception;
	
	/**
	 * 检索账单列表
	 * @param map
	 * @return
	 * @throws Exception
	 */
	public List<PmsAppTransInfo> searchMyBillList(Map<String, String> map) throws Exception;
	
	/**
	 * 检索交易记录
	 * @return
	 * @throws Exception
	 */
	public List<PmsAppTransInfo> searchTransRecord(HashMap<String, String> map) throws Exception;

    /**
     * 检索5分钟内没有返回状态的订单
     * @return
     * @throws Exception
     */
    public List<PmsAppTransInfo> searchNeedCallBackList() throws  Exception;
    
    /**
	 * 根据订单号查询订单状态和是否已经修改过余额
	 * wumeng   20150528
	 */
    public HashMap<String, String> selectAccountingFlagAndStatus(String orderid) throws Exception;
    
    /**
	 * 修改订单是否已经修改过余额标记
	 * wumeng   20150528
	 */
	public int updateOrderAccountingFlag(String orderid) throws Exception;

    /**
     * 获取近N天内的按照天统计的总金额记录
     * @return
     * @throws Exception
     */
    public List<TransLatestData> selectTransLatestDate(Map<String,String> map) throws Exception;


    /**
     * 获取近N天内的按照天统计的分页数据
     * @return
     * @throws Exception
     */
    public List<PmsAppTransInfo> selectTransLatestData(Map<String,String> map) throws Exception;
    /**
     * 获取近N天内的按照天统计的总条数
     * @return
     * @throws Exception
     */
    public Integer selectLatesCountData(Map<String,String> map) throws Exception;

    /**
     *  获取近N天内的按照天统计的总金额记录  分页
     * @param map
     * @return
     * @throws Exception
     */
    public List<TransLatestData>  selectLatestDayDataPage(Map<String,String> map) throws Exception;

  /**
     *  获取近N天内的按照天统计的总金额记录  分页条数
     * @param map
     * @return
     * @throws Exception
     */
    public Integer  selectLatestDayDataPageCount(Map<String,String> map) throws Exception;


    /**
     * 获取按月的统计项
     * @param map
     * @return
     * @throws Exception
     */
    public List<TransLatestData>  selectLatestMonth(Map<String,String> map) throws Exception;

    /**
     * 修改订单表中的订单状态为 0 订单已经完成
     * @param orderid
     * @return
     * @throws Exception
     */
	public int updateOrderStatus(Map<String, String> param)throws Exception;
	/**
     * 修改订单状态为6等待清算系统结算
     * @param orderid
     * @return
     * @throws Exception
     */
	public int updateOrderStatusForSettle(String orderid)throws Exception;
	
	
	
	
    /**
     * 根据日期查询交易记录条数
     * @param map
     * @return
     * @throws Exception
     */
    public Integer selectCountByDate(Map<String, String> map) throws Exception;

    /**
     * 询当前id的项在当天的所有项的行数是多少，用于账单分页判断
     * @param map
     * @return
     * @throws Exception
     */
    public Integer getRowNumByDate(Map<String,String> map) throws Exception;

    /**
     * 获取按月的统计项   默认获取一年的数据
     * @param map
     * @return
     * @throws Exception
     */
    public List<TransLatestData> selectLatestMonthAll(Map<String, String> map) throws Exception;

	public void updateChanNum(PmsAppTransInfo pmsAppTransInfo);
	/**
	 * 检索一个小时内未处理的订单江苏电商
	 * @param map
	 * @return
	 * @throws Exception
	 */
	public List<PmsAppTransInfo> searchMyorder() throws Exception;
	/**
	 * 检索一个小时内未处理的订单  畅捷通
	 * @param map
	 * @return
	 * @throws Exception
	 */
	public List<PmsAppTransInfo> searchMyorder1() throws Exception;
}
