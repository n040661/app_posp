package xdt.dto;

import xdt.model.BillDetail;
import xdt.model.PmsAppTransInfo;

/**
 * 单个账单详情相应响应
 * User: Jeff
 * Date: 15-5-22
 * Time: 下午8:57
 * To change this template use File | Settings | File Templates.
 */
public class BillDetailResponseDTO {
    private Integer retCode;// 信息编号

    private String retMessage;// 信息描述

    private BillDetail billDetail;

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

    public BillDetail getBillDetail() {
        return billDetail;
    }

    public void setBillDetail(BillDetail billDetail) {
        this.billDetail = billDetail;
    }
}
