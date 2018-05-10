package xdt.controller;

import java.io.IOException;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.google.gson.Gson;

import xdt.dto.MroducedTwoDimensionResponseDTO;
import xdt.model.SessionInfo;
import xdt.service.IUtilityService;

/**
 * 水煤电
 * 
 * @author lev12
 * 
 */
@Controller
@RequestMapping("convenientAction")
public class UtilityAction extends BaseAction {

	private Logger logger = Logger.getLogger(UtilityAction.class);

	@Resource
	private IUtilityService utilityService;// 水煤电服务层

	/**
	 * 省份查询
	 */
	@RequestMapping("/queryProvinceList")
	public void getProvinceList(HttpServletResponse response,
			HttpSession session, HttpServletRequest request) {
		try {
			String jsonString = utilityService.getProvinceList(session);
			outPrint(response, jsonString);
		} catch (Exception e) {
			try {
				outPrint(response, utilityService.getProvinceListException());
			} catch (Exception e1) {
				e1.printStackTrace();
			}
			setSession(session, request.getRemoteAddr(), false);
			logger.info("[app_exception]" + e.fillInStackTrace());
			e.printStackTrace();
		}
	}

	/**
	 * 城市查询
	 */
	@RequestMapping("/queryCityList")
	public void getCityList(
			HttpServletResponse response, HttpSession session,
			HttpServletRequest request) {
		String getCityListInfo = requestClient(request);
		try {
			String jsonString = utilityService.getCityList(session, getCityListInfo);
			outPrint(response, jsonString);
		} catch (Exception e) {
			try {
				outPrint(response, utilityService
						.getCityListException(getCityListInfo));
			} catch (Exception e1) {
				e1.printStackTrace();
			}
			setSession(session, request.getRemoteAddr(), false);
			logger.info("[app_exception]" + e.fillInStackTrace());
			e.printStackTrace();
		}
	}

	/**
	 * 水煤电充值类型查询
	 */
	@RequestMapping("/queryPayProjectList")
	public void getPayProjectList(
			HttpServletResponse response, HttpSession session,
			HttpServletRequest request) {
		String getPayProjectListInfo = requestClient(request);
		try {
			String jsonString = utilityService
					.getPayProjectList(session, getPayProjectListInfo);
			outPrint(response, jsonString);
		} catch (Exception e) {
			try {
				outPrint(response, utilityService
						.getCityListException(getPayProjectListInfo));
			} catch (Exception e1) {
				e1.printStackTrace();
			}
			setSession(session, request.getRemoteAddr(), false);
			logger.info("[app_exception]" + e.fillInStackTrace());
			e.printStackTrace();
		}
	}

	/**
	 * 水煤电缴费单位查询
	 */
	@RequestMapping("/queryPayUnitList")
	public void getPayUnitList(
			HttpServletResponse response, HttpSession session,
			HttpServletRequest request) {
		String getPayUnitListInfo = requestClient(request);
		try {
			String jsonString = utilityService
					.getPayUnitList(session, getPayUnitListInfo);
			outPrint(response, jsonString);
		} catch (Exception e) {
			try {
				outPrint(response, utilityService
						.getCityListException(getPayUnitListInfo));
			} catch (Exception e1) {
				e1.printStackTrace();
			}
			setSession(session, request.getRemoteAddr(), false);
			logger.info("[app_exception]" + e.fillInStackTrace());
			e.printStackTrace();
		}
	}

	/**
	 * 水煤电商品信息查询
	 */
	@RequestMapping("/queryClassId")
	public void queryClassId(
			HttpServletResponse response, HttpSession session,
			HttpServletRequest request) {
		String queryClassIdInfo = requestClient(request);
		try {
			String jsonString = utilityService.queryClassId(session, queryClassIdInfo);
			outPrint(response, jsonString);
		} catch (Exception e) {
			try {
				outPrint(response, utilityService
						.getCityListException(queryClassIdInfo));
			} catch (Exception e1) {
				e1.printStackTrace();
			}
			setSession(session, request.getRemoteAddr(), false);
			logger.info("[app_exception]" + e.fillInStackTrace());
			e.printStackTrace();
		}
	}

	/**
	 * 水电煤账户欠费查询
	 */
	@RequestMapping("/queryBalance")
	public void queryBalance(
			HttpServletResponse response, HttpSession session,
			HttpServletRequest request) {
		String queryBalanceInfo = requestClient(request);
		try {
			String jsonString = utilityService.queryBalance(session, queryBalanceInfo);
			outPrint(response, jsonString);
		} catch (Exception e) {
			try {
				outPrint(response, utilityService
						.getCityListException(queryBalanceInfo));
			} catch (Exception e1) {
				e1.printStackTrace();
			}
			setSession(session, request.getRemoteAddr(), false);
			logger.info("[app_exception]" + e.fillInStackTrace());
			e.printStackTrace();
		}
	}
	
	/**
	 * 缴费详情查看
	 */
	@RequestMapping("/queryPayDetail")
	public void queryPayDetail(
			HttpServletResponse response, HttpSession session,
			HttpServletRequest request) {
		String queryPayDetailInfo = requestClient(request);
		try {
			String jsonString = utilityService.queryPayDetail(session,queryPayDetailInfo);
			outPrint(response, jsonString);
		} catch (Exception e) {
			try {
				outPrint(response, utilityService
						.queryPayDetailException(queryPayDetailInfo));
			} catch (Exception e1) {
				e1.printStackTrace();
			}
			setSession(session, request.getRemoteAddr(), false);
			logger.info("[app_exception]" + e.fillInStackTrace());
			e.printStackTrace();
		}
	}
	
	/**
	 * 水煤电生成订单
	 * @param response
	 */
	@RequestMapping("/producedOrder")
	public void producedOrder(
			HttpServletRequest reuqest,HttpServletResponse response,HttpSession session,HttpServletRequest request){
		String utilityInfo = requestClient(request);
		try {
			String jsonString = utilityService.producedOrder(utilityInfo,session);
			outPrint(response, jsonString);
		} catch (Exception e) {
			try {
				outPrint(response, utilityService.producedOrderException(session));
			} catch (IOException e1) {
				e1.printStackTrace();
			} catch (Exception e1) {
				e1.printStackTrace();
			}
			setSession(session,request.getRemoteAddr(),true);
			logger.info("[app_exception]"+e.fillInStackTrace());
			e.printStackTrace();
		}
	}
	
}
