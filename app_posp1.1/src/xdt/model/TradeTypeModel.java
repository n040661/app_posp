package xdt.model;

/**
 * 交易类型封装
 * User: Jeff
 * Date: 15-5-26
 * Time: 上午10:27
 * To change this template use File | Settings | File Templates.
 */
public class TradeTypeModel {

    //交易类型
    String tradeTypeCode;
    //交易类型编码
    String tradeTypeName;

    public String getTradeTypeCode() {
        return tradeTypeCode;
    }

    public void setTradeTypeCode(String tradeTypeCode) {
        this.tradeTypeCode = tradeTypeCode;
    }

    public String getTradeTypeName() {
        return tradeTypeName;
    }

    public void setTradeTypeName(String tradeTypeName) {
        this.tradeTypeName = tradeTypeName;
    }
}
