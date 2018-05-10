package xdt.upload;
import java.io.File;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.FileUploadBase.SizeLimitExceededException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.log4j.Logger;
import org.apache.log4j.MDC;

import xdt.controller.PmsImageAction;
import xdt.model.PmsImage;
import xdt.model.SessionInfo;
public class FileUpload extends HttpServlet {
	private static Logger logger = Logger.getLogger(FileUpload.class);
	
	/**
	 * 文件上传
	 */
	@SuppressWarnings({ "unchecked"})
	public void doPost(HttpServletRequest request, HttpServletResponse response){
		PmsImageAction imageAction = new PmsImageAction();
		SessionInfo session = (SessionInfo)request.getSession().getAttribute(SessionInfo.SESSIONINFO);
		if(session!=null){
			MDC.put("ip",request.getRemoteAddr());
			MDC.put("session",request.getSession().getId().substring(0,10));
			MDC.put("mobilePhone",session.getMobilephone());
			logger.info("[upload]开始上传商户的实名认证文件了...");
		}else{
			imageAction.saveRealNameAuthenticationUploadFiles(13, "会话失效,请重新登录",response,request);
			return;
		}
		SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd");
		
		//父目录
//		String pPath = FileUtil.getDir(request, Common.UPLOAD_FILE_P_PATH);
		String pPath = Common.UPLOAD_FILE_P_PATH  + File.separator + sdf1.format(new Date());
		//临时目录
//		String dir = FileUtil.getDir(request, Common.UPLOAD_FILE_TEMP_PATH);
		String dir = Common.UPLOAD_FILE_TEMP_PATH;
		File tempPath = new File(dir);
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
		List<PmsImage> list = new ArrayList<PmsImage>();
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
				//上传的文件名称是 项目名称+upload文件夹+商户编号+年月日+6位随机数
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy.MM.dd");
				String mercId = "";String mobilePhone="";
				Random random = new Random();
				int number = random.nextInt(899999)+100000;//6位随机数
				if(session!=null){
					mercId = session.getMercId();//商户编号
					mobilePhone = session.getMobilephone();//手机号
				}else{
					imageAction.saveRealNameAuthenticationUploadFiles(13, "会话失效,请重新登录",response,request);
					return;
				}
				String time = sdf.format(new Date());//年月日
				String t_name = path.substring(path.lastIndexOf("\\") + 1);
				String sava_name = mobilePhone+"_"+time+"_"+number+"_"+t_name;
				//得到文件的扩展名(无扩展名时将得到全名)
				String t_ext = t_name.substring(t_name.lastIndexOf(".") + 1);
				//拒绝接受规定文件格式之外的文件类型
				int allowFlag = 0;
				int allowedExtCount = Common.UPLOAD_FILE_TYPE.length;
				for (;allowFlag < allowedExtCount; allowFlag++) {
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
						String u_name = pPath+ "/" + sava_name;
						//将文件存入tomcat中
						fileItem.write(new File(u_name));
						//文件上传成功 将信息放入list中
						PmsImage pmsImage = new PmsImage();
						String servicePath = Common.SERVICE_URL + File.separator + sdf1.format(new Date()) + "/" + sava_name;
						logger.info("[file_name]"+sava_name);
						SimpleDateFormat myFmt=new SimpleDateFormat("yyyy/MM/dd"); 
						pmsImage.setPath(servicePath);
						pmsImage.setMerchantNum(mercId);
						pmsImage.setCreationName(mobilePhone);
						pmsImage.setFlag(new BigDecimal(1));
						pmsImage.setCreationdate(myFmt.format(new Date()));
						list.add(pmsImage);
					}else{
						// 文件异常
						imageAction.saveRealNameAuthenticationUploadFiles(5, "文件存储路径异常",response,request);
						return;
					}

				} catch (Exception e) {
					logger.info("文件上传失败"+e.fillInStackTrace());
					imageAction.saveRealNameAuthenticationUploadFiles(10, "文件上传失败",response,request);
					e.printStackTrace();
			}
		}
		if(fileList.size()==list.size()){
			request.getSession().setAttribute("fileList", list);
			imageAction.saveRealNameAuthenticationUploadFiles(0, "文件上传成功",response,request);
			return;
		}
	} 
	
}