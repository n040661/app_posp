package xdt.util;

import xdt.model.TradeTypeModel;

import java.util.ArrayList;
import java.util.List;

/**
 * 支付方式
 *  accountPay("1", "账号（余额）支付",1),
 *  baiduPay("2", "百度支付",2),
 *  weixinPay("3", "微信支付",3),
 *  zhifubaoPay("4", "支付宝支付",4),
 *  shuakaPay("5", "刷卡支付",5),
 *  ydhbPay("6", "移动和包支付",6);
 * 
 */
public enum PaymentCodeEnum {

	//支付方式 例如： 1 账号（余额）支付、2 百度支付（扫码）、3 微信支付、4 支付宝支付、5刷卡支付、6移动和包支付、7，百度SDK支付、8，微信SDK支付、9，支付宝SDK支付 10,摩宝快捷支付,11 恒丰快捷支付 0其它

	accountPay("1", "账号（余额）支付",1),
    baiduPay("2", "百度支付",2),
    weixinPay("3", "微信支付",3),
    zhifubaoPay("4", "支付宝支付",4),
    shuakaPay("5", "刷卡支付",5),
    ydhbPay("6", "移动和包支付",6),
    bdSDKPay("7", "百度支付H5",7),
    weixinSDKPay("8", "微信支付H5",8),
    zhifubaoSDKPay("9", "支付宝H5",9),
    moBaoQuickPay("10", "快捷支付",10),
	hengFengQuickPay("11", "银联在线支付",11),
	GatewayCodePay("16","网关支付",16),
	WithholdPay("17","代扣",17),
	JingDong("18","京东扫码",18),
	//浦发
	QRCodePay("12", "银联二维码",12),
	BCQuickPay("13", "BC快捷",13),
	//北农商
	PNCodePay("14","微信公众号",14),
   //江苏
    QQCodePay("15","QQ扫码",15),
	suningPay("19","苏宁扫码",19);


	String typeCode;
    String typeName;
    Integer typeCodeInt;

    private PaymentCodeEnum(String typeCode, String typeName, Integer typeCodeInt) {
        this.typeCode = typeCode;
        this.typeName = typeName;
        this.typeCodeInt = typeCodeInt;
    }

    public String getTypeCode() {
        return typeCode;
    }

    public void setTypeCode(String typeCode) {
        this.typeCode = typeCode;
    }

    public String getTypeName() {
        return typeName;
    }

    public void setTypeName(String typeName) {
        this.typeName = typeName;
    }

    public Integer getTypeCodeInt() {
        return typeCodeInt;
    }

    public void setTypeCodeInt(Integer typeCodeInt) {
        this.typeCodeInt = typeCodeInt;
    }

    public static List<TradeTypeModel>  getTradeTypeList(){
         List<TradeTypeModel> resultList = null;
         PaymentCodeEnum[] paymentCodeEnums = PaymentCodeEnum.values();

        if(paymentCodeEnums != null && paymentCodeEnums.length > 0){
            resultList = new ArrayList<TradeTypeModel>();
            TradeTypeModel typeModel = null;
            for(PaymentCodeEnum typeEnum:paymentCodeEnums){
                typeModel = new TradeTypeModel();
                typeModel.setTradeTypeCode(typeEnum.getTypeCode());
                typeModel.setTradeTypeName(typeEnum.getTypeName());
                resultList.add(typeModel);
            }
         }
        return resultList;
    }

}
