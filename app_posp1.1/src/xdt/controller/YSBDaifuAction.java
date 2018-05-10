package xdt.controller;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSON;
import com.google.gson.Gson;
import com.uns.inf.api.model.CallBack;
import com.uns.inf.api.model.Request;
import com.uns.inf.api.service.Service;
import net.sf.json.JSONObject;
import xdt.dto.SubmitOrderNoCardPayResponseDTO;
import xdt.dto.payeasy.DaifuRequestEntity;
import xdt.model.ChannleMerchantConfigKey;
import xdt.model.OriginalOrderInfo;
import xdt.quickpay.hengfeng.util.Bean2QueryStrUtil;
import xdt.quickpay.hengfeng.util.HFSignUtil;
import xdt.quickpay.hengfeng.util.HttpClientUtil;
import xdt.quickpay.hf.comm.SampleConstant;
import xdt.quickpay.hf.entity.PayRequestEntity;
import xdt.quickpay.hf.entity.PayResponseEntity;
import xdt.quickpay.hf.util.PlatBase64Utils;
import xdt.quickpay.hf.util.PlatKeyGenerator;
import xdt.quickpay.hf.util.PreSignUtil;
import xdt.quickpay.ysb.Constants;
import xdt.quickpay.ysb.model.YsbRequsetEntity;
import xdt.quickpay.ysb.util.SignUtil;
import xdt.quickpay.ysb.util.YsbSignUtil;
import xdt.service.IYsbDaifuService;
import xdt.util.HttpURLConection;
import xdt.util.HttpUtil;




@Controller
@RequestMapping("ysb")
public class YSBDaifuAction extends BaseAction{
	
	private String accountId=null;
	private String key=null;
	/**
	 * 日志记录
	 */
	private Logger log = Logger.getLogger(YSBDaifuAction.class);
	
	@Resource
	private IYsbDaifuService ysbService;
	/**
	 * 1.1 实时代付接口
	 * @param request
	 * @param dcrequest
	 * @return
	 * @throws Exception 
	 */
	@RequestMapping(value = "pay")
	public void pay(YsbRequsetEntity temp,HttpServletRequest request, HttpServletResponse response) throws Exception{
//		String resultCode=null;

		String merchantId=temp.getMerchantId();
		logger.info("商户号:"+merchantId);
		YsbRequsetEntity param=new YsbRequsetEntity();
		// 所有的流程通过 就发起支付 上送数据
		Map<String, String> retMap  = ysbService.pay(temp);
		DaifuRequestEntity daifu=new DaifuRequestEntity();
		daifu.setV_batch_no(temp.getOrderId());
		if ("00".equals(retMap.get("status"))) {
			daifu.setResponsecode("00");
			this.ysbService.UpdateDaifu(daifu);
		} else if("20".equals(retMap.get("status"))){
			daifu.setResponsecode("01");
			int num = this.ysbService.UpdateDaifu(daifu);
			log.info("修改结果" + num);
		}else{
			daifu.setResponsecode("200");
			int num = this.ysbService.UpdateDaifu(daifu);
			log.info("修改结果" + num);
		}
		this.log.info("向下游 发送的数据:" + retMap);
		outString(response, this.gson.toJson(retMap));
		this.log.info("向下游 发送数据成功");
		outString(response, gson.toJson(retMap));
	}
	
	/**
	 * 1.2 订单状态查询接口
	 * @param request
	 * @param dcrequest
	 * @return
	 * @throws IOException 
	 */
	@RequestMapping(value = "queryOrderStatus")
	public void queryOrderStatus(YsbRequsetEntity temp,HttpServletRequest request, HttpServletResponse response) throws IOException{
		Request dprequest=new Request();
		Map<String, String> retMap=new HashMap<String,String>();
		dprequest.put("accountId", "2120170904150304001");
		dprequest.put("orderId", temp.getOrderId());
		dprequest.put("key", "30eccdd59dbee2");
		try {
			String result=Service.sendPost(dprequest, "http://114.80.54.73:8081/unspay-external/delegatePay/queryOrderStatus");
			log.info("result:"+result);
			JSONObject jb = JSONObject.fromObject(result);
			String resultCode=(String) jb.get("result_code");
			String result_msg=(String) jb.get("result_msg");
			String amount=(String) jb.get("amount");
			String desc=(String) jb.get("desc");
			String status=(String) jb.get("status");
			retMap.put("result_code", resultCode);
			retMap.put("result_msg", result_msg);
			retMap.put("amount", amount);
			retMap.put("desc", desc);
			retMap.put("status", status);
		} catch (Exception e) {
			e.printStackTrace();
		}
		outString(response, gson.toJson(retMap));
	}
	
	/**
	 * 1.4商户账户余额及保证金余额查询接口
	 * @param request
	 * @param dcRequest
	 * @return
	 */
	@RequestMapping(value = "queryBalance")
	public String queryBalance(HttpServletRequest request, HttpServletResponse response){
		String resultCode=null;
		Request dpRequest=new Request();
		dpRequest.put("accountId", request.getParameter("accountId"));
		dpRequest.put("key", request.getParameter("key"));
		try {
			String result=Service.sendPost(dpRequest, Constants.QUERY_BLANCE);
			log.info("result:"+result);
			JSONObject jb = JSONObject.fromObject(result);
			resultCode=(String) jb.get("result_code");
		} catch (Exception e) {
			e.printStackTrace();
		}
		request.setAttribute("resultCode", resultCode);
		return "page/result";
	}
	/**
	 * 1.1子协议录入接口
	 * @param request
	 * @param dcRequest
	 * @return
	 * @throws Exception 
	 */
	@RequestMapping(value = "signSimpleSubContract")
	public void signSimpleSubContract(YsbRequsetEntity temp,HttpServletRequest request,HttpServletResponse response) throws Exception{
		
		String merchantId=temp.getMerchantId();
		logger.info("商户号:"+merchantId);
		
		YsbRequsetEntity param=new YsbRequsetEntity();
		// 所有的流程通过 就发起支付 上送数据
		Map<String, String> retMap  = ysbService.customerRegister(temp);
		outString(response, gson.toJson(retMap));
	}
	/**
	 * 1.2委托代扣接口(子协议号)
	 * @param request
	 * @param dcRequest
	 * @return
	 * @throws Exception 
	 */
	@RequestMapping(value = "collect")
	public void collect(YsbRequsetEntity temp,HttpServletRequest request,HttpServletResponse response) throws Exception{
		
		String merchantId=temp.getMerchantId();
		logger.info("商户号:"+merchantId);
		
		YsbRequsetEntity param=new YsbRequsetEntity();
		// 所有的流程通过 就发起支付 上送数据
		Map<String, String> retMap  = ysbService.payHandle(temp);
		outString(response, gson.toJson(retMap));
	}
	/**
	 * 1.2委托代扣接口(子协议号)
	 * @param request
	 * @param dcRequest
	 * @return
	 * @throws Exception 
	 */
	@RequestMapping(value = "collect1")
	public void collect1(YsbRequsetEntity temp,HttpServletRequest request,HttpServletResponse response) throws Exception{
		
		String merchantId=temp.getMerchantId();
		logger.info("商户号:"+merchantId);
		
		YsbRequsetEntity param=new YsbRequsetEntity();
		// 所有的流程通过 就发起支付 上送数据
		Map<String, String> retMap  = ysbService.payHandle1(temp);
		outString(response, gson.toJson(retMap));
	}
	/**
	 * 1.3代扣订单状态查询接口
	 * @param request
	 * @param dcRequest
	 * @return
	 * @throws Exception 
	 */
	@RequestMapping(value = "queryOrderStatusdaiKou")
	public void queryOrderStatusdaiKou(YsbRequsetEntity params,HttpServletRequest request,HttpServletResponse response) throws Exception{
		Map<String, String> retMap=new HashMap<String,String>();
		Request dcRequest=new Request();
		dcRequest.put("accountId", "2120170904150304001");
		dcRequest.put("orderId", params.getOrderId());
		dcRequest.put("key", "30eccdd59dbee2");
		// 验证签名
		ChannleMerchantConfigKey keyinfo = ysbService.getChannelConfigKey(params.getMerchantId());
		String merchantKey = keyinfo.getMerchantkey();
		SignUtil signUtil = new SignUtil();
		if (!signUtil.verify(YsbSignUtil.ybsdaikouSigiquery(params), params.getSign(), merchantKey)) {
			log.error("签名错误!");
			retMap.put("respCode", "15");
			retMap.put("respMsg", "签名错误");
		}else{
			String result = Service.sendPost(dcRequest, "http://114.80.54.73:8081/unspay-external/delegateCollect/queryOrderStatus");
			log.info("result:"+result);
			JSONObject jb = JSONObject.fromObject(result);
			String resultCode=(String) jb.get("result_code");
			String result_msg=(String) jb.get("result_msg");
			String amount=(String) jb.get("amount");
			String desc=(String) jb.get("desc");
			String status=(String) jb.get("status");
			retMap.put("result_code", resultCode);
			retMap.put("result_msg", result_msg);
			retMap.put("amount", amount);
			retMap.put("desc", desc);
		}
		outString(response, gson.toJson(retMap));
	}
	/**
	 * 1.4子协议号查询接口
	 * @param request
	 * @param dcRequest
	 * @return
	 * @throws Exception 
	 */
	@RequestMapping(value = "querySubContractId")
	public void querySubContractId(YsbRequsetEntity params,HttpServletRequest request,HttpServletResponse response) throws Exception{
		Map<String, String> retMap=new HashMap<String,String>();
		Request dcRequest=new Request();
		dcRequest.put("accountId", "2120170904150304001");
		dcRequest.put("name", params.getName());
		dcRequest.put("cardNo", params.getCardNo());
		dcRequest.put("idCardNo", params.getIdCardNo());
		dcRequest.put("key", "30eccdd59dbee2");
		// 验证签名
		ChannleMerchantConfigKey keyinfo = ysbService.getChannelConfigKey(params.getMerchantId());
		String merchantKey = keyinfo.getMerchantkey();
		SignUtil signUtil = new SignUtil();
		if (!signUtil.verify(YsbSignUtil.ybsdaikouSigimerchant(params), params.getSign(), merchantKey)) {
			log.error("签名错误!");
			retMap.put("respCode", "15");
			retMap.put("respMsg", "签名错误");
		}else{
			String result = Service.sendPost(dcRequest, "http://114.80.54.73:8081/unspay-external/subcontract/querySubContractId");
			log.info("result:"+result);
			JSONObject jb = JSONObject.fromObject(result);
			String resultCode=(String) jb.get("result_code");
			String result_msg=(String) jb.get("result_msg");
			String status=(String) jb.get("status");
			String subContractId=(String) jb.get("subContractId");
			retMap.put("result_code", resultCode);
			retMap.put("result_msg", result_msg);
			retMap.put("status", status);
			retMap.put("subContractId", subContractId);
		}
		outString(response, gson.toJson(retMap));
	}
	/**
	 * 1.5子协议延期接口
	 * @param request
	 * @param dcRequest
	 * @return
	 * @throws Exception 
	 */
	@RequestMapping(value = "subConstractExtension")
	public void subConstractExtension(YsbRequsetEntity params,HttpServletRequest request,HttpServletResponse response) throws Exception{

		Map<String, String> retMap=new HashMap<String,String>();
		Request dcRequest=new Request();
		dcRequest.put("accountId", "2120170904150304001");
		dcRequest.put("contractId", "2120170904150304001");
		dcRequest.put("subContractId", params.getSubContractId());
		dcRequest.put("startDate", params.getStartDate());
		dcRequest.put("endDate", params.getEndDate());
		dcRequest.put("key", "30eccdd59dbee2");
		// 验证签名
		ChannleMerchantConfigKey keyinfo = ysbService.getChannelConfigKey(params.getMerchantId());
		String merchantKey = keyinfo.getMerchantkey();
		SignUtil signUtil = new SignUtil();
		if (!signUtil.verify(YsbSignUtil.ybsdaikouSigitime(params), params.getSign(), merchantKey)) {
			log.error("签名错误!");
			retMap.put("respCode", "15");
			retMap.put("respMsg", "签名错误");
		}else{
			String result = Service.sendPost(dcRequest, "http://114.80.54.73:8081/unspay-external/subcontract/subConstractExtension");
			log.info("result:"+result);
			JSONObject jb = JSONObject.fromObject(result);
			String resultCode=(String) jb.get("result_code");
			String result_msg=(String) jb.get("result_msg");
			retMap.put("result_code", resultCode);
			retMap.put("result_msg", result_msg);
		}
		outString(response, gson.toJson(retMap));
	}
	/**
	 * 下游接入 demo
	 * 
	 * @param param
	 * @param request
	 * @param response
	 * @throws Exception
	 */
	@RequestMapping(value = "hfsignForWap")
	public void merSignServletForWap(YsbRequsetEntity params, HttpServletRequest request, HttpServletResponse response)
			throws Exception {

		log.info("原始订单信息：" + params);

		// 根据商户号查询key
		ChannleMerchantConfigKey keyinfo = ysbService.getChannelConfigKey(params.getMerchantId());
		if (keyinfo != null) {

			String merchantKey = keyinfo.getMerchantkey();

			SignUtil signUtil = new SignUtil();
			// 生成签名
			String signmsg = signUtil.sign(YsbSignUtil.ybsdaifuSigiString(params), merchantKey);
			log.info("生成签名：" + signmsg);
			params.setSign(signmsg);
			// 返回页面参数
			request.setAttribute("temp", params);
			request.getRequestDispatcher("/pay/ysb/daikou/signSimpleSubContract_submit.jsp").forward(request, response);
		}

	}
	/**
	 * 下游接入 demo
	 * 
	 * @param param
	 * @param request
	 * @param response
	 * @throws Exception
	 */
	@RequestMapping(value = "hfsignmessage")
	public void messageSignServletForWap(YsbRequsetEntity params, HttpServletRequest request, HttpServletResponse response) throws Exception {

		log.info("原始订单信息：" + params);

		// 根据商户号查询key
		ChannleMerchantConfigKey keyinfo = ysbService.getChannelConfigKey(params.getMerchantId());
		if (keyinfo != null) {

			String merchantKey = keyinfo.getMerchantkey();

			SignUtil signUtil = new SignUtil();
			// 生成签名
			String signmsg = signUtil.sign(YsbSignUtil.ybsdaikouSigiString(params), merchantKey);
			log.info("生成签名：" + signmsg);
			params.setSign(signmsg);
			// 返回页面参数
			request.setAttribute("temp", params);
			request.getRequestDispatcher("/pay/ysb/daikou/collect_submit.jsp").forward(request, response);
		} else {
			// 返回页面参数
			outString(response, "商户号找不到Key");
		}

	}
	/**
	 * 下游接入 demo
	 * 
	 * @param param
	 * @param request
	 * @param response
	 * @throws Exception
	 */
	@RequestMapping(value = "hfsignmessage1")
	public void messageSignServletForWap1(YsbRequsetEntity params, HttpServletRequest request, HttpServletResponse response) throws Exception {

		log.info("原始订单信息：" + params);

		// 根据商户号查询key
		ChannleMerchantConfigKey keyinfo = ysbService.getChannelConfigKey(params.getMerchantId());
		if (keyinfo != null) {

			String merchantKey = keyinfo.getMerchantkey();

			SignUtil signUtil = new SignUtil();
			// 生成签名
			String signmsg = signUtil.sign(YsbSignUtil.ybsdaikouSigiString(params), merchantKey);
			log.info("生成签名：" + signmsg);
			params.setSign(signmsg);
			// 返回页面参数
			request.setAttribute("temp", params);
			request.getRequestDispatcher("/pay/ysb/daikou/collect_submit1.jsp").forward(request, response);
		} else {
			// 返回页面参数
			outString(response, "商户号找不到Key");
		}

	}
	/**
	 * 下游接入 demo
	 * 
	 * @param param
	 * @param request
	 * @param response
	 * @throws Exception
	 */
	@RequestMapping(value = "ysbsigntime")
	public void ysbsigntime(YsbRequsetEntity params, HttpServletRequest request, HttpServletResponse response) throws Exception {

		log.info("原始订单信息：" + params);

		// 根据商户号查询key
		ChannleMerchantConfigKey keyinfo = ysbService.getChannelConfigKey(params.getMerchantId());
		if (keyinfo != null) {

			String merchantKey = keyinfo.getMerchantkey();

			SignUtil signUtil = new SignUtil();
			// 生成签名
			String signmsg = signUtil.sign(YsbSignUtil.ybsdaikouSigitime(params), merchantKey);
			log.info("生成签名：" + signmsg);
			params.setSign(signmsg);
			// 返回页面参数
			request.setAttribute("temp", params);
			request.getRequestDispatcher("/pay/ysb/daikou/subConstractExtension_submit.jsp").forward(request, response);
		} else {
			// 返回页面参数
			outString(response, "商户号找不到Key");
		}

	}
	/**
	 * 下游接入 demo
	 * 
	 * @param param
	 * @param request
	 * @param response
	 * @throws Exception
	 */
	@RequestMapping(value = "ysbsignquery")
	public void ysbsignquery(YsbRequsetEntity params, HttpServletRequest request, HttpServletResponse response) throws Exception {

		log.info("原始订单信息：" + params);

		// 根据商户号查询key
		ChannleMerchantConfigKey keyinfo = ysbService.getChannelConfigKey(params.getMerchantId());
		if (keyinfo != null) {

			String merchantKey = keyinfo.getMerchantkey();

			SignUtil signUtil = new SignUtil();
			// 生成签名
			String signmsg = signUtil.sign(YsbSignUtil.ybsdaikouSigiquery(params), merchantKey);
			log.info("生成签名：" + signmsg);
			params.setSign(signmsg);
			// 返回页面参数
			request.setAttribute("temp", params);
			request.getRequestDispatcher("/pay/ysb/daikou/queryOrderStatus_submit.jsp").forward(request, response);
		} else {
			// 返回页面参数
			outString(response, "商户号找不到Key");
		}

	}
	/**
	 * 下游接入 demo
	 * 
	 * @param param
	 * @param request
	 * @param response
	 * @throws Exception
	 */
	@RequestMapping(value = "ysbsignmerchant")
	public void ysbsignmerchant(YsbRequsetEntity params, HttpServletRequest request, HttpServletResponse response) throws Exception {

		log.info("原始订单信息：" + params);

		// 根据商户号查询key
		ChannleMerchantConfigKey keyinfo = ysbService.getChannelConfigKey(params.getMerchantId());
		if (keyinfo != null) {

			String merchantKey = keyinfo.getMerchantkey();

			SignUtil signUtil = new SignUtil();
			// 生成签名
			String signmsg = signUtil.sign(YsbSignUtil.ybsdaikouSigimerchant(params), merchantKey);
			log.info("生成签名：" + signmsg);
			params.setSign(signmsg);
			// 返回页面参数
			request.setAttribute("temp", params);
			request.getRequestDispatcher("/pay/ysb/daikou/querySubContractId_submit.jsp").forward(request, response);
		} else {
			// 返回页面参数
			outString(response, "商户号找不到Key");
		}

	}
	/**
	 * 下游接入 demo
	 * 
	 * @param param
	 * @param request
	 * @param response
	 * @throws Exception
	 */
	@RequestMapping(value = "ysbsignPay")
	public void ysbsignPay(YsbRequsetEntity params, HttpServletRequest request, HttpServletResponse response) throws Exception {

		log.info("原始订单信息：" + params);

		// 根据商户号查询key
		ChannleMerchantConfigKey keyinfo = ysbService.getChannelConfigKey(params.getMerchantId());
		if (keyinfo != null) {

			String merchantKey = keyinfo.getMerchantkey();

			SignUtil signUtil = new SignUtil();
			// 生成签名
			String signmsg = signUtil.sign(YsbSignUtil.ybsdaifuSigiPay(params), merchantKey);
			log.info("生成签名：" + signmsg);
			params.setSign(signmsg);
			// 返回页面参数
			request.setAttribute("temp", params);
			request.getRequestDispatcher("/pay/ysb/daifu/pay_submit.jsp").forward(request, response);
		} else {
			// 返回页面参数
			outString(response, "商户号找不到Key");
		}

	}
	/**
	 * 和上游交互 支付完成后同步返回支付结果
	 *
	 * @param request
	 *            requet对象
	 * @param response
	 *            response对象
	 * @param temp
	 *            银生宝返回的数据
	 * @throws Exception
	 */
	@RequestMapping(value = "bgPayResult")
	public void bgPayResult(CallBack callBack,HttpServletRequest request, HttpServletResponse response) throws Exception {

		response.setHeader("Content-type", "text/html;charset=UTF-8");
		String result_code="";
		log.info("异步返回的数据:" + callBack);
		callBack.setAccountId("2120170904150304001");
		callBack.setKey("30eccdd59dbee2");
		if(Service.validMac(callBack))
		{
			log.info("验证成功");
			result_code="200";
			// 处理这笔交易 修改订单表中的交易表
			ysbService.otherInvoke(callBack);
			// 交易id
			String tranId = callBack.getOrderId();
			// 查询商户上送原始信息
			OriginalOrderInfo originalInfo = ysbService.getOriginOrderInfo(tranId);

			// 给下游主动返回支付结果
			Bean2QueryStrUtil bean2Util = new Bean2QueryStrUtil();
			String path = originalInfo.getBgUrl() + "?" + bean2Util.bean2QueryStr(callBack);
			log.info("bgUrl 平台服务器重定向：" + path);
			String result = HttpClientUtil.post(originalInfo.getBgUrl(), bean2Util.bean2QueryStr(callBack));
			
			log.info("响应信息:"+result);
		}else
		{
			log.info("验证失败");
			result_code="202";
		}


		
	
		log.info("向下游 发送数据成功");

	}



}
