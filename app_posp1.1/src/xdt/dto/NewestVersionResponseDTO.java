package xdt.dto;

import xdt.model.AppVersion;

/**
 * 查询最新版本信息返回封装
 * User: Jeff
 * Date: 15-5-26
 * Time: 下午1:53
 * To change this template use File | Settings | File Templates.
 */
public class NewestVersionResponseDTO {
    private int retCode ;//返回码
    private String retMessage;//返回码信息
    private String versionTime; // 升级时间
    private String downUrl;  //下载地址
    private String versionId; //  版本序列号
    private String updateFlag;// 是否强制升级，0否，1是
    private String updatInfo;// 升级信息
    private String versionNo; //版本号

    public int getRetCode() {
        return retCode;
    }

    public void setRetCode(int retCode) {
        this.retCode = retCode;
    }

    public String getRetMessage() {
        return retMessage;
    }

    public void setRetMessage(String retMessage) {
        this.retMessage = retMessage;
    }

    public String getVersionTime() {
        return versionTime;
    }

    public void setVersionTime(String versionTime) {
        this.versionTime = versionTime;
    }

    public String getDownUrl() {
        return downUrl;
    }

    public void setDownUrl(String downUrl) {
        this.downUrl = downUrl;
    }

    public String getVersionId() {
        return versionId;
    }

    public void setVersionId(String versionId) {
        this.versionId = versionId;
    }

    public String getUpdateFlag() {
        return updateFlag;
    }

    public void setUpdateFlag(String updateFlag) {
        this.updateFlag = updateFlag;
    }

    public String getUpdatInfo() {
        return updatInfo;
    }

    public void setUpdatInfo(String updatInfo) {
        this.updatInfo = updatInfo;
    }

    public String getVersionNo() {
        return versionNo;
    }

    public void setVersionNo(String versionNo) {
        this.versionNo = versionNo;
    }
}
