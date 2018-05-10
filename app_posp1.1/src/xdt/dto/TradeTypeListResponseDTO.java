package xdt.dto;

import xdt.model.TradeTypeModel;

import java.util.List;

/**
 * 交易类型列表的返回封装
 * User: Jeff
 * Date: 15-5-26
 * Time: 上午11:16
 * To change this template use File | Settings | File Templates.
 */
public class TradeTypeListResponseDTO {
    private Integer retCode;// 信息编号

    private String retMessage;// 信息描述

    private List<TradeTypeModel>  tradeTypeModels;//交易类型列表

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

    public List<TradeTypeModel> getTradeTypeModels() {
        return tradeTypeModels;
    }

    public void setTradeTypeModels(List<TradeTypeModel> tradeTypeModels) {
        this.tradeTypeModels = tradeTypeModels;
    }
}
