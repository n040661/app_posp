package xdt.service;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

/**
 * app系统错误信息
 * @author wumeng
 *
 */
public  interface IAppErrorLogService  {
	
  /**
   * 添加app系统错误信息
   * @param pmsMerchantMessage
   * @param session
   * @return
   * @throws Exception
   */
   public void  appErrorLogInsert(String pmsMerchantMessage ,HttpSession session,HttpServletRequest request,String mercid)throws Exception;
   
  
}
