package xdt.dto.hj;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.log4j.Logger;

import com.alibaba.fastjson.JSON;

import xdt.dao.IPmsMerchantInfoDao;
import xdt.model.PmsBusinessPos;
import xdt.model.PmsMerchantInfo;
import xdt.service.IHJService;

public class HJThread extends Thread{

	private Logger log =Logger.getLogger(this.getClass());
	
	private IPmsMerchantInfoDao pmsMerchantInfoDao;
	
	private IHJService service;

	private HJPayRequest hjPayRequest;
	
	private PmsBusinessPos pmsBusinessPos;
	
	private PmsMerchantInfo merchantinfo;

	private Double shouxufei;

	public HJThread(IPmsMerchantInfoDao pmsMerchantInfoDao, IHJService service, HJPayRequest hjPayRequest,
			PmsBusinessPos pmsBusinessPos, PmsMerchantInfo merchantinfo, Double shouxufei) {
		super();
		this.pmsMerchantInfoDao = pmsMerchantInfoDao;
		this.service = service;
		this.hjPayRequest = hjPayRequest;
		this.pmsBusinessPos = pmsBusinessPos;
		this.merchantinfo = merchantinfo;
		this.shouxufei = shouxufei;
	}


	@Override
	public void run() {
		try {
			Map<String, String> map =new HashMap<>();
			Map<String, String> result=new HashMap<>();
			sleep(60000);
			for (int i = 0; i < 30; i++) {
				HJPayResponse payResponse =service.paySelect(hjPayRequest, pmsBusinessPos);
				result.put("respMsg", payResponse.getRc_CodeMsg());
				 log.info("汇聚查询代付状态："+JSON.toJSONString(payResponse));
				if("100".equals(payResponse.getRb_Code())||"102".equals(payResponse.getRb_Code())){
					String de[]=payResponse.getR3_Details().split("\\|");
					log.info("查询返回状态参数："+de[de.length-1]);
					if("100".equals(de[de.length-1])){
						service.UpdateDaifu(hjPayRequest.getBatchNo(), "00");
						return;
					}else if("101".equals(de[de.length-1])||"103".equals(de[de.length-1])){
						service.UpdateDaifu(hjPayRequest.getBatchNo(), "02");
						map.put("mercId", hjPayRequest.getMerchantNo());
						map.put("payMoney",Double.parseDouble(hjPayRequest.getAmount())+shouxufei+"");
						int nus = pmsMerchantInfoDao.updataPay(map);
						if(nus==1){
							log.info("汇聚***补款成功");
							hjPayRequest.setBatchNo(hjPayRequest.getBatchNo()+"/A");
							int id =service.add(hjPayRequest, merchantinfo, result, "00");
							if(id==1){
								log.info("汇聚代付补单成功");
							}
						}
						return;
					}
				}
				sleep(150000);
			}
			
		} catch (Exception e) {
			// TODO: handle exception
		}
	}




	private void sleep(String string) {
		// TODO Auto-generated method stub
		
	}
	
	
}
