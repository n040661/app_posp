package xdt.service;

import javax.servlet.http.HttpSession;

public interface IAppRateConfigService {

	/**
	 * 查询刷卡费率
	 * 
	 * @param appRateConfigInfo
	 * @return
	 * @throws Exception
	 */
	public String appRateConfigQuery(HttpSession session) throws Exception;

	/**
	 * 查询刷卡费率异常
	 * 
	 * @return
	 * @throws Exception
	 */
	public String appRateConfigQueryException() throws Exception;

}