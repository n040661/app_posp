package xdt.controller;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.yeepay.g3.facade.yop.ca.dto.DigitalEnvelopeDTO;
import com.yeepay.g3.facade.yop.ca.enums.CertTypeEnum;
import com.yeepay.g3.frame.yop.ca.DigitalEnvelopeUtils;
import com.yeepay.g3.sdk.yop.utils.InternalConfig;

import java.io.IOException;
import java.net.URLDecoder;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;
import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import net.sf.json.JSONObject;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import xdt.dto.quickPay.entity.QueryRequestEntity;
import xdt.dto.quickPay.entity.QueryResponseEntity;
import xdt.dto.quickPay.util.MbUtilThread;
import xdt.model.ChannleMerchantConfigKey;
import xdt.model.OriginalOrderInfo;
import xdt.model.PmsBusinessPos;
import xdt.quickpay.clearQuickPay.entity.ClearPayQueryEntity;
import xdt.quickpay.clearQuickPay.entity.ClearPayRequestEntity;
import xdt.quickpay.clearQuickPay.util.BeanToMapUtil;
import xdt.quickpay.clearQuickPay.util.HttpClientUtil;
import xdt.quickpay.clearQuickPay.util.MD5Util;
import xdt.quickpay.clearQuickPay.util.SignatureUtil;
import xdt.schedule.ThreadPool;
import xdt.service.IClearPayQuickService;

@Controller
@RequestMapping("clearPay")
public class ClearPayQucikController  extends BaseAction{
		
		@Resource(name="IClearPayQuickService")
		private IClearPayQuickService clearPayService;

		/**
		 * 快捷(直清)支付生成签名
		 * 
		 * @param request
		 * @param response
		 * @throws Exception
		 */
		@ResponseBody
		@RequestMapping(value = "clear_pay_sign")
		  public void clearPayScan(ClearPayRequestEntity entity, HttpServletRequest request, HttpServletResponse response)
		    throws Exception
		  {
		    response.setHeader("Access-Control-Allow-Origin", "*");
		    response.setContentType("text/html;charset=utf-8");
		    logger.info("下游上送的参数:" + entity);
		    
		    ChannleMerchantConfigKey keyinfo =clearPayService.getChannelConfigKey(entity.getV_mid());
		    String merchantKey = keyinfo.getMerchantkey();
		    logger.info("下游商户密钥:" + keyinfo);
		    String sign = SignatureUtil.getSign(beanToMap(entity), merchantKey);
		    entity.setV_sign(sign);
		    
		    request.setCharacterEncoding("UTF-8");
		    request.setAttribute("temp", entity);
		    request.getRequestDispatcher("/quick/clear/quick_clear_pay_submit.jsp").forward(request, response);
		  }
		/**
		 * 快捷(直清)查询生成签名
		 * 
		 * @param request
		 * @param response
		 * @throws Exception
		 */
		@ResponseBody
		@RequestMapping(value = "clear_pay__query_sign")
		  public void wapPayQueryScan(ClearPayQueryEntity entity, HttpServletRequest request, HttpServletResponse response)
		    throws Exception
		  {
		    response.setHeader("Access-Control-Allow-Origin", "*");
		    response.setContentType("text/html;charset=utf-8");
		    logger.info("下游上送的参数:" + entity);
		    
		    ChannleMerchantConfigKey keyinfo =clearPayService.getChannelConfigKey(entity.getV_mid());
		    String merchantKey = keyinfo.getMerchantkey();
		    logger.info("下游商户密钥:" + keyinfo);
		    String sign = SignatureUtil.getSign(beanToMap(entity), merchantKey);
		    entity.setV_sign(sign);
		    
		    request.setCharacterEncoding("UTF-8");
		    request.setAttribute("temp", entity);
		    request.getRequestDispatcher("/quick/quick_conformity_query_submit.jsp").forward(request, response);
		  }
		/**
		 * 快捷(直清)支付请求
		 * 
		 * @param request
		 * @param response
		 * @throws Exception
		 */
		  @ResponseBody
		  @RequestMapping(value = "quick/clear/pay")
		  public void quickClearPay(ClearPayRequestEntity param, HttpServletRequest request, HttpServletResponse response)
		    throws Exception
		  {
		    logger.info("############快捷(直清)支付##################");
		    response.setHeader("Access-Control-Allow-Origin", "*");
		    response.setContentType("text/html;charset=UTF-8");
		    Map<String, String> result = new HashMap();
		    PmsBusinessPos pmsBusinessPos = clearPayService.selectKey(param.getV_mid());
		    
		    ChannleMerchantConfigKey keyinfo = clearPayService.getChannelConfigKey(param.getV_mid());
		    
		    String merchantKey = keyinfo.getMerchantkey();
		    logger.info("下游上送参数:{}" + param);
		    if (!StringUtils.isEmpty(param.getV_mid()))
		    {
		      logger.info("下游上送签名串{}" + param.getV_sign());
		      SignatureUtil signUtil = new SignatureUtil();
		      Map map = BeanToMapUtil.convertBean(param);
		      if (SignatureUtil.checkSign(map, merchantKey))
		      {
		        logger.info("对比签名成功");
		        result = clearPayService.payHandle(param);
		      }
		      else
		      {
		        logger.error("签名错误!");
		        result.put("v_code", "02");
		        result.put("v_msg", "签名错误!");	        
		       logger.info("返回结果:{}" + result);
		      }
		    }
		    else
		    {
		      logger.error("上送交易参数空!");
		      result.put("v_code", "01");
		      result.put("v_msg", "上送交易参数空");
		     logger.info("返回结果:{}" + result);
		      
		    }
			String sign = SignatureUtil.getSigns(result, merchantKey);
			logger.info("---返回数据签名:" + sign);
			result.put("v_sign", sign);
		    outString(response, this.gson.toJson(result));
		  }
			/**
			 * 快捷(直清)查询请求
			 * 
			 * @param request
			 * @param response
			 * @throws Exception
			 */
		  @RequestMapping(value = "quickPay/clear/query")
			public void query(ClearPayQueryEntity query,HttpServletRequest request, HttpServletResponse response) throws Exception {
				logger.info("------快捷查询上传参数：" + JSON.toJSONString(query));
				response.setHeader("Access-Control-Allow-Origin", "*");
				response.setContentType("text/html;charset=utf-8");
				Map<String, String> result = new HashMap<>();
				if (!StringUtils.isEmpty(query.getV_mid())) {

					// 检验数据是否合法
					logger.info("下游上送签名串{}" + query.getV_sign());
					// 查询商户密钥
					ChannleMerchantConfigKey keyinfo = clearPayService.getChannelConfigKey(query.getV_mid());
					// ------------------------需要改签名
					String merchantKey = keyinfo.getMerchantkey();
					SignatureUtil signUtil = new SignatureUtil();

					Map map = BeanToMapUtil.convertBean(query);
					if (signUtil.checkSign(map, merchantKey)) {

						logger.info("对比签名成功");
						//result = clearPayService.quickQuery(query);
						QueryResponseEntity queryconsume = (QueryResponseEntity) BeanToMapUtil
								.convertMap(QueryResponseEntity.class, result);
						logger.info("---返回数据签名签的数据:" + beanToMap(queryconsume));
						String sign = SignatureUtil.getSign(beanToMap(queryconsume), merchantKey);
						logger.info("---返回数据签名:" + sign);
						result.put("v_sign", sign);

					} else {
						logger.error("签名错误!");
						result.put("v_code", "02");
						result.put("v_msg", "签名错误!");
					}

				} else {
					logger.error("上送交易参数空!");
					result.put("v_code", "01");
					result.put("v_msg", "上送交易参数空");
				}
				try {
					outString(response, JSON.toJSONString(result));
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				this.logger.info("向下游 发送数据成功");

			}

			/**
			 * 易宝异步响应信息
			 * 
			 * @param request
			 * @param response
			 * @throws Exception
			 */
			@ResponseBody
			@RequestMapping(value = "clearPayNotifyUrl")
			public void clearPayNotifyUrl(HttpServletRequest request, HttpServletResponse response) {
				try {
					
					logger.info("############易通异步##################");
					  Map<String, String> result = new HashMap<String,String>();
					String respCode = request.getParameter("respCode");
					String merOrderNum = request.getParameter("merOrderNum");
					String reserver3 = request.getParameter("reserver3");
					logger.info("易通异步获取参数：" + merOrderNum);
					if (!StringUtils.isEmpty(merOrderNum)) {
						response.getWriter().write("success");
						OriginalOrderInfo originalInfo = null;
						if (merOrderNum != null && merOrderNum != "") {
							originalInfo = clearPayService.getOriginOrderInfo(merOrderNum);
						}
						logger.info("易通支付异步原始订单交易时间:" + originalInfo.getOrderTime());
						result.put("v_mid", originalInfo.getPid());
						result.put("v_oid", originalInfo.getOrderId());
						result.put("v_txnAmt", originalInfo.getOrderAmount());
						result.put("v_time", originalInfo.getOrderTime());
						result.put("v_code", "00");
						result.put("v_msg", "请求成功");
						result.put("v_attach", originalInfo.getAttach());				
						if ("0000".equals(respCode)) {

							result.put("v_payStatus", "0000");
							result.put("v_payMsg", "支付成功");

						} else {
							result.put("v_payStatus", "1001");
							result.put("v_payMsg", "支付失败:"+new String(MD5Util.hexStr2Bytes(reserver3), "UTF-8"));
							logger.info("交易错误码:" + request.getParameter("payStatus") + ",错误信息:"
									+ URLDecoder.decode(request.getParameter("payMsg"), "UTF-8"));
						}
						ChannleMerchantConfigKey keyinfo =clearPayService.getChannelConfigKey(originalInfo.getPid());
						// 获取商户秘钥
						String key = keyinfo.getMerchantkey();
						// 修改订单状态
						clearPayService.otherInvoke(merOrderNum,respCode);
						logger.info("易通支付异步回调地址:" + originalInfo.getBgUrl());
						// 生成签名
						String sign = SignatureUtil.getSigns(result, key);
						result.put("v_sign", sign);

						logger.info("易通支付异步封装前参数：" + result);
						logger.info("易通支付异步封装后参数：" + HttpClientUtil.toJson(result));
						String html = HttpClientUtil.post(originalInfo.getBgUrl(),HttpClientUtil.toJson(result));
						logger.info("易通支付下游响应信息:" + html);
						JSONObject ob = JSONObject.fromObject(html);
						Iterator it = ob.keys();
						Map<String, String> map = new HashMap<>();
						while (it.hasNext()) {
							String keys = (String) it.next();
							if (keys.equals("success")) {
								String value = ob.getString(keys);
								logger.info("易通支付回馈的结果:" + "\t" + value);
								map.put("success", value);
							}
						}
						if (map.get("success").equals("false")) {

							logger.info("易宝支付启动线程进行异步通知");
							// 启线程进行异步通知
							ThreadPool.executor(new MbUtilThread(originalInfo.getBgUrl(), HttpClientUtil.toJson(result)));
						}
						logger.info("易宝支付向下游 发送数据成功");

					} else {
						response.getWriter().write("FAIL");
						logger.error("回调的参数为空!");
						result.put("v_code", "15");
						result.put("v_msg", "请求失败");
					}
					// outString(response, str);
				} catch (Exception e) {
					logger.info("易宝异步回调异常:" + e);
					e.printStackTrace();
				}
			}
}
