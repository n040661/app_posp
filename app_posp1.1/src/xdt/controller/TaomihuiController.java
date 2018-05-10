package xdt.controller;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
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

import com.alibaba.fastjson.JSON;

import net.sf.json.JSONObject;
import xdt.dto.nbs.alipay.AlipayParamRequest;
import xdt.dto.nbs.micro.WechatMicroRequest;
import xdt.dto.nbs.micro.WechatMicroResponse;
import xdt.dto.nbs.orderquery.WechatOrderQueryRequest;
import xdt.dto.nbs.orderquery.WechatOrderQueryResponse;
import xdt.dto.nbs.scan.WechatScannedRequest;
import xdt.dto.nbs.scan.WechatScannedResponse;
import xdt.dto.nbs.settle.SettleQueryWebPayRequest;
import xdt.dto.nbs.settle.SettleQueryWebPayResponse;
import xdt.dto.nbs.settle.SettleWebPayRequest;
import xdt.dto.nbs.settle.SettleWebPayResponse;
import xdt.dto.nbs.webpay.WechatWebPay;
import xdt.dto.nbs.webpay.WechatWebPayRequest;
import xdt.dto.nbs.webpay.WechatWebPayResponse;
import xdt.model.ChannleMerchantConfigKey;
import xdt.model.OriginalOrderInfo;
import xdt.model.PmsBusinessPos;
import xdt.pufa.security.PuFaSignUtil;
import xdt.quickpay.hengfeng.util.Bean2QueryStrUtil;
import xdt.quickpay.nbs.common.constant.Constant;
import xdt.quickpay.nbs.common.util.DateUtil;
import xdt.quickpay.nbs.common.util.RandomUtil;
import xdt.quickpay.nbs.common.util.SignatureUtil;
import xdt.quickpay.nbs.common.util.ThertClien;
import xdt.quickpay.taomihui.entity.TaoPayRequestEntity;
import xdt.schedule.ThreadPool;
import xdt.service.IClientCollectionPayService;
import xdt.service.ITmhService;
import xdt.service.IWechatService;
import xdt.util.BeanToMapUtil;
import xdt.util.HttpURLConection;
import xdt.util.HttpUtil;

@Controller
@RequestMapping("tmh")
public class TaomihuiController extends BaseAction {

	private Logger log = LoggerFactory.getLogger(this.getClass());

	@Resource
	private ITmhService tmhService;


	/**
	 * 扫码参数
	 *
	 * @param request
	 * @return
	 * @throws Exception 
	 */
	@ResponseBody
	@RequestMapping("pay")
	public void scanParam(HttpServletRequest request, HttpServletResponse response) throws Exception {
		log.info("支付宝支付进来了");
		Map<String, Object> result = new HashMap<String, Object>();
		logger.info("生成二维码");

		String param = requestClient(request);
		log.info("下游上送参数:{}", param);
		//AlipayParamResponse alipayresponse=new AlipayParamResponse();
		if (!StringUtils.isEmpty(param)) {
			TaoPayRequestEntity entity = gson.fromJson(param, TaoPayRequestEntity.class);
			log.info("json转换扫码反扫对象{}", entity);
			log.info("下游上送签名串{}",entity.getSign());
			//查询商户密钥
			ChannleMerchantConfigKey keyinfo = tmhService.getChannelConfigKey(entity.getMerchantId());
			// ------------------------需要改签名
			String merchantKey = keyinfo.getMerchantkey();
			SignatureUtil signUtil=new SignatureUtil();
			
			Map map=BeanToMapUtil.convertBean(entity);
			
			if(signUtil.checkSign(map, merchantKey, log)){
				log.info("对比签名成功");
				result= tmhService.updateHandle(entity);
				log.info("支付宝支付生成二维码成功");
			}else {
				log.error("签名错误!");
				result.put("respCode", "15");
				result.put("respMsg", "签名错误!");
			}
			
		}else {
			log.error("上送交易参数空!");
			result.put("respCode", "01");
			result.put("respMsg", "fail");
		}

		log.info("返回结果:{}", result);
	}
//	/**
//	 * 订单查询
//	 *
//	 * @param request
//	 * @return
//	 * @throws IOException
//	 * @throws ServletException
//	 */
//	@RequestMapping("orderquery_param")
//	public void orderQuery(HttpServletRequest request, HttpServletResponse response) throws Exception {
//		Map<String, String> result = new HashMap<String, String>();
//		String param = requestClient(request);
//		log.info("下游上送的参数：" + param);
//		if (!StringUtils.isEmpty(param)) {
//			WechatOrderQueryRequest orderQueryRequest = gson.fromJson(param, WechatOrderQueryRequest.class);
//			log.info("下游上送的签名{}", orderQueryRequest.getSign());
//			if (signVerify(orderQueryRequest, orderQueryRequest.getSign())) {
//				// 查询上游商户号和密钥
//				PmsBusinessPos busInfo = wechatService.selectKey(orderQueryRequest.getMerchantId());
//				String key = busInfo.getKek();
//				String service_type = Constant.BRCB_SERVICE_TYPE_ORDERQUERY;
//				String mch_id = "C" + busInfo.getBusinessnum();// Constant.BRCB_MCH_ID;
//				String out_trade_no = orderQueryRequest.getOut_trade_no();
//				String nonce_str = RandomUtil.randomUUID();
//
//				orderQueryRequest = new WechatOrderQueryRequest(key, service_type, mch_id, out_trade_no, nonce_str,
//						log);
//				// 请求
//				WechatOrderQueryResponse orderQueryResponse = wechatService.doOrderQuery(orderQueryRequest, log);
//				result = BeanToMapUtil.convertBean(orderQueryResponse);
//				if (result != null) {
//					log.info("给下游返回的数据{}:", result);
//					try {
//						outString(response, gson.toJson(result));
//					} catch (IOException e) {
//						e.printStackTrace();
//					}
//	
//				} else {
//					log.info("查询订单失败");
//				}
//			} else {
//				log.error("签名错误!");
//				result.put("respCode", "15");
//				result.put("respMsg", "签名错误!");
//			}
//
//		} else {
//			log.error("上送交易参数空!");
//			result.put("respCode", "01");
//			result.put("respMsg", "fail");
//		}
//		try {
//			outString(response, gson.toJson(result));
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
//	}

//	/**
//	 * 和上游交互 支付完成后同步返回支付结果
//	 * 
//	 * @param request
//	 *            requet对象
//	 * @param response
//	 *            response对象
//	 * @param temp
//	 *            银联返回的数据
//	 * @throws Exception
//	 */
//	@RequestMapping("bgPayResult")
//	public void payResult(HttpServletRequest request, HttpServletResponse response, WechatWebPayResponse temp)
//			throws Exception {
//
//		log.info("**********进入异步通知的地址中***********");
//		Map<String, String> result = new HashMap<String, String>();
//		BufferedReader br = new BufferedReader(
//				new InputStreamReader((ServletInputStream) request.getInputStream(), "UTF-8"));
//		String line = null;
//		StringBuffer sb = new StringBuffer();
//		while ((line = br.readLine()) != null) {
//			sb.append(line);
//		}
//		String appMsg = sb.toString();
//		log.info("请求参数：" + appMsg);
//		if (!StringUtils.isEmpty(appMsg)) {
//			response.getWriter().write("SUCCESS");
//			JSONObject ob = JSONObject.fromObject(appMsg);
//			log.info("封装之后的数据:{}", ob);
//			Iterator it = ob.keys();
//			while (it.hasNext()) {
//				String key = (String) it.next();
//				if (key.equals("appid")) {
//					String value = ob.getString(key);
//					log.info("公众号ID:" + "\t" + value);
//					temp.setAppid(value);
//				}
//				if (key.equals("bank_type")) {
//					String value = ob.getString(key);
//					log.info("付款银行:" + "\t" + value);
//					temp.setBank_type(value);
//				}
//				if (key.equals("fee_type")) {
//					String value = ob.getString(key);
//					log.info("货币种类:" + "\t" + value);
//					temp.setFee_type(value);
//				}
//				if (key.equals("return_msg")) {
//					String value = ob.getString(key);
//					log.info("返回信息:" + "\t" + value);
//					temp.setReturn_msg(value);
//				}
//				if (key.equals("attach")) {
//					String value = ob.getString(key);
//					log.info("附加信息:" + "\t" + value);
//					temp.setAttach(value);
//				}
//				if (key.equals("err_code")) {
//					String value = ob.getString(key);
//					log.info("错误码:" + "\t" + value);
//					temp.setErr_code_des(value);
//				}
//				if (key.equals("err_code_des")) {
//					String value = ob.getString(key);
//					log.info("错误信息描述:" + "\t" + value);
//					temp.setErr_code_des(value);
//				}
//				if (key.equals("is_subscribe")) {
//					String value = ob.getString(key);
//					log.info("是否关注公众账号:" + "\t" + value);
//					temp.setIs_subscribe(value);
//				}
//				if (key.equals("nonce_str")) {
//					String value = ob.getString(key);
//					log.info("随机字符串:" + "\t" + value);
//					temp.setNonce_str(value);
//				}
//				if (key.equals("openid")) {
//					String value = ob.getString(key);
//					log.info("用户标识:" + "\t" + value);
//					temp.setOpenid(value);
//				}
//				if (key.equals("out_trade_no")) {
//					String value = ob.getString(key);
//					log.info("商户订单号:" + "\t" + value);
//					temp.setOut_trade_no(value);
//				}
//				if (key.equals("result_code")) {
//					String value = ob.getString(key);
//					log.info("业务结果:" + "\t" + value);
//					temp.setResult_code(value);
//				}
//				if (key.equals("return_code")) {
//					String value = ob.getString(key);
//					log.info("返回状态码:" + "\t" + value);
//					temp.setReturn_code(value);
//				}
//				if (key.equals("time_end")) {
//					String value = ob.getString(key);
//					log.info("支付完成时间:" + "\t" + value);
//					temp.setTime_end(value);
//				}
//				if (key.equals("total_fee")) {
//					String value = ob.getString(key);
//					log.info("订单金额:" + "\t" + value);
//					temp.setTotal_fee(value);
//				}
//				if (key.equals("trade_state")) {
//					String value = ob.getString(key);
//					log.info("交易状态:" + "\t" + value);
//					temp.setTrade_state(value);
//				}
//				if (key.equals("trade_type")) {
//					String value = ob.getString(key);
//					log.info("交易类型:" + "\t" + value);
//					temp.setTrade_type(value);
//				}
//				if (key.equals("wechat_transaction_id")) {
//					String value = ob.getString(key);
//					log.info("微信订单号:" + "\t" + value);
//					temp.setWechat_transaction_id(value);
//				}
//				if (key.equals("alipay_transaction_id")) {
//					String value = ob.getString(key);
//					log.info("支付宝订单号:" + "\t" + value);
//					temp.setAlipay_transaction_id(value);
//				}
//			}
//			log.info("支付后返回的信息：" + temp);
//			// 处理这笔交易 修改订单表中的交易表
//			wechatService.otherInvoke(temp);
//			// 交易id
//			String tranId = temp.getOut_trade_no();
//			// 查询商户上送原始信息
//			OriginalOrderInfo originalInfo = wechatService.getOriginOrderInfo(tranId);
//			log.info("查询上送的原始数据{}:" + originalInfo);
//			Bean2QueryStrUtil bean2Util = new Bean2QueryStrUtil();
//			String path = "";
//			if (originalInfo.getBgUrl().indexOf("?") == -1) {
//
//				path = originalInfo.getBgUrl() + "?" + bean2Util.bean2QueryStr(temp);
//			} else {
//
//				path = originalInfo.getBgUrl() + "&" + bean2Util.bean2QueryStr(temp);
//			}
//			log.info("bgUrl 平台服务器重定向：" + path);
//			HttpUtil http = new HttpUtil();
//			// Map map = BeanToMapUtil.convertBean(temp);
//			// response.sendRedirect(path.replace(" ", ""));
//			String result1 = HttpURLConection.httpURLConnectionPOST(originalInfo.getBgUrl(),
//					bean2Util.bean2QueryStr(temp));
//			log.info("下游返回状态" + result1);
//			if (!"SUCCESS".equals(result1)) {
//				ThreadPool.executor(new ThertClien(temp, originalInfo, bean2Util));
//			}
//			response.getWriter().write("SUCCESS");
//			log.info("向下游 发送数据成功");
//
//		} else {
//			response.getWriter().write("FAIL");
//			log.error("回调的参数为空!");
//			result.put("respCode", "01");
//			result.put("respMsg", "fail");
//			response.getWriter().write("FAIL");
//		}
//
//	}

	@RequestMapping("paySign")
	public void paySigns(HttpServletRequest request, HttpServletResponse response) throws Exception {
		response.setHeader("Access-Control-Allow-Origin", "*");
		response.setContentType("text/html;charset=utf-8");
		String param = requestClient(request);
		log.info("下游上送的参数：" + param);
		TaoPayRequestEntity taomihui = gson.fromJson(param, TaoPayRequestEntity.class);
		//查询商户密钥
		ChannleMerchantConfigKey keyinfo = tmhService
				.getChannelConfigKey(taomihui.getMerchantId());
		// ------------------------需要改签名
		String merchantKey = keyinfo.getMerchantkey();
		Map map = BeanToMapUtil.convertBean(taomihui);
		String sign = SignatureUtil.getSign(map, merchantKey, log);
		log.info("支付签名");
		outString(response, sign);
	}
}
