
/**   
 * @Title: WXQrCodeServiceImpl.java 
 * @Package: xdt.service.impl 
 * @Description: TODO
 * @author Shiwen . Li
 * @date 2016年12月3日 下午3:05:14 
 * @version 1.3.1 
 */


package xdt.service.impl;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.net.URL;
import java.net.URLConnection;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Service;

import com.google.gson.Gson;
import com.hmjr.wxp.util.Common;

import xdt.dao.ChannleMerchantConfigKeyDao;
import xdt.dao.IAppRateConfigDao;
import xdt.dao.IMerchantMineDao;
import xdt.dao.IPayCmmtufitDao;
import xdt.dao.IPmsAddressDao;
import xdt.dao.IPmsAppTransInfoDao;
import xdt.dao.IPmsGoodsOrderDao;
import xdt.dao.IPmsMerchantInfoDao;
import xdt.dao.IPmsMessageDao;
import xdt.dao.IPmsOrderHelpDao;
import xdt.dao.IPospTransInfoDAO;
import xdt.dao.IQuickpayPreRecordDao;
import xdt.dao.IQuickpayRecordDao;
import xdt.dao.OriginalOrderInfoDao;
import xdt.dto.BaseUtil;
import xdt.dto.pufa.PayRequestEntity;
import xdt.dto.weixin.CallbackDto;
import xdt.dto.weixin.ChangeRateDto;
import xdt.dto.weixin.PayRequestDto;
import xdt.dto.weixin.QueryRequestDto;
import xdt.dto.weixin.RegisterDto;
import xdt.dto.weixin.VerifyInfoDto;
import xdt.model.AppRateConfig;
import xdt.model.OriginalOrderInfo;
import xdt.model.PmsAppTransInfo;
import xdt.model.PmsBusinessInfo;
import xdt.model.PmsMerchantInfo;
import xdt.model.PmsWeixinMerchartInfo;
import xdt.model.PospRouteInfo;
import xdt.model.PospTransInfo;
import xdt.model.ResultInfo;
import xdt.quickpay.hengfeng.util.HttpClientUtil;
import xdt.quickpay.hengfeng.util.MD5Utils;
import xdt.quickpay.mobao.MobaoPayHandel;
import xdt.service.IMerchantCollectMoneyService;
import xdt.service.IPmsMessageService;
import xdt.service.IPublicTradeVerifyService;
import xdt.service.PmsWeixinMerchartInfoService;
import xdt.service.WXQrCodeService;
import xdt.util.JsonUtil;
import xdt.util.OrderStatusEnum;
import xdt.util.PaymentCodeEnum;
import xdt.util.QrCodeEnum;
import xdt.util.TradeTypeEnum;
import xdt.util.UtilDate;
import xdt.util.UtilMethod;

/** 
 * @Description 
 * @author Shiwen .Li
 * @date 2016年12月3日 下午3:05:14 
 * @version V1.3.1
 */

@SuppressWarnings("unchecked")
@Service
public class WXQrCodeServiceImpl extends BaseServiceImpl implements WXQrCodeService  {


	protected static Gson gson=new Gson();
	private Logger logger=Logger.getLogger(WXQrCodeServiceImpl.class);
	
	
	/**
	 * 微信商户信息
	 */
	@Resource
	public PmsWeixinMerchartInfoService weixinService;

	/**
	 * 商户信息服务层
	 */
	@Resource
	private IPmsMerchantInfoDao pmsMerchantInfoDao;
	
	@Resource
	private OriginalOrderInfoDao originalDao;
	

	@Resource
	private IMerchantMineDao merchantMineDao;
	@Resource
	public IPmsAppTransInfoDao pmsAppTransInfoDao;
	@Resource
	private IPmsGoodsOrderDao pmsGoodsOrderDao;
	@Resource
	private IPmsAddressDao pmsAddressDao;
	@Resource
	private IPmsOrderHelpDao pmsOrderHelpDao;
	@Resource
	private IPublicTradeVerifyService iPublicTradeVerifyService;
	@Resource
	IPayCmmtufitDao iPayCmmtufitDao;
	@Resource
	MobaoPayHandel mobaoPayHandel;
	@Resource
	IQuickpayRecordDao quickpayRecordDao;
	@Resource
	IPospTransInfoDAO pospTransInfoDAO;
	@Resource
	IMerchantCollectMoneyService merchantCollectMoneyService;
	@Resource
	IPmsMessageService pmsMessageService;
	@Resource
	IPmsMessageDao pmsMessageDao;
	@Resource
	private IAppRateConfigDao appRateConfigDao;
	@Resource
	private IQuickpayPreRecordDao quickpayPreRecordDao;

	/**
	 * 渠道商户密钥信息
	 */
	@Resource
	private ChannleMerchantConfigKeyDao cmckeyDao;
	
	@Override
	public Map<String, String> updatePay(PayRequestDto req) throws Exception {
		
		try{
			logger.info("******************生成二维码："+HF_WX_WeixinPayURL);

			//商户号
			String merchId=req.getMerchId();

			//金额
			String acount=req.getTotalFee();
			//商户订单号
			
			logger.info("******************根据商户号查询");
			
			PmsWeixinMerchartInfo weixin=weixinService.selectByPrimaryKey(req.getAccount());
			
			if(weixin==null){
				return setResp("0007", "下单失败！");
			}
			

			if(QrCodeEnum.weixin.type.equals(req.getOrderCode())){
				if(StringUtils.isEmpty(weixin.getWxRate())){
					return setResp("0008", "微信费率未同步！");
				}
			}
			if(QrCodeEnum.alipay.type.equals(req.getOrderCode())){
				if(StringUtils.isEmpty(weixin.getAlipayRate())){
					return setResp("0008", "支付宝费率未同步！");
				}
			}
			OriginalOrderInfo oriInfo = new OriginalOrderInfo();
			oriInfo.setMerchantOrderId(req.getOrderNo());
			oriInfo.setPid(req.getMerchId());

			oriInfo = originalDao.selectByOriginal(oriInfo);

			if (oriInfo != null) {
				logger.error("下单重复");
				setResp("000016", "下单重复");
			}else{

			PmsMerchantInfo merchantinfo=getMerchantInfo(merchId);
			
			logger.info("******************商户信息:"+merchantinfo);
			
			if(merchantinfo!=null){
				
					String orderNumber = UtilMethod.getOrderid("185");
					
					saveOriginAlInfo(req, orderNumber,merchId);
					
					logger.info("******************orderNumber:"+orderNumber);
					String oAgentNo = merchantinfo.getoAgentNo();
					
					logger.info("******************实际金额");
					String factAmount = acount;
					logger.info("******************校验欧单金额限制");
					ResultInfo payCheckResult = iPublicTradeVerifyService.amountVerifyOagent((int) Double.parseDouble(factAmount),TradeTypeEnum.merchantCollect, oAgentNo);
					
					if (payCheckResult.getErrCode().equals("0")) {
						
						logger.info("******************校验欧单模块是否开启");
						ResultInfo payCheckResult1 = iPublicTradeVerifyService.moduleVerifyOagent(TradeTypeEnum.merchantCollect, oAgentNo);
						
						if (payCheckResult1.getErrCode().equals("0")) {
							logger.info("******************校验商户模块是否开启");
							ResultInfo payCheckResult3 = iPublicTradeVerifyService.moduelVerifyMer(TradeTypeEnum.merchantCollect, merchId);
							if (payCheckResult3.getErrCode().equals("0")) {
								
								logger.info("******************校验商户金额限制");
								Map<String, String> paramMap = new HashMap<String, String>();
								paramMap.put("mercid", merchId);
								paramMap.put("businesscode",TradeTypeEnum.merchantCollect.getTypeCode());// 业务编号
								paramMap.put("oAgentNo", oAgentNo);
								//
								logger.info("******************商户 商城 业务信息 ");
								Map<String, String> resultMap = merchantMineDao.queryBusinessInfo(paramMap);
								
								if (!(resultMap == null || resultMap.size() == 0)) {
									
									String maxTransMoney = resultMap.get("MAX_AMOUNT"); // 每笔最大交易金额
									String minTransMoney = resultMap.get("MIN_AMOUNT"); // 每笔最小交易金额
									String paymentAmount = factAmount;// 交易金额
									
									if (new BigDecimal(paymentAmount).compareTo(new BigDecimal(maxTransMoney)) == 1) {
										// 金额超过最大金额
										logger.info("******************交易金额大于最打金额");
										return setResp("0004", "交易金额大于最打金额");
									} else if (new BigDecimal(paymentAmount).compareTo(new BigDecimal(minTransMoney)) == -1) {
										logger.info("******************交易金额小于最小金额");
										return setResp("0004", "金额小于最小交易金额");
									}else{
										
										// 
										logger.info("******************组装订单数据");
										PmsAppTransInfo pmsAppTransInfo = new PmsAppTransInfo();
										logger.info("******************写入欧单编号");
										
										pmsAppTransInfo.setoAgentNo(oAgentNo);
										pmsAppTransInfo.setStatus(OrderStatusEnum.initlize.getStatus());// 订单初始化状态
										pmsAppTransInfo.setTradetype(TradeTypeEnum.merchantCollect.getTypeName());// 业务功能模块名称
										logger.info(req.getOrderCode());
										logger.info("******************商户收款");
										logger.info(UtilDate.getDateFormatter());
										pmsAppTransInfo.setTradetime(UtilDate.getDateFormatter()); // 设置时间
										pmsAppTransInfo.setMercid(merchantinfo.getMercId());
										pmsAppTransInfo.setTradetypecode(TradeTypeEnum.merchantCollect.getTypeCode());// 业务功能模块编号
										pmsAppTransInfo.setOrderid(orderNumber);// 设置订单号
										pmsAppTransInfo.setUrl(req.getNotify_url()); //设置下游回调地址
										if(QrCodeEnum.weixin.type.equals(req.getOrderCode())){
											pmsAppTransInfo.setPaymenttype(PaymentCodeEnum.weixinPay.getTypeName());
											pmsAppTransInfo.setPaymentcode(PaymentCodeEnum.weixinPay.getTypeCode());
										}
										if(QrCodeEnum.alipay.type.equals(req.getOrderCode())){
											pmsAppTransInfo.setPaymenttype(PaymentCodeEnum.zhifubaoPay.getTypeName());
											pmsAppTransInfo.setPaymentcode(PaymentCodeEnum.zhifubaoPay.getTypeCode());
										}
										
										BigDecimal factBigDecimal = new BigDecimal(factAmount);
										BigDecimal orderAmountBigDecimal = new BigDecimal(factAmount);

										pmsAppTransInfo.setFactamount(factBigDecimal.stripTrailingZeros().toPlainString());// 实际金额
										pmsAppTransInfo.setOrderamount(orderAmountBigDecimal.stripTrailingZeros().toPlainString());// 订单金额
										pmsAppTransInfo.setDrawMoneyType("1");// 普通提款
										
										logger.info("******************插入订单信息");
										Integer insertAppTrans = pmsAppTransInfoDao.insert(pmsAppTransInfo);
										
										if (insertAppTrans == 1) {
											
											logger.info("******************查询订单信息");
											pmsAppTransInfo = pmsAppTransInfoDao.searchOrderInfo(pmsAppTransInfo.getOrderid());

											String quickRateType = resultMap.get("QUICKRATETYPE").toString();//快捷支付费率类型

											logger.info("******************获取o单第三方支付的费率");
											AppRateConfig appRate = new AppRateConfig();
											appRate.setRateType(quickRateType);
											appRate.setoAgentNo(oAgentNo);
											AppRateConfig appRateConfig = appRateConfigDao.getByRateTypeAndoAgentNo(appRate);
												
											if (appRateConfig != null) {
												String isTop = appRateConfig.getIsTop();
												String rate ="0.004";
												if(QrCodeEnum.alipay.type.equals(req.getOrderCode())){
													logger.info("*******************************当前为支付宝二维宝");
													rate=weixin.getAlipayRate();
												}
												if(QrCodeEnum.weixin.type.equals(req.getOrderCode())){
													logger.info("*******************************当前为微信二维宝");
													rate=weixin.getWxRate();
												}
												
												
												String topPoundage = appRateConfig.getTopPoundage();// 封顶手续费
												paymentAmount = pmsAppTransInfo.getFactamount();// 支付金额
												String minPoundageStr = appRateConfig.getBottomPoundage();// 最低手续费
												Double minPoundage = 0.0; // 附加费
												
												if (StringUtils.isNotBlank(appRateConfig.getIsBottom())
														&& appRateConfig.getIsBottom().equals("1")) {// 是否有清算费用，"1":有，“0”无
													if (StringUtils.isNotBlank(minPoundageStr)) {
														minPoundage = Double.parseDouble(minPoundageStr); // 清算手续费
													} else {
														// 若查到的是空值，直接返回错误
														logger.info("******************没有查到相关费率附加费（最低手续费）："+ merchantinfo.getMobilephone());
														return setResp("0004", "没有查到相关费率配置（附加费），请联系客服人员");
													}
												}else{
													
													BigDecimal payAmount = null;
													BigDecimal dfactAmount = new BigDecimal(pmsAppTransInfo.getFactamount());
													// 费率
													BigDecimal fee = new BigDecimal(0);
													String rateStr = "";
													// 计算结算金额
													if ("1".equals(isTop)) {

														rateStr = rate + "-" + topPoundage;
														logger.info("******************是封顶费率类型");
														fee = new BigDecimal(rate).multiply(dfactAmount);

														if (fee.compareTo(new BigDecimal(topPoundage)) == 1) {
															logger.info("******************手续费大于封顶金额，按封顶金额处理");
															payAmount = dfactAmount.subtract(new BigDecimal(topPoundage).subtract(new BigDecimal(minPoundage)));
															fee = new BigDecimal(topPoundage).add(new BigDecimal(minPoundage));
														} else {
															logger.info("******************按当前费率处理");
															rateStr = rate;
															fee.add(new BigDecimal(minPoundage));
															payAmount = dfactAmount.subtract(fee);
														}

													} else {
														logger.info("******************按当前费率处理"); 
														rateStr = rate;
														fee = new BigDecimal(rate).multiply(dfactAmount).add(new BigDecimal(minPoundage));
														payAmount = dfactAmount.subtract(fee);
													}
													
													logger.info("******************设置结算金额");
													pmsAppTransInfo.setPayamount(payAmount.toString());// 结算金额
													pmsAppTransInfo.setRate(rate);// 0.50_35 || 0.50
													pmsAppTransInfo.setPoundage(fee.toString());
													pmsAppTransInfo.setDrawMoneyType("1");// 普通提款
													
													Integer paymentAmountInt = (int) Double.parseDouble(paymentAmount);
													
													logger.info("******************验证支付方式是否开启");
													payCheckResult =null;
													if(QrCodeEnum.alipay.type.equals(req.getOrderCode())){
														logger.info("*******************************当前为支付宝二维宝");
														payCheckResult=iPublicTradeVerifyService.totalVerify(paymentAmountInt, TradeTypeEnum.merchantCollect,PaymentCodeEnum.zhifubaoPay, oAgentNo,merchantinfo.getMercId());
													}
													if(QrCodeEnum.weixin.type.equals(req.getOrderCode())){
														logger.info("*******************************当前为微信二维宝");
														payCheckResult=iPublicTradeVerifyService.totalVerify(paymentAmountInt, TradeTypeEnum.merchantCollect,PaymentCodeEnum.weixinPay, oAgentNo,merchantinfo.getMercId());
													}
													
													if (!payCheckResult.getErrCode().equals("0")) {
														logger.info("******************不支持的支付方式，oAagentNo:"+ oAgentNo+ ",payType:"+ PaymentCodeEnum.weixinPay.getTypeCode());
														return setResp("0004", "交易不支持");
													}else{
														
														logger.info("******************查看当前交易是否已经生成了流水表");
														PospTransInfo pospTransInfo = null;
														logger.info("******************流水表是否需要更新的标记 0 insert，1：update");
														logger.info("******************生成上送流水号");
														
														String transOrderId = "";
														if(QrCodeEnum.alipay.type.equals(req.getOrderCode())){
															transOrderId=generateTransOrderId(TradeTypeEnum.merchantCollect,PaymentCodeEnum.zhifubaoPay);
														}
														if(QrCodeEnum.weixin.type.equals(req.getOrderCode())){
															transOrderId=generateTransOrderId(TradeTypeEnum.merchantCollect,PaymentCodeEnum.weixinPay);
														}
														
														
														logger.info("******************不存在流水，生成一个流水");
														pospTransInfo = generateTransFromAppTrans(pmsAppTransInfo);
														logger.info("******************设置上送流水号");
														pospTransInfo.setPospsn(transOrderId);
															
														pospTransInfoDAO.insert(pospTransInfo);
														
														String userid="";//路由获取
														
														PospRouteInfo route=super.route(merchId);
														
														PmsBusinessInfo busInfo = pmsBusinessInfoDao.searchById(route.getMerchantId().toString());
														

														pmsAppTransInfo.setBusinessNum(busInfo.getBusinessNum());
														pmsAppTransInfo.setChannelNum(busInfo.getChannelId());
														
														userid=busInfo.getBusinessNum();
														
														Map<String,String> params=new HashMap<String,String>();
														
														params.put("account", req.getAccount());
														params.put("amount", req.getTotalFee());
														params.put("userid", "21923");
														
														param2signStr(weixin,params);
											            params.put("orderCode",req.getOrderCode());
											            params.put("pay_number",pmsAppTransInfo.getOrderid());
											            params.put("notify_url",BaseUtil.url+"/cj/qrcode/callback.action");
														
														logger.info("************************************请求报文:"+params);
														String resp=HttpClientUtil.post("http://extman.kefupay.cn/tradition/WeChatpayment_mobile.action", params);
														logger.info("************************************响应报文:"+resp);
														Map result = JsonUtil.jsonToMap(resp);
														
														String respCode =result.get("respCode").toString();
														
														if("000000".equals(respCode)){
															result.put("respCode", "0000");
															pospTransInfo.setSysseqno((String)result.get("orderId"));
														}else{
															result.put("respCode", "0001");
														}
														
														logger.info("******************修改订单信息");
														logger.info(pmsAppTransInfo);
														pmsAppTransInfoDao.update(pmsAppTransInfo);
														pospTransInfoDAO.updateByOrderId(pospTransInfo);

														result.put("orderId", pmsAppTransInfo.getOrderid());
														return result;
													   
													}
													
												}
												
											}else{
												// 若查到的是空值，直接返回错误
												logger.info("******************没有查到相关费率配置："+ merchantinfo.getMobilephone());
												return setResp("0004","没有查到相关费率配置，请联系客服人员！！");
												
											}
											
										}else{
											return setResp("0004","下单失败！！");
										}
									}
									
								}else{
									return setResp("0004","没有查到相关费率配置，请联系客服人员");
								}
								
							}else{
								// 交易不支持
								logger.info("******************商户模块限制，oAagentNo:" + oAgentNo + ",payType:"+ req.getOrderCode());
								return setResp("0004",payCheckResult3.getMsg());
							}
							
						}else{
							// 交易不支持
							logger.info("******************欧单模块限制，oAagentNo:" + oAgentNo + ",payType:"+ req.getOrderCode());
							return setResp("0004",payCheckResult.getMsg());
						}
						
					}else{
						logger.info("******************欧单金额限制，oAagentNo:" + oAgentNo + ",payType:"+req.getOrderCode());
						return setResp("0004",payCheckResult.getMsg()); 
					}
			}
			
			}
		}catch(Exception e){
			logger.info("生成二维码失败");
			logger.info(e);
		}

		return setResp("0008", "商户不存在！");
		
	}

	/**
	 * 
	 * @Description 签名参数   
	 * @author Administrator
	 * @param weixin
	 * @param params
	 */
	@SuppressWarnings("static-access")
	private void param2signStr(PmsWeixinMerchartInfo weixin,
			Map<String, String> params) {
		/**参与验签的字段*/
		xdt.quickpay.hengfeng.util.MD5Utils md5Utils = new MD5Utils();
		String sign = md5Utils.getSignParam(params);
		logger.info("************************************计算签名的报文为："+sign);
		sign = md5Utils.getKeyedDigest(sign, "40ef106bf7a87e452e6a8d8b5674abcd");
		params.put("sign", sign);
	}

	/**
	 * 
	 * @Description 设置响应信息 
	 * @author Administrator
	 * @param respCode
	 * @param respInfo
	 * @return
	 */
	private Map<String, String> setResp(String respCode, String respInfo) {
		Map<String, String> result=new HashMap<String, String>();
		result.put("respCode", respCode);
		result.put("respInfo", respInfo);
		return result;
	}


	@Override
	public Map<String, String> updateQuery(QueryRequestDto req) throws Exception {
		
		logger.info("*******************************线程处理订单状态");
		logger.info("******************确认订单状态："+HF_COMMON_URL);
		
		String merchId=req.getMerchId();
		
		logger.info("******************根据商户号查询");

		PmsMerchantInfo merchantinfo=getMerchantInfo(merchId);
		
		if(merchantinfo!=null){
			
			String userid="";//路由获取
			
			PospRouteInfo route=super.route(merchId);
			
			PmsBusinessInfo busInfo = pmsBusinessInfoDao.searchById(route.getMerchantId().toString());
			
			userid=busInfo.getBusinessNum();
			
			PmsWeixinMerchartInfo weixin=weixinService.selectByPrimaryKey(req.getAccount());
			
			if(weixin==null){
				return setResp("0008", "商户不存在！");
			}
			
			
			Map<String,String> params=new HashMap<String,String>();
			
			
			params.put("userid","21923");
			params.put("account", req.getPhone());
			params.put("orderId", req.getPos_platOrderId());
			param2signStr(weixin, params);
			/**上送的值为*/
			params.put("orderCode", Common.orderConfirm);
			
			
			logger.info("******************请求报文:"+params);
			String resp=HttpClientUtil.post("http://extman.kefupay.cn/tradition/WeChatpayment_mobile.action", params);
			logger.info("******************响应报文:"+resp);
			if(resp==""||null==resp){
				return null;
			}
			Map result = JsonUtil.jsonToMap(resp);
			
			String respCode =result.get("respCode").toString();
			
			if("000000".equals(respCode)){
				
				String sysseqno=result.get("orderId").toString();
				
				logger.info("******************上游订单号:"+sysseqno+"响应码："+respCode);
				logger.info("******************流水dao:"+pospTransInfoDAO);
				PospTransInfo pospTransInfo= pospTransInfoDAO.selectBySysseqno(sysseqno);
				if(pospTransInfo!=null){
					logger.info("******************查询流水:"+pospTransInfo);
					
					PmsAppTransInfo pmsAppTransInfo=pmsAppTransInfoDao.searchOrderInfo(pospTransInfo.getOrderId());
					if(pmsAppTransInfo!=null){
						logger.info("******************查询订单:"+pmsAppTransInfo);
						if("000000".equals(respCode)){
							result.put("respCode", "0000");
							updateOrderSuccess(pmsAppTransInfo,pospTransInfo);
							
						}else{
							result.put("respCode", "0001");
							updateOrderWaitPay(pmsAppTransInfo,pospTransInfo);
						}
					}
					
					return result;
				}
			}
			
			
		}
			return null;
	}

	@Deprecated
	@Override
	public Map<String, String> updateDownkey(RegisterDto req) throws Exception {
		
		
		logger.info("******************下载私钥地址："+HF_COMMON_URL);
		
		Map result =new HashMap<String,String>();
		
		String merchId=req.getMerchId();
		
		logger.info("******************根据商户号查询");

		PmsMerchantInfo merchantinfo=getMerchantInfo(merchId);
		
		if(merchantinfo!=null){
			
			Map<String,String> params=new HashMap<String,String>();
			
			String userid="";//路由获取
			
			PospRouteInfo route=super.route(merchId);
			
			PmsBusinessInfo busInfo = pmsBusinessInfoDao.searchById(route.getMerchantId().toString());
			
			userid=busInfo.getBusinessNum();
			
			PmsWeixinMerchartInfo weixin=weixinService.selectByPrimaryKey(req.getAccount());
			
			if(weixin==null){
				return setResp("0008", "商户不存在！");
			}
			if(StringUtils.isEmpty(weixin.getPrivateKey())){
				
				params.put("account", req.getAccount());
				params.put("pass", req.getPassword());
				params.put("userid","21923");
				param2signStr(weixin, params);
				params.remove("privatekey");
				/**上送的值为*/
				params.put("orderCode", Common.downLoadKeys);
				
				logger.info("******************请求报文:"+params);
				//String resp=HttpURLConection.sendPost(HF_COMMON_URL, params);
				String resp=HttpClientUtil.post("http://extman.kefupay.cn/tradition/WeChatpayment_mobile.action", params);
				logger.info("******************响应报文:"+resp);
				 result = JsonUtil.jsonToMap(resp);
				
				String respCode =result.get("respCode").toString();
				
				if("000000".equals(respCode)){
					result.put("respCode", "0000");
					//修改本地数据
					PmsWeixinMerchartInfo entity=weixin;
					if(entity!=null){
						entity.setMerchartId(merchId);
						entity.setPrivateKey(result.get("privatekey").toString());
						weixinService.updateByPrimaryKeySelective(entity);
						logger.info(entity);
					}
					result.put("userid", merchId);
					logger.info("******************####################下载私钥信息");
					
				}else{
					result.put("respCode", "0001");
				}
			}else{
				result.put("respCode", "0000");
				//修改本地数据
				result.put("userid", merchId);
				result.put("privatekey", weixin.getPrivateKey());
				
			}
			
			return result;
		}
			return null;
			
	}


	@Override
	public Map<String, String> updateRegister(RegisterDto req) throws Exception {
		logger.info("******************注册地址："+HF_COMMON_URL);
		
		String merchId=req.getMerchId();
		
		logger.info("******************根据商户号查询");

		PmsMerchantInfo merchantinfo=getMerchantInfo(merchId);
		
		if(merchantinfo!=null){
			
			Map<String,String> params=new HashMap<String,String>();
			
			String userid="";//路由获取
			
			PospRouteInfo route=super.route(merchId);
			logger.info("******************路由:"+route);
			PmsBusinessInfo busInfo = pmsBusinessInfoDao.searchById(route.getMerchantId().toString());
			
			PmsWeixinMerchartInfo weixin=weixinService.selectByPrimaryKey(req.getAccount());
			
			if(weixin!=null){
				Map<String,String> result=setResp("100005", "账号已经注册！");
				result.put("userid", merchId);
				return result;
			}
			
			userid=busInfo.getBusinessNum();
			xdt.quickpay.hengfeng.util.MD5Utils md5Utils = new MD5Utils();
			
			params.put("account", req.getAccount());
			params.put("userid","21923");
			params.put("pass", req.getPassword());
			String sign = md5Utils.getSignParam(params);
			logger.info("计算签名的报文为："+sign);
	        //sign = md5Utils.getKeyedDigest(sign,"ebebac9be2ce02a03393df17e6b4b959");
			sign = md5Utils.getKeyedDigest(sign,"40ef106bf7a87e452e6a8d8b5674abcd");
	        params.put("orderCode", Common.regesitor);
	        params.put("sign", sign);
			
			logger.info("******************发送报文:"+params);
			 String baowen = md5Utils.getSignParam(params);
			 
			String resp=HttpClientUtil.post("http://extman.kefupay.cn/tradition/WeChatpayment_mobile.action", params);
			logger.info("******************响应报文:"+resp);
			Map result = JsonUtil.jsonToMap(resp);
			
			String respCode =result.get("respCode").toString();
			
			if("000000".equals(respCode)){
				
				result.put("respCode", "0000");
				//修改本地数据
				PmsWeixinMerchartInfo entity=new PmsWeixinMerchartInfo();
				entity.setAccount(req.getAccount());
				entity.setPassword(req.getPassword());
				logger.info("******************####################注册微信商户信息");
				logger.info(entity);
				weixinService.updateRegister(entity);
			}else{
				result.put("respCode", "0001");
			}
			result.put("userid", merchId);
			
			return result;
		}
		
		return null;
	}


	
	@Override
	public Map<String, String> updateValidator(VerifyInfoDto info) throws Exception {
		logger.info("******************校验地址："+HF_COMMON_URL);
		
		String merchId=info.getMerchId();
		
		logger.info("******************根据商户号查询");

		PmsMerchantInfo merchantinfo=getMerchantInfo(merchId);
		
		if(merchantinfo!=null){
			
			Map<String,String> params=new HashMap<String,String>();
			
			String userid="";//路由获取
			
			PospRouteInfo route=super.route(merchId);
			
			PmsBusinessInfo busInfo = pmsBusinessInfoDao.searchById(route.getMerchantId().toString());
			
			userid=busInfo.getBusinessNum();
			
			PmsWeixinMerchartInfo weixin=weixinService.selectByPrimaryKey(info.getAccount());
			
			if(weixin==null){
				return setResp("0008", "商户不存在！");
			}
			
//			params.put("account", info.getAccount());
//			params.put("real_name", info.getRealName());
//			params.put("cmer", info.getMerchartName());
//			params.put("cmer_sort", info.getMerchartNameSort());
//			params.put("phone", info.getPhone());
//			params.put("card_no", info.getCardNo());
//			params.put("cert_no", info.getCertNo());
//			params.put("mobile", info.getMobile());
//			params.put("location", info.getLocation());
//			params.put("userid",userid);
			params.put("account", info.getAccount());
			params.put("real_name", info.getRealName());
			params.put("cmer", info.getMerchartName());
			params.put("cmer_sort",info.getMerchartNameSort());
			params.put("phone", info.getPhone());
			params.put("card_no", info.getCardNo());
			params.put("cert_no", info.getCertNo());
			params.put("mobile", info.getMobile());
			params.put("location",info.getLocation());
			params.put("userid","21923");
			
			param2signStr(weixin, params);

			params.put("orderCode", Common.verify);
		
			logger.info("******************请求报文:"+params);
			//String resp=HttpURLConection.sendPost(HF_COMMON_URL, params);
			String resp=HttpClientUtil.post("http://extman.kefupay.cn/tradition/WeChatpayment_mobile.action", params);
			logger.info("******************响应报文:"+resp);
			Map result = JsonUtil.jsonToMap(resp);
			
			String respCode =result.get("respCode").toString();
			
			if("000000".equals(respCode)){
				
				result.put("respCode", "0000");
				//修改本地数据
				PmsWeixinMerchartInfo entity=weixin;
				if(entity!=null){
					entity.setCardNo(info.getCardNo());
					entity.setCardType(info.getCardType());
					entity.setRealName(info.getRealName());
					entity.setMerchartName(info.getMerchartName());
					entity.setMerchartNameSort(info.getMerchartNameSort());
					entity.setCertType(info.getCertType());
					entity.setCertNo(info.getCertNo());
					entity.setMobile(info.getMobile());
					entity.setLocation(info.getLocation());
					entity.setPhone(info.getPhone());
					logger.info("******************####################修改微信商户卡信息");
					logger.info(entity);
				}
				weixinService.updateByPrimaryKeySelective(entity);
				result.put("userid", merchId);
			}else{
				result.put("respCode", "0001");
			}
			
			return result;
		}
		
		return null;
		
	}


	@Override
	public Map<String, String> updateRate(ChangeRateDto info) throws Exception {
		logger.info("******************同步费率地址："+HF_COMMON_URL);
		
		String merchId=info.getMerchId();
		
		logger.info("******************根据商户号查询");

		PmsMerchantInfo merchantinfo=getMerchantInfo(merchId);
		
		if(merchantinfo!=null){

			Map<String,String> params=new HashMap<String,String>();
			
			String userid="";//路由获取
			
			PospRouteInfo route=super.route(merchId);
			
			PmsBusinessInfo busInfo = pmsBusinessInfoDao.searchById(route.getMerchantId().toString());
			
			userid=busInfo.getBusinessNum();
			
				
			if(StringUtils.isEmpty(info.getWxRate())||StringUtils.isEmpty(info.getAlipayRate())){
				
				return setResp("0006", "商户费率错误！！"); 
			}
				
			
			PmsWeixinMerchartInfo weixin=weixinService.selectByPrimaryKey(info.getAccount());
			
			if(weixin==null){
				return setResp("0008", "商户不存在！");
			}
			
			params.put("userid","21923");
			params.put("account", info.getAccount());
			params.put("wx_rate", info.getWxRate());
			params.put("ali_rate", info.getAlipayRate());
			params.put("pass", info.getPassword());
			param2signStr(weixin, params);
			params.put("orderCode", Common.changeRate);
			
			logger.info("******************请求报文:"+params);
			String resp=HttpClientUtil.post("http://extman.kefupay.cn/tradition/WeChatpayment_mobile.action", params);
			logger.info("******************响应报文:"+resp);
			Map result = JsonUtil.jsonToMap(resp);
			
			String respCode =result.get("respCode").toString();
			
			if("000000".equals(respCode)){
				
				result.put("respCode", "0000");
				//修改本地数据
				PmsWeixinMerchartInfo entity=weixin;
				if(entity!=null){
					entity.setWxRate(info.getWxRate());
					entity.setAlipayRate(info.getAlipayRate());
					logger.info("******************####################修改微信商户费率");
					logger.info(entity);
					weixinService.updateByPrimaryKeySelective(entity);
				}
				
			}else{
				result.put("respCode", "0001");
			}
			result.put("userid", merchId);
			return result;
		}
		return null;
	}
	
	/**
	 * 
	 * @Description 获取商户信息 
	 * @author Administrator
	 * @param merchId
	 * @return
	 * @throws Exception
	 */
	private PmsMerchantInfo getMerchantInfo(String merchId) throws Exception{
		PmsMerchantInfo merchantinfo = new PmsMerchantInfo();
		merchantinfo.setMercId(merchId);

		logger.info("******************查询当前商户信息");
		List<PmsMerchantInfo> merchantList = pmsMerchantInfoDao.searchList(merchantinfo);
		if(merchantList.isEmpty()&&merchantList.size()==0){
			return null;
		}else{
			return merchantList.get(0);
		}
		
	}


	/**
	 * 
	 * Description 回调处理订单状态 
	 * @param callback
	 * @return
	 * @throws Exception 
	 * @see xdt.service.WXQrCodeService#updateHandleOrder(xdt.dto.weixin.CallbackDto)
	 */
	@Override
	public Map<String, String> updateHandleOrder(CallbackDto callback) throws Exception {
		logger.info("*******************************回调处理订单状态");
		String sysseqno=callback.getOrderId();
		PospTransInfo transInfo=pospTransInfoDAO.selectBySysseqno(sysseqno);
		PmsAppTransInfo pmsAppTransInfo=pmsAppTransInfoDao.searchOrderInfo(transInfo.getOrderId());
		Map<String, String> params=new HashMap<String, String>();
		params.put("orderId", pmsAppTransInfo.getOrderid());
		params.put("respCode", callback.getRespCode());
		params.put("respInfo", callback.getRespInfo());
		params.put("WXOrderNo", callback.getWXOrderNo());
		params.put("amount", callback.getAmount());
		sendPost(pmsAppTransInfo.getUrl(), params);
		if("000000".equals(callback.getRespCode())){
			updateOrderSuccess(pmsAppTransInfo, transInfo);
		}else{
			updateOrderWaitPay(pmsAppTransInfo,transInfo);
		}
		//下游接入
		return setResp("", "");
	}
	
	
	/**
	 * 
	 * @Description 修改订单为等待支付  超过30分钟失败处理
	 * @author Administrator
	 * @param pmsAppTransInfo
	 * @param pospTransInfo
	 */
	private void updateOrderWaitPay(PmsAppTransInfo pmsAppTransInfo,
			PospTransInfo pospTransInfo) {
		logger.info("******************修改订单为失败");
		
		try {
			if(!UtilDate.isInDefiMinit(pmsAppTransInfo.getTradetime(), 5)){
				logger.info("******************订单在30分未完成支付  修改为失败");
				pmsAppTransInfo.setFinishtime(UtilDate.getOrderNum());
				pmsAppTransInfo.setStatus("1");
			}else{
				pmsAppTransInfo.setStatus("2");
			}
			pmsAppTransInfoDao.update(pmsAppTransInfo);
		} catch (Exception e) {
			logger.error("修改订单为失败",e);
		}		
	}
	/**
	 * 
	 * @Description 修改订单成功状态 
	 * @author Administrator
	 * @param pmsAppTransInfo
	 * @param pospTransInfo
	 */
	private void updateOrderSuccess(PmsAppTransInfo pmsAppTransInfo,
			PospTransInfo pospTransInfo) {
		pmsAppTransInfo.setFinishtime(UtilDate.getOrderNum());
		pmsAppTransInfo.setStatus("0");
		pospTransInfo.setResponsecode("0000");
		try {
			pospTransInfoDAO.update(pospTransInfo);
			pmsAppTransInfoDao.update(pmsAppTransInfo);
		} catch (Exception e) {
			logger.error("修改订单为失败",e);
		}		
	}

	@Override
	public Map<String, String> queryOrderInfo(QueryRequestDto req) throws Exception {
		Map<String, String> result = new HashMap<String, String>();

			// 查询返回结果
			PmsAppTransInfo orderinfo = pmsAppTransInfoDao.searchOrderInfo(req.getOrderNo ());
			if(orderinfo==null){
				logger.error("订单不存在!");
				result.put("respCode", "0005");
				result.put("respInfo", "订单不存在!");
			}else{
				logger.info("******************订单本地信息" + orderinfo);
				if ("0".equals(orderinfo.getStatus())) {
					logger.info("******************交易成功");
					result.put("orderId", req.getOrderNo());
					result.put("respCode", "0000");
					result.put("respInfo", "success");
				} else if ("1".equals(orderinfo.getStatus())) {
					logger.info("******************交易失败");
					result.put("orderId", req.getOrderNo());
					result.put("respCode", "0001");
					result.put("respInfo", "fail");
				}else{
					logger.info("******************扫码创建订单 等待支付");
					result.put("orderId", req.getOrderNo());
					result.put("respCode", "0002");
					result.put("respInfo", "wait");
				}
				logger.info("******************查询返回结果:" + result);
			}
			
		return result;
	}
	/**
	 * 
	 * @Description 插入原始订单表信息
	 * @author Administrator
	 * @param reqeustInfo
	 * @param orderid
	 * @param mercId
	 * @throws Exception
	 */
	private int saveOriginAlInfo(PayRequestDto req, String orderid,
			String mercId) throws Exception {
		// 插入原始信息
		OriginalOrderInfo info = new OriginalOrderInfo();
		info.setPid(mercId);
		info.setMerchantOrderId(req.getOrderNo());
		info.setOrderId(orderid);
		info.setOrderTime(UtilDate.getOrderNum());
		//info.setPayType(reqeustInfo.getPayType());
		Double amt = Double.parseDouble(req.getTotalFee());// 单位分

		amt /= 100;
		DecimalFormat df = new DecimalFormat("######0.00");

		info.setOrderAmount(df.format(amt));

		return originalDao.insert(info);
	}
	/**
	 * 
	 * @Description 异步通知给下游数据
	 * @author Administrator
	 * @param pmsAppTransInfo
	 * @param pospTransInfo
	 */
    public static synchronized String sendPost(String url,Map<String, String> params)
    {
    	System.out.println("输出————————————————————————");
        PrintWriter out = null;
        BufferedReader in = null;
        String result = "";
        try
        {
            URL realUrl = new URL(url);
            System.out.println("打开和URL之间的连接");
            System.out.println(url);
            URLConnection conn = realUrl.openConnection();
            System.out.println("设置通用的请求属性");
            conn.setRequestProperty("accept", "*/*");
            conn.setRequestProperty("user-agent", "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1;SV1)");
            System.out.println("发送POST请求必须设置如下两行");
            conn.setDoOutput(true);
            conn.setDoInput(true);
            System.out.println("获取URLConnection对象对应的输出流");
            out = new PrintWriter(conn.getOutputStream());
            out.println(gson.toJson(params));

            // flush输出流的缓冲
            out.flush();
            // 定义BufferedReader输入流来读取URL的响应
            in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String line;
            while ((line = in.readLine()) != null)
            {
                result += line;
            }
            System.err.println(result);
        }
        catch (Exception e)
        {
            System.out.println("发送 POST 请求出现异常！" + e);
            e.printStackTrace();
        }
        // 使用finally块来关闭输出流、输入流
        finally
        {
            try
            {
                if (out != null)
                {
                    out.close();
                }
                if (in != null)
                {
                    in.close();
                }
            }
            catch (IOException ex)
            {
                ex.printStackTrace();
            }
        }
        return result;
    }



}
