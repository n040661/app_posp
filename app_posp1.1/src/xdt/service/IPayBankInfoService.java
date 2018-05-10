package xdt.service;

import java.util.List;

import xdt.model.PayBankInfo;

public interface IPayBankInfoService {
	/**
	 * 根据联行号查询银行卡信息
	 * 
	 * @param IPayBankInfoService
	 * @return
	 * @throws Exception
	 */
	public PayBankInfo selectByBankInfo(PayBankInfo pay) throws Exception;
	/**
	 * 根据银行编码查询银行ID
	 * 
	 * @param IPayBankInfoService
	 * @return
	 * @throws Exception
	 */
	public List<PayBankInfo> selectBankCodes(PayBankInfo pay) throws Exception;

}
