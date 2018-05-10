package xdt.dto;

import xdt.model.LoginMsgModel;
import xdt.model.PmsAppBusinessConfig;

import java.util.List;
import java.util.Map;

/**
 * 商户登录接口响应
 * 
 * @author lev12
 */
public class MerchantLoginResponseDTO {

	private String userName; // 登录名

	private String shortName; // 商户名称

	private String attestationSign; // 实民认标记，1末认证，2已认证

	private Integer retCode;// 信息编号

	private String retMessage;// 信息描述

	private String status; // 审核状态 1:app默认审核成功 2：人工审核成功

	private List<PmsAppBusinessConfig> list; // 业务配置列表

	private String backReason; // 打回原因

	private String deviceStatus;// 设备状态

	private String roleId;// 0主账户1收银员

	private MerPayChannel merPayChannel;// 用户支付方式

    private String isRead;//是否有末读消息标记（0：有末读消息；1：已全部读过）
    
    private Map<String,PmsAppBusinessConfig> map; // 业务配置列表

    private LoginMsgModel loginMsg;//登录时的提示信息
    
    private String isGPRS;//是否需要GPRS（0：需要GPRS；1：不需要GPRS）
    
	public String getIsRead() {
		return isRead;
	}

	public void setIsRead(String isRead) {
		this.isRead = isRead;
	}

	public String getIsGPRS() {
		return isGPRS;
	}

	public void setIsGPRS(String isGPRS) {
		this.isGPRS = isGPRS;
	}

	public Map<String, PmsAppBusinessConfig> getMap() {
		return map;
	}

	public void setMap(Map<String, PmsAppBusinessConfig> map) {
		this.map = map;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getShortName() {
		return shortName;
	}

	public void setShortName(String shortName) {
		this.shortName = shortName;
	}

	public String getAttestationSign() {
		return attestationSign;
	}

	public void setAttestationSign(String attestationSign) {
		this.attestationSign = attestationSign;
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

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public List<PmsAppBusinessConfig> getList() {
		return list;
	}

	public void setList(List<PmsAppBusinessConfig> list) {
		this.list = list;
	}

	public String getBackReason() {
		return backReason;
	}

	public void setBackReason(String backReason) {
		this.backReason = backReason;
	}

	public String getDeviceStatus() {
		return deviceStatus;
	}

	public void setDeviceStatus(String deviceStatus) {
		this.deviceStatus = deviceStatus;
	}

	public String getRoleId() {
		return roleId;
	}

	public void setRoleId(String roleId) {
		this.roleId = roleId;
	}

	public MerPayChannel getMerPayChannel() {
		return merPayChannel;
	}

	public void setMerPayChannel(MerPayChannel merPayChannel) {
		this.merPayChannel = merPayChannel;
	}

    public String getRead() {
        return isRead;
    }

    public void setRead(String read) {
        isRead = read;
    }

    public LoginMsgModel getLoginMsg() {
        return loginMsg;
    }

    public void setLoginMsg(LoginMsgModel loginMsg) {
        this.loginMsg = loginMsg;
    }
}
