package xdt.model;

import java.util.List;

public class ChannelSupportBank {
    private String channelNum;

    private String bnkCode;

    private String bnkName;

    private String bnkImage;

    private String cardType;

    private List<String> cardTypeList;

    public String getChannelNum() {
        return channelNum;
    }

    public void setChannelNum(String channelNum) {
        this.channelNum = channelNum == null ? null : channelNum.trim();
    }

    public String getBnkCode() {
        return bnkCode;
    }

    public void setBnkCode(String bnkCode) {
        this.bnkCode = bnkCode == null ? null : bnkCode.trim();
    }

    public String getBnkName() {
        return bnkName;
    }

    public void setBnkName(String bnkName) {
        this.bnkName = bnkName == null ? null : bnkName.trim();
    }

    public String getBnkImage() {
        return bnkImage;
    }

    public void setBnkImage(String bnkImage) {
        this.bnkImage = bnkImage == null ? null : bnkImage.trim();
    }

    public String getCardType() {
        return cardType;
    }

    public void setCardType(String cardType) {
        this.cardType = cardType;
    }

    public List<String> getCardTypeList() {
        return cardTypeList;
    }

    public void setCardTypeList(List<String> cardTypeList) {
        this.cardTypeList = cardTypeList;
    }
}