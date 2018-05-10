package xdt.dao.impl;

import org.springframework.stereotype.Repository;
import xdt.dao.ITAppSettleAccountTempDao;
import xdt.model.TAppSettleAccountTemp;
import xdt.model.TransLatestData;

import java.util.List;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: Jeff
 * Date: 15-7-28
 * Time: 下午5:22
 * To change this template use File | Settings | File Templates.
 */
@Repository
public class TAppSettleAccountTempDaoImpl  extends BaseDaoImpl<TAppSettleAccountTemp> implements ITAppSettleAccountTempDao {

    //获取按月的统计项   默认获取一年的数据
    private final static String SELECTLATESTMONTH = "selectLatestMonth";
    //获取近N天内的按照天统计的总金额记录  分页
    private final static String SELECTLATESTDAYDATAPAGE = "selectLatestDayDataPage";
    // 获取近N天内的按照天统计的总金额记录  分页条数
    private final static String SELECTLATESTDAYDATAPAGECOUNT = "selectLatestDayDataPageCount";

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
}
