package xdt.service;
import javax.servlet.http.HttpServletRequest;
public interface IPmsImageService {
	
	/**
	 * 保存实名认证上传的文件
	 * @param list
	 * @param session
	 * @return
	 */
	public String saveRealNameAuthenticationUploadFiles(Integer retCode,String retMessage,HttpServletRequest request) throws Exception;
	
	/**
	 * 保存实名认证上传的文件异常
	 * @return
	 * @throws Exception
	 */
	public String saveRealNameAuthenticationUploadFilesException(HttpServletRequest request) throws Exception;

}
