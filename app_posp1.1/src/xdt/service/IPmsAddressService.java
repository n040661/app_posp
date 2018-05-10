package xdt.service;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

/**
 * @author lev12
 * 
 */
public interface IPmsAddressService {

	/**
	 * 收货地址列表查看
	 * @param session
	 * @return
	 * @throws Exception
	 */
	public String queryAddressList(HttpSession session) throws Exception;

	/**
	 * 收货地址列表查看异常
	 * 
	 * @return
	 * @throws Exception
	 */
	public String queryAddressListException() throws Exception;

	/**
	 * 增加收货地址
	 * 
	 * @param requestData 
	 * @param session
	 * @param request
	 * @return
	 * @throws Exception
	 */
	public String addAddress(String requestData, HttpSession session, HttpServletRequest request) throws Exception;

	/**
	 * 增加收货地址异常
	 * 
	 * @return
	 * @throws Exception
	 */
	public String addAddressException() throws Exception;

	/**
	 * 删除收货地址
	 * 
	 * @param requestData
	 * @param session
	 * @param request
	 * @return
	 * @throws Exception
	 */
	public String delAddress(String requestData, HttpSession session, HttpServletRequest request) throws Exception;

	/**
	 * 删除收货地址异常
	 * 
	 * @return
	 * @throws Exception
	 */
	public String delAddressException() throws Exception;



}
