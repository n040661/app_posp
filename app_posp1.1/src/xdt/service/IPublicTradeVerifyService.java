package xdt.service;

import xdt.model.ResultInfo;
import xdt.util.PaymentCodeEnum;
import xdt.util.TradeTypeEnum;

/**
 * Created with IntelliJ IDEA.
 * User: Jeff
 * Date: 16-1-21
 * Time: 上午10:55
 * To change this template use File | Settings | File Templates.
 */
public interface IPublicTradeVerifyService {

    /**
     * 交易的总校验（判断当前商户的当前交易是否可以进行）
     * @param tradeMoney 交易金额
     * @param paymentCodeEnum 支付类型
     * @param oAgentNo 欧单编号
     * @param mercCode 商户编号
     * @return
     */
    ResultInfo totalVerify(int tradeMoney,TradeTypeEnum tradeTypeEnum,PaymentCodeEnum paymentCodeEnum,String oAgentNo,String mercCode);
    /**
     * 功能模块按照欧单的限制
     * @param tradeTypeEnum
     * @param oAgentNo
     * @return
     */
    ResultInfo moduleVerifyOagent(TradeTypeEnum tradeTypeEnum,String oAgentNo);
    /**
     * 交易类型交易金额欧单限制
     * @param amount
     * @param tradeTypeEnum
     * @param oAgentNo
     * @return
     */
    public ResultInfo amountVerifyOagent(int amount,TradeTypeEnum tradeTypeEnum,String oAgentNo);
    /**
     * 支付类型按照欧单的限制
     * @param payTypeEnum
     * @param oAgentNo
     * @return
     */
    public ResultInfo paytypeVerifyOagent(PaymentCodeEnum payTypeEnum,String oAgentNo);
    /**
     * 支付类型按照商户限制
     * @param payTypeEnum
     * @param mercCode
     * @return
     */
    ResultInfo payTypeVerifyMer(PaymentCodeEnum payTypeEnum,String mercCode);
    /**
     * 商户金额校验
     * @param amount
     * @param mercCode
     * @return
     */
    ResultInfo amountVerifyMer(int amount,TradeTypeEnum tradeTypeEnum,String mercCode);
    /**
     * 模块开启按照商户限制
     * @param tradeTypeEnum
     * @param mercCode
     * @return
     */
    ResultInfo moduelVerifyMer(TradeTypeEnum tradeTypeEnum,String mercCode);


}
