package xdt.dao;

import xdt.model.AppVersion;

/**
 * App版本 DAO层
 * User: Jeff
 * Date: 15-5-26
 * Time: 下午1:36
 * To change this template use File | Settings | File Templates.
 */
public interface IAppVersionDao extends IBaseDao<AppVersion>  {

    /**
     * 查找最新的版本信息
     * @return
     */
    public  AppVersion selectNewestOne(AppVersion appv);

}
