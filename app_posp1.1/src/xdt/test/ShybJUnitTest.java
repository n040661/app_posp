package xdt.test;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;

import xdt.dto.gateway.entity.GateWayQueryResponseEntity;
import xdt.dto.quickPay.entity.QueryResponseEntity;
import xdt.quickpay.hengfeng.util.Bean2QueryStrUtil;
import xdt.quickpay.hengfeng.util.HttpClientUtil;
import xdt.quickpay.nbs.common.util.MD5Util;
import xdt.quickpay.nbs.common.util.SignatureUtil;
import xdt.quickpay.nbs.common.util.StringUtil;
import xdt.util.BeanToMapUtil;
import xdt.util.HttpURLConection;

public class ShybJUnitTest {
	
	public static void main(String[] args) {
	
//		Map<String, String> result = new HashMap<String, String>();
//		result.put("v_oid", "QUI_1521772189417");
//		result.put("v_txnAmt", "10");
//		result.put("v_code", "00");
//		result.put("v_mid", "10034294463");
//		result.put("v_msg", "请求成功");
//		result.put("v_attach", "v_attach");
//		result.put("v_status_code", "200");
//		result.put("v_status_msg", "初始化");
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
//			html = HttpClientUtil.post("https://member.goldwang.cn/notify/changjiezhifu",result);
//		} catch (Exception e1) {
//			// TODO Auto-generated catch block
//			e1.printStackTrace();
//		} 
//		System.out.println("下游返回状态" + html);
		String params="bankCode=SPDB&amount=3&code=0000&splits=%7B%22splitType%22%3A%22TRADE_SPLITTER%22%2C%22totalAmount%22%3A%220.0%22%2C%22list%22%3A%5B%7B%22amount%22%3A%220.0%22%2C%22splitTime%22%3A%222018-03-27+17%3A01%3A03.431531%22%2C%22orderNo%22%3A%22672713618882019328%22%2C%22splitStatus%22%3A%22SUCCESS%22%2C%22splitCustomerNumber%22%3A%2210019598826%22%7D%5D%7D&payTime=2018-03-27+17%3A01%3A02&src=B&fee=0.01&payerPhone=183****6951&customerNumber=10020866509&message=%E6%88%90%E5%8A%9F&externalld=672713618882019328&lastNo=625958******7983&createTime=2018-03-27+16%3A59%3A26&requestId=QP20180327165835&hmac=97f05e3df1ffbc09304382b066c7efb7&payerName=%E5%AD%99*%E8%8E%B9&busiType=COMMON&status=SUCCESS";
		
		String[] array=params.split("\\&");
		System.out.println(array[array.length-1]);
		String key1 = params.split("&")[0].split("=")[0];
		String key2 = params.split("&")[1].split("=")[0];
		System.out.println(key1 + "--" + key2);
		String[] strs = params.split("&");
		Map<String, String> m = new HashMap<String, String>();
		for(String s:strs){
		String[] ms = s.split("=");
		m.put(ms[0], ms[1]);
		}
		System.out.println(m.get("status"));
		System.out.println(m.get("requestId"));
		
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
