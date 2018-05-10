package xdt.common.security;

public class UserContext implements java.io.Serializable{

	public static final String USERCONTEXT = "USERCONTEXT";
	public static final int SESSION_STATE_NULLSESSION = 0; //没有握手
	public static final int SESSION_STATE_UNLOGIN = 1;//没有登录
	public static final int SESSION_STATE_LOGIN = 2;//登录
	public static final int SESSION_STATE_TIMEOUT = 3;//session超时
	private static final long serialVersionUID = 1013267476226517164L;

	public String sessionId;
	public String clientRandom;
	public String serverRandom;
	public String clientType; //android,iphone
	public String clientVersion; //app version
	public String devicesId; //phone uuid
	public int sessionState = SESSION_STATE_NULLSESSION; //session:1 is valid;2 is inValid
	public String tokenId = ""; //check repeat submit
	public String deviceModel = "";//设备型号
	public String deviceVersion = "";//设备系统版本号
	public String oAgentNo; //O单编号
	  
	public String getSessionId() {
		return sessionId;
	}
	public void setSessionId(String sessionId) {
		this.sessionId = sessionId;
	}
	public String getClientRandom() {
		return clientRandom;
	}
	public void setClientRandom(String clientRandom) {
		this.clientRandom = clientRandom;
	}
	public String getServerRandom() {
		return serverRandom;
	}
	public void setServerRandom(String serverRandom) {
		this.serverRandom = serverRandom;
	}
	public String getClientType() {
		return clientType;
	}
	public void setClientType(String clientType) {
		this.clientType = clientType;
	}
	public String getClientVersion() {
		return clientVersion;
	}
	public void setClientVersion(String clientVersion) {
		this.clientVersion = clientVersion;
	}
	public String getDevicesId() {
		return devicesId;
	}
	public void setDevicesId(String devicesId) {
		this.devicesId = devicesId;
	}
	public int getSessionState() {
		return sessionState;
	}
	public void setSessionState(int sessionState) {
		this.sessionState = sessionState;
	}
	public String getTokenId() {
		return tokenId;
	}
	public void setTokenId(String tokenId) {
		this.tokenId = tokenId;
	}
	public String getDeviceModel() {
		return deviceModel;
	}
	public void setDeviceModel(String deviceModel) {
		this.deviceModel = deviceModel;
	}
	public String getDeviceVersion() {
		return deviceVersion;
	}
	public void setDeviceVersion(String deviceVersion) {
		this.deviceVersion = deviceVersion;
	}
	public String getOAgentNo() {
		return oAgentNo;
	}
	public void setOAgentNo(String oAgentNo) {
		this.oAgentNo = oAgentNo;
	}
	
	
	  
	  

}
