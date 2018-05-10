package xdt.model;

import java.math.BigDecimal;

public class ViewKyChannelInfo {
    private String channelNum;//渠道编号

    private String url;//渠道地址

    private BigDecimal priority;//优先级

    private String businessnum;//业务编号

    private BigDecimal isused;//是否可用
    
    private String callbackurl; //回调地址
    
    private String channelNO; //第三方账户（eg:欧飞）
    
    private String channelPwd; //第三方密码
    
    private String version; //第三方版本
    
    private String oAgentNo;//O单编号

    public String getCallbackurl() {
		return callbackurl;
	}

	public void setCallbackurl(String callbackurl) {
		this.callbackurl = callbackurl == null ? null : callbackurl.trim();
	}

	public String getChannelNum() {
        return channelNum;
    }

    public void setChannelNum(String channelNum) {
        this.channelNum = channelNum == null ? null : channelNum.trim();
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url == null ? null : url.trim();
    }

    public BigDecimal getPriority() {
        return priority;
    }

    public void setPriority(BigDecimal priority) {
        this.priority = priority;
    }

    public String getBusinessnum() {
        return businessnum;
    }

    public void setBusinessnum(String businessnum) {
        this.businessnum = businessnum == null ? null : businessnum.trim();
    }

    public BigDecimal getIsused() {
        return isused;
    }

    public void setIsused(BigDecimal isused) {
        this.isused = isused;
    }

	public String getChannelNO() {
		return channelNO;
	}

	public void setChannelNO(String channelNO) {
		this.channelNO = channelNO;
	}

	public String getChannelPwd() {
		return channelPwd;
	}

	public void setChannelPwd(String channelPwd) {
		this.channelPwd = channelPwd;
	}

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public String getoAgentNo() {
		return oAgentNo;
	}

	public void setoAgentNo(String oAgentNo) {
		this.oAgentNo = oAgentNo;
	}
    
}