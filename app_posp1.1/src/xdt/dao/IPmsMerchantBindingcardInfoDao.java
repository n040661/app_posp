package xdt.dao;

import java.util.HashMap;
import java.util.List;

import xdt.model.PmsMerchantBindingcardInfo;

public interface IPmsMerchantBindingcardInfoDao extends IBaseDao<PmsMerchantBindingcardInfo> {
		
	/**
	 * 检索银行卡列表
	 * @param mercId
	 * @return
	 * @throws Exception
	 */
	public List<PmsMerchantBindingcardInfo> selectCardListByMercId (HashMap<String,String> map) throws Exception;
	
	/**
	 * 根据商户id与银行卡号查询银行卡信息
	 * @param map
	 * @return
	 * @throws Exception
	 */
	public PmsMerchantBindingcardInfo searchBankCardInfo(HashMap<String,String> map) throws Exception;

}
