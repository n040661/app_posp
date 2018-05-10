package xdt.dao;

import xdt.model.PmsMerchantFee;

/**
 * Created with IntelliJ IDEA.
 * User: Jeff
 * Date: 15-8-29
 * Time: 下午4:14
 * To change this template use File | Settings | File Templates.
 */
public interface IPmsMerchantFeeDao extends IBaseDao<PmsMerchantFee> {
    /**
     * 根据mercid获取通道费率信息
     * @param mercId
     * @return
     */
    public PmsMerchantFee getByMercId(String mercId);
}
