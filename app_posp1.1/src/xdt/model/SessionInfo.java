package xdt.model;

/**
 * 用户存放session中要存入的信息
 * @author xiaomei
 *
 */
public class SessionInfo implements java.io.Serializable{

	private static final long serialVersionUID = -2854041054695326857L;
	public static String SESSIONINFO = "sessionInfo";
	
    private String id;//id

    private String mercId;//商户编号
    
    private String shortname;//商户简称
    
    private String mobilephone;//法人手机
    
    private String accNum; //账户
    
    private String externalId; //商户编号（对外）
    
    private Userinfo userinfo;//账号信息

    private String oAgentNo;// o单编号
    
	public SessionInfo(String id, String mercId, String shortname,
			String mobilephone, String accNum, String externalId,Userinfo userinfo, String oAgentNo) {
		super();
		this.id = id;
		this.mercId = mercId;
		this.shortname = shortname;
		this.mobilephone = mobilephone;
		this.accNum = accNum;
		this.externalId = externalId;
		this.userinfo = userinfo;
		this.oAgentNo = oAgentNo;
	}

	public String getAccNum() {
		return accNum;
	}

	public void setAccNum(String accNum) {
		this.accNum = accNum;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getMercId() {
		return mercId;
	}

	public void setMercId(String mercId) {
		this.mercId = mercId;
	}

	public String getShortname() {
		return shortname;
	}

	public void setShortname(String shortname) {
		this.shortname = shortname;
	}

	public String getMobilephone() {
		return mobilephone;
	}

	public void setMobilephone(String mobilephone) {
		this.mobilephone = mobilephone;
	}

	public String getExternalId() {
		return externalId;
	}

	public void setExternalId(String externalId) {
		this.externalId = externalId;
	}

	public Userinfo getUserinfo() {
		return userinfo;
	}

	public void setUserinfo(Userinfo userinfo) {
		this.userinfo = userinfo;
	}

	public String getoAgentNo() {
		return oAgentNo;
	}

	public void setoAgentNo(String oAgentNo) {
		this.oAgentNo = oAgentNo;
	}
	
}
