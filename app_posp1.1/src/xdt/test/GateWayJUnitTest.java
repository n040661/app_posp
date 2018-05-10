package xdt.test;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;

import com.sun.jndi.toolkit.url.Uri;

import xdt.dto.gateway.entity.GateWayQueryResponseEntity;
import xdt.dto.quickPay.entity.QueryResponseEntity;
import xdt.quickpay.hddh.util.Base64;
import xdt.quickpay.hengfeng.util.Bean2QueryStrUtil;
import xdt.quickpay.hengfeng.util.HttpClientUtil;
import xdt.quickpay.nbs.common.util.MD5Util;
import xdt.quickpay.nbs.common.util.SignatureUtil;
import xdt.quickpay.nbs.common.util.StringUtil;
import xdt.util.BeanToMapUtil;
import xdt.util.HttpURLConection;

public class GateWayJUnitTest {
	
	public static void main(String[] args) {
	
		Map<String, String> result = new HashMap<String, String>();
		result.put("v_oid", "QUI_1521772189417");
		result.put("v_txnAmt", "10");
		result.put("v_code", "00");
		result.put("v_mid", "10034294463");
		result.put("v_msg", "请求成功");
		result.put("v_attach", "v_attach");
		result.put("v_status_code", "200");
		result.put("v_status_msg", "初始化");
//		GateWayQueryResponseEntity gatewey = (GateWayQueryResponseEntity) BeanToMapUtil
//				.convertMap(GateWayQueryResponseEntity.class, result);
//		 String sign = getSign(beanToMap(gatewey), "7d2feb66b6474964adcbfebe82e29744");
//		QueryResponseEntity queryconsume = (QueryResponseEntity) BeanToMapUtil
//				.convertMap(QueryResponseEntity.class, result);
//		System.out.println("---返回数据签名签的数据:" + beanToMap(queryconsume));
//		 String sign = getSign(beanToMap(queryconsume), "7d2feb66b6474964adcbfebe82e29744");
//		 result.put("v_sign", sign);
//		 GateWayQueryResponseEntity gatewey1= (GateWayQueryResponseEntity)
//		 BeanToMapUtil.convertMap(GateWayQueryResponseEntity.class, result);
//		 Bean2QueryStrUtil bean2Util = new Bean2QueryStrUtil();
//		 String html="";
//		 try {
//			System.out.println("甬易给下游异步后的数据:" + bean2Util.bean2QueryStr(gatewey1));
//			html = HttpClientUtil.post("http://175w302m69.51mypc.cn/app/gateway/payback",result);
//		} catch (Exception e1) {
//			// TODO Auto-generated catch block
//			e1.printStackTrace();
//		} 
//		System.out.println("下游返回状态" + html);
//		String str1="1345.01";
//
//		DecimalFormat df=new DecimalFormat("000000000000");
//
//		String str2=df.format(Double.parseDouble(str1)*100);
//
//		System.out.println(str2);
		String aa="PGtDb2xsIGlkPSJpbnB1dCIgYXBwZW5kPSJmYWxzZSI%2BPGZpZWxkIGlkPSJtYXN0ZXJJZCIgdmFs%0AdWU9IjIwMDA4NDQ3MzUiLz48ZmllbGQgaWQ9ImFtb3VudCIgdmFsdWU9IjEwMC4wMCIvPjxmaWVs%0AZCBpZD0ib3JkZXJJZCIgdmFsdWU9IjIwMDA4NDQ3MzUwNDEwNTM2MTIzNjMiLz48ZmllbGQgaWQ9%0AImN1cnJlbmN5IiB2YWx1ZT0iUk1CIi8%2BPGZpZWxkIGlkPSJvYmplY3ROYW1lIiB2YWx1ZT0isuLK%0A1CIvPjxmaWVsZCBpZD0idmFsaWR0aW1lIiB2YWx1ZT0iMCIvPjxmaWVsZCBpZD0icmVtYXJrIiB2%0AYWx1ZT0ie2FjY2Vzc01pZD0xNDI3MDUyNn0iLz48ZmllbGQgaWQ9InBheWRhdGUiIHZhbHVlPSIy%0AMDE4MDQxMDExMjE0OCIvPjxmaWVsZCBpZD0icGF5VHlwZSIgdmFsdWU9IjAyIi8%2BPGZpZWxkIGlk%0APSJpc3NJbnNDb2RlIiB2YWx1ZT0iQUJDIi8%2BPGZpZWxkIGlkPSJwYXlDYXJkVHlwZSIgdmFsdWU9%0AIjAwIi8%2BPC9rQ29sbD4%3D%0A";

			//String ToSubmitHTML = new String(Base64.decode(aa),"UTF-8");
			System.out.println(URLEncoder.encode(aa));    
		
	}
	public static String getSign(Map<String, Object> map, String key) {
		ArrayList<String> list = new ArrayList<String>();
		for (Map.Entry<String, Object> entry : map.entrySet()) {
			if (entry.getValue() != null && StringUtil.isNotBlank(String.valueOf(entry.getValue()))) {
				list.add(entry.getKey() + "=" + entry.getValue() + "&");
			}
		}
		int size = list.size();
		String[] arrayToSort = list.toArray(new String[size]);
		Arrays.sort(arrayToSort, String.CASE_INSENSITIVE_ORDER);
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < size; i++) {
			sb.append(arrayToSort[i]);
		}
		String result = sb.toString();	
		result = result.substring(0, result.length() - 1);
		result += key;
		System.out.println("Sign Before MD5: {}"+result);
		result = MD5Util.MD5Encode(result).toUpperCase();
		System.out.println("Sign Result: {}"+result);
		return result;

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
	}
