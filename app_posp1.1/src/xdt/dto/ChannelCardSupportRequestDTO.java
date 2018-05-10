package xdt.dto;

/**
 * 银行卡和通道获取银行信息，并返回是否支持次通道
 * User: Jeff
 * Date: 16-3-16
 * Time: 上午9:49
 * To change this template use File | Settings | File Templates.
 */
public class ChannelCardSupportRequestDTO {
    String channelNum;
    String cardNo;

    public String getChannelNum() {
        return channelNum;
    }

    public void setChannelNum(String channelNum) {
        this.channelNum = channelNum;
    }

    public String getCardNo() {
        return cardNo;
    }

    public void setCardNo(String cardNo) {
        this.cardNo = cardNo;
    }

    @Override
    public String toString() {
        return "ChannelCardSupportRequestDTO{" +
                "channelNum='" + channelNum + '\'' +
                ", cardNo='" + cardNo + '\'' +
                '}';
    }
}
