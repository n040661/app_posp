package xdt.service.impl;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Service;

import xdt.common.RetAppMessage;
import xdt.common.security.UserContext;
import xdt.dao.*;
import xdt.dto.*;
import xdt.model.*;
import xdt.preutil.StringTools;
import xdt.service.INewsInfoService;
import xdt.service.IPmsMerchantInfoService;
import xdt.util.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import java.math.BigDecimal;
import java.security.MessageDigest;
import java.text.SimpleDateFormat;
import java.util.*;

@Service("pmsMerchantInfoService")
public class PmsMerchantInfoServiceImpl extends BaseServiceImpl implements
		IPmsMerchantInfoService {

	@Resource
	private ITAccAccountDao accountDao; // 系统快易账户服务层
	@Resource
	private IPmsMerchantInfoDao pmsMerchantInfoDao; // 商户信息服务层
	@Resource
	private IUserInfoDao userInfoDao; // 账号信息服务层
	@Resource
	private IPmsMerchantBussinessDao pmsMerchantBussinessDao; // 商户业务服务层
	@Resource
	private IPmsAppBusinessConfigDao pmsAppBusinessConfigDao; // 业务配置服务层
	@Resource
	private IPmsImageDao imageDao; // 上传图片服务层
	@Resource
	private IPayCmmtufitDao payCmmtufitDao; // 系统支持的银行卡服务层
	@Resource
	private IPmsMerchantBindingcardInfoDao bindingcardInfoDao; // 绑定卡列表服务层
	@Resource
	private IPmsMerchantPosDao pmsMerchantPosDao;
	@Resource
	private IPmsPosInfoDao tPosDao;
	@Resource
	private IPmsAppAmountAndRateConfigDao amountAndRateConfigDao;
	@Resource
	private IPmsAppMerchantPayChannelDao pmsAppMerchantPayChannelDao;//商户支付通道表
	@Resource
	private IPmsDictionaryDao pmsDictionaryDao; // 数据字典服务层
    @Resource
    private IPmsAgentInfoDao pmsAgentInfoDao;//代理商
	@Resource
	private IAppRateConfigDao appRateConfigDao;//费率
	@Resource
	private IMerchantMineDao merchantMineDao;
	@Resource
    private IPmsAppAmountAndRateConfigDao pmsAppAmountAndRateConfigDao;//商户费率配置
    @Resource
    private INewsInfoService newsInfoService;
    @Resource
    private IAmountLimitControlDao iAmountLimitControlDao;
    @Resource
    private IPayTypeControlDao iPayTypeControlDao;
    @Resource
    private IPmsUnionpayDao iPmsUnionpayDao;
	@Resource
	private IAgentInviteCodeDao iAgentInviteCodeDao;

	private Logger logger = Logger.getLogger(PmsMerchantInfoServiceImpl.class);

	/**
	 * 商户注册
	 */
	public String merchantRegister(String merchantRegisterInfo,
			HttpSession session, HttpServletRequest request) throws Exception {
		setMethodSession(request.getRemoteAddr());
		logger.info("商户注册");
		String message = INITIALIZEMESSAGE;
		Object obj = parseJsonString(merchantRegisterInfo,
				MerchantRegisterRequestDTO.class);
		String oAgentNo = "";//欧单编号
        //是否输入了代理商编号
        Boolean agentInput = false;
		if (!obj.equals(DATAPARSINGMESSAGE)) {
			MerchantRegisterRequestDTO merchantInfo = (MerchantRegisterRequestDTO) obj;
			
			  oAgentNo = merchantInfo.getoAgentNo();
	            //获取欧单编号，如果没有直接返回失败
	            if(StringUtils.isBlank(oAgentNo)){
	                //没有欧单编号，默认欧单编号是付呗
                    oAgentNo = "100844";
	                logger.info("当前用户是老版本，注册时兼容，默认欧单编号是100844，phone:"+merchantInfo.getMobilePhone());
	            }


            //判断是否输入了代理商户编号
            if(StringUtils.isNotBlank(merchantInfo.getParentAgentNum())){
                //判断代理商编号是否正确
                PmsAgentInfo pmsAgentInfo = pmsAgentInfoDao.selectByAgentNum(merchantInfo.getParentAgentNum());
                if(pmsAgentInfo != null){
                    agentInput =true;
                }else{
                    MerchantRegisterResponseDTO responseData = new MerchantRegisterResponseDTO();
                    responseData.setRetCode(5);
                    responseData.setRetMessage("代理商不存在");
                    String jsonString = createJsonString(responseData);
                    logger.info("[app_rsp]" + createJson(responseData));
                    return jsonString;
                }
            }

			// 获取商户输入的手机号 密码 验证码
			String mobilePhone = merchantInfo.getMobilePhone();
			String password = merchantInfo.getPassWord();
			String messageAuthenticationCode = merchantInfo.getValidCode();
			setSession(request.getRemoteAddr(), session.getId(), mobilePhone);
			logger.info("[client_req]" + createJson(merchantInfo));
			// 非空验证
			if (!isNotEmptyValidate(messageAuthenticationCode)
					|| !isNotEmptyValidate(mobilePhone)
					|| !isNotEmptyValidate(password)) {
				insertAppLogs(mobilePhone, "", "2002");
				message = EMPTYMESSAGE;
			} else {
				// 验证手机号是否合法
				if (checkPhone(mobilePhone)) {
					// 验证手机号是否已注册
					PmsMerchantInfo pmsMerchantInfo = new PmsMerchantInfo();


					//查找当前商户的所属欧
					pmsMerchantInfo.setMobilephone(mobilePhone);
					pmsMerchantInfo.setCustomertype("3");
                    pmsMerchantInfo.setoAgentNo(oAgentNo);
                    //验证账号是否存在的时候需要加上欧单编号
					List<PmsMerchantInfo> phoneList = searchMerchantListInfo(pmsMerchantInfo);
					if (null != phoneList && phoneList.size() >= 1) { // 账号已存在
						insertAppLogs(mobilePhone, "", "2090");
						message = EXISTMESSAGE;
					} else {
						// 验证商户输入的验证码与服务器接收的是否一致
						message = verificationCode(mobilePhone,
								messageAuthenticationCode, PmsMessage.REGISER,oAgentNo);
						if (message.equalsIgnoreCase(SUCCESSMESSAGE)) {
							// 判断新生成的用户编号是否存在
							boolean flag = false;
							String mercId = "";
							pmsMerchantInfo = new PmsMerchantInfo();
							while (!flag) {
								mercId = MERCHANTNUMBERPREFIX
										+ createRandomNumber();
								pmsMerchantInfo.setMercId(mercId);
								pmsMerchantInfo.setCustomertype("3");
								List<PmsMerchantInfo> idList = searchMerchantListInfo(pmsMerchantInfo);
								if (null != idList && idList.size() >= 1) {
									flag = false;
								} else {
									flag = true;
								}
							}


							//判断当前欧单的类型是否是输入邀请码的欧单
							if(StringUtils.isNotBlank(merchantInfo.getInviteCode())){
								//查询当前欧单的信息
								AgentInviteCodePrimaryKey agentInviteCodePrimaryKey = new AgentInviteCodePrimaryKey();
								agentInviteCodePrimaryKey.setOagentno(oAgentNo);
								agentInviteCodePrimaryKey.setInvitecode(merchantInfo.getInviteCode());
								AgentInviteCode agentInviteCode =  iAgentInviteCodeDao.getByPrimaryKeys(agentInviteCodePrimaryKey);
								if(agentInviteCode != null){
									if(agentInviteCode.getStatus() != null && agentInviteCode.getStatus().equals("0")){
										//修改当前商户的代理
										pmsMerchantInfo.setAgentNumber(agentInviteCode.getAgentno());
										//修改当前邀请码的状态为已使用
										agentInviteCode.setStatus("1");
										//修改邀请码的使用商户编号
										agentInviteCode.setMercNum(mercId);
										SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:ss:mm");
										agentInviteCode.setUpdatetime(sdf.format(new Date()));
										//更新状态
										iAgentInviteCodeDao.update(agentInviteCode);

										//获取当前欧单的所有清算费率
										AppRateConfig appRateConfig = new AppRateConfig();
										appRateConfig.setoAgentNo(oAgentNo);
										appRateConfig.setIsThirdpart("3");

											//获取当前代理
										PmsAgentInfo pmsAgentInfo = pmsAgentInfoDao.selectByAgentNum(agentInviteCode.getAgentno());
										//t+0带积分 设置商户清算信息
										if(pmsAgentInfo.getClearType() != null && pmsAgentInfo.getClearType().equals("2") && pmsAgentInfo.getLowestSettleRate() != null){
											List<AppRateConfig> appRateConfigList = appRateConfigDao.searchList(appRateConfig);
										    if(appRateConfigList != null && appRateConfigList.size() > 0){
											   //排序
												String minRateType = "";
												Double minRate = 100.0;
										       for(AppRateConfig appRateConfig1:appRateConfigList){
												   if(Double.parseDouble(appRateConfig1.getRate()) < minRate
														   && Double.parseDouble(pmsAgentInfo.getLowestSettleRate()) <= Double.parseDouble(appRateConfig1.getRate())){
													   minRate = Double.parseDouble(appRateConfig1.getRate());
													   minRateType =  appRateConfig1.getRateType();
												   }
											   }
												if(minRate != 100.0 && minRateType != ""){
													pmsMerchantInfo.setSettleRateType(minRateType);
												}
											}
										}


									}else{
										//邀请码失效
										MerchantRegisterResponseDTO responseData = new MerchantRegisterResponseDTO();
										responseData.setRetCode(10);
										responseData.setRetMessage("邀请码失效，请联系代理商确认");
										String jsonString = createJsonString(responseData);
										logger.info("[app_rsp]" + createJson(responseData));
										return jsonString;
									}
								}else{
									//当前邀请码无效
									MerchantRegisterResponseDTO responseData = new MerchantRegisterResponseDTO();
									responseData.setRetCode(10);
									responseData.setRetMessage("当前邀请码无效，请联系代理商确认");
									String jsonString = createJsonString(responseData);
									logger.info("[app_rsp]" + createJson(responseData));
									return jsonString;
								}
							}


							// 判断新生成的账户编号是否存在
							boolean mark = false;
							String accNum = "";
							while (!mark) {
								accNum = System.currentTimeMillis() + "";
								TAccAccount accountInfo = accountDao
										.searchAccountInfo(accNum);
								if (accountInfo != null) {
									mark = false;
								} else {
									mark = true;
								}
							}
							// 动态生成账户信息
							TAccAccount account = new TAccAccount();
							account.setAccNum(accNum);// 帐户
							account.setBalance(new BigDecimal(0.0000)); // 余额
							account.setLastBalance(new BigDecimal(0.0000));// 上次余额
							account.setAccountTime(new Date());// 开户时间
							account.setStatus("1");// 0=初始，1=启用，2=冻结，3=停用
							account.setIsCredit(new BigDecimal(0)); // 0 储蓄卡
							account.setCreditLimit(new BigDecimal(0.0000));// 信用额度
							account.setFreezeBalance(new BigDecimal(0.0000)); // 冻结余额
							account
									.setLastFreezeBalance(new BigDecimal(0.0000)); // 上次冻结余额
							account.setAccLevel(new BigDecimal("3"));
							account.setName(mobilePhone); // 手机号
							account.setAccType("1");// 1.商户账户
							account.setClearResult("0"); // 0 未结算
                            account.setoAgentNo(oAgentNo); //添加欧单编号
							if (accountDao.insert(account) == 1) {
								SimpleDateFormat format = new SimpleDateFormat(
										"yyyy-MM-dd");
								Date dd = new Date();
								Calendar calendar = Calendar.getInstance();
								calendar.setTime(dd);
								calendar.add(Calendar.DATE, 10);
								String T1 = format.format(calendar.getTime());
                                pmsMerchantInfo.setoAgentNo(oAgentNo);//设置欧单编号
								pmsMerchantInfo.setMobilephone(mobilePhone);// 手机号
								pmsMerchantInfo.setPassword(password);// 登录密码
								pmsMerchantInfo.setMercId(mercId);// 编号
								pmsMerchantInfo.setCustomertype("3");// 3.手机用户
								pmsMerchantInfo.setMercSts("10");// 10.申请商户
								pmsMerchantInfo.setCreationName(mobilePhone);// 创建人登录名
								pmsMerchantInfo.setKyaccount(accNum);// 快易的账户编号
								pmsMerchantInfo.setAddress("");// 客户地址
								pmsMerchantInfo
										.setRegisteredcapital(new BigDecimal(10)); // 默认10万
								pmsMerchantInfo.setRemovetag(new BigDecimal(0)); // 正常
								pmsMerchantInfo.setLicenseissuingdate(format
										.format(new Date()));// 执照签发日期
								pmsMerchantInfo.setLicenseduedate(T1); // 执照到期日期
								pmsMerchantInfo.setCreationdate(sdf
										.format(new Date()));// 创建时间
								pmsMerchantInfo.setApplydate(sdf
										.format(new Date()));// 打成申请商户时间
								pmsMerchantInfo.setStatus(new BigDecimal(0)); // 未审核
								pmsMerchantInfo.setRetMessage(""); // 打回原因
								pmsMerchantInfo.setPremiumrate(PERMIUMRATE);// 收款费率

                                if(agentInput){
                                    //如果是输入了代理商编号的，将编号加入其中
                                    pmsMerchantInfo.setAgentNumber(merchantInfo.getParentAgentNum());
                                }
                                if(StringUtils.isNotBlank(merchantInfo.getAltLat())){
                                   //如果传入了经纬度则设置
                                    pmsMerchantInfo.setAltLat(merchantInfo.getAltLat());
                                }
                                if(StringUtils.isNotBlank(merchantInfo.getGpsAddress())){
                                   //如果传入了gps地址则设置
                                    pmsMerchantInfo.setGpsAddress(merchantInfo.getGpsAddress());
                                }

                                if (merchantRegister(pmsMerchantInfo) == 1) {
									// 查询刚注册商户的id值
									pmsMerchantInfo = new PmsMerchantInfo();
									pmsMerchantInfo.setMercId(mercId);
									pmsMerchantInfo.setCustomertype("3");
									List<PmsMerchantInfo> idList = searchMerchantListInfo(pmsMerchantInfo);
									if (null != idList && idList.size() == 1) {
										String id = idList.get(0).getId();
											// 商户注册成功后 默认分配登录的用户表
                                            Map<String,String> params = new HashMap<String,String>();
                                            params.put("mobile",mobilePhone);
                                            params.put("oAgentNo",oAgentNo);
                                            if(userInfoDao.searchUserinfoByMobile(params) == null){
                                                Userinfo userinfo = new Userinfo();
                                                userinfo.setLoginName(mobilePhone);
                                                userinfo.setLoginPwd(password);
                                                userinfo.setRoleId(0l);
                                                userinfo.setMobileno(mobilePhone);
                                                userinfo.setPwdDate(new Date());
                                                userinfo.setUserStatus(0l);
                                                userinfo.setMerchantId(mercId);
                                                userinfo.setoAgentNo(oAgentNo);
                                                userInfoDao.insert(userinfo);
                                            }

											//获取当前欧单的交易金额限制项目
                                            List<AmountLimitControl> amountLimitControlList =  iAmountLimitControlDao.getListByOagentNo(oAgentNo);
											// 商户注册成功后 默认分配费率
											PmsAppAmountAndRateConfig pmsAppAmountAndRateConfig = null;
											//获取当前欧单的权限菜单
										    PmsAppBusinessConfig pmsAppBusinessConfig = new PmsAppBusinessConfig();
										    pmsAppBusinessConfig.setoAgentNo(oAgentNo);
										    List<PmsAppBusinessConfig> pmsAppBusinessConfigs = pmsAppBusinessConfigDao.searchList(pmsAppBusinessConfig);
											for (PmsAppBusinessConfig pmsAppBusinessConfig1:pmsAppBusinessConfigs) {

												pmsAppAmountAndRateConfig = new PmsAppAmountAndRateConfig();
                                                //设置欧单编号
                                                pmsAppAmountAndRateConfig.setoAgentNo(oAgentNo);
												pmsAppAmountAndRateConfig.setMercId(mercId);
												pmsAppAmountAndRateConfig.setBusinesscode(pmsAppBusinessConfig1.getModulecode());
												Integer min = 2 * 100;
												Integer max = 20000 * 100;
												pmsAppAmountAndRateConfig.setMinAmount(min.toString());
												pmsAppAmountAndRateConfig.setMaxAmount(max.toString());
												pmsAppAmountAndRateConfig.setAccountTime("一个工作日内");
												if (pmsAppBusinessConfig1.getModulecode().equals(TradeTypeEnum.drawMoney.getTypeCode())) {
													
													Integer drawMin ;
													
													if(ZHONGCIOAGENTNO.equals(oAgentNo)){//修改中磁的提现次数为5次，最低提现金额10元      2015-10-30   wm
														// 只有提现业务才有操作次数
														pmsAppAmountAndRateConfig.setNumberoftimes("5");
														// 提现业务 最小金额为10元
														 drawMin = 10 * 100;
													}else{
														// 只有提现业务才有操作次数
														pmsAppAmountAndRateConfig.setNumberoftimes("3");
														// 提现业务 最小金额为1000元
														 drawMin = 1000 * 100;
													}
													
													
													pmsAppAmountAndRateConfig.setMinAmount(drawMin.toString());
													pmsAppAmountAndRateConfig.setRatetype(RateTypeEnum.tixianRateType.getTypeCode());
												}else if(pmsAppBusinessConfig1.getModulecode().equals(TradeTypeEnum.transeMoney.getTypeCode())||
														pmsAppBusinessConfig1.getModulecode().equals(TradeTypeEnum.creditCardRePay.getTypeCode())
                                                        ){
													// 转账汇款  信用卡还款   最小金额为10元  最大金额为  50000元
													Integer cardMin = 10 * 100;
													Integer cardmax = 50000 * 100;
													pmsAppAmountAndRateConfig.setMinAmount(cardMin.toString());
													pmsAppAmountAndRateConfig.setMaxAmount(cardmax.toString());
													pmsAppAmountAndRateConfig.setRatetype(RateTypeEnum.biaozhunRateType.getTypeCode());
												} else if( pmsAppBusinessConfig1.getModulecode().equals(TradeTypeEnum.merchantCollect.getTypeCode())){
                                                    //商户收款
                                                    Integer cardMin = 10 * 100;
                                                    Integer cardmax = 20000 * 100;
                                                    pmsAppAmountAndRateConfig.setMinAmount(cardMin.toString());
                                                    pmsAppAmountAndRateConfig.setMaxAmount(cardmax.toString());
                                                    pmsAppAmountAndRateConfig.setRatetype(RateTypeEnum.biaozhunRateType.getTypeCode());
                                                }else if(pmsAppBusinessConfig1.getModulecode().equals(TradeTypeEnum.shop.getTypeCode())){
                                                   //商城
                                                    Integer cardMin = 10 * 100;
                                                    Integer cardmax = 20000 * 100;
                                                    pmsAppAmountAndRateConfig.setMinAmount(cardMin.toString());
                                                    pmsAppAmountAndRateConfig.setMaxAmount(cardmax.toString());
                                                    //这里设置的是刷卡费率，第三方的费率直接从app_rate_config中获取
                                                    pmsAppAmountAndRateConfig.setRatetype(RateTypeEnum.biaozhunRateType.getTypeCode());
                                                } else {
													pmsAppAmountAndRateConfig.setRatetype(RateTypeEnum.biaozhunRateType.getTypeCode());
												}
												// 商户收款 转账汇款 信用卡还款 提现
												// 默认不开通，在实名认证后开通
												if (pmsAppBusinessConfig1.getModulecode().equals(TradeTypeEnum.merchantCollect.getTypeCode())
														|| pmsAppBusinessConfig1.getModulecode().equals(TradeTypeEnum.transeMoney.getTypeCode())
														|| pmsAppBusinessConfig1.getModulecode().equals(TradeTypeEnum.creditCardRePay.getTypeCode())
														|| pmsAppBusinessConfig1.getModulecode().equals(TradeTypeEnum.drawMoney.getTypeCode())) {
													pmsAppAmountAndRateConfig.setStatus("0");
												} else {
													pmsAppAmountAndRateConfig.setStatus("1");
												}
												pmsAppAmountAndRateConfig.setAccountType("1");
												pmsAppAmountAndRateConfig.setDescription(pmsAppBusinessConfig1.getBusinessname());
												pmsAppAmountAndRateConfig.setCreateTime(UtilDate.getDateFormatter());
												pmsAppAmountAndRateConfig.setModifyTime("");
												pmsAppAmountAndRateConfig.setModifyUser("");


                                                //设置欧单的默认设置，如果为空则按照原来的默认数据数据进行设置
                                               if(amountLimitControlList != null && amountLimitControlList.size() > 0){
                                                   //如果为1 则找到了一条当前交易的记录 如果为0 则没有找到
                                                   int amountOagentFlag = 0;
                                                   for(AmountLimitControl amountLimitControl:amountLimitControlList){
                                                        if(amountLimitControl.getTradetype().equals(pmsAppBusinessConfig1.getModulecode())){
                                                            pmsAppAmountAndRateConfig.setMinAmount(amountLimitControl.getMinamount().toString());
                                                            pmsAppAmountAndRateConfig.setMaxAmount(amountLimitControl.getMaxamount().toString());
                                                            amountOagentFlag = 1;
                                                            break;
                                                        }
                                                    }
                                                   if(amountOagentFlag == 0){
                                                      logger.info("没有找到欧单配置交易金额的项目"+oAgentNo+",tradetype:"+pmsAppBusinessConfig1.getModulecode());
                                                      continue;
                                                   }
                                               }

												amountAndRateConfigDao.insert(pmsAppAmountAndRateConfig);
											}
											//添加商户支付通道表数据
											/*List<TradeTypeModel> tradeTypeList = TradeTypeEnum.getTradeTypeList();
											for (int i=0;i<3;i++) {//取前三种交易类型 1 商户收款、2 转账汇款、3 信用卡还款
												TradeTypeModel typeModel = tradeTypeList.get(i);*/
												
												PmsAppMerchantPayChannel pmsAppMerchantPayChannel = null;
                                        //获取当前欧单的支付方式限制
                                        List<PayTypeControl> payTypeControlList =  iPayTypeControlDao.getListByOagentNo(oAgentNo);
												for(TradeTypeModel paymentCodeEnumTypeModel : PaymentCodeEnum.getTradeTypeList()){
													pmsAppMerchantPayChannel = new PmsAppMerchantPayChannel();
													pmsAppMerchantPayChannel.setMercId(mercId);
                                                    pmsAppMerchantPayChannel.setoAgentNo(oAgentNo);
													pmsAppMerchantPayChannel.setBusinesscode("empty");//暂时为空
													pmsAppMerchantPayChannel.setCreatetime(UtilDate.getDateFormatter());
													pmsAppMerchantPayChannel.setPaymentcode(paymentCodeEnumTypeModel.getTradeTypeCode());
													pmsAppMerchantPayChannel.setStatus("0");//状态 0 有效 1 无效 
													pmsAppMerchantPayChannel.setModifytime("");
													pmsAppMerchantPayChannel.setModifyuser("");
													pmsAppMerchantPayChannel.setDescribe(paymentCodeEnumTypeModel.getTradeTypeName());
													//支付宝默认不开通设置为1无效
													if (paymentCodeEnumTypeModel.getTradeTypeCode().equals(PaymentCodeEnum.zhifubaoPay.getTypeCode())){
														pmsAppMerchantPayChannel.setStatus("1");//状态 0 有效 1 无效 
													}


                                                    //设置欧单的默认设置，如果为空则按照原来的默认数据数据进行设置
                                                    if(payTypeControlList != null && payTypeControlList.size() > 0){
                                                        //如果为1 则找到了一条当前交易的记录 如果为0 则没有找到
                                                        int payOagentFlag = 0;
                                                        for(PayTypeControl payTypeControl:payTypeControlList){
                                                            if(payTypeControl.getPaytype().equals(paymentCodeEnumTypeModel.getTradeTypeCode())){
                                                                if(payTypeControl.getStatus().equals("0")){
                                                                    pmsAppMerchantPayChannel.setStatus("1");
                                                                }else{
                                                                    pmsAppMerchantPayChannel.setStatus("0");
                                                                }
                                                                payOagentFlag = 1;
                                                                break;
                                                            }
                                                        }
                                                        if(payOagentFlag == 0){
                                                            logger.info("没有找到欧单配置支付方式的项目"+oAgentNo+",paytype:"+paymentCodeEnumTypeModel.getTradeTypeCode());
                                                            continue;
                                                        }
                                                    }


													
													pmsAppMerchantPayChannelDao.insert(pmsAppMerchantPayChannel);
												}
												
											//}
											
											
											message = SUCCESSMESSAGE;


									} else {
										insertAppLogs(mobilePhone, "", "2006");
										message = FAILMESSAGE + "save";
									}
								} else {
									insertAppLogs(mobilePhone, "", "2005");
									message = FAILMESSAGE;
								}
							} else {
								insertAppLogs(mobilePhone, "", "2004");
								message = FAILMESSAGE + "insert";
							}
						}
					}
				} else {
					insertAppLogs(mobilePhone, "", "2003");
					message = INVALIDMESSAGE;
				}
			}
		} else {
			insertAppLogs("", "", "2001");
			message = DATAPARSINGMESSAGE;
		}
		// 解析要返回的信息
		int retCode = Integer.parseInt(message.split(":")[0]);
		String retMessage = message.split(":")[1];
		if (retMessage.equals("initialize")) {
			retMessage = "系统初始化";
		} else if (retMessage.equals("dataParsing")) {
			retMessage = "数据解析错误";
		} else if (retMessage.equals("empty")) {
			retMessage = "注册信息不能为空";
		} else if (retMessage.equals("invalid")) {
			retMessage = "请输入合法的手机号";
		} else if (retMessage.equals("exist")) {
			retMessage = "手机号已注册";
		} else if (retMessage.equals("error")) {
			retMessage = "验证码输入错误";
		} else if (retMessage.equals("failure")) {
			retMessage = "验证码失效，请重新获取";
		} else if (retMessage.equals("success")) {
			retMessage = "注册成功";
		} else if (retMessage.equals("fail")) {
			retMessage = "注册失败";
		} else if (retMessage.equals("failinsert")) {
			retMessage = "账户信息保存失败";
		} else if (retMessage.equals("failsave")) {
			retMessage = "手机业务保存失败";
		}
		MerchantRegisterResponseDTO responseData = new MerchantRegisterResponseDTO();
		responseData.setRetCode(retCode);
		responseData.setRetMessage(retMessage);
		String jsonString = createJsonString(responseData);
		logger.info("[app_rsp]" + createJson(responseData));
		return jsonString;
	}

	/**
	 * 商户注册异常
	 */
	public String merchantRegisterException(String merchantRegisterInfo)
			throws Exception {
		MerchantRegisterResponseDTO responseData = new MerchantRegisterResponseDTO();
		responseData.setRetCode(100);
		responseData.setRetMessage("系统异常");
		logger.info("[app_rsp]" + createJson(responseData));
		Object obj = parseJsonString(merchantRegisterInfo,
				MerchantRegisterRequestDTO.class);
		if (!obj.equals(DATAPARSINGMESSAGE)) {
			MerchantRegisterRequestDTO merchantInfo = (MerchantRegisterRequestDTO) obj;
			String mobilePhone = merchantInfo.getMobilePhone();
			insertAppLogs(mobilePhone, "", "2080");
		} else {
			insertAppLogs("", "", "2001");
		}
		return createJsonString(responseData);
	}

	/**
	 * 商户登录
	 */
	@SuppressWarnings("deprecation")
	public String merchantLogin(String merchantLoginInfo, HttpSession session,
			HttpServletRequest request) throws Exception {
		setMethodSession(request.getRemoteAddr());
		logger.info("商户登录");
		String message = INITIALIZEMESSAGE;
		// 解析商户登录信息
		Object obj = parseJsonString(merchantLoginInfo,
				MerchantLoginRequestDTO.class);
		String userName = null; // 登录名
		String shortName = null; // 商户名称3
		String attestationSign = null;// 实民认标记
		String status = null; // 审核状态
		String backReason = null; // 打回原因
		String deviceStatus = null;// 设备状态
		String roleId = null;// 0主账户1收银员
        String isRead = null;// 是否有末读消息标记（0：有末读消息；1：已全部读过）
        String isGPRS = null;// 是否需要GPRS（0：需要GPRS；1：不需要GPRS）
        LoginMsgModel loginMsg = new LoginMsgModel();//登录时的提示信息
        NewsInfo newsInfo = null;
		MerPayChannel mpc = new MerPayChannel();
		List<PmsAppBusinessConfig> businessList = null;
		Map<String,PmsAppBusinessConfig> map = null;
		if (!obj.equals(DATAPARSINGMESSAGE)) {
			MerchantLoginRequestDTO merchantInfo = (MerchantLoginRequestDTO) obj;
			// 获取商户输入的登录名 密码
			userName = merchantInfo.getUserName();
			String password = merchantInfo.getPassWord();
			String oAgentNo = merchantInfo.getoAgentNo();
			
			if("".equals(oAgentNo) || oAgentNo == null){
				oAgentNo = "100844";
			}

			// 判断登录名与密码是否为空
			if (!isNotEmptyValidate(userName) || !isNotEmptyValidate(password) || !isNotEmptyValidate(oAgentNo)) {
				insertAppLogs(userName, "", "2002");
				message = EMPTYMESSAGE;
			} else {
				setSession(request.getRemoteAddr(), session.getId(), userName);
				logger.info("[client_req]" + createJson(merchantInfo));

				Userinfo userinfo = new Userinfo();
				userinfo.setLoginName(userName);
				userinfo.setoAgentNo(oAgentNo);
                userinfo.setUserStatus(0l);
				List<Userinfo> userList = userInfoDao.searchList(userinfo);
				if (null != userList && userList.size() >= 1) {
					userinfo.setLoginPwd(password);
					List<Userinfo> userinfoList = userInfoDao
							.searchList(userinfo);
					if (null != userinfoList && userinfoList.size() >= 1) {
						userinfo = userinfoList.get(0);
						// 根据登录名与密码去数据库判断是否存在对应的记录
						PmsMerchantInfo pmsMerchantInfo = new PmsMerchantInfo();
						pmsMerchantInfo.setMercId(userinfo.getMerchantId());
						pmsMerchantInfo.setCustomertype("3");

						List<PmsMerchantInfo> list = searchMerchantListInfo(pmsMerchantInfo);
						if (null != list && list.size() >= 1) {
							PmsMerchantInfo merchant = list.get(0);
							// 判断是否是无效商户
							if (merchant.getMercSts().equalsIgnoreCase("80")) {
								logger.info("80：无效商户");
								insertAppLogs(userName, "", "2081");
								message = INVALIDMESSAGE;
							} else {
								if (MessageDigest.isEqual(StringTools
										.hexStringToBytes(userinfo
												.getLoginPwd()), StringTools
										.hexStringToBytes(password))) {
									shortName = merchant.getShortname();
									attestationSign = merchant.getStatus()
											.toString();
									String mercId = merchant.getMercId();
									status = merchant.getStatus().toString();
									backReason = merchant.getRetMessage();
									roleId = userinfo.getRoleId().toString();
									
									//兼容老版本
									UserContext userContext = (UserContext)session.getAttribute(UserContext.USERCONTEXT);
									String clientType = userContext.getClientType();
									String clientVersion = userContext.getClientVersion();
									
									PmsDictionary pmsDictionary = new PmsDictionary();
									pmsDictionary.setType(oAgentNo);
									List<PmsDictionary> pmsDictionaryList = pmsDictionaryDao.searchList(pmsDictionary);
									if(pmsDictionaryList != null && pmsDictionaryList.size() > 0){
										pmsDictionary.setKey(clientType);
										pmsDictionary.setValue(clientVersion);
										pmsDictionaryList = pmsDictionaryDao.searchList(pmsDictionary);
										if(pmsDictionaryList != null && pmsDictionaryList.size() > 0){
											// 查询用户的业务列表(老版本)
											businessList = pmsAppBusinessConfigDao.searchBusinessInfo(mercId);
										}else{
											// 查询用户的业务列表(新版本)
											businessList = pmsAppBusinessConfigDao.searchBusinessInfo1(mercId);
										}
									}else{
										// 查询用户的业务列表(老版本)
										businessList = pmsAppBusinessConfigDao.searchBusinessInfo(mercId);
									}
									
									for (PmsAppBusinessConfig p : businessList) {
										if (p.getBusinessname().equals("违章查询")) {
											/*
											 * 此处使用AES-128-ECB加密模式，key需要为16位。
											 */
											String cKey = "1234567890123456";

											String externalId = merchant.getExternalId();
											String accno = p.getAccno();

											// 需要加密的字串
											String cSrc = externalId.concat(accno);

											String token = AES.Encrypt(cSrc,cKey);

											token = java.net.URLEncoder.encode(token);

											String externalurl = p.getExternalurl().concat("?" + Constants.USER_FROM + "=" + accno + "&" 
													+ Constants.USER_ID + "=" + externalId + "&" + Constants.TOKEN + "=" + token);

											p .setExternalurl(java.net.URLEncoder .encode(externalurl));
										}
									}
									
									map = new HashMap<String, PmsAppBusinessConfig>();
									
									PmsAppBusinessConfig pab = null;
									
									for (PmsAppBusinessConfig p : businessList) {
										if(!p.getStatus().equals(p.getStatus1()) && "1".equals(p.getStatus())){
											p.setStatus(p.getStatus1());
											p.setMessage(p.getMessage1());
											p.setExternalurl(null);
										}
										if("1".equals(p.getType())){
											pab = p;
										}
										map.put(p.getModulecode(), p);
									}
									
									businessList.remove(pab);

									// 将用户的基本信息存入session中
									String id = merchant.getId();
									String shortname = merchant.getShortname();
									String mobilephone = merchant
											.getMobilephone();
									String accNum = merchant.getKyaccount();
									String externalId = merchant
											.getExternalId();
									String oAgentNo1 = merchant.getoAgentNo();
									String agentNumber = merchant.getAgentNumber();
									session.setAttribute("agentNumber",agentNumber);
									session.setAttribute(
											SessionInfo.SESSIONINFO,
											new SessionInfo(id, mercId,
													shortname, mobilephone,
													accNum, externalId,
													userinfo, oAgentNo1));
									message = SUCCESSMESSAGE;

									//用户支付方式
									PmsAppMerchantPayChannel merPayChannnel = new PmsAppMerchantPayChannel();
									merPayChannnel.setMercId(mercId);
									List<PmsAppMerchantPayChannel> merPayChannnelList = pmsAppMerchantPayChannelDao.searchList(merPayChannnel);
									if(merPayChannnelList == null || merPayChannnelList.size() > 0){
										for(PmsAppMerchantPayChannel p : merPayChannnelList){
											String paymentcode = p.getPaymentcode();
											String pstatus = p.getStatus();
											if(PaymentCodeEnum.accountPay.getTypeCode().equals(paymentcode)){
												mpc.setAccountPay(pstatus);
											}else if(PaymentCodeEnum.baiduPay.getTypeCode().equals(paymentcode)){
												mpc.setBaiduPay(pstatus);
											}else if(PaymentCodeEnum.weixinPay.getTypeCode().equals(paymentcode)){
												mpc.setWeixinPay(pstatus);
											}else if(PaymentCodeEnum.zhifubaoPay.getTypeCode().equals(paymentcode)){
												mpc.setZhifubaoPay(pstatus);
											}else if(PaymentCodeEnum.shuakaPay.getTypeCode().equals(paymentcode)){
												mpc.setShuakaPay(pstatus);
											}else if(PaymentCodeEnum.ydhbPay.getTypeCode().equals(paymentcode)){
												mpc.setYdhbPay(pstatus);
											}
										}
									}
									
									//设备状态
									PmsMerchantPos p = new PmsMerchantPos();
									p.setMerchantid(new BigDecimal(id));
									List<PmsMerchantPos> searchList = pmsMerchantPosDao
											.searchList(p);

									if (searchList != null
											&& searchList.size() > 0) {
										String postype1 = null;
										String postype2 = null;
										for (PmsMerchantPos merchantPos : searchList) {
											BigDecimal posId = merchantPos
													.getPosid();
											PmsPosInfo selectPosId = tPosDao
													.selectPosId(posId
															.toString());
											String postype = selectPosId
													.getPostype();
											if ("1".equals(postype)) {
												postype1 = postype;
											} else if ("2".equals(postype)) {
												postype2 = postype;
											}
										}

										if (postype1 == null
												&& postype2 == null) {
											deviceStatus = "0";
										} else if (postype1 != null
												&& postype2 == null) {
											deviceStatus = "1";
										} else if (postype1 == null
												&& postype2 != null) {
											deviceStatus = "2";
										} else if (postype1 != null
												&& postype2 != null) {
											deviceStatus = "3";
										}
									}

                                    //查询当前商户的消息列表
                                   isRead =  newsInfoService.haveUnReadMsg(mercId,oAgentNo);
                                    //查询登录提示信息
                                    newsInfo = newsInfoService.loginRemind(oAgentNo);
                                    if(newsInfo != null && StringUtils.isNotBlank(newsInfo.getNewsContent())){
                                        loginMsg.setStatus("1");
                                        loginMsg.setMsg(newsInfo.getNewsContent());
                                    }else{
                                        loginMsg.setStatus("0");
                                        logger.info("当前欧单没有登录提示信息："
                                                +oAgentNo);
                                    }
                                    
                                    //查询是否需要地理位置
                                    PmsUnionpay pmsUnionpay = iPmsUnionpayDao.searchById(mercId);
                                    if(pmsUnionpay == null){
                                    	isGPRS = "1";
                                    }else{
                                    	isGPRS = "0";
                                    }

								} else {
									logger.info("商户注册密码："
											+ merchant.getPassword());
									insertAppLogs(userName, "", "2083");
									message = ERRORMESSAGE;
								}

							}
						} else {
							insertAppLogs(userName, "", "2006");
							message = EXISTMESSAGE;
						}

					} else {
						message = ERRORMESSAGE;
					}
				} else {
					insertAppLogs(userName, "", "2006");
					message = EXISTMESSAGE;
				}
			}
		} else {
			insertAppLogs("", "", "2001");
			message = DATAPARSINGMESSAGE;
		}
		// 解析要返回的信息
		int retCode = Integer.parseInt(message.split(":")[0]);
		String retMessage = message.split(":")[1];
		if (retMessage.equals("initialize")) {
			retMessage = "系统初始化";
		} else if (retMessage.equals("dataParsing")) {
			retMessage = "数据解析错误";
		} else if (retMessage.equals("empty")) {
			retMessage = "登录信息不能为空";
		} else if (retMessage.equals("exist")) {
			retMessage = "手机号未注册";
		} else if (retMessage.equals("invalid")) {
			retMessage = "无效商户";
		} else if (retMessage.equals("success")) {
			retMessage = "登录成功";
		} else if (retMessage.equals("error")) {
			retMessage = "用户名或密码错误";
		} else if (retMessage.equals("fail")) {
			retMessage = "请输入合法的手机号";
		}
		// 向客户端返回判断信息
		MerchantLoginResponseDTO responseData = new MerchantLoginResponseDTO();
		responseData.setRetCode(retCode);
		responseData.setRetMessage(retMessage);
		responseData.setUserName(userName);
		responseData.setAttestationSign(attestationSign);
		responseData.setShortName(shortName);
		responseData.setStatus(status);
		responseData.setBackReason(backReason);
		responseData.setDeviceStatus(deviceStatus);
		responseData.setRoleId(roleId);
		responseData.setMerPayChannel(mpc);
        responseData.setRead(isRead);
		responseData.setList(businessList);
		responseData.setMap(map);
        responseData.setLoginMsg(loginMsg);
		responseData.setIsGPRS(isGPRS);

		logger.info("[app_rsp]" + createJson(responseData));
		String jsonString = createJsonString(responseData);
		return jsonString;
	}

	/**
	 * 商户登录异常
	 */
	@Override
	public String merchantLoginException(String merchantLoginInfo)
			throws Exception {
		MerchantLoginResponseDTO responseData = new MerchantLoginResponseDTO();
		responseData.setUserName("");
		responseData.setAttestationSign("");
		responseData.setRetCode(100);
		responseData.setRetMessage("系统异常");
		responseData.setShortName("");
		responseData.setBackReason("");
		responseData.setStatus("");
		responseData.setList(null);
		logger.info("[app_rsp]" + createJson(responseData));
		Object obj = parseJsonString(merchantLoginInfo,
				MerchantLoginRequestDTO.class);
		if (!obj.equals(DATAPARSINGMESSAGE)) {
			MerchantLoginRequestDTO merchantInfo = (MerchantLoginRequestDTO) obj;
			insertAppLogs(merchantInfo.getUserName(), "", "2082");
		} else {
			insertAppLogs("", "", "2001");
		}
		return createJsonString(responseData);
	}

	/**
	 * 检索商户信息
	 */
	public List<PmsMerchantInfo> searchMerchantListInfo(
			PmsMerchantInfo pmsMerchantInfo) throws Exception {
		return this.pmsMerchantInfoDao.searchList(pmsMerchantInfo);
	}

	/**
	 * 商户注册
	 */
	public int merchantRegister(PmsMerchantInfo pmsMerchantInfo)
			throws Exception {
		if (pmsMerchantInfo != null) {
			UUID uuid = UUID.randomUUID();
			pmsMerchantInfo.setExternalId(uuid.toString().replaceAll("-", ""));
		}
		return this.pmsMerchantInfoDao.insert(pmsMerchantInfo);
	}

	/**
	 * 找回密码验证确认
	 */
	public String retrievePasswordValidationConfirm(
			String retrievePasswordValidationConfirmInfo,
			HttpServletRequest request) throws Exception {
		setMethodSession(request.getRemoteAddr());
		logger.info("找回密码 确认信息");
		String message = INITIALIZEMESSAGE;
		Object obj = parseJsonString(retrievePasswordValidationConfirmInfo,
				RetrievePasswordValidationConfirmRequestDTO.class);
		if (!obj.equals(DATAPARSINGMESSAGE)) {
			RetrievePasswordValidationConfirmRequestDTO confirmInfo = (RetrievePasswordValidationConfirmRequestDTO) obj;
			String validCode = confirmInfo.getValidCode();
			String mobilePhone = confirmInfo.getMobilePhone();
			String oAgentNo = confirmInfo.getoAgentNo();
			
			if("".equals(oAgentNo) || oAgentNo == null){
				oAgentNo = "100844";
			}
			
			setSession(request.getRemoteAddr(), request.getSession().getId(),
					mobilePhone);
			logger.info("[client_req]" + createJson(confirmInfo));

			PmsMerchantInfo pmsMerchantInfo = new PmsMerchantInfo();
			pmsMerchantInfo.setMobilephone(mobilePhone);
			pmsMerchantInfo.setoAgentNo(oAgentNo);
			pmsMerchantInfo.setCustomertype("3");
			List<PmsMerchantInfo> list = searchMerchantListInfo(pmsMerchantInfo);
			if (null != list && list.size() >= 1) {
				PmsMerchantInfo merchantInfo = list.get(0);
				int mercSts = Integer.parseInt(merchantInfo.getMercSts());
				// 判断此商户是否是注销商户（无效商户）
				if (mercSts == 80) {
					logger.info("80：无效商户");
					insertAppLogs(mobilePhone, "", "2081");
					message = INVALIDMESSAGE;
				} else {
					if (mercSts >= 30 && mercSts <= 60) { // 是实名认证的商户
						// 必须输入身份证号
						String crpIdNo = confirmInfo.getIdentityCard();
						if (null == crpIdNo || "".equals(crpIdNo)) {
							insertAppLogs(mobilePhone, "", "2002");
							message = EMPTYMESSAGE;
						} else {
							// 判断输入的身份证号格式是否正确
							if (checkIdCard(crpIdNo)) {
								String idCard = merchantInfo.getCrpIdNo();
								// 判断输入的身份证号是否正确
								if (idCard.equals(crpIdNo)) {
									message = verificationCode(mobilePhone,
											validCode, PmsMessage.FINDPASS, oAgentNo);
								} else {
									logger.info(mobilePhone + "," + crpIdNo
											+ ",实名认证填写的身份证号：" + idCard
											+ ",身份证号输入错误");
									insertAppLogs(mobilePhone, "", "2084");
									message = FAILMESSAGE;
								}
							} else {
								insertAppLogs(mobilePhone, "", "2016");
								message = ERRORMESSAGE + "r";
							}

						}
					} else {
						message = verificationCode(mobilePhone, validCode,
								PmsMessage.FINDPASS,oAgentNo);
					}
				}
			} else {
				insertAppLogs(mobilePhone, "", "2006");
				message = EXISTMESSAGE;
			}

		} else {
			insertAppLogs("", "", "2001");
			message = DATAPARSINGMESSAGE;
		}
		// 解析要返回的信息
		int retCode = Integer.parseInt(message.split(":")[0]);
		String retMessage = message.split(":")[1];
		if (retMessage.equals("initialize")) {
			retMessage = "系统初始化";
		} else if (retMessage.equals("dataParsing")) {
			retMessage = "数据解析错误";
		} else if (retMessage.equals("exist")) {
			retMessage = "手机号未注册";
		} else if (retMessage.equals("invalid")) {
			retMessage = "无效商户";
		} else if (retMessage.equals("success")) {
			retMessage = "验证成功";
		} else if (retMessage.equals("error")) {
			retMessage = "验证码输入错误";
		} else if (retMessage.equals("failure")) {
			retMessage = "验证码失效，请重新获取";
		} else if (retMessage.equals("empty")) {
			retMessage = "身份证号不能为空";
		} else if (retMessage.equals("errorr")) {
			retMessage = "请输入合法的身份证号";
		} else if (retMessage.equals("fail")) {
			retMessage = "身份证号输入错误";
		}
		// 向客户端返回判断信息
		RetrievePasswordValidationConfirmResponseDTO responseData = new RetrievePasswordValidationConfirmResponseDTO();
		responseData.setRetCode(retCode);
		responseData.setRetMessage(retMessage);
		String jsonString = createJsonString(responseData);
		logger.info("[app_rsp]" + createJson(responseData));
		return jsonString;
	}

	/**
	 * 找回密码验证确认异常
	 */
	@Override
	public String retrievePasswordValidationConfirmException(
			String retrievePasswordValidationConfirmInfo) throws Exception {
		RetrievePasswordValidationConfirmResponseDTO responseData = new RetrievePasswordValidationConfirmResponseDTO();
		responseData.setRetCode(100);
		responseData.setRetMessage("系统异常");
		logger.info("[app_rsp]" + createJson(responseData));
		Object obj = parseJsonString(retrievePasswordValidationConfirmInfo,
				RetrievePasswordValidationConfirmRequestDTO.class);
		if (!obj.equals(DATAPARSINGMESSAGE)) {
			RetrievePasswordValidationConfirmRequestDTO confirmInfo = (RetrievePasswordValidationConfirmRequestDTO) obj;
			String mobilePhone = confirmInfo.getMobilePhone();
			insertAppLogs(mobilePhone, "", "2085");
		} else {
			insertAppLogs("", "", "2001");
		}
		return createJsonString(responseData);
	}

	/**
	 * 找回密码验证通过 修改密码
	 */
	public String updatePassword(String changePasswordInfo,
			HttpSession session, HttpServletRequest request) throws Exception {
		setMethodSession(request.getRemoteAddr());
		logger.info("找回密码 修改密码");
		String message = INITIALIZEMESSAGE;
		Object obj = parseJsonString(changePasswordInfo,
				ChangePasswordRequestDTO.class);
		if (!obj.equals(DATAPARSINGMESSAGE)) {
			ChangePasswordRequestDTO retrieveInfo = (ChangePasswordRequestDTO) obj;
			String mobilePhone = retrieveInfo.getMobilePhone();
            String oAgentNo = retrieveInfo.getoAgentNo();

            //兼容老数据 如果欧单为空，则默认为付呗欧单编号
            if(StringUtils.isBlank(oAgentNo)){
            	oAgentNo = "100844";
            }

			setSession(request.getRemoteAddr(), session.getId(), mobilePhone);
			logger.info("[client_req]" + createJson(retrieveInfo));
			String newPassword = retrieveInfo.getNewPassWord();
			if (!isNotEmptyValidate(newPassword)
					|| !isNotEmptyValidate(mobilePhone)) {
				insertAppLogs(mobilePhone, "", "2002");
				message = EMPTYMESSAGE;
			} else {
				Userinfo userinfo = new Userinfo();
				userinfo.setLoginName(mobilePhone);
				userinfo.setLoginPwd(newPassword);
                userinfo.setoAgentNo(oAgentNo);
				// 根据账号修改用户密码
				int result = userInfoDao.update(userinfo);

				if (result == 1) {
					message = SUCCESSMESSAGE;
				} else {
					insertAppLogs(mobilePhone, "", "2008");
					message = FAILMESSAGE;
				}
			}
		} else {
			insertAppLogs("", "", "2001");
			message = DATAPARSINGMESSAGE;
		}
		// 解析要返回的信息
		int retCode = Integer.parseInt(message.split(":")[0]);
		String retMessage = message.split(":")[1];
		if (retMessage.equals("initialize")) {
			retMessage = "系统初始化";
		} else if (retMessage.equals("dataParsing")) {
			retMessage = "数据解析错误";
		} else if (retMessage.equals("success")) {
			retMessage = "修改成功";
		} else if (retMessage.equals("fail")) {
			retMessage = "修改失败";
		} else if (retMessage.equals("empty")) {
			retMessage = "信息不能为空";
		}
		ChangePasswordResponseDTO responseData = new ChangePasswordResponseDTO();
		responseData.setRetCode(retCode);
		responseData.setRetMessage(retMessage);
		String jsonString = createJsonString(responseData);
		logger.info("[app_rsp]" + createJson(responseData));
		return jsonString;
	}

	/**
	 * 修改密码异常
	 */
	@Override
	public String updatePasswordException(String changePasswordInfo)
			throws Exception {
		ChangePasswordResponseDTO responseData = new ChangePasswordResponseDTO();
		responseData.setRetCode(100);
		responseData.setRetMessage("系统异常");
		logger.info("[app_rsp]" + createJson(responseData));
		Object obj = parseJsonString(changePasswordInfo,
				ChangePasswordRequestDTO.class);
		if (!obj.equals(DATAPARSINGMESSAGE)) {
			ChangePasswordRequestDTO retrieveInfo = (ChangePasswordRequestDTO) obj;
			String mobilePhone = retrieveInfo.getMobilePhone();
			insertAppLogs(mobilePhone, "", "2086");
		} else {
			insertAppLogs("", "", "2001");
		}
		return createJsonString(responseData);
	}

	/**
	 * 商户信息更新
	 */
	public int merchantUpdate(PmsMerchantInfo pmsMerchantInfo) throws Exception {
		return this.pmsMerchantInfoDao
				.merchantUpdateByPmsMerchantInfo(pmsMerchantInfo);
	}

	/**
	 * 检索商户实名认证信息
	 */
	public String searchRealNameAuthenticationInformation(
			String realNameAuthenticationInfo,HttpSession session, HttpServletRequest request)
			throws Exception {
		setMethodSession(request.getRemoteAddr());
		logger.info("实名认证信息查询");
		String message = INITIALIZEMESSAGE;
		SearchRealNameAuthenticationInformationResponseDTO responseData = new SearchRealNameAuthenticationInformationResponseDTO();
		Object obj = parseJsonString(realNameAuthenticationInfo,
				SearchRealNameAuthenticationInformationRequestDTO.class);
		if (!obj.equals(DATAPARSINGMESSAGE)) {
			SearchRealNameAuthenticationInformationRequestDTO authenticationInformation = (SearchRealNameAuthenticationInformationRequestDTO) obj;
			String mobilePhone = authenticationInformation.getMobilePhone();
			setSession(request.getRemoteAddr(), request.getSession().getId(),
					mobilePhone);
			
			SessionInfo sessionInfo = (SessionInfo)session.getAttribute(SessionInfo.SESSIONINFO);
			String oAgentNo = sessionInfo.getoAgentNo();
			String mercId = sessionInfo.getMercId();
			
			logger.info("[client_req]" + createJson(authenticationInformation));
			if (isNotEmptyValidate(mobilePhone)) {
				// 根据手机号检索商户的实名认证信息
				PmsMerchantInfo merchantInfo = new PmsMerchantInfo();
				merchantInfo.setMobilephone(mobilePhone);
				merchantInfo.setCustomertype("3");
				merchantInfo.setoAgentNo(oAgentNo);
				List<PmsMerchantInfo> list = searchMerchantListInfo(merchantInfo);
				if (null != list && list.size() >= 1) {
					PmsMerchantInfo pmsMerchantInfo = list.get(0);
					String clr = pmsMerchantInfo.getClrMerc();
					String idNo = pmsMerchantInfo.getCrpIdNo();

					Map<String, String> tempMap = new HashMap<String, String>();
					MaskTypeUtil.getIndexShowValue(tempMap, MaskType.IDCARD,
							"idNo", idNo);

					responseData.setIdentityCard(tempMap.get("idNo_Show"));// 身份证

					String crpIdTyp = pmsMerchantInfo.getCrpIdTyp();

					PmsDictionary p = new PmsDictionary();
					p.setType("cardType");
					p.setKey(crpIdTyp);
					PmsDictionary pmsDictionary = pmsDictionaryDao
							.searchList(p).get(0);

					responseData.setCardType(pmsDictionary.getValue());// 证件类型
					responseData.setEmail(pmsMerchantInfo.getEmail());// 邮箱
					responseData.setMerchantName(pmsMerchantInfo.getMercName());// 商户名称
					responseData.setMercId(pmsMerchantInfo.getMercId());// 商户编号
					
					
					
					MaskTypeUtil.getIndexShowValue(tempMap, MaskType.PHONE, "mobilePhone",pmsMerchantInfo.getMobilephone());
					responseData.setMobilephone(tempMap.get("mobilePhone_Show"));//手机号
					
					MaskTypeUtil.getIndexShowValue(tempMap, MaskType.ACCOUNT,
							"accNo", clr);

					responseData.setAccNo(tempMap.get("accNo_Show")); // 卡号

					responseData.setName(pmsMerchantInfo.getCrpNm());// 商户姓名
					responseData.setBankName(pmsMerchantInfo.getBankname());// 开户行
					responseData.setAddress(pmsMerchantInfo.getAddress());// 商户地址
					responseData.setBackReason(pmsMerchantInfo.getRetMessage());// 打回原因
					
					Map<String, String>   paramMap = new HashMap<String, String>();
					
					paramMap.put("mercid",sessionInfo.getMercId());//商户编号
					paramMap.put("businesscode",TradeTypeEnum.merchantCollect.getTypeCode());//业务编号
					paramMap.put("oAgentNo",sessionInfo.getoAgentNo());//o单编号
					
					//查询商户费率 和  最 低收款金额
		            AppRateTypeAndAmount appRateTypeAndAmount = pmsAppAmountAndRateConfigDao.queryAmountAndRateInfoForShuaka(paramMap);
					
		          //商户收款费率
					if("1".equals(appRateTypeAndAmount.getIsTop())){//1表示封顶费率
						responseData.setBrushRate(new BigDecimal(appRateTypeAndAmount.getRate()).multiply(new BigDecimal(100)).stripTrailingZeros()+"%-"+new BigDecimal(appRateTypeAndAmount.getTopPoundage()).divide((new BigDecimal(100)))+"封顶");
					}else{
						responseData.setBrushRate(new BigDecimal(appRateTypeAndAmount.getRate()).multiply(new BigDecimal(100)).stripTrailingZeros()+"%");
					}
					
					
					List<AppRateConfig> hirdpartRateList = appRateConfigDao
							.getThirdpartRate(sessionInfo.getoAgentNo());
					
					if(hirdpartRateList != null && hirdpartRateList.size() > 0){
						responseData.setThirdPartRate((new BigDecimal(
								hirdpartRateList.get(0).getRate()).multiply(
								new BigDecimal(100)).stripTrailingZeros() + "%"));
					}

					//查询是否需要商户信息
                    PmsUnionpay pmsUnionpay = iPmsUnionpayDao.searchById(mercId);
                    if(pmsUnionpay == null){
                    }else{
                    	responseData.setMercId(pmsUnionpay.getMerchantCode());
                    	responseData.setMerchantName(pmsUnionpay.getMerchantName());
                    }

					message = SUCCESSMESSAGE;
				} else {
					insertAppLogs(mobilePhone, "", "2081");
					message = INVALIDMESSAGE;
				}
			} else {
				insertAppLogs(mobilePhone, "", "2002");
				message = EMPTYMESSAGE;
			}
		} else {
			insertAppLogs("", "", "2001");
			message = DATAPARSINGMESSAGE;
		}
		// 解析要返回的信息
		int retCode = Integer.parseInt(message.split(":")[0]);
		String retMessage = message.split(":")[1];
		if (retMessage.equals("initialize")) {
			retMessage = "系统初始化";
		} else if (retMessage.equals("dataParsing")) {
			retMessage = "数据解析错误";
		} else if (retMessage.equals("empty")) {
			retMessage = "信息不能为空";
		} else if (retMessage.equals("success")) {
			retMessage = "检索成功";
		} else if (retMessage.equals("invalid")) {
			retMessage = "商户未实名认证";
		}
		responseData.setRetCode(retCode);
		responseData.setRetMessage(retMessage);
		logger.info("[app_rsp]" + createJson(responseData));
		String jsonString = createJsonString(responseData);
		return jsonString;
	}

	/**
	 * 检索商户实名认证信息异常
	 */
	@Override
	public String searchRealNameAuthenticationInformationException(
			String realNameAuthenticationInfo) throws Exception {
		SearchRealNameAuthenticationInformationResponseDTO responseData = new SearchRealNameAuthenticationInformationResponseDTO();
		responseData.setIdentityCard("");
		responseData.setCardType("");
		responseData.setEmail("");
		responseData.setRetCode(100);
		responseData.setRetMessage("系统异常");
		responseData.setMerchantName("");
		logger.info("[app_rsp]" + createJson(responseData));
		Object obj = parseJsonString(realNameAuthenticationInfo,
				SearchRealNameAuthenticationInformationRequestDTO.class);
		if (!obj.equals(DATAPARSINGMESSAGE)) {
			SearchRealNameAuthenticationInformationRequestDTO authenticationInformation = (SearchRealNameAuthenticationInformationRequestDTO) obj;
			String mobilePhone = authenticationInformation.getMobilePhone();
			insertAppLogs(mobilePhone, "", "2088");
		} else {
			insertAppLogs("", "", "2001");
		}
		return createJsonString(responseData);
	}

	/**
	 * 保存实名认证信息
	 */
	@SuppressWarnings("unchecked")
	public String saveRealNameAuthenticationInformation(
			String realNameAuthenticationInfo, HttpSession session,
			HttpServletRequest request) throws Exception {
		setMethodSession(request.getRemoteAddr());
		logger.info("保存实名认证信息");
		HashMap<String, Object> map = validateNullAndParseData(session,
				realNameAuthenticationInfo,
				RealNameAuthenticationUploadFilesRequestDTO.class);
		String message = map.get("message").toString();
		PmsMerchantInfo merchantInfo = new PmsMerchantInfo();
		PmsMerchantBindingcardInfo bindingcardInfo = new PmsMerchantBindingcardInfo();
		if (message.equals(RetAppMessage.DATAANALYTICALSUCCESS)) {
			RealNameAuthenticationUploadFilesRequestDTO info = (RealNameAuthenticationUploadFilesRequestDTO) map
					.get("obj");
			SessionInfo sessionInfo = (SessionInfo) map.get("sessionInfo");
			setSession(request.getRemoteAddr(), session.getId(), sessionInfo
					.getMobilephone());
			
			String oAgentNo = sessionInfo.getoAgentNo();
			
			logger.info("[client_req]" + createJson(info));
			String name = info.getName();// 商户姓名
			String crpIdNo = info.getIdentityCard(); // 身份证号
			String crpIdType = info.getCardType(); // 证件类型
			String email = info.getEmail();// 邮箱
			String bankCardNumber = info.getAccNO(); // 银行卡号
			String headquartersName = info.getHeadBankName(); // 总行名称
			String provinceId = info.getProvinceId(); // 省id
			String cityId = info.getCityId(); // 市id
			String bankCode = info.getBankCode(); // 银行id
			String merchantName = info.getMerchantName();// 商户名称
			String address = info.getAddress();// 商户地址
			String merchantAddress = info.getMerchantAddress();// 商户地址
			String rate = info.getRate();// 商户费率
			
			if(address == null || "".equals(address)){
				address = merchantAddress;
			}
			
			AppRateConfig a = new AppRateConfig();
			String clearType = null;
			String lowestrate = null;
			a.setoAgentNo(oAgentNo);
			  
			PmsAgentInfo pmsAgentInfo = new PmsAgentInfo();
			pmsAgentInfo.setoAgentNo(oAgentNo);
			pmsAgentInfo.setAgentLevel("0");
			List<PmsAgentInfo> selectList2 = pmsAgentInfoDao.searchList(pmsAgentInfo);
			if(selectList2 != null && selectList2.size() > 0){
			  pmsAgentInfo = selectList2.get(0);
			  clearType = pmsAgentInfo.getClearType();
			  if("2".equals(clearType)){
				  String agentNumber = session.getAttribute("agentNumber").toString();
				  pmsAgentInfo = new PmsAgentInfo();
				  pmsAgentInfo.setAgentNumber(agentNumber);
				  List<PmsAgentInfo> selectList3 = pmsAgentInfoDao.searchList(pmsAgentInfo);
				  if(selectList3 != null && selectList3.size() > 0){
					  pmsAgentInfo = selectList3.get(0);
					  lowestrate = pmsAgentInfo.getLowestRate();
					  
					  a.setRateType(rate);
					  List list = appRateConfigDao.searchList(a);
						
					if(list != null && list.size() > 0){
						AppRateConfig appRateConfig = (AppRateConfig) list.get(0);
						String topPoundage = appRateConfig.getTopPoundage();
						String[] split = lowestrate.split("-");
						if(split.length > 1){
							String lowestrates = lowestrate.split("-")[1];
							if(topPoundage !=null && !"".equals(topPoundage)){
								BigDecimal topPoundage1 = new BigDecimal(topPoundage);
								BigDecimal lowestrate1 = new BigDecimal(lowestrates);
								if(topPoundage1.compareTo(lowestrate1)==-1){
									rate = null;
								}
							}else{
								rate = null;
							}
						}else{
							rate = null;
						}
					}
				  }
			  }
			}
			
			if(!isNotEmptyValidate(rate)){
				insertAppLogs(sessionInfo.getMobilephone(), "", "43");
				message = RetAppMessage.RATEEXIST;
			}
			// 必要信息的非空验证
			else if (!isNotEmptyValidate(merchantName)
					|| !isNotEmptyValidate(crpIdNo)
					|| !isNotEmptyValidate(bankCode)
					|| !isNotEmptyValidate(cityId)
					|| !isNotEmptyValidate(bankCardNumber)
					|| !isNotEmptyValidate(headquartersName)
					|| !isNotEmptyValidate(provinceId)) {
				insertAppLogs(sessionInfo.getMobilephone(), "", "2002");
				message = EMPTYMESSAGE;
			} else {
				// 第一步 验证身份证号，邮箱，银行卡
				if (checkIdCard(crpIdNo)) {
					if (email != null && !email.equals("")) {
						if (!checkEmail(email)) {
							insertAppLogs(sessionInfo.getMobilephone(), "",
									"2017");
							message = RetAppMessage.EMAILERROR;
						} else {
							// 判断邮箱是否存在
//							merchantInfo.setCustomertype("3");
//							merchantInfo.setEmail(email);
//							List<PmsMerchantInfo> searchList = pmsMerchantInfoDao
//									.searchList(merchantInfo);
//							if (searchList.size() >= 1) {
//								if (!searchList.get(0).getMercId().equals(
//										sessionInfo.getMercId())) {
//									insertAppLogs(sessionInfo.getMobilephone(),
//											"", "2091");
//									message = RetAppMessage.EMAILEXIST;
//								} else {
//									message = INVALIDMESSAGE;
//								}
//							} else {
//								message = INVALIDMESSAGE;
//							}
						}
					}
					if (message.equals(RetAppMessage.DATAANALYTICALSUCCESS)
							|| message.equals(INVALIDMESSAGE)) {
						if (checkBankCard(bankCardNumber)) {
							// 第二步 判断输入的身份证号是否已存在
							boolean mark1 = true;
							if ("1".equals(crpIdType)) {
								merchantInfo = new PmsMerchantInfo();
								merchantInfo.setCustomertype("3");
								merchantInfo.setCrpIdNo(crpIdNo);
								merchantInfo.setoAgentNo(oAgentNo);
								merchantInfo.setMercSts("60");
								List<PmsMerchantInfo> existsList = pmsMerchantInfoDao
										.searchList(merchantInfo);
								if (existsList.size() >= 1) {
									if (!existsList.get(0).getMercId().equals(
											sessionInfo.getMercId())) {
										mark1 = false;
										message = RetAppMessage.IDNUMBEREXIST;
										insertAppLogs(sessionInfo
												.getMobilephone(), "", "2092");
									}
								}
							}
							if (mark1) {
								// 第三步 验证银行卡是否被绑定
								String mobilePhone = sessionInfo
										.getMobilephone();
								String mercId = sessionInfo.getMercId();
								String id = sessionInfo.getId();
//								HashMap<String, String> hashMap = new HashMap<String, String>();
//								hashMap.put("mercId", id);
//								hashMap.put("clrMerc", bankCardNumber);
//								PmsMerchantBindingcardInfo bindingInfo = bindingcardInfoDao
//										.searchBankCardInfo(hashMap);
								PmsMerchantBindingcardInfo bindingInfo = null;
								if (null == bindingInfo) {
									// 第四步 判断是否是支持的银行卡,选择的银行与银行卡对应的银行是否匹配
									List<PayCmmtufit> cardList = payCmmtufitDao
											.searchCardInfoByBeforeSix(bankCardNumber
													.substring(0, 6)
													+ "%");
									if (null != cardList
											&& cardList.size() >= 1) {
										PayCmmtufit bankInfo = cardList.get(0);
										String cardName = bankInfo.getCrdNm();
										String bankName = bankInfo.getBnkName();
										String bnkCode = bankInfo.getBnkCode();
										if (headquartersName.equals(bankName)
												|| bankName
														.indexOf(headquartersName) != -1
												|| headquartersName
														.substring(2).equals(
																bankName)) {
											// 第五步 将商户的账户启用
											TAccAccount account = new TAccAccount();
											account.setAccNum(sessionInfo
													.getAccNum());// 帐户
											account.setStatus("1");// 1=启用
											account.setModifiedTime(new Date()); // 修改时间
											account.setName(name + "："
													+ mobilePhone); // 持卡人+手机号
											account.setoAgentNo(oAgentNo);
											if (accountDao.update(account) == 1) {
												// 第六步 更新商户曾上传过的图片及保存新上传的图片
												boolean flag = false;
												List<PmsImage> updateList = imageDao
														.searchUploadFiles(mercId);
												if (null != updateList
														&& updateList.size() >= 1) {
													for (int i = 0; i < updateList
															.size(); i++) {
														int lastIndex = updateList
																.get(i)
																.getPath()
																.lastIndexOf(
																		"_") + 1;
														if (updateList
																.get(i)
																.getPath()
																.substring(
																		lastIndex)
																.equals(
																		"head.jpg")) {
															updateList
																	.remove(i);
															break;
														}
													}
													if (null != updateList
															&& updateList
																	.size() >= 1) {
														// 上传文件更新成功
														if (imageDao
																.updateUploadFiles(updateList) == updateList
																.size()) {
															flag = true;
														}
													}
												} else {
													flag = true;
												}
												if (flag) {
													List<PmsImage> list = (List<PmsImage>) session
															.getAttribute("fileList");
													String crpIdNofo = crpIdNo
															.substring(0, 4);
													String mercNum = mercId
															.substring(0, 3)
															+ crpIdNofo
															+ mercId
																	.substring(7);
													merchantInfo = new PmsMerchantInfo();
													merchantInfo
															.setMercId(mercNum);
													merchantInfo
															.setCustomertype("3");
													boolean mark = true;
													List<PmsMerchantInfo> idList = searchMerchantListInfo(merchantInfo);
													if (null != idList
															&& idList.size() == 1) {
														if (!idList
																.get(0)
																.getMercId()
																.equals(
																		sessionInfo
																				.getMercId())) {
															mark = false;
														}
													}
													String newMercId = mercNum;
													while (!mark) {
														// 判断生成的编号是否已存在
														newMercId = mercNum
																.substring(0, 7)
																+ createRandomNumber();
														merchantInfo
																.setMercId(newMercId);
														merchantInfo
																.setCustomertype("3");
														List<PmsMerchantInfo> idListInfo = searchMerchantListInfo(merchantInfo);
														if (null != idListInfo
																&& idListInfo
																		.size() == 1) {
															mark = false;
														} else {
															mark = true;
														}
													}
													int num = imageDao
															.saveUploadFiles(
																	list,
																	mercId);
													if (num == list.size()) {
														session
																.removeAttribute("fileList");
														int rzResult = 0;
														if (newMercId
																.equals(sessionInfo
																		.getMercId())) {
															// 将之前的银行卡替换掉
															bindingcardInfo
																	.setMercId(id);
															bindingcardInfo
																	.setClrMerc(bankCardNumber);
															bindingcardInfo
																	.setSettlementname(name);
															bindingcardInfo
																	.setHeadquartersbank(headquartersName);
															bindingcardInfo
																	.setCardname(cardName);
															bindingcardInfo
																	.setShortbankcardnumber("尾号"
																			+ bankCardNumber
																					.substring(bankCardNumber
																							.length() - 4));
															int index = cardName
																	.indexOf("(");
															if (index != -1) {
																cardName = cardName
																		.substring(
																				index + 1)
																		.replace(
																				")",
																				"");
															}
															bindingcardInfo
																	.setShortbankcardname(cardName);
															bindingcardInfo
																	.setProvinceid(provinceId);
															bindingcardInfo
																	.setCityid(cityId);
															bindingcardInfo
																	.setBankcode(bankCode);
															bindingcardInfo
																	.setBanksysnumber(bnkCode);
															rzResult = bindingcardInfoDao
																	.update(bindingcardInfo);
														} else {
															// 第七步 保存新添加的卡信息
															bindingcardInfo
																	.setMercId(id);
															bindingcardInfo
																	.setClrMerc(bankCardNumber);
															bindingcardInfo
																	.setSettlementname(name);
															bindingcardInfo
																	.setHeadquartersbank(headquartersName);
															bindingcardInfo
																	.setCardname(cardName);
															bindingcardInfo
																	.setShortbankcardnumber("尾号"
																			+ bankCardNumber
																					.substring(bankCardNumber
																							.length() - 4));
															int index = cardName
																	.indexOf("(");
															if (index != -1) {
																cardName = cardName
																		.substring(
																				index + 1)
																		.replace(
																				")",
																				"");
															}
															bindingcardInfo
																	.setShortbankcardname(cardName);
															bindingcardInfo
																	.setProvinceid(provinceId);
															bindingcardInfo
																	.setCityid(cityId);
															bindingcardInfo
																	.setBankcode(bankCode);
															bindingcardInfo
																	.setBanksysnumber(bnkCode);
															rzResult = bindingcardInfoDao
																	.insert(bindingcardInfo);
														}
														if (rzResult == 1) {
															// 第八步 更新商户信息
															merchantInfo = new PmsMerchantInfo();
															merchantInfo
																	.setCustomertype("3");
															merchantInfo
																	.setCrpIdNo(crpIdNo);
															merchantInfo
																	.setShortname(merchantName);
															merchantInfo
																	.setMercName(merchantName);
															merchantInfo
																	.setCrpIdTyp(crpIdType);
															merchantInfo
																	.setEmail(email);
															merchantInfo
																	.setMobilephone(mobilePhone);
															merchantInfo
																	.setMercSts("30"); // 核实商户
															merchantInfo
																	.setBankname(headquartersName);// 开户行
															merchantInfo
																	.setSettlementname(name);// 结算账户名
															merchantInfo
																	.setClrMerc(bankCardNumber);// 结算账号
															merchantInfo
																	.setCrpNm(name);// 法人代表
															merchantInfo
																	.setCheckmerdate(sdf
																			.format(new Date()));// 打成核实商户时间
															merchantInfo
																	.setBanksysnumber(bnkCode);// 开户行支付系统行号
															// merchantInfo.setMercId(newMercId);//
															// 商户id
															merchantInfo
																	.setClearType(new BigDecimal(
																			1));// 0:T+0;
															// 1:T+1;
															// 2:T+N
															merchantInfo
																	.setStatus(new BigDecimal(
																			1));// 1.认证中
															merchantInfo
																	.setRetMessage(""); // 将之前不通过原因清空
															merchantInfo
																	.setAddress(address);// 地址
															merchantInfo.setoAgentNo(oAgentNo);
															int result = pmsMerchantInfoDao
																	.merchantUpdateByPmsMerchantInfo(merchantInfo);
															if (result == 1) {
																// sessionInfo.setMercId(newMercId);//
																// 更新session里的商户id值
																sessionInfo
																		.setShortname(merchantName);// 商户名称

																// 修改商户费率
																PmsAppAmountAndRateConfig p = new PmsAppAmountAndRateConfig();
																p
																		.setMercId(mercId);
																p
																		.setStatus("1");
																p
																		.setRatetype(rate);
																p
																		.setBusinesscode(TradeTypeEnum.drawMoney
																				.getTypeCode());

																int count = amountAndRateConfigDao
																		.updateByMercId(p);
																if (count > 0) {
																	// 修改商户费率
																	PmsAppAmountAndRateConfig p1 = new PmsAppAmountAndRateConfig();
																	p1
																			.setMercId(mercId);
																	p1
																			.setStatus("1");
																	p1
																			.setBusinesscode(TradeTypeEnum.drawMoney
																					.getTypeCode());
																	int count1 = amountAndRateConfigDao
																			.update(p1);
																	if (count1 == 1) {
																		Userinfo userinfo = new Userinfo();
																		userinfo
																				.setLoginName(mobilePhone);
																		userinfo
																				.setTrueName(name);
																		userinfo.setoAgentNo(oAgentNo);
																		int count2 = userInfoDao
																				.update(userinfo);
																		if (count2 == 1) {
																			message = SUCCESSMESSAGE;
																		} else {
																			insertAppLogs(
																					mobilePhone,
																					"",
																					"2014");
																			message = FAILMESSAGE;
																		}
																	} else {
																		insertAppLogs(
																				mobilePhone,
																				"",
																				"2014");
																		message = FAILMESSAGE;
																	}
																} else {
																	insertAppLogs(
																			mobilePhone,
																			"",
																			"2014");
																	message = FAILMESSAGE;
																}
															} else {
																insertAppLogs(
																		mobilePhone,
																		"",
																		"2014");
																message = FAILMESSAGE;
															}
														} else {
															insertAppLogs(
																	mobilePhone,
																	"", "2013");
															message = RetAppMessage.BINDINGBANKCARDSAVEFAILED;
														}
													} else {
														insertAppLogs(
																mobilePhone,
																"", "2012");
														session
																.removeAttribute("fileList");
														message = RetAppMessage.IMAGEUPLOADFAIL;
													}
												} else {
													insertAppLogs(mobilePhone,
															"", "2011");
													session
															.removeAttribute("fileList");
													message = RetAppMessage.IMAGEUPLOADFAIL;
												}
											} else {
												insertAppLogs(mobilePhone, "",
														"2010");
												message = FAILMESSAGE;
											}
										} else {
											insertAppLogs(mobilePhone, "",
													"2094");
											message = RetAppMessage.BANKCARDNUMBERANDBANKNAMENOMATCH;
										}
									} else {
										insertAppLogs(mobilePhone, "", "2009");
										message = RetAppMessage.BANKCARDISNOTSUPPORTED;
									}
								} else {
									if (bindingInfo.getMercId().equals(id)) {
										boolean flag = false;
										List<PmsImage> updateList = imageDao
												.searchUploadFiles(mercId);
										if (null != updateList
												&& updateList.size() >= 1) {
											for (int i = 0; i < updateList
													.size(); i++) {
												int lastIndex = updateList.get(
														i).getPath()
														.lastIndexOf("_") + 1;
												if (updateList.get(i).getPath()
														.substring(lastIndex)
														.equals("head.jpg")) {
													updateList.remove(i);
													break;
												}
											}
											if (null != updateList
													&& updateList.size() >= 1) {
												// 上传文件更新成功
												if (imageDao
														.updateUploadFiles(updateList) == updateList
														.size()) {
													flag = true;
												}
											}
										} else {
											flag = true;
										}
										if (flag) {
											List<PmsImage> list = (List<PmsImage>) session
													.getAttribute("fileList");
											int num = imageDao.saveUploadFiles(
													list, sessionInfo
															.getMercId());
											if (num == list.size()) {
												session
														.removeAttribute("fileList");
												// 更新绑定的银行卡信息
												bindingcardInfo.setMercId(id);
												bindingcardInfo.setClrMerc(bankCardNumber);
												bindingcardInfo
														.setSettlementname(name);
												bindingcardInfo
														.setHeadquartersbank(headquartersName);
												bindingcardInfo
														.setProvinceid(provinceId);
												bindingcardInfo
														.setCityid(cityId);
												if (bindingcardInfoDao
														.update(bindingcardInfo) == 1) {
													// 修改商户信息
													merchantInfo = new PmsMerchantInfo();
													merchantInfo
															.setCustomertype("3");
													merchantInfo
															.setCrpIdNo(crpIdNo);
													merchantInfo
													.setShortname(merchantName);
													merchantInfo
															.setMercName(merchantName);
													merchantInfo.setAddress(address);
													merchantInfo
															.setMobilephone(mobilePhone);
													merchantInfo
															.setCrpIdTyp(crpIdType);
													merchantInfo
															.setMercSts("30"); // 已认证
													merchantInfo.setCrpNm(name);// 法人代表
													merchantInfo
															.setCheckmerdate(sdf
																	.format(new Date()));// 打成核实商户时间
													merchantInfo
															.setStatus(new BigDecimal(
																	1));// 1:app默认审核成功
													merchantInfo
															.setEmail(email);
													merchantInfo
															.setBankname(headquartersName);// 开户行
													merchantInfo
															.setSettlementname(name);// 结算账户名
													merchantInfo.setClrMerc(bankCardNumber);//结算卡号
													merchantInfo
															.setClearType(new BigDecimal(
																	1));// 0:T+0;
													// 1:T+1;
													// 2:T+N
													merchantInfo
															.setRetMessage(""); // 将之前不通过原因清空
													merchantInfo
													.setAddress(address);// 地址
													merchantInfo.setoAgentNo(oAgentNo);
													int result = pmsMerchantInfoDao
															.merchantUpdateByPmsMerchantInfo(merchantInfo);
													if (result == 1) {
														sessionInfo
																.setShortname(merchantName);// 商户名称

														// 修改商户费率
														PmsAppAmountAndRateConfig p = new PmsAppAmountAndRateConfig();
														p.setMercId(mercId);
														p.setStatus("1");
														p.setRatetype(rate);
														p
																.setBusinesscode(TradeTypeEnum.drawMoney
																		.getTypeCode());

														int count = amountAndRateConfigDao
																.updateByMercId(p);
														if (count > 0) {
															// 修改商户费率
															PmsAppAmountAndRateConfig p1 = new PmsAppAmountAndRateConfig();
															p1
																	.setMercId(mercId);
															p1.setStatus("1");
															p1
																	.setBusinesscode(TradeTypeEnum.drawMoney
																			.getTypeCode());
															int count1 = amountAndRateConfigDao
																	.update(p1);
															if (count1 == 1) {
																Userinfo userinfo = new Userinfo();
																userinfo
																		.setLoginName(mobilePhone);
																userinfo
																		.setTrueName(name);
																userinfo.setoAgentNo(oAgentNo);
																int count2 = userInfoDao
																		.update(userinfo);
																if (count2 == 1) {
																	message = SUCCESSMESSAGE;
																} else {
																	insertAppLogs(
																			mobilePhone,
																			"",
																			"2014");
																	message = FAILMESSAGE;
																}
															} else {
																insertAppLogs(
																		mobilePhone,
																		"",
																		"2014");
																message = FAILMESSAGE;
															}
														} else {
															insertAppLogs(
																	mobilePhone,
																	"", "2014");
															message = FAILMESSAGE;
														}
													} else {
														insertAppLogs(
																mobilePhone,
																"", "2014");
														message = FAILMESSAGE;
													}
												} else {
													insertAppLogs(mobilePhone,
															"", "2013");
													message = RetAppMessage.BINDINGBANKCARDSAVEFAILED;
												}
											} else {
												insertAppLogs(mobilePhone, "",
														"2012");
												session
														.removeAttribute("fileList");
												message = RetAppMessage.IMAGEUPLOADFAIL;
											}
										} else {
											insertAppLogs(mobilePhone, "",
													"2011");
											session.removeAttribute("fileList");
											message = RetAppMessage.IMAGEUPLOADFAIL;
										}
									} else {
										insertAppLogs(mobilePhone, "", "2093");
										message = RetAppMessage.BANKCARDISBOUND;
									}
								}
							}
						} else {
							insertAppLogs(sessionInfo.getMobilephone(), "",
									"2015");
							message = RetAppMessage.BANKCARDNUMBERISILLEGAL;
						}
					}
				} else {
					insertAppLogs(sessionInfo.getMobilephone(), "", "2016");
					message = RetAppMessage.IDNUMBERERROR;
				}
			}
		}
		int retCode = Integer.parseInt(message.split(":")[0]);
		String retMessage = message.split(":")[1];
		retMessage = RetAppMessage.parseMessageCode(retMessage);
		RealNameAuthenticationUploadFilesResponseDTO responseData = new RealNameAuthenticationUploadFilesResponseDTO();
		responseData.setRetCode(retCode);
		responseData.setRetMessage(retMessage);
		String jsonString = createJsonString(responseData);
		logger.info("[app_rsp]" + createJson(responseData));
		return jsonString;
	}

	/**
	 * 保存实名认证信息异常
	 */
	@Override
	public String saveRealNameAuthenticationInformationException(
			HttpSession session) throws Exception {
		RealNameAuthenticationUploadFilesResponseDTO responseData = new RealNameAuthenticationUploadFilesResponseDTO();
		responseData.setRetCode(100);
		responseData.setRetMessage("系统异常");
		logger.info("[app_rsp]" + createJson(responseData));
		insertAppLogs(((SessionInfo) session
				.getAttribute(SessionInfo.SESSIONINFO)).getMobilephone(), "",
				"2089");
		return createJsonString(responseData);
	}

	/**
	 * 修改密码验证确认
	 */
	public String changePasswordValidationConfirm(
			String changePasswordValidationConfirmInfo, HttpSession session,
			HttpServletRequest request) throws Exception {
		setMethodSession(request.getRemoteAddr());
		logger.info("个人信息 修改密码");
		String message = INITIALIZEMESSAGE;
		Object obj = parseJsonString(changePasswordValidationConfirmInfo,
				ChangePasswordValidationConfirmRequestDTO.class);
		SessionInfo sessionInfo = (SessionInfo)session.getAttribute(SessionInfo.SESSIONINFO);
		String oAgentNo = sessionInfo.getoAgentNo();
		if (!obj.equals(DATAPARSINGMESSAGE)) {
			ChangePasswordValidationConfirmRequestDTO confirmInfo = (ChangePasswordValidationConfirmRequestDTO) obj;
			String mobilePhone = confirmInfo.getMobilePhone();
			setSession(request.getRemoteAddr(), session.getId(), mobilePhone);
			logger.info("[client_req]" + createJson(confirmInfo));
			// 根据此手机号检索商户信息
			Userinfo userinfo = new Userinfo();
			userinfo.setLoginName(mobilePhone);
			userinfo.setoAgentNo(oAgentNo);

			Userinfo searchUserinfo = userInfoDao.searchList(userinfo).get(0);
			if (null != searchUserinfo) {
				String password = searchUserinfo.getLoginPwd();

				String oldPassword = confirmInfo.getOldPassword();
				String newPassword = confirmInfo.getNewPassword();
				// 非空验证
				if (!isNotEmptyValidate(oldPassword)
						|| !isNotEmptyValidate(newPassword)
						|| !isNotEmptyValidate(mobilePhone)) {
					insertAppLogs(mobilePhone, "", "2002");
					message = EMPTYMESSAGE;
				} else {
					if (MessageDigest.isEqual(StringTools
							.hexStringToBytes(password), StringTools
							.hexStringToBytes(oldPassword))) {
						// 密码输入正确 判断验证码是否与服务器接收的一致
						Userinfo u = new Userinfo();
						u.setLoginName(mobilePhone);
						u.setLoginPwd(newPassword);
						u.setoAgentNo(oAgentNo);
						
						// 根据手机号修改用户密码
						int result = userInfoDao.update(u);
						if (result == 1) {
							message = SUCCESSMESSAGE;
						} else {
							insertAppLogs(mobilePhone, "", "2008");
							message = FAILMESSAGE;
						}
					} else {
						logger.info("商户注册密码：" + password);
						insertAppLogs(mobilePhone, "", "2083");
						message = ERRORMESSAGE + "p";
					}
				}
			} else {
				insertAppLogs(mobilePhone, "", "2006");
				message = EXISTMESSAGE;
			}
		} else {
			insertAppLogs("", "", "2001");
			message = DATAPARSINGMESSAGE;
		}
		// 解析要返回的信息
		int retCode = Integer.parseInt(message.split(":")[0]);
		String retMessage = message.split(":")[1];
		if (retMessage.equals("initialize")) {
			retMessage = "系统初始化";
		} else if (retMessage.equals("dataParsing")) {
			retMessage = "数据解析错误";
		} else if (retMessage.equals("exist")) {
			retMessage = "手机号未注册";
		} else if (retMessage.equals("empty")) {
			retMessage = "信息不能为空";
		} else if (retMessage.equals("errorp")) {
			retMessage = "旧密码错误";
		} else if (retMessage.equals("failure")) {
			retMessage = "验证码失效，请重新获取";
		} else if (retMessage.equals("error")) {
			retMessage = "验证码输入错误";
		} else if (retMessage.equals("success")) {
			retMessage = "修改成功";
		} else if (retMessage.equals("fail")) {
			retMessage = "修改失败";
		}
		// 向客户端返回判断信息
		ChangePasswordValidationConfirmResponseDTO responseData = new ChangePasswordValidationConfirmResponseDTO();
		responseData.setRetCode(retCode);
		responseData.setRetMessage(retMessage);
		String jsonString = createJsonString(responseData);
		logger.info("[app_rsp]" + createJson(responseData));
		return jsonString;
	}

	/**
	 * 修改密码验证确认异常
	 */
	@Override
	public String changePasswordValidationConfirmException(
			String changePasswordValidationConfirmInfo) throws Exception {
		ChangePasswordValidationConfirmResponseDTO responseData = new ChangePasswordValidationConfirmResponseDTO();
		responseData.setRetCode(100);
		responseData.setRetMessage("系统异常");
		logger.info("[app_rsp]" + createJson(responseData));
		Object obj = parseJsonString(changePasswordValidationConfirmInfo,
				ChangePasswordValidationConfirmRequestDTO.class);
		if (!obj.equals(DATAPARSINGMESSAGE)) {
			ChangePasswordValidationConfirmRequestDTO confirmInfo = (ChangePasswordValidationConfirmRequestDTO) obj;
			String mobilePhone = confirmInfo.getMobilePhone();
			insertAppLogs(mobilePhone, "", "2087");
		} else {
			insertAppLogs("", "", "2001");
		}
		return createJsonString(responseData);
	}

	@Override
	public int UpdatePmsMerchantInfo(PmsMerchantInfo pmsMerchantInfo)
			throws Exception {
		return pmsMerchantInfoDao.UpdatePmsMerchantInfo(pmsMerchantInfo);
	}

	@Override
	public int updataPay(Map<String, String> map) {
		// TODO Auto-generated method stub
		return pmsMerchantInfoDao.updataPay(map);
	}

	@Override
	public int updataPayT1(Map<String, String> map) {
		// TODO Auto-generated method stub
		return pmsMerchantInfoDao.updataPayT1(map);
	}

}