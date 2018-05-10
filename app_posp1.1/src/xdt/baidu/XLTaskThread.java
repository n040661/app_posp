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

public class XLTaskThread extends Thread {
	
	private Logger logger = Logger.getLogger(XLTaskThread.class);

	private MerchantCollectMoneyServiceImpl  merchantCollectMoneyServiceImpl;
	public IPmsAppTransInfoDao pmsAppTransInfoDao; // 业务配置服务层
	private ITAccAccountDao accountDao;//商户账户配置服务层
	private IPmsAppTransInfoService pmsAppTransInfoService;//流水
	private IPospTransInfoDAO pospTransInfoDAO;
	private IPmsAgentInfoDao pmsAgentInfoDao;
	private String order_no;//订单号
	private String serialNo;//讯联批次号
	private String merInfo;//商户编号
	private String paymenttype;//查询区分微信（025）还是支付宝（015）
	private String tradeTime;//讯联订单交易时间
	 private String searchNum;//讯联检索参考号
	public XLTaskThread(String order_no,String serialNo,String merInfo,String paymenttype,String tradeTime,String searchNum, IPmsAppTransInfoDao pmsAppTransInfoDao,ITAccAccountDao accountDao,
			IPmsAppTransInfoService pmsAppTransInfoService,IPospTransInfoDAO pospTransInfoDAO,IPmsAgentInfoDao pmsAgentInfoDao){
		super();
		this.order_no=order_no;
		this.serialNo=serialNo;
		this.merInfo=merInfo;
		this.paymenttype=paymenttype;
		this.tradeTime=tradeTime;
		this.searchNum=searchNum;
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
		
		int []times = {5000,5000,5000,5000,5000,5000,5000,5000,5000,5000,10000,15000,30000,60000};
		
		try {
			sleep(2000);
 			for(int i=0;i<times.length;i++){
 				
 				logger.info("讯联线程处理调用讯联订单查询接口开始，时间："+UtilDate.getDateFormatter());
				//根据订单号获取到本地订单
		        PmsAppTransInfo appTransInfo = pmsAppTransInfoDao.searchOrderInfo(order_no);
				if(appTransInfo!=null){
					if("0".equals(appTransInfo.getStatus())||"3".equals(appTransInfo.getStatus())){
						//本地订单属于订单完成状态    什么都不操作
						break;
					}else{
						//把pmsAppTransInfoDao传回sermerchantCollectMoneyServiceImplvice
						merchantCollectMoneyServiceImpl.setDao(pmsAppTransInfoDao,accountDao,pmsAppTransInfoService,pospTransInfoDAO,pmsAgentInfoDao);
						merchantCollectMoneyServiceImpl.xLHandelOrder(order_no,serialNo,merInfo,paymenttype,tradeTime,searchNum);
				        
					}
					
				}
				
				sleep(times[i]);//线程休眠固定时间再次执行
				logger.info("讯联线程处理调用讯联订单查询接口结束，时间："+UtilDate.getDateFormatter());	
			}
	        
		} catch (Exception e) {
			logger.debug("讯联线程处理调用讯联订单查询接口异常，时间："+UtilDate.getDateFormatter(),e);
		}

        
		
		
		
		
	}
		
	
	
	
}
