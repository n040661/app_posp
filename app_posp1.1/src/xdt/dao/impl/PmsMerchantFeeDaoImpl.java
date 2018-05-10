package xdt.dao.impl;

import org.springframework.stereotype.Repository;
import xdt.dao.IPmsMerchantFeeDao;
import xdt.model.PmsMerchantFee;

/**
 * 通道费率配置
 * User: Jeff
 * Date: 15-8-29
 * Time: 下午4:21
 * To change this template use File | Settings | File Templates.
 */
@Repository
public class PmsMerchantFeeDaoImpl extends BaseDaoImpl<PmsMerchantFee> implements IPmsMerchantFeeDao {
    //根据商户实体更新商户 信息
    private static final String GETBYMERCID = "getByMercId";

    /**
     * 根据商户id获取通道费率
     * @param mercId
     * @return
     */
    @Override
    public PmsMerchantFee getByMercId(String mercId) {
        String sql = this.getStatementId(GETBYMERCID);
        return sqlSession.selectOne(sql,mercId);
    }
}
