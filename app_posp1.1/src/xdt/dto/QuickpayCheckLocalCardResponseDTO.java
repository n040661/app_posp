package xdt.dto;

/**
 * 校验银行卡信息在本地是否存在的返回实体
 * User: Jeff
 * Date: 16-3-9
 * Time: 下午3:34
 * To change this template use File | Settings | File Templates.
 */
public class QuickpayCheckLocalCardResponseDTO {
    //0:不存在 1：存在
    String status;
    String cardType; //00   贷记卡 01   借记卡 02   准贷记卡
    private Integer retCode;// 操作返回代码

    private String retMessage;// 返回码信息 0成功1 失败100 系统异常

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Integer getRetCode() {
        return retCode;
    }

    public void setRetCode(Integer retCode) {
        this.retCode = retCode;
    }

    public String getRetMessage() {
        return retMessage;
    }

    public void setRetMessage(String retMessage) {
        this.retMessage = retMessage;
    }

    public String getCardType() {
        return cardType;
    }

    public void setCardType(String cardType) {
        this.cardType = cardType;
    }
}
