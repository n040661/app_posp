package xdt.aspect;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Component;

import xdt.dao.IPmsAppTransInfoDao;
import xdt.dao.IPmsDaifuMerchantInfoDao;
import xdt.dao.IPospTransInfoDAO;
import xdt.dto.pay.PayRequest;
import xdt.model.PmsAppTransInfo;
import xdt.model.PmsDaifuMerchantInfo;
import xdt.model.PospTransInfo;
import xdt.service.IPayService;
import xdt.service.IPmsAppTransInfoService;
import xdt.service.IPmsDaifuMerchantInfoService;
import xdt.util.OrderStatusEnum;
import xdt.util.UtilDate;

@Component
public class CJPayTiming {
	Logger logger =Logger.getLogger(PayTiming.class);
	@Resource
	private IPmsDaifuMerchantInfoService daifuMerchantInfoService;
	@Resource
	private IPmsDaifuMerchantInfoDao pmsDaifuMerchantInfoDao;
	@Resource
	private IPayService payService;
	
	@Resource
	private IPmsAppTransInfoDao appTransInfoDao;
	
	@Resource
	public IPospTransInfoDAO pospTransInfoDAO;
	/**
	 * 代付
	 * @throws Exception
	 */
	public void CJPayTimingSelect() throws Exception{
		
		
		List<PmsDaifuMerchantInfo> list = daifuMerchantInfoService.selectDaifu3();
		
		for (PmsDaifuMerchantInfo pmsDaifuMerchantInfo : list) {
			
			// 查询上游商户号
		    PayRequest payRequest =new PayRequest();
		    Map<String, String> result= new HashMap<>();
		    payRequest.setMerchantId(pmsDaifuMerchantInfo.getMercId());
		    payRequest.setOrderId(pmsDaifuMerchantInfo.getBatchNo());
		    payRequest.setMerchantUuid(pmsDaifuMerchantInfo.getAgentnumber()); 
		    payRequest.setType("28");
		    result = payService.paySelect(payRequest, result);
		    
		    if("00".equals(result.get("respCode"))){
				if("1".equals(result.get("code"))) {
					payService.UpdateDaifu(result.get("reqFlowNo"), "00");
					return;
				}else if("3".equals(result.get("code"))) {
					payService.UpdateDaifu(result.get("reqFlowNo"), "01");
					return;
				}
			}
		}
		
	}
	/**
	 * 支付
	 * @throws Exception
	 */
	public void CJTimingSelect() throws Exception{
		
		List<PmsAppTransInfo> array1=appTransInfoDao.searchMyorder1();
		if(array1.size()>0) {
			for (PmsAppTransInfo pmsAppTransInfo : array1) {		
				 PayRequest payRequest =new PayRequest();
				    Map<String, String> result= new HashMap<>();
				    payRequest.setMerchantId(pmsAppTransInfo.getMercid());
				    payRequest.setOrderId(pmsAppTransInfo.getOrderid());
				    payRequest.setType("22");
				    result = payService.select(payRequest, result);
				    PospTransInfo posp = pospTransInfoDAO.searchBytransOrderId(pmsAppTransInfo.getOrderid());
					logger.info("流水表信息" + posp);
					// 订单信息
					if(posp==null) {
						continue;
					}
					PmsAppTransInfo trans = appTransInfoDao.searchOrderInfo(posp.getOrderId());
					logger.info("订单表信息" + pmsAppTransInfo);
				    if("00".equals(result.get("respCode"))) {
				    	// 支付成功
						trans.setStatus(OrderStatusEnum.paySuccess.getStatus());
						trans.setFinishtime(UtilDate.getDateFormatter());
						// 修改订单
						int updateAppTrans = appTransInfoDao.update(trans);
						if (updateAppTrans == 1) {
							// log.info("修改余额");
							// 修改余额
							logger.info(pmsAppTransInfo);
							// updateMerchantBanlance(pmsAppTransInfo);
							// 更新流水表
							posp.setResponsecode("00");
							posp.setPospsn(pmsAppTransInfo.getOrderid());
							logger.info("更新流水");
							logger.info(posp);
							pospTransInfoDAO.updateByOrderId(posp);
							Thread.interrupted();
						}
				    }else if("01".equals(result.get("respCode"))) {
				    	trans.setStatus(OrderStatusEnum.payFail.getStatus());
						trans.setFinishtime(UtilDate.getDateFormatter());
						// 修改订单
						int updateAppTrans = appTransInfoDao.update(trans);
						if (updateAppTrans == 1) {
							// 更新流水表
							posp.setResponsecode("02");
							posp.setPospsn(pmsAppTransInfo.getOrderid());
							logger.info("更新流水");
							logger.info(posp);
							pospTransInfoDAO.updateByOrderId(posp);
						}
				    }
			}
		}
		
	}
	
}
