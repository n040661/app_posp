package xdt.controller;

import xdt.service.ICreditCardPaymentsService;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

/**
 * 信用卡还款
 * @author Jeff
 */
@Controller
@RequestMapping("creditCardAction")
public class CreditCardAction extends BaseAction {

	@Resource
	private ICreditCardPaymentsService creditCardPaymentsService;//信用卡还款服务层
	private Logger logger = Logger.getLogger(CreditCardAction.class);
	
	/**
	 * 添加信用卡
	 * @param addCreditCardInfo
	 * @param response
	 * @param session
	 */
	@RequestMapping("/addCreditCard")
	public void addCreditCard( String addCreditCardInfo,HttpServletResponse response,HttpSession session,HttpServletRequest request){
		try {
            String param = requestClient(request);
			outPrint(response, creditCardPaymentsService.addCreditCard(param,session,request));
		} catch (Exception e) {
			try {
                String param = requestClient(request);
				outPrint(response, creditCardPaymentsService.addCreditCardException(session));
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
	 * 信用卡还款，生成订单
	 * @param produceInfo
	 * @param response
	 * @param session
	 */
	@RequestMapping("/producedOrder")
	public void producedOrder( String produceInfo,HttpServletResponse response,HttpSession session,HttpServletRequest request){
		try {
            String param = requestClient(request);
			outPrint(response, creditCardPaymentsService.pruduceOrder(param,session,request));
		} catch (Exception e) {
			try {
                String param = requestClient(request);
				outPrint(response, creditCardPaymentsService.addCreditCardException(session));
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
