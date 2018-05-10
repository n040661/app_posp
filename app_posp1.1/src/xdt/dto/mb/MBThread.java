package xdt.dto.mb;
/** 
* @author 作者 E-mail: 
* @version 创建时间：2017年12月29日 下午3:53:44 
* 类说明 
*/

import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;

import com.alibaba.fastjson.JSON;

import xdt.dao.IPmsMerchantInfoDao;
import xdt.model.PmsMerchantInfo;
import xdt.service.IMBService;

public class MBThread extends Thread {

	private Logger log =Logger.getLogger(this.getClass());
	private IMBService imbService;
	private MBReqest mbReqest;
	private IPmsMerchantInfoDao pmsMerchantInfoDao;
	private PmsMerchantInfo merchantinfo;
	

	public MBThread(IMBService imbService, MBReqest mbReqest, IPmsMerchantInfoDao pmsMerchantInfoDao,
			PmsMerchantInfo merchantinfo) {
		super();
		this.imbService = imbService;
		this.mbReqest = mbReqest;
		this.pmsMerchantInfoDao = pmsMerchantInfoDao;
		this.merchantinfo = merchantinfo;
	}


	@Override
	public void run() {
		
		try {
			Map<String, String> map =new HashMap<>();
			Map<String, String> result=new HashMap<>();
			for (int i = 0; i < 30; i++) {
				result =imbService.paysSelect(mbReqest, result);
				log.info("laile!!:"+mbReqest.getMerId());
				String merId=mbReqest.getMerId();
				log.info("魔宝查询代付状态："+JSON.toJSONString(result));
				if("00".equals(result.get("respCode").toString())){
					
					if("1".equals(result.get("status").toString())){
						imbService.UpdateDaifu(result.get("orderId").toString(), "00");
						return;
					}else if("2".equals(result.get("status").toString())){
						imbService.UpdateDaifu(result.get("orderId").toString(), "02");
						Double payMoney =Double.parseDouble(mbReqest.getTransAmount())+Double.parseDouble(merchantinfo.getPoundage())*100;
						map.put("machId", merId);
						map.put("payMoney",payMoney.toString());
						int nus = pmsMerchantInfoDao.updataPay(map);
						if(nus==1){
							log.info("魔宝**补款成功");
							PmsMerchantInfo merchantinfo =new PmsMerchantInfo();
							mbReqest.setOrderId(result.get("orderId").toString()+"/A");
							int id =imbService.add(mbReqest, merchantinfo, result, "00");
							if(id==1){
								log.info("魔宝代付补单成功");
							}
						}
						return;
					}else if("3".equals(result.get("status").toString())){
						
					}
				}
				sleep(80000);
			}
			
		} catch (Exception e) {
			// TODO: handle exception
		}
	}
	
	
	
}
