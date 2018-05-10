package xdt.controller.hengfeng;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import xdt.controller.BaseAction;
import xdt.dto.weixin.CallbackDto;
import xdt.dto.weixin.ChangeRateDto;
import xdt.dto.weixin.PayRequestDto;
import xdt.dto.weixin.QueryRequestDto;
import xdt.dto.weixin.RegisterDto;
import xdt.dto.weixin.VerifyInfoDto;
import xdt.service.WXQrCodeService;

/**
 * 
 * @Description 微信二维码
 * @date 2016年12月3日 上午10:34:27
 * @version V1.3.1
 */
@Controller
@RequestMapping("cj/qrcode/")
public class WXQrCodeAction extends BaseAction {

	
	private Logger logger=Logger.getLogger(WXQrCodeAction.class);
	
	
	/**
	
	/**
	 * 微信二维码
	 */
	@Resource
	public WXQrCodeService wxcodeService;
	
	
	
	/**
	 * 
	 * @Description 生成支付二维码
	 * @author Administrator
	 * @param request
	 * @param response
	 * @throws Exception 
	 */
	@RequestMapping("pay")
	public void pay(HttpServletRequest request, HttpServletResponse response,PayRequestDto req) throws Exception {
		Map<String, String> result=new HashMap<String, String>();
		logger.info("##############################生成支付二维码");
		logger.info("下游上送参数："+req);
		try{
			result=wxcodeService.updatePay(req);
			outString(response, gson.toJson(result));
		}catch(Exception e){
			result=new HashMap<String, String>();
			result.put("respCode", "0003");
			result.put("respInfo", e.getMessage());
			outString(response, gson.toJson(result));
		}
	}

	/**
	 * 
	 * @Description 查询订单信息
	 * @author Administrator
	 * @param request
	 * @param response
	 * @throws Exception 
	 */
	@RequestMapping("query")
	@Deprecated
	public void query(HttpServletRequest request, HttpServletResponse response,QueryRequestDto req) throws Exception {
		Map<String, String> result=new HashMap<String, String>();
		logger.info("##############################查询订单信RR息");
		logger.info("下游上送参数："+req);
		try{
			result=wxcodeService.queryOrderInfo(req);
			outString(response, gson.toJson(result));
		}catch(Exception e){
			result=new HashMap<String, String>();
			result.put("respCode", "0003");
			result.put("respInfo", e.getMessage());
			outString(response, gson.toJson(result));
		}
	}

	/**
	 * 
	 * @Description 异步通知支付结果
	 * @author Administrator
	 * @param request
	 * @param response
	 * @throws Exception 
	 */
	@RequestMapping("callback")
	public void callback(HttpServletRequest request,HttpServletResponse response,CallbackDto callback) throws Exception {
		Map<String, String> result=new HashMap<String, String>();
		logger.info("##############################异步通知支付结果");
		logger.info("下游上送参数："+callback);

		wxcodeService.updateHandleOrder(callback);
		
		//TODO 在这一步处理下游
		
		try{
			outString(response, gson.toJson(result));
		}catch(Exception e){
			result=new HashMap<String, String>();
			result.put("respCode", "0003");
			result.put("respInfo", e.getMessage());
			outString(response, gson.toJson(result));
		}
		
	}

	/**
	 * 
	 * @Description 下载支付private key
	 * @author Administrator
	 * @param request
	 * @param response
	 * @throws Exception 
	 */
	@RequestMapping("downkey")
	public void downkey(HttpServletRequest request, HttpServletResponse response,RegisterDto req) throws Exception {
		Map<String, String> result=new HashMap<String, String>();
		logger.info("##############################下载支付private key");
		logger.info("下游上送参数："+req);
		
		try{
			result=wxcodeService.updateDownkey(req);
			outString(response, gson.toJson(result));
		}catch(Exception e){
			result=new HashMap<String, String>();
			result.put("respCode", "0003");
			result.put("respInfo", e.getMessage());
			outString(response, gson.toJson(result));
		}
	
	}

	/**
	 * 
	 * @Description 注册商户
	 * @author Administrator
	 * @param request
	 * @param response
	 * @throws Exception 
	 */
	@RequestMapping("register")
	public void register(HttpServletRequest request,HttpServletResponse response,RegisterDto req) throws Exception {
		Map<String, String> result=new HashMap<String, String>();
		logger.info(" ##############################注册商户");
		logger.info("下游上送参数："+req);
		
		try{
			result=wxcodeService.updateRegister(req);
			outString(response, gson.toJson(result));
		}catch(Exception e){
			result=new HashMap<String, String>();
			result.put("respCode", "0003");
			result.put("respInfo", e.getMessage());
			outString(response, gson.toJson(result));
		}
	}

	/**
	 * 
	 * @Description 校验商户身份 银行卡
	 * @author Administrator
	 * @param request
	 * @param response
	 */
	@RequestMapping("valida")
	public void validator(HttpServletRequest request,HttpServletResponse response,VerifyInfoDto info) throws Exception {
		Map<String, String> result=new HashMap<String, String>();
		logger.info("##############################校验商户身份 银行卡");
		logger.info("下游上送参数："+info);
		
		try{
			result=wxcodeService.updateValidator(info);
			outString(response, gson.toJson(result));
		}catch(Exception e){
			result=new HashMap<String, String>();
			result.put("respCode", "0003");
			result.put("respInfo", e.getMessage());
			outString(response, gson.toJson(result));
		}
	}
	@RequestMapping("updateRate")
	public void updateRate(HttpServletRequest request,HttpServletResponse response,ChangeRateDto info) throws Exception {
		Map<String, String> result=new HashMap<String, String>();
		logger.info("##############################校验商户身份 银行卡");
		logger.info("下游上送参数："+info);
		try{
			result=wxcodeService.updateRate(info);
			outString(response, gson.toJson(result));
		}catch(Exception e){
			result=new HashMap<String, String>();
			result.put("respCode", "0003");
			result.put("respInfo", e.getMessage());
			logger.info("同步费率错误：",e);
			outString(response, gson.toJson(result));
		}
	}
}
