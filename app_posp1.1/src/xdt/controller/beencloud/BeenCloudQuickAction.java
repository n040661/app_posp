package xdt.controller.beencloud;

import java.io.BufferedReader;
import java.io.PrintWriter;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONObject;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import xdt.controller.BaseAction;
import xdt.dto.SubmitOrderNoCardPayResponseDTO;
import xdt.model.ChannleMerchantConfigKey;
import xdt.model.OriginalOrderInfo;
import xdt.model.PospTransInfo;
import xdt.quickpay.hengfeng.entity.PayQueryRequestEntity;
import xdt.quickpay.hengfeng.entity.PayQueryResponseEntity;
import xdt.quickpay.hengfeng.entity.PayRequestEntity;
import xdt.quickpay.hengfeng.entity.PayResponseEntity;
import xdt.quickpay.hengfeng.util.Bean2QueryStrUtil;
import xdt.quickpay.hengfeng.util.HFSignUtil;
import xdt.quickpay.hengfeng.util.PreSginUtil;
import xdt.service.BeenQuickPayService;
import xdt.util.HttpURLConection;

import com.google.gson.Gson;

/**
 * 
 * @Description
 * @version V1.3.1
 */
@Controller
@RequestMapping("cj/quick")
public class BeenCloudQuickAction extends BaseAction {

	/**
	 * 日志记录
	 */
	private Logger logger = Logger.getLogger(BeenCloudQuickAction.class);

	/**
	 * 
	 */
	@Resource
	private BeenQuickPayService beenQuickPayService;

	/**
	 * 
	 * @Description 支付
	 * @author Administrator
	 * @param temp
	 *            支付信息
	 * @param request
	 * @param response
	 * @throws Exception
	 */
	@RequestMapping("pay")
	public void pay(PayRequestEntity temp, HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		logger.info("进入支付");
		
		//处理下单流程保存原始交易信息
		beenQuickPayService.payHandle(temp,request,response);
		
		//上送交易
		
	}

	/**
	 * 
	 * @Description 查询支付信息
	 * @author Administrator
	 * @param request
	 * @param response
	 * @param queryInfo
	 *            订单标识
	 * @throws Exception
	 */
	@RequestMapping("query")
	public void query(HttpServletRequest request, HttpServletResponse response,
			PayQueryRequestEntity queryInfo) throws Exception {
		logger.info("进入查询");
		
		String jsonStr = "";
		
		Gson gson = new Gson();
		
		logger.info("给下游调用 查询结果");
		String tranId = queryInfo.getTransactionId();
		logger.info("查询商户上送原始信息");
		OriginalOrderInfo queryWhere=new OriginalOrderInfo();
		
		queryWhere.setMerchantOrderId(tranId);
		queryWhere.setPid(queryInfo.getMerId());
		logger.info("查询原始订单"+queryWhere);
		OriginalOrderInfo originalInfo = beenQuickPayService.selectByOriginal(queryWhere);
		
		if(originalInfo!=null){
			logger.info("原始数据 订单表关联id");
			String orderId = originalInfo.getOrderId();
			logger.info("查询流水信息");
			PospTransInfo transInfo = beenQuickPayService.getTransInfo(orderId);
			
			if(transInfo==null){
				
				SubmitOrderNoCardPayResponseDTO responseDTO = new SubmitOrderNoCardPayResponseDTO();
				
				responseDTO.setRetCode(1);
				responseDTO.setRetMessage("订单信息不存在");
				jsonStr = gson.toJson(responseDTO);
			}else{
				
				PayQueryResponseEntity resp = beenQuickPayService.queryLocalOrderStatus(transInfo.getOrderId());
				
				// 商户key
				String merchantkey = beenQuickPayService.getChannelConfigKey(queryInfo.getMerId()).getMerchantkey();
				
				
				resp.setMerId(originalInfo.getPid());
				resp.setTransactionId(originalInfo.getMerchantOrderId());
				resp.setOrderTime(originalInfo.getOrderTime());
				// 上游流水id
				
				//查询签名
				HFSignUtil sign=new HFSignUtil();
				
				String str=PreSginUtil.payResultString(resp);
				
				String signData=sign.sign(str, merchantkey);
				
				resp.setSignData(signData);
				
				// 返回给下游 json
				jsonStr = gson.toJson(resp);
				}
			}else{
				SubmitOrderNoCardPayResponseDTO responseDTO = new SubmitOrderNoCardPayResponseDTO();
				
				responseDTO.setRetCode(1);
				responseDTO.setRetMessage("订单信息不存在");
				jsonStr = gson.toJson(responseDTO);
			}
				
		response.setContentType("text/html; charset=utf-8");
		PrintWriter out = response.getWriter();
		out.print(jsonStr);
		out.flush();
		out.close();
			
		

	}

	/**
	 * @Description webhook通知
	 * @author Administrator
	 * @throws Exception 
	 */
	@RequestMapping("notify")
	public void webhook( HttpServletRequest request, HttpServletResponse response) throws Exception {
		
		logger.info("通知");
		
		PayResponseEntity temp=new PayResponseEntity();
		
		StringBuffer json = new StringBuffer();
	    String line = null;
		request.setCharacterEncoding("utf-8");
		PrintWriter out=response.getWriter();
		
        BufferedReader reader = request.getReader();
        while ((line = reader.readLine()) != null) {
            json.append(line);
        }
        try {
            request.setCharacterEncoding("utf-8");
            reader = request.getReader();
            while ((line = reader.readLine()) != null) {
                json.append(line);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        JSONObject jsonObj = JSONObject.fromObject(json.toString());
        String sign = jsonObj.getString("sign");
        String timestamp = jsonObj.getString("timestamp");
        boolean status = verifySign(sign, timestamp);
        if(status){
        	out.println("success"); 
        	String message_detail = jsonObj.getString("message_detail");
        	logger.info("订单信息:"+message_detail);
        	
        	if(!StringUtils.isEmpty(message_detail)){
        		JSONObject json2 = JSONObject.fromObject(message_detail.toString());
        		String txnAmt = json2.getString("transactionFee");
        		
        		//艾尚产生对象id
        		String orderId = json2.getString("bill_id");
        		
        		//交易结果
        		boolean transResult=Boolean.parseBoolean(json2.getString("tradeSuccess"));
        		
        		
        		beenQuickPayService.handleLocalOrderInfo(orderId);
        		
        		// 查询商户上送原始信息
        		OriginalOrderInfo originalInfo = beenQuickPayService.getOriginOrderInfoByPospsn(orderId);
        		if(originalInfo!=null){
        			ChannleMerchantConfigKey cmkey=beenQuickPayService.getChannelConfigKey(temp.getPid());
        			
        			//设置交易信息
        			temp.setPaytype(originalInfo.getPayType());
        			temp.setPid(originalInfo.getPid());
        			temp.setTransactionid(originalInfo.getMerchantOrderId());
        			temp.setOrdertime(originalInfo.getOrderTime());
        			temp.setDealid(orderId);
        			temp.setDealtime(originalInfo.getOrderTime());
        			temp.setPayamount(txnAmt);
        			temp.setPayresult(transResult?"10":"11");
        			
        			HFSignUtil signUtil=new HFSignUtil();
        			
        			String str=PreSginUtil.payBgResultString(temp);
        			if(cmkey!=null){
        				
        				String signData=signUtil.sign(str, cmkey.getMerchantkey());
        				
        				temp.setSignmsg(signData);
        			}
        			//修改本地订单状态
        			
        			
        			Bean2QueryStrUtil bean2Util = new Bean2QueryStrUtil();
        			
        			// 给下游主动返回支付结果
        			String path=originalInfo.getBgUrl() + "?" + bean2Util.bean2QueryStr(temp);
        			logger.info("bgUrl 平台服务器重定向："+path);
        			
        			int count=5;
        			while(count-->0){
        				String result =HttpURLConection.httpURLConectionGET(path.replace(" ", ""), "UTF-8");
        				if(!StringUtils.isEmpty(result)){
        					if("<result>ok<result>".trim().equals(result)){
        						break;
        					}
        				}
        			}
        			
        			logger.info("向下游 发送数据成功");
        		
        	}
    		}
    		
    		
        	
        }else{
        	out.println("fail");
        }
		
		
	}

	

	/**
	 * 
	 * @Description 支付成功回调
	 * @author Administrator
	 * @throws Exception 
	 */
	@RequestMapping("payfinish/callback")
	public void payCallBack(HttpServletRequest request, HttpServletResponse response) throws Exception {
		
		
		PayResponseEntity temp=new PayResponseEntity();
		logger.info("支付成功回调");
		
		//本系统流水id pospsn
		String orderId = request.getParameter("orderId");
		
		String respCode = request.getParameter("respCode");
		String txnAmt = request.getParameter("txnAmt");
		String txnTime = request.getParameter("txnTime");
		
		//遍历所有字段代码样例
		Enumeration<String> enu = request.getParameterNames();
		while(enu.hasMoreElements()) {
			String key = enu.nextElement();
			logger.info("key:" + key + ";value:" + request.getParameter(key));
		}
		
		// 查询商户上送原始信息
		OriginalOrderInfo originalInfo = beenQuickPayService.getOriginOrderInfoByPospsn(orderId);
		
		ChannleMerchantConfigKey cmkey=beenQuickPayService.getChannelConfigKey(originalInfo.getPid());
		
		//设置交易信息
		temp.setPaytype(originalInfo.getPayType());
		temp.setPid(originalInfo.getPid());
		temp.setTransactionid(originalInfo.getMerchantOrderId());
		temp.setOrdertime(originalInfo.getOrderTime());
		temp.setDealid(orderId);
		temp.setDealtime(txnTime);
		temp.setPayamount(txnAmt);
		temp.setErrcode(respCode);
		temp.setPayresult("00".equals(respCode)?"10":"11");
		
		if("00".equals(respCode)){
			temp.setErrcode("0000");
		}else{
			temp.setErrcode("0001");
		}
		
		HFSignUtil sign=new HFSignUtil();
		
		String str=PreSginUtil.payBgResultString(temp);
		if(cmkey!=null){
			
			String signData=sign.sign(str, cmkey.getMerchantkey());
			
			temp.setSignmsg(signData);
		}
		
		Bean2QueryStrUtil bean2Util = new Bean2QueryStrUtil();

		//给下游手动返回支付结果
		String path=originalInfo.getPageUrl() + "?" + bean2Util.bean2QueryStr(temp);
		logger.info("pageUrl 商户页面 重定向："+path);
		response.sendRedirect(path.replace(" ", ""));
		
	}
	
	/**
	 * 下游接入 demo
	 * 
	 * @param param
	 * @param request
	 * @param response
	 * @throws Exception
	 */
	@RequestMapping(value = "signForWap")
	public void merSignServletForWap(PayRequestEntity param, HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		
		logger.info("原始订单信息："+param);
		
		Map<String,String> result=new HashMap<String,String>();
		
		if("100510112345708".equals(param.getPid())){
			// 根据商户号查询key
			ChannleMerchantConfigKey keyinfo = beenQuickPayService.getChannelConfigKey(param.getPid());
			if (keyinfo != null) {
				String merchantKey = keyinfo.getMerchantkey();
				HFSignUtil signUtil = new HFSignUtil();
				// 生成签名
				String signmsg = signUtil.sign(PreSginUtil.paySigiString(param), merchantKey);
				logger.info("生成签名：" + signmsg);
				result.put("signmsg", signmsg);
				result.put("status", "1");
			}else{
				result.put("signmsg", "");
				result.put("status", "0");
			}
		}else{
			result.put("signmsg", "只能用于demo签名");
			result.put("status", "2");
		}
		outString(response, new Gson().toJson(result));
	}
}
