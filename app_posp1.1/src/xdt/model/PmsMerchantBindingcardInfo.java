package xdt.model;

import java.math.BigDecimal;

public class PmsMerchantBindingcardInfo {
    private BigDecimal id;

    private String mercId; //商户编号

    private String bankname; //银行名称

    private String settlementname;//账户名

    private String clrMerc;//卡号

    private String bankProvince; //省份

    private String bankCity;//城市

    private String banksysnumber; //系统号

    private String headquartersbank; //总行

    private String cardname; //银行卡名称

    private String shortbankcardnumber; //卡号简称 例如：尾号0000

    private String shortbankcardname; //银行卡简称  例如：借记卡 

    private String provinceid; //省id

    private String cityid; //市id

    private String bankcode; //银行编码

    public BigDecimal getId() {
        return id;
    }

    public void setId(BigDecimal id) {
        this.id = id;
    }

    public String getMercId() {
        return mercId;
    }

    public void setMercId(String mercId) {
        this.mercId = mercId == null ? null : mercId.trim();
    }

    public String getBankname() {
        return bankname;
    }

    public void setBankname(String bankname) {
        this.bankname = bankname == null ? null : bankname.trim();
    }

    public String getSettlementname() {
        return settlementname;
    }

    public void setSettlementname(String settlementname) {
        this.settlementname = settlementname == null ? null : settlementname.trim();
    }

    public String getClrMerc() {
        return clrMerc;
    }

    public void setClrMerc(String clrMerc) {
        this.clrMerc = clrMerc == null ? null : clrMerc.trim();
    }

    public String getBankProvince() {
        return bankProvince;
    }

    public void setBankProvince(String bankProvince) {
        this.bankProvince = bankProvince == null ? null : bankProvince.trim();
    }

    public String getBankCity() {
        return bankCity;
    }

    public void setBankCity(String bankCity) {
        this.bankCity = bankCity == null ? null : bankCity.trim();
    }

    public String getBanksysnumber() {
        return banksysnumber;
    }

    public void setBanksysnumber(String banksysnumber) {
        this.banksysnumber = banksysnumber == null ? null : banksysnumber.trim();
    }

    public String getHeadquartersbank() {
        return headquartersbank;
    }

    public void setHeadquartersbank(String headquartersbank) {
        this.headquartersbank = headquartersbank == null ? null : headquartersbank.trim();
    }

    public String getCardname() {
        return cardname;
    }

    public void setCardname(String cardname) {
        this.cardname = cardname == null ? null : cardname.trim();
    }

    public String getShortbankcardnumber() {
        return shortbankcardnumber;
    }

    public void setShortbankcardnumber(String shortbankcardnumber) {
        this.shortbankcardnumber = shortbankcardnumber == null ? null : shortbankcardnumber.trim();
    }

    public String getShortbankcardname() {
        return shortbankcardname;
    }

    public void setShortbankcardname(String shortbankcardname) {
        this.shortbankcardname = shortbankcardname == null ? null : shortbankcardname.trim();
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

    public String getBankcode() {
        return bankcode;
    }

    public void setBankcode(String bankcode) {
        this.bankcode = bankcode == null ? null : bankcode.trim();
    }
}