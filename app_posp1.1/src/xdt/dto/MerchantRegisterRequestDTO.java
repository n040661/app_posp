package xdt.dto;

/**
 * 商户注册接口请求
 * @author Jeff
 */
public class MerchantRegisterRequestDTO {
	
	private String mobilePhone; //手机号码
	
	private String passWord; //登录密码
	
    private String validCode;//短信验证码

    private String parentAgentNum;//要绑定到的代理商编号
    
    private String oAgentNo; //欧单编号

    private String altLat;//经纬度

    private String gpsAddress;//gps获取的地址信息(中文)

    private String inviteCode;//邀请码
    
	public String getMobilePhone() {
		return mobilePhone;
	}
	public void setMobilePhone(String mobilePhone) {
		this.mobilePhone = mobilePhone;
	}

    public String getPassWord() {
        return passWord;
    }

    public void setPassWord(String passWord) {
        this.passWord = passWord;
    }

    public String getValidCode() {
        return validCode;
    }

    public void setValidCode(String validCode) {
        this.validCode = validCode;
    }

    public String getParentAgentNum() {
        return parentAgentNum;
    }

    public void setParentAgentNum(String parentAgentNum) {
        this.parentAgentNum = parentAgentNum;
    }
	public String getoAgentNo() {
		return oAgentNo;
	}
	public void setoAgentNo(String oAgentNo) {
		this.oAgentNo = oAgentNo;
	}

    public String getAltLat() {
        return altLat;
    }

    public void setAltLat(String altLat) {
        this.altLat = altLat;
    }

    public String getGpsAddress() {
        return gpsAddress;
    }

    public void setGpsAddress(String gpsAddress) {
        this.gpsAddress = gpsAddress;
    }

    public String getInviteCode() {
        return inviteCode;
    }

    public void setInviteCode(String inviteCode) {
        this.inviteCode = inviteCode;
    }
}
