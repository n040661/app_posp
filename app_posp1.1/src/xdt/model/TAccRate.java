package xdt.model;

import java.math.BigDecimal;

public class TAccRate {
    private BigDecimal id;//id

    private String dealType;//交易类型

    private String dealGrade;//等级

    private String rate;//费率

    private String ratePrompt;//费率提示

    public BigDecimal getId() {
        return id;
    }

    public void setId(BigDecimal id) {
        this.id = id;
    }

    public String getDealType() {
        return dealType;
    }

    public void setDealType(String dealType) {
        this.dealType = dealType == null ? null : dealType.trim();
    }

    public String getDealGrade() {
        return dealGrade;
    }

    public void setDealGrade(String dealGrade) {
        this.dealGrade = dealGrade == null ? null : dealGrade.trim();
    }

    public String getRate() {
        return rate;
    }

    public void setRate(String rate) {
        this.rate = rate == null ? null : rate.trim();
    }

    public String getRatePrompt() {
        return ratePrompt;
    }

    public void setRatePrompt(String ratePrompt) {
        this.ratePrompt = ratePrompt == null ? null : ratePrompt.trim();
    }
}