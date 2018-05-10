package xdt.service.impl;
import javax.servlet.http.HttpServletRequest;


import org.apache.log4j.Logger;
import org.springframework.stereotype.Service;

import xdt.dao.IPmsImageDao;
import xdt.dao.impl.PmsImageDaoImpl;
import xdt.dto.UploadFilesResponseDTO;
import xdt.model.SessionInfo;
import xdt.preutil.HttpEncode;
import xdt.service.IPmsImageService;

import com.google.gson.Gson;
@Service("pmsImageService")
public class PmsImageServiceImpl extends BaseServiceImpl implements IPmsImageService {
	private Logger logger = Logger.getLogger(PmsImageServiceImpl.class);
	
	/**
	 * 保存实名认证上传的文件
	 */
	public String saveRealNameAuthenticationUploadFiles(Integer retCode,String retMessage,HttpServletRequest request) throws Exception{
		SessionInfo sessionInfo = (SessionInfo)request.getSession().getAttribute(SessionInfo.SESSIONINFO);
		String phone = "mobilePhone";
		String sessionId = "session";
		if(null!=sessionInfo){
			phone = sessionInfo.getMobilephone();
			sessionId = request.getSession().getId();
		}
		setSession(request.getRemoteAddr(),sessionId,phone);
		if(retCode!=13){
			if(retCode!=0){ //文件未上传
				IPmsImageDao imageDao = new PmsImageDaoImpl();
				String errorCode = "";
				if(retCode==2){
					errorCode = "2103"; //上传文件不能为空
				}else if(retCode==10){
					errorCode = "2104"; //文件上传失败
				}else{
					errorCode = "2105"; //上传文件不正确
				}
				imageDao.insertErrorLog(phone,errorCode);
			}
		}
		setSession(request.getRemoteAddr(),request.getSession().getId(),phone);
		Gson gson = new Gson();
		HttpEncode encode = new HttpEncode();
		UploadFilesResponseDTO responseData = new UploadFilesResponseDTO();
		responseData.setRetCode(retCode);
		responseData.setRetMessage(retMessage);
		
		Object authPath = request.getAttribute("authPath");
		
		if(authPath != null){
			responseData.setAuthPath(authPath.toString());
		}
		String jsonString = gson.toJson(responseData);
		logger.info("[app_rsp]"+createJson(responseData));
		return jsonString;
	}

	/**
	 * 保存实名认证上传的文件异常
	 */
	@Override
	public String saveRealNameAuthenticationUploadFilesException(HttpServletRequest request)throws Exception {
		UploadFilesResponseDTO responseData = new UploadFilesResponseDTO();
		responseData.setRetCode(100);
		responseData.setRetMessage("系统异常");
		insertAppLogs(((SessionInfo)request.getSession().getAttribute(SessionInfo.SESSIONINFO)).getMobilephone(),"","2096");
		return createJsonString(responseData);
	}
}