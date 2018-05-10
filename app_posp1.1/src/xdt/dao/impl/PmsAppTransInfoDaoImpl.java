package xdt.dao.impl;

import xdt.dao.IPmsAppTransInfoDao;
import xdt.dao.impl.BaseDaoImpl;
import xdt.model.PmsAppTransInfo;
import org.springframework.stereotype.Repository;
import xdt.model.TransLatestData;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
public class PmsAppTransInfoDaoImpl extends BaseDaoImpl<PmsAppTransInfo> implements IPmsAppTransInfoDao {

	private final static String SELECTONE = "selectByOrderId";
	
	private final static String UPDATE = "updateResponseCode";
	
	private final static String UPDATEXL = "updateXLResponseCode";
	
	
	private final static String SELECTLIST = "selectMyBillList";
	
	private final static String SELECTRECORD = "selectByDate";

	private final static String SELECTLATESTDAYDATA = "selectLatestDayData";
	private final static String SELECTLATESTDATA = "selectLatestData";
	private final static String SELECTLATESTCountDATA = "selectLatestCountData";

	private final static String SELECTNEEDCALLBACK = "selecNeedCallBack";
	//修改订单是否已经修改过余额标记
	private final static String UPDATEORDERACCOUNTINGFLAG = "updateOrderAccountingFlag";
	//根据订单号查询订单状态和是否已经修改过余额
	private final static String SELECTACCOUNTINGFLAGANDSTATUS = "selectAccountingFlagAndStatus";
    //获取近N天内的按照天统计的总金额记录  分页
	private final static String SELECTLATESTDAYDATAPAGE = "selectLatestDayDataPage";
	// 获取近N天内的按照天统计的总金额记录  分页条数
    private final static String SELECTLATESTDAYDATAPAGECOUNT = "selectLatestDayDataPageCount";
    //获取按月的统计项   默认获取一年的数据
    private final static String SELECTLATESTMONTH = "selectLatestMonth";
    
    //修改订单表中的订单状态为 0 订单已经完成
	private final static String UPDATEORDERSTATUS = "updateOrderStatus";
	//修改订单状态为6等待清算系统结算
	private final static String  UPDATEORDERSTATUSFORSETTLE="updateOrderStatusForSettle";
	//按日期查询当日记录总数
	private final static String COUNTBYDATE = "countByDate";
    //询当前id的项在当天的所有项的行数是多少，用于账单分页判断
    private final static String  GETROWNUMBYDATE= "getRowNumByDate";
    //按月份的数据统计,默认获取一年的数据,获取全部账单数据
    private final static String  SELECTLATESTMONTHALL="selectLatestMonthAll";
    //查询一个小时内未处理的订单
    private final static String  SELECTORDER="selectorder";
    
	/**
	 * 根据交易号查询订单
	 */
	public PmsAppTransInfo searchOrderInfo(String orderId) throws Exception {
	   String sql = getStatementId(SELECTONE);
	   return sqlSession.selectOne(sql,orderId);
	}

	/**
	 * 更新响应码
	 */
	@Override
	public int updateResponseCode(HashMap<String, String> map) throws Exception {
		String sql = getStatementId(UPDATE);
		return sqlSession.update(sql,map);
	}

	/**
	 * 更新讯联响应码和批次号
	 * @param map
	 * @return
	 * @throws Exception
	 */
	@Override
	public int updateXLResponseCode(HashMap<String, String> map)throws Exception{
		String sql = getStatementId(UPDATEXL);
		return sqlSession.update(sql,map);
	}
	
	
	
	
	/**
	 * 检索账单列表
	 */
	@Override
	public List<PmsAppTransInfo> searchMyBillList(Map<String, String> map)throws Exception {
		String sql = getStatementId(SELECTLIST);	
		return sqlSession.selectList(sql,map);
	}

	/**
	 * 检索交易记录
	 */
	@Override
	public List<PmsAppTransInfo> searchTransRecord(HashMap<String, String> map)throws Exception {
		String sql = getStatementId(SELECTRECORD);	
		return sqlSession.selectList(sql,map);
	}

    /**
     * 查询当天状态为 null（初始），2（平台支付中（百度，支付宝....），4（平台支付完成，等待调用通道支付），5（通道等待支付（欧飞...）））
     *
     * @return
     * @throws Exception
     */
    @Override
    public List<PmsAppTransInfo> searchNeedCallBackList() throws Exception {
        String sql = getStatementId(SELECTNEEDCALLBACK);
        return sqlSession.selectList(sql);
    }
    
    /**
	 * 根据订单号查询订单状态和是否已经修改过余额
	 * wumeng   20150528
	 */
    @Override
    public HashMap<String, String> selectAccountingFlagAndStatus(String orderid) throws Exception {
        String sql = getStatementId(SELECTACCOUNTINGFLAGANDSTATUS);
        return sqlSession.selectOne(sql,orderid);
    }
    
    
    /**
	 * 修改订单是否已经修改过余额标记
	 * wumeng   20150528
	 */
	@Override
	public int updateOrderAccountingFlag(String orderid) throws Exception {
		String sql = getStatementId(UPDATEORDERACCOUNTINGFLAG);
		return sqlSession.update(sql,orderid);
	}

    /**
     * 获取近N天内的按照天统计的总金额记录
     * @param map
     * @return
     * @throws Exception
     */
    @Override
    public List<TransLatestData> selectTransLatestDate(Map<String, String> map) throws Exception {
        String sql = getStatementId(SELECTLATESTDAYDATA);
        return sqlSession.selectList(sql,map);
    }


    /**
     * 获取近N天内的按照天统计的分页数据
     * @param map
     * @return
     * @throws Exception
     */
    @Override
    public List<PmsAppTransInfo> selectTransLatestData(Map<String, String> map) throws Exception {
        List<PmsAppTransInfo> result = null;
        String sql = getStatementId(SELECTLATESTDATA);
        result = sqlSession.selectList(sql,map);
        return result;

    }


    /**
     * 获取近N天内的按照天统计的总条数
     * @param map
     * @return
     * @throws Exception
     */
    @Override
    public Integer selectLatesCountData(Map<String, String> map) throws Exception {
        Integer result = 0;
        List s =null;
        String sql = getStatementId(SELECTLATESTCountDATA);
        if( (s= sqlSession.selectList(sql,map)) != null){
            result = Integer.parseInt(s.get(0).toString());
        }
        return   result;
    }

    /**
     *  获取近N天内的按照天统计的总金额记录  分页
     * @param map
     * @return
     * @throws Exception
     */
    @Override
    public List<TransLatestData> selectLatestDayDataPage(Map<String, String> map) throws Exception {
        List<TransLatestData> result = null;
        String sql = getStatementId(SELECTLATESTDAYDATAPAGE);
        result = sqlSession.selectList(sql,map);
        return result;
    }

    /**
     * 获取近N天内的按照天统计的总金额记录 条数
     * @param map
     * @return
     * @throws Exception
     */
    @Override
    public Integer selectLatestDayDataPageCount(Map<String, String> map) throws Exception {
        Integer result = 0;
        List s =null;
        String sql = getStatementId(SELECTLATESTDAYDATAPAGECOUNT);
        if( (s= sqlSession.selectList(sql,map)) != null){
            result = Integer.parseInt(s.get(0).toString());
        }
        return   result;
    }

    /**
     * 获取按月的统计项   默认获取一年的数据
     * @param map
     * @return
     * @throws Exception
     */
    @Override
    public List<TransLatestData> selectLatestMonth(Map<String, String> map) throws Exception {
        List<TransLatestData> result = null;
        String sql = getStatementId(SELECTLATESTMONTH);
        result = sqlSession.selectList(sql,map);
        return result;
    }
    /**
     * 修改订单表中的订单状态为 0 订单已经完成
     * @param orderid
     * @return
     * @throws Exception
     */
	public int updateOrderStatus(Map<String, String> param)throws Exception{
		String sql = getStatementId(UPDATEORDERSTATUS);
		return sqlSession.update(sql,param);

	}
	
	/**
     * 修改订单状态为6等待清算系统结算
     * @param orderid
     * @return
     * @throws Exception
     */
	public int updateOrderStatusForSettle(String orderid)throws Exception{
		String sql = getStatementId(UPDATEORDERSTATUSFORSETTLE);
		return sqlSession.update(sql,orderid);

	}

    /**
     * 获取近N天内的按照天统计的分页数据
     * @param map
     * @return
     * @throws Exception
     */
    @Override
    public Integer selectCountByDate(Map<String, String> map) throws Exception {
        Integer result = 0;
        String sql = getStatementId(COUNTBYDATE);
        List s =null;
        s = sqlSession.selectList(sql,map);
        if( (s= sqlSession.selectList(sql,map)) != null){
            result = Integer.parseInt(s.get(0).toString());
        }
        return result;

    }

    @Override
    public Integer getRowNumByDate(Map<String, String> map) throws Exception {
        Integer result = 0;
        String sql = getStatementId(GETROWNUMBYDATE);
        List s =null;
        s = sqlSession.selectList(sql,map);
        if( (s= sqlSession.selectList(sql,map)) != null){
            result = Integer.parseInt(s.get(0).toString());
        }
        return result;
    }


    /**
     * 获取按月的统计项   默认获取一年的数据
     * @param map
     * @return
     * @throws Exception
     */
    @Override
    public List<TransLatestData> selectLatestMonthAll(Map<String, String> map) throws Exception {
        List<TransLatestData> result = null;
        String sql = getStatementId(SELECTLATESTMONTHALL);
        result = sqlSession.selectList(sql,map);
        return result;
    }

	@Override
	public void updateChanNum(PmsAppTransInfo pmsAppTransInfo) {
		this.sqlSession.update("updateChanNum", pmsAppTransInfo);
	}

	@Override
	public List<PmsAppTransInfo> searchMyorder() throws Exception {
		
		  String sql = getStatementId(SELECTORDER);
	      return sqlSession.selectList(sql);
	}
	@Override
	public List<PmsAppTransInfo> searchMyorder1() throws Exception {
		
		  String sql = getStatementId("selectorder1");
	      return sqlSession.selectList(sql);
	}
}
