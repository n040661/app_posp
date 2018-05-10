package xdt.service;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

public interface IPmsMerchantPosService {
	/**
	 * 通过商户id查询商户绑定刷卡器
	 */
	public String selectPmsMerchantPos(HttpSession session,
			HttpServletRequest request) throws Exception;

	/**
	 * 通过商户id查询商户绑定刷卡器异常
	 * 
	 * @return
	 * @throws Exception
	 */
	public String selectPmsMerchantPosException() throws Exception;

	/**
	 * 通过posId解绑设备
	 * 
	 * @return
	 * @throws Exception
	 */
	public String updateMineDevice(String pmsMerchantInfo, HttpSession session,
			HttpServletRequest request) throws Exception;

	/**
	 * 通过posId解绑设备异常
	 * 
	 * @return
	 * @throws Exception
	 */
	public String updateMineDeviceException() throws Exception;

	/**
	 * 确认设备认证
	 * 
	 * @return
	 * @throws Exception
	 */
	public String confirmDevice(String pmsMerchantInfo, HttpSession session,
			HttpServletRequest request) throws Exception;

	/**
	 * 确认设备认证异常
	 * 
	 * @return
	 * @throws Exception 
	 * @throws Exception
	 */
	public Object confirmDeviceException() throws Exception;

}
