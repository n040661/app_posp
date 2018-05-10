package xdt.model;


public class AgentInviteCode  {

    private String oagentno;

    private String invitecode;

    private String agentno;

    private String mercNum;

    private String status;

    private String batchno;

    private String createpeople;

    private String createtime;

    private String updatetime;

    public String getAgentno() {
        return agentno;
    }

    public void setAgentno(String agentno) {
        this.agentno = agentno == null ? null : agentno.trim();
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status == null ? null : status.trim();
    }

    public String getBatchno() {
        return batchno;
    }

    public void setBatchno(String batchno) {
        this.batchno = batchno == null ? null : batchno.trim();
    }

    public String getCreatepeople() {
        return createpeople;
    }

    public void setCreatepeople(String createpeople) {
        this.createpeople = createpeople == null ? null : createpeople.trim();
    }

    public String getCreatetime() {
        return createtime;
    }

    public void setCreatetime(String createtime) {
        this.createtime = createtime == null ? null : createtime.trim();
    }

    public String getOagentno() {
        return oagentno;
    }

    public void setOagentno(String oagentno) {
        this.oagentno = oagentno;
    }

    public String getInvitecode() {
        return invitecode;
    }

    public void setInvitecode(String invitecode) {
        this.invitecode = invitecode;
    }

    public String getMercNum() {
        return mercNum;
    }

    public void setMercNum(String mercNum) {
        this.mercNum = mercNum;
    }

    public String getUpdatetime() {
        return updatetime;
    }

    public void setUpdatetime(String updatetime) {
        this.updatetime = updatetime;
    }
}