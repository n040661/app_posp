package xdt.model;

public class MerchantNewsInfo {
    private String merchantNum;

    private String haveReadNews;

    private String oagentno;

    public String getMerchantNum() {
        return merchantNum;
    }

    public void setMerchantNum(String merchantNum) {
        this.merchantNum = merchantNum == null ? null : merchantNum.trim();
    }

    public String getHaveReadNews() {
        return haveReadNews;
    }

    public void setHaveReadNews(String haveReadNews) {
        this.haveReadNews = haveReadNews == null ? null : haveReadNews.trim();
    }

    public String getOagentno() {
        return oagentno;
    }

    public void setOagentno(String oagentno) {
        this.oagentno = oagentno == null ? null : oagentno.trim();
    }
}