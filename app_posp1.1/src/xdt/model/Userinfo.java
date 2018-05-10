package xdt.model;

import java.util.Date;

public class Userinfo implements java.io.Serializable{
    private static final long serialVersionUID = 3296395773044143136L;
    private Long id;

    private String loginName;

    private String loginPwd;

    private Object trueName;

    private Long roleId;

    private String mobileno;

    private Object email;

    private Date pwdDate;

    private Long userStatus;

    private String merchantId;

    private String oAgentNo;//欧单编号

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getLoginName() {
        return loginName;
    }

    public void setLoginName(String loginName) {
        this.loginName = loginName == null ? null : loginName.trim();
    }

    public String getLoginPwd() {
        return loginPwd;
    }

    public void setLoginPwd(String loginPwd) {
        this.loginPwd = loginPwd == null ? null : loginPwd.trim();
    }

    public Object getTrueName() {
        return trueName;
    }

    public void setTrueName(Object trueName) {
        this.trueName = trueName;
    }

    public Long getRoleId() {
        return roleId;
    }

    public void setRoleId(Long roleId) {
        this.roleId = roleId;
    }

    public String getMobileno() {
        return mobileno;
    }

    public void setMobileno(String mobileno) {
        this.mobileno = mobileno == null ? null : mobileno.trim();
    }

    public Object getEmail() {
        return email;
    }

    public void setEmail(Object email) {
        this.email = email;
    }

    public Date getPwdDate() {
        return pwdDate;
    }

    public void setPwdDate(Date pwdDate) {
        this.pwdDate = pwdDate;
    }

    public Long getUserStatus() {
        return userStatus;
    }

    public void setUserStatus(Long userStatus) {
        this.userStatus = userStatus;
    }

    public String getMerchantId() {
        return merchantId;
    }

    public void setMerchantId(String merchantId) {
        this.merchantId = merchantId == null ? null : merchantId.trim();
    }

    public String getoAgentNo() {
        return oAgentNo;
    }

    public void setoAgentNo(String oAgentNo) {
        this.oAgentNo = oAgentNo;
    }
}