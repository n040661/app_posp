package xdt.dto;

import xdt.model.ChannelSupportBank;

import java.util.List;

/**
 * 查询支持的银行卡信息返回列表
 * User: Jeff
 * Date: 16-3-15
 * Time: 上午11:15
 * To change this template use File | Settings | File Templates.
 */
public class BankSupportListResponseDTO {

    private Integer retCode;// 信息编号

    private String retMessage;// 信息描述

    List<ChannelSupportBank>  supportBankList;

    public List<ChannelSupportBank> getSupportBankList() {
        return supportBankList;
    }

    public void setSupportBankList(List<ChannelSupportBank> supportBankList) {
        this.supportBankList = supportBankList;
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
}
