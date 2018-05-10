package xdt.service;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.web.bind.annotation.RequestParam;

import xdt.model.Balance;
import xdt.model.City;
import xdt.model.PayProject;
import xdt.model.PayUnit;
import xdt.model.Card;
import xdt.model.Province;

/**
 * 水煤电查询接口
 * 
 * @author lev12
 * 
 */
public interface IUtilityService {

	/**
	 * 省份查询
	 * 
	 * @return
	 * @throws Exception
	 */
	public String getProvinceList(HttpSession session) throws Exception;

	/**
	 * 省份查询异常
	 * 
	 * @return
	 * @throws Exception
	 */
	public String getProvinceListException() throws Exception;

	/**
	 * 城市查询
	 * 
	 * @param getCityListInfo
	 * @return
	 * @throws Exception
	 */
	public String getCityList(HttpSession session, String getCityListInfo) throws Exception;

	/**
	 * 城市查询异常
	 * 
	 * @param getCityListInfo
	 * @return
	 * @throws Exception
	 */
	public String getCityListException(String getCityListInfo) throws Exception;

	/**
	 * 水煤电充值类型查询
	 * 
	 * @param getPayProjectListInfo
	 * @return
	 * @throws Exception
	 */
	public String getPayProjectList(HttpSession session, String getPayProjectListInfo)
			throws Exception;

	/**
	 * 水煤电充值类型查询异常
	 * 
	 * @param getPayProjectListInfo
	 * @return
	 * @throws Exception
	 */
	public String getPayProjectListException(String getPayProjectListInfo)
			throws Exception;

	/**
	 * 水煤电缴费单位查询
	 * 
	 * @param getPayUnitListInfo
	 * @return
	 * @throws Exception
	 */
	public String getPayUnitList(HttpSession session, String getPayUnitListInfo) throws Exception;

	/**
	 * 水煤电缴费单位查询异常
	 * 
	 * @param getPayUnitListInfo
	 * @return
	 * @throws Exception
	 */
	public String getPayUnitListException(String getPayUnitListInfo)
			throws Exception;

	/**
	 * 水煤电商品信息查询
	 * 
	 * @param queryClassIdInfo
	 * @return
	 * @throws Exception
	 */
	public String queryClassId(HttpSession session, String queryClassIdInfo) throws Exception;

	/**
	 * 水煤电商品信息查询异常
	 * 
	 * @param queryClassIdInfo
	 * @return
	 * @throws Exception
	 */
	public String queryClassIdException(String queryClassIdInfo)
			throws Exception;

	/**
	 * 水电煤账户欠费查询
	 * 
	 * @param queryBalanceInfo
	 * @return
	 * @throws Exception
	 */
	public String queryBalance(HttpSession session, String queryBalanceInfo) throws Exception;

	/**
	 * 水电煤账户欠费查询异常
	 * 
	 * @param queryBalanceInfo
	 * @return
	 * @throws Exception
	 */
	public String queryBalanceException(String queryBalanceInfo)
			throws Exception;

	/**
	 * 缴费详情查看
	 * 
	 * @param queryPayDetailInfo
	 * @return
	 * @throws Exception
	 */
	public String queryPayDetail(HttpSession session, String queryPayDetailInfo)
			throws Exception;

	/**
	 * 缴费详情查看异常
	 * 
	 * @param queryPayDetailInfo
	 * @return
	 * @throws Exception
	 */
	public Object queryPayDetailException(String queryPayDetailInfo)
			throws Exception;

	/**
	 * 生成订单
	 * 
	 * @param utilityInfo
	 * @param session
	 * @return
	 */
	public String producedOrder(String utilityInfo, HttpSession session)
			throws Exception;

	/**
	 * 生成订单异常
	 * 
	 * @param utilityInfo
	 * @param session
	 * @return
	 */
	public String producedOrderException(HttpSession session) throws Exception;

}
