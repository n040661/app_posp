package xdt.dto;

import xdt.model.ChannelSupportBank;

/**
 * 银行卡和通道获取银行信息，并返回是否支持次通道
 * User: Jeff
 * Date: 16-3-16
 * Time: 上午9:49
 * To change this template use File | Settings | File Templates.
 */
public class ChannelCardSupportResponseDTO {

    private ChannelSupportBank channelSupportBank;//通道支持的银行

    private String  localFlag;//1:本地存在 2：本地不存在 3：不支持的卡宾

    private String transAmountLimitMsg;//交易金额限制的提示信息

    private Integer retCode;// 信息编号

    private String retMessage;// 信息描述

    public ChannelSupportBank getChannelSupportBank() {
        return channelSupportBank;
    }

    public void setChannelSupportBank(ChannelSupportBank channelSupportBank) {
        this.channelSupportBank = channelSupportBank;
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

    public String getLocalFlag() {
        return localFlag;
    }

    public void setLocalFlag(String localFlag) {
        this.localFlag = localFlag;
    }

    public String getTransAmountLimitMsg() {
        return transAmountLimitMsg;
    }

    public void setTransAmountLimitMsg(String transAmountLimitMsg) {
        this.transAmountLimitMsg = transAmountLimitMsg;
    }

    @Override
    public String toString() {
        return "ChannelCardSupportResponseDTO{" +
                "channelSupportBank=" + channelSupportBank +
                ", localFlag='" + localFlag + '\'' +
                ", transAmountLimitMsg='" + transAmountLimitMsg + '\'' +
                ", retCode=" + retCode +
                ", retMessage='" + retMessage + '\'' +
                '}';
    }
}
