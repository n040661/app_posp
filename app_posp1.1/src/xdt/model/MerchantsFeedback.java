package xdt.model;

import java.math.BigDecimal;

public class MerchantsFeedback {
    private BigDecimal id;//id

    private String userId;//商户id（对应PMS_BUSINESS_INFO表的MERC_ID)

    private String opinon;//意见

    private String creationTime;//创建时间

    private String revertTime;//回复时间

    private String revertOpinion;//回复意见

    private String appvaise;//回复评价

    private String reply;//是否回复（0：未回复  1:已回复）

    private String userType;//登录方式（1：手机登陆用户 2：...）

    private String remarks;//备注信息（管理员操作）

    private String revertMan;//回复人姓名 	

    private String revertManId;//回复人id（ 对应T_SYS_USER(id)）
    private String oAgentNo;//O单编号


    public BigDecimal getId() {
        return id;
    }

    public void setId(BigDecimal id) {
        this.id = id;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId == null ? null : userId.trim();
    }

    public String getOpinon() {
        return opinon;
    }

    public void setOpinon(String opinon) {
        this.opinon = opinon == null ? null : opinon.trim();
    }

    public String getCreationTime() {
        return creationTime;
    }

    public void setCreationTime(String creationTime) {
        this.creationTime = creationTime == null ? null : creationTime.trim();
    }

    public String getRevertTime() {
        return revertTime;
    }

    public void setRevertTime(String revertTime) {
        this.revertTime = revertTime == null ? null : revertTime.trim();
    }

    public String getRevertOpinion() {
        return revertOpinion;
    }

    public void setRevertOpinion(String revertOpinion) {
        this.revertOpinion = revertOpinion == null ? null : revertOpinion.trim();
    }

    public String getAppvaise() {
        return appvaise;
    }

    public void setAppvaise(String appvaise) {
        this.appvaise = appvaise == null ? null : appvaise.trim();
    }


    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks == null ? null : remarks.trim();
    }

    public String getRevertMan() {
        return revertMan;
    }

    public void setRevertMan(String revertMan) {
        this.revertMan = revertMan == null ? null : revertMan.trim();
    }

	public String getReply() {
		return reply;
	}

	public void setReply(String reply) {
		this.reply = reply;
	}

	public String getUserType() {
		return userType;
	}

	public void setUserType(String userType) {
		this.userType = userType;
	}

	public String getRevertManId() {
		return revertManId;
	}

	public void setRevertManId(String revertManId) {
		this.revertManId = revertManId;
	}

	public String getoAgentNo() {
		return oAgentNo;
	}

	public void setoAgentNo(String oAgentNo) {
		this.oAgentNo = oAgentNo;
	}

}