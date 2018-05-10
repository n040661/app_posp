package xdt.dao.impl;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Repository;
import xdt.dao.IAppRateConfigDao;
import xdt.model.AppRateConfig;

/**
 * 费率配置dao
 * User: Jeff
 * Date: 15-6-4
 * Time: 下午7:32
 * To change this template use File | Settings | File Templates.
 */
@Repository
public class AppRateConfigImpl  extends BaseDaoImpl<AppRateConfig> implements IAppRateConfigDao {
    public static  final  String SELECTBYRATETYPE =  "selectByRateTypeAndoAgentNo";
   
    //查询第三方费率
    public static  final  String GETTHIRDPARTRATE =  "getThirdpartRate";
    
    //查询第三方费率值
    public static  final  String GETTHIRDPARTRATEVALUE =  "selectByPrimaryRate";
    /**
     * 根据费率类型和 o单编号查询记录
     * @param appRateConfig
     * @return
     */
    @Override
    public  AppRateConfig  getByRateTypeAndoAgentNo (AppRateConfig appRateConfig){
        String sql = getStatementId(SELECTBYRATETYPE);
        return sqlSession.selectOne(sql, appRateConfig);
    }
    /**
     * 查询第三方费率
     * @return AppRateConfig
     * @param oAgentNo
     */
    @Override
    public  List<AppRateConfig>  getThirdpartRate(String oAgentNo){
    	String sql = getStatementId(GETTHIRDPARTRATE);
        return sqlSession.selectList(sql,oAgentNo);
    }
    /**
     * 查询第三方费率值
     * @return AppRateConfig
     * @param oAgentNo
     */
    @Override
    public   AppRateConfig  getThirdpartRateValue(AppRateConfig appRateConfig){
    	String sql = getStatementId(GETTHIRDPARTRATEVALUE);
        return sqlSession.selectOne(sql,appRateConfig);
    }
}
