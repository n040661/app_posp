package xdt.dto;


import java.math.BigDecimal;

/**
 * 手机充值生成订单请求类
 * User: Jeff
 * Date: 15-5-25
 * Time: 下午2:35
 * To change this template use File | Settings | File Templates.
 */
public class GenerateMobileOrderRequestDTO {

    private String mobilePhone;  //充值手机
    private BigDecimal rechargeAmt;//充值金额 以分为单位
    private String operatorCode;//运营商编号
    private String operatorName; //运营商姓名
    private BigDecimal rechargeAmtValue; //充值金额面值（分）
    private String payType;//1.刷卡支付，2.第三方支付
    private String payChannel;//1.支付宝，2.微信，3百度等  注：当payType为2时，必填
    private String brushType;//刷卡类型：1音频刷卡，2蓝牙刷卡  注：当payType为1时，必填
    private String snNO;//刷卡器设备号
    private String altLat;//经纬度（逗号隔开）
    private String gpsAddress;//gps获取的地址信息(中文)

    private BrushCalorieOfConsumptionRequestDTO dto; //刷卡支付请求


    public String getMobilePhone() {
        return mobilePhone;
    }

    public void setMobilePhone(String mobilePhone) {
        this.mobilePhone = mobilePhone;
    }

    public BigDecimal getRechargeAmt() {
        return rechargeAmt;
    }

    public void setRechargeAmt(BigDecimal rechargeAmt) {
        this.rechargeAmt = rechargeAmt;
    }

    public String getOperatorCode() {
        return operatorCode;
    }

    public void setOperatorCode(String operatorCode) {
        this.operatorCode = operatorCode;
    }

    public String getOperatorName() {
        return operatorName;
    }

    public void setOperatorName(String operatorName) {
        this.operatorName = operatorName;
    }

    public BigDecimal getRechargeAmtValue() {
        return rechargeAmtValue;
    }

    public void setRechargeAmtValue(BigDecimal rechargeAmtValue) {
        this.rechargeAmtValue = rechargeAmtValue;
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

    public String getSnNO() {
        return snNO;
    }

    public void setSnNO(String snNO) {
        this.snNO = snNO;
    }

    public BrushCalorieOfConsumptionRequestDTO getDto() {
        return dto;
    }

    public void setDto(BrushCalorieOfConsumptionRequestDTO dto) {
        this.dto = dto;
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
