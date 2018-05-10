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
import xdt.service.IUserInfoService;
import xdt.service.impl.BaseServiceImpl;

/**
 * 我的收银员
 * 
 * @author lev12
 * 
 */
@Controller
@RequestMapping("userinfoAction")
public class UsrinfoAction extends BaseAction {

	private Logger logger = Logger.getLogger(UsrinfoAction.class);

	@Resource
	private BaseServiceImpl baseServiceImpl;
	@Resource
	private IUserInfoService userInfoService;// 收银员服务层

	/**
	 * 验证手机号
	 * 
	 * @param checkPhone
	 * @param response
	 * @param session
	 */
	@RequestMapping("checkPhone")
	public void checkPhone(HttpServletResponse response, HttpSession session,
			HttpServletRequest request) {
		String checkPhone = requestClient(request);
		try {
			String pos = userInfoService.checkPhone(checkPhone, session,
					request);
			outPrint(response, pos);
		} catch (Exception e) {
			try {
				setSession(session, request.getRemoteAddr(), true);
				baseServiceImpl.insertAppLogs(((SessionInfo) session
						.getAttribute(SessionInfo.SESSIONINFO))
						.getMobilephone().toString(), "1382");
				outPrint(response, userInfoService.checkPhoneException());
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
	 * 我的收银员
	 */
	@RequestMapping("/queryUserinfoList")
	public void queryUserinfoList(HttpServletResponse response,
			HttpSession session, HttpServletRequest request) {
		String queryUserinfo = requestClient(request);
		try {
			String jsonString = userInfoService.queryUserinfoList(
					queryUserinfo, session, request);
			outPrint(response, jsonString);
		} catch (Exception e) {
			try {
				outPrint(response, userInfoService.queryUserinfoListException());
			} catch (Exception e1) {
				e1.printStackTrace();
			}
			setSession(session, request.getRemoteAddr(), false);
			logger.info("[app_exception]" + e.fillInStackTrace());
			e.printStackTrace();
		}
	}

	/**
	 * 添加账户
	 * 
	 * @param userInfo
	 * @param response
	 * @param session
	 */
	@RequestMapping("addUserinfo")
	public void addUserinfo(HttpServletResponse response, HttpSession session,
			HttpServletRequest request) {
		String userinfo = requestClient(request);
		try {
			String pos = userInfoService
					.addUserinfo(userinfo, session, request);
			outPrint(response, pos);
		} catch (Exception e) {
			try {
				setSession(session, request.getRemoteAddr(), true);
				baseServiceImpl.insertAppLogs(((SessionInfo) session
						.getAttribute(SessionInfo.SESSIONINFO))
						.getMobilephone().toString(), "1382");
				outPrint(response, userInfoService.addUserinfoException());
			} catch (IOException e1) {
				e1.printStackTrace();
			} catch (Exception e1) {
				e1.printStackTrace();
			}
			logger.info("[app_exception]" + e.fillInStackTrace());
			e.printStackTrace();
		}
	}

}
