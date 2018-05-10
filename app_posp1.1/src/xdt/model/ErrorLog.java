package xdt.model;

import java.math.BigDecimal;
import java.util.Date;

public class ErrorLog {
	private BigDecimal id; //id

    private String phoneNo; //手机号

    private String pospsn; //流水号

    private Date errorDate; //错误时间

    private String errorNo;//错误码编号

    public BigDecimal getId() {
        return id;
    }

    public void setId(BigDecimal id) {
        this.id = id;
    }

    public String getPhoneNo() {
        return phoneNo;
    }

    public void setPhoneNo(String phoneNo) {
        this.phoneNo = phoneNo == null ? null : phoneNo.trim();
    }

    public String getPospsn() {
        return pospsn;
    }

    public void setPospsn(String pospsn) {
        this.pospsn = pospsn == null ? null : pospsn.trim();
    }

    public Date getErrorDate() {
        return errorDate;
    }

    public void setErrorDate(Date errorDate) {
        this.errorDate = errorDate;
    }

    public String getErrorNo() {
        return errorNo;
    }

    public void setErrorNo(String errorNo) {
        this.errorNo = errorNo == null ? null : errorNo.trim();
    }
}