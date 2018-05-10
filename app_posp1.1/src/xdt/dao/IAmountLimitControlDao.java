package xdt.dao;


import xdt.model.AmountLimitControl;
import xdt.model.ResultInfo;

import java.math.BigDecimal;
import java.util.List;

public interface IAmountLimitControlDao extends IBaseDao<AmountLimitControl> {

    /**
     *
     * @param oAgentNo 欧单编号
     * @param transAmount 交易金额
     * @param tradeType  交易类型
     * @return  ResultInfo
     */
    ResultInfo checkLimit(String oAgentNo,BigDecimal transAmount,String tradeType);

    /**
     * 根据欧单编号获取业务金额限制列表
     * @param oAgentNo
     * @return
     */
    List<AmountLimitControl> getListByOagentNo(String oAgentNo);

}
