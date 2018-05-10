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

import xdt.dto.tfb.WxPayApplyRequest;
import xdt.model.ChannleMerchantConfigKey;
import xdt.service.IClientCollectionPayService;
import xdt.service.ITFBService;
import xdt.util.JsdsUtil;
import xdt.util.utils.MD5Utils;
import xdt.util.utils.RequestUtils;

@Controller
@RequestMapping("/PublicSelectController")
public class PublicSelectController extends BaseAction{
	
	Logger log =Logger.getLogger(this.getClass());
	@Resource
	private IClientCollectionPayService clientCollectionPayService;
	@Resource
	private ITFBService itfbService;
	
	@RequestMapping(value="paySelect")
	public void paySelect(HttpServletResponse response,WxPayApplyRequest payApplyRequest){
		Map<String, Object> results =new HashMap<>();
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
			results =itfbService.paySelect(payApplyRequest, results);
			log.info("results："+JSON.toJSON(results));
			//写逻辑
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
	
	
	
	
}
