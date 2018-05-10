package xdt.dto.yf;

/**
 * 文件下载
 * @author r.g
 *  接口版本号		version			String	是	固定值：1.0.0
	商户代码			merchantId	String	是	商户代码必须为整数,且长度须在1-24之间
	文件类型			fileType			String	是	01-代付结果文件  02-	退票结果文件
	文件名称			fileName		String	是	需要下载的文件名称
	自定义保留域		misc				String	否	
 */
public class FileDownReq extends BaseReq{
	/**
	 * 
	 */
	private static final long serialVersionUID = 8189779048084235019L;
	private String version;
	private String merchantId;
	private String fileType;
	private String fileName;
	private String misc;
	public String getVersion() {
		return version;
	}
	public void setVersion(String version) {
		this.version = version;
	}
	public String getMerchantId() {
		return merchantId;
	}
	public void setMerchantId(String merchantId) {
		this.merchantId = merchantId;
	}
	public String getFileType() {
		return fileType;
	}
	public void setFileType(String fileType) {
		this.fileType = fileType;
	}
	public String getFileName() {
		return fileName;
	}
	public void setFileName(String fileName) {
		this.fileName = fileName;
	}
	public String getMisc() {
		return misc;
	}
	public void setMisc(String misc) {
		this.misc = misc;
	}
	@Override
	public String toString() {
		return "FileDownReq [version=" + version + ", merchantId=" + merchantId + ", fileType=" + fileType + ", fileName=" + fileName + ", misc=" + misc + "]";
	}
	
}
