package xdt.controller.hlb;

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

import xdt.controller.BaseAction;
import xdt.dto.hj.HJRequest;
import xdt.dto.hlb.HLBRequest;
import xdt.dto.hlb.HLBResponse;
import xdt.model.ChannleMerchantConfigKey;
import xdt.model.OriginalOrderInfo;
import xdt.quickpay.hengfeng.util.Bean2QueryStrUtil;
import xdt.schedule.ThreadPool;
import xdt.service.HfQuickPayService;
import xdt.service.IClientCollectionPayService;
import xdt.service.IHLBService;
import xdt.util.HttpURLConection;
import xdt.util.HttpUtil;
import xdt.util.JsdsUtil;
import xdt.util.utils.MD5Utils;
import xdt.util.utils.RequestUtils;
import xdt.util.utils.UtilThread;
@Controller
@RequestMapping("/HLBController")
public class HLBController extends BaseAction{

	private Logger log =Logger.getLogger(this.getClass());
	
	@Resource
	private IClientCollectionPayService clientCollectionPayService;
	@Resource
	private HfQuickPayService payService;
	@Resource
	private IHLBService service;
	/**
	 * 签名
	 * @param hfbRequest
	 * @param response
	 */
	@RequestMapping(value="paySign")
	public void paySign(HLBRequest hlbRequest,HttpServletResponse response){
		
		log.info("合利宝--签名发来的参数："+JSON.toJSONString(hlbRequest));
		
		TreeMap<String, String> result = new TreeMap<String, String>();
		ChannleMerchantConfigKey keyinfo = clientCollectionPayService
				.getChannelConfigKey(hlbRequest.getMerNo());
		// 获取商户秘钥
		String key = keyinfo.getMerchantkey();
		result.putAll(JsdsUtil.beanToMap(hlbRequest));
		String paramSrc = RequestUtils.getParamSrc(result);
		log.info("签名前数据**********合利宝支付:" + paramSrc);
		String md5 = MD5Utils.sign(paramSrc, key, "UTF-8");
		log.info("签名**********合利宝支付:" + md5);
		Map<String, String> map =new HashMap<>();
		map.put("sign", md5);
		try {
			outString(response, JSON.toJSON(map));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	/**
	 * 首次支付下单接口
	 * @param hlbRequest
	 * @param response
	 * @param request
	 */
	@RequestMapping(value="cardPay")
	public void cadePay(HLBRequest hlbRequest,HttpServletResponse response,HttpServletRequest request){
		
		log.info("合利宝网关上传参数"+JSON.toJSONString(hlbRequest));
		
		Map<String, String> result =new HashMap<>();
		ChannleMerchantConfigKey keyinfo = clientCollectionPayService
				.getChannelConfigKey(hlbRequest.getMerNo());
		// 获取商户秘钥
		String key = keyinfo.getMerchantkey();
		TreeMap<String, String> results = new TreeMap<String, String>();
		TreeMap<String, String> results1 = new TreeMap<String, String>();
		results.putAll(JsdsUtil.beanToMap(hlbRequest));
		String paramSrc = RequestUtils.getParamSrc(results);
		log.info("验证签名前字符串"+paramSrc);
		boolean b = MD5Utils.verify(paramSrc, hlbRequest.getSign(), key,
				"UTF-8");
		if(b){
			log.info("签名正确");
			hlbRequest.setUrl(hlbRequest.getNotifyUrl());
			if("cj001".equals(hlbRequest.getType())){//下单
				result  =service.cardPay(hlbRequest, result);
			}else if("cj002".equals(hlbRequest.getType())){//获取短信验证码
				result  =service.sendValidateCode(hlbRequest, result);
			}else if("cj003".equals(hlbRequest.getType())){//确认支付
				result  =service.confirmPay(hlbRequest, result);
			}else if("cj004".equals(hlbRequest.getType())){//鉴权绑卡短信
				result  =service.authenticationCardPay(hlbRequest, result);
			}else if("cj005".equals(hlbRequest.getType())){//鉴权绑卡
				result  =service.authenticationCard(hlbRequest, result);
			}else if("cj006".equals(hlbRequest.getType())){//绑卡支付短信
				result  =service.paymentCardPay(hlbRequest, result);
			}else if("cj007".equals(hlbRequest.getType())){//绑卡支付
				result  =service.paymentCard(hlbRequest, result);
			}else if("cj008".equals(hlbRequest.getType())){//绑结算卡
				result  =service.settlementCardBind(hlbRequest, result);
			}else if("cj009".equals(hlbRequest.getType())||"cj011".equals(hlbRequest.getType())){//cj009借记卡代付cj011信用卡代付
				result  =service.pay(hlbRequest, result);
			}else if("cj010".equals(hlbRequest.getType())){
				result  =service.selectBalance(hlbRequest, result);
			}else {
				result.put("respCode", "06");
				result.put("respMsg","type 不对，或为空！");
			}
			
			log.info("合利宝返回参数"+JSON.toJSON(result));
			
		}else{
			result.put("respCode", "01");
			result.put("respMsg","签名错误！");
		}
		results1.putAll(result);
		String paramSrc1 = RequestUtils.getParamSrc(results1);
		log.info("合利宝---返回数据签名签的数据:"+JSON.toJSONString(paramSrc1));
		String md5 = MD5Utils.sign(paramSrc1, key, "UTF-8");
		result.put("sign", md5);
		
		try {
			outString(response, JSON.toJSONString(result));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	@RequestMapping(value="notifyUrl")
	public void notifyUrl(HLBResponse hlbResponse,HttpServletResponse response){
		try {
		log.info("合利宝异步通知进来了："+JSON.toJSONString(hlbResponse));
		String str ="";
		if(hlbResponse.getRt5_orderId()!=null){
			 str ="success";
			 if("0000".equals(hlbResponse.getRt2_retCode())){
				if("SUCCESS".equals(hlbResponse.getRt9_orderStatus())){
					service.update(hlbResponse.getRt5_orderId(), "0", "00",hlbResponse.getRt10_bindId());
					OriginalOrderInfo originalInfo = null;
					if (hlbResponse.getRt5_orderId() != null && hlbResponse.getRt5_orderId()!= "") {
						originalInfo = this.payService.getOriginOrderInfo(hlbResponse.getRt5_orderId());
					}
					log.info("订单数据:" + JSON.toJSON(originalInfo));
					Bean2QueryStrUtil queryUtil = new Bean2QueryStrUtil();
					log.info("下游的异步地址" + originalInfo.getBgUrl());
					TreeMap<String, String> result = new TreeMap<String, String>();
					ChannleMerchantConfigKey keyinfo = clientCollectionPayService
							.getChannelConfigKey(originalInfo.getPid());
					// 获取商户秘钥
					String key = keyinfo.getMerchantkey();
					result.put("respCode", "00");
					result.put("merNo",originalInfo.getPid());
					result.put("respMsg", "支付成功");
					result.put("orderId", hlbResponse.getRt5_orderId());
					String paramSrc = RequestUtils.getParamSrc(result);
					log.info("签名前数据**********合利宝支付:" + paramSrc);
					String md5 = MD5Utils.sign(paramSrc, key, "UTF-8");
					result.put("sign", md5);
					
					String result1=HttpURLConection.httpURLConnectionPOST(originalInfo.getBgUrl(),queryUtil.bean2QueryStr(result));
					if (!"SUCCESS".equals(result1)) {
						ThreadPool.executor(new UtilThread(originalInfo
								.getBgUrl(), queryUtil
								.bean2QueryStr(result)));
					}
				} 
			 }
		}else{
			str ="fale";
		}
			outString(response, str);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
