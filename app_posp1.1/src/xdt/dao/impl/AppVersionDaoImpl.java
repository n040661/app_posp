package xdt.dao.impl;

import org.springframework.stereotype.Repository;
import xdt.dao.IAppVersionDao;
import xdt.model.AppVersion;

/**
 * App版本dao层
 * User: Jeff
 * Date: 15-5-26
 * Time: 下午1:40
 */
@Repository
public class AppVersionDaoImpl  extends BaseDaoImpl<AppVersion> implements IAppVersionDao{

    //查找最新的一条记录
    private static final String SELECTNEWESTONE = "selectNewestOne";

    /**
     * 查找最新的一条记录
     * @return
     */
    @Override
    public AppVersion selectNewestOne(AppVersion appV) {
        String sql = this.getStatementId(SELECTNEWESTONE);
        return sqlSession.selectOne(sql,appV);
    }
}
