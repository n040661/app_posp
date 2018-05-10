package xdt.service;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

public interface IPmsPosInfoService {
	/**
	 *通过sn号查询出相关的数据，如果已经被 绑定返回失败，没有则进行绑定
	 */
	public String addMineDevice(String accountInfo, HttpSession session,
			HttpServletRequest request) throws Exception;

	/**
	 * 通过sn号查询出相关的数据异常
	 * 
	 * @return
	 * @throws Exception
	 */
	public String addMineDeviceException() throws Exception;

}
