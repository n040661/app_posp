package xdt.service.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.commons.lang.StringUtils;

import java.math.BigDecimal;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.text.DecimalFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;

import xdt.dao.IAppRateConfigDao;
import xdt.dao.IMerchantMineDao;
import xdt.dao.IPmsAppTransInfoDao;
import xdt.dao.IPmsMerchantInfoDao;
import xdt.dao.IPospTransInfoDAO;
import xdt.dao.OriginalOrderInfoDao;

import com.google.gson.Gson;

import xdt.dao.ChannleMerchantConfigKeyDao;
import xdt.dao.IAmountLimitControlDao;
import xdt.dao.IAppRateConfigDao;
import xdt.dao.IMerchantMineDao;
import xdt.dao.IPayTypeControlDao;
import xdt.dao.IPmsAppAmountAndRateConfigDao;
import xdt.dao.IPmsAppTransInfoDao;
import xdt.dao.IPmsMerchantInfoDao;
import xdt.dao.IPospTransInfoDAO;
import xdt.dao.OriginalOrderInfoDao;
import xdt.dto.BaseUtil;
import xdt.dto.BusinessInfoRequestAndResponseDTO;
import xdt.dto.mb.MD5;
import xdt.dto.nbs.NSThread;
import xdt.dto.nbs.alipay.AlipayParamRequest;
import xdt.dto.nbs.alipay.AlipayParamResponse;
import xdt.dto.nbs.base.WechatRequestBase;
import xdt.dto.nbs.base.WechatResponseBase;
import xdt.dto.nbs.base.WechatWebPayRequestBase;
import xdt.dto.nbs.base.WechatWebPayResponseBase;
import xdt.dto.nbs.micro.WechatMicroRequest;
import xdt.dto.nbs.micro.WechatMicroResponse;
import xdt.dto.nbs.orderquery.WechatOrderQueryRequest;
import xdt.dto.nbs.orderquery.WechatOrderQueryResponse;
import xdt.dto.nbs.scan.WechatScannedRequest;
import xdt.dto.nbs.scan.WechatScannedResponse;
import xdt.dto.nbs.settle.SettleQueryWebPayRequest;
import xdt.dto.nbs.settle.SettleQueryWebPayResponse;
import xdt.dto.nbs.settle.SettleWebPayRequest;
import xdt.dto.nbs.settle.SettleWebPayResponse;
import xdt.dto.nbs.webpay.WechatWebPay;
import xdt.dto.nbs.webpay.WechatWebPayRequest;
import xdt.dto.nbs.webpay.WechatWebPayResponse;
import xdt.dto.weixin.QueryRequestDto;
import xdt.model.AppRateConfig;
import xdt.model.OriginalOrderInfo;
import xdt.model.PmsAppTransInfo;
import xdt.model.PmsBusinessInfo;
import xdt.model.PmsBusinessPos;
import xdt.model.PmsMerchantInfo;
import xdt.model.PmsWeixinMerchartInfo;
import xdt.model.PospRouteInfo;
import xdt.model.PospTransInfo;
import xdt.model.ResultInfo;
import xdt.model.ViewKyChannelInfo;
import xdt.pufa.base.Body;
import xdt.pufa.base.Head;
import xdt.pufa.base.PufaFieldDefine;
import xdt.quickpay.hengfeng.entity.PayResponseEntity;
import xdt.quickpay.hengfeng.util.HttpClientUtil;
import xdt.model.AppRateConfig;
import xdt.model.AppRateTypeAndAmount;
import xdt.model.ChannleMerchantConfigKey;
import xdt.model.OriginalOrderInfo;
import xdt.model.PmsAppTransInfo;
import xdt.model.PmsMerchantInfo;
import xdt.model.PospTransInfo;
import xdt.model.ResultInfo;
import xdt.quickpay.nbs.common.constant.Constant;
import xdt.quickpay.nbs.common.util.DateUtil;
import xdt.quickpay.nbs.common.util.HttpClientJSONUtil;
import xdt.quickpay.nbs.common.util.JSONUtil;
import xdt.quickpay.nbs.common.util.RandomUtil;
import xdt.quickpay.nbs.common.util.StringUtil;
import xdt.quickpay.qianlong.util.HttpClientHelper;
import xdt.quickpay.qianlong.util.HttpResponse;
import xdt.quickpay.qianlong.util.MyRSAUtils;
import xdt.quickpay.qianlong.util.SdkUtil;
import xdt.quickpay.qianlong.util.SignatureUtil;
import xdt.schedule.ThreadPool;
import xdt.service.IPublicTradeVerifyService;
import xdt.schedule.ThreadPool;
import xdt.service.IPublicTradeVerifyService;
import xdt.service.IWechatService;
import xdt.servlet.AppPospContext;
import xdt.util.BeanToMapUtil;
import xdt.util.BnsThread;
import xdt.util.Coder;
import xdt.util.HengFengThread;
import xdt.util.JsonUtil;
import xdt.util.MD5Util;
import xdt.util.OrderStatusEnum;
import xdt.util.PaymentCodeEnum;
import xdt.util.QrCodeEnum;
import xdt.util.TradeTypeEnum;
import xdt.util.UtilDate;
import xdt.util.UtilMethod;
import xdt.util.utils.MD5Utils;
import xdt.util.Constants;
import xdt.util.PaymentCodeEnum;
import xdt.util.TradeTypeEnum;
import xdt.util.UtilDate;

/**
 * 微信业务实现
 *
 * @author liming.Gao
 * @version nongshang
 * @date 2017
 */
@Service
public class WechatServiceImpl extends BaseServiceImpl implements IWechatService {
	private Logger log = LoggerFactory.getLogger(WechatServiceImpl.class);

	private static Gson gson = new Gson();
	@Resource
	private IMerchantMineDao merchantMineDao;

	// 商户信息服务层
	@Resource
	private IPmsMerchantInfoDao pmsMerchantInfoDao;

	// 原始数据
	@Resource
	private OriginalOrderInfoDao originalDao;

	@Resource
	private IPmsAppAmountAndRateConfigDao pmsAppAmountAndRateConfigDao;// 商户费率配置

	@Resource
	private IAppRateConfigDao appRateConfigDao;// 费率

	@Resource
	public IPmsAppTransInfoDao pmsAppTransInfoDao; // 业务配置服务层

	@Resource
	private IPospTransInfoDAO pospTransInfoDAO;// 流水

	@Resource
	private IPublicTradeVerifyService publicTradeVerifyService;

	@Resource
	private IPayTypeControlDao payTypeControlDao;// 开关
	@Resource
	private IPublicTradeVerifyService iPublicTradeVerifyService;
	@Resource
	private IAmountLimitControlDao amountLimitControlDao;// 最大值最小值总开关判断

	/**
	 * 渠道商户密钥信息
	 */
	@Resource
	private ChannleMerchantConfigKeyDao cmckeyDao;
	/**
	 * 分发请求
	 * 
	 * @param reqData
	 * @return
	 * @throws Exception
	 */
	public Map<String, Object> updateHandle(AlipayParamRequest alipayParam) throws Exception {
		// TODO Auto-generated method stub
		Map<String, Object> result = new HashMap<String, Object>();

		log.info("************************北农商支付----二维码----处理转发 开始");

		// 查询上游商户号
		PmsBusinessPos busInfo = selectKey(alipayParam.getMerchantId());
		try {

			switch (alipayParam.getService_type() == null ? alipayParam.getService_type() : alipayParam.getService_type()) {
			case "1":
			case "2":
			case "3":
				log.info("************************北农商----支付宝、刷卡、服务窗----处理 开始");
				result = (Map<String, Object>) this.alipayParam(alipayParam, result, busInfo);
				break;
			case "4":
				log.info("************************江苏电商----支付宝退款----处理 开始");
				result = this.alipayRefund(alipayParam, result, busInfo);
				break;
			case "5":
				log.info("************************江苏电商----支付宝撤销----处理 开始");
				result = this.alipayReverseorder(alipayParam, result, busInfo);
				break;
			case "6":
				log.info("************************北农商----支付宝查询----处理 开始");
				result = this.alipayScanSelect(alipayParam, result, busInfo);
				break;
			default:
				break;
				
			}

		} catch (Exception e) {
			log.info("************************江苏电商----二维码----处理转发 失败", e);
			throw new RuntimeException("系统错误");
		}

		log.info("************************江苏电商----二维码----处理转发 结果:{}" + result);

		return result;
	}

	/**
	 * 扫码支付
	 * 
	 * Map request;
	 * 
	 * @return
	 */
	public Map<String, Object> createMerpay(WechatMicroRequest microRequest, Logger log) {

		// 假设路由是上面选择的
		// 响应
		Map<String, Object> response = new HashMap<String, Object>();
		// 请求
		Map<String, Object> request = new HashMap<String, Object>();

		// ------------------------------------------------------------->>>>
		// 查找路由
		PospRouteInfo route = route(microRequest.getMerchantId());
		PmsBusinessInfo busInfo = new PmsBusinessInfo();
		request.put("route", route);

		// 根据路由查询通道商户
		try {
			busInfo = pmsBusinessInfoDao.searchById(route.getMerchantId().toString());
			request.put("busInfo", busInfo);
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		// -------------------------------------------------------------<<<<

		PospTransInfo transInfo = new PospTransInfo();

		log.info("----扫码支付----构建浦发交易报文 start");

		// ---------------------------------------------------------->>>>
		// 获取路由表当前绑定可用路由数据
		// *步骤1 设置BODY --start*
		Body body = new Body();
		// body.setOrder_id((String)request.get(PufaFieldDefine.PF_REQ_BODY_ORDER_ID));//前端订单号
		body.setMchnt_cd(busInfo.getBusinessNum());// 商户号
		body.setTran_amt(microRequest.getTotal_fee());// 交易金额-分为单位
		// *步骤1 设置BODY --end*

		// *步骤2 设置HEAD --start*
		Head head = new Head();
		head.setTran_dt_tm((String) request.get(PufaFieldDefine.PF_HEAD_TRAN_DT_TM));// 日期时间

		// ----------------------------------------------------------<<<<

		// *步骤3 保存本次流水 *
		// 插入流水
		// transInfo = InsertJournal(request, microRequest);
		// *步骤5 发送请求报文 XML *
		// transInfo = getJourByUniqueKey(transInfo.getUniqueKey());

		// *步骤6 解析响应报文 XML *

		// *步骤7 更新表状态，向前传递 *
		// transInfo.setResponsecode(resCode);
		// transInfo.setSysseqno((String)
		// response.get(PufaFieldDefine.PF_REQ_BODY_SYS_ORDER_ID));
		// transInfo.setRemark((String)
		// response.get(PufaFieldDefine.PF_REQ_BODY_RET_MSG));
		// try {
		// transInfoDao.update(transInfo);
		// } catch (Exception e) {
		// logger.info("---更新流水错误---- ");
		// req.put(PufaFieldDefine.PF_REQ_BODY_RET_CD, "99");
		/// e.printStackTrace();
		// return req;
		// }

		log.info("----扫码支付----构建浦发交易报文 end");
		return response;
	}

	@Override
	public WechatScannedResponse doScanned(WechatScannedRequest scannedRequest, Logger log) {
		// step 1 init param
		WechatScannedResponse scannedResponse = new WechatScannedResponse();
		log.info("请求参数:{}", scannedRequest);
		String brcbGatewayUrl = Constant.BRCB_GATEWAY_URL;
		log.info("请求地址: {}", brcbGatewayUrl);

		// step 2 send json post
		String sendPost = sendJsonPost(brcbGatewayUrl, scannedRequest, scannedResponse, log);
		if (StringUtil.isBlank(sendPost)) {
			return scannedResponse;
		}

		// step 3 covert response
		try {
			scannedResponse = JSONUtil.parseObject(sendPost, WechatScannedResponse.class);
		} catch (Exception e) {
			log.info("转换返回数据异常,信息: {}", e);
			scannedResponse.setReturn_code(Constant.FAIL);
			scannedResponse.setReturn_msg("返回参数转换异常");
			return scannedResponse;
		}
		log.info("强转的对象为: {}", scannedResponse);

		// step 4 return
		return scannedResponse;
	}

	public SettleWebPayResponse doScanned(SettleWebPayRequest settle, Logger log) {
		// step 1 init param
		SettleWebPayResponse scannedResponse = new SettleWebPayResponse();
		log.info("请求参数:{}", settle);
		String brcbGatewayUrl = "http://brcb.pufubao.net/T0Settle";
		log.info("请求地址: {}", brcbGatewayUrl);

		// step 2 send json post
		String sendPost = sendJsonPost(brcbGatewayUrl, settle, scannedResponse, log);
		if (StringUtil.isBlank(sendPost)) {
			return scannedResponse;
		}

		// step 3 covert response
		try {
			scannedResponse = JSONUtil.parseObject(sendPost, SettleWebPayResponse.class);
		} catch (Exception e) {
			log.info("转换返回数据异常,信息: {}", e);
			scannedResponse.setReturn_code(Constant.FAIL);
			scannedResponse.setReturn_msg("返回参数转换异常");
			return scannedResponse;
		}
		log.info("强转的对象为: {}", scannedResponse);

		// step 4 return
		return scannedResponse;
	}

	public SettleQueryWebPayResponse doScanned(SettleQueryWebPayRequest settle, Logger log) {
		// step 1 init param
		SettleQueryWebPayResponse scannedResponse = new SettleQueryWebPayResponse();
		log.info("请求参数:{}", settle);
		String brcbGatewayUrl = "http://brcb.pufubao.net/T0SettleQuery";
		log.info("请求地址: {}", brcbGatewayUrl);

		// step 2 send json post
		String sendPost = sendJsonPost(brcbGatewayUrl, settle, scannedResponse, log);
		if (StringUtil.isBlank(sendPost)) {
			return scannedResponse;
		}

		// step 3 covert response
		try {
			scannedResponse = JSONUtil.parseObject(sendPost, SettleQueryWebPayResponse.class);
		} catch (Exception e) {
			log.info("转换返回数据异常,信息: {}", e);
			scannedResponse.setReturn_code(Constant.FAIL);
			scannedResponse.setReturn_msg("返回参数转换异常");
			return scannedResponse;
		}
		log.info("强转的对象为: {}", scannedResponse);

		// step 4 return
		return scannedResponse;
	}

	@Override
	public WechatMicroResponse doMicro(WechatMicroRequest microRequest, Logger log) {
		// step 1 init param
		WechatMicroResponse microResponse = new WechatMicroResponse();
		log.info("请求参数:{}", microRequest);
		String brcbGatewayUrl = Constant.BRCB_GATEWAY_URL;
		log.info("请求地址: {}", brcbGatewayUrl);

		long costTimeStart = System.currentTimeMillis();// start
		// step 2 send json post
		String sendPost = sendJsonPost(brcbGatewayUrl, microRequest, microResponse, log);
		if (StringUtil.isBlank(sendPost)) {
			return microResponse;
		}

		// step 3 covert response
		try {
			microResponse = JSONUtil.parseObject(sendPost, WechatMicroResponse.class);
		} catch (Exception e) {
			log.info("转换返回数据异常,信息: {}", e);
			microResponse.setReturn_code(Constant.FAIL);
			microResponse.setReturn_msg("返回参数转换异常");
			return microResponse;
		}
		log.info("强转的对象为: {}", microResponse);

		// step 4 return
		return microResponse;
	}

	/**
	 * 支付宝支付实现
	 *
	 * @author liming.Gao
	 * @version nongshang
	 * @date 2017
	 */
	public AlipayParamResponse alipayScan(AlipayParamRequest microRequest, Logger log) {
		// step 1 init param
		AlipayParamResponse microResponse = new AlipayParamResponse();
		log.info("请求参数:{}", microRequest);
		String brcbGatewayUrl = Constant.BRCB_GATEWAY_URL;
		log.info("请求地址: {}", brcbGatewayUrl);

		long costTimeStart = System.currentTimeMillis();// start
		// step 2 send json post
		String sendPost = sendJsonPost(brcbGatewayUrl, microRequest, microResponse, log);
		if (StringUtil.isBlank(sendPost)) {
			return microResponse;
		}

		// step 3 covert response
		try {
			microResponse = JSONUtil.parseObject(sendPost, AlipayParamResponse.class);
		} catch (Exception e) {
			log.info("转换返回数据异常,信息: {}", e);
			microResponse.setReturn_code(Constant.FAIL);
			microResponse.setReturn_msg("返回参数转换异常");
			return microResponse;
		}
		log.info("强转的对象为: {}", microResponse);

		// step 4 return
		return microResponse;
	}

	@Override
	public WechatOrderQueryResponse doOrderQuery(WechatOrderQueryRequest orderQueryRequest, Logger log) {
		// step 1 init param
		WechatOrderQueryResponse orderQueryResponse = new WechatOrderQueryResponse();
		log.info("请求参数:{}", orderQueryRequest);

		String brcbGatewayUrl = Constant.BRCB_GATEWAY_URL;
		log.info("请求地址: {}", brcbGatewayUrl);

		// step 2 send json post
		String sendPost = sendJsonPost(brcbGatewayUrl, orderQueryRequest, orderQueryResponse, log);
		if (StringUtil.isBlank(sendPost)) {
			return orderQueryResponse;
		}

		// step 3 covert response
		try {
			orderQueryResponse = JSONUtil.parseObject(sendPost, WechatOrderQueryResponse.class);
		} catch (Exception e) {
			log.info("转换返回数据异常,信息: {}", e);
			orderQueryResponse.setReturn_code(Constant.FAIL);
			orderQueryResponse.setReturn_msg("返回参数转换异常");
			return orderQueryResponse;
		}
		log.info("强转的对象为: {}", orderQueryResponse);

		// step 4 return
		return orderQueryResponse;
	}

	/**
	 * send json post
	 *
	 * @param url
	 * @param requestBase
	 * @param responseBase
	 * @param log
	 * @return
	 */
	String sendJsonPost(String url, WechatRequestBase requestBase, WechatResponseBase responseBase, Logger log) {
		String sendPost = null;
		long costTimeStart = System.currentTimeMillis();// start
		try {
			sendPost = HttpClientJSONUtil.postJSONUTF8(url, JSONUtil.toJSONString(requestBase.toMap()));
		} catch (Exception e) {
			log.info("请求出错: {}", e);
			responseBase.setReturn_code(Constant.FAIL);
			responseBase.setReturn_msg("请求出错");
		}
		long costTimeEnd = System.currentTimeMillis();// end
		long totalTimeCost = costTimeEnd - costTimeStart;// 总耗时
		log.info("请求总耗时：{}ms", totalTimeCost);
		log.info("返回数据: {}", sendPost);
		return sendPost;
	}

	/**
	 * 公共号支付
	 *
	 * @param url
	 * @param requestBase
	 * @param responseBase
	 * @return
	 * @throws Exception
	 */
	public Map<String, String> pay(WechatWebPay wechat, Logger log) throws Exception {

		// log.info("******************生成二维码："+HF_WX_WeixinPayURL);

		// 商户号
		String merchId = wechat.getMerchantId();
		// 金额
		String acount = wechat.getTotal_fee();
		// 商户订单号
		log.info("******************根据商户号查询");

		PmsMerchantInfo merchantinfo = getMerchantInfo(merchId, log);

		log.info("******************商户信息:" + merchantinfo);

		if (merchantinfo != null) {

			String orderNumber = wechat.getOut_trade_no();

			log.info("******************orderNumber:" + orderNumber);
			String oAgentNo = merchantinfo.getoAgentNo();
			OriginalOrderInfo oriInfo = new OriginalOrderInfo();
			oriInfo.setMerchantOrderId(wechat.getOut_trade_no());
			oriInfo.setPid(wechat.getMerchantId());
			oriInfo = originalDao.selectByOriginal(oriInfo);
			if (oriInfo != null) {
				log.error("下单重复");
				setResp("16", "下单重复");
			} else if ("60".equals(merchantinfo.getMercSts())) {
				// 判断是否为正式商户
				OriginalOrderInfo original = new OriginalOrderInfo();
				original.setMerchantOrderId(wechat.getOut_trade_no());// 原始数据的订单编号
				original.setOrderId(wechat.getOut_trade_no()); // 为主键
				original.setBgUrl(wechat.getNotify_url());
				if (wechat.getCallback_url() != null) {
					original.setPageUrl(wechat.getCallback_url());
				}
				originalDao.insert(original);
				log.info("******************实际金额");
				String factAmount = acount;
				log.info("******************校验欧单金额限制");
				ResultInfo payCheckResult = iPublicTradeVerifyService.amountVerifyOagent(
						(int) Double.parseDouble(factAmount), TradeTypeEnum.merchantCollect, oAgentNo);

				if (payCheckResult.getErrCode().equals("0")) {

					log.info("******************校验欧单模块是否开启");
					ResultInfo payCheckResult1 = iPublicTradeVerifyService
							.moduleVerifyOagent(TradeTypeEnum.merchantCollect, oAgentNo);

					if (payCheckResult1.getErrCode().equals("0")) {
						log.info("******************校验商户模块是否开启");
						ResultInfo payCheckResult3 = iPublicTradeVerifyService
								.moduelVerifyMer(TradeTypeEnum.merchantCollect, merchId);
						if (payCheckResult3.getErrCode().equals("0")) {

							log.info("******************校验商户金额限制");
							Map<String, String> paramMap = new HashMap<String, String>();
							paramMap.put("mercid", merchId);
							paramMap.put("businesscode", TradeTypeEnum.merchantCollect.getTypeCode());// 业务编号
							paramMap.put("oAgentNo", oAgentNo);
							//
							log.info("******************商户 商城 业务信息 ");
							Map<String, String> resultMap = merchantMineDao.queryBusinessInfo(paramMap);

							if (!(resultMap == null || resultMap.size() == 0)) {

								String maxTransMoney = resultMap.get("MAX_AMOUNT"); // 每笔最大交易金额
								String minTransMoney = resultMap.get("MIN_AMOUNT"); // 每笔最小交易金额
								String paymentAmount = factAmount;// 交易金额

								if (new BigDecimal(paymentAmount).compareTo(new BigDecimal(maxTransMoney)) == 1) {
									// 金额超过最大金额
									log.info("******************交易金额大于最打金额");
									return setResp("0004", "交易金额大于最打金额");
								} else if (new BigDecimal(paymentAmount)
										.compareTo(new BigDecimal(minTransMoney)) == -1) {
									log.info("******************交易金额小于最小金额");
									return setResp("0004", "金额小于最小交易金额");
								} else {

									//
									log.info("******************组装订单数据");
									PmsAppTransInfo pmsAppTransInfo = new PmsAppTransInfo();
									log.info("******************写入欧单编号");

									pmsAppTransInfo.setoAgentNo(oAgentNo);
									pmsAppTransInfo.setStatus(OrderStatusEnum.initlize.getStatus());// 订单初始化状态
									pmsAppTransInfo.setTradetype(TradeTypeEnum.merchantCollect.getTypeName());// 业务功能模块名称
									// log.info(req.getOrderCode());
									log.info("******************商户收款");
									log.info(UtilDate.getDateFormatter());
									pmsAppTransInfo.setTradetime(UtilDate.getDateFormatter()); // 设置时间
									pmsAppTransInfo.setMercid(merchantinfo.getMercId());
									pmsAppTransInfo.setTradetypecode(TradeTypeEnum.merchantCollect.getTypeCode());// 业务功能模块编号
									pmsAppTransInfo.setOrderid(orderNumber);// 设置订单号
									pmsAppTransInfo.setPaymenttype(PaymentCodeEnum.PNCodePay.getTypeName());
									pmsAppTransInfo.setPaymentcode(PaymentCodeEnum.PNCodePay.getTypeCode());
									BigDecimal factBigDecimal = new BigDecimal(factAmount);
									BigDecimal orderAmountBigDecimal = new BigDecimal(factAmount);

									pmsAppTransInfo.setFactamount(factBigDecimal.stripTrailingZeros().toPlainString());// 实际金额
									pmsAppTransInfo
											.setOrderamount(orderAmountBigDecimal.stripTrailingZeros().toPlainString());// 订单金额
									pmsAppTransInfo.setDrawMoneyType("1");// 普通提款

									log.info("******************插入订单信息");
									Integer insertAppTrans = pmsAppTransInfoDao.insert(pmsAppTransInfo);

									if (insertAppTrans == 1) {

										log.info("******************查询订单信息");
										pmsAppTransInfo = pmsAppTransInfoDao
												.searchOrderInfo(pmsAppTransInfo.getOrderid());

										String quickRateType = resultMap.get("QUICKRATETYPE").toString();// 快捷支付费率类型

										log.info("******************获取o单第三方支付的费率");
										AppRateConfig appRate = new AppRateConfig();
										appRate.setRateType(quickRateType);
										appRate.setoAgentNo(oAgentNo);
										AppRateConfig appRateConfig = appRateConfigDao
												.getByRateTypeAndoAgentNo(appRate);

										if (appRateConfig != null) {
											String isTop = appRateConfig.getIsTop();
											String rate = appRateConfig.getRate();
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
													log.info("******************没有查到相关费率附加费（最低手续费）："
															+ merchantinfo.getMobilephone());
													return setResp("0004", "没有查到相关费率配置（附加费），请联系客服人员");
												}
											} else {

												BigDecimal payAmount = null;
												BigDecimal dfactAmount = new BigDecimal(
														pmsAppTransInfo.getFactamount());
												// 费率
												BigDecimal fee = new BigDecimal(0);
												BigDecimal b = new BigDecimal(0);
												String rateStr = "";
												// 计算结算金额
												if ("1".equals(isTop)) {

													rateStr = rate + "-" + topPoundage;
													log.info("******************是封顶费率类型");
													fee = new BigDecimal(rate).multiply(dfactAmount);

													if (fee.compareTo(new BigDecimal(topPoundage)) == 1) {
														log.info("******************手续费大于封顶金额，按封顶金额处理");
														payAmount = dfactAmount.subtract(new BigDecimal(topPoundage)
																.subtract(new BigDecimal(minPoundage)));
														fee = new BigDecimal(topPoundage)
																.add(new BigDecimal(minPoundage));
													} else {
														log.info("******************按当前费率处理");
														rateStr = rate;
														fee.add(new BigDecimal(minPoundage));
														payAmount = dfactAmount.subtract(fee);
													}

												} else {
													log.info("******************按当前费率处理");
													rateStr = rate;
													fee = new BigDecimal(rate).multiply(dfactAmount)
															.add(new BigDecimal(minPoundage));
													double fee1 = fee.doubleValue();
													if (merchantinfo.getCounter() != null) {
														double num = Double.parseDouble(merchantinfo.getCounter())
																* 100;
														if (fee1 < num) {
															b = new BigDecimal(Double.toString(num));
															payAmount = dfactAmount.subtract(b);
														} else {
															b = fee;
															payAmount = dfactAmount.subtract(b);
														}
													}
												}

												log.info("******************设置结算金额");
												pmsAppTransInfo.setPayamount(payAmount.toString());// 结算金额
												// pmsAppTransInfo.setRate(weixin.getRate());//
												// 0.50_35 || 0.50
												pmsAppTransInfo.setPoundage(b.toString());
												pmsAppTransInfo.setDrawMoneyType("1");// 普通提款

												Integer paymentAmountInt = (int) Double.parseDouble(paymentAmount);

												log.info("******************验证支付方式是否开启*********");
												log.info("***********当前为公众号支付**********");
												payCheckResult = iPublicTradeVerifyService.totalVerify(paymentAmountInt,
														TradeTypeEnum.merchantCollect, PaymentCodeEnum.PNCodePay,
														oAgentNo, merchantinfo.getMercId());
												if (!payCheckResult.getErrCode().equals("0")) {
													log.info("******************不支持的支付方式，oAagentNo:" + oAgentNo
															+ ",payType:" + PaymentCodeEnum.PNCodePay.getTypeCode());
													return setResp("0004", "交易不支持");
												} else {
													String userid = "";// 路由获取

													PospRouteInfo route = super.route(merchId);

													PmsBusinessInfo busInfo = pmsBusinessInfoDao
															.searchById(route.getMerchantId().toString());

													// 设置通道信息
													pmsAppTransInfo.setBusinessNum(busInfo.getBusinessNum());
													pmsAppTransInfo.setChannelNum(busInfo.getChannelId());

													userid = busInfo.getBusinessNum();

													// 查看当前交易是否已经生成了流水表
													PospTransInfo pospTransInfo = null;
													// 流水表是否需要更新的标记 0
													// insert，1：update
													int insertOrUpdateFlag = 0;
													// 生成上送流水号
													// String transOrderId =
													// generateTransOrderId(TradeTypeEnum.onlinePay,
													// PaymentCodeEnum.PNCodePay);
													if ((pospTransInfo = pospTransInfoDAO
															.searchByOrderId(pmsAppTransInfo.getOrderid())) != null) {
														// 已经存在，修改流水号，设置pospsn为空
														// log.info("订单号：" +
														// pmsAppTransInfo.getOrderid()
														// + ",生成上送通道的流水号：" +
														// transOrderId);
														pospTransInfo.setTransOrderId(wechat.getOut_trade_no());
														pospTransInfo.setResponsecode("99");
														pospTransInfo.setPospsn("");
														insertOrUpdateFlag = 1;
													} else {
														// 不存在流水，生成一个流水
														pospTransInfo = generateTransFromAppTrans(pmsAppTransInfo);
														// 设置上送流水号
														pospTransInfo.setTransOrderId(wechat.getOut_trade_no());
														insertOrUpdateFlag = 0;
													}
													// 插入流水表信息
													if (insertOrUpdateFlag == 0) {
														// 插入一条流水
														pospTransInfoDAO.insert(pospTransInfo);
													} else if (insertOrUpdateFlag == 1) {
														// 更新一条流水
														pospTransInfoDAO.updateByOrderId(pospTransInfo);
													}
													log.info("修改订单信息");
													pmsAppTransInfoDao.update(pmsAppTransInfo);
													Map<String, String> result = new HashMap<String, String>();
													result.put("brcb_gateway_url", Constant.BRCB_GATEWAY_URL);
													return result;
												}

											}

										} else {
											// 若查到的是空值，直接返回错误
											log.info("******************没有查到相关费率配置：" + merchantinfo.getMobilephone());
											return setResp("0004", "没有查到相关费率配置，请联系客服人员！！");

										}

									} else {
										return setResp("0004", "下单失败！！");
									}
								}

							} else {
								return setResp("0004", "没有查到相关费率配置，请联系客服人员");
							}

						} else {
							// 交易不支持
							log.info("******************商户模块限制，oAagentNo:" + oAgentNo + ",payType:" + "公众号支付");
							return setResp("0004", payCheckResult3.getMsg());
						}

					} else {
						// 交易不支持
						log.info("******************欧单模块限制，oAagentNo:" + oAgentNo + ",payType:" + "公众号支付");
						return setResp("0004", payCheckResult.getMsg());
					}

				} else {
					log.info("******************欧单金额限制，oAagentNo:" + oAgentNo + ",payType:" + "公众号支付");
					return setResp("0004", payCheckResult.getMsg());
				}
			} else {
				log.error("不是正式商户!");
				setResp("03", "不是正式商户");
			}
		}

		return setResp("0008", "商户不存在！");

	}

	/**
	 * 
	 * @Description 获取商户信息
	 * @author Administrator
	 * @param merchId
	 * @return
	 * @throws Exception
	 */
	private PmsMerchantInfo getMerchantInfo(String merchId, Logger log) throws Exception {
		PmsMerchantInfo merchantinfo = new PmsMerchantInfo();
		merchantinfo.setMercId(merchId);

		log.info("******************查询当前商户信息");
		List<PmsMerchantInfo> merchantList = pmsMerchantInfoDao.searchList(merchantinfo);
		if (merchantList.isEmpty() && merchantList.size() == 0) {
			return null;
		} else {
			return merchantList.get(0);
		}

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
		Map<String, String> result = new HashMap<String, String>();
		result.put("respCode", respCode);
		result.put("respInfo", respInfo);
		return result;
	}

	public WechatScannedResponse updateTwoDimensionCode(WechatScannedRequest wechatScannedRequest) {
		Map<String, Object> result = new HashMap<String, Object>();
		WechatScannedResponse scannedResponse = null;
		log.info("处理二维码生成");

		log.info("根据商户号查询");

		String out_trade_no = "";// 订单号
		out_trade_no = wechatScannedRequest.getOut_trade_no(); // 10业务号2业务细; 订单号
																// 现根据规则生成订单号
		log.info("根据商户号查询");
		String mercId = wechatScannedRequest.getMerchantId();

		// o单编号
		String oAgentNo = "";

		PmsMerchantInfo merchantinfo = new PmsMerchantInfo();
		merchantinfo.setMercId(mercId);

		// 查询当前商户信息
		try {
			List<PmsMerchantInfo> merchantList = pmsMerchantInfoDao.searchList(merchantinfo);

			if (!(merchantList.size() == 0 || merchantList.isEmpty())) {

				// 正式商户
				merchantinfo = merchantList.get(0);

				oAgentNo = merchantinfo.getoAgentNo();//

				OriginalOrderInfo oriInfo = new OriginalOrderInfo();
				oriInfo.setMerchantOrderId(wechatScannedRequest.getProduct_id());
				oriInfo.setPid(wechatScannedRequest.getMerchantId());

				oriInfo = originalDao.selectByOriginal(oriInfo);

				if (oriInfo != null) {
					log.error("下单重复");
					result.put("respCode", "16");
					result.put("respMsg", "下单重复");
				} else if ("60".equals(merchantinfo.getMercSts())) {
					// 判断是否为正式商户

					saveOriginAlInfo(wechatScannedRequest, out_trade_no, mercId);
					// 校验商户金额限制
					Map<String, String> paramMap = new HashMap<String, String>();
					paramMap.put("mercid", merchantinfo.getMercId());// 商户编号
					paramMap.put("businesscode", TradeTypeEnum.merchantCollect.getTypeCode());// 业务编号
					paramMap.put("oAgentNo", oAgentNo);
					// 商户 网购 业务信息
					Map<String, String> resultMap = merchantMineDao.queryBusinessInfo(paramMap);

					String quickRateType = resultMap.get("QUICKRATETYPE");// 快捷支付费率类型

					// 获取o单第三方支付的费率
					AppRateConfig appRate = new AppRateConfig();
					appRate.setRateType(quickRateType);
					appRate.setoAgentNo(oAgentNo);
					AppRateConfig appRateConfig = appRateConfigDao.getByRateTypeAndoAgentNo(appRate);

					paramMap.put("mercid", mercId);
					paramMap.put("businesscode", TradeTypeEnum.merchantCollect.getTypeCode());
					// 微信支付
					paramMap.put("paymentcode", PaymentCodeEnum.weixinPay.getTypeCode());

					// 查询商户费率 和 最 低收款金额 支付方式是否开通 业务是否开通 等参数
					AppRateTypeAndAmount appRateTypeAndAmount = pmsAppAmountAndRateConfigDao
							.queryAmountAndStatus(paramMap);
					if (appRateTypeAndAmount != null) {

						String status = appRateTypeAndAmount.getStatus();// 此业务是否开通

						// String statusMessage =
						// appRateTypeAndAmount.getMessage();//此业务是否开通的描述

						String payStatus = appRateTypeAndAmount.getPayStatus();// 此支付方式是否开通

						// 判断此业务O单是否开通（总）
						ResultInfo resultInfoForOAgentNo = publicTradeVerifyService
								.moduleVerifyOagent(TradeTypeEnum.merchantCollect, oAgentNo);

						if (!resultInfoForOAgentNo.getErrCode().equals("0")) {
							if (StringUtils.isEmpty(resultInfoForOAgentNo.getMsg())) {
								log.error("此功能暂时关闭!");
								result.put("respCode", "05");
								result.put("respMsg", "此功能暂时关闭");
							} else {
								log.error(resultInfoForOAgentNo.getMsg());
								result.put("respCode", "05");
								result.put("respMsg", resultInfoForOAgentNo.getMsg());
							}
						} else {

							if ("1".equals(status)) {

								// 判断支付方式时候开通总开关
								ResultInfo payCheckResult = null;

								payCheckResult = payTypeControlDao.checkLimit(oAgentNo,
										PaymentCodeEnum.weixinPay.getTypeCode());

								if (!payCheckResult.getErrCode().equals("0")) {
									// 支付方式时候开通总开关 禁用
									result.put("respCode", "07");
									result.put("respMsg", "此支付方式暂时关闭");
									log.info("此支付方式暂时关闭");
								} else {

									BigDecimal payAmt = new BigDecimal(wechatScannedRequest.getTotal_fee());// 收款金额
									// 判读 交易金额是不是在欧单区间控制之内
									ResultInfo resultInfo = amountLimitControlDao.checkLimit(oAgentNo, payAmt,
											TradeTypeEnum.merchantCollect.getTypeCode());
									// 返回不为0，一律按照交易失败处理
									if (!resultInfo.getErrCode().equals("0")) {
										result.put("respCode", "08");
										result.put("respMsg", "交易金额不在申请的范围之内");
										log.info("交易金额不在申请的范围之内");

									} else {

										// 商户渠道支付方式
										// 商户渠道交易类型
										// 验证支付方式是否开启

										ResultInfo resultinfo = null;
										resultinfo = iPublicTradeVerifyService
												.payTypeVerifyMer(PaymentCodeEnum.weixinPay, mercId);
										if ("0".equals(resultinfo.getErrCode())) {
											if ("0".equals(payStatus)) {
												// 有效
												// MIN_AMOUNT,MAX_AMOUNT ,RATE
												// ,STATUS
												String rateStr = appRateConfig.getRate(); // 商户费率
																							// RATE

												BigDecimal min_amount = new BigDecimal(
														appRateTypeAndAmount.getMinAmount());// 最低收款金额
												// MIN_AMOUNT
												BigDecimal max_amount = new BigDecimal(
														appRateTypeAndAmount.getMaxAmount());// 最高收款金额
												// MAX_AMOUNT

												if (min_amount.compareTo(payAmt) != 1) {// 判断收款金额是否大于最低收款金额
													// 大于等于执行
													// 小于不执行
													if (payAmt.compareTo(max_amount) != 1) {
														// 组装报文
														String totalAmount = wechatScannedRequest.getTotal_fee(); // 交易金额

														PmsAppTransInfo appTransInfo = this.insertOrder(out_trade_no,
																totalAmount, mercId, rateStr, oAgentNo);

														if (appTransInfo != null) {
															// 处理生成二维码
															scannedResponse = this.twoDimensionCodeProcess(
																	wechatScannedRequest, result, appTransInfo);
														} else {
															// 交易金额小于收款最低金额
															result.put("respCode", "11");
															result.put("respMsg", "生成订单流水失败");
															log.info("生成订单流水失败");
														}

													} else {

														// 交易金额小于收款最低金额
														result.put("respCode", "10");
														result.put("respMsg", "交易金额大于收款最高金额");
														log.info("交易金额大于收款最高金额");
													}

												} else {
													// 交易金额小于收款最低金额
													result.put("respCode", "09");
													result.put("respMsg", "交易金额小于收款最低金额");
													log.info("交易金额小于收款最低金额");
												}

											} else {
												// 交易金额小于收款最低金额
												result.put("respCode", "12");
												result.put("respMsg", "商户收款关闭");
												log.info("商户交易关闭");
											}
										} else {
											// 交易金额小于收款最低金额
											result.put("respCode", "13");
											result.put("respMsg", "扫码支付关闭");
											log.info("扫码支付关闭");
										}

									}
								}

							} else {
								log.error("此功能暂未开通");
								result.put("respCode", "06");
								result.put("respMsg", "此功能暂未开通");

							}

						}

					} else {
						log.error("没有找到商户费率");
						result.put("respCode", "04");
						result.put("respMsg", "没有找到商户费率");
					}

				} else {
					log.error("不是正式商户!");
					result.put("respCode", "03");
					result.put("respMsg", "不是正式商户");
				}

			} else {
				log.error("商户不存在!");
				result.put("respCode", "02");
				result.put("respMsg", "商户不存在");
			}

		} catch (Exception e) {
			e.printStackTrace();
			log.info("处理异常", e);
		}

		return scannedResponse;
	}

	/***
	 * 刷卡的
	 */
	public WechatMicroResponse payByCard(WechatMicroRequest wechatMicroRequest) {
		Map<String, Object> result = new HashMap<String, Object>();
		WechatMicroResponse wechatMicroResponse = null;
		log.info("处理二维码生成");

		log.info("根据商户号查询");

		String out_trade_no = "";// 订单号
		out_trade_no = wechatMicroRequest.getOut_trade_no(); // 10业务号2业务细; 订单号
																// 现根据规则生成订单号
		log.info("根据商户号查询");
		String mercId = wechatMicroRequest.getMerchantId();

		// o单编号
		String oAgentNo = "";

		PmsMerchantInfo merchantinfo = new PmsMerchantInfo();
		merchantinfo.setMercId(mercId);

		// 查询当前商户信息
		try {
			List<PmsMerchantInfo> merchantList = pmsMerchantInfoDao.searchList(merchantinfo);

			if (!(merchantList.size() == 0 || merchantList.isEmpty())) {

				// 正式商户
				merchantinfo = merchantList.get(0);

				oAgentNo = merchantinfo.getoAgentNo();//

				OriginalOrderInfo oriInfo = new OriginalOrderInfo();
				oriInfo.setMerchantOrderId(wechatMicroRequest.getProduct_id());
				oriInfo.setPid(wechatMicroRequest.getMerchantId());

				oriInfo = originalDao.selectByOriginal(oriInfo);

				if (oriInfo != null) {
					log.error("下单重复");
					result.put("respCode", "16");
					result.put("respMsg", "下单重复");
				} else if ("60".equals(merchantinfo.getMercSts())) {
					// 判断是否为正式商户

					saveOriginAlInfos(wechatMicroRequest, out_trade_no, mercId);
					// 校验商户金额限制
					Map<String, String> paramMap = new HashMap<String, String>();
					paramMap.put("mercid", merchantinfo.getMercId());// 商户编号
					paramMap.put("businesscode", TradeTypeEnum.merchantCollect.getTypeCode());// 业务编号
					paramMap.put("oAgentNo", oAgentNo);
					// 商户 网购 业务信息
					Map<String, String> resultMap = merchantMineDao.queryBusinessInfo(paramMap);

					String quickRateType = resultMap.get("QUICKRATETYPE");// 快捷支付费率类型

					// 获取o单第三方支付的费率
					AppRateConfig appRate = new AppRateConfig();
					appRate.setRateType(quickRateType);
					appRate.setoAgentNo(oAgentNo);
					AppRateConfig appRateConfig = appRateConfigDao.getByRateTypeAndoAgentNo(appRate);

					paramMap.put("mercid", mercId);
					paramMap.put("businesscode", TradeTypeEnum.merchantCollect.getTypeCode());
					// 刷卡支付
					paramMap.put("paymentcode", PaymentCodeEnum.shuakaPay.getTypeCode());

					// 查询商户费率 和 最 低收款金额 支付方式是否开通 业务是否开通 等参数
					AppRateTypeAndAmount appRateTypeAndAmount = pmsAppAmountAndRateConfigDao
							.queryAmountAndStatus(paramMap);
					if (appRateTypeAndAmount != null) {

						String status = appRateTypeAndAmount.getStatus();// 此业务是否开通

						// String statusMessage =
						// appRateTypeAndAmount.getMessage();//此业务是否开通的描述

						String payStatus = appRateTypeAndAmount.getPayStatus();// 此支付方式是否开通

						// 判断此业务O单是否开通（总）
						ResultInfo resultInfoForOAgentNo = publicTradeVerifyService
								.moduleVerifyOagent(TradeTypeEnum.merchantCollect, oAgentNo);

						if (!resultInfoForOAgentNo.getErrCode().equals("0")) {
							if (StringUtils.isEmpty(resultInfoForOAgentNo.getMsg())) {
								log.error("此功能暂时关闭!");
								result.put("respCode", "05");
								result.put("respMsg", "此功能暂时关闭");
							} else {
								log.error(resultInfoForOAgentNo.getMsg());
								result.put("respCode", "05");
								result.put("respMsg", resultInfoForOAgentNo.getMsg());
							}
						} else {

							if ("1".equals(status)) {

								// 判断支付方式时候开通总开关
								ResultInfo payCheckResult = null;

								payCheckResult = payTypeControlDao.checkLimit(oAgentNo,
										PaymentCodeEnum.weixinPay.getTypeCode());
								String ss = payCheckResult.getErrCode();
								if (!payCheckResult.getErrCode().equals("0")) {
									// 支付方式时候开通总开关 禁用
									result.put("respCode", "07");
									result.put("respMsg", "此支付方式暂时关闭");
									log.info("此支付方式暂时关闭");
								} else {

									BigDecimal payAmt = new BigDecimal(wechatMicroRequest.getTotal_fee());// 收款金额
									// 判读 交易金额是不是在欧单区间控制之内
									ResultInfo resultInfo = amountLimitControlDao.checkLimit(oAgentNo, payAmt,
											TradeTypeEnum.merchantCollect.getTypeCode());
									// 返回不为0，一律按照交易失败处理
									if (!resultInfo.getErrCode().equals("0")) {
										result.put("respCode", "08");
										result.put("respMsg", "交易金额不在申请的范围之内");
										log.info("交易金额不在申请的范围之内");

									} else {

										// 商户渠道支付方式
										// 商户渠道交易类型
										// 验证支付方式是否开启

										ResultInfo resultinfo = null;
										resultinfo = iPublicTradeVerifyService
												.payTypeVerifyMer(PaymentCodeEnum.weixinPay, mercId);
										if ("0".equals(resultinfo.getErrCode())) {
											if ("0".equals(payStatus)) {
												// 有效
												// MIN_AMOUNT,MAX_AMOUNT ,RATE
												// ,STATUS
												String rateStr = appRateConfig.getRate(); // 商户费率
																							// RATE

												BigDecimal min_amount = new BigDecimal(
														appRateTypeAndAmount.getMinAmount());// 最低收款金额
												// MIN_AMOUNT
												BigDecimal max_amount = new BigDecimal(
														appRateTypeAndAmount.getMaxAmount());// 最高收款金额
												// MAX_AMOUNT

												if (min_amount.compareTo(payAmt) != 1) {// 判断收款金额是否大于最低收款金额
													// 大于等于执行
													// 小于不执行
													if (payAmt.compareTo(max_amount) != 1) {
														// 组装报文
														String totalAmount = wechatMicroRequest.getTotal_fee(); // 交易金额

														PmsAppTransInfo appTransInfo = this.insertOrder(out_trade_no,
																totalAmount, mercId, rateStr, oAgentNo);

														if (appTransInfo != null) {
															// 处理生成二维码
															wechatMicroResponse = this.twoDimensionCodeProcesss(
																	wechatMicroRequest, result, appTransInfo);
														} else {
															// 交易金额小于收款最低金额
															result.put("respCode", "11");
															result.put("respMsg", "生成订单流水失败");
															log.info("生成订单流水失败");
														}

													} else {

														// 交易金额小于收款最低金额
														result.put("respCode", "10");
														result.put("respMsg", "交易金额大于收款最高金额");
														log.info("交易金额大于收款最高金额");
													}

												} else {
													// 交易金额小于收款最低金额
													result.put("respCode", "09");
													result.put("respMsg", "交易金额小于收款最低金额");
													log.info("交易金额小于收款最低金额");
												}

											} else {
												// 交易金额小于收款最低金额
												result.put("respCode", "12");
												result.put("respMsg", "商户收款关闭");
												log.info("商户交易关闭");
											}
										} else {
											// 交易金额小于收款最低金额
											result.put("respCode", "13");
											result.put("respMsg", "扫码支付关闭");
											log.info("扫码支付关闭");
										}

									}
								}

							} else {
								log.error("此功能暂未开通");
								result.put("respCode", "06");
								result.put("respMsg", "此功能暂未开通");

							}

						}

					} else {
						log.error("没有找到商户费率");
						result.put("respCode", "04");
						result.put("respMsg", "没有找到商户费率");
					}

				} else {
					log.error("不是正式商户!");
					result.put("respCode", "03");
					result.put("respMsg", "不是正式商户");
				}

			} else {
				log.error("商户不存在!");
				result.put("respCode", "02");
				result.put("respMsg", "商户不存在");
			}

		} catch (Exception e) {
			e.printStackTrace();
			log.info("处理异常", e);
		}

		return wechatMicroResponse;
	}

	/**
	 * 支付宝接口
	 * 
	 * @param alipayScanParamRequest
	 * @return
	 */
	public Map<String, Object> alipayParam(AlipayParamRequest alipayScanParamRequest, Map<String, Object> result, PmsBusinessPos busInfo) {
		//Map<String, Object> result = new HashMap<String, Object>();
		AlipayParamResponse AlipayParamResponse = null;
		log.info("生成支付宝二维码");

		log.info("根据商户号查询");

		String out_trade_no = "";// 订单号
		out_trade_no = alipayScanParamRequest.getOut_trade_no(); // 10业务号2业务细;
																	// 订单号
																	// 现根据规则生成订单号
		log.info("根据商户号查询");
		// 下游商户号
		String mercId = alipayScanParamRequest.getMerchantId();

		// o单编号
		String oAgentNo = "";

		PmsMerchantInfo merchantinfo = new PmsMerchantInfo();
		merchantinfo.setMercId(mercId);

		// 查询当前商户信息
		try {
			List<PmsMerchantInfo> merchantList = pmsMerchantInfoDao.searchList(merchantinfo);

			if (!(merchantList.size() == 0 || merchantList.isEmpty())) {

				// 正式商户
 				merchantinfo = merchantList.get(0);

				oAgentNo = merchantinfo.getoAgentNo();//

				OriginalOrderInfo oriInfo = new OriginalOrderInfo();
				oriInfo.setMerchantOrderId(alipayScanParamRequest.getOut_trade_no());
				oriInfo.setPid(alipayScanParamRequest.getMch_id());

				oriInfo = originalDao.selectByOriginal(oriInfo);

				if (oriInfo != null) {
					log.error("下单重复");
					result.put("respCode", "16");
					result.put("respMsg", "下单重复");
				} else if ("60".equals(merchantinfo.getMercSts())) {
					// 判断是否为正式商户

					saveOriginAlInfoAlipay(alipayScanParamRequest, out_trade_no, mercId);
					// 校验商户金额限制
					Map<String, String> paramMap = new HashMap<String, String>();
					paramMap.put("mercid", merchantinfo.getMercId());// 商户编号
					paramMap.put("businesscode", TradeTypeEnum.merchantCollect.getTypeCode());// 业务编号
					paramMap.put("oAgentNo", oAgentNo);
					// 商户 网购 业务信息
					Map<String, String> resultMap = merchantMineDao.queryBusinessInfo(paramMap);

					String quickRateType = resultMap.get("QUICKRATETYPE");// 快捷支付费率类型

					// 获取o单第三方支付的费率
					AppRateConfig appRate = new AppRateConfig();
					appRate.setRateType(quickRateType);
					appRate.setoAgentNo(oAgentNo);
					AppRateConfig appRateConfig = appRateConfigDao.getByRateTypeAndoAgentNo(appRate);

					paramMap.put("mercid", mercId);
					paramMap.put("businesscode", TradeTypeEnum.merchantCollect.getTypeCode());
					// 支付宝支付
					paramMap.put("paymentcode", PaymentCodeEnum.zhifubaoPay.getTypeCode());

					// 查询商户费率 和 最 低收款金额 支付方式是否开通 业务是否开通 等参数
					AppRateTypeAndAmount appRateTypeAndAmount = pmsAppAmountAndRateConfigDao
							.queryAmountAndStatus(paramMap);
					if (appRateTypeAndAmount != null) {

						String status = appRateTypeAndAmount.getStatus();// 此业务是否开通

						String payStatus = appRateTypeAndAmount.getPayStatus();// 此支付方式是否开通

						// 判断此业务O单是否开通（总）
						ResultInfo resultInfoForOAgentNo = publicTradeVerifyService
								.moduleVerifyOagent(TradeTypeEnum.merchantCollect, oAgentNo);

						if (!resultInfoForOAgentNo.getErrCode().equals("0")) {
							if (StringUtils.isEmpty(resultInfoForOAgentNo.getMsg())) {
								log.error("此功能暂时关闭!");
								result.put("respCode", "05");
								result.put("respMsg", "此功能暂时关闭");
							} else {
								log.error(resultInfoForOAgentNo.getMsg());
								result.put("respCode", "05");
								result.put("respMsg", resultInfoForOAgentNo.getMsg());
							}
						} else {

							if ("1".equals(status)) {

								// 判断支付方式时候开通总开关
								ResultInfo payCheckResult = null;

								payCheckResult = payTypeControlDao.checkLimit(oAgentNo,
										PaymentCodeEnum.zhifubaoPay.getTypeCode());
								String ss = payCheckResult.getErrCode();
								if (!payCheckResult.getErrCode().equals("0")) {
									// 支付方式时候开通总开关 禁用
									result.put("respCode", "07");
									result.put("respMsg", "此支付方式暂时关闭");
									log.info("此支付方式暂时关闭");
								} else {

									BigDecimal payAmt = new BigDecimal(alipayScanParamRequest.getTotal_fee());// 收款金额
									// 判读 交易金额是不是在欧单区间控制之内
									ResultInfo resultInfo = amountLimitControlDao.checkLimit(oAgentNo, payAmt,
											TradeTypeEnum.merchantCollect.getTypeCode());
									// 返回不为0，一律按照交易失败处理
									if (!resultInfo.getErrCode().equals("0")) {
										result.put("respCode", "08");
										result.put("respMsg", "交易金额不在申请的范围之内");
										log.info("交易金额不在申请的范围之内");

									} else {

										// 商户渠道支付方式
										// 商户渠道交易类型
										// 验证支付方式是否开启

										ResultInfo resultinfo = null;
										resultinfo = iPublicTradeVerifyService
												.payTypeVerifyMer(PaymentCodeEnum.zhifubaoPay, mercId);
										if ("0".equals(resultinfo.getErrCode())) {
											if ("0".equals(payStatus)) {
												// 有效
												// MIN_AMOUNT,MAX_AMOUNT ,RATE
												// ,STATUS
												String rateStr = appRateConfig.getRate(); // 商户费率
																							// RATE

												BigDecimal min_amount = new BigDecimal(
														appRateTypeAndAmount.getMinAmount());// 最低收款金额
												// MIN_AMOUNT
												BigDecimal max_amount = new BigDecimal(
														appRateTypeAndAmount.getMaxAmount());// 最高收款金额
												// MAX_AMOUNT

												if (min_amount.compareTo(payAmt) != 1) {// 判断收款金额是否大于最低收款金额
													// 大于等于执行
													// 小于不执行
													if (payAmt.compareTo(max_amount) != 1) {
														// 组装报文
														String totalAmount = alipayScanParamRequest.getTotal_fee()+""; // 交易金额

														PmsAppTransInfo appTransInfo = this.insertOrder(out_trade_no,
																totalAmount, mercId, rateStr, oAgentNo);

														if (appTransInfo != null) {
															// 处理生成二维码
															// ------------------------------------------
															AlipayParamResponse = this.twoDimensionCodeAlipayScanParam(
																	alipayScanParamRequest, result, appTransInfo);
															result=BeanToMapUtil.convertBean(AlipayParamResponse);
														} else {
															// 交易金额小于收款最低金额
															result.put("respCode", "11");
															result.put("respMsg", "生成订单流水失败");
															log.info("生成订单流水失败");
														}

													} else {

														// 交易金额小于收款最低金额
														result.put("respCode", "10");
														result.put("respMsg", "交易金额大于收款最高金额");
														log.info("交易金额大于收款最高金额");
													}

												} else {
													// 交易金额小于收款最低金额
													result.put("respCode", "09");
													result.put("respMsg", "交易金额小于收款最低金额");
													log.info("交易金额小于收款最低金额");
												}

											} else {
												// 交易金额小于收款最低金额
												result.put("respCode", "12");
												result.put("respMsg", "商户收款关闭");
												log.info("商户交易关闭");
											}
										} else {
											// 交易金额小于收款最低金额
											result.put("respCode", "13");
											result.put("respMsg", "扫码支付关闭");
											log.info("扫码支付关闭");
										}

									}
								}

							} else {
								log.error("此功能暂未开通");
								result.put("respCode", "06");
								result.put("respMsg", "此功能暂未开通");

							}

						}

					} else {
						log.error("没有找到商户费率");
						result.put("respCode", "04");
						result.put("respMsg", "没有找到商户费率");
					}

				} else {
					log.error("不是正式商户!");
					result.put("respCode", "03");
					result.put("respMsg", "不是正式商户");
				}

			} else {
				log.error("商户不存在!");
				result.put("respCode", "02");
				result.put("respMsg", "商户不存在");
			}

		} catch (Exception e) {
			e.printStackTrace();
			log.info("处理异常", e);
		}

		return result;

	}

	/**
	 * 
	 * @Description 刷卡插入原始订单表信息
	 * @author Administrator
	 * @param reqeustInfo
	 * @param orderid
	 * @param mercId
	 * @throws Exception
	 */
	private int saveOriginAlInfos(WechatMicroRequest wechatMicroRequest, String orderid, String mercId)
			throws Exception {
		// 插入原始信息
		OriginalOrderInfo info = new OriginalOrderInfo();
		info.setPid(mercId);
		info.setMerchantOrderId(wechatMicroRequest.getProduct_id());
		info.setOrderId(orderid);
		info.setOrderTime(UtilDate.getOrderNum());
		info.setPayType("刷卡");
		Double amt = Double.parseDouble(wechatMicroRequest.getTotal_fee());// 单位分
		amt /= 100;
		DecimalFormat df = new DecimalFormat("######0.00");

		return originalDao.insert(info);
	}

	/**
	 * 
	 * @Description 微信插入原始订单表信息
	 * @author Administrator
	 * @param reqeustInfo
	 * @param orderid
	 * @param mercId
	 * @throws Exception
	 */
	private int saveOriginAlInfo(WechatScannedRequest wechatScannedRequest, String orderid, String mercId)
			throws Exception {
		// 插入原始信息
		OriginalOrderInfo info = new OriginalOrderInfo();
		info.setPid(mercId);
		info.setMerchantOrderId(wechatScannedRequest.getProduct_id());
		info.setOrderId(orderid);
		info.setOrderTime(UtilDate.getOrderNum());
		info.setPayType("微信扫码");
		info.setBgUrl(wechatScannedRequest.getNotify_url());
		Double amt = Double.parseDouble(wechatScannedRequest.getTotal_fee());// 单位分
		amt /= 100;
		DecimalFormat df = new DecimalFormat("######0.00");

		info.setOrderAmount(df.format(amt));

		return originalDao.insert(info);
	}

	/**
	 * 
	 * @Description 支付宝插入原始订单表信息 公众号
	 * @author Administrator
	 * @param reqeustInfo
	 * @param orderid
	 * @param mercId
	 * @throws Exception
	 */
	private int saveOriginAlInfoAlipay(AlipayParamRequest alipayScanParamRequest, String orderid, String mercId)
			throws Exception {
		// 插入原始信息
		OriginalOrderInfo info = new OriginalOrderInfo();
		info.setPid(mercId);
		info.setMerchantOrderId(alipayScanParamRequest.getOut_trade_no());
		info.setOrderId(orderid);
		info.setOrderTime(UtilDate.getOrderNum());
		info.setPayType("公众号扫码");
		info.setBgUrl(alipayScanParamRequest.getNotify_url());
		Double amt = Double.parseDouble(alipayScanParamRequest.getTotal_fee()+"");// 单位分
		amt /= 100;
		DecimalFormat df = new DecimalFormat("######0.00");

		info.setOrderAmount(df.format(amt));

		return originalDao.insert(info);
	}

	/**
	 * 
	 * @Description 插入原始订单表信息 公众号
	 * @author Administrator
	 * @param reqeustInfo
	 * @param orderid
	 * @param mercId
	 * @throws Exception
	 */
	private int saveOriginAlInfoAlipayCard(AlipayParamRequest alipayScanParamRequest, String orderid, String mercId)
			throws Exception {
		// 插入原始信息
		OriginalOrderInfo info = new OriginalOrderInfo();
		info.setPid(mercId);
		info.setMerchantOrderId(alipayScanParamRequest.getOut_trade_no());
		info.setOrderId(orderid);
		info.setOrderTime(UtilDate.getOrderNum());
		info.setPayType("公众号刷卡");
		info.setBgUrl(alipayScanParamRequest.getNotify_url());
		Double amt = Double.parseDouble(alipayScanParamRequest.getTotal_fee()+"");// 单位分
		amt /= 100;
		DecimalFormat df = new DecimalFormat("######0.00");

		info.setOrderAmount(df.format(amt));

		return originalDao.insert(info);
	}

	/**
	 * 订单入库
	 * 
	 * @Description
	 * @author Administrator
	 * @param orderid
	 * @param payamount
	 * @param mercId
	 * @param rateStr
	 * @param businessnum
	 * @param oAgentNo
	 * @return
	 * @throws Exception
	 */
	public PmsAppTransInfo insertOrder(String orderid, String payamount, String mercId, String rateStr, String oAgentNo)
			throws Exception {

		System.out.println("12345613454354=" + orderid);
		// 查询商户费率
		BigDecimal rate = new BigDecimal(rateStr);
		BigDecimal amount = new BigDecimal(payamount);

		// 成功后订到入库app后台
		PmsAppTransInfo pmsAppTransInfo = new PmsAppTransInfo();

		pmsAppTransInfo.setTradetype(TradeTypeEnum.merchantCollect.getTypeName());
		pmsAppTransInfo.setTradetime(UtilDate.getDateFormatter());
		pmsAppTransInfo.setOrderid(orderid);// 上送的订单号

		pmsAppTransInfo.setReasonofpayment(TradeTypeEnum.merchantCollect.getTypeName());
		pmsAppTransInfo.setMercid(mercId);
		pmsAppTransInfo.setFactamount(payamount);// 实际金额 按分为最小单位 例如：1元=100分
													// 采用100
		pmsAppTransInfo.setTradetypecode("1");
		pmsAppTransInfo.setOrderamount(payamount);// 订单金额 按分为最小单位 例如：1元=100分
													// 采用100
		pmsAppTransInfo.setStatus(Constants.ORDERINITSTATUS);// 订单初始化状态
		pmsAppTransInfo.setoAgentNo(oAgentNo);// o单编号

		BigDecimal poundage = amount.multiply(rate);// 手续费
		BigDecimal b = new BigDecimal(0);

		BigDecimal dfactAmount = new BigDecimal(pmsAppTransInfo.getFactamount());
		double fee1 = poundage.doubleValue();
		PmsMerchantInfo merchantinfo = new PmsMerchantInfo();
		merchantinfo.setMercId(mercId);
		// 结算金额
		BigDecimal payAmount = null;
		List<PmsMerchantInfo> merchantList = pmsMerchantInfoDao.searchList(merchantinfo);

		if (!(merchantList.size() == 0 || merchantList.isEmpty())) {

			// 正式商户
			merchantinfo = merchantList.get(0);
			if (merchantinfo.getCounter() != null) {
				double num = Double.parseDouble(merchantinfo.getCounter()) * 100;
				if (fee1 < num) {
					b = new BigDecimal(String.valueOf(num));
					payAmount = dfactAmount.subtract(b);
				} else {
					b = poundage;
					payAmount = dfactAmount.subtract(b);
				}
			}
		}
		pmsAppTransInfo.setRate(rateStr);// 费率

		// 结算金额 按分为最小单位 例如：1元=100分 采用100 商户收款时给商户记账时减去费率(实际金额-手续费)
		pmsAppTransInfo.setPayamount(payAmount.toString());

		pmsAppTransInfo.setPoundage(b.toString());// 手续费 按分为最小单位
													// 例如：1元=100分 采用100
		String sendString = createJsonString(pmsAppTransInfo);

		try {
			if (pmsAppTransInfoDao.insert(pmsAppTransInfo) != 1) {
				log.info("订单入库失败， 订单号：" + orderid + "，结束时间：" + UtilDate.getDateFormatter() + "。订单详细信息：" + sendString);
				throw new RuntimeException("手动抛出");
			}
		} catch (Exception e) {
			log.info("订单入库失败， 订单号：" + orderid + "，结束时间：" + UtilDate.getDateFormatter() + "。订单详细信息：" + sendString, e);
			throw new RuntimeException("手动抛出");
		}
		return pmsAppTransInfo;

	}

	/**
	 * 
	 * @Description 处理生成二维码
	 * @author Administrator
	 * @param wechatScannedRequest
	 * @param result
	 * @param pospTransInfo
	 * @throws Exception
	 */
	private WechatScannedResponse twoDimensionCodeProcess(WechatScannedRequest wechatScannedRequest,
			Map<String, Object> result, PmsAppTransInfo pmsAppTransInfo) throws Exception {
		pmsAppTransInfo = pmsAppTransInfoDao.searchOrderInfo(pmsAppTransInfo.getOrderid());
		log.info("请求交易生成二维码map");
		// 查询上游商户号和密钥
		PmsBusinessPos busInfo = selectKey(pmsAppTransInfo.getMercid());
		// 组装上送参数
		// 1微信
		pmsAppTransInfo.setPaymenttype(PaymentCodeEnum.weixinPay.getTypeName());
		pmsAppTransInfo.setPaymentcode(PaymentCodeEnum.weixinPay.getTypeCode());
		// 产品代码
		Date now = new Date();
		String key = busInfo.getKek();// Constant.BRCB_KEY;
		String service_type = Constant.BRCB_SERVICE_TYPE_SCANNED;
		// String appid = "";
		String mch_id = "C" + busInfo.getBusinessnum();// Constant.BRCB_MCH_ID;
		String out_trade_no = wechatScannedRequest.getOut_trade_no();
		// String device_info = wechatScannedRequest.getDevice_info();
		String body = wechatScannedRequest.getBody();
		// String detail = wechatScannedRequest.getDetail();
		// String attach = wechatScannedRequest.getAttach();
		// String fee_type = Constant.BRCB_FEE_TYPE;
		String total_fee = pmsAppTransInfo.getOrderamount();
		String spbill_create_ip = null;
		try {
			spbill_create_ip = InetAddress.getLocalHost().getHostAddress();
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}
		String notify_url = Constant.BRCB_NOTIFY_URL;
		// String time_start = DateUtil.format(now,
		// DateUtil.DATE_STYLE_YYYYMMDDHHMMSS);
		// String time_expire = DateUtil.format(DateUtil.addMinutes(now, 10),
		// DateUtil.DATE_STYLE_YYYYMMDDHHMMSS);
		// String op_user_id = mch_id;
		// String goods_tag = Constant.BRCB_ORDER_TYPE_WECHAT;
		// String product_id = RandomUtil.getRandomStringByLength(10);
		String nonce_str = RandomUtil.randomUUID();
		// String limit_pay = "";
		WechatScannedRequest scannedRequest = new WechatScannedRequest(key, service_type, mch_id, nonce_str, body,
				out_trade_no, total_fee, spbill_create_ip, notify_url, log);
		WechatScannedResponse scannedResponse = doScanned(scannedRequest, log);

		log.info("上送返回结果", scannedResponse);

		// 启线程查询订单状态
		String status = scannedResponse.getResult_code();
		log.info(pmsAppTransInfo.toString());
		log.info(scannedResponse.toString());

		// 查看当前交易是否已经生成了流水表
		PospTransInfo pospTransInfo = null;
		// 流水表是否需要更新的标记 0 insert，1：update
		int insertOrUpdateFlag = 0;
		log.info("***************进入payHandle5-14-3***************");
		// 生成上送流水号
		String transOrderId = out_trade_no;
		log.info("***************进入payHandle5-15***************");
		if ((pospTransInfo = pospTransInfoDAO.searchByOrderId(pmsAppTransInfo.getOrderid())) != null) {
			// 已经存在，修改流水号，设置pospsn为空
			log.info("订单号：" + pmsAppTransInfo.getOrderid() + ",生成上送通道的流水号：" + transOrderId);
			pospTransInfo.setTransOrderId(transOrderId);
			pospTransInfo.setResponsecode("20");
			pospTransInfo.setPospsn("");
			insertOrUpdateFlag = 1;
			log.info("***************进入payHandle5-16***************");
		} else {
			// 不存在流水，生成一个流水
			pospTransInfo = generateTransFromAppTrans(pmsAppTransInfo);
			// 设置上送流水号
			pospTransInfo.setTransOrderId(transOrderId);
			insertOrUpdateFlag = 0;
		}
		log.info("***************进入payHandle5-17***************");
		// 插入流水表信息
		if (insertOrUpdateFlag == 0) {
			// 插入一条流水
			pospTransInfoDAO.insert(pospTransInfo);
		} else if (insertOrUpdateFlag == 1) {
			// 更新一条流水
			pospTransInfoDAO.updateByOrderId(pospTransInfo);
		}
		pmsAppTransInfoDao.update(pmsAppTransInfo);
		///////
		if ("SUCCESS".equals(status)) {

			// 启线程查询订单状态
			ThreadPool.executor(new NSThread(pmsAppTransInfo.getOrderid(), this, pmsAppTransInfoDao));

		} else {
			log.info("生成二维码失败");
		}

		log.info("设置result map 返回值");
		OriginalOrderInfo original = originalDao.getOriginalOrderInfoByOrderid(pmsAppTransInfo.getOrderid());
		if (original != null) {
			original.setUrl((String) result.get("url"));
			log.info("修改原始订单" + original);
			originalDao.update(original);
		}

		return scannedResponse;
	}

	/**
	 * 
	 * @Description 刷卡处理生成二维码
	 * @author Administrator
	 * @param wechatScannedRequest
	 * @param result
	 * @param pospTransInfo
	 * @throws Exception
	 */
	private WechatMicroResponse twoDimensionCodeProcesss(WechatMicroRequest wechatMicroRequest,
			Map<String, Object> result, PmsAppTransInfo pmsAppTransInfo) throws Exception {
		pmsAppTransInfo = pmsAppTransInfoDao.searchOrderInfo(pmsAppTransInfo.getOrderid());
		log.info

		("请求交易生成二维码map");
		// 查询上游商户号和密钥
		PmsBusinessPos busInfo = selectKey(pmsAppTransInfo.getMercid());
		// 组装上送参数
		// 1微信
		pmsAppTransInfo.setPaymenttype(PaymentCodeEnum.shuakaPay.getTypeName());
		pmsAppTransInfo.setPaymentcode(PaymentCodeEnum.shuakaPay.getTypeCode());
		// 产品代码
		Date now = new Date();
		String key = busInfo.getKek(); // Constant.BRCB_KEY;
		String service_type = Constant.BRCB_SERVICE_TYPE_MICRO;
		String appid = "";
		String mch_id = "C" + busInfo.getBusinessnum();
		String out_trade_no = wechatMicroRequest.getOut_trade_no();
		String device_info = wechatMicroRequest.getDevice_info();
		String body = wechatMicroRequest.getBody();
		String detail = wechatMicroRequest.getDetail();
		String attach = wechatMicroRequest.getAttach();
		String fee_type = Constant.BRCB_FEE_TYPE;
		String total_fee = wechatMicroRequest.getTotal_fee();
		String spbill_create_ip = null;
		try {
			spbill_create_ip = InetAddress.getLocalHost().getHostAddress();
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}
		String time_start = DateUtil.format(now, DateUtil.DATE_STYLE_YYYYMMDDHHMMSS);
		String time_expire = DateUtil.format(DateUtil.addMinutes(now, 10), DateUtil.DATE_STYLE_YYYYMMDDHHMMSS);
		String op_user_id = mch_id;
		String goods_tag = Constant.BRCB_ORDER_TYPE_WECHAT;
		String product_id = RandomUtil.getRandomStringByLength(10);
		String nonce_str = RandomUtil.randomUUID();
		String auth_code = wechatMicroRequest.getAuth_code();
		WechatMicroRequest microRequest = new WechatMicroRequest(key, service_type, appid, mch_id, nonce_str,
				out_trade_no, auth_code, body, total_fee, attach, detail, device_info, fee_type, goods_tag, product_id,
				spbill_create_ip, op_user_id, time_expire, time_start, log);
		WechatMicroResponse microResponse = doMicro(microRequest, log);
		microResponse.setMerchantId(wechatMicroRequest.getMerchantId());
		log.info

		("上送返回结果", microResponse);

		// 启线程查询订单状态
		String status = microResponse.getReturn_code();
		log.info

		(pmsAppTransInfo.toString());
		log.info

		(microResponse.toString());

		// ------------------------------------
		// 查看当前交易是否已经生成了流水表
		PospTransInfo pospTransInfo = null;
		// 流水表是否需要更新的标记 0 insert，1：update
		int insertOrUpdateFlag = 0;
		log.info

		("***************进入payHandle5-14-3***************");
		// 生成上送流水号
		String transOrderId = out_trade_no;
		log.info

		("***************进入payHandle5-15***************");
		if ((pospTransInfo = pospTransInfoDAO.searchByOrderId(pmsAppTransInfo.getOrderid())) != null) {
			// 已经存在，修改流水号，设置pospsn为空
			log.info

			("订单号：" + pmsAppTransInfo.getOrderid() + ",生成上送通道的流水号：" + transOrderId);
			pospTransInfo.setTransOrderId(transOrderId);
			pospTransInfo.setResponsecode("20");
			pospTransInfo.setPospsn("");
			insertOrUpdateFlag = 1;
			log.info

			("***************进入payHandle5-16***************");
		} else {
			// 不存在流水，生成一个流水
			pospTransInfo = InsertJournal(pmsAppTransInfo);
			// 设置上送流水号
			// 通道订单号
			pospTransInfo.setTransOrderId(transOrderId);
			insertOrUpdateFlag = 0;
		}
		log.info

		("***************进入payHandle5-17***************");
		// 插入流水表信息
		if (insertOrUpdateFlag == 0) {
			// 插入一条流水
			pospTransInfoDAO.insert(pospTransInfo);
		} else if (insertOrUpdateFlag == 1) {
			// 更新一条流水
			pospTransInfoDAO.updateByOrderId(pospTransInfo);
		} // -----------------------------------------------
		pmsAppTransInfoDao.update(pmsAppTransInfo);
		///////
		if ("SUCCESS".equals(status)) {

			// 启线程查询订单状态
			ThreadPool.executor(new NSThread(pmsAppTransInfo.getOrderid(), this, pmsAppTransInfoDao));

		} else {
			log.info

			("失败");
		}

		log.info

		("设置result map 返回值");
		OriginalOrderInfo original = originalDao.getOriginalOrderInfoByOrderid(pmsAppTransInfo.getOrderid());
		if (original != null) {
			original.setUrl((String) result.get("url"));
			log.info

			("修改原始订单" + original);
			originalDao.update(original);
		}

		return microResponse;
	}

	// ------------------------------------------------------------------------------------------------------------------------
	/**
	 * 
	 * @Description 支付宝处理生成二维码
	 * @author Administrator
	 * @param wechatScannedRequest
	 * @param result
	 * @param pospTransInfo
	 * @throws Exception
	 */
	@SuppressWarnings("unused")
	private AlipayParamResponse twoDimensionCodeAlipayScanParam(AlipayParamRequest alipayScanParamRequest,
			Map<String, Object> result, PmsAppTransInfo pmsAppTransInfo) throws Exception {

		// 查看当前交易是否已经生成了流水表
		PospTransInfo pospTransInfo = null;
		// 流水表是否需要更新的标记 0 insert，1：update
		int insertOrUpdateFlag = 0;
		log.info("***************进入payHandle5-14-3***************");
		// 生成上送流水号
		String transOrderId = alipayScanParamRequest.getOut_trade_no();
		log.info("***************进入payHandle5-15***************");
		if ((pospTransInfo = pospTransInfoDAO.searchByOrderId(pmsAppTransInfo.getOrderid())) != null) {
			// 已经存在，修改流水号，设置pospsn为空
			log.info("订单号：" + pmsAppTransInfo.getOrderid() + ",生成上送通道的流水号：" + transOrderId);
			pospTransInfo.setTransOrderId(transOrderId);
			pospTransInfo.setResponsecode("20");
			pospTransInfo.setPospsn("");
			insertOrUpdateFlag = 1;
			log.info("***************进入payHandle5-16***************");
		} else {
			// 不存在流水，生成一个流水
			pospTransInfo = InsertJournal(pmsAppTransInfo);
			// 设置上送流水号
			// 通道订单号
			pospTransInfo.setTransOrderId(transOrderId);
			insertOrUpdateFlag = 0;
		}
		log.info("***************进入payHandle5-17***************");
		// 插入流水表信息
		if (insertOrUpdateFlag == 0) {
			// 插入一条流水
			pospTransInfoDAO.insert(pospTransInfo);
		} else if (insertOrUpdateFlag == 1) {
			// 更新一条流水
			pospTransInfoDAO.updateByOrderId(pospTransInfo);
		}
		pmsAppTransInfo = pmsAppTransInfoDao.searchOrderInfo(pmsAppTransInfo.getOrderid());
		log.info("请求交易生成二维码map");
		// 查询上游商户号和密钥
		PmsBusinessPos busInfo = selectKey(pmsAppTransInfo.getMercid());
		// 业务代码
		// 组装上送参数
		String service_type = "";
		switch (alipayScanParamRequest.getService_type()) {
		case "1":
			// 1 支付宝扫码
			service_type = "ALIPAY_SCANNED";
			pmsAppTransInfo.setPaymenttype(PaymentCodeEnum.zhifubaoPay.getTypeName());
			pmsAppTransInfo.setPaymentcode(PaymentCodeEnum.zhifubaoPay.getTypeCode());
			break;
		case "2":
			// 2支付宝刷卡
			service_type = "ALIPAY_MICRO";
			pmsAppTransInfo.setPaymenttype(PaymentCodeEnum.zhifubaoPay.getTypeName());
			pmsAppTransInfo.setPaymentcode(PaymentCodeEnum.zhifubaoPay.getTypeCode());
			break;
		case "3":
			// 3支付宝服务窗
			service_type = "ALIPAY_SERVICEWINDOW";
			pmsAppTransInfo.setPaymenttype(PaymentCodeEnum.zhifubaoPay.getTypeName());
			pmsAppTransInfo.setPaymentcode(PaymentCodeEnum.zhifubaoPay.getTypeCode());
			break;
		case "4":
			// 4支付宝退款
			service_type = "ALIPAY_REFUND";
			pmsAppTransInfo.setPaymenttype(PaymentCodeEnum.zhifubaoPay.getTypeName());
			pmsAppTransInfo.setPaymentcode(PaymentCodeEnum.zhifubaoPay.getTypeCode());
			break;
		case "5":
			// 5支付宝撤销
			service_type = "ALIPAY_REVERSEORDER";
			pmsAppTransInfo.setPaymenttype(PaymentCodeEnum.zhifubaoPay.getTypeName());
			pmsAppTransInfo.setPaymentcode(PaymentCodeEnum.zhifubaoPay.getTypeCode());
			break;
		default:
			break;

		}
		log.info("订单表信息:" + pmsAppTransInfo);
		pmsAppTransInfoDao.update(pmsAppTransInfo);
		// 产品代码
		Date now = new Date();
		String key = busInfo.getKek(); // Constant.BRCB_KEY;
		// String service_type = "ALIPAY_SCANNED";
		String mch_id = "C" + busInfo.getBusinessnum();
		String merchantId = "";
		String out_trade_no = alipayScanParamRequest.getOut_trade_no();
		int total_fee = alipayScanParamRequest.getTotal_fee();
		String subject = alipayScanParamRequest.getSubject();
		String body = alipayScanParamRequest.getBody();
		String time_start = "";
		if (alipayScanParamRequest.getTime_start() != null) {
			time_start = alipayScanParamRequest.getTime_start();
		}
		String time_expire = "";
		if (alipayScanParamRequest.getTime_expire() != null) {
			time_expire = alipayScanParamRequest.getTime_expire();
		}
		String op_user_id = "";
		if (alipayScanParamRequest.getOp_user_id() != null) {
			op_user_id = mch_id;
		}
		String device_info = "";
		if (alipayScanParamRequest.getDevice_info() != null) {
			device_info = alipayScanParamRequest.getDevice_info();
		}
		String notify_url = BaseUtil.url+"/wechat/bgPayResult.action";
		String nonce_str = alipayScanParamRequest.getNonce_str();
		String limit_pay = "";
		if (alipayScanParamRequest.getLimit_pay() != null) {
			limit_pay = alipayScanParamRequest.getLimit_pay();
		}
		String store_id = "";
		String auth_code = "";
		String scene = "";
		if(alipayScanParamRequest.getScene()!=null)
		{
			scene=alipayScanParamRequest.getScene();
		}
		String out_refund_no = "";
		String refund_fee = "";
		String attach = "";
		String detail = "";
		String spbill_create_ip=null;
		try {
			spbill_create_ip = InetAddress.getLocalHost().getHostAddress();
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}
		String callback_url = "";
		String transaction_id = "";
		String refund_reason = "";
		String out_request_no = "";
		AlipayParamRequest alipay = new AlipayParamRequest(key, service_type, mch_id, merchantId, out_trade_no,
				total_fee, subject, body, time_start, time_expire, device_info, notify_url, nonce_str, attach,
				op_user_id, store_id, limit_pay, auth_code, detail, scene, spbill_create_ip,callback_url, transaction_id, out_refund_no,
				refund_fee, refund_reason, out_request_no, log);
		String sign = alipay.getSign();
		log.info("上送的签名:" + sign);
		AlipayParamResponse microResponse=new AlipayParamResponse();
		if("3".equals(alipayScanParamRequest.getService_type()))
		{
			String brcbGatewayUrl = Constant.BRCB_GATEWAY_URL;
			log.info("上送的url:"+brcbGatewayUrl);
//			String query="service_type="+alipay.getService_type()+"&mch_id="+alipay.getMch_id()+"&out_trade_no="+alipay.getOut_trade_no()+"&total_fee="+alipay.getTotal_fee()+"&subject="+alipay.getSubject()+"&body="+alipay.getBody()+"&time_start="+alipay.getTime_start()+"&time_expire="
//					+alipay.getTime_expire() +"&device_info="+alipay.getDevice_info()+"&spbill_create_ip="+alipay.getSpbill_create_ip()+"&notify_url="+alipay.getNotify_url()+"&callback_url="+alipay.getCallback_url()+"&nonce_str="+alipay.getNonce_str()+"&sign="+alipay.getSign()+"&brcbGatewayUrl="+brcbGatewayUrl;
			String query=alipay.getService_type()+"|"+alipay.getMch_id()+"|"+alipay.getOut_trade_no()+"|"+alipay.getTotal_fee()+"|"+alipay.getSubject()+"|"+alipay.getBody()+"|"+alipay.getTime_start()+"|"
					+alipay.getTime_expire() +"|"+alipay.getDevice_info()+"|"+alipay.getSpbill_create_ip()+"|"+alipay.getNotify_url()+"|"+alipay.getCallback_url()+"|"+alipay.getNonce_str()+"|"+alipay.getSign();
			log.info("上送的参数:"+query);
			microResponse.setMerchantId(alipayScanParamRequest.getMch_id());
			//microResponse.setQuerystring(query);
			//microResponse.setBrcbGatewayUrl(brcbGatewayUrl);
//			//对原始数据进行加密
//			Coder base64=new Coder();
//			byte[] b=query.getBytes();
//			String encode=base64.encryptBASE64(b);
			//md5.convertMD5(md5.convertMD5(encode));
//			 log.info("加密之后的数据:"+encode);
			log.info("支付宝服务窗上传地址:"+brcbGatewayUrl);	
			String code_url=BaseUtil.url+"/pay/bns/wechat/alipay/alipay_sunfund.jsp?query="+query;
			log.info("向上游发送的数据:"+code_url);
			microResponse.setCode_url(code_url);
			
		}else{
			
			microResponse = alipayScan(alipay, log);
			log.info("上游返回的参数:" + microResponse);
			microResponse.setMerchantId(alipayScanParamRequest.getMch_id());
			
		}
		return microResponse;

	}
	/**
	 * 
	 * @Description 支付宝处理查询
	 * @author Administrator
	 * @param wechatScannedRequest
	 * @param result
	 * @param pospTransInfo
	 * @throws Exception
	 */
	public Map<String, Object> alipayScanSelect(AlipayParamRequest alipayScanParamRequest, Map<String, Object> result, PmsBusinessPos busInfo) throws Exception {
		
		log.info("下游上送的数据:"+alipayScanParamRequest);
		String merchantId=alipayScanParamRequest.getMerchantId();
		//PmsBusinessPos busInfo = selectKey(merchantId);
		String key = busInfo.getKek(); // Constant.BRCB_KEY;
		String service_type = "ALIPAY_ORDERQUERY";
		String mch_id = "C" + busInfo.getBusinessnum();
		String transaction_id=alipayScanParamRequest.getTransaction_id();
		String out_trade_no = alipayScanParamRequest.getOut_trade_no();
		int total_fee =0;
		String subject = "";
		String body = "";
		String time_start = "";
		String time_expire = "";
		String op_user_id = "";
		String device_info = "";
		String notify_url = "";
		String nonce_str = alipayScanParamRequest.getNonce_str();
		String store_id = "";
		String detail="";
		String spbill_create_ip="";
		String auth_code = "";
		String scene = "";
		String out_refund_no = "";
		String limit_pay="";
		String refund_fee = "";
		String callback_url="";
		String refund_reason="";
		String out_request_no="";
		String attach = "";
		AlipayParamRequest alipay=new AlipayParamRequest(key, service_type,mch_id,merchantId,out_trade_no,total_fee, subject,body, time_start,time_expire, device_info, notify_url, nonce_str, attach,
				 op_user_id,store_id,limit_pay,auth_code,detail,scene,spbill_create_ip,callback_url,transaction_id,out_refund_no,refund_fee,refund_reason,out_request_no, log);
		String sign=alipay.getSign();
		AlipayParamResponse microResponse=new AlipayParamResponse();
		microResponse = alipayScan(alipay, log);
		log.info("上游返回的参数:" + microResponse);
		microResponse.setMerchantId(alipayScanParamRequest.getMch_id());
		result=BeanToMapUtil.convertBean(microResponse);
		return result;
	}

	// ------------------------------------------------------------------------------------------------------------------------
	/**
	 * 录入交易流水 并记算费率
	 * 
	 * @throws Exception
	 */
	public PospTransInfo InsertJournal(PmsAppTransInfo pmsAppTransInfo) throws Exception {
		log.info("----插入流水开始----");
		PospTransInfo pospTransInfo = new PospTransInfo();
		Integer id = pospTransInfoDAO.getNextTransid();
		if (id != null && id != 0) {
			pospTransInfo.setId(id);
		} else {
			log.info("根据订单生成流水失败，orderid：" + pmsAppTransInfo.getOrderid());
			return null;
		}
		// 获取通道的标准费率 END

		// 设置主机交易流水号
		pospTransInfo.setSysseqno(null);
		// 设置宣称费率
		pospTransInfo.setTransfee2(null);
		// 设置通道费率
		pospTransInfo.setTransfee4(null);
		// 设置实际佣金
		pospTransInfo.setTransfee1(null);
		// 设置消费冲正原因
		pospTransInfo.setReason(null);
		// 设置说明
		pospTransInfo.setRemark(pmsAppTransInfo.getTradetype() + "  金额：" + pmsAppTransInfo.getFactamount());
		// 设置SIM卡
		pospTransInfo.setSimId(null);
		// 设置 TAC
		pospTransInfo.setTac(null);
		// 设置银行编码
		pospTransInfo.setBnkCd(null);
		// 设置平台流水奥 这里默认设置第三方订单号
		pospTransInfo.setPospsn(pmsAppTransInfo.getPortorderid());
		// 设置卡有效期
		pospTransInfo.setCardvaliddate(null);
		// 设置通道pos终端号
		pospTransInfo.setBuspos(null);
		// 设置pos平台交易吗
		pospTransInfo.setPospservicecode(null);
		// 设置冲正流水
		pospTransInfo.setCancelflag(null);
		// 设置商户号
		pospTransInfo.setMerchantcode(pmsAppTransInfo.getMercid());
		// 设置补录时记录上传的终端机流水号
		pospTransInfo.setTerminalsn(null);
		// 设置交易上送帐期
		pospTransInfo.setSenddate(new Date());
		// 服务网点PIN码
		pospTransInfo.setCounterpin(null);
		// 设置渠道号 03：手机
		pospTransInfo.setChannelno("03");
		// 设置银行名称
		pospTransInfo.setBnkNm(null);
		// 设置posid
		pospTransInfo.setPosid(null);
		// 设置交易码 默认都为消费业务
		pospTransInfo.setTranscode("000000");
		// 设置交易安全控制信息
		pospTransInfo.setTranssecuritycontrol(null);
		// 设置卡类型
		pospTransInfo.setCrdTyp(null);
		// 设置卡号
		pospTransInfo.setCardno(null);
		// 设置真正的交易类型 交易码 +交易类型+支付方式
		pospTransInfo
				.setSearchTransCode("000000" + pmsAppTransInfo.getTradetypecode() + pmsAppTransInfo.getPaymentcode());
		// 设置pos交易日期
		pospTransInfo.setTransdate(UtilDate.getDate());
		// 设置pos交易时间
		pospTransInfo.setTranstime(UtilDate.getDateTime());
		// 设置批量结算结果标志
		pospTransInfo.setSettlementflag(null);
		// 设置最近批结算ID
		pospTransInfo.setSettlementid(null);
		// 设置授权码
		pospTransInfo.setAuthoritycode(null);
		// 设置是否自清 默认自清
		pospTransInfo.setIsClearSelf(null);
		// 设置交易响应标志 00-成功
		pospTransInfo.setResponsecode(null);
		/*
		 * if(pmsAppTransInfo.getStatus().equals("0")){
		 * pospTransInfo.setResponsecode("00"); }else{
		 * pospTransInfo.setResponsecode(null); }
		 */
		// 设置订单id
		pospTransInfo.setOrderId(pmsAppTransInfo.getOrderid());
		// 设置通道商户编码 商户编码不设置
		pospTransInfo.setBusinfo(null);
		// 设置附加费用
		pospTransInfo.setAddfee(null);
		// 设置刷卡费率 当前处理为调用第三方处理，刷卡费率不设置
		pospTransInfo.setPremiumrate(null);
		// 设置原始交易记录报文id
		pospTransInfo.setPfmtid(null);
		// 服务网点输入方式
		pospTransInfo.setInputtype(null);
		// 0-脱机POS上送流水，1-联机消费流水
		pospTransInfo.setTransstatus(null);
		// 设置基站信息
		pospTransInfo.setStationInfo(null);
		// 设置交易时间间隔 这里先不处理，没有发现需要用到的地方
		pospTransInfo.setInterVal(null);
		// 设置关联路由id
		pospTransInfo.setRouteid(null);
		// 设置交易消息类型 交易类型+支付方式
		pospTransInfo.setMsgtype(pmsAppTransInfo.getTradetypecode() + pmsAppTransInfo.getPaymentcode());
		// 设置发生额
		pospTransInfo.setTransamt(new BigDecimal(pmsAppTransInfo.getFactamount()));
		// 设置终端号
		pospTransInfo.setPosterminalid(null);
		// 设置操作员id
		pospTransInfo.setOperid(null);
		// 设置POS服务平台代码
		pospTransInfo.setPospid(null);
		// 设置货币代码
		pospTransInfo.setCurrencycode(null);
		// 结算日期
		pospTransInfo.setBalancedate(null);
		// PSAM卡号
		pospTransInfo.setPsamno(null);
		// 个人标识码
		pospTransInfo.setPersonalid(null);
		// 卡号
		pospTransInfo.setCrdNm(null);
		// 设置冲正标志 0-正常交易，1-冲正交易，2-被冲正交易
		pospTransInfo.setCancelflag(0);
		// 设置冻结状态
		pospTransInfo.setFreezeState(null);
		// 设置终端序列号
		pospTransInfo.setPossn(null);
		// 设置服务网点条件码
		pospTransInfo.setConuterconditioncode(null);
		// 是否App交易
		pospTransInfo.setIsapp(1);
		// 设置支付方式
		pospTransInfo.setPaymentType(pmsAppTransInfo.getPaymentcode());
		// 设置批次号
		pospTransInfo.setBatno(null);
		// O单编号
		pospTransInfo.setoAgentNo(pmsAppTransInfo.getoAgentNo());

		return pospTransInfo;
	}

	/**
	 * 定时任务用
	 * 
	 * @Description
	 * @author Administrator
	 * @param pmsAppTransInfo
	 * @throws Exception
	 */
	public void updateOrderStatusByOrder(PmsAppTransInfo pmsAppTransInfo) throws Exception {
		// 查询上游商户号和密钥
		PmsBusinessPos busInfo = selectKey(pmsAppTransInfo.getMercid());
		// 请求交易map
		String key = busInfo.getKek();// Constant.BRCB_KEY;
		String service_type = Constant.BRCB_SERVICE_TYPE_ORDERQUERY;
		String appid = "";
		String mch_id = "C" + busInfo.getBusinessnum();// Constant.BRCB_MCH_ID;
		String transaction_id = "";
		String out_trade_no = pmsAppTransInfo.getOrderid();
		String nonce_str = RandomUtil.randomUUID();
		WechatOrderQueryRequest orderQueryRequest = new WechatOrderQueryRequest(key, service_type, mch_id, out_trade_no,
				nonce_str, log);
		System.out.println("orderQueryRequest=" + orderQueryRequest);
		// 请求
		WechatOrderQueryResponse orderQueryResponse = doOrderQuery(orderQueryRequest, log);

		if (orderQueryResponse != null) {

			PospTransInfo pospTranInfo = pospTransInfoDAO.searchByOrderId(pmsAppTransInfo.getOrderid());
			System.out.println("pospTranInfo:" + pospTranInfo);
			String tradeState = orderQueryResponse.getTrade_state();
			switch (tradeState) {
			case "SUCCESS":
				log.info("交易成功");
				updateOrderSuccess(pmsAppTransInfo, orderQueryResponse, pospTranInfo);
				break;
			case "PAYERROR":
				log.info("支付失败");
				updateOrderFail(pmsAppTransInfo, pospTranInfo);
			case "USERPAYING":
				log.info("交易查询到商户  等待付款");
				updateOrderWaitPay(pmsAppTransInfo, orderQueryResponse);
				break;
			case "CLOSED":
				log.info("已关闭");
				updateOrderFail(pmsAppTransInfo, pospTranInfo);
			case "NOTPAY":
				log.info("未支付");
				updateOrderFail(pmsAppTransInfo, pospTranInfo);
			case "REFUND":
				log.info("转入退款");
			default:
				log.info("其他某种错误！--FIAL");
				updateOrderFail(pmsAppTransInfo, pospTranInfo);
				break;
			}

		}

	}

	/**
	 * 
	 * @Description 等待支付
	 * @author Administrator
	 * @param pmsAppTransInfo
	 * @param transResult
	 * @param pospTranInfo
	 */
	private void updateOrderWaitPay(PmsAppTransInfo pmsAppTransInfo,
			WechatOrderQueryResponse wechatOrderQueryResponse) {
		log.info("交易查询到商户  等待付款");
		pmsAppTransInfo.setStatus("2");
		log.info("查询原始订单表");
		log.info("修改原始订单");
		try {
			pmsAppTransInfoDao.update(pmsAppTransInfo);
		} catch (Exception e) {
			log.error("修改订单为失败", e);
		}
	}

	/**
	 * 
	 * @Description 修改订单状态为失败
	 * @author Administrator
	 * @param pmsAppTransInfo
	 *            订单
	 */
	private void updateOrderFail(PmsAppTransInfo pmsAppTransInfo, PospTransInfo pospTranInfo) {
		log.info("修改订单为失败");
		pmsAppTransInfo.setFinishtime(UtilDate.getOrderNum());
		pmsAppTransInfo.setStatus("1");
		pospTranInfo.setResponsecode("1");
		try {
			// if (!UtilDate.isInDefiMinit(pmsAppTransInfo.getTradetime(), 20))
			// {
			log.info("订单在20分未完成支付  修改为失败");
			pmsAppTransInfoDao.update(pmsAppTransInfo);
			pospTransInfoDAO.update(pospTranInfo);
			// }
		} catch (Exception e) {
			log.error("修改订单为失败", e);
		}
	}

	/**
	 * 修改订单为成功状态
	 * 
	 * @Description
	 * @author Administrator
	 * @param pmsAppTransInfo
	 *            订单
	 * @param transResult
	 *            上游查询返回数据
	 * @param pospTranInfo
	 *            流水记录
	 * @throws Exception
	 */
	private void updateOrderSuccess(PmsAppTransInfo pmsAppTransInfo, WechatOrderQueryResponse orderQueryResponse,
			PospTransInfo pospTranInfo) throws Exception {

		String respCode = (String) orderQueryResponse.getReturn_code();
		// String buyerUser =
		// (String)orderQueryResponse.get(PufaFieldDefine.PF_REQ_BODY_BUYER_USER);

		if ("SUCCESS".equals(respCode)) {
			log.info("修改订单为成功");
			pmsAppTransInfo.setFinishtime(UtilDate.getOrderNum());
			pmsAppTransInfo.setStatus("0");
			pospTranInfo.setResponsecode("00");
			log.info("修改流水状态响应码" + pospTranInfo);
			try {
				pospTransInfoDAO.update(pospTranInfo);
				pmsAppTransInfoDao.update(pmsAppTransInfo);
			} catch (Exception e) {
				log.error("修改订单为失败", e);
			}
		} else {
			updateOrderWaitPay(pmsAppTransInfo, orderQueryResponse);
		}
	}

	/**
	 * 将传入的对象封装成JSON字符串,并进行加密
	 * 
	 * @param obj
	 * @return
	 */
	public String createJsonString(Object obj) throws Exception {
		return gson.toJson(obj);
	}

	public void otherInvoke(WechatWebPayResponse result) throws Exception {

		log.info("**************进入修改方法*************************");
		// 流水表transOrderId
		String transOrderId = result.getOut_trade_no();
		log.info("商户号:" + transOrderId);
		// 流水信息
		PospTransInfo pospTransInfo = pospTransInfoDAO.searchBytransOrderId(transOrderId);
		log.info("流水表信息：" + pospTransInfo);
		// 订单信息
		PmsAppTransInfo pmsAppTransInfo = pmsAppTransInfoDao.searchOrderInfo(pospTransInfo.getOrderId());
		log.info("订单表信息：" + pmsAppTransInfo);
		// 查询结果成功
		if ("SUCCESS".equals(result.getTrade_state())) {
			// 支付成功
			pmsAppTransInfo.setStatus(OrderStatusEnum.paySuccess.getStatus());
			pmsAppTransInfo.setThirdPartResultCode("0");
			pmsAppTransInfo.setFinishtime(UtilDate.getDateFormatter());
			// 修改订单
			int updateAppTrans = pmsAppTransInfoDao.update(pmsAppTransInfo);
			if (updateAppTrans == 1) {
				// log.info("修改余额");
				// 修改余额
				log.info("订单表信息：" + pmsAppTransInfo);
				// updateMerchantBanlance(pmsAppTransInfo);
				// 更新流水表
				pospTransInfo.setResponsecode("00");
				pospTransInfo.setPospsn(result.getTransaction_id());
				log.info("更新流水");
				log.info("流水表信息：" + pospTransInfo);
				pospTransInfoDAO.updateByOrderId(pospTransInfo);
			}
		} else if ("PAYERROR".equals(result.getTrade_state())) {
			// 支付失败
			pmsAppTransInfo.setStatus(OrderStatusEnum.payFail.getStatus());
			pmsAppTransInfo.setThirdPartResultCode("1");
			pmsAppTransInfo.setFinishtime(UtilDate.getDateFormatter());
			// 修改订单
			int updateAppTrans = pmsAppTransInfoDao.update(pmsAppTransInfo);
			if (updateAppTrans == 1) {
				// 更新流水表
				pospTransInfo.setResponsecode("02");
				pospTransInfo.setPospsn(result.getTransaction_id());
				log.info("更新流水");
				log.info("流水表信息：" + pospTransInfo);
				pospTransInfoDAO.updateByOrderId(pospTransInfo);
			}
		} else if ("REFUND".equals(result.getTrade_state())) {
			// 转入退款
			pmsAppTransInfo.setStatus(OrderStatusEnum.returnMoneySuccess.getStatus());
			pmsAppTransInfo.setThirdPartResultCode("3");
			pmsAppTransInfo.setFinishtime(UtilDate.getDateFormatter());
			// 修改订单
			int updateAppTrans = pmsAppTransInfoDao.update(pmsAppTransInfo);
			if (updateAppTrans == 1) {
				// 更新流水表
				pospTransInfo.setResponsecode("02");
				pospTransInfo.setPospsn(result.getTransaction_id());
				log.info("更新流水");
				log.info("流水表信息：" + pospTransInfo);
				pospTransInfoDAO.updateByOrderId(pospTransInfo);
			}
		} else if ("NOTPAY".equals(result.getTrade_state())) {
			// 等待支付
			pmsAppTransInfo.setStatus(OrderStatusEnum.waitingClientPay.getStatus());
			pmsAppTransInfo.setThirdPartResultCode("2");
			pmsAppTransInfo.setFinishtime(UtilDate.getDateFormatter());
			// 修改订单
			int updateAppTrans = pmsAppTransInfoDao.update(pmsAppTransInfo);
			if (updateAppTrans == 1) {
				// 更新流水表
				pospTransInfo.setResponsecode("20");
				pospTransInfo.setPospsn(result.getTransaction_id());
				log.info("更新流水");
				log.info("流水表信息：" + pospTransInfo);
				pospTransInfoDAO.updateByOrderId(pospTransInfo);
			}
		}
	}

	@Override
	public OriginalOrderInfo getOriginOrderInfo(String tranId) throws Exception {
		OriginalOrderInfo original = null;
		// 查询流水信息
		PospTransInfo transInfo = pospTransInfoDAO.searchBytransOrderId(tranId);
		String oderId = transInfo.getOrderId();
		log.info("根据上送订单号  查询商户上送原始信息");
		original = originalDao.getOriginalOrderInfoByOrderid(oderId);
		return original;
	}


	public ChannleMerchantConfigKey getChannelConfigKey(String merchantId) throws Exception {
		log.info("获取商户密钥信息");
		return cmckeyDao.get(merchantId);
	}

	/**
	 * 支付宝退款接口
	 * 
	 * @param obj
	 * @return
	 */
	public Map<String, Object> alipayRefund(AlipayParamRequest alipayParamRequest, Map<String, Object> result,
			PmsBusinessPos busInfo) throws Exception {
		log.info("下游上送的数据:"+alipayParamRequest);
		String merchantId="";
		//PmsBusinessPos busInfo = selectKey(merchantId);
		String key = busInfo.getKek(); // Constant.BRCB_KEY;
		String service_type = "ALIPAY_REFUND";
		String mch_id = "C" + busInfo.getBusinessnum();
		String transaction_id=alipayParamRequest.getTransaction_id();
		String out_trade_no = alipayParamRequest.getOut_trade_no();
		int total_fee =0;
		String subject = "";
		String body = "";
		String time_start = "";
		String time_expire = "";
		String op_user_id = "";
		String device_info = "";
		String notify_url = "";
		String nonce_str = alipayParamRequest.getNonce_str();
		String store_id = "";
		String detail="";
		String spbill_create_ip="";
		String auth_code = "";
		String scene = "";
		String out_refund_no = alipayParamRequest.getOut_trade_no();
		String limit_pay="";
		String refund_fee = "1000";
		String callback_url="";
		String refund_reason="";
		String out_request_no="";
		String attach = "abc";
		AlipayParamRequest alipay=new AlipayParamRequest(key, service_type,mch_id,merchantId,out_trade_no,total_fee, subject,body, time_start,time_expire, device_info, notify_url, nonce_str, attach,
				 op_user_id,store_id,limit_pay,auth_code,detail,scene,spbill_create_ip,callback_url,transaction_id,out_refund_no,refund_fee,refund_reason,out_request_no, log);
		String sign=alipay.getSign();
		AlipayParamResponse microResponse=new AlipayParamResponse();
		microResponse = alipayScan(alipay, log);
		log.info("上游返回的参数:" + microResponse);
		microResponse.setMerchantId(alipayParamRequest.getMch_id());
		result=BeanToMapUtil.convertBean(microResponse);
		return result;
	}

	/**
	 * 支付宝撤销接口
	 * 
	 * @param obj
	 * @return
	 */
	public Map<String, Object> alipayReverseorder(AlipayParamRequest alipayParamRequest, Map<String, Object> result,
			PmsBusinessPos busInfo) throws Exception {
		log.info("下游上送的数据:"+alipayParamRequest);
		String merchantId="";
		//PmsBusinessPos busInfo = selectKey(merchantId);
		String key = busInfo.getKek(); // Constant.BRCB_KEY;
		String service_type = "ALIPAY_REFUND";
		String mch_id = "C" + busInfo.getBusinessnum();
		String transaction_id=alipayParamRequest.getTransaction_id();
		String out_trade_no = alipayParamRequest.getOut_trade_no();
		int total_fee =0;
		String subject = "";
		String body = "";
		String time_start = "";
		String time_expire = "";
		String op_user_id = "";
		String device_info = "";
		String notify_url = "";
		String nonce_str = alipayParamRequest.getNonce_str();
		String store_id = "";
		String detail="";
		String spbill_create_ip="";
		String auth_code = "";
		String scene = "";
		String out_refund_no = alipayParamRequest.getOut_trade_no();
		String limit_pay="";
		String refund_fee = "1000";
		String callback_url="";
		String refund_reason="";
		String out_request_no="";
		String attach = "abc";
		AlipayParamRequest alipay=new AlipayParamRequest(key, service_type,mch_id,merchantId,out_trade_no,total_fee, subject,body, time_start,time_expire, device_info, notify_url, nonce_str, attach,
				 op_user_id,store_id,limit_pay,auth_code,detail,scene,spbill_create_ip,callback_url,transaction_id,out_refund_no,refund_fee,refund_reason,out_request_no, log);
		String sign=alipay.getSign();
		AlipayParamResponse microResponse=new AlipayParamResponse();
		microResponse = alipayScan(alipay, log);
		log.info("上游返回的参数:" + microResponse);
		microResponse.setMerchantId(alipayParamRequest.getMch_id());
		result=BeanToMapUtil.convertBean(microResponse);
		return result;
	}

	@Override
	public Map<String, Object> wechatCloseorder(AlipayParamRequest alipayParamRequest, Map<String, Object> result,
			PmsBusinessPos busInfo) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Map<String, Object> wechatRefund(AlipayParamRequest alipayParamRequest, Map<String, Object> result,
			PmsBusinessPos busInfo) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Map<String, Object> wechatReverseorder(AlipayParamRequest alipayParamRequest, Map<String, Object> result,
			PmsBusinessPos busInfo) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}



}
