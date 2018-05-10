package xdt.controller;

import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import xdt.model.ChannleMerchantConfigKey;
import xdt.quickpay.wzf.WzfSignUtil;
import xdt.quickpay.ysb.model.YsbRequsetEntity;
import xdt.quickpay.ysb.util.SignUtil;
import xdt.quickpay.ysb.util.YsbSignUtil;
import xdt.service.IWZFPayService;
import xdt.service.IDaiKouService;

/**
 * @ClassName: WZFController
 * @Description: 沃支付
 * @author 尚延超
 * @date 2017年11月10日
 * 
 */
@Controller
@RequestMapping("wquick")
public class WZFController extends BaseAction {

	private Logger log = LoggerFactory.getLogger(this.getClass());
	
	@Resource
	private IWZFPayService wService;
	
	/**
	 * 下游接入 demo
	 * 
	 * @param param
	 * @param request
	 * @param response
	 * @return 
	 * @throws Exception
	 */
	@RequestMapping(value = "wzfAgreeSign")
	public String ysbsigntime(YsbRequsetEntity params, HttpServletRequest request, HttpServletResponse response) throws Exception {

		log.info("原始订单信息：" + params);

		// 根据商户号查询key
		ChannleMerchantConfigKey keyinfo = wService.getChannelConfigKey(params.getMerchantId());
		
		String signmsg="";
		if (keyinfo != null) {

			String merchantKey = keyinfo.getMerchantkey();

			SignUtil signUtil = new SignUtil();
			// 生成签名
			signmsg = signUtil.sign(WzfSignUtil.wzfdaifuSigiString(params), merchantKey);
			log.info("生成签名：" + signmsg);
		} else {
			// 返回页面参数
			outString(response, "商户号找不到Key");
		}
		
      return signmsg;
	}
	/**
	 * 1.1子协议录入接口
	 * @param request
	 * @param dcRequest
	 * @return
	 * @throws Exception 
	 */
	@RequestMapping(value = "signSimpleSubContract")
	public void signSimpleSubContract(YsbRequsetEntity temp,HttpServletRequest request,HttpServletResponse response) throws Exception{
		
		String merchantId=temp.getMerchantId();
		logger.info("商户号:"+merchantId);
		
		
		String sign=ysbsigntime(temp,request,response);
		temp.setSign(sign);
		YsbRequsetEntity param=new YsbRequsetEntity();
		// 所有的流程通过 就发起支付 上送数据
		Map<String, String> retMap  = wService.customerRegister(temp);
		outString(response, gson.toJson(retMap));
	}
	/**
	 * 下游接入 demo
	 * 
	 * @param param
	 * @param request
	 * @param response
	 * @return 
	 * @throws Exception
	 */
	@RequestMapping(value = "wzfPaySign")
	public String wzfPaySign(YsbRequsetEntity params, HttpServletRequest request, HttpServletResponse response) throws Exception {

		log.info("原始订单信息：" + params);

		// 根据商户号查询key
		ChannleMerchantConfigKey keyinfo = wService.getChannelConfigKey(params.getMerchantId());
		
		String signmsg="";
		if (keyinfo != null) {

			String merchantKey = keyinfo.getMerchantkey();

			SignUtil signUtil = new SignUtil();
			// 生成签名
			signmsg = signUtil.sign(WzfSignUtil.wzfdaikouSigiString(params), merchantKey);
			log.info("生成签名：" + signmsg);
		} else {
			// 返回页面参数
			outString(response, "商户号找不到Key");
		}
		
      return signmsg;
	}
	/**
	 * 1.2委托代扣接口(子协议号)
	 * @param request
	 * @param dcRequest
	 * @return
	 * @throws Exception 
	 */
	@RequestMapping(value = "collect")
	public void collect(YsbRequsetEntity temp,HttpServletRequest request,HttpServletResponse response) throws Exception{
		
		String merchantId=temp.getMerchantId();
		logger.info("商户号:"+merchantId);
		
		YsbRequsetEntity param=new YsbRequsetEntity();
		// 所有的流程通过 就发起支付 上送数据
		Map<String, String> retMap  = wService.payHandle(temp);
		outString(response, gson.toJson(retMap));
	}
}
