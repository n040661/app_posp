package xdt.controller;


import org.apache.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import xdt.service.IPrepaidPhoneService;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

/**
 * 手机充值
 * @author xiaomei
 */
@Controller
@RequestMapping("mobilePhoneRechargeAction")
public class MobilePhoneRechargeAction extends BaseAction {

    @Resource
	private IPrepaidPhoneService prepaidService;//手机充值服务层
    private Logger logger = Logger.getLogger(MobilePhoneRechargeAction.class);
    
    /**
     * 号段查询
     * @param payPhoneAccountInfo
     * @param response
     * @param session
     */
    @RequestMapping("/queryMobilePhoneOperator")
    public void themRoughlyQuery(String payPhoneAccountInfo,HttpServletResponse response,HttpSession session,HttpServletRequest request){
        String param = requestClient(request);
        try {
            String jsonString = prepaidService.themRoughlyQuery(param,session,request);
    		outPrint(response, jsonString);
		} catch (Exception e) {
			try {
				outPrint(response, prepaidService.themRoughlyQueryException(session));
			} catch (IOException e1) {
				e1.printStackTrace();
			} catch (Exception e1) {
				e1.printStackTrace();
			}
			setSession(session,request.getRemoteAddr(),true);
			e.printStackTrace();
		}
    }
    
    /**
	 * 手机充值金额查询
	 * @param response
	 */
	@RequestMapping("/queryRechargeAmtValue")
	public void phoneMoneyQuery( String phoneInfo,HttpServletRequest reuqest,HttpServletResponse response,HttpSession session,HttpServletRequest request){
        String param = requestClient(request);
        try {
			String jsonString = prepaidService.phoneMoneyQuery(param,session);
			outPrint(response, jsonString);
		} catch (Exception e) {
			try {
				outPrint(response, prepaidService.phoneMoneyQueryException(session));
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
    /**
	 * 充值生成订单
	 * @param response
	 */
	@RequestMapping("/producedOrder")
	public void producedOrder(String phoneInfo,HttpServletRequest reuqest,HttpServletResponse response,HttpSession session,HttpServletRequest request){
        String param = requestClient(request);
        try {
			String jsonString = prepaidService.producedOrder(param,session);
			outPrint(response, jsonString);
		} catch (Exception e) {
			try {
				outPrint(response, prepaidService.phoneMoneyQueryException(session));
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