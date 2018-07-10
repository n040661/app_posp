package xdt.controller;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang.StringUtils;
import org.apache.http.client.ClientProtocolException;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.multipart.MultipartFile;

import com.alibaba.fastjson.JSON;
import com.yeepay.shade.org.springframework.beans.factory.annotation.Autowired;
import com.yufusoft.payplatform.security.cipher.YufuCipher;
import com.yufusoft.payplatform.security.vo.ParamPacket;

import net.sf.json.JSONObject;
import xdt.dto.quickPay.entity.ConsumeResponseEntity;
import xdt.dto.quickPay.util.MbUtilThread;
import xdt.dto.scanCode.entity.ScanCodeResponseEntity;
import xdt.dto.transfer_accounts.entity.BalanceRequestEntity;
import xdt.dto.transfer_accounts.entity.BalanceResponseEntity;
import xdt.dto.transfer_accounts.entity.DaifuQueryRequestEntity;
import xdt.dto.transfer_accounts.entity.DaifuQueryResponseEntity;
import xdt.dto.transfer_accounts.entity.DaifuRequestEntity;
import xdt.dto.transfer_accounts.entity.DaifuResponseEntity;
import xdt.dto.yf.DisburseResultQueryReq;
import xdt.dto.yf.DisburseResultQueryRsp;
import xdt.dto.yf.DoYf;
import xdt.dto.yf.FileDownReq;
import xdt.dto.yf.RefundChequeResultDownReq;
import xdt.dto.yf.RefundChequeResultDownRsp;
import xdt.dto.yf.YufuCipherSupport;
import xdt.model.ChannleMerchantConfigKey;
import xdt.model.OriginalOrderInfo;
import xdt.model.PmsBusinessPos;
import xdt.model.PmsDaifuMerchantInfo;
import xdt.model.PmsMerchantInfo;
import xdt.quickpay.hengfeng.util.HttpClientUtil;
import xdt.quickpay.nbs.common.util.SignatureUtil;
import xdt.schedule.ThreadPool;
import xdt.service.IClientCollectionPayService;
import xdt.service.IGateWayService;
import xdt.service.ITotalPayService;
import xdt.util.BeanToMapUtil;
import xdt.util.DSDES;
import xdt.util.HttpURLConection;

/**
 * @author 作者 E-mail: 代付设置总路由
 * @version 创建时间：2017年12月27日 上午10:25:22 类说明
 */
@Controller
@RequestMapping("/totalPayController")
public class TotalPayController extends BaseAction {
	private Logger log = Logger.getLogger(this.getClass());
	//private Logger log = LoggerFactory.getLogger(this.getClass());
	@Resource
	private IClientCollectionPayService clientCollectionPayService;

	@Resource
	private ITotalPayService service;
	@Resource
	private IGateWayService gateWayService;
	/**
	 * 代付签名
	 * 
	 * @param hfbRequest
	 * @param response
	 */
	@RequestMapping(value = "paySign")
	public void paySign(DaifuRequestEntity payRequest, HttpServletResponse response) {

		log.info("--签名发来的参数：" + JSON.toJSONString(payRequest));
		ChannleMerchantConfigKey keyinfo = clientCollectionPayService.getChannelConfigKey(payRequest.getV_mid());
		// 获取商户秘钥
		String key = keyinfo.getMerchantkey();
		log.info("签名前数据**********支付:" + beanToMap(payRequest));
		// String md5 = MD5Utils.sign(paramSrc, key, "UTF-8");
		String sign = SignatureUtil.getSign(beanToMap(payRequest), key, log);
		log.info("签名**********支付:" + sign);
		try {
			outString(response, sign);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
	/**
	 * 代付查询签名
	 * 
	 * @param hfbRequest
	 * @param response
	 */
	@RequestMapping(value = "querySign")
	public void querySign(DaifuQueryRequestEntity payRequest, HttpServletResponse response) {

		log.info("--签名发来的参数：" + JSON.toJSONString(payRequest));
		ChannleMerchantConfigKey keyinfo = clientCollectionPayService.getChannelConfigKey(payRequest.getV_mid());
		// 获取商户秘钥
		String key = keyinfo.getMerchantkey();
		log.info("签名前数据**********支付:" + beanToMap(payRequest));
		// String md5 = MD5Utils.sign(paramSrc, key, "UTF-8");
		String sign = SignatureUtil.getSign(beanToMap(payRequest), key, log);
		log.info("签名**********支付:" + sign);
		try {
			outString(response, sign);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
	/**
	 * 代付余额查询签名
	 * 
	 * @param hfbRequest
	 * @param response
	 */
	@RequestMapping(value = "balanceSign")
	public void balanceSign(BalanceRequestEntity payRequest, HttpServletResponse response) {

		log.info("--签名发来的参数：" + JSON.toJSONString(payRequest));
		ChannleMerchantConfigKey keyinfo = clientCollectionPayService.getChannelConfigKey(payRequest.getV_mid());
		// 获取商户秘钥
		String key = keyinfo.getMerchantkey();
		log.info("签名前数据**********支付:" + beanToMap(payRequest));
		// String md5 = MD5Utils.sign(paramSrc, key, "UTF-8");
		String sign = SignatureUtil.getSign(beanToMap(payRequest), key, log);
		log.info("签名**********支付:" + sign);
		try {
			outString(response, sign);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	@RequestMapping(value = "merchant/virement/mer_query")
	public void query(DaifuQueryRequestEntity query, HttpServletResponse response) throws Exception {
		log.info("------代付查询上传参数：" + JSON.toJSONString(query));
		response.setHeader("Access-Control-Allow-Origin", "*");
		response.setContentType("text/html;charset=utf-8");
		Map<String, String> result = new HashMap<>();
		if (!StringUtils.isEmpty(query.getV_mid())) {

			    //检验数据是否合法
				log.info("下游上送签名串{}" + query.getV_sign());
				// 查询商户密钥
				ChannleMerchantConfigKey keyinfo = clientCollectionPayService
						.getChannelConfigKey(query.getV_mid());
				// ------------------------需要改签名
				String merchantKey = keyinfo.getMerchantkey();
				SignatureUtil signUtil = new SignatureUtil();

				Map map = BeanToMapUtil.convertBean(query);
				if (signUtil.checkSign(map, merchantKey, log)) {

					log.info("对比签名成功");
					result = service.daifuQuery(query);
					DaifuQueryResponseEntity daifuconsume = (DaifuQueryResponseEntity) BeanToMapUtil
							.convertMap(DaifuQueryResponseEntity.class, result);
					log.info("---返回数据签名签的数据:" + beanToMap(daifuconsume));
					String sign = SignatureUtil.getSign(beanToMap(daifuconsume), merchantKey, log);
					log.info("---返回数据签名:" + sign);
					result.put("v_sign", sign);

				} else {
					log.error("签名错误!");
					result.put("v_code", "02");
					result.put("v_msg", "签名错误!");
					log.info("返回的参数:" + JSON.toJSON(result));
				}

		} else {
			log.error("上送交易参数空!");
			result.put("v_code", "01");
			result.put("v_msg", "上送交易参数空");
		}
		try {
			outString(response, JSON.toJSONString(result));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		this.log.info("向下游 发送数据成功");

	}
	@RequestMapping(value = "merchant/virement/mer_payment")
	public void pay(DaifuRequestEntity payRequest, HttpServletResponse response,HttpServletRequest request) throws Exception {
		log.info("------代付上传参数：" + JSON.toJSONString(payRequest));
		response.setHeader("Access-Control-Allow-Origin", "*");
		response.setContentType("text/html;charset=utf-8");
		
		Map<String, String> result = new HashMap<>();
		if (!StringUtils.isEmpty(payRequest.getV_mid())) {
			ChannleMerchantConfigKey keyinfo = clientCollectionPayService
					.getChannelConfigKey(payRequest.getV_mid());
			PmsBusinessPos busInfo = gateWayService.selectKey(payRequest.getV_mid());
			MultipartFile file =payRequest.getV_fileName();
			SignatureUtil signUtil = new SignatureUtil();
			Map map = BeanToMapUtil.convertBean(payRequest);
			switch (busInfo.getChannelnum()) {
			case "YFWG":
				
				if (file!=null) {
					if(!file.isEmpty()) {
						try {
			                // 文件保存路径  
			                String filePath = request.getSession().getServletContext().getRealPath("/") + "upload/"  
			                        + file.getOriginalFilename();  
			                // 转存文件  
			                file.transferTo(new File(filePath)); 
			                if (signUtil.checkSign(map, keyinfo.getMerchantkey(), log)) {

								log.info("裕福批量代付对比签名成功");
								 result.put("type", "1");
								 result = service.pay(payRequest, result);

							} else {
								log.error("签名错误!");
								result.put("v_code", "02");
								result.put("v_msg", "签名错误!");
								log.info("返回的参数:" + JSON.toJSON(result));
							}
			               
			            } catch (Exception e) {  
			                e.printStackTrace();  
			            }  
					}else {
						log.info("文件有问题！");
					}
		            
		        } else {
		        	//result.put("v_code", "15");
		 			//result.put("v_msg", "代付文件为null");
		 			log.info("casnhu:"+JSON.toJSONString(payRequest));
		 			//检验数据是否合法
					if (service.validationStr(payRequest)) {
						log.info("下游上送签名串{}" + payRequest.getV_sign());
						// 查询商户密钥
						
						// ------------------------需要改签名
						String merchantKey = keyinfo.getMerchantkey();
						
						if (signUtil.checkSign(map, merchantKey, log)) {

							log.info("对比签名成功");
							result.put("type", "0");
							result = service.pay(payRequest, result);

						} else {
							log.error("签名错误!");
							result.put("v_code", "02");
							result.put("v_msg", "签名错误!");
							log.info("返回的参数:" + JSON.toJSON(result));
						}
					}else {
						
						log.error("数据不合法!");
						result.put("v_code", "12");
						result.put("v_msg", "数据不合法");
						
					}
					
		        } 
				break;
			default:
				//检验数据是否合法
				if (service.validationStr(payRequest)) {
					log.info("下游上送签名串{}" + payRequest.getV_sign());
					// 查询商户密钥
					
					// ------------------------需要改签名
					String merchantKey = keyinfo.getMerchantkey();
					
					if (signUtil.checkSign(map, merchantKey, log)) {

						log.info("对比签名成功");
						
						result = service.pay(payRequest, result);

					} else {
						log.error("签名错误!");
						result.put("v_code", "02");
						result.put("v_msg", "签名错误!");
						log.info("返回的参数:" + JSON.toJSON(result));
					}
				}else {
					
					log.error("数据不合法!");
					result.put("v_code", "12");
					result.put("v_msg", "数据不合法");
					
				}
				
				break;
			}
			DaifuResponseEntity daifuconsume = (DaifuResponseEntity) BeanToMapUtil
					.convertMap(DaifuResponseEntity.class, result);
			log.info("---返回数据签名签的数据:" + beanToMap(daifuconsume));
			String sign = SignatureUtil.getSign(beanToMap(daifuconsume), keyinfo.getMerchantkey(), log);
			log.info("---返回数据签名:" + sign);
			result.put("v_sign", sign);

		} else {
			log.error("上送交易参数空!");
			result.put("v_code", "01");
			result.put("v_msg", "上送交易参数空");
		}
		try {
			outString(response, JSON.toJSONString(result));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		this.log.info("向下游 发送数据成功");

	}
	
	
	@RequestMapping(value = "yfPay")
	public void YfPay(DaifuRequestEntity payRequest, HttpServletResponse response,HttpServletRequest request) throws Exception {
		log.info("------代付上传参数：" + payRequest);
		response.setHeader("Access-Control-Allow-Origin", "*");
		response.setContentType("text/html;charset=utf-8");
		MultipartFile file =payRequest.getV_fileName();
		System.out.println(file);
		Map<String, String> result = new HashMap<>();
		if (!StringUtils.isEmpty(payRequest.getV_mid())) {

			//检验数据是否合法

			log.info("下游上送签名串{}" + payRequest.getV_sign());
			// 查询商户密钥
			ChannleMerchantConfigKey keyinfo = clientCollectionPayService
					.getChannelConfigKey(payRequest.getV_mid());
			// ------------------------需要改签名
			String merchantKey = keyinfo.getMerchantkey();
			SignatureUtil signUtil = new SignatureUtil();

			Map map = BeanToMapUtil.convertBean(payRequest);
			if (signUtil.checkSign(map, merchantKey, log)) {
				log.info("对比签名成功");
				 if (!file.isEmpty()) {  
			            try {  
			                // 文件保存路径  
			                String filePath = request.getSession().getServletContext().getRealPath("/") + "upload/"  
			                        + file.getOriginalFilename();  
			                // 转存文件  
			                file.transferTo(new File(filePath));  
			                
			                result = service.pay(payRequest, result);
			            } catch (Exception e) {  
			                e.printStackTrace();  
			            }  
			        } else {
			        	result.put("v_code", "15");
			 			result.put("v_msg", "代付文件为null");
			        } 
				
				
				
				DaifuResponseEntity daifuconsume = (DaifuResponseEntity) BeanToMapUtil
						.convertMap(DaifuResponseEntity.class, result);
				log.info("---返回数据签名签的数据:" + beanToMap(daifuconsume));
				String sign = SignatureUtil.getSign(beanToMap(daifuconsume), merchantKey, log);
				log.info("---返回数据签名:" + sign);
				result.put("v_sign", sign);

			} else {
				log.error("签名错误!");
				result.put("v_code", "02");
				result.put("v_msg", "签名错误!");
				log.info("返回的参数:" + JSON.toJSON(result));
			}
		

		} else {
			log.error("上送交易参数空!");
			result.put("v_code", "01");
			result.put("v_msg", "上送交易参数空");
		}
		try {
			outString(response, JSON.toJSONString(result));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		this.log.info("向下游 发送数据成功");

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

	@RequestMapping("balance/scan_param")
	public void balance(BalanceRequestEntity scan, HttpServletRequest request, HttpServletResponse response)
			throws Exception {

		log.info("------代付上传参数：" + JSON.toJSONString(scan));
		response.setHeader("Access-Control-Allow-Origin", "*");
		response.setContentType("text/html;charset=utf-8");
		Map<String, String> result = new HashMap<String, String>();
		if (!StringUtils.isEmpty(scan.getV_mid())) {

			log.info("下游上送签名串{}" + scan.getV_sign());

			// 查询商户密钥
			ChannleMerchantConfigKey keyinfo = clientCollectionPayService.getChannelConfigKey(scan.getV_mid());
			// ------------------------需要改签名
			String merchantKey = keyinfo.getMerchantkey();
			SignatureUtil signUtil = new SignatureUtil();
			Map map = BeanToMapUtil.convertBean(scan);
			if (signUtil.checkSign(map, merchantKey, log)) {

				// 所有的流程通过 就发起查询
				result = service.balance(scan, result);
				BalanceResponseEntity balance = (BalanceResponseEntity) BeanToMapUtil
						.convertMap(BalanceResponseEntity.class, result);
				log.info("---返回数据签名签的数据:" + beanToMap(balance));
				String sign = SignatureUtil.getSign(beanToMap(balance), merchantKey, log);
				log.info("---返回数据签名:" + sign);
				result.put("v_sign", sign);
			} else {
				log.error("签名错误!");
				result.put("v_code", "02");
				result.put("v_msg", "签名错误!");
				log.info("返回的参数:" + JSON.toJSON(result));
			}

		} else {
			log.error("上送交易参数空!");
			result.put("v_code", "01");
			result.put("v_msg", "上送交易参数空");
		}
		try {
			outString(response, JSON.toJSONString(result));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		this.log.info("向下游 发送数据成功");
	}
	
	
	
	
	
	/**
	 * 裕福代付异步响应信息
	 * 
	 * @param request
	 * @param response
	 * @throws Exception
	 */
	@RequestMapping(value = "pay_notifyUrl")
	public void payReturnUrl(HttpServletResponse response, HttpServletRequest request) {
		log.info("裕福异步通知来了");
		log.info("data:"+request.getParameter("data"));
		log.info("enc:"+request.getParameter("enc"));
		log.info("sign:"+request.getParameter("sign"));
		log.info("merchantId:"+request.getParameter("merchantId"));
		String str;
		try {
		if (request.getParameter("merchantId") != null) {
			str = "success";
			outString(response, str);
		PmsBusinessPos businessPos =service.selectMer(request.getParameter("merchantId"));
		log.info("info:"+JSON.toJSONString(businessPos));
		log.info("pmsBusinessPos:"+JSON.toJSONString(businessPos));
		Map<String, String> result = new HashMap<String, String>();
		Map<String, String> results = new HashMap<String, String>();
		List<String> list =new ArrayList<>();
		// HJResponse hjResponses =new HJResponse();
		YufuCipher cipher = null;
		YufuCipherSupport instance = null;
		
			final String merCertPath= new File(this.getClass().getResource("/").getPath()).getParentFile().getParentFile().getCanonicalPath()+"//ky//"+request.getParameter("merchantId")+".cer";
			final String pfxPath= new File(this.getClass().getResource("/").getPath()).getParentFile().getParentFile().getCanonicalPath()+"//ky//"+request.getParameter("merchantId")+".pfx";
			final String pfxPwd= businessPos.getKek();
			
			cipher = YufuCipherSupport.getCipherInstance( merCertPath, pfxPath, pfxPwd,cipher,instance);
			//cipher = YufuCipherSupport.getCipherInstance(merCertPath, pfxPath, pfxPwd);
			Map<String, String> synchNotifyParams = cipher
					.unPack(new ParamPacket(request.getParameter("data"), request.getParameter("enc"), request.getParameter("sign")));
			log.info("裕福----异步通知返回数据:" + JSON.toJSONString(synchNotifyParams));
			String orderId=synchNotifyParams.get("merchantOrderId");
			Map<String, String> map =new HashMap<>();
			int nus =0;
			OriginalOrderInfo originalInfo = null;
			Map<String, String> mapParams = new HashMap<String, String>();
			PmsDaifuMerchantInfo pmsDaifuMerchantInfo =new PmsDaifuMerchantInfo();
			pmsDaifuMerchantInfo.setBatchNo(orderId);
			List<PmsDaifuMerchantInfo> pmsDaifuMerchantInfos =service.selectDaifu(pmsDaifuMerchantInfo);
				log.info("pmsDaifuMerchantInfos:"+JSON.toJSONString(pmsDaifuMerchantInfos));
				PmsMerchantInfo info =service.selecrMerId(pmsDaifuMerchantInfos.get(0).getMercId());
				results.put("v_mid", info.getMercId());
				results.put("v_oid", orderId);
				results.put("v_time", new SimpleDateFormat("yyyyMMddHHmmss").format(new Date()));
				Double dd=0.0;
				if("01".equals(synchNotifyParams.get("transStatus"))) {
					log.info("失败！");
					results.put("v_code", "00");
					results.put("v_msg", "受理成功");
					results.put("v_status", "1001");
					results.put("v_status_msg", "代付失败");
					service.UpdateDaifu(orderId, "02");
					dd=(dd+Double.parseDouble(pmsDaifuMerchantInfos.get(0).getAmount())+Double.parseDouble(pmsDaifuMerchantInfos.get(0).getPayCounter()))*100;
					log.info("手续费"+-Double.parseDouble(info.getPoundage()));
					mapParams.put("machId",info.getMercId());
					mapParams.put("payMoney",dd.toString());
					nus =service.updataPayT1(mapParams);
					for (PmsDaifuMerchantInfo pmsDaifuMerchantInfo2 : pmsDaifuMerchantInfos) {
						log.info("来了222");
						DaifuRequestEntity payRequest =new DaifuRequestEntity();
						payRequest.setV_mid(pmsDaifuMerchantInfo2.getMercId());
						payRequest.setV_batch_no(pmsDaifuMerchantInfo2.getBatchNo()+"/A");
						payRequest.setV_amount(pmsDaifuMerchantInfo2.getPayamount().replace("-", ""));
						payRequest.setV_sum_amount(pmsDaifuMerchantInfo2.getPayamount().replace("-", ""));
						payRequest.setV_identity(pmsDaifuMerchantInfo2.getIdentity());
						payRequest.setV_cardNo(pmsDaifuMerchantInfo2.getCardno());
						payRequest.setV_city(pmsDaifuMerchantInfo2.getCity());
						payRequest.setV_province(pmsDaifuMerchantInfo2.getProvince());
						payRequest.setV_type("1");
						payRequest.setV_pmsBankNo(pmsDaifuMerchantInfo2.getPmsbankno());
						result.put("fee", info.getPoundage());
						int ii =service.add(payRequest, info, result, "00");
						log.info("补款订单状态："+ii);
					}
					
					log.info("来了333");
				}else if("05".equals(synchNotifyParams.get("transStatus"))){
					results.put("v_code", "00");
					results.put("v_msg", "受理成功");
					results.put("v_status", "200");
					results.put("v_status_msg", "代付中");
					if("01".equals(synchNotifyParams.get("notifyType"))) {
						log.info("代付！");
						RefundChequeResultDownReq req = new RefundChequeResultDownReq();
						req.setMerchantId(synchNotifyParams.get("merchantId"));
						req.setOriMerchantOrderId(orderId); // 商户批次订单号长度必须在8-32之间
						req.setOriMerchantOrderTime(synchNotifyParams.get("merchantOrderTime")); // 商户订单时间格式为：yyyyMMddHHmmss
						req.setVersion("1.0.1");
						log.info("req:"+JSON.toJSONString(req));
						RefundChequeResultDownRsp rsp =DoYf.refundChequeResultPayDown(req, merCertPath, pfxPath, pfxPwd);
						log.info("rsp:"+JSON.toJSONString(rsp));
							if("0000".equals(rsp.getRespCode())) {
								String path =new File(this.getClass().getResource("/").getPath()).getParentFile().getParentFile().getCanonicalPath()+"/upload/"+rsp.getResultFileName();
								log.info("path:"+path);
								FileDownReq req1 = new FileDownReq();
								req1.setFileName(rsp.getResultFileName());
								req1.setFileType("01");
								req1.setMerchantId(synchNotifyParams.get("merchantId"));
								req1.setVersion("1.0.1");
								DoYf.downResourse(req1, path, merCertPath, pfxPath, pfxPwd);
								log.info("来了111");
								
								list =service.readZipContext(path);
								result.put("data", JSON.toJSONString(list));
								log.info("来了！！222"+JSON.toJSONString(list));
								for (int i = 0; i < list.size(); i++) {
									String record =list.get(i);
									System.out.println(record);
									String [] arr =record.split("\\|");
									System.out.println("代付 i:"+i+","+JSON.toJSON(arr));
									if("02".equals(arr[9])) {
										service.UpdateDaifu(orderId, arr[0], "00");
									}else if("01".equals(arr[9])) {
										service.UpdateDaifu(orderId, arr[0], "02");
										dd=(dd+Double.parseDouble(arr[7])+Double.parseDouble(info.getPoundage())*100);
										log.info("钱数："+dd.toString());
										map.put("payMoney",dd.toString());
										map.put("machId", info.getMercId());
										nus = service.updataPayT1(map);
										DaifuRequestEntity payRequest =new DaifuRequestEntity();
										payRequest.setV_mid(pmsDaifuMerchantInfos.get(0).getMercId());
										payRequest.setV_batch_no(pmsDaifuMerchantInfos.get(0).getBatchNo()+"/A");
										payRequest.setV_amount(Double.parseDouble(arr[7])/100+"");
										payRequest.setV_sum_amount(Double.parseDouble(arr[7])/100+"");
										payRequest.setV_identity(arr[0]);
										payRequest.setV_cardNo(arr[2]);
										payRequest.setV_city(arr[5]);
										payRequest.setV_province(arr[4]);
										payRequest.setV_type("1");
										payRequest.setV_pmsBankNo(arr[10]);
										payRequest.setV_realName(arr[1]);
										result.put("fee", info.getPoundage());
										int ii =service.add(payRequest, info, result, "00");
										log.info("补款订单状态："+ii);
									}
								}
							}else {
								log.info("下载代付文件失败！");
								result.put("v_code", "01");
								result.put("v_msg", rsp.getRespDesc());
							}
					}else if("02".equals(synchNotifyParams.get("notifyType"))) {
						log.info("退票！");
						RefundChequeResultDownReq req = new RefundChequeResultDownReq();
						req.setMerchantId(synchNotifyParams.get("merchantId"));
						req.setOriMerchantOrderTime(synchNotifyParams.get("merchantOrderTime")); // 商户订单时间格式为：yyyyMMddHHmmss
						req.setVersion("1.0.1");
						RefundChequeResultDownRsp refundChequeResultDown =DoYf.refundChequeResultDown(req, merCertPath, pfxPath, pfxPwd);
						log.info("refundChequeResultDown:"+JSON.toJSONString(refundChequeResultDown));
						
							if("0000".equals(refundChequeResultDown.getRespCode())) {
								String path =new File(this.getClass().getResource("/").getPath()).getParentFile().getParentFile().getCanonicalPath()+"//upload//"+refundChequeResultDown.getResultFileName();
								log.info("path:"+path);
								FileDownReq req1 = new FileDownReq();
								req1.setFileName(refundChequeResultDown.getResultFileName());
								req1.setFileType("02");
								req1.setMerchantId(synchNotifyParams.get("merchantId"));
								req1.setVersion("1.0.1");
								DoYf.downResourse(req1, path, merCertPath, pfxPath, pfxPwd);
								list =service.readZipContext(path);
								result.put("data", JSON.toJSONString(list));
								for (int i = 0; i < list.size(); i++) {
									String record =list.get(i);
									System.out.println(record);
									String [] arr =record.split("\\|");
									System.out.println("退票 i:"+i+","+JSON.toJSON(arr));
									service.UpdateDaifu(orderId, arr[0], "02");
									dd=(dd+Double.parseDouble(arr[7])+list.size()*Double.parseDouble(info.getPoundage())*100);
									map.put("payMoney", dd.toString());
									map.put("machId", info.getMercId());
									nus = service.updataPayT1(map);
									log.info("钱数："+dd.toString());
									DaifuRequestEntity payRequest =new DaifuRequestEntity();
									payRequest.setV_mid(pmsDaifuMerchantInfos.get(0).getMercId());
									payRequest.setV_batch_no(pmsDaifuMerchantInfos.get(0).getBatchNo()+"/A");
									payRequest.setV_amount(Double.parseDouble(arr[7])/100+"");
									payRequest.setV_sum_amount(Double.parseDouble(arr[7])/100+"");
									payRequest.setV_identity(arr[0]);
									payRequest.setV_cardNo(arr[2]);
									payRequest.setV_city(arr[5]);
									payRequest.setV_province(arr[4]);
									payRequest.setV_type("1");
									payRequest.setV_pmsBankNo(arr[10]);
									payRequest.setV_realName(arr[1]);
									result.put("fee", info.getPoundage());
									int ii =service.add(payRequest, info, result, "00");
									log.info("补款订单状态："+ii);
								}
							}else {
								log.info("下载退票文件失败！");
								result.put("v_code", "01");
								result.put("v_msg", refundChequeResultDown.getRespDesc());
							}
					}
					
					if(nus==1) {
						log.info("代付补款成功");
					}else {
						log.info("代付补款失败");
					}
				}
				log.info("能来吗");
				ChannleMerchantConfigKey keyinfo=new ChannleMerchantConfigKey();
				try {
					keyinfo = gateWayService.getChannelConfigKey(info.getMercId());
				} catch (Exception e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				originalInfo = gateWayService.getOriginOrderInfos(orderId);
				
				ConsumeResponseEntity consume = (ConsumeResponseEntity) BeanToMapUtil
						.convertMap(ConsumeResponseEntity.class, results);
				String sign = SignatureUtil.getSign(beanToMap(consume), keyinfo.getMerchantkey(), log);
				result.put("v_sign", sign);
				String params = HttpURLConection.parseParams(results);
				log.info("给下游同步的数据:" + params);
				String html =HttpClientUtil.post(originalInfo.getBgUrl(),params);
			    logger.info("下游返回状态" + html);
				JSONObject ob = JSONObject.fromObject(html);
				Iterator it = ob.keys();
				Map<String, String> resp=new HashMap<>();
				 while (it.hasNext()) {
				 String keys = (String) it.next();
				 if (keys.equals("success")) {
				 String value = ob.getString(keys);
				 logger.info("异步回馈的结果:" + "\t" + value);
				 resp.put("success", value);}
				 }
				 if (resp.get("success").equals("false")) {
				
				 logger.info("启动线程进行异步通知");
				 // 启线程进行异步通知
				 ThreadPool.executor(new MbUtilThread(originalInfo.getBgUrl(),params));
				 logger.info("向下游 发送数据成功");
				request.getSession();
			} else {
				str = "FAIL";
				outString(response, str);
			}
		  }
		} catch (Exception e) {
			
			e.printStackTrace();
		}
	}
		@RequestMapping(value="yfReturnUrl")
		public void yfReturnUrl(HttpServletResponse response, HttpServletRequest request) {
			log.info("实时单笔裕福异步通知来了");
			log.info("data:"+request.getParameter("data"));
			log.info("enc:"+request.getParameter("enc"));
			log.info("sign:"+request.getParameter("sign"));
			log.info("merchantId:"+request.getParameter("merchantId"));
			String str;
			try {
			if (request.getParameter("merchantId") != null) {
				str = "success";
				outString(response, str);
			PmsBusinessPos businessPos =service.selectMer(request.getParameter("merchantId"));
			log.info("info:"+JSON.toJSONString(businessPos));
			log.info("pmsBusinessPos:"+JSON.toJSONString(businessPos));
			Map<String, String> result = new HashMap<String, String>();
			Map<String, String> results = new HashMap<String, String>();
			// HJResponse hjResponses =new HJResponse();
			
				final String merCertPath= new File(this.getClass().getResource("/").getPath()).getParentFile().getParentFile().getCanonicalPath()+"//ky//"+request.getParameter("merchantId")+".cer";
				final String pfxPath= new File(this.getClass().getResource("/").getPath()).getParentFile().getParentFile().getCanonicalPath()+"//ky//"+request.getParameter("merchantId")+".pfx";
				final String pfxPwd= businessPos.getKek();
				YufuCipher cipher = null;
				YufuCipherSupport instance = null;
				cipher = YufuCipherSupport.getCipherInstance( merCertPath, pfxPath, pfxPwd,cipher,instance);
				//cipher = YufuCipherSupport.getCipherInstance(merCertPath, pfxPath, pfxPwd);
				Map<String, String> synchNotifyParams = cipher
						.unPack(new ParamPacket(request.getParameter("data"), request.getParameter("enc"), request.getParameter("sign")));
				log.info("裕福----实时单笔异步通知返回数据:" + JSON.toJSONString(synchNotifyParams));
				String orderId=synchNotifyParams.get("merchantOrderId");
				int nus =0;
				OriginalOrderInfo originalInfo = null;
				Map<String, String> mapParams = new HashMap<String, String>();
				PmsDaifuMerchantInfo pmsDaifuMerchantInfo =new PmsDaifuMerchantInfo();
				pmsDaifuMerchantInfo.setBatchNo(orderId);
				List<PmsDaifuMerchantInfo> pmsDaifuMerchantInfos =service.selectDaifu(pmsDaifuMerchantInfo);
					log.info("pmsDaifuMerchantInfos:"+JSON.toJSONString(pmsDaifuMerchantInfos));
					PmsMerchantInfo info =service.selecrMerId(pmsDaifuMerchantInfos.get(0).getMercId());
					results.put("v_mid", info.getMercId());
					results.put("v_oid", orderId);
					results.put("v_time", new SimpleDateFormat("yyyyMMddHHmmss").format(new Date()));
					Double dd=0.0;
					if("02".equals(synchNotifyParams.get("notifyType"))||"03".equals(synchNotifyParams.get("notifyType"))||"05".equals(synchNotifyParams.get("notifyType"))||"02".equals(synchNotifyParams.get("notifyType"))) {
						log.info("实时单笔裕福退票成功！");
						results.put("v_code", "00");
						results.put("v_msg", "受理成功");
						results.put("v_status", "1001");
						results.put("v_status_msg", "代付失败,退票成功");
						service.UpdateDaifu(orderId, "02");
						dd=(dd+Double.parseDouble(pmsDaifuMerchantInfos.get(0).getAmount())+Double.parseDouble(pmsDaifuMerchantInfos.get(0).getPayCounter()))*100;
						log.info("手续费"+-Double.parseDouble(info.getPoundage()));
						mapParams.put("machId",info.getMercId());
						mapParams.put("payMoney",dd.toString());
						if("D0".equals(pmsDaifuMerchantInfos.get(0).getRemarks())) {
							nus =service.updataPay(mapParams);
						}else if("T1".equals(pmsDaifuMerchantInfos.get(0).getRemarks())) {
							nus =service.updataPayT1(mapParams);
						}
						for (PmsDaifuMerchantInfo pmsDaifuMerchantInfo2 : pmsDaifuMerchantInfos) {
							log.info("来了222");
							DaifuRequestEntity payRequest =new DaifuRequestEntity();
							payRequest.setV_mid(pmsDaifuMerchantInfo2.getMercId());
							payRequest.setV_batch_no(pmsDaifuMerchantInfo2.getBatchNo()+"/A");
							payRequest.setV_amount(pmsDaifuMerchantInfo2.getPayamount().replace("-", ""));
							payRequest.setV_sum_amount(pmsDaifuMerchantInfo2.getPayamount().replace("-", ""));
							payRequest.setV_identity(pmsDaifuMerchantInfo2.getIdentity());
							payRequest.setV_realName(pmsDaifuMerchantInfo2.getRealname());
							payRequest.setV_cardNo(pmsDaifuMerchantInfo2.getCardno());
							payRequest.setV_city(pmsDaifuMerchantInfo2.getCity());
							payRequest.setV_province(pmsDaifuMerchantInfo2.getProvince());
							if("D0".equals(pmsDaifuMerchantInfo2.getRemarks())) {
								payRequest.setV_type("0");
							}else if("T1".equals(pmsDaifuMerchantInfo2.getRemarks())) {
								payRequest.setV_type("1");
							}
							payRequest.setV_pmsBankNo(pmsDaifuMerchantInfo2.getPmsbankno());
							result.put("fee", info.getPoundage());
							int ii =service.add(payRequest, info, result, "00");
							log.info("补款订单状态："+ii);
						}
						log.info("来了333");
					}else if("01".equals(synchNotifyParams.get("notifyType"))){
						results.put("v_code", "00");
						results.put("v_msg", "受理成功");
						if("04".equals(synchNotifyParams.get("transStatus"))) {
							results.put("v_status", "1001");
							results.put("v_status_msg", "代付失败");
							service.UpdateDaifu(orderId, "02");
							dd=(dd+Double.parseDouble(pmsDaifuMerchantInfos.get(0).getAmount())+Double.parseDouble(pmsDaifuMerchantInfos.get(0).getPayCounter()))*100;
							log.info("手续费"+-Double.parseDouble(info.getPoundage()));
							mapParams.put("machId",info.getMercId());
							mapParams.put("payMoney",dd.toString());
							if("D0".equals(pmsDaifuMerchantInfos.get(0).getRemarks())) {
								nus =service.updataPay(mapParams);
							}else if("T1".equals(pmsDaifuMerchantInfos.get(0).getRemarks())) {
								nus =service.updataPayT1(mapParams);
							}
							for (PmsDaifuMerchantInfo pmsDaifuMerchantInfo2 : pmsDaifuMerchantInfos) {
								log.info("来了222");
								DaifuRequestEntity payRequest =new DaifuRequestEntity();
								payRequest.setV_mid(pmsDaifuMerchantInfo2.getMercId());
								payRequest.setV_batch_no(pmsDaifuMerchantInfo2.getBatchNo()+"/A");
								payRequest.setV_amount(pmsDaifuMerchantInfo2.getPayamount().replace("-", ""));
								payRequest.setV_sum_amount(pmsDaifuMerchantInfo2.getPayamount().replace("-", ""));
								payRequest.setV_identity(pmsDaifuMerchantInfo2.getIdentity());
								payRequest.setV_realName(pmsDaifuMerchantInfo2.getRealname());
								payRequest.setV_cardNo(pmsDaifuMerchantInfo2.getCardno());
								payRequest.setV_city(pmsDaifuMerchantInfo2.getCity());
								payRequest.setV_province(pmsDaifuMerchantInfo2.getProvince());
								if("D0".equals(pmsDaifuMerchantInfo2.getRemarks())) {
									payRequest.setV_type("0");
								}else if("T1".equals(pmsDaifuMerchantInfo2.getRemarks())) {
									payRequest.setV_type("1");
								}
								payRequest.setV_pmsBankNo(pmsDaifuMerchantInfo2.getPmsbankno());
								result.put("fee", info.getPoundage());
								int ii =service.add(payRequest, info, result, "00");
								log.info("补款订单状态："+ii);
							}
						}else if("05".equals(synchNotifyParams.get("transStatus"))) {
							results.put("v_status", "0000");
							results.put("v_status_msg", "代付成功");
							service.UpdateDaifu(orderId, "00");
						}
						if(nus==1) {
							log.info("代付补款成功");
						}else {
							log.info("代付补款失败");
						}
					}
					log.info("实时单笔能来吗");
					ChannleMerchantConfigKey keyinfo=new ChannleMerchantConfigKey();
					try {
						keyinfo = gateWayService.getChannelConfigKey(info.getMercId());
					} catch (Exception e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
					originalInfo = gateWayService.getOriginOrderInfos(orderId);
					
					ConsumeResponseEntity consume = (ConsumeResponseEntity) BeanToMapUtil
							.convertMap(ConsumeResponseEntity.class, results);
					String sign = SignatureUtil.getSign(beanToMap(consume), keyinfo.getMerchantkey(), log);
					result.put("v_sign", sign);
					String params = HttpURLConection.parseParams(results);
					log.info("实时单笔给下游同步的数据:" + params);
					String html =HttpClientUtil.post(originalInfo.getBgUrl(),params);
				    logger.info("实时单笔下游返回状态" + html);
					JSONObject ob = JSONObject.fromObject(html);
					Iterator it = ob.keys();
					Map<String, String> resp=new HashMap<>();
					 while (it.hasNext()) {
					 String keys = (String) it.next();
					 if (keys.equals("success")) {
					 String value = ob.getString(keys);
					 logger.info("异步回馈的结果:" + "\t" + value);
					 resp.put("success", value);}
					 }
					 if (resp.get("success").equals("false")) {
					
					 logger.info("启动线程进行异步通知");
					 // 启线程进行异步通知
					 ThreadPool.executor(new MbUtilThread(originalInfo.getBgUrl(),params));
					 logger.info("向下游 发送数据成功");
					request.getSession();
				} else {
					str = "FAIL";
					outString(response, str);
				}
			  }
			} catch (Exception e) {
				
				e.printStackTrace();
			}
		
	}/**
	 * 聚佰宝代付异步响应信息
	 * 
	 * @param request
	 * @param response
	 * @throws Exception
	 */
	@RequestMapping(value = "jbb_notifyUrl")
	public void jbbNotifyUrl(HttpServletResponse response, HttpServletRequest request) {
		logger.info("聚佰宝异步通知来了");
		logger.info("聚佰宝代付流水号:"+request.getParameter("merInside"));
		logger.info("聚佰宝商户号:"+request.getParameter("merNo"));
		logger.info("聚佰宝状态码:"+request.getParameter("status"));
		logger.info("聚佰宝批次号:"+request.getParameter("batchNo"));
		String str;
		try {
		if (request.getParameter("batchNo") != null) {
			str = "success";
			outString(response, str);
		PmsBusinessPos businessPos =service.selectMer(request.getParameter("merNo"));
		log.info("info:"+JSON.toJSONString(businessPos));
		log.info("pmsBusinessPos:"+JSON.toJSONString(businessPos));
		Map<String, String> result = new HashMap<String, String>();
		Map<String, String> results = new HashMap<String, String>();
		List<String> list =new ArrayList<>();
		// HJResponse hjResponses =new HJResponse();
		
			final String merCertPath= new File(this.getClass().getResource("/").getPath()).getParentFile().getParentFile().getCanonicalPath()+"//ky//"+request.getParameter("merchantId")+".cer";
			final String pfxPath= new File(this.getClass().getResource("/").getPath()).getParentFile().getParentFile().getCanonicalPath()+"//ky//"+request.getParameter("merchantId")+".pfx";
			final String pfxPwd= businessPos.getKek();
			YufuCipher cipher = null;
			YufuCipherSupport instance = null;
			cipher = YufuCipherSupport.getCipherInstance( merCertPath, pfxPath, pfxPwd,cipher,instance);
			//cipher = YufuCipherSupport.getCipherInstance(merCertPath, pfxPath, pfxPwd);
			Map<String, String> synchNotifyParams = cipher
					.unPack(new ParamPacket(request.getParameter("data"), request.getParameter("enc"), request.getParameter("sign")));
			log.info("----异步通知返回数据:" + JSON.toJSONString(synchNotifyParams));
			String orderId=synchNotifyParams.get("merchantOrderId");
			Map<String, String> map =new HashMap<>();
			int nus =0;
			OriginalOrderInfo originalInfo = null;
			Map<String, String> mapParams = new HashMap<String, String>();
			PmsDaifuMerchantInfo pmsDaifuMerchantInfo =new PmsDaifuMerchantInfo();
			pmsDaifuMerchantInfo.setBatchNo(orderId);
			List<PmsDaifuMerchantInfo> pmsDaifuMerchantInfos =service.selectDaifu(pmsDaifuMerchantInfo);
				log.info("pmsDaifuMerchantInfos:"+JSON.toJSONString(pmsDaifuMerchantInfos));
				PmsMerchantInfo info =service.selecrMerId(pmsDaifuMerchantInfos.get(0).getMercId());
				results.put("v_mid", info.getMercId());
				results.put("v_oid", orderId);
				Double dd=0.0;
				if("01".equals(synchNotifyParams.get("transStatus"))) {
					log.info("失败！");
					results.put("v_code", "00");
					results.put("v_msg", "受理成功");
					results.put("v_status", "1001");
					results.put("v_status_msg", "代付失败");
					service.UpdateDaifu(orderId, "02");
					dd=(dd+Double.parseDouble(pmsDaifuMerchantInfos.get(0).getAmount())+Double.parseDouble(pmsDaifuMerchantInfos.get(0).getPayCounter()))*100;
					log.info("手续费"+-Double.parseDouble(info.getPoundage()));
					mapParams.put("machId",info.getMercId());
					mapParams.put("payMoney",dd.toString());
					nus =service.updataPayT1(mapParams);
					for (PmsDaifuMerchantInfo pmsDaifuMerchantInfo2 : pmsDaifuMerchantInfos) {
						log.info("来了222");
						DaifuRequestEntity payRequest =new DaifuRequestEntity();
						payRequest.setV_mid(pmsDaifuMerchantInfo2.getMercId());
						payRequest.setV_batch_no(pmsDaifuMerchantInfo2.getBatchNo()+"/A");
						payRequest.setV_amount(pmsDaifuMerchantInfo2.getPayamount().replace("-", ""));
						payRequest.setV_sum_amount(pmsDaifuMerchantInfo2.getPayamount().replace("-", ""));
						payRequest.setV_identity(pmsDaifuMerchantInfo2.getIdentity());
						payRequest.setV_cardNo(pmsDaifuMerchantInfo2.getCardno());
						payRequest.setV_city(pmsDaifuMerchantInfo2.getCity());
						payRequest.setV_province(pmsDaifuMerchantInfo2.getProvince());
						payRequest.setV_type("1");
						payRequest.setV_pmsBankNo(pmsDaifuMerchantInfo2.getPmsbankno());
						result.put("fee", info.getPoundage());
						int ii =service.add(payRequest, info, result, "00");
						log.info("补款订单状态："+ii);
					}
					
					log.info("来了333");
				}else if("05".equals(synchNotifyParams.get("transStatus"))){
					results.put("v_code", "00");
					results.put("v_msg", "受理成功");
					results.put("v_status", "200");
					results.put("v_status_msg", "代付中");
					if("01".equals(synchNotifyParams.get("notifyType"))) {
						log.info("代付！");
						RefundChequeResultDownReq req = new RefundChequeResultDownReq();
						req.setMerchantId(synchNotifyParams.get("merchantId"));
						req.setOriMerchantOrderId(orderId); // 商户批次订单号长度必须在8-32之间
						req.setOriMerchantOrderTime(synchNotifyParams.get("merchantOrderTime")); // 商户订单时间格式为：yyyyMMddHHmmss
						req.setVersion("1.0.1");
						log.info("req:"+JSON.toJSONString(req));
						RefundChequeResultDownRsp rsp =DoYf.refundChequeResultPayDown(req, merCertPath, pfxPath, pfxPwd);
						log.info("rsp:"+JSON.toJSONString(rsp));
							if("0000".equals(rsp.getRespCode())) {
								String path =new File(this.getClass().getResource("/").getPath()).getParentFile().getParentFile().getCanonicalPath()+"/upload/"+rsp.getResultFileName();
								log.info("path:"+path);
								FileDownReq req1 = new FileDownReq();
								req1.setFileName(rsp.getResultFileName());
								req1.setFileType("01");
								req1.setMerchantId(synchNotifyParams.get("merchantId"));
								req1.setVersion("1.0.1");
								DoYf.downResourse(req1, path, merCertPath, pfxPath, pfxPwd);
								log.info("来了111");
								
								list =service.readZipContext(path);
								result.put("data", JSON.toJSONString(list));
								log.info("来了！！222"+JSON.toJSONString(list));
								for (int i = 0; i < list.size(); i++) {
									String record =list.get(i);
									System.out.println(record);
									String [] arr =record.split("\\|");
									System.out.println("代付 i:"+i+","+JSON.toJSON(arr));
									if("02".equals(arr[9])) {
										service.UpdateDaifu(orderId, arr[0], "00");
									}else if("01".equals(arr[9])) {
										service.UpdateDaifu(orderId, arr[0], "02");
										dd=(dd+Double.parseDouble(arr[7])+Double.parseDouble(info.getPoundage())*100);
										log.info("钱数："+dd.toString());
										map.put("payMoney",dd.toString());
										map.put("machId", info.getMercId());
										nus = service.updataPayT1(map);
										DaifuRequestEntity payRequest =new DaifuRequestEntity();
										payRequest.setV_mid(pmsDaifuMerchantInfos.get(0).getMercId());
										payRequest.setV_batch_no(pmsDaifuMerchantInfos.get(0).getBatchNo()+"/A");
										payRequest.setV_amount(Double.parseDouble(arr[7])/100+"");
										payRequest.setV_sum_amount(Double.parseDouble(arr[7])/100+"");
										payRequest.setV_identity(arr[0]);
										payRequest.setV_cardNo(arr[2]);
										payRequest.setV_city(arr[5]);
										payRequest.setV_province(arr[4]);
										payRequest.setV_type("1");
										payRequest.setV_pmsBankNo(arr[10]);
										payRequest.setV_realName(arr[1]);
										result.put("fee", info.getPoundage());
										int ii =service.add(payRequest, info, result, "00");
										log.info("补款订单状态："+ii);
									}
								}
							}else {
								log.info("下载代付文件失败！");
								result.put("v_code", "01");
								result.put("v_msg", rsp.getRespDesc());
							}
					}else if("02".equals(synchNotifyParams.get("notifyType"))) {
						log.info("退票！");
						RefundChequeResultDownReq req = new RefundChequeResultDownReq();
						req.setMerchantId(synchNotifyParams.get("merchantId"));
						req.setOriMerchantOrderTime(synchNotifyParams.get("merchantOrderTime")); // 商户订单时间格式为：yyyyMMddHHmmss
						req.setVersion("1.0.1");
						RefundChequeResultDownRsp refundChequeResultDown =DoYf.refundChequeResultDown(req, merCertPath, pfxPath, pfxPwd);
						log.info("refundChequeResultDown:"+JSON.toJSONString(refundChequeResultDown));
						
							if("0000".equals(refundChequeResultDown.getRespCode())) {
								String path =new File(this.getClass().getResource("/").getPath()).getParentFile().getParentFile().getCanonicalPath()+"//upload//"+refundChequeResultDown.getResultFileName();
								log.info("path:"+path);
								FileDownReq req1 = new FileDownReq();
								req1.setFileName(refundChequeResultDown.getResultFileName());
								req1.setFileType("02");
								req1.setMerchantId(synchNotifyParams.get("merchantId"));
								req1.setVersion("1.0.1");
								DoYf.downResourse(req1, path, merCertPath, pfxPath, pfxPwd);
								list =service.readZipContext(path);
								result.put("data", JSON.toJSONString(list));
								for (int i = 0; i < list.size(); i++) {
									String record =list.get(i);
									System.out.println(record);
									String [] arr =record.split("\\|");
									System.out.println("退票 i:"+i+","+JSON.toJSON(arr));
									service.UpdateDaifu(orderId, arr[0], "02");
									dd=(dd+Double.parseDouble(arr[7])+list.size()*Double.parseDouble(info.getPoundage())*100);
									map.put("payMoney", dd.toString());
									map.put("machId", info.getMercId());
									nus = service.updataPayT1(map);
									log.info("钱数："+dd.toString());
									DaifuRequestEntity payRequest =new DaifuRequestEntity();
									payRequest.setV_mid(pmsDaifuMerchantInfos.get(0).getMercId());
									payRequest.setV_batch_no(pmsDaifuMerchantInfos.get(0).getBatchNo()+"/A");
									payRequest.setV_amount(Double.parseDouble(arr[7])/100+"");
									payRequest.setV_sum_amount(Double.parseDouble(arr[7])/100+"");
									payRequest.setV_identity(arr[0]);
									payRequest.setV_cardNo(arr[2]);
									payRequest.setV_city(arr[5]);
									payRequest.setV_province(arr[4]);
									payRequest.setV_type("1");
									payRequest.setV_pmsBankNo(arr[10]);
									payRequest.setV_realName(arr[1]);
									result.put("fee", info.getPoundage());
									int ii =service.add(payRequest, info, result, "00");
									log.info("补款订单状态："+ii);
								}
							}else {
								log.info("下载退票文件失败！");
								result.put("v_code", "01");
								result.put("v_msg", refundChequeResultDown.getRespDesc());
							}
					}
					
					if(nus==1) {
						log.info("代付补款成功");
					}else {
						log.info("代付补款失败");
					}
				}
				log.info("能来吗");
				ChannleMerchantConfigKey keyinfo=new ChannleMerchantConfigKey();
				try {
					keyinfo = gateWayService.getChannelConfigKey(info.getMercId());
				} catch (Exception e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				originalInfo = gateWayService.getOriginOrderInfos(orderId);
				
				ConsumeResponseEntity consume = (ConsumeResponseEntity) BeanToMapUtil
						.convertMap(ConsumeResponseEntity.class, results);
				String sign = SignatureUtil.getSign(beanToMap(consume), keyinfo.getMerchantkey(), log);
				result.put("v_sign", sign);
				String params = HttpURLConection.parseParams(results);
				log.info("给下游同步的数据:" + params);
				String html =HttpClientUtil.post(originalInfo.getBgUrl(),params);
			    logger.info("下游返回状态" + html);
				JSONObject ob = JSONObject.fromObject(html);
				Iterator it = ob.keys();
				Map<String, String> resp=new HashMap<>();
				 while (it.hasNext()) {
				 String keys = (String) it.next();
				 if (keys.equals("success")) {
				 String value = ob.getString(keys);
				 logger.info("异步回馈的结果:" + "\t" + value);
				 resp.put("success", value);}
				 }
				 if (resp.get("success").equals("false")) {
				
				 logger.info("启动线程进行异步通知");
				 // 启线程进行异步通知
				 ThreadPool.executor(new MbUtilThread(originalInfo.getBgUrl(),params));
				 logger.info("向下游 发送数据成功");
				request.getSession();
			} else {
				str = "FAIL";
				outString(response, str);
			}
		  }
		} catch (Exception e) {
			
			e.printStackTrace();
		}
	
	}
	@RequestMapping(value="ysbNotifyUrl")
	public void ysbNotifyUrl(HttpServletResponse response,HttpServletRequest request) {
		log.info("银生宝代付异步来了！");
		//BufferedReader br;
		//String key ="";
		String result_code=request.getParameter("result_code");
		String result_msg =request.getParameter("result_msg");
		String amount =request.getParameter("amount");
		String orderId =request.getParameter("orderId");
		String mac =request.getParameter("mac");
		
		log.info("result_code:"+result_code);
		log.info("result_msg:"+result_msg);
		log.info("amount:"+amount);
		log.info("orderId:"+orderId);
		log.info("mac:"+mac);
		Map<String, String> maps =new HashMap<>();
		
		/*try {
			br = new BufferedReader(new InputStreamReader((ServletInputStream) request.getInputStream(), "UTF-8"));
			String line = null;
			StringBuffer sb = new StringBuffer();
			while ((line = br.readLine()) != null) {
				sb.append(line);
			}
			String appMsg = sb.toString();
			log.info("银生宝代付异步参数：" + appMsg);
			
			net.sf.json.JSONObject ob = net.sf.json.JSONObject.fromObject(appMsg);
			Iterator it = ob.keys();
			while (it.hasNext()) {
				key = (String) it.next();
				if (key.equals("result_code")) {
					result_code = ob.getString(key);
					System.out.println(result_code);
				}
				if (key.equals("result_msg")) {
					result_msg = ob.getString(key);
					System.out.println(result_msg);
				}
				if (key.equals("p3_uno")) {
					amount = ob.getString(key);
					System.out.println(amount);
				}
				if (key.equals("orderId")) {
					orderId = ob.getString(key);
					System.out.println(orderId);
				}
				if (key.equals("mac")) {
					mac = ob.getString(key);
					System.out.println(mac);
				}

			}
		} catch (Exception e) {
		log.info("获取银生宝代付异步参数异常："+e);
		}*/
		request.getSession();
		if(result_code!=""&&result_code!=null&&mac!=null&&mac!=""&&orderId!=""&&orderId!=null) {
			try {
				outString(response, "OK");
			} catch (IOException e) {
				e.printStackTrace();
			}
			PmsDaifuMerchantInfo pmsDaifuMerchantInfo =new PmsDaifuMerchantInfo();
			pmsDaifuMerchantInfo.setBatchNo(orderId);
			List<PmsDaifuMerchantInfo> pmsDaifuMerchantInfos =service.selectDaifu(pmsDaifuMerchantInfo);
				ChannleMerchantConfigKey keyinfo=new ChannleMerchantConfigKey();
				OriginalOrderInfo originalInfo=null;
				try {
					originalInfo  = this.gateWayService.getOriginOrderInfos(orderId);
					keyinfo = clientCollectionPayService.getChannelConfigKey(originalInfo.getPid());
				} catch (Exception e) {
					e.printStackTrace();
				}
				log.info("订单数据:" + JSON.toJSON(originalInfo));
				
				log.info("下游的异步地址" + originalInfo.getBgUrl());
				maps.put("v_mid", originalInfo.getPid());
				maps.put("v_oid", originalInfo.getOrderId());
				maps.put("v_txnAmt", originalInfo.getOrderAmount());
				maps.put("v_attach", originalInfo.getAttach());
				maps.put("v_code", "00");
				maps.put("v_time", new SimpleDateFormat("yyyyMMddHHmmss").format(new Date()));
				if("0000".equals(result_code)) {
					maps.put("v_status", "0000");
					maps.put("v_msg", "支付成功");
					try {
						service.UpdateDaifu(orderId, "00");
					} catch (Exception e) {
						log.info("银生宝修改成功代付状态异常："+e);
						e.printStackTrace();
					}
				}else {
					maps.put("v_status", "1001");
					maps.put("v_msg", result_msg);
					try {
						service.UpdateDaifu(orderId, "02");
					} catch (Exception e) {
						log.info("银生宝修改失败代付状态异常："+e);
						e.printStackTrace();
					}
					Map<String, String> map =new HashMap<>();
					map.put("machId",originalInfo.getPid());
					map.put("payMoney",(Double.parseDouble(pmsDaifuMerchantInfos.get(0).getAmount())+Double.parseDouble(pmsDaifuMerchantInfos.get(0).getPayCounter()))*100+"");
					int nus =service.updataPayT1(map);
					if(nus==1) {
						log.info("银生宝代付补款成功");
						DaifuRequestEntity entity =new DaifuRequestEntity();
		 				entity.setV_mid(pmsDaifuMerchantInfos.get(0).getMercId());
		 				entity.setV_batch_no(pmsDaifuMerchantInfos.get(0).getBatchNo()+"/A");
		 				entity.setV_amount(pmsDaifuMerchantInfos.get(0).getAmount());
		 				entity.setV_sum_amount(pmsDaifuMerchantInfos.get(0).getAmount());
		 				entity.setV_identity(pmsDaifuMerchantInfos.get(0).getIdentity());
		 				entity.setV_cardNo(pmsDaifuMerchantInfos.get(0).getCardno());
		 				entity.setV_city(pmsDaifuMerchantInfos.get(0).getCity());
		 				entity.setV_province(pmsDaifuMerchantInfos.get(0).getProvince());
		 				entity.setV_type("1");
		 				entity.setV_pmsBankNo(pmsDaifuMerchantInfos.get(0).getPmsbankno());
		 				PmsMerchantInfo merchantinfo =new PmsMerchantInfo();
						int ii;
						try {
							ii = service.add(entity, merchantinfo, maps, "00");
							log.info("补款订单状态："+ii);
						} catch (Exception e) {
							log.info("银生宝补款状态异常："+e);
							e.printStackTrace();
						}
						
					}else {
						log.info("银生宝代付补款失败");
					}
				}
				ScanCodeResponseEntity consume = (ScanCodeResponseEntity) BeanToMapUtil
						.convertMap(ScanCodeResponseEntity.class, maps);
				String signs = SignatureUtil.getSign(beanToMap(consume), keyinfo.getMerchantkey(), log);
				maps.put("v_sign", signs);
				String params = HttpURLConection.parseParams(maps);
				log.info("银生宝代付给下游同步的数据:" + params);
				try {
					response.sendRedirect(originalInfo.getPageUrl()+"?"+params);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				logger.info("银生宝代付向下游 发送数据成功");
			//}else {
			//	log.info("异步验签失败！");
			//}
		}else {
			try {
				outString(response, "FAIL");
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	/**
	 * 汇聚异步
	 * @param response
	 * @param request
	 */
	@RequestMapping(value="hjNotifyUrl")
	public void hjNotifyUrl(HttpServletResponse response,HttpServletRequest request) {
		log.info("汇聚代付异步来了！");
		
		BufferedReader br;
		String status="";
		String errorCode ="";
		String errorCodeDesc ="";
		String userNo ="";
		String merchantOrderNo ="";
		String platformSerialNo="";
		String receiverAccountNoEnc="";
		String receiverNameEnc="";
		String paidAmount="";
		String fee="";
		String hmac="";
		try {
			br = new BufferedReader(new InputStreamReader((ServletInputStream) request.getInputStream(), "UTF-8"));
			String line = null;
			StringBuffer sb = new StringBuffer();
			while ((line = br.readLine()) != null) {
				sb.append(line);
			}
			String appMsg = sb.toString();
			log.info("汇聚代付异步来了：" + appMsg);
			com.alibaba.fastjson.JSONObject json =com.alibaba.fastjson.JSONObject.parseObject(appMsg);
			status=json.getString("status");
			errorCode =json.getString("errorCode");
			errorCodeDesc =json.getString("errorCodeDesc");
			userNo =json.getString("userNo");
			merchantOrderNo =json.getString("merchantOrderNo");
			platformSerialNo=json.getString("platformSerialNo");
			receiverAccountNoEnc=json.getString("receiverAccountNoEnc");
			receiverNameEnc=json.getString("receiverNameEnc");
			paidAmount=json.getString("paidAmount");
			fee=json.getString("fee");
			hmac=json.getString("hmac");
			log.info("status:"+status);
			log.info("errorCode:"+errorCode);
			log.info("errorCodeDesc:"+errorCodeDesc);
			log.info("userNo:"+userNo);
			log.info("merchantOrderNo"+merchantOrderNo);
			log.info("platformSerialNo:"+platformSerialNo);
			log.info("receiverAccountNoEnc:"+receiverAccountNoEnc);
			log.info("receiverNameEnc:"+receiverNameEnc);
			log.info("paidAmount:"+paidAmount);
			log.info("fee:"+fee);
			log.info("hmac:"+hmac);
			
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} 
		Map<String, String> maps =new HashMap<>();
		Map<String, String> resp=new HashMap<>();
		request.getSession();
		if(status!=""&&status!=null&&hmac!=null&&hmac!=""&&merchantOrderNo!=""&&merchantOrderNo!=null) {
			try {
				resp.put("statusCode", "2001");
				resp.put("message", "成功");
				outString(response, JSON.toJSON(resp));
			} catch (IOException e) {
				e.printStackTrace();
			}
			PmsDaifuMerchantInfo pmsDaifuMerchantInfo =new PmsDaifuMerchantInfo();
			pmsDaifuMerchantInfo.setBatchNo(merchantOrderNo);
			List<PmsDaifuMerchantInfo> pmsDaifuMerchantInfos =service.selectDaifu(pmsDaifuMerchantInfo);
				ChannleMerchantConfigKey keyinfo=new ChannleMerchantConfigKey();
				OriginalOrderInfo originalInfo=null;
				try {
					originalInfo  = this.gateWayService.getOriginOrderInfos(merchantOrderNo);
					keyinfo = clientCollectionPayService.getChannelConfigKey(originalInfo.getPid());
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				log.info("订单数据:" + JSON.toJSON(originalInfo));
				
				log.info("下游的异步地址" + originalInfo.getBgUrl());
				maps.put("v_mid", originalInfo.getPid());
				maps.put("v_oid", originalInfo.getOrderId());
				maps.put("v_txnAmt", originalInfo.getOrderAmount());
				maps.put("v_attach", originalInfo.getAttach());
				maps.put("v_code", "00");
				maps.put("v_time", new SimpleDateFormat("yyyyMMddHHmmss").format(new Date()));
				if("205".equals(status)) {
					maps.put("v_status", "0000");
					maps.put("v_msg", "代付成功");
					try {
						service.UpdateDaifu(merchantOrderNo, "00");
					} catch (Exception e) {
						log.info("汇聚修改成功代付状态异常："+e);
						e.printStackTrace();
					}
				}else if("203".equals(status)||"203".equals(status)||"203".equals(status)||"203".equals(status)||"203".equals(status)){
					
				}else {
					maps.put("v_status", "1001");
					maps.put("v_msg", errorCodeDesc);
					try {
						service.UpdateDaifu(merchantOrderNo, "02");
					} catch (Exception e) {
						log.info("汇聚修改失败代付状态异常："+e);
						e.printStackTrace();
					}
					Map<String, String> map =new HashMap<>();
					map.put("machId",originalInfo.getPid());
					map.put("payMoney",(Double.parseDouble(pmsDaifuMerchantInfos.get(0).getAmount())+Double.parseDouble(pmsDaifuMerchantInfos.get(0).getPayCounter()))*100+"");
					int nus =service.updataPay(map);
					if(nus==1) {
						log.info("汇聚代付补款成功");
						DaifuRequestEntity entity =new DaifuRequestEntity();
		 				entity.setV_mid(pmsDaifuMerchantInfos.get(0).getMercId());
		 				entity.setV_batch_no(pmsDaifuMerchantInfos.get(0).getBatchNo()+"/A");
		 				entity.setV_amount(pmsDaifuMerchantInfos.get(0).getAmount());
		 				entity.setV_sum_amount(pmsDaifuMerchantInfos.get(0).getAmount());
		 				entity.setV_identity(pmsDaifuMerchantInfos.get(0).getIdentity());
		 				entity.setV_cardNo(pmsDaifuMerchantInfos.get(0).getCardno());
		 				entity.setV_city(pmsDaifuMerchantInfos.get(0).getCity());
		 				entity.setV_province(pmsDaifuMerchantInfos.get(0).getProvince());
		 				entity.setV_type("0");
		 				entity.setV_pmsBankNo(pmsDaifuMerchantInfos.get(0).getPmsbankno());
		 				PmsMerchantInfo merchantinfo =new PmsMerchantInfo();
						int ii;
						try {
							ii = service.add(entity, merchantinfo, maps, "00");
							log.info("汇聚补款订单状态："+ii);
						} catch (Exception e) {
							log.info("汇聚补款状态异常："+e);
							e.printStackTrace();
						}
						
					}else {
						log.info("汇聚代付补款失败");
					}
				}
				ScanCodeResponseEntity consume = (ScanCodeResponseEntity) BeanToMapUtil
						.convertMap(ScanCodeResponseEntity.class, maps);
				String signs = SignatureUtil.getSign(beanToMap(consume), keyinfo.getMerchantkey(), log);
				maps.put("v_sign", signs);
				String params = HttpURLConection.parseParams(maps);
				log.info("汇聚代付给下游同步的数据:" + params);
				try {
					response.sendRedirect(originalInfo.getPageUrl()+"?"+params);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				logger.info("汇聚代付向下游 发送数据成功");
			//}else {
			//	log.info("异步验签失败！");
			//}
		}else {
			try {
				resp.put("statusCode", "2002");
				resp.put("message", "未收到参数");
				outString(response, JSON.toJSON(resp));
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * 银盈通异步
	 * @param response
	 * @param request
	 */
	@RequestMapping(value="yytNotifyUrl")
	public void yytNotifyUrl(HttpServletResponse response,HttpServletRequest request) {
		log.info("银盈通代付异步来了！");
		
		String dstbdata= request.getParameter("dstbdata") ;
		String dstbdatasign= request.getParameter("dstbdatasign") ;
		Map<String, String> maps =new HashMap<>();
		request.getSession();
		//String sign="";
		
		Map<String, String> respMap =new HashMap<>();
		
		if(dstbdata!=""&&dstbdata!=null&&dstbdatasign!=null&&dstbdatasign!="") {
			/*try {
				sign=DSDES.getBlackData("1b2aeaa4".getBytes(), dstbdata.getBytes("UTF-8"));
			}  catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			if(sign.equals(dstbdatasign)) {*/
				try {
					outString(response, "00");
				} catch (IOException e) {
					e.printStackTrace();
				}
				
				String [] dstbdatas =dstbdata.split("&");
				for (int i = 0; i < dstbdatas.length; i++) {
					String[] dstbdatas1 =dstbdatas[i].split("=");
					respMap.put(dstbdatas1[0], dstbdatas1[1]);
				}
				PmsDaifuMerchantInfo pmsDaifuMerchantInfo =new PmsDaifuMerchantInfo();
				pmsDaifuMerchantInfo.setBatchNo(respMap.get("dsorderid"));
				List<PmsDaifuMerchantInfo> pmsDaifuMerchantInfos =service.selectDaifu(pmsDaifuMerchantInfo);
					ChannleMerchantConfigKey keyinfo=new ChannleMerchantConfigKey();
					OriginalOrderInfo originalInfo=null;
					try {
						originalInfo  = this.gateWayService.getOriginOrderInfos(respMap.get("dsorderid"));
						keyinfo = clientCollectionPayService.getChannelConfigKey(originalInfo.getPid());
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					log.info("银盈通订单数据:" + JSON.toJSON(originalInfo));
					
					log.info("银盈通下游的异步地址" + originalInfo.getBgUrl());
					maps.put("v_mid", originalInfo.getPid());
					maps.put("v_oid", originalInfo.getOrderId());
					maps.put("v_txnAmt", originalInfo.getOrderAmount());
					maps.put("v_attach", originalInfo.getAttach());
					maps.put("v_code", "00");
					maps.put("v_time", new SimpleDateFormat("yyyyMMddHHmmss").format(new Date()));
					if("00".equals(respMap.get("returncode"))) {
						maps.put("v_status", "0000");
						maps.put("v_msg", "代付成功");
						try {
							service.UpdateDaifu(respMap.get("dsorderid"), "00");
						} catch (Exception e) {
							log.info("银盈通修改成功代付状态异常："+e);
							e.printStackTrace();
						}
					}else {
						maps.put("v_status", "1001");
						maps.put("v_msg", respMap.get("errtext"));
						try {
							service.UpdateDaifu(respMap.get("dsorderid"), "02");
						} catch (Exception e) {
							log.info("银盈通修改失败代付状态异常："+e);
							e.printStackTrace();
						}
						Map<String, String> map =new HashMap<>();
						map.put("machId",originalInfo.getPid());
						map.put("payMoney",(Double.parseDouble(pmsDaifuMerchantInfos.get(0).getAmount())+Double.parseDouble(pmsDaifuMerchantInfos.get(0).getPayCounter()))*100+"");
						int nus =service.updataPay(map);
						if(nus==1) {
							log.info("银盈通代付补款成功");
							DaifuRequestEntity entity =new DaifuRequestEntity();
			 				entity.setV_mid(pmsDaifuMerchantInfos.get(0).getMercId());
			 				entity.setV_batch_no(pmsDaifuMerchantInfos.get(0).getBatchNo()+"/A");
			 				entity.setV_amount(pmsDaifuMerchantInfos.get(0).getAmount());
			 				entity.setV_sum_amount(pmsDaifuMerchantInfos.get(0).getAmount());
			 				entity.setV_identity(pmsDaifuMerchantInfos.get(0).getIdentity());
			 				entity.setV_cardNo(pmsDaifuMerchantInfos.get(0).getCardno());
			 				entity.setV_city(pmsDaifuMerchantInfos.get(0).getCity());
			 				entity.setV_province(pmsDaifuMerchantInfos.get(0).getProvince());
			 				entity.setV_type("0");
			 				entity.setV_pmsBankNo(pmsDaifuMerchantInfos.get(0).getPmsbankno());
			 				PmsMerchantInfo merchantinfo =new PmsMerchantInfo();
							int ii;
							try {
								ii = service.add(entity, merchantinfo, maps, "00");
								log.info("银盈通补款订单状态："+ii);
							} catch (Exception e) {
								log.info("银盈通补款状态异常："+e);
								e.printStackTrace();
							}
							
						}else {
							log.info("银盈通代付补款失败");
						}
					}
					ScanCodeResponseEntity consume = (ScanCodeResponseEntity) BeanToMapUtil
							.convertMap(ScanCodeResponseEntity.class, maps);
					String signs = SignatureUtil.getSign(beanToMap(consume), keyinfo.getMerchantkey(), log);
					maps.put("v_sign", signs);
					String params = HttpURLConection.parseParams(maps);
					log.info("银盈通代付给下游同步的数据:" + params);
					String html;
					try {
						html = HttpClientUtil.post(originalInfo.getBgUrl(), params);
						logger.info("下游返回状态" + html);
						JSONObject ob = JSONObject.fromObject(html);
						Iterator it = ob.keys();
						Map<String, String> resp = new HashMap<>();
						while (it.hasNext()) {
							String keys = (String) it.next();
							if (keys.equals("success")) {
								String value = ob.getString(keys);
								logger.info("异步回馈的结果:"+ value);
								resp.put("success", value);
							}
						}
						if (!resp.get("success").equals("true")) {
		
							logger.info("启动线程进行异步通知");
							// 启线程进行异步通知
							ThreadPool.executor(new MbUtilThread(originalInfo.getBgUrl(), params));
							logger.info("银盈通代付向下游 发送数据成功");
						}
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				/*}else {
					log.info("银盈通代付异步验签失败！");
					try {
						outString(response, "01");
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}*/
		}else {
			try {
				outString(response, "01");
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	
	
	@RequestMapping(value="jmNotifyUrl")
	public void jmNotifyUrl(HttpServletResponse response,HttpServletRequest request) {
		log.info("金米代付异步来了！");
		BufferedReader br;
		String key ="";
		String rsp_code="";
		String rsp_msg ="";
		String req_pay_no ="";
		String account_name ="";
		String account_id_no ="";
		String account_mobile ="";
		String account_branch ="";
		String account_cnaps_no ="";
		String account_card_no ="";
		String order_amt ="";
		String state ="";
		String sign="";
		Map<String, String> maps =new HashMap<>();
		
		try {
			br = new BufferedReader(new InputStreamReader((ServletInputStream) request.getInputStream(), "UTF-8"));
			String line = null;
			StringBuffer sb = new StringBuffer();
			while ((line = br.readLine()) != null) {
				sb.append(line);
			}
			String appMsg = sb.toString();
			log.info("金米代付异步参数：" + appMsg);
			
			net.sf.json.JSONObject ob = net.sf.json.JSONObject.fromObject(appMsg);
			Iterator it = ob.keys();
			while (it.hasNext()) {
				key = (String) it.next();
				if (key.equals("rsp_code")) {
					rsp_code = ob.getString(key);
					System.out.println(rsp_code);
				}
				if (key.equals("rsp_msg")) {
					rsp_msg = ob.getString(key);
					System.out.println(rsp_msg);
				}
				if (key.equals("req_pay_no")) {
					req_pay_no = ob.getString(key);
					System.out.println(req_pay_no);
				}
				if (key.equals("account_name")) {
					account_name = ob.getString(key);
					System.out.println(account_name);
				}
				if (key.equals("account_id_no")) {
					account_id_no = ob.getString(key);
					System.out.println(account_id_no);
				}
				if (key.equals("account_mobile")) {
					account_mobile = ob.getString(key);
					System.out.println(account_mobile);
				}
				if (key.equals("account_branch")) {
					account_branch = ob.getString(key);
					System.out.println(account_branch);
				}
				if (key.equals("account_cnaps_no")) {
					account_cnaps_no = ob.getString(key);
					System.out.println(account_cnaps_no);
				}
				if (key.equals("account_card_no")) {
					account_card_no = ob.getString(key);
					System.out.println(account_card_no);
				}
				if (key.equals("order_amt")) {
					order_amt = ob.getString(key);
					System.out.println(order_amt);
				}
				if (key.equals("state")) {
					state = ob.getString(key);
					System.out.println(state);
				}
				if (key.equals("sign")) {
					sign = ob.getString(key);
					System.out.println(sign);
				}

			}
		} catch (Exception e) {
		log.info("获取金米代付异步参数异常："+e);
		}
		if(rsp_code!=""&&rsp_code!=null&&sign!=null&&sign!=""&&req_pay_no!=""&&req_pay_no!=null) {
			log.info("来了！！！");
			try {
				outString(response, "SUCCESS");
			} catch (IOException e) {
				log.info("金米代付给上游发送参数异常："+e);
				e.printStackTrace();
			}
			log.info("来了！！！!");
			PmsDaifuMerchantInfo pmsDaifuMerchantInfo =new PmsDaifuMerchantInfo();
			pmsDaifuMerchantInfo.setBatchNo(req_pay_no);
			log.info("1来了！！！");
			List<PmsDaifuMerchantInfo> pmsDaifuMerchantInfos =service.selectDaifu(pmsDaifuMerchantInfo);
			log.info("2来了！！！");
				ChannleMerchantConfigKey keyinfo=new ChannleMerchantConfigKey();
				OriginalOrderInfo originalInfo=null;
				try {
					log.info("3来了！！！");
					originalInfo  = this.gateWayService.getOriginOrderInfos(req_pay_no);
					log.info("4来了！！！");
					keyinfo = clientCollectionPayService.getChannelConfigKey(originalInfo.getPid());
					log.info("5来了！！！");
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				log.info("金米订单数据:" + JSON.toJSON(originalInfo));
				
				log.info("金米下游的异步地址" + originalInfo.getBgUrl());
				maps.put("v_mid", originalInfo.getPid());
				log.info("金米来了11！");
				maps.put("v_oid", originalInfo.getOrderId());
				log.info("金米来了22！");
				maps.put("v_txnAmt", originalInfo.getOrderAmount());
				log.info("金米来了33！");
				maps.put("v_attach", originalInfo.getAttach());
				log.info("金米来了44！");
				maps.put("v_code", "00");
				maps.put("v_time", new SimpleDateFormat("yyyyMMddHHmmss").format(new Date()));
				log.info("金米来了！33");
				if("00".equals(rsp_code)) {
					if("0".equals(state)){
						maps.put("v_status", "0000");
						maps.put("v_msg", "支付成功");
						log.info("金米来了！11");
						try {
							service.UpdateDaifu(req_pay_no, "00");
							log.info("金米来了！22");
						} catch (Exception e) {
							log.info("金米修改成功代付状态异常："+e);
							e.printStackTrace();
						}
					}else if("1".equals(state)){
						maps.put("v_status", "1001");
						maps.put("v_msg", rsp_msg);
						try {
							service.UpdateDaifu(req_pay_no, "02");
						} catch (Exception e) {
							log.info("金米修改失败代付状态异常："+e);
							e.printStackTrace();
						}
						Map<String, String> map =new HashMap<>();
						map.put("machId",originalInfo.getPid());
						map.put("payMoney",(Double.parseDouble(pmsDaifuMerchantInfos.get(0).getAmount())+Double.parseDouble(pmsDaifuMerchantInfos.get(0).getPayCounter()))*100+"");
						int nus =service.updataPayT1(map);
						if(nus==1) {
							log.info("金米代付补款成功");
							DaifuRequestEntity entity =new DaifuRequestEntity();
			 				entity.setV_mid(pmsDaifuMerchantInfos.get(0).getMercId());
			 				entity.setV_batch_no(pmsDaifuMerchantInfos.get(0).getBatchNo()+"/A");
			 				entity.setV_amount(pmsDaifuMerchantInfos.get(0).getAmount());
			 				entity.setV_sum_amount(pmsDaifuMerchantInfos.get(0).getAmount());
			 				entity.setV_identity(pmsDaifuMerchantInfos.get(0).getIdentity());
			 				entity.setV_cardNo(pmsDaifuMerchantInfos.get(0).getCardno());
			 				entity.setV_city(pmsDaifuMerchantInfos.get(0).getCity());
			 				entity.setV_province(pmsDaifuMerchantInfos.get(0).getProvince());
			 				entity.setV_type("1");
			 				entity.setV_pmsBankNo(pmsDaifuMerchantInfos.get(0).getPmsbankno());
			 				PmsMerchantInfo merchantinfo =new PmsMerchantInfo();
							int ii;
							try {
								ii = service.add(entity, merchantinfo, maps, "00");
								log.info("金米补款订单状态："+ii);
							} catch (Exception e) {
								log.info("金米补款状态异常："+e);
								e.printStackTrace();
							}
							
						}else {
							log.info("金米代付补款失败");
						}
					}
				}else {
					maps.put("v_status", "1001");
					maps.put("v_msg", rsp_msg);
					try {
						service.UpdateDaifu(req_pay_no, "02");
					} catch (Exception e) {
						log.info("金米修改失败代付状态异常："+e);
						e.printStackTrace();
					}
					Map<String, String> map =new HashMap<>();
					map.put("machId",originalInfo.getPid());
					map.put("payMoney",(Double.parseDouble(pmsDaifuMerchantInfos.get(0).getAmount())+Double.parseDouble(pmsDaifuMerchantInfos.get(0).getPayCounter()))*100+"");
					int nus =service.updataPayT1(map);
					if(nus==1) {
						log.info("金米代付补款成功");
						DaifuRequestEntity entity =new DaifuRequestEntity();
		 				entity.setV_mid(pmsDaifuMerchantInfos.get(0).getMercId());
		 				entity.setV_batch_no(pmsDaifuMerchantInfos.get(0).getBatchNo()+"/A");
		 				entity.setV_amount(pmsDaifuMerchantInfos.get(0).getAmount());
		 				entity.setV_sum_amount(pmsDaifuMerchantInfos.get(0).getAmount());
		 				entity.setV_identity(pmsDaifuMerchantInfos.get(0).getIdentity());
		 				entity.setV_cardNo(pmsDaifuMerchantInfos.get(0).getCardno());
		 				entity.setV_city(pmsDaifuMerchantInfos.get(0).getCity());
		 				entity.setV_province(pmsDaifuMerchantInfos.get(0).getProvince());
		 				entity.setV_type("1");
		 				entity.setV_pmsBankNo(pmsDaifuMerchantInfos.get(0).getPmsbankno());
		 				PmsMerchantInfo merchantinfo =new PmsMerchantInfo();
						int ii;
						try {
							ii = service.add(entity, merchantinfo, maps, "00");
							log.info("金米补款订单状态："+ii);
						} catch (Exception e) {
							log.info("金米补款状态异常："+e);
							e.printStackTrace();
						}
						
					}else {
						log.info("金米代付补款失败");
					}
				}
				ScanCodeResponseEntity consume = (ScanCodeResponseEntity) BeanToMapUtil
						.convertMap(ScanCodeResponseEntity.class, maps);
				String signs = SignatureUtil.getSign(beanToMap(consume), keyinfo.getMerchantkey(), log);
				maps.put("v_sign", signs);
				String params = HttpURLConection.parseParams(maps);
				log.info("金米代付给下游同步的数据:" + params);
				try {
					response.sendRedirect(originalInfo.getPageUrl()+"?"+params);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				logger.info("金米代付向下游 发送数据成功");
			//}else {
			//	log.info("异步验签失败！");
			//}
		}else {
			try {
				outString(response, "FAIL");
			} catch (IOException e) {
				log.info("金米代付给上游发送参数异常："+e);
				e.printStackTrace();
			}
		}
	}
	
	@RequestMapping(value="selectPay")
	public void selectPay(DaifuRequestEntity payRequest, HttpServletResponse response) {
		
		Map<String, String> map =service.selectPay(payRequest);
		try {
			outString(response, JSON.toJSON(map.get("batchInfoList")));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	@RequestMapping(value="DownExcel")
	public void DownExcel (DaifuRequestEntity payRequest,String path , HttpServletResponse response) {
		Map<String, String> map =service.DownExcel(payRequest, path);
		log.info("返回参数："+JSON.toJSONString(map));
		try {
			outString(response, JSON.toJSON(map));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public static void main(String[] args) {
		String dstbdata="returncode=25&errtext=余额不足或其他错误&merchno=SHID20180704093&dsorderid=1530757612454&amount=0.02&orderid=null&transdate=20180705&transtime=103628";
		Map<String, String> map =new HashMap<>();
		String [] dstbdatas =dstbdata.split("&");
		for (int i = 0; i < dstbdatas.length; i++) {
			String[] dstbdatas1 =dstbdatas[i].split("=");
			map.put(dstbdatas1[0], dstbdatas1[1]);
		}
		System.out.println(JSON.toJSON(map));
	}
}
