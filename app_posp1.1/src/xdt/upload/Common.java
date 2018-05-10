package xdt.upload;

public class Common {

	/**
	 * 支持上传的类型,配置文件读取
	 */
	public static String UPLOAD_FILE_TYPE_CONF = null;
	
	/**
	 * 支持上传的类型 集合
	 */
	public static String[] UPLOAD_FILE_TYPE = null;
	
	/**
	 * 限制上传单文件上限 ，单位字节，默认6M
	 */
	public static int UPLOAD_FILE_MAX_SIZE = 0;  
	
	/**
	 * 文件上传存储父目录，配置文件读取
	 */
	public static String  UPLOAD_FILE_P_PATH = null;
    
	/**
	 * 文件上传存储临时目录，配置文件读取
	 */
	public static String  UPLOAD_FILE_TEMP_PATH = null;
	
	/**
	 * 临时存放文件的内存大小，单位字节 ，默认4K
	 */
	public static int UPLOAD_FILE_MEM_TEMP_SIZE = 0; 
	
	/**
	 * 保存上传文件路径的日期格式部分  yyyyMMdd  使用/分割日期代表将根据年月日创建对应的子目录，默认yyyy/dd即可
	 */
	public static String UPLOAD_FILE_PATH_DATE = null;
	
	/**
	 * 服务器的访问路径
	 */
	public static String SERVICE_URL = null;
	/**
	 * 个人签名的路径
	 */   
	public static String AUTOGRAPH_URL = null;
	/**
	 * 消费签名图片存储父目录，配置文件读取
	 */
	public static String AUTOGRAPH_FILE_P_PATH = null;
	/**
	 * 消费签名图片存储临时文件，配置文件读取
	 */
	public static String AUTOGRAPH_FILE_TEMP_PATH = null;
	
	static{
		UPLOAD_FILE_TYPE_CONF = Config.getProPerties("upload.file.type", "jpg|jpeg|gif");
		
		UPLOAD_FILE_TYPE = UPLOAD_FILE_TYPE_CONF.split("\\|");
		
		UPLOAD_FILE_MAX_SIZE = Config.getProPerties("upload.file.max.size", 6144)*1024;
		
		UPLOAD_FILE_P_PATH = Config.getProPerties("upload.file.p.path", "fileupload");
		
		UPLOAD_FILE_P_PATH = UPLOAD_FILE_P_PATH.replaceAll("\\\\", "/");
		
		UPLOAD_FILE_TEMP_PATH = Config.getProPerties("upload.file.temp.path", "fileupload/temp");
		
		UPLOAD_FILE_TEMP_PATH = UPLOAD_FILE_TEMP_PATH.replaceAll("\\\\", "/");
		
		UPLOAD_FILE_MEM_TEMP_SIZE = Config.getProPerties("upload.file.mem.temp.size", 4*1024);
		
		UPLOAD_FILE_PATH_DATE  = Config.getProPerties("upload.file.path.date", "yyyy/MM");
		
		SERVICE_URL = Config.getProPerties("service.url","");
		
		AUTOGRAPH_URL = Config.getProPerties("autograph.url","");
		
		AUTOGRAPH_FILE_P_PATH = Config.getProPerties("autograph.file.p.path", "autograph");
		
		AUTOGRAPH_FILE_TEMP_PATH = Config.getProPerties("autograph.file.temp.path", "autograph/temp");
		
	}
}