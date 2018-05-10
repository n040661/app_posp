package xdt.controller;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.alibaba.fastjson.JSON;

import xdt.dto.lhzf.LhzfRequset;
import xdt.model.ChannleMerchantConfigKey;
import xdt.service.IClientCollectionPayService;
import xdt.service.ILhzfService;
import xdt.util.JsdsUtil;
import xdt.util.utils.MD5Utils;
import xdt.util.utils.RequestUtils;

/** 
* @author 作者 E-mail: 
* @version 创建时间：2018年1月8日 下午2:43:49 
* 类说明 
*/
@Controller
@RequestMapping("/YPLController")
public class YPLController extends BaseAction{

	Logger log =Logger.getLogger(this.getClass());
	
	@Resource
	private IClientCollectionPayService clientCollectionPayService;
	@Resource 
	private ILhzfService iLhzfService;
	@RequestMapping(value="quick")
	public void quick(LhzfRequset lhzfRequset,HttpServletResponse response) {
		log.info("易票联快捷请求参数"+JSON.toJSON(lhzfRequset));
		try {
		Map<String, String> results =new HashMap<>();
		log.info("支付参数:"+JSON.toJSON(lhzfRequset));
		ChannleMerchantConfigKey keyinfo = clientCollectionPayService.getChannelConfigKey(lhzfRequset.getMerNo());
		//获取商户秘钥
		String key = keyinfo.getMerchantkey();
		TreeMap<String, String> result = new TreeMap<String, String>();
		result.putAll(JsdsUtil.beanToMap(lhzfRequset));
		String paramSrc =RequestUtils.getParamSrc(result);
		log.info("蓝海---签名之前参数");
		boolean b =MD5Utils.verify(paramSrc, lhzfRequset.getSign(), key, "UTF-8");
		if(b){
			log.info("签名正确");
			lhzfRequset.setUrl(lhzfRequset.getNotifyUrl());
			lhzfRequset.setReUrl(lhzfRequset.getReturnUrl());
			results = iLhzfService.quickAgentPayH5(lhzfRequset, results);
		}else{
			log.info("签名错误");
			results.put("respCode", "01");
			results.put("respMsg","签名错误！");
		}
			outString(response, JSON.toJSONString(results));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
