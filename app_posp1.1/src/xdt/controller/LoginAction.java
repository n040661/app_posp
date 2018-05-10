package xdt.controller;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.druid.sql.dialect.oracle.ast.stmt.OracleAlterTableSplitPartition.TableSpaceItem;

import xdt.common.XDTSHA1;
import xdt.common.security.XDTConverter;
import xdt.model.Userinfo;
import xdt.service.TSysUserService;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

@Controller
@RequestMapping("login")
public class LoginAction extends BaseAction 
{
	/**
	 * 日志记录
	 */
	public static final Logger log = LoggerFactory.getLogger(LoginAction.class);
	@Resource
    private TSysUserService userSer;
	/**
	 * 用户登录
	 * 
	 * @param name
	 *            完成登录功能
	 * @param param
	 *            登录信息
	 * @throws Exception
	 * 
	 */
	@ResponseBody
	@RequestMapping(value = "userLogin")
    public void login(Userinfo user,HttpServletRequest request, HttpServletResponse response) throws Exception {

        Map<String, String> result=new HashMap<String, String>();
       // Userinfo u = (Userinfo)session.getAttribute("user");
        Userinfo t=new Userinfo();
        if (user != null && (user.getLoginName() != null || user.getLoginPwd()!=null))
        {
            user.setLoginPwd(XDTConverter.bytesToHex(XDTSHA1.getHashByString(user.getLoginPwd())));
            Userinfo u = this.userSer.selectList(t);
        }
    }
}