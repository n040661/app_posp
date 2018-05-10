package xdt.controller;

import java.io.IOException;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import xdt.model.SessionInfo;
import xdt.service.IAppRateConfigService;
import xdt.service.IPmsDictionaryService;
import xdt.service.IPmsMerchantInfoService;
import xdt.service.IPmsMerchantPosService;
import xdt.service.IPmsPosInfoService;
import xdt.service.impl.BaseServiceImpl;

/**
 * 商户信息
 * 
 * @author Jeff
 * 
 */
@Controller
@RequestMapping("pmsMerchantInfoAction")
public class PmsMerchantInfoAction extends BaseAction {

	@Resource
	private BaseServiceImpl baseServiceImpl;
	@Resource
	private IPmsPosInfoService tbusinessPS;
	@Resource
	private IPmsMerchantInfoService pmsMerchantInfoService;// 商户信息服务层
	@Resource
	private IPmsMerchantPosService pmsMerchantPosService;// 商户pos信息服务层
	@Resource
	private IAppRateConfigService appRateConfigService;// 查询费率服务层
	@Resource
	private IPmsDictionaryService pmsDictionaryService;// 数据字典服务层
	private Logger logger = Logger.getLogger(PmsMerchantInfoAction.class);

	/**
	 * 商户注册
	 * 
	 * @param merchantRegisterInfo
	 *            商户在客户端填写的注册信息
	 * @param response
	 */
	@RequestMapping("/merchantRegister")
	public void merchantRegister(String merchantRegisterInfo,
			HttpServletResponse response, HttpSession session,
			HttpServletRequest request) {
		String param = requestClient(request);
		try {
			String jsonString = pmsMerchantInfoService.merchantRegister(param,
					session, request);
			outPrint(response, jsonString);
		} catch (Exception e) {
			try {
				outPrint(response, pmsMerchantInfoService
						.merchantRegisterException(param));
			} catch (IOException e1) {
				e1.printStackTrace();
			} catch (Exception e1) {
				e1.printStackTrace();
			}
			setSession(session, request.getRemoteAddr(), false);
			logger.info("[app_exception]" + e.fillInStackTrace());
			e.printStackTrace();
		}
	}

	/**
	 * 商户登录
	 * 
	 * @param merchantLoginInfo
	 *            商户在客户端填写的登录信息
	 * @param response
	 */
	@RequestMapping("/merchantLogin")
	public void merchantLogin(HttpServletRequest request,
			HttpServletResponse response, HttpSession session) {
		String merchantLoginInfo = requestClient(request);
		try {
			String jsonString = pmsMerchantInfoService.merchantLogin(
					merchantLoginInfo, session, request);
			outPrint(response, jsonString);
		} catch (Exception e) {
			try {
				outPrint(response, pmsMerchantInfoService
						.merchantLoginException(merchantLoginInfo));
			} catch (IOException e1) {
				e1.printStackTrace();
			} catch (Exception e1) {
				e1.printStackTrace();
			}
			setSession(session, request.getRemoteAddr(), false);
			logger.info("[app_exception]" + e.fillInStackTrace());
			e.printStackTrace();
		}
	}

	/**
	 * 找回密码验证确认
	 * 
	 * @param retrievePasswordValidationConfirmInfo
	 * @param response
	 */
	@RequestMapping("/retrievePasswordValidationConfirm")
	public void retrievePasswordValidationConfirm(HttpServletResponse response,
			HttpSession session, HttpServletRequest request) {
		String retrievePasswordValidationConfirmInfo = requestClient(request);
		try {
			String jsonString = pmsMerchantInfoService
					.retrievePasswordValidationConfirm(
							retrievePasswordValidationConfirmInfo, request);
			outPrint(response, jsonString);
		} catch (Exception e) {
			try {
				outPrint(
						response,
						pmsMerchantInfoService
								.retrievePasswordValidationConfirmException(retrievePasswordValidationConfirmInfo));
			} catch (IOException e1) {
				e1.printStackTrace();
			} catch (Exception e1) {
				e1.printStackTrace();
			}
			setSession(session, request.getRemoteAddr(), false);
			logger.info("[app_exception]" + e.fillInStackTrace());
			e.printStackTrace();
		}
	}

	/**
	 * 修改密码
	 * 
	 * @param changePasswordInfo
	 *            商户在客户端填写的找回密码信息
	 * @param response
	 */
	@RequestMapping("/changePassword")
	public void changePassword(HttpServletResponse response,
			HttpSession session, HttpServletRequest request) {
		String changePasswordInfo = requestClient(request);
		try {
			String jsonString = pmsMerchantInfoService.updatePassword(
					changePasswordInfo, session, request);
			outPrint(response, jsonString);
		} catch (Exception e) {
			try {
				outPrint(response, pmsMerchantInfoService
						.updatePasswordException(changePasswordInfo));
			} catch (IOException e1) {
				e1.printStackTrace();
			} catch (Exception e1) {
				e1.printStackTrace();
			}
			setSession(session, request.getRemoteAddr(), false);
			logger.info("[app_exception]" + e.fillInStackTrace());
			e.printStackTrace();
		}
	}

	/**
	 * 检索商户实名认证信息
	 * 
	 * @param realNameAuthenticationInfo
	 * @param response
	 */
	@RequestMapping("/searchRealNameAuthenticationInformation")
	public void searchRealNameAuthenticationInformation(
			HttpServletResponse response, HttpSession session,
			HttpServletRequest request) {
		String realNameAuthenticationInfo = requestClient(request);
		try {
			String jsonString = pmsMerchantInfoService
					.searchRealNameAuthenticationInformation(
							realNameAuthenticationInfo, session, request);
			outPrint(response, jsonString);
		} catch (Exception e) {
			try {
				outPrint(
						response,
						pmsMerchantInfoService
								.searchRealNameAuthenticationInformationException(realNameAuthenticationInfo));
			} catch (IOException e1){
				e1.printStackTrace();
			} catch (Exception e1) {
				e1.printStackTrace();
			}
			setSession(session, request.getRemoteAddr(), true);
			logger.info("[app_exception]" + e.fillInStackTrace());
			e.printStackTrace();
		}
	}

	/**
	 * 保存实名认证信息
	 * 
	 * @param realNameAuthenticationInfo
	 * @param response
	 */
	@RequestMapping("/saveRealNameAuthenticationInformation")
	public void saveRealNameAuthenticationInformation(
			HttpServletResponse response, HttpSession session,
			HttpServletRequest request) {
		String realNameAuthenticationInfo = requestClient(request);
		try {
			String jsonString = pmsMerchantInfoService
					.saveRealNameAuthenticationInformation(
							realNameAuthenticationInfo, session, request);
			outPrint(response, jsonString);
		} catch (Exception e) {
			try {
				outPrint(
						response,
						pmsMerchantInfoService
								.saveRealNameAuthenticationInformationException(session));
			} catch (IOException e1) {
				e1.printStackTrace();
			} catch (Exception e1) {
				e1.printStackTrace();
			}
			setSession(session, request.getRemoteAddr(), true);
			logger.info("[app_exception]" + e.fillInStackTrace());
			e.printStackTrace();
		}
	}

	/**
	 * 我的设备查询
	 * 
	 * @param merchantRegisterInfo
	 * @param response
	 */
	@RequestMapping("queryMineDevice")
	public void queryMineDevice(HttpServletResponse response,
			HttpSession session, HttpServletRequest request) {
		try {
			String pos = pmsMerchantPosService.selectPmsMerchantPos(session,
					request);
			outPrint(response, pos);
		} catch (Exception e) {
			try {
				setSession(session, request.getRemoteAddr(), true);
				baseServiceImpl.insertAppLogs(((SessionInfo) session
						.getAttribute(SessionInfo.SESSIONINFO))
						.getMobilephone().toString(), "1383");
				outPrint(response, pmsMerchantPosService
						.selectPmsMerchantPosException());
			} catch (Exception e1) {
				e1.printStackTrace();
			}
			logger.info("[app_exception]" + e.fillInStackTrace());
			e.printStackTrace();
		}
	}

	/**
	 * 通过sn号查询出相关的数据，如果已经被绑定判断是不是本用户绑定的，没有则进行绑定
	 * 
	 * @param Tbusine
	 * @param response
	 * @param session
	 */
	@RequestMapping("addMineDevice")
	public void addMineDevice(HttpServletResponse response,
			HttpSession session, HttpServletRequest request) {
		String tbusine = requestClient(request);
		try {
			String pos = tbusinessPS.addMineDevice(tbusine, session,request);
			outPrint(response, pos);
		} catch (Exception e) {
			try {
				setSession(session, request.getRemoteAddr(), true);
				baseServiceImpl.insertAppLogs(((SessionInfo) session
						.getAttribute(SessionInfo.SESSIONINFO))
						.getMobilephone().toString(), "1382");
				outPrint(response, tbusinessPS.addMineDeviceException());
			} catch (IOException e1) {
				e1.printStackTrace();
			} catch (Exception e1) {
				e1.printStackTrace();
			}
			logger.info("[app_exception]" + e.fillInStackTrace());
			e.printStackTrace();
		}
	}

	/**
	 * 通过posId号查询出相关的数据，如果已经被绑定，则进行解绑
	 * 
	 * @param pmsMerchantInfo
	 * @param response
	 * @param session
	 */
	@RequestMapping("delMineDevice")
	public void delMineDevice(HttpServletResponse response,
			HttpSession session, HttpServletRequest request) {
		String pmsMerchantInfo = requestClient(request);
		try {
			String pos = pmsMerchantPosService.updateMineDevice(
					pmsMerchantInfo, session, request);
			outPrint(response, pos);
		} catch (Exception e) {
			try {
				setSession(session, request.getRemoteAddr(), true);
				baseServiceImpl.insertAppLogs(((SessionInfo) session
						.getAttribute(SessionInfo.SESSIONINFO))
						.getMobilephone().toString(), "1382");
				outPrint(response, pmsMerchantPosService
						.updateMineDeviceException());
			} catch (IOException e1) {
				e1.printStackTrace();
			} catch (Exception e1) {
				e1.printStackTrace();
			}
			logger.info("[app_exception]" + e.fillInStackTrace());
			e.printStackTrace();
		}
	}

	/**
	 * 确认设备认证
	 * 
	 * @param pmsMerchantInfo
	 * @param response
	 * @param session
	 */
	@RequestMapping("confirmDevice")
	public void confirmDevice(HttpServletResponse response,
			HttpSession session, HttpServletRequest request) {
		String pmsMerchantInfo = requestClient(request);
		try {
			String pos = pmsMerchantPosService.confirmDevice(pmsMerchantInfo,
					session, request);
			outPrint(response, pos);
		} catch (Exception e) {
			try {
				setSession(session, request.getRemoteAddr(), true);
				baseServiceImpl.insertAppLogs(((SessionInfo) session
						.getAttribute(SessionInfo.SESSIONINFO))
						.getMobilephone().toString(), "1382");
				outPrint(response, pmsMerchantPosService
						.confirmDeviceException());
			} catch (IOException e1) {
				e1.printStackTrace();
			} catch (Exception e1) {
				e1.printStackTrace();
			}
			logger.info("[app_exception]" + e.fillInStackTrace());
			e.printStackTrace();
		}
	}

	/**
	 * 修改密码验证确认
	 * 
	 * @param changePasswordValidationConfirmInfo
	 * @param response
	 */
	@RequestMapping("/changePasswordValidationConfirm")
	public void changePasswordValidationConfirm(HttpServletResponse response,
			HttpSession session, HttpServletRequest request) {
		String changePasswordValidationConfirmInfo = requestClient(request);
		try {
			String jsonString = pmsMerchantInfoService
					.changePasswordValidationConfirm(
							changePasswordValidationConfirmInfo, session,
							request);
			outPrint(response, jsonString);
		} catch (Exception e) {
			try {
				outPrint(
						response,
						pmsMerchantInfoService
								.changePasswordValidationConfirmException(changePasswordValidationConfirmInfo));
			} catch (IOException e1) {
				e1.printStackTrace();
			} catch (Exception e1) {
				e1.printStackTrace();
			}
			setSession(session, request.getRemoteAddr(), true);
			logger.info("[app_exception]" + e.fillInStackTrace());
			e.printStackTrace();
		}
	}

	/**
	 * 查询刷卡费率
	 * 
	 * @param response
	 */
	@RequestMapping("/appRateConfigQuery")
	public void appRateConfigQuery(HttpServletResponse response,
			HttpSession session, HttpServletRequest request) {
		try {
			String jsonString = appRateConfigService.appRateConfigQuery(session);
			outPrint(response, jsonString);
		} catch (Exception e) {
			try {
				outPrint(response, appRateConfigService
						.appRateConfigQueryException());
			} catch (IOException e1) {
				e1.printStackTrace();
			} catch (Exception e1) {
				e1.printStackTrace();
			}
			setSession(session, request.getRemoteAddr(), true);
			logger.info("[app_exception]" + e.fillInStackTrace());
			e.printStackTrace();
		}
	}

	/**
	 * 查询证件类型
	 * 
	 * @param response
	 */
	@RequestMapping("/cardTypeQuery")
	public void cardTypeQuery(HttpServletResponse response,
			HttpSession session, HttpServletRequest request) {
		try {
			String jsonString = pmsDictionaryService.cardTypeQuery();
			outPrint(response, jsonString);
		} catch (Exception e) {
			try {
				outPrint(response, pmsDictionaryService
						.cardTypeQueryException());
			} catch (IOException e1) {
				e1.printStackTrace();
			} catch (Exception e1) {
				e1.printStackTrace();
			}
			setSession(session, request.getRemoteAddr(), true);
			logger.info("[app_exception]" + e.fillInStackTrace());
			e.printStackTrace();
		}
	}
}