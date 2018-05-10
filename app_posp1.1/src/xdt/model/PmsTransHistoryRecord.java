package xdt.model;

import java.math.BigDecimal;

public class PmsTransHistoryRecord {
    private BigDecimal id;

    private String mercid;//商户id

    private BigDecimal businessnumber;//业务编号

    private String businessname;//业务名称

    private String bankcardnumber;//银行卡号

    private String bankid;//银行编码

    private String bankname;//银行名称

    private String openingname;//开户行

    private String bankcardname;//银行卡名称

    private String shortbankcardname;//银行卡名称简称

    private String cardholdername;//持卡人

    private String mobilephone;//手机号

    private String shortbankcardnumber;//银行卡号简称

    private String provinceid;//省id

    private String cityid;//市id

    private String createtime;//创建时间

    private String state;//状态 0有效

    private String oAgentNo;//欧单编号

    private String filed2;

    private String filed3;

    public BigDecimal getId() {
        return id;
    }

    public void setId(BigDecimal id) {
        this.id = id;
    }

    public String getMercid() {
        return mercid;
    }

    public void setMercid(String mercid) {
        this.mercid = mercid == null ? null : mercid.trim();
    }

    public BigDecimal getBusinessnumber() {
        return businessnumber;
    }

    public void setBusinessnumber(BigDecimal businessnumber) {
        this.businessnumber = businessnumber;
    }

    public String getBusinessname() {
        return businessname;
    }

    public void setBusinessname(String businessname) {
        this.businessname = businessname == null ? null : businessname.trim();
    }

    public String getBankcardnumber() {
        return bankcardnumber;
    }

    public void setBankcardnumber(String bankcardnumber) {
        this.bankcardnumber = bankcardnumber == null ? null : bankcardnumber.trim();
    }

    public String getBankid() {
        return bankid;
    }

    public void setBankid(String bankid) {
        this.bankid = bankid == null ? null : bankid.trim();
    }

    public String getBankname() {
        return bankname;
    }

    public void setBankname(String bankname) {
        this.bankname = bankname == null ? null : bankname.trim();
    }

    public String getOpeningname() {
        return openingname;
    }

    public void setOpeningname(String openingname) {
        this.openingname = openingname == null ? null : openingname.trim();
    }

    public String getBankcardname() {
        return bankcardname;
    }

    public void setBankcardname(String bankcardname) {
        this.bankcardname = bankcardname == null ? null : bankcardname.trim();
    }

    public String getShortbankcardname() {
        return shortbankcardname;
    }

    public void setShortbankcardname(String shortbankcardname) {
        this.shortbankcardname = shortbankcardname == null ? null : shortbankcardname.trim();
    }

    public String getCardholdername() {
        return cardholdername;
    }

    public void setCardholdername(String cardholdername) {
        this.cardholdername = cardholdername == null ? null : cardholdername.trim();
    }

    public String getMobilephone() {
        return mobilephone;
    }

    public void setMobilephone(String mobilephone) {
        this.mobilephone = mobilephone == null ? null : mobilephone.trim();
    }

    public String getShortbankcardnumber() {
        return shortbankcardnumber;
    }

    public void setShortbankcardnumber(String shortbankcardnumber) {
        this.shortbankcardnumber = shortbankcardnumber == null ? null : shortbankcardnumber.trim();
    }

    public String getProvinceid() {
        return provinceid;
    }

    public void setProvinceid(String provinceid) {
        this.provinceid = provinceid == null ? null : provinceid.trim();
    }

    public String getCityid() {
        return cityid;
    }

    public void setCityid(String cityid) {
        this.cityid = cityid == null ? null : cityid.trim();
    }

    public String getCreatetime() {
        return createtime;
    }

    public void setCreatetime(String createtime) {
        this.createtime = createtime == null ? null : createtime.trim();
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state == null ? null : state.trim();
    }

    public String getoAgentNo() {
        return oAgentNo;
    }

    public void setoAgentNo(String oAgentNo) {
        this.oAgentNo = oAgentNo;
    }

    public String getFiled2() {
        return filed2;
    }

    public void setFiled2(String filed2) {
        this.filed2 = filed2 == null ? null : filed2.trim();
    }

    public String getFiled3() {
        return filed3;
    }

    public void setFiled3(String filed3) {
        this.filed3 = filed3 == null ? null : filed3.trim();
    }
}