package xdt.baidu;


import org.apache.log4j.Logger;

import xdt.dao.IPmsAgentInfoDao;
import xdt.dao.IPmsAppTransInfoDao;
import xdt.dao.IPospTransInfoDAO;
import xdt.dao.ITAccAccountDao;
import xdt.model.PmsAppTransInfo;
import xdt.service.IPmsAppTransInfoService;
import xdt.service.impl.MerchantCollectMoneyServiceImpl;
import xdt.util.UtilDate;

public class YDHBTaskThread extends Thread {
	
	private Logger logger = Logger.getLogger(YDHBTaskThread.class);

	private MerchantCollectMoneyServiceImpl  merchantCollectMoneyServiceImpl;
	public IPmsAppTransInfoDao pmsAppTransInfoDao; // 业务配置服务层
	private ITAccAccountDao accountDao;//商户账户配置服务层
	 private IPmsAppTransInfoService pmsAppTransInfoService;//流水
	 private IPospTransInfoDAO pospTransInfoDAO;
	 private IPmsAgentInfoDao pmsAgentInfoDao;
	private String order_no;
	public YDHBTaskThread(String order_no, IPmsAppTransInfoDao pmsAppTransInfoDao,ITAccAccountDao accountDao,
			IPmsAppTransInfoService pmsAppTransInfoService,IPospTransInfoDAO pospTransInfoDAO,IPmsAgentInfoDao pmsAgentInfoDao){
		super();
		this.order_no=order_no;
		this.merchantCollectMoneyServiceImpl=new MerchantCollectMoneyServiceImpl();
		this.pmsAppTransInfoDao=pmsAppTransInfoDao;
		this.accountDao=accountDao;
		this.pmsAppTransInfoService=pmsAppTransInfoService;
		this.pospTransInfoDAO=pospTransInfoDAO;
		this.pmsAgentInfoDao=pmsAgentInfoDao;
	}

	@Override
	public void run() {
		//线程处理
		//1、先查询本地库订单是否是完成状态
		//2、如果是完成状态跳过第三方查询并结束；   否则进行第三方查询     分隔时间进行查询在进行下一步处理 
		//3、第三方查询结果后根据结果处理本地订单并结束
		
		int []times = {3000,3000,3000,3000,3000,3000,3000,3000,3000,3000,5000,15000,30000,60000};
		
		try {
			sleep(2000);
 			for(int i=0;i<times.length;i++){
 				logger.info("移动和包线程处理调用百度订单查询接口开始，时间："+UtilDate.getDateFormatter());
				//根据订单号获取到本地订单
		        PmsAppTransInfo appTransInfo = pmsAppTransInfoDao.searchOrderInfo(order_no);
				if(appTransInfo!=null){
					if("0".equals(appTransInfo.getStatus())||"3".equals(appTransInfo.getStatus())){
						//本地订单属于订单完成状态    什么都不操作
						break;
					}else{
						//把pmsAppTransInfoDao传回sermerchantCollectMoneyServiceImplvice
						merchantCollectMoneyServiceImpl.setDao(pmsAppTransInfoDao,accountDao,pmsAppTransInfoService,pospTransInfoDAO,pmsAgentInfoDao);
						merchantCollectMoneyServiceImpl.yDHBHandelOrder(order_no);
				        
					}
					
				}
				
				sleep(times[i]);//线程休眠固定时间再次执行
				logger.info("移动和包线程处理调用百度订单查询接口结束，时间："+UtilDate.getDateFormatter());	
			}
	        
		} catch (Exception e) {
			logger.debug("移动和包线程处理调用百度订单查询接口异常，时间："+UtilDate.getDateFormatter(),e);
		}

        
		
		
		
		
	}
		
	
	
	
}
