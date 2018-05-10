package xdt.dao;

import xdt.model.Userinfo;

import java.util.Map;

public interface IUserInfoDao extends IBaseDao<Userinfo> {

	/**
	 * 获取账号
	 */
	public Userinfo searchUserinfo(Userinfo userinfo) throws Exception;

    /**
     * 按照手机查询用户信息
     * @param map
     * @return
     * @throws Exception
     */
    public Userinfo searchUserinfoByMobile(Map<String,String> map) throws Exception;
}
