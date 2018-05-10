package xdt.model;

import java.math.BigDecimal;

public class NewsInfo {
    private BigDecimal newsId;

    private String newsTitle;

    private String newsType;

    private String newsDate;

    private String iamgesUrl;

    private String h5Url;

    private String orginalAddr;

    private String status;

    private BigDecimal sortNum;

    private String oagentno;

    private String newsContent;

    private String isRead;

    public BigDecimal getNewsId() {
        return newsId;
    }

    public void setNewsId(BigDecimal newsId) {
        this.newsId = newsId;
    }

    public String getNewsTitle() {
        return newsTitle;
    }

    public void setNewsTitle(String newsTitle) {
        this.newsTitle = newsTitle == null ? null : newsTitle.trim();
    }

    public String getNewsType() {
        return newsType;
    }

    public void setNewsType(String newsType) {
        this.newsType = newsType == null ? null : newsType.trim();
    }

    public String getNewsDate() {
        return newsDate;
    }

    public void setNewsDate(String newsDate) {
        this.newsDate = newsDate == null ? null : newsDate.trim();
    }

    public String getIamgesUrl() {
        return iamgesUrl;
    }

    public void setIamgesUrl(String iamgesUrl) {
        this.iamgesUrl = iamgesUrl == null ? null : iamgesUrl.trim();
    }

    public String getH5Url() {
        return h5Url;
    }

    public void setH5Url(String h5Url) {
        this.h5Url = h5Url == null ? null : h5Url.trim();
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status == null ? null : status.trim();
    }

    public BigDecimal getSortNum() {
        return sortNum;
    }

    public void setSortNum(BigDecimal sortNum) {
        this.sortNum = sortNum;
    }

    public String getOagentno() {
        return oagentno;
    }

    public void setOagentno(String oagentno) {
        this.oagentno = oagentno == null ? null : oagentno.trim();
    }

    public String getNewsContent() {
        return newsContent;
    }

    public void setNewsContent(String newsContent) {
        this.newsContent = newsContent == null ? null : newsContent.trim();
    }

    public String getRead() {
        return isRead;
    }

    public void setRead(String read) {
        isRead = read;
    }

    public String getOrginalAddr() {
        return orginalAddr;
    }

    public void setOrginalAddr(String orginalAddr) {
        this.orginalAddr = orginalAddr;
    }
}