package xdt.controller.jsds;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.omg.PortableInterceptor.RequestInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import com.alibaba.fastjson.JSON;

import net.sf.json.JSONObject;
import xdt.controller.BaseAction;
import xdt.dao.ChannleMerchantConfigKeyDao;
import xdt.dto.BaseUtil;
import xdt.dto.jsds.CustomerRegister;
import xdt.dto.jsds.JsdsRequestDto;
import xdt.dto.jsds.JsdsResponseDto;
import xdt.dto.payeasy.PayEasyResponseEntitys;
import xdt.dto.pufa.PayRequestEntity;
import xdt.model.ChannleMerchantConfigKey;
import xdt.model.OriginalOrderInfo;
import xdt.pufa.security.PuFaSignUtil;
import xdt.quickpay.hengfeng.util.Bean2QueryStrUtil;
import xdt.quickpay.hengfeng.util.HttpClientUtil;
import xdt.schedule.ThreadPool;
import xdt.service.BeenQuickPayService;
import xdt.service.JsdsQrCodeService;
import xdt.util.BeanToMapUtil;
import xdt.util.HttpURLConection;
import xdt.util.HttpUtil;
import xdt.util.JsPostThread;
import xdt.util.JsdsPostThread;
import xdt.util.JsdsUtil;
import xdt.util.UtilMethod;

/**
 * 
 * @Description 江苏电商
 * @author Shiwen .Li
 * @date 2017年3月5日 下午1:36:44
 * @version V1.3.1
 */
@Controller
@Scope("prototype")
@RequestMapping(value = { "test/qrcode/", "live/qrcode/" })
public class JsdsQrCodeAction extends BaseAction {

	private static final Logger logger = LoggerFactory.getLogger(JsdsQrCodeAction.class);

	@Resource
	private JsdsQrCodeService jddsQrCodeService;

	/**
	 * 渠道商户密钥信息
	 */
	@Resource
	private ChannleMerchantConfigKeyDao cmckeyDao;

	/**
	 * 
	 * @Description 生成支付二维码
	 * @author Administrator
	 * @param request
	 * @param response
	 * @throws Exception
	 */
	@RequestMapping("interface")
	public void qrInterface(JsdsRequestDto reqData, HttpServletResponse response) throws Exception {
		Map<String, String> result = new HashMap<String, String>();
		logger.info("##############################生成支付二维码");
		logger.info("下游上送参数：{}", reqData);
		try {
			result = jddsQrCodeService.updateHandle(reqData);
			logger.info("*************************结果:{}", result);
			if ("0000".equals(result.get("respCode"))) {
				setCmmon(result, reqData);
			}
			outString(response, gson.toJson(result));
		} catch (Exception e) {
			result.put("respCode", "0009");
			result.put("respMsg", "系统错误");
			logger.info("**********************生成支付二维码失败:{}", e);
			outString(response, gson.toJson(result));
		}
	}

	/**
	 * 
	 * @Description 生成支付二维码
	 * @author Administrator
	 * @param request
	 * @param response
	 * @throws Exception
	 */
	@RequestMapping("gateway")
	public void gateway(JsdsRequestDto reqData, HttpServletRequest request,HttpServletResponse response) throws Exception {
		Map<String, String> result = new HashMap<String, String>();
		logger.info("##############################生成支付二维码");
		logger.info("下游上送参数：{}", reqData);
		try {
			result = jddsQrCodeService.updateHandle(reqData);
			logger.info("*************************结果:{}", result);
			if (result.get("respCode").equals("0000")) {

			        request.setAttribute("url", result.get("url").toString());
			        request.setAttribute("sign", result.get("sign").toString());
			        request.setAttribute("valid_order", result.get("valid_order").toString());
			        request.setAttribute("no_order", result.get("no_order").toString());
			        request.setAttribute("oid_partner", result.get("oid_partner").toString());
			        request.setAttribute("pay_type", result.get("pay_type").toString());
			        request.setAttribute("url_return", result.get("url_return").toString());
			        request.setAttribute("notify_url", result.get("notify_url").toString());
			        request.setAttribute("name_goods", result.get("name_goods").toString());
			        request.setAttribute("dt_order", result.get("dt_order").toString());
			        request.setAttribute("user_id", result.get("user_id").toString());
			        request.setAttribute("money_order", result.get("money_order").toString());
			        request.setAttribute("bank_code", result.get("bank_code").toString());
				    request.getRequestDispatcher("/pay/success.jsp").forward(request, response);
			}
		} catch (Exception e) {
			result.put("respCode", "0009");
			result.put("respMsg", "系统错误");
			logger.info("**********************生成支付二维码失败:{}", e);
			outString(response, gson.toJson(result));
		}
	}

	private void setCmmon(Map<String, String> result, JsdsRequestDto reqData) {
		// result.putAll(JsdsUtil.beanToMap(reqData));
		Set<String> keys = new HashSet<String>();
		// 剔除值为空的
		for (String key : result.keySet()) {
			if ("".equals(result.get(key)) || result.get(key) == null) {
				keys.add(key);
			}
		}
		for (String key : keys) {
			result.remove(key);
		}
		String key;
		try {
			// 设置签名
			String merchNo = reqData.getMerchantCode();
			logger.info("********************江苏电商-----------------当前商户号:" + merchNo);
			ChannleMerchantConfigKey channerKey = cmckeyDao.get(merchNo);
			logger.info("********************江苏电商-----------------商户密钥:" + channerKey);
			key = channerKey.getMerchantkey();
			result.remove("sign");
			result.put("sign", JsdsUtil.sign(result, key));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	@RequestMapping("paySign")
	public void paySign(HttpServletResponse response, JsdsRequestDto reqData) throws IOException {
		Map<String, String> result = new HashMap<String, String>();
		try {
			result.putAll(JsdsUtil.beanToMap(reqData));
			Set<String> keys = new HashSet<String>();
			// 剔除值为空的
			for (String key : result.keySet()) {
				if ("".equals(result.get(key)) || result.get(key) == null) {
					keys.add(key);
				}
			}
			for (String key : keys) {
				result.remove(key);
			}
			String key;

			// 设置签名
			String merchNo = reqData.getMerchantCode();
			logger.info("********************江苏电商-----------------当前商户号:" + merchNo);
			ChannleMerchantConfigKey channerKey = cmckeyDao.get(merchNo);
			logger.info("********************江苏电商-----------------商户密钥:" + channerKey);
			key = channerKey.getMerchantkey();
			System.out.println("生成签名的数据:" + result);
			System.out.println("秘钥:" + key);
			result.remove("sign");
			this.outString(response, JsdsUtil.sign(result, key));
		} catch (Exception e) {
			logger.error("********************江苏电商-----", e);
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
	 *            银联返回的数据
	 * @throws Exception
	 */
	 
	@RequestMapping(value = "gatewayResult")
	public void gatewayResult(JsdsResponseDto temp, HttpServletRequest request, HttpServletResponse response)
			throws Exception {

		logger.info("支付结果信息：" + temp);
		if (temp != null) {
			response.getWriter().write("SUCCESS");
			// 处理这笔交易 修改订单表中的交易表
			Map<String, String> param = jddsQrCodeService.gatewayNofity(temp);
			logger.info("响应信息：" + param);
			// 交易id
			String tranId = param.get("orderNum");
			// 查询商户上送原始信息
			OriginalOrderInfo originalInfo = jddsQrCodeService.getOriginOrderInfo(tranId);
			Map<String, String> result1 = new HashMap<String, String>();
			String amount=originalInfo.getOrderAmount().replaceAll(",", "");
			String orderNumber = param.get("pl_orderNum");
			logger.info("异步返回的金额:"+amount);
			logger.info("异步返回的平台订单号:"+orderNumber);
			result1.put("orderNum", tranId);
			result1.put("pl_orderNum", orderNumber);
			result1.put("pl_payState", param.get("pl_payState"));
			result1.put("pl_payMessage", "支付成功");
			result1.put("pl_amount", amount);
			// 设置签名
			String merchNo = originalInfo.getPid();
			logger.info("********************江苏电商-----------------当前商户号:" + merchNo);
			ChannleMerchantConfigKey channerKey = cmckeyDao.get(merchNo);
			logger.info("********************江苏电商-----------------商户密钥:" + channerKey);
			String key = channerKey.getMerchantkey();
			logger.info("生成签名的数据:" + result1);
			logger.info("秘钥:" + key);
			logger.info("签名:" + JsdsUtil.sign(result1, key));
			// 向下游发送的数据
			JsdsResponseDto jsds = new JsdsResponseDto();
			jsds.setOrderNum(tranId);
			jsds.setPl_orderNum(orderNumber);
			jsds.setPl_payState(param.get("pl_payState"));
			jsds.setPl_amount(amount);
			jsds.setPl_payMessage("支付成功");
			jsds.setSign(JsdsUtil.sign(result1, key));
			// 替换成下游商户的
			jsds.setPl_orderNum(orderNumber);

			logger.info("向下游发送的数据" + jsds);

			Bean2QueryStrUtil bean2Util = new Bean2QueryStrUtil();

			logger.info("拼接之后的数据:" + bean2Util.bean2QueryStr(jsds));
			logger.info("下游上送的url:" + originalInfo.getBgUrl());
			// 给下游主动返回支付结果
			String path = originalInfo.getBgUrl() + "?" + bean2Util.bean2QueryStr(jsds);
			logger.info("bgUrl 平台服务器重定向：" + path);
			logger.info("向下游发送的地址" + originalInfo.getBgUrl());
			logger.info("向下游发送的数据" + bean2Util.bean2QueryStr(jsds));
			String result = HttpClientUtil.post(originalInfo.getBgUrl(), bean2Util.bean2QueryStr(jsds));
			JSONObject ob1 = JSONObject.fromObject(result);
			Iterator it1 = ob1.keys();
			Map<String, String> map = new HashMap<>();
			while (it1.hasNext()) {
				String key1 = (String) it1.next();
				if (key1.equals("success")) {
					String value = ob1.getString(key1);
					logger.info("异步回馈的结果:" + "\t" + value);
					map.put("success", value);
				}
			}
			if (map.get("success").equals("false")) {

				logger.info("启动线程进行异步通知");
				// 启线程进行异步通知
				ThreadPool.executor(new JsPostThread(originalInfo.getBgUrl(), jsds));
			}
			logger.info("向下游 发送数据成功");
		} else {
			logger.info("接受数据失败!");
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
	 *            银联返回的数据
	 * @throws Exception
	 */
	 
	@RequestMapping(value = "gatewayResult1")
	public void gatewayResult1(JsdsResponseDto temp, HttpServletRequest request, HttpServletResponse response)
			throws Exception {

		logger.info("支付结果信息：" + temp);
		if (temp != null) {
			response.getWriter().write("SUCCESS");
			// 处理这笔交易 修改订单表中的交易表
			Map<String, String> param = jddsQrCodeService.gatewayNofity1(temp);
			logger.info("响应信息：" + param);
			// 交易id
			String tranId = param.get("orderNum");
			// 查询商户上送原始信息
			OriginalOrderInfo originalInfo = jddsQrCodeService.getOriginOrderInfo(tranId);
			Map<String, String> result1 = new HashMap<String, String>();
			String amount=originalInfo.getOrderAmount().replaceAll(",", "");
			String orderNumber = param.get("pl_orderNum");
			logger.info("异步返回的金额:"+amount);
			logger.info("异步返回的平台订单号:"+orderNumber);
			result1.put("orderNum", tranId);
			result1.put("pl_orderNum", orderNumber);
			result1.put("pl_payState", param.get("pl_payState"));
			result1.put("pl_payMessage", "支付成功");
			result1.put("pl_amount", amount);
			// 设置签名
			String merchNo = originalInfo.getPid();
			logger.info("********************江苏电商-----------------当前商户号:" + merchNo);
			ChannleMerchantConfigKey channerKey = cmckeyDao.get(merchNo);
			logger.info("********************江苏电商-----------------商户密钥:" + channerKey);
			String key = channerKey.getMerchantkey();
			logger.info("生成签名的数据:" + result1);
			logger.info("秘钥:" + key);
			logger.info("签名:" + JsdsUtil.sign(result1, key));
			// 向下游发送的数据
			JsdsResponseDto jsds = new JsdsResponseDto();
			jsds.setOrderNum(tranId);
			jsds.setPl_orderNum(orderNumber);
			jsds.setPl_payState(param.get("pl_payState"));
			jsds.setPl_amount(amount);
			jsds.setPl_payMessage("支付成功");
			jsds.setSign(JsdsUtil.sign(result1, key));
			// 替换成下游商户的
			jsds.setPl_orderNum(orderNumber);

			logger.info("向下游发送的数据" + jsds);

			Bean2QueryStrUtil bean2Util = new Bean2QueryStrUtil();

			logger.info("拼接之后的数据:" + bean2Util.bean2QueryStr(jsds));
			logger.info("下游上送的url:" + originalInfo.getBgUrl());
			// 给下游主动返回支付结果
			String path = originalInfo.getBgUrl() + "?" + bean2Util.bean2QueryStr(jsds);
			logger.info("bgUrl 平台服务器重定向：" + path);
			logger.info("向下游发送的地址" + originalInfo.getBgUrl());
			logger.info("向下游发送的数据" + bean2Util.bean2QueryStr(jsds));
			String result = HttpClientUtil.post(originalInfo.getBgUrl(), bean2Util.bean2QueryStr(jsds));
			JSONObject ob1 = JSONObject.fromObject(result);
			Iterator it1 = ob1.keys();
			Map<String, String> map = new HashMap<>();
			while (it1.hasNext()) {
				String key1 = (String) it1.next();
				if (key1.equals("success")) {
					String value = ob1.getString(key1);
					logger.info("异步回馈的结果:" + "\t" + value);
					map.put("success", value);
				}
			}
			if (map.get("success").equals("false")) {

				logger.info("启动线程进行异步通知");
				// 启线程进行异步通知
				ThreadPool.executor(new JsPostThread(originalInfo.getBgUrl(), jsds));
			}
			logger.info("向下游 发送数据成功");
		} else {
			logger.info("接受数据失败!");
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
	 *            银联返回的数据
	 * @throws Exception
	 */
	 
	@RequestMapping(value = "JsdsgPayResult")
	public void JsdsgPayResult(JsdsResponseDto temp, HttpServletRequest request, HttpServletResponse response)
			throws Exception {

		logger.info("支付结果信息：" + temp);
		if (temp.getPl_sign() != null ) {
			outString(response, "SUCCESS");
			// 处理这笔交易 修改订单表中的交易表
			Map<String, String> param = jddsQrCodeService.handleNofity(temp);
			logger.info("响应信息：" + param);
			// 交易id
			String tranId = param.get("orderNum");
			// 查询商户上送原始信息
			OriginalOrderInfo originalInfo = jddsQrCodeService.getOriginOrderInfo(tranId);
			logger.info("来了11111"+JSON.toJSONString(originalInfo));
			Map<String, String> result1 = new HashMap<String, String>();
			String amount=originalInfo.getOrderAmount().replaceAll(",", "");
			logger.info("来了2222");
			String orderNumber = UtilMethod.getOrderid("180");
			logger.info("异步返回的金额:"+amount);
			logger.info("异步返回的平台订单号:"+orderNumber);
			result1.put("orderNum", tranId);
			result1.put("pl_orderNum", orderNumber);
			result1.put("pl_payState", param.get("pl_payState"));
			result1.put("pl_payMessage", "支付成功");
			result1.put("pl_amount", amount);
			// 设置签名
			String merchNo = originalInfo.getPid();
			logger.info("********************江苏电商-----------------当前商户号:" + merchNo);
			ChannleMerchantConfigKey channerKey = cmckeyDao.get(merchNo);
			logger.info("********************江苏电商-----------------商户密钥:" + channerKey);
			String key = channerKey.getMerchantkey();
			logger.info("生成签名的数据:" + result1);
			logger.info("秘钥:" + key);
			logger.info("签名:" + JsdsUtil.sign(result1, key));
			// 向下游发送的数据
			JsdsResponseDto jsds = new JsdsResponseDto();
			jsds.setOrderNum(tranId);
			jsds.setPl_orderNum(orderNumber);
			jsds.setPl_payState(param.get("pl_payState"));
			jsds.setPl_amount(amount);
			jsds.setPl_payMessage("支付成功");
			jsds.setSign(JsdsUtil.sign(result1, key));
			// 替换成下游商户的
			jsds.setPl_orderNum(orderNumber);


			logger.info("向下游发送的数据" + jsds);

			Bean2QueryStrUtil bean2Util = new Bean2QueryStrUtil();

			logger.info("拼接之后的数据:" + bean2Util.bean2QueryStr(jsds));
			logger.info("下游上送的url:" + originalInfo.getBgUrl());
			// 给下游主动返回支付结果
			String path = originalInfo.getBgUrl() + "?" + bean2Util.bean2QueryStr(jsds);
			logger.info("bgUrl 平台服务器重定向：" + path);
			logger.info("向下游发送的地址" + originalInfo.getBgUrl());
			logger.info("向下游发送的数据" + bean2Util.bean2QueryStr(jsds));
			String result = HttpClientUtil.post(originalInfo.getBgUrl(), bean2Util.bean2QueryStr(jsds));
			JSONObject ob1 = JSONObject.fromObject(result);
			Iterator it1 = ob1.keys();
			Map<String, String> map = new HashMap<>();
			while (it1.hasNext()) {
				String key1 = (String) it1.next();
				if (key1.equals("success")) {
					String value = ob1.getString(key1);
					logger.info("异步回馈的结果:" + "\t" + value);
					map.put("success", value);
				}
			}
			if (map.get("success").equals("false")) {

				logger.info("启动线程进行异步通知");
				// 启线程进行异步通知
				ThreadPool.executor(new JsPostThread(originalInfo.getBgUrl(), jsds));
			}
			logger.info("向下游 发送数据成功");
		} else {
			outString(response, "FALE");
			logger.info("接受数据失败!");
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
	 *            银联返回的数据
	 * @throws Exception
	 */
	@RequestMapping(value = "JsbgPayResult")
	public void payResult(JsdsResponseDto temp, HttpServletRequest request, HttpServletResponse response)
			throws Exception {

		logger.info("支付结果信息：" + temp);
		if (temp != null) {
			response.getWriter().write("SUCCESS");
			// 处理这笔交易 修改订单表中的交易表
			Map<String, String> param = jddsQrCodeService.otherInvoke(temp);
			logger.info("响应信息：" + param);
			// 交易id
			String tranId = param.get("orderNum");
			// 查询商户上送原始信息
			OriginalOrderInfo originalInfo = jddsQrCodeService.getOriginOrderInfo(tranId);
			Map<String, String> result1 = new HashMap<String, String>();
			String amount=originalInfo.getOrderAmount().replaceAll(",", "");
			String orderNumber = param.get("pl_orderNum");
			logger.info("异步返回的金额:"+amount);
			logger.info("异步返回的平台订单号:"+orderNumber);
			result1.put("orderNum", tranId);
			result1.put("pl_orderNum", orderNumber);
			result1.put("pl_payState", param.get("pl_payState"));
			result1.put("pl_payMessage", "支付成功");
			result1.put("pl_amount", amount);
			// 设置签名
			String merchNo = originalInfo.getPid();
			logger.info("********************江苏电商-----------------当前商户号:" + merchNo);
			ChannleMerchantConfigKey channerKey = cmckeyDao.get(merchNo);
			logger.info("********************江苏电商-----------------商户密钥:" + channerKey);
			String key = channerKey.getMerchantkey();
			logger.info("生成签名的数据:" + result1);
			logger.info("秘钥:" + key);
			logger.info("签名:" + JsdsUtil.sign(result1, key));
			// 向下游发送的数据
			JsdsResponseDto jsds = new JsdsResponseDto();
			jsds.setOrderNum(tranId);
			jsds.setPl_orderNum(orderNumber);
			jsds.setPl_payState(param.get("pl_payState"));
			jsds.setPl_amount(amount);
			jsds.setPl_payMessage("支付成功");
			jsds.setSign(JsdsUtil.sign(result1, key));
			// 替换成下游商户的
			jsds.setPl_orderNum(orderNumber);


			logger.info("向下游发送的数据" + jsds);

			Bean2QueryStrUtil bean2Util = new Bean2QueryStrUtil();

			logger.info("拼接之后的数据:" + bean2Util.bean2QueryStr(jsds));
			logger.info("下游上送的url:" + originalInfo.getBgUrl());
			// 给下游主动返回支付结果
			String path = originalInfo.getBgUrl() + "?" + bean2Util.bean2QueryStr(jsds);
			logger.info("bgUrl 平台服务器重定向：" + path);
			// String result =
			// HttpURLConection.httpURLConnectionPOST(originalInfo.getBgUrl(),bean2Util.bean2QueryStr(temp));
			logger.info("向下游发送的地址" + originalInfo.getBgUrl());
			logger.info("向下游发送的数据" + bean2Util.bean2QueryStr(jsds));
			String result = HttpClientUtil.post(originalInfo.getBgUrl(), bean2Util.bean2QueryStr(jsds));
			JSONObject ob1 = JSONObject.fromObject(result);
			Iterator it1 = ob1.keys();
			Map<String, String> map = new HashMap<>();
			while (it1.hasNext()) {
				String key1 = (String) it1.next();
				if (key1.equals("success")) {
					String value = ob1.getString(key1);
					logger.info("异步回馈的结果:" + "\t" + value);
					map.put("success", value);
				}
			}
			if (map.get("success").equals("false")) {

				logger.info("启动线程进行异步通知");
				// 启线程进行异步通知
				ThreadPool.executor(new JsPostThread(originalInfo.getBgUrl(), jsds));
			}
			logger.info("向下游 发送数据成功");
		} else {
			logger.info("接受数据失败!");
		}

	}

	@RequestMapping("paySign1")
	public void paySign1(HttpServletRequest request, HttpServletResponse response) throws Exception {
		response.setHeader("Access-Control-Allow-Origin", "*");
		response.setContentType("text/html;charset=utf-8");
		String param = requestClient(request);
		CustomerRegister entity = gson.fromJson(param, CustomerRegister.class);
		Map map = BeanToMapUtil.convertBean(entity);
		logger.info("支付签名");
		String sign = PuFaSignUtil.sign(map);
		outString(response, sign);
	}

	@RequestMapping("querySign")
	public void querySign(HttpServletRequest request, HttpServletResponse response) throws Exception {
		response.setHeader("Access-Control-Allow-Origin", "*");
		response.setContentType("text/html;charset=utf-8");
		String param = requestClient(request);
		CustomerRegister entity = gson.fromJson(param, CustomerRegister.class);
		Map map = BeanToMapUtil.convertBean(entity);
		logger.info("支付签名");
		String sign = PuFaSignUtil.sign(map);
		outString(response, sign);
	}

	/**
	 * 和上游交互 支付完成后同步返回支付结果
	 * 
	 * @param request
	 *            requet对象
	 * @param response
	 *            response对象
	 * @param temp
	 *            银联返回的数据
	 * @throws Exception
	 */
	@RequestMapping(value = "customerRegister")
	public void customerRegister(HttpServletRequest request, HttpServletResponse response) throws Exception {

		// 返回结果
		Map<String, String> result = new HashMap<String, String>();
		String param = requestClient(request);
		logger.info("下游上送参数:{}", param);

		if (!StringUtils.isEmpty(param)) {

			CustomerRegister reqeustInfo = gson.fromJson(param, CustomerRegister.class);

			logger.info("json转换扫码反扫对象{}", reqeustInfo);
			logger.info("下游上送签名串{}", reqeustInfo.getSign());
			if (signVerify(reqeustInfo, reqeustInfo.getSign())) {
				logger.info("开始处理生成二维码");
				Map<String, String> params = new HashMap<String, String>();

				params.put("merchantCode", reqeustInfo.getMerchantCode());
				params.put("payType", reqeustInfo.getPayType());
				params.put("product", reqeustInfo.getProduct());
				params.put("tranTp", reqeustInfo.getTranTp());
				params.put("fee", reqeustInfo.getFee());
				params.put("paymentfee", reqeustInfo.getPaymentfee());
				if ("1".equals(reqeustInfo.getPayType())) {
					params.put("realName", reqeustInfo.getRealName());
					params.put("cardNo", reqeustInfo.getCardNo());
				}
				if (reqeustInfo.getPayType().equals("0")) {
					 if(reqeustInfo.getTranTp().equals("0")&&reqeustInfo.getProduct().equals("1"))
					 {
							String url = BaseUtil.url+"/pay/hengfeng/customer_token.jsp";
							String str = HttpUtil.parseParams(params);
							String path = url + "?" + str;
							reqeustInfo.setUrl(path);
					 }else if(reqeustInfo.getTranTp().equals("1")&&reqeustInfo.getProduct().equals("0"))
					 {
							String url = BaseUtil.url+"/pay/hengfeng/customer_token.jsp";
							String str = HttpUtil.parseParams(params);
							String path = url + "?" + str;
							reqeustInfo.setUrl(path); 
					 }
						 
				} else if (reqeustInfo.getPayType().equals("1")) {
					if(reqeustInfo.getTranTp().equals("0"))
					{
						String url = BaseUtil.url+"/app_posp/pay/yilian/yilian.jsp";
						String str = HttpUtil.parseParams(params);
						String path = url + "?" + str;
						reqeustInfo.setUrl(path);
					}else if(reqeustInfo.getTranTp().equals("1")&&reqeustInfo.getProduct().equals("0"))
					{
						String url = BaseUtil.url+"/app_posp/pay/yilian/yilianT1.jsp";
						String str = HttpUtil.parseParams(params);
						String path = url + "?" + str;
						reqeustInfo.setUrl(path);
					}
						
					
				}
				try {
					result = jddsQrCodeService.Register(reqeustInfo);
					logger.info("响应信息：" + result);
				} catch (Exception e) {
					result.put("respCode", "0009");
					result.put("respMsg", "系统错误");
					logger.info("**********************生成支付二维码失败:{}", e);
					outString(response, gson.toJson(result));
				}
				logger.info("处理完成生成二维码");
			} else {
				logger.error("签名错误!");
				result.put("respCode", "15");
				result.put("respMsg", "签名错误");
			}

		} else {
			logger.error("上送交易参数空!");
			result.put("respCode", "01");
			result.put("respMsg", "fail");
		}

		logger.info("返回结果:{}", result);

		outString(response, gson.toJson(result));

	}

	/**
	 * 和上游交互 支付完成后同步返回支付结果
	 * 
	 * @param request
	 *            requet对象
	 * @param response
	 *            response对象
	 * @param temp
	 *            银联返回的数据
	 * @throws Exception
	 */
	@RequestMapping(value = "query")
	public void query(HttpServletRequest request, HttpServletResponse response) throws Exception {

		// 返回结果
		Map<String, String> result = new HashMap<String, String>();
		String param = requestClient(request);
		logger.info("下游上送参数:{}", param);

		if (!StringUtils.isEmpty(param)) {

			CustomerRegister reqeustInfo = gson.fromJson(param, CustomerRegister.class);

			logger.info("json转换扫码反扫对象{}", reqeustInfo);
			logger.info("下游上送签名串{}", reqeustInfo.getSign());
			if (signVerify(reqeustInfo, reqeustInfo.getSign())) {
				logger.info("开始处理生成二维码");
				try {
					OriginalOrderInfo orig = jddsQrCodeService.selectKeyUrl(reqeustInfo);
					logger.info("响应信息：" + orig);
					result.put("url", orig.getBgUrl());
				} catch (Exception e) {
					result.put("respCode", "0009");
					result.put("respMsg", "系统错误");
					logger.info("**********************生成支付二维码失败:{}", e);
					outString(response, gson.toJson(result));
				}
				logger.info("处理完成生成二维码");
			} else {
				logger.error("签名错误!");
				result.put("respCode", "15");
				result.put("respMsg", "签名错误");
			}

		} else {
			logger.error("上送交易参数空!");
			result.put("respCode", "01");
			result.put("respMsg", "fail");
		}

		logger.info("返回结果:{}", result);

		outString(response, gson.toJson(result));

	}
}
