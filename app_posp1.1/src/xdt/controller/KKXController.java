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

import xdt.dto.kkx.KKXRequest;
import xdt.dto.kkx.KKXResponse;
import xdt.dto.sxf.SXFRequest;
import xdt.dto.sxf.SXFResponse;
import xdt.model.ChannleMerchantConfigKey;
import xdt.model.OriginalOrderInfo;
import xdt.quickpay.hengfeng.util.Bean2QueryStrUtil;
import xdt.schedule.ThreadPool;
import xdt.service.HfQuickPayService;
import xdt.service.IClientCollectionPayService;
import xdt.service.IClientH5Service;
import xdt.service.IKKXService;
import xdt.util.HttpURLConection;
import xdt.util.JsdsUtil;
import xdt.util.utils.MD5Utils;
import xdt.util.utils.RequestUtils;
import xdt.util.utils.UtilThread;
@Controller
@RequestMapping("/KKXController")
public class KKXController extends BaseAction{

	
	Logger log =Logger.getLogger(this.getClass());
	
	@Resource
	private IClientCollectionPayService clientCollectionPayService;
	
	@Resource
	private HfQuickPayService payService;
	
	@Resource 
	private  IClientH5Service ClientH5ServiceImpl;
	
	@Resource
	private IKKXService kkxServiceImpl;
	
	@RequestMapping(value="cardPayParameter")
	public void cardPayParameter(HttpServletResponse response, KKXRequest kkxRequest){
		
		log.info("网关支付获取参数"+JSON.toJSON(kkxRequest));
		TreeMap<String, String> result = new TreeMap<String, String>();
		ChannleMerchantConfigKey keyinfo = clientCollectionPayService.getChannelConfigKey(kkxRequest.getAccount());
		//获取商户秘钥
		String key = keyinfo.getMerchantkey();
		result.putAll(JsdsUtil.beanToMap(kkxRequest));
		String paramSrc =RequestUtils.getParamSrc(result);
		log.info("签名前数据:"+paramSrc);
		String md5 =MD5Utils.sign(paramSrc, key, "UTF-8");
		log.info("签名:"+md5);
		try {
			outString(response, md5);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	@RequestMapping(value="pay")
	public void pay(HttpServletResponse response, KKXRequest kkxRequest){
		
		log.info("下游传的数据:"+JSON.toJSON(kkxRequest));
		
		Map<String, String> results =new HashMap<>();
		ChannleMerchantConfigKey keyinfo = clientCollectionPayService.getChannelConfigKey(kkxRequest.getAccount());
		//获取商户秘钥
		String key = keyinfo.getMerchantkey();
		TreeMap<String, String> result = new TreeMap<String, String>();
		result.putAll(JsdsUtil.beanToMap(kkxRequest));
		String paramSrc =RequestUtils.getParamSrc(result);
		boolean b =MD5Utils.verify(paramSrc, kkxRequest.getSign(), key, "UTF-8");
		if(b){
			log.info("签名成功");
			//写逻辑
			kkxRequest.setUrl(kkxRequest.getNotifyurl());
			kkxRequest.setReUrl(kkxRequest.getNotifyurl());
			results =kkxServiceImpl.pay(kkxRequest, results);
			log.info("results"+results);
		}else{
			log.info("签名错误！");
			results.put("retcode", "1");
			results.put("retmsg", "签名错误");
		}
		
	}
	
	/**
	 * 异步通知返回的地址
	 * @param response
	 * @param cardPayApplyＲesponse
	 * @throws Exception 
	 */
	@RequestMapping(value = "notifyUrl")
	public void notifyUrl(HttpServletResponse response,HttpServletRequest request,KKXResponse kkxResponse) throws Exception{
		try {
			
			log.info("异步通知来了");
			log.info("异步通知数据:"+kkxResponse);
			String str ;
			Map<String, String> map =new HashMap<>();
			if(kkxResponse.getOrder_id() !=null &&kkxResponse.getOrder_id() !=""){
				str ="SUCCESS"; 
				outString(response, JSON.toJSON(str));
				
				OriginalOrderInfo originalInfo = null;
				if(kkxResponse.getOrder_id()!=null&& kkxResponse.getOrder_id()!=""){
					originalInfo = this.payService.getOriginOrderInfo(kkxResponse.getOrder_id());
				}
				 log.info("订单数据:"+JSON.toJSON(originalInfo));
				    Bean2QueryStrUtil queryUtil = new Bean2QueryStrUtil();
				    log.info("下游的异步地址" + originalInfo.getBgUrl());
				    log.info("天付宝异步回调参数:"+JSON.toJSON(kkxResponse));
					if("000000".equals(kkxResponse.getCode())){
						//后面要改这个方法没有支付中这个判断
						//sxfServiceImpl.update(sxfResponse);
						
					}else{
						log.info("交易错误码:"+kkxResponse.getCode()+",错误信息:"+kkxResponse.getMsg());
					}
				 String result1=HttpURLConection.httpURLConnectionPOST(originalInfo.getBgUrl(),queryUtil.bean2QueryStr(kkxResponse));
				log.info("下游返回状态"+result1);
				if(!"SUCCESS".equals(result1)){
					ThreadPool.executor(new UtilThread(originalInfo.getBgUrl(),queryUtil.bean2QueryStr(kkxResponse)));
				}		
					
			}else{
				str ="FAIL";
				outString(response, JSON.toJSON(str));
			}
		} catch (IOException e) {
			log.info("天付宝异步回调异常:"+e);
			e.printStackTrace();
		}
		
		
	}
	
		/**
		 * 同步返回的地址
		 * @param response
		 * @param cardPayApplyＲesponse
		 */
		@RequestMapping(value = "returnUrl")
		public void returnUrl(HttpServletResponse response,SXFResponse sxfResponse){
			
			log.info("同步数据来了！！");
		}
}
