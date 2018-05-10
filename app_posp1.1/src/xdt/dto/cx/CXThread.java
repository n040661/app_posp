package xdt.dto.cx;

import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;

import com.alibaba.fastjson.JSON;

import xdt.dao.IPmsMerchantInfoDao;
import xdt.dto.transfer_accounts.entity.DaifuRequestEntity;
import xdt.model.PmsMerchantInfo;
import xdt.service.ITotalPayService;

public class CXThread extends Thread{
	private Logger log =Logger.getLogger(this.getClass());
	
	
	private IPmsMerchantInfoDao pmsMerchantInfoDao;
	
	private DaifuRequestEntity payRequest;
	private ITotalPayService service;
	
	private String batchNo;


	public CXThread(IPmsMerchantInfoDao pmsMerchantInfoDao, DaifuRequestEntity payRequest, ITotalPayService service,
			String batchNo) {
		super();
		this.pmsMerchantInfoDao = pmsMerchantInfoDao;
		this.payRequest = payRequest;
		this.service = service;
		this.batchNo = batchNo;
	}
	@Override
	public void run() {
		try {
			sleep(5000);
			Map<String, String> result=new HashMap<>();
			Map<String, String> m =new HashMap<>();
			PmsMerchantInfo merchantinfo =new PmsMerchantInfo();
			for (int i = 0; i < 30; i++) {
				result =service.cxQuick(payRequest.getV_mid(), batchNo, result);
				log.info("创新查询代付状态："+JSON.toJSONString(result));
				
				if("10000".equals(result.get("respCode").toString())){
					service.UpdateDaifu(batchNo, "00");
					return;
				}else if("10001".equals(result.get("respCode").toString())){
					
					//if("90001".equals(result.get("respCode").toString()))
				}else {
					service.UpdateDaifu(batchNo, "01");
					m.put("payMoney", Double.parseDouble(result.get("amount"))*100+"");
					m.put("machId", payRequest.getV_mid());
					int nus=0;
					if ("0".equals(payRequest.getV_type())) {
						 nus = pmsMerchantInfoDao.updataPay(m);
					}else if ("1".equals(payRequest.getV_type())) {
						 nus = pmsMerchantInfoDao.updataPayT1(m);
					}
					if (nus == 1) {
						log.info("创新***补款成功");
						payRequest.setV_batch_no(payRequest.getV_batch_no() + "/A");
						int id = service.add(payRequest, merchantinfo, result, "00");
						if (id == 1) {
							log.info("创新代付补单成功");
						}
					}
					return;
				}
				
				sleep(80000);
			 }
				
			} catch (Exception e) {
				
			}
	}
	
	
	
}

