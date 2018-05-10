package xdt.service;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import xdt.model.PmsMerchantInfo;

public interface IPmsMerchantInfoService {

	/**
	 * 商户注册
	 * 
	 * @param merchantRegisterInfo
	 * @return
	 * @throws Exception
	 */
	public String merchantRegister(String merchantRegisterInfo,
			HttpSession session, HttpServletRequest request) throws Exception;

	/**
	 * 商户注册异常
	 * 
	 * @return
	 * @throws Exception
	 */
	public String merchantRegisterException(String merchantRegisterInfo)
			throws Exception;

	/**
	 * 商户登录
	 * 
	 * @param merchantLoginInfo
	 * @return
	 * @throws Exception
	 */
	public String merchantLogin(String merchantLoginInfo, HttpSession session,
			HttpServletRequest request) throws Exception;

	/**
	 * 商户登录异常
	 * 
	 * @return
	 * @throws Exception
	 */
	public String merchantLoginException(String merchantLoginInfo)
			throws Exception;

	/**
	 * 找回密码验证确认
	 * 
	 * @param retrievePasswordValidationConfirmInfo
	 * @return
	 * @throws Exception
	 */
	public String retrievePasswordValidationConfirm(
			String retrievePasswordValidationConfirmInfo,
			HttpServletRequest request) throws Exception;

	/**
	 * 找回密码验证确认密码
	 * 
	 * @return
	 * @throws Exception
	 */
	public String retrievePasswordValidationConfirmException(
			String retrievePasswordValidationConfirmInfo) throws Exception;

	/**
	 * 修改密码
	 * 
	 * @param changePasswordInfo
	 * @return
	 * @throws Exception
	 */
	public String updatePassword(String changePasswordInfo,
			HttpSession session, HttpServletRequest request) throws Exception;

	/**
	 * 修改密码异常
	 * 
	 * @return
	 * @throws Exception
	 */
	public String updatePasswordException(String changePasswordInfo)
			throws Exception;

	/**
	 * 检索商户实名认证信息
	 */
	public String searchRealNameAuthenticationInformation(
			String realNameAuthenticationInfo,HttpSession session, HttpServletRequest request)
			throws Exception;

	/**
	 * 检索实名认证信息异常
	 * 
	 * @return
	 * @throws Exception
	 */
	public String searchRealNameAuthenticationInformationException(
			String realNameAuthenticationInfo) throws Exception;

	/**
	 * 保存实名认证信息
	 * 
	 * @param realNameAuthenticationInfo
	 * @return
	 * @throws Exception
	 */
	public String saveRealNameAuthenticationInformation(
			String realNameAuthenticationInfo, HttpSession session,
			HttpServletRequest request) throws Exception;

	/**
	 * 保存实名认证信息异常
	 * 
	 * @return
	 * @throws Exception
	 */
	public String saveRealNameAuthenticationInformationException(
			HttpSession session) throws Exception;

	/**
	 * 修改密码验证确认
	 * @param changePasswordValidationConfirmInfo
	 * @return
	 * @throws Exception
	 */
	public String changePasswordValidationConfirm(String changePasswordValidationConfirmInfo,HttpSession session,HttpServletRequest request) throws Exception;
	
	/**
	 * 修改密码验证确认异常
	 * @return
	 * @throws Exception
	 */
	public String changePasswordValidationConfirmException(String changePasswordValidationConfirmInfo)throws Exception;
	/**
	 * 修改额度
	 * @param pmsMerchantInfo
	 * @return
	 * @throws Exception
	 */
	public int UpdatePmsMerchantInfo(PmsMerchantInfo pmsMerchantInfo) throws Exception;
	
	/**
	 * 代付更新金额D0
	 */
	public int updataPay(Map<String, String> map);
	
	/**
	 * 代付更新金额T1
	 */
	public int updataPayT1(Map<String, String> map);
}
