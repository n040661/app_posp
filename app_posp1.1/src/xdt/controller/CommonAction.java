package xdt.controller;

import com.google.gson.Gson;
import xdt.controller.BaseAction;
import xdt.dto.CreditPaymentSignResponseDTO;
import xdt.encode.HttpEncode;
import xdt.model.SessionInfo;
import xdt.service.ICommonService;
import xdt.service.impl.BaseServiceImpl;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * 公共Action
 * @author p
 *
 */
@Controller
@RequestMapping("commonAction")
public class CommonAction extends BaseAction {
	
	@Resource 
	private ICommonService commonService;
	@Resource
	private HttpEncode encode;//加密服务层
	//传递的参数
	private static final String  requestData= "requestData";
	private Logger logger = Logger.getLogger(CommonAction.class);
	
	/**
	 * 刷卡支付 签到
	 * @param account
	 * @param response
	 * @param session
	 * PersonalPaymentAction/SignInAction
	 * PersonalPaymentAction/SignInAction
	 */
	@RequestMapping("signInAction")         
	public void signInAction( String account,HttpServletResponse response,HttpSession session,HttpServletRequest request){
        try {
            String param = requestClient(request);
			String signIn = commonService.creditCardPaymentSignIn(param,session,request);
			outPrint(response, signIn);
		} catch (Exception e) {
			try {
				CreditPaymentSignResponseDTO reseData=new CreditPaymentSignResponseDTO();
				reseData.setRetCode(100);
				reseData.setRetMessage("系统异常");
				Gson gson = new Gson();
				String jsonng = gson.toJson(reseData);
				outPrint(response, jsonng);
			}catch (Exception e1) {
			}
			setSession(session,request.getRemoteAddr(),true);
			logger.info("[app_exception]"+e.fillInStackTrace());
			e.printStackTrace();
		}
	}
	/**
	 * 刷卡状态修改
	 * @param response
	 * @param request
	 * @param session
	 */
	@RequestMapping("conditionAction")
	public void ConditionAction( String account,HttpServletResponse response,HttpServletRequest request,HttpSession session){
		try {
            String param = requestClient(request);
			String status = commonService.creditCardPaymentStatus(param,session, request);
			outPrint(response, status);
		} catch (Exception e) {
			try {
				CreditPaymentSignResponseDTO reseData=new CreditPaymentSignResponseDTO();
				reseData.setRetCode(100);
				reseData.setRetMessage("系统异常");
				Gson gson = new Gson();
				String jsonng = gson.toJson(reseData);
				String jsonstring = encode.createEncode(jsonng);
				outPrint(response, jsonstring);
			} catch (Exception e1) {}
			setSession(session,request.getRemoteAddr(),true);
			logger.info("[app_exception]"+e.fillInStackTrace());
			e.printStackTrace();
		}
	}
}
