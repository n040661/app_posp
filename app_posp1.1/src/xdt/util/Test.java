package xdt.util;

import java.net.URLDecoder;
import java.security.PrivateKey;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;
import java.util.TimeZone;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import com.google.gson.Gson;
//import com.kspay.cert.LoadKeyFromPKCS12;

import net.sf.json.JSON;
import net.sf.json.JSONObject;
import sun.misc.BASE64Decoder;
import xdt.quickpay.cjt.util.RSA;
import xdt.quickpay.hengfeng.util.HttpClientUtil;
import xdt.quickpay.hf.comm.SampleConstant;
import xdt.quickpay.hf.entity.PayRequestEntity;
import xdt.quickpay.hf.entity.PayResponseEntity;
import xdt.dto.jsds.JsdsResponseDto;
import xdt.dto.nbs.webpay.WechatWebPay;
import xdt.dto.nbs.webpay.WechatWebPayResponse;
import xdt.dto.payeasy.DaifuRequestEntity;
import xdt.pufa.PufaSignUtil;
import xdt.pufa.Sign;
import xdt.pufa.security.PuFaSignUtil;
import xdt.quickpay.hengfeng.util.Bean2QueryStrUtil;
import xdt.quickpay.hengfeng.util.HFSignUtil;
import xdt.quickpay.hengfeng.util.PreSginUtil;

public class Test {

	// json工具
	protected static Gson gson = new Gson();

	private static String public_key = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCIRuTinaFSATJFYnjeS5LTkdZB/Q35YrFVb5J3QrTRHIOERJ6I9kC0I0Iao3epVUVw657Ib0VwOtBDUrGmma4Hbz5Ybt56W7eJEyyv/VYWFteTzJYhpUCqc+WfnXYOw9aRmSKqkzedykqblxsnrQGOsv/jjoHBHpNW5FNr161XVQIDAQAB";

	private static String fact_public_key = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCIRuTinaFSATJFYnjeS5LTkdZB/Q35YrFVb5J3QrTRHIOERJ6I9kC0I0Iao3epVUVw657Ib0VwOtBDUrGmma4Hbz5Ybt56W7eJEyyv/VYWFteTzJYhpUCqc+WfnXYOw9aRmSKqkzedykqblxsnrQGOsv/jjoHBHpNW5FNr161XVQIDAQAB";

	public static String deUnicode(String content) {// 将16进制数转换为汉字
		String enUnicode = null;
		String deUnicode = null;
		for (int i = 0; i < content.length(); i++) {
			if (enUnicode == null) {
				enUnicode = String.valueOf(content.charAt(i));
			} else {
				enUnicode = enUnicode + content.charAt(i);
			}
			if (i % 4 == 3) {
				if (enUnicode != null) {
					if (deUnicode == null) {
						deUnicode = String.valueOf((char) Integer.valueOf(enUnicode, 16).intValue());
					} else {
						deUnicode = deUnicode + String.valueOf((char) Integer.valueOf(enUnicode, 16).intValue());
					}
				}
				enUnicode = null;
			}

		}
		return deUnicode;
	}
	public static String decode(String s, String enc)  
		       throws UnsupportedEncodingException{  
		  
		       boolean needToChange = false;  
		       int numChars = s.length();  
		       StringBuffer sb = new StringBuffer(numChars > 500 ? numChars / 2 : numChars);  
		       int i = 0;  
		  
		       if (enc.length() == 0) {  
		           throw new UnsupportedEncodingException ("URLDecoder: empty string enc parameter");  
		       }  
		  
		       char c;  
		       byte[] bytes = null;  
		       while (i < numChars) {  
		           c = s.charAt(i);  
		           switch (c) {  
		           case '+':  
		               sb.append(' ');  
		               i++;  
		               needToChange = true;  
		               break;  
		           case '%':  
		               /* 
		                * Starting with this instance of %, process all 
		                * consecutive substrings of the form %xy. Each 
		                * substring %xy will yield a byte. Convert all 
		                * consecutive  bytes obtained this way to whatever 
		                * character(s) they represent in the provided 
		                * encoding. 
		                */  
		  
		               try {  
		  
		                   // (numChars-i)/3 is an upper bound for the number  
		                   // of remaining bytes  
		                   if (bytes == null)  
		                       bytes = new byte[(numChars-i)/3];  
		                   int pos = 0;  
		  
		                   while ( ((i+2) < numChars) &&  
		                           (c=='%')) {  
		                       int v = Integer.parseInt(s.substring(i+1,i+3),16);  
		                       if (v < 0)  
		                           throw new IllegalArgumentException("URLDecoder: Illegal hex characters in escape (%) pattern - negative value");  
		                       bytes[pos++] = (byte) v;  
		                       i+= 3;  
		                       if (i < numChars)  
		                           c = s.charAt(i);  
		                   }  
		  
		                   // A trailing, incomplete byte encoding such as  
		                   // "%x" will cause an exception to be thrown  
		  
		                   if ((i < numChars) && (c=='%'))  
		                       throw new IllegalArgumentException(  
		                        "URLDecoder: Incomplete trailing escape (%) pattern");  
		  
		                   sb.append(new String(bytes, 0, pos, enc));  
		               } catch (NumberFormatException e) {  
		                   throw new IllegalArgumentException(  
		                   "URLDecoder: Illegal hex characters in escape (%) pattern - "  
		                   + e.getMessage());  
		               }  
		               needToChange = true;  
		               break;  
		           default:  
		               sb.append(c);  
		               i++;  
		               break;  
		           }  
		       }  
		  
		       return (needToChange? sb.toString() : s);  
		   }  
	public static String decode(String s) {  
		  
	       String str = null;  
	  
	       try {  
	           str = decode(s, "utf-8");  
	       } catch (UnsupportedEncodingException e) {  
	           // The system should always have the platform default  
	       }  
	  
	       return str;  
	   }  
	public static void main(String[] args) throws Exception {
		
//		System.out.println(URLDecoder.decode("\u672a\u77e5\u7ed3\u679c",""));
//		
//		WechatWebPayResponse temp=new WechatWebPayResponse();
//
//			temp.setBank_type("ABC_CREDIT");
//
//			temp.setFee_type("CNY");
//
//			temp.setReturn_msg("OK");
//
//			temp.setIs_subscribe("N");
//
//			temp.setNonce_str("959a7666b3a347abb4883c46398130ef");
//
//            temp.setOpenid("o0Od1wuoYTw3Ljrkdv1Bfsxq_lmk");
//			temp.setOut_trade_no("2201709131343125923un");
//
//			temp.setResult_code("SUCCESS");
//
//			temp.setReturn_code("SUCCESS");
//
//			temp.setTime_end("20170913134321");
//
//			temp.setTotal_fee("2000");
//			temp.setTrade_state("SUCCESS");
//
//			temp.setTrade_type("WECHAT_WEBPAY");
//		Bean2QueryStrUtil bean2Util = new Bean2QueryStrUtil();
//		String result1 = HttpURLConection.httpURLConnectionPOST("http://www.515mall.com/index.php?c=pay&m=thirdWxPayBack",
//				bean2Util.bean2QueryStr(temp));

   String aa="FR3/wATXs+E0qtQUbtNOxqqJ0XnOQ+N976DEFHbKC8dFLuSO35WDOguR8MF26RO244dVR+jzsnOvSNa1ll98D4A2HsaRw3+dlKnSSuWG0tmctCdcyxqnZrsYoPZB9vlAB+Irs7UP0wTP00ytBUnxL8ec2XDS89n5ReX+NusLh4sL72K3QMgzSDY6rGBEfBh40oGG5LS4xa+Jt30SaZSsi19nZkxkTzXh7J4ilhEoY1v4h2CMQA2sYiU1ep338lz13K5KRtAi1+ZVvMLEuQC5Z4yotl5n14TGnfwCLIzqGDltk9s4z5IZvdNPSs06AIotIN4lX8P+taUCOpOF9JI5qg==";
   String baseSign = URLDecoder.decode(aa, "UTF-8");

	baseSign = baseSign.replace(" ", "+");

	byte[] a = RSAUtil.verify("MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCIRuTinaFSATJFYnjeS5LTkdZB/Q35YrFVb5J3QrTRHIOERJ6I9kC0I0Iao3epVUVw657Ib0VwOtBDUrGmma4Hbz5Ybt56W7eJEyyv/VYWFteTzJYhpUCqc+WfnXYOw9aRmSKqkzedykqblxsnrQGOsv/jjoHBHpNW5FNr161XVQIDAQAB",
			RSAUtil.base64Decode(baseSign));

	String Str = new String(a);

	System.out.println("解析之后的数据:" + Str);
	
	String[] array = Str.split("\\&");
	String list2 = array[2].replaceAll("pl_url=", "");
	System.out.println("URL:" + URLDecoder.decode(list2, "UTF-8"));
	//byte[] a = RSAUtil.verify("MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCIRuTinaFSATJFYnjeS5LTkdZB/Q35YrFVb5J3QrTRHIOERJ6I9kC0I0Iao3epVUVw657Ib0VwOtBDUrGmma4Hbz5Ybt56W7eJEyyv/VYWFteTzJYhpUCqc+WfnXYOw9aRmSKqkzedykqblxsnrQGOsv/jjoHBHpNW5FNr161XVQIDAQAB",RSAUtil.base64Decode(baseSign));
	System.out.println("\u6b64\u4ea4\u6613\u53ea\u5141\u8bb8\u4fe1\u7528\u5361");
}
}