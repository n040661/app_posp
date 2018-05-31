package xdt.dto.yf;

import java.io.File;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.Map;
import java.util.TreeMap;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.yufusoft.payplatform.security.cipher.YufuCipher;
import com.yufusoft.payplatform.security.vo.ParamPacket;

/** 
* @author 作者 E-mail: 
* @version 创建时间：2018年3月27日 上午9:45:42 
* 类说明 
*/
public class DoYf {
	private static String url="http://www.yfpayment.com";
	private static String urlTest="http://malltest.yfpayment.com";
	public static String doPaySet(String paysetUrl, Map<String, String> params,String merCertPath,String pfxPath,String pfxPwd) {

		String retString = null;
		String merchantId = params.get("merchantId");
		try {
			// 一、置单
			YufuCipher cipher = null;
			YufuCipherSupport instance = null;
			cipher = YufuCipherSupport.getCipherInstance( merCertPath, pfxPath, pfxPwd,cipher,instance);
			ParamPacket bo = cipher.doPack(params);

			TreeMap<String, String> map_param = new TreeMap<>();
			map_param.put("merchantId", merchantId);
			map_param.put("data", URLEncoder.encode(bo.getData(), "utf-8"));
			map_param.put("enc", URLEncoder.encode(bo.getEnc(), "utf-8"));
			map_param.put("sign", URLEncoder.encode(bo.getSign(), "utf-8"));
			System.out.println("置单密文报文：" + map_param.toString());
			System.out.println("置单地址：" + paysetUrl);
			String p = PostUtils.doPost(paysetUrl, map_param);

			if (p != null && !"".equals(p)) {
				// 二、验签解密
				p = URLDecoder.decode(p, "utf-8");
				System.out.println("URL解码后的置单应答结果：" + p);
				TreeMap<String, String> boMap = JSON.parseObject(p, new TypeReference<TreeMap<String, String>>() {
				});
				Map<String, String> payshowParams = cipher
						.unPack(new ParamPacket(boMap.get("data"), boMap.get("enc"), boMap.get("sign")));
				System.out.println("解密后的置单应答结果：" + payshowParams);

				// 三、加密回传
				ParamPacket pp = cipher.doPack(payshowParams);

				TreeMap<String, String> result = new TreeMap<>();
				result.put("merchantId", merchantId);
				result.put("data", pp.getData());
				result.put("enc", pp.getEnc());
				result.put("sign", pp.getSign());
				retString = JSON.toJSONString(result);
			} else {
				System.out.println("置单返回报文为空！");
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

		return retString;
	}
	
	
	public static String createHtml(String actionUrl, Map<String, String> paramMap) {
		StringBuilder html = new StringBuilder();

		html.append("<script language=\"javascript\">window.onload=function(){document.pay_form.submit();}</script>\n");
		html.append("<form id=\"pay_form\" method=\"post\" name=\"pay_form\" action=\"").append(actionUrl)
				.append("\" method=\"post\">\n");

		for (String key : paramMap.keySet()) {
			html.append("<input type=\"hidden\" name=\"" + key + "\" id=\"" + key + "\" value=\"" + paramMap.get(key)
					+ "\">\n");
		}
		html.append("</form>\n");
		return html.toString();
	}
	
	/**
	 * 下载文件
	 * @param req
	 * @param downloadFilePath
	 * @param merCertPath
	 * @param pfxPath
	 * @param pfxPwd
	 */
	public static void downResourse(FileDownReq req,String downloadFilePath,String merCertPath,String pfxPath,String pfxPwd) {
		FileDownRsp rsp =null;
		try{
			YufuCipher cipher = null;
			YufuCipherSupport instance = null;
			cipher = YufuCipherSupport.getCipherInstance( merCertPath, pfxPath, pfxPwd,cipher,instance);
	       //YufuCipher cipher = YufuCipherSupport.getCipherInstance( merCertPath, pfxPath, pfxPwd);
			
	       String data = GsonUtil.objToJson(req);
	      
	       Map<String, String> params = GsonUtil.jsonToObj(data, Map.class);
	       ParamPacket bo = cipher.doPack(params);
	       
	       TreeMap<String, String> map_param = new TreeMap<>();
			map_param.put("merchantId", req.getMerchantId());
			map_param.put("data", bo.getData());
			map_param.put("enc", bo.getEnc());
			map_param.put("sign", bo.getSign());
			String url ="";
			if("000001110100000812".equals(req.getMerchantId())) {
				url ="http://malltest.yfpayment.com/batchpay/download.do";
			}else {
				url ="http://www.yfpayment.com/batchpay/download.do";
			}
			String returnStr = DisburseClientUtil.sendPostInstream(url, map_param,downloadFilePath);
			if(StringUtil.isNotEmpty(returnStr)){
				String pbody = URLDecoder.decode(returnStr, "UTF-8");
				TreeMap<String, String> dataMap = JSON.parseObject(pbody, new TypeReference<TreeMap<String, String>>() {});
				ParamPacket po = new ParamPacket(dataMap.get("data"), dataMap.get("enc"), dataMap.get("sign"));
				Map<String, String> resultMap  = cipher.unPack(po);
				String resultJson = GsonUtil.objToJson(resultMap);
				rsp  = DTOUtil.parseDTO(resultJson, FileDownRsp.class, "json");
			}else{
				System.out.println("--------返回为空---------");
			}
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	/**
	 * 代付结果查询
	 * @param req
	 * @return
	 */
	public static DisburseResultQueryRsp query(DisburseResultQueryReq req,String merCertPath,String pfxPath,String pfxPwd) {
		DisburseResultQueryRsp rsp = null;
		try {
			YufuCipher cipher = null;
			YufuCipherSupport instance = null;
			cipher = YufuCipherSupport.getCipherInstance( merCertPath, pfxPath, pfxPwd,cipher,instance);
			//YufuCipher cipher = YufuCipherSupport.getCipherInstance( merCertPath, pfxPath, pfxPwd);

			String data = GsonUtil.objToJson(req);

			Map<String, String> params = GsonUtil.jsonToObj(data, Map.class);
			ParamPacket bo = cipher.doPack(params);

			TreeMap<String, String> map_param = new TreeMap<>();
			map_param.put("merchantId", req.getMerchantId());
			map_param.put("data", bo.getData());
			map_param.put("enc", bo.getEnc());
			map_param.put("sign", bo.getSign());
			String url ="";
			if("000001110100000812".equals(req.getMerchantId())) {
				url ="http://malltest.yfpayment.com/batchpay/payquery.do";
			}else  {
				url ="http://www.yfpayment.com/batchpay/payquery.do";
			}
			String returnStr = DisburseClientUtil.sendPost(url, map_param);

			if (StringUtil.isNotEmpty(returnStr)) {
				String pbody = URLDecoder.decode(returnStr, "UTF-8");
				TreeMap<String, String> dataMap = JSON.parseObject(pbody, new TypeReference<TreeMap<String, String>>() {
				});
				ParamPacket po = new ParamPacket(dataMap.get("data"), dataMap.get("enc"), dataMap.get("sign"));
				Map<String, String> resultMap = cipher.unPack(po);
				String resultJson = GsonUtil.objToJson(resultMap);
				rsp = DTOUtil.parseDTO(resultJson, DisburseResultQueryRsp.class, "json");
			} else {
				System.out.println("返回为空");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return rsp;
	}
	/**
	 * 代付结果下载
	 * @param req
	 * @return
	 */
	public static RefundChequeResultDownRsp refundChequeResultPayDown(RefundChequeResultDownReq req,String merCertPath,String pfxPath,String pfxPwd) {

		RefundChequeResultDownRsp rsp =null;
		try{
			YufuCipher cipher = null;
			YufuCipherSupport instance = null;
			cipher = YufuCipherSupport.getCipherInstance( merCertPath, pfxPath, pfxPwd,cipher,instance);
	       //YufuCipher cipher = YufuCipherSupport.getCipherInstance( merCertPath, pfxPath, pfxPwd);
			
	       String data = GsonUtil.objToJson(req);
	      
	       Map<String, String> params = GsonUtil.jsonToObj(data, Map.class);
	       ParamPacket bo = cipher.doPack(params);
	       
	       TreeMap<String, String> map_param = new TreeMap<>();
			map_param.put("merchantId", req.getMerchantId());
			map_param.put("data", bo.getData());
			map_param.put("enc", bo.getEnc());
			map_param.put("sign", bo.getSign());
			String url ="";
			if("000001110100000812".equals(req.getMerchantId())) {
				url ="http://malltest.yfpayment.com/batchpay/payfetch.do";
			}else  {
				url ="http://www.yfpayment.com/batchpay/payfetch.do";
			}
			String returnStr = DisburseClientUtil.sendPost(url, map_param);
			
			if(StringUtil.isNotEmpty(returnStr)){
				String pbody = URLDecoder.decode(returnStr, "UTF-8");
				TreeMap<String, String> dataMap = JSON.parseObject(pbody, new TypeReference<TreeMap<String, String>>() {});
				ParamPacket po = new ParamPacket(dataMap.get("data"), dataMap.get("enc"), dataMap.get("sign"));
				Map<String, String> resultMap  = cipher.unPack(po);
				String resultJson = GsonUtil.objToJson(resultMap);
				rsp  = DTOUtil.parseDTO(resultJson, RefundChequeResultDownRsp.class, "json");
			}else{
				System.out.println("返回为空");
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		return rsp;
	}
	
	
	/**
	 * 退票结果下载
	 * @param req
	 * @return
	 */
	public static RefundChequeResultDownRsp refundChequeResultDown(RefundChequeResultDownReq req,String merCertPath,String pfxPath,String pfxPwd) {

		RefundChequeResultDownRsp rsp =null;
		try{
			YufuCipher cipher = null;
			YufuCipherSupport instance = null;
			cipher = YufuCipherSupport.getCipherInstance( merCertPath, pfxPath, pfxPwd,cipher,instance);
//	       YufuCipher cipher = YufuCipherSupport.getCipherInstance( merCertPath, pfxPath, pfxPwd);
			
	       String data = GsonUtil.objToJson(req);
	      
	       Map<String, String> params = GsonUtil.jsonToObj(data, Map.class);
	       ParamPacket bo = cipher.doPack(params);
	       
	       TreeMap<String, String> map_param = new TreeMap<>();
			map_param.put("merchantId", req.getMerchantId());
			map_param.put("data", bo.getData());
			map_param.put("enc", bo.getEnc());
			map_param.put("sign", bo.getSign());
			String url ="";
			if("000001110100000812".equals(req.getMerchantId())) {
				url ="http://malltest.yfpayment.com/batchpay/refundfetch.do";
			}else {
				url ="http://www.yfpayment.com/batchpay/refundfetch.do";
			}
			String returnStr = DisburseClientUtil.sendPost(url, map_param);
			
			if(StringUtil.isNotEmpty(returnStr)){
				String pbody = URLDecoder.decode(returnStr, "UTF-8");
				TreeMap<String, String> dataMap = JSON.parseObject(pbody, new TypeReference<TreeMap<String, String>>() {});
				ParamPacket po = new ParamPacket(dataMap.get("data"), dataMap.get("enc"), dataMap.get("sign"));
				Map<String, String> resultMap  = cipher.unPack(po);
				String resultJson = GsonUtil.objToJson(resultMap);
				rsp  = DTOUtil.parseDTO(resultJson, RefundChequeResultDownRsp.class, "json");
			}else{
				System.out.println("返回为空");
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		return rsp;
	}
}
