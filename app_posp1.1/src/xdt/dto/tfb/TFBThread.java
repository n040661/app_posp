package xdt.dto.tfb;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import xdt.dao.IPmsDaifuMerchantInfoDao;
import xdt.dao.IPmsMerchantInfoDao;
import xdt.model.PmsDaifuMerchantInfo;
import xdt.model.PmsMerchantInfo;
import xdt.service.ITFBService;

public class TFBThread extends Thread {
	public static final Logger log=Logger.getLogger(TFBThread.class);
	
	
	private ITFBService itfbService;
	
	private PayRequest payRequest;
	
	private IPmsDaifuMerchantInfoDao pmsDaifuMerchantInfoDao;
	
	private IPmsMerchantInfoDao pmsMerchantInfoDao;
	
	private String type;



	public TFBThread(ITFBService itfbService, PayRequest payRequest,
			IPmsDaifuMerchantInfoDao pmsDaifuMerchantInfoDao,
			IPmsMerchantInfoDao pmsMerchantInfoDao,String type) {
		super();
		this.itfbService = itfbService;
		this.payRequest = payRequest;
		this.pmsDaifuMerchantInfoDao = pmsDaifuMerchantInfoDao;
		this.pmsMerchantInfoDao = pmsMerchantInfoDao;
		this.type=type;
	}





	@Override
	public void run() {
		try {
			Thread.sleep(2000);
			PmsDaifuMerchantInfo model = new PmsDaifuMerchantInfo();
			PmsMerchantInfo merchantinfo = new PmsMerchantInfo();
			Map<String, String> maps =new HashMap<>();
			merchantinfo.setMercId(payRequest.getSpid());
			BigDecimal b1;
			for (int i = 0; i < 3; i++) {
				List merchantList = this.pmsMerchantInfoDao.searchList(merchantinfo);
				merchantinfo=(PmsMerchantInfo) merchantList.get(0);
				b1=new BigDecimal(merchantinfo.getPosition());
				Double dd =Double.parseDouble(payRequest.getTran_amt());
				Double db =dd+b1.doubleValue();
				log.info("进入线程查询");
				Map<String, String> result =new HashMap<>();
				result= itfbService.PaySelect(payRequest, result);
				if("1".equals(result.get("serialno_state"))){
					if(payRequest.getSp_batch_no()!=""&&payRequest.getSp_batch_no()!=null){
						itfbService.UpdateDaifu(payRequest.getSp_batch_no(), "00");
					}else{
						itfbService.UpdateDaifu(payRequest.getSp_serialno(), "00");
					}
					return;
				}else if("3".equals(result.get("serialno_state"))){
					model.setMercId(payRequest.getSpid());
					model.setCount("1");
					if(payRequest.getSp_batch_no()!=""&&payRequest.getSp_batch_no()!=null){
						model.setBatchNo(payRequest.getSp_batch_no()+"/A");
						model.setIdentity(payRequest.getSp_batch_no());
					}else{
						model.setBatchNo(payRequest.getSp_serialno()+"/A");
						model.setIdentity(payRequest.getSp_serialno());
					}
					model.setAmount(Double.parseDouble(payRequest.getTran_amt()) / 100 + "");
					model.setCardno(payRequest.getAcct_id());
					model.setRealname(payRequest.getAcct_name());
					model.setPayamount( Double.parseDouble(payRequest.getTran_amt()) / 100+"");
					model.setPmsbankno(payRequest.getBank_settle_no());
					model.setTransactionType("代付补款");
					model.setPosition(db.toString());
					model.setRemarks("D0");
					model.setRecordDescription("批次号:" + payRequest.getSp_batch_no()+"订单号："+payRequest.getSp_serialno()+ "错误原因:" + result.get("respMsg"));
					model.setResponsecode("01");
					model.setOagentno("100333");
					maps.put("mercId", payRequest.getSpid());
					maps.put("payMoney", b1.doubleValue() + "");
					int nus = 0 ;
					if(type.equals("1")){
					  nus = pmsMerchantInfoDao.updataPayT1(maps);
					}else if(type.equals("3")){
					  nus = pmsMerchantInfoDao.updataPay(maps);
					}
					
					//merchantinfo.setPosition(db.toString());
					int p1=pmsDaifuMerchantInfoDao.insert(model);
					//int p2=pmsMerchantInfoDao.UpdatePmsMerchantInfo(merchantinfo);
					log.info("p1："+p1+",p2："+nus);
					if(payRequest.getSp_batch_no()!=""&&payRequest.getSp_batch_no()!=null){
						itfbService.UpdateDaifu(payRequest.getSp_batch_no(), "01");
					}else{
						itfbService.UpdateDaifu(payRequest.getSp_serialno(), "01");
					}
					return;
				}
				
			}
			Thread.sleep(300000);
		} catch (Exception e) {
			// TODO: handle exception
		}
		
		
	}

}
