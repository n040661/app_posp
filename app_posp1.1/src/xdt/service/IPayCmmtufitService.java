package xdt.service;

import javax.servlet.http.HttpSession;

public interface IPayCmmtufitService {
	
	/**
	 * 根据前6位数字检索银行卡信息
	 * @param beforeSixCardNumber
	 * @param session
	 * @return
	 * @throws Exception
	 */
	public String searchCardListByBeforeSix(String beforeSixCardNumber,HttpSession session) throws Exception;
	
	/**
	 * 检索银行列表
	 * @param session
	 * @return
	 * @throws Exception
	 */
	public String searchBankList(String pageInfo,HttpSession session) throws Exception;
	
	/**
	 * 检索银行列表异常
	 * @return
	 * @throws Exception
	 */
	public String searchBankListException(HttpSession session)throws Exception;

}
