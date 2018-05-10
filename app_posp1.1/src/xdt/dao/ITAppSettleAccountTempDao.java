package xdt.dao;

import xdt.model.TAppSettleAccountTemp;
import xdt.model.TransLatestData;

import java.util.List;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: Jeff
 * Date: 15-7-28
 * Time: 下午5:20
 * To change this template use File | Settings | File Templates.
 */
public interface ITAppSettleAccountTempDao extends IBaseDao<TAppSettleAccountTemp>  {


    /**
     * 获取按月的统计项
     * @param map
     * @return
     * @throws Exception
     */
    public List<TransLatestData> selectLatestMonth(Map<String,String> map) throws Exception;


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
}
