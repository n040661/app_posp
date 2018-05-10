package xdt.controller;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.ServletException;
import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import xdt.dto.nbs.webpay.WechatWebPayResponse;
import xdt.dto.qianlong.PayRequestEntity;
import xdt.dto.qianlong.ValidateSign;
import xdt.model.OriginalOrderInfo;
import xdt.model.PmsAgentInfo;
import xdt.model.PmsAppTransInfo;
import xdt.model.PmsWeixinMerchartInfo;
import xdt.model.PospTransInfo;
import xdt.pufa.security.PuFaSignUtil;
import xdt.quickpay.cjt.comm.Constant;
import xdt.quickpay.cjt.temp.ChanpayGatewayQpayDemo;
import xdt.quickpay.hengfeng.util.Bean2QueryStrUtil;
import xdt.quickpay.qianlong.model.Merchant;
import xdt.quickpay.qianlong.model.Order;
import xdt.quickpay.qianlong.model.PayResponseEntity;
import xdt.quickpay.qianlong.util.HttpClientHelper;
import xdt.quickpay.qianlong.util.HttpResponse;
import xdt.quickpay.qianlong.util.MyRSAUtils;
import xdt.quickpay.qianlong.util.QLPostThread;
import xdt.quickpay.qianlong.util.SdkUtil;
import xdt.quickpay.qianlong.util.SignatureUtil;
import xdt.schedule.ThreadPool;
import xdt.service.IPmsAgentInfoService;
import xdt.service.QLpayService;
import xdt.util.BeanToMapUtil;
import xdt.util.HttpURLConection;
import xdt.util.HttpUtil;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.google.gson.Gson;

import net.sf.json.JSONObject;

/**
 * @ClassName: CjtQuickPaymentAction
 * @Description: 钱龙二维码支付
 * @author 尚延超
 * @date 2016年11月30日
 * 
 */
@Controller
@RequestMapping("ql")
public class QLPaymentAction extends BaseAction {
	/**
	 * 日志记录
	 */
	public static final Logger log = LoggerFactory.getLogger(QLPaymentAction.class);

	@Resource
	private QLpayService qLpayService;

	@Resource
	private IPmsAgentInfoService pmsAgentInfoService;

	public static final String SUCCESS_CODE = "200";// 成功

	/**
	 * 和上游交互
	 * 
	 * @param name
	 *            完成注册功能
	 * @param param
	 *            注册信息
	 * @param request
	 *            请求对象
	 * @param response
	 *            响应对象
	 * @throws Exception
	 * 
	 */
	@ResponseBody
	@RequestMapping(value = "register")
	public void register(HttpServletRequest request, HttpServletResponse response) throws Exception {
		Map<String, String> result = new HashMap<String, String>();
		logger.info(" ##############################注册商户");
		String param = requestClient(request);
		logger.info("下游上送参数{}：" + param);
		if (!StringUtils.isEmpty(param)) {
			PmsWeixinMerchartInfo weixin = gson.fromJson(param, PmsWeixinMerchartInfo.class);
			if (signVerify(weixin, weixin.getSign())) {

				try {
					result = qLpayService.customerRegister(weixin);
				} catch (Exception e) {
                     e.printStackTrace();
				}
			} else {
				log.error("签名错误!");
				result.put("respCode", "15");
				result.put("respMsg", "签名错误");
			}
		} else {
			log.error("上送交易参数空!");
			result.put("respCode", "01");
			result.put("respMsg", "fail");
		}
		outString(response, gson.toJson(result));
		log.info("返回结果:{}");
	}

	/**
	 * 和上游交互
	 * 
	 * @param name
	 *            验证注册是否成功
	 * @param request
	 *            请求对象
	 * @param response
	 *            响应对象
	 * @throws Exception
	 * 
	 */
	@ResponseBody
	@RequestMapping(value = "validate")
	public void validate(HttpServletRequest request, HttpServletResponse response) throws Exception {
		Merchant mer = new Merchant();// 上送参数

		log.info("*******************开始执行qlpayService.payHandle**************************");
		String param = requestClient(request);
		logger.info("下游上送参数：" + param);
		Map<String, String> result = new HashMap<String, String>();
		if (!StringUtils.isEmpty(param)) {
			PmsWeixinMerchartInfo weixin = gson.fromJson(param, PmsWeixinMerchartInfo.class);
			if (signVerify(weixin, weixin.getSign())) {
				// 所有的流程通过 就发起上送数据
				try {
					Map<String, String> params = new HashMap<String, String>();
					params.put("orgId", SdkUtil.getStringValue("chroneOrgId"));
					params.put("account", weixin.getAccount());
					String bigStr = SignatureUtil.hex(params);
					params.put("signature", MyRSAUtils.sign(SdkUtil.getStringValue("chronePrivateKey"), bigStr,
							MyRSAUtils.MD5_SIGN_ALGORITHM));
					String postData = JSON.toJSONString(params);
					List<String[]> headers = new ArrayList<String[]>();
					headers.add(new String[] { "Content-Type", "application/json" });
					HttpResponse response1 = HttpClientHelper.doHttp(SdkUtil.getStringValue("chronePayforQueryUrl"),
							HttpClientHelper.POST, headers, "utf-8", postData, "60000");
					if (StringUtils.isNotEmpty(response1.getRspStr())) {
						log.debug("chrone regist result:" + response1.getRspStr());
						result = JSON.parseObject(response1.getRspStr(), new TypeReference<Map<String, String>>() {
						});
						if (SUCCESS_CODE.equals(result.get("respCode"))) {

							// weixin.setResult("00");
						}
					}
				} catch (Exception e) {
					log.error("商户注册查询请求失败");
					e.printStackTrace();
				}
			} else {
				log.error("签名错误!");
				result.put("respCode", "15");
				result.put("respMsg", "签名错误");
			}
		} else {
			log.error("上送交易参数空!");
			result.put("respCode", "01");
			result.put("respMsg", "fail");
		}
		outString(response, gson.toJson(result));
		log.info("返回结果:{}");
	}

	/**
	 * 和上游交互
	 * 
	 * @param name
	 *            完成二维码支付的功能
	 * @param param
	 *            支付信息
	 * @param request
	 *            请求对象
	 * @param response
	 *            响应对象
	 * @throws Exception
	 * 
	 */
	@ResponseBody
	@RequestMapping(value = "dimension")
	public void twodimension(HttpServletRequest request, HttpServletResponse response) throws Exception {
		// 返回结果
		Map<String, String> result = new HashMap();

		log.info("生成二维码");

		String param = requestClient(request);
		log.info("下游上送参数:{}", param);

		if (!StringUtils.isEmpty(param)) {

			PayRequestEntity temp = gson.fromJson(param, PayRequestEntity.class);
			log.info("json转换扫码反扫对象{}");
			log.info("下游上送签名串{}");
			if (signVerify(temp, temp.getSign())) {
				log.info("开始处理生成二维码");
				result = qLpayService.twoDimensionCode(temp);
				log.info("处理完成生成二维码");
			} else {
				log.error("签名错误!");
				result.put("respCode", "15");
				result.put("respMsg", "签名错误");
			}

		} else {
			log.error("上送交易参数空!");
			result.put("respCode", "01");
			result.put("respMsg", "fail");
		}
		outString(response, gson.toJson(result));
		log.info("返回结果:{}");

	}

	/**
	 * 和上游交互
	 * 
	 * @param name
	 *            完成固态二维码支付的功能
	 * @param param
	 *            支付信息
	 * @param request
	 *            请求对象
	 * @param response
	 *            响应对象
	 * @throws Exception
	 * 
	 */
	@ResponseBody
	@RequestMapping(value = "solidtwodimension")
	public void solidtwodimension(HttpServletRequest request, HttpServletResponse response) throws Exception {
		// 返回结果
		Map<String, String> result = new HashMap();

		log.info("生成二维码");

		String param = requestClient(request);
		log.info("下游上送参数:{}", param);

		if (!StringUtils.isEmpty(param)) {

			PayRequestEntity temp = gson.fromJson(param, PayRequestEntity.class);
			log.info("json转换扫码反扫对象{}");
			log.info("下游上送签名串{}");
			if (signVerify(temp, temp.getSign())) {
				log.info("开始处理生成二维码");
				result = qLpayService.solidtwodimensionCode(temp);
				log.info("处理完成生成二维码");
			} else {
				log.error("签名错误!");
				result.put("respCode", "15");
				result.put("respMsg", "签名错误");
			}

		} else {
			log.error("上送交易参数空!");
			result.put("respCode", "01");
			result.put("respMsg", "fail");
		}
		outString(response, gson.toJson(result));
		log.info("返回结果:{}");

	}

	/**
	 * 生成签名
	 * 
	 * @param name
	 *            完成生成签名的功能
	 * @param param
	 *            签名信息
	 * @param request
	 *            请求对象
	 * @param response
	 *            响应对象
	 * @throws Exception
	 * 
	 */
	@ResponseBody
	@RequestMapping("paySign")
	public void paySign(HttpServletRequest request, HttpServletResponse response) throws Exception {
		response.setHeader("Access-Control-Allow-Origin", "*");
		response.setContentType("text/html;charset=utf-8");
		String param = requestClient(request);
		PayRequestEntity entity = gson.fromJson(param, PayRequestEntity.class);
		Map map = BeanToMapUtil.convertBean(entity);
		String sign = PuFaSignUtil.sign(map);
		log.info("支付签名");
		outString(response, sign);
	}

	/**
	 * 生成签名
	 * 
	 * @param name
	 *            完成生成签名的功能
	 * @param param
	 *            签名信息
	 * @param request
	 *            请求对象
	 * @param response
	 *            响应对象
	 * @throws Exception
	 * 
	 */
	@ResponseBody
	@RequestMapping("paySign1")
	public void paySign1(HttpServletRequest request, HttpServletResponse response) throws Exception {
		response.setHeader("Access-Control-Allow-Origin", "*");
		response.setContentType("text/html;charset=utf-8");
		String param = requestClient(request);
		PmsWeixinMerchartInfo entity = gson.fromJson(param, PmsWeixinMerchartInfo.class);
		Map map = BeanToMapUtil.convertBean(entity);
		String sign = PuFaSignUtil.sign(map);
		log.info("支付签名");
		outString(response, sign);
	}

	/**
	 * 和上游交互 支付完成后返回支付结果
	 * 
	 * @param request
	 *            requet对象
	 * @param response
	 *            response对象
	 * @param temp
	 *            扫码之后返回的数据
	 * @throws Exception
	 */
	@RequestMapping(value = "payQuery")
	public void payQuery(HttpServletRequest request, HttpServletResponse response) throws Exception {

		// Merchant mer = new Merchant();// 上送参数
		log.info("查询结果");

		String param = requestClient(request);
		log.info("下游上送参数:{}", param);

		log.info("*******************开始执行qlpayService.payHandle**************************");
		Map<String, String> result = new HashMap<String, String>();
		// 所有的流程通过 就发起上送数据
		if (!StringUtils.isEmpty(param)) {
			PayRequestEntity temp = gson.fromJson(param, PayRequestEntity.class);
			if (signVerify(temp, temp.getSign())) {
				try {
					Map<String, String> params = new HashMap<String, String>();
					params.put("orgId", SdkUtil.getStringValue("chroneOrgId"));
					params.put("orgOrderNo", temp.getOrgOrderNo());
					String bigStr = SignatureUtil.hex(params);
					log.info("生成的明文:{}", bigStr);
					params.put("signature", MyRSAUtils.sign(SdkUtil.getStringValue("chronePrivateKey"), bigStr,
							MyRSAUtils.MD5_SIGN_ALGORITHM));
					String postData = JSON.toJSONString(params);
					log.info("封装之后的数据:{}", postData);
					List<String[]> headers = new ArrayList<String[]>();
					headers.add(new String[] { "Content-Type", "application/json" });
					HttpResponse response1 = HttpClientHelper.doHttp(SdkUtil.getStringValue("chroneSkpayUrl"),
							HttpClientHelper.POST, headers, "utf-8", postData, "60000");
					if (StringUtils.isNotEmpty(response1.getRspStr())) {
						log.debug("chrone regist result:" + response1.getRspStr());
						result = JSON.parseObject(response1.getRspStr(), new TypeReference<Map<String, String>>() {
						});
						// outString(response, gson.toJson(retMap));
						log.info("解析的结果{}:", result);
						if ("2".equals(result.get("paySt"))) {

							log.info("支付查询成功!");
						}
					}
				} catch (Exception e) {
					log.error("支付查询请求失败");
					e.printStackTrace();
				}
			} else {
				log.error("签名错误!");
				result.put("respCode", "15");
				result.put("respMsg", "签名错误");
			}
		} else {
			log.error("上送交易参数空!");
			result.put("respCode", "01");
			result.put("respMsg", "fail");
		}
		outString(response, gson.toJson(result));
		log.info("返回结果:{}");
	}

	/**
	 * 和上游交互 支付完成后清算结果查询
	 * 
	 * @param request
	 *            requet对象
	 * @param response
	 *            response对象
	 * @param temp
	 *            扫码之后返回的数据
	 * @throws Exception
	 */
	@RequestMapping(value = "query")
	public void query(HttpServletRequest request, HttpServletResponse response) throws Exception {

		// Merchant mer = new Merchant();// 上送参数
		log.info("查询结果");

		String param = requestClient(request);
		log.info("下游上送参数:{}", param);
		Map<String, String> result = new HashMap<String, String>();
		if (!StringUtils.isEmpty(param)) {
			PayRequestEntity temp = gson.fromJson(param, PayRequestEntity.class);
			if (signVerify(temp, temp.getSign())) {

				log.info("*******************开始执行qlpayService.payHandle**************************");
				// 所有的流程通过 就发起上送数据
				try {
					Map<String, String> params = new HashMap<String, String>();
					params.put("orgId", SdkUtil.getStringValue("chroneOrgId"));
					params.put("orderNo", temp.getOrderNo());
					String bigStr = SignatureUtil.hex(params);
					params.put("signature", MyRSAUtils.sign(SdkUtil.getStringValue("chronePrivateKey"), bigStr,
							MyRSAUtils.MD5_SIGN_ALGORITHM));
					String postData = JSON.toJSONString(params);
					List<String[]> headers = new ArrayList<String[]>();
					headers.add(new String[] { "Content-Type", "application/json" });
					HttpResponse response1 = HttpClientHelper.doHttp(SdkUtil.getStringValue("chroneFkpayUrl"),
							HttpClientHelper.POST, headers, "utf-8", postData, "60000");
					if (StringUtils.isNotEmpty(response1.getRspStr())) {
						log.debug("chrone regist result:" + response1.getRspStr());
						result = JSON.parseObject(response1.getRspStr(), new TypeReference<Map<String, String>>() {
						});
						// outString(response, gson.toJson(result));
						log.info("解析的结果{}:", result);
						if ("2".equals(result.get("paySt"))) {

							log.info("付款成功!");
						}
					}
				} catch (Exception e) {
					log.error("付款查询请求失败");
					e.printStackTrace();
				}
			} else {
				log.error("签名错误!");
				result.put("respCode", "15");
				result.put("respMsg", "签名错误");
			}
		} else {
			log.error("上送交易参数空!");
			result.put("respCode", "01");
			result.put("respMsg", "fail");
		}
		outString(response, gson.toJson(result));
		log.info("返回结果:{}");

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
	@RequestMapping(value = "bgPayResult")
	public void payResult(HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		log.info("**********进入异步通知的地址中***********");
		PayResponseEntity temp = new PayResponseEntity();
		Map<String, String> result = new HashMap<String, String>();
		BufferedReader br = new BufferedReader(new InputStreamReader((ServletInputStream) request.getInputStream()));
		String line = null;
		StringBuffer sb = new StringBuffer();
		while ((line = br.readLine()) != null) {
			sb.append(line);
		}
		String appMsg = sb.toString();
		log.info("请求参数：" + appMsg);
		if (!StringUtils.isEmpty(appMsg)) {
			// response.getWriter().write("SUCCESS");
			JSONObject ob = JSONObject.fromObject(appMsg);
			log.info("封装之后的数据:{}", ob);
			Iterator it = ob.keys();
			while (it.hasNext()) {
				String key = (String) it.next();
				if (key.equals("amount")) {
					String value = ob.getString(key);
					log.info("订单总金额:" + "\t" + value);
					temp.setAmount(value);
				}
				if (key.equals("extra")) {
					String value = ob.getString(key);
					log.info("订单额外参数:" + "\t" + value);
					temp.setExtra(value);
				}
				if (key.equals("orderDt")) {
					String value = ob.getString(key);
					log.info("订单日期:" + "\t" + value);
					temp.setOrderDt(value);
				}
				if (key.equals("orderNo")) {
					String value = ob.getString(key);
					log.info("平台订单号:" + "\t" + value);
					temp.setOrderNo(value);
				}
				if (key.equals("orgOrderNo")) {
					String value = ob.getString(key);
					log.info("机构订单号:" + "\t" + value);
					temp.setOrgOrderNo(value);
				}
				if (key.equals("body")) {
					String value = ob.getString(key);
					log.info("订单描述:" + "\t" + value);
					temp.setBody(value);
				}
				if (key.equals("orgId")) {
					String value = ob.getString(key);
					log.info("所属机构:" + "\t" + value);
					temp.setOrgId(value);
				}
				if (key.equals("paySt")) {
					String value = ob.getString(key);
					log.info("支付状态:" + "\t" + value);
					temp.setPaySt(value);
				}
				if (key.equals("fee")) {
					String value = ob.getString(key);
					log.info("手续费:" + "\t" + value);
					temp.setFee(value);
				}
				if (key.equals("signature")) {
					String value = ob.getString(key);
					log.info("签名:" + "\t" + value);
					temp.setSignature(value);
				}
				if (key.equals("subject")) {
					String value = ob.getString(key);
					log.info("订单标题:" + "\t" + value);
					temp.setSubject(value);
				}
				if (key.equals("respMsg")) {
					String value = ob.getString(key);
					log.info("应答码描述:" + "\t" + value);
					temp.setRespMsg(value);
				}
				if (key.equals("description")) {
					String value = ob.getString(key);
					log.info("订单附件描述信息:" + "\t" + value);
					temp.setDescription(value);
				}
				if (key.equals("account")) {
					String value = ob.getString(key);
					log.info("所属商户账号:" + "\t" + value);
					temp.setAccount(value);
				}
				if (key.equals("respCode")) {
					String value = ob.getString(key);
					log.info("应答码:" + "\t" + value);
					temp.setRespCode(value);
				}
			}
			log.info("支付后返回的信息：" + temp);
			Map params = new HashMap();
			this.qLpayService.otherInvoke(temp);
			response.getWriter().write("{\"success\":\"true\"}");
			String tranId = temp.getOrgOrderNo();
			// 查询商户上送原始信息
			OriginalOrderInfo originalInfo = qLpayService.getOriginOrderInfo(tranId);

			// 替换成下游商户的
			temp.setOrgOrderNo(originalInfo.getMerchantOrderId());
			temp.setOrderDt(originalInfo.getOrderTime());
			temp.setMerchartId(originalInfo.getPid());

			Bean2QueryStrUtil bean2Util = new Bean2QueryStrUtil();
			// 给下游主动返回支付结果

			String path = originalInfo.getBgUrl() + "?" + bean2Util.bean2QueryStr(temp);
			// response.sendRedirect(path.replace(" ", ""));
			log.info("bgUrl 平台服务器重定向：" + path);
			String json = HttpURLConection.sendPost(originalInfo.getBgUrl(), bean2Util.bean2QueryStr(temp));
			JSONObject ob1 = JSONObject.fromObject(json);
			Iterator it1 = ob1.keys();
			Map<String, String> map = new HashMap<>();
			while (it1.hasNext()) {
				String key1 = (String) it1.next();
				if (key1.equals("success")) {
					String value = ob1.getString(key1);
					log.info("异步回馈的结果:" + "\t" + value);
					map.put("success", value);
				}
			}
			if (map.get("success").equals("false")) {
				
				log.info("启动线程进行异步通知");
				// 启线程进行异步通知
				ThreadPool.executor(new QLPostThread(originalInfo.getBgUrl(),temp));
			}
			log.info("向下游 发送数据成功");
		} else {
			log.error("回调的参数为空!");
			result.put("respCode", "01");
			result.put("respMsg", "fail");
		}
	}

	/**
	 * 和上游交互 支付完成后返回支付结果
	 * 
	 * @param request
	 *            requet对象
	 * @param response
	 *            response对象
	 * @param temp
	 *            银联返回的数据
	 * @throws Exception
	 */
	@RequestMapping(value = "pagePayResultt")
	public void pagePayResult(HttpServletRequest request, HttpServletResponse response, PayResponseEntity temp)
			throws Exception {

		log.info("响应信息：" + temp);
		log.info("响应参数：" + request.getQueryString());

		Map<String, String> params = new HashMap<String, String>();
		params = qLpayService.payQuery(temp);
		log.info("返回结果：{}" + params);
		// String sysseqno=temp.getOrgOrderNo();
		// PospTransInfo transInfo=pospTransInfoDAO.selectBySysseqno(sysseqno);
		// PmsAppTransInfo
		// pmsAppTransInfo=pmsAppTransInfoDao.searchOrderInfo(transInfo.getOrderId());
		if ("0000".equals(params.get("respCode"))) {
			response.getWriter().write("{\"success\":\"true\"}");

			// qLpayService.otherInvoke(temp);
			// 交易id
			String tranId = temp.getOrgOrderNo();
			// 查询商户上送原始信息
			OriginalOrderInfo originalInfo = qLpayService.getOriginOrderInfo(tranId);

			// 替换成下游商户的
			temp.setOrgOrderNo(originalInfo.getMerchantOrderId());
			temp.setOrderDt(originalInfo.getOrderTime());
			temp.setMerchartId(originalInfo.getPid());

			Bean2QueryStrUtil bean2Util = new Bean2QueryStrUtil();
			// 给下游主动返回支付结果

			String path = "";

			if (originalInfo.getBgUrl().indexOf("?") == -1) {

				path = originalInfo.getBgUrl() + "?" + bean2Util.bean2QueryStr(temp);
			} else {

				path = originalInfo.getBgUrl() + "&" + bean2Util.bean2QueryStr(temp);
			}

			log.info("bgUrl 平台服务器重定向：" + path);
			response.sendRedirect(path.replace(" ", ""));

		}

	}

	/**
	 * 验证签名
	 * 
	 * @param name
	 *            完成验证签名的功能
	 * @param param
	 *            签名信息
	 * @param request
	 *            请求对象
	 * @param response
	 *            响应对象
	 * @throws Exception
	 * 
	 */
	@ResponseBody
	@RequestMapping("checkSign")
	public void checkSign(ValidateSign temp, HttpServletRequest request, HttpServletResponse response)
			throws IOException {
		log.info("下游上送参数:{}", temp);
		Map<String, String> params = new HashMap<String, String>();
		params.put("amount", temp.getAmount());
		params.put("orderDt", temp.getOrderDt());
		params.put("orderNo", temp.getOrderNo());
		params.put("orgOrderNo", temp.getOrgOrderNo());
		params.put("orgId", temp.getOrgId());
		params.put("paySt", temp.getPaySt());
		params.put("fee", temp.getFee());
		params.put("subject", temp.getSubject());
		params.put("respMsg", temp.getRespMsg());
		params.put("account", temp.getAccount());
		params.put("respCode", temp.getRespCode());
		log.info("生成的集合:{}", params);
		String bigStr = SignatureUtil.hex(params);
		log.info("生成的明文:{}", bigStr);
		String signnature = MyRSAUtils.sign(SdkUtil.getStringValue("chronePrivateKey"), bigStr,
				MyRSAUtils.MD5_SIGN_ALGORITHM);
		log.info("生成的签名:{}", signnature);
		String name = null;
		if (!(MyRSAUtils.verifySignature(SdkUtil.getStringValue("chronePublicKey"), temp.getSignature(), bigStr,
				MyRSAUtils.MD5_SIGN_ALGORITHM))) {
			name = "验签失败";
			log.info("验签失败");
		} else {
			name = "验签成功";
			log.info("验签成功");
		}
		log.info("支付签名");
		outString(response, name);
	}

	/**
	 * 生成签名
	 * 
	 * @param name
	 *            完成生成签名的功能
	 * @param param
	 *            签名信息
	 * @param request
	 *            请求对象
	 * @param response
	 *            响应对象
	 * @throws IOException
	 * @throws ServletException
	 * @throws Exception
	 * 
	 */
	@ResponseBody
	@RequestMapping("identifying")
	public void identifying(PayRequestEntity order, HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		Map<String, String> params = new HashMap<>();
		params.put("orgId", SdkUtil.getStringValue("chroneOrgId"));
		params.put("source", order.getSource() + "");
		params.put("subject", order.getSubject() + "");
		params.put("settleAmt", order.getSettleAmt() + "");
		params.put("account", order.getAccount());
		params.put("amount", order.getAmount() + "");
		params.put("notifyUrl", SdkUtil.getStringValue("chroneNotifyurl"));
		params.put("callbackUrl", SdkUtil.getStringValue("chroneCallbackUrl"));
		params.put("tranTp", order.getTranTp() + "");
		params.put("orgOrderNo", order.getOrgOrderNo());
		String bigStr = SignatureUtil.hex(params);
		params.put("signature",
				MyRSAUtils.sign(SdkUtil.getStringValue("chronePrivateKey"), bigStr, MyRSAUtils.MD5_SIGN_ALGORITHM));
		request.setAttribute("temp", params);
		request.getRequestDispatcher("/login.jsp").forward(request, response);
	}

}
