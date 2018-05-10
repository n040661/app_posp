package xdt.service.impl;

import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Service;

import xdt.dao.IUserInfoDao;
import xdt.dto.AddUserinfoRequestDTO;
import xdt.dto.AddUserinfoResponseDTO;
import xdt.dto.ChangePasswordValidationConfirmRequestDTO;
import xdt.dto.ChangePasswordValidationConfirmResponseDTO;
import xdt.dto.CheckPhoneRequestDTO;
import xdt.dto.CheckPhoneResponseDTO;
import xdt.dto.QueryUserinfoListRequsetDTO;
import xdt.dto.QueryUserinfoListResponseDTO;
import xdt.model.SessionInfo;
import xdt.model.Userinfo;
import xdt.preutil.StringTools;
import xdt.service.IUserInfoService;

@Service("userInfoService")
public class UserInfoServiceImpl extends BaseServiceImpl implements
		IUserInfoService {

	@Resource
	private IUserInfoDao userInfoDao; // 账号信息服务层
	private Logger logger = Logger.getLogger(UserInfoServiceImpl.class);

	/**
	 * 验证手机号
	 * 
	 * @return
	 * @throws Exception
	 */
	@Override
	public String checkPhone(String checkPhone, HttpSession session,
			HttpServletRequest request) throws Exception {
		setMethodSession(request.getRemoteAddr());
		logger.info("验证手机号");
		String message = INITIALIZEMESSAGE;
		Object obj = parseJsonString(checkPhone, CheckPhoneRequestDTO.class);
		Userinfo userinfo = new Userinfo();
		// 获取session信息
		SessionInfo sessionInfo = (SessionInfo) session
				.getAttribute(SessionInfo.SESSIONINFO);
		String loginName = "";
		if (null != sessionInfo) {
			if (!obj.equals(DATAPARSINGMESSAGE)) {
				CheckPhoneRequestDTO requestDTO = (CheckPhoneRequestDTO) obj;
				// 获取商户输入的手机号 密码 验证码
				String mobilePhone = requestDTO.getMobilePhone();
				String validCode = requestDTO.getValidCode();
				String flag = requestDTO.getFlag();
				setSession(request.getRemoteAddr(), session.getId(),
						mobilePhone);
				logger.info("[client_req]" + createJson(requestDTO));
				// 非空验证
				if (!isNotEmptyValidate(validCode)
						|| !isNotEmptyValidate(mobilePhone)) {
					insertAppLogs(mobilePhone, "", "2002");
					message = EMPTYMESSAGE;
				} else {
					// 验证手机号是否合法
					if (checkPhone(mobilePhone)) {
						// 验证商户输入的验证码与服务器接收的是否一致
                        if(StringUtils.isNumeric(flag)){
                            message = verificationCode(mobilePhone, validCode,Integer.parseInt(flag),sessionInfo.getoAgentNo());
                        }
						if ("3".equals(flag)) {
							if (message.equalsIgnoreCase(SUCCESSMESSAGE)) {
								userinfo.setMerchantId(sessionInfo.getMercId());
								userinfo.setoAgentNo(sessionInfo.getoAgentNo());
								userinfo = userInfoDao.searchUserinfo(userinfo);
								String merchantId = userinfo.getMerchantId();
								String loginNameStr = userinfo.getLoginName();

								if (Long.valueOf((userinfo.getRoleId())) == 0) {
									loginName = merchantId.substring(merchantId
											.length() - 9)
											+ "000";
								} else {
									loginName = (Long.parseLong(loginNameStr) + 1)
											+ "";
								}
								session.setAttribute("userinfoLoginName",
										loginName);
							}
						}
					}
				}
			}
		}

		// 解析要返回的信息
		int retCode = Integer.parseInt(message.split(":")[0]);
		String retMessage = message.split(":")[1];
		if (retMessage.equals("initialize")) {
			retMessage = "系统初始化";
		} else if (retMessage.equals("dataParsing")) {
			retMessage = "数据解析错误";
		} else if (retMessage.equals("empty")) {
			retMessage = "注册信息不能为空";
		} else if (retMessage.equals("invalid")) {
			retMessage = "请输入合法的手机号";
		} else if (retMessage.equals("exist")) {
			retMessage = "手机号已注册";
		} else if (retMessage.equals("error")) {
			retMessage = "验证码输入错误";
		} else if (retMessage.equals("failure")) {
			retMessage = "验证码失效，请重新获取";
		} else if (retMessage.equals("success")) {
			retMessage = "验证成功";
		} else if (retMessage.equals("fail")) {
			retMessage = "验证失败";
		} else if (retMessage.equals("failinsert")) {
			retMessage = "账户信息保存失败";
		} else if (retMessage.equals("failsave")) {
			retMessage = "手机业务保存失败";
		}

		CheckPhoneResponseDTO responseData = new CheckPhoneResponseDTO();
		responseData.setRetCode(retCode);
		responseData.setRetMessage(retMessage);
		responseData.setLoginName(loginName);
		String jsonString = createJsonString(responseData);
		logger.info("[app_rsp]" + createJson(responseData));
		return jsonString;
	}

	/**
	 * 验证手机号异常
	 * 
	 * @return
	 * @throws Exception
	 */
	@Override
	public String checkPhoneException() throws Exception {
		CheckPhoneResponseDTO responseData = new CheckPhoneResponseDTO();
		responseData.setRetCode(100);
		responseData.setRetMessage("系统异常");
		logger.info("[app_rsp]" + createJson(responseData));
		return createJsonString(responseData);
	}

	/**
	 * 保存账号
	 * 
	 * @return
	 * @throws Exception
	 */
	@Override
	public String addUserinfo(String userinfo, HttpSession session,
			HttpServletRequest request) throws Exception {
		setMethodSession(request.getRemoteAddr());
		logger.info("添加账号");
		String message = INITIALIZEMESSAGE;
		Object obj = parseJsonString(userinfo, AddUserinfoRequestDTO.class);
		// 获取session信息
		SessionInfo sessionInfo = (SessionInfo) session
				.getAttribute(SessionInfo.SESSIONINFO);
		if (!obj.equals(DATAPARSINGMESSAGE)) {
			AddUserinfoRequestDTO resRequestDTO = (AddUserinfoRequestDTO) obj;
			// 获取商户输入的手机号 密码 验证码
			String loginName = resRequestDTO.getLoginName();
			String loginPwd = resRequestDTO.getLoginPwd();

			String trueName = resRequestDTO.getTrueName();
			String mobilePhone = sessionInfo.getMobilephone();
			String merchantId = sessionInfo.getMercId();
			String userinfoLoginName = session
					.getAttribute("userinfoLoginName").toString();
			String oAgentNo = sessionInfo.getoAgentNo();

			setSession(request.getRemoteAddr(), session.getId(), mobilePhone);
			logger.info("[client_req]" + createJson(resRequestDTO));
			// 非空验证
			if (!isNotEmptyValidate(loginName) || !isNotEmptyValidate(trueName)
					|| !isNotEmptyValidate(loginPwd)) {
				insertAppLogs(mobilePhone, "", "2002");
				message = EMPTYMESSAGE;
			} else {
				if (loginName.equals(userinfoLoginName)) {
					Userinfo u = new Userinfo();
					u.setLoginName(loginName);
					u.setLoginPwd(loginPwd);
					u.setTrueName(trueName);
					u.setRoleId(1L);
					u.setUserStatus(0L);
					u.setMerchantId(merchantId);
					u.setPwdDate(new Date());
					u.setoAgentNo(oAgentNo);

					int count = userInfoDao.insert(u);
					if (count == 1) {
						message = SUCCESSMESSAGE;
					}
				}
			}
		} else {
			insertAppLogs("", "", "2001");
			message = DATAPARSINGMESSAGE;
		}
		// 解析要返回的信息
		int retCode = Integer.parseInt(message.split(":")[0]);
		String retMessage = message.split(":")[1];
		if (retMessage.equals("initialize")) {
			retMessage = "系统初始化";
		} else if (retMessage.equals("dataParsing")) {
			retMessage = "数据解析错误";
		} else if (retMessage.equals("empty")) {
			retMessage = "注册信息不能为空";
		} else if (retMessage.equals("invalid")) {
			retMessage = "请输入合法的手机号";
		} else if (retMessage.equals("exist")) {
			retMessage = "手机号已注册";
		} else if (retMessage.equals("error")) {
			retMessage = "验证码输入错误";
		} else if (retMessage.equals("failure")) {
			retMessage = "验证码失效，请重新获取";
		} else if (retMessage.equals("success")) {
			retMessage = "添加成功";
		} else if (retMessage.equals("fail")) {
			retMessage = "注册失败";
		} else if (retMessage.equals("failinsert")) {
			retMessage = "账户信息保存失败";
		} else if (retMessage.equals("failsave")) {
			retMessage = "手机业务保存失败";
		}
		AddUserinfoResponseDTO responseData = new AddUserinfoResponseDTO();
		responseData.setRetCode(retCode);
		responseData.setRetMessage(retMessage);
		String jsonString = createJsonString(responseData);
		logger.info("[app_rsp]" + createJson(responseData));
		return jsonString;
	}

	/**
	 * 保存账号异常
	 * 
	 * @return
	 * @throws Exception
	 */
	@Override
	public String addUserinfoException() throws Exception {
		AddUserinfoResponseDTO responseData = new AddUserinfoResponseDTO();
		responseData.setRetCode(100);
		responseData.setRetMessage("系统异常");
		logger.info("[app_rsp]" + createJson(responseData));
		return createJsonString(responseData);
	}

	/**
	 * 收银员列表
	 * 
	 * @param queryUserinfo
	 * 
	 * @return
	 * @throws Exception
	 */
	@Override
	public String queryUserinfoList(String queryUserinfo, HttpSession session,
			HttpServletRequest request) throws Exception {
		setMethodSession(request.getRemoteAddr());
		logger.info("收银员列表");
		String message = INITIALIZEMESSAGE;
		Object obj = parseJsonString(queryUserinfo,
				QueryUserinfoListRequsetDTO.class);
		// 获取session信息
		SessionInfo sessionInfo = (SessionInfo) session
				.getAttribute(SessionInfo.SESSIONINFO);

		List<Userinfo> searchList = new ArrayList<Userinfo>();
		if (!obj.equals(DATAPARSINGMESSAGE)) {
			QueryUserinfoListRequsetDTO resRequestDTO = (QueryUserinfoListRequsetDTO) obj;
			// 获取商户输入的手机号 密码 验证码
			String mobilePhone = resRequestDTO.getMobilePhone();

			setSession(request.getRemoteAddr(), session.getId(), mobilePhone);
			logger.info("[client_req]" + createJson(resRequestDTO));
			// 非空验证
			if (!isNotEmptyValidate(mobilePhone)) {
				insertAppLogs(mobilePhone, "", "2002");
				message = EMPTYMESSAGE;
			} else {
				Userinfo u = new Userinfo();
				u.setMerchantId(sessionInfo.getMercId());
				searchList = userInfoDao.searchList(u);

				if (searchList != null && searchList.size() > 0) {
					message = SUCCESSMESSAGE;
				} else {
					message = FAILMESSAGE;
				}
			}
		} else {
			insertAppLogs("", "", "2001");
			message = DATAPARSINGMESSAGE;
		}
		// 解析要返回的信息
		int retCode = Integer.parseInt(message.split(":")[0]);
		String retMessage = message.split(":")[1];
		if (retMessage.equals("initialize")) {
			retMessage = "系统初始化";
		} else if (retMessage.equals("dataParsing")) {
			retMessage = "数据解析错误";
		} else if (retMessage.equals("empty")) {
			retMessage = "注册信息不能为空";
		} else if (retMessage.equals("invalid")) {
			retMessage = "请输入合法的手机号";
		} else if (retMessage.equals("exist")) {
			retMessage = "手机号已注册";
		} else if (retMessage.equals("error")) {
			retMessage = "验证码输入错误";
		} else if (retMessage.equals("failure")) {
			retMessage = "验证码失效，请重新获取";
		} else if (retMessage.equals("success")) {
			retMessage = "查询成功";
		} else if (retMessage.equals("fail")) {
			retMessage = "查询失败";
		} else if (retMessage.equals("failinsert")) {
			retMessage = "账户信息保存失败";
		} else if (retMessage.equals("failsave")) {
			retMessage = "手机业务保存失败";
		}
		QueryUserinfoListResponseDTO responseData = new QueryUserinfoListResponseDTO();
		responseData.setRetCode(retCode);
		responseData.setRetMessage(retMessage);
		responseData.setUserinfoList(searchList);
		String jsonString = createJsonString(responseData);
		logger.info("[app_rsp]" + createJson(responseData));
		return jsonString;
	}

	/**
	 * 收银员列表异常
	 * 
	 * @param queryUserinfo
	 * @return
	 * @throws Exception
	 */
	@Override
	public String queryUserinfoListException() throws Exception {
		QueryUserinfoListResponseDTO responseData = new QueryUserinfoListResponseDTO();
		responseData.setRetCode(100);
		responseData.setRetMessage("系统异常");
		logger.info("[app_rsp]" + createJson(responseData));
		return createJsonString(responseData);
	}

}