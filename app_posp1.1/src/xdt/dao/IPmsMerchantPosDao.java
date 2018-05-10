package xdt.dao;


import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import xdt.model.PmsMerchantPos;

/**
 * pos注册表
 * @author p
 *
 */
public interface IPmsMerchantPosDao extends IBaseDao<PmsMerchantPos> {
	// 通过商户id和sn进行查询
	public PmsMerchantPos selectMerchantidAndSn(Map<String,String> paramMap)throws Exception;
	//通过sn号进行查询
	public PmsMerchantPos selectSn(int posbusinessno)throws Exception;
	//通过sn号查询pos信息
    public PmsMerchantPos searchMerchantPosBySN(String serialno)throws Exception;
	//通过posid进行查询是否存在sn号绑定的用户
    public  PmsMerchantPos selectMerchantPos(String posid)throws Exception;
    //通过posid进行解绑
	public int updateByPosId(PmsMerchantPos pmsMerchantPos)throws Exception;
}
