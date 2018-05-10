package xdt.pufa.security;

import org.apache.log4j.Logger;

import xdt.common.security.RSA;
import xdt.util.XMLUtil;

public class EncryptGeneric {
	private static Logger logger = Logger.getLogger(EncryptGeneric.class);

	
	/**
	 * 校验签名sha1
	 * @param xml
	 * @return
	 */
	protected static boolean checkSign(String xml){
		logger.info("----校验浦发响应交易报文签名----");
        String body = XMLUtil.getElement(xml, "BODY");
        
		String signHead = XMLUtil.getElementChild(xml, "HEAD", "signed_str");
		logger.info("浦发响应报文签名>>>>"+signHead);
		
		boolean sign =  xdt.common.security.RSA.doCheck(body, signHead);
		
		//校验通过
		 if(sign){
			 logger.info("校验浦发响应交易报文签名>>>> 通过");
			 return true;
		 }
		 
		 logger.info("校验浦发响应交易报文签名>>>> 签名不一致");
		 
		return false;
	}
	
	
	/**
	 * 生成签名sha1
	 * 可以生成一个签名
	 * 也可以放置一个签名
	 * @param reqMap
	 * @param xml
	 * @return
	 */
	protected static String getSign(String xml){
//        String body = XMLUtil.getElement(xml, "BODY");
		logger.info("----生成浦发请求交易报文签名----");
        String sign = RSA.sign(xml);
        logger.info("报文签名>>>>" + sign);
		return sign;
	}
	
	protected static String isNeedSign(String xml){
//      String body = XMLUtil.getElement(xml, "BODY");
		logger.info("----生成浦发请求交易报文签名----");
      String sign = RSA.sign(xml);
      logger.info("报文签名>>>>" + sign);
		return sign;
	}

}
