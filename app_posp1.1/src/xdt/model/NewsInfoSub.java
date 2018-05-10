package xdt.model;

/**
 * 用于返回前台的消息对象
 * User: Jeff
 * Date: 15-12-11
 * Time: 下午5:32
 * To change this template use File | Settings | File Templates.
 */
public class NewsInfoSub {
    String newsType;
    String newsTitle;
    String newsId;
    String newsDate;
    String isRead;

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

    public String getNewsTitle() {
        return newsTitle;
    }

    public void setNewsTitle(String newsTitle) {
        this.newsTitle = newsTitle;
    }
}
