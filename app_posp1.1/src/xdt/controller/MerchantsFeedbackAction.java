package xdt.controller;

import java.io.IOException;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import xdt.model.SessionInfo;
import xdt.service.IMerchantsFeedbackService;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
/**
 * 商户反馈信息
 * @author wumeng
 *
 */
@Controller
@RequestMapping("merchantsFeedbackAction")
public class MerchantsFeedbackAction extends BaseAction{
	@Resource
	private IMerchantsFeedbackService merchantsFeedbackService;
	private Logger logger = Logger.getLogger(MerchantsFeedbackAction.class);
	
	/**
	 * 添加用户反馈信息
	 * @param merchantsFeedback
	 * @param response
	 * @param session
	 */
	@RequestMapping("merchantFeedbackInsert")
	public void merchantInsert(HttpServletRequest request,HttpServletResponse response,HttpSession session){
		SessionInfo  sessionInfo = (SessionInfo) session.getAttribute(SessionInfo.SESSIONINFO);//得到登录用户session信息
		try {
			String param = requestClient(request);
			outPrint(response,merchantsFeedbackService.merchantFeedback(param,sessionInfo));
		}catch (Exception e) {
			try {
				outPrint(response, merchantsFeedbackService.merchantFeedbackException(session));
			} catch (IOException e1) {
				e1.printStackTrace();
			} catch (Exception e1) {
				e1.printStackTrace();
			}
			setSession(session,request.getRemoteAddr(),true);
			logger.debug("用户编码："+sessionInfo.getMercId()+"添加用户反馈信息失败！", e);
			e.printStackTrace();
		}
	}
}