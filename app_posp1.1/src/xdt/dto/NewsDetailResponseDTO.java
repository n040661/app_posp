package xdt.dto;

/**
 * Created with IntelliJ IDEA.
 * User: Jeff
 * Date: 15-12-14
 * Time: 上午10:19
 * To change this template use File | Settings | File Templates.
 */
public class NewsDetailResponseDTO {
    private Integer retCode;// 信息编号

    private String retMessage;// 信息描述

    private String newsType; //消息类型 (0:普通文本显示，1：h5显示)

    private String newsId;

    private String newsTitle;

    private String newsDate;

    private String isRead;

    private String newsContent;

    private String imagesURL;

    private String h5URL;

    private String originalAddr;//消息来源


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

    public String getNewsType() {
        return newsType;
    }

    public void setNewsType(String newsType) {
        this.newsType = newsType;
    }

    public String getNewsId() {
        return newsId;
    }

    public void setNewsId(String newsId) {
        this.newsId = newsId;
    }

    public String getNewsTitle() {
        return newsTitle;
    }

    public void setNewsTitle(String newsTitle) {
        this.newsTitle = newsTitle;
    }

    public String getNewsDate() {
        return newsDate;
    }

    public void setNewsDate(String newsDate) {
        this.newsDate = newsDate;
    }

    public String getRead() {
        return isRead;
    }

    public void setRead(String read) {
        isRead = read;
    }

    public String getNewsContent() {
        return newsContent;
    }

    public void setNewsContent(String newsContent) {
        this.newsContent = newsContent;
    }

    public String getImagesURL() {
        return imagesURL;
    }

    public void setImagesURL(String imagesURL) {
        this.imagesURL = imagesURL;
    }

    public String getH5URL() {
        return h5URL;
    }

    public void setH5URL(String h5URL) {
        this.h5URL = h5URL;
    }

    public String getOriginalAddr() {
        return originalAddr;
    }

    public void setOriginalAddr(String originalAddr) {
        this.originalAddr = originalAddr;
    }
}
