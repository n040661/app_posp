package xdt.controller;
import java.util.HashMap;
import java.util.Map;
import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import xdt.dto.balance.BalanceRequestEntity;
import xdt.service.IWechatScanCodeService;
@Controller
@RequestMapping("balance")
public class WechatScanCodeController extends BaseAction{

	private Logger log = LoggerFactory.getLogger(this.getClass());
	
	@Resource
	private IWechatScanCodeService wechatScanCodeService;

	/**
	 * 商户余额查询
	 *
	 * @param request
	 * @return
	 * @throws Exception \
	 */
	@ResponseBody
	@RequestMapping("scan_param")
	public void  scanParam(BalanceRequestEntity scan,HttpServletRequest request, HttpServletResponse response) throws Exception {
		
		log.info("下游上送的参数："+scan);
		Map<String, String> result = new HashMap<String, String>();
		if(scan!=null){
		// 所有的流程通过 就发起查询
		 result =wechatScanCodeService.payHandle(scan);
		}else{
			result.put("05", "上送的参数为空");
		}
		this.log.info("向下游 发送的数据:" + result);
		outString(response, this.gson.toJson(result));
		this.log.info("向下游 发送数据成功");
	}


}
