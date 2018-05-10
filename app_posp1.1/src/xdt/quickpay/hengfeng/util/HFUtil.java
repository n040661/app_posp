package xdt.quickpay.hengfeng.util;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.buybal.epay.util.EncException;
import com.buybal.epay.util.PaySign;

import xdt.quickpay.hengfeng.comm.Constant;
import xdt.quickpay.hengfeng.entity.PayQueryRequestEntity;
import xdt.quickpay.hengfeng.entity.PayRequestEntity;
import xdt.quickpay.ysb.Request;
import xdt.quickpay.ysb.Util;
import xdt.util.UtilDate;

/**
 * @ClassName: HFUtil
 * @Description:恒丰相关工具类
 * @author LiShiwen
 * @date 2016年6月14日 上午10:15:37
 *
 */
public class HFUtil {
	
	  private static final String CHARSET = "UTF-8";
	  private static final String CONTENT_TYPE = "application/x-www-form-urlencoded";
	  private static final Integer TIME_OUT = Integer.valueOf(10000);

	/**
	 * 生成商户订单号
	 * 
	 * @return 返回订单号
	 */
	public static String randomOrder() {

		SimpleDateFormat fmt = new SimpleDateFormat("yyyyMMddHHmmssSSS");

		StringBuffer sb = new StringBuffer();

		sb.append("ZF");

		sb.append(fmt.format(new Date()));

		return sb.toString();
	}
	/**
	 * 生成恒丰商户订单号
	 * 
	 * @return 返回订单号
	 */
	public static String HFrandomOrder() {

		SimpleDateFormat fmt = new SimpleDateFormat("yyyyMMddHHmmssS");

		StringBuffer sb = new StringBuffer();

		sb.append(fmt.format(new Date()));

		return sb.toString();
	}
	/**
	 * 返回时间
	 * 
	 * @return
	 */
	public static String dateTime() {
		
		SimpleDateFormat fmt = new SimpleDateFormat("yyyyMMddHHmmss");
		
		StringBuffer sb = new StringBuffer();
		
		sb.append(fmt.format(new Date()));
		
		return sb.toString();
	}

	/**
	 * 生成支付请求签名
	 * 
	 * @param req
	 *            支付请求信息
	 * @param merchantkey
	 *            商户key
	 * @return
	 */
	public static String sign(PayRequestEntity req, String merchantkey) {

		System.out.println("商户key:" + merchantkey);

		String sign = null;

		String dataString = null;

		PaySign paySign = new PaySign();

		// 支付平台时的组成加密串
		dataString = PreSginUtil.paySigiString(req);

		try {
			sign = paySign.sign(dataString, merchantkey);
			System.out.println("交易密钥："+sign);
		} catch (EncException e) {
			e.printStackTrace();
		}

		return sign;
	}
	  public static String sendPost(Request request, String url)
			    throws Exception
			  {
			    String result = null;
			    
			    String mab = Util.getMab(request);
			    

			    request.put("mac", Util.getMac(mab));
			    

			    String sendData = Util.getSendData(request);
			    result = post(url, sendData, TIME_OUT.intValue());
			    return result;
			  }
	

	/**
	 * 生成查询支付结果签名
	 * 
	 * @param req
	 * @param merchantkey
	 * @return
	 */
	public static String querySign(PayQueryRequestEntity req, String merchantkey) {
		
		System.out.println("商户key:" + merchantkey);

		String sign = null;

		String dataString = null;

		PaySign paySign = new PaySign();
		
		dataString = PreSginUtil.payQuerySignString(req);

		try {
			sign = paySign.sign(dataString, merchantkey);
			System.out.println("查询交易密钥："+sign);
		} catch (EncException e) {
			System.err.println("查询支付结果签名生成失败");
		}
		
		return sign;
	}
	  public static String post(String url, String parameterData, int timeOut)
	    throws Exception
	  {
	    URL localURL = new URL(url);
	    URLConnection connection = localURL.openConnection();
	    HttpURLConnection httpURLConnection = (HttpURLConnection)connection;
	    
	    httpURLConnection.setDoOutput(true);
	    httpURLConnection.setRequestMethod("POST");
	    httpURLConnection.setRequestProperty("Accept-Charset", "UTF-8");
	    httpURLConnection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
	    httpURLConnection.setRequestProperty("Content-Length", String.valueOf(parameterData.length()));
	    httpURLConnection.setReadTimeout(timeOut);
	    OutputStream outputStream = null;
	    OutputStreamWriter outputStreamWriter = null;
	    InputStream inputStream = null;
	    InputStreamReader inputStreamReader = null;
		BufferedReader reader = null;
	    StringBuffer resultBuffer = new StringBuffer();
	    String tempLine = null;
	    try
	    {
	      outputStream = httpURLConnection.getOutputStream();
	      outputStreamWriter = new OutputStreamWriter(outputStream);
	      outputStreamWriter.write(parameterData.toString());
	      outputStreamWriter.flush();
	      if (httpURLConnection.getResponseCode() >= 300) {
	        throw new Exception("HTTP Request is not success, Response code is " + httpURLConnection.getResponseCode());
	      }
	      inputStream = httpURLConnection.getInputStream();
	      inputStreamReader = new InputStreamReader(inputStream, Charset.forName("UTF-8"));
	      reader = new BufferedReader(inputStreamReader);
	      while ((tempLine = reader.readLine()) != null) {
	        resultBuffer.append(tempLine);
	      }
	    }
	    finally
	    {
	      if (outputStreamWriter != null) {
	        outputStreamWriter.close();
	      }
	      if (outputStream != null) {
	        outputStream.close();
	      }
	      if (reader != null) {
	        reader.close();
	      }
	      if (inputStreamReader != null) {
	        inputStreamReader.close();
	      }
	      if (inputStream != null) {
	        inputStream.close();
	      }
	      if (httpURLConnection != null) {
	        httpURLConnection.disconnect();
	      }
	    }
	    return resultBuffer.toString();
	  }
	

	public static void main(String[] args) {
		// 生成订单号
		// System.out.println(randomOrder());

		// 测试支付
		PayRequestEntity req = new PayRequestEntity();
		req.setPageurl("http://test.rytpay.com/ppayTestMer/result.jsp");
		req.setBgurl("http://test.rytpay.com/ppayTestMer/index.jsp");
		req.setPid("105290054110500");
		req.setTransactionid(randomOrder());
		req.setOrderamount("0.01");
		req.setOrdertime(UtilDate.getOrderNum());
		req.setProductname("测试商品");
		req.setProductnum("1");
		req.setProductdesc("测试商品");
		req.setBankid("SLT");
		req.setPaytype("13");
		sign(req, Constant.MERCHANT_KEY);
		
		PayQueryRequestEntity queryRequest=new PayQueryRequestEntity();
		queryRequest.setMerId("105290054110500");
		queryRequest.setTransactionId(randomOrder());
		querySign(queryRequest, Constant.MERCHANT_KEY);
		
		

	}

}
