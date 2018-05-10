package xdt.controller;

import java.io.IOException;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import xdt.dto.DrawMoneyAccRequestAndResponseDTO;
import xdt.dto.MroducedTwoDimensionResponseDTO;
import xdt.model.SessionInfo;
import xdt.service.IMerchantCollectMoneyService;
import xdt.service.IMerchantMineService;

import com.google.gson.Gson;



/**
 * 商户个人账户信息 action
 * wumeng 20150525
 */
@Controller
@RequestMapping("merchantMineAction")
public class MerchantMineAction extends BaseAction{

	
	private Logger logger = Logger.getLogger(MerchantCollectMoneyAction.class);
	
	@Resource
	private IMerchantMineService  merchantMineService;// 百度 service
	@Resource
	private IMerchantCollectMoneyService  merchantCollectMoneyService;// 百度 service
	
	
	/**
	 * 查看商户在客户端显示图片、手机号、账户金额等 个人信息
	 * wumeng  20150525
	 * @param param
	 * @param response
	 * @param session
	 */
	@RequestMapping("/queryMineAcc")
	public void queryMineAcc(HttpServletResponse response,HttpSession session,HttpServletRequest request){
		SessionInfo  sessionInfo = (SessionInfo) session.getAttribute(SessionInfo.SESSIONINFO);//得到登录用户session信息
		try {
			String param = requestClient(request);
			if(sessionInfo==null){//判断用户会话时候过期过期后请重新登录在进行
				MroducedTwoDimensionResponseDTO mroducedTwoDimensionResultDTO = new MroducedTwoDimensionResponseDTO();
				mroducedTwoDimensionResultDTO.setRetCode("13");//会话过期13
				mroducedTwoDimensionResultDTO.setRetMessage("会话过期，请重新登录。");
				Gson gson = new Gson();
				outPrint(response,gson.toJson(mroducedTwoDimensionResultDTO));
			}else{
				logger.info("用户编码："+sessionInfo.getMercId()+"查看账户信息接口调用开始");
				outPrint(response, merchantMineService.queryMineAcc(param,sessionInfo));
				logger.info("用户编码："+sessionInfo.getMercId()+"查看账户信息接口调用结束");
			}
		} catch (Exception e) {
			try {
				outPrint(response, merchantCollectMoneyService.exceptUtility(session));
			} catch (IOException e1) {
				e1.printStackTrace();
			} catch (Exception e1) {
				e1.printStackTrace();
			}
			setSession(session,request.getRemoteAddr(),true);
			logger.info("用户编码："+sessionInfo.getMercId()+"查看账户信息接口调用失败！", e);
			e.printStackTrace();
		}
	}
	
	
	
	
	

	/**
	 * 商户在客户端查询绑定的卡信息   提现页面显示使用
	 * wumeng  20150525
	 * @param param
	 * @param response
	 * @param session
	 */
	@RequestMapping("/queryDrawMoneyAcc")
	public void queryDrawMoneyAcc(HttpServletResponse response,HttpSession session,HttpServletRequest request){
		SessionInfo  sessionInfo = (SessionInfo) session.getAttribute(SessionInfo.SESSIONINFO);//得到登录用户session信息
		try {
			String param = requestClient(request);
			if(sessionInfo==null){//判断用户会话时候过期过期后请重新登录在进行
				MroducedTwoDimensionResponseDTO mroducedTwoDimensionResultDTO = new MroducedTwoDimensionResponseDTO();
				mroducedTwoDimensionResultDTO.setRetCode("13");//会话过期13
				mroducedTwoDimensionResultDTO.setRetMessage("会话过期，请重新登录。");
				Gson gson = new Gson();
				outPrint(response,gson.toJson(mroducedTwoDimensionResultDTO));
			}else{
				logger.info("用户编码："+sessionInfo.getMercId()+"商户在客户端查询绑定的卡信息接口调用开始");
				outPrint(response, merchantMineService.queryDrawMoneyAcc(param,sessionInfo));
				logger.info("用户编码："+sessionInfo.getMercId()+"商户在客户端查询绑定的卡信息接口调用结束");
			}
		} catch (Exception e) {
			try {
				outPrint(response, merchantCollectMoneyService.exceptUtility(session));
			} catch (IOException e1) {
				e1.printStackTrace();
			} catch (Exception e1) {
				e1.printStackTrace();
			}
			setSession(session,request.getRemoteAddr(),true);
			logger.info("用户编码："+sessionInfo.getMercId()+"商户在客户端查询绑定的卡信息接口调用失败！", e);
			e.printStackTrace();
		}
	}
	
	
	/**
	 * 商户把钱款现到绑定的卡上操作   提现
	 * wumeng  20150525
	 * @param param
	 * @param response
	 * @param session
	 */
	@RequestMapping("/confirmDrawMoneyAcc")
	public void confirmDrawMoneyAcc(HttpServletResponse response,HttpSession session,HttpServletRequest request){
		SessionInfo  sessionInfo = (SessionInfo) session.getAttribute(SessionInfo.SESSIONINFO);//得到登录用户session信息
		try {
			String param = requestClient(request);
			if(sessionInfo==null){//判断用户会话时候过期过期后请重新登录在进行
				MroducedTwoDimensionResponseDTO mroducedTwoDimensionResultDTO = new MroducedTwoDimensionResponseDTO();
				mroducedTwoDimensionResultDTO.setRetCode("13");//会话过期13
				mroducedTwoDimensionResultDTO.setRetMessage("会话过期，请重新登录。");
				Gson gson = new Gson();
				outPrint(response,gson.toJson(mroducedTwoDimensionResultDTO));
			}else{
				logger.info("用户编码："+sessionInfo.getMercId()+"商户在客户端调用提现接口调用开始");
				
				synchronized(this) {
					String  result ="";
					//第一步生成订单并添加流水
					
					DrawMoneyAccRequestAndResponseDTO drawMoneyAccRequestAndResponseDTO =  merchantMineService.insertDrawMoneyAcc(param,sessionInfo);
					
					/*//第二步处理余额提现   
					if("0".equals(drawMoneyAccRequestAndResponseDTO.getRetCode())){//第一步  生成订单  添加流水   提款数据添加成功
						
						result = merchantMineService.confirmDrawMoneyAcc(sessionInfo.getMercId(), drawMoneyAccRequestAndResponseDTO.getPmsAppTransInfo());
						
					}else{
						result = new Gson().toJson(drawMoneyAccRequestAndResponseDTO);
					}*/
					result = new Gson().toJson(drawMoneyAccRequestAndResponseDTO);
					outPrint(response,result);
					
				}
				
				logger.info("用户编码："+sessionInfo.getMercId()+"商户在客户端调用提现接口调用结束");
			}
		} catch (Exception e) {
			try {
				outPrint(response, merchantCollectMoneyService.exceptUtility(session));
			} catch (IOException e1) {
				e1.printStackTrace();
			} catch (Exception e1) {
				e1.printStackTrace();
			}
			setSession(session,request.getRemoteAddr(),true);
			logger.info("用户编码："+sessionInfo.getMercId()+"商户在客户端调用提现接口调用失败！", e);
			e.printStackTrace();
		}
	}
	
	
	/**
	 * 获取业务信息   最大值、最小值、费率
	 * wumeng  20150626
	 * @param param
	 * @param response
	 * @param session
	 */
	@RequestMapping("/queryBusinessInfo")
	public void queryBusinessInfo(HttpServletResponse response,HttpSession session,HttpServletRequest request){
		SessionInfo  sessionInfo = (SessionInfo) session.getAttribute(SessionInfo.SESSIONINFO);//得到登录用户session信息
		try {
			String param = requestClient(request);
			if(sessionInfo==null){//判断用户会话时候过期过期后请重新登录在进行
				MroducedTwoDimensionResponseDTO mroducedTwoDimensionResultDTO = new MroducedTwoDimensionResponseDTO();
				mroducedTwoDimensionResultDTO.setRetCode("13");//会话过期13
				mroducedTwoDimensionResultDTO.setRetMessage("会话过期，请重新登录。");
				Gson gson = new Gson();
				outPrint(response,gson.toJson(mroducedTwoDimensionResultDTO));
			}else{
				logger.info("用户编码："+sessionInfo.getMercId()+"获取业务信息查询接口调用开始");
				outPrint(response, merchantMineService.queryBusinessInfo(param,sessionInfo));
				logger.info("用户编码："+sessionInfo.getMercId()+"获取业务信息查询接口调用结束");
			}
		} catch (Exception e) {
			try {
				outPrint(response, merchantCollectMoneyService.exceptUtility(session));
			} catch (IOException e1) {
				e1.printStackTrace();
			} catch (Exception e1) {
				e1.printStackTrace();
			}
			setSession(session,request.getRemoteAddr(),true);
			logger.info("用户编码："+sessionInfo.getMercId()+"获取业务信息查询接口调用失败！", e);
			e.printStackTrace();
		}
	}
	
	
	
	/**
	 * 微信公众号信息获取
	 * wumeng  20150831
	 * @param param
	 * @param response
	 * @param session
	 */
	@RequestMapping("/queryWechatPublicNo")
	public void queryWechatPublicNo(HttpServletResponse response,HttpSession session,HttpServletRequest request){
		SessionInfo  sessionInfo = (SessionInfo) session.getAttribute(SessionInfo.SESSIONINFO);//得到登录用户session信息
		try {
			String param = requestClient(request);
			if(sessionInfo==null){//判断用户会话时候过期过期后请重新登录在进行
				MroducedTwoDimensionResponseDTO mroducedTwoDimensionResultDTO = new MroducedTwoDimensionResponseDTO();
				mroducedTwoDimensionResultDTO.setRetCode("13");//会话过期13
				mroducedTwoDimensionResultDTO.setRetMessage("会话过期，请重新登录。");
				Gson gson = new Gson();
				outPrint(response,gson.toJson(mroducedTwoDimensionResultDTO));
			}else{
				logger.info("用户编码："+sessionInfo.getMercId()+"微信公众号信息获取接口调用开始");
				outPrint(response, merchantMineService.queryWechatPublicNo(param,sessionInfo));
				logger.info("用户编码："+sessionInfo.getMercId()+"微信公众号信息获取接口调用结束");
			}
		} catch (Exception e) {
			try {
				outPrint(response, merchantCollectMoneyService.exceptUtility(session));
			} catch (IOException e1) {
				e1.printStackTrace();
			} catch (Exception e1) {
				e1.printStackTrace();
			}
			setSession(session,request.getRemoteAddr(),true);
			logger.info("用户编码："+sessionInfo.getMercId()+"微信公众号信息获取接口调用失败！", e);
			e.printStackTrace();
		}
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	/**
	 * 开发测试  以后删掉
	 */
	@RequestMapping("/testAction")
	public void baiduResult1(){
		try {
			//merchantMineService.insertDrawMoneyAcc(null, null, null, null);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
	
	
	
}
