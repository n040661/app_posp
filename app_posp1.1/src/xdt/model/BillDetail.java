package xdt.model;

import org.apache.commons.lang.StringUtils;
import xdt.dto.PospTransInfoOrgRspDTO;
import xdt.util.OrderStatusEnum;
import xdt.util.UtilMath;

/**
 * 账单详情
 * User: Jeff
 * Date: 15-6-1
 * Time: 下午3:54
 * To change this template use File | Settings | File Templates.
 */
public class BillDetail {

    String paymentType;//支付方式
    String paymentTypeCode;//支付方式编码 例如： 1 账号（余额）支付、2 百度支付、3 微信支付、4 支付宝支付、5刷卡支付
    String orderId;  //订单号
    String tradeTime; //交易时间
    String bankName;  //发卡行
    String bankCardnum;   //卡号
    String amount;// 金额
    String status; //交易状态  0成功，其他失败
    String snNO;//刷卡设备编号
    String mercNum;//商户编号
    String mercName;//商户名称
    String bnkCd;//银行编号
    String operId;//操作员id
    String searchNum;//交易参考号
    String batchNo;//批次号
    String authPath;//认证图片路径
    String voucherNum;//凭证号
    String authorizedNum;//授权码
    String altLat;//经纬度（逗号隔开）
    String gpsAddress;//gps获取的地址信息(中文)
    String tradeType;//交易类型







    public String getPaymentType() {
        return paymentType;
    }

    public void setPaymentType(String paymentType) {
        this.paymentType = paymentType;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public String getTradeTime() {
        return tradeTime;
    }

    public void setTradeTime(String tradeTime) {
        this.tradeTime = tradeTime;
    }

    public String getBankName() {
        return bankName;
    }

    public void setBankName(String bankName) {
        this.bankName = bankName;
    }

    public String getBankCardnum() {
        return bankCardnum;
    }

    public void setBankCardnum(String bankCardnum) {
        this.bankCardnum = bankCardnum;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getPaymentTypeCode() {
        return paymentTypeCode;
    }

    public void setPaymentTypeCode(String paymentTypeCode) {
        this.paymentTypeCode = paymentTypeCode;
    }

    public String getSnNO() {
        return snNO;
    }

    public void setSnNO(String snNO) {
        this.snNO = snNO;
    }

    public String getMercNum() {
        return mercNum;
    }

    public void setMercNum(String mercNum) {
        this.mercNum = mercNum;
    }

    public String getMercName() {
        return mercName;
    }

    public void setMercName(String mercName) {
        this.mercName = mercName;
    }

    public String getBnkCd() {
        return bnkCd;
    }

    public void setBnkCd(String bnkCd) {
        this.bnkCd = bnkCd;
    }

    public String getOperId() {
        return operId;
    }

    public void setOperId(String operId) {
        this.operId = operId;
    }

    public String getSearchNum() {
        return searchNum;
    }

    public void setSearchNum(String searchNum) {
        this.searchNum = searchNum;
    }

    public String getBatchNo() {
        return batchNo;
    }

    public void setBatchNo(String batchNo) {
        this.batchNo = batchNo;
    }

    public String getAuthPath() {
        return authPath;
    }

    public void setAuthPath(String authPath) {
        this.authPath = authPath;
    }

    public static BillDetail parseFromTransInfo(PmsAppTransInfo pmsAppTransInfo,PospTransInfo pospTransInfo){
        BillDetail result = new BillDetail();

         //生成的交易信息
        if(pmsAppTransInfo != null){
            result.setPaymentType(pmsAppTransInfo.getPaymenttype());
            result.setOrderId(pmsAppTransInfo.getOrderid());
            result.setTradeTime(pmsAppTransInfo.getTradetime());
            result.setBankName(pmsAppTransInfo.getBankname());
            if(StringUtils.isNotBlank(pmsAppTransInfo.getBankno())){
                result.setBankCardnum(pmsAppTransInfo.getBankno());
            }
           result.setAmount(UtilMath.keepUpDouble(pmsAppTransInfo.getFactamount()).toString());

            if(pmsAppTransInfo != null && pmsAppTransInfo.getStatus() != null){
                if(pmsAppTransInfo.getStatus().equals(OrderStatusEnum.paySuccess.getStatus())
                        || pmsAppTransInfo.getStatus().equals(OrderStatusEnum.returnMoneySuccess.getStatus())
                        ){
                    //支付成功 退款成功
                    result.setStatus("0");
                }else if(pmsAppTransInfo.getStatus().equals(OrderStatusEnum.payFail.getStatus()) ||
                        pmsAppTransInfo.getStatus().equals(OrderStatusEnum.systemErro.getStatus())
                        ){
                    //支付失败 系统异常
                    result.setStatus("2");
                }else {
                    //正在支付
                    result.setStatus("1");
                }
            } else {
                //支付失败
                result.setStatus("2");
            }
            result.setMercName(pmsAppTransInfo.getMercname());
            result.setMercNum(pmsAppTransInfo.getMercid());
            result.setSnNO(pmsAppTransInfo.getSnNO());
            result.setPaymentTypeCode(pmsAppTransInfo.getPaymentcode());


           result.setAuthPath(pmsAppTransInfo.getAuthPath());
            result.setTradeType("交易");


            if(StringUtils.isNotBlank(pmsAppTransInfo.getAltLat())){
               result.setAltLat(pmsAppTransInfo.getAltLat());
           }
            if(StringUtils.isNotBlank(pmsAppTransInfo.getGpsAddress())){
                result.setGpsAddress(pmsAppTransInfo.getGpsAddress());
            }
        }
        //生成的支付信息
        if(pospTransInfo != null){
            //设置银行编号
            result.setBnkCd(pospTransInfo.getBnkCd());
            result.setOperId(pospTransInfo.getOperid());
            result.setSearchNum(pospTransInfo.getSysseqno());
            result.setBatchNo(pospTransInfo.getTerminalsn());
            //TODO 设置凭证号和批次号相同，暂定  Jeff
            result.setVoucherNum(pospTransInfo.getTerminalsn());
            result.setAuthorizedNum(pospTransInfo.getAuthoritycode());
        }
        return  result;
    }

    /**
     * 生成透传的订单详情
     * @param pmsAppTransInfo
     * @return
     */
    public static BillDetail parseFromTransInfoOrg(PmsAppTransInfo pmsAppTransInfo,PospTransInfoOrgRspDTO pospTransInfoOrgRspDTO,PmsUnionpay pmsUnionpay ){
        BillDetail result = new BillDetail();

        //生成的交易信息
        if(pmsAppTransInfo != null){


            if(pmsAppTransInfo != null && pmsAppTransInfo.getStatus() != null){
                if(pmsAppTransInfo.getStatus().equals(OrderStatusEnum.paySuccess.getStatus())
                        || pmsAppTransInfo.getStatus().equals(OrderStatusEnum.returnMoneySuccess.getStatus())
                        ){
                    //支付成功 退款成功
                    result.setStatus("0");
                }else if(pmsAppTransInfo.getStatus().equals(OrderStatusEnum.payFail.getStatus()) ||
                        pmsAppTransInfo.getStatus().equals(OrderStatusEnum.systemErro.getStatus())
                        ){
                    //支付失败 系统异常
                    result.setStatus("2");
                }else {
                    //正在支付
                    result.setStatus("1");
                }
            } else {
                //支付失败
                result.setStatus("2");
            }
            result.setAuthPath(pmsAppTransInfo.getAuthPath());
            result.setPaymentTypeCode(pmsAppTransInfo.getPaymentcode());
            if(StringUtils.isNotBlank(pmsAppTransInfo.getAltLat())){
                result.setAltLat(pmsAppTransInfo.getAltLat());
            }
            if(StringUtils.isNotBlank(pmsAppTransInfo.getGpsAddress())){
                result.setGpsAddress(pmsAppTransInfo.getGpsAddress());
            }
        }
        //生成的支付信息
        if(pospTransInfoOrgRspDTO != null){
           result.setMercName(pmsUnionpay.getMerchantName());
            result.setMercNum(pmsUnionpay.getMerchantCode());
            result.setSnNO(pmsUnionpay.getPosTerminalId());
            result.setOrderId(pospTransInfoOrgRspDTO.getTerminalsn());
            result.setOperId(pospTransInfoOrgRspDTO.getOperid());
            result.setBankName(pmsAppTransInfo.getBankname());
            if(StringUtils.isNotBlank(pmsAppTransInfo.getBankno())){
                result.setBankCardnum(pmsAppTransInfo.getBankno());
            }
            result.setPaymentType(pmsAppTransInfo.getPaymenttype());
            result.setAuthorizedNum(pospTransInfoOrgRspDTO.getAuthoritycode());
            result.setVoucherNum(pospTransInfoOrgRspDTO.getSysseqno());
            result.setSearchNum(pospTransInfoOrgRspDTO.getSysseqno());
            result.setTradeTime(pmsAppTransInfo.getTradetime());
            if(pospTransInfoOrgRspDTO.getTransamt() != null){
                result.setAmount(UtilMath.keepUpDouble(pospTransInfoOrgRspDTO.getTransamt().toString()).toString());
            }
            result.setTradeType("消费");

        }
        return  result;
    }

    public String getVoucherNum() {
        return voucherNum;
    }

    public void setVoucherNum(String voucherNum) {
        this.voucherNum = voucherNum;
    }

    public String getAuthorizedNum() {
        return authorizedNum;
    }

    public void setAuthorizedNum(String authorizedNum) {
        this.authorizedNum = authorizedNum;
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

    public String getTradeType() {
        return tradeType;
    }

    public void setTradeType(String tradeType) {
        this.tradeType = tradeType;
    }
}
