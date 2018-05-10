package xdt.dao;

import xdt.model.ShareInfo;

public interface IShareInfoDao extends IBaseDao<ShareInfo>{

    /**
     * 根据欧单编号查询
     * @param oAgentNo
     * @return
     * @throws Exception
     */
    public ShareInfo selectByOagentNo(String oAgentNo);

}
