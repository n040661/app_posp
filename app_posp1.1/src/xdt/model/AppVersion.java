
package xdt.model;

/**
 * app版本号
 * User: Jeff
 * Date: 15-5-24
 * Time: 上午11:41
 * To change this template use File | Settings | File Templates.
 */
public class AppVersion {

	private Integer id;		//主键
	private String createtime;		//创建时间 YYYY-MM-DD HH:mm:SS
	private String status;		//是否启用，1：启用，0：失效
	private String description;		//更新内容描述
	private String version;		//版本号
    private String forceFlag;//是否强制更新  1：是 0：否
    private String clientType; //客户端类型 1：andoid 2：ios
    private String downUrl;   //下载地址
    private String versionCode; //版本序列表
    private String oAgentNo;//欧单编号
	public Integer getId() {
		return this.id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getCreatetime() {
		return this.createtime;
	}

	public void setCreatetime(String createtime) {
		this.createtime = createtime;
	}

	public String getStatus() {
		return this.status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getDescription() {
		return this.description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getVersion() {
		return this.version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

    public String getForceFlag() {
        return forceFlag;
    }

    public void setForceFlag(String forceFlag) {
        this.forceFlag = forceFlag;
    }

    public String getClientType() {
        return clientType;
    }

    public void setClientType(String clientType) {
        this.clientType = clientType;
    }

    public String getDownUrl() {
        return downUrl;
    }

    public void setDownUrl(String downUrl) {
        this.downUrl = downUrl;
    }

    public String getVersionCode() {
        return versionCode;
    }

    public void setVersionCode(String versionCode) {
        this.versionCode = versionCode;
    }

    public String getoAgentNo() {
        return oAgentNo;
    }

    public void setoAgentNo(String oAgentNo) {
        this.oAgentNo = oAgentNo;
    }
}

