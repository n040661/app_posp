package xdt.service.impl;

import xdt.common.RetAppMessage;
import xdt.dao.*;
import xdt.dto.BrushCalorieOfConsumptionRequestDTO;
import xdt.dto.BrushCalorieOfConsumptionResponseDTO;
import xdt.model.*;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Service;
import xdt.dto.RemitProOrderRequestDTO;
import xdt.dto.RemitProOrderResponseDTO;
import xdt.service.IPublicTradeVerifyService;
import xdt.service.IRemitService;
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

/**
 * 转账汇款
 * User: Jeff
 * Date: 15-5-20
 * Time: 上午9:22
 * To change this template use File | Settings | File Templates.
 */
@Service
public class RemitServiceImpl extends BaseServiceImpl implements IRemitService {

    private Logger logger = Logger.getLogger(RemitServiceImpl.class);

    @Resource
    private IPmsMerchantInfoDao pmsMerchantInfoDao;//商户信息层
    @Resource
    private ITAccRateDao tAccRateDao;
    @Resource
    private IMerchantMineDao merchantMineDao;
    @Resource
    public IPmsAppTransInfoDao pmsAppTransInfoDao;
    @Resource
    private IPublicTradeVerifyService iPublicTradeVerifyService;


    /**
     * 生成订单
     * @param payPhoneAccountInfo
     * @param session
     * @param request
     * @return
     */
    @Override
    public String producedOrder(String payPhoneAccountInfo, HttpSession session, HttpServletRequest request) {
        String jsonString = "";
        RemitProOrderResponseDTO remitProOrderResponseDTO = new RemitProOrderResponseDTO();
        String message = INITIALIZEMESSAGE;
        SessionInfo sessionInfo = (SessionInfo)session.getAttribute(SessionInfo.SESSIONINFO);
        RemitProOrderRequestDTO requestDTO = null;
        //刷卡信息
        BrushCalorieOfConsumptionRequestDTO dto = null;
        //判断当前用户是否登录
        if(	sessionInfo != null ){


            //判断当前回话中是否存在欧单编号，不存在直接返回错误
            String oAgentNo = sessionInfo.getoAgentNo();
            if(StringUtils.isBlank(oAgentNo)){
                //未登录
                remitProOrderResponseDTO.setRetCode(13);
                remitProOrderResponseDTO.setRetMessage("会话过期，请重新登陆");
                try {
                    jsonString = createJsonString(remitProOrderResponseDTO);
                } catch (Exception em) {
                    em.printStackTrace();
                }
                return jsonString;
            }



            //判断请求体
            if(StringUtils.isNotBlank(payPhoneAccountInfo)){


                //解析请求对象
                try {
                    requestDTO = (RemitProOrderRequestDTO)parseJsonString(payPhoneAccountInfo,RemitProOrderRequestDTO.class);
                } catch (Exception e) {
                    remitProOrderResponseDTO.setRetCode(1);
                    remitProOrderResponseDTO.setRetMessage("参数出错");
                    logger.info("参数出错");
                    //参数出错
                    try {
                        jsonString = createJsonString(remitProOrderResponseDTO);
                    } catch (Exception em) {
                        em.printStackTrace();
                    }
                    return jsonString;

                }

                if(requestDTO != null && requestDTO.getDto() != null){
                     dto = requestDTO.getDto();
                }else{
                    //刷卡信息为空
                    remitProOrderResponseDTO.setRetCode(1);
                    remitProOrderResponseDTO.setRetMessage("刷卡信息为空");
                    logger.info("刷卡信息为空");
                    try {
                        jsonString = createJsonString(remitProOrderResponseDTO);
                    } catch (Exception em) {
                        em.printStackTrace();
                    }
                    return jsonString;

                }


                //判断当前用户是否有转账资格,正式商户才能转账
                PmsMerchantInfo merchantInfo = new PmsMerchantInfo();
                merchantInfo.setMobilephone(sessionInfo.getMobilephone());
                merchantInfo.setoAgentNo(oAgentNo);
                merchantInfo.setCustomertype("3");
                try {
                    List<PmsMerchantInfo> list = pmsMerchantInfoDao.searchList(merchantInfo);
                    if (list != null && list.size() > 0) {
                        PmsMerchantInfo pmsMerchantInfo = list.get(0);
                        if(pmsMerchantInfo.getMercSts().equals("60")){

                            //判断汇款金额是否超限

                            //获取通道的费率
                            Map<String, String>   paramMap = new HashMap<String, String>();
                            paramMap.put("mercid",sessionInfo.getMercId());//商户编号
                            paramMap.put("businesscode",TradeTypeEnum.transeMoney.getTypeCode());//业务编号
                            paramMap.put("oAgentNo",oAgentNo);
                            Map<String, String> resultMap= merchantMineDao.queryBusinessInfo(paramMap);

                            if(resultMap == null || resultMap.size() == 0){
                                //若查到的是空值，直接返回错误
                                remitProOrderResponseDTO.setRetCode(1);
                                remitProOrderResponseDTO.setRetMessage("没有查到相关费率配置，请联系客服人员");
                                logger.info("没有查到相关费率配置：" +sessionInfo.getMobilephone());
                                try {
                                    jsonString = createJsonString(remitProOrderResponseDTO);
                                } catch (Exception em) {
                                    em.printStackTrace();
                                }
                                return jsonString;
                            }


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
                            }else{
                                //若查到的是空值，直接返回错误
                                remitProOrderResponseDTO.setRetCode(1);
                                remitProOrderResponseDTO.setRetMessage("没有查到相关费率配置，请联系客服人员");
                                logger.info("没有查到相关费率配置：" +sessionInfo.getMobilephone());
                                try {
                                    jsonString = createJsonString(remitProOrderResponseDTO);
                                } catch (Exception em) {
                                    em.printStackTrace();
                                }
                                return jsonString;
                            }

                            if(Double.parseDouble(paymentAmount) > Double.parseDouble(maxTransMoney)){
                                 //金额超过最大金额
                                remitProOrderResponseDTO.setRetCode(1);
                                remitProOrderResponseDTO.setRetMessage("金额超过最大金额");
                                logger.info("交易金额大于最打金额");
                                try {
                                    jsonString = createJsonString(remitProOrderResponseDTO);
                                } catch (Exception em) {
                                    em.printStackTrace();
                                }
                                return jsonString;
                            }else if(Double.parseDouble(paymentAmount) < Double.parseDouble(minTransMoney)){
                                 // 金额小于最小金额
                                remitProOrderResponseDTO.setRetCode(1);
                                remitProOrderResponseDTO.setRetMessage("金额小于最小金额");
                                try {
                                    jsonString = createJsonString(remitProOrderResponseDTO);
                                } catch (Exception em) {
                                    em.printStackTrace();
                                }
                                logger.info("交易金额小于最小金额");
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
                                remitProOrderResponseDTO.setRetCode(1);
                                remitProOrderResponseDTO.setRetMessage("汇款卡号输入有误");
                                try {
                                    jsonString = createJsonString(remitProOrderResponseDTO);
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
                                    //写入欧单编号
                                    pmsAppTransInfo.setoAgentNo(oAgentNo);
                                    pmsAppTransInfo.setStatus(OrderStatusEnum.initlize.getStatus());//订单初始化状态
                                    pmsAppTransInfo.setTradetype(TradeTypeEnum.transeMoney.getTypeName());//转账汇款
                                    pmsAppTransInfo.setTradetime(UtilDate.getDateFormatter()); //设置时间
                                    pmsAppTransInfo.setPayeename(requestDTO.getCollectName());
                                    pmsAppTransInfo.setMercid(sessionInfo.getMercId());
                                    pmsAppTransInfo.setTradetypecode(TradeTypeEnum.transeMoney.getTypeCode());//转账汇款
                                    pmsAppTransInfo.setBankno(requestDTO.getCollectAccNo());
                                    pmsAppTransInfo.setBankname(requestDTO.getCollectBankName());
                                    pmsAppTransInfo.setChannelNum(SHUAKA);
                                    pmsAppTransInfo.setSnNO(dto.getSn());//设置sn


                                    //判断是何种付款方式
                                    if (requestDTO.getPayType().equals("1")) {//刷卡支付
                                        ViewKyChannelInfo channelInfo = AppPospContext.context.get(SHUAKA+REMITPAYMENT);
                                        pmsAppTransInfo.setBusinessNum(channelInfo.getBusinessnum());
                                        pmsAppTransInfo.setPayamount(dto.getPayAmount());//交易金额
                                        String orderNumber = UtilMethod.getOrderid("111");
                                        pmsAppTransInfo.setOrderid(orderNumber);//设置订单号
                                        pmsAppTransInfo.setPaymenttype("刷卡支付");
                                        pmsAppTransInfo.setPaymentcode("5");


                                        //写入凭证信息
                                        if(StringUtils.isBlank(dto.getAuthPath())){
                                            //如果凭证信息为空，直接返回失败
                                            //上送参数错误
                                            logger.info("上送参数错误， 商户号："+sessionInfo.getMercId() +"，结束时间："+ UtilDate.getDateFormatter());
                                            remitProOrderResponseDTO.setRetCode(1);
                                            remitProOrderResponseDTO.setRetMessage("凭证信息为空");
                                            try {
                                                jsonString = createJsonString(remitProOrderResponseDTO);
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
                                             fee = Double.parseDouble(rate) *  (Double.parseDouble(paymentAmount) + minPoundage);

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
                                            fee = Double.parseDouble(rate) * (Double.parseDouble(paymentAmount) + minPoundage) + minPoundage;
                                            factAmount = Double.parseDouble(paymentAmount) + fee;
                                        }

                                        pmsAppTransInfo.setPayamount(dto.getPayAmount());//交易金额
                                        pmsAppTransInfo.setFactamount(factAmount.toString());//实际金额
                                        pmsAppTransInfo.setOrderamount(dto.getPayAmount());//订单金额
                                        pmsAppTransInfo.setRate(rate);
                                        pmsAppTransInfo.setPoundage(fee.toString());
                                        pmsAppTransInfo.setCreditcardnumber(dto.getCardNo());
                                        pmsAppTransInfo.setDrawMoneyType("1");//普通提款

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
                                            ResultInfo payCheckResult = iPublicTradeVerifyService.totalVerify(Integer.parseInt(paymentAmount), TradeTypeEnum.transeMoney, PaymentCodeEnum.shuakaPay, oAgentNo, sessionInfo.getMercId());
                                            if(!payCheckResult.getErrCode().equals("0")){
                                                // 交易不支持
                                                remitProOrderResponseDTO.setRetCode(1);
                                                remitProOrderResponseDTO.setRetMessage(payCheckResult.getMsg());
                                                try {
                                                    jsonString = createJsonString(remitProOrderResponseDTO);
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
                                            String  sendStr8583 =	"param="+this.createBrushCalorieOfConsumptionDTORequest(sessionInfo, dto, orderNumber, REMITPAYMENT, rateStr,dto.getSn());
                                            if("param=fail".equals(sendStr8583)){
                                                //上送参数错误
                                                logger.info("上送参数错误， 订单号："+orderNumber +"，结束时间："+ UtilDate.getDateFormatter());
                                                // 金额小于最小金额
                                                remitProOrderResponseDTO.setRetCode(1);
                                                remitProOrderResponseDTO.setRetMessage("上送参数错误");
                                                try {
                                                    jsonString = createJsonString(remitProOrderResponseDTO);
                                                } catch (Exception em) {
                                                    em.printStackTrace();
                                                }
                                                return jsonString;
                                            }else if ("param=meros".equals(sendStr8583)){
                                                //上送参数错误
                                                logger.info("pos机信息读取失败， 订单号："+orderNumber +"，结束时间："+ UtilDate.getDateFormatter());
                                                // 金额小于最小金额
                                                remitProOrderResponseDTO.setRetCode(1);
                                                remitProOrderResponseDTO.setRetMessage("pos机信息读取失败，不支持的卡类型");
                                                try {
                                                    jsonString = createJsonString(remitProOrderResponseDTO);
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
                                                        merchantMinel.setoAgentNo(oAgentNo);
                                                        merchantMinel.setOrderid(orderNumber);   //订单号
                                                        merchantMinel.setBanksysnumber("xxxxx");//没有该项
                                                        merchantMinel.setAmount(pmsAppTransInfo.getPayamount()); //实际打款金额
                                                        merchantMinel.setBankname(requestDTO.getCollectBankName());	//		//开户行名称
                                                        merchantMinel.setStatus("2");		//是否成功    0 成功   1失败 2等待处理
                                                        merchantMinel.setMercId(sessionInfo.getMercId());	//商户编号
                                                        merchantMinel.setBusinesscode(TradeTypeEnum.transeMoney.getTypeCode());//业务编号（ 1 商户收款、2 转账汇款、3 信用卡还款、4手机充值、5 水煤电、 6 加油卡充值、7 提款（提现））
                                                        merchantMinel.setClrMerc(requestDTO.getCollectAccNo());		//结算账号（卡号）
                                                        merchantMinel.setCreateTime(UtilDate.getDateAndTimes()); 	//创建时间（提款  汇款  还款  请求时间）  格式YYYYMMDDHHmmssSSS   20150526105900000   17位
                                                        merchantMinel.setSettlementname(requestDTO.getCollectName());	//持卡人姓名
                                                        merchantMinel.setRate(rateStr);//费率
                                                        merchantMinel.setPoundage(fee.toString());//手续费
                                                        merchantMinel.setOrderamount(pmsAppTransInfo.getOrderamount());//订单金额

                                                        Integer mineResult = merchantMineDao.saveDrawMoneyAcc(merchantMinel);

                                                        if(mineResult.equals(1)){


                                                            //存入清算记录成功
                                                            remitProOrderResponseDTO.setRetCode(0);
                                                            remitProOrderResponseDTO.setRetMessage("交易成功");
                                                            try {
                                                                jsonString = createJsonString(remitProOrderResponseDTO);
                                                            } catch (Exception em) {
                                                                em.printStackTrace();
                                                            }
                                                            String tradeTime = sdf.format(new Date());
                                                            //插入转账历史记录表
                                                            PmsTransHistoryRecord record = new PmsTransHistoryRecord();
                                                            //存入欧单编号
                                                            record.setoAgentNo(oAgentNo);
                                                            record.setMercid(sessionInfo.getMercId());//商户id
                                                            record.setBankcardnumber(requestDTO.getCollectAccNo());//银行卡号
                                                            record.setBusinessnumber(new BigDecimal(2));//业务编号    2：银行卡
                                                            record.setBusinessname("转账汇款");//业务名称
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
                                                            }else if (message.equals(RetAppMessage.HISTORYALREADYEXIST)){
                                                                //记录已经存在
                                                                logger.info("存入收款人记录失败，记录已经存在， 订单号："+orderNumber );
                                                            }else{
                                                                logger.info("存入收款人记录失败， 订单号："+orderNumber );
                                                            }
                                                            return jsonString;
                                                        }else{
                                                            //存入清算记录失败，记录日志
                                                            logger.info("存入清算记录失败， 订单号："+orderNumber );
                                                            remitProOrderResponseDTO.setRetCode(1);
                                                            remitProOrderResponseDTO.setRetMessage("存入清算记录失败");
                                                            try {
                                                                jsonString = createJsonString(remitProOrderResponseDTO);
                                                            } catch (Exception em) {
                                                                em.printStackTrace();
                                                            }
                                                            return jsonString;
                                                        }
                                                    }else{
                                                        //修改当前订单失败
                                                        logger.info("参数提交异常，请联系管理员， 订单号："+orderNumber );
                                                        remitProOrderResponseDTO.setRetCode(1);
                                                        remitProOrderResponseDTO.setRetMessage("参数提交异常，请联系管理员");
                                                        try {
                                                            jsonString = createJsonString(remitProOrderResponseDTO);
                                                        } catch (Exception em) {
                                                            em.printStackTrace();
                                                        }
                                                        return jsonString;
                                                    }

                                                }else{
                                                    //调用pre失败
                                                    logger.info("刷卡返回失败， 订单号："+orderNumber );
                                                    remitProOrderResponseDTO.setRetCode(1);
                                                    remitProOrderResponseDTO.setRetMessage(response.getRetMessage());
                                                    try {
                                                        jsonString = createJsonString(remitProOrderResponseDTO);
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
                                    remitProOrderResponseDTO.setRetCode(1);
                                    remitProOrderResponseDTO.setRetMessage("刷卡信息出错");
                                    try {
                                        jsonString = createJsonString(remitProOrderResponseDTO);
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
                        remitProOrderResponseDTO.setRetCode(1);
                        remitProOrderResponseDTO.setRetMessage("该商户不存在");
                        try {
                            jsonString = createJsonString(remitProOrderResponseDTO);
                        } catch (Exception em) {
                            em.printStackTrace();
                        }
                        return jsonString;
                    }
                } catch (Exception e) {
                    //查询商户信息时出错
                    e.printStackTrace();
                    remitProOrderResponseDTO.setRetCode(1);
                    remitProOrderResponseDTO.setRetMessage("查询商户信息出错");
                    try {
                        jsonString = createJsonString(remitProOrderResponseDTO);
                    } catch (Exception em) {
                        em.printStackTrace();
                    }
                    return jsonString;
                }
            }else{
                //参数为空
                remitProOrderResponseDTO.setRetCode(1);
                remitProOrderResponseDTO.setRetMessage("参数不正确");
                try {
                    jsonString = createJsonString(remitProOrderResponseDTO);
                } catch (Exception em) {
                    em.printStackTrace();
                }
                return jsonString;
            }
        }else{
            //未登录
            remitProOrderResponseDTO.setRetCode(13);
            remitProOrderResponseDTO.setRetMessage("会话过期，请重新登陆");
            try {
                jsonString = createJsonString(remitProOrderResponseDTO);
            } catch (Exception em) {
                em.printStackTrace();
            }
            return jsonString;
        }
        try {
            jsonString = createJsonString(remitProOrderResponseDTO);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return jsonString;
    }
}
