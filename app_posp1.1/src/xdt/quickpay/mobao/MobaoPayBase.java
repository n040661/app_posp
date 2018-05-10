package xdt.quickpay.mobao;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.log4j.Logger;
import xdt.service.impl.BaseServiceImpl;
import xdt.servlet.AppPospContext;

import java.io.IOException;


public class MobaoPayBase{

	static	HttpClient   client = new HttpClient();;
    static private Logger logger=Logger.getLogger(MobaoPayBase.class);


    public static String   requestBody(String merId,String transData){
     String   testUrl = AppPospContext.context.get(BaseServiceImpl.MOBAOCHANNELNUM+BaseServiceImpl.MOBAOPAY).getUrl();
    logger.info("开始调用摩宝快捷支付，摩宝商户号："+merId+",请求串："+transData);
	PostMethod  method= new PostMethod(testUrl);
	method.setRequestHeader("Content-Type","application/x-www-form-urlencoded;charset=gbk");    
	method.setParameter("merId",merId);
	method.setParameter("transData", transData);
	try {
		int statusCode = client.executeMethod(method);
		if (statusCode != 200) {
			logger.info("http请求错误，http返回状态码：" + statusCode);
			return null;
	    }else{
	    	String resp = method.getResponseBodyAsString();

            logger.info("摩宝返回数据："+resp);
	    	return resp;
	    }
	} catch (HttpException e){
		e.printStackTrace();
		return ""+e.getMessage();
	} catch (IOException e) {
		e.printStackTrace();
		return ""+e.getMessage();
	}
	}

}
