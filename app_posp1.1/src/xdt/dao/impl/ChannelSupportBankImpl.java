package xdt.dao.impl;

import org.springframework.stereotype.Repository;
import xdt.dao.IChannelSuportBankDao;
import xdt.model.ChannelSupportBank;

import java.util.List;

@Repository
public class ChannelSupportBankImpl extends BaseDaoImpl<ChannelSupportBank> implements IChannelSuportBankDao {

    private static  final String SEARCHBYENTRY = "selectListByChannelAndCardType";

    /**
     * 按照实体插叙列表记录
     * @param channelSupportBank
     * @return
     */
    @Override
    public List<ChannelSupportBank> getBankByEntry(ChannelSupportBank channelSupportBank) {
        String sql = getStatementId(SEARCHBYENTRY);
        return sqlSession.selectList(sql,channelSupportBank);
    }
}
