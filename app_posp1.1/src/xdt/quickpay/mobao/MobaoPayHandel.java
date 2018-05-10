package xdt.quickpay.mobao;

import com.alibaba.fastjson.JSONObject;
import com.kspay.AESUtil;
import com.kspay.MD5Util;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Component;
import xdt.dao.IPayCmmtufitDao;
import xdt.dao.IQuickpayRecordDao;
import xdt.model.*;
import xdt.service.IPmsMessageService;
import xdt.service.impl.BaseServiceImpl;
import xdt.servlet.AppPospContext;
import xdt.util.EncodeUtil;
import xdt.util.OrderStatusEnum;
import xdt.util.PaymentCodeEnum;
import xdt.util.UtilDate;

import javax.annotation.Resource;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 处理摩宝渠道的主要业务
 * User: Jeff
 * Date: 16-3-8
 * Time: 下午5:23
 * To change this template use File | Settings | File Templates.
 */
@Component
public class MobaoPayHandel extends MobaoPayBase {
    @Resource
    IQuickpayRecordDao quickpayRecordDao;
    @Resource
    IPayCmmtufitDao iPayCmmtufitDao;
    @Resource
    IPmsMessageService pmsMessageService;
    private Logger logger = Logger.getLogger(MobaoPayHandel.class);

    /**
     * 摩宝预消费接口
     * 失败则返回失败
     * 如果成功，发送短信验证码
     *
     * @param pmsAppTransInfo
     * @return
     */
    public synchronized MobaoPrePayResponseDto prePay(PmsAppTransInfo pmsAppTransInfo,PospTransInfo pospTransInfo, QuickpayCardRecord quickpayCardRecord, ViewKyChannelInfo channelInfo) {
        logger.info("进入摩宝银行卡预支付模块，mercid=" + pmsAppTransInfo.getMercid() + "," + quickpayCardRecord.toString());
        //获取通道信息
        MobaoPrePayResponseDto mobaoPrePayResponseDto = null;
        /**
         * 校验银行卡信息
         */
        if (checkPreBankCard(quickpayCardRecord) == null) {
            logger.info("银行卡信息校验失败");
            mobaoPrePayResponseDto = new MobaoPrePayResponseDto();
            mobaoPrePayResponseDto.setRefCode("02");
            mobaoPrePayResponseDto.setRefMsg("银行卡信息校验失败");
            return mobaoPrePayResponseDto;
        } else {
            logger.info("银行卡信息校验成功");
        }
        /**
         *校验订单信息
         */
        if (!checkPreAppTrans(pmsAppTransInfo)) {
            logger.info("订单信息校验失败");
            mobaoPrePayResponseDto = new MobaoPrePayResponseDto();
            mobaoPrePayResponseDto.setRefCode("02");
            mobaoPrePayResponseDto.setRefMsg("订单信息校验失败");
            return mobaoPrePayResponseDto;
        } else {
            logger.info("订单信息校验成功");
        }
        /**
         *组装参数
         */
        //获取通道信息
        String assembleredData = assemberPreSaleParamData(pmsAppTransInfo,pospTransInfo, quickpayCardRecord, channelInfo);
        String response = null;
        if (StringUtils.isNotBlank(assembleredData) && channelInfo != null) {
            response = requestBody(channelInfo.getChannelNO(), assembleredData);
            //解析返回的数据
            mobaoPrePayResponseDto = JSONObject.parseObject(response, MobaoPrePayResponseDto.class);
            //判断当前预消费是否成功，如果成功则调用短信接口发送短信
            if (mobaoPrePayResponseDto != null) {
                if (mobaoPrePayResponseDto.getRefCode().equals("01")) {
                    if (StringUtils.isNotBlank(mobaoPrePayResponseDto.getYzm())) {
                        try {
                            QuickPayMessage quickPayMessage = new QuickPayMessage();
                            quickPayMessage.setOrderId(pmsAppTransInfo.getOrderid());
                            quickPayMessage.setMessage(mobaoPrePayResponseDto.getYzm());
                            String msgResult = pmsMessageService.getMessageAuthenticationCode(quickpayCardRecord.getMobile(), PmsMessage.QUICKPAYPRE, pmsAppTransInfo.getoAgentNo(),quickPayMessage);
                            if (StringUtils.isNotBlank(msgResult) && msgResult.equals(BaseServiceImpl.SUCCESSMESSAGE)) {
                               //发送短信成功
                                return  mobaoPrePayResponseDto;
                            } else {
                                logger.info("发送短信失败");
                                mobaoPrePayResponseDto = new MobaoPrePayResponseDto();
                                mobaoPrePayResponseDto.setRefCode("02");
                                mobaoPrePayResponseDto.setRefMsg("发送短信失败");
                                return mobaoPrePayResponseDto;
                            }
                        } catch (Exception e) {
                            logger.info("发送短信报错," + e.getMessage());
                            mobaoPrePayResponseDto = new MobaoPrePayResponseDto();
                            mobaoPrePayResponseDto.setRefCode("02");
                            mobaoPrePayResponseDto.setRefMsg("发送短信失败");
                            return mobaoPrePayResponseDto;
                        }
                    } else {
                        logger.info("返回的信息中没有验证码," + mobaoPrePayResponseDto.toString());
                        mobaoPrePayResponseDto = new MobaoPrePayResponseDto();
                        mobaoPrePayResponseDto.setRefCode("02");
                        mobaoPrePayResponseDto.setRefMsg("发送验证码失败");
                        return mobaoPrePayResponseDto;
                    }
                }else{
                    logger.info("调用接口返回失败" + mobaoPrePayResponseDto.toString());
                    return mobaoPrePayResponseDto;
                }
            }
        }

        return mobaoPrePayResponseDto;
    }

    /**
     * 校验银行卡信息
     *
     * @param quickpayCardRecord
     * @return
     */
    public PayCmmtufit checkPreBankCard(QuickpayCardRecord quickpayCardRecord) {
        PayCmmtufit result = null;

        if (quickpayCardRecord != null && StringUtils.isNotBlank(quickpayCardRecord.getCardNumber())) {
            try {
                PayCmmtufit payCmmtufit = iPayCmmtufitDao.selectByCardNum(quickpayCardRecord.getCardNumber());
                if (payCmmtufit != null) {
                    if (!payCmmtufit.getCrdFlg().equals(quickpayCardRecord.getCardType())) {
                        result = payCmmtufit;
                        logger.info("卡类型校验成功");
                    }
                } else {
                    result = null;
                    logger.info("找不到卡宾，" + quickpayCardRecord.getCardNumber());
                }
            } catch (Exception e) {
                logger.info(e.getMessage());
            }
        }

        return result;
    }

    /**
     * 校验订单信息
     *
     * @return
     */
    private Boolean checkPreAppTrans(PmsAppTransInfo pmsAppTransInfo) {
        Boolean result = false;

        if (pmsAppTransInfo != null && StringUtils.isNotBlank(pmsAppTransInfo.getOrderid())) {
            result = true;
        }

        return result;
    }

    /**
     * 校验商品信息
     *
     * @param pmsGoodsOrder
     * @return
     */
    private Boolean checkPreGoods(PmsGoodsOrder pmsGoodsOrder) {
        Boolean result = false;

        if (pmsGoodsOrder != null) {
            result = true;
        }

        return result;
    }

    /**
     * 组装向摩宝快捷预消费的请求参数
     *
     * @param pmsAppTransInfo
     * @param quickpayCardRecord
     * @return
     */
    private String assemberPreSaleParamData(PmsAppTransInfo pmsAppTransInfo,PospTransInfo pospTransInfo, QuickpayCardRecord quickpayCardRecord, ViewKyChannelInfo channelInfo) {
        String result = null;

        if (channelInfo != null) {
            //商户号码
            String merId = channelInfo.getChannelNO();
            //商户号私钥
            String merKey = channelInfo.getChannelPwd();

            Map<String, String> transmap = new LinkedHashMap<String, String>();
            transmap.put("versionId", "001");
            transmap.put("businessType", "1401");
            transmap.put("insCode", "");
            transmap.put("merId", merId);
            transmap.put("orderId",pospTransInfo.getTransOrderId());
            transmap.put("transDate", UtilDate.getOrderNum());
            //将金额单位转化为元
            String transAmountStr = pmsAppTransInfo.getFactamount();
            BigDecimal transAmountBigFen = new BigDecimal(transAmountStr);
            BigDecimal transAmountBigYuan = transAmountBigFen.movePointLeft(2);
            transmap.put("transAmount", transAmountBigYuan.stripTrailingZeros().toPlainString());

            try {

                   transmap.put("cardByName", MD5Util.encode(quickpayCardRecord.getCardByName().getBytes("UTF-8")));  //此处的MD5util为Base64加密

            } catch (UnsupportedEncodingException e) {
                logger.info(e.getMessage());
            }
            transmap.put("cardByNo", quickpayCardRecord.getCardNumber());
            if (quickpayCardRecord.getCardType().equals("00")) {
                //信用卡
                transmap.put("cardType", "00");
                transmap.put("expireDate", quickpayCardRecord.getExpireDate());
                transmap.put("CVV", quickpayCardRecord.getCvv());
            } else {
                //借记卡 或 准借贷卡
                transmap.put("cardType", "01");
                transmap.put("CVV", "");
            }
            transmap.put("bankCode", "");
            transmap.put("openBankName", "");



                transmap.put("cerType", "01");
                transmap.put("cerNumber", quickpayCardRecord.getCerNumber());

            transmap.put("mobile", quickpayCardRecord.getMobile());
            transmap.put("isAcceptYzm", "01");
            transmap.put("pageNotifyUrl", "");
            transmap.put("backNotifyUrl", "");
            transmap.put("orderDesc", "");
            transmap.put("dev", "");
            transmap.put("fee", "");
            //需要加密的字符串
            String signstr = EncodeUtil.getUrlStr(transmap);
            System.out.println("需要签名的明文" + signstr);
            String signtrue = MD5Util.MD5Encode(signstr + merKey);
            transmap.put("signType", "MD5");
            transmap.put("signData", signtrue);
            //AES加密
            String transData = EncodeUtil.getUrlStr(transmap);
            result = AESUtil.encrypt(transData, merKey);
        }
        return result;
    }

    /**
     * 摩宝验证支付接口
     *
     * @return
     */
    public synchronized MobaoPayValidateResponseDto validatePay(PmsAppTransInfo pmsAppTransInfo,PospTransInfo pospTransInfo,String message,QuickpayCardRecord quickpayCardRecord) {
        MobaoPayValidateResponseDto mobaoPayValidateResponseDto = null;
        //校验支付订单信息
        if (!checkOrder(pmsAppTransInfo)) {
            logger.info("订单校验失败");
            mobaoPayValidateResponseDto = new MobaoPayValidateResponseDto();
            mobaoPayValidateResponseDto.setRefCode("02");
            mobaoPayValidateResponseDto.setRefMsg("订单校验失败");
            return mobaoPayValidateResponseDto;
        }
        //校验验证码(只判空)  这里验证码不做校验，由上游控制风险 ，来判断当前交易是否需要风控
        if (StringUtils.isBlank(message)) {
            logger.info("订单校验失败");
            mobaoPayValidateResponseDto = new MobaoPayValidateResponseDto();
            mobaoPayValidateResponseDto.setRefCode("02");
            mobaoPayValidateResponseDto.setRefMsg("订单校验失败");
            return mobaoPayValidateResponseDto;
        }
        //校验银行卡号
        //查询本地记录信息
        if (StringUtils.isNotBlank(pmsAppTransInfo.getBankno())) {
        } else {
            logger.info("订单中银行卡号为空");
            mobaoPayValidateResponseDto = new MobaoPayValidateResponseDto();
            mobaoPayValidateResponseDto.setRefCode("02");
            mobaoPayValidateResponseDto.setRefMsg("订单中银行卡号为空");
            return mobaoPayValidateResponseDto;
        }

        //组装上送数据
        //获取通道信息
        String response;
        ViewKyChannelInfo channelInfo = AppPospContext.context.get(BaseServiceImpl.MOBAOCHANNELNUM + BaseServiceImpl.MOBAOPAY);
        String assemberParamData = assemberValidatePayParamData(pmsAppTransInfo,pospTransInfo, quickpayCardRecord, channelInfo, message);
        if (StringUtils.isNotBlank(assemberParamData)) {
            response = requestBody(channelInfo.getChannelNO(), assemberParamData);
            //解析返回的数据
            mobaoPayValidateResponseDto = JSONObject.parseObject(response, MobaoPayValidateResponseDto.class);
        } else {
            logger.info("组装验证支付数据错误，" + pmsAppTransInfo.getOrderid());
            mobaoPayValidateResponseDto = new MobaoPayValidateResponseDto();
            mobaoPayValidateResponseDto.setRefCode("020");
            mobaoPayValidateResponseDto.setRefMsg("组装验证支付数据错误");
            return mobaoPayValidateResponseDto;
        }

        return mobaoPayValidateResponseDto;
    }

    private Boolean checkOrder(PmsAppTransInfo pmsAppTransInfo) {
        Boolean result = false;
        if (pmsAppTransInfo != null && pmsAppTransInfo.getStatus().equals(OrderStatusEnum.waitingClientPay.getStatus())) {
            if (pmsAppTransInfo.getPaymentcode().equals(PaymentCodeEnum.moBaoQuickPay.getTypeCode())) {
                result = true;
            } else {
                logger.info("支付方式不是摩宝支付");
            }
        } else {
            logger.info("传入订单为空");
        }
        return result;
    }

    /**
     * 组装摩宝验证支付接口参数数据
     *
     * @param pmsAppTransInfo
     * @param quickpayCardRecord
     * @param channelInfo
     * @return
     */
    private String assemberValidatePayParamData(PmsAppTransInfo pmsAppTransInfo,PospTransInfo pospTransInfo, QuickpayCardRecord quickpayCardRecord, ViewKyChannelInfo channelInfo, String message) {
        String result = null;

        if (channelInfo != null) {
            //商户号码
            String merId = channelInfo.getChannelNO();
            //商户号私钥
            String merKey = channelInfo.getChannelPwd();

            Map<String, String> transmap = new LinkedHashMap<String, String>();
            transmap.put("versionId", "001");
            transmap.put("businessType", "1411");
            transmap.put("insCode", "");
            transmap.put("merId", merId);
            transmap.put("transDate", UtilDate.getOrderNum());

            BigDecimal transAmountBigFen = new BigDecimal(pmsAppTransInfo.getFactamount());
            BigDecimal transAmountBigYuan = transAmountBigFen.movePointLeft(2);
            transmap.put("transAmount", transAmountBigYuan.stripTrailingZeros().toPlainString());
            try {

                    transmap.put("cardByName", MD5Util.encode(quickpayCardRecord.getCardByName().getBytes("UTF-8")));  //此处的MD5util为Base64加密

            } catch (UnsupportedEncodingException e) {
                logger.info(e.getMessage());
                return result;
            }
            transmap.put("cardByNo", quickpayCardRecord.getCardNumber());
            transmap.put("cardType", quickpayCardRecord.getCardType());
            if (quickpayCardRecord.getCardType().equals("00")) {
                //信用卡
                transmap.put("expireDate", quickpayCardRecord.getExpireDate());
                transmap.put("CVV", quickpayCardRecord.getCvv());
            } else {
                //借记卡
                transmap.put("CVV", "");
            }

            transmap.put("bankCode", "");
            transmap.put("openBankName", "");


                transmap.put("cerType", "01");
                transmap.put("cerNumber", quickpayCardRecord.getCerNumber());

                transmap.put("cerType", "01");
                transmap.put("cerNumber", quickpayCardRecord.getCerNumber());


            transmap.put("mobile", quickpayCardRecord.getMobile());
            transmap.put("yzm", message);         //从1401交易 获取的yzm 填入此项完成支付验证
            transmap.put("ksPayOrderId", pospTransInfo.getPospsn()); //从1401交易 获取的ksPayOrderId 填入此项 寻找原交易 完成支付
            //前期先不用通知，4分钟线程查询该订单
            transmap.put("pageNotifyUrl", "");
            transmap.put("backNotifyUrl", "");
            transmap.put("orderDesc", "");
            transmap.put("dev", "");
            transmap.put("fee", "");
            //需要加密的字符串
            String signstr = EncodeUtil.getUrlStr(transmap);
            System.out.println("需要签名的明文" + signstr);
            String signtrue = MD5Util.MD5Encode(signstr + merKey);
            transmap.put("signType", "MD5");
            transmap.put("signData", signtrue);
            //AES加密
            String transData = EncodeUtil.getUrlStr(transmap);
            result = AESUtil.encrypt(transData, merKey);
        }
        return result;
    }

    /**
     * 摩宝查询接口
     *
     * @return
     */
    public synchronized MobaoTransSearchResponseDto transSearch(PmsAppTransInfo pmsAppTransInfo,PospTransInfo pospTransInfo) {
        MobaoTransSearchResponseDto responseDto = null;
        //校验订单
        if (!checkSearchTransOrder(pmsAppTransInfo)) {
            logger.info("订单不是等待支付的状态，orderid=" + pmsAppTransInfo.getOrderid());
            responseDto = new MobaoTransSearchResponseDto();
            responseDto.setRefCode("02");
            responseDto.setRefMsg("订单没有支付，" + pmsAppTransInfo.getOrderid());
            return responseDto;
        }
        String response = null;
        //组装参数
        ViewKyChannelInfo channelInfo = AppPospContext.context.get(BaseServiceImpl.MOBAOCHANNELNUM + BaseServiceImpl.MOBAOPAY);
        String assemberParamData = assemberSearchTransParamData(channelInfo,pospTransInfo);
        if (StringUtils.isNotBlank(assemberParamData)) {
            response = requestBody(channelInfo.getChannelNO(), assemberParamData);
            //解析返回的数据
            responseDto = JSONObject.parseObject(response, MobaoTransSearchResponseDto.class);
        } else {
            logger.info("组装验证支付数据错误，" + pmsAppTransInfo.getOrderid());
            responseDto = new MobaoTransSearchResponseDto();
            responseDto.setRefCode("02");
            responseDto.setRefMsg("组装验证支付数据错误");
            return responseDto;
        }
        return responseDto;
    }

    /**
     * 组装摩宝验证支付接口参数数据
     *
     * @param pmsAppTransInfo
     * @return
     */
    private String assemberSearchTransParamData( ViewKyChannelInfo channelInfo,PospTransInfo pospTransInfo) {
        String result = null;

        if (channelInfo != null) {
            //商户号码
            String merId = channelInfo.getChannelNO();
            //商户号私钥
            String merKey = channelInfo.getChannelPwd();

            Map<String, String> transmap = new LinkedHashMap<String, String>();
            transmap.put("versionId", "001");
            transmap.put("businessType", "1421");
            transmap.put("insCode", "");
            transmap.put("merId", merId);
            transmap.put("orderId", pospTransInfo.getTransOrderId());
            transmap.put("transDate", UtilDate.getOrderNum());
            //需要加密的字符串
            String signstr = EncodeUtil.getUrlStr(transmap);
            System.out.println("需要签名的明文" + signstr);
            String signtrue = MD5Util.MD5Encode(signstr + merKey);
            transmap.put("signType", "MD5");
            transmap.put("signData", signtrue);
            //AES加密
            String transData = EncodeUtil.getUrlStr(transmap);
            result = AESUtil.encrypt(transData, merKey);
        }
        return result;
    }

    /**
     * 组装摩宝验证支付接口参数数据
     *
     * @param pmsAppTransInfo
     * @return
     */
    private String assemberReSendMsgParamData(PmsAppTransInfo pmsAppTransInfo, ViewKyChannelInfo channelInfo) {
        String result = null;

        if (channelInfo != null) {
            //商户号码
            String merId = channelInfo.getChannelNO();
            //商户号私钥
            String merKey = channelInfo.getChannelPwd();

            Map<String, String> transmap = new LinkedHashMap<String, String>();
            transmap.put("versionId", "001");
            transmap.put("businessType", "1431");
            transmap.put("insCode", "");
            transmap.put("merId", merId);
            transmap.put("orderId", pmsAppTransInfo.getOrderid());
            //需要加密的字符串
            String signstr = EncodeUtil.getUrlStr(transmap);
            System.out.println("需要签名的明文" + signstr);
            String signtrue = MD5Util.MD5Encode(signstr + merKey);
            transmap.put("signType", "MD5");
            transmap.put("signData", signtrue);
            //AES加密
            String transData = EncodeUtil.getUrlStr(transmap);
            result = AESUtil.encrypt(transData, merKey);
        }
        return result;
    }

    /**
     * 校验查询订单时的传入订单信息
     *
     * @param pmsAppTransInfo
     * @return
     */
    private Boolean checkSearchTransOrder(PmsAppTransInfo pmsAppTransInfo) {
        Boolean result = false;
        if (pmsAppTransInfo != null && pmsAppTransInfo.getStatus().equals(OrderStatusEnum.waitingClientPay.getStatus())) {
            if (pmsAppTransInfo.getPaymentcode().equals(PaymentCodeEnum.moBaoQuickPay.getTypeCode())) {
                result = true;
            } else {
                logger.info("支付方式不是摩宝支付");
            }
        } else {
            logger.info("传入订单状态不为：" + OrderStatusEnum.waitingClientPay.getStatus());
        }
        return result;
    }

    /**
     * 摩宝重新获取验证码接口
     *
     * @return
     */
//    public synchronized MobaoTransReSendMsgDto reSendMsg(PmsAppTransInfo pmsAppTransInfo, QuickpayCardRecord quickpayCardRecord) {
//        MobaoTransReSendMsgDto responseDto = null;
//        //校验订单
//        if (!checkSearchTransOrder(pmsAppTransInfo)) {
//            logger.info("订单不是等待支付的状态，orderid=" + pmsAppTransInfo.getOrderid());
//            responseDto = new MobaoTransReSendMsgDto();
//            responseDto.setRefCode("02");
//            responseDto.setRefMsg("订单没有支付，" + pmsAppTransInfo.getOrderid());
//            return responseDto;
//        }
//        String response = null;
//        //组装参数
//        ViewKyChannelInfo channelInfo = AppPospContext.context.get(BaseServiceImpl.MOBAOCHANNELNUM + BaseServiceImpl.MOBAOPAY);
//        String assemberParamData = assemberReSendMsgParamData(pmsAppTransInfo, channelInfo);
//        if (StringUtils.isNotBlank(assemberParamData)) {
//            response = requestBody(channelInfo.getChannelNO(), assemberParamData);
//            //解析返回的数据
//            responseDto = JSONObject.parseObject(response, MobaoTransReSendMsgDto.class);
//
//            if (responseDto != null) {
//                if (responseDto.getStatus().equals("00")) {
//                    //调用成功，调用短信发送接口发送短信
//                    try {
//                        QuickPayMessage quickPayMessage = new QuickPayMessage();
//                        quickPayMessage.setOrderId(pmsAppTransInfo.getOrderid());
//                        quickPayMessage.setMessage(responseDto.getYzm());
//                        String msgResult = pmsMessageService.getMessageAuthenticationCode(quickpayCardRecord.getMobile(), PmsMessage.QUICKPAYPRE, pmsAppTransInfo.getoAgentNo(), quickPayMessage);
//                        if (StringUtils.isNotBlank(msgResult) && msgResult.equals(BaseServiceImpl.SUCCESSMESSAGE)) {
//                             return responseDto;
//                        } else {
//                            logger.info("发送短信失败");
//                            responseDto = new MobaoTransReSendMsgDto();
//                            responseDto.setRefCode("02");
//                            responseDto.setRefMsg("发送短信失败");
//                            return responseDto;
//                        }
//
//                    } catch (Exception e) {
//                        logger.info(e.getMessage());
//                        return responseDto;
//                    }
//
//                } else {
//                    logger.info("调用重发接口失败");
//                    responseDto = new MobaoTransReSendMsgDto();
//                    responseDto.setRefCode("02");
//                    responseDto.setRefMsg(responseDto.getRefMsg());
//                    return responseDto;
//                }
//            } else {
//                logger.info("调用重发接口返回数据为空，" + pmsAppTransInfo.getOrderid());
//                return responseDto;
//            }
//
//        } else {
//            logger.info("组装验证支付数据错误，" + pmsAppTransInfo.getOrderid());
//            responseDto = new MobaoTransReSendMsgDto();
//            responseDto.setRefCode("02");
//            responseDto.setRefMsg("组装验证支付数据错误");
//            return responseDto;
//        }
//    }


}
