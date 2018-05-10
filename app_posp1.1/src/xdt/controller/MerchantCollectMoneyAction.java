package xdt.controller;

import com.google.gson.Gson;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;
import xdt.dao.IPmsAgentInfoDao;
import xdt.dto.BaiduBackRequestDTO;
import xdt.dto.MroducedTwoDimensionResponseDTO;
import xdt.dto.OffiBackRequestDTO;
import xdt.dto.PayCardResponseDTO;
import xdt.model.PmsAgentInfo;
import xdt.model.SessionInfo;
import xdt.service.IMerchantCollectMoneyService;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

/**
 * 商户收款 action
 * wumeng 20150506
 */
@Controller
@RequestMapping("merchantCollectMoneyAction")
public class MerchantCollectMoneyAction extends BaseAction{
	

	@Resource
	private IMerchantCollectMoneyService  merchantCollectMoneyService;// 百度 service
	private Logger logger = Logger.getLogger(MerchantCollectMoneyAction.class);
	@Resource
	private IPmsAgentInfoDao pmsAgentInfoDao;

	/**
	 * 生成二维码    JSON  or image
	 * wumeng  20150506
	 * @param request
	 * @param response
	 * @param session
	 */
	@RequestMapping("/producedTwoDimension")
	public void producedTwoDimension(HttpServletResponse response,HttpSession session,HttpServletRequest request){
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
				logger.info("用户编码："+sessionInfo.getMercId()+"生成二维码接口调用开始");
				String result = "";
				//第一步生成订单并添加流水
				
				MroducedTwoDimensionResponseDTO mroducedTwoDimensionResponseDTO =merchantCollectMoneyService.producedTwoDimension(param,sessionInfo);
				
				//第二步处理主要逻辑
				if("0".equals(mroducedTwoDimensionResponseDTO.getRetCode())){
					
					result = merchantCollectMoneyService.producedTwoDimension(mroducedTwoDimensionResponseDTO);
					
				}else{
					result = new Gson().toJson(mroducedTwoDimensionResponseDTO);
				}
				
				outPrint(response,result);
				logger.info("用户编码："+sessionInfo.getMercId()+"生成二维码接口调用结束");
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
			logger.info("用户编码："+sessionInfo.getMercId()+"生成二维码接口调用失败！", e);
			e.printStackTrace();
		}
	}
	
	
	
	/**
	 * 反扫    用户扫商户    
	 * wumeng  20150506
	 * @param request
	 * @param response
	 * @param session
	 */
	@RequestMapping("/producedScanCodeOrder")
	public void producedScanCodeOrder(HttpServletResponse response,HttpSession session,HttpServletRequest request){
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
				logger.info("用户编码："+sessionInfo.getMercId()+"扫码（用户扫商户）支付接口调用开始");	
				
				String result = "";
				//第一步生成订单并添加流水
				MroducedTwoDimensionResponseDTO mroducedTwoDimensionResponseDTO =merchantCollectMoneyService.producedScanCodeOrder(param,sessionInfo);

				//第二步处理主要逻辑
				if("0".equals(mroducedTwoDimensionResponseDTO.getRetCode())){
					
					result = merchantCollectMoneyService.producedScanCodeOrder(mroducedTwoDimensionResponseDTO);
					
				}else{
					result = new Gson().toJson(mroducedTwoDimensionResponseDTO);
				}
				
				outPrint(response,result);
				
				logger.info("用户编码："+sessionInfo.getMercId()+"扫码（用户扫商户）支付接口调用结束");	
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
			logger.info("用户编码："+sessionInfo.getMercId()+"扫码（用户扫商户）支付接口调用失败！", e);
			e.printStackTrace();
		}
	}
	
	
	/**
	 * 刷卡收款    
	 * wumeng  20150515
	 * @param response
	 * @param session
	 */
	@RequestMapping("/submitOrderPay")
	public void submitOrderPay(HttpServletResponse response,HttpSession session,HttpServletRequest request){
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
			logger.info("用户编码："+sessionInfo.getMercId()+"调用刷卡收款开始");	
			
			String  result ="";
			
			//先判断O单类型      是T0还是T1的走不通的方法刷卡
			
			PmsAgentInfo pmsAgentInfo = pmsAgentInfoDao.selectOagentByMercNum(sessionInfo.getMercId());
			
			String  clearType = pmsAgentInfo.getClearType();//清算类型 ：0:T+0;  1:T+1;  2:T+N
			
			if("0".equals(clearType)){
				//O单类型      是T0   (清算手续费固定    附加费)
				//第一步生成订单并添加流水
				PayCardResponseDTO	payCardResponseDTO = merchantCollectMoneyService.insertOrderPayFor0(param,sessionInfo);
				//第二步调用三方前置接口进行收款处理
				if("0".equals(payCardResponseDTO.getRetCode())){//生成订单并添加流水成功
					
					result = merchantCollectMoneyService.submitOrderPayFor0(param, sessionInfo, payCardResponseDTO.getPmsAppTransInfo());

				}else{
					result = new Gson().toJson(payCardResponseDTO);
				}
			}else if("2".equals(clearType)){
				
				//O单类型      是T0   (清算手续费百分比算)
				
				//第一步生成订单并添加流水
				PayCardResponseDTO	payCardResponseDTO = merchantCollectMoneyService.insertOrderPayFor0Settle(param,sessionInfo);
				//第二步调用三方前置接口进行收款处理
				if("0".equals(payCardResponseDTO.getRetCode())){//生成订单并添加流水成功
					
					result = merchantCollectMoneyService.submitOrderPayFor0Settle(param, sessionInfo, payCardResponseDTO.getPmsAppTransInfo());

				}else{
					result = new Gson().toJson(payCardResponseDTO);
				}
				
			}else{
				//第一步生成订单并添加流水
				PayCardResponseDTO	payCardResponseDTO = merchantCollectMoneyService.insertOrderPay(param,sessionInfo);
				//第二步调用三方前置接口进行收款处理
				if("0".equals(payCardResponseDTO.getRetCode())){//生成订单并添加流水成功
					
					result = merchantCollectMoneyService.submitOrderPay(param, sessionInfo, payCardResponseDTO.getPmsAppTransInfo());

				}else{
					result = new Gson().toJson(payCardResponseDTO);
				}
			}
			
			
			
			
			outPrint(response,result);
			logger.info("用户编码："+sessionInfo.getMercId()+"调用刷卡收款结束");	
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
			logger.info("用户编码："+sessionInfo.getMercId()+"刷卡收款调用失败！", e);
			e.printStackTrace();
		}
	}
	
	/**
	 * app请求  订单查询    确认支付成功
	 * wumeng  20150507
	 * @param request
	 * @param response
	 * @param session
	 */
	@RequestMapping("/confirmPay")
	public void confirmPay(HttpServletResponse response,HttpSession session,HttpServletRequest request){
		SessionInfo  sessionInfo = (SessionInfo) session.getAttribute(SessionInfo.SESSIONINFO);//得到登录用户session信息
		try {
			String param = requestClient(request);
			outPrint(response, merchantCollectMoneyService.queryOrder(param,session,request));
		} catch (Exception e) {
			try {
				outPrint(response, merchantCollectMoneyService.exceptUtility(session));
			} catch (IOException e1) {
				e1.printStackTrace();
			} catch (Exception e1) {
				e1.printStackTrace();
			}
			setSession(session,request.getRemoteAddr(),true);
			logger.info("用户编码："+sessionInfo.getMercId()+"确认支付接口调用失败！", e);
			e.printStackTrace();
		}
	}
	
	
	
	

    /**
     * @des 百度回调方法
     * @author Jeff
     * @param response
     * @param session
     * @param request
     */
    @RequestMapping("/baiduOrderCallBack")
    public ModelAndView baiduOrderCallBack(BaiduBackRequestDTO baiduBackRequestDTO,HttpServletResponse response,HttpSession session,HttpServletRequest request){

        Integer result = 0;
		try {
			result = merchantCollectMoneyService.baiduCallBackHandel(baiduBackRequestDTO,response,session,request);
		} catch (Exception e) {
			logger.error("百度回调方法失败！请求：" + baiduBackRequestDTO.toString() + " 详情：" + e.getMessage());
		}
        if( result == 1 ){
            return new ModelAndView("success");
        }else{
            return new ModelAndView("fail");
        }

    }
    
    
    
    /**
     * @des 移动和包回调方法
     * @author wumeng
     * @param response
     * @param session
     * @param request
     */
	@RequestMapping("/yDHBOrderCallBack")
    public ModelAndView yDHBOrderCallBack(HttpServletResponse response,HttpSession session,HttpServletRequest request){

        Integer result = 0;
		try {
			
			result = merchantCollectMoneyService.yDHBOrderCallBack(response,session,request);
		} catch (Exception e) {
			logger.error("百度回调方法失败！  详情：" + e.getMessage());
		}
        if( result == 1 ){
            return new ModelAndView("success");
        }else{
            return new ModelAndView("fail");
        }

    }
    
    
    
    /**
     * @des 欧飞回调方法
     * @author Jeff
     * @param response
     * @param session
     * @param request
     */
    @RequestMapping("/offiOrderCallBack")
    public ModelAndView offiOrderCallBack(OffiBackRequestDTO offiBackRequestDTO,HttpServletResponse response,HttpSession session,HttpServletRequest request){

        Integer result = 0;
		try {
			result = merchantCollectMoneyService.offiCallBackHandel(offiBackRequestDTO);
		} catch (Exception e) {
			logger.info("百度回调方法失败！请求："+offiBackRequestDTO+" 详情："+e.getMessage());
		}
        if( result == 1 ){
            return new ModelAndView("success");
        }else{
            return new ModelAndView("fail");
        }
    }





}
