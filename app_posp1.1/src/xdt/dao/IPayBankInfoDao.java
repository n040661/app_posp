package xdt.dao;

import java.util.List;

import xdt.model.PayBankInfo;

public interface IPayBankInfoDao extends IBaseDao<PayBankInfo>{
	
	public PayBankInfo selectByBankInfo(PayBankInfo pay) throws Exception;
	
	public List<PayBankInfo> selectBankCodes(PayBankInfo pay) throws Exception;

}
