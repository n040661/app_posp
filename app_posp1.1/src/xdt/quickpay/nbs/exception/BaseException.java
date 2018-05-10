package xdt.quickpay.nbs.exception;
/**
 * 异常基础类
 * 
 * @author Cover/ZhangHui
 */
@SuppressWarnings("serial")
public class BaseException extends RuntimeException {
	private int code;
	private String errorCode;

	public BaseException() {
		super();
	}

	public BaseException(int code) {
		super();
		this.code = code;
	}

	public BaseException(String message) {
		super(message);
	}

	public BaseException(int code, String message) {
		super(message);
		this.code = code;
	}

	public BaseException(String errorCode, String message) {
		super(message);
		this.errorCode = errorCode;
	}

	public BaseException(String message, Throwable cause) {
		super(message, cause);
	}

	public int getCode() {
		return code;
	}

	public void setCode(int code) {
		this.code = code;
	}

	public String getErrorCode() {
		return errorCode;
	}

	public void setErrorCode(String errorCode) {
		this.errorCode = errorCode;
	}

}
