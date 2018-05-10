package xdt.service.impl;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpSession;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import xdt.dao.IUserInfoDao;
import xdt.model.Userinfo;
import xdt.service.TSysUserService;

/**
* ********************************************************
* @ClassName: TSysUserServiceImpl
* @Description: 用户表
* @author 生成service类
* @date 2014-06-09 下午 06:42:03 
*******************************************************
*/
@Service
public class TSysUserServiceImpl extends BaseServiceImpl implements TSysUserService{

	@Autowired
	private IUserInfoDao tsysuserDao;

	
	public Userinfo selectList(Userinfo obj) throws Exception {
		Userinfo user = tsysuserDao.searchUserinfo(obj);
		return user;
	}
}

