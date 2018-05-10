package xdt.service;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

public interface IUserInfoService {

	/**
	 * 验证手机号
	 * 
	 * @return
	 * @throws Exception
	 */
	public String checkPhone(String checkPhone, HttpSession session,
			HttpServletRequest request) throws Exception;

	/**
	 * 验证手机号异常
	 * 
	 * @return
	 * @throws Exception
	 */
	public String checkPhoneException() throws Exception;

	/**
	 * 保存账号
	 * 
	 * @return
	 * @throws Exception
	 */
	public String addUserinfo(String userinfo, HttpSession session,
			HttpServletRequest request) throws Exception;

	/**
	 * 保存账号异常
	 * 
	 * @return
	 * @throws Exception
	 */
	public String addUserinfoException() throws Exception;

	/**
	 * 收银员列表
	 * 
	 * @param request
	 * @param session
	 * @param queryUserinfo
	 * 
	 * @return
	 * @throws Exception
	 */
	public String queryUserinfoList(String queryUserinfo, HttpSession session,
			HttpServletRequest request) throws Exception;

	/**
	 * 收银员列表异常
	 * 
	 * @return
	 * @throws Exception
	 */
	public String queryUserinfoListException() throws Exception;

}
