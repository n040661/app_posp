package xdt.dto.scanCode.util;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.log4j.Logger;

import xdt.dto.scanCode.entity.ScanCodeResponseEntity;
import xdt.dto.transfer_accounts.entity.DaifuRequestEntity;
import xdt.model.PmsMerchantInfo;
import xdt.service.IScanCodeService;
import xdt.service.ITotalPayService;
import xdt.util.BeanToMapUtil;

public class ZHJHThread extends Thread {
Logger log = Logger.getLogger(this.getClass());
	
	@Resource
	private IScanCodeService service;
	private String mer;
	
	private String orderId;



	public ZHJHThread(IScanCodeService service, String mer, String orderId
			) {
		super();
		this.service = service;
		this.mer = mer;
		this.orderId = orderId;
	}


	@Override
	public void run() {
		Map<String, String> map =new HashMap<>();
		try {
			//Thread.sleep(20000);
			for (int i = 0; i < 11; i++) {
			 map= service.zhjhQuick(mer, orderId);
			 
			 if(map!=null){
				 if("00".equals(map.get("v_code"))) {
					 if(!"".equals(map.get("v_status"))&&map.get("v_status")!=null) {
						 ScanCodeResponseEntity consume = (ScanCodeResponseEntity) BeanToMapUtil
									.convertMap(ScanCodeResponseEntity.class, map);
							try {
								service.otherInvoke(consume);
							} catch (Exception e1) {
								log.info("兆行修改状态失败");
								e1.printStackTrace();
							}
				 		 break;
					 }
				 }
				 	
			 }
			 Thread.sleep(60000);
			}
		} catch (Exception e) {
			// TODO: handle exception
		}
	}
}
