package xdt.service.impl;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Service;
import xdt.baidu.BDUtil;
import xdt.common.RetAppMessage;
import xdt.dao.*;
import xdt.dto.*;
import xdt.model.*;
import xdt.offi.OffiPay;
import xdt.service.IPrepaidPhoneService;
import xdt.service.IPublicTradeVerifyService;
import xdt.servlet.AppPospContext;
import xdt.util.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: Jeff
 * Date: 15-5-8
 * Time: 下午2:58
 * To change this template use File | Settings | File Templates.
 */
@Service("prepaidPhoneService")
public class PrepaidPhoneServiceImpl extends BaseServiceImpl implements IPrepaidPhoneService {


    @Resource
    private IViewKyChannelInfoDao channelInfoDao; //通道信息层
    @Resource
    private IPmsMerchantInfoDao merchantInfoDao;//商户信息
    @Resource
    private IPmsAppTransInfoDao pmsAppTransInfoDao;//订单处理
    @Resource
    private OffiPay offiPay; //欧飞
    @Resource
    private IPmsAppAmountAndRateConfigDao pmsAppAmountAndRateConfigDao;
    @Resource
    private IMerchantMineDao merchantMineDao;
    @Resource
    private IPayCmmtufitDao payCmmtufitDao; //银行卡信息层
    @Resource
    private IAppRateConfigDao appRateConfigDao;
    @Resource
    private IPublicTradeVerifyService iPublicTradeVerifyService;
    private Logger logger = Logger.getLogger(PrepaidPhoneServiceImpl.class);
    /**
     * 号段查询
     *
     * @param payPhoneAccountInfo
     * @param session
     * @param request
     * @return
     * @throws Exception
     */
    @Override
    public String themRoughlyQuery(String payPhoneAccountInfo, HttpSession session, HttpServletRequest request) throws Exception {
        setMethodSession(request.getRemoteAddr());
        String message = INITIALIZEMESSAGE;
        PrepaidPhoneThemRoughlyQueryResponseDTO responseData = new PrepaidPhoneThemRoughlyQueryResponseDTO();
        SessionInfo sessionInfo = (SessionInfo) session.getAttribute(SessionInfo.SESSIONINFO);
        String oAgentNo = "";
        //判断会话是否失效
        if (null != sessionInfo) {
            oAgentNo = sessionInfo.getoAgentNo();
            if(StringUtils.isBlank(oAgentNo)){
                responseData.setRetCode(1);
                responseData.setRetMessage("参数错误");
                String jsonString = createJsonString(responseData);
                return jsonString;
            }

            Object obj = parseJsonString(payPhoneAccountInfo, PrepaidPhoneThemRoughlyQueryRequestDTO.class);
            if (!obj.equals(DATAPARSINGMESSAGE)) {
                PrepaidPhoneThemRoughlyQueryRequestDTO phoneInfo = (PrepaidPhoneThemRoughlyQueryRequestDTO) obj;
                String mobilePhone = phoneInfo.getMobilePhone(); //手机号
                setSession(request.getRemoteAddr(), session.getId(), mobilePhone);
                //判断信息是否为空
                if (isNotEmptyValidate(mobilePhone)) {
                    //输入的手机号是否合法
                    if (checkPhone(mobilePhone)) {
                        ViewKyChannelInfo channelInfo = AppPospContext.context.get(OFFICHANNEL+PHONETHEMROUGHLYTHEQUERYBUSSINESSNUM);
                        if (null != channelInfo) {
                            //请求第三方手机充值号段查询接口
                            String path = channelInfo.getUrl();
                            if (StringUtils.isNotBlank(path)) {
                                path += "?mobilenum=" + mobilePhone.trim();
                                String result = HttpURLConection.httpURLConectionGET(path, "gb2312");
                                if (StringUtils.isNotBlank(result)) {
                                    if (result.contains("|")) {
                                        String[] params = result.split("\\|");
                                        if (params.length == 3) {
                                            String area = params[1];
                                            if(StringUtils.isNotBlank(params[1]) && (params[1].length() == 4)){
                                                   String str1 = params[1].substring(0,2);
                                                   String str2 = params[1].substring(2,4);
                                                 if(str1.equals(str2)){
                                                     area = str1;
                                                 }
                                            }
                                            responseData.setArea(area);

                                            responseData.setIsptype(params[2]);
                                            message = SUCCESSMESSAGE;
                                        } else {
                                            insertAppLogs(mobilePhone, "", "2042");
                                            message = ERRORMESSAGE;
                                        }
                                    } else {
                                        insertAppLogs(mobilePhone, "", "2042");
                                        message = ERRORMESSAGE;
                                    }
                                } else {
                                    insertAppLogs(mobilePhone, "", "2042");
                                    message = FAILMESSAGE;
                                }
                            }
                        } else {
                            insertAppLogs(mobilePhone, "", "2024");
                            message = EXISTMESSAGE;
                        }
                    } else {
                        insertAppLogs(mobilePhone, "", "2003");
                        message = INVALIDMESSAGE;
                    }
                } else {
                    insertAppLogs(payPhoneAccountInfo, "", "2002");
                    message = EMPTYMESSAGE;
                }
            } else {
                insertAppLogs("", "", "2001");
                message = DATAPARSINGMESSAGE;
            }
        } else {
            message = RetAppMessage.SESSIONINVALIDATION;
        }
        //解析要返回的信息
        int retCode = Integer.parseInt(message.split(":")[0]);
        String retMessage = message.split(":")[1];
        if (retMessage.equals("initialize")) {
            retMessage = "系统初始化";
        } else if (retMessage.equals("sessionInvalidation")) {
            retMessage = "会话失效，请重新登录";
        } else if (retMessage.equals("dataParsing")) {
            retMessage = "数据解析错误";
        } else if (retMessage.equals("empty")) {
            retMessage = "手机号不能为空";
        } else if (retMessage.equals("invalid")) {
            retMessage = "手机号不合法";
        } else if (retMessage.equals("exist")) {
            retMessage = "通道方不存在";
        } else if (retMessage.equals("fail")) {
            retMessage = "查询失败";
        } else if (retMessage.equals("error")) {
            retMessage = "号段查询错误";
        } else if (retMessage.equals("success")) {
            retMessage = "查询成功";
        }
        responseData.setRetCode(retCode);
        responseData.setRetMessage(retMessage);
        String jsonString = createJsonString(responseData);
        return jsonString;
    }

    /**
     * 号段查询异常
     *
     * @param session
     * @return
     * @throws Exception
     */
    @Override
    public String themRoughlyQueryException(HttpSession session) throws Exception {
        PrepaidPhoneThemRoughlyQueryResponseDTO responseData = new PrepaidPhoneThemRoughlyQueryResponseDTO();
        responseData.setRetCode(100);
        responseData.setRetMessage("系统异常");
        insertAppLogs(((SessionInfo) session.getAttribute(SessionInfo.SESSIONINFO)).getMobilephone(), "", "2043");
        return createJsonString(responseData);
    }

    /**
     * 手机充值金额查询
     *
     * @param payPhoneAccountInfo
     * @param session
     * @return
     * @throws Exception
     */
    @Override
    public String phoneMoneyQuery(String payPhoneAccountInfo, HttpSession session) throws Exception {
        String message = INITIALIZEMESSAGE;
        Object obj = parseJsonString(payPhoneAccountInfo, PrepaidPhoneProductQueryRequestDTO.class);
        SessionInfo sessionInfo = (SessionInfo) session.getAttribute(SessionInfo.SESSIONINFO);
        //欧单编号
        String oAgentNo = "";
        String inprice = "";
        if (null != sessionInfo) {
            oAgentNo = sessionInfo.getoAgentNo();
            if(StringUtils.isBlank(oAgentNo)){
                //如果没有欧单编号直接返回错误
                PrepaidPhoneProductQueryResponseDTO responseData = new PrepaidPhoneProductQueryResponseDTO();
                responseData.setRetCode(1);
                responseData.setRetMessage("参数错误");
                String jsonString = createJsonString(responseData);
                return jsonString;
            }

            if (!obj.equals(DATAPARSINGMESSAGE)) {
                PrepaidPhoneProductQueryRequestDTO phoneInfo = (PrepaidPhoneProductQueryRequestDTO) obj;
                String mobilePhone = phoneInfo.getMobilePhone(); //手机号
                String price = phoneInfo.getPrice();//充值金额
                Integer pricePay = 0;
                if(StringUtils.isNotBlank(price) && StringUtils.isNumeric(price)){
                    pricePay = Integer.parseInt(price);
                    pricePay = pricePay/100;
                }
                PmsAppAmountAndRateConfig pmsAppAmountAndRateConfig = new PmsAppAmountAndRateConfig();
                pmsAppAmountAndRateConfig.setMercId(sessionInfo.getMercId());
                //设置充值话费业务
                pmsAppAmountAndRateConfig.setBusinesscode(TradeTypeEnum.phonePay.getTypeCode());
                    if (StringUtils.isNotBlank(mobilePhone) && checkPhone(mobilePhone)) {
                        inprice = offiPay.phoneMoneyQuery(mobilePhone, pricePay.toString());
                        if (StringUtils.isNotBlank(inprice)) {
                            //转换单位为分
                            Double d = Double.parseDouble(inprice) * 100;
                            //向上取整
                            inprice = String.valueOf((int)Math.ceil(d));
                            message = SUCCESSMESSAGE;
                        }else{
                            message=" 1:erroPrice";
                        }
                    } else {
                        message = DATAPARSINGMESSAGE;
                    }
            }
        } else {
            message = RetAppMessage.SESSIONINVALIDATION;
        }
        //解析要返回的信息
        int retCode = Integer.parseInt(message.split(":")[0]);
        String retMessage = message.split(":")[1];
        if (retMessage.equals("initialize")) {
            retMessage = "系统初始化";
        } else if (retMessage.equals("dataParsing")) {
            retMessage = "数据解析错误";
        } else if (retMessage.equals("success")) {
            retMessage = "查询成功";
        } else if (retMessage.equals("fail")) {
            retMessage = "查询失败";
        } else if (retMessage.equals("sessionInvalidation")) {
            retMessage = "会话失效，请重新登录";
        } else if (retMessage.equals("erroPrice")) {
            retMessage = "该运营商不支持该充值金额";
        }
        PrepaidPhoneProductQueryResponseDTO responseData = new PrepaidPhoneProductQueryResponseDTO();
        responseData.setInprice(inprice);
        responseData.setRetCode(retCode);
        responseData.setRetMessage(retMessage);
        String jsonString = createJsonString(responseData);
        return jsonString;
    }


    /**
     * 手机充值金额查询异常
     * @param session
     * @return
     * @throws Exception
     */
    @Override
    public String phoneMoneyQueryException(HttpSession session) throws Exception {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    /**
     * 手机充值，生成订单
     * @param phoneInfo
     * @param session
     * @return
     * @throws Exception
     */
    @Override
    public synchronized String producedOrder(String phoneInfo, HttpSession session) throws Exception {
        String message = INITIALIZEMESSAGE;
        String jsonString = null;
        Object obj = parseJsonString(phoneInfo, GenerateMobileOrderRequestDTO.class);
        GeneralMobileOrderResponseDTO responseDTO = new GeneralMobileOrderResponseDTO();
        SessionInfo sessionInfo = (SessionInfo) session.getAttribute(SessionInfo.SESSIONINFO);
        String oAgentNo = "";
        String paymentAmount = "";

        if (null != sessionInfo){
        	oAgentNo = sessionInfo.getoAgentNo();

            if(StringUtils.isBlank(oAgentNo)){
                //如果没有欧单编号，直接返回错误
                responseDTO.setRetCode(1);
                responseDTO.setRetMessage("参数错误");
                jsonString = createJsonString(responseDTO);
            }




            if (!obj.equals(DATAPARSINGMESSAGE)) {

                GenerateMobileOrderRequestDTO generateMobileOrderRequestDTO = (GenerateMobileOrderRequestDTO) obj;
                if(StringUtils.isNotBlank(generateMobileOrderRequestDTO.getMobilePhone())){
                    //分发请求
                    if (StringUtils.isNotBlank(generateMobileOrderRequestDTO.getPayType())) {

                        //组装订单相同的部分
                        String orderId = UtilMethod.getOrderid("130");
                        PmsAppTransInfo pmsAppTransInfo = new PmsAppTransInfo();
                        //设置欧单编号
                        pmsAppTransInfo.setoAgentNo(oAgentNo);
                        pmsAppTransInfo.setTradetype(TradeTypeEnum.phonePay.getTypeName());//手机充值
                        pmsAppTransInfo.setTradetypecode(TradeTypeEnum.phonePay.getTypeCode());//手机充值

                        pmsAppTransInfo.setTradetime(UtilDate.getDateFormatter());
                        pmsAppTransInfo.setOrderid(orderId);//手机充值业务编码
                        pmsAppTransInfo.setStatus(OrderStatusEnum.initlize.getStatus());
                        pmsAppTransInfo.setPhonenumbertype(generateMobileOrderRequestDTO.getOperatorName());//河北联通，唐山移动。。。
                        pmsAppTransInfo.setPrepaidphonenumber(generateMobileOrderRequestDTO.getMobilePhone());//充值手机号
                        pmsAppTransInfo.setAmount(generateMobileOrderRequestDTO.getRechargeAmtValue().toString()); //充值面额 (分) 例：500

                        pmsAppTransInfo.setReasonofpayment("手机充值");
                        //访问欧飞，计算充值面额的实际金额
                        paymentAmount = offiPay.phoneMoneyQuery(generateMobileOrderRequestDTO.getMobilePhone(),
                                generateMobileOrderRequestDTO.getRechargeAmtValue().divide(new BigDecimal(100)).toString());


                        if(StringUtils.isNotBlank(paymentAmount)){
                            Double inpriceD = Double.parseDouble(paymentAmount);
                            //转换单位
                            inpriceD = inpriceD * 100;
                            //计算订单金额
                            paymentAmount = String.valueOf((int)Math.ceil(inpriceD));
                        }else{
                            // 交易金额不支持
                            responseDTO.setRetCode(1);
                            responseDTO.setRetMessage("交易金额不支持");
                            try {
                                jsonString = createJsonString(responseDTO);
                            } catch (Exception em) {
                                em.printStackTrace();
                            }
                            logger.info("交易金额不支持");
                            return jsonString;
                        }
                        //第一次不做计算直接存入面值 和订单金额
                        pmsAppTransInfo.setFactamount(paymentAmount); //设置实际金额，初步设置，后期随着状态的改变将改变
                        pmsAppTransInfo.setMercid(sessionInfo.getMercId()); //设置商户id
                        pmsAppTransInfo.setOrderamount(generateMobileOrderRequestDTO.getRechargeAmtValue().toString());//设置订单金额
                        pmsAppTransInfo.setPayamount(paymentAmount);//设置结算

                        //如果当前为刷卡，则先写入凭证信息
                        if(generateMobileOrderRequestDTO.getDto() != null && StringUtils.isNotBlank(generateMobileOrderRequestDTO.getDto().getAuthPath())){
                            pmsAppTransInfo.setAuthPath(PIRPREURL+generateMobileOrderRequestDTO.getDto().getAuthPath());
                        }

                        //设置交易地址
                        if(StringUtils.isNotBlank(generateMobileOrderRequestDTO.getAltLat())){
                            pmsAppTransInfo.setAltLat(generateMobileOrderRequestDTO.getAltLat());
                        }
                        if(StringUtils.isNotBlank(generateMobileOrderRequestDTO.getGpsAddress())){
                            pmsAppTransInfo.setGpsAddress(generateMobileOrderRequestDTO.getGpsAddress());
                        }
                        Integer appTransInsert = pmsAppTransInfoDao.insert(pmsAppTransInfo);





                        if(appTransInsert == 1){
                            //插入成功，下面的操作


                            if (generateMobileOrderRequestDTO.getPayType().equals("1")) {


                                //验证支付方式是否开启
                                ResultInfo payCheckResult = iPublicTradeVerifyService.totalVerify(Integer.parseInt(paymentAmount),TradeTypeEnum.phonePay,PaymentCodeEnum.shuakaPay,oAgentNo,sessionInfo.getMercId());
                                if(!payCheckResult.getErrCode().equals("0")){
                                    // 交易不支持
                                    responseDTO.setRetCode(1);
                                    responseDTO.setRetMessage(payCheckResult.getMsg());
                                    try {
                                        jsonString = createJsonString(responseDTO);
                                    } catch (Exception em) {
                                        em.printStackTrace();
                                    }
                                    logger.info("不支持的支付方式，oAagentNo:"+oAgentNo+",payType:"+PaymentCodeEnum.shuakaPay.getTypeCode());
                                    return jsonString;
                                }

                                //刷卡支付  约定这里只操作订单表，三方前置负责流水表处理
                                BrushCalorieOfConsumptionRequestDTO dto  = generateMobileOrderRequestDTO.getDto();
                                //获取通道的费率
                                Map<String, String> paramMap = new HashMap<String, String>();
                                paramMap.put("mercid",sessionInfo.getMercId());//手机号
                                paramMap.put("businesscode",TradeTypeEnum.transeMoney.getTypeCode());//业务编号
                                paramMap.put("oAgentNo", oAgentNo);//欧单编号
                                Map<String, String> resultMap= merchantMineDao.queryBusinessInfo(paramMap);

                                String isTop =  resultMap.get("IS_TOP");
                                String rate =  resultMap.get("RATE");
                                String topPoundage =   resultMap.get("TOP_POUNDAGE");//封顶费率
                                String maxTransMoney =  resultMap.get("MAX_AMOUNT"); //每笔最大交易金额
                                String minTransMoney = resultMap.get("MIN_AMOUNT"); //每笔最小交易金额


                                if(Double.parseDouble(paymentAmount) > Double.parseDouble(maxTransMoney)){
                                    //金额超过最大金额
                                    responseDTO.setRetCode(1);
                                    responseDTO.setRetMessage("金额超过最大金额");
                                    logger.info("交易金额大于最打金额");
                                    try {
                                        jsonString = createJsonString(responseDTO);
                                    } catch (Exception em) {
                                        em.printStackTrace();
                                    }
                                    return jsonString;
                                }else if(Double.parseDouble(paymentAmount) < Double.parseDouble(minTransMoney)){
                                    // 金额小于最小金额
                                    responseDTO.setRetCode(1);
                                    responseDTO.setRetMessage("金额小于最小金额");
                                    try {
                                        jsonString = createJsonString(responseDTO);
                                    } catch (Exception em) {
                                        em.printStackTrace();
                                    }
                                    logger.info("交易金额小于最小金额");
                                    return jsonString;
                                }


                                Double factAmount = 0.0;
                                //费率
                                Double fee =0.0;
                                String rateStr = "";
                                //计算实际金额
                                if("1".equals(isTop)){

                                    rateStr = rate +"-"+ topPoundage;
                                    //是封顶费率类型
                                    fee = Double.parseDouble(rate) * Double.parseDouble(paymentAmount);

                                    if( fee > Double.parseDouble(topPoundage) ){
                                        //费率大于最大手续费，按最大手续费处理
                                        factAmount =  Double.parseDouble(topPoundage) + Double.parseDouble(paymentAmount);
                                        fee =   Double.parseDouble(topPoundage);
                                    }else {
                                        //按当前费率处理
                                        rateStr = rate;
                                        factAmount = Double.parseDouble(paymentAmount) + fee;
                                    }

                                }else{
                                    //按当前费率处理
                                    rateStr = rate;
                                    fee = Double.parseDouble(rate) * Double.parseDouble(paymentAmount);
                                    factAmount = Double.parseDouble(paymentAmount) + fee;
                                }
                                //将刷卡金额改为加上费率后的金额
                                dto.setPayAmount(String.valueOf((int)Math.ceil(factAmount)));
                                String sendStr8583 = "param="+createBrushCalorieOfConsumptionDTORequest(sessionInfo,dto,pmsAppTransInfo.getOrderid(),CREDITPHONEPAY,rateStr,dto.getSn());
                                if("param=fail".equals(sendStr8583)){
                                    //上送参数错误
                                    logger.info("上送参数错误， 订单号："+orderId +"，结束时间："+ UtilDate.getDateFormatter());
                                    // 金额小于最小金额
                                    responseDTO.setRetCode(1);
                                    responseDTO.setRetMessage("上送参数错误");
                                    try {
                                        jsonString = createJsonString(responseDTO);
                                    } catch (Exception em) {
                                        em.printStackTrace();
                                    }
                                    return jsonString;
                                }else if ("param=meros".equals(sendStr8583)){
                                    //上送参数错误
                                    logger.info("pos机信息读取失败， 订单号："+orderId +"，结束时间："+ UtilDate.getDateFormatter());
                                    // 金额小于最小金额
                                    responseDTO.setRetCode(1);
                                    responseDTO.setRetMessage("pos机信息读取失败，不支持的卡类型");
                                    try {
                                        jsonString = createJsonString(responseDTO);
                                    } catch (Exception em) {
                                        em.printStackTrace();
                                    }
                                    return jsonString;
                                }else{

                                    logger.info("调用三方前置刷卡接口请求参数：" + sendStr8583 + "，结束时间：" + UtilDate.getDateFormatter());
                                    ViewKyChannelInfo channelInfo = AppPospContext.context.get(SHUAKA+CREDITPHONEPAY);

                                    String successFlag = HttpURLConection.httpURLConnectionPOST(channelInfo.getUrl(), sendStr8583);

                                    logger.info("调用三方前置刷卡接口返回参数：" + successFlag + "，结束时间：" + UtilDate.getDateFormatter());

                                    BrushCalorieOfConsumptionResponseDTO response = (BrushCalorieOfConsumptionResponseDTO)parseJsonString(successFlag,BrushCalorieOfConsumptionResponseDTO.class);

                                    if("0000".equals(response.getRetCode())){//判断调用接口处理是否成功    0000表示刷卡成功
                                        //修改订单状态 加入相关信息
                                        PmsAppTransInfo pmsAppTrans = pmsAppTransInfoDao.searchOrderInfo(orderId);
                                        pmsAppTrans.setStatus(OrderStatusEnum.waitingPlantPay.getStatus());
                                        pmsAppTrans.setFactamount(factAmount.toString());//设置实际金额
                                        pmsAppTrans.setBankno(dto.getCardNo());//设置卡号
                                        pmsAppTrans.setPoundage(fee.toString()); //设置费率
                                        pmsAppTrans.setPaymentcode("5");//刷卡支付
                                        pmsAppTrans.setBrushType(generateMobileOrderRequestDTO.getBrushType());//设置刷卡类型
                                        pmsAppTrans.setSnNO(dto.getSn());//设置sn
                                        pmsAppTrans.setRate(rateStr);//设置费率
                                        pmsAppTrans.setPaymenttype("刷卡支付");
                                        List<PayCmmtufit> cardList = payCmmtufitDao.searchCardInfoByBeforeSix(dto.getCardNo().substring(0, 6)+ "%");
                                        if(cardList != null && cardList.size() > 0){
                                            pmsAppTrans.setBankname(cardList.get(0).getBnkName());
                                        }

                                        pmsAppTrans.setBusinessNum(PAYPHONEACCOUNTBUSINESSNUM);
                                        pmsAppTrans.setChannelNum(SELFCHANEL);
                                        Integer appTransUpdate = pmsAppTransInfoDao.update(pmsAppTrans);
                                        if(appTransUpdate == 1){
                                            //调用欧飞接口充值话费
                                            Integer resultOffi = offiPay.mobilePay(pmsAppTrans);
                                            if (resultOffi == 1) {//支付成功
                                                //支付成功，修改订单状态
                                                pmsAppTrans.setThirdPartResultCode(resultOffi.toString());
                                                pmsAppTrans.setFinishtime(UtilDate.getDateFormatter());
                                                pmsAppTrans.setThirdPartResultCode("1");//设置第三方返回码
                                                pmsAppTrans.setStatus(OrderStatusEnum.paySuccess.getStatus());
                                                pmsAppTransInfoDao.update(pmsAppTrans);
                                            } else if (resultOffi == 2) {  //正在支付，将状态改为正在支付
                                                pmsAppTrans.setThirdPartResultCode(resultOffi.toString());
                                                pmsAppTrans.setStatus(OrderStatusEnum.plantPayingNow.getStatus());
                                                pmsAppTransInfoDao.update(pmsAppTrans);
                                            }

                                            message = SUCCESSMESSAGE;
                                        }else{
                                            //刷卡支付错误
                                            logger.info("更新订单出错， 订单号："+orderId +"，结束时间："+ UtilDate.getDateFormatter());
                                            responseDTO.setRetCode(1);
                                            responseDTO.setRetMessage("更新订单出错，请查询订单");
                                            try {
                                                jsonString = createJsonString(responseDTO);
                                            } catch (Exception em) {
                                                em.printStackTrace();
                                            }
                                            return jsonString;
                                        }
                                    }else{
                                        //刷卡支付错误
                                        logger.info("刷卡支付错误，pre返回报错， 订单号："+orderId +"错误码："+response.getRetCode()+"，结束时间："+ UtilDate.getDateFormatter());
                                        // 金额小于最小金额
                                        responseDTO.setRetCode(1);
                                        responseDTO.setRetMessage(response.getRetMessage());
                                        try {
                                            jsonString = createJsonString(responseDTO);
                                        } catch (Exception em) {
                                            em.printStackTrace();
                                        }
                                        return jsonString;
                                    }
                                }
                            } else if (generateMobileOrderRequestDTO.getPayType().equals("2")) {

                                //第三方支付
                                if (StringUtils.isNotBlank(generateMobileOrderRequestDTO.getPayChannel())) {
                                    if (generateMobileOrderRequestDTO.getPayChannel().equals("1")) {
                                        //支付宝SDK
                                        //设置费率，手续费
                                    } else if (generateMobileOrderRequestDTO.getPayChannel().equals("2")) {
                                        //微信SDK
                                        //设置费率，手续费
                                    } else if (generateMobileOrderRequestDTO.getPayChannel().equals("3")) {
                                        //百度SDK
                                        //验证支付方式是否开启
                                        ResultInfo payCheckResult = iPublicTradeVerifyService.totalVerify(Integer.parseInt(paymentAmount),TradeTypeEnum.phonePay,PaymentCodeEnum.bdSDKPay,oAgentNo,sessionInfo.getMercId());
                                        if(!payCheckResult.getErrCode().equals("0")){
                                            // 交易不支持
                                            responseDTO.setRetCode(1);
                                            responseDTO.setRetMessage(payCheckResult.getMsg());
                                            try {
                                                jsonString = createJsonString(responseDTO);
                                            } catch (Exception em) {
                                                em.printStackTrace();
                                            }
                                            logger.info("不支持的支付方式，oAagentNo:"+oAgentNo+",payType:"+PaymentCodeEnum.shuakaPay.getTypeCode());
                                            return jsonString;
                                        }

                                        //查询当前订单
                                        //计算费率
                                        String rateStr = "0.006";
                                        AppRateConfig appRate = new AppRateConfig();
                                        appRate.setRateType("3");
                                        appRate.setoAgentNo(oAgentNo);
                                        AppRateConfig appRateConfig = appRateConfigDao.getByRateTypeAndoAgentNo(appRate);
                                        if(appRateConfig != null && StringUtils.isNotBlank(appRateConfig.getRate())){
                                            rateStr = appRateConfig.getRate();
                                        }

                                        Double fee = Double.parseDouble(rateStr) * Double.parseDouble(paymentAmount);
                                        Double factAmount = Double.parseDouble(paymentAmount) + fee ;
                                        PmsAppTransInfo pmsAppTrans = pmsAppTransInfoDao.searchOrderInfo(orderId);
                                        pmsAppTrans.setFactamount(String.valueOf(Math.ceil(factAmount)));
                                        pmsAppTrans.setPoundage(fee.toString());
                                        pmsAppTrans.setPaymenttype("百度支付");
                                        pmsAppTrans.setPaymentcode("2");
                                        pmsAppTrans.setStatus(OrderStatusEnum.waitingClientPay.getStatus());
                                        pmsAppTrans.setRate(rateStr);
                                        pmsAppTrans.setBusinessNum(BAIDUCALLBACKURL);
                                        //设置费率，手续费(百度没有)
                                        responseDTO.setOrderNumber(orderId);
                                        responseDTO.setPageUrl(BDUtil.generalBDSDKCallStr(pmsAppTrans));
                                        if( pmsAppTransInfoDao.update(pmsAppTrans) == 1){
                                            message = SUCCESSMESSAGE;
                                        }
                                    }
                                }
                            }
                        }else{
                            //插入数据错误
                            logger.info("插入数据错误， 订单号："+orderId +"，结束时间："+ UtilDate.getDateFormatter());
                            // 金额小于最小金额
                            responseDTO.setRetCode(1);
                            responseDTO.setRetMessage("系统错误，请重新下单");
                            try {
                                jsonString = createJsonString(responseDTO);
                            } catch (Exception em) {
                                em.printStackTrace();
                            }
                            return jsonString;
                        }
                    }
                }
            }
        } else {
            message = RetAppMessage.SESSIONINVALIDATION;
        }
        //解析要返回的信息
        int retCode = Integer.parseInt(message.split(":")[0]);
        String retMessage = message.split(":")[1];
        if (retMessage.equals("initialize")) {
            retMessage = "系统初始化";
        } else if (retMessage.equals("dataParsing")) {
            retMessage = "数据解析错误";
        } else if (retMessage.equals("success")) {
            retMessage = "充值成功";
        } else if (retMessage.equals("fail")) {
            retMessage = "查询失败";
        } else if (retMessage.equals("sessionInvalidation")) {
            retMessage = "会话失效，请重新登录";
        }
        responseDTO.setRetCode(retCode);
        responseDTO.setRetMessage(retMessage);
        jsonString = createJsonString(responseDTO);
        return  jsonString;
    }

}
