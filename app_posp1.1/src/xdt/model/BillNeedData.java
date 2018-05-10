package xdt.model;

import xdt.util.OrderStatusEnum;

import java.util.ArrayList;
import java.util.List;

/**
 * 组装返回账单对象
 * User: Jeff
 * Date: 15-5-25
 * Time: 下午1:28
 * To change this template use File | Settings | File Templates.
 */
public class BillNeedData {

        String paymentTypeCode;//支付方式码
        String paymentType;//支付方式
        String payTime;//支付时间
        String payResultCode;//交易结果码
        String payResultMsg;//交易结果信息
        String bankName; //银行名称
        String orderId;//订单号
        String factAmount;//实际交易金额
        String rate;//费率

    /**
     * 将订单对象转换成账单对象
     * @param pmsAppTransInfos
     * @return
     */
       public static List<BillNeedData> parseFromAppTranseInfoList(List<PmsAppTransInfo> pmsAppTransInfos){
                 List<BillNeedData> resultList = null;
                 if(pmsAppTransInfos != null && pmsAppTransInfos.size() > 0){
                     resultList = new ArrayList<BillNeedData>();
                     BillNeedData billListNeedData = null;
                    for(PmsAppTransInfo pmsAppTransInfo : pmsAppTransInfos ){
                        resultList.add(parseFromAppTranseInfo(pmsAppTransInfo));
                    }
                 }
                 return resultList;
       }

    /**
     *  将订单对象转换成账单对象
     * @param pmsAppTransInfo
     * @return
     */
    public static BillNeedData parseFromAppTranseInfo(PmsAppTransInfo pmsAppTransInfo){
        BillNeedData billListNeedData = null;
        if(pmsAppTransInfo != null){
            billListNeedData = new BillNeedData();
            billListNeedData = new BillNeedData();
            billListNeedData.setPaymentTypeCode(pmsAppTransInfo.getPaymentcode());
            billListNeedData.setPaymentType(pmsAppTransInfo.getPaymenttype());
            billListNeedData.setPayTime(pmsAppTransInfo.getTradetime());
            billListNeedData.setPayResultCode(pmsAppTransInfo.getStatus());
            billListNeedData.setPayResultMsg(OrderStatusEnum.getDesFromStatu(pmsAppTransInfo.getStatus()));
            billListNeedData.setBankName(pmsAppTransInfo.getBankname());
            billListNeedData.setOrderId(pmsAppTransInfo.getOrderid());
            billListNeedData.setFactAmount(pmsAppTransInfo.getFactamount());
            billListNeedData.setRate(pmsAppTransInfo.getRate());
        }
        return  billListNeedData;
    }

    public String getPaymentTypeCode() {
        return paymentTypeCode;
    }

    public void setPaymentTypeCode(String paymentTypeCode) {
        this.paymentTypeCode = paymentTypeCode;
    }

    public String getPaymentType() {
        return paymentType;
    }

    public void setPaymentType(String paymentType) {
        this.paymentType = paymentType;
    }

    public String getPayTime() {
        return payTime;
    }

    public void setPayTime(String payTime) {
        this.payTime = payTime;
    }

    public String getPayResultCode() {
        return payResultCode;
    }

    public void setPayResultCode(String payResultCode) {
        this.payResultCode = payResultCode;
    }

    public String getPayResultMsg() {
        return payResultMsg;
    }

    public void setPayResultMsg(String payResultMsg) {
        this.payResultMsg = payResultMsg;
    }

    public String getBankName() {
        return bankName;
    }

    public void setBankName(String bankName) {
        this.bankName = bankName;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public String getFactAmount() {
        return factAmount;
    }

    public void setFactAmount(String factAmount) {
        this.factAmount = factAmount;
    }

    public String getRate() {
        return rate;
    }

    public void setRate(String rate) {
        this.rate = rate;
    }
}