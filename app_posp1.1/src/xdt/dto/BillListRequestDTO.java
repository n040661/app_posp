package xdt.dto;

/**
 * 账单列表请求体
 * User: Jeff
 * Date: 15-5-22
 * Time: 下午2:02
 * To change this template use File | Settings | File Templates.
 */
public class BillListRequestDTO {

    String tradetypecode;//交易类型  1 商户收款、2 转账汇款、3 信用卡还款、4手机充值、5 水煤电、 6 加油卡充值、7 提款（提现）。。。 具体参照 TradeTypeEnum
    String paymentcode;//支付方式  1 账号支付、2 百度支付、3 微信支付、4 支付宝支付（可以多个，以";"隔开）,不传为全部
    Integer pageNum;// 当前页

    public String getPaymentcode() {
        return paymentcode;
    }

    public void setPaymentcode(String paymentcode) {
        this.paymentcode = paymentcode;
    }

    public String getTradetypecode() {
        return tradetypecode;
    }

    public void setTradetypecode(String tradetypecode) {
        this.tradetypecode = tradetypecode;
    }

    public Integer getPageNum() {
        return pageNum;
    }

    public void setPageNum(Integer pageNum) {
        this.pageNum = pageNum;
    }
}
