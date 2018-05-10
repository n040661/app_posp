package xdt.controller;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONObject;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.alibaba.fastjson.JSON;

import xdt.model.ChannleMerchantConfigKey;
import xdt.model.Jq;
import xdt.service.IClientCollectionPayService;
import xdt.service.IJqService;
import xdt.util.JsdsUtil;
import xdt.util.JsonUtil;
import xdt.util.utils.MD5Utils;
import xdt.util.utils.RequestUtils;

@Controller
@RequestMapping("/jqController")
public class jqController extends BaseAction{
	
	Logger log =Logger.getLogger(this.getClass());
	@Resource
	private IClientCollectionPayService clientCollectionPayService;
	@Resource
	private IJqService jqService;
	@RequestMapping(value="jqParameter")
	public void jqParameter(HttpServletResponse response,Jq jq){
		log.info("jq:"+JSON.toJSON(jq));
		
		TreeMap<String, String> result = new TreeMap<String, String>();
		//获取商户秘钥
		result.putAll(JsdsUtil.beanToMap(jq));
		String paramSrc =RequestUtils.getParamSrc(result);
		
		String md5= RequestUtils.MD5(clientCollectionPayService, jq.getSpid(), paramSrc);
		try {
			outString(response, md5);
		} catch (IOException e) {
			
			e.printStackTrace();
		}
	}
	@SuppressWarnings("unchecked")
	@RequestMapping(value="jq")
	public void jq(HttpServletResponse response,Jq jq){
		response.setHeader("Access-Control-Allow-Origin", "*");
		Map<String, String> results =new HashMap<>();
		String str;
		log.info("支付参数:"+JSON.toJSON(jq));
		ChannleMerchantConfigKey keyinfo = clientCollectionPayService.getChannelConfigKey(jq.getSpid());
		//获取商户秘钥
		String key = keyinfo.getMerchantkey();
		TreeMap<String, String> result = new TreeMap<String, String>();
		result.putAll(JsdsUtil.beanToMap(jq));
		String paramSrc =RequestUtils.getParamSrc(result);
		boolean b =MD5Utils.verify(paramSrc, jq.getSign(), key, "UTF-8");
		if(b){
			log.info("签名正确！");
			//写逻辑
			results =jqService.select(jq);
			log.info("str："+JSON.toJSON(results));
		}else{
			log.info("签名错误！");
			str="{\"retcode\":\"01\",\"retmsg\":\"签名错误！\"}";
			results.put("retcode", "01");
			results.put("retmsg", "签名错误！");
		}
		try {
			outString(response, JSON.toJSON(results));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	@RequestMapping(value="csjq")
	public void csjq(HttpServletResponse response,Jq jq) throws Exception{
		response.setHeader("Access-Control-Allow-Origin", "*");
		Map<String, String> results =new HashMap<>();
		String str;
		log.info("支付参数:"+JSON.toJSON(jq));
		ChannleMerchantConfigKey keyinfo = clientCollectionPayService.getChannelConfigKey(jq.getSpid());
		//获取商户秘钥
		String key = keyinfo.getMerchantkey();
		TreeMap<String, String> result = new TreeMap<String, String>();
		result.putAll(JsdsUtil.beanToMap(jq));
		String paramSrc =RequestUtils.getParamSrc(result);
		boolean b =MD5Utils.verify(paramSrc, jq.getSign(), key, "UTF-8");
		if(b){
			log.info("签名正确！");
			//写逻辑
			results =jqService.selectJq(jq);
			log.info("str："+JSON.toJSON(results));
		}else{
			log.info("签名错误！");
			str="{\"retcode\":\"01\",\"retmsg\":\"签名错误！\"}";
			results.put("retcode", "01");
			results.put("retmsg", "签名错误！");
		}
		try {
			outString(response, JSON.toJSON(results));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
