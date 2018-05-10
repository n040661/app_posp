package xdt.controller.lhzf;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.alibaba.fastjson.JSON;

import xdt.controller.BaseAction;
import xdt.dto.hfb.HfbResponse;
import xdt.dto.lhzf.LhzfRequset;
import xdt.dto.lhzf.LhzfResponse;
import xdt.dto.tfb.CardPayApplyRequest;
import xdt.model.ChannleMerchantConfigKey;
import xdt.model.OriginalOrderInfo;
import xdt.quickpay.hengfeng.util.Bean2QueryStrUtil;
import xdt.schedule.ThreadPool;
import xdt.service.HfQuickPayService;
import xdt.service.IClientCollectionPayService;
import xdt.service.IClientH5Service;
import xdt.service.ILhzfService;
import xdt.service.ITFBService;
import xdt.util.HttpUtil;
import xdt.util.JsdsUtil;
import xdt.util.utils.MD5Utils;
import xdt.util.utils.RequestUtils;
import xdt.util.utils.UtilThread;
@Controller
@RequestMapping("/LqzfController")
public class LhzfController extends BaseAction{

	
	Logger log =Logger.getLogger(this.getClass());
	@Resource
	private IClientCollectionPayService clientCollectionPayService;
	
	@Resource
	private HfQuickPayService payService;
	
	@Resource 
	private  IClientH5Service ClientH5ServiceImpl;
	
	@Resource 
	private ILhzfService iLhzfService;
	
	/**
	 * 网关支付获取参数签名
	 * @param response
	 * @param cardPayApplyＲequest
	 */
	@RequestMapping(value = "paySign")
	public void cardpayParameter(HttpServletResponse response,HttpServletRequest request,
			LhzfRequset lhzfRequset) {
		log.info("网关支付获取参数"+JSON.toJSON(lhzfRequset));
		TreeMap<String, String> result = new TreeMap<String, String>();
		ChannleMerchantConfigKey keyinfo = clientCollectionPayService.getChannelConfigKey(lhzfRequset.getMerNo());
		//获取商户秘钥
		String key = keyinfo.getMerchantkey();
		result.putAll(JsdsUtil.beanToMap(lhzfRequset));
		String paramSrc =RequestUtils.getParamSrc(result);
		log.info("签名前数据:"+paramSrc);
		String md5 =MD5Utils.sign(paramSrc, key, "UTF-8");
		log.info("签名:"+md5);
		try {
			outString(response, md5);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	@RequestMapping(value="wxpayParameter")
	public void quickAgentPayH5(LhzfRequset lhzfRequset,HttpServletResponse response,HttpServletRequest request){
		
		log.info("蓝海H5参数："+JSON.toJSONString(lhzfRequset));
		
		Map<String, String> results =new HashMap<>();
		log.info("支付参数:"+JSON.toJSON(lhzfRequset));
		ChannleMerchantConfigKey keyinfo = clientCollectionPayService.getChannelConfigKey(lhzfRequset.getMerNo());
		//获取商户秘钥
		String key = keyinfo.getMerchantkey();
		TreeMap<String, String> result = new TreeMap<String, String>();
		result.putAll(JsdsUtil.beanToMap(lhzfRequset));
		String paramSrc =RequestUtils.getParamSrc(result);
		log.info("蓝海---签名之前参数");
		boolean b =MD5Utils.verify(paramSrc, lhzfRequset.getSign(), key, "UTF-8");
		if(b){
			
			log.info("签名正确");
			lhzfRequset.setUrl(lhzfRequset.getNotifyUrl());
			lhzfRequset.setReUrl(lhzfRequset.getReturnUrl());
			results = iLhzfService.quickAgentPayH5(lhzfRequset, results);
			if("00".equals(results.get("respCode"))){
				String str="idNo="+results.get("idNo")+"&idType="+results.get("idNo")+"&payeeCardType="+results.get("idNo")+"&orderDesc="+results.get("idNo")+"&remark="+results.get("idNo")+"&transInfo="+results.get("idNo");
				str+="&idName="+results.get("idNo")+"&cardType="+results.get("idNo")+"&transAmt="+results.get("idNo")+"&currency="+results.get("idNo")+"&sign="+results.get("idNo")+"&cardNo="+results.get("idNo")+"&serialNo="+results.get("idNo");
				str+="&transDate="+results.get("idNo")+"&orderNo="+results.get("idNo")+"&transId="+results.get("idNo")+"&transTime="+results.get("idNo")+"&extraInfo="+results.get("idNo")+"&payeeCardNo="+results.get("idNo")+"&merKey="+results.get("idNo");
				str+="&notifyUrl="+results.get("idNo")+"&mobileNo="+results.get("idNo")+"&requestUrl="+results.get("idNo")+"&returnUrl="+results.get("idNo");
				String url ="../pay/lhzf/zhfu.jsp?"+str;
				try {
					request.setCharacterEncoding("UTF-8");
					request.setAttribute("idNo", results.get("idNo"));
					request.setAttribute("idType", results.get("idType"));
					request.setAttribute("payeeIdType", results.get("payeeIdType"));
					request.setAttribute("orderDesc", results.get("orderDesc"));
					request.setAttribute("remark", results.get("remark"));
					request.setAttribute("transInfo", results.get("transInfo"));
					request.setAttribute("idName", results.get("idName"));
					request.setAttribute("cardType", results.get("cardType"));
					request.setAttribute("transAmt", results.get("transAmt"));
					request.setAttribute("currency", results.get("currency"));
					request.setAttribute("sign", results.get("sign"));
					request.setAttribute("cardNo", results.get("cardNo"));
					request.setAttribute("serialNo", results.get("serialNo"));
					request.setAttribute("transDate", results.get("transDate"));
					request.setAttribute("orderNo", results.get("orderNo"));
					request.setAttribute("transId", results.get("transId"));
					request.setAttribute("transTime", results.get("transTime"));
					request.setAttribute("extraInfo", results.get("extraInfo"));
					request.setAttribute("payeeCardNo", results.get("payeeCardNo"));
					request.setAttribute("merKey", results.get("merKey"));
					request.setAttribute("notifyUrl", results.get("notifyUrl"));
					request.setAttribute("mobileNo", results.get("mobileNo"));
					request.setAttribute("requestUrl", results.get("requestUrl"));
					request.setAttribute("returnUrl", results.get("returnUrl"));
					request.setAttribute("userRate", results.get("userRate"));
					request.setAttribute("userFee", results.get("userFee"));
					request.setAttribute("payeeCurrency", results.get("payeeCurrency"));
					request.setAttribute("bankCode", results.get("bankCode"));
					request.setAttribute("payeeBankCode", results.get("payeeBankCode"));
					
					request.setAttribute("merIp", results.get("merIp"));
					request.setAttribute("payeeIdName", results.get("payeeIdName"));
					request.setAttribute("payeeMobileNo", results.get("payeeMobileNo"));
					
					
					request.getRequestDispatcher("../pay/lhzf/zhfu.jsp").forward(request, response);
					//response.sendRedirect(url);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			
			log.info("laile: "+JSON.toJSONString(results));
			
		}else{
			log.info("签名错误");
			results.put("respCode", "01");
			results.put("respMsg","签名错误！");
		}
		try {
			outString(response, JSON.toJSONString(results));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	@RequestMapping(value="notifyUrl")
	public void notifyUrl(LhzfResponse lhzfResponse, HttpServletResponse response){
		try {
			log.info("蓝海异步通知来了");
			log.info("蓝海异步参数："+JSON.toJSONString(lhzfResponse));
			LhzfResponse lhzfResponses =new LhzfResponse();
			String str;
			if (lhzfResponse.getOrderNo()!=null) {
				str = "SUCCESS";
				OriginalOrderInfo originalInfo = null;
				if (lhzfResponse.getOrderNo() != null && lhzfResponse.getOrderNo()!= "") {
					originalInfo = this.payService.getOriginOrderInfo(lhzfResponse.getOrderNo());
				}
				log.info("订单数据:" + JSON.toJSON(originalInfo));
				Bean2QueryStrUtil queryUtil = new Bean2QueryStrUtil();
				log.info("下游的异步地址" + originalInfo.getBgUrl());
				log.info("蓝海异步返回解析参数"+JSON.toJSON(lhzfResponse));
				if("0000".equals(lhzfResponse.getRespCode())){
					iLhzfService.update(lhzfResponse);
					//---------------------------------------------------
					//返回参数
					lhzfResponses.setMerNo(originalInfo.getPid());
					lhzfResponses.setOrderNo(lhzfResponse.getOrderNo());
					if("SUCCESS".equals(lhzfResponse.getStatus())){
						lhzfResponses.setRespCode("00");
						lhzfResponses.setRespMsg("支付成功");
					}else if("FAILED".equals(lhzfResponse.getStatus())){
						lhzfResponses.setRespCode("01");
						lhzfResponses.setRespMsg("支付失败");
					}else if("CREATED".equals(lhzfResponse.getStatus())){
						lhzfResponses.setRespCode("03");
						lhzfResponses.setRespMsg("交易已创建");
					}else if("PROCESSING".equals(lhzfResponse.getStatus())){
						lhzfResponses.setRespCode("200");
						lhzfResponses.setRespMsg("交易处理中");
					}else if("UNKNOWN".equals(lhzfResponse.getStatus())){
						lhzfResponses.setRespCode("04");
						lhzfResponses.setRespMsg("状态未知");
					}else if("CANCELED".equals(lhzfResponse.getStatus())){
						lhzfResponses.setRespCode("01");
						lhzfResponses.setRespMsg("交易已取消");
					}else if("SUSPENDING".equals(lhzfResponse.getStatus())){
						lhzfResponses.setRespCode("01");
						lhzfResponses.setRespMsg("交易暂停");
					}
				}else if("T001".equals(lhzfResponse.getRespCode())){
					lhzfResponses.setMerNo(originalInfo.getPid());
					lhzfResponses.setOrderNo(lhzfResponse.getOrderNo());
					lhzfResponses.setRespCode("T001");
					lhzfResponses.setRespMsg("等待异步交易结果");
					
				}else if("T000".equals(lhzfResponse.getRespCode())){
					lhzfResponses.setMerNo(originalInfo.getPid());
					lhzfResponses.setOrderNo(lhzfResponse.getOrderNo());
					lhzfResponses.setRespCode("T000");
					lhzfResponses.setRespMsg("不支持的交易类型");
					
				}else if("T002".equals(lhzfResponse.getRespCode())){
					lhzfResponses.setMerNo(originalInfo.getPid());
					lhzfResponses.setOrderNo(lhzfResponse.getOrderNo());
					lhzfResponses.setRespCode("T002");
					lhzfResponses.setRespMsg("未知错误，稍后查询");
				}
				//和下面的签名
				//---------------------------------------------------
				TreeMap<String, String> result = new TreeMap<String, String>();
				ChannleMerchantConfigKey keyinfo = clientCollectionPayService
						.getChannelConfigKey(originalInfo.getPid());
				// 获取商户秘钥
				String key = keyinfo.getMerchantkey();
				result.putAll(JsdsUtil.beanToMap(lhzfResponses));
				String paramSrc = RequestUtils.getParamSrc(result);
				log.info("签名前数据**********蓝海支付:" + paramSrc);
				String md5 = MD5Utils.sign(paramSrc, key, "UTF-8");
				lhzfResponses.setSign(md5);
				
				String result1=HttpUtil.sendPost(originalInfo.getBgUrl()+"?"+queryUtil.bean2QueryStr(lhzfResponses));
				log.info("下游返回状态" + result1);
				if (!"SUCCESS".equals(result1)) {
					ThreadPool.executor(new UtilThread(originalInfo
							.getBgUrl(), queryUtil
							.bean2QueryStr(lhzfResponses)));
				}
			} else {
				str = "FAIL";
			}
			outString(response, str);
		} catch (Exception e) {
			log.info("蓝海异步回调异常:" + e);
			e.printStackTrace();
		}
	}
	@RequestMapping(value="returnUrl")
	public void returnUrl(LhzfResponse lhzfResponse, HttpServletResponse response){
		try {
		log.info("蓝海同步数据返回参数:"+JSON.toJSONString(lhzfResponse));
		LhzfResponse lhzfResponses =new LhzfResponse();
		OriginalOrderInfo originalInfo = null;
		if (lhzfResponse.getOrderNo() != null && lhzfResponse.getOrderNo()!= "") {
			originalInfo = this.payService.getOriginOrderInfo(lhzfResponse.getOrderNo());
		}
		log.info("订单数据:" + JSON.toJSON(originalInfo));
		Bean2QueryStrUtil queryUtil = new Bean2QueryStrUtil();
		log.info("下游的异步地址" + originalInfo.getBgUrl());
		log.info("蓝海异步返回解析参数"+JSON.toJSON(lhzfResponse));
		if("0000".equals(lhzfResponse.getRespCode())){
			//---------------------------------------------------
			//返回参数
			lhzfResponses.setMerNo(originalInfo.getPid());
			lhzfResponses.setOrderNo(lhzfResponses.getOrderNo());
			if("SUCCESS".equals(lhzfResponses.getStatus())){
				lhzfResponses.setRespCode("00");
				lhzfResponses.setRespMsg("支付成功");
			}else if("FAILED".equals(lhzfResponses.getStatus())){
				lhzfResponses.setRespCode("01");
				lhzfResponses.setRespMsg("支付失败");
			}else if("CREATED".equals(lhzfResponses.getStatus())){
				lhzfResponses.setRespCode("03");
				lhzfResponses.setRespMsg("交易已创建");
			}else if("PROCESSING".equals(lhzfResponses.getStatus())){
				lhzfResponses.setRespCode("200");
				lhzfResponses.setRespMsg("交易处理中");
			}else if("UNKNOWN".equals(lhzfResponses.getStatus())){
				lhzfResponses.setRespCode("04");
				lhzfResponses.setRespMsg("状态未知");
			}else if("CANCELED".equals(lhzfResponses.getStatus())){
				lhzfResponses.setRespCode("01");
				lhzfResponses.setRespMsg("交易已取消");
			}else if("SUSPENDING".equals(lhzfResponses.getStatus())){
				lhzfResponses.setRespCode("01");
				lhzfResponses.setRespMsg("交易暂停");
			}
		}else if("T001".equals(lhzfResponse.getRespCode())){
			lhzfResponses.setMerNo(originalInfo.getPid());
			lhzfResponses.setOrderNo(lhzfResponses.getOrderNo());
			lhzfResponses.setRespCode("T001");
			lhzfResponses.setRespMsg("等待异步交易结果");
			
		}else if("T000".equals(lhzfResponse.getRespCode())){
			lhzfResponses.setMerNo(originalInfo.getPid());
			lhzfResponses.setOrderNo(lhzfResponses.getOrderNo());
			lhzfResponses.setRespCode("T000");
			lhzfResponses.setRespMsg("不支持的交易类型");
			
		}else if("T002".equals(lhzfResponse.getRespCode())){
			lhzfResponses.setMerNo(originalInfo.getPid());
			lhzfResponses.setOrderNo(lhzfResponses.getOrderNo());
			lhzfResponses.setRespCode("T002");
			lhzfResponses.setRespMsg("未知错误，稍后查询");
		}
		//和下面的签名
		//---------------------------------------------------
		TreeMap<String, String> result = new TreeMap<String, String>();
		ChannleMerchantConfigKey keyinfo = clientCollectionPayService
				.getChannelConfigKey(originalInfo.getPid());
		// 获取商户秘钥
		String key = keyinfo.getMerchantkey();
		log.info("来了11");
		result.putAll(JsdsUtil.beanToMap(lhzfResponses));
		log.info("来了2");
		String paramSrc = RequestUtils.getParamSrc(result);
		log.info("签名前数据**********蓝海支付:" + paramSrc);
		String md5 = MD5Utils.sign(paramSrc, key, "UTF-8");
		lhzfResponses.setSign(md5);
		String path=queryUtil.bean2QueryStr(lhzfResponses);
		response.sendRedirect(originalInfo.getPageUrl()+"?"+path);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
