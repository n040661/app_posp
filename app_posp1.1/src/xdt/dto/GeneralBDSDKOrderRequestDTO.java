package xdt.dto;

/**
 * 生成百度SDK订单的请求.
 * User: Jeff
 * Date: 15-5-12
 * Time: 上午9:22
 * To change this template use File | Settings | File Templates.
 */
public class GeneralBDSDKOrderRequestDTO {

    private String paymenttype;//支付方式 例如： 1 账号（余额）支付、2 百度支付、3 微信支付、4 支付宝支付

    private String phonenumbertype;//手机号类型    例如：联通、电信、移动   （充值业务使用

    private String payamount;//交易金额   按分为最小单位  例如：1元=100分   采用100

    private String prepaidphonenumber;//充值手机号

    private String amount;//充值面额 （充值业务使用）

    private String bankcardname;//银行卡名称

    private String mercname;//账户持卡人

    private String bankname;//银行名称

    private String shortbankcardnumber;//银行卡尾号后四位

    private String payeename;//收款人

    private String reasonofpayment;//付款理由

    private String prepaidcomeoncardcompany;//石油集团名称

    private String comeonkaka;//加油卡卡号

    private String factamount;//实际金额

    private String tradetypecode; //订单类型编码 三位字符串

    private String bankno; //银行卡号

    private String creditcardnumber; //刷卡银行卡号

    private String finishtime;//订单完成时间

    private String orderamount;//订单金额


    public String getPaymenttype() {
        return paymenttype;
    }

    public void setPaymenttype(String paymenttype) {
        this.paymenttype = paymenttype;
    }

    public String getPhonenumbertype() {
        return phonenumbertype;
    }

    public void setPhonenumbertype(String phonenumbertype) {
        this.phonenumbertype = phonenumbertype;
    }

    public String getPayamount() {
        return payamount;
    }

    public void setPayamount(String payamount) {
        this.payamount = payamount;
    }

    public String getPrepaidphonenumber() {
        return prepaidphonenumber;
    }

    public void setPrepaidphonenumber(String prepaidphonenumber) {
        this.prepaidphonenumber = prepaidphonenumber;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getBankcardname() {
        return bankcardname;
    }

    public void setBankcardname(String bankcardname) {
        this.bankcardname = bankcardname;
    }

    public String getMercname() {
        return mercname;
    }

    public void setMercname(String mercname) {
        this.mercname = mercname;
    }

    public String getBankname() {
        return bankname;
    }

    public void setBankname(String bankname) {
        this.bankname = bankname;
    }

    public String getShortbankcardnumber() {
        return shortbankcardnumber;
    }

    public void setShortbankcardnumber(String shortbankcardnumber) {
        this.shortbankcardnumber = shortbankcardnumber;
    }

    public String getPayeename() {
        return payeename;
    }

    public void setPayeename(String payeename) {
        this.payeename = payeename;
    }

    public String getReasonofpayment() {
        return reasonofpayment;
    }

    public void setReasonofpayment(String reasonofpayment) {
        this.reasonofpayment = reasonofpayment;
    }

    public String getPrepaidcomeoncardcompany() {
        return prepaidcomeoncardcompany;
    }

    public void setPrepaidcomeoncardcompany(String prepaidcomeoncardcompany) {
        this.prepaidcomeoncardcompany = prepaidcomeoncardcompany;
    }

    public String getComeonkaka() {
        return comeonkaka;
    }

    public void setComeonkaka(String comeonkaka) {
        this.comeonkaka = comeonkaka;
    }

    public String getFactamount() {
        return factamount;
    }

    public void setFactamount(String factamount) {
        this.factamount = factamount;
    }

    public String getTradetypecode() {
        return tradetypecode;
    }

    public void setTradetypecode(String tradetypecode) {
        this.tradetypecode = tradetypecode;
    }

    public String getBankno() {
        return bankno;
    }

    public void setBankno(String bankno) {
        this.bankno = bankno;
    }

    public String getCreditcardnumber() {
        return creditcardnumber;
    }

    public void setCreditcardnumber(String creditcardnumber) {
        this.creditcardnumber = creditcardnumber;
    }

    public String getFinishtime() {
        return finishtime;
    }

    public void setFinishtime(String finishtime) {
        this.finishtime = finishtime;
    }

    public String getOrderamount() {
        return orderamount;
    }

    public void setOrderamount(String orderamount) {
        this.orderamount = orderamount;
    }
}
