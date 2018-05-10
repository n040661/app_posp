package xdt.aspect;

import java.net.URLDecoder;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

import javax.annotation.Resource;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Component;

import net.sf.json.JSONObject;
import xdt.dao.IPmsAppTransInfoDao;
import xdt.dao.IPospTransInfoDAO;
import xdt.model.OriginalOrderInfo;
import xdt.model.PmsAppTransInfo;
import xdt.model.PmsBusinessPos;
import xdt.model.PospTransInfo;
import xdt.service.IPmsAppTransInfoService;
import xdt.service.JsdsQrCodeService;
import xdt.util.HttpURLConection;
import xdt.util.HttpUtil;
import xdt.util.OrderStatusEnum;
import xdt.util.RSAUtil;
import xdt.util.UtilDate;

@Component
public class JsTimeTask {
	Logger logger = Logger.getLogger(JsTimeTask.class);
	
	@Resource
	private IPmsAppTransInfoService pmsAppTransInfoService;
	@Resource
	private JsdsQrCodeService jsdsQrCodeService;
	@Resource
	public IPmsAppTransInfoDao pmsAppTransInfoDao;
	@Resource
	public IPospTransInfoDAO pospTransInfoDAO;

	
	public void JsTimeSelect() throws Exception{
		
		List<PmsAppTransInfo> array1=pmsAppTransInfoService.selectOrderPmsAppTransInfo();
		for (PmsAppTransInfo pmsAppTransInfo : array1) {		
			// 查询上游商户号
			PmsBusinessPos busInfo = jsdsQrCodeService.selectKey(pmsAppTransInfo.getMercid());
			String private_key = busInfo.getKek();
			logger.info("秘钥:" + private_key);
			String merchantCode = busInfo.getBusinessnum();
			logger.info("商户号:" + merchantCode);
			String orderid = pmsAppTransInfo.getOrderid();
			logger.info("订单号:" + orderid);
			Map<String, String> params = new HashMap<String, String>();
			params.put("merchantCode", busInfo.getBusinessnum());
			params.put("orderNum", orderid);
			String apply = HttpUtil.parseParams(params);
			logger.info("生成签名前的数据:" + apply);
			byte[] sign = RSAUtil.encrypt(private_key, apply.getBytes());
			logger.info("上送的签名:" + sign);
			Map<String, String> map = new HashMap<String, String>();
			map.put("groupId", busInfo.getDepartmentnum());
			map.put("service", "SMZF006");
			map.put("signType", "RSA");
			map.put("sign", RSAUtil.base64Encode(sign));
			map.put("datetime", UtilDate.getOrderNum());
			String jsonmap = HttpUtil.parseParams(map);
			logger.info("上送数据:" + jsonmap);
			String respJson = HttpURLConection.httpURLConnectionPOST("http://180.96.28.2:8048/TransQueryInterface/TransRequest",//http://180.96.28.8:8044/TransInterface/TransRequest
					jsonmap);
			logger.info("**********江苏电商响应报文:{}" + respJson);
			Map<String, String> result = new HashMap<String, String>();
			if (respJson != null) {
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

					byte[] a = RSAUtil.verify(busInfo.getKek(), RSAUtil.base64Decode(baseSign));

					String Str = new String(a);

					logger.info("解析之后的数据:" + Str);

					String[] array = Str.split("\\&");

					logger.info("拆分数据:" + array);
					String[] list = array[0].split("\\=");
					if (list[0].equals("orderNum")) {
						logger.info("合作商订单号:" + list[1]);

						result.put("orderNum", list[1]);

					}
					String[] list1 = array[1].split("\\=");
					if (list1[0].equals("pl_orderNum")) {
						logger.info("平台订单号:" + list1[1]);
						result.put("pl_orderNum", list1[1]);

					}
					String[] list2 = array[2].split("\\=");
					if (list2[0].equals("pl_payMessage")) {
						logger.info("支付状态:" + list2[1]);
						result.put("pl_payMessage", list2[1]);
					}
					String[] list3 = array[3].split("\\=");
					if (list3[0].equals("pl_payState")) {
						logger.info("支付状态描述:" + list3[1]);
						result.put("payStatus", list3[1]);
					}
				} else {

					result.put("pl_msg", "交易失败");
				}
				//查询原始记录数据
				logger.info("原始订单号:" + orderid);
				OriginalOrderInfo orig = jsdsQrCodeService.getOriginOrderInfo(orderid);
				if(orig !=null) {
					// 流水信息
					PospTransInfo posp = pospTransInfoDAO.searchBytransOrderId(orderid);
					logger.info("流水表信息" + posp);
					// 订单信息
					if(posp==null) {
						continue;
					}
					PmsAppTransInfo trans = pmsAppTransInfoDao.searchOrderInfo(posp.getOrderId());
					logger.info("订单表信息" + pmsAppTransInfo);
					if ("4".equals(result.get("payStatus"))) {

						Calendar cal1 = Calendar.getInstance();
						TimeZone.setDefault(TimeZone.getTimeZone("GMT+8:00"));
						java.text.SimpleDateFormat sdf = new SimpleDateFormat("kk:mm:ss");

						if (sdf.parse(sdf.format(cal1.getTime())).getTime() > sdf.parse("03:00:00").getTime()
								&& sdf.parse(sdf.format(cal1.getTime())).getTime() < sdf.parse("22:30:00").getTime()) {
							logger.info("D0订单号:" + orderid);
							if ("105962".equals(busInfo.getDepartmentnum())) {
								
								jsdsQrCodeService.UpdatePmsMerchantInfo449(orig);
								
							} else if ("107382".equals(busInfo.getDepartmentnum())) {
								
								jsdsQrCodeService.UpdatePmsMerchantInfo(orig);
								
							}
						}
						// 支付成功
						trans.setStatus(OrderStatusEnum.paySuccess.getStatus());
						trans.setFinishtime(UtilDate.getDateFormatter());
						// 修改订单
						int updateAppTrans = pmsAppTransInfoDao.update(trans);
						if (updateAppTrans == 1) {
							// log.info("修改余额");
							// 修改余额
							logger.info(pmsAppTransInfo);
							// updateMerchantBanlance(pmsAppTransInfo);
							// 更新流水表
							posp.setResponsecode("00");
							posp.setPospsn(orderid);
							logger.info("更新流水");
							logger.info(posp);
							pospTransInfoDAO.updateByOrderId(posp);
							Thread.interrupted();
						}
					} else if ("5".equals(result.get("payStatus"))){
						trans.setStatus(OrderStatusEnum.payFail.getStatus());
						trans.setFinishtime(UtilDate.getDateFormatter());
						// 修改订单
						int updateAppTrans = pmsAppTransInfoDao.update(trans);
						if (updateAppTrans == 1) {
							// 更新流水表
							posp.setResponsecode("02");
							posp.setPospsn(orderid);
							logger.info("更新流水");
							logger.info(posp);
							pospTransInfoDAO.updateByOrderId(posp);
						}
					}else{
						trans.setStatus(OrderStatusEnum.waitingClientPay.getStatus());
						trans.setFinishtime(UtilDate.getDateFormatter());
						// 修改订单
						int updateAppTrans = pmsAppTransInfoDao.update(trans);
						if (updateAppTrans == 1) {
							// 更新流水表
							posp.setResponsecode("20");
							posp.setPospsn(orderid);
							logger.info("更新流水");
							logger.info(posp);
							pospTransInfoDAO.updateByOrderId(posp);
						}

					}
				}
				
			}
			
		}
	}
}
