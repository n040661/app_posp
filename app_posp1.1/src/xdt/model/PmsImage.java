package xdt.model;

import java.math.BigDecimal;

public class PmsImage implements java.io.Serializable{

    private static final long serialVersionUID = 2399447290508851468L;
    private String id;//id

    private String path;//图片保存路径

    private String merchantNum;//商户编号

    private String creationName;//创建登录名

    private String creationdate;//创建时间

    private BigDecimal removetag;//删除标记

    private BigDecimal state;//状态!!

    private BigDecimal flag;//1:商户资料图片 2：商户小票

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id == null ? null : id.trim();
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path == null ? null : path.trim();
    }

    public String getMerchantNum() {
        return merchantNum;
    }

    public void setMerchantNum(String merchantNum) {
        this.merchantNum = merchantNum == null ? null : merchantNum.trim();
    }

    public String getCreationName() {
        return creationName;
    }

    public void setCreationName(String creationName) {
        this.creationName = creationName == null ? null : creationName.trim();
    }

    public String getCreationdate() {
        return creationdate;
    }

    public void setCreationdate(String creationdate) {
        this.creationdate = creationdate == null ? null : creationdate.trim();
    }

    public BigDecimal getRemovetag() {
        return removetag;
    }

    public void setRemovetag(BigDecimal removetag) {
        this.removetag = removetag;
    }

    public BigDecimal getState() {
        return state;
    }

    public void setState(BigDecimal state) {
        this.state = state;
    }

    public BigDecimal getFlag() {
        return flag;
    }

    public void setFlag(BigDecimal flag) {
        this.flag = flag;
    }
}