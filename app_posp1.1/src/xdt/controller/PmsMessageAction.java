package xdt.controller;

import xdt.controller.BaseAction;
import xdt.model.PmsMessage;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import xdt.service.IPmsMessageService;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.math.BigDecimal;

/**
 * 获取短信验证码
 * @author Jeff
 *
 */
@Controller
@RequestMapping("pmsMessageAction")
public class PmsMessageAction extends BaseAction{
	
	@Resource
	private IPmsMessageService messageService; //调用短信通道信息服务层
	private Logger logger = Logger.getLogger(PmsMessageAction.class);

	/**
	 * 验证短信验证码
	 * @param response
	 */
	@RequestMapping("/captchaValidation")
	public void captchaValidation(HttpServletResponse response,HttpSession session,HttpServletRequest request){
		String captchaValidationInfo = requestClient(request);
		try {
			String jsonString = messageService.captchaValidation(captchaValidationInfo,session,request);
			outPrint(response,jsonString);
		} catch (Exception e) {
			try {
				outPrint(response, messageService.captchaValidationException(captchaValidationInfo));
			} catch (IOException e1) {
				e1.printStackTrace();
			} catch (Exception e1) {
				e1.printStackTrace();
			}
			setSession(session,request.getRemoteAddr(),false);
			logger.info("[app_exception]"+e.fillInStackTrace());
			e.printStackTrace();
		}
	}
	

}