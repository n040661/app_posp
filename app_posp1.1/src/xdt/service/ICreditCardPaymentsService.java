package xdt.service;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.HashMap;


public interface ICreditCardPaymentsService {
	
	/**
	 * 添加信用卡信息
	 * @param addCreditCardInfo
	 * @param session
	 * @return
	 */
	public String addCreditCard(String addCreditCardInfo, HttpSession session, HttpServletRequest request) throws Exception;
	
	/**
	 * 添加信用卡信息异常
	 * @return
	 * @throws Exception
	 */
	public String addCreditCardException(HttpSession session)throws Exception;

    /**
     * 生成订单
     * @param produceOrderInfo
     * @param session
     * @param request
     * @return
     * @throws Exception
     */
    public String pruduceOrder(String produceOrderInfo, HttpSession session, HttpServletRequest request) throws Exception ;

}
