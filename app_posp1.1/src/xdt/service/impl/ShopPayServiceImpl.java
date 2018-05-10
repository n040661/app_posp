package xdt.service.impl;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Service;
import xdt.common.RetAppMessage;
import xdt.dao.*;
import xdt.dto.*;
import xdt.model.*;
import xdt.quickpay.mobao.MobaoPayHandel;
import xdt.quickpay.mobao.MobaoPayValidateResponseDto;
import xdt.quickpay.mobao.MobaoPrePayResponseDto;
import xdt.service.IMerchantCollectMoneyService;
import xdt.service.IPmsMessageService;
import xdt.service.IPublicTradeVerifyService;
import xdt.service.IShopPayService;
import xdt.servlet.AppPospContext;
import xdt.util.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * 商城支付服务层
 * User: Jeff
 * Date: 16-3-11
 * Time: 下午2:27
 * To change this template use File | Settings | File Templates.
 */
@Service
public class ShopPayServiceImpl extends BaseServiceImpl implements IShopPayService {
    @Resource
    IPmsGoodsDao pmsGoodsDao;
    private Logger logger = Logger.getLogger(ShopPayServiceImpl.class);
    @Resource
    private IPmsMerchantInfoDao pmsMerchantInfoDao;//商户信息层
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
     * 商城生成订单
     *
     * @param session
     * @param shopPayRequest
     * @return
     */
    @Override
    public synchronized String produceOrder(HttpSession session, String shopPayRequest) throws Exception {
        String message = INITIALIZEMESSAGE;
        String jsonString = null;
        Object obj = parseJsonString(shopPayRequest, SubmitOrderNoCardPayRequestDTO.class);
        SubmitOrderNoCardPayResponseDTO responseDTO = new SubmitOrderNoCardPayResponseDTO();
        SessionInfo sessionInfo = (SessionInfo) session.getAttribute(SessionInfo.SESSIONINFO);
        String oAgentNo = "";


        //判断当前用户是否登录
        if (null != sessionInfo) {
            oAgentNo = sessionInfo.getoAgentNo();

            if (StringUtils.isBlank(oAgentNo)) {
                //如果没有欧单编号，直接返回错误
                responseDTO.setRetCode(1);
                responseDTO.setRetMessage("参数错误");
                jsonString = createJsonString(responseDTO);
                logger.info("参数错误,没有欧单编号");
                return jsonString;
            }

            //判断当前商户是否有使用商城的权限  只有正式商户才能使用商城
            PmsMerchantInfo merchantInfo = new PmsMerchantInfo();
            merchantInfo.setMobilephone(sessionInfo.getMobilephone());
            merchantInfo.setoAgentNo(oAgentNo);
            merchantInfo.setCustomertype("3");
            List<PmsMerchantInfo> list = pmsMerchantInfoDao.searchList(merchantInfo);
            if (list != null && list.size() > 0) {

                PmsMerchantInfo pmsMerchantInfo = list.get(0);
                if (pmsMerchantInfo.getMercSts().equals("60")) {


                    if (!obj.equals(DATAPARSINGMESSAGE)) {

                        SubmitOrderNoCardPayRequestDTO submitOrderNoCardPayRequestDTO = (SubmitOrderNoCardPayRequestDTO) obj;
                        //校验请求参数
                        if(StringUtils.isNotBlank(submitOrderNoCardPayRequestDTO.getReceiveType())){
                             if(submitOrderNoCardPayRequestDTO.getReceiveType().equals("1")){
                                    //物流配送
                                 if(StringUtils.isBlank(submitOrderNoCardPayRequestDTO.getReceiveAddressId())){
                                     //若查到的是空值，直接返回错误
                                     responseDTO.setRetCode(1);
                                     responseDTO.setRetMessage("参数错误，没有填写地址信息");
                                     logger.info("参数错误，没有填写地址信息：" +sessionInfo.getMobilephone());
                                     try {
                                         jsonString = createJsonString(responseDTO);
                                     } catch (Exception em) {
                                         em.printStackTrace();
                                     }
                                     return jsonString;
                                 }
                             }
                        }else{
                            //若查到的是空值，直接返回错误
                            responseDTO.setRetCode(1);
                            responseDTO.setRetMessage("参数错误，没有选择配送方式");
                            logger.info("参数错误，没有选择配送方式：" +sessionInfo.getMobilephone());
                            try {
                                jsonString = createJsonString(responseDTO);
                            } catch (Exception em) {
                                em.printStackTrace();
                            }
                            return jsonString;
                        }
                        if (submitOrderNoCardPayRequestDTO != null) {
                            //校验商品信息
                            if (submitOrderNoCardPayRequestDTO.getGoodsList() != null && submitOrderNoCardPayRequestDTO.getGoodsList().size() > 0) {

                                //查询商品价格
                                List<GoodsRequest> goodsList = submitOrderNoCardPayRequestDTO.getGoodsList();
                                //订单金额
                                Integer orderAmount = pmsGoodsDao.getGoodsPriceSum(goodsList);
                                //实际金额
                                String factAmount = submitOrderNoCardPayRequestDTO.getOrderRealTotalAmt();

                                //校验欧单金额限制
                                ResultInfo payCheckResult = iPublicTradeVerifyService.amountVerifyOagent((int)Double.parseDouble(factAmount), TradeTypeEnum.shop,oAgentNo);
                                if(!payCheckResult.getErrCode().equals("0")){
                                    // 交易不支持
                                    responseDTO.setRetCode(1);
                                    responseDTO.setRetMessage(payCheckResult.getMsg());
                                    try {
                                        jsonString = createJsonString(responseDTO);
                                    } catch (Exception em) {
                                        em.printStackTrace();
                                    }
                                    logger.info("欧单金额限制，oAagentNo:"+oAgentNo+",payType:"+PaymentCodeEnum.shuakaPay.getTypeCode());
                                    return jsonString;
                                }
                                //校验欧单模块是否开启
                                ResultInfo payCheckResult1 = iPublicTradeVerifyService.moduleVerifyOagent(TradeTypeEnum.shop,oAgentNo);
                                if(!payCheckResult1.getErrCode().equals("0")){
                                    // 交易不支持
                                    responseDTO.setRetCode(1);
                                    responseDTO.setRetMessage(payCheckResult1.getMsg());
                                    try {
                                        jsonString = createJsonString(responseDTO);
                                    } catch (Exception em) {
                                        em.printStackTrace();
                                    }
                                    logger.info("欧单模块限制，oAagentNo:"+oAgentNo+",payType:"+PaymentCodeEnum.shuakaPay.getTypeCode());
                                    return jsonString;
                                }
                                //校验商户模块是否开启
                                ResultInfo payCheckResult3 = iPublicTradeVerifyService.moduleVerifyOagent(TradeTypeEnum.shop,oAgentNo);
                                if(!payCheckResult3.getErrCode().equals("0")){
                                    // 交易不支持
                                    responseDTO.setRetCode(1);
                                    responseDTO.setRetMessage(payCheckResult3.getMsg());
                                    try {
                                        jsonString = createJsonString(responseDTO);
                                    } catch (Exception em) {
                                        em.printStackTrace();
                                    }
                                    logger.info("商户模块限制，oAagentNo:"+oAgentNo+",payType:"+PaymentCodeEnum.shuakaPay.getTypeCode());
                                    return jsonString;
                                }
                                //校验商户的商城类型交易金额是否超限

                                //获取通道的费率

                                Map<String, String> paramMap = new HashMap<String, String>();
                                paramMap.put("mercid",sessionInfo.getMercId());//商户编号
                                paramMap.put("businesscode", TradeTypeEnum.shop.getTypeCode());//业务编号
                                paramMap.put("oAgentNo",oAgentNo);
                                Map<String, String> resultMap= merchantMineDao.queryBusinessInfo(paramMap);

                                if(resultMap == null || resultMap.size() == 0){
                                    //若查到的是空值，直接返回错误
                                    responseDTO.setRetCode(1);
                                    responseDTO.setRetMessage("没有查到相关费率配置，请联系客服人员");
                                    logger.info("没有查到相关费率配置：" +sessionInfo.getMobilephone());
                                    try {
                                        jsonString = createJsonString(responseDTO);
                                    } catch (Exception em) {
                                        em.printStackTrace();
                                    }
                                    return jsonString;
                                }


                                String maxTransMoney =  resultMap.get("MAX_AMOUNT"); //每笔最大交易金额
                                String minTransMoney = resultMap.get("MIN_AMOUNT"); //每笔最小交易金额
                                String paymentAmount = factAmount;//交易金额

                                if(Double.parseDouble(paymentAmount) > Double.parseDouble(maxTransMoney)){
                                    //金额超过最大金额
                                    responseDTO.setRetCode(1);
                                    responseDTO.setRetMessage("金额超过最大交易金额");
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
                                    responseDTO.setRetMessage("金额小于最小交易金额");
                                    try {
                                        jsonString = createJsonString(responseDTO);
                                    } catch (Exception em) {
                                        em.printStackTrace();
                                    }
                                    logger.info("交易金额小于最小金额");
                                    return jsonString;
                                }


                                //组装订单数据
                                PmsAppTransInfo pmsAppTransInfo = new PmsAppTransInfo();

                                //写入欧单编号
                                pmsAppTransInfo.setoAgentNo(oAgentNo);
                                pmsAppTransInfo.setStatus(OrderStatusEnum.initlize.getStatus());//订单初始化状态
                                pmsAppTransInfo.setTradetype(TradeTypeEnum.shop.getTypeName());//商城
                                pmsAppTransInfo.setTradetime(UtilDate.getDateFormatter()); //设置时间
                                pmsAppTransInfo.setMercid(sessionInfo.getMercId());
                                pmsAppTransInfo.setTradetypecode(TradeTypeEnum.shop.getTypeCode());//商城
                                String orderNumber = UtilMethod.getOrderid("111");
                                pmsAppTransInfo.setOrderid(orderNumber);//设置订单号
                                //去掉金额末尾的无效0
                                BigDecimal factBigDecimal  = new BigDecimal(factAmount);
                                BigDecimal orderAmountBigDecimal  = new BigDecimal(orderAmount);
                                pmsAppTransInfo.setFactamount(factBigDecimal.stripTrailingZeros().toPlainString());//实际金额
                                pmsAppTransInfo.setOrderamount(orderAmountBigDecimal.stripTrailingZeros().toPlainString());//订单金额
                                pmsAppTransInfo.setDrawMoneyType("1");//普通提款
                                //设置交易地址
                                if(StringUtils.isNotBlank(submitOrderNoCardPayRequestDTO.getAltLat())){
                                    pmsAppTransInfo.setAltLat(submitOrderNoCardPayRequestDTO.getAltLat());
                                }
                                if(StringUtils.isNotBlank(submitOrderNoCardPayRequestDTO.getGpsAddress())){
                                    pmsAppTransInfo.setGpsAddress(submitOrderNoCardPayRequestDTO.getGpsAddress());
                                }
                                Integer insertAppTrans = pmsAppTransInfoDao.insert(pmsAppTransInfo);
                                if(insertAppTrans == 1){
                                   //添加商户商品订单
                                    List<String> goodsIdList = new ArrayList<String>();
                                    for(GoodsRequest goodsRequest:goodsList){
                                        goodsIdList.add(goodsRequest.getGoodsId());
                                    }
                                   //查询商品信息
                                    List<PmsGoods> pmsGoodsList = pmsGoodsDao.getPmsGoodsByIds(goodsIdList);
                                   //生成商品订单列表
                                    List<PmsGoodsOrder> pmsGoodsOrders = new ArrayList<PmsGoodsOrder>();
                                    for(GoodsRequest goodsRequest:goodsList){
                                        PmsGoodsOrder pmsGoodsOrder = new PmsGoodsOrder();
                                        pmsGoodsOrder.setOrderNo(orderNumber);
                                        pmsGoodsOrder.setGoodsId(goodsRequest.getGoodsId());
                                        pmsGoodsOrder.setGoodsNum(goodsRequest.getGoodsNum().toString());
                                        //从本地获取商品图片，单价等信息
                                        PmsGoods pmsGoods = getGoodsInfoById(pmsGoodsList,goodsRequest.getGoodsId());
                                        pmsGoodsOrder.setGoodsImageUrl(pmsGoods.getGoodsImageUrl());
                                        pmsGoodsOrder.setGoodsPrice(pmsGoods.getGoodsPrice());
                                        pmsGoodsOrder.setTotalAmt(String.valueOf(Double.parseDouble(pmsGoods.getGoodsPrice()) * goodsRequest.getGoodsNum()));
                                        pmsGoodsOrders.add(pmsGoodsOrder);
                                    }

                                    if(pmsGoodsOrders != null && pmsGoodsOrders.size() > 0 ) {
                                        //插入商品订单信息
                                       int insertbathcResult = pmsGoodsOrderDao.insertBatch(pmsGoodsOrders);
                                        if( insertbathcResult > 0){
                                            //插入成功 插入商品地址，留言等信息
                                            PmsOrderHelp pmsOrderHelp = new PmsOrderHelp();
                                            pmsOrderHelp.setOrderNo(orderNumber);
                                            pmsOrderHelp.setAddressId(submitOrderNoCardPayRequestDTO.getReceiveAddressId());
                                            //更具id 获取地址信息
                                            PmsAddress address = null;
                                            if(submitOrderNoCardPayRequestDTO.getReceiveType().equals("1")){
                                                address = pmsAddressDao.searchById(submitOrderNoCardPayRequestDTO.getReceiveAddressId());
                                                if(address != null){
                                                    pmsOrderHelp.setAddress(address.getAddress());
                                                }else{
                                                    //请求参数为空
                                                    logger.info("没有当前地址id的地址信息，request:"+submitOrderNoCardPayRequestDTO.toString());
                                                    responseDTO.setRetCode(1);
                                                    responseDTO.setRetMessage("地址信息获取异常");
                                                    jsonString = createJsonString(responseDTO);
                                                    return jsonString;
                                                }
                                            }
                                            pmsOrderHelp.setReceiveType(submitOrderNoCardPayRequestDTO.getReceiveType());
                                            //设置留言
                                            pmsOrderHelp.setBuyerMessage(submitOrderNoCardPayRequestDTO.getBuyerMessage());
                                            //插入数据库
                                            pmsOrderHelpDao.insert(pmsOrderHelp);

                                            //组装返回数据
                                            message = RetAppMessage.TRADINGSUCCESS;
                                            responseDTO.setOrderNo(orderNumber);
                                            responseDTO.setOrderAmt(factAmount);

                                            //载入模板配置文件
                                            List<OagentMsgCfg> oagentMsgCfgList = this.oagentMsgCfgList;
                                            if (oagentMsgCfgList != null && oagentMsgCfgList.size() > 0) {
                                                for (OagentMsgCfg oagentMsgCfg : oagentMsgCfgList) {
                                                    if (oagentMsgCfg.getoAgentNo().equals(oAgentNo)) {
                                                        responseDTO.setGoodsName(oagentMsgCfg.getDescribe());
                                                        break;
                                                    }
                                                }
                                            }
                                        }else{
                                            //请求参数为空
                                            logger.info("商品信息插入失败");
                                            responseDTO.setRetCode(1);
                                            responseDTO.setRetMessage("系统异常，请重试");
                                            jsonString = createJsonString(responseDTO);
                                            return jsonString;
                                        }
                                    }
                                }

                            } else {
                                //请求参数为空
                                logger.info("商品信息为空");
                                responseDTO.setRetCode(1);
                                responseDTO.setRetMessage("参数错误，请重试");
                                jsonString = createJsonString(responseDTO);
                                return jsonString;
                            }
                        } else {
                            //请求参数为空
                            logger.info("请求参数为空");
                            responseDTO.setRetCode(1);
                            responseDTO.setRetMessage("参数错误，请重试");
                            jsonString = createJsonString(responseDTO);
                            return jsonString;
                        }

                    } else {
                        //请求参数为空
                        logger.info("数据解析错误");
                        responseDTO.setRetCode(1);
                        responseDTO.setRetMessage("参数错误，请重试");
                        jsonString = createJsonString(responseDTO);
                        return jsonString;
                    }
                } else {
                    //请求参数为空
                    logger.info("商户没有进行实名认证，" + sessionInfo.getMercId());
                    responseDTO.setRetCode(1);
                    responseDTO.setRetMessage("还没有进行实名认证，请先去进行实名认证，或者等待客服审核");
                    jsonString = createJsonString(responseDTO);
                    return jsonString;
                }
            } else {
                message = RetAppMessage.SESSIONINVALIDATION;
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
        return jsonString;
    }

    /**
     * 商城快捷支付预下单
     * @param session
     * @param shopPayRequest
     * @return
     * @throws Exception
     */
    public synchronized String shopOrderQuickPrePay(HttpSession session, String shopPayRequest) throws Exception{
        SessionInfo sessionInfo = (SessionInfo) session.getAttribute(SessionInfo.SESSIONINFO);
        ShopOrderQuickPrePayResponseDTO  responseDTO = new ShopOrderQuickPrePayResponseDTO();
        Object obj = parseJsonString(shopPayRequest, ShopOrderQuickPrePayRequestDTO.class);
        String oAgentNo = "";
        String jsonString = null;
        //校验用户是否登录
        if (null != sessionInfo) {
            oAgentNo = sessionInfo.getoAgentNo();

            if (StringUtils.isBlank(oAgentNo)) {
                //如果没有欧单编号，直接返回错误
                responseDTO.setRetCode(1);
                responseDTO.setRetMessage("参数错误");
                jsonString = createJsonString(responseDTO);
                logger.info("参数错误,没有欧单编号");
                return jsonString;
            }

            //判断当前商户是否有使用商城的权限  只有正式商户才能使用商城
            PmsMerchantInfo merchantInfo = new PmsMerchantInfo();
            merchantInfo.setMobilephone(sessionInfo.getMobilephone());
            merchantInfo.setoAgentNo(oAgentNo);
            merchantInfo.setCustomertype("3");
            List<PmsMerchantInfo> list = pmsMerchantInfoDao.searchList(merchantInfo);
            if (list != null && list.size() > 0) {

                PmsMerchantInfo pmsMerchantInfo = list.get(0);
                if (pmsMerchantInfo.getMercSts().equals("60")) {
                    if (!obj.equals(DATAPARSINGMESSAGE)) {

                        ShopOrderQuickPrePayRequestDTO shopOrderQuickPrePayRequestDTO = (ShopOrderQuickPrePayRequestDTO) obj;
                        QuickpayCardRecord quickpayCardRed = null;
                        //校验参数
                        if(shopOrderQuickPrePayRequestDTO != null && StringUtils.isNotBlank(shopOrderQuickPrePayRequestDTO.getOrderId()) && StringUtils.isNotBlank(shopOrderQuickPrePayRequestDTO.getCardNo()) && StringUtils.isNotBlank(shopOrderQuickPrePayRequestDTO.getMobile())){
                            //解析银行卡
                            PayCmmtufit payCmmtufit = iPayCmmtufitDao.selectByCardNum(shopOrderQuickPrePayRequestDTO.getCardNo());
                            //查询当前卡是否进行了快捷支付认证
                            //查询卡片的本地记录
                            quickpayCardRed= quickpayRecordDao.searchById(shopOrderQuickPrePayRequestDTO.getCardNo());
                            if(quickpayCardRed != null){
                                 //已经认证过了

                                // 判断请求的手机号码是否是数据库中的手机号码
                                if(!shopOrderQuickPrePayRequestDTO.getMobile().equals(quickpayCardRed.getMobile())){
                                    //将传入的手机号设置到查出的记录中，兼容用户修改手机号的情况
                                    quickpayCardRed.setMobile(shopOrderQuickPrePayRequestDTO.getMobile());
                                }

                            }else{
                                //没有认证过，检查卡类型
                                if(payCmmtufit.getCrdFlg().equals("00")){
                                     //借记卡 查看证件类型，证件号，手机号，持卡人姓名
                                    logger.info("当前卡是借记卡，"+shopOrderQuickPrePayRequestDTO.toString());
                                      if(StringUtils.isBlank(shopOrderQuickPrePayRequestDTO.getCerType()) ||
                                              StringUtils.isBlank(shopOrderQuickPrePayRequestDTO.getCerNumber())
                                              ||StringUtils.isBlank(shopOrderQuickPrePayRequestDTO.getMobile())
                                      ||StringUtils.isBlank(shopOrderQuickPrePayRequestDTO.getCardByName())){
                                          //若查到的是空值，直接返回错误
                                          responseDTO.setRetCode(1);
                                          responseDTO.setRetMessage("借记卡快捷认证参数不完整");
                                          logger.info("借记卡快捷认证参数不完整：" +shopOrderQuickPrePayRequestDTO.toString());
                                          try {
                                              jsonString = createJsonString(responseDTO);
                                          } catch (Exception em) {
                                              em.printStackTrace();
                                          }
                                          return jsonString;
                                      }else{
                                          //认证借记卡快捷支付
                                          quickpayCardRed = new QuickpayCardRecord();
                                          quickpayCardRed.setCardType("01");
                                          quickpayCardRed.setMobile(shopOrderQuickPrePayRequestDTO.getMobile());
                                          quickpayCardRed.setCerType(shopOrderQuickPrePayRequestDTO.getCerType());
                                          quickpayCardRed.setCerNumber(shopOrderQuickPrePayRequestDTO.getCerNumber());
                                          quickpayCardRed.setCardByName(shopOrderQuickPrePayRequestDTO.getCardByName());
                                          quickpayCardRed.setCardNumber(shopOrderQuickPrePayRequestDTO.getCardNo());
                                          quickpayCardRed.setBankName(payCmmtufit.getBnkName());
                                      }
                                }else{
                                    //贷记卡  查看手机号 CVV 卡有效期
                                    if(StringUtils.isBlank(shopOrderQuickPrePayRequestDTO.getCerType()) ||
                                            StringUtils.isBlank(shopOrderQuickPrePayRequestDTO.getCerNumber())||
                                            StringUtils.isBlank(shopOrderQuickPrePayRequestDTO.getCardByName())||
                                            StringUtils.isBlank(shopOrderQuickPrePayRequestDTO.getMobile())||
                                            StringUtils.isBlank(shopOrderQuickPrePayRequestDTO.getCvv())
                                            ||StringUtils.isBlank(shopOrderQuickPrePayRequestDTO.getExpireDate())){
                                        //若查到的是空值，直接返回错误
                                        responseDTO.setRetCode(1);
                                        responseDTO.setRetMessage("贷记卡快捷认证参数不完整");
                                        logger.info("贷记卡快捷认证参数不完整：" +shopOrderQuickPrePayRequestDTO.toString());
                                        try {
                                            jsonString = createJsonString(responseDTO);
                                        } catch (Exception em) {
                                            em.printStackTrace();
                                        }
                                        return jsonString;
                                    }else{
                                        //认证贷记卡快捷支付
                                        quickpayCardRed = new QuickpayCardRecord();
                                        quickpayCardRed.setCardType("00");
                                        quickpayCardRed.setMobile(shopOrderQuickPrePayRequestDTO.getMobile());
                                        quickpayCardRed.setCvv(shopOrderQuickPrePayRequestDTO.getCvv());
                                        quickpayCardRed.setExpireDate(shopOrderQuickPrePayRequestDTO.getExpireDate());
                                        quickpayCardRed.setCardNumber(shopOrderQuickPrePayRequestDTO.getCardNo());
                                        quickpayCardRed.setBankName(payCmmtufit.getBnkName());
                                        quickpayCardRed.setCerType(shopOrderQuickPrePayRequestDTO.getCerType());
                                        quickpayCardRed.setCerNumber(shopOrderQuickPrePayRequestDTO.getCerNumber());
                                        quickpayCardRed.setCardByName(shopOrderQuickPrePayRequestDTO.getCardByName());
                                    }
                                }
                                quickpayCardRed.setMercId(sessionInfo.getMercId());
                                quickpayCardRed.setCreateTime(UtilDate.getTXDateTime());
                            }

                            //查询订单
                            PmsAppTransInfo pmsAppTransInfo = pmsAppTransInfoDao.searchOrderInfo(shopOrderQuickPrePayRequestDTO.getOrderId());

                            if(pmsAppTransInfo != null){
                                //校验订单状态  只有初始化状态或者等待客户端支付的状态才能支付
                                if( !pmsAppTransInfo.getStatus().equals(OrderStatusEnum.initlize.getStatus()) &&
                                        !pmsAppTransInfo.getStatus().equals(OrderStatusEnum.waitingClientPay.getStatus())){
                                    //如果没有欧单编号，直接返回错误
                                    responseDTO.setRetCode(1);
                                    responseDTO.setRetMessage("当前订单超时或已经失效，请重新下单或联系客服");
                                    jsonString = createJsonString(responseDTO);
                                    logger.info("当前订单不是可支付的状态，order:"+pmsAppTransInfo.getOrderid());
                                    return jsonString;
                                }
                                //校验当前订单发送短信的次数，如果超限，直接返回失败
                                if(!checkMessageLimit(pmsAppTransInfo.getOrderid())){
                                    //更新当前订单为失败状态
                                    pmsAppTransInfo.setStatus(OrderStatusEnum.payFail.getStatus());
                                    int updateReuslt = pmsAppTransInfoDao.update(pmsAppTransInfo);
                                    if(updateReuslt != 1){
                                        logger.info("修改订单为失败时失败 updateReuslt="+updateReuslt);
                                    }
                                    //如果没有欧单编号，直接返回错误
                                    responseDTO.setOrderStatus("1");
                                    responseDTO.setRetCode(103);
                                    responseDTO.setRetMessage("当前订单发送短信次数超限，请重新下单进行支付");
                                    jsonString = createJsonString(responseDTO);
                                    logger.info("当前订单发送短信次数超限");
                                    return jsonString;
                                }
                                 //设置支付方式
                                pmsAppTransInfo.setPaymenttype(PaymentCodeEnum.moBaoQuickPay.getTypeName());
                                pmsAppTransInfo.setPaymentcode(PaymentCodeEnum.moBaoQuickPay.getTypeCode());

                                //设置费率信息


                                //获取通道的费率

                                AppRateConfig appRate = new AppRateConfig();
                                appRate.setRateType(RateTypeEnum.mobaoQuickPayRateType.getTypeCode());
                                appRate.setoAgentNo(oAgentNo);
                                AppRateConfig appRateConfig = appRateConfigDao.getByRateTypeAndoAgentNo(appRate);

                                if(appRateConfig == null){
                                    //若查到的是空值，直接返回错误
                                    responseDTO.setRetCode(1);
                                    responseDTO.setRetMessage("没有查到相关费率配置，请联系客服人员");
                                    logger.info("没有查到相关费率配置：" +sessionInfo.getMobilephone());
                                    try {
                                        jsonString = createJsonString(responseDTO);
                                    } catch (Exception em) {
                                        em.printStackTrace();
                                    }
                                    return jsonString;
                                }


                                String isTop =  appRateConfig.getIsTop();
                                String rate =  appRateConfig.getRate();
                                String topPoundage =   appRateConfig.getTopPoundage();//封顶费率
                                String paymentAmount = pmsAppTransInfo.getFactamount();//支付金额
                                String minPoundageStr = appRateConfig.getBottomPoundage();//最低手续费
                                Double minPoundage = 0.0; //附加费

                                if(StringUtils.isNotBlank(appRateConfig.getIsBottom()) && appRateConfig.getIsBottom().equals("1")){
                                   if(StringUtils.isNotBlank(minPoundageStr)){
                                       minPoundage = Double.parseDouble(minPoundageStr);
                                   }else{
                                       //若查到的是空值，直接返回错误
                                       responseDTO.setRetCode(1);
                                       responseDTO.setRetMessage("没有查到相关费率配置（附加费），请联系客服人员");
                                       logger.info("没有查到相关费率附加费（最低手续费）：" +sessionInfo.getMobilephone());
                                       try {
                                           jsonString = createJsonString(responseDTO);
                                       } catch (Exception em) {
                                           em.printStackTrace();
                                       }
                                       return jsonString;
                                   }

                                }


                                Double payAmount = null;
                                Double factAmount = Double.parseDouble(pmsAppTransInfo.getFactamount());
                                //费率
                                Double fee =0.0;
                                String rateStr = "";
                                //计算结算金额
                                if("1".equals(isTop)){

                                    rateStr = rate +"-"+ topPoundage;
                                    //是封顶费率类型
                                    fee = Double.parseDouble(rate) *  factAmount;

                                    if( fee > Double.parseDouble(topPoundage) ){
                                        //费率大于最大手续费，按最大手续费处理
                                        payAmount = factAmount -  Double.parseDouble(topPoundage) - minPoundage;
                                        fee =   Double.parseDouble(topPoundage) + minPoundage;
                                    }else {
                                        //按当前费率处理
                                        rateStr = rate;
                                        fee += minPoundage;
                                        payAmount = factAmount - fee;
                                    }

                                }else{
                                    //按当前费率处理
                                    rateStr = rate;
                                    fee = Double.parseDouble(rate) * factAmount+ minPoundage;
                                    payAmount = factAmount - fee;
                                }

                                //设置结算金额
                                pmsAppTransInfo.setPayamount(payAmount.toString());//结算金额
                                pmsAppTransInfo.setRate(rateStr);
                                pmsAppTransInfo.setPoundage(fee.toString());
                                pmsAppTransInfo.setDrawMoneyType("1");//普通提款
                                //转换double为int
                                Integer paymentAmountInt = (int)Double.parseDouble(paymentAmount);
                                //验证支付方式是否开启
                                ResultInfo payCheckResult = iPublicTradeVerifyService.totalVerify(paymentAmountInt, TradeTypeEnum.shop, PaymentCodeEnum.moBaoQuickPay, oAgentNo, sessionInfo.getMercId());
                                if(!payCheckResult.getErrCode().equals("0")){
                                    // 交易不支持
                                    responseDTO.setRetCode(1);
                                    responseDTO.setRetMessage(payCheckResult.getMsg());
                                    try {
                                        jsonString = createJsonString(responseDTO);
                                    } catch (Exception em) {
                                        em.printStackTrace();
                                    }
                                    logger.info("不支持的支付方式，oAagentNo:"+oAgentNo+",payType:"+PaymentCodeEnum.moBaoQuickPay.getTypeCode());
                                    return jsonString;
                                }
                                ViewKyChannelInfo channelInfo = AppPospContext.context.get(MOBAOCHANNELNUM+MOBAOPAY);
                                //设置通道信息
                                pmsAppTransInfo.setBusinessNum(channelInfo.getBusinessnum());
                                pmsAppTransInfo.setChannelNum(MOBAOCHANNELNUM);
                                //设置银行卡及用户信息

                                if(payCmmtufit != null){
                                    pmsAppTransInfo.setBankno(shopOrderQuickPrePayRequestDTO.getCardNo());
                                    pmsAppTransInfo.setBankname(payCmmtufit.getBnkName());
                                    pmsAppTransInfo.setBankcardname(payCmmtufit.getCrdNm());

                                    if(quickpayCardRed != null){
                                        //将持卡人信息记录插入到预交易信息记录表中
                                        //查询当前与交易记录表中是否存在这条记录
                                        QuickpayPreRecord quickpayPreRecord = quickpayPreRecordDao.searchById(pmsAppTransInfo.getOrderid());
                                        if(quickpayPreRecord != null){
                                            //修改记录
                                            quickpayPreRecord.setBankName(quickpayCardRed.getBankName());
                                            quickpayPreRecord.setCardNumber(quickpayCardRed.getCardNumber());
                                            quickpayPreRecord.setCardType(quickpayCardRed.getCardType());
                                            quickpayPreRecord.setCerType(quickpayCardRed.getCerType());
                                            quickpayPreRecord.setCerNumber(quickpayCardRed.getCerNumber());
                                            quickpayPreRecord.setMobile(quickpayCardRed.getMobile());
                                            quickpayPreRecord.setCreateTime(UtilDate.getTXDateTime());
                                            quickpayPreRecord.setMercId(sessionInfo.getMercId());
                                            quickpayPreRecord.setCardByName(quickpayCardRed.getCardByName());
                                            quickpayPreRecord.setCvv(quickpayCardRed.getCvv());
                                            quickpayPreRecord.setExpireDate(quickpayCardRed.getExpireDate());
                                            quickpayPreRecord.setOrderId(pmsAppTransInfo.getOrderid());
                                            int updateReuslt = quickpayPreRecordDao.update(quickpayPreRecord);
                                            if(updateReuslt != 1){
                                                 logger.info("更新预交易记录表失败");
                                            }
                                        }else{
                                            //如果是空的，则插入一条记录
                                            QuickpayPreRecord preRecord = new QuickpayPreRecord();
                                            preRecord.setOrderId(pmsAppTransInfo.getOrderid());
                                            preRecord.setBankName(quickpayCardRed.getBankName());
                                            preRecord.setCardNumber(quickpayCardRed.getCardNumber());
                                            preRecord.setCardType(quickpayCardRed.getCardType());
                                            preRecord.setCerType(quickpayCardRed.getCerType());
                                            preRecord.setCerNumber(quickpayCardRed.getCerNumber());
                                            preRecord.setMobile(quickpayCardRed.getMobile());
                                            preRecord.setCreateTime(UtilDate.getTXDateTime());
                                            preRecord.setMercId(sessionInfo.getMercId());
                                            preRecord.setCardByName(quickpayCardRed.getCardByName());
                                            preRecord.setCvv(quickpayCardRed.getCvv());
                                            preRecord.setExpireDate(quickpayCardRed.getExpireDate());
                                            int insertResult = quickpayPreRecordDao.insert(preRecord);
                                            if(insertResult != 1){
                                                logger.info("插入预交易记录表失败");
                                            }
                                        }
                                        //查看当前交易是否已经生成了流水表
                                        PospTransInfo pospTransInfo = null;
                                        //流水表是否需要更新的标记 0 insert，1：update
                                        int insertOrUpdateFlag = 0;
                                        //生成上送流水号
                                        String transOrderId =  generateTransOrderId(TradeTypeEnum.shop,PaymentCodeEnum.shuakaPay);
                                        if((pospTransInfo = pospTransInfoDAO.searchByOrderId(pmsAppTransInfo.getOrderid())) != null){
                                            //已经存在，修改流水号，设置pospsn为空
                                            logger.info("订单号："+pmsAppTransInfo.getOrderid()+",生成上送通道的流水号："+transOrderId);
                                            pospTransInfo.setTransOrderId(transOrderId);
                                            pospTransInfo.setResponsecode("99");
                                            pospTransInfo.setCardno(shopOrderQuickPrePayRequestDTO.getCardNo());
                                            pospTransInfo.setPospsn("");
                                            insertOrUpdateFlag = 1 ;
                                        }else{
                                            //不存在流水，生成一个流水
                                            pospTransInfo =  generateTransFromAppTrans(pmsAppTransInfo);
                                            //设置上送流水号
                                            pospTransInfo.setTransOrderId(transOrderId);
                                            pospTransInfo.setCardno(shopOrderQuickPrePayRequestDTO.getCardNo());
                                            insertOrUpdateFlag = 0 ;
                                        }

                                       //调用摩宝快捷支付
                                        MobaoPrePayResponseDto mobaoPrePayResponseDto =  mobaoPayHandel.prePay(pmsAppTransInfo,pospTransInfo, quickpayCardRed,channelInfo);

                                        if(mobaoPrePayResponseDto == null){
                                            //成功
                                            responseDTO.setOrderStatus("1");
                                            responseDTO.setRetCode(1);
                                            responseDTO.setRetMessage("系统错误，请联系客服或重新下单");
                                            jsonString = createJsonString(responseDTO);
                                            logger.info("参数错误：request"+shopOrderQuickPrePayRequestDTO.toString()+",result="+responseDTO+",response="+jsonString);
                                            return jsonString;
                                        }
                                        pospTransInfo.setPospsn(mobaoPrePayResponseDto.getKsPayOrderId());
                                        //修改流水信息
                                        int handelTransResult = 0;
                                        if(insertOrUpdateFlag == 0){
                                           //插入一条流水
                                            handelTransResult =  pospTransInfoDAO.insert(pospTransInfo);
                                        }else if(insertOrUpdateFlag == 1){
                                           //更新一条流水
                                            handelTransResult = pospTransInfoDAO.updateByOrderId(pospTransInfo);
                                        }
                                        if(handelTransResult != 1){
                                            //成功
                                            responseDTO.setOrderStatus("1");
                                            responseDTO.setRetCode(1);
                                            responseDTO.setRetMessage("系统错误，请联系客服或重新下单");
                                            jsonString = createJsonString(responseDTO);
                                            logger.info("插入或更新流水表失败：request"+shopOrderQuickPrePayRequestDTO.toString()+",result="+responseDTO+",response="+jsonString);
                                            return jsonString;
                                        }
                                        pmsAppTransInfo.setStatus(OrderStatusEnum.waitingClientPay.getStatus());
                                        pmsAppTransInfo.setPortorderid(mobaoPrePayResponseDto.getKsPayOrderId());
                                        //修改订单信息
                                        int resultUpdateTrans = pmsAppTransInfoDao.update(pmsAppTransInfo);
                                        if(resultUpdateTrans == 1 && mobaoPrePayResponseDto != null && mobaoPrePayResponseDto.getRefCode() != null && mobaoPrePayResponseDto.getRefCode().equals("01")){
                                           //成功
                                            responseDTO.setOrderStatus("0");
                                            responseDTO.setRetCode(0);
                                            responseDTO.setRetMessage("调用成功");
                                            jsonString = createJsonString(responseDTO);
                                            logger.info("调用快捷支付预支付接口成功：response"+responseDTO.toString());
                                            return jsonString;
                                        }else if(mobaoPrePayResponseDto != null && mobaoPrePayResponseDto.getRefCode().equals("010104")){
                                            //已经存在的订单，调用短信重发接口重发接口
                                            responseDTO.setRetCode(0);
                                            responseDTO.setOrderStatus(mobaoPrePayResponseDto.getRefCode());
                                            responseDTO.setRetMessage(mobaoPrePayResponseDto.getRefMsg());
                                            jsonString = createJsonString(responseDTO);
                                            logger.info("调用银联快捷支付通道失败：response"+responseDTO.toString());
                                            return jsonString;
                                        }else if(mobaoPrePayResponseDto != null){
                                            responseDTO.setRetCode(1);
                                            responseDTO.setOrderStatus(mobaoPrePayResponseDto.getRefCode());
                                            responseDTO.setRetMessage(mobaoPrePayResponseDto.getRefMsg());
                                            jsonString = createJsonString(responseDTO);
                                            logger.info("调用银联快捷支付通道失败：response"+responseDTO.toString());
                                            return jsonString;
                                        }else{
                                            responseDTO.setRetCode(1);
                                            responseDTO.setOrderStatus("1");
                                            responseDTO.setRetMessage("调用银联快捷支付通道失败，请检查网络或者联系客服");
                                            jsonString = createJsonString(responseDTO);
                                            logger.info("调用银联快捷支付通道失败：response"+responseDTO.toString());
                                            return jsonString;
                                        }
                                    }else{
                                        responseDTO.setRetCode(1);
                                        responseDTO.setOrderStatus("1");
                                        responseDTO.setRetMessage("本地不存在该卡片的卡片信息,请先进行快捷支付卡片登记，或联系客服");
                                        jsonString = createJsonString(responseDTO);
                                        logger.info("本地不存在该卡片的卡片信息："+responseDTO.toString());
                                        return jsonString;
                                    }
                                }else{
                                    responseDTO.setRetCode(1);
                                    responseDTO.setOrderStatus("1");
                                    responseDTO.setRetMessage("占不支持此卡，请检查订单信息或联系客服");
                                    jsonString = createJsonString(responseDTO);
                                    logger.info("卡宾不存在："+responseDTO.toString());
                                    return jsonString;
                                }

                            }else{
                                responseDTO.setRetCode(1);
                                responseDTO.setOrderStatus("1");
                                responseDTO.setRetMessage("当前订单不存在，请检查订单信息或联系客服");
                                jsonString = createJsonString(responseDTO);
                                logger.info("当前订单不存在："+shopOrderQuickPrePayRequestDTO.toString());
                                return jsonString;
                            }

                        }else{
                            responseDTO.setRetCode(1);
                            responseDTO.setOrderStatus("1");
                            responseDTO.setRetMessage("参数错误");
                            jsonString = createJsonString(responseDTO);
                            logger.info("请求参数错误："+shopOrderQuickPrePayRequestDTO.toString());
                            return jsonString;
                        }
                    }
                }
            }

        }else{
            //回话失效
            responseDTO.setRetCode(13);
            responseDTO.setRetMessage("回话失效，请重新登陆");
            jsonString = createJsonString(responseDTO);
            logger.info("回话失效，请重新登陆");
            return jsonString;
        }
        //回话失效
        responseDTO.setRetCode(1);
        responseDTO.setRetMessage("系统错误，请重试");
        jsonString = createJsonString(responseDTO);
        logger.info("系统错误，请重试");
        return jsonString;
    }





    /**
     * 商城快捷支付认证
     * @param session
     * @param shopPayRequest
     * @return
     * @throws Exception
     */
    public synchronized String shopOrderQuickPay(HttpSession session, String shopPayRequest) throws Exception{
        SessionInfo sessionInfo = (SessionInfo) session.getAttribute(SessionInfo.SESSIONINFO);
        ShopOrderQuickPayResponseDTO  responseDTO = new ShopOrderQuickPayResponseDTO();
        Object obj = parseJsonString(shopPayRequest, ShopOrderQuickPayRequestDTO.class);
        String oAgentNo = "";
        String jsonString = null;
        //校验用户是否登录
        if (null != sessionInfo) {
            oAgentNo = sessionInfo.getoAgentNo();

            if (StringUtils.isBlank(oAgentNo)) {
                //如果没有欧单编号，直接返回错误
                responseDTO.setRetCode(1);
                responseDTO.setRetMessage("参数错误");
                jsonString = createJsonString(responseDTO);
                logger.info("参数错误,没有欧单编号");
                return jsonString;
            }

            //判断当前商户是否有使用商城的权限  只有正式商户才能使用商城
            PmsMerchantInfo merchantInfo = new PmsMerchantInfo();
            merchantInfo.setMobilephone(sessionInfo.getMobilephone());
            merchantInfo.setoAgentNo(oAgentNo);
            merchantInfo.setCustomertype("3");
            List<PmsMerchantInfo> list = pmsMerchantInfoDao.searchList(merchantInfo);
            if (list != null && list.size() > 0) {

                PmsMerchantInfo pmsMerchantInfo = list.get(0);
                if (pmsMerchantInfo.getMercSts().equals("60")) {



                    if (!obj.equals(DATAPARSINGMESSAGE)) {
                        //是否需要修改预留手机号标志
                        int mobileChangeFlag = 0;
                        //是否需要插入本地快捷认证记录标志
                        int needInsertQuichPayRecordFlag = 0;
                        ShopOrderQuickPayRequestDTO shopOrderQuickPayRequestDTO = (ShopOrderQuickPayRequestDTO) obj;
                        QuickpayCardRecord quickpayCardRed = null;
                        //校验参数
                        if(shopOrderQuickPayRequestDTO != null &&
                                StringUtils.isNotBlank(shopOrderQuickPayRequestDTO.getOrderId()) &&
                                StringUtils.isNotBlank(shopOrderQuickPayRequestDTO.getCardNo()) &&
                                StringUtils.isNotBlank(shopOrderQuickPayRequestDTO.getMessage())&&
                                StringUtils.isNotBlank(shopOrderQuickPayRequestDTO.getMobile())){

                            //解析银行卡
                            PayCmmtufit payCmmtufit = iPayCmmtufitDao.selectByCardNum(shopOrderQuickPayRequestDTO.getCardNo());
                            //查询当前卡是否进行了快捷支付认证
                            quickpayCardRed= quickpayRecordDao.searchById(shopOrderQuickPayRequestDTO.getCardNo());
                            if(quickpayCardRed != null){
                                //已经认证过了

                                // 判断请求的手机号码是否是数据库中的手机号码
                                if(!shopOrderQuickPayRequestDTO.getMobile().equals(quickpayCardRed.getMobile())){
                                    //将传入的手机号设置到查出的记录中，兼容用户修改预留手机号的情况
                                    quickpayCardRed.setMobile(shopOrderQuickPayRequestDTO.getMobile());
                                    mobileChangeFlag =1 ;
                                }

                            }else{
                                //没有认证过

                                needInsertQuichPayRecordFlag = 1;
                                //没有认证过，检查卡类型
                                if(payCmmtufit.getCrdFlg().equals("00")){
                                    //借记卡 查看证件类型，证件号，手机号，持卡人姓名
                                    logger.info("当前卡是借记卡，"+shopOrderQuickPayRequestDTO.toString());
                                    if(StringUtils.isBlank(shopOrderQuickPayRequestDTO.getCerType()) ||
                                            StringUtils.isBlank(shopOrderQuickPayRequestDTO.getCerNumber())
                                            ||StringUtils.isBlank(shopOrderQuickPayRequestDTO.getMobile())
                                            ||StringUtils.isBlank(shopOrderQuickPayRequestDTO.getCardByName())){
                                        //若查到的是空值，直接返回错误
                                        responseDTO.setRetCode(1);
                                        responseDTO.setRetMessage("借记卡快捷认证参数不完整");
                                        logger.info("借记卡快捷认证参数不完整：" +shopOrderQuickPayRequestDTO.toString());
                                        try {
                                            jsonString = createJsonString(responseDTO);
                                        } catch (Exception em) {
                                            em.printStackTrace();
                                        }
                                        return jsonString;
                                    }else{
                                        //认证借记卡快捷支付
                                        quickpayCardRed = new QuickpayCardRecord();
                                        quickpayCardRed.setCardType("01");
                                        quickpayCardRed.setMobile(shopOrderQuickPayRequestDTO.getMobile());
                                        quickpayCardRed.setCerType(shopOrderQuickPayRequestDTO.getCerType());
                                        quickpayCardRed.setCerNumber(shopOrderQuickPayRequestDTO.getCerNumber());
                                        quickpayCardRed.setCardByName(shopOrderQuickPayRequestDTO.getCardByName());
                                        quickpayCardRed.setCardNumber(shopOrderQuickPayRequestDTO.getCardNo());
                                        quickpayCardRed.setBankName(payCmmtufit.getBnkName());
                                    }
                                }else{
                                    //贷记卡  查看手机号 CVV 卡有效期
                                    if(StringUtils.isBlank(shopOrderQuickPayRequestDTO.getCerType()) ||
                                            StringUtils.isBlank(shopOrderQuickPayRequestDTO.getCerNumber())
                                            ||StringUtils.isBlank(shopOrderQuickPayRequestDTO.getCardByName())||
                                            StringUtils.isBlank(shopOrderQuickPayRequestDTO.getMobile())||
                                            StringUtils.isBlank(shopOrderQuickPayRequestDTO.getCvv())
                                            ||StringUtils.isBlank(shopOrderQuickPayRequestDTO.getExpireDate())){
                                        //若查到的是空值，直接返回错误
                                        responseDTO.setRetCode(1);
                                        responseDTO.setRetMessage("贷记卡快捷认证参数不完整");
                                        logger.info("贷记卡快捷认证参数不完整：" +shopOrderQuickPayRequestDTO.toString());
                                        try {
                                            jsonString = createJsonString(responseDTO);
                                        } catch (Exception em) {
                                            em.printStackTrace();
                                        }
                                        return jsonString;
                                    }else{
                                        //认证贷记卡快捷支付
                                        quickpayCardRed = new QuickpayCardRecord();
                                        quickpayCardRed.setCardType("00");
                                        quickpayCardRed.setMobile(shopOrderQuickPayRequestDTO.getMobile());
                                        quickpayCardRed.setCvv(shopOrderQuickPayRequestDTO.getCvv());
                                        quickpayCardRed.setExpireDate(shopOrderQuickPayRequestDTO.getExpireDate());
                                        quickpayCardRed.setCardNumber(shopOrderQuickPayRequestDTO.getCardNo());
                                        quickpayCardRed.setBankName(payCmmtufit.getBnkName());
                                        quickpayCardRed.setCardByName(shopOrderQuickPayRequestDTO.getCardByName());
                                        quickpayCardRed.setCerType(shopOrderQuickPayRequestDTO.getCerType());
                                        quickpayCardRed.setCerNumber(shopOrderQuickPayRequestDTO.getCerNumber());

                                    }
                                }
                                quickpayCardRed.setMercId(sessionInfo.getMercId());
                                quickpayCardRed.setCreateTime(UtilDate.getTXDateTime());
                            }

                            //校验支付参数与预消费的参数是否一致
                            //查询预消费的记录
                            QuickpayPreRecord quickpayPreRecord = quickpayPreRecordDao.searchById(shopOrderQuickPayRequestDTO.getOrderId());
                            if(quickpayPreRecord != null){
                                 //比较传入参数
                                Boolean compareReuslt= comparePreRecord(quickpayCardRed,quickpayPreRecord);
                                if(!compareReuslt){
                                    responseDTO.setRetCode(1);
                                    responseDTO.setRetMessage("支付信息不正确，请检查后重试");
                                    jsonString = createJsonString(responseDTO);
                                    logger.info("支付信息与预交易信息不否："+shopOrderQuickPayRequestDTO.toString());
                                    return jsonString;
                                }
                            }else{
                                responseDTO.setRetCode(1);
                                responseDTO.setRetMessage("请先发送验证码再进行确认支付操作");
                                jsonString = createJsonString(responseDTO);
                                logger.info("没有查到快捷支付预下单记录："+shopOrderQuickPayRequestDTO.toString());
                                return jsonString;
                            }


                            //查询订单
                            PmsAppTransInfo pmsAppTransInfo = pmsAppTransInfoDao.searchOrderInfo(shopOrderQuickPayRequestDTO.getOrderId());
                            if(pmsAppTransInfo != null){
                                 //校验订单状态
                                if(!pmsAppTransInfo.getStatus().equals(OrderStatusEnum.waitingClientPay.getStatus())){
                                    //当前订单不是可支付状态
                                    responseDTO.setRetCode(1);
                                    responseDTO.setRetMessage("当前订单不是可支付的状态，请检查是否获取了验证码或联系客服");
                                    responseDTO.setOrderStatus("1");
                                    jsonString = createJsonString(responseDTO);
                                    logger.info("当前订单不是可支付的状态："+shopOrderQuickPayRequestDTO.toString());
                                    return jsonString;
                                }
                                //验证支付方式是否开启
                                ResultInfo payCheckResult = iPublicTradeVerifyService.totalVerify((int)(Double.parseDouble(pmsAppTransInfo.getFactamount())), TradeTypeEnum.shop, PaymentCodeEnum.moBaoQuickPay, oAgentNo, sessionInfo.getMercId());
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
                                //判断当前银行卡是否是订单的银行卡
                                if(pmsAppTransInfo.getBankno().equals(shopOrderQuickPayRequestDTO.getCardNo())){
                                    //查询流水表
                                    PospTransInfo pospTransInfo = pospTransInfoDAO.searchByOrderId(pmsAppTransInfo.getOrderid());
                                    if(pospTransInfo != null){
                                        //先在本地校验验证码是否正确，不正确则直接返回
                                        PmsMessage pmsMessage = new PmsMessage();
                                        pmsMessage.setOrderId(pmsAppTransInfo.getOrderid());
                                        List<PmsMessage> pmsMessageList = pmsMessageDao.selectList(pmsMessage);
                                        if(pmsMessageList != null && pmsMessageList.size() > 0){
                                            pmsMessage = pmsMessageList.get(0);
                                            Integer payTimes = pospTransInfo.getPayTimes();
                                            if(payTimes != null && payTimes >= 5){
                                              //获取验证码次数超限，将当亲订单制为无效，返回失败
                                               pmsAppTransInfo.setStatus(OrderStatusEnum.payFail.getStatus());
                                               int updateOrderResult = pmsAppTransInfoDao.update(pmsAppTransInfo);
                                                if(updateOrderResult != 1){
                                                   logger.info("修改订单为失败时出错，order:"+pmsAppTransInfo.getOrderid()+",result="+updateOrderResult);
                                                }
                                                //没有发送短信，返回错误
                                                responseDTO.setRetCode(0);
                                                responseDTO.setRetMessage("交易次数超限，请重新下单");
                                                responseDTO.setOrderMessage("交易次数超限，请重新下单");
                                                responseDTO.setOrderStatus("1");
                                                jsonString = createJsonString(responseDTO);
                                                logger.info("交易次数超限："+shopOrderQuickPayRequestDTO.toString());
                                                return jsonString;
                                            }
                                            if(payTimes != null){
                                                payTimes++;
                                            }else{
                                                payTimes = 1;
                                            }
                                            pospTransInfo.setPayTimes(payTimes);
                                            //更新流水表
                                           pospTransInfoDAO.update(pospTransInfo);
                                            if(!pmsMessage.getContext().equals(shopOrderQuickPayRequestDTO.getMessage())){
                                                responseDTO.setRetCode(1);
                                                responseDTO.setRetMessage("验证码不正确");
                                                responseDTO.setOrderStatus("1");
                                                jsonString = createJsonString(responseDTO);
                                                logger.info("验证码出错："+shopOrderQuickPayRequestDTO.toString());
                                                return jsonString;
                                            }
                                        }else{
                                            //没有发送短信，返回错误
                                            responseDTO.setRetCode(1);
                                            responseDTO.setRetMessage("请先发送短信");
                                            responseDTO.setOrderStatus("1");
                                            jsonString = createJsonString(responseDTO);
                                            logger.info("没有查到短信记录："+shopOrderQuickPayRequestDTO.toString());
                                            return jsonString;
                                        }
                                        //调用摩宝接口支付认证
                                        MobaoPayValidateResponseDto mobaoPayValidateResponseDto =  mobaoPayHandel.validatePay(pmsAppTransInfo,pospTransInfo, shopOrderQuickPayRequestDTO.getMessage(),quickpayCardRed);
                                        if(mobaoPayValidateResponseDto != null&&mobaoPayValidateResponseDto.getStatus()!= null){
                                            if(mobaoPayValidateResponseDto.getStatus().equals("00") ){
                                                if(mobaoPayValidateResponseDto.getRefCode().equals("00")){
                                                //成功  更新订单表和流水表
                                                //更新订单表
                                                pmsAppTransInfo.setStatus(OrderStatusEnum.paySuccess.getStatus());
                                                pmsAppTransInfo.setThirdPartResultCode(mobaoPayValidateResponseDto.getRefCode());
                                                pmsAppTransInfo.setFinishtime(UtilDate.getDateFormatter());
                                                int updateAppTrans = pmsAppTransInfoDao.update(pmsAppTransInfo);
                                                if(updateAppTrans == 1){
                                                  //修改余额
                                                int updateResult = updateMerchantBanlance(pmsAppTransInfo);
                                                 if(updateResult != 1){
                                                     logger.info("更新账户余额失败："+shopOrderQuickPayRequestDTO.toString()+",result="+updateResult);
                                                 }
                                                 //更新流水表
                                                    pospTransInfo.setResponsecode("00");
                                                    int updateTrans = pospTransInfoDAO.updateByOrderId(pospTransInfo);
                                                    if(updateTrans != 1){
                                                        logger.info("更新流水表出错："+shopOrderQuickPayRequestDTO.toString()+","+updateTrans);
                                                    }
                                                }

                                                    //判断是否需要插入快捷支付卡记录表
                                                    if(needInsertQuichPayRecordFlag == 1){
                                                        //插入快捷记录
                                                        int quickpayRecordResult  = quickpayRecordDao.insert(quickpayCardRed);
                                                        if(quickpayRecordResult==0){
                                                            logger.info("插入快捷支付出错：" +shopOrderQuickPayRequestDTO.toString());
                                                        }
                                                    }else{
                                                        //判断是否需要更新用户手机号
                                                        if(mobileChangeFlag == 1){
                                                            int quickpayRecordUpdateResult  =  quickpayRecordDao.update(quickpayCardRed);
                                                            if(quickpayRecordUpdateResult==0){
                                                                logger.info("更新快捷支付手机号出错：" +shopOrderQuickPayRequestDTO.toString());
                                                            }
                                                        }
                                                    }
                                                //组装返回对象
                                                responseDTO.setRetCode(0);
                                                responseDTO.setRetMessage("支付成功");
                                                responseDTO.setOrderStatus("0");
                                                jsonString = createJsonString(responseDTO);
                                                logger.info("支付成功："+shopOrderQuickPayRequestDTO.toString());
                                                return jsonString;
                                                }else if(mobaoPayValidateResponseDto.getRefCode().equals("03")){
                                                   //交易处理中
                                                    //更新订单表
                                                    pmsAppTransInfo.setStatus(OrderStatusEnum.plantPayingNow.getStatus());
                                                    pmsAppTransInfo.setThirdPartResultCode(mobaoPayValidateResponseDto.getRefCode());
                                                    int updateAppTrans = pmsAppTransInfoDao.update(pmsAppTransInfo);
                                                    if(updateAppTrans == 1){
                                                        //更新流水表
                                                        pospTransInfo.setResponsecode("00");
                                                        int updateTrans = pospTransInfoDAO.updateByOrderId(pospTransInfo);
                                                        if(updateTrans != 1){
                                                            logger.info("更新流水表出错："+shopOrderQuickPayRequestDTO.toString()+","+updateTrans);
                                                        }
                                                    }
                                                    //组装返回对象
                                                    responseDTO.setRetCode(0);
                                                    responseDTO.setRetMessage("支付正在进行中，请稍后再账单中查询");
                                                    responseDTO.setOrderMessage("支付正在进行中，请稍后再账单中查询");
                                                    responseDTO.setOrderStatus("1");
                                                    jsonString = createJsonString(responseDTO);
                                                    logger.info("校验出错："+shopOrderQuickPayRequestDTO.toString());
                                                    return jsonString;
                                                }else{
                                                    //交易处理中
                                                    //更新订单表
                                                    pmsAppTransInfo.setThirdPartResultCode(mobaoPayValidateResponseDto.getRefCode());
                                                    int updateAppTrans = pmsAppTransInfoDao.update(pmsAppTransInfo);
                                                    responseDTO.setRetCode(0);
                                                    responseDTO.setRetMessage(mobaoPayValidateResponseDto.getRefMsg());
                                                    responseDTO.setOrderStatus("1");
                                                    responseDTO.setOrderMessage(mobaoPayValidateResponseDto.getRefMsg());
                                                    jsonString = createJsonString(responseDTO);
                                                    logger.info("校验出错："+shopOrderQuickPayRequestDTO.toString());
                                                    return jsonString;
                                                }

                                            }else{
                                                //失败  将信息鸳鸯返回到  更新订单表
                                                //更新订单表
                                                pmsAppTransInfo.setThirdPartResultCode(mobaoPayValidateResponseDto.getRefCode());
                                                int updateAppTrans = pmsAppTransInfoDao.update(pmsAppTransInfo);
                                                if(updateAppTrans != 1){
                                                    logger.info("更新订单表出错："+shopOrderQuickPayRequestDTO.toString()+","+updateAppTrans);
                                                }
                                                //组装返回对象
                                                responseDTO.setRetCode(0);
                                                responseDTO.setRetMessage(mobaoPayValidateResponseDto.getRefMsg());
                                                responseDTO.setOrderStatus("1");
                                                responseDTO.setOrderMessage(mobaoPayValidateResponseDto.getRefMsg());
                                                responseDTO.setOrderMessage(mobaoPayValidateResponseDto.getRefMsg());
                                                jsonString = createJsonString(responseDTO);
                                                logger.info("支付失败：request:"+shopOrderQuickPayRequestDTO.toString()+",response:"+mobaoPayValidateResponseDto.toString());
                                                return jsonString;
                                            }

                                        }else{
                                            responseDTO.setRetCode(0);
                                            responseDTO.setRetMessage("参数校验出错，请重新下单或联系客服");
                                            jsonString = createJsonString(responseDTO);
                                            logger.info("校验出错："+shopOrderQuickPayRequestDTO.toString());
                                            return jsonString;
                                        }


                                    }else{
                                        responseDTO.setRetCode(1);
                                        responseDTO.setRetMessage("数据存储异常");
                                        jsonString = createJsonString(responseDTO);
                                        logger.info("查询流水表为空："+shopOrderQuickPayRequestDTO.toString());
                                        return jsonString;
                                    }


                                }else{
                                    responseDTO.setRetCode(1);
                                    responseDTO.setRetMessage("参数出错，请检查卡片信息");
                                    jsonString = createJsonString(responseDTO);
                                    logger.info("快捷支付预请求卡片信息与支付卡片信息不一致："+shopOrderQuickPayRequestDTO.toString());
                                    return jsonString;
                                }

                            }else{
                                responseDTO.setRetCode(1);
                                responseDTO.setRetMessage("当前订单不存在，请检查订单信息或联系客服");
                                jsonString = createJsonString(responseDTO);
                                logger.info("当前订单不存在："+shopOrderQuickPayRequestDTO.toString());
                                return jsonString;
                            }

                        }else{
                            responseDTO.setRetCode(1);
                            responseDTO.setRetMessage("参数错误");
                            jsonString = createJsonString(responseDTO);
                            logger.info("请求参数错误："+shopOrderQuickPayRequestDTO.toString());
                            return jsonString;
                        }
                    }
                }
            }
        }else{
            //回话失效
            responseDTO.setRetCode(13);
            responseDTO.setRetMessage("回话失效，请重新登陆");
            jsonString = createJsonString(responseDTO);
            logger.info("回话失效，请重新登陆");
            return jsonString;
        }
        //回话失效
        responseDTO.setRetCode(1);
        responseDTO.setRetMessage("系统错误，请重试");
        jsonString = createJsonString(responseDTO);
        logger.info("系统错误，请重试");
        return jsonString;
    }

    /**
     * 订单预下单，短信重发
     * @param session
     * @param shopPayRequest
     * @return
     * @throws Exception
     */
    @Override
    public synchronized String shopOrderPrePayReSendMsg(HttpSession session, String shopPayRequest) throws Exception {

        SessionInfo sessionInfo = (SessionInfo) session.getAttribute(SessionInfo.SESSIONINFO);
        ShopOrderPreReSendMsgResponseDTO  responseDTO = new ShopOrderPreReSendMsgResponseDTO();
        Object obj = parseJsonString(shopPayRequest, ShopOrderPreReSendMsgRequestDTO.class);
        String oAgentNo = "";
        String jsonString = null;
        //校验用户是否登录
        if (null != sessionInfo) {
            oAgentNo = sessionInfo.getoAgentNo();

            if (StringUtils.isBlank(oAgentNo)) {
                //如果没有欧单编号，直接返回错误
                responseDTO.setRetCode(1);
                responseDTO.setRetMessage("参数错误");
                jsonString = createJsonString(responseDTO);
                logger.info("参数错误,没有欧单编号");
                return jsonString;
            }

            //判断当前商户是否有使用商城的权限  只有正式商户才能使用商城
            PmsMerchantInfo merchantInfo = new PmsMerchantInfo();
            merchantInfo.setMobilephone(sessionInfo.getMobilephone());
            merchantInfo.setoAgentNo(oAgentNo);
            merchantInfo.setCustomertype("3");
            List<PmsMerchantInfo> list = pmsMerchantInfoDao.searchList(merchantInfo);
            if (list != null && list.size() > 0) {

                PmsMerchantInfo pmsMerchantInfo = list.get(0);
                if (pmsMerchantInfo.getMercSts().equals("60")) {

                    if (!obj.equals(DATAPARSINGMESSAGE)) {

                        ShopOrderPreReSendMsgRequestDTO shopOrderPreReSendMsgRequestDTO = (ShopOrderPreReSendMsgRequestDTO) obj;

                        //校验参数
                        if(shopOrderPreReSendMsgRequestDTO != null && StringUtils.isNotBlank(shopOrderPreReSendMsgRequestDTO.getOrderId()) && StringUtils.isNotBlank(shopOrderPreReSendMsgRequestDTO.getCardNo())){
                            //查询订单
                            PmsAppTransInfo pmsAppTransInfo = pmsAppTransInfoDao.searchOrderInfo(shopOrderPreReSendMsgRequestDTO.getOrderId());
                            if(pmsAppTransInfo != null){
                                 //校验订单的状态
                                if(!pmsAppTransInfo.getStatus().equals(OrderStatusEnum.waitingClientPay.getStatus())){
                                    responseDTO.setRetCode(1);
                                    responseDTO.setRetMessage("订单不是可支付的状态，请联系客服");
                                    jsonString = createJsonString(responseDTO);
                                    logger.info("订单不是可支付的状态，请联系客服："+shopOrderPreReSendMsgRequestDTO.toString());
                                    return jsonString;
                                }
                                //验证支付方式是否开启
                                ResultInfo payCheckResult = iPublicTradeVerifyService.totalVerify((int)(Double.parseDouble(pmsAppTransInfo.getFactamount())), TradeTypeEnum.shop, PaymentCodeEnum.moBaoQuickPay, oAgentNo, sessionInfo.getMercId());
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
                                //判断当前银行卡是否是订单的银行卡
                                if(pmsAppTransInfo.getBankno().equals(shopOrderPreReSendMsgRequestDTO.getCardNo())){
                                    //查询当前订单的短信信息，调用接口重新发送短信
                                       PmsMessage pmsMessage = new PmsMessage();
                                       pmsMessage.setOrderId(pmsAppTransInfo.getOrderid());
                                       List<PmsMessage> messageList = pmsMessageDao.searchList(pmsMessage);
                                       if(messageList != null && messageList.size() > 0 ){
                                           //获取其中一条记录
                                           PmsMessage message = messageList.get(0);
                                           //校验短信正确性
                                           if(StringUtils.isNotBlank(message.getOrderId()) && StringUtils.isNotBlank(message.getContext()) && message.getOrderId().equals(pmsAppTransInfo.getOrderid())){
                                               //调用短信接口重新发送短信
                                               QuickPayMessage quickPayMessage = new QuickPayMessage();
                                               quickPayMessage.setOrderId(message.getOrderId());
                                               quickPayMessage.setMessage(message.getContext());
                                               String snedResult = pmsMessageService.getMessageAuthenticationCode(message.getPhoneNumber(),message.getMsgType(),message.getoAgentNo(),quickPayMessage);
                                               if(snedResult.equals(SUCCESSMESSAGE)){
                                                      //成功
                                                   //重发成功 返回成功标识
                                                   //调用成功 组装返回对象
                                                   responseDTO.setRetCode(0);
                                                   responseDTO.setOrderStatus("0");
                                                   responseDTO.setRetMessage("调用成功");
                                                   jsonString = createJsonString(responseDTO);
                                                   logger.info("调用成功："+shopOrderPreReSendMsgRequestDTO.toString());
                                                   return jsonString;
                                               }else{
                                                   //短信接口调用失败
                                                   responseDTO.setRetCode(1);
                                                   responseDTO.setOrderStatus("1");
                                                   responseDTO.setRetMessage("系统异常，请重新下单或联系客服");
                                                   jsonString = createJsonString(responseDTO);
                                                   logger.info("系统异常："+shopOrderPreReSendMsgRequestDTO.toString());
                                                   return jsonString;
                                               }
                                           }else{
                                               responseDTO.setRetCode(1);
                                               responseDTO.setOrderStatus("1");
                                               responseDTO.setRetMessage("调用错误，请重新下单");
                                               jsonString = createJsonString(responseDTO);
                                               logger.info("调用错误，请重新下单："+shopOrderPreReSendMsgRequestDTO.toString());
                                               return jsonString;
                                           }
                                       }else{
                                           responseDTO.setRetCode(1);
                                           responseDTO.setOrderStatus("1");
                                           responseDTO.setRetMessage("调用错误，请重新下单");
                                           jsonString = createJsonString(responseDTO);
                                           logger.info("调用错误，请重新下单："+shopOrderPreReSendMsgRequestDTO.toString());
                                           return jsonString;
                                       }

                                }else{
                                    responseDTO.setRetCode(1);
                                    responseDTO.setOrderStatus("1");
                                    responseDTO.setRetMessage("参数出错，请检查卡片信息");
                                    jsonString = createJsonString(responseDTO);
                                    logger.info("快捷支付预请求卡片信息与支付卡片信息不一致："+shopOrderPreReSendMsgRequestDTO.toString());
                                    return jsonString;
                                }

                            }else{
                                responseDTO.setRetCode(1);
                                responseDTO.setOrderStatus("1");
                                responseDTO.setRetMessage("当前订单不存在，请检查订单信息或联系客服");
                                jsonString = createJsonString(responseDTO);
                                logger.info("当前订单不存在："+shopOrderPreReSendMsgRequestDTO.toString());
                                return jsonString;
                            }

                        }else{
                            responseDTO.setRetCode(1);
                            responseDTO.setOrderStatus("1");
                            responseDTO.setRetMessage("参数错误");
                            jsonString = createJsonString(responseDTO);
                            logger.info("请求参数错误："+shopOrderPreReSendMsgRequestDTO.toString());
                            return jsonString;
                        }
                    }
                }
            }
        }else{
            //回话失效
            responseDTO.setRetCode(13);
            responseDTO.setRetMessage("回话失效，请重新登陆");
            jsonString = createJsonString(responseDTO);
            logger.info("回话失效，请重新登陆");
            return jsonString;
        }
        //回话失效
        responseDTO.setRetCode(1);
        responseDTO.setRetMessage("系统错误，请重试");
        jsonString = createJsonString(responseDTO);
        logger.info("系统错误，请重试");
        return jsonString;
    }


    /**
     * 从传入的商品列表中获取指定的商品信息
     * @param goodsList
     * @param id
     * @return
     */
    private PmsGoods getGoodsInfoById(List<PmsGoods> goodsList,String id){
        PmsGoods result = null;
        if(goodsList != null && goodsList.size() > 0){
          for(PmsGoods pmsGoods : goodsList){
                if(pmsGoods.getGoodsId().equals(id)){
                    result = pmsGoods;
                    break;
                }
          }
        }
        return result;
    }

    /**
     * 更新账单信息
     * @param pmsAppTransInfo
     * @return
     */
    public synchronized int updateMerchantBanlance(PmsAppTransInfo pmsAppTransInfo){
         int result = 0;
        try {
            result = merchantCollectMoneyService.updateMerchantBalance(pmsAppTransInfo);
        } catch (Exception e) {
            logger.info("修改余额的时候出错("+pmsAppTransInfo.getOrderid()+")："+e.getMessage());
        }
        return result;
    }


    private Boolean checkMessageLimit(String orderId){
       Boolean result = false;
        if(StringUtils.isNotBlank(orderId)){
           //查询当前订单号已经发送的短信记录
            PmsMessage pmsMessage = new PmsMessage();
            pmsMessage.setOrderId(orderId);
            try {
                List<PmsMessage> messageList = pmsMessageDao.searchList(pmsMessage);
                if(messageList == null || messageList.size() < 5){
                    result = true;
                }
            } catch (Exception e) {
               logger.info("根据订单号查找短信出错"+e.getMessage());
            }
        }
        return result;
    }

    /**
     * 产
     * @param quickpayCardRecord
     * @param quickpayPreRecord
     * @return
     */
    private  Boolean comparePreRecord(QuickpayCardRecord quickpayCardRecord,QuickpayPreRecord quickpayPreRecord){
        Boolean result = true;
        if(quickpayCardRecord.getCardNumber() != null && !quickpayCardRecord.getCardNumber().equals(quickpayPreRecord.getCardNumber())){
            result = false;
        }
        if(quickpayCardRecord.getBankName() != null && !quickpayCardRecord.getBankName().equals(quickpayPreRecord.getBankName())){
            result = false;
        }
        if(quickpayCardRecord.getCardType() != null && !quickpayCardRecord.getCardType().equals(quickpayPreRecord.getCardType())){
            result = false;
        }
        if(quickpayCardRecord.getCerType() != null && !quickpayCardRecord.getCerType().equals(quickpayPreRecord.getCerType())){
            result = false;
        }
        if(quickpayCardRecord.getCerNumber() != null && !quickpayCardRecord.getCerNumber().equals(quickpayPreRecord.getCerNumber())){
            result = false;
        }
        if(quickpayCardRecord.getMobile() != null && !quickpayCardRecord.getMobile().equals(quickpayPreRecord.getMobile())){
            result = false;
        }
        if(quickpayCardRecord.getCardByName() != null && !quickpayCardRecord.getCardByName().equals(quickpayPreRecord.getCardByName())){
            result = false;
        }
        if(quickpayCardRecord.getCvv() != null && !quickpayCardRecord.getCvv().equals(quickpayPreRecord.getCvv())){
            result = false;
        }
        if(quickpayCardRecord.getExpireDate() != null && !quickpayCardRecord.getExpireDate().equals(quickpayPreRecord.getExpireDate())){
            result = false;
        }
        return result;
    }
}
