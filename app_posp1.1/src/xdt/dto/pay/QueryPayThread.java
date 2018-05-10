package xdt.dto.pay;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.TimeZone;

import org.apache.log4j.Logger;

import xdt.dao.IPmsMerchantInfoDao;
import xdt.model.OriginalOrderInfo;
import xdt.model.PmsMerchantInfo;
import xdt.service.IPayService;
import xdt.service.PmsWeixinMerchartInfoService;

/**
 * 代付查询线程
 * @author GAO
 *
 */
public class QueryPayThread extends Thread{

	
private Logger log =Logger.getLogger(this.getClass());
	
	private IPayService PayService;
	private PayRequest payRequest;

	private IPmsMerchantInfoDao pmsMerchantInfoDao;

	public PmsWeixinMerchartInfoService weixinService;
	



	public QueryPayThread(IPayService payService, PayRequest payRequest, IPmsMerchantInfoDao pmsMerchantInfoDao,
			PmsWeixinMerchartInfoService weixinService) {
		super();
		PayService = payService;
		this.payRequest = payRequest;
		this.pmsMerchantInfoDao = pmsMerchantInfoDao;
		this.weixinService = weixinService;
	}

	@Override
	public void run() {
		Map<String, String> result=new HashMap<>();
		try {
		for (int i = 0; i < 10; i++) {
			result =PayService.paySelect(payRequest, result);
			if("00".equals(result.get("respCode"))){
				if("1".equals(result.get("code"))) {
					PayService.UpdateDaifu(result.get("reqFlowNo"), "00");
					return;
				}else if("3".equals(result.get("code"))) {
					PayService.UpdateDaifu(result.get("reqFlowNo"), "01");
					Map<String, String> m=new HashMap<>();
					m.put("payMoney",payRequest.getAmount());
	     			m.put("machId", payRequest.getMerchantId());
	     			m.put("account",result.get("account"));
	     			/*int nus =0;
	     			int nusy=0;
					if("400".equals(result.get("walletType"))) {
						 nus = pmsMerchantInfoDao.updataPay(m);
						 nusy = weixinService.updataPay(m);
					}else{
						 nus = pmsMerchantInfoDao.updataPayT1(m);
						 nusy = weixinService.updataPayT1(m);
					}*/
					/*log.info("大钱包补款状态："+nus+",小钱包补款状态："+nusy);
					if(nus==1&&nusy==1){
						log.info("畅捷***补款成功");
						PmsMerchantInfo info= new PmsMerchantInfo();
						payRequest.setOrderId(payRequest.getOrderId()+"/A");
						int id =PayService.add(payRequest, info, result, "00");
						if(id==1){
							log.info("畅捷代付补单成功");
						}
					}*/
					return;
				}
			}
			log.info("第"+i+"次结束！");
			sleep(80000);
		}
		} catch (Exception e) {
			// TODO: handle exception
		}
	}
	
	
}
