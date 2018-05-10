package xdt.dao.impl;

import org.springframework.stereotype.Repository;
import xdt.dao.IMerchantNewsInfoDao;
import xdt.model.MerchantNewsInfo;

/**
 * Created with IntelliJ IDEA.
 * User: Jeff
 * Date: 15-12-11
 * Time: 下午5:49
 * To change this template use File | Settings | File Templates.
 */
@Repository
public class MerchantNewsInfoDaoImpl extends BaseDaoImpl<MerchantNewsInfo> implements IMerchantNewsInfoDao {

    @Override
    public MerchantNewsInfo selectByMerc(String mercId) {
        MerchantNewsInfo m = null;
        try {
             m =  this.searchById(mercId);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return  m;
    }

}
