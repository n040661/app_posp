package xdt.dto.jsds;

import java.net.URLDecoder;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import net.sf.json.JSONObject;

import org.apache.log4j.Logger;

import com.alibaba.fastjson.JSON;

import xdt.dao.IPmsDaifuMerchantInfoDao;
import xdt.dao.IPmsMerchantInfoDao;
import xdt.model.PmsBusinessPos;
import xdt.model.PmsDaifuMerchantInfo;
import xdt.model.PmsMerchantInfo;
import xdt.service.IPmsDaifuMerchantInfoService;
import xdt.service.JsdsQrCodeService;
import xdt.util.HttpURLConection;
import xdt.util.HttpUtil;
import xdt.util.RSAUtil;
import xdt.util.UtilDate;


public class JsThread extends Thread {

	public static final Logger logger=Logger.getLogger(JsThread.class);
	
	public IPmsDaifuMerchantInfoService daifuMerchantInfoService;
	
	public JsdsRequestDto reqData;
	
	public JsdsQrCodeService jsdsQrcodeServiceImpl;

	public IPmsDaifuMerchantInfoDao pmsDaifuMerchantInfoDao;
	
	public IPmsMerchantInfoDao pmsMerchantInfoDao;

	public PmsMerchantInfo merchantinfo;

	public JsThread(IPmsDaifuMerchantInfoService daifuMerchantInfoService,
			JsdsRequestDto reqData, JsdsQrCodeService jsdsQrcodeServiceImpl,
			IPmsDaifuMerchantInfoDao pmsDaifuMerchantInfoDao,
			IPmsMerchantInfoDao pmsMerchantInfoDao, PmsMerchantInfo merchantinfo) {
		super();
		this.daifuMerchantInfoService = daifuMerchantInfoService;
		this.reqData = reqData;
		this.jsdsQrcodeServiceImpl = jsdsQrcodeServiceImpl;
		this.pmsDaifuMerchantInfoDao = pmsDaifuMerchantInfoDao;
		this.pmsMerchantInfoDao = pmsMerchantInfoDao;
		this.merchantinfo = merchantinfo;
	}




	@Override
	public void run() {
		logger.info("============江苏代付查询开始==========");
		logger.info("============江苏代付查询reqData参数=========="+JSON.toJSON(reqData));
		logger.info("============江苏代付查询merchantinfo参数=========="+JSON.toJSON(merchantinfo));
		try {
			String type;
			Double sa;
			Thread.sleep(2000);
			for (int i = 0; i < 90; i++) {
				PmsDaifuMerchantInfo model = new PmsDaifuMerchantInfo();
					
					// 查询上游商户号
				    PmsBusinessPos busInfo = jsdsQrcodeServiceImpl.selectKey(merchantinfo.getMercId());
					
				    String private_key=busInfo.getKek();
				    logger.info("秘钥:"+private_key);
				    String merchantCode=busInfo.getBusinessnum();
				    logger.info("商户号:"+merchantCode);
				    String orderid=reqData.getOrderNum();
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
								jsdsQrcodeServiceImpl.UpdateDaifu(result.get("orderNum"), "00");
								break;
							}else if("2".equals(result.get("pl_transState"))){
								jsdsQrcodeServiceImpl.UpdateDaifu(result.get("orderNum"), "01");
								Map<String, String> map1 = new HashMap<>();
								map1.put("mercId", reqData.getMerchantCode());
								map1.put("payMoney", reqData.getTransMoney() + "");
								int nus = pmsMerchantInfoDao.updataPayT1(map1);
								if (nus == 1) {
									logger.info("加款成功！！");
									// 代付钱的总金额
									if (reqData.getType().equals("0")) {
										type = "D0";
										 sa = Double.parseDouble(merchantinfo.getPosition()) + Double.parseDouble(reqData.getTransMoney());
									} else {
										type = "T1";
										 sa = Double.parseDouble(merchantinfo.getPositionT1()) + Double.parseDouble(reqData.getTransMoney());
									}
									model.setCount("1");
									model.setIdentity(reqData.getOrderNum());
									model.setBatchNo(reqData.getOrderNum() + "/A");
									model.setAmount(Double.parseDouble(reqData.getTransMoney()) / 100 + "");
									model.setCardno(reqData.getAccountName());
									model.setRealname(reqData.getBankName());
									model.setPayamount(Double.parseDouble(reqData.getTransMoney()) / 100 + "");
									model.setPmsbankno(reqData.getBankLinked());
									model.setTransactionType("代付补款");
									model.setPosition(sa.toString());
									model.setRemarks(type);
									model.setRecordDescription("批次号:" + reqData.getOrderNum());
									model.setResponsecode("00");
									model.setOagentno("100333");
									model.setPayCounter("");
									Thread.sleep(1000);
									int ii = pmsDaifuMerchantInfoDao.insert(model);
									if (ii == 1) {
										logger.info("添加代付补款记录成功！");
									}
								}
								
								break;
							}
							
							
					}
					
				}
				Thread.sleep(5000);
			}
	} catch (Exception e) {
		e.printStackTrace();
	}
}}
