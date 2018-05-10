package xdt.dto;
/**
 * 支付 签到响应
 * @author p
 *
 */
public class CreditPaymentSignResponseDTO {
	private String  theSecretKey;//主秘钥

	private String reservedPrivate;//62域 解密工作密钥mac pin 自定义域  22位

    private String zmChkVal;//主密钥校验值

    private String wkChkVal;//作密钥校验值
	
	private Integer retCode;// 信息编号

	private String retMessage;// 信息描述

    private String isNeedZMK; //是否需要下载主密钥。0-不需要，1-需要


    private String workKeyFormat;

	public String getWorkKeyFormat() {
		return workKeyFormat;
	}

	public void setWorkKeyFormat(String workKeyFormat) {
		this.workKeyFormat = workKeyFormat;
	}

	public String getTheSecretKey() {
		return theSecretKey;
	}

	public void setTheSecretKey(String theSecretKey) {
		this.theSecretKey = theSecretKey;
	}

	public String getReservedPrivate() {
		return reservedPrivate;
	}

	public void setReservedPrivate(String reservedPrivate) {
		this.reservedPrivate = reservedPrivate;
	}

	public Integer getRetCode() {
		return retCode;
	}

	public void setRetCode(Integer retCode) {
		this.retCode = retCode;
	}

	public String getRetMessage() {
		return retMessage;
	}

	public void setRetMessage(String retMessage) {
		this.retMessage = retMessage;
	}

    public String getZmChkVal() {
        return zmChkVal;
    }

    public void setZmChkVal(String zmChkVal) {
        this.zmChkVal = zmChkVal;
    }

    public String getWkChkVal() {
        return wkChkVal;
    }

    public void setWkChkVal(String wkChkVal) {
        this.wkChkVal = wkChkVal;
    }

    public String getNeedZMK() {
        return isNeedZMK;
    }

    public void setNeedZMK(String needZMK) {
        isNeedZMK = needZMK;
    }
}
