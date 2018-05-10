package xdt.upload;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.FileUploadBase.SizeLimitExceededException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.log4j.Logger;
import org.apache.log4j.MDC;

import xdt.controller.PmsImageAction;
import xdt.model.PmsAppTransInfo;
import xdt.model.SessionInfo;
public class FileAutograph extends HttpServlet {
	/**
	 * 文件上传
	 */
	private static Logger logger = Logger.getLogger(FileAutograph.class);
	private SimpleDateFormat dtoSdf = new SimpleDateFormat("yyyyMMddHHmmss"); //第三方要求的时间格式
	@SuppressWarnings("unchecked")
	public void doPost(HttpServletRequest request, HttpServletResponse response){
		PmsImageAction imageAction = new PmsImageAction();
		SessionInfo session = (SessionInfo)request.getSession().getAttribute(SessionInfo.SESSIONINFO);
		if(session!=null){
			MDC.put("ip",request.getRemoteAddr());
			MDC.put("session",request.getSession().getId().substring(0,10));
			MDC.put("mobilePhone",session.getMobilephone());
			logger.info("[upload]开始上传商户的消费签名图片了...");
		}else{
			imageAction.saveRealNameAuthenticationUploadFiles(13, "会话失效,请重新登录",response,request);
			return;
		}
		SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd");
		
		//父目录
//		String pPath = FileUtil.getDir(request, Common.AUTOGRAPH_FILE_P_PATH);
		String pPath = Common.AUTOGRAPH_FILE_P_PATH  + File.separator + sdf1.format(new Date());
		//临时目录
//		FileUtil.getDir(request, Common.AUTOGRAPH_FILE_TEMP_PATH);
		String tPath = Common.AUTOGRAPH_FILE_TEMP_PATH;
		File tempPath = new File(tPath);
		FileUtil.mkdir(tempPath);
		//源文件存储目录
		File saveSrcFile = new File(pPath);
		FileUtil.mkdir(saveSrcFile);
		//实例化一个硬盘文件工厂,用来配置上传组件ServletFileUpload
		DiskFileItemFactory dfif = new DiskFileItemFactory();
		dfif.setSizeThreshold(Common.UPLOAD_FILE_MEM_TEMP_SIZE);
		//设置存放临时文件的目录,web根目录下的ImagesUploadTemp目录
		dfif.setRepository(tempPath);
		//用以上工厂实例化上传组件
		ServletFileUpload sfu = new ServletFileUpload(dfif);
		sfu.setHeaderEncoding("UTF-8");
		//设置最大上传尺寸
		sfu.setSizeMax(Common.UPLOAD_FILE_MAX_SIZE);
		List fileList = null;
		PmsAppTransInfo pmsAppTransInfo = new PmsAppTransInfo();
		try {
			fileList = sfu.parseRequest(request);
		} catch (FileUploadException e) {
			if (e instanceof SizeLimitExceededException) {
				imageAction.saveRealNameAuthenticationUploadFiles(1, "上传文件超过限制大小",response,request);
				return;
			}
			logger.info("文件上传失败"+e.fillInStackTrace());
			imageAction.saveRealNameAuthenticationUploadFiles(10, "文件上传失败",response,request);
			e.printStackTrace();
		}
		//没有文件上传
		if (fileList == null || fileList.size() == 0) {
			imageAction.saveRealNameAuthenticationUploadFiles(2, "上传文件不能为空",response,request);
			return;
		}
		//得到所有上传的文件
		Iterator fileItr = fileList.iterator();
		while (fileItr.hasNext()) {
			FileItem fileItem = null;
			String path = null;
			long size = 0;
			fileItem = (FileItem) fileItr.next();
			if (fileItem == null || fileItem.isFormField()) {
				continue;
			}
			path = fileItem.getName();//文件名称
			size = fileItem.getSize();//文件大小
			if ("".equals(path) || size == 0) {
				imageAction.saveRealNameAuthenticationUploadFiles(3, "禁止上传空文件",response,request);
				return;
			}
			//上传的文件名称是订单号
			if(session!=null){
				path = fileItem.getName();//文件名称
			}else{
				imageAction.saveRealNameAuthenticationUploadFiles(13, "会话失效,请重新登录",response,request);
				return;
			}
			String t_name = path.substring(path.lastIndexOf("\\") + 1);
			String nano = System.nanoTime()+"";
			String save_naString=dtoSdf.format(new Date()).substring(2)+nano.substring(nano.length()-8);
			String sava_name =save_naString +"_"+session.getId();
			String t_ext = t_name.substring(t_name.lastIndexOf(".") + 1);
			//拒绝接受规定文件格式之外的文件类型
			int allowFlag = 0;
			int allowedExtCount = Common.UPLOAD_FILE_TYPE.length;
			for (;allowFlag <allowedExtCount; allowFlag++) {
				if (Common.UPLOAD_FILE_TYPE[allowFlag].equalsIgnoreCase(t_ext))
					break;
			}
			if (allowFlag == allowedExtCount) {
				String msg = "不支持的上传文件格式 ，仅支持："+Common.UPLOAD_FILE_TYPE_CONF.replaceAll("|", "");
				imageAction.saveRealNameAuthenticationUploadFiles(4, msg,response,request);
				return;
			}
			try {
				if(FileUtil.isDir(saveSrcFile)&&FileUtil.isDir(tempPath)){
					//保存的最终文件完整路径
					String u_name = pPath + "/" + sava_name + "." + t_ext;
					//将文件存入tomcat中
					fileItem.write(new File(u_name));
					//文件上传成功 将信息放入list中
					String servicePath = Common.AUTOGRAPH_URL + File.separator + sdf1.format(new Date()) + "/" + sava_name + "." +t_ext;
					logger.info("[file_name]"+sava_name + "." +t_ext);
					pmsAppTransInfo.setUrl(servicePath);
					
					request.setAttribute("authPath", servicePath);
					
					pmsAppTransInfo.setOrderid(save_naString);//交易号
				}else{
					// 文件异常
					imageAction.saveRealNameAuthenticationUploadFiles(5, "文件存储路径异常",response,request);
					return;
			}} catch (Exception e) {
				logger.info("文件上传失败"+e.fillInStackTrace());
				imageAction.saveRealNameAuthenticationUploadFiles(10, "文件上传失败",response,request);
				e.printStackTrace();
			}}
		if(pmsAppTransInfo!=null){
			HttpSession session2 = request.getSession();
			session2.setAttribute("fileList", pmsAppTransInfo);
			imageAction.saveRealNameAuthenticationUploadFiles(0, "文件上传成功",response,request);
			return;
		}} 
}