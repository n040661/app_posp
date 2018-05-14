package xdt.controller;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URLDecoder;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSON;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import xdt.dto.lhzf.LhzfResponse;
import xdt.dto.pay.PayRequest;
import xdt.dto.pufa.PayRequestEntity;
import xdt.dto.quickPay.entity.ConsumeResponseEntity;
import xdt.dto.quickPay.util.MbUtilThread;
import xdt.dto.ys.YSThread;
import xdt.model.ChannleMerchantConfigKey;
import xdt.model.OriginalOrderInfo;
import xdt.model.PmsBusinessPos;
import xdt.quickpay.gyy.util.ApiUtil;
import xdt.quickpay.hddh.entity.CallbackEntity;
import xdt.quickpay.hddh.entity.RegisterCallbackEntity;
import xdt.quickpay.hddh.entity.RegisterRequestEntity;
import xdt.quickpay.hddh.entity.ReplacePayQueryRequestEntity;
import xdt.quickpay.hddh.entity.ReplacePayRequestEntity;
import xdt.quickpay.hddh.entity.ReplaceQueryRequestEntity;
import xdt.quickpay.hddh.util.Base64;
import xdt.quickpay.hengfeng.util.Bean2QueryStrUtil;
import xdt.quickpay.hengfeng.util.HttpClientUtil;
import xdt.quickpay.nbs.common.util.SignatureUtil;
import xdt.schedule.ThreadPool;
import xdt.service.HfQuickPayService;
import xdt.service.IClientCollectionPayService;
import xdt.service.IYSService;
import xdt.service.IhddhService;
import xdt.service.OriginalOrderInfoService;
import xdt.util.BeanToMapUtil;
import xdt.util.HttpURLConection;
import xdt.util.HttpUtil;
import xdt.util.JsdsUtil;
import xdt.util.UtilDate;
import xdt.util.utils.MD5Utils;
import xdt.util.utils.RequestUtils;
import xdt.util.utils.UtilThread;

/**
 * @author 作者 E-mail:
 * @version 创建时间：2018年2月2日 上午9:53:01 类说明
 */
@Controller
@RequestMapping("/hddh")
public class HddhController extends BaseAction {

	/**
	 * 日志记录
	 */
	private Logger log = LoggerFactory.getLogger(this.getClass());
	@Resource
	private IhddhService hddhService;

	@Resource
	private OriginalOrderInfoService Origi; // 原始信息Service

	/**
	 * 绑卡签名
	 * 
	 * @param hfbRequest
	 * @param response
	 */
	@RequestMapping(value = "registerSign")
	public void paySign(RegisterRequestEntity entity, HttpServletRequest request, HttpServletResponse response) {

		logger.info("上海漪雷绑卡签名发来的参数：" + JSON.toJSONString(entity));
		ChannleMerchantConfigKey keyinfo = new ChannleMerchantConfigKey();
		try {
			keyinfo = hddhService.getChannelConfigKey(entity.getMerid());

			// 获取商户秘钥
			String key = keyinfo.getMerchantkey();
			logger.info("下游商户密钥:" + keyinfo);
			String sign = SignatureUtil.getSign(beanToMap(entity), key, log);
			entity.setV_sign(sign);
			// 返回页面参数
			request.setCharacterEncoding("UTF-8");
			request.setAttribute("temp", entity);
			request.getRequestDispatcher("/pay/hddh/transfer_register_submit.jsp").forward(request, response);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * 代还签名
	 * 
	 * @param hfbRequest
	 * @param response
	 */
	@RequestMapping(value = "replacePaySign")
	public void paySign(ReplacePayRequestEntity entity, HttpServletRequest request, HttpServletResponse response) {

		response.setHeader("Access-Control-Allow-Origin", "*");
		response.setContentType("text/html;charset=utf-8");
		logger.info("上海漪雷代还签名发来的参数：" + JSON.toJSONString(entity));
		ChannleMerchantConfigKey keyinfo = new ChannleMerchantConfigKey();
		try {
			keyinfo = hddhService.getChannelConfigKey(entity.getMerid());

			// 获取商户秘钥
			String key = keyinfo.getMerchantkey();
			logger.info("下游商户密钥:" + keyinfo);
			String sign = SignatureUtil.getSign(beanToMap(entity), key, log);
			entity.setV_sign(sign);
			// 返回页面参数
			request.setCharacterEncoding("UTF-8");
			request.setAttribute("temp", entity);
			request.getRequestDispatcher("/pay/hddh/transfer_pay_submit.jsp").forward(request, response);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * 上海漪雷代还签约
	 * 
	 * @param response
	 * @param payRequest
	 */
	@RequestMapping(value = "register")
	public void register(HttpServletResponse response, HttpServletRequest request, RegisterRequestEntity param) {

		logger.info("上海漪雷代还签约下游上传参数：" + JSON.toJSONString(param));
		Map<String, String> result = new HashMap<String, String>();
		PmsBusinessPos pmsBusinessPos = new PmsBusinessPos();
		if (!StringUtils.isEmpty(param.getMerid())) {
			logger.info("下游上送签名串{}" + param.getV_sign());
			// 查询商户密钥
			ChannleMerchantConfigKey keyinfo = new ChannleMerchantConfigKey();
			try {
				pmsBusinessPos = hddhService.selectKey(param.getMerid());
				keyinfo = hddhService.getChannelConfigKey(param.getMerid());
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			// ------------------------需要改签名
			String merchantKey = keyinfo.getMerchantkey();
			SignatureUtil signUtil = new SignatureUtil();

			Map map = BeanToMapUtil.convertBean(param);

			if (signUtil.checkSign(map, merchantKey, log)) {
				logger.info("对比签名成功");
				try {
					result = hddhService.registerHandle(param);
					switch (pmsBusinessPos.getBusinessnum()) {
					case "12345678":// 上海漪雷代还
						String url = result.get("html");
						logger.info("URL 重定向：" + url);
						request.getSession();
						response.setCharacterEncoding("GBK");
						response.sendRedirect(url.replace(" ", " "));
						break;
					default:
						break;
					}
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
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
	}

	/**
	 * 上海漪雷代还
	 * 
	 * @param response
	 * @param payRequest
	 */
	@RequestMapping(value = "replacePay")
	public void register(ReplacePayRequestEntity entity, HttpServletResponse response, HttpServletRequest request) {

		logger.info("上海漪雷代还签约下游上传参数：" + JSON.toJSONString(entity));
		Map<String, String> result = new HashMap<String, String>();
		PmsBusinessPos pmsBusinessPos = new PmsBusinessPos();
		if (!StringUtils.isEmpty(entity.getMerid())) {
			logger.info("下游上送签名串{}" + entity.getV_sign());
			// 查询商户密钥
			ChannleMerchantConfigKey keyinfo = new ChannleMerchantConfigKey();
			try {
				pmsBusinessPos = hddhService.selectKey(entity.getMerid());
				keyinfo = hddhService.getChannelConfigKey(entity.getMerid());
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			// ------------------------需要改签名
			String merchantKey = keyinfo.getMerchantkey();
			SignatureUtil signUtil = new SignatureUtil();

			Map map = BeanToMapUtil.convertBean(entity);

			if (signUtil.checkSign(map, merchantKey, log)) {
				logger.info("对比签名成功");
				try {
					result = hddhService.replaceHandle(entity);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
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
		logger.info("上海漪雷代还返回结果:{}"+JSON.toJSON(result));
		try {
			outString(response,JSON.toJSON(result));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
	/**
	 * 上海漪雷代还
	 * 
	 * @param response
	 * @param payRequest
	 */
	@RequestMapping(value = "replacePayQuery")
	public void replacePayQuery(ReplacePayQueryRequestEntity entity, HttpServletResponse response, HttpServletRequest request) {

		logger.info("上海漪雷代还查询下游上传参数：" + JSON.toJSONString(entity));
		Map<String, String> result = new HashMap<String, String>();
		PmsBusinessPos pmsBusinessPos = new PmsBusinessPos();
		if (!StringUtils.isEmpty(entity.getMerid())) {
			logger.info("下游上送签名串{}" + entity.getV_sign());
			// 查询商户密钥
			ChannleMerchantConfigKey keyinfo = new ChannleMerchantConfigKey();
			try {
				pmsBusinessPos = hddhService.selectKey(entity.getMerid());
				keyinfo = hddhService.getChannelConfigKey(entity.getMerid());
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			// ------------------------需要改签名
			String merchantKey = keyinfo.getMerchantkey();
			SignatureUtil signUtil = new SignatureUtil();

			Map map = BeanToMapUtil.convertBean(entity);

			if (signUtil.checkSign(map, merchantKey, log)) {
				logger.info("对比签名成功");
				try {
					logger.info("#################上海漪雷代还查询######################");
					String cooperator_item_id = entity.getCooperator_item_id();
					String repayPlanId = entity.getRepayPlanId();
					String cooperatorId = "b3b4f7f52060ab7fcc81d9f60382ee1e";

					Map<String, String> maps = new HashMap<String, String>();
					maps.put("cooperator_item_id", cooperator_item_id);
					maps.put("repayPlanId", repayPlanId);
					maps.put("cooperatorId", cooperatorId);
					net.sf.json.JSONObject j = net.sf.json.JSONObject.fromObject(maps);
					logger.info("海德绑卡签名json数据:" + j.toString());
					xdt.quickpay.hddh.util.MD5 md = new xdt.quickpay.hddh.util.MD5();
					byte[] raw;
					raw = j.toString().getBytes("utf-8");
					String data = Base64.encode(raw, 0, raw.length);
					String sign = "data=" + data + "&key=bb946c036823d4372617c366e7939efd";
					logger.info("海德绑卡生成的签名前的数据:" + sign);
					String signMsg = md.md5(sign);
					logger.info("海德绑卡生成的签名:" + signMsg);

					String url = "http://api.kuaikuaifu.net/ypapi/repay/queryRepayItemStatus.do";

					String params = "sign=" + signMsg + "&cooperatorId=" + cooperatorId + "&data=" + data;

					String path = url + "?" + params;

					HttpURLConection http = new HttpURLConection();
					String results = http.httpURLConectionGET(path, "UTF-8");
					logger.info("海德绑卡响应结果" + results);
					result = ApiUtil.toMap(results);
					logger.info("海德解析map结果" + result);
					String datas = new String(xdt.quickpay.jbb.util.Base64.decode(result.get("data")),
							Charset.forName("UTF-8"));
					result = ApiUtil.toMap(datas);
					logger.info("海德解析data结果" + result);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
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
		logger.info("返回结果:{}"+result);
		try {
			outString(response, result);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
	/**
	 * 上海漪雷代还
	 * 
	 * @param response
	 * @param payRequest
	 */
	@RequestMapping(value = "replaceQuery")
	public void replaceQuery(ReplaceQueryRequestEntity entity, HttpServletResponse response, HttpServletRequest request) {

		logger.info("上海漪雷代还计划创建查询下游上传参数：" + JSON.toJSONString(entity));
		Map<String, String> result = new HashMap<String, String>();
		PmsBusinessPos pmsBusinessPos = new PmsBusinessPos();
		if (!StringUtils.isEmpty(entity.getMerid())) {
			logger.info("下游上送签名串{}" + entity.getV_sign());
			// 查询商户密钥
			ChannleMerchantConfigKey keyinfo = new ChannleMerchantConfigKey();
			try {
				pmsBusinessPos = hddhService.selectKey(entity.getMerid());
				keyinfo = hddhService.getChannelConfigKey(entity.getMerid());
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			// ------------------------需要改签名
			String merchantKey = keyinfo.getMerchantkey();
			SignatureUtil signUtil = new SignatureUtil();

			Map map = BeanToMapUtil.convertBean(entity);

			if (signUtil.checkSign(map, merchantKey, log)) {
				logger.info("对比签名成功");
				try {
					logger.info("#################上海漪雷代还查询######################");
					String cooperator_repay_order_id = entity.getCooperator_repay_order_id();
					String cooperatorId = "b3b4f7f52060ab7fcc81d9f60382ee1e";

					Map<String, String> maps = new HashMap<String, String>();
					maps.put("cooperator_repay_order_id", cooperator_repay_order_id);
					maps.put("cooperatorId", cooperatorId);
					net.sf.json.JSONObject j = net.sf.json.JSONObject.fromObject(maps);
					logger.info("海德绑卡签名json数据:" + j.toString());
					xdt.quickpay.hddh.util.MD5 md = new xdt.quickpay.hddh.util.MD5();
					byte[] raw;
					raw = j.toString().getBytes("utf-8");
					String data = Base64.encode(raw, 0, raw.length);
					String sign = "data=" + data + "&key=bb946c036823d4372617c366e7939efd";
					logger.info("海德绑卡生成的签名前的数据:" + sign);
					String signMsg = md.md5(sign);
					logger.info("海德绑卡生成的签名:" + signMsg);

					String url = "http://api.kuaikuaifu.net/ypapi/repay/queryRepayPlanStatus.do";

					String params = "sign=" + signMsg + "&cooperatorId=" + cooperatorId + "&data=" + data;

					String path = url + "?" + params;

					HttpURLConection http = new HttpURLConection();
					String results = http.httpURLConectionGET(path, "UTF-8");
					logger.info("海德绑卡响应结果" + results);
					result = ApiUtil.toMap(results);
					logger.info("海德解析map结果" + result);
					String datas = new String(xdt.quickpay.jbb.util.Base64.decode(result.get("data")),
							Charset.forName("UTF-8"));
					result = ApiUtil.toMap(datas);
					logger.info("海德解析data结果" + result);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
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
		logger.info("返回结果:{}"+result);
		try {
			outString(response, result);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	/**
	 * 上海漪雷绑卡异步响应信息
	 * 
	 * @param request
	 * @param response
	 * @throws IOException
	 * @throws UnsupportedEncodingException
	 * @throws Exception
	 */
	@ResponseBody
	@RequestMapping(value = "hddhNotifyUrl")
	public void hddhNotifyUrl(HttpServletRequest request, HttpServletResponse response)
			throws UnsupportedEncodingException, IOException {
		// try {
		logger.info("上海漪雷绑卡异步通知来了");
		String data = request.getParameter("data");
		String isoString = new String(Base64.decode(data), "UTF-8");
		Map mapTypes = JSON.parseObject(isoString);
		logger.info("上海漪雷绑卡异步响应data数据：" + isoString);
		;
		String card_id = (String) mapTypes.get("card_id");
		String cardNo = (String) mapTypes.get("cardNo");
		String mobile = (String) mapTypes.get("card_id");
		String bankName = (String) mapTypes.get("bankName");
		String bankCode = (String) mapTypes.get("bankCode");
		String idcard_no = (String) mapTypes.get("idcard_no");
		String user_name = (String) mapTypes.get("user_name");
		String cooperator_user_id = (String) mapTypes.get("cooperator_user_id");
		String cooperator_order_id = (String) mapTypes.get("cooperator_order_id");
		logger.info("上海漪雷绑卡异步响应data：" + data);
		logger.info("上海漪雷绑卡异步响应卡号：" + card_id);
		logger.info("上海漪雷绑卡异步响应手机号：" + mobile);
		logger.info("上海漪雷绑卡异步响应合作商用户ID：" + cooperator_user_id);
		logger.info("上海漪雷绑卡异步响应合作商绑卡订单号：" + cooperator_order_id);
		OriginalOrderInfo originalInfo = null;
		Map<String, String> result = new HashMap<String, String>();

		result.put("card_id", card_id);
		result.put("cardNo", cardNo);
		result.put("mobile", mobile);
		result.put("bankName", bankName);
		result.put("bankCode", bankCode);
		result.put("idcard_no", idcard_no);
		result.put("user_name", user_name);
		result.put("cooperator_user_id", cooperator_user_id);
		result.put("cooperator_order_id", cooperator_order_id);
		if (!StringUtils.isEmpty(card_id)) {

			// 查询原始订单信息
			if (cooperator_user_id != null && cooperator_user_id != "") {
				try {
					originalInfo = Origi.get(cooperator_user_id);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			logger.info("上海漪雷绑卡异步订单数据:" + JSON.toJSON(originalInfo));
			ChannleMerchantConfigKey keyinfo = new ChannleMerchantConfigKey();
			try {
				keyinfo = hddhService.getChannelConfigKey(originalInfo.getPid());
			} catch (Exception e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			// 获取商户秘钥
			String key = keyinfo.getMerchantkey();
			result.put("merid", originalInfo.getPid());
			RegisterCallbackEntity consume = (RegisterCallbackEntity) BeanToMapUtil
					.convertMap(RegisterCallbackEntity.class, result);
			String sign = SignatureUtil.getSign(beanToMap(consume), key, log);
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

		} else {
			logger.error("回调的参数为空!");
			result.put("v_code", "15");
			result.put("v_msg", "请求失败");
		}

	}

	/**
	 * 上海漪雷代还绑卡异步响应信息
	 * 
	 * @param request
	 * @param response
	 * @throws IOException
	 * @throws UnsupportedEncodingException
	 * @throws Exception
	 */
	@ResponseBody
	@RequestMapping(value = "hddhReturnUrl")
	public void hddhReturnUrl(HttpServletRequest request, HttpServletResponse response)
			throws UnsupportedEncodingException, IOException {
		// try {
		logger.info("上海漪雷代还绑卡异步通知来了");
		String cooperator_user_id = request.getParameter("cooperator_user_id");
		OriginalOrderInfo originalInfo = null;
		// 查询原始订单信息
		if (cooperator_user_id != null && cooperator_user_id != "") {
			try {
				originalInfo = Origi.get(cooperator_user_id);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		logger.info("上海漪雷绑卡异步订单数据:" + JSON.toJSON(originalInfo));
		request.getSession();
		response.sendRedirect(originalInfo.getPageUrl().replace(" ", ""));

	}

	/**
	 * 上海漪雷代还异步响应信息
	 * 
	 * @param request
	 * @param response
	 * @throws IOException
	 * @throws UnsupportedEncodingException
	 * @throws Exception
	 */
	@RequestMapping(value = "hddh_pay_NotifyUrl")
	public void notifyUrl(HttpServletRequest request, HttpServletResponse response) {
		try {
			logger.info("上海漪雷代还异步通知来了");
			String data = request.getParameter("data");
			String isoString = new String(Base64.decode(URLDecoder.decode(data,"UTF-8")), "UTF-8");
			Map mapTypes = JSON.parseObject(isoString);
			logger.info("上海漪雷绑卡异步响应data数据：" + isoString);
			String cooperator_user_id = mapTypes.get("cooperator_user_id").toString();
			String repayPlanId = mapTypes.get("repayPlanId").toString();
			String cooperator_item_id = mapTypes.get("cooperator_item_id").toString();
			String status = mapTypes.get("status").toString();
			String channelStatus = mapTypes.get("channelStatus").toString();// B2支付状态
			logger.info("上海漪雷代还异步响应码：" + channelStatus);
			OriginalOrderInfo originalInfo = null;
			// 查询原始订单信息
			if (cooperator_user_id != null && cooperator_user_id != "") {
				try {
					originalInfo = Origi.get(cooperator_user_id);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			logger.info("上海漪雷代还异步订单号：" + JSON.toJSONString(cooperator_user_id));
			TreeMap<String, String> result = new TreeMap<String, String>();
			Map<String, String> map = new HashMap<>();
			CallbackEntity consume = new CallbackEntity();
			consume.setMerid(originalInfo.getPid());
			consume.setCooperator_user_id(cooperator_user_id);
			consume.setCooperator_item_id(cooperator_item_id);
			consume.setRepayPlanId(repayPlanId);
			consume.setStatus(status);
			consume.setChannelStatus(channelStatus);
			if ("3".equals(channelStatus) || "9".equals(channelStatus)) {
				hddhService.otherInvoke(cooperator_item_id, "SUCCESS");

			} else if ("4".equals(channelStatus) || "8".equals(channelStatus)) {
				hddhService.otherInvoke(cooperator_item_id, "FAIL");

			} else if ("6".equals(channelStatus) || "14".equals(channelStatus)) {
				hddhService.UpdateDaifu(cooperator_item_id, "00");
			} else if ("7".equals(channelStatus) || "15".equals(channelStatus)) {
				hddhService.UpdateDaifu(cooperator_item_id, "01");
			}
			ChannleMerchantConfigKey keyinfo = this.hddhService.getChannelConfigKey(originalInfo.getPid());

			String key = keyinfo.getMerchantkey();

			String sign = SignatureUtil.getSign(beanToMap(consume), key, log);
			this.logger.info("上海漪雷代还生成的签名:" + sign);
			consume.setV_sign(sign);
			Bean2QueryStrUtil bean2Util = new Bean2QueryStrUtil();
			this.logger.info("上海漪雷代还向下游返回的数据:" + bean2Util.bean2QueryStr(consume));
			this.logger.info("上海漪雷代还向下游商户号:" + originalInfo.getPid());
			
			String url="";
			if("10036046733".equals(originalInfo.getPid()))
			{
				url="http://service.blueseapay.com/gateway/notify/async/upin/UPIN20180511100103";
			}
			this.logger.info("上海漪雷代还向下游传递的异步地址:" + url);
			String html = HttpClientUtil.post(url, bean2Util.bean2QueryStr(consume));
			this.logger.info("上海漪雷代还下游返回状态" + html);
			JSONObject ob = JSONObject.fromObject(html);
			Iterator it = ob.keys();
			Map<String, String> maps = new HashMap();
			while (it.hasNext()) {
				String keys = (String) it.next();
				if (keys.equals("success")) {
					String value = ob.getString(keys);
					this.logger.info("上海漪雷代还异步回馈的结果:\t" + value);
					maps.put("success", value);
				}
			}
			if (((String) maps.get("success")).equals("false")) {
				this.logger.info("上海漪雷代还启动线程进行异步通知");

				ThreadPool.executor(new MbUtilThread(originalInfo.getBgUrl(), bean2Util.bean2QueryStr(consume)));
			}
		} catch (Exception e) {
			logger.info("上海漪雷代还异步回调异常:" + e);
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
