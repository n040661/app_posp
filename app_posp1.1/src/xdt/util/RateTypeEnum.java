package xdt.util;

import xdt.model.TradeTypeModel;

import java.util.ArrayList;
import java.util.List;

/**
 * 	费率类型
 * 
 *    biaozhunRateType                1           标准费率=协定费率 类型   
 *    fengdingRateType               2           封顶费率和金额类型           
 *    baiduRateType                3           第三方百度费率  类型         
 *    weixinRateType                4            第三方微信费率类型           
 *    zhifubaoRateType               5           第三方支付宝费率 类型      
 *    tixianRateType               6           商户提现费率  类型         
 *    hebaoRateType              7           第三方移动和包费率 类型
 * 
 */
public enum RateTypeEnum {

	//支付方式 例如： 1 账号（余额）支付、2 百度支付、3 微信支付、4 支付宝支付、5刷卡支付、6移动和包支付、0其它

	biaozhunRateType("1", "标准费率=协定费率",1),
	fengdingRateType("2", "封顶费率和金额",2),
	baiduRateType("3", "第三方百度费率 ",3),
	weixinRateType("4", "第三方微信费率",4),
	zhifubaoRateType("5", "第三方支付宝费率",5),
	tixianRateType("6", "商户提现费率",6),
	hebaoRateType("7", "第三方移动和包费率",6),

    //这个字段和吴萌做了协商，数据库中同一欧单下不能重复（费率表是他做的设计）
    mobaoQuickPayRateType("12","第三方摩宝快捷支付费率",12),

    
	hengfengQuickPayRateType("50","第三方恒丰快捷支付费率",50),
	
	//浦发
	pufaRateType("51","扫码费率",51),
	
	beecloudRateType("52","BC快捷支付费率",52);

	String typeCode;
    String typeName;
    Integer typeCodeInt;

    private RateTypeEnum(String typeCode, String typeName, Integer typeCodeInt) {
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
         RateTypeEnum[] paymentCodeEnums = RateTypeEnum.values();

        if(paymentCodeEnums != null && paymentCodeEnums.length > 0){
            resultList = new ArrayList<TradeTypeModel>();
            TradeTypeModel typeModel = null;
            for(RateTypeEnum typeEnum:paymentCodeEnums){
                typeModel = new TradeTypeModel();
                typeModel.setTradeTypeCode(typeEnum.getTypeCode());
                typeModel.setTradeTypeName(typeEnum.getTypeName());
                resultList.add(typeModel);
            }
         }
        return resultList;
    }

}
