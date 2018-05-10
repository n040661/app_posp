package xdt.util;

import xdt.model.TradeTypeModel;

import java.util.ArrayList;
import java.util.List;

/**
 * 交易类型枚举类
 *      merchantCollect("1","商户收款",1),
 *      transeMoney("2","转账汇款",2),
 *      creditCardRePay("3","信用卡还款",3),
 *      phonePay("4","手机充值",4),
 *      utility("5","水煤电",5),
 *      sinopecPay("6","加油卡充值",6),
 *      drawMoney("7","提现",7);
 *      expressQuery("8", "快递查询",8),
 * 		peccancy("9", "违章查询",9),
 * 		planeTicket("10", "机票",10),
 * 		trainTickets("11", "火车票",11),
 * 		scenicSpotTicket("12", "酒店",12),
 * 		ydhbPay("13", "景区门票",13),
 * 		shop("14", "商城",14);
 * 
 * User: Jeff
 * Date: 15-5-26
 * Time: 上午10:10
 * To change this template use File | Settings | File Templates.
 */
public enum TradeTypeEnum {

    //商户收款  min 10
    merchantCollect("1","商户收款",1),
    //转账汇款   10
    transeMoney("2","转账汇款",2),
    //信用卡还款   10
    creditCardRePay("3","信用卡还款",3),
    //手机充值
    phonePay("4","手机充值",4),
    //水煤电
    utility("5","水煤电",5),
    //加油卡充值
    sinopecPay("6","加油卡充值",6),
    //提款  1000
    drawMoney("7","提现",7),
    //快递查询
    expressQuery("8", "快递查询",8),
    //违章查询
	peccancy("9", "违章查询",9),
	//"机票
	planeTicket("10", "机票",10),
	//火车票
	trainTickets("11", "火车票",11),
	//酒店
	scenicSpotTicket("12", "酒店",12),
	//景区门票
	ydhbPay("13", "景区门票",13),
    //商城
	shop("14", "商城",14),
    //信用卡申请
   creditCardApply("15", "信用卡申请",15),
    //个人贷款
   personalLoan("16", "个人贷款",16),
	//网购代表仅网银在线客户使用
    onlinePay("17", "网购",17),
    
    refund("18", "退款",18);

    String typeCode;
    String typeName;
    Integer typeCodeInt;

    private TradeTypeEnum(String typeCode, String typeName, Integer typeCodeInt) {
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
         TradeTypeEnum[] tradeTypeEnums = TradeTypeEnum.values();

        if(tradeTypeEnums != null && tradeTypeEnums.length > 0){
            resultList = new ArrayList<TradeTypeModel>();
            TradeTypeModel typeModel = null;
            for(TradeTypeEnum typeEnum:tradeTypeEnums){
                typeModel = new TradeTypeModel();
                typeModel.setTradeTypeCode(typeEnum.getTypeCode());
                typeModel.setTradeTypeName(typeEnum.getTypeName());
                resultList.add(typeModel);
            }
         }
        return resultList;
    }

}
