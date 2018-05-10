package xdt.aspect;


import java.net.URLDecoder;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import net.sf.json.JSONObject;

import org.apache.log4j.Logger;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import xdt.dao.IPmsDaifuMerchantInfoDao;
import xdt.model.PmsBusinessPos;
import xdt.model.PmsDaifuMerchantInfo;
import xdt.service.IPmsDaifuMerchantInfoService;
import xdt.service.JsdsQrCodeService;
import xdt.util.HttpURLConection;
import xdt.util.HttpUtil;
import xdt.util.RSAUtil;
import xdt.util.UtilDate;

@Component
public class PayTiming {

	Logger logger =Logger.getLogger(PayTiming.class);
	@Resource
	private IPmsDaifuMerchantInfoService daifuMerchantInfoService;
	@Resource
	private JsdsQrCodeService jsdsQrCodeService;
	@Resource
	private IPmsDaifuMerchantInfoDao pmsDaifuMerchantInfoDao;
	
	public void PayTimingSelect() throws Exception{
		
		
		List<PmsDaifuMerchantInfo> list = daifuMerchantInfoService.selectDaifu1();
		
		for (PmsDaifuMerchantInfo pmsDaifuMerchantInfo : list) {
			
			// 查询上游商户号
		    PmsBusinessPos busInfo = jsdsQrCodeService.selectKey(pmsDaifuMerchantInfo.getMercId());
			
		    String private_key=busInfo.getKek();
		    logger.info("秘钥:"+private_key);
		    String merchantCode=busInfo.getBusinessnum();
		    logger.info("商户号:"+merchantCode);
		    String orderid=pmsDaifuMerchantInfo.getBatchNo();
		    logger.info("订单号:"+orderid);
		    Map<String, String> params = new HashMap<String, String>();
			params.put("merchantCode", merchantCode);
			params.put("orderNum", orderid);
			String apply = HttpUtil.parseParams(params);
			logger.info("生成签名前的数据:" + apply);
			byte[] sign = RSAUtil.encrypt(private_key,
					apply.getBytes());
			logger.info("上送的签名:" + sign);
			Map<String, String> map = new HashMap<String, String>();
			map.put("groupId", busInfo.getDepartmentnum());
			map.put("service", "SMZF010");
			map.put("signType", "RSA");
			map.put("sign", RSAUtil.base64Encode(sign));
			map.put("datetime", UtilDate.getOrderNum());
			String jsonmap = HttpUtil.parseParams(map);
			logger.info("上送数据:" + jsonmap);
			String respJson = HttpURLConection.httpURLConnectionPOST(
					"http://180.96.28.2:8048/TransQueryInterface/TransRequest",//http://180.96.28.8:8044/TransInterface/TransRequest
					jsonmap);
			logger.info("**********江苏电商响应报文:{}" + respJson);
			Map<String, String> result = new HashMap<String, String>();
			if(respJson!=null){
				JSONObject ob = JSONObject.fromObject(respJson);
				
				logger.info("封装之后的数据:{}" + ob);
				Iterator it = ob.keys();
				while (it.hasNext()) {
					String key = (String) it.next();
					if (key.equals("pl_code")) {
						String value = ob.getString(key);
						logger.info("提交状态:" + "\t" + value);
						result.put("respCode", value);
					}
					if (key.equals("pl_sign")) {
						String value = ob.getString(key);
						logger.info("签名:" + "\t" + value);
						result.put("sign", value);
					}
					if (key.equals("pl_datetime")) {
						String value = ob.getString(key);
						logger.info("交易时间:" + "\t" + value);
						result.put("pl_datetime", value);
					}
					if (key.equals("pl_message")) {
						String value = ob.getString(key);
						logger.info("交易描述:" + "\t" + value);
						result.put("pl_message", value);
					}

				}
				if (result.get("respCode").equals("0000")) {

					String sign1 = result.get("sign");
					String baseSign = URLDecoder.decode(sign1, "UTF-8");

					baseSign = baseSign.replace(" ", "+");

					byte[] a = RSAUtil.verify(busInfo.getKek(),
							RSAUtil.base64Decode(baseSign));

					String Str = new String(a);

					logger.info("解析之后的数据:" + Str);

					String[] array = Str.split("\\&");

					logger.info("拆分数据:" + array);
					String[] list0 = array[0].split("\\=");
					if (list0[0].equals("orderNum")) {
						logger.info("合作商订单号:" + list0[1]);

						result.put("orderNum", list0[1]);

					}
					String[] list1 = array[1].split("\\=");
					if (list1[0].equals("pl_orderNum")) {
						logger.info("平台订单号:" + list1[1]);
						 result.put("pl_orderNum",
						 list1[1]);

					}
					String[] list2 = array[2].split("\\=");
					if (list2[0].equals("pl_transMessage")) {
						logger.info("支付状态描述:" + list2[1]);
						result.put("pl_transMessage", list2[1]);
					}
					String[] list3 = array[3].split("\\=");
					if (list3[0].equals("pl_transState")) {
						logger.info("支付状态:" + list3[1]);
						result.put("pl_transState", list3[1]);
					}
					
					
					if("1".equals(result.get("pl_transState"))){
						UpdateDaifu(result.get("orderNum"), "00");
					}else if("2".equals(result.get("pl_transState"))){
						UpdateDaifu(result.get("orderNum"), "01");
					}else{
						UpdateDaifu(result.get("orderNum"), "200");
					}
			}
			
		}
		
		
	}
		
}
	// 修改代付状态
		public int UpdateDaifu(String batchNo, String responsecode) throws Exception {

			logger.info("原始数据:" + batchNo);

			PmsDaifuMerchantInfo pdf = new PmsDaifuMerchantInfo();

			logger.info("上送的批次号:" + batchNo);

			pdf.setBatchNo(batchNo);
			pdf.setResponsecode(responsecode);
			return pmsDaifuMerchantInfoDao.update(pdf);
		}
}
