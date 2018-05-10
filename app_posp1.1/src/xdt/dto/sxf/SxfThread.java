package xdt.dto.sxf;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

import xdt.dao.IPmsMerchantInfoDao;
import xdt.model.PmsBusinessPos;
import xdt.model.PmsMerchantInfo;
import xdt.service.ISxfService;

public class SxfThread extends Thread {

	private Logger log =Logger.getLogger(this.getClass());
	private Map<String, String> results ;
	
	private ISxfService sxfServiceImpl;
	
	private PmsBusinessPos pmsBusinessPos;
	
	private Double surplus;
	private IPmsMerchantInfoDao pmsMerchantInfoDao;





	public SxfThread(Map<String, String> results, ISxfService sxfServiceImpl,
			PmsBusinessPos pmsBusinessPos, Double surplus,
			IPmsMerchantInfoDao pmsMerchantInfoDao) {
		super();
		this.results = results;
		this.sxfServiceImpl = sxfServiceImpl;
		this.pmsBusinessPos = pmsBusinessPos;
		this.surplus = surplus;
		this.pmsMerchantInfoDao = pmsMerchantInfoDao;
	}





	@Override
	public void run() {
		
		try {
			log.info("随行付查询来了！");
			log.info("自己生成订单"+results.get("payItemId"));
			sleep(2000);
			PayRequsest payRequsest  =new PayRequsest();
			PmsMerchantInfo merchantinfo = new PmsMerchantInfo();
			for (int i = 0; i < 50; i++) {
				
				Map<String, Object> resMap = new HashMap<String, Object>();
				RequestMessage rm = new RequestMessage();
				rm.setClientId(pmsBusinessPos.getBusinessnum().toString());
				//rm.setClientId(BankCodeUtils.clientId);
				rm.setReqId(System.currentTimeMillis()+"");
				//rm.setReqId("2015122516091210001");
				rm.setTranCd("DF1004");
				rm.setVersion("0.0.0.1");
				Map<String, String> map =new HashMap<>();
				map.put("payItemId", results.get("payItemId"));
				String json =JSON.toJSONString(map);
				log.info("随行付订单json:"+json);
				try{
					byte[] bs = DESUtils.encrypt(json.getBytes("UTF-8"), "12345678");
					//Base64编码
					String reqDataEncrypt = Base64Utils.encode(bs);
					
					System.out.println("Base64编码："+reqDataEncrypt);
					
					rm.setReqData(reqDataEncrypt);
					String priKey = SXFUtil.PrivateKey;
//					//RSA签名
					rm.setSign(RSAUtils.sign(reqDataEncrypt, priKey));
				}catch(Exception e){
					e.printStackTrace();
					resMap.put("resCode","0002");
			        resMap.put("resMsg", "加密参数出现异常");
			        resMap.put("msg", "0002,加密参数出现异常");
			        log.info("随行付代付线性查询加密错误："+JSON.toJSONString(resMap));
				}
				String reqStr=JsonUtils.toJson(rm);
				log.info("rm:"+JSON.toJSON(rm));
				String url = "http://dpay.suixingpay.com:38080/paygateway/queryPayResult.do";
				log.info("随行付查询上传参数"+reqStr);
				log.info("随行付查询发送地址"+url);
				String body=HttpClientUtil.doPost(url,reqStr);
				JSONObject jsons =JSONObject.parseObject(body);
				log.info("随行付查询返回参数1："+JSON.toJSONString(jsons));
				log.info("随行付查询返回参数2"+jsons);
				String resData = jsons.getString("resData");
				log.info("resData:"+resData);
				byte[] base64bs = Base64Utils.decode(resData);
				log.info("base64bs:"+base64bs);
				// DES解密
				byte[] debs = DESUtils.decrypt(base64bs, "12345678");
				log.info("debs:"+debs);
				String resDataDecrypt = new String(debs,"UTF-8");
				log.info("随行付resDataDecrypt："+JSON.toJSONString(resDataDecrypt));
				if(!"000000".equals(jsons.get("resCode"))){
					log.info("来了1");
					resMap.put("resCode",jsons.get("resCode"));
			        resMap.put("resMsg", jsons.get("resMsg"));
			        log.info("随行付代付线性查询请求失败错误信息："+JSON.toJSONString(resMap));
			        log.info("来了2");
			        int ii =sxfServiceImpl.UpdateDaifu(results.get("reqId"), "01");
			        payRequsest.setReqId(results.get("reqId")+"/A");
					surplus = surplus+Double.parseDouble(results.get("payAmt"));
					merchantinfo.setPositionT1(surplus.toString());
					ii =sxfServiceImpl.add(payRequsest, merchantinfo, results, "00");
					log.info("添加失败订单3："+ii);
					map.put("mercId", payRequsest.getClientId());
					map.put("payMoney",payRequsest.getPayAmt());
					int nus = pmsMerchantInfoDao.updataPayT1(map);
					if(nus==1){
						log.info("随行付***补款成功");
					}
			        log.info("修改订单状态："+ii);
				}else{
					log.info("来了3");
					JSONObject js =JSONObject.parseObject(resDataDecrypt);
					log.info("随行付查询返回参数解析："+JSON.toJSONString(js));
					
					if("01".equals(js.get("tranSts"))){
						log.info("200:"+results.get("reqId"));
						int ii =sxfServiceImpl.UpdateDaifu(results.get("reqId"), "200");
						log.info("修改订单状态："+ii);
					}else if("00".equals(js.get("tranSts"))){
						log.info("00:"+results.get("reqId"));
						int ii =sxfServiceImpl.UpdateDaifu(results.get("reqId"), "00");
						log.info("修改订单状态："+ii);
						return ;
					}else if("03".equals(js.get("tranSts"))){
						log.info("01:"+results.get("reqId"));
						int ii =sxfServiceImpl.UpdateDaifu(results.get("reqId"), "01");
						log.info("修改订单状态："+ii);
						payRequsest.setReqId(results.get("reqId")+"/A");
						surplus = surplus+Double.parseDouble(results.get("payAmt"));
						merchantinfo.setPositionT1(surplus.toString());
						ii =sxfServiceImpl.add(payRequsest, merchantinfo, results, "00");
						log.info("添加失败订单3："+ii);
						map.put("mercId", payRequsest.getClientId());
						map.put("payMoney",payRequsest.getPayAmt());
						int nus = pmsMerchantInfoDao.updataPayT1(map);
						if(nus==1){
							log.info("随行付***补款成功");
						}
				        log.info("修改订单状态："+ii);
				        return ;
					}
					
					String msg=JsonUtils.toJson(resDataDecrypt);
					log.info(msg);
					
				}
				sleep(50000);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
}
