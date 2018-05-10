package xdt.model;

public class QuickpayCardRecord {
    private String cardNumber;

    private String bankName;

    private String cardType;

    private String cerType;

    private String cerNumber;

    private String mobile;

    private String createTime;

    private String mercId;

    private String cardByName;

    private String cvv;

    private String expireDate;

    public String getCardNumber() {
        return cardNumber;
    }

    public void setCardNumber(String cardNumber) {
        this.cardNumber = cardNumber == null ? null : cardNumber.trim();
    }

    public String getBankName() {
        return bankName;
    }

    public void setBankName(String bankName) {
        this.bankName = bankName == null ? null : bankName.trim();
    }

    public String getCardType() {
        return cardType;
    }

    public void setCardType(String cardType) {
        this.cardType = cardType == null ? null : cardType.trim();
    }

    public String getCerType() {
        return cerType;
    }

    public void setCerType(String cerType) {
        this.cerType = cerType == null ? null : cerType.trim();
    }

    public String getCerNumber() {
        return cerNumber;
    }

    public void setCerNumber(String cerNumber) {
        this.cerNumber = cerNumber == null ? null : cerNumber.trim();
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile == null ? null : mobile.trim();
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime == null ? null : createTime.trim();
    }

    public String getMercId() {
        return mercId;
    }

    public void setMercId(String mercId) {
        this.mercId = mercId == null ? null : mercId.trim();
    }

    public String getCardByName() {
        return cardByName;
    }

    public void setCardByName(String cardByName) {
        this.cardByName = cardByName == null ? null : cardByName.trim();
    }

    public String getCvv() {
        return cvv;
    }

    public void setCvv(String cvv) {
        this.cvv = cvv == null ? null : cvv.trim();
    }

    public String getExpireDate() {
        return expireDate;
    }

    public void setExpireDate(String expireDate) {
        this.expireDate = expireDate == null ? null : expireDate.trim();
    }

    @Override
    public String toString() {
        return "QuickpayCardRecord{" +
                "cardNumber='" + cardNumber + '\'' +
                ", bankName='" + bankName + '\'' +
                ", cardType='" + cardType + '\'' +
                ", cerType='" + cerType + '\'' +
                ", cerNumber='" + cerNumber + '\'' +
                ", mobile='" + mobile + '\'' +
                ", createTime='" + createTime + '\'' +
                ", mercId='" + mercId + '\'' +
                ", cardByName='" + cardByName + '\'' +
                ", cvv='" + cvv + '\'' +
                ", expireDate='" + expireDate + '\'' +
                '}';
    }
}