package xdt.service.impl;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Service;

import xdt.baidu.TiXianTaskThread;
import xdt.dao.IAmountLimitControlDao;
import xdt.dao.IAppRateConfigDao;
import xdt.dao.IMerchantMineDao;
import xdt.dao.IPmsAppAmountAndRateConfigDao;
import xdt.dao.IPmsAppBusinessConfigDao;
import xdt.dao.IPmsAppTransInfoDao;
import xdt.dao.IPmsMerchantInfoDao;
import xdt.dao.IPospTransInfoDAO;
import xdt.dto.BusinessInfoRequestAndResponseDTO;
import xdt.dto.DrawMoneyAccRequestAndResponseDTO;
import xdt.dto.MerchantMineRequestAndResponseDTO;
import xdt.model.AppRateConfig;
import xdt.model.AppRateTypeAndAmount;
import xdt.model.PmsAppBusinessConfig;
import xdt.model.PmsAppTransInfo;
import xdt.model.MerchantMinel;
import xdt.model.PospTransInfo;
import xdt.model.ResultInfo;
import xdt.model.SessionInfo;
import xdt.model.WeChatPublicNo;
import xdt.schedule.ThreadPool;
import xdt.service.IMerchantCollectMoneyService;
import xdt.service.IMerchantMineService;
import xdt.service.INewsInfoService;
import xdt.service.IPublicTradeVerifyService;
import xdt.util.AES;
import xdt.util.Constants;
import xdt.util.MaskType;
import xdt.util.MaskTypeUtil;
import xdt.util.RateTypeEnum;
import xdt.util.TradeTypeEnum;
import xdt.util.UtilDate;
import xdt.util.UtilMethod;


/**
 * 商户个人账户信息  serviceImpl
 * wumeng 20150525
 */
@Service
public class MerchantMineServiceImpl extends BaseServiceImpl implements IMerchantMineService {
	
	private  Logger logger=Logger.getLogger(MerchantMineServiceImpl.class);
	
	@Resource
	private IPmsMerchantInfoDao pmsMerchantInfoDao; // 商户信息服务层
	@Resource
	private IMerchantMineDao merchantMineDao;
	@Resource
	public IPmsAppTransInfoDao pmsAppTransInfoDao; // 业务配置服务层
	@Resource
	private IMerchantCollectMoneyService  merchantCollectMoneyService;// 商户收款service
	@Resource
	private IPospTransInfoDAO pospTransInfoDAO;//流水
	@Resource
    private IPmsAppAmountAndRateConfigDao pmsAppAmountAndRateConfigDao;//商户费率配置
	@Resource
	private IAppRateConfigDao appRateConfigDao;//费率
    @Resource
    private INewsInfoService newsInfoService;
    @Resource
	private IAmountLimitControlDao  amountLimitControlDao;//最大值最小值总开关判断
    
    @Resource
	private IPublicTradeVerifyService publicTradeVerifyService;
    
    @Resource
	private IPmsAppBusinessConfigDao pmsAppBusinessConfigDao; // 业务配置服务层
    
	/**
	 * 查看商户在客户端显示图片、手机号、账户金额等   个人信息
	 * wumeng  20150525
	 * @param param
	 * @param response
	 * @param session
	 * @throws Exception 
	 */
	@Override
	public String queryMineAcc(String param,SessionInfo  sessionInfo) throws Exception {
		logger.info("查看账户信息接收app参数: "+param+"时间："+UtilDate.getDateFormatter());
		
		MerchantMineRequestAndResponseDTO  merchantMineRequestAndResponseDTO = (MerchantMineRequestAndResponseDTO)parseJsonString(param,MerchantMineRequestAndResponseDTO.class);
		
		String  result = "";
		if(!merchantMineRequestAndResponseDTO.equals(DATAPARSINGMESSAGE)){//判断解析数据是否成功
			
			String mercid = sessionInfo.getMercId();//商户编号
			
			Map<String, String> map = pmsMerchantInfoDao.queryMercuryInfo(mercid);
			
			Map<String,String> paramMap = new HashMap<String, String>();
            paramMap.put("mercid", mercid);
            paramMap.put("businesscode", TradeTypeEnum.merchantCollect.getTypeCode());
            paramMap.put("oAgentNo", sessionInfo.getoAgentNo());
            
            //查询商户费率 和  最 低收款金额
            AppRateTypeAndAmount appRateTypeAndAmount = pmsAppAmountAndRateConfigDao.queryAmountAndRateInfoForShuaka(paramMap);

            //查询消息中心中是否有未读的消息
            String readFlag = newsInfoService.haveUnReadMsg(mercid,sessionInfo.getoAgentNo());
            if(map!=null){
				merchantMineRequestAndResponseDTO.setRetCode("0");//返回码
				merchantMineRequestAndResponseDTO.setRetMessage("查询成功");//返回信息
				merchantMineRequestAndResponseDTO.setMerchantName(map.get("MERC_NAME"));//商户名
				merchantMineRequestAndResponseDTO.setMerchantNo(map.get("MERC_ID"));//商户编号
				merchantMineRequestAndResponseDTO.setHeadImage(map.get("IMAGE_URL"));//商户图像
				merchantMineRequestAndResponseDTO.setMobilePhone(map.get("MOBILEPHONE"));//手机号
				merchantMineRequestAndResponseDTO.setAccBalanceAmt(new BigDecimal(map.get("BALANCE")).setScale(0, RoundingMode.HALF_DOWN).toString());//账户余额
				merchantMineRequestAndResponseDTO.setAttestationSign(map.get("STATUS"));//实民认证标记，0:未认证 1:认证中 2：认证成功 3:认证失败
                merchantMineRequestAndResponseDTO.setRead(readFlag);
				String pealName= pmsMerchantInfoDao.queryMercuryStatus(mercid);//查询商户是否实名认证
				
				if(AUTHENTICATIONFLAG.equals(pealName)){//已经实名认证
					//商户收款费率
					if("1".equals(appRateTypeAndAmount.getIsTop())){//1表示封顶费率
						merchantMineRequestAndResponseDTO.setRate(new BigDecimal(appRateTypeAndAmount.getRate()).multiply(new BigDecimal(100)).stripTrailingZeros()+"%-"+new BigDecimal(appRateTypeAndAmount.getTopPoundage()).divide((new BigDecimal(100)))+"封顶");
					}else{
						merchantMineRequestAndResponseDTO.setRate(new BigDecimal(appRateTypeAndAmount.getRate()).multiply(new BigDecimal(100)).stripTrailingZeros()+"%");
					}
				}
				
				// 查询用户的业务列表
				List<PmsAppBusinessConfig> businessList = pmsAppBusinessConfigDao.searchBusinessInfo(mercid);

				for (PmsAppBusinessConfig p : businessList) {
					if (p.getBusinessname().equals("违章查询")) {
						/*
						 * 此处使用AES-128-ECB加密模式，key需要为16位。
						 */
						String cKey = "1234567890123456";

						String externalId = map.get("EXTERNALID");
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
				
				Map<String,PmsAppBusinessConfig> map1 = new HashMap<String, PmsAppBusinessConfig>();
				
				PmsAppBusinessConfig pab = null;
				
				for (PmsAppBusinessConfig p : businessList) {
					if(!p.getStatus().equals(p.getStatus1()) && "1".equals(p.getStatus())){
						p.setStatus(p.getStatus1());
						p.setMessage(p.getMessage1());
						p.setExternalurl(null);
					}
					map1.put(p.getModulecode(), p);
				}
				
				merchantMineRequestAndResponseDTO.setMap(map1);
				
			}else{
				merchantMineRequestAndResponseDTO.setRetCode("1");//返回码
				merchantMineRequestAndResponseDTO.setRetMessage("查询失败");//返回信息
			}
			
			result = createJsonString(merchantMineRequestAndResponseDTO);
		}
		
		
		logger.info("查看账户信息返回app参数: "+result+"时间："+UtilDate.getDateFormatter());
		
		return result;
	}

	
	/**
	 * 商户在客户端查询绑定的卡信息   提现页面显示使用
	 * wumeng  20150525
	 * @param param
	 * @param response
	 * @param session
	 */
	@Override
	public String queryDrawMoneyAcc(String param,SessionInfo sessionInfo)throws Exception {
		logger.info("商户在客户端查看绑定的卡列表接收app参数: "+param+"时间："+UtilDate.getDateFormatter());
		DrawMoneyAccRequestAndResponseDTO  drawMoneyAccRequestAndResponseDTO =(DrawMoneyAccRequestAndResponseDTO)parseJsonString(param,DrawMoneyAccRequestAndResponseDTO.class);
		String  result = "";
		if(!drawMoneyAccRequestAndResponseDTO.equals(DATAPARSINGMESSAGE)){//判断解析数据是否成功
			
			Map<String, String>   paramMap = new HashMap<String, String>();
			
			
			
			int nowTime = Integer.parseInt(UtilDate.getTXDateTime());
				
			if(nowTime>=2300){//提现超过23点算第二天
				paramMap.put("date", UtilDate.addDay(UtilDate.strToDate(UtilDate.getDayDate()),1).replace("-", ""));//时间加一天
				paramMap.put("taatransDate", UtilDate.addDay(UtilDate.strToDate(UtilDate.getDayDate()),1));//时间加1天
			}else{
				paramMap.put("date", UtilDate.getDate());//提款时间YYYYMMDD
				paramMap.put("taatransDate", UtilDate.getDayDate());//修改余额记录时间YYYY-MM-DD
			}
			
			paramMap.put("mercid",sessionInfo.getMercId());//商户编号
			paramMap.put("businesscode",TradeTypeEnum.drawMoney.getTypeCode());//业务编号
			paramMap.put("oAgentNo",sessionInfo.getoAgentNo());//O单编号
			
			
			Map<String, String> resultMap= merchantMineDao.queryDrawMoneyAcc(paramMap);
			if(resultMap!=null){
				drawMoneyAccRequestAndResponseDTO.setRetCode("0");//返回码
				drawMoneyAccRequestAndResponseDTO.setRetMessage("查询成功");//返回信息
				drawMoneyAccRequestAndResponseDTO.setBankName(resultMap.get("BANKNAME"));// 开卡行名
				
				MaskTypeUtil.getIndexShowValue(resultMap, MaskType.ACCOUNT, "CLR_MERC",resultMap.get("CLR_MERC") );
				drawMoneyAccRequestAndResponseDTO.setAccNo_Show(resultMap.get("CLR_MERC_Show"));//卡号(遮盖),显示用
				drawMoneyAccRequestAndResponseDTO.setAccNo(resultMap.get("CLR_MERC"));//卡号遮盖索引值，传递用
				
				MaskTypeUtil.getIndexShowValue(resultMap, MaskType.ACCNONAME, "SETTLEMENTNAME",resultMap.get("SETTLEMENTNAME"));
				drawMoneyAccRequestAndResponseDTO.setAccName_Show(resultMap.get("SETTLEMENTNAME_Show"));//持卡人姓名(遮盖),显示用    
				drawMoneyAccRequestAndResponseDTO.setAccName(resultMap.get("SETTLEMENTNAME"));//持卡人遮盖索引值，传递用   
				
				if("".equals(resultMap.get("BALANCE"))||resultMap.get("BALANCE")==null){
					drawMoneyAccRequestAndResponseDTO.setAccBalance("0");//当天交易余额
				}else{
					drawMoneyAccRequestAndResponseDTO.setAccBalance(resultMap.get("BALANCE"));//当天交易余额
				}
				
				  
				drawMoneyAccRequestAndResponseDTO.setDrawMoneyTimes(resultMap.get("NUMBEROFTIMES"));//本日可提款次数   
				
				int maxTimes = Integer.parseInt(resultMap.get("NUMBEROFTIMES"));
				int alreadyTimes = Integer.parseInt(resultMap.get("ALREADYTIMES"));
				int caseTimes = maxTimes-alreadyTimes;
				drawMoneyAccRequestAndResponseDTO.setRemainDrawMoneyTimes(String.valueOf(caseTimes));////剩余提款次数      
				drawMoneyAccRequestAndResponseDTO.setChargeRate(resultMap.get("RATE"));//手续费费率     
				drawMoneyAccRequestAndResponseDTO.setTranMinAmt(resultMap.get("MIN_AMOUNT"));//每笔最少金额   
				drawMoneyAccRequestAndResponseDTO.setTranMaxAmt(resultMap.get("MAX_AMOUNT"));//每笔最大金额   
				drawMoneyAccRequestAndResponseDTO.setAlerts(resultMap.get("ACCOUNT_TIME"));//到账时间提示信息，如：下个工作日24点到账
				drawMoneyAccRequestAndResponseDTO.setBottomPoundage(resultMap.get("BOTTOM_POUNDAGE"));//最低手续费
			}else{
				drawMoneyAccRequestAndResponseDTO.setRetCode("1");//返回码
				drawMoneyAccRequestAndResponseDTO.setRetMessage("查询失败");//返回信息
			}
				
			result = createJsonString(drawMoneyAccRequestAndResponseDTO);		
		
		}
		logger.info("商户在客户端查看绑定的卡列表返回app参数: "+result+"时间："+UtilDate.getDateFormatter());
		return result;
	}

	/**
	 * 商户把钱款现到绑定的卡上操作  第一步  生成订单  添加流水   提款数据添加
	 * wumeng  20150525
	 * @param param
	 * @param response
	 * @param session
	 */
	@Override
	public synchronized DrawMoneyAccRequestAndResponseDTO insertDrawMoneyAcc(String param,SessionInfo  sessionInfo) throws Exception {
		
		logger.info("商户提现接口第一步调用接收app参数: "+param+"时间："+UtilDate.getDateFormatter());
		DrawMoneyAccRequestAndResponseDTO  drawMoneyAccRequestAndResponseDTO =(DrawMoneyAccRequestAndResponseDTO)parseJsonString(param,DrawMoneyAccRequestAndResponseDTO.class);
		if(!drawMoneyAccRequestAndResponseDTO.equals(DATAPARSINGMESSAGE)){//判断解析数据是否成功
			
			String mercid = sessionInfo.getMercId();
			
			String pealName= pmsMerchantInfoDao.queryMercuryStatus(mercid);//查询商户是否实名认证
			
			if(AUTHENTICATIONFLAG.equals(pealName)){//60 正式商户
			 
			
				String oAgentNo = sessionInfo.getoAgentNo();
				
				
				
				//判断提现功能是否开启（总开关）根据O单编号和模块编号
				 ResultInfo resultInfoForOAgentNo =  publicTradeVerifyService.moduleVerifyOagent(TradeTypeEnum.drawMoney,oAgentNo);
                //返回不为0，一律按照交易失败处理
                if(!resultInfoForOAgentNo.getErrCode().equals("0")){
                	 drawMoneyAccRequestAndResponseDTO.setRetCode("22");//返回码
                	if("".equals(resultInfoForOAgentNo.getMsg())||resultInfoForOAgentNo.getMsg()==null){
                		drawMoneyAccRequestAndResponseDTO.setRetMessage("此功能暂时关闭");//返回信息	
					 }else{
						 drawMoneyAccRequestAndResponseDTO.setRetMessage(resultInfoForOAgentNo.getMsg());
					 }
                	 return drawMoneyAccRequestAndResponseDTO;
                }
				
                int nowTime = Integer.parseInt(UtilDate.getTXDateTime());
				Map<String, String>   paramMap = new HashMap<String, String>();
				
				
				
				
				if(nowTime>=2300){//提现超过23点算第二天
					paramMap.put("taatransDate", UtilDate.addDay(UtilDate.strToDate(UtilDate.getDayDate()),1));//时间加1天
					paramMap.put("date", UtilDate.addDay(UtilDate.strToDate(UtilDate.getDayDate()),1).replace("-", ""));//时间加一天
				}else{
					paramMap.put("taatransDate", UtilDate.getDayDate());//修改余额记录时间YYYY-MM-DD
					paramMap.put("date", UtilDate.getDate());//提款时间YYYYMMDD
				}
				
				
				paramMap.put("mercid",mercid);//商户编号
				paramMap.put("businesscode",TradeTypeEnum.drawMoney.getTypeCode());//业务编号
				paramMap.put("oAgentNo",sessionInfo.getoAgentNo());//O单编号
				
				Map<String, String> resultMap= merchantMineDao.queryDrawMoneyAcc(paramMap);
				
				String status = resultMap.get("STATUS");//此业务是否开通
				String statusMessage = resultMap.get("MESSAGE");//此业务是否开通的描述
				
				if("1".equals(status)){//1开通
					
					/*String	message = verificationCode(drawMoneyAccRequestAndResponseDTO.getMobilePhone(),
							drawMoneyAccRequestAndResponseDTO.getValidCode(), PmsMessage.DRAWMONEY,oAgentNo);
					// 解析要返回的信息
					String retMessageStatus = message.split(":")[1];
					String retMessage="";
					if ("success".equals(retMessageStatus)) {
						retMessage = "验证成功";
					} else if ("error".equals(retMessageStatus)) {
						retMessage = "验证码输入错误";
					} else if ("failure".equals(retMessageStatus)) {
						retMessage = "验证码失效，请重新获取";
					}
				
					if("success".equals(retMessageStatus)){*/ //去点短信验证  2015-12-14 下面还有
						int count = merchantMineDao.queryFestival(UtilDate.getDayDate());
						/*if(count>0){
							drawMoneyAccRequestAndResponseDTO.setRetCode("11");//返回码
							drawMoneyAccRequestAndResponseDTO.setRetMessage("节假日此操作不能进行");//返回信息
						}else{*///去掉节假日提现的限制   20151110
					
						 	
							//添加提现时间段判断  9:00到18:30    wm 2015-10-15 
							Map<String, String> tiXianTimeMap= merchantMineDao.queryTiXianTime();
							
							String tiXianTime = tiXianTimeMap.get("VALUE");//提现时间段 格式0900#1830#1200#1830  
							String[] tiXianTimeArrary = tiXianTime.split("#");
	
							int tiXianStartTime;
							int tiXianEndTime;
							
							if(count>0){//判断节假日  确定节假日体现是时间
								//节假日
								 tiXianStartTime = Integer.parseInt(tiXianTimeArrary[2]);
								 tiXianEndTime = Integer.parseInt(tiXianTimeArrary[3]);
							}else{
								//不是节假日
								 tiXianStartTime = Integer.parseInt(tiXianTimeArrary[0]);
								 tiXianEndTime = Integer.parseInt(tiXianTimeArrary[1]);
							}
							
							
							
							
							if((nowTime>=tiXianStartTime)&&(nowTime<=tiXianEndTime)){	
								
									if(resultMap!=null){
										
										int maxTimes = Integer.parseInt(resultMap.get("NUMBEROFTIMES"));//最大提款次数
										int alreadyTimes = Integer.parseInt(resultMap.get("ALREADYTIMES"));//已经提款次数
										
										if(alreadyTimes<maxTimes){//小于等于最大提款次数进行提款操作
											
											BigDecimal bottomPoundage = new BigDecimal(resultMap.get("BOTTOM_POUNDAGE"));//最低手续费
											BigDecimal payAmt = new BigDecimal(drawMoneyAccRequestAndResponseDTO.getDrawAmt());//订单金额
											
											
											
						      				//判读  交易金额是不是在欧单区间控制之内
						      					 ResultInfo resultInfo =  amountLimitControlDao.checkLimit(oAgentNo,payAmt,TradeTypeEnum.drawMoney.getTypeCode());
						                         //返回不为0，一律按照交易失败处理
						                         if(!resultInfo.getErrCode().equals("0")){
						       						
						                        	 drawMoneyAccRequestAndResponseDTO.setRetCode("21");//返回码
						                     		 drawMoneyAccRequestAndResponseDTO.setRetMessage(resultInfo.getMsg());//返回信息	
						                     		 return drawMoneyAccRequestAndResponseDTO;
						                         }
											
											
											if(bottomPoundage.compareTo(payAmt)==-1){//判断订单金额够不够付最低手续费
												String todayBlance = "";//今日交易额
												
												if("".equals(resultMap.get("BALANCE"))||resultMap.get("BALANCE")==null){
													todayBlance="0";//账户可用余额
												}else{
													todayBlance = resultMap.get("BALANCE");//账户可用余额
												}
												
												BigDecimal balance= new BigDecimal(todayBlance);//今日收益（即今天可提现金额）
												
												if(payAmt.compareTo(balance)!=1){
													BigDecimal min_amount = new  BigDecimal(resultMap.get("MIN_AMOUNT"));//最低收款金额   MIN_AMOUNT
													BigDecimal max_amount = new  BigDecimal(resultMap.get("MAX_AMOUNT"));//最高收款金额   MAX_AMOUNT
													
													if(min_amount.compareTo(payAmt)!=1){//判断收款金额是否大于最低收款金额   大于等于执行   小于不执行
														
														if(payAmt.compareTo(max_amount)!=1){
															
															BigDecimal rate = new BigDecimal(resultMap.get("RATE"));//费率
															String orderid=UtilMethod.getOrderid("160");//订单号
															
															PmsAppTransInfo  pmsAppTransInfo= new PmsAppTransInfo();
															pmsAppTransInfo.setTradetype("商户提款");
															pmsAppTransInfo.setTradetime(UtilDate.getDateFormatter());
															
															pmsAppTransInfo.setOrderid(orderid);//上送的订单号
															
															String payamount = "";
															
															
															BigDecimal poundage = payAmt.multiply(rate);//手续费
															
															if(bottomPoundage.compareTo(poundage)==1){//判断手续费是否于最低手续费   小于按最低收   大于正常收取
																//小于最低手续费
																payamount =payAmt.subtract(bottomPoundage).toString();
																pmsAppTransInfo.setPoundage(resultMap.get("BOTTOM_POUNDAGE"));//手续费  按分为最小单位  例如：1元=100分   采用100
																poundage=bottomPoundage;
															}else{
																//大于最低手续费
																payamount =payAmt.subtract(payAmt.multiply(rate)).toString();
																pmsAppTransInfo.setPoundage(payAmt.multiply(rate).toString());//手续费  按分为最小单位  例如：1元=100分   采用100
															}
															
															pmsAppTransInfo.setReasonofpayment("商户提款");
															pmsAppTransInfo.setMercid(mercid);
		
															//结算金额    按分为最小单位  例如：1元=100分   采用100（给商户打款的金额）
															pmsAppTransInfo.setPayamount("-"+payamount);
															
															//实际金额   按分为最小单位  例如：1元=100分   采用100  商户提现提了多钱      结算金额=实际金额-手续费
															pmsAppTransInfo.setFactamount("-"+drawMoneyAccRequestAndResponseDTO.getDrawAmt());
															
															//订单金额  按分为最小单位  例如：1元=100分   采用100
															pmsAppTransInfo.setOrderamount("-"+drawMoneyAccRequestAndResponseDTO.getDrawAmt());
															
															
															pmsAppTransInfo.setTradetypecode("7");//交易类型
															pmsAppTransInfo.setPaymentcode("0");//支付方式  提现没有值  为了添加流水表设置为空字符串
															pmsAppTransInfo.setPaymenttype("商户提款");//支付方式  提现没有值  为了添加流水表设置为空字符串
															
															pmsAppTransInfo.setRate(resultMap.get("RATE"));//费率
															
															
															pmsAppTransInfo.setChannelNum(TXIAN);
															pmsAppTransInfo.setBusinessNum(TIXIANDRAWMONEY);
															pmsAppTransInfo.setStatus(Constants.ORDERINITSTATUS);//订单初始化状态
															pmsAppTransInfo.setoAgentNo(oAgentNo);//o单编号
															String temp =createJsonString(pmsAppTransInfo);
															
															
		
															if(pmsAppTransInfoDao.insert(pmsAppTransInfo)==1){//订单添加成功
																
																PospTransInfo pospTransInfo = generateTransFromAppTrans(pmsAppTransInfo);//拼插入流水表实体类
																if(pospTransInfo != null){
																	if(pospTransInfoDAO.insert(pospTransInfo)==1){
																		//提款记录存入PMS_MERCHANT_COLLECT_MANAGER表
																		MerchantMinel merchantMinel= new MerchantMinel();
																			
																		merchantMinel.setOrderid(orderid);   //订单号 
																		merchantMinel.setAmount(payamount); //结算金额
																		merchantMinel.setBanksysnumber(resultMap.get("BANKSYSNUMBER"));	//开户行支付系统行号（联行号）
																		merchantMinel.setBankname(resultMap.get("BANKNAME"));	//		//开户行名称
																		merchantMinel.setStatus("2");		//是否成功    0 成功   1失败 2等待处理
																		merchantMinel.setMercId(mercid);	//商户编号
																		merchantMinel.setBusinesscode("7");//业务编号（ 1 商户收款、2 转账汇款、3 信用卡还款、4手机充值、5 水煤电、 6 加油卡充值、7 提款（提现））	
																		merchantMinel.setClrMerc(resultMap.get("CLR_MERC"));		//结算账号（卡号）
																		merchantMinel.setCreateTime(UtilDate.getDateAndTimes()); 	//创建时间（提款  汇款  还款  请求时间）  格式YYYYMMDDHHmmssSSS   20150526105900000   17位
																		merchantMinel.setSettlementname(resultMap.get("SETTLEMENTNAME"));	//持卡人姓名
																		merchantMinel.setRate(resultMap.get("RATE"));//费率
																		merchantMinel.setPoundage(poundage.toString());//手续费
																		merchantMinel.setOrderamount(drawMoneyAccRequestAndResponseDTO.getDrawAmt());//订单金额
																		merchantMinel.setoAgentNo(oAgentNo);//o单编号
																		
																		if(this.merchantMineDao.saveDrawMoneyAcc(merchantMinel)!=1){
																			logger.info("提款记录存入失败， 订单号："+orderid +"，结束时间："+ UtilDate.getDateFormatter()+"。详细信息："+temp);
																			drawMoneyAccRequestAndResponseDTO.setRetCode("1");//返回码
																			drawMoneyAccRequestAndResponseDTO.setRetMessage("订单提交失败");//返回信息
																			 throw new RuntimeException("手动抛出");
																		}else{
																			/*drawMoneyAccRequestAndResponseDTO.setRetCode("0");//返回码
																			drawMoneyAccRequestAndResponseDTO.setRetMessage("生成订单  添加流水   提款数据添加成功");//返回信息
																			drawMoneyAccRequestAndResponseDTO.setPmsAppTransInfo(pmsAppTransInfo);
																			*/
																			
																			//第二步操作调用confirmDrawMoneyAcc
																			
																			int resultInt = merchantCollectMoneyService.updateMerchantBalance(pmsAppTransInfo);
																			
																			if(resultInt!=1){
																			    logger.info("更新商户账户余额失败，商户ID："+mercid +"，结束时间："+ UtilDate.getDateFormatter()+"。更新金额："+ pmsAppTransInfo.getOrderamount());
																			    throw new RuntimeException("手动抛出");
																			}else{//修改流水表状态
																				
																				if(pmsAppTransInfoDao.updateOrderStatusForSettle(orderid)!=1){//修改订单状态为6等待清算系统结算
																					logger.info("订单状态修改失败， 订单号：" + orderid + "，结束时间：" + UtilDate.getDateFormatter());
																	                drawMoneyAccRequestAndResponseDTO.setRetCode("12");//返回码
																					 drawMoneyAccRequestAndResponseDTO.setRetMessage("订单状态修改失败");//返回信息
																				}else{
																					/*if(pospTransInfoDAO.updetePospTransInfo(orderid)!=1){
																		               	 logger.info("流水表订单状态修改失败， 订单号：" + orderid + "，结束时间：" + UtilDate.getDateFormatter());
																		                 drawMoneyAccRequestAndResponseDTO.setRetCode("10");//返回码
																						 drawMoneyAccRequestAndResponseDTO.setRetMessage("流水表订单状态修改失败");//返回信息
																					}else{*/
																						drawMoneyAccRequestAndResponseDTO.setRetCode("0");//返回码
																						drawMoneyAccRequestAndResponseDTO.setRetMessage("订单提交成功");//返回信息
																						drawMoneyAccRequestAndResponseDTO.setOrderNumber(orderid);//订单号
																					//}
																						
																						//调用清算系统代付
																						
																						try {
																							ThreadPool.executor(new TiXianTaskThread(orderid));
																						} catch (Exception e) {
																							logger.info("提现调用清算代付系统失败，失败订单号"+orderid +"，失败时间："+ UtilDate.getDateFormatter());
																						}
																						
																				}
																				
																				
																			}
																			
																			
																		}
																	}else{
																		logger.info("添加流水表失败， 订单号："+orderid +"，结束时间："+ UtilDate.getDateFormatter()+"。详细信息："+temp);
																		drawMoneyAccRequestAndResponseDTO.setRetCode("9");//返回码
																		drawMoneyAccRequestAndResponseDTO.setRetMessage("添加流水表失败");//返回信息
																		 throw new RuntimeException("手动抛出");
																	}
												                }else{
												                	logger.info("添加流水表失败， 订单号："+orderid +"，结束时间："+ UtilDate.getDateFormatter()+"。详细信息："+temp);
																	drawMoneyAccRequestAndResponseDTO.setRetCode("9");//返回码
																	drawMoneyAccRequestAndResponseDTO.setRetMessage("添加流水表失败");//返回信息
																	 throw new RuntimeException("手动抛出");
												                }
																
															}else{
																logger.info("订单入库失败， 订单号："+orderid +"，结束时间："+ UtilDate.getDateFormatter()+"。详细信息："+temp);
																drawMoneyAccRequestAndResponseDTO.setRetCode("1");//返回码
																drawMoneyAccRequestAndResponseDTO.setRetMessage("订单提交失败");//返回信息
																 throw new RuntimeException("手动抛出");
															}
														
															
														}else{
															drawMoneyAccRequestAndResponseDTO.setRetCode("3");//返回码
															drawMoneyAccRequestAndResponseDTO.setRetMessage("交易金额大于提款最高金额:"+max_amount.divide(new BigDecimal(100)));//返回信息	
														}
													}else{
														drawMoneyAccRequestAndResponseDTO.setRetCode("4");//返回码
														drawMoneyAccRequestAndResponseDTO.setRetMessage("交易金额小于收提款最低金额:"+min_amount.divide(new BigDecimal(100)));//返回信息
													}
													
													
												}else{
													drawMoneyAccRequestAndResponseDTO.setRetCode("5");//返回码
													drawMoneyAccRequestAndResponseDTO.setRetMessage("商户余额不足");//返回信息
												}
												
												
											}else{
												drawMoneyAccRequestAndResponseDTO.setRetCode("18");//返回码
												drawMoneyAccRequestAndResponseDTO.setRetMessage("订单金额不能小于最低手续费:"+bottomPoundage.divide(new BigDecimal(100)).toString()+"元");//返回信息
											}
											
											
										}else{
										  drawMoneyAccRequestAndResponseDTO.setRetCode("6");//返回码
										  drawMoneyAccRequestAndResponseDTO.setRetMessage("提款次数已经超过当日最大提款次数");//返回信息
										}
										
									}else{
										drawMoneyAccRequestAndResponseDTO.setRetCode("8");//返回码
										 drawMoneyAccRequestAndResponseDTO.setRetMessage("商户没有绑定卡等信息");//返回信息
									}
									
								}else{
									drawMoneyAccRequestAndResponseDTO.setRetCode("17");//返回码
									drawMoneyAccRequestAndResponseDTO.setRetMessage("非提现时间，不能提现");//返回信息
									
								}
							/*}else{
									drawMoneyAccRequestAndResponseDTO.setRetCode("16");//返回码
									drawMoneyAccRequestAndResponseDTO.setRetMessage("节假日，不能提现");//返回信息
									
							  }*///去掉节假日提现的限制   20151110  上面还有
						
						
						/*}else{
							 drawMoneyAccRequestAndResponseDTO.setRetCode("15");//返回码
							 drawMoneyAccRequestAndResponseDTO.setRetMessage(retMessage);//返回信息
						}*/ //去点短信验证  2015-12-14 上面还有	
						 
					 }else{
						//此功能暂未开通或被禁用
							drawMoneyAccRequestAndResponseDTO.setRetCode("14");//
							
							if("".equals(statusMessage)||statusMessage==null){
								drawMoneyAccRequestAndResponseDTO.setRetMessage("此功能暂未开通");
							}else{
								drawMoneyAccRequestAndResponseDTO.setRetMessage(statusMessage);
							}
							
							
					 }
				
			}else{ 
				drawMoneyAccRequestAndResponseDTO.setRetCode("7");//返回码
				 drawMoneyAccRequestAndResponseDTO.setRetMessage("不是正式商户");//返回信息
            	
            }
			
		}
		
		logger.info("商户提现接口调用第一步返回参数: "+createJsonString(drawMoneyAccRequestAndResponseDTO)+"时间："+UtilDate.getDateFormatter());
		return drawMoneyAccRequestAndResponseDTO;
	}
	
	
	/**
	 * 商户把钱款现到绑定的卡上操作  第二步确认订单并支付
	 * wumeng  20150515
	 * @param param
	 * @param response
	 * @param session
	 */
	@Override
	public String confirmDrawMoneyAcc(String mercid,PmsAppTransInfo  pmsAppTransInfo)throws Exception{
		logger.info("商户提现接口调用第二步确认订单并支付, 时间："+UtilDate.getDateFormatter());
		String  result = "";
			
		DrawMoneyAccRequestAndResponseDTO  drawMoneyAccRequestAndResponseDTO = new DrawMoneyAccRequestAndResponseDTO();	

		String orderid = pmsAppTransInfo.getOrderid();//订单号
		int resultInt = merchantCollectMoneyService.updateMerchantBalance(pmsAppTransInfo);
		
		if(resultInt!=1){
		    logger.info("更新商户账户余额失败，商户ID："+mercid +"，结束时间："+ UtilDate.getDateFormatter()+"。更新金额："+ pmsAppTransInfo.getOrderamount());
		    throw new RuntimeException("手动抛出");
		}else{//修改流水表状态
			
			if(pmsAppTransInfoDao.updateOrderStatusForSettle(orderid)!=1){//修改订单状态为6等待清算系统结算
				logger.info("订单状态修改失败， 订单号：" + orderid + "，结束时间：" + UtilDate.getDateFormatter());
                drawMoneyAccRequestAndResponseDTO.setRetCode("12");//返回码
				 drawMoneyAccRequestAndResponseDTO.setRetMessage("订单状态修改失败");//返回信息
			}else{
				/*if(pospTransInfoDAO.updetePospTransInfo(orderid)!=1){
	               	 logger.info("流水表订单状态修改失败， 订单号：" + orderid + "，结束时间：" + UtilDate.getDateFormatter());
	                 drawMoneyAccRequestAndResponseDTO.setRetCode("10");//返回码
					 drawMoneyAccRequestAndResponseDTO.setRetMessage("流水表订单状态修改失败");//返回信息
				}else{*/
					drawMoneyAccRequestAndResponseDTO.setRetCode("0");//返回码
					drawMoneyAccRequestAndResponseDTO.setRetMessage("订单提交成功");//返回信息
					drawMoneyAccRequestAndResponseDTO.setOrderNumber(orderid);//订单号
				//}
			}
			
			
		}
									
		result = createJsonString(drawMoneyAccRequestAndResponseDTO);

		logger.info("商户提现接口调用第二步确认订单并支付 返回app参数: "+result+"时间："+UtilDate.getDateFormatter());


		return result;

	}
	
	/**
	 * 获取业务信息   最大值、最小值、费率 是否封顶   封顶金额
	 * wumeng  20150626
	 * @param param
	 * @param response
	 * @param session
	 */
	@Override
	public String queryBusinessInfo(String param,SessionInfo  sessionInfo)throws Exception {
		logger.info("获取业务信息接收app参数: "+param+"时间："+UtilDate.getDateFormatter());
		BusinessInfoRequestAndResponseDTO  businessInfoRequestAndResponseDTO =(BusinessInfoRequestAndResponseDTO)parseJsonString(param,BusinessInfoRequestAndResponseDTO.class);
		String  result = "";
		if(!businessInfoRequestAndResponseDTO.equals(DATAPARSINGMESSAGE)){//判断解析数据是否成功
			
			Map<String, String>   paramMap = new HashMap<String, String>();
			
			paramMap.put("mercid",sessionInfo.getMercId());//商户编号
			paramMap.put("businesscode",businessInfoRequestAndResponseDTO.getBusinessCode());//业务编号
			paramMap.put("oAgentNo",sessionInfo.getoAgentNo());//o单编号
			
			Map<String, String> resultMap= merchantMineDao.queryBusinessInfo(paramMap);
			
			List<AppRateConfig> hirdpartRateList = appRateConfigDao.getThirdpartRate(sessionInfo.getoAgentNo());
			if(resultMap!=null){
				businessInfoRequestAndResponseDTO.setRetCode("0");//返回码
				businessInfoRequestAndResponseDTO.setRetMessage("查询成功");//返回信息
				businessInfoRequestAndResponseDTO.setChargeRate(resultMap.get("RATE"));//手续费费率     
				businessInfoRequestAndResponseDTO.setTranMinAmt(resultMap.get("MIN_AMOUNT"));//每笔最少金额   
				businessInfoRequestAndResponseDTO.setTranMaxAmt(resultMap.get("MAX_AMOUNT"));//每笔最大金额   
				businessInfoRequestAndResponseDTO.setIsTop(resultMap.get("IS_TOP"));//是否是封顶费率       1封顶      0不封顶
				businessInfoRequestAndResponseDTO.setTopPoundage(resultMap.get("TOP_POUNDAGE"));//封顶手续费
				businessInfoRequestAndResponseDTO.setMinSettleAmount(resultMap.get("MINSETTLE"));//T+1清算最低金额
				
				
				if(TradeTypeEnum.transeMoney.getTypeCode().equals(businessInfoRequestAndResponseDTO.getBusinessCode())||
						TradeTypeEnum.creditCardRePay.getTypeCode().equals(businessInfoRequestAndResponseDTO.getBusinessCode())){
					businessInfoRequestAndResponseDTO.setBottomPoundage(resultMap.get("BOTTOM_POUNDAGE"));//最低手续费
					businessInfoRequestAndResponseDTO.setIsBottom(resultMap.get("IS_BOTTOM"));//是否最低  1最低   0不是最低
				}
				
				
				
				for(AppRateConfig appRateConfig:hirdpartRateList){
					if(RateTypeEnum.baiduRateType.getTypeCode().equals(appRateConfig.getRateType())){
						businessInfoRequestAndResponseDTO.setBaiduRate(appRateConfig.getRate());
					}else if(RateTypeEnum.weixinRateType.getTypeCode().equals(appRateConfig.getRateType())){
						businessInfoRequestAndResponseDTO.setWeixnRate(appRateConfig.getRate());
					}else if(RateTypeEnum.zhifubaoRateType.getTypeCode().equals(appRateConfig.getRateType())){
						businessInfoRequestAndResponseDTO.setZhifubaoRate(appRateConfig.getRate());
					}else if(RateTypeEnum.hebaoRateType.getTypeCode().equals(appRateConfig.getRateType())){
						businessInfoRequestAndResponseDTO.setYdhbRate(appRateConfig.getRate());
					}
				}
				
				List<AppRateConfig> list = new ArrayList<AppRateConfig>();

				AppRateConfig appRateConfig = new AppRateConfig();
				appRateConfig.setIsThirdpart("0");
				appRateConfig.setoAgentNo(sessionInfo.getoAgentNo());
				list = appRateConfigDao.searchList(appRateConfig);

				if (list != null && list.size() > 0) {
					for(AppRateConfig a : list){
						String isTop = a.getIsTop();
						if("1".equals(isTop)){
							a.setRate(new BigDecimal(a.getRate()).multiply(new BigDecimal(100)).stripTrailingZeros()+"%-"+new BigDecimal(a.getTopPoundage()).divide((new BigDecimal(100)))+"封顶（"+a.getRemark()+"）");
						}else{
							a.setRate(new BigDecimal(a.getRate()).multiply(new BigDecimal(100)).stripTrailingZeros()+"%（"+a.getRemark()+"）");
						}
					}
				}				
				
				businessInfoRequestAndResponseDTO.setList(list);//刷卡费率list
				
			}else{
				businessInfoRequestAndResponseDTO.setRetCode("1");//返回码
				businessInfoRequestAndResponseDTO.setRetMessage("查询失败");//返回信息
			}
				
			result = createJsonString(businessInfoRequestAndResponseDTO);		
		
		}
		logger.info("获取业务信息返回app参数: "+result+"时间："+UtilDate.getDateFormatter());
		return result;
	}

	
	/**
	 * 微信公众号信息获取
	 * wumeng  20150831
	 * @param param
	 * @param sessionInfo
	 */
	@SuppressWarnings("null")
	public String queryWechatPublicNo(String param, SessionInfo sessionInfo)throws Exception{
		String result="";
		
		WeChatPublicNo weChatPublicNo = merchantMineDao.queryWechatPublicNo(sessionInfo.getoAgentNo());
		if(weChatPublicNo==null){
			weChatPublicNo.setRetCode("1");
			weChatPublicNo.setRetMessage("初始化查询失败");
		}else{
			weChatPublicNo.setRetCode("0");//返回码
			weChatPublicNo.setRetMessage("初始化查询成功");
			
		}
		result = createJsonString(weChatPublicNo);	
		return result;
	}
	
	
	
}
