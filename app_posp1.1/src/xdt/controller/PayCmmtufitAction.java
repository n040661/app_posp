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

import xdt.service.IPayCmmtufitService;

/**
 * 银行卡信息
 * @author lev12
 *
 */
@Controller
@RequestMapping("payCmmtufitAction")
public class PayCmmtufitAction extends BaseAction{
	
	@Resource
	private IPayCmmtufitService payCmmtufitService;//银行卡服务层
	private Logger logger = Logger.getLogger(PayCmmtufitAction.class);
	
	/**
	 * 根据前6位数字检索银行卡信息
	 * @param beforeSixCardNumber
	 * @param response
	 * @param session
	 */
	@RequestMapping("/searchBankCardByBeforeSix")
	public void searchCardListByBeforeSix(
			HttpServletRequest request,
			HttpServletResponse response,HttpSession session){
		String beforeSixCardNumber = requestClient(request);
		try {
			String jsonString = payCmmtufitService.searchCardListByBeforeSix(beforeSixCardNumber,session);
			outPrint(response, jsonString);
		} catch (Exception e) {
			logger.info("[app_exception]"+e.fillInStackTrace());
			e.printStackTrace();
		}
	}
	
	/**
	 * 检索银行信息列表
	 * @param response
	 * @param session
	 */
	@RequestMapping("/searchBankList")
	public void searchBankList(
			HttpServletResponse response,HttpSession session,HttpServletRequest request){
		String pageInfo = requestClient(request);
		try {
			String jsonString = payCmmtufitService.searchBankList(pageInfo,session);
			outPrint(response, jsonString);
		} catch (Exception e) {
			try {
				outPrint(response,payCmmtufitService.searchBankListException(session));
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
