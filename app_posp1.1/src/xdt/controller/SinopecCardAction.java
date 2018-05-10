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

import xdt.service.ISinopecCardService;
import xdt.service.IUtilityService;

/**
 * 加油卡
 * 
 * @author lev12
 * 
 */
@Controller
@RequestMapping("gasCardAction")
public class SinopecCardAction extends BaseAction {

	private Logger logger = Logger.getLogger(SinopecCardAction.class);

	@Resource
	private ISinopecCardService sinopecCardService;

	/**
	 * 中石化加油卡卡号信息查询
	 */
	@RequestMapping("/queryGasCardDetail")
	public void queryCardInfo(
			HttpServletResponse response, HttpSession session,
			HttpServletRequest request) {
		String queryCardInfo = requestClient(request);
		try {
			String jsonString = sinopecCardService.queryCardInfo(session,
					queryCardInfo);
			outPrint(response, jsonString);
		} catch (Exception e) {
			try {
				outPrint(response, sinopecCardService
						.queryCardInfoException(queryCardInfo));
			} catch (Exception e1) {
				e1.printStackTrace();
			}
			setSession(session, request.getRemoteAddr(), false);
			logger.info("[app_exception]" + e.fillInStackTrace());
			e.printStackTrace();
		}
	}

	/**
	 * 加油卡生成订单
	 * 
	 * @param response
	 */
	@RequestMapping("/producedOrder")
	public void producedOrder(
			HttpServletRequest reuqest, HttpServletResponse response,
			HttpSession session, HttpServletRequest request) {
		String sinopecCardInfo = requestClient(request);
		try {
			String jsonString = sinopecCardService.producedOrder(
					sinopecCardInfo, session);
			outPrint(response, jsonString);
		} catch (Exception e) {
			try {
				outPrint(response, sinopecCardService
						.producedOrderException(session));
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
