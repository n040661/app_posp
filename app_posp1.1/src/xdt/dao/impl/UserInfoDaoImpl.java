package xdt.dao.impl;

import org.springframework.stereotype.Repository;
import xdt.dao.IUserInfoDao;
import xdt.model.Userinfo;

import java.util.Map;

@Repository
public class UserInfoDaoImpl extends BaseDaoImpl<Userinfo> implements
		IUserInfoDao {

	private static final String SELECT = "selectByModel";

    //按照手机号查询
    private static final String SELECTBYBMOBILE ="selectByMobile";

	@Override
	public Userinfo searchUserinfo(Userinfo userinfo) throws Exception {
		return sqlSession.selectOne(getStatementId(SELECT), userinfo);
	}

	@Override
	public Userinfo searchUserinfoByMobile(Map<String,String> params) throws Exception {
		return sqlSession.selectOne(getStatementId(SELECTBYBMOBILE), params);
	}

}