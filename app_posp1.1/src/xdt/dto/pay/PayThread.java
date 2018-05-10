package xdt.dto.pay;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.TimeZone;

import org.apache.log4j.Logger;

import xdt.controller.PayController;
import xdt.model.OriginalOrderInfo;
import xdt.service.IPayService;

public class PayThread extends Thread {

	private Logger log =Logger.getLogger(this.getClass());
	
	private IPayService PayService;
	private PayRequest payRequest;

	private OriginalOrderInfo originalInfo;



	public PayThread(IPayService payService, PayRequest payRequest,
			OriginalOrderInfo originalInfo) {
		super();
		PayService = payService;
		this.payRequest = payRequest;
		this.originalInfo = originalInfo;
	}



	@Override
	public void run() {
		Map<String, String> result=new HashMap<>();
		try {
		for (int i = 0; i < 10; i++) {
			result =PayService.select(payRequest, result);
			if("00".equals(result.get("respCode"))){
				PayService.updateByOrderId(payRequest.getOrderId(), "2", result);
				/*Calendar cal1 = Calendar.getInstance();
				TimeZone.setDefault(TimeZone.getTimeZone("GMT+8:00"));
				java.text.SimpleDateFormat sdf = new SimpleDateFormat("kk:mm:ss");
				if (sdf.parse(sdf.format(new Date(Long.parseLong(String.valueOf(result.get("payDate")))))).getTime() > sdf.parse("21:00:00").getTime()
						&& sdf.parse(sdf.format(new Date(Long.parseLong(String.valueOf(result.get("payDate")))))).getTime() < sdf.parse("09:00:00").getTime()) {
					log.info("时间不在正常入金时间内!");
					result.put("type", "1");
					PayService.UpdatePmsMerchantInfo(originalInfo,result);
				}else{
					result.put("type", "0");
					PayService.UpdatePmsMerchantInfo(originalInfo,result);
			    }*/
				return;
			}else if("200".equals(result.get("respCode"))){
				
			}else{
				PayService.updateByOrderId(payRequest.getOrderId(), "5", result);
				return;
			}
			log.info("第"+i+"次结束！");
			sleep(80000);
		}
		} catch (Exception e) {
			// TODO: handle exception
		}
	}
	
	
	
}
