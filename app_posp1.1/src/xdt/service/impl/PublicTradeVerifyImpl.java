package xdt.service.impl;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Service;
import xdt.dao.*;
import xdt.model.PmsAppAmountAndRateConfig;
import xdt.model.PmsAppBusinessConfig;
import xdt.model.PmsAppMerchantPayChannel;
import xdt.model.ResultInfo;
import xdt.service.IPublicTradeVerifyService;
import xdt.util.PaymentCodeEnum;
import xdt.util.TradeTypeEnum;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.List;

/**
 * 交易是否可以进行的校验服务
 * User: Jeff
 * Date: 16-1-21
 * Time: 上午11:04
 * To change this template use File | Settings | File Templates.
 */
@Service
public class PublicTradeVerifyImpl extends BaseServiceImpl implements IPublicTradeVerifyService {

    private Logger logger = Logger.getLogger(PublicTradeVerifyImpl.class);
    @Resource
    IAmountLimitControlDao iAmountLimitControlDao;//交易类型按照金额按照欧单控制
    @Resource
    IPayTypeControlDao iPayTypeControlDao;//支付类型按照欧单控制
    @Resource
    IPmsAppBusinessConfigDao iPmsAppBusinessConfigDao;//功能模块按照欧单控制
    @Resource
    IPmsAppAmountAndRateConfigDao iPmsAppAmountAndRateConfigDao;//商户金额，功能，支付方式控制
    @Resource
    IPmsAppMerchantPayChannelDao iPmsAppMerchantPayChannelDao;//商户支付类型的控制

    @Override
    public ResultInfo totalVerify(int tradeMoney,TradeTypeEnum tradeTypeEnum, PaymentCodeEnum paymentCodeEnum, String oAgentNo, String mercCode) {
       ResultInfo resultInfo = null;

       //校验欧单的模块
        resultInfo = moduleVerifyOagent(tradeTypeEnum,oAgentNo);
        if(!resultInfo.getErrCode().equals("0")){
            return resultInfo;
        }
       //校验欧单的支付方式
        resultInfo = paytypeVerifyOagent(paymentCodeEnum,oAgentNo);
        if(!resultInfo.getErrCode().equals("0")){
            return resultInfo;
        }
        //校验欧单的金额
        resultInfo = amountVerifyOagent(tradeMoney,tradeTypeEnum,oAgentNo);
        if(!resultInfo.getErrCode().equals("0")){
            return resultInfo;
        }
        //校验商户的模块
        resultInfo =  moduelVerifyMer(tradeTypeEnum,mercCode);
        if(!resultInfo.getErrCode().equals("0")){
            return resultInfo;
        }
        //校验商户的支付方式
        resultInfo = payTypeVerifyMer(paymentCodeEnum,mercCode);
        if(!resultInfo.getErrCode().equals("0")){
            return resultInfo;
        }
        //校验商户的金额
        resultInfo = amountVerifyMer(tradeMoney,tradeTypeEnum,mercCode);
        if(!resultInfo.getErrCode().equals("0")){
            return resultInfo;
        }
        return resultInfo;
    }

    /**
     * 功能模块按照欧单的限制
     * @param moduleEnum
     * @param oAgentNo
     * @return
     */
    @Override
    public ResultInfo moduleVerifyOagent(TradeTypeEnum moduleEnum,String oAgentNo){
        ResultInfo resultInfo = new ResultInfo();
        //校验入口参数
        if(StringUtils.isBlank(oAgentNo)){
            logger.info("欧单编号为空");
            resultInfo.setErrCode("2");
            resultInfo.setMsg("参数异常，请重试");
            return resultInfo;
        }
        //查询当前欧单的当前功能
        PmsAppBusinessConfig pmsAppBusinessConfig = new PmsAppBusinessConfig();
        pmsAppBusinessConfig.setoAgentNo(oAgentNo);
        pmsAppBusinessConfig.setModulecode(moduleEnum.getTypeCode());
        List<PmsAppBusinessConfig>  businessConfigs = null;
        try {
            businessConfigs  = iPmsAppBusinessConfigDao.searchList(pmsAppBusinessConfig);
            if(businessConfigs != null && businessConfigs.size() > 0){
                 //判断当前欧单的当前业务是否开启
                if(businessConfigs.get(0).getStatus().equals("1")){
                       resultInfo.setMsg("欧单的当前模块开启");
                       resultInfo.setErrCode("0");
                        return resultInfo;
                }else{
                    resultInfo.setMsg(businessConfigs.get(0).getMessage());
                    resultInfo.setErrCode("1");
                    return resultInfo;
                }
            }else{
                logger.info("没有查到当前欧单的模块记录，oagentno:"+oAgentNo+",module:"+moduleEnum.getTypeName());
                resultInfo.setErrCode("2");
                resultInfo.setMsg("系统异常，请重试或联系客服");
                return resultInfo;
            }
        } catch (Exception e) {
            logger.info("查询sql异常，"+e.getMessage());
            e.printStackTrace();
            resultInfo.setErrCode("2");
            resultInfo.setMsg("系统异常，请重试或联系客服");
            return resultInfo;
        }
    }

    /**
     * 交易类型交易金额欧单限制
     * @param amount
     * @param tradeTypeEnum
     * @param oAgentNo
     * @return
     */
    @Override
    public ResultInfo amountVerifyOagent(int amount,TradeTypeEnum tradeTypeEnum,String oAgentNo){
       return  iAmountLimitControlDao.checkLimit(oAgentNo,new BigDecimal(amount),tradeTypeEnum.getTypeCode());
    }

    /**
     * 支付类型按照欧单的限制
     * @param payTypeEnum
     * @param oAgentNo
     * @return
     */
    @Override
    public ResultInfo paytypeVerifyOagent(PaymentCodeEnum payTypeEnum,String oAgentNo){
      return  iPayTypeControlDao.checkLimit(oAgentNo,payTypeEnum.getTypeCode());
    }

    /**
     * 模块开启按照商户限制
     * @param tradeTypeEnum
     * @param mercCode
     * @return
     */
    @Override
    public ResultInfo moduelVerifyMer(TradeTypeEnum tradeTypeEnum,String mercCode){
        ResultInfo resultInfo = new ResultInfo();
        PmsAppAmountAndRateConfig pmsAppAmountAndRateConfig = new PmsAppAmountAndRateConfig();
        pmsAppAmountAndRateConfig.setMercId(mercCode);
        pmsAppAmountAndRateConfig.setBusinesscode(tradeTypeEnum.getTypeCode());
        try {
            List<PmsAppAmountAndRateConfig> pmsAppAmountAndRateConfigList = iPmsAppAmountAndRateConfigDao.searchList(pmsAppAmountAndRateConfig);
            if(pmsAppAmountAndRateConfigList != null && pmsAppAmountAndRateConfigList.size() > 0){
                //校验是否开启
                if(pmsAppAmountAndRateConfigList.get(0).getStatus().equals("1")){
                    resultInfo.setErrCode("0");
                    resultInfo.setMsg("校验通过");
                    return resultInfo;
                }else{
                    resultInfo.setMsg(pmsAppAmountAndRateConfigList.get(0).getMessage());
                    resultInfo.setErrCode("1");
                    return resultInfo;
                }
            }else{
                logger.info("没有查到当前商户的模块记录，mercCode:"+mercCode+",tradeType:"+tradeTypeEnum.getTypeName());
                resultInfo.setErrCode("2");
                resultInfo.setMsg("系统异常，请重试或联系客服");
                return resultInfo;
            }
        } catch (Exception e) {
            logger.info("查询sql异常，"+e.getMessage());
            e.printStackTrace();
            resultInfo.setErrCode("2");
            resultInfo.setMsg("系统异常，请重试或联系客服");
            return resultInfo;
        }


    }

    /**
     * 商户金额校验
     * @param amount
     * @param mercCode
     * @return
     */
    @Override
    public ResultInfo amountVerifyMer(int amount,TradeTypeEnum tradeTypeEnum,String mercCode){
        ResultInfo resultInfo = new ResultInfo();
        if(amount <= 0){
            logger.info("交易金额小于0");
            resultInfo.setErrCode("2");
            resultInfo.setMsg("参数异常，请重试");
            return resultInfo;
        }
        PmsAppAmountAndRateConfig pmsAppAmountAndRateConfig = new PmsAppAmountAndRateConfig();
        pmsAppAmountAndRateConfig.setMercId(mercCode);
        pmsAppAmountAndRateConfig.setBusinesscode(tradeTypeEnum.getTypeCode());
        //查询交易金额限制
        try {
            List<PmsAppAmountAndRateConfig> pmsAppAmountAndRateConfigList = iPmsAppAmountAndRateConfigDao.searchList(pmsAppAmountAndRateConfig);
            if(pmsAppAmountAndRateConfigList != null && pmsAppAmountAndRateConfigList.size() > 0){
                //校验最大值最小值
                PmsAppAmountAndRateConfig p = pmsAppAmountAndRateConfigList.get(0);
                String minAmount = p.getMinAmount();
                String maxAmount = p.getMaxAmount();
                
                int t =Integer.parseInt(maxAmount);
                
                if(amount >= Integer.parseInt(minAmount) && amount <= Integer.parseInt(maxAmount)){
                    resultInfo.setErrCode("0");
                    resultInfo.setMsg("校验通过");
                    return resultInfo;
                }else{
                    resultInfo.setMsg(pmsAppAmountAndRateConfigList.get(0).getMessage());
                    resultInfo.setErrCode("1");
                    return resultInfo;
                }

            }else{
                logger.info("没有查到当商户的交易金额限制记录，mercCode:"+mercCode+",tradeType:"+tradeTypeEnum.getTypeName());
                resultInfo.setErrCode("2");
                resultInfo.setMsg("系统异常，请重试或联系客服");
                return resultInfo;
            }
        } catch (Exception e) {
            logger.info("查询sql异常，"+e.getMessage());
            e.printStackTrace();
            resultInfo.setErrCode("2");
            resultInfo.setMsg("系统异常，请重试或联系客服");
            return resultInfo;
        }

    }

    /**
     * 支付类型按照商户限制
     * @param payTypeEnum
     * @param mercCode
     * @return
     */
    @Override
    public ResultInfo payTypeVerifyMer(PaymentCodeEnum payTypeEnum,String mercCode){
        ResultInfo resultInfo = new ResultInfo();
        PmsAppMerchantPayChannel pmsAppMerchantPayChannel = new PmsAppMerchantPayChannel();
        pmsAppMerchantPayChannel.setMercId(mercCode);
        pmsAppMerchantPayChannel.setPaymentcode(payTypeEnum.getTypeCode());
        try {
            List<PmsAppMerchantPayChannel> list = iPmsAppMerchantPayChannelDao.searchList(pmsAppMerchantPayChannel);
            if(list != null && list.size() > 0){
               if(list.get(0).getStatus().equals("0")){
                   resultInfo.setErrCode("0");
                   resultInfo.setMsg("校验通过");
                   return resultInfo;
               }else{
                   resultInfo.setMsg(list.get(0).getReason());
                   resultInfo.setErrCode("1");
                   return resultInfo;
               }
            }else{
                logger.info("没有查到当前商户的金额限制记录，mercCode:"+mercCode+",payType:"+payTypeEnum.getTypeName());
                resultInfo.setErrCode("2");
                resultInfo.setMsg("系统异常，请重试或联系客服");
                return resultInfo;
            }
        } catch (Exception e) {
            logger.info("查询sql异常，"+e.getMessage());
            e.printStackTrace();
            resultInfo.setErrCode("2");
            resultInfo.setMsg("系统异常，请重试或联系客服");
            return resultInfo;
        }
    }

}
