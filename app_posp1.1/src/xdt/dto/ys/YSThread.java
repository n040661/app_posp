package xdt.dto.ys;
/** 
* @author 作者 E-mail: 
* @version 创建时间：2018年2月6日 下午3:02:08 
* 类说明 
*/

import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;

import com.alibaba.fastjson.JSON;

import xdt.model.PmsMerchantInfo;
import xdt.service.IYSService;

public class YSThread extends Thread {

	private Logger log =Logger.getLogger(this.getClass());
	private IYSService service;
	
	private String orderId;
	
	private String merchantId;

	public YSThread(IYSService service, String orderId, String merchantId) {
		super();
		this.service = service;
		this.orderId = orderId;
		this.merchantId = merchantId;
	}

	@Override
	public void run() {
		try {
			Map<String, String> result=new HashMap<>();
			for (int i = 0; i < 30; i++) {
				result =service.selectB2(orderId, merchantId, result);
				log.info("易生查询代付状态："+JSON.toJSONString(result));
				if("00".equals(result.get("respCode").toString())){
					
					if("00".equals(result.get("code").toString())){
						service.UpdateDaifu(orderId, "00");
						return;
					}else if("01".equals(result.get("code").toString())){
						service.UpdateDaifu(orderId, "02");
						return;
					}
				}
				sleep(80000);
			}
			} catch (Exception e) {
				
			}
	}
	
	
	
	
}
