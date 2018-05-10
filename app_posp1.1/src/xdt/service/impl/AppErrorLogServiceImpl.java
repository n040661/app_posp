package xdt.service.impl;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import xdt.dao.IAppErrorLogDao;
import xdt.dto.AppErrorLog;
import xdt.service.IAppErrorLogService;
import xdt.util.UtilDate;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Service;
/**
 * app系统错误信息
 * @author wumeng
 *
 */
@Service
public class  AppErrorLogServiceImpl extends BaseServiceImpl implements IAppErrorLogService{
	@Resource
	private IAppErrorLogDao appErrorLogDao;
	private Logger logger = Logger.getLogger(AppErrorLogServiceImpl.class);
	/**
	 * app系统错误信息添加
	 */
	public void appErrorLogInsert(String param,HttpSession session,HttpServletRequest request,String mercid) throws Exception {
		logger.info("调用app系统错误信息添加开始接收参数："+param+"；结束时间："+ UtilDate.getDateFormatter());
		AppErrorLog appErrorLog = (AppErrorLog)parseJsonString(param,AppErrorLog.class);
		if(!appErrorLog.equals(DATAPARSINGMESSAGE)){//判断解析数据是否成功
			appErrorLog.setMercid(mercid);//商户编号
			appErrorLog.setCreatetime(UtilDate.getDateFormatter());//创建时间
			
			if(appErrorLogDao.insert(appErrorLog)==1){
				logger.info("app系统错误信息添加成功；结束时间："+ UtilDate.getDateFormatter());
			}else{
				logger.info("app系统错误信息添加失败；结束时间："+ UtilDate.getDateFormatter());
				
			}
		}
		
		logger.info("调用app系统错误信息添加结束；结束时间："+ UtilDate.getDateFormatter());
	}
	
	
	
	
	
}