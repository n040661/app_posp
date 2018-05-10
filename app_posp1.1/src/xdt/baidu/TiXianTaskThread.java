package xdt.baidu;



import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import com.google.gson.Gson;


import xdt.dto.TiXianSettleResponse;
import xdt.dto.TiXianSettleRqeuest;
import xdt.preutil.CheckUtil;
import xdt.util.Global;
import xdt.util.HttpURLConection;
import xdt.util.UtilDate;
/**
 * 处理商户提现  秒到（调用清算平台接口  代付支付）
 * @author wm
 * 2016-01-27
 */
public class TiXianTaskThread extends Thread {
	
	private Logger logger = Logger.getLogger(TiXianTaskThread.class);

	private String orderid =null;//订单号
	public TiXianTaskThread(String orderid){
		super();
		this.orderid=orderid;
	}

	@Override
	public void run() {
		//线程处理
		
		try {
			
			 logger.info("处理商户提现调用清算接口开始，时间："+UtilDate.getDateFormatter());
			 TiXianSettleRqeuest tiXianSettleRqeuest= new TiXianSettleRqeuest();
			
			 //加密   调用清算系统   
	    	
			 tiXianSettleRqeuest.setInput_charset("GBk");// 请求中文参数的字符编码 参数值的编码为GBK
	    	
			 tiXianSettleRqeuest.setSign_method("MD5");// 签名方法 签名算法为MD5
			 
			 tiXianSettleRqeuest.setOrderNumber(orderid);
  			   
  			 String   input_charset = "input_charset=GBk";       
			 String   orderNumber = "orderNumber="+orderid;
			 String   sign_method  = "sign_method=MD5";
			   
			  //签名数组
			 String[] checkArray = {input_charset,orderNumber,sign_method};
			   
			 String checkKey = CheckUtil.makeSign(checkArray, Global.getConfig("tiXianHttpUrlKey"));
  			   
			 tiXianSettleRqeuest.setSign(checkKey);
			 
			 //上送给清算系统
			 String param = "param="+new Gson().toJson(tiXianSettleRqeuest);
            
        	 //向清算系统发送报文
             String result = HttpURLConection.httpURLConnectionPOST(Global.getConfig("tiXianHttpUrl"),  param);
             
             logger.info("商户提现调用清算接口放回数据:  "+result+"     时间："+UtilDate.getDateFormatter());	

             if(StringUtils.isBlank(result)){
            	 logger.info("处理商户提现调用清算接口返回数据为空，时间："+UtilDate.getDateFormatter());	
             }else{
            	 
            	 TiXianSettleResponse tiXianSettleResponse= (TiXianSettleResponse)new Gson().fromJson(result,TiXianSettleResponse.class);
            	 
            	 
            	 if("0000".equals(tiXianSettleResponse.getRetCode())){
            		 logger.info("处理商户提现调用清算接口代付成功，时间："+UtilDate.getDateFormatter()+"清算返回信息："+tiXianSettleResponse.getRetMessage());	
            	 }else{
            		 logger.info("处理商户提现调用清算接代付失败，时间："+UtilDate.getDateFormatter()+"清算返回信息："+tiXianSettleResponse.getRetMessage());	
            	 }
            	
             }
			
			
			logger.info("处理商户提现调用清算接口结束，时间："+UtilDate.getDateFormatter());	
        
		} catch (Exception e) {
			e.printStackTrace();
			logger.debug("处理商户提现调用清算接口异常，时间："+UtilDate.getDateFormatter(),e);
		}

        
		
		
		
		
	}
		
	
	
	
}
