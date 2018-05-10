package xdt.service;

import javax.servlet.http.HttpSession;

/**
 * 中石化加油卡卡号信息查询接口
 * 
 * @author lev12
 * 
 */
public interface ISinopecCardService {

	/**
	 * 中石化加油卡卡号信息查询接口
	 * 
	 * @return
	 * @throws Exception
	 */
	public String queryCardInfo(HttpSession session, String queryCardInfo)
			throws Exception;

	/**
	 * 中石化加油卡卡号信息查询接口异常
	 * 
	 * @return
	 * @throws Exception
	 */
	public String queryCardInfoException(String queryCardInfo) throws Exception;

	/**
	 * 中石化加油卡生成订单
	 * 
	 * @return
	 * @throws Exception
	 */
	public String producedOrder(String sinopecCardInfo, HttpSession session)
			throws Exception;

	/**
	 * 中石化加油卡生成订单异常
	 * 
	 * @return
	 * @throws Exception
	 */
	public String producedOrderException(HttpSession session) throws Exception;

}
