package xdt.pufa;

import org.apache.log4j.Logger;

import xdt.dao.IPmsAppTransInfoDao;
import xdt.model.PmsAppTransInfo;
import xdt.service.impl.PufaServiceImpl;

public class PuFaThread extends Thread {

	public static final Logger logger=Logger.getLogger(PuFaThread.class);
	
	private String orderId;
	
	public PufaServiceImpl pufaService;
	
	public IPmsAppTransInfoDao pmsAppTransInfoDao;
	
	public PuFaThread(String orderId,PufaServiceImpl pufaService, IPmsAppTransInfoDao pmsAppTransInfoDao) {
		this.pufaService=pufaService;
		this.orderId=orderId;
		this.pmsAppTransInfoDao=pmsAppTransInfoDao;
	}

	@Override
	public synchronized void run() {
		
		//线程处理
		//1、先查询本地库订单是否是完成状态
		//2、如果是完成状态跳过第三方查询并结束；   否则进行第三方查询     分隔时间进行查询在进行下一步处理 
		//3、第三方查询结果后根据结果处理本地订单并结束
		
			
		try {
			Thread.sleep(2000);
				for(int i=0;i<1000;i++){
					logger.info("启线程查询订单状态");
					PmsAppTransInfo	pmsAppTransInfo=pmsAppTransInfoDao.searchOrderInfo(orderId);
					logger.info(pmsAppTransInfo);
					if("0".equals(pmsAppTransInfo.getStatus())){
						break;
					}
					pufaService.updateOrderStatusByOrder(pmsAppTransInfo);
					Thread.sleep(5000);
				}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		

	}

}
