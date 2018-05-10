package xdt.controller;


import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import xdt.model.SessionInfo;
import xdt.service.IAppErrorLogService;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
/**
 * app系统错误信息
 * @author wumeng
 *
 */
@Controller
@RequestMapping("appErrorLogAction")
public class AppErrorLogAction extends BaseAction{
	@Resource
	private IAppErrorLogService appErrorLogService;
	private Logger logger = Logger.getLogger(AppErrorLogAction.class);
	
	/**
	 * 添加app系统错误信息
	 * @param merchantsFeedback
	 * @param response
	 * @param session
	 */
	@RequestMapping("appErrorLogInsert")
	public void appErrorLogInsert(HttpServletRequest request,HttpServletResponse response,HttpSession session){
		SessionInfo  mercid = (SessionInfo) session.getAttribute(SessionInfo.SESSIONINFO);//得到登录用户session信息
		try {
			String param = requestClient(request);
			appErrorLogService.appErrorLogInsert(param,session,request,mercid.getMercId());
		}catch (Exception e) {
			logger.debug("用户编码："+mercid.getMercId()+"添加app系统错误信息失败！", e);
			e.printStackTrace();
		}
	}
}