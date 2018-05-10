package xdt.baidu;



import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import com.google.gson.Gson;


import xdt.dto.AgentSettleResponse;
import xdt.dto.AgentSettleRqeuest;
import xdt.preutil.CheckUtil;
import xdt.util.Global;
import xdt.util.HttpURLConection;
import xdt.util.UtilDate;
/**
 * 处理O单T0 秒到（调用清算平台接口  代付支付）
 * @author wm
 * 2016-02-19
 */
public class AgentT0TaskThread extends Thread {
	
	private Logger logger = Logger.getLogger(AgentT0TaskThread.class);

	private String orderid =null;//订单号
	public AgentT0TaskThread(String orderid){
		super();
		this.orderid=orderid;
	}

	@Override
	public void run() {
		//线程处理
		
		try {
			
			 logger.info("处理商户O单T0调用清算接口开始，时间："+UtilDate.getDateFormatter());
			 AgentSettleRqeuest agentSettleRqeuest= new AgentSettleRqeuest();
			
			 //加密   调用清算系统   
	    	
			 agentSettleRqeuest.setInput_charset("GBk");// 请求中文参数的字符编码 参数值的编码为GBK
	    	
			 agentSettleRqeuest.setSign_method("MD5");// 签名方法 签名算法为MD5
			 
			 agentSettleRqeuest.setOrderNumber(orderid);
  			   
  			 String   input_charset = "input_charset=GBk";       
			 String   orderNumber = "orderNumber="+orderid;
			 String   sign_method  = "sign_method=MD5";
			   
			  //签名数组
			 String[] checkArray = {input_charset,orderNumber,sign_method};
			   
			 String checkKey = CheckUtil.makeSign(checkArray, Global.getConfig("fubeiagentsettle"));
  			   
			 agentSettleRqeuest.setSign(checkKey);
			 
			 //上送给清算系统
			 String param = "param="+new Gson().toJson(agentSettleRqeuest);
            
        	 //向清算系统发送报文
             String result = HttpURLConection.httpURLConnectionPOST(Global.getConfig("agentSettleHttpUrl"),  param);
             
             logger.info("商户O单T0调用清算接口放回数据:  "+result+"     时间："+UtilDate.getDateFormatter());	

             if(StringUtils.isBlank(result)){
            	 logger.info("处理商户O单T0调用清算接口返回数据为空，时间："+UtilDate.getDateFormatter());	
             }else{
            	 
            	 AgentSettleResponse agentSettleResponse= (AgentSettleResponse)new Gson().fromJson(result,AgentSettleResponse.class);
            	 
            	 
            	 if("0000".equals(agentSettleResponse.getRetCode())){
            		 logger.info("处理商户O单T0调用清算接口代付成功，时间："+UtilDate.getDateFormatter()+"清算返回信息："+agentSettleResponse.getRetMessage());	
            	 }else{
            		 logger.info("处理商户O单T0调用清算接代付失败，时间："+UtilDate.getDateFormatter()+"清算返回信息："+agentSettleResponse.getRetMessage());	
            	 }
            	
             }
			
			
			logger.info("处理商户O单T0调用清算接口结束，时间："+UtilDate.getDateFormatter());	
        
		} catch (Exception e) {
			e.printStackTrace();
			logger.debug("处理商户O单T0调用清算接口异常，时间："+UtilDate.getDateFormatter(),e);
		}

        
		
		
		
		
	}
		
	
	
	
}
