package xdt.service.impl;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Service;
import xdt.common.RetAppMessage;
import xdt.dao.*;
import xdt.dto.*;
import xdt.model.*;
import xdt.service.ICreditCardPaymentsService;
import xdt.service.IPublicTradeVerifyService;
import xdt.servlet.AppPospContext;
import xdt.util.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service("creditCardPaymentsService")
public class CreditCardPaymentsServiceImpl extends BaseServiceImpl implements ICreditCardPaymentsService {

	@Resource
	private IViewKyChannelInfoDao channelInfoDao; //通道信息层
	@Resource
	private IPmsAppTransInfoDao pmsAppTransInfoDao; //交易流水记录
	@Resource
	private IPmsTransHistoryRecordDao pmsTransHistoryRecordDao; //交易历史记录

    @Resource
    private IPmsMerchantInfoDao pmsMerchantInfoDao;//商户信息层
    @Resource
    private IMerchantMineDao merchantMineDao;
    @Resource
    private IPublicTradeVerifyService iPublicTradeVerifyService;

    private Logger logger=Logger.getLogger(CreditCardPaymentsServiceImpl.class);
	/**
	 * 添加信用卡
	 */
	@Override
	public String addCreditCard(String addCreditCardInfo, HttpSession session,HttpServletRequest request)throws Exception {
		setMethodSession(request.getRemoteAddr());
		logger.info("添加信用卡");
		HashMap<String,Object> map = validateNullAndParseData(session, addCreditCardInfo,AddCreditCardRequestDTO.class);
		AddCreditCardResponseDTO responseData = new AddCreditCardResponseDTO();
		String message = map.get("message").toString();
		PmsTransHistoryRecord record = new PmsTransHistoryRecord();
		if(message.equals(RetAppMessage.DATAANALYTICALSUCCESS)){
			SessionInfo sessionInfo = ((SessionInfo)map.get("sessionInfo"));


            if(sessionInfo == null){
                responseData.setRetCode(13);
                responseData.setRetMessage("会话失效，请重新登陆");
                String jsonString = createJsonString(responseData);
                return jsonString;
            }

            //检测欧单编号
            String oAgentNo = "";
            oAgentNo = sessionInfo.getoAgentNo();
            if(StringUtils.isBlank(oAgentNo)){
                //如果欧单编号为空，直接返回失败
                responseData.setRetCode(1);
                responseData.setRetMessage("参数错误");
                String jsonString = createJsonString(responseData);
                return jsonString;
            }



			setSession(request.getRemoteAddr(),session.getId(),sessionInfo.getMobilephone());
			AddCreditCardRequestDTO addInfo = (AddCreditCardRequestDTO)map.get("obj");
			logger.info("[client_req]"+createJson(addInfo));
			String bankCardNumber = addInfo.getBankCardNumber();//信用卡卡号
			String cardHolderName = addInfo.getCardHolderName();//持卡人姓名
			String bankId = addInfo.getBankId();//银行编码
			String bankName = addInfo.getBankName();//银行名称
			if(isNotEmptyValidate(bankCardNumber)&&isNotEmptyValidate(cardHolderName)&&isNotEmptyValidate(bankId)&&isNotEmptyValidate(bankName)){
				//判断信用卡号是否填写合法
				if(validateCreditCard(bankCardNumber)){
					//根据银行卡的前6位模糊匹配银行卡名称
					String str [] = getBankCardInfo(bankName,bankCardNumber,sessionInfo.getMobilephone()).split("-");
					message = str[0];
					if(message.equals(SUCCESSMESSAGE)){
						String bankCardName = str[1];
						String tradeTime = sdf.format(new Date());
                        record.setoAgentNo(oAgentNo);
						record.setMercid(sessionInfo.getMercId());//商户id
						record.setBankcardnumber(bankCardNumber);//银行卡号
						record.setBusinessnumber(new BigDecimal(1));//业务编号
						record.setBusinessname("信用卡还款");//业务名称
						record.setBankid(bankId); //银行编码
						record.setBankname(bankName);//银行名称
						record.setBankcardname(bankCardName); //银行卡名称
						int index = bankCardName.indexOf("(");
						if(index!=-1){bankCardName = bankCardName.substring(index+1).replace(")","");}
						record.setShortbankcardname(bankCardName);//银行卡名称简称
						record.setCardholdername(cardHolderName);//持卡人
						String shortBankCardNumber = "尾号"+bankCardNumber.substring(bankCardNumber.length()-4);
						record.setShortbankcardnumber(shortBankCardNumber);//银行卡号后四位简称
						record.setCreatetime(tradeTime); //创建时间
						record.setState("0");//有效
						record.setMobilephone(sessionInfo.getMobilephone());//手机号
						message = getTransHistoryRecord(record);
						if(message.equals(RetAppMessage.HISTORYRECORDSAVESUCCESS) || message.equals(RetAppMessage.TRADINGINFOSAVESUCCESS)){
                            message = SUCCESSMESSAGE;
                        }else if(message.equals(RetAppMessage.HISTORYALREADYEXIST)){
                            message = "1:该信用卡已经存在";
                        }else{
                            message = FAILMESSAGE;
                        }
					}
				}else{
					insertAppLogs(sessionInfo.getMobilephone(),"","2015");
					message = RetAppMessage.BANKCARDNUMBERISILLEGAL;
				}
			}else{
				insertAppLogs(sessionInfo.getMobilephone(),"","2002");
				message = EMPTYMESSAGE;
			}
		}
		String retCode = message.split(":")[0];
		String retMessage = message.split(":")[1];
		retMessage = RetAppMessage.parseMessageCode(retMessage);
		responseData.setRetCode(Integer.parseInt(retCode));
		responseData.setRetMessage(retMessage);
		logger.info("[app_rsp]"+createJson(responseData));
		String jsonString = createJsonString(responseData);
		return jsonString;
	}

	/**
	 * 添加信用卡异常
	 */
	@Override
	public String addCreditCardException(HttpSession session) throws Exception {
		AddCreditCardResponseDTO responseData = new AddCreditCardResponseDTO();
		responseData.setBankName("");
		responseData.setCardHolderName("");
		responseData.setCount(0);
		responseData.setRetCode(100);
		responseData.setRetMessage("系统异常");
		responseData.setShortBankCardNumber("");
		responseData.setBankCardNumber("");
		logger.info("[app_rsp]"+createJson(responseData));
		insertAppLogs(((SessionInfo)session.getAttribute(SessionInfo.SESSIONINFO)).getMobilephone(),"","2028");
		return createJsonString(responseData);
	}

    /**
     * 信用卡还款，生成订单
     * @param produceOrderInfo
     * @param session
     * @param request
     * @return
     * @throws Exception
     */
    @Override
    public String pruduceOrder(String produceOrderInfo, HttpSession session, HttpServletRequest request) throws Exception {

        String jsonString = "";
        CreditCardProOrderResponseDTO creditCardProOrderResponseDTO = new CreditCardProOrderResponseDTO();
        String message = INITIALIZEMESSAGE;
        SessionInfo sessionInfo = (SessionInfo)session.getAttribute(SessionInfo.SESSIONINFO);
        CreditCardProOrderRequestDTO requestDTO = null;
        //刷卡信息
        BrushCalorieOfConsumptionRequestDTO dto = null;
        //欧单编号
        String oAgentNo = "";
        //判断当前用户是否登录
        if(	sessionInfo != null ){

            oAgentNo = sessionInfo.getoAgentNo();
            if(StringUtils.isBlank(oAgentNo)){
                //欧单编号为空，直接返回错误
                creditCardProOrderResponseDTO.setRetCode(1);
                creditCardProOrderResponseDTO.setRetMessage("参数出错");
                try {
                    jsonString = createJsonString(creditCardProOrderResponseDTO);
                } catch (Exception em) {
                    em.printStackTrace();
                }
                return jsonString;
            }

            //判断请求体
            if(StringUtils.isNotBlank(produceOrderInfo)){


                //解析请求对象
                try {
                    requestDTO = (CreditCardProOrderRequestDTO)parseJsonString(produceOrderInfo,CreditCardProOrderRequestDTO.class);
                } catch (Exception e) {
                    creditCardProOrderResponseDTO.setRetCode(1);
                    creditCardProOrderResponseDTO.setRetMessage("参数出错");
                    logger.info("参数出错");
                    //参数出错
                    try {
                        jsonString = createJsonString(creditCardProOrderResponseDTO);
                    } catch (Exception em) {
                        em.printStackTrace();
                    }
                    return jsonString;

                }

                if(requestDTO != null && requestDTO.getDto() != null){
                    dto = requestDTO.getDto();
                }else{
                    //刷卡信息为空
                    creditCardProOrderResponseDTO.setRetCode(1);
                    creditCardProOrderResponseDTO.setRetMessage("刷卡信息为空");
                    logger.info("刷卡信息为空");
                    try {
                        jsonString = createJsonString(creditCardProOrderResponseDTO);
                    } catch (Exception em) {
                        em.printStackTrace();
                    }
                    return jsonString;

                }


                //判断当前用户是否有转账资格,正式商户才能转账
                PmsMerchantInfo merchantInfo = new PmsMerchantInfo();
                //设置欧单编号
                merchantInfo.setoAgentNo(oAgentNo);
                merchantInfo.setMobilephone(sessionInfo.getMobilephone());
                merchantInfo.setCustomertype("3");
                try {
                    List<PmsMerchantInfo> list = pmsMerchantInfoDao.searchList(merchantInfo);
                    if (list != null && list.size() > 0) {
                        PmsMerchantInfo pmsMerchantInfo = list.get(0);
                        if(pmsMerchantInfo.getMercSts().equals("60")){

                            //校验信用卡号格式是否正确
                            if(!validateCreditCard(requestDTO.getCollectAccNo())){
                                creditCardProOrderResponseDTO.setRetCode(1);
                                creditCardProOrderResponseDTO.setRetMessage("信用卡卡号格式不正确");
                                logger.info("信用卡卡号格式不正确：" + requestDTO.getCollectAccNo());
                                try {
                                    jsonString = createJsonString(creditCardProOrderResponseDTO);
                                } catch (Exception em) {
                                    em.printStackTrace();
                                }
                                return jsonString;
                            }
                            //判断汇款金额是否超限
                            //获取通道的费率
                            Map<String, String>   paramMap = new HashMap<String, String>();
                            paramMap.put("mercid",sessionInfo.getMercId());//商户编号
                            paramMap.put("businesscode", TradeTypeEnum.creditCardRePay.getTypeCode());//业务编号
                            paramMap.put("oAgentNo", oAgentNo);//业务编号
                            Map<String, String> resultMap= merchantMineDao.queryBusinessInfo(paramMap);

                            String isTop =  resultMap.get("IS_TOP");
                            String rate =  resultMap.get("RATE");
                            String topPoundage =   resultMap.get("TOP_POUNDAGE");//封顶费率
                            String maxTransMoney =  resultMap.get("MAX_AMOUNT"); //每笔最大交易金额
                            String minTransMoney = resultMap.get("MIN_AMOUNT"); //每笔最小交易金额
                            String paymentAmount = dto.getPayAmount();//刷卡金额
                            String minPoundageStr = resultMap.get("BOTTOM_POUNDAGE");//最低手续费
                            Double minPoundage = null;

                            if(StringUtils.isNotBlank(minPoundageStr)){
                                  minPoundage = Double.parseDouble(minPoundageStr);
                            } else{
                                creditCardProOrderResponseDTO.setRetCode(1);
                                creditCardProOrderResponseDTO.setRetMessage("查不到费率配置，请联系客服");
                                try {
                                    jsonString = createJsonString(creditCardProOrderResponseDTO);
                                } catch (Exception em) {
                                    em.printStackTrace();
                                }
                                return jsonString;
                            }

                            if(Double.parseDouble(paymentAmount) > Double.parseDouble(maxTransMoney)){
                                //金额超过最大金额
                                creditCardProOrderResponseDTO.setRetCode(1);
                                creditCardProOrderResponseDTO.setRetMessage("金额超过最大金额");
                                logger.info("金额大于最大金额");
                                try {
                                    jsonString = createJsonString(creditCardProOrderResponseDTO);
                                } catch (Exception em) {
                                    em.printStackTrace();
                                }
                                return jsonString;
                            }else if(Double.parseDouble(paymentAmount) < Double.parseDouble(minTransMoney)){
                                // 金额小于最小金额
                                creditCardProOrderResponseDTO.setRetCode(1);
                                creditCardProOrderResponseDTO.setRetMessage("金额小于最小金额");
                                try {
                                    jsonString = createJsonString(creditCardProOrderResponseDTO);
                                } catch (Exception em) {
                                    em.printStackTrace();
                                }
                                logger.info("金额小于最小金额");
                                return jsonString;
                            }


                            //判断汇款银行卡号的合法性
                            String str[] = new String[0];
                            try {
                                str = getBankCardInfo(requestDTO.getCollectBankName(), requestDTO.getCollectAccNo(), sessionInfo.getMobilephone()).split("-");
                            } catch (Exception e) {
                                //汇款卡号输入有误
                                e.printStackTrace();
                                logger.info("汇款卡号输入有误");
                                // 金额小于最小金额
                                creditCardProOrderResponseDTO.setRetCode(1);
                                creditCardProOrderResponseDTO.setRetMessage("汇款卡号输入有误");
                                try {
                                    jsonString = createJsonString(creditCardProOrderResponseDTO);
                                } catch (Exception em) {
                                    em.printStackTrace();
                                }
                                return jsonString;
                            }
                            message = str[0];
                            if(message.equals(SUCCESSMESSAGE)){

                                //判断刷卡参数不为空
                                if(requestDTO.getDto() != null){
                                    //组装订单数据
                                    PmsAppTransInfo pmsAppTransInfo = new PmsAppTransInfo();
                                    //设置欧单编号
                                    pmsAppTransInfo.setoAgentNo(oAgentNo);
                                    pmsAppTransInfo.setStatus(OrderStatusEnum.initlize.getStatus());//订单初始化状态
                                    pmsAppTransInfo.setTradetype(TradeTypeEnum.creditCardRePay.getTypeName());//信用卡还款
                                    pmsAppTransInfo.setTradetime(UtilDate.getDateFormatter()); //设置时间
                                    pmsAppTransInfo.setPayeename(requestDTO.getCollectName());
                                    pmsAppTransInfo.setMercid(sessionInfo.getMercId());
                                    pmsAppTransInfo.setTradetypecode(TradeTypeEnum.creditCardRePay.getTypeCode());//转账汇款
                                    pmsAppTransInfo.setBankno(requestDTO.getCollectAccNo());
                                    pmsAppTransInfo.setBankname(requestDTO.getCollectBankName());

                                    //判断是何种付款方式
                                    if (requestDTO.getPayType().equals("1")) {//刷卡支付
                                        ViewKyChannelInfo channelInfo = AppPospContext.context.get(SHUAKA+PAYCREDITCARDSLOTCARDBUSINESSNUM);
                                        pmsAppTransInfo.setPayamount(dto.getPayAmount());//交易金额
                                        String orderNumber = UtilMethod.getOrderid("120");
                                        pmsAppTransInfo.setOrderid(orderNumber);//设置订单号
                                        pmsAppTransInfo.setPaymenttype("刷卡支付");
                                        pmsAppTransInfo.setPaymentcode("5");
                                        pmsAppTransInfo.setChannelNum(SHUAKA);
                                        pmsAppTransInfo.setBusinessNum(channelInfo.getBusinessnum());



                                        //写入凭证信息
                                        if(StringUtils.isBlank(dto.getAuthPath())){
                                            //如果凭证信息为空，直接返回失败
                                            //上送参数错误
                                            logger.info("上送参数错误， 商户号："+sessionInfo.getMercId() +"，结束时间："+ UtilDate.getDateFormatter());
                                            creditCardProOrderResponseDTO.setRetCode(1);
                                            creditCardProOrderResponseDTO.setRetMessage("凭证信息为空");
                                            try {
                                                jsonString = createJsonString(creditCardProOrderResponseDTO);
                                            } catch (Exception em) {
                                                em.printStackTrace();
                                            }
                                            return jsonString;
                                        }
                                        pmsAppTransInfo.setAuthPath(PIRPREURL+dto.getAuthPath());
                                        Double factAmount = 0.0;
                                        //费率
                                        Double fee =0.0;
                                        String rateStr = "";
                                        //计算实际金额
                                        if("1".equals(isTop)){

                                            rateStr = rate +"-"+ topPoundage;
                                            //是封顶费率类型
                                            fee = Double.parseDouble(rate) * (Double.parseDouble(paymentAmount) + minPoundage);

                                            if( fee > Double.parseDouble(topPoundage) ){
                                                //费率大于最大手续费，按最大手续费处理
                                                factAmount =  Double.parseDouble(topPoundage) + Double.parseDouble(paymentAmount);
                                                fee =   Double.parseDouble(topPoundage) + minPoundage;
                                            }else {
                                                //按当前费率处理
                                                rateStr = rate;
                                                fee += minPoundage;
                                                factAmount = Double.parseDouble(paymentAmount) + fee;
                                            }

                                        }else{
                                            //按当前费率处理
                                            rateStr = rate;
                                            fee = Double.parseDouble(rate) *  (Double.parseDouble(paymentAmount) + minPoundage) +  minPoundage;
                                            factAmount = Double.parseDouble(paymentAmount) + fee;
                                        }

                                        pmsAppTransInfo.setPayamount(dto.getPayAmount());//交易金额
                                        pmsAppTransInfo.setFactamount(factAmount.toString());//实际金额
                                        pmsAppTransInfo.setOrderamount(dto.getPayAmount());//订单金额
                                        pmsAppTransInfo.setRate(rate);
                                        pmsAppTransInfo.setCreditcardnumber(dto.getCardNo());
                                        pmsAppTransInfo.setDrawMoneyType("1");//普通提款
                                        pmsAppTransInfo.setPoundage(fee.toString());
                                        //设置交易地址
                                        if(StringUtils.isNotBlank(requestDTO.getAltLat())){
                                            pmsAppTransInfo.setAltLat(requestDTO.getAltLat());
                                        }
                                        if(StringUtils.isNotBlank(requestDTO.getGpsAddress())){
                                            pmsAppTransInfo.setGpsAddress(requestDTO.getGpsAddress());
                                        }

                                        Integer insertAppTrans = pmsAppTransInfoDao.insert(pmsAppTransInfo);
                                        if(insertAppTrans == 1){

                                            //验证支付方式是否开启
                                            ResultInfo payCheckResult = iPublicTradeVerifyService.totalVerify(Integer.parseInt(paymentAmount), TradeTypeEnum.creditCardRePay, PaymentCodeEnum.shuakaPay, oAgentNo, sessionInfo.getMercId());
                                            if(!payCheckResult.getErrCode().equals("0")){
                                                // 交易不支持
                                                creditCardProOrderResponseDTO.setRetCode(1);
                                                creditCardProOrderResponseDTO.setRetMessage(payCheckResult.getMsg());
                                                try {
                                                    jsonString = createJsonString(creditCardProOrderResponseDTO);
                                                } catch (Exception em) {
                                                    em.printStackTrace();
                                                }
                                                logger.info("不支持的支付方式，oAagentNo:"+oAgentNo+",payType:"+PaymentCodeEnum.shuakaPay.getTypeCode());
                                                return jsonString;
                                            }
                                            //调用三方前置
                                            //将发送报文中的金额改为实际金额，pre不处理费率
                                            double sendAmount = Math.ceil(factAmount);
                                            dto.setPayAmount(String.valueOf((int)sendAmount));
                                            String  sendStr8583 =	"param="+this.createBrushCalorieOfConsumptionDTORequest(sessionInfo, dto, orderNumber, PAYCREDITCARDSLOTCARDBUSINESSNUM, rateStr,dto.getSn());
                                            if("param=fail".equals(sendStr8583)){
                                                //上送参数错误
                                                logger.info("上送参数错误， 订单号："+orderNumber +"，结束时间："+ UtilDate.getDateFormatter());
                                                // 金额小于最小金额
                                                creditCardProOrderResponseDTO.setRetCode(1);
                                                creditCardProOrderResponseDTO.setRetMessage("上送参数错误");
                                                try {
                                                    jsonString = createJsonString(creditCardProOrderResponseDTO);
                                                } catch (Exception em) {
                                                    em.printStackTrace();
                                                }
                                                return jsonString;
                                            }else if ("param=meros".equals(sendStr8583)){
                                                //上送参数错误
                                                logger.info("pos机信息读取失败， 订单号："+orderNumber +"，结束时间："+ UtilDate.getDateFormatter());
                                                // 金额小于最小金额
                                                creditCardProOrderResponseDTO.setRetCode(1);
                                                creditCardProOrderResponseDTO.setRetMessage("pos机信息读取失败，不支持的卡类型");
                                                try {
                                                    jsonString = createJsonString(creditCardProOrderResponseDTO);
                                                } catch (Exception em) {
                                                    em.printStackTrace();
                                                }
                                                return jsonString;
                                            }else{
                                                logger.info("调用三方前置刷卡接口请求参数：" + sendStr8583 + "，结束时间：" + UtilDate.getDateFormatter());

                                                String successFlag = HttpURLConection.httpURLConnectionPOST(channelInfo.getUrl(), sendStr8583);

                                                logger.info("调用三方前置刷卡接口返回参数：" + successFlag + "，结束时间：" + UtilDate.getDateFormatter());

                                                BrushCalorieOfConsumptionResponseDTO response = (BrushCalorieOfConsumptionResponseDTO)parseJsonString(successFlag,BrushCalorieOfConsumptionResponseDTO.class);

                                                if("0000".equals(response.getRetCode())){//判断调用接口处理是否成功    0000表示刷卡成功
                                                    //修改当前订单的状态为6，等待清系统结算
                                                    Integer updateAppTrans = pmsAppTransInfoDao.updateOrderStatusForSettle(orderNumber);

                                                    if(updateAppTrans.equals(1)){
                                                        //更新成功
                                                        //提款记录存入PMS_MERCHANT_COLLECT_MANAGER表 ，等待清算
                                                        MerchantMinel merchantMinel= new MerchantMinel();
                                                        //设置欧单编号
                                                        merchantMinel.setoAgentNo(oAgentNo);
                                                        merchantMinel.setOrderid(orderNumber);   //订单号
                                                        merchantMinel.setBanksysnumber("xxxxx");//没有该项
                                                        merchantMinel.setAmount(pmsAppTransInfo.getPayamount()); //实际打款金额
                                                        merchantMinel.setBankname(requestDTO.getCollectBankName());	//		//开户行名称
                                                        merchantMinel.setStatus("2");		//是否成功    0 成功   1失败 2等待处理
                                                        merchantMinel.setMercId(sessionInfo.getMercId());	//商户编号
                                                        merchantMinel.setBusinesscode(TradeTypeEnum.creditCardRePay.getTypeCode());//业务编号（ 1 商户收款、2 转账汇款、3 信用卡还款、4手机充值、5 水煤电、 6 加油卡充值、7 提款（提现））
                                                        merchantMinel.setClrMerc(requestDTO.getCollectAccNo());		//结算账号（卡号）
                                                        merchantMinel.setCreateTime(UtilDate.getDateAndTimes()); 	//创建时间（提款  汇款  还款  请求时间）  格式YYYYMMDDHHmmssSSS   20150526105900000   17位
                                                        merchantMinel.setSettlementname(requestDTO.getCollectName());	//持卡人姓名
                                                        merchantMinel.setRate(rateStr);//费率
                                                        merchantMinel.setPoundage(fee.toString());//手续费
                                                        merchantMinel.setOrderamount(pmsAppTransInfo.getOrderamount());//订单金额

                                                        Integer mineResult = merchantMineDao.saveDrawMoneyAcc(merchantMinel);

                                                        if(mineResult.equals(1)){


                                                            //存入清算记录成功
                                                            creditCardProOrderResponseDTO.setRetCode(0);
                                                            creditCardProOrderResponseDTO.setRetMessage("交易成功");
                                                            try {
                                                                jsonString = createJsonString(creditCardProOrderResponseDTO);
                                                            } catch (Exception em) {
                                                                em.printStackTrace();
                                                            }
                                                            String tradeTime = sdf.format(new Date());
                                                            //插入转账历史记录表
                                                            PmsTransHistoryRecord record = new PmsTransHistoryRecord();
                                                            record.setoAgentNo(oAgentNo);
                                                            record.setMercid(sessionInfo.getMercId());//商户id
                                                            record.setBankcardnumber(requestDTO.getCollectAccNo());//银行卡号
                                                            record.setBusinessnumber(new BigDecimal(1));//业务编号
                                                            record.setBusinessname("信用卡还款");//业务名称
                                                            record.setBankid(requestDTO.getCollectBankId()); //银行编码
                                                            record.setBankname(requestDTO.getCollectBankName());//银行名称
                                                            record.setCardholdername(requestDTO.getCollectName());//持卡人
                                                            String shortBankCardNumber = "尾号"+requestDTO.getCollectAccNo().substring(requestDTO.getCollectAccNo().length() - 4);
                                                            record.setShortbankcardnumber(shortBankCardNumber);//银行卡号后四位简称
                                                            record.setCreatetime(tradeTime); //创建时间
                                                            record.setState("0");//有效
                                                            record.setMobilephone(sessionInfo.getMobilephone());//手机号
                                                            //插入记录
                                                            message = getTransHistoryRecord(record);

                                                            if(message.equals(RetAppMessage.HISTORYRECORDSAVESUCCESS) || message.equals(RetAppMessage.TRADINGINFOSAVESUCCESS)){
                                                                logger.info("存入收款人记录成功， 订单号："+orderNumber );
                                                            }else{
                                                                logger.info("存入收款人记录失败， 订单号："+orderNumber );
                                                            }
                                                            return jsonString;
                                                        }else{
                                                            //存入清算记录失败，记录日志
                                                            logger.info("存入清算记录失败， 订单号："+orderNumber );
                                                            creditCardProOrderResponseDTO.setRetCode(1);
                                                            creditCardProOrderResponseDTO.setRetMessage("存入清算记录失败");
                                                            try {
                                                                jsonString = createJsonString(creditCardProOrderResponseDTO);
                                                            } catch (Exception em) {
                                                                em.printStackTrace();
                                                            }
                                                            return jsonString;
                                                        }
                                                    }else{
                                                        //修改当前订单失败
                                                        logger.info("参数提交异常，请联系管理员， 订单号："+orderNumber );
                                                        creditCardProOrderResponseDTO.setRetCode(1);
                                                        creditCardProOrderResponseDTO.setRetMessage("参数提交异常，请联系管理员");
                                                        try {
                                                            jsonString = createJsonString(creditCardProOrderResponseDTO);
                                                        } catch (Exception em) {
                                                            em.printStackTrace();
                                                        }
                                                        return jsonString;
                                                    }

                                                }else{
                                                    //调用pre失败
                                                    logger.info(response.getRetMessage()+"调用第三方支付失败， 订单号："+orderNumber );
                                                    creditCardProOrderResponseDTO.setRetCode(1);
                                                    creditCardProOrderResponseDTO.setRetMessage(response.getRetMessage());
                                                    try {
                                                        jsonString = createJsonString(creditCardProOrderResponseDTO);
                                                    } catch (Exception em) {
                                                        em.printStackTrace();
                                                    }
                                                    return jsonString;
                                                }
                                            }
                                        }


                                    }else if(requestDTO.getPayType().equals("2")){

                                    }
                                }else{
                                    //刷卡信息出错
                                    logger.info("刷卡信息出错" );
                                    creditCardProOrderResponseDTO.setRetCode(1);
                                    creditCardProOrderResponseDTO.setRetMessage("刷卡信息出错");
                                    try {
                                        jsonString = createJsonString(creditCardProOrderResponseDTO);
                                    } catch (Exception em) {
                                        em.printStackTrace();
                                    }
                                    return jsonString;
                                }
                            }
                        }
                    }else{
                        //商户不存在
                        logger.info("该商户不存在" );
                        creditCardProOrderResponseDTO.setRetCode(1);
                        creditCardProOrderResponseDTO.setRetMessage("该商户不存在");
                        try {
                            jsonString = createJsonString(creditCardProOrderResponseDTO);
                        } catch (Exception em) {
                            em.printStackTrace();
                        }
                        return jsonString;
                    }
                } catch (Exception e) {
                    //查询商户信息时出错
                    e.printStackTrace();
                    creditCardProOrderResponseDTO.setRetCode(1);
                    creditCardProOrderResponseDTO.setRetMessage("查询商户信息出错");
                    try {
                        jsonString = createJsonString(creditCardProOrderResponseDTO);
                    } catch (Exception em) {
                        em.printStackTrace();
                    }
                    return jsonString;
                }
            }else{
                //参数为空
                creditCardProOrderResponseDTO.setRetCode(1);
                creditCardProOrderResponseDTO.setRetMessage("参数不正确");
                try {
                    jsonString = createJsonString(creditCardProOrderResponseDTO);
                } catch (Exception em) {
                    em.printStackTrace();
                }
                return jsonString;
            }
        }else{
            //未登录
            creditCardProOrderResponseDTO.setRetCode(13);
            creditCardProOrderResponseDTO.setRetMessage("会话过期，请重新登陆");
            try {
                jsonString = createJsonString(creditCardProOrderResponseDTO);
            } catch (Exception em) {
                em.printStackTrace();
            }
            return jsonString;
        }
        try {
            jsonString = createJsonString(creditCardProOrderResponseDTO);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return jsonString;
    }

}