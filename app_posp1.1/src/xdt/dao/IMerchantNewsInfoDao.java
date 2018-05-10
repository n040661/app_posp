package xdt.dao;

import xdt.model.MerchantNewsInfo;

public interface IMerchantNewsInfoDao extends IBaseDao<MerchantNewsInfo>{

    /**
     * 根据欧单编号查询
     * @param mercId
     * @return
     * @throws Exception
     */
    public MerchantNewsInfo selectByMerc(String mercId);

}
