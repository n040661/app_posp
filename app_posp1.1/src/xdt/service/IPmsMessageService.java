package xdt.service;

import xdt.model.PmsMessage;
import xdt.model.QuickPayMessage;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.List;

/**
 * @author jeff
 */
public interface IPmsMessageService {
	

	/**
	 * 与短信通道对接 并判断是否成功发送短信验证码
	 * @param mobilePhone
	 * @param type
	 * @return
	 * @throws Exception
	 */
	public String getMessageAuthenticationCode(String mobilePhone,Integer type,String oAgentNo,QuickPayMessage quickPayMessage) throws Exception;
	
	/**
	 * 短信验证码验证
	 * @param captchaValidationInfo
	 * @return
	 * @throws Exception
	 */
	public String captchaValidation(String captchaValidationInfo, HttpSession session, HttpServletRequest request) throws Exception;
	
	/**
	 * 短信验证码验证异常
	 * @return
	 * @throws Exception
	 */
	public String captchaValidationException(String captchaValidationInfo)throws Exception;

    public List<PmsMessage> selectList(String phoneNum)throws Exception;
	

}
