package xdt.dao;

import xdt.model.ChannelSupportBank;

import java.util.List;

public interface IChannelSuportBankDao extends IBaseDao<ChannelSupportBank> {

    /**
     * 按照实体查询记录
     * @param channelSupportBank
     * @return
     */
    List<ChannelSupportBank> getBankByEntry(ChannelSupportBank channelSupportBank);
}
