package xdt.quickpay.hddh.util;
import java.io.IOException;
import java.nio.charset.Charset;
import java.text.SimpleDateFormat;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.ObjectMapper;

public class MD5Signer {

	private static final Logger logger = LoggerFactory.getLogger(MD5Signer.class);

	private static ObjectMapper objectMapper = new ObjectMapper();

//	static {
//		objectMapper.setDateFormat(new SimpleDateFormat("yyyy-MM-dd hh:mm:ss"));
//	}

	public static String getSign(String base64Str,String key){
		String signedData = String.format("data=%s&key=%s", base64Str,key);
		String sign = MD5.md5(signedData, Charset.forName("UTF-8"));
		logger.info("signedData={},sign={}",signedData,sign);
		return sign;
	}

	public static Boolean checkSign(String base64Str,String sign,String key){

		String signedData = String.format("data=%s&key=%s", base64Str,key);
		String expectedSign = MD5.md5(signedData, Charset.forName("UTF-8"));
		logger.info("signedData={},expectedSign={},sign={}",signedData,expectedSign,sign);

		return expectedSign.equals(sign);
	}

	public static String getBase64(String plainText){
		byte[] data=plainText.getBytes(Charset.forName("UTF-8"));
		String base64Str=Base64.encode(data, 0, data.length);
		return base64Str;
	}

//	public static <T> T getDataObjectFromBase64(String base64Str, Class<T> type) {
//		// TODO Auto-generated method stub
//		try {
//			String plainText = new String(Base64.decode(base64Str),Charset.forName("UTF-8"));
//			return objectMapper.readValue(plainText, type);
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			//e.printStackTrace();
//			logger.info("Exception:",e);
//			throw new ServiceException(-1, "获取数据异常:"+e.getMessage());
//		}
//
//
//	}

}
