package xdt.dao;

import java.util.List;

import xdt.model.AppRateConfig;

/**
 * 费率配置
 * User: Jeff
 * Date: 15-6-4
 * Time: 下午7:31
 * To change this template use File | Settings | File Templates.
 */
public interface IAppRateConfigDao extends IBaseDao<AppRateConfig>  {
    /**
     * 根据费率类型和 o单编号查询记录
     * @param appRateConfig
     * @return
     */
	public  AppRateConfig  getByRateTypeAndoAgentNo (AppRateConfig appRateConfig);
    /**
     * 查询第三方费率
     * @return AppRateConfig
     * @param oAgentNo
     */
	public List<AppRateConfig>  getThirdpartRate(String oAgentNo);
	
    /**
     * 查询第三方费率值
     * @return AppRateConfig
     * @param oAgentNo
     */
	public  AppRateConfig  getThirdpartRateValue(AppRateConfig appRateConfig);
}
