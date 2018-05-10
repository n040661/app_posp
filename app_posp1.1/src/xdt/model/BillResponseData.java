package xdt.model;

/**
 * 账单列表需要返回的数据
 * User: Jeff
 * Date: 15-5-30
 * Time: 下午1:22
 * To change this template use File | Settings | File Templates.
 */
public class BillResponseData {
    String date; //日期
    String time;  //时间
    String paymengType; //支付类型
    String paymengTypeCode; //支付类型码
    String tradeTypeCode;   //交易类型码
    String tradeType;   //交易类型
    String amount;   //金额
    String orderId; //订单号
    String status;//0:成功  1：正在支付（待定）  2：支付失败（待定）
    Integer isRoot;    //是否是根节点 1：是  其他：不是

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getPaymengType() {
        return paymengType;
    }

    public void setPaymengType(String paymengType) {
        this.paymengType = paymengType;
    }

    public String getTradeType() {
        return tradeType;
    }

    public void setTradeType(String tradeType) {
        this.tradeType = tradeType;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public Integer getRoot() {
        return isRoot;
    }

    public void setRoot(Integer root) {
        isRoot = root;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getPaymengTypeCode() {
        return paymengTypeCode;
    }

    public void setPaymengTypeCode(String paymengTypeCode) {
        this.paymengTypeCode = paymengTypeCode;
    }

    public String getTradeTypeCode() {
        return tradeTypeCode;
    }

    public void setTradeTypeCode(String tradeTypeCode) {
        this.tradeTypeCode = tradeTypeCode;
    }
}
