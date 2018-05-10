package xdt.controller;

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

import xdt.dto.BaseUtil;
import xdt.dto.lhzf.LhzfResponse;
import xdt.dto.pay.PayRequest;
import xdt.dto.ys.YSThread;
import xdt.model.ChannleMerchantConfigKey;
import xdt.model.OriginalOrderInfo;
import xdt.quickpay.hengfeng.util.Bean2QueryStrUtil;
import xdt.schedule.ThreadPool;
import xdt.service.HfQuickPayService;
import xdt.service.IClientCollectionPayService;
import xdt.service.IYSService;
import xdt.util.HttpUtil;
import xdt.util.JsdsUtil;
import xdt.util.utils.MD5Utils;
import xdt.util.utils.RequestUtils;
import xdt.util.utils.UtilThread;

/** 
* @author 作者 E-mail: 
* @version 创建时间：2018年2月2日 上午9:53:01 
* 类说明 
*/
@Controller
@RequestMapping("/YSController")
public class YSController extends BaseAction{

	Logger log = Logger.getLogger(this.getClass());

	@Resource
	private IClientCollectionPayService clientCollectionPayService;
	
	@Resource
	private IYSService service;
	
	@Resource
	private HfQuickPayService payService;
	
	/**
	 * 签名
	 * 
	 * @param hfbRequest
	 * @param response
	 */
	@RequestMapping(value = "paySign")
	public void paySign(PayRequest payRequest, HttpServletResponse response) {

		log.info("--签名发来的参数：" + JSON.toJSONString(payRequest));

		TreeMap<String, String> result = new TreeMap<String, String>();
		ChannleMerchantConfigKey keyinfo = clientCollectionPayService
				.getChannelConfigKey(payRequest.getMerchantId());
		// 获取商户秘钥
		String key = keyinfo.getMerchantkey();
		result.putAll(JsdsUtil.beanToMap(payRequest));
		String paramSrc = RequestUtils.getParamSrc(result);
		log.info("签名前数据**********支付:" + paramSrc);
		String md5 = MD5Utils.sign(paramSrc, key, "UTF-8");
		log.info("签名**********支付:" + md5);
		try {
			outString(response, md5);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	/**
	 * 易宝快捷支付
	 * @param response
	 * @param payRequest
	 */
	@RequestMapping(value="quickPay")
	public void quickPay(HttpServletResponse response,PayRequest payRequest) {
		
		log.info("下游上传参数："+JSON.toJSONString(payRequest));
		Map<String, String> result = new HashMap<>();
		ChannleMerchantConfigKey keyinfo = clientCollectionPayService
				.getChannelConfigKey(payRequest.getMerchantId());
		// 获取商户秘钥
		String key = keyinfo.getMerchantkey();
		TreeMap<String, String> results = new TreeMap<String, String>();
		TreeMap<String, String> results1 = new TreeMap<String, String>();
		results.putAll(JsdsUtil.beanToMap(payRequest));
		String paramSrc = RequestUtils.getParamSrc(results);
		log.info("验证签名前字符串" + paramSrc);
		boolean b = MD5Utils
				.verify(paramSrc, payRequest.getSign(), key, "UTF-8");
		if (b) {
			log.info("签名正确");
			log.info("签名正确");
			payRequest.setUrl(payRequest.getNotifyUrl());
			payRequest.setReUrl(payRequest.getReturnUrl());
			if("cj001".equals(payRequest.getType())) {
				result =service.register(payRequest, result);
			}else if("cj002".equals(payRequest.getType())) {
				result =service.update(payRequest, result);
			}else if("cj003".equals(payRequest.getType())) {
				result = service.quickPay(payRequest, result);
			}else if("cj004".equals(payRequest.getType())) {
				result = service.openC2(payRequest, result);
			}else if("cj005".equals(payRequest.getType())) {
				result = service.updateC2(payRequest, result);
			}else if("cj006".equals(payRequest.getType())) {
				result = service.bindingCard(payRequest, result);
			}else if("cj007".equals(payRequest.getType())) {
				result = service.quickPay(payRequest, result);
			}else if("cj008".equals(payRequest.getType())) {
				result = service.confirmPay(payRequest, result);
			}
			
			log.info("返回的参数:" + JSON.toJSON(result));
		} else {
			result.put("respCode", "0001");
			result.put("respMsg", "签名错误！");
		}
		results1.putAll(result);
		String paramSrc1 = RequestUtils.getParamSrc(results1);
		log.info("---返回数据签名签的数据:" + JSON.toJSONString(paramSrc1));
		String md5 = MD5Utils.sign(paramSrc1, key, "UTF-8");
		result.put("sign", md5);
		try {
			if("00".equals(result.get("respCode"))) {
				if("00".equals(result.get("code"))) {
					if("cj003".equals(payRequest.getType())){
						outString(response, result.get("html"));
					}else {
						outString(response, JSON.toJSON(result));
					}
				}else {
					outString(response, JSON.toJSON(result));
				}
			}else {
				outString(response, JSON.toJSON(result));
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	@RequestMapping(value="notifyUrl")
	public void notifyUrl(HttpServletRequest request, HttpServletResponse response){
		try {
			log.info("易生异步通知来了");
			String sp_id=request.getParameter("sp_id");
			String mch_id=request.getParameter("mch_id");
			String out_trade_no=request.getParameter("out_trade_no");
			String sys_trade_no=request.getParameter("sys_trade_no");
			String pay_state=request.getParameter("pay_state");//B2支付状态
			String trade_state=request.getParameter("trade_state");//C2支付状态
			String daifu_state=request.getParameter("daifu_state");
			String total_fee=request.getParameter("total_fee");
			String daifu_fee=request.getParameter("daifu_fee");
			String sign=request.getParameter("sign");
			System.out.println("sp_id:"+sp_id);
			System.out.println("mch_id:"+mch_id);
			System.out.println("out_trade_no:"+out_trade_no);
			System.out.println("sys_trade_no:"+sys_trade_no);
			System.out.println("pay_state:"+pay_state);
			System.out.println("trade_state:"+trade_state);
			System.out.println("daifu_state:"+daifu_state);
			System.out.println("total_fee:"+total_fee);
			System.out.println("daifu_fee:"+daifu_fee);
			System.out.println("sign:"+sign);
			
			log.info("易生返回订单号："+JSON.toJSONString(out_trade_no));
			TreeMap<String, String> result = new TreeMap<String, String>();
			Map<String, String> map =new HashMap<>();
			String str;
			if (out_trade_no !=null) {
				str = "SUCCESS";
				OriginalOrderInfo originalInfo = null;
				if (out_trade_no != null && out_trade_no!= "") {
					originalInfo = this.payService.getOriginOrderInfo(out_trade_no);
				}else {
					log.info("未收到订单号");
				}
				log.info("订单数据:" + JSON.toJSON(originalInfo));
				log.info("下游的异步地址" + originalInfo.getBgUrl());
				if(!"".equals(trade_state)&& trade_state!=null) {
					service.updateOrdeId(trade_state, originalInfo);
				}else if(!"".equals(pay_state)&& pay_state!=null){
					service.updateOrdeId(pay_state, originalInfo);
				}
				
				String state="200";
				PayRequest payRequest=new PayRequest();
				payRequest.setOrderId(out_trade_no);
				payRequest.setMerchantId(originalInfo.getPid());
				payRequest.setAmount(daifu_fee);
				String daifuState="200";
				String payState="200";
				if("SUCCESS".equals(pay_state)||"SUCCESS".equals(trade_state)) {
					payState="00";
				}else if("PAYERROR".equals(pay_state)||"CLOSED".equals(pay_state)){
					payState="01";
				}
				if("SUCCESS".equals(daifu_state)) {
					map.put("respMsg", "代付成功");
					state="00";
					daifuState="00";
				}else if("FAIL".equals(daifu_state)) {
					state="01";
					map.put("respMsg", "代付失败，无效结算银行卡信息");
					daifuState="01";
				}
				result.put("orderId", out_trade_no);
				result.put("merchantId", originalInfo.getPid());
				result.put("totalFee", total_fee);//支付金额
				result.put("daifuFee", daifu_fee);//代付金额
				result.put("payCode", payState);//支付状态
				result.put("daifuCode", daifuState);//代付状态
				service.add(payRequest, map, state);
				if("200".equals(state)) {
					ThreadPool.executor(new YSThread(service, out_trade_no, originalInfo.getPid()));
				}
				//和下面的签名
				//---------------------------------------------------
				
				ChannleMerchantConfigKey keyinfo = clientCollectionPayService
						.getChannelConfigKey(originalInfo.getPid());
				// 获取商户秘钥
				String key = keyinfo.getMerchantkey();
				String paramSrc = RequestUtils.getParamSrc(result);
				log.info("签名前数据**********蓝海支付:" + paramSrc);
				String md5 = MD5Utils.sign(paramSrc, key, "UTF-8");
				result.put("sign", md5);
				
				String result1=HttpUtil.sendPost(originalInfo.getBgUrl()+"?"+RequestUtils.getParamSrc(result));
				log.info("下游返回状态" + result1);
				if (!"SUCCESS".equals(result1)) {
					ThreadPool.executor(new UtilThread(originalInfo
							.getBgUrl(), RequestUtils.getParamSrc(result)));
				}
			} else {
				str = "FAIL";
			}
			outString(response, str);
		} catch (Exception e) {
			log.info("易生异步回调异常:" + e);
			e.printStackTrace();
		}
	}
	
	@RequestMapping(value="returnUrl")
	public void returnUrl(HttpServletRequest request, HttpServletResponse response){
		try {
			log.info("易生同步通知来了");
			String orderNo=request.getParameter("orderNo");
			String hpMerCode=request.getParameter("hpMerCode");
			String transDate=request.getParameter("transDate");
			String transStatus=request.getParameter("transStatus");
			String transAmount=request.getParameter("transAmount");
			String actualAmount=request.getParameter("actualAmount");
			String transSeq=request.getParameter("transSeq");
			String statusCode=request.getParameter("statusCode");
			String statusMsg=request.getParameter("statusMsg");
			System.out.println("orderNo:"+orderNo);
			System.out.println("hpMerCode:"+hpMerCode);
			System.out.println("transDate:"+transDate);
			System.out.println("transStatus:"+transStatus);
			System.out.println("transAmount:"+transAmount);
			System.out.println("actualAmount:"+actualAmount);
			System.out.println("transSeq:"+transSeq);
			System.out.println("statusCode:"+statusCode);
			System.out.println("statusMsg:"+statusMsg);
			
			
			log.info("易生返回订单号："+JSON.toJSONString(transSeq));
			TreeMap<String, String> result = new TreeMap<String, String>();
			String str;
			if (transSeq!=null) {
				str = "SUCCESS";
				OriginalOrderInfo originalInfo = null;
				if (transSeq != null && transSeq!= "") {
					originalInfo = this.payService.getOriginOrderInfo(transSeq);
				}
				log.info("订单数据:" + JSON.toJSON(originalInfo));
				log.info("下游的异步地址" + originalInfo.getPageUrl());
				
				result.put("respCode", "00");
				result.put("respMsg", statusMsg);
				result.put("orderId", transSeq);
				result.put("amount", transAmount);
				result.put("code", transStatus);//交易状态 00:成功、其他:失败
				result.put("merchantCode", hpMerCode);
				result.put("transDate", transDate);
				result.put("merchantId", originalInfo.getPid());
				//和下面的签名
				//---------------------------------------------------
				
				ChannleMerchantConfigKey keyinfo = clientCollectionPayService
						.getChannelConfigKey(originalInfo.getPid());
				// 获取商户秘钥
				String key = keyinfo.getMerchantkey();
				String paramSrc = RequestUtils.getParamSrc(result);
				log.info("签名前数据**********蓝海支付:" + paramSrc);
				String md5 = MD5Utils.sign(paramSrc, key, "UTF-8");
				result.put("sign", md5);
				
				String result1=HttpUtil.sendPost(originalInfo.getPageUrl()+"?"+RequestUtils.getParamSrc(result));
				log.info("下游返回状态" + result1);
				if (!"SUCCESS".equals(result1)) {
					ThreadPool.executor(new UtilThread(originalInfo
							.getPageUrl(), RequestUtils.getParamSrc(result)));
				}
			} else {
				str = "FAIL";
			}
			outString(response, str);
		} catch (Exception e) {
			log.info("易生异步回调异常:" + e);
			e.printStackTrace();
		}
	}
	public static void main(String[] args) {
		String str="sp_id=C-001&mch_id=YBBH0000015&out_trade_no=1517898317403&sys_trade_no=YB180206142755466370&pay_state=SUCCESS&daifu_state=PROCESSING&total_fee=10000&daifu_fee=9604&sign=AA4187F43026F2B507B032A9CE5A12E5F49E61E51A199A74D03BCCDA678F84F6";
		
		
		String result1=HttpUtil.sendPost(BaseUtil.url+"/YSController/notifyUrl.action"+"?"+str);
		
		System.out.println(result1);
	}
	
}
