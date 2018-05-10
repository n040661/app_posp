//package xdt.quickpay.hf.util;
//
//import java.util.HashMap;
//import java.util.Iterator;
//import java.util.Map;
//
//import org.apache.log4j.Logger;
//
//import com.alibaba.fastjson.JSON;
//import com.google.gson.Gson;
//
//import net.sf.json.JSONObject;
//import xdt.model.OriginalOrderInfo;
//import xdt.quickpay.hengfeng.util.Bean2QueryStrUtil;
//import xdt.quickpay.hf.comm.SampleConstant;
//import xdt.quickpay.hf.entity.PayRequestEntity;
//import xdt.quickpay.hf.entity.PayResponseEntity;
//import xdt.schedule.ThreadPool;
//import xdt.service.HfQPayService;
//import xdt.util.HttpURLConection;
//import xdt.util.HttpUtil;
//import xdt.util.JsPostThread;
//
//public class HfQueryThread extends Thread {
//	public static final Logger logger = Logger.getLogger(HfPostThread.class);
//	
//	//json工具
//	protected Gson gson=new Gson();
//
//	// T1查询支付地址
//	private static final String query_url1 = "rytpay/unionpay/wtz/token.do?api/v1/order/query/service";
//
//	// T0查询支付地址
//	private static final String query_url0 = "rytpay/unionpay/wtz/token/to.do?api/v1/order/query/service";
//
//	private PayRequestEntity entity;
//
//	public HfQPayService hfQPayService;
//
//	public HfQueryThread(PayRequestEntity pay) {
//		this.entity = pay;
//	}
//
//	@Override
//	public synchronized void run() {
//
//		// 线程处理
//		// 1、先查询本地库订单是否是完成状态
//		// 2、如果是完成状态跳过第三方查询并结束； 否则进行第三方查询 分隔时间进行查询在进行下一步处理
//		// 3、第三方查询结果后根据结果处理本地订单并结束
//
//		try {
//			Thread.sleep(2000);
//			for (int i = 0; i < 1000; i++) {
//				logger.info("进入线程中");
//				Map<String, String> param = new HashMap<String, String>();
//				PayResponseEntity pay = new PayResponseEntity();
//				// 设置上送信息
//				if (entity.getTranTp().equals("1")) {
//					param.put("appId", SampleConstant.APP_ID1);
//					param.put("appCode", SampleConstant.APP_CODE1);
//
//				} else if (entity.getTranTp().equals("0")) {
//					param.put("appId", SampleConstant.APP_ID0);
//					param.put("appCode", SampleConstant.APP_CODE0);
//				}
//				param.put("orderId", entity.getOrderId());
//				param.put("txnTime", entity.getTxnTime());
//				String jsonString = JSON.toJSONString(param);
//				logger.info("上送的数据:" + jsonString);
//				byte[] encodeData = null;
//				if (entity.getTranTp().equals("1")) {
//
//					encodeData = PlatKeyGenerator.encryptByPublicKey(jsonString.getBytes("UTF-8"),
//							SampleConstant.PUB_KEY1);
//				} else if (entity.getTranTp().equals("0")) {
//
//					encodeData = PlatKeyGenerator.encryptByPublicKey(jsonString.getBytes("UTF-8"),
//							SampleConstant.PUB_KEY0);
//				}
//				String data = PlatBase64Utils.encode(encodeData);
//
//				Map<String, String> result = new HashMap<String, String>();
//				result.put("data", data);
//				if (entity.getTranTp().equals("1")) {
//					result.put("appId", SampleConstant.APP_ID1);
//				} else if (entity.getTranTp().equals("0")) {
//					result.put("appId", SampleConstant.APP_ID0);
//				}
//
//				// 设置转发页面
//				System.out.println(result);
//				String returnJson = "";
//				if (entity.getTranTp().equals("1")) {
//					returnJson = EffersonPayService.postAsString(result, SampleConstant.REMOTE_PATH + query_url1,
//							"UTF-8");
//				} else if (entity.getTranTp().equals("0")) {
//					returnJson = EffersonPayService.postAsString(result, SampleConstant.REMOTE_PATH + query_url0,
//							"UTF-8");
//				}
//				JSONObject ob1 = JSONObject.fromObject(returnJson);
//				Iterator it1 = ob1.keys();
//				Map<String, String> map = new HashMap<>();
//				Map<String, String> resultMap=new HashMap<>();
//				while (it1.hasNext()) {
//					String key1 = (String) it1.next();
//					if (key1.equals("attributes")) {
//						String value = ob1.getString(key1);
//						logger.info("解析同步返回的结果:" + "\t" + value);
//						resultMap = JSON.parseObject(value, Map.class);
//						logger.info("解析之后的map集合："+resultMap);
//						JSONObject ob = JSONObject.fromObject(value);
//						String params = HttpUtil.toJson3(resultMap);
//						pay = gson.fromJson(params, PayResponseEntity.class);
//						Iterator it = ob.keys();
//						while (it.hasNext()) {
//							String key = (String) it.next();
//							if (key.equals("origRespCode")) {
//								String value1 = ob.getString(key);
//								logger.info("解析同步返回的状态:" + "\t" + value1);
//								map.put("origRespCode", value1);
//							}
//						}
//					}
//				}
//				if ("00".equals(map.get("origRespCode"))) {
//					// 修改订单状态
//					hfQPayService.otherInvoke(pay);
//					// 查询商户上送原始信息
//					OriginalOrderInfo originalInfo = hfQPayService.getOriginOrderInfo(entity.getOrderId());
//					logger.info("启动线程进行异步通知");
//					// 启线程进行异步通知
//					Bean2QueryStrUtil bean2Util = new Bean2QueryStrUtil();
//					String path = originalInfo.getBgUrl() + "?" + bean2Util.bean2QueryStr(pay);
//					logger.info("bgUrl 平台服务器重定向：" + path);
//
//					String result1 = HttpURLConection.httpURLConnectionPOST(originalInfo.getBgUrl(),
//							bean2Util.bean2QueryStr(pay));
//					JSONObject ob2 = JSONObject.fromObject(result1);
//					Iterator it2 = ob1.keys();
//					Map<String, String> map1 = new HashMap<>();
//					while (it2.hasNext()) {
//						String key1 = (String) it2.next();
//						if (key1.equals("success")) {
//							String value = ob1.getString(key1);
//							logger.info("异步回馈的结果:" + "\t" + value);
//							map1.put("success", value);
//						}
//					}
//					if (map1.get("success").equals("false")) {
//						
//						break;
//					}
//					
//				}
//				Thread.sleep(5000);
//			}
//
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//
//	}
//
//}
