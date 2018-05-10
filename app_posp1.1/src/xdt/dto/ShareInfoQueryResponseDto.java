package xdt.dto;

/**
 * 分享消息返回
 * User: Jeff
 * Date: 15-12-11
 * Time: 下午3:03
 * To change this template use File | Settings | File Templates.
 */
public class ShareInfoQueryResponseDto {
    private Integer retCode;// 信息编号

    private String retMessage;// 信息描述

    private String shareTitle;

    private String shareContent;

    private String shareURL;

    private String shareImages;

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

    public String getShareTitle() {
        return shareTitle;
    }

    public void setShareTitle(String shareTitle) {
        this.shareTitle = shareTitle;
    }

    public String getShareContent() {
        return shareContent;
    }

    public void setShareContent(String shareContent) {
        this.shareContent = shareContent;
    }

    public String getShareURL() {
        return shareURL;
    }

    public void setShareURL(String shareURL) {
        this.shareURL = shareURL;
    }

    public String getShareImages() {
        return shareImages;
    }

    public void setShareImages(String shareImages) {
        this.shareImages = shareImages;
    }
}
