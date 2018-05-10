package xdt.dto;

/**
 * 校验银行卡在本地是否存在的请求实体
 * User: Jeff
 * Date: 16-3-9
 * Time: 下午3:33
 * To change this template use File | Settings | File Templates.
 */
public class QuickpayCheckLocalCardRequestDTO {
    String cardNo;

    public String getCardNo() {
        return cardNo;
    }

    public void setCardNo(String cardNo) {
        this.cardNo = cardNo;
    }
}
