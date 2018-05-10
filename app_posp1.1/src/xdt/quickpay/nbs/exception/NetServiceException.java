package xdt.quickpay.nbs.exception;
/**
 * 网络服务异常
 * 
 * @author Cover/ZhangHui
 */
@SuppressWarnings("serial")
public class NetServiceException extends BaseException {
	public NetServiceException(int code) {
		super(code);
	}

	public NetServiceException(int code, String message) {
		super(code, message);
	}

	public NetServiceException(String message) {
		super(message);
	}

	public NetServiceException(String message, Throwable cause) {
		super(message, cause);
	}

	public NetServiceException(String errorCode, String message) {
		super(errorCode, message);
	}
}
