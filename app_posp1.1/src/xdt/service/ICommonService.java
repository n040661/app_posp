package xdt.service;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

public interface ICommonService {
	
	/**
	 * 刷卡支付 签到
	 * @param sn
	 * @return
	 */
	public String creditCardPaymentSignIn(String sn, HttpSession session, HttpServletRequest request)throws Exception ;
	
	/**
	 * 刷卡支付签到状态修改
	 */
	public String creditCardPaymentStatus(String account, HttpSession session, HttpServletRequest request)throws Exception;
}
