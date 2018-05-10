package xdt.dao;


import xdt.model.PayTypeControl;
import xdt.model.ResultInfo;

import java.util.List;

public interface IPayTypeControlDao extends IBaseDao<PayTypeControl> {

    /**
     * 判断当前支付方式在当前欧单下是否支持
     * @param oAgentNo 欧单编号
     * @param payType  交易类型
     * @return  ResultInfo
     */
    ResultInfo checkLimit(String oAgentNo,String payType);

    /**
     * 根据欧单获取列表
     * @param oAgentNo
     * @return
     */
    List<PayTypeControl> getListByOagentNo(String oAgentNo);

    


}
