package xdt.controller;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URLDecoder;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.yeepay.g3.facade.yop.ca.dto.DigitalEnvelopeDTO;
import com.yeepay.g3.facade.yop.ca.enums.CertTypeEnum;
import com.yeepay.g3.frame.yop.ca.DigitalEnvelopeUtils;
import com.yeepay.g3.sdk.yop.utils.InternalConfig;

import net.sf.json.JSONObject;
import xdt.dao.IPmsMerchantInfoDao;
import xdt.dto.quickPay.entity.ConsumeResponseEntity;
import xdt.dto.quickPay.util.MbUtilThread;
import xdt.model.ChannleMerchantConfigKey;
import xdt.model.OriginalOrderInfo;
import xdt.quickpay.hengfeng.util.Bean2QueryStrUtil;
import xdt.quickpay.hengfeng.util.HttpClientUtil;
import xdt.quickpay.nbs.common.util.SignatureUtil;
import xdt.quickpay.yb.entity.PayRequestEntity;
import xdt.quickpay.yb.entity.PayResponseEntity;
import xdt.schedule.ThreadPool;
import xdt.service.HfQuickPayService;
import xdt.service.IQuickPayService;
import xdt.service.ITotalPayService;
import xdt.service.IYbQuickPayService;
import xdt.util.BeanToMapUtil;
import xdt.util.HttpURLConection;
import xdt.util.UtilDate;

@Controller
@RequestMapping("YBController")
public class YBController extends BaseAction {

	private Logger log = LoggerFactory.getLogger(this.getClass());
	@Resource
	private ITotalPayService TotalPayServiceImpl;
	@Resource
	private HfQuickPayService payService;
	@Resource
	private IPmsMerchantInfoDao pmsMerchantInfoDao;

	@Resource
	private IQuickPayService quickPayService;

	@Resource
	private IYbQuickPayService ybPayService;

	@RequestMapping(value = "notifyUrl")
	public void notifyUrl(HttpServletResponse response, HttpServletRequest request) {
		// String resp= (String) request.getAttribute("response");
		String resp = request.getParameter("response");
		String test = request.getParameter("test");
		String test2 = request.getParameter("test2");
		logger.info("测试参数：test:" + test + "test2:" + test2);
		logger.info("易宝异步进来了参数：" + resp);
		String str;
		try {
			if (resp != null && resp != "") {
				str = "SUCCESS";
				outString(response, str);
				Map<String, String> jsonMap = new HashMap<>();
				DigitalEnvelopeDTO dto = new DigitalEnvelopeDTO();
				dto.setCipherText(resp);
				PrivateKey privateKey = InternalConfig.getISVPrivateKey(CertTypeEnum.RSA2048);
				logger.info("privateKey: " + privateKey);
				PublicKey publicKey = InternalConfig.getYopPublicKey(CertTypeEnum.RSA2048);
				logger.info("publicKey: " + publicKey);

				dto = DigitalEnvelopeUtils.decrypt(dto, privateKey, publicKey);
				logger.info("-------:" + dto.getPlainText());
				jsonMap = JSON.parseObject(dto.getPlainText(), new TypeReference<TreeMap<String, String>>() {
				});
				logger.info("解析之后的数据:" + jsonMap);

				String customerNumber = jsonMap.get("customerNumber");// 商编
				String batchNo = jsonMap.get("batchNo");// 批次号
				String orderId = jsonMap.get("orderId");// 订单号
				String transferStatusCode = jsonMap.get("transferStatusCode");// 出款状态码
				String bankTrxStatusCode = jsonMap.get("bankTrxStatusCode");// 银行状态码
				Map<String, String> m = new HashMap<>();
				// OriginalOrderInfo originalInfo = this.payService.getOriginOrderInfo(batchNo);
				Map<String, String> result = new HashMap<>();
				/*
				 * PmsMerchantInfo merchantinfo =new PmsMerchantInfo();
				 * merchantinfo.setMercId(originalInfo.getPid()); DaifuRequestEntity payRequest
				 * =new DaifuRequestEntity(); payRequest.setV_type("0");
				 * payRequest.setV_amount(originalInfo.getOrderAmount());
				 * payRequest.setV_mid(originalInfo.getPid());
				 * payRequest.setV_batch_no(batchNo); payRequest.setV_identity(orderId);
				 */
				if ("0026".equals(transferStatusCode) || "0027".equals(transferStatusCode)) {
					if ("S".equals(bankTrxStatusCode)) {
						TotalPayServiceImpl.UpdateDaifu(batchNo, "00");
					} else if ("I".equals(bankTrxStatusCode) || "U".equals(bankTrxStatusCode)) {

					} else if ("F".equals(bankTrxStatusCode) || "W".equals(bankTrxStatusCode)) {
						TotalPayServiceImpl.UpdateDaifu(batchNo, "01");
						/*
						 * m.put("payMoney", originalInfo.getOrderAmount()); m.put("machId",
						 * originalInfo.getPid()); int nus = pmsMerchantInfoDao.updataPay(m); if (nus ==
						 * 1) { log.info("易宝***补款成功"); // surplus =
						 * surplus+Double.parseDouble(payRequest.getAmount()); //
						 * merchantinfo.setPosition(surplus.toString());
						 * payRequest.setV_batch_no(payRequest.getV_batch_no() + "/A"); int id =
						 * TotalPayServiceImpl.add(payRequest, merchantinfo, result, "00"); if (id == 1)
						 * { log.info("易宝代付补单成功"); } }
						 */
					}
				}
			} else {
				str = "FALL";
				logger.info("没有收到易宝的异步数据");
				outString(response, str);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	@RequestMapping(value = "ybQuert")
	public void ybQuert(String merid, String ordid, String batchNo, HttpServletResponse response) {
		logger.info("查询参数进来了：merid：" + merid + ",ordid:" + ordid);
		Map<String, String> result = new HashMap<>();
		result = TotalPayServiceImpl.ybQuick(batchNo, result, ordid);
		logger.info("查询返回参数：" + JSON.toJSONString(result));
		try {
			outString(response, JSON.toJSON(result));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	/**
	 * 快捷支付生成签名
	 * 
	 * @param request
	 * @param response
	 * @throws Exception
	 */
	@ResponseBody
	@RequestMapping(value = "payScan")
	public void payScan(PayRequestEntity entity, HttpServletRequest request, HttpServletResponse response)
			throws Exception {

		response.setHeader("Access-Control-Allow-Origin", "*");
		response.setContentType("text/html;charset=utf-8");
		log.info("下游上送的参数:{}" , entity.getMerchantNo());
		Map<String, Object> map = new HashMap<String, Object>();
		// 查询商户密钥
		ChannleMerchantConfigKey keyinfo = quickPayService.getChannelConfigKey(entity.getMerchantNo());
		String merchantKey = keyinfo.getMerchantkey();
		log.info("下游商户密钥:" , merchantKey);
		String sign = SignatureUtil.getSign(beanToMap(entity), merchantKey, log);
		entity.setV_sign(sign);
		// 返回页面参数
		request.setCharacterEncoding("UTF-8");
		request.setAttribute("temp", entity);
		request.getRequestDispatcher("/pay/yb/quick_pay_submit.jsp").forward(request, response);
	}

	@ResponseBody
	@RequestMapping(value = "pays")
	public void pay(PayRequestEntity pay, HttpServletResponse response, HttpServletRequest request) throws Exception {
		logger.info("快捷获取token");
		response.setHeader("Access-Control-Allow-Origin", "*");
		response.setContentType("text/html;charset=UTF-8");
		Map<String, String> result = new HashMap<String, String>();
		// 查询商户密钥
		ChannleMerchantConfigKey keyinfo = quickPayService.getChannelConfigKey(pay.getMerchantNo());
		// ------------------------需要改签名
		String merchantKey = keyinfo.getMerchantkey();
		logger.info("下游上送参数:{}"+ pay);
		if (!StringUtils.isEmpty(pay.getMerchantNo())) {

			logger.info("下游上送签名串{}" + pay.getV_sign());
			SignatureUtil signUtil = new SignatureUtil();
			Map map = BeanToMapUtil.convertBean(pay);
			if (signUtil.checkSign(map, merchantKey, log)) {
				logger.info("对比签名成功");
				result = ybPayService.updateHandle(pay);
				String url = result.get("path");
				logger.info("URL 重定向：" + url);
				String path = url.replace("https://cash.yeepay.com/cashier/std",
						"http://www.lssc888.com/shop/control/yibao_request_vt3.php");
				logger.info("demo 重定向：" + path);
				request.getSession();
				response.setCharacterEncoding("UTF-8");
				response.sendRedirect(path.replace(" ", " "));
			} else {
				logger.info("签名错误!");
				result.put("v_code", "02");
				result.put("v_msg", "签名错误!");
			}

		} else {
			logger.error("上送交易参数空!");
			result.put("v_code", "01");
			result.put("v_msg", "上送交易参数空");
		}
		logger.info("返回结果:{}"+result);
		outString(response, gson.toJson(result));

	}

	@ResponseBody
	@RequestMapping(value = "yb_notifyUrl")
	public void ybnotifyUrl(HttpServletResponse response, HttpServletRequest request) {
		try {
			logger.info("易宝异步通知来了");
			String appMsg = request.getParameter("response");
			String customerIdentification = request.getParameter("customerIdentification");
			Map<String, String> result = new HashMap<String, String>();
			result.put("response", appMsg);
			result.put("customerIdentification", customerIdentification);
			logger.info("请求参数：" + appMsg);
			if (!StringUtils.isEmpty(appMsg)) {
				response.getWriter().write("SUCCESS");
				logger.info("易宝解密数据:" + appMsg);
				// 开始解密
				Map<String, String> jsonMap = new HashMap<>();
				DigitalEnvelopeDTO dto = new DigitalEnvelopeDTO();
				dto.setCipherText(appMsg);
				PrivateKey privateKey = InternalConfig.getISVPrivateKey(CertTypeEnum.RSA2048);
				logger.info("privateKey: " + privateKey);
				PublicKey publicKey = InternalConfig.getYopPublicKey(CertTypeEnum.RSA2048);
				logger.info("publicKey: " + publicKey);

				dto = DigitalEnvelopeUtils.decrypt(dto, privateKey, publicKey);
				logger.info("-------:" + dto.getPlainText());
				jsonMap = JSON.parseObject(dto.getPlainText(), new TypeReference<TreeMap<String, String>>() {
				});
				logger.info("解析之后的数据:" + jsonMap);

				String status = jsonMap.get("status");
				logger.info("易宝支付异步返回的订单状态:" + jsonMap);
				String orderId = jsonMap.get("orderId");
				logger.info("易宝支付异步返回的订单号:" + orderId);
				OriginalOrderInfo originalInfo = null;
				if (orderId != null && orderId != "") {
					originalInfo = this.quickPayService.getOriginOrderInfo(orderId);
				}
				result.put("v_oid", originalInfo.getOrderId());
				result.put("v_txnAmt", originalInfo.getOrderAmount());
				result.put("v_code", "00");
				result.put("v_attach", originalInfo.getAttach());
				result.put("v_mid", originalInfo.getPid());
				result.put("v_time", UtilDate.getTXDateTime());
				if ("SUCCESS".equals(status)) {

					result.put("v_status", "0000");
					result.put("v_msg", "支付成功");

				} else {
					result.put("v_status", "1001");
					result.put("v_msg", "支付失败");
					logger.info("交易错误码:" + request.getParameter("payStatus") + ",错误信息:"
							+ URLDecoder.decode(request.getParameter("payMsg"), "UTF-8"));
				}
				ChannleMerchantConfigKey keyinfo = quickPayService.getChannelConfigKey(originalInfo.getPid());
				// 获取商户秘钥
				String key = keyinfo.getMerchantkey();
				ConsumeResponseEntity consume = (ConsumeResponseEntity) BeanToMapUtil
						.convertMap(ConsumeResponseEntity.class, result);
				// 修改订单状态
				quickPayService.otherInvoke(consume);

				// 生成签名
				String sign = SignatureUtil.getSign(beanToMap(consume), key, log);
				result.put("v_sign", sign);
				logger.info("异步之前的参数：" + result);
				// 修改订单状态
				ybPayService.otherInvoke(orderId, status);
				ConsumeResponseEntity consumeResponseEntity = (ConsumeResponseEntity) BeanToMapUtil
						.convertMap(ConsumeResponseEntity.class, result);
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
		} catch (Exception e) {
			e.printStackTrace();
		}

	}
	/**
	 * 易宝同步响应信息
	 *
	 * @param request
	 * @param response
	 * @throws Exception
	 */
	@ResponseBody
	@RequestMapping(value = "yb_ReturnUrl")
	public void ybReturnUrl(HttpServletRequest request, HttpServletResponse response) {
		try {
			logger.info("易宝同步数据返回参数:");
			String orderId = request.getParameter("orderId");

			OriginalOrderInfo originalInfo = null;
			if (orderId != null && orderId != "") {
				originalInfo = this.quickPayService.getOriginOrderInfo(orderId);
			}
			logger.info("订单数据:" + JSON.toJSON(originalInfo));
			Bean2QueryStrUtil queryUtil = new Bean2QueryStrUtil();
			logger.info("下游的同步地址" + originalInfo.getPageUrl());
			TreeMap<String, String> result = new TreeMap<String, String>();
			String params = "";
			if (!StringUtils.isEmpty(orderId)) {
				ChannleMerchantConfigKey keyinfo = quickPayService.getChannelConfigKey(originalInfo.getPid());
				// 获取商户秘钥
				String key = keyinfo.getMerchantkey();
				result.put("v_oid", originalInfo.getOrderId());
				result.put("v_txnAmt", originalInfo.getOrderAmount());
				result.put("v_code", "00");
				result.put("v_msg", "请求成功");
				result.put("v_time", originalInfo.getOrderTime());
				result.put("v_mid", originalInfo.getPid());
				ConsumeResponseEntity consume = (ConsumeResponseEntity) BeanToMapUtil
						.convertMap(ConsumeResponseEntity.class, result);
				String sign = SignatureUtil.getSign(beanToMap(consume), key, log);
				result.put("v_sign", sign);
				params = HttpURLConection.parseParams(result);
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
				logger.info("没有收到易宝的同步数据");
				// outString(response, str);
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
