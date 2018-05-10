package xdt.service;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpSession;

import org.springframework.stereotype.Service;

import xdt.model.Userinfo;

/**
* ********************************************************
* @ClassName: TSysUserService
* @Description: 用户表
* @author 生成service类
* @date 2014-06-09 下午 06:42:03 
*******************************************************
*/
public interface TSysUserService{

	public Userinfo selectList(Userinfo obj) throws Exception;
}

