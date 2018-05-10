package xdt.upload;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import javax.servlet.http.HttpServletRequest;

public class FileUtil {
	
	/**
	 * 获取文件真实存储路径
	 * @param path
	 * @return
	 */
	public static String getSaveSrcPath(String path){
		SimpleDateFormat format = new SimpleDateFormat(Common.UPLOAD_FILE_PATH_DATE);
		String dd =format.format(new Date());
		return path+"/"+dd;
	}

	/**
	 * 获取绝对路径
	 * @param path
	 * @return
	 */
	@SuppressWarnings("deprecation")
	public static String getDir(HttpServletRequest request,String path){
		String str = "";
		if(path.subSequence(0, 1).equals("/") || path.subSequence(1, 2).equals(":")){
			str= path;
			str = str.replaceAll("\\", "/");
			return str;
		}else{
			str = request.getRealPath("/")+ path;
			str = str.replaceAll("\\\\", "/");
			return str;
		}
	}
	
	/**
	 * 判断文件夹是否存在
	 * @param file
	 * @return
	 */
	public static boolean isDir(File file){
		
		if(file == null){
			return false;
		}else if  (!file .exists()  && !file .isDirectory()){       
			return false;
		} else {  
			return true; 
		}  
	}
	
	/**
	 * 
	 * @param file
	 */
	public static void mkdir(File file){
		
		if(isDir(file) == true){
			
		}else{
			file.mkdirs();
		}
	}
}
