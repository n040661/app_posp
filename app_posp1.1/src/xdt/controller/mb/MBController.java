package xdt.controller.mb;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;
import java.util.Map.Entry;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.alibaba.fastjson.JSON;
import com.sun.net.httpserver.HttpServer;

import xdt.controller.BaseAction;
import xdt.dto.mb.MBReqest;
import xdt.dto.mb.MBResponse;
import xdt.model.ChannleMerchantConfigKey;
import xdt.model.OriginalOrderInfo;
import xdt.quickpay.hengfeng.util.Bean2QueryStrUtil;
import xdt.quickpay.hf.util.EffersonPayService;
import xdt.schedule.ThreadPool;
import xdt.service.HfQuickPayService;
import xdt.service.IClientCollectionPayService;
import xdt.service.IClientH5Service;
import xdt.service.IMBService;
import xdt.util.HttpURLConection;
import xdt.util.HttpUtil;
import xdt.util.JsdsUtil;
import xdt.util.utils.MD5Utils;
import xdt.util.utils.RequestUtils;
import xdt.util.utils.UtilThread;

@Controller
@RequestMapping("/MBController")
public class MBController extends BaseAction {

	Logger log = Logger.getLogger(this.getClass());

	@Resource
	private IClientCollectionPayService clientCollectionPayService;

	@Resource
	private HfQuickPayService payService;

	@Resource
	private IClientH5Service ClientH5ServiceImpl;

	@Resource
	private IMBService imbService;

	@RequestMapping(value = "payClienty")
	public void mbPay(HttpServletRequest request, HttpServletResponse response, MBReqest mbReqest) throws Exception {
		try {
			log.info("下游传来的参数**********魔宝支付:" + JSON.toJSON(mbReqest));
			Map<String, String> results = new HashMap<>();
			ChannleMerchantConfigKey keyinfo = clientCollectionPayService.getChannelConfigKey(mbReqest.getMerId());
			// 获取商户秘钥
			String key = keyinfo.getMerchantkey();

			TreeMap<String, String> result = new TreeMap<String, String>();
			TreeMap<String, String> result1 = new TreeMap<String, String>();
			if (mbReqest.getSign() == null) {
				log.info("下游传来的参数签名参数**********魔宝支付:" + JSON.toJSON(mbReqest));

				result.putAll(JsdsUtil.beanToMap(mbReqest));
				String paramSrc = RequestUtils.getParamSrc(result);
				log.info("签名前数据**********魔宝支付:" + paramSrc);
				String md5 = MD5Utils.sign(paramSrc, key, "UTF-8");
				mbReqest.setSign(md5);
			}
			result.putAll(JsdsUtil.beanToMap(mbReqest));
			String paramSrc = RequestUtils.getParamSrc(result);
			log.info("签名前参数：" + paramSrc);
			boolean b = MD5Utils.verify(paramSrc, mbReqest.getSign(), key, "UTF-8");
			if (b) {
				log.info("签名成功**********魔宝支付");
				// 写逻辑
				mbReqest.setUrl(mbReqest.getBackNotifyUrl());
				if ("cj001".equals(mbReqest.getType())) {
					results = imbService.unionPayScanCode(mbReqest, results);
				} else if ("cj002".equals(mbReqest.getType())) {
					results = imbService.paySelect(mbReqest, results);
				} else if ("cj003".equals(mbReqest.getType())) {
					results = imbService.pay(mbReqest, results);
				} else if ("cj004".equals(mbReqest.getType())) {
					results = imbService.paysSelect(mbReqest, results);
				} else if ("cj005".equals(mbReqest.getType())) {
					results = imbService.unionPayScanCode(mbReqest, results);// 快捷短信支付
				} else if ("cj006".equals(mbReqest.getType())) {
					results = imbService.verification(mbReqest, results);// 快捷验证支付

				} else if ("cj007".equals(mbReqest.getType())) {

					results = imbService.unionPayScanCode(mbReqest, results);// 快捷支付
					// String html =
					// EffersonPayService.createAutoFormHtml("http://hanyipay.com/ks_netbank/mpay.c",
					// results, "GBK");
					// log.info("返回结果:{}" + html);
					// outString(response, html);

					logger.info("摩宝快捷上送的数据:" + results);
					String params = HttpURLConection.parseParams(results);
					logger.info("摩宝快捷上送的数据:" + params);
					String path = "http://hanyipay.com/ks_netbank/mpay.c?" + params;
					logger.info("demo 重定向：" + path);
					request.getSession();
					response.setCharacterEncoding("GBK");
					response.sendRedirect(path.replace(" ", " "));
				}

				// 获取商户秘钥
				result1.putAll(results);
				/*
				 * try { log.info("魔宝乱码吗》》"+URLDecoder.decode(result1.get("respMsg"),"GBK"));
				 * result1.put("refMsg", URLDecoder.decode(result1.get("respMsg"),"GBK")); }
				 * catch (UnsupportedEncodingException e) { // TODO Auto-generated catch block
				 * e.printStackTrace(); }
				 */

				String paramSrcs = RequestUtils.getParamSrc(result1);
				log.info("签名前数据**********魔宝支付:" + paramSrcs);
				String md5 = MD5Utils.sign(paramSrcs, key, "UTF-8");
				log.info("签名**********魔宝支付:" + md5);
				log.info("results" + results);
				results.put("sign", md5);
				log.info("摩宝**给下游返回支付的二维码数据：" + JSON.toJSON(results));
			} else {
				log.info("签名错误**********魔宝支付！");
				results.put("retcode", "1");
				results.put("retmsg", "签名错误");
			}
			outString(response, JSON.toJSON(results));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/*
	 * private Map<String, String> mbReqest(HttpServletResponse response, MBReqest
	 * mbReqest) {
	 * 
	 * return null; }
	 * 
	 * //@RequestMapping(value="cardPayParameter") public Map<String, String>
	 * unionPayScanCode(HttpServletResponse response,MBReqest mbReqest){
	 * 
	 * log.info("下游传来的参数**********魔宝支付:"+JSON.toJSON(mbReqest)); Map<String, String>
	 * results = new HashMap<>(); ChannleMerchantConfigKey keyinfo =
	 * clientCollectionPayService .getChannelConfigKey(mbReqest.getMerId()); //
	 * 获取商户秘钥 String key = keyinfo.getMerchantkey(); TreeMap<String, String> result
	 * = new TreeMap<String, String>(); TreeMap<String, String> result1 = new
	 * TreeMap<String, String>(); result.putAll(JsdsUtil.beanToMap(mbReqest));
	 * String paramSrc = RequestUtils.getParamSrc(result); boolean b =
	 * MD5Utils.verify(paramSrc, mbReqest.getSign(), key, "UTF-8"); if (b) {
	 * log.info("签名成功**********魔宝支付"); // 写逻辑
	 * mbReqest.setUrl(mbReqest.getBackNotifyUrl()); results
	 * =imbService.unionPayScanCode(mbReqest, results);
	 * 
	 * // 获取商户秘钥 result1.putAll(results); try {
	 * log.info("魔宝乱码吗》》"+URLDecoder.decode(result1.get("respMsg"),"GBK"));
	 * result1.put("refMsg", URLDecoder.decode(result1.get("respMsg"),"GBK")); }
	 * catch (UnsupportedEncodingException e) { // TODO Auto-generated catch block
	 * e.printStackTrace(); }
	 * 
	 * String paramSrcs = RequestUtils.getParamSrc(result1);
	 * log.info("签名前数据**********魔宝支付:" + paramSrcs); String md5 =
	 * MD5Utils.sign(paramSrcs, key, "UTF-8"); log.info("签名**********魔宝支付:" + md5);
	 * log.info("results" + results); results.put("sign", md5);
	 * log.info("摩宝**给下游返回支付的二维码数据："+JSON.toJSON(results)); } else {
	 * log.info("签名错误**********魔宝支付！"); results.put("retcode", "1");
	 * results.put("retmsg", "签名错误"); } return results; try { outString(response,
	 * JSON.toJSON(result1)); } catch (IOException e) {
	 * 
	 * e.printStackTrace(); }
	 * 
	 * 
	 * }
	 */

	@RequestMapping(value = "paySign")
	public void sign(HttpServletResponse response, MBReqest mbReqest) {
		log.info("下游传来的参数签名参数**********魔宝支付:" + JSON.toJSON(mbReqest));

		TreeMap<String, String> result = new TreeMap<String, String>();
		ChannleMerchantConfigKey keyinfo = clientCollectionPayService.getChannelConfigKey(mbReqest.getMerId());
		// 获取商户秘钥
		String key = keyinfo.getMerchantkey();
		result.putAll(JsdsUtil.beanToMap(mbReqest));
		String paramSrc = RequestUtils.getParamSrc(result);
		log.info("签名前数据**********魔宝支付:" + paramSrc);
		String md5 = MD5Utils.sign(paramSrc, key, "UTF-8");
		log.info("签名**********魔宝支付:" + md5);
		try {
			outString(response, md5);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@SuppressWarnings("unused")
	@RequestMapping(value = "unionPayNotifyUrl")
	public void unionPaynotifyUrl(HttpServletResponse response, MBResponse mbResponse, HttpServletRequest request) {
		log.info("魔宝异步进来了，参数：" + JSON.toJSON(mbResponse));
		String str;
		TreeMap<String, String> result = new TreeMap<>();
		try {

			mbResponse.setRefcode(request.getParameter("payStatus"));
			mbResponse.setOrderId(request.getParameter("orderId"));
			mbResponse.setRefMsg(URLDecoder.decode(request.getParameter("payMsg"), "UTF-8"));
			log.info("131:" + request.getParameter("payMsg"));
			log.info("131UTF-8:" + URLDecoder.decode(request.getParameter("payMsg"), "UTF-8"));
			log.info("131GBK:" + URLDecoder.decode(request.getParameter("payMsg"), "GBK"));
			log.info("131ISO-8859-1:" + URLDecoder.decode(request.getParameter("payMsg"), "ISO-8859-1"));
			if (mbResponse != null) {
				str = "OK";
				outString(response, str);
				OriginalOrderInfo originalInfo = null;
				if (mbResponse.getOrderId() != null && mbResponse.getOrderId() != "") {
					originalInfo = this.payService.getOriginOrderInfo(mbResponse.getOrderId());
				}
				log.info("订单数据**魔宝:" + JSON.toJSON(originalInfo));
				Bean2QueryStrUtil queryUtil = new Bean2QueryStrUtil();
				log.info("下游的异步地址**魔宝" + originalInfo.getBgUrl());
				ChannleMerchantConfigKey keyinfo = clientCollectionPayService
						.getChannelConfigKey(originalInfo.getPid());
				// 获取商户秘钥
				String key = keyinfo.getMerchantkey();
				imbService.update1(mbResponse);
				result.put("orderId", mbResponse.getOrderId());
				result.put("transAmount", Double.parseDouble(mbResponse.getTransAmount()) * 100 + "");
				result.put("respCode", "00");
				result.put("respMsg", "交易成功");
				if (mbResponse.getOrderDesc() != null && mbResponse.getOrderDesc() != "") {
					result.put("orderDesc", mbResponse.getOrderDesc());
				}
				if (mbResponse.getDev() != null && mbResponse.getDev() != "") {
					result.put("dev", originalInfo.getAttach());
				}
				result.put("merId", originalInfo.getPid());
				result.put("status", mbResponse.getRefcode());
				if ("00".equals(request.getParameter("payStatus"))) {
					int i = imbService.UpdatePmsMerchantInfo(originalInfo);
					if (i > 0) {
						log.info("魔宝*****实时入金完成");
					} else {
						log.info("魔宝*****实时入金失败");
					}
				} else {
					log.info("交易错误码:" + request.getParameter("payStatus") + ",错误信息:"
							+ URLDecoder.decode(request.getParameter("payMsg"), "UTF-8"));
				}
				String paramSrcs = RequestUtils.getParamSrc(result);
				log.info("签名前数据**********魔宝支付:" + paramSrcs);
				String md5 = MD5Utils.sign(paramSrcs, key, "UTF-8");

				log.info("异步之前的参数：" + RequestUtils.getParamSrc(result));
				String result1 = HttpURLConection.httpURLConnectionPOST(originalInfo.getBgUrl(),
						RequestUtils.getParamSrc(result) + "&sign=" + md5);
				log.info("下游返回状态" + result1);
				if (!"SUCCESS".equals(result1)) {
					ThreadPool.executor(
							new UtilThread(originalInfo.getBgUrl(), RequestUtils.getParamSrc(result) + "&sign=" + md5));
				}
			} else {
				str = "FALL";
				log.info("没有收到魔宝的异步数据");
				outString(response, str);
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	/*
	 * //@RequestMapping(value="paySelect") public Map<String, String>
	 * paySelect(HttpServletResponse response,MBReqest mbReqest) throws Exception{
	 * 
	 * log.info("魔宝***查询参数："+JSON.toJSON(mbReqest)); Map<String, String> result =new
	 * HashMap<>(); TreeMap<String, String> map =new TreeMap<>(); TreeMap<String,
	 * String> results = new TreeMap<String, String>(); ChannleMerchantConfigKey
	 * keyinfo = clientCollectionPayService
	 * .getChannelConfigKey(mbReqest.getMerId()); // 获取商户秘钥 String key =
	 * keyinfo.getMerchantkey(); results.putAll(JsdsUtil.beanToMap(mbReqest));
	 * String paramSrcss = RequestUtils.getParamSrc(results); boolean b =
	 * MD5Utils.verify(paramSrcss, mbReqest.getSign(), key, "UTF-8");
	 * 
	 * if(b){ log.info("魔宝***验签成功！"); result =imbService.paySelect(mbReqest,
	 * result); if(result!=null){ map.put("respCode", result.get("respCode"));
	 * map.put("status", result.get("status")); map.put("orderId",
	 * result.get("orderId"));
	 * map.put("respMsg",URLDecoder.decode(result.get("refMsg"),"GBK"));
	 * map.put("merId", result.get("merId"));
	 * if("00".equals(result.get("respCode"))){
	 * map.put("orderDesc",URLDecoder.decode(result.get("	"),"UTF-8") );
	 * map.put("dev", result.get("dev")); } }else{ map.put("respCode", "0001");
	 * map.put("respMsg","订单号不存在"); }
	 * 
	 * }else{ map.put("respCode", "0001"); map.put("respMsg","签名错误！"); } String
	 * paramSrcs = RequestUtils.getParamSrc(map); log.info("签名前数据**********魔宝支付:" +
	 * paramSrcs); String md5 = MD5Utils.sign(paramSrcs, key, "UTF-8");
	 * result.put("sign", md5); map.put("sign",md5);
	 * log.info("魔宝查询结果："+JSON.toJSON(map)); //outString(response,
	 * JSON.toJSON(map)); return map; }
	 * 
	 * public Map<String, String> pay(HttpServletResponse response,MBReqest
	 * mbReqest){ log.info("魔宝***代付参数："+JSON.toJSON(mbReqest)); Map<String, String>
	 * result =new HashMap<>(); ChannleMerchantConfigKey keyinfo =
	 * clientCollectionPayService .getChannelConfigKey(mbReqest.getMerId()); //
	 * 获取商户秘钥 String key = keyinfo.getMerchantkey(); TreeMap<String, String> results
	 * = new TreeMap<String, String>(); TreeMap<String, String> map =new
	 * TreeMap<>(); results.putAll(JsdsUtil.beanToMap(mbReqest)); String paramSrc =
	 * RequestUtils.getParamSrc(results); boolean b = MD5Utils.verify(paramSrc,
	 * mbReqest.getSign(), key, "UTF-8"); if(b){ log.info("签名正确");
	 * result=imbService.pay(mbReqest, result); map.putAll(result); String paramSrcs
	 * = RequestUtils.getParamSrc(map); String md5 = MD5Utils.sign(paramSrcs, key,
	 * "UTF-8"); map.put("sign", md5); }else{ map.put("respCode", "0001");
	 * map.put("respMsg","签名错误！"); }
	 * 
	 * return map; } public Map<String, String> paysSelect(HttpServletResponse
	 * response,MBReqest mbReqest) throws Exception{
	 * 
	 * log.info("魔宝***查询参数："+JSON.toJSON(mbReqest)); Map<String, String> result =new
	 * HashMap<>(); TreeMap<String, String> map =new TreeMap<>(); TreeMap<String,
	 * String> results = new TreeMap<String, String>(); ChannleMerchantConfigKey
	 * keyinfo = clientCollectionPayService
	 * .getChannelConfigKey(mbReqest.getMerId()); // 获取商户秘钥 String key =
	 * keyinfo.getMerchantkey(); results.putAll(JsdsUtil.beanToMap(mbReqest));
	 * String paramSrcss = RequestUtils.getParamSrc(results); boolean b =
	 * MD5Utils.verify(paramSrcss, mbReqest.getSign(), key, "UTF-8");
	 * 
	 * if(b){ log.info("魔宝***验签成功！"); result =imbService.paysSelect(mbReqest,
	 * result); if(result!=null){ log.info("ddsad:"+result); map.putAll(result);
	 * }else{ map.put("respCode", "0001"); map.put("respMsg","订单号不存在"); }
	 * 
	 * }else{ map.put("respCode", "0001"); map.put("respMsg","签名错误！"); } String
	 * paramSrcs = RequestUtils.getParamSrc(map); log.info("签名前数据**********魔宝支付:" +
	 * paramSrcs); String md5 = MD5Utils.sign(paramSrcs, key, "UTF-8");
	 * result.put("sign", md5); map.put("sign",md5);
	 * log.info("魔宝查询结果："+JSON.toJSON(map)); //outString(response,
	 * JSON.toJSON(map)); return map; }
	 */
	@SuppressWarnings("unused")
	@RequestMapping(value = "notifyUrl")
	public void notifyUrl(HttpServletResponse response, MBResponse mbResponse, HttpServletRequest request) {
		log.info("魔宝异步进来了，参数：" + JSON.toJSON(mbResponse));
		String str;
		TreeMap<String, String> result = new TreeMap<>();
		try {
			if(!"".equals(request.getParameter("refcode"))&&request.getParameter("refcode")!=null) {
				mbResponse.setRefcode(request.getParameter("refcode"));
			}else if(!"".equals(request.getParameter("refCode"))&&request.getParameter("refCode")!=null) {
				mbResponse.setRefcode(request.getParameter("refCode"));
			}
			mbResponse.setOrderId(request.getParameter("orderId"));
			mbResponse.setRefMsg(URLDecoder.decode(request.getParameter("refMsg"), "UTF-8"));
			log.info("131:" + request.getParameter("refMsg"));
			log.info("131UTF-8:" + URLDecoder.decode(request.getParameter("refMsg"), "UTF-8"));
			log.info("131GBK:" + URLDecoder.decode(request.getParameter("refMsg"), "GBK"));
			log.info("131ISO-8859-1:" + URLDecoder.decode(request.getParameter("refMsg"), "ISO-8859-1"));
			
			if (mbResponse != null) {
				str = "OK";
				outString(response, str);
				OriginalOrderInfo originalInfo = null;
				if (mbResponse.getOrderId() != null && mbResponse.getOrderId() != "") {
					originalInfo = this.payService.getOriginOrderInfo(mbResponse.getOrderId());
				}
				log.info("订单数据**魔宝:" + JSON.toJSON(originalInfo));
				Bean2QueryStrUtil queryUtil = new Bean2QueryStrUtil();
				log.info("下游的异步地址**魔宝" + originalInfo.getBgUrl());
				ChannleMerchantConfigKey keyinfo = clientCollectionPayService
						.getChannelConfigKey(originalInfo.getPid());
				// 获取商户秘钥
				String key = keyinfo.getMerchantkey();
				imbService.update(mbResponse);
				result.put("orderId", mbResponse.getOrderId());
				result.put("transAmount", Double.parseDouble(mbResponse.getTransAmount()) * 100 + "");
				result.put("respCode", "00");
				result.put("respMsg", "交易成功");
				if (mbResponse.getOrderDesc() != null && mbResponse.getOrderDesc() != "") {
					result.put("orderDesc", mbResponse.getOrderDesc());
				}
				if (mbResponse.getDev() != null && mbResponse.getDev() != "") {
					result.put("dev", mbResponse.getDev());
				}
				result.put("merId", originalInfo.getPid());
				result.put("status", mbResponse.getRefcode());
				if ("00".equals(request.getParameter("refCode"))||"00".equals(request.getParameter("refcode"))) {
					int i = imbService.UpdatePmsMerchantInfo(originalInfo);
					if (i > 0) {
						log.info("魔宝*****实时入金完成");
					} else {
						log.info("魔宝*****实时入金失败");
					}
				} else {
					log.info("交易错误码:" + request.getParameter("refCode")+"或"+request.getParameter("refcode") + ",错误信息:"
							+ URLDecoder.decode(request.getParameter("refMsg"), "UTF-8"));
				}
				String paramSrcs = RequestUtils.getParamSrc(result);
				log.info("签名前数据**********魔宝支付:" + paramSrcs);
				String md5 = MD5Utils.sign(paramSrcs, key, "UTF-8");

				log.info("异步之前的参数：" + RequestUtils.getParamSrc(result));
				String result1 = HttpURLConection.httpURLConnectionPOST(originalInfo.getBgUrl(),
						RequestUtils.getParamSrc(result) + "&sign=" + md5);
				log.info("下游返回状态" + result1);
				if (!"SUCCESS".equals(result1)) {
					ThreadPool.executor(
							new UtilThread(originalInfo.getBgUrl(), RequestUtils.getParamSrc(result) + "&sign=" + md5));
				}
			} else {
				str = "FALL";
				log.info("没有收到魔宝的异步数据");
				outString(response, str);
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	@SuppressWarnings("unused")
	@RequestMapping(value = "returnUrl")
	public void returnUrl(HttpServletResponse response, MBResponse mbResponse, HttpServletRequest request) {
		log.info("魔宝前台通知进来了，参数：" + JSON.toJSON(mbResponse));
		TreeMap<String, String> result = new TreeMap<>();
		try {

			mbResponse.setRefcode(request.getParameter("payStatus"));
			mbResponse.setOrderId(request.getParameter("orderId"));
			mbResponse.setRefMsg(URLDecoder.decode(request.getParameter("payMsg"), "UTF-8"));
			if (mbResponse != null) {
				// outString(response, str);
				OriginalOrderInfo originalInfo = null;
				if (mbResponse.getOrderId() != null && mbResponse.getOrderId() != "") {
					originalInfo = this.payService.getOriginOrderInfo(mbResponse.getOrderId());
				}
				log.info("订单数据**魔宝:" + JSON.toJSON(originalInfo));
				Bean2QueryStrUtil queryUtil = new Bean2QueryStrUtil();
				log.info("下游的同步地址**魔宝" + originalInfo.getBgUrl());
				ChannleMerchantConfigKey keyinfo = clientCollectionPayService
						.getChannelConfigKey(originalInfo.getPid());
				// 获取商户秘钥
				String key = keyinfo.getMerchantkey();
				result.put("orderId", mbResponse.getOrderId());
				result.put("transAmount", Double.parseDouble(mbResponse.getTransAmount()) * 100 + "");
				result.put("respCode", "00");
				result.put("respMsg", "交易成功");
				result.put("merId", originalInfo.getPid());
				String paramSrcs = RequestUtils.getParamSrc(result);
				log.info("签名前数据**********魔宝支付:" + paramSrcs);
				String md5 = MD5Utils.sign(paramSrcs, key, "UTF-8");

				result.put("sign", md5);
				String params = HttpURLConection.parseParams(result);
				logger.info("给下游同步的数据:" + params);
				logger.info("给下游同步的地址:" + originalInfo.getPageUrl());
				request.getSession();
				// 给下游手动返回支付结果
				if (originalInfo.getPageUrl().indexOf("?") == -1) {
					String path = originalInfo.getPageUrl() + "?" + params;
					log.info("pageUrl 商户页面 重定向：" + path);
					response.sendRedirect(path.replace(" ", ""));
				} else {
					log.info("pageUrl 商户页面 重定向：" + originalInfo.getPageUrl());
					String path = originalInfo.getPageUrl() + "&" + params;
					log.info("pageUrl 商户页面 重定向：" + path);
					response.sendRedirect(path.replace(" ", ""));
				}

			} else {
				log.info("没有收到魔宝的同步数据");
				// outString(response, str);
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args) {
		try {
			System.out.println(URLDecoder.decode("%B8%C3%C9%CC%BB%A7%B2%BB%B4%E6%D4%DA%BB%F2%D5%DF%C9%CC%BB%A7%D7%B4%CC%AC%CE%AA%BD%FB%D6%B9%2C%C7%EB%C1%AA%CF%B5%C4%A6%B1%A6%BF%CD%B7%FE", "GBK"));
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
