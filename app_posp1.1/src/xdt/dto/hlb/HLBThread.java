package xdt.dto.hlb;

import java.util.HashMap;
import java.util.Map;



import org.apache.log4j.Logger;

import xdt.dao.IPmsMerchantInfoDao;
import xdt.model.PmsMerchantInfo;
import xdt.service.IHLBService;

public class HLBThread extends Thread {
	Logger log=Logger.getLogger(this.getClass());
	private IHLBService ihlbService;
	
	private HLBRequest hlbRequest;
	
	private IPmsMerchantInfoDao infoDao;
	
	private PmsMerchantInfo merchantinfo;
	
	
	
	public HLBThread(IHLBService ihlbService, HLBRequest hlbRequest,
			IPmsMerchantInfoDao infoDao, PmsMerchantInfo merchantinfo) {
		super();
		this.ihlbService = ihlbService;
		this.hlbRequest = hlbRequest;
		this.infoDao = infoDao;
		this.merchantinfo = merchantinfo;
	}



	@Override
	public void run() {
		Map<String, String> result =new HashMap<>();
		Map<String, String> map =new HashMap<>();
		try {
		for (int i = 0; i < 10; i++) {
			
		result =ihlbService.transferQuery(hlbRequest, result);
		if("00".equals(result.get("respCode"))){
		  ihlbService.UpdateDaifu(hlbRequest.getOrderId(), "00");
		  return;
		}else if("01".equals(result.get("respCode"))){
		  ihlbService.UpdateDaifu(hlbRequest.getOrderId(), "01");
		  map.put("mercId", hlbRequest.getOrderId());
			map.put("payMoney",hlbRequest.getOrderAmount());
			int nus=0;
			if("0".equals(hlbRequest.getDataType())){
				nus = infoDao.updataPay(map);
			}else if("1".equals(hlbRequest.getDataType())){
				nus = infoDao.updataPay(map);
			}
			if(nus==1){
				log.info("合利宝***补款成功");
				hlbRequest.setOrderId(hlbRequest.getOrderId()+"/A");
				String type="T1";
				if("0".equals(hlbRequest.getDataType())){
					type="D0";
				}
				int id =ihlbService.add(hlbRequest, merchantinfo, result, "00",type);
				if(id==1){
					log.info("合利宝代付补单成功");
				}
			}
		  return;
		}
		
		sleep(80000);
		}
		
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	
}
