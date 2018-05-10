package xdt.dto;

/**
 * 查询支持的银行卡信息列表
 * User: Jeff
 * Date: 16-3-15
 * Time: 上午11:15
 * To change this template use File | Settings | File Templates.
 */
public class BankSupportListRequestDTO {

    String channelNum;//渠道编号
    String cardType;//银行卡类型  00   贷记卡  01   借记卡 02   准贷记卡 (多个用逗号隔开)

    public String getChannelNum() {
        return channelNum;
    }

    public void setChannelNum(String channelNum) {
        this.channelNum = channelNum;
    }

    public String getCardType() {
        return cardType;
    }

    public void setCardType(String cardType) {
        this.cardType = cardType;
    }
}
