package xdt.controller;
import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import xdt.model.SessionInfo;
import xdt.service.IPmsImageService;
import xdt.service.impl.PmsImageServiceImpl;

/**
 * 图片上传
 * @author lev12
 *
 */
@Controller
@RequestMapping("pmsImageAction")
public class PmsImageAction extends BaseAction{
	
	private IPmsImageService imageService;//图片上传服务层
	private Logger logger = Logger.getLogger(PmsImageAction.class);
	
	/**
	 * 保存上传的文件
	 * @param request
	 * @param response
	 * @throws IOException 
	 */
	public void saveRealNameAuthenticationUploadFiles(Integer retCode,String retMessage,HttpServletResponse response,HttpServletRequest request){
        try {
        	imageService = new PmsImageServiceImpl();
        	String obj = imageService.saveRealNameAuthenticationUploadFiles(retCode,retMessage,request);
        	response.setContentType("text/html;charset=UTF-8");
    		response.setHeader("progma","no-cache");
    		response.setHeader("Cache-Control","no-cache");
    		PrintWriter out = response.getWriter();
    		out.print(obj);
    		out.flush();
    		out.close();
		} catch (Exception e) {
			try {
				outPrint(response,imageService.saveRealNameAuthenticationUploadFilesException(request));
			} catch (IOException e1) {
				e1.printStackTrace();
			} catch (Exception e1) {
				e1.printStackTrace();
			}
			setSession(request.getSession(),request.getRemoteAddr(),true);
			logger.info("[app_exception]"+e.fillInStackTrace());
			e.printStackTrace();
		}
	}
}