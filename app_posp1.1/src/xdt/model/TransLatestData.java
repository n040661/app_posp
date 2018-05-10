package xdt.model;

/**
 * 订单按日期记录
 * User: Jeff
 * Date: 15-5-29
 * Time: 下午7:22
 * To change this template use File | Settings | File Templates.
 */
public class TransLatestData {
    String dateStr; //YYYY-MM-DD     //完成时间
    String dateStrTrade; //YYYY-MM-DD //交易时间金额
    String amountSum; //总金额
    Double poundageSum;//总手续费

    public String getDateStr() {
        return dateStr;
    }

    public void setDateStr(String dateStr) {
        this.dateStr = dateStr;
    }

    public String getAmountSum() {
        return amountSum;
    }

    public void setAmountSum(String amountSum) {
        this.amountSum = amountSum;
    }

    public Double getPoundageSum() {
        return poundageSum;
    }

    public void setPoundageSum(Double poundageSum) {
        this.poundageSum = poundageSum;
    }

    public String getDateStrTrade() {
        return dateStrTrade;
    }

    public void setDateStrTrade(String dateStrTrade) {
        this.dateStrTrade = dateStrTrade;
    }
}
