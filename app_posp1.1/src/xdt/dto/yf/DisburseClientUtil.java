package xdt.dto.yf;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSONObject;

public class DisburseClientUtil {
	
	private static final String CHARSET = "UTF-8";
	
	private static Logger log = LoggerFactory.getLogger(DisburseClientUtil.class);
	
	public static String sendPost(String url , Map param ) throws Exception {
		String responseStr = "";
		try {
			log.debug("开始--调用裕福批量代付系统");
			log.debug("---请求url---"+url);
			log.debug("---请求param---"+param==null?"":JSONObject.toJSONString(param));
			responseStr = HttpClientUtils.sendPost(url, param, CHARSET);
			log.debug("返回结果--调用裕福批量代付系统--"+responseStr);
			log.debug("结束--调用裕福批量代付系统");
		} catch (Exception e) {
			e.printStackTrace();
			log.debug("异常--调用裕福批量代付系统"+e);
			log.error("异常--调用裕福批量代付系统"+e);
			throw e;
		}
		return responseStr;
	}
	
	public static String sendMutiPost(String url , Map param, String  filePath) throws Exception {
		String responseStr = "";
	    try {
	      log.debug("开始--调用裕福批量代付系统");
	      log.debug("---请求url---" + url);
	      log.debug("---请求param---" + param == null ? "" : JSONObject.toJSONString(param));
	      File rfile=new File(filePath);
	      InputStream fileInputStream = new FileInputStream(rfile);
	      byte[] bytes = new byte[fileInputStream.available()];
	      fileInputStream.read(bytes);
	      fileInputStream.close();

	      HTTPFile file = new HTTPFile(rfile.getName(), "data", bytes);
	      List filedata = new ArrayList();
	      filedata.add(file);
	      responseStr = HttpClientUtilBuilds.sendPost(url, param, filedata, "UTF-8");
	      log.debug("返回结果--调用裕福批量代付系统--" + responseStr);
	      log.debug("结束--调用裕福批量代付系统");
	    } catch (Exception e) {
	      e.printStackTrace();
	      log.debug("异常--调用裕福批量代付系统" + e);
	      log.error("异常--调用裕福批量代付系统" + e);
	      throw e;
	    }
	    return responseStr;
	}
	
	public static String sendPostInstream(String url , Map param ,String downloadFilePath) throws Exception {
		String responseStr = "";
		try {
			log.debug("开始--调用裕福批量代付系统");
			log.debug("---请求url---"+url);
			log.debug("---请求param---"+param==null?"":JSONObject.toJSONString(param));
			responseStr = HttpClientUtils.sendPostInstream(url, param, CHARSET,downloadFilePath);
			log.debug("返回结果--调用裕福批量代付系统--"+responseStr);
			log.debug("结束--调用裕福批量代付系统");
		} catch (Exception e) {
			e.printStackTrace();
			log.debug("异常--调用裕福批量代付系统"+e);
			log.error("异常--调用裕福批量代付系统"+e);
			throw e;
		}
		return responseStr;
	}
	
	public static void main(String[] args) {
		final String CHARSET = "UTF-8";
		String url = "http://unidev.auth.yufu.cn/authorize";
		String body = "client_id=yf_wallet&client_secret=s5&grant_type=authorization_code&redirect_uri=layoutid&code=UR6RIC";
		try {
			String result = HttpClientUtils.sendPost2Body(url, body, CHARSET);
		    System.out.println(result);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
