package xdt.quickpay.cjt.utils;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.lang.StringUtils;

public class ChanpayGatewayDemo {

	/**
	 * 畅捷支付平台公钥
	 */
	// 生产测试环境
	private static String MERCHANT_PUBLIC_KEY = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQDPq3oXX5aFeBQGf3Ag/86zNu0VICXmkof85r+DDL46w3vHcTnkEWVbp9DaDurcF7DMctzJngO0u9OG1cb4mn+Pn/uNC1fp7S4JH4xtwST6jFgHtXcTG9uewWFYWKw/8b3zf4fXyRuI/2ekeLSstftqnMQdenVP7XCxMuEnnmM1RwIDAQAB";// 生产测试环境
	// private static String MERCHANT_PUBLIC_KEY =
	// "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQDONXNe9xgdWykFwTLRLKWKmGQC6ZLp66tqLRoUlvjUJnwoej8aD+KUuimcOXpIh9XuTDEO0YYh/D5xtnEN+q2wvZzK3G2l+xEirCowE7CM388t/yplGdJMw81CSaUQUeAz/5NCwbXA8i8OTv8/h0kLIdO/omMD8aJKgpmtyJ3IEQIDAQAB";
	// T环境
	// private static String MERCHANT_PUBLIC_KEY =
	// "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQDPq3oXX5aFeBQGf3Ag/86zNu0VICXmkof85r+DDL46w3vHcTnkEWVbp9DaDurcF7DMctzJngO0u9OG1cb4mn+Pn/uNC1fp7S4JH4xtwST6jFgHtXcTG9uewWFYWKw/8b3zf4fXyRuI/2ekeLSstftqnMQdenVP7XCxMuEnnmM1RwIDAQAB";//
	/**
	 * 商户私钥
	 */
	// 生产测试环境
	private static String MERCHANT_PRIVATE_KEY = "MIICdgIBADANBgkqhkiG9w0BAQEFAASCAmAwggJcAgEAAoGBANB5cQ5pf+QHF9Z2+DjrAXstdxQHJDHyrni1PHijKVn5VHy/+ONiEUwSd5nx1d/W+mtYKxyc6HiN+5lgWSB5DFimyYCiOInh3tGQtN+pN/AtE0dhMh4J9NXad0XEetLPRgmZ795O/sZZTnA3yo54NBquT19ijYfrvi0JVf3BY9glAgMBAAECgYBFdSCox5eXlpFnn+2lsQ6mRoiVAKgbiBp/FwsVum7NjleK1L8MqyDOMpzsinlSgaKfXxnGB7UgbVW1TTeErS/iQ06zx3r4CNMDeIG1lYwiUUuguIDMedIJxzSNXfk65Bhps37lm129AE/VnIecpKxzelaUuzyGEoFWYGevwc/lQQJBAPO0mGUxOR/0eDzqsf7ehE+Iq9tEr+aztPVacrLsEBAwqOjUEYABvEasJiBVj4tECnbgGxXeZAwyQAJ5YmgseLUCQQDa/dgviW/4UMrY+cQnzXVSZewISKg/bv+nW1rsbnk+NNwdVBxR09j7ifxg9DnQNk1Edardpu3z7ipHDTC+z7exAkAM5llOue1JKLqYlt+3GvYr85MNNzSMZKTGe/QoTmCHStwV/uuyN+VMZF5cRcskVwSqyDAG10+6aYqD1wMDep8lAkBQBoVS0cmOF5AY/CTXWrht1PsNB+gbzic0dCjkz3YU6mIpgYwbxuu69/C3SWg7EyznQIyhFRhNlJH0hvhyMhvxAkEAuf7DNrgmOJjRPcmAXfkbaZUf+F4iK+szpggOZ9XvKAhJ+JGd+3894Y/05uYYRhECmSlPv55CBAPwd8VUsSb/1w==";// 生产测试环境
	// private static String MERCHANT_PRIVATE_KEY =
	// "MIICeAIBADANBgkqhkiG9w0BAQEFAASCAmIwggJeAgEAAoGBAM41c173GB1bKQXBMtEspYqYZALpkunrq2otGhSW+NQmfCh6PxoP4pS6KZw5ekiH1e5MMQ7RhiH8PnG2cQ36rbC9nMrcbaX7ESKsKjATsIzfzy3/KmUZ0kzDzUJJpRBR4DP/k0LBtcDyLw5O/z+HSQsh07+iYwPxokqCma3IncgRAgMBAAECgYEAqxgsXso0hv8BXZX8vRQHUqS4rrXwwQhalOFRN25AjX5VHBy6SLyPaonARrBmJtIWDf/H4Jy2Z3JsuVsAGPI6s+HG4FgnYStURlp7Z66e/IeRoxghduSiBQfKQAbqPozG6OPNZiu1fypMDYq1b0l7A5Jl4XU0cRq9PQtIyOQte0ECQQD0uQDmID1OPSU/qzOMRSBAEtUvxXw0HE00saoXQjZbjLAw3+ISYoY7mf90xQZFgncKvvREOiVtQHvH3p8tb6R5AkEA17YZMz9BaHojEHyMB49grDrJr+tfBtwGfWoOibAKHT56GeS6/aMvCeV+JwNQ1ZjGfGqC5izek9TpTYqzoG/qWQJAb+dpEdIMNoqfTc3rdGjnpmT2NzIG6Y1HgKD9NkWAtbxPlQnLRrzmxLpWpf6yL3bJGJDZAZnVI3ApKewSvaQfgQJBAKY/XOqnLLTltNKG+rD06o+yezCO2V0DBClyLKQnPZROS3JbW1YRhzY8Xd6ZappENW079YiKOjx7EhfF9yyGNHkCQQCQIBjgwBN2WljOyNMmjh0NEfwayaL9DgV0udbL/0E3TxdV5d45KjmSTRKVU5ooti77COZJnkw74lrGQYo2XL33";
	// T环境
	// private static String MERCHANT_PRIVATE_KEY =
	// "MIICdwIBADANBgkqhkiG9w0BAQEFAASCAmEwggJdAgEAAoGBAO/6rPCvyCC+IMalLzTy3cVBz/+wamCFNiq9qKEilEBDTttP7Rd/GAS51lsfCrsISbg5td/w25+wulDfuMbjjlW9Afh0p7Jscmbo1skqIOIUPYfVQEL687B0EmJufMlljfu52b2efVAyWZF9QBG1vx/AJz1EVyfskMaYVqPiTesZAgMBAAECgYEAtVnkk0bjoArOTg/KquLWQRlJDFrPKP3CP25wHsU4749t6kJuU5FSH1Ao81d0Dn9m5neGQCOOdRFi23cV9gdFKYMhwPE6+nTAloxI3vb8K9NNMe0zcFksva9c9bUaMGH2p40szMoOpO6TrSHO9Hx4GJ6UfsUUqkFFlN76XprwE+ECQQD9rXwfbr9GKh9QMNvnwo9xxyVl4kI88iq0X6G4qVXo1Tv6/DBDJNkX1mbXKFYL5NOW1waZzR+Z/XcKWAmUT8J9AkEA8i0WT/ieNsF3IuFvrIYG4WUadbUqObcYP4Y7Vt836zggRbu0qvYiqAv92Leruaq3ZN1khxp6gZKl/OJHXc5xzQJACqr1AU1i9cxnrLOhS8m+xoYdaH9vUajNavBqmJ1mY3g0IYXhcbFm/72gbYPgundQ/pLkUCt0HMGv89tn67i+8QJBALV6UgkVnsIbkkKCOyRGv2syT3S7kOv1J+eamGcOGSJcSdrXwZiHoArcCZrYcIhOxOWB/m47ymfE1Dw/+QjzxlUCQCmnGFUO9zN862mKYjEkjDN65n1IUB9Fmc1msHkIZAQaQknmxmCIOHC75u4W0PGRyVzq8KkxpNBq62ICl7xmsPM=";
	/**
	 * 编码类型
	 */
	private static String charset = "UTF-8";

	/**
	 * 获取SimpleDateFormat
	 * 
	 * @param parttern
	 *            日期格式
	 * @return SimpleDateFormat对象
	 * @throws RuntimeException
	 *             异常：非法日期格式
	 */
	private static SimpleDateFormat getDateFormat(String parttern)
			throws RuntimeException {
		return new SimpleDateFormat(parttern);
	}

	/**
	 * 建立请求，以模拟远程HTTP的POST请求方式构造并获取钱包的处理结果
	 * 如果接口中没有上传文件参数，那么strParaFileName与strFilePath设置为空值 如：buildRequest("",
	 * "",sParaTemp)
	 *
	 * @param strParaFileName
	 *            文件类型的参数名
	 * @param strFilePath
	 *            文件路径
	 * @param sParaTemp
	 *            请求参数数组
	 * @return 钱包处理结果
	 * @throws Exception
	 */
	public static String buildRequest(Map<String, String> sParaTemp,
			String signType, String key, String inputCharset, String gatewayUrl)
			throws Exception {
		// 待请求参数数组
		Map<String, String> sPara = buildRequestPara(sParaTemp, signType, key,
				inputCharset);
		HttpProtocolHandler httpProtocolHandler = HttpProtocolHandler
				.getInstance();
		HttpRequest request = new HttpRequest(HttpResultType.BYTES);
		// 设置编码集
		request.setCharset(inputCharset);
		request.setMethod(HttpRequest.METHOD_POST);
		request.setParameters(generatNameValuePair(
				createLinkRequestParas(sPara), inputCharset));
		request.setUrl(gatewayUrl);
		HttpResponse response = httpProtocolHandler
				.execute(request, null, null);
		if (response == null) {
			return null;
		}
		String strResult = response.getStringResult();
		return strResult;
	}

	/**
	 * MAP类型数组转换成NameValuePair类型
	 *
	 * @param properties
	 *            MAP类型数组
	 * @return NameValuePair类型数组
	 */
	private static NameValuePair[] generatNameValuePair(
			Map<String, String> properties, String charset) throws Exception {
		NameValuePair[] nameValuePair = new NameValuePair[properties.size()];
		int i = 0;
		for (Map.Entry<String, String> entry : properties.entrySet()) {
			// nameValuePair[i++] = new NameValuePair(entry.getKey(),
			// URLEncoder.encode(entry.getValue(),charset));
			nameValuePair[i++] = new NameValuePair(entry.getKey(),
					entry.getValue());
		}
		return nameValuePair;
	}

	/**
	 * 生成要请求给钱包的参数数组
	 * 
	 * @param sParaTemp
	 *            请求前的参数数组
	 * @param signType
	 *            RSA
	 * @param key
	 *            商户自己生成的商户私钥
	 * @param inputCharset
	 *            UTF-8
	 * @return 要请求的参数数组
	 * @throws Exception
	 */
	public static Map<String, String> buildRequestPara(
			Map<String, String> sParaTemp, String signType, String key,
			String inputCharset) throws Exception {
		// 除去数组中的空值和签名参数
		Map<String, String> sPara = paraFilter(sParaTemp);
		// 生成签名结果
		String mysign = "";
		if ("MD5".equalsIgnoreCase(signType)) {
			mysign = buildRequestByMD5(sPara, key, inputCharset);
		} else if ("RSA".equalsIgnoreCase(signType)) {
			mysign = buildRequestByRSA(sPara, key, inputCharset);
		}
		// 签名结果与签名方式加入请求提交参数组中
		System.out.println("sign:" + mysign);
		sPara.put("Sign", mysign);
		sPara.put("SignType", signType);

		return sPara;
	}

	/**
	 * 生成MD5签名结果
	 *
	 * @param sPara
	 *            要签名的数组
	 * @return 签名结果字符串
	 */
	public static String buildRequestByMD5(Map<String, String> sPara,
			String key, String inputCharset) throws Exception {
		String prestr = createLinkString(sPara, false); // 把数组所有元素，按照“参数=参数值”的模式用“&”字符拼接成字符串
		String mysign = "";
		mysign = MD5.sign(prestr, key, inputCharset);
		return mysign;
	}

	/**
	 * 生成RSA签名结果
	 *
	 * @param sPara
	 *            要签名的数组
	 * @return 签名结果字符串
	 */
	public static String buildRequestByRSA(Map<String, String> sPara,
			String privateKey, String inputCharset) throws Exception {
		String prestr = createLinkString(sPara, false); // 把数组所有元素，按照“参数=参数值”的模式用“&”字符拼接成字符串
		String mysign = "";
		mysign = RSA.sign(prestr, privateKey, inputCharset);
		return mysign;
	}

	/**
	 * 把数组所有元素排序，并按照“参数=参数值”的模式用“&”字符拼接成字符串
	 *
	 * @param params
	 *            需要排序并参与字符拼接的参数组
	 * @param encode
	 *            是否需要urlEncode
	 * @return 拼接后字符串
	 */
	public static Map<String, String> createLinkRequestParas(
			Map<String, String> params) {
		Map<String, String> encodeParamsValueMap = new HashMap<String, String>();
		List<String> keys = new ArrayList<String>(params.keySet());
		String charset = params.get("Charset");
		if (StringUtils.isBlank(charset)) {
			charset = params.get("InputCharset");
		}
		for (int i = 0; i < keys.size(); i++) {
			String key = keys.get(i);
			String value;
			try {
				value = URLEncoder.encode(params.get(key), charset);
				encodeParamsValueMap.put(key, value);
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
		}

		return encodeParamsValueMap;
	}

	/**
	 * 把数组所有元素排序，并按照“参数=参数值”的模式用“&”字符拼接成字符串
	 *
	 * @param params
	 *            需要排序并参与字符拼接的参数组
	 * @param encode
	 *            是否需要urlEncode
	 * @return 拼接后字符串
	 */
	public static String createLinkString(Map<String, String> params,
			boolean encode) {

		// params = paraFilter(params);

		List<String> keys = new ArrayList<String>(params.keySet());
		Collections.sort(keys);

		String prestr = "";

		String charset = params.get("Charset");
		if (StringUtils.isBlank(charset)) {
			charset = params.get("InputCharset");
		}

		for (int i = 0; i < keys.size(); i++) {
			String key = keys.get(i);
			String value = params.get(key);
			if (encode) {
				try {
					value = URLEncoder.encode(value, charset);
				} catch (UnsupportedEncodingException e) {
					e.printStackTrace();
				}
			}

			if (i == keys.size() - 1) {// 拼接时，不包括最后一个&字符
				prestr = prestr + key + "=" + value;
			} else {
				prestr = prestr + key + "=" + value + "&";
			}
		}

		return prestr;
	}

	/**
	 * 除去数组中的空值和签名参数
	 *
	 * @param sArray
	 *            签名参数组
	 * @return 去掉空值与签名参数后的新签名参数组
	 */
	public static Map<String, String> paraFilter(Map<String, String> sArray) {

		Map<String, String> result = new HashMap<String, String>();

		if (sArray == null || sArray.size() <= 0) {
			return result;
		}

		for (String key : sArray.keySet()) {
			String value = sArray.get(key);
			if (value == null || value.equals("")
					|| key.equalsIgnoreCase("sign")
					|| key.equalsIgnoreCase("sign_type")) {
				continue;
			}
			// try {
			// value = URLEncoder.encode(value,charset);
			// } catch (UnsupportedEncodingException e) {
			// e.printStackTrace();
			// }
			result.put(key, value);
		}

		return result;
	}

	/**
	 * 向测试服务器发送post请求
	 * 
	 * @param origMap
	 *            参数map
	 * @param charset
	 *            编码字符集
	 * @param MERCHANT_PRIVATE_KEY
	 *            私钥
	 */
	public void gatewayPost(Map<String, String> origMap, String charset,
			String MERCHANT_PRIVATE_KEY) {
		try {
//			String urlStr = "https://tpay.chanpay.com/mag-unify/gateway/receiveOrder.do?";
			 String urlStr =
			 "https://pay.chanpay.com/mag-unify/gateway/receiveOrder.do?";
			Map<String, String> sPara = buildRequestPara(origMap, "RSA",
					MERCHANT_PRIVATE_KEY, charset);
			System.out.println(urlStr + createLinkString(sPara, true));
			String resultString = buildRequest(origMap, "RSA",
					MERCHANT_PRIVATE_KEY, charset, urlStr);
			System.out.println(resultString);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 加密，部分接口，有参数需要加密
	 * 
	 * @param src
	 *            原值
	 * @param publicKey
	 *            畅捷支付发送的平台公钥
	 * @param charset
	 *            UTF-8
	 * @return RSA加密后的密文
	 */
	private String encrypt(String src, String publicKey, String charset) {
		try {
			byte[] bytes = RSA.encryptByPublicKey(src.getBytes(charset),
					publicKey);
			return Base64.encodeBase64String(bytes);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 异步通知验签仅供参考
	 */
	public void notifyVerify() {

		String sign = "5Ji173IpLoax0pqDKNLOCp2SVOHZanTSSmESI3Y/66RtY02DxcAWvGukwRZ9/6+neP1OoDXWpAVqhFeMpdYMcHPqDImt9o5O+7MFuH5qjqM2WolgTT+54qzlnzuo3ST60eQWkS31ePmHulknJZNVsjFwmS4TB1d2lWQAW4Zo7Cg=";
		Map<String, String> paramMap = new HashMap<String, String>();
		paramMap.put("notify_id", "82daf55676574973b8f25c0154a8d2ef");
		paramMap.put("notify_type", "trade_status_sync");
		paramMap.put("notify_time", "20160715130526");
		paramMap.put("Charset", "UTF-8");
		paramMap.put("Version", "1.0");
		paramMap.put("outer_trade_no", "NO201607062019409608");
		paramMap.put("inner_trade_no", "101146780758085591877");
		paramMap.put("trade_status", "TRADE_SUCCESS");
		paramMap.put("trade_amount", "200.00");
		paramMap.put("gmt_create", "20160706202016");
		paramMap.put("gmt_payment", "20160706202016");
		// paramMap.put("gmt_close", "");
		paramMap.put("extension", "{}");
		String text = createLinkString(paramMap, false);
		System.out.println("ori_text:" + text);
		try {
			System.out.println(RSA.verify(text, sign, MERCHANT_PUBLIC_KEY,
					charset));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 4.2.1.1. api nmg_ebank_pay 下单支付(网银支付)
	 */
	public void nmg_ebank_pay() {

		Date date = new Date();
		Map<String, String> origMap = new HashMap<String, String>();
		// 基本参数
		origMap.put("Service", "nmg_ebank_pay");
		origMap.put("Version", "1.0");
		origMap.put("PartnerId", "200001280008");
		origMap.put("InputCharset", charset);
		origMap.put("TradeDate", getDateFormat("yyyyMMdd").format(date));
		origMap.put("TradeTime", getDateFormat("HHmmss").format(date));
		// origMap.put("SignType","RSA");
		origMap.put("ReturnUrl", "http://dev.chanpay.com/receive.php");// 前台跳转url
		origMap.put("Memo", "备注");

		// 4.2.1.1. 网银支付 api 业务参数
		origMap.put("OutTradeNo", Long.toString(System.currentTimeMillis()));
		origMap.put("MchId", "200001280007");
		origMap.put("MchName", "XX商户");
		origMap.put("ChannelType", "02");
		origMap.put("BizType", "01");
		origMap.put("CardFlag", "01");
		origMap.put("PayFlag", "00");
		origMap.put("ServiceType", "01");
		origMap.put("BankCode", "CCB");
		origMap.put("OrderDesc", "XXXX商户订单");
		origMap.put("BuyerId", "");
		origMap.put("BuyerName", "");
		origMap.put("BuyerMoblie", "");
		origMap.put("BuyerAddress", "");
		origMap.put("ConsigneeAddress", "");
		origMap.put("BuyerCertType", "");
		origMap.put("BuyerCertId", "");

		origMap.put("TradeType", "00");
		origMap.put("GoodsType", "00");
		origMap.put("GoodsName", "测试");
		origMap.put("GoodsDetail", "测试");
		origMap.put("Currency", "00");
		origMap.put("OrderStartTime", "20170731191900");
		origMap.put("ExpiredTime", "2d");

		origMap.put("OrderAmt", "1");
		origMap.put("EnsureAmt", "");
		origMap.put("NotifyUrl",
				"http://kadmin.chanpay.com/tpu/mag/asynNotify.do");
		origMap.put("UserIp", "127.0.0.1");
		origMap.put("PreferentialAmt", "");
		origMap.put("SplitList", "");
		origMap.put("Ext", "{'ext':'ext1'}");

		this.gatewayPost(origMap, charset, MERCHANT_PRIVATE_KEY);
	}

	/**
	 * 4.2.1.2. api nmg_ebank_list_query 查询银行卡列表
	 */
	public void nmg_ebank_list_query() {

		Date date = new Date();
		Map<String, String> origMap = new HashMap<String, String>();
		// 基本参数
		origMap.put("Service", "nmg_ebank_list_query");
		origMap.put("Version", "1.0");
		origMap.put("PartnerId", "200001280008");
		origMap.put("InputCharset", charset);
		origMap.put("TradeDate", getDateFormat("yyyyMMdd").format(date));
		origMap.put("TradeTime", getDateFormat("HHmmss").format(date));
		// origMap.put("SignType","RSA");
		origMap.put("ReturnUrl", "http://dev.chanpay.com/receive.php");// 前台跳转url
		origMap.put("Memo", "备注");

		// 4.2.1.1. 查询银行卡列表 api 业务参数
		origMap.put("MchId", "200001280008");
		origMap.put("BizType", "01");
		origMap.put("BankCode", "CITIC");
		origMap.put("CardFlag", "DC,CC,GC");
		origMap.put("ChannelType", "WEB,WAP");
		origMap.put("Ext", "");

		this.gatewayPost(origMap, charset, MERCHANT_PRIVATE_KEY);
	}

	/**
	 * 
	 * 4.2.1.7. 商户订单状态查询 api nmg_api_query_trade
	 */
	public void nmg_api_query_trade() {

		Date date = new Date();
		Map<String, String> origMap = new HashMap<String, String>();
		// 2.1 基本参数
		origMap.put("Service", "nmg_api_query_trade");
		origMap.put("Version", "1.0");
		// origMap.put("PartnerId", "200003681062");// 畅捷支付分配的商户号
		origMap.put("PartnerId", "200001280008");// 畅捷支付分配的商户号

		origMap.put("InputCharset", charset);// 字符集
		origMap.put("TradeDate", getDateFormat("yyyyMMdd").format(date));
		origMap.put("TradeTime", getDateFormat("HHmmss").format(date));
		// origMap.put("SignType","RSA");
		origMap.put("ReturnUrl", "");// 前台跳转url
		origMap.put("Memo", "备注");

		// 2.2 业务参数
		origMap.put("TrxId", Long.toString(System.currentTimeMillis()));// 订单号
		origMap.put("OrderTrxId", "2016031709330077864824122488");// 原业务请求订单号
		origMap.put("TradeType", "pay_order");// 原业务订单类型
		origMap.put("Extension", "");// 原业务订单类型
		this.gatewayPost(origMap, charset, MERCHANT_PRIVATE_KEY);
	}

	/**
	 * 
	 * 4.2.1.8. 商户退款请求 api nmg_api_refund
	 */

	public void nmg_api_refund() {

		Date date = new Date();
		Map<String, String> origMap = new HashMap<String, String>();
		// 2.1 基本参数
		origMap.put("Service", "nmg_api_refund");
		origMap.put("Version", "1.0");
		origMap.put("PartnerId", "200001280008");// 畅捷支付分配的商户号
		origMap.put("InputCharset", charset);// 字符集
		origMap.put("TradeDate", getDateFormat("yyyyMMdd").format(date));
		origMap.put("TradeTime", getDateFormat("HHmmss").format(date));
		// origMap.put("SignType","RSA");
		origMap.put("ReturnUrl", "");// 前台跳转url
		origMap.put("Memo", "备注");
		// 2.2 业务参数
		origMap.put("TrxId", "2017030915302022");// 订单号
		origMap.put("OriTrxId", "20170309134520111");// 原有支付请求订单号
		origMap.put("TrxAmt", "20");// 退款金额
		origMap.put("RefundEnsureAmount", null);// 退担保金额
		origMap.put(
				"RoyaltyParameters",
				"[{'userId':'13890009900','PID':'2','account_type':'101','amount':'100.00'},{'userId':'13890009900','PID':'2','account_type':'101','amount':'100.00'}]");// 退款分润账号集
		origMap.put("NotifyUrl", "www.baidu.com");// 异步通知地址
		origMap.put("Extension", "");// 扩展字段
		this.gatewayPost(origMap, charset, MERCHANT_PRIVATE_KEY);
	}

	/**
	 * 2.10 商户日交易对账单文件
	 */
	public void nmg_api_everyday_trade_file() {

		Date date = new Date();
		Map<String, String> origMap = new HashMap<String, String>();
		// 2.1 基本参数
		origMap.put("Service", "nmg_api_everyday_trade_file");// 支付的接口名
		origMap.put("Version", "1.0");
		origMap.put("PartnerId", "200001280008");// 畅捷支付分配的商户号
		origMap.put("InputCharset", charset);// 字符集
		origMap.put("TradeDate", getDateFormat("yyyyMMdd").format(date));
		origMap.put("TradeTime", getDateFormat("HHmmss").format(date));
		// origMap.put("SignType","RSA");
		origMap.put("ReturnUrl", "");// 前台跳转url
		origMap.put("Memo", "备注");

		// 2.11 日支付对账文件
		origMap.put("TransDate", "20170512");// 交易日期
		this.gatewayPost(origMap, charset, MERCHANT_PRIVATE_KEY);
	}

	/**
	 * nmg_api_refund_trade_file
	 */
	public void nmg_api_refund_trade_file() {

		Date date = new Date();
		Map<String, String> origMap = new HashMap<String, String>();
		// 2.1 基本参数
		origMap.put("Service", "nmg_api_refund_trade_file");// 支付的接口名
		origMap.put("Version", "1.0");
		origMap.put("PartnerId", "200000140001");// 畅捷支付分配的商户号
		origMap.put("InputCharset", charset);// 字符集
		origMap.put("TradeDate", getDateFormat("yyyyMMdd").format(date));
		origMap.put("TradeTime", getDateFormat("HHmmss").format(date));
		// origMap.put("SignType","RSA");
		origMap.put("ReturnUrl", "");// 前台跳转url
		origMap.put("Memo", "备注");

		// 2.11 日支付对账文件
		origMap.put("TransDate", "20160728");// 交易日期

		this.gatewayPost(origMap, charset, MERCHANT_PRIVATE_KEY);
	}

	public static void main(String[] args) {
		ChanpayGatewayDemo test = new ChanpayGatewayDemo();
		 test.nmg_ebank_pay();// 网银支付
// 		test.nmg_ebank_list_query();// 查询银行卡列表

		// test.nmg_api_query_trade();// 商户订单状态查询
		// test.nmg_api_refund();//商户退款请求
		// test.nmg_api_everyday_trade_file();//商户日交易对账单文件
		// test.nmg_api_refund_trade_file();//商户日退款对账单文件
	}
}
