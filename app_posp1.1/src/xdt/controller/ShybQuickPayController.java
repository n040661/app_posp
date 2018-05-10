package xdt.controller;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URLDecoder;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;
import java.util.UUID;

import javax.annotation.Resource;
import javax.servlet.ServletContext;
import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.multipart.MultipartRequestEntity;
import org.apache.commons.httpclient.methods.multipart.Part;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.commons.CommonsMultipartFile;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.yeepay.g3.facade.yop.ca.dto.DigitalEnvelopeDTO;
import com.yeepay.g3.facade.yop.ca.enums.CertTypeEnum;
import com.yeepay.g3.frame.yop.ca.DigitalEnvelopeUtils;
import com.yeepay.g3.sdk.yop.utils.InternalConfig;

import net.sf.json.JSONObject;
import xdt.dto.BaseUtil;
import xdt.dto.quickPay.entity.ConsumeResponseEntity;
import xdt.dto.quickPay.entity.MessAgeResponseEntity;
import xdt.dto.quickPay.entity.MessageRequestEntity;
import xdt.dto.quickPay.util.MbUtilThread;
import xdt.dto.transfer_accounts.entity.DaifuRequestEntity;
import xdt.model.ChannleMerchantConfigKey;
import xdt.model.OriginalOrderInfo;
import xdt.model.PmsBusinessPos;
import xdt.quickpay.hengfeng.util.Bean2QueryStrUtil;
import xdt.quickpay.hengfeng.util.HttpClientUtil;
import xdt.quickpay.nbs.common.util.SignatureUtil;
import xdt.quickpay.shyb.entity.ShybQuickCallbackEntity;
import xdt.quickpay.shyb.entity.ShybQuickPayRequestEntity;
import xdt.quickpay.shyb.entity.ShybQuickPayResponseEntity;
import xdt.quickpay.shyb.entity.ShybQuickRequestEntity;
import xdt.quickpay.shyb.entity.ShybQuickResponseEntity;
import xdt.quickpay.shyb.entity.ShybTransferRequestEntity;
import xdt.quickpay.shyb.entity.ShybUpdateRateQueryRequestEntity;
import xdt.quickpay.shyb.entity.ShybUpdateRateRequestEntity;
import xdt.quickpay.shyb.entity.ShybUpdateRateResponseEntity;
import xdt.quickpay.shyb.util.Digest;
import xdt.quickpay.shyb.util.RegisterPartsBuilder;
import xdt.quickpay.shyb.util.TransferPartsBuilder;
import xdt.schedule.ThreadPool;
import xdt.service.IShybQuickPayService;
import xdt.service.OriginalOrderInfoService;
import xdt.util.BeanToMapUtil;
import xdt.util.HttpURLConection;
import xdt.util.UtilDate;

@Controller
@RequestMapping("shyb_app")
public class ShybQuickPayController extends BaseAction {

	private Logger logger = LoggerFactory.getLogger(this.getClass());
	
	@Resource
	private IShybQuickPayService shybQuickPay;
	@Resource
	private OriginalOrderInfoService Origi; // 原始信息Service

	/**
	 * 上海易宝签名
	 * 
	 * @param hfbRequest
	 * @param response
	 */
	@RequestMapping(value = "registerSign")
	public void registerSign(HttpServletRequest request, HttpServletResponse response, ShybQuickRequestEntity payRequest) {

		logger.info("--签名发来的参数：" + JSON.toJSONString(payRequest));
		ChannleMerchantConfigKey keyinfo = new ChannleMerchantConfigKey();
		try {
			keyinfo = shybQuickPay.getChannelConfigKey(payRequest.getCustomerNumber());
			// 获取商户秘钥
			String key = keyinfo.getMerchantkey();
			logger.info("签名前数据**********支付:" + beanToMap(payRequest));
			// String md5 = MD5Utils.sign(paramSrc, key, "UTF-8");
			String sign = SignatureUtil.getSign(beanToMap(payRequest), key, logger);
			logger.info("签名**********支付:" + sign);
			
			payRequest.setV_sign(sign);
			// 返回页面参数
			request.setCharacterEncoding("UTF-8");
			request.setAttribute("temp", payRequest);
			request.getRequestDispatcher("/pay/shyb/shyb_register_submit.jsp").forward(request, response);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
	/**
	 * 上海易宝支付签名
	 * 
	 * @param hfbRequest
	 * @param response
	 */
	@RequestMapping(value = "paySign")
	public void paySign(HttpServletRequest request, HttpServletResponse response, ShybQuickPayRequestEntity payRequest) {

		logger.info("--上海易宝费率设置签名发来的参数：" + JSON.toJSONString(payRequest));
		ChannleMerchantConfigKey keyinfo = new ChannleMerchantConfigKey();
		try {
			keyinfo = shybQuickPay.getChannelConfigKey(payRequest.getCustomerNumber());
			// 获取商户秘钥
			String key = keyinfo.getMerchantkey();
			logger.info("签名前数据**********支付:" + beanToMap(payRequest));
			// String md5 = MD5Utils.sign(paramSrc, key, "UTF-8");
			String sign = SignatureUtil.getSign(beanToMap(payRequest), key, logger);
			logger.info("签名**********支付:" + sign);
			
			payRequest.setV_sign(sign);
			// 返回页面参数
			request.setCharacterEncoding("UTF-8");
			request.setAttribute("temp", payRequest);
			request.getRequestDispatcher("/pay/shyb/shyb_pay_submit.jsp").forward(request, response);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
	/**
	 * 上海易宝代付签名
	 * 
	 * @param hfbRequest
	 * @param response
	 */
	@RequestMapping(value = "transferSign")
	public void transferSign(HttpServletRequest request, HttpServletResponse response, ShybTransferRequestEntity payRequest) {

		logger.info("--上海易宝费率设置签名发来的参数：" + JSON.toJSONString(payRequest));
		ChannleMerchantConfigKey keyinfo = new ChannleMerchantConfigKey();
		try {
			keyinfo = shybQuickPay.getChannelConfigKey(payRequest.getCustomerNumber());
			// 获取商户秘钥
			String key = keyinfo.getMerchantkey();
			logger.info("签名前数据**********支付:" + beanToMap(payRequest));
			// String md5 = MD5Utils.sign(paramSrc, key, "UTF-8");
			String sign = SignatureUtil.getSign(beanToMap(payRequest), key, logger);
			logger.info("签名**********支付:" + sign);
			
			payRequest.setV_sign(sign);
			// 返回页面参数
			request.setCharacterEncoding("UTF-8");
			request.setAttribute("temp", payRequest);
			request.getRequestDispatcher("/pay/shyb/shyb_transfer_submit.jsp").forward(request, response);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
	/**
	 * 上海易宝修改费率签名
	 * 
	 * @param hfbRequest
	 * @param response
	 */
	@RequestMapping(value = "updateRateSign")
	public void updateRateSign(HttpServletRequest request, HttpServletResponse response, ShybUpdateRateRequestEntity payRequest) {

		logger.info("--上海易宝费率设置签名发来的参数：" + JSON.toJSONString(payRequest));
		ChannleMerchantConfigKey keyinfo = new ChannleMerchantConfigKey();
		try {
			keyinfo = shybQuickPay.getChannelConfigKey(payRequest.getCustomerNumber());
			// 获取商户秘钥
			String key = keyinfo.getMerchantkey();
			logger.info("签名前数据**********支付:" + beanToMap(payRequest));
			// String md5 = MD5Utils.sign(paramSrc, key, "UTF-8");
			String sign = SignatureUtil.getSign(beanToMap(payRequest), key, logger);
			logger.info("签名**********支付:" + sign);
			
			payRequest.setV_sign(sign);
			// 返回页面参数
			request.setCharacterEncoding("UTF-8");
			request.setAttribute("temp", payRequest);
			request.getRequestDispatcher("/pay/shyb/shyb_updateRate_submit.jsp").forward(request, response);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
	/**
	 * 上海易宝注册请求参数
	 * 
	 * @param request
	 * @param response
	 * @throws Exception
	 */
	@RequestMapping(value = "/register")
	public void registerScanParam(@RequestParam(value ="file",required =false ) MultipartFile[] file, 
			HttpServletRequest request, HttpServletResponse response, ShybQuickRequestEntity param)
			throws Exception {
		response.setHeader("Access-Control-Allow-Origin", "*");
		response.setContentType("text/html;charset=utf-8");
		Map<String, String> result = new HashMap<String, String>();
		logger.info("上海易宝注册进来了");
		 // 获得原始文件名  
         String bankCardPhoto="";  //银行卡正面照
         String idCardBackPhoto=""; //身份证背面照
         String idCardPhoto=""; //身份证正面照
         String personPhoto=""; //身份证+银行卡+本人合照
		logger.info("下游上送参数:{}", param);
		if (!StringUtils.isEmpty(param.getCustomerNumber())) {
			logger.info("下游上送签名串{}" + param.getV_sign());
			// 查询商户密钥
			ChannleMerchantConfigKey keyinfo = shybQuickPay.getChannelConfigKey(param.getCustomerNumber());
			// ------------------------需要改签名
			String merchantKey = keyinfo.getMerchantkey();
			SignatureUtil signUtil = new SignatureUtil();

			Map map = BeanToMapUtil.convertBean(param);

			if (signUtil.checkSign(map, merchantKey, logger)) {
				logger.info("对比签名成功");
				   // 获得项目的路径  
		        ServletContext sc = request.getSession().getServletContext();		        
		        String path = sc.getRealPath("/images") + "/person"; // 设定文件保存的目录 
				for(int i=0;i<file.length;i++)
				{
				
				  String fileName = file[i].getOriginalFilename();  
		          logger.info("原始文件名:" + fileName);
		          
		
		          if(fileName.equals("idCardBackPhoto.jpg"))
		          {
		        	  idCardBackPhoto=fileName;
		        	  File iCBPhoto = new File(path+"/"+idCardBackPhoto);
		        	  param.setIdCardBackPhoto(iCBPhoto);
		        	  continue;
		          }
		          if(fileName.equals("idCardPhoto.jpg"))
		          {
		        	  idCardPhoto=fileName;
		        	  File iCPhoto = new File(path+"/"+idCardPhoto);
		        	  param.setIdCardPhoto(iCPhoto);
		        	  continue;
		          }
		          if(fileName.equals("personPhoto.jpg")) {
		        	  
		        	  personPhoto=fileName;   
		        	  File pPhoto = new File(path+"/"+personPhoto);
		        	  param.setPersonPhoto(pPhoto);
		        	  continue;
		          }
		          if(fileName.equals("bankCardPhoto.jpg"));
		          {  
		        	  bankCardPhoto=fileName;
		        	  File bCPhoto = new File(path+"/"+bankCardPhoto);
		        	  param.setBankCardPhoto(bCPhoto);
		        	  continue;  
		          }
		   
				}
				result = shybQuickPay.updateHandle(param);
				logger.info("上海易宝响应信息:" + result);
				ShybQuickResponseEntity quick = (ShybQuickResponseEntity) BeanToMapUtil
						.convertMap(ShybQuickResponseEntity.class, result);
				String sign = SignatureUtil.getSign(beanToMap(quick), merchantKey, logger);
				result.put("v_sign", sign);
			} else {
				logger.error("签名错误!");
				result.put("v_code", "02");
				result.put("v_msg", "签名错误!");
			}

		} else {
			logger.error("上送交易参数空!");
			result.put("v_code", "01");
			result.put("v_msg", "上送交易参数空");
		}
		outString(response, gson.toJson(result));
	}
	/**
	 * 上海易宝修改费率请求参数
	 * 
	 * @param request
	 * @param response
	 * @throws Exception
	 */
	@RequestMapping(value = "updateRate")
	public void updateRateScanParam(HttpServletRequest request, HttpServletResponse response, ShybUpdateRateRequestEntity param)
			throws Exception {
		response.setHeader("Access-Control-Allow-Origin", "*");
		response.setContentType("text/html;charset=utf-8");
		Map<String, String> result = new HashMap<String, String>();
		logger.info("上海易宝修改费率进来了");
		logger.info("下游上送参数:{}", param);
		if (!StringUtils.isEmpty(param.getCustomerNumber())) {
			logger.info("下游上送签名串{}" + param.getV_sign());
			// 查询商户密钥
			ChannleMerchantConfigKey keyinfo = shybQuickPay.getChannelConfigKey(param.getCustomerNumber());
			// ------------------------需要改签名
			String merchantKey = keyinfo.getMerchantkey();
			SignatureUtil signUtil = new SignatureUtil();

			Map map = BeanToMapUtil.convertBean(param);

			if (signUtil.checkSign(map, merchantKey, logger)) {
				logger.info("对比签名成功");
				result = shybQuickPay.updateRate(param);
				logger.info("上海易宝响应信息:" + result);
				ShybUpdateRateResponseEntity updateRate = (ShybUpdateRateResponseEntity) BeanToMapUtil
						.convertMap(ShybUpdateRateResponseEntity.class, result);
				String sign = SignatureUtil.getSign(beanToMap(updateRate), merchantKey, logger);
				result.put("v_sign", sign);
			} else {
				logger.error("签名错误!");
				result.put("v_code", "02");
				result.put("v_msg", "签名错误!");
			}

		} else {
			logger.error("上送交易参数空!");
			result.put("v_code", "01");
			result.put("v_msg", "上送交易参数空");
		}
		outString(response, gson.toJson(result));
	}
	/**
	 * 上海易宝修改费率请求参数
	 * 
	 * @param request
	 * @param response
	 * @throws Exception
	 */
	@RequestMapping(value = "pay")
	public void payScanParam(HttpServletRequest request, HttpServletResponse response, ShybQuickPayRequestEntity param)
			throws Exception {
		response.setHeader("Access-Control-Allow-Origin", "*");
		response.setContentType("text/html;charset=utf-8");
		Map<String, String> result = new HashMap<String, String>();
		logger.info("上海易宝支付进来了");
		logger.info("下游上送参数:{}", param);
		if (!StringUtils.isEmpty(param.getCustomerNumber())) {
			logger.info("下游上送签名串{}" + param.getV_sign());
			// 查询商户密钥
			ChannleMerchantConfigKey keyinfo = shybQuickPay.getChannelConfigKey(param.getCustomerNumber());
			// ------------------------需要改签名
			String merchantKey = keyinfo.getMerchantkey();
			SignatureUtil signUtil = new SignatureUtil();

			Map map = BeanToMapUtil.convertBean(param);

			if (signUtil.checkSign(map, merchantKey, logger)) {
				logger.info("对比签名成功");
				result = shybQuickPay.payHandle(param);
				logger.info("上海易宝响应信息:" + result);
				if("00".equals(result.get("code")))
				{
					logger.info("URL 重定向：" + result.get("url"));
					request.getSession();
					response.setCharacterEncoding("UTF-8");
					response.sendRedirect(result.get("url").replace(" ", " "));
				}
			
			} else {
				logger.error("签名错误!");
				result.put("v_code", "02");
				result.put("v_msg", "签名错误!");
			}

		} else {
			logger.error("上送交易参数空!");
			result.put("v_code", "01");
			result.put("v_msg", "上送交易参数空");
		}
		outString(response, gson.toJson(result));
	}
	/**
	 * 上海易宝修改费率请求参数
	 * 
	 * @param request
	 * @param response
	 * @throws Exception
	 */
	@RequestMapping(value = "transfer")
	public void  transferParam(HttpServletRequest request, HttpServletResponse response, ShybTransferRequestEntity param)
			throws Exception {
		response.setHeader("Access-Control-Allow-Origin", "*");
		response.setContentType("text/html;charset=utf-8");
		Map<String, String> result = new HashMap<String, String>();
		logger.info("上海易宝代付进来了");
		logger.info("下游上送参数:{}", param);
		// 查询上游商户号
		PmsBusinessPos busInfo = shybQuickPay.selectKey(param.getCustomerNumber());

		String MainCustomerNumber = busInfo.getBusinessnum();

		logger.info("上海易宝上送的商户号:" + MainCustomerNumber);
		String key = busInfo.getKek();

		logger.info("上海易宝上送的密钥:" + key);
		PostMethod postMethod = new PostMethod("https://skb.yeepay.com/skb-app/withDrawApi.action");
		HttpClient client = new HttpClient();
		if (!StringUtils.isEmpty(param.getCustomerNumber())) {
			logger.info("下游上送签名串{}" + param.getV_sign());
			// 查询商户密钥
			ChannleMerchantConfigKey keyinfo = shybQuickPay.getChannelConfigKey(param.getCustomerNumber());
			// ------------------------需要改签名
			String merchantKey = keyinfo.getMerchantkey();
			SignatureUtil signUtil = new SignatureUtil();

			Map map = BeanToMapUtil.convertBean(param);

			if (signUtil.checkSign(map, merchantKey, logger)) {
				logger.info("对比签名成功");
				StringBuffer signature = new StringBuffer();
				signature.append(param.getAmount() == null ? "" : param.getAmount())
						.append(param.getSubContractId() == null ? "" : param.getSubContractId())
						.append(param.getExternalNo() == null ? "" : param.getExternalNo())
						.append(MainCustomerNumber == null ? "" : MainCustomerNumber)
						.append(param.getTransferWay() == null ? "" : param.getTransferWay())
						.append(BaseUtil.url+"/shyb_app/notifyUrl.action");
				logger.info("上海易宝生成签名前的数据：" + signature.toString());
				String hmac = Digest.hmacSign(signature.toString(), key);
				logger.info("上海易宝生成的签名：" + hmac);

				Part[] parts = new TransferPartsBuilder().setHmac(hmac).setMainCustomerNumber(MainCustomerNumber)
						.setAmount(param.getAmount()).
						setCustomerNumber(param.getSubContractId())
						.setExternalNo(param.getExternalNo())
						.setTransferWay(param.getTransferWay())
						.setCallBackUrl(BaseUtil.url+"/shyb_app/notifyUrl.action")
						.generateParams();
				  postMethod.addRequestHeader("Content-Type",
			                "application/x-www-form-urlencoded; charset=UTF-8");
				postMethod.setRequestEntity(new MultipartRequestEntity(parts, postMethod.getParams()));

				logger.info("上海易宝发送数据:" + postMethod.toString());

				int status = client.executeMethod(postMethod);
				logger.info("上海易宝返回的状态码:" + status);
				logger.info("上海易宝返回的响应数据:" + postMethod.getResponseBodyAsString());
				logger.info("上海易宝响应信息:" + result);
			
			} else {
				logger.error("签名错误!");
				result.put("v_code", "02");
				result.put("v_msg", "签名错误!");
			}

		} else {
			logger.error("上送交易参数空!");
			result.put("v_code", "01");
			result.put("v_msg", "上送交易参数空");
		}
		outString(response, gson.toJson(result));
	}
	/**
	 * 上海易宝异步响应信息
	 * 
	 * @param request
	 * @param response
	 * @throws Exception
	 */
	@ResponseBody
	@RequestMapping(value = "notifyUrl")
	public void notifyUrl(HttpServletRequest request, HttpServletResponse response) {
		try {
			logger.info("上海易宝异步通知来了");
			Map<String, String> result = new HashMap<String, String>();
			BufferedReader br = new BufferedReader(
					new InputStreamReader((ServletInputStream) request.getInputStream(), "UTF-8"));
			String line = null;
			StringBuffer sb = new StringBuffer();
			while ((line = br.readLine()) != null) {
				sb.append(line);
			}
			String key = "";
			String appMsg = sb.toString();
			logger.info("请求参数：" + appMsg);
			if (!StringUtils.isEmpty(appMsg)) {
				response.getWriter().write("SUCCESS");
				logger.info("上海易宝解密数据:" + appMsg);
				// 开始解密
				String[] strs = appMsg.split("&");
				Map<String, String> m = new HashMap<String, String>();
				for(String s:strs){
				String[] ms = s.split("=");
				m.put(ms[0], ms[1]);
				}
				logger.info("上海易宝返回的状态码" + m.get("status"));
				logger.info("上海易宝返回的订单号:" + m.get("requestId"));
				OriginalOrderInfo originalInfo = null;
				if (m.get("requestId") != null && m.get("requestId") != "") {
					originalInfo = this.shybQuickPay.getOriginOrderInfo(m.get("requestId"));
				}
				ChannleMerchantConfigKey keyinfo = shybQuickPay.getChannelConfigKey(originalInfo.getPid());
				// 获取商户秘钥
				key = keyinfo.getMerchantkey();
				ShybQuickCallbackEntity consume = (ShybQuickCallbackEntity) BeanToMapUtil
						.convertMap(ShybQuickCallbackEntity.class, result);
				// 修改订单状态
				shybQuickPay.otherInvoke(consume);
				m.put("customerNumber", originalInfo.getPid());

				// 生成签名
				String sign = SignatureUtil.getSign(beanToMap(consume), key, logger);
				m.put("v_sign", sign);

				logger.info("异步之前的参数：" + m);
				ShybQuickCallbackEntity consumeResponseEntity = (ShybQuickCallbackEntity) BeanToMapUtil
						.convertMap(ShybQuickCallbackEntity.class, m);
				Bean2QueryStrUtil bean2Util = new Bean2QueryStrUtil();
				logger.info("异步给下游传的数据参数：" + bean2Util.bean2QueryStr(consumeResponseEntity));			
				String html = HttpClientUtil.post(originalInfo.getBgUrl(),
						bean2Util.bean2QueryStr(consumeResponseEntity));
				logger.info("下游返回状态" + html);
				JSONObject ob = JSONObject.fromObject(html);
				Iterator it = ob.keys();
				Map<String, String> map = new HashMap<>();
				while (it.hasNext()) {
					String keys = (String) it.next();
					if (keys.equals("success")) {
						String value = ob.getString(keys);
						logger.info("异步回馈的结果:" + "\t" + value);
						map.put("success", value);
					}
				}
				if (map.get("success").equals("false")) {

					logger.info("启动线程进行异步通知");
					// 启线程进行异步通知
					ThreadPool.executor(
							new MbUtilThread(originalInfo.getBgUrl(), bean2Util.bean2QueryStr(consumeResponseEntity)));
				}
				logger.info("向下游 发送数据成功");

			} else {
				response.getWriter().write("FAIL");
				logger.error("回调的参数为空!");
				result.put("v_code", "15");
				result.put("v_msg", "请求失败");
			}
			// outString(response, str);
		} catch (Exception e) {
			logger.info("易宝异步回调异常:" + e);
			e.printStackTrace();
		}
	}
	/**
	 * 上海易宝代付异步响应信息
	 * 
	 * @param request
	 * @param response
	 * @throws Exception
	 */
	@ResponseBody
	@RequestMapping(value = "transfer_notifyUrl")
	public void transfernotifyUrl(HttpServletRequest request, HttpServletResponse response) {
		try {
			logger.info("上海易宝代付异步通知来了");
			Map<String, String> result = new HashMap<String, String>();
			BufferedReader br = new BufferedReader(
					new InputStreamReader((ServletInputStream) request.getInputStream(), "UTF-8"));
			String line = null;
			StringBuffer sb = new StringBuffer();
			while ((line = br.readLine()) != null) {
				sb.append(line);
			}
			String key = "";
			String appMsg = sb.toString();
			logger.info("请求参数：" + appMsg);
			if (!StringUtils.isEmpty(appMsg)) {
				response.getWriter().write("SUCCESS");
				logger.info("上海易宝解密数据:" + appMsg);
				// 开始解密
				String[] strs = appMsg.split("&");
				Map<String, String> m = new HashMap<String, String>();
				for(String s:strs){
				String[] ms = s.split("=");
				m.put(ms[0], ms[1]);
				}
				logger.info("上海易宝返回的状态码" + m.get("status"));
				logger.info("上海易宝返回的订单号:" + m.get("requestId"));
				OriginalOrderInfo originalInfo = null;
				if (m.get("requestId") != null && m.get("requestId") != "") {
					originalInfo = this.shybQuickPay.getOriginOrderInfo(m.get("requestId"));
				}
				ChannleMerchantConfigKey keyinfo = shybQuickPay.getChannelConfigKey(originalInfo.getPid());
				// 获取商户秘钥
				key = keyinfo.getMerchantkey();
				ShybQuickCallbackEntity consume = (ShybQuickCallbackEntity) BeanToMapUtil
						.convertMap(ShybQuickCallbackEntity.class, result);
				// 修改订单状态
				shybQuickPay.otherInvoke(consume);
				m.put("customerNumber", originalInfo.getPid());

				// 生成签名
				String sign = SignatureUtil.getSign(beanToMap(consume), key, logger);
				m.put("v_sign", sign);

				logger.info("异步之前的参数：" + m);
				ShybQuickCallbackEntity consumeResponseEntity = (ShybQuickCallbackEntity) BeanToMapUtil
						.convertMap(ShybQuickCallbackEntity.class, m);
				Bean2QueryStrUtil bean2Util = new Bean2QueryStrUtil();
				logger.info("异步给下游传的数据参数：" + bean2Util.bean2QueryStr(consumeResponseEntity));			
				String html = HttpClientUtil.post(originalInfo.getBgUrl(),
						bean2Util.bean2QueryStr(consumeResponseEntity));
				logger.info("下游返回状态" + html);
				JSONObject ob = JSONObject.fromObject(html);
				Iterator it = ob.keys();
				Map<String, String> map = new HashMap<>();
				while (it.hasNext()) {
					String keys = (String) it.next();
					if (keys.equals("success")) {
						String value = ob.getString(keys);
						logger.info("异步回馈的结果:" + "\t" + value);
						map.put("success", value);
					}
				}
				if (map.get("success").equals("false")) {

					logger.info("启动线程进行异步通知");
					// 启线程进行异步通知
					ThreadPool.executor(
							new MbUtilThread(originalInfo.getBgUrl(), bean2Util.bean2QueryStr(consumeResponseEntity)));
				}
				logger.info("向下游 发送数据成功");

			} else {
				response.getWriter().write("FAIL");
				logger.error("回调的参数为空!");
				result.put("v_code", "15");
				result.put("v_msg", "请求失败");
			}
			// outString(response, str);
		} catch (Exception e) {
			logger.info("易宝异步回调异常:" + e);
			e.printStackTrace();
		}
	}
	/**
	 * 上海易宝同步响应信息
	 * 
	 * @param request
	 * @param response
	 * @throws Exception
	 */
	@ResponseBody
	@RequestMapping(value = "returnUrl")
	public void returnUrl(HttpServletRequest request, HttpServletResponse response) {
		
			logger.info("上海易宝通知来了");
			TreeMap<String, String> result = new TreeMap<String, String>();
			String orderId = request.getParameter("orderNo");
			logger.info("上海易宝同步返回的订单号:" + orderId);
			OriginalOrderInfo originalInfo=new OriginalOrderInfo();
			ChannleMerchantConfigKey keyinfo=new ChannleMerchantConfigKey();
			try {
				originalInfo = Origi.get(orderId);
				keyinfo = shybQuickPay.getChannelConfigKey(originalInfo.getPid());
			} catch (Exception e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			logger.info("上海易宝同步订单数据:" + JSON.toJSON(originalInfo));
			
			// 获取商户秘钥
			String key = keyinfo.getMerchantkey();
			result.put("code", "");
			result.put("message", "");
			result.put("customerNumber", originalInfo.getPid());
			result.put("requestId", orderId);
			ShybQuickPayResponseEntity consume = (ShybQuickPayResponseEntity) BeanToMapUtil
					.convertMap(ShybQuickPayResponseEntity.class, result);
			String sign = SignatureUtil.getSign(beanToMap(consume), key, logger);
			result.put("v_sign", sign);
			String params = HttpURLConection.parseParams(result);
			logger.info("给下游同步的数据:" + params);
			request.getSession();
			try {
				// 给下游手动返回支付结果
				if (originalInfo.getPageUrl().indexOf("?") == -1) {

					String path = originalInfo.getPageUrl() + "?" + params;
					logger.info("pageUrl 商户页面 重定向：" + path);

					response.sendRedirect(path.replace(" ", ""));
				} else {
					logger.info("pageUrl 商户页面 重定向：" + originalInfo.getPageUrl());
					String path = originalInfo.getPageUrl() + "&" + params;
					logger.info("pageUrl 商户页面 重定向：" + path);
					response.sendRedirect(path.replace(" ", ""));
				}
			} catch (Exception e) {
				// TODO: handle exception
				e.printStackTrace();
			}
	}


	/**
	 * bean 转化为实体
	 * 
	 * @param bean
	 * @return
	 */
	public static HashMap<String, Object> beanToMap(Object bean) {
		HashMap<String, Object> map = new HashMap<String, Object>();
		if (null == bean) {
			return map;
		}
		Class<?> clazz = bean.getClass();
		BeanInfo beanInfo = null;
		try {
			beanInfo = Introspector.getBeanInfo(clazz);
		} catch (IntrospectionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		PropertyDescriptor[] descriptors = beanInfo.getPropertyDescriptors();
		for (PropertyDescriptor descriptor : descriptors) {
			String propertyName = descriptor.getName();
			if (!"class".equals(propertyName)) {
				Method method = descriptor.getReadMethod();
				String result;
				try {
					result = (String) method.invoke(bean);
					if (null != result) {
						map.put(propertyName, result);
					} else {
						map.put(propertyName, "");
					}
				} catch (IllegalArgumentException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IllegalAccessException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (InvocationTargetException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}

		return map;
	}

}
