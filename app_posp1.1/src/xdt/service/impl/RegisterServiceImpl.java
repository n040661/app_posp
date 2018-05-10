package xdt.service.impl;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.StringReader;
import java.math.BigDecimal;
import java.net.URLEncoder;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONObject;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import xdt.dao.ChannleMerchantConfigKeyDao;
import xdt.dao.IAppRateConfigDao;
import xdt.dao.IPmsAppAmountAndRateConfigDao;
import xdt.dao.IPmsAppMerchantPayChannelDao;
import xdt.dao.IPmsBusinessInfoDao;
import xdt.dao.IPmsBusinessPosDao;
import xdt.dao.IPmsMerchantInfoDao;
import xdt.dao.IPospRouteInfoDAO;
import xdt.dto.nbs.register.HttpclientJsonRespFileUtil;
import xdt.dto.nbs.register.Register;
import xdt.dto.nbs.register.RegisterResponse;
import xdt.mapper.PmsWeixinMerchartInfoMapper;
import xdt.model.AppRateConfig;
import xdt.model.ChannleMerchantConfigKey;
import xdt.model.PmsAppAmountAndRateConfig;
import xdt.model.PmsAppMerchantPayChannel;
import xdt.model.PmsBusinessInfo;
import xdt.model.PmsBusinessPos;
import xdt.model.PmsMerchantInfo;
import xdt.model.PmsWeixinMerchartInfo;
import xdt.model.PospRouteInfo;
import xdt.model.TradeTypeModel;
import xdt.quickpay.nbs.common.constant.Constant;
import xdt.quickpay.nbs.common.util.HttpClientJSONUtil;
import xdt.quickpay.nbs.common.util.JSONUtil;
import xdt.quickpay.nbs.common.util.RandomUtil;
import xdt.quickpay.nbs.common.util.StringUtil;
import xdt.service.IRegisterService;
import xdt.service.IWechatService;
import xdt.service.PmsWeixinMerchartInfoService;
import xdt.util.PaymentCodeEnum;
import xdt.util.TradeTypeEnum;
import xdt.util.UtilDate;

@Service
public class RegisterServiceImpl extends BaseServiceImpl implements IRegisterService {
	private Logger log = LoggerFactory.getLogger(this.getClass());

	@Resource
	private PmsWeixinMerchartInfoMapper weixinMerchMapper;
	@Resource
	private IPmsBusinessInfoDao pmsBusinessInfoDaoImpl;
	@Resource
	private IPmsBusinessPosDao pmsBusinessPosDaoImpl;
	@Resource
	private IPmsMerchantInfoDao pmsMerchantInfoDaoImpl;
	@Resource
	private IPospRouteInfoDAO pospRouteInfoDAO;
	@Resource
	private IWechatService WechatServiceImpl;
	@Resource
	private IPmsMerchantInfoDao pmsMerchantInfoDao; // 商户信息服务层
	@Resource
	private ChannleMerchantConfigKeyDao channleMerchantConfigKeyDao; // 商户信息服务层

	@Resource
	private IPmsAppAmountAndRateConfigDao pmsAppAmountAndRateConfigDao; // 商户费率信息

	@Resource
	private IAppRateConfigDao appRateConfigDao; // 通道费率信息
	@Resource
	private IPmsAppMerchantPayChannelDao pmsAppMerchantPayChannelDao; // 通道支付类型信息

	/**
	 * 微信商户信息
	 */
	@Resource
	public PmsWeixinMerchartInfoService weixinService;

	@Override
	public RegisterResponse inster(Register register) throws Exception {
		log.info("下游注册上送的参数" + register);
		RegisterResponse response = new RegisterResponse();
		register.setServiceType("CUSTOMER_ENTER");
		register.setAgentNum("A148473699892010919");
		register.setApiKey("fbf7f057a90346d1a834b470ddb5dd15");
		// Map<String, String> retMap = new HashMap<String, String>();
		PmsMerchantInfo merchantinfo = new PmsMerchantInfo();
		merchantinfo.setMercId(register.getOutMchId());
		// o单编号
		String oAgentNo = "";
		// 查询当前商户信息
		List<PmsMerchantInfo> merchantList = pmsMerchantInfoDao.searchList(merchantinfo);
		if (!(merchantList.size() == 0 || merchantList.isEmpty())) {

			merchantinfo = merchantList.get(0);
			// merchantinfo.setCustomertype("3");

			oAgentNo = merchantinfo.getoAgentNo();//

			if (StringUtils.isBlank(oAgentNo)) {
				// 如果没有欧单编号，直接返回错误
				// 如果没有欧单编号，直接返回错误
				log.error("参数错误!");
				response.setReturn_code("16");
				response.setReturn_msg("参数错误,没有欧单编号");
				return response;
			}
			// 判断是否为正式商户
			if ("60".equals(merchantinfo.getMercSts())) {

				PmsMerchantInfo merchant = new PmsMerchantInfo();
				UtilDate r = new UtilDate();
				String num = r.Rands(1, 3);
				String mercid = "101" + register.getBankCard().substring(0, 5) + num;
				merchant.setMercId(mercid);
				// 查询当前商户信息
				List<PmsMerchantInfo> merchantList1 = pmsMerchantInfoDao.searchList(merchant);
				if ((merchantList1.size() == 0 || merchantList1.isEmpty())) {

					PmsMerchantInfo pmsMerchantInfo = new PmsMerchantInfo();
					pmsMerchantInfo.setMainbusiness(register.getBusinessType());
					pmsMerchantInfo.setMercId(mercid);
					pmsMerchantInfo.setSellerNo(merchantinfo.getAgentNumber());
					pmsMerchantInfo.setMercName(register.getCustomerName());
					pmsMerchantInfo.setCustomertype(register.getCustomerType());
					pmsMerchantInfo.setMccCd(register.getBusinessType());
					pmsMerchantInfo.setBusinessname(register.getBusinessName());
					pmsMerchantInfo.setCrpIdNo(register.getLegalId());
					pmsMerchantInfo.setCrpNm(register.getLegalName());
					pmsMerchantInfo.setMobilephone(register.getContactPhone());
					pmsMerchantInfo.setEmail(register.getContactEmail());
					pmsMerchantInfo.setPhone(register.getServicePhone());
					pmsMerchantInfo.setBusAddr(register.getAddress());
					pmsMerchantInfo.setBusinessarea(register.getProvinceName() + "-" + register.getCityName() + "-"
							+ register.getDistrictName());
					pmsMerchantInfo.setTaxCertId(register.getLicenseNo());
					pmsMerchantInfo.setMercSts("60");
					pmsMerchantInfo.setPayChannel(register.getPayChannel());
					// 根据商户费率查询商户的费率值
					DecimalFormat df = new DecimalFormat("0.0000");
					Double rate = Double.parseDouble(register.getRate().toString()) / 100;
		
					log.info("商户上传的费率为:" + df.format(rate));
					AppRateConfig appRateConfig = new AppRateConfig();
					appRateConfig.setRate(df.format(rate));
					appRateConfig.setRemark("快捷");
					AppRateConfig arc = appRateConfigDao.getThirdpartRateValue(appRateConfig);
					if (arc != null) {
						log.info("商户费率值为:" + arc.getRateType());
						pmsMerchantInfo.setQuickRateType(arc.getRateType());
					}
					pmsMerchantInfo.setT0Status(register.getT0Status());
					pmsMerchantInfo.setSettleRate(register.getSettleRate());
					pmsMerchantInfo.setFixedFee(register.getFixedFee());
					pmsMerchantInfo.setIsCapped(register.getIsCapped());
					pmsMerchantInfo.setSettleMode(register.getSettleMode());
					pmsMerchantInfo.setUpperFee(register.getUpperFee() + "");
					pmsMerchantInfo.setAccountType(register.getAccountType());
					pmsMerchantInfo.setType("0");
					pmsMerchantInfo.setSettlementname(register.getAccountName());
					pmsMerchantInfo.setClrMerc(register.getBankCard());
					pmsMerchantInfo.setBankname(register.getBankName());
					pmsMerchantInfo.setBankProvince(register.getProvince() + "-" + register.getBankAddress());
					pmsMerchantInfo.setBankCity(register.getCity());
					pmsMerchantInfo.setBanksysnumber(register.getAlliedBankNo());
					pmsMerchantInfo.setCreationName("100333");
					pmsMerchantInfo.setAgentNumber(merchantinfo.getAgentNumber());
					pmsMerchantInfo.setoAgentNo("100333");
					pmsMerchantInfo.setTruemerdate(new SimpleDateFormat().format(new Date()));
					pmsMerchantInfo.setOrgId(register.getMerchantNumber());
					pmsMerchantInfo.setCounter(merchantinfo.getCounter());
					if ((pmsMerchantInfo.getClrMerc() != null) && (pmsMerchantInfo.getClrMerc() != null)) {
						pmsMerchantInfo.setClrMerc(pmsMerchantInfo.getClrMerc().replaceFirst(" ", ""));
					}
					SimpleDateFormat myFmt = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
					pmsMerchantInfo.setCreationdate(myFmt.format(new Date()).toString());

					int param1 = pmsMerchantInfoDaoImpl.insert(pmsMerchantInfo);
					if (param1 == 1) {
						// 注册小微商户信息
						PmsWeixinMerchartInfo entity = new PmsWeixinMerchartInfo();
						entity.setMerchartId(register.getOutMchId());
						entity.setAccount(mercid);
						// entity.setCardType(weixin.getCardType());
						// entity.setPassword(weixin.getPassword());
						// entity.setCertCorrect(weixin.getCertCorrect());
						entity.setPmsBankNo(register.getAlliedBankNo());
						// entity.setCardOpposite(weixin.getCardOpposite());
						entity.setCertNo(register.getLegalId());
						entity.setMobile(register.getContactPhone());
						// entity.setCertMeet(weixin.getCertMeet());
						entity.setCardNo(register.getBankCard());
						entity.setRealName(register.getAccountName());
						// entity.setCardCorrect(weixin.getCardCorrect());
						// entity.setCertType(weixin.getCertType());
						// entity.setCertOpposite(weixin.getCertOpposite());
						entity.setMerchartName(register.getCustomerName());
						entity.setWxT1Fee(df.format(rate));
						entity.setAlipayT1Fee(df.format(rate));
						if(register.getSettleRate()!=null)
						{
							Double settlerate = Double.parseDouble(register.getSettleRate().toString()) / 100;
							entity.setWxT0Fee(df.format(settlerate));
							entity.setAlipayT0Fee(df.format(settlerate));
						}
						entity.setoAgentNo("100333");
						weixinService.updateRegister(entity);
						log.info("******************####################注册微信商户信息");
						Register registers = new Register("fbf7f057a90346d1a834b470ddb5dd15", register.getServiceType(),
								register.getAgentNum(), register.getMerchantNumber(), register.getApiKey(), mercid,
								register.getAppId(), register.getCustomerType(), register.getBusinessType(),
								register.getBusinessName(), register.getLegalId(), register.getLegalName(),
								register.getContact(), register.getContactPhone(), register.getContactEmail(),
								register.getServicePhone(), register.getCustomerName(), register.getAddress(),
								register.getProvinceName(), register.getCityName(), register.getDistrictName(),
								register.getLicenseNo(), register.getPayChannel(), register.getRate(),
								register.getT0Status(), register.getSettleRate(), register.getFixedFee(),
								register.getIsCapped(), register.getSettleMode(), register.getUpperFee(),
								register.getAccountType(), register.getAccountName(), register.getBankCard(),
								register.getBankName(), register.getProvince(), register.getCity(),
								register.getBankAddress(), register.getAlliedBankNo(), register.getRightID(),
								register.getReservedID(), register.getIDWithHand(), register.getRightBankCard(),
								register.getLicenseImage(), register.getDoorHeadImage(), register.getAccountLicence(),
								register.getQueryType(), register.getCustomerNum(), register.getOrderDate(),
								register.getCheckDate(), log);

						log.info(registers + "");
						String result = doScanned(registers, log);

						log.info("北农商上游返回的信息为:" + result);
						JSONObject ob = JSONObject.fromObject(result);
						log.info("封装之后的数据:{}", ob);
						Iterator it = ob.keys();
						while (it.hasNext()) {
							String key1 = (String) it.next();
							if (key1.equals("return_code")) {
								String value = ob.getString(key1);
								log.info("响应状态:" + "\t" + value);
								response.setReturn_code(value);
								if (value.equals("000000")) {
									// 商户注册成功后 默认分配费率
									PmsAppAmountAndRateConfig pmsAppAmountAndRateConfig = null;
									for (TradeTypeModel typeModel : TradeTypeEnum.getTradeTypeList()) {

										pmsAppAmountAndRateConfig = new PmsAppAmountAndRateConfig();
										// 设置欧单编号
										pmsAppAmountAndRateConfig.setoAgentNo(pmsMerchantInfo.getoAgentNo());
										pmsAppAmountAndRateConfig.setMercId(pmsMerchantInfo.getMercId());
										pmsAppAmountAndRateConfig.setBusinesscode(typeModel.getTradeTypeCode());
										Integer min = 2 * 100;
										Integer max = 20000 * 100;
										pmsAppAmountAndRateConfig.setMinAmount(min.toString());
										pmsAppAmountAndRateConfig.setMaxAmount(max.toString());
										pmsAppAmountAndRateConfig.setAccountTime("一个工作日内");
										if (typeModel.getTradeTypeCode()
												.equals(TradeTypeEnum.drawMoney.getTypeCode())) {

											Integer drawMin;

											// 只有提现业务才有操作次数
											pmsAppAmountAndRateConfig.setNumberoftimes("3");
											// 提现业务 最小金额为1000元
											drawMin = 100 * 100;

											pmsAppAmountAndRateConfig.setMinAmount(drawMin.toString());
											pmsAppAmountAndRateConfig.setRatetype("6");
										} else if (typeModel.getTradeTypeCode()
												.equals(TradeTypeEnum.transeMoney.getTypeCode())
												|| typeModel.getTradeTypeCode()
														.equals(TradeTypeEnum.creditCardRePay.getTypeCode())) {
											// 转账汇款 信用卡还款 最小金额为10元 最大金额为 50000元
											Integer cardMin = 10 * 100;
											Integer cardmax = 50000 * 100;
											pmsAppAmountAndRateConfig.setMinAmount(cardMin.toString());
											pmsAppAmountAndRateConfig.setMaxAmount(cardmax.toString());
											pmsAppAmountAndRateConfig.setRatetype("1");
										} else if (typeModel.getTradeTypeCode()
												.equals(TradeTypeEnum.merchantCollect.getTypeCode())) {
											// 商户收款
											Integer cardMin = 10 * 100;
											Integer cardmax = 20000 * 100;
											pmsAppAmountAndRateConfig.setMinAmount(cardMin.toString());
											pmsAppAmountAndRateConfig.setMaxAmount(cardmax.toString());
											pmsAppAmountAndRateConfig.setRatetype("1");
										} else {
											pmsAppAmountAndRateConfig.setRatetype("1");
										}

										pmsAppAmountAndRateConfig.setStatus("1");
										pmsAppAmountAndRateConfig.setAccountType("1");
										pmsAppAmountAndRateConfig.setDescription(typeModel.getTradeTypeName());
										pmsAppAmountAndRateConfig.setCreateTime(UtilDate.getDateFormatter());
										pmsAppAmountAndRateConfig.setModifyTime("");
										pmsAppAmountAndRateConfig.setModifyUser("");
										pmsAppAmountAndRateConfig.setQuickRateType(pmsMerchantInfo.getQuickRateType());
										pmsAppAmountAndRateConfigDao.insert(pmsAppAmountAndRateConfig);
									}
									// 添加商户支付通道表数

									PmsAppMerchantPayChannel pmsAppMerchantPayChannel = null;

									for (TradeTypeModel paymentCodeEnumTypeModel : PaymentCodeEnum.getTradeTypeList()) {
										pmsAppMerchantPayChannel = new PmsAppMerchantPayChannel();
										pmsAppMerchantPayChannel.setMercId(pmsMerchantInfo.getMercId());
										pmsAppMerchantPayChannel.setoAgentNo(pmsMerchantInfo.getoAgentNo());
										pmsAppMerchantPayChannel.setBusinesscode("empty");// 暂时为空
										pmsAppMerchantPayChannel.setCreatetime(UtilDate.getDateFormatter());
										pmsAppMerchantPayChannel
												.setPaymentcode(paymentCodeEnumTypeModel.getTradeTypeCode());
										pmsAppMerchantPayChannel.setStatus("0");// 状态
																				// 0
																				// 有效
																				// 1
																				// 无效
										pmsAppMerchantPayChannel.setModifytime("");
										pmsAppMerchantPayChannel.setModifyuser("");
										pmsAppMerchantPayChannel
												.setDescribe(paymentCodeEnumTypeModel.getTradeTypeName());
										pmsAppMerchantPayChannelDao.insert(pmsAppMerchantPayChannel);
									}
									// 生成商户密钥
									ChannleMerchantConfigKey key = new ChannleMerchantConfigKey();
									key.setMercid(mercid);
									key.setChannletype("11");
									key.setCreatetime(myFmt.format(new Date()).toString());
									if (channleMerchantConfigKeyDao.get(key.getMercid()) != null) {
										response.setReturn_code("1001");
										response.setReturn_msg("该商户密钥已经存在！");
										return response;
									} else {
										key.setMerchantkey(UUID.randomUUID().toString().replace("-", ""));
										int number = channleMerchantConfigKeyDao.saveKey(key);
										if (number == 1) {
											ChannleMerchantConfigKey channerKey = channleMerchantConfigKeyDao
													.get(mercid);
											log.info("查询商户密钥信息:" + channerKey);
											String mer_key = channerKey.getMerchantkey();
											response.setApi_key(mer_key);
											if (mer_key != null) {
												log.info("北农商上游返回的信息为:" + result);
												register.setOutMchId(mercid);
												register.setMerchantNumber(mercid);
												insertPmsBusinessPos(result, register, log);
												insertPmsBusinessInfo(result, register, log);
												inserts(result, register, log);
												insertPospRouteInfo(result, register, log);

												response.setCustomer_num(mercid);
											}

										}
									}
								}
							}
							if (key1.equals("return_msg")) {
								String value = ob.getString(key1);
								log.info("响应状态描述:" + "\t" + value);
								response.setReturn_msg(value);
								;
							}
						}
					}

				} else {
					log.info("商户没有进行实名认证，" + merchantinfo.getMercId());
					response.setReturn_code("18");
					response.setReturn_msg("该商户已经存在，请重新录入");
					return response;
				}

			} else {
				// 请求参数为空
				log.info("商户没有进行实名认证，" + merchantinfo.getMercId());
				response.setReturn_code("17");
				response.setReturn_msg("商户没有进行实名认证");
				return response;
			}
		} else {
			log.info("商户没有进行实名认证，" + merchantinfo.getMercId());
			response.setReturn_code("17");
			response.setReturn_msg("商户没有进行实名认证");
			return response;
		}

		return response;
	}

	String doScanned(Register register, Logger log) {
		// step 1 init param
		log.info("请求参数:{}", register);
		String brcbGatewayUrl = "http://brcb.pufubao.net/customer/service";
		log.info("请求地址: {}", brcbGatewayUrl);

		// step 2 send json post
		String sendPost = sendJsonPost(brcbGatewayUrl, register, log);
		if (StringUtil.isBlank(sendPost)) {
			System.out.println("错误");
		}

		return sendPost;
	}

	String sendJsonPost(String url, Register register, Logger log) {
		String sendPost = null;
		long costTimeStart = System.currentTimeMillis();// start
		try {
			sendPost = HttpClientJSONUtil.postJSONUTF8(url, JSONUtil.toJSONString(register.toMap()));
		} catch (Exception e) {
			log.info("请求出错: {}", e);
		}
		long costTimeEnd = System.currentTimeMillis();// end
		long totalTimeCost = costTimeEnd - costTimeStart;// 总耗时
		log.info("请求总耗时：{}ms", totalTimeCost);
		log.info("返回数据: {}", sendPost);
		// System.out.println("是不是字符串呢？"+sendPost);
		return sendPost;
	}

	/**
	 * 向PmsBusinessPos表插入数据
	 * 
	 * @return
	 * @throws Exception
	 */
	@Override
	public void insertPmsBusinessPos(String json, Register register, Logger log) throws Exception {
		JSONObject jsons = JSONObject.fromObject(json);
		log.info("json转换之后的数据:" + jsons);
		PmsBusinessPos pmsBusinessPos = new PmsBusinessPos();
		pmsBusinessPos.setPosnum("bsn00X");
		pmsBusinessPos.setBusinessnum(jsons.getString("customer_num").replace("C", ""));
		pmsBusinessPos.setChannelnum("BNS001");
		pmsBusinessPos.setPersonnum("1003");
		pmsBusinessPos.setIndate(new SimpleDateFormat("yyyy/MM/dd").format(new Date()));
		pmsBusinessPos.setOutdate(new SimpleDateFormat("yyyy/MM/dd").format(new Date()));
		String ss = RandomUtil.getOrderNum();
		pmsBusinessPos.setBatchinno(ss);
		pmsBusinessPos.setBatchoutno(ss);
		pmsBusinessPos.setStatus("2");
		pmsBusinessPos.setPosstatus("1");
		pmsBusinessPos.setKek(jsons.getString("api_key"));
		pmsBusinessPos.setPosopenstatus("2");
		log.info("向路由表里插入数据的信息:{}" + pmsBusinessPos);
		pmsBusinessPosDaoImpl.insert(pmsBusinessPos);
		log.info("添加成功！！");
	}

	/**
	 * 向PmsBusinessInfo表插入数据
	 * 
	 * @return
	 */
	@Override
	public void insertPmsBusinessInfo(String json, Register register, Logger log) {
		JSONObject jsons = JSONObject.fromObject(json);
		PmsBusinessInfo pmsBusinessInfo = new PmsBusinessInfo();
		pmsBusinessInfo.setBusinessNum(jsons.getString("customer_num").replace("C", ""));
		pmsBusinessInfo.setBusinessName(register.getBusinessName());
		pmsBusinessInfo.setType("1");
		pmsBusinessInfo.setTime(new SimpleDateFormat("yyyy/MM/dd").format(new Date()));
		pmsBusinessInfo.setState("1");
		pmsBusinessInfo.setTotalSum(new BigDecimal("50000.00"));
		pmsBusinessInfo.setFailures(new BigDecimal("3"));
		pmsBusinessInfo.setMoneyStart(new BigDecimal("10.00"));
		pmsBusinessInfo.setMoneyEnd(new BigDecimal("50000.00"));
		double bg = register.getRate().divide(new BigDecimal("100"), 2, BigDecimal.ROUND_HALF_UP).doubleValue();
		log.info(bg + "");
		pmsBusinessInfo.setPremiumerate(bg + "");
		pmsBusinessInfo.setChannelId("BNS001");
		pmsBusinessInfo.setCity(register.getProvinceName());
		pmsBusinessInfo.setProvince(register.getCityName());
		pmsBusinessInfo.setPayType("5");
		pmsBusinessInfo.setPayTypeName("微信-支付宝");
		try {
			pmsBusinessInfoDaoImpl.insert(pmsBusinessInfo);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		log.info("添加成功！！");
	}

	@Override
	public void inserts(String json, Register register, Logger log) {
		log.info("上又返回的数据" + json);
		if (json != null) {
			log.info("进来了");
			JSONObject jsons = JSONObject.fromObject(json);
			PmsWeixinMerchartInfo pmsWeixinMerchartInfo = new PmsWeixinMerchartInfo();
			pmsWeixinMerchartInfo.setCustomerNum(jsons.getString("customer_num").replace("C", ""));
			pmsWeixinMerchartInfo.setApiKey(jsons.getString("api_key"));
			pmsWeixinMerchartInfo.setMerchartId(register.getOutMchId());
			weixinMerchMapper.updateByMerchartId(pmsWeixinMerchartInfo);
			log.info("添加成功！！");
		}

	}

	@Override
	public void insertPospRouteInfo(String json, Register register, Logger log) throws Exception {

		PospRouteInfo pospRouteInfo = new PospRouteInfo();
		PmsMerchantInfo info = pmsMerchantInfoDaoImpl.selectMerchantInfoByMercid(register.getOutMchId());
		log.info("info的id：" + info.getId());
		JSONObject jsons = JSONObject.fromObject(json);
		PmsBusinessPos businessPos = pmsBusinessPosDaoImpl
				.selectBusinessposBusinessNum(jsons.getString("customer_num").replace("C", ""));
		log.info("businessPos的id：" + businessPos.getId());
		PmsBusinessInfo pmsBusinessInfo = pmsBusinessInfoDaoImpl
				.selectBusinessInfoBusinessNum(jsons.getString("customer_num").replace("C", ""));
		log.info("pmsBusinessInfo的id：" + pmsBusinessInfo.getId());

		pospRouteInfo.setEffectFrom("00:00:00");
		pospRouteInfo.setEffectTo("23:59:59");
		pospRouteInfo.setPriority(new BigDecimal("8"));
		pospRouteInfo.setRounttype("2");
		pospRouteInfo.setChannelCode("BNS001");
		pospRouteInfo.setStatus(new BigDecimal("1"));
		pospRouteInfo.setOwnerId(new BigDecimal(info.getId()));
		pospRouteInfo.setPosId(new BigDecimal(businessPos.getId()));
		pospRouteInfo.setMerchantId(new BigDecimal(pmsBusinessInfo.getId() + ""));
		pospRouteInfoDAO.insertPospRouteInfo(pospRouteInfo);
		log.info("添加成功！！");
	}

	/**
	 * 商户查询接口
	 */
	@Override
	public String select(Register register, Logger log) {
		String ss = "";
		try {
			PmsBusinessPos busInfo = WechatServiceImpl.selectKey(register.getOutMchId());

			register.setCustomerNum("C" + busInfo.getBusinessnum());
			register.setQueryType("0");
			register.setServiceType(Constant.serviceTypes);
			register.setAgentNum(Constant.agentNum);
			register.setApiKey(Constant.apiKey);
			Register registers = new Register(Constant.apiKey, register.getServiceType(), register.getAgentNum(),
					register.getMerchantNumber(), register.getApiKey(), register.getOutMchId(), register.getAppId(),
					register.getCustomerType(), register.getBusinessType(), register.getBusinessName(),
					register.getLegalId(), register.getLegalName(), register.getContact(), register.getContactPhone(),
					register.getContactEmail(), register.getServicePhone(), register.getCustomerName(),
					register.getAddress(), register.getProvinceName(), register.getCityName(),
					register.getDistrictName(), register.getLicenseNo(), register.getPayChannel(), register.getRate(),
					register.getT0Status(), register.getSettleRate(), register.getFixedFee(), register.getIsCapped(),
					register.getSettleMode(), register.getUpperFee(), register.getAccountType(),
					register.getAccountName(), register.getBankCard(), register.getBankName(), register.getProvince(),
					register.getCity(), register.getBankAddress(), register.getAlliedBankNo(), register.getRightID(),
					register.getReservedID(), register.getIDWithHand(), register.getRightBankCard(),
					register.getLicenseImage(), register.getDoorHeadImage(), register.getAccountLicence(),
					register.getQueryType(), register.getCustomerNum(), register.getOrderDate(),
					register.getCheckDate(), log);

			ss = doScanned(registers, log);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return ss;
	}

	/**
	 * 修改接口
	 * 
	 * @param register
	 * @param log
	 * @return
	 */

	public String update(Register register, Logger log) {
		String ss = "";
		try {
			PmsBusinessPos busInfo = WechatServiceImpl.selectKey(register.getOutMchId());
			PmsMerchantInfo info = pmsMerchantInfoDaoImpl.selectMerchantInfoByMercid(register.getOutMchId());
			register.setCustomerNum("C" + busInfo.getBusinessnum());
			register.setServiceType(Constant.serviceTypee);
			register.setAgentNum(Constant.agentNum);
			register.setApiKey(Constant.apiKey);
			// register.setPayChannel(Constant.payChannel);

			PmsMerchantInfo pmsMerchantInfo = new PmsMerchantInfo();
			pmsMerchantInfo.setId(info.getId());
			if (register.getCustomerType() != null && !register.getCustomerType().equals("")) {
				pmsMerchantInfo.setCustomertype(register.getCustomerType());
			}
			if (register.getBusinessType() != null && !register.getBusinessType().equals("")) {
				pmsMerchantInfo.setMccCd(register.getBusinessType());
			}
			if (register.getBusinessName() != null && !register.getBusinessName().equals("")) {
				pmsMerchantInfo.setBusinessname(register.getBusinessName());
			}
			if (register.getLegalId() != null && !register.getLegalId().equals("")) {
				pmsMerchantInfo.setCrpIdNo(register.getLegalId());
			}
			if (register.getLegalName() != null && !register.getLegalName().equals("")) {
				pmsMerchantInfo.setCrpNm(register.getLegalName());
			}
			if (register.getContactPhone() != null && !register.getContactPhone().equals("")) {
				pmsMerchantInfo.setMobilephone(register.getContactPhone());
			}
			if (register.getContactEmail() != null && !register.getContactEmail().equals("")) {
				pmsMerchantInfo.setEmail(register.getContactEmail());
			}
			if (register.getServicePhone() != null && !register.getServicePhone().equals("")) {
				pmsMerchantInfo.setPhone(register.getServicePhone());
			}
			if (register.getCustomerName() != null && !register.getCustomerName().equals("")) {
				pmsMerchantInfo.setMercName(register.getCustomerName());
			}
			if (register.getAddress() != null && !register.getAddress().equals("")) {
				pmsMerchantInfo.setBusAddr(register.getAddress());
			}
			if (register.getProvinceName() != null && register.getCityName() != null
					&& register.getDistrictName() != null && !register.getProvinceName().equals("")
					&& !register.getCityName().equals("") && !register.getDistrictName().equals("")) {
				pmsMerchantInfo.setBusinessarea(
						register.getProvinceName() + "-" + register.getCityName() + "-" + register.getDistrictName());
			}
			if (register.getLicenseNo() != null && !register.getLicenseNo().equals("")) {
				pmsMerchantInfo.setTaxCertId(register.getLicenseNo());
			}
			if (register.getPayChannel() != null && !register.getPayChannel().equals("")) {
				pmsMerchantInfo.setPayChannel(register.getPayChannel());
			}
			if (register.getT0Status() != null && !register.getT0Status().equals("")) {
				pmsMerchantInfo.setT0Status(register.getT0Status());
			}
			if (register.getSettleRate() != null && !register.getSettleRate().equals("")) {
				pmsMerchantInfo.setSettleRate(register.getSettleRate());
			}
			if (register.getFixedFee() != null && !register.getFixedFee().equals("")) {
				pmsMerchantInfo.setFixedFee(register.getFixedFee());
			}
			if (register.getIsCapped() != null && !register.getIsCapped().equals("")) {
				pmsMerchantInfo.setIsCapped(register.getIsCapped());
			}
			if (register.getSettleMode() != null && !register.getSettleMode().equals("")) {
				pmsMerchantInfo.setSettleMode(register.getSettleMode());
			}
			if (register.getUpperFee() != null && !register.getUpperFee().equals("")) {
				pmsMerchantInfo.setUpperFee(register.getUpperFee() + "");
			}
			if (register.getAccountType() != null && !register.getAccountType().equals("")) {
				pmsMerchantInfo.setAccountType(register.getAccountType());
			}
			if (register.getAccountName() != null && !register.getAccountName().equals("")) {
				pmsMerchantInfo.setSettlementname(register.getAccountName());
			}
			if (register.getBankCard() != null && !register.getBankCard().equals("")) {
				pmsMerchantInfo.setClrMerc(register.getBankCard());
			}
			if (register.getBankName() != null && !register.getBankName().equals("")) {
				pmsMerchantInfo.setBankname(register.getBankName());
			}
			if (register.getProvince() != null && register.getBankAddress() != null
					&& !register.getProvince().equals("") && !register.getBankAddress().equals("")) {
				pmsMerchantInfo.setBankProvince(register.getProvince() + "-" + register.getBankAddress());
			}
			if (register.getCity() != null && !register.getCity().equals("")) {
				pmsMerchantInfo.setBankCity(register.getCity());
			}
			if (register.getAlliedBankNo() != null && !register.getAlliedBankNo().equals("")) {
				pmsMerchantInfo.setBanksysnumber(register.getAlliedBankNo());
			}

			Register registers = new Register(Constant.apiKey, register.getServiceType(), register.getAgentNum(),
					register.getMerchantNumber(), register.getApiKey(), register.getOutMchId(), register.getAppId(),
					register.getCustomerType(), register.getBusinessType(), register.getBusinessName(),
					register.getLegalId(), register.getLegalName(), register.getContact(), register.getContactPhone(),
					register.getContactEmail(), register.getServicePhone(), register.getCustomerName(),
					register.getAddress(), register.getProvinceName(), register.getCityName(),
					register.getDistrictName(), register.getLicenseNo(), register.getPayChannel(), register.getRate(),
					register.getT0Status(), register.getSettleRate(), register.getFixedFee(), register.getIsCapped(),
					register.getSettleMode(), register.getUpperFee(), register.getAccountType(),
					register.getAccountName(), register.getBankCard(), register.getBankName(), register.getProvince(),
					register.getCity(), register.getBankAddress(), register.getAlliedBankNo(), register.getRightID(),
					register.getReservedID(), register.getIDWithHand(), register.getRightBankCard(),
					register.getLicenseImage(), register.getDoorHeadImage(), register.getAccountLicence(),
					register.getQueryType(), register.getCustomerNum(), register.getOrderDate(),
					register.getCheckDate(), log);

			ss = doScanned(registers, log);
			if (JSONObject.fromObject(ss).getString("return_msg").equals("操作成功")) {
				pmsMerchantInfoDaoImpl.merchantUpdateByPmsMerchantInfos(pmsMerchantInfo);
			}

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return ss;
	}

	/**
	 * 对账文件下载
	 */
	public String merchantDownload(Register register, Logger log) {
		String ss = "";
		try {
			// PmsBusinessPos busInfo=
			// WechatServiceImpl.selectKey(register.getOutMchId());
			// register.setCustomerNum("C"+busInfo.getBusinessnum());
			register.setServiceType(Constant.serviceTypeOeder);
			register.setAgentNum(Constant.agentNum);
			register.setApiKey(Constant.apiKey);
			Register registers = new Register(Constant.apiKey, register.getServiceType(), register.getAgentNum(),
					register.getMerchantNumber(), register.getApiKey(), register.getOutMchId(), register.getAppId(),
					register.getCustomerType(), register.getBusinessType(), register.getBusinessName(),
					register.getLegalId(), register.getLegalName(), register.getContact(), register.getContactPhone(),
					register.getContactEmail(), register.getServicePhone(), register.getCustomerName(),
					register.getAddress(), register.getProvinceName(), register.getCityName(),
					register.getDistrictName(), register.getLicenseNo(), register.getPayChannel(), register.getRate(),
					register.getT0Status(), register.getSettleRate(), register.getFixedFee(), register.getIsCapped(),
					register.getSettleMode(), register.getUpperFee(), register.getAccountType(),
					register.getAccountName(), register.getBankCard(), register.getBankName(), register.getProvince(),
					register.getCity(), register.getBankAddress(), register.getAlliedBankNo(), register.getRightID(),
					register.getReservedID(), register.getIDWithHand(), register.getRightBankCard(),
					register.getLicenseImage(), register.getDoorHeadImage(), register.getAccountLicence(),
					register.getQueryType(), register.getCustomerNum(), register.getOrderDate(),
					register.getCheckDate(), log);

			// ss = doScanned(registers, log);
			String brcbGatewayUrl = Constant.register;
			HttpclientJsonRespFileUtil httpclient = new HttpclientJsonRespFileUtil(brcbGatewayUrl);
			String path = "C:\\Users\\Administrator\\Desktop\\" + "brcbstatement" + register.getOrderDate() + ".csv";
			// String path ="D://ing/"+"brcbstatement" + register.getOrderDate()
			// + ".csv";
			httpclient.sendJsonPostDownloadFile(JSONUtil.toJSONString(registers.toMap()), path, "UTF-8");
			log.info("下载账单完成.");
			ss = "下载账单完成.";
		} catch (Exception e) {
			e.printStackTrace();
		}

		return ss;

	}

	@Override
	public String selectSettlementStatus(Register register, Logger log) {
		String ss = "";
		try {
			PmsBusinessPos busInfo = WechatServiceImpl.selectKey(register.getOutMchId());
			register.setCustomerNum("C" + busInfo.getBusinessnum());
			register.setServiceType(Constant.serviceTypeCheck);
			register.setAgentNum(Constant.agentNum);
			register.setApiKey(Constant.apiKey);
			Register registers = new Register(Constant.apiKey, register.getServiceType(), register.getAgentNum(),
					register.getMerchantNumber(), register.getApiKey(), register.getOutMchId(), register.getAppId(),
					register.getCustomerType(), register.getBusinessType(), register.getBusinessName(),
					register.getLegalId(), register.getLegalName(), register.getContact(), register.getContactPhone(),
					register.getContactEmail(), register.getServicePhone(), register.getCustomerName(),
					register.getAddress(), register.getProvinceName(), register.getCityName(),
					register.getDistrictName(), register.getLicenseNo(), register.getPayChannel(), register.getRate(),
					register.getT0Status(), register.getSettleRate(), register.getFixedFee(), register.getIsCapped(),
					register.getSettleMode(), register.getUpperFee(), register.getAccountType(),
					register.getAccountName(), register.getBankCard(), register.getBankName(), register.getProvince(),
					register.getCity(), register.getBankAddress(), register.getAlliedBankNo(), register.getRightID(),
					register.getReservedID(), register.getIDWithHand(), register.getRightBankCard(),
					register.getLicenseImage(), register.getDoorHeadImage(), register.getAccountLicence(),
					register.getQueryType(), register.getCustomerNum(), register.getOrderDate(),
					register.getCheckDate(), log);

			ss = doScanned(registers, log);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return ss;
	}

	public ChannleMerchantConfigKey getChannelConfigKey(String merchantId) throws Exception {
		log.info("获取商户密钥信息");
		return channleMerchantConfigKeyDao.get(merchantId);
	}

}
