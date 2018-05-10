package xdt.service;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import xdt.model.SessionInfo;

/**
 * 商户反馈信息
 * @author wumeng
 *
 */
public  interface IMerchantsFeedbackService  {
	
  /**
   * 添加用户反馈信息
   * @param param
   * @param sessionInfo
   * @return
   * @throws Exception
   */
   public String  merchantFeedback(String param ,SessionInfo sessionInfo)throws Exception;
   
   /**
    * 添加用户反馈信息异常
    * @return
    */
   public String merchantFeedbackException(HttpSession session)throws Exception;
}
