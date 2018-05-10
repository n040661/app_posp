package xdt.test;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSON;
import com.sun.java_cup.internal.runtime.Symbol;
import com.sun.org.apache.xalan.internal.xsltc.compiler.sym;

import cn.com.sandpay.cashier.sdk.util.CertUtil;
import net.sf.json.JSONArray;
import xdt.dto.gateway.entity.GateWayResponseEntity;
import xdt.quickpay.gyy.util.ApiUtil;
import xdt.quickpay.hengfeng.util.Bean2QueryStrUtil;
import xdt.quickpay.hengfeng.util.HttpClientUtil;
import xdt.quickpay.jbb.util.Base64;
import xdt.quickpay.nbs.common.util.MD5Util;
import xdt.quickpay.nbs.common.util.SignatureUtil;
import xdt.util.BeanToMapUtil;
import xdt.util.HttpURLConection;
import xdt.util.StringUtils;

public class QuickJUnitTest {
	
	private  Logger logger = LoggerFactory.getLogger(this.getClass());
	
	public static void main(String[] args) {
		
//		Map<String, String> result = new HashMap<String, String>();
//		result.put("v_oid", "KZRY974459132922822656");
//		result.put("v_txnAmt", "50.00");
//		result.put("v_code", "00");
//		result.put("v_time", "20180316093536");
//		result.put("v_mid", "10041276998");
//		result.put("v_msg", "请求成功");
//		result.put("v_status", "0000");
//		result.put("v_status_msg", "支付成功");
//		result.put("v_attach", "v_attach");
//		String params = HttpURLConection.parseParams(result);
//		String result1 = params.toString();	
//		//result1 = result1.substring(0, result1.length() - 1);
//		result1 +="b57355b60fb14e9899c555c98a9d5ff8";
//		System.out.println(result1);
//		String sign =  MD5Util.MD5Encode(result1).toUpperCase();
//		result.put("v_sign", sign);
//		params = HttpURLConection.parseParams(result);
//		//String params1 = "v_mid=10036049569&v_oid=66662018031510437844&v_txnAmt=10.0&v_code=00&v_msg=支付成功&v_time=1539&v_attach=644b57629daa46b6aae6feafaef88152&v_status=0000&v_sign=87B40DDD8F00525E5C230E5256E9AF7A";
//		System.out.println("给下游异步的数据:" + params);
//		Bean2QueryStrUtil bean2Util = new Bean2QueryStrUtil();
//		String html="";
//		try {
//			html = HttpClientUtil.post("http://101.200.38.184:8008/gateway/notify/async/upin/UPIN20180423100089","merid=10036046733&cooperator_user_id=66662018042610440738&repayPlanId=01-20180426135025-001325&cooperator_item_id=66662018042613500014&status=Repaying&channelStatus=3&v_sign=1A0BA976C9C69E909107B55337E584BA");
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//		System.out.println("下游返回状态" + html);
//		try {
//			
//			String param = "DQoNCg0KDQoNCg0KDQogDQogDQogDQoNCg0KPCFET0NUWVBFIEhUTUwgUFVCTElDICItLy9XM0Mv\\nL0RURCBIVE1MIDQuMDEvL0VOIiAiaHR0cDovL3d3dy53My5vcmcvVFIvaHRtbDQvc3RyaWN0LmR0\\nZCI+DQoNCjxodG1sPg0KPGhlYWQ+DQo8dGl0bGU+PGxpYW5hOkkxOE4gbmFtZT0i5bmz5a6J572R\\n5LiK6ZO26KGMIi8+PC90aXRsZT4NCg0KDQo8L2hlYWQ+DQo8Ym9keSBvbmxvYWQ9ImRvY3VtZW50\\nLmF1dG9KdW1wRm9ybS5zdWJtaXQoKTsiPg0KPGZvcm0gbmFtZT0iYXV0b0p1bXBGb3JtIiBtZXRo\\nb2Q9InBvc3QiIGFjdGlvbj0iaHR0cHM6Ly8xMDEuMjMxLjIwNC44MDo1MDAwL2dhdGV3YXkvYXBp\\nL2Zyb250VHJhbnNSZXEuZG8iPg0KCQkJDQoNCiAgICAgICAgICAgIDxpbnB1dCB0eXBlPWhpZGRl\\nbiBuYW1lPSJ2ZXJzaW9uIiBpZD0idmVyc2lvbiIgdmFsdWU9IjUuMC4wIj4NCiAgICAgICAgICAg\\nIDxpbnB1dCB0eXBlPWhpZGRlbiBuYW1lPSJlbmNvZGluZyIgaWQ9ImVuY29kaW5nIiB2YWx1ZT0i\\nVVRGLTgiPg0KICAgICAgICAgICAgPGlucHV0IHR5cGU9aGlkZGVuIG5hbWU9InNpZ25NZXRob2Qi\\nIGlkPSJzaWduTWV0aG9kIiB2YWx1ZT0iMDEiPg0KICAgICAgICAgICAgPGlucHV0IHR5cGU9aGlk\\nZGVuIG5hbWU9InR4blR5cGUiIGlkPSJ0eG5UeXBlIiB2YWx1ZT0iNzkiPg0KICAgICAgICAgICAg\\nPGlucHV0IHR5cGU9aGlkZGVuIG5hbWU9InR4blN1YlR5cGUiIGlkPSJ0eG5TdWJUeXBlIiB2YWx1\\nZT0iMDAiPg0KICAgICAgICAgICAgPGlucHV0IHR5cGU9aGlkZGVuIG5hbWU9ImJpelR5cGUiIGlk\\nPSJiaXpUeXBlIiB2YWx1ZT0iMDAwMzAxIj4NCiAgICAgICAgICAgIDxpbnB1dCB0eXBlPWhpZGRl\\nbiBuYW1lPSJjaGFubmVsVHlwZSIgaWQ9ImNoYW5uZWxUeXBlIiB2YWx1ZT0iMDciPg0KICAgICAg\\nICAgICAgPGlucHV0IHR5cGU9aGlkZGVuIG5hbWU9ImFjY2Vzc1R5cGUiIGlkPSJhY2Nlc3NUeXBl\\nIiB2YWx1ZT0iMSI+DQogICAgICAgICAgICA8aW5wdXQgdHlwZT1oaWRkZW4gbmFtZT0iYWNxSW5z\\nQ29kZSIgaWQ9ImFjcUluc0NvZGUiIHZhbHVlPSIwNDEwMzkzMCI+DQogICAgICAgICAgICA8aW5w\\ndXQgdHlwZT1oaWRkZW4gbmFtZT0ibWVyQ2F0Q29kZSIgaWQ9Im1lckNhdENvZGUiIHZhbHVlPSI1\\nOTk4Ij4NCiAgICAgICAgICAgIDxpbnB1dCB0eXBlPWhpZGRlbiBuYW1lPSJtZXJOYW1lIiBpZD0i\\nbWVyTmFtZSIgdmFsdWU9IuW5s+WuiemTtuihjCI+DQogICAgICAgICAgICA8aW5wdXQgdHlwZT1o\\naWRkZW4gbmFtZT0ibWVyQWJiciIgaWQ9Im1lckFiYnIiIHZhbHVlPSLlubPlronpk7booYwiPg0K\\nICAgICAgICAgICAgPGlucHV0IHR5cGU9aGlkZGVuIG5hbWU9ImNlcnRJZCIgaWQ9ImNlcklkIiB2\\nYWx1ZT0iNjg3NTk1MjkyMjUiPg0KICAgICAgICAgICAgPGlucHV0IHR5cGU9aGlkZGVuIG5hbWU9\\nInNpZ25hdHVyZSIgaWQ9InNpZ25hdHVyZSIgdmFsdWU9IkdFenVXb1VaU2FtTDFlc291UFkwdU1l\\nRWtRd2lqVTZwc1RuVm9lSHVObzRaVzFodmZWeXZvNkpsUWg5dTh4UDgvUXNIR2dKQnVWMXRiSTl3\\nUjlUdzl1eVhVQmpSRm1VN0JHL2hPVFd3VmY1cTg5akRTOVQyVWZTK0pZMFVaRmlQKzc2VXVoanRD\\nemtEMzB0MUJZUUZrUlJkVnBMWGt4WG9LbmcxWk45UEx0a2ZUeU1aNjE5amtYdG56R2V0Y1FZOUdm\\nQmhXdElGWUVJcFVER0d2MUFjMk03ek04VGZqSTN3eStMbk9HYjdjWHpqSnMxWW55V2tWR0FDM0d6\\nV0k3cWRYZE43QVdXMThTTHFEYVBKTk8xSHZidURnSG5sWDNkUGx4cnlHZGhCazVjMERjenJXWGph\\nTlNJY1pDeHF6TDBqMTBZaVZpbzJEaEpMc0pQOW02YngwZz09Ij4NCiAgICAgICAgICAgIDxpbnB1\\ndCB0eXBlPWhpZGRlbiBuYW1lPSJvcmRlcklkIiBpZD0ib3JkZXJJZCIgdmFsdWU9IjIwMDAzMTEx\\nNDYyMDE4MDQwNDAwMDA2MzM0Ij4NCiAgICAgICAgICAgIDxpbnB1dCB0eXBlPWhpZGRlbiBuYW1l\\nPSJ0eG5UaW1lIiBpZD0idHhuVGltZSIgdmFsdWU9IjIwMTgwNDA0MTY1NDU3Ij4NCiAgICAgICAg\\nICAgIDxpbnB1dCB0eXBlPWhpZGRlbiBuYW1lPSJtZXJJZCIgaWQ9Im1lcklkIiB2YWx1ZT0iNDEw\\nMzUwMjU5OTgwMTk1Ij4NCiAgICAgICAgICAgIDxpbnB1dCB0eXBlPWhpZGRlbiBuYW1lPSJiYWNr\\nVXJsIiBpZD0iYmFja1VybCIgdmFsdWU9Imh0dHBzOi8vbXktdWF0MS5vcmFuZ2ViYW5rLmNvbS5j\\nbi9raHBheW1lbnQvUGF5VW5pb25BUElfT3Blbk5vdGlmeS5kbyI+DQogICAgICAgICAgIDxpbnB1\\ndCB0eXBlPWhpZGRlbiBuYW1lPSJmcm9udFVybCIgaWQ9ImZyb250VXJsIiB2YWx1ZT0iaHR0cHM6\\nLy9teS11YXQxLm9yYW5nZWJhbmsuY29tLmNuL2tocGF5bWVudC9QYXlVbmlvbkFQSV9PcGVuUmV0\\ndXJuLmRvP3JldHVybnVybD1hSFIwY0Rvdkx6VTRMalUyTGpJekxqZzVPamN3TURJdlRtVjBVR0Y1\\nTDFCQlFsRjFhV05yVTJsbmJrNXZkR2xtZVM1aFkzUnBiMjQlM0QlMEEiPg0KICAgICAgICAgICAg\\nPGlucHV0IHR5cGU9aGlkZGVuIG5hbWU9InJlcVJlc2VydmVkIiBpZD0icmVxUmVzZXJ2ZWQiIHZh\\nbHVlPSIyMDAwMzExMTQ2IzEyMDEwNTE5NzUxMDA1NTQyMCMxMTEjaHR0cDovLzU4LjU2LjIzLjg5\\nOjcwMDIvTmV0UGF5L1BBQlF1aWNrU2lnbk5vdGlmeS5hY3Rpb24iPg0KICAgICAgICAgIA0KPC9m\\nb3JtPg0KPC9ib2R5Pg0KPC9odG1sPg0K\\n";
//			StringUtils st=new StringUtils();
//			String params=param.replaceAll("\\\\[n]", "");
//			 System.out.println(params);
//			String ToSubmitHTML = new String(Base64.decode(params),"UTF-8");
//		    System.out.println(ToSubmitHTML);
//		} catch (UnsupportedEncodingException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//		String str = "0423";
//		char[] cc=str.toCharArray();
//		
//		System.err.println(cc[0]);
//		System.err.println(cc[1]);
//		System.err.println(cc[2]);
//		System.err.println(cc[3]);
//		StringBuffer bb=new StringBuffer();
//		bb.append(cc[2]);
//		bb.append(cc[3]);
//		bb.append(cc[0]);
//		bb.append(cc[1]);
//		System.out.println(bb.toString());
//		String replaceAll = str.substring(str.length()-2,str.length());
//		String replaceAlls = str.substring(0,2);
//		System.err.println(replaceAll);
//		System.err.println(str.length());
//		String strs=str.replaceAll("", "");
			
		//String aa="eyJqdW1wVXJsIjoiaHR0cDovL3Bvcy55ZWFoa2EuY29tL2xlcG9zd2ViL2NyZWRpdGNhcmQvYXBpL2FnZW50RW50cmFuY2UuZG8/YWdlbnRJZD0xODI4MzcwJmNhbGxCYWNrVXJsPWh0dHA6Ly9hcGkua3VhaWt1YWlmdS5uZXQveXBhcGkvcmVwYXkvMDEtMjAxODA0MTAxODE0NDctMDAwMTcxL2JpbmRDYXJkTm90aWZ5LmRvJmRhdGVUaW1lPTIwMTgwNDEwMTgxNCZqb2luVHlwZT1INSZ1c2VySWQ9YWRlNjkwYjRlYWVkNGIwNDAzNmMmdmVyc2lvbj0xLjAmc2lnbj04OUY0MDE1ODg3QURDQTM5N0I0Q0U2MjM5NUE2Qzk4OCIsInJlcGF5T3JkZXJJZCI6IjAxLTIwMTgwNDEwMTgxNDQ3LTAwMDE3MSIsImNvb3BlcmF0b3JfdXNlcl9pZCI6IjE4MTQ0MyIsInJlcGF5X2NoYW5uZWxfdXNlcl9pZCI6ImFkZTY5MGI0ZWFlZDRiMDQwMzZjIiwiY29vcGVyYXRvcl9vcmRlcl9pZCI6IlFQMjAxODA0MTAxODE0NDAyNDc2NDkifQ==";
		
		
//		try {		
//			Map<String, String> map=new HashMap<String,String>();
//			String json="{\"code\":0,\"data\":\"eyJqdW1wVXJsIjoiaHR0cDovL3Bvcy55ZWFoa2EuY29tL2xlcG9zd2ViL2NyZWRpdGNhcmQvYXBpL2FnZW50RW50cmFuY2UuZG8/YWdlbnRJZD0xODI4MzcwJmNhbGxCYWNrVXJsPWh0dHA6Ly9hcGkua3VhaWt1YWlmdS5uZXQveXBhcGkvcmVwYXkvMDEtMjAxODA0MTExMDA5MzEtMDAwMDkxL2JpbmRDYXJkTm90aWZ5LmRvJmRhdGVUaW1lPTIwMTgwNDExMTAwOSZqb2luVHlwZT1INSZ1c2VySWQ9OGIzNjllMzhmZGIyMzFkZDY1Y2QmdmVyc2lvbj0xLjAmc2lnbj0xRkQxNDJGRDE2RTc5NUFERjc0REZBQUYxNzU4NjQzNSIsInJlcGF5T3JkZXJJZCI6IjAxLTIwMTgwNDExMTAwOTMxLTAwMDA5MSIsImNvb3BlcmF0b3JfdXNlcl9pZCI6IjEwMDkyNSIsInJlcGF5X2NoYW5uZWxfdXNlcl9pZCI6IjhiMzY5ZTM4ZmRiMjMxZGQ2NWNkIiwiY29vcGVyYXRvcl9vcmRlcl9pZCI6IlFQMjAxODA0MTExMDA5MjMyMjM3MTUifQ==\",\"success\":true,\"sign\":\"edef70c37d9e1ef3b0c84ebf53e103ed\",\"message\":\"操作成功\"}";
//			map = ApiUtil.toMap(json);
//			System.out.println(map.get("data"));
//			String plainText = new String(Base64.decode(map.get("data")),Charset.forName("UTF-8"));
//			 map= ApiUtil.toMap(plainText);
//			System.out.println(map.get("jumpUrl"));
//		} catch (Exception e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//		Integer amount=(int) (Double.parseDouble("1")*100);
//		String amounts=amount.toString();
//		System.out.println(amounts);
//		String data="eyJjb29wZXJhdG9yX3VzZXJfaWQiOiI2NjY2MjAxODA0MjUxMDQ0MDY4MyIsInJlcGF5UGxhbklkIjoiMDEtMjAxODA0MjUxNjE2NDQtMDAwOTAyIiwiY29vcGVyYXRvcl9pdGVtX2lkIjoiNjY2NjIwMTgwNDI1MTYxNjEyNzYiLCJzdGF0dXMiOiJTdWNjZXNzIiwiY2hhbm5lbFN0YXR1cyI6OX==";
//		try {			
//			String isoString = new String(xdt.quickpay.hddh.util.Base64.decode(URLDecoder.decode(data,"UTF-8")),Charset.forName("UTF-8"));
//			System.out.println(isoString);
//			Map mapTypes = JSON.parseObject(isoString);  
//			System.out.println(mapTypes.get("card_id"));
//		} catch (Exception e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//		String aa="MIIEvAIBADANBgkqhkiG9w0BAQEFAASCBKYwggSiAgEAAoIBAQC7c3iWBnKBusNNCkS7KovThoju12grWE+sL8k9VHFGDWULgZ7eGq7hk94VAR672FCg8NBB71ruopL9GuO+APZuvMOinVa+HdH1JQ6PXniZQDLksi2GGaHFHvIj9Oi/0P+aKmx/lvB6hXTn4bHEsyrfSYlh4xx0wBdDwHyyS3CF0abb+v3FLDB8HxTwL8h8EobDCtGqpKDm2JoIi9IEmMMsNhOIfENG66/5hKa6UjpKV7mPxyJqq9gH/q0XYZCVPVtMOy02eh0YNsW9Gjc061JFedpSNAE0PhKl+MJ6BKG74awa6R+tqpNvoLW5bSTdg3aFec9QfgqE7B3qZEsERf1RAgMBAAECggEAc8Me8D+xDY7iN7ykr5XZ3lrSS/X5XfNe2K52MArrHWD+SvnUu8NsxuCDoSMc8NLpEEtHwcBovsDl16t4hAmiJscqZk2WmThfRm6JLF8iSTpNc9DZrlw7/DldPnlsF7GheHrEoJL9lhy/Esjd+OTREC4gyrPwCeXrOCsylwVhyzGbmx4QTjfjMisWQS6LssE/dRrexRQCFqSkW2v5xBjZ4P1FWcP17PxD9Cys/TMyaMrMOYFWjmX4VePa5efyxhmFFK/hSCwRt5Zc69c7CsixTaOQZvggaznY96lrRYnAXsOlxUYJ/Rt0Q1zik3PGZZ1DywBHl1612ZpXNsRErXUuBQKBgQDvmnpajyNUv7T5NkxClBuaJEfgoUw8YEAuubQHDJUN6N6YUdYe5cGFOdcgIOs480OqWaRUbojgO7Cx0bjqQ9ojyHL0omdKnFLjEuHEZnPYG1X3nd3WDO7et4MJ+UloVUt1xcd0nZn3S9m8jTqj8wtZ05Wo1y9RCmJWsDKbath5vwKBgQDIR1r7HfJu0MyAIXcafvQvI931EOSFLB8R84/Sz8UGguV8f0xe1MNQL1Ht8PLGl3rzj+S0EN4GYOhjLx+Alng3I+lG12cx3sWHDKPE2cPkNfz1wkfDfM/THVvZ3ltcKSmfwvUODSCPS19AxZZsfUXjK/rXZFzkP2pRXN9EBaCs7wKBgBaYFpmbjdVcDpDBkgdlOAUdDrob+7hlyyec02Emypd5MiRi8zIOsGUDw0mAUS4ZonPZ6CkeE0Ix3sOl5y4QH+3n5Q80kVDz1M0c5rVChAII8d5bwKOCrLPL0kuKemWgRXFhqSFSC0bKnEmvd6wmKxfZX+9/+zJFLGjITL1jIe65AoGAU+IXCCBl3uYTEQNzOCS+L1RNbYwyulhddw5VSc19zxlZ9sf2e62PVHFNVmRZurXNaF/3QTfjVjaWfpxJDHl5RlcDKImljiyo/MRdf2BZ/KZkGabSmd0Xymt8ggbwquwOo/xq2QTEg1/lOGLJew0JDXnpKPe/NFYfOI62cG4O+v8CgYBdNErJu51YlGVWDn1HjdoTcAynsMKIp5pVgD50HPWp9som3fItvT3V/yYaQYeJGpRbNERibCSzypGSuZgyN3YFIzGJLjG5HiCq7XQSRR7GsFpH80pKPT0PFLxvKefud3K3lJ7yeV4a2DMA3WmER/CzsuM+703iAfk00WpuJBFAYA==";
		//Integer tranAmt = 
		
		//System.out.println(tranAmt.toString());
		
//		String aa="2018-04-16 15:10:26|2018-04-16 16:10:26|50215|50000|215|2017082312$2018-04-16 15:25:26|2018-04-16 16:25:26|50215|50000|215|20170823234";
//		List<Map<String, String>> list=new ArrayList<Map<String, String>>();
//		
//		String[] array=aa.split("\\$");	
//		
//		for (int i = 0; i < array.length; i++) {
//			Map<String, String> map=new LinkedHashMap<String,String>();
//			String bb=array[i];
//			 String[] bb1=bb.split("\\|");
//			 String[] name= {"trade_time","transfer_time","trade_amount","transfer_amount","fee","cooperator_item_id"};
//			 for (int j = 0; j < bb1.length; j++) {
//			
//			   map.put(name[j], bb1[j]);			 
//			}
//			list.add(map);
//			System.out.println();
//		}
//		 JSONArray json = JSONArray.fromObject(list);
//		 
//		 String cc=json.toString();
//		 
//		// com.alibaba.fastjson.JSONObject jsonObject = JSON.parseObject(cc);
//		 System.out.println(list);
//		System.out.println(json);
//		System.out.println(cc.replaceAll("\\[", "").replaceAll("\\]", ""));
//		try {
//			CertUtil.init("classpath:厦门聚佰宝商贸有限公司.cer", "classpath:CA20171129111100厦门聚佰宝商贸有限公司.pfx", "11111111");
//		} catch (Exception e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}	
		String subject="%E9%87%8D%E5%BA%86%E5%B0%8F%E9%9D%A2";
		
		String sub="";
		try {
			sub = URLDecoder.decode(subject);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println(sub);
	}
	/**
	 * bean 转化为实体
	 * 
	 * @param bean
	 * @return
	 */
	public static HashMap<String, Object> beanToMap(Object bean) {
		HashMap<String, Object> map = new HashMap<String, Object>();
		if (null == bean) {
			return map;
		}
		Class<?> clazz = bean.getClass();
		BeanInfo beanInfo = null;
		try {
			beanInfo = Introspector.getBeanInfo(clazz);
		} catch (IntrospectionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		PropertyDescriptor[] descriptors = beanInfo.getPropertyDescriptors();
		for (PropertyDescriptor descriptor : descriptors) {
			String propertyName = descriptor.getName();
			if (!"class".equals(propertyName)) {
				Method method = descriptor.getReadMethod();
				String result;
				try {
					result = (String) method.invoke(bean);
					if (null != result) {
						map.put(propertyName, result);
					} else {
						map.put(propertyName, "");
					}
				} catch (IllegalArgumentException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IllegalAccessException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (InvocationTargetException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}

		return map;
	}
//	public static String  TrimByString(String zfc,String qz,String hz) {
//		zfc=StringUtils.trim(StringUtils.substringBetween(zfc, qz, hz));
//		return zfc;
//	}
//    private String rtnNoHtml(String con){
//		
//		return convertHtml2TxtMedia(con, true, true).getTxts().toString().replace("[", "").replace("]", "");
//	}

}
