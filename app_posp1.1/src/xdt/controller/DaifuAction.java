package xdt.controller;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.ibatis.annotations.Result;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import xdt.model.ChannleMerchantConfigKey;
import xdt.quickpay.daifu.DaiFuRequestEntity;
import xdt.quickpay.daikou.util.SignUtil;
import xdt.quickpay.daikou.util.SignUtilEntity;
import xdt.service.IDaiFuService;
import xdt.util.BeanToMapUtil;
import xdt.util.JsdsUtil;

@Controller
@RequestMapping("df")
public class DaifuAction extends BaseAction {

	/**
	 * 日志记录
	 */
	private Logger log = Logger.getLogger(DaifuAction.class);

	@Resource
	private IDaiFuService dfService;

	/**
	 * 1.1 实时代付接口
	 * 
	 * @param request
	 * @param dcrequest
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "payroll")
	public void pay(DaiFuRequestEntity temp, HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		// String resultCode=null;

		Map<String, String> result = new HashMap<String, String>();

		logger.info("##############################代付交易");
		logger.info("下游上送参数：{}" + temp);
		String merchantId = temp.getMerchantId();
		logger.info("商户号:" + merchantId);
		Map map = BeanToMapUtil.convertBean(temp);
		String signmsg = "";
		if (temp.getSign() == null) {
			// 根据商户号查询key
			ChannleMerchantConfigKey keyinfo = dfService.getChannelConfigKey(merchantId);

			if (keyinfo != null) {

				String merchantKey = keyinfo.getMerchantkey();
				// 生成签名
				signmsg = JsdsUtil.sign(map, merchantKey);
				log.info("生成签名：" + signmsg);
				temp.setSign(signmsg);
			} else {
				// 返回页面参数
				result.put("001","商户号找不到Key");
				
			}
		}
		result = dfService.Payroll(temp);
		this.log.info("向下游 发送的数据:" + result);		
		outString(response, gson.toJson(result));
		this.log.info("向下游 发送数据成功");
	}

}
