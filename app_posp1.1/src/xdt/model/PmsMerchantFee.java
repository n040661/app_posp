package xdt.model;

import java.math.BigDecimal;

public class PmsMerchantFee {
    private Integer id;

    private String mercId;

    private String feeTyp;

    private BigDecimal feeRat1;

    private BigDecimal feeRat2;

    private BigDecimal maxFeeAmt1;

    private BigDecimal maxFeeAmt2;

    private BigDecimal fixedFeeAmt;

    private String upFeeTyp;

    private BigDecimal upFeeRat;

    private BigDecimal upMaxFeeAmt;

    private BigDecimal upFixedFeeAmt;

    private String typ;

    private BigDecimal maxAmt;

    private BigDecimal fixedAmt;

    private BigDecimal rat;

    private BigDecimal upFeeRat1;

    private BigDecimal upMaxFeeAmt1;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getMercId() {
        return mercId;
    }

    public void setMercId(String mercId) {
        this.mercId = mercId == null ? null : mercId.trim();
    }

    public String getFeeTyp() {
        return feeTyp;
    }

    public void setFeeTyp(String feeTyp) {
        this.feeTyp = feeTyp == null ? null : feeTyp.trim();
    }

    public BigDecimal getFeeRat1() {
        return feeRat1;
    }

    public void setFeeRat1(BigDecimal feeRat1) {
        this.feeRat1 = feeRat1;
    }

    public BigDecimal getFeeRat2() {
        return feeRat2;
    }

    public void setFeeRat2(BigDecimal feeRat2) {
        this.feeRat2 = feeRat2;
    }

    public BigDecimal getMaxFeeAmt1() {
        return maxFeeAmt1;
    }

    public void setMaxFeeAmt1(BigDecimal maxFeeAmt1) {
        this.maxFeeAmt1 = maxFeeAmt1;
    }

    public BigDecimal getMaxFeeAmt2() {
        return maxFeeAmt2;
    }

    public void setMaxFeeAmt2(BigDecimal maxFeeAmt2) {
        this.maxFeeAmt2 = maxFeeAmt2;
    }

    public BigDecimal getFixedFeeAmt() {
        return fixedFeeAmt;
    }

    public void setFixedFeeAmt(BigDecimal fixedFeeAmt) {
        this.fixedFeeAmt = fixedFeeAmt;
    }

    public String getUpFeeTyp() {
        return upFeeTyp;
    }

    public void setUpFeeTyp(String upFeeTyp) {
        this.upFeeTyp = upFeeTyp == null ? null : upFeeTyp.trim();
    }

    public BigDecimal getUpFeeRat() {
        return upFeeRat;
    }

    public void setUpFeeRat(BigDecimal upFeeRat) {
        this.upFeeRat = upFeeRat;
    }

    public BigDecimal getUpMaxFeeAmt() {
        return upMaxFeeAmt;
    }

    public void setUpMaxFeeAmt(BigDecimal upMaxFeeAmt) {
        this.upMaxFeeAmt = upMaxFeeAmt;
    }

    public BigDecimal getUpFixedFeeAmt() {
        return upFixedFeeAmt;
    }

    public void setUpFixedFeeAmt(BigDecimal upFixedFeeAmt) {
        this.upFixedFeeAmt = upFixedFeeAmt;
    }

    public String getTyp() {
        return typ;
    }

    public void setTyp(String typ) {
        this.typ = typ == null ? null : typ.trim();
    }

    public BigDecimal getMaxAmt() {
        return maxAmt;
    }

    public void setMaxAmt(BigDecimal maxAmt) {
        this.maxAmt = maxAmt;
    }

    public BigDecimal getFixedAmt() {
        return fixedAmt;
    }

    public void setFixedAmt(BigDecimal fixedAmt) {
        this.fixedAmt = fixedAmt;
    }

    public BigDecimal getRat() {
        return rat;
    }

    public void setRat(BigDecimal rat) {
        this.rat = rat;
    }

    public BigDecimal getUpFeeRat1() {
        return upFeeRat1;
    }

    public void setUpFeeRat1(BigDecimal upFeeRat1) {
        this.upFeeRat1 = upFeeRat1;
    }

    public BigDecimal getUpMaxFeeAmt1() {
        return upMaxFeeAmt1;
    }

    public void setUpMaxFeeAmt1(BigDecimal upMaxFeeAmt1) {
        this.upMaxFeeAmt1 = upMaxFeeAmt1;
    }
}