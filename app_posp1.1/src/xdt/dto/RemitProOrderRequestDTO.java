package xdt.dto;

/**
 * 转账生成订单号封装类
 * User: Jeff
 * Date: 15-5-19
 * Time: 下午7:50
 * To change this template use File | Settings | File Templates.
 */
public class RemitProOrderRequestDTO {
    public String collectName;//收款人姓名
    public String collectAccNo; //收款人卡号
    public String collectBankName;//收款人卡号所属银行名称
    public String collectBankId;//收款人银行编号
    public String payType;//1.刷卡支付，2.第三方支付
    public String payChannel;//1.支付宝，2.微信，3百度等   注：当payType为2时  必填
    public String brushType;//刷卡类型：1音频刷卡，2蓝牙刷卡   注：当payType为1时，必填
    public String altLat;//经纬度（逗号隔开）
    public String gpsAddress;//gps获取的地址信息(中文)
    public BrushCalorieOfConsumptionRequestDTO dto;//刷卡信息

    public String getCollectName() {
        return collectName;
    }

    public void setCollectName(String collectName) {
        this.collectName = collectName;
    }

    public String getCollectAccNo() {
        return collectAccNo;
    }

    public void setCollectAccNo(String collectAccNo) {
        this.collectAccNo = collectAccNo;
    }

    public String getCollectBankName() {
        return collectBankName;
    }

    public void setCollectBankName(String collectBankName) {
        this.collectBankName = collectBankName;
    }

    public String getPayType() {
        return payType;
    }

    public void setPayType(String payType) {
        this.payType = payType;
    }

    public String getPayChannel() {
        return payChannel;
    }

    public void setPayChannel(String payChannel) {
        this.payChannel = payChannel;
    }

    public String getBrushType() {
        return brushType;
    }

    public void setBrushType(String brushType) {
        this.brushType = brushType;
    }

    public BrushCalorieOfConsumptionRequestDTO getDto() {
        return dto;
    }

    public void setDto(BrushCalorieOfConsumptionRequestDTO dto) {
        this.dto = dto;
    }

    public String getCollectBankId() {
        return collectBankId;
    }

    public void setCollectBankId(String collectBankId) {
        this.collectBankId = collectBankId;
    }

    public String getAltLat() {
        return altLat;
    }

    public void setAltLat(String altLat) {
        this.altLat = altLat;
    }

    public String getGpsAddress() {
        return gpsAddress;
    }

    public void setGpsAddress(String gpsAddress) {
        this.gpsAddress = gpsAddress;
    }
}
