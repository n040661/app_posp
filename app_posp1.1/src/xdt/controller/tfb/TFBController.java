package xdt.controller.tfb;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URLEncoder;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.TimeZone;
import java.util.TreeMap;

import javax.annotation.Resource;
import javax.servlet.ServletException;
import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.alibaba.fastjson.JSON;

import xdt.controller.BaseAction;
import xdt.dto.tfb.CardPayApplyRequest;
import xdt.dto.tfb.PayRequest;
import xdt.dto.tfb.TFBConfig;
import xdt.dto.tfb.WxPayApplyRequest;
import xdt.dto.tfb.WxPayApplyResponse;
import xdt.model.ChannleMerchantConfigKey;
import xdt.model.OriginalOrderInfo;
import xdt.quickpay.hengfeng.util.Bean2QueryStrUtil;
import xdt.schedule.ThreadPool;
import xdt.service.HfQuickPayService;
import xdt.service.IClientCollectionPayService;
import xdt.service.IClientH5Service;
import xdt.service.ITFBService;
import xdt.util.HttpURLConection;
import xdt.util.JsdsUtil;
import xdt.util.utils.MD5Utils;
import xdt.util.utils.RequestUtils;
import xdt.util.utils.UtilThread;

@Controller
@RequestMapping("/TFBController")
public class TFBController extends BaseAction {

	Logger log =Logger.getLogger(this.getClass());
	@Resource
	private IClientCollectionPayService clientCollectionPayService;
	@Resource
	private ITFBService itfbService;
	
	@Resource
	private HfQuickPayService payService;
	
	@Resource 
	private  IClientH5Service ClientH5ServiceImpl;
	/**
	 * 网关支付获取参数签名
	 * @param response
	 * @param cardPayApplyＲequest
	 */
	@RequestMapping(value = "cardpayParameter")
	public void cardpayParameter(HttpServletResponse response,HttpServletRequest request,
			CardPayApplyRequest cardPayApplyＲequest) {
		log.info("网关支付获取参数"+JSON.toJSON(cardPayApplyＲequest));
		TreeMap<String, String> result = new TreeMap<String, String>();
		ChannleMerchantConfigKey keyinfo = clientCollectionPayService.getChannelConfigKey(cardPayApplyＲequest.getSpid());
		//获取商户秘钥
		String key = keyinfo.getMerchantkey();
		result.putAll(JsdsUtil.beanToMap(cardPayApplyＲequest));
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
		
	/**
	 * 网关支付接口
	 * @param response
	 * @param cardPayApplyＲequest
	 */
	@RequestMapping(value = "cardpayApply")
	public void cardpayApply(HttpServletResponse response,
			CardPayApplyRequest cardPayApplyＲequest) {
		Map<String, String> results =new HashMap<>();
		log.info("支付参数:"+JSON.toJSON(cardPayApplyＲequest));
		ChannleMerchantConfigKey keyinfo = clientCollectionPayService.getChannelConfigKey(cardPayApplyＲequest.getSpid());
		//获取商户秘钥
		String key = keyinfo.getMerchantkey();
		TreeMap<String, String> result = new TreeMap<String, String>();
		result.putAll(JsdsUtil.beanToMap(cardPayApplyＲequest));
		String paramSrc =RequestUtils.getParamSrc(result);
		boolean b =MD5Utils.verify(paramSrc, cardPayApplyＲequest.getSign(), key, "UTF-8");
		if(b){
			log.info("签名成功");
			//写逻辑
			;
			cardPayApplyＲequest.setUrl(cardPayApplyＲequest.getNotify_url());
			cardPayApplyＲequest.setReUrl(cardPayApplyＲequest.getReturn_url()!=null&&cardPayApplyＲequest.getReturn_url()!=""?cardPayApplyＲequest.getReturn_url():"");
			results =itfbService.cardPayApply( cardPayApplyＲequest, results);
			
			try {
				String str =results.get("str");
				String url =new File(this.getClass().getResource("/").getPath()).getParentFile().getParentFile().getCanonicalPath()+"/ky"+TFBConfig.GC_PUBLIC_KEY_PATH;
		         log.info("url:"+url);
		         log.info("str:"+str);
		         results.put("str", TFBConfig.cardPayApplyApi + "?cipher_data="
		 				+ URLEncoder.encode(str, "GBK"));
			} catch (Exception e) {
				// TODO: handle exception
			}
		}else{
			log.info("签名错误！");
			results.put("retcode", "1");
			results.put("retmsg", "签名错误");
		}
		
		try {
			outString(response, JSON.toJSON(results));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	/**
	 * 微信/qq钱包支付参数
	 * @param response
	 * @param request
	 * @param payApplyRequest
	 */
	@RequestMapping(value="wxpayParameter")
	public void wxpayParameter(HttpServletResponse response,HttpServletRequest request,WxPayApplyRequest payApplyRequest) {
		log.info("网关支付获取参数"+JSON.toJSON(payApplyRequest));
		TreeMap<String, String> result = new TreeMap<String, String>();
		ChannleMerchantConfigKey keyinfo = clientCollectionPayService.getChannelConfigKey(payApplyRequest.getSpid());
		//获取商户秘钥
		String key = keyinfo.getMerchantkey();
		result.putAll(JsdsUtil.beanToMap(payApplyRequest));
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
	/**
	 * 微信/qq钱包支付
	 * @param response
	 * @param payApplyRequest
	 */
	@RequestMapping(value="wxPayApply")
	public void wxPayApply(HttpServletResponse response,WxPayApplyRequest payApplyRequest){
		
		Map<String, String> results =new HashMap<>();
		log.info("支付参数:"+JSON.toJSON(payApplyRequest));
		ChannleMerchantConfigKey keyinfo = clientCollectionPayService.getChannelConfigKey(payApplyRequest.getSpid());
		//获取商户秘钥
		String key = keyinfo.getMerchantkey();
		TreeMap<String, String> result = new TreeMap<String, String>();
		result.putAll(JsdsUtil.beanToMap(payApplyRequest));
		String paramSrc =RequestUtils.getParamSrc(result);
		boolean b =MD5Utils.verify(paramSrc, payApplyRequest.getSign(), key, "UTF-8");
		if(b){
			log.info("签名成功");
			payApplyRequest.setUrl(payApplyRequest.getNotify_url());
			payApplyRequest.setReUrl(payApplyRequest.getPay_show_url());
			results =itfbService.wxPayApply(payApplyRequest, results);
			
			//写逻辑
		}else{
			log.info("签名错误！");
			results.put("retcode", "1");
			results.put("retmsg", "签名错误");
		}
		try {
			outString(response, JSON.toJSON(results));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	/**
	 * 天下付代付参数
	 * @param payRequest
	 * @param response
	 */
	@RequestMapping(value="payApplyParameter")
	public void payParameter(PayRequest payRequest,HttpServletResponse response){
		log.info("代付获取参数"+JSON.toJSON(payRequest));
		TreeMap<String, String> result = new TreeMap<String, String>();
		ChannleMerchantConfigKey keyinfo = clientCollectionPayService.getChannelConfigKey(payRequest.getSpid());
		//获取商户秘钥
		String key = keyinfo.getMerchantkey();
		result.putAll(JsdsUtil.beanToMap(payRequest));
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
	/**
	 * 天下付单笔代付
	 * @param payRequest
	 * @param response
	 */
	@RequestMapping(value="payApply")
	public void payApply(PayRequest payRequest,HttpServletResponse response){
		Map<String, String> results =new HashMap<>();
		log.info("支付参数:"+JSON.toJSON(payRequest));
		ChannleMerchantConfigKey keyinfo = clientCollectionPayService.getChannelConfigKey(payRequest.getSpid());
		//获取商户秘钥
		String key = keyinfo.getMerchantkey();
		TreeMap<String, String> result = new TreeMap<String, String>();
		result.putAll(JsdsUtil.beanToMap(payRequest));
		String paramSrc =RequestUtils.getParamSrc(result);
		boolean b =MD5Utils.verify(paramSrc, payRequest.getSign(), key, "UTF-8");
		if(b){
			log.info("签名成功");
			results =itfbService.payApply(payRequest, results);
			
		}else{
			log.info("签名错误！");
			results.put("retcode", "1");
			results.put("retmsg", "签名错误");
		}
		try {
			outString(response, results);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	/**
	 * 网关查询
	 * @param response
	 * @param cardPayApplyRequest
	 */
	@RequestMapping(value="cardSelect")
	public void cardSelect(HttpServletResponse response,CardPayApplyRequest cardPayApplyRequest){
		log.info("下游查询数据:"+JSON.toJSON(cardPayApplyRequest));
		Map<String, String> map =new HashMap<>();
		TreeMap<String, String> result = new TreeMap<String, String>();
		map =itfbService.cardSelect(cardPayApplyRequest, result);
		log.info("查询返回参数"+JSON.toJSON(map));
		try {
			outString(response, JSON.toJSON(map));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	/**
	 * 微信/qq支付订单查询
	 * @param spid
	 * @param sp_billno
	 * @return
	 */
	@RequestMapping(value="select")
	public void select(HttpServletResponse response,WxPayApplyRequest payApplyRequest){
		log.info("下游查询数据:"+JSON.toJSON(payApplyRequest));
		Map<String, Object> map =new HashMap<>();
		TreeMap<String, String> result = new TreeMap<String, String>();
		
		map =itfbService.paySelect(payApplyRequest, map);
		log.info("查询返回参数"+JSON.toJSON(map));
		try {
			outString(response, JSON.toJSON(map));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	/**
	 * 单笔代付查询
	 * @param response
	 * @param payRequest
	 */
	@RequestMapping(value="paySelect")
	public void paySelect(HttpServletResponse response,PayRequest payRequest){
		log.info("下游查询数据:"+JSON.toJSON(payRequest));
		Map<String, String> map =new HashMap<>();
		TreeMap<String, String> result = new TreeMap<String, String>();
		map =itfbService.PaySelect(payRequest, result);
		log.info("查询返回参数"+JSON.toJSON(map));
		try {
			outString(response, JSON.toJSON(map));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	/**
	 * 异步通知返回的地址
	 * @param response
	 * @param cardPayApplyＲesponse
	 * @throws Exception 
	 */
	@RequestMapping(value = "notifyUrl")
	public void notifyUrl(HttpServletResponse response,HttpServletRequest request,WxPayApplyResponse wxPayApplyResponse) throws Exception{
		try {
			log.info("异步通知来了");
			Map<String, String> map =new HashMap<>();
			String encoding= request.getParameter("encoding");
			log.info("encoding："+encoding);
			if(wxPayApplyResponse !=null){
				log.info("Retmsg:"+wxPayApplyResponse.getRetmsg());
				wxPayApplyResponse.setRetmsg(new String(wxPayApplyResponse.getRetmsg().getBytes("ISO-8859-1"),"GBK"));
				
				map.put("retcode", "SUCCESS");
				outString(response, JSON.toJSON(map));
				if(wxPayApplyResponse.getCipher_data()!=null&&wxPayApplyResponse.getCipher_data()!=""){
					log.info("Cipher_data："+wxPayApplyResponse.getCipher_data());
					String privateKey =new File(this.getClass().getResource("/").getPath()).getParentFile().getParentFile().getCanonicalPath()+"/ky"+TFBConfig.PRIVATE_KEY_PATH;
			 		String responseData = RequestUtils.decryptResponseData(wxPayApplyResponse.getCipher_data(), privateKey);
			 		log.info("responseData："+responseData);
			 		HashMap<String, String> maps = RequestUtils.parseString(responseData);
			 		log.info("maps："+JSON.toJSON(maps));
			 		wxPayApplyResponse.setCur_type(maps.get("cur_type")+"");
			 		wxPayApplyResponse.setMoney(maps.get("money")+"");
			 		wxPayApplyResponse.setPay_type(maps.get("pay_type")+"");
			 		wxPayApplyResponse.setResult(maps.get("result")+"");
			 		wxPayApplyResponse.setSpbillno(maps.get("spbillno")+"");
			 		wxPayApplyResponse.setUser_type(maps.get("user_type")+"");
			 		log.info("解析完的:wxPayApplyResponse："+JSON.toJSON(wxPayApplyResponse));
				}
				log.info("来了111");
				
				itfbService.update(wxPayApplyResponse);
				
				String path = "";
				 OriginalOrderInfo originalInfo = null;
				if(wxPayApplyResponse.getSp_billno()!=null&&wxPayApplyResponse.getSp_billno()!=""){
					originalInfo = this.payService.getOriginOrderInfo(wxPayApplyResponse.getSp_billno());
				}else if(wxPayApplyResponse.getSpbillno()!=null&&wxPayApplyResponse.getSpbillno()!=""){
					originalInfo = this.payService.getOriginOrderInfo(wxPayApplyResponse.getSpbillno());
				}
			    log.info("来了222");
			    log.info("订单数据:"+JSON.toJSON(originalInfo));
			    Bean2QueryStrUtil queryUtil = new Bean2QueryStrUtil();
			    log.info("下游的异步地址" + originalInfo.getBgUrl());
			    log.info("天付宝异步回调参数:"+JSON.toJSON(wxPayApplyResponse));
				if("00".equals(wxPayApplyResponse.getRetcode())){
					wxPayApplyResponse.setRetmsg("操作成功");
					Calendar cal1 = Calendar.getInstance();
					TimeZone.setDefault(TimeZone.getTimeZone("GMT+8:00"));
					java.text.SimpleDateFormat sdf = new SimpleDateFormat("kk:mm:ss");
					if (sdf.parse(sdf.format(cal1.getTime())).getTime() > sdf.parse("22:00:00").getTime()
							&& sdf.parse(sdf.format(cal1.getTime())).getTime() < sdf.parse("23:59:59").getTime()) {
						log.info("时间不在正常入金时间内!");
					}else{
						if("1".equals(wxPayApplyResponse.getTran_state())||"1".equals(wxPayApplyResponse.getResult())){
							log.info("成功，修改状态！");
						      int i = this.itfbService.UpdatePmsMerchantInfo(originalInfo);
						      this.log.info("修改状态失败0成功1：" + i);
						}
				    }
				}else{
					wxPayApplyResponse.setRetmsg("操作失败");
					log.info("交易错误码:"+wxPayApplyResponse.getRetcode()+",错误信息:"+wxPayApplyResponse.getRetmsg());
				}
				wxPayApplyResponse.setItem_name("");
				
			    if (originalInfo.getBgUrl() != null) {
			      if (originalInfo.getBgUrl().indexOf("?") != -1) {
			        path = originalInfo.getBgUrl().replaceAll(",", "&") + "&" + queryUtil.bean2QueryStr(wxPayApplyResponse);
			        this.log.info("给下游返回的地址1" + path);
			      } else {
			        path = originalInfo.getBgUrl() + "?" + queryUtil.bean2QueryStr(wxPayApplyResponse);
			        this.log.info("给下游返回的地址2" + path);
			      }
			     // this.log.info("demo 重定向：" + path);
			      
			      String result1=HttpURLConection.httpURLConnectionPOST(originalInfo.getBgUrl(),queryUtil.bean2QueryStr(wxPayApplyResponse));
					log.info("下游返回状态"+result1);
					if(!"SUCCESS".equals(result1)){
						ThreadPool.executor(new UtilThread(originalInfo.getBgUrl(),queryUtil.bean2QueryStr(wxPayApplyResponse)));
					}
					
			     // response.sendRedirect(path.replace(" ", ""));
			     // this.log.info(" 重定向成功！");
			    }
				
				
			}else{
				map.put("retcode", "FAIL");
				outString(response, JSON.toJSON(map));
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
	public void returnUrl(HttpServletResponse response,WxPayApplyResponse wxPayApplyResponse){
		
		log.info("同步数据来了！！");
	}
}
