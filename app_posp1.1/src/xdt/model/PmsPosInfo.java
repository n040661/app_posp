package xdt.model;

import java.math.BigDecimal;
/**
 * PMS_POS_INFO(POS基本信息表)
 * @author p
 *
 */
public class PmsPosInfo {
    private BigDecimal id;//PK

    private String serialno;//pos现实终端序号

    private String storagestatus;//库存状态

    private String manuno;//厂商编号

    private BigDecimal posmodel;//pos型号id

    private String outdate;//公司出库时间

    private String posstatus;//设备状态1-正常 2-报修 3-报废 4-丢失

    private String batchinno;//入库批号

    private String batchoutno;//公司出库批号

    private String postype;//终端序列号第一位 A  专用终端    B  手机客户端    C  OTA STK卡    D  识读卡    E  PC 协同工作客户端

    private String usepersion;//领用人

    private String usedepartment;//领用部门

    private String outmercode;//出库代理商编号

    private String posmold;//1 自备集 2 他被集

    private String outindate;//公司入库时间

    private String batchoutinno;//公司入库批号

    private String outinpersion;//公司入库人

    private String outpersion;//公司出库人

    private String outindepartment;//公司入库部门

    private String outdepartment;//公司出库部门

    private String agentNumber;//接受库存的一级代理

    private Short usestatus;//终端使用状态 0 未使用， 1 已使用

    private BigDecimal companyId;//公司id

    private String agentbatchinno;//代理入库批次

    private String agentindate;//代理入库时间

    public BigDecimal getId() {
        return id;
    }

    public void setId(BigDecimal id) {
        this.id = id;
    }

    public String getSerialno() {
        return serialno;
    }

    public void setSerialno(String serialno) {
        this.serialno = serialno == null ? null : serialno.trim();
    }

    public String getStoragestatus() {
        return storagestatus;
    }

    public void setStoragestatus(String storagestatus) {
        this.storagestatus = storagestatus == null ? null : storagestatus.trim();
    }

    public String getManuno() {
        return manuno;
    }

    public void setManuno(String manuno) {
        this.manuno = manuno == null ? null : manuno.trim();
    }

    public BigDecimal getPosmodel() {
        return posmodel;
    }

    public void setPosmodel(BigDecimal posmodel) {
        this.posmodel = posmodel;
    }

    public String getOutdate() {
        return outdate;
    }

    public void setOutdate(String outdate) {
        this.outdate = outdate == null ? null : outdate.trim();
    }

    public String getPosstatus() {
        return posstatus;
    }

    public void setPosstatus(String posstatus) {
        this.posstatus = posstatus == null ? null : posstatus.trim();
    }

    public String getBatchinno() {
        return batchinno;
    }

    public void setBatchinno(String batchinno) {
        this.batchinno = batchinno == null ? null : batchinno.trim();
    }

    public String getBatchoutno() {
        return batchoutno;
    }

    public void setBatchoutno(String batchoutno) {
        this.batchoutno = batchoutno == null ? null : batchoutno.trim();
    }

    public String getPostype() {
        return postype;
    }

    public void setPostype(String postype) {
        this.postype = postype == null ? null : postype.trim();
    }

    public String getUsepersion() {
        return usepersion;
    }

    public void setUsepersion(String usepersion) {
        this.usepersion = usepersion == null ? null : usepersion.trim();
    }

    public String getUsedepartment() {
        return usedepartment;
    }

    public void setUsedepartment(String usedepartment) {
        this.usedepartment = usedepartment == null ? null : usedepartment.trim();
    }

    public String getOutmercode() {
        return outmercode;
    }

    public void setOutmercode(String outmercode) {
        this.outmercode = outmercode == null ? null : outmercode.trim();
    }

    public String getPosmold() {
        return posmold;
    }

    public void setPosmold(String posmold) {
        this.posmold = posmold == null ? null : posmold.trim();
    }

    public String getOutindate() {
        return outindate;
    }

    public void setOutindate(String outindate) {
        this.outindate = outindate == null ? null : outindate.trim();
    }

    public String getBatchoutinno() {
        return batchoutinno;
    }

    public void setBatchoutinno(String batchoutinno) {
        this.batchoutinno = batchoutinno == null ? null : batchoutinno.trim();
    }

    public String getOutinpersion() {
        return outinpersion;
    }

    public void setOutinpersion(String outinpersion) {
        this.outinpersion = outinpersion == null ? null : outinpersion.trim();
    }

    public String getOutpersion() {
        return outpersion;
    }

    public void setOutpersion(String outpersion) {
        this.outpersion = outpersion == null ? null : outpersion.trim();
    }

    public String getOutindepartment() {
        return outindepartment;
    }

    public void setOutindepartment(String outindepartment) {
        this.outindepartment = outindepartment == null ? null : outindepartment.trim();
    }

    public String getOutdepartment() {
        return outdepartment;
    }

    public void setOutdepartment(String outdepartment) {
        this.outdepartment = outdepartment == null ? null : outdepartment.trim();
    }

    public String getAgentNumber() {
        return agentNumber;
    }

    public void setAgentNumber(String agentNumber) {
        this.agentNumber = agentNumber == null ? null : agentNumber.trim();
    }

    public Short getUsestatus() {
        return usestatus;
    }

    public void setUsestatus(Short usestatus) {
        this.usestatus = usestatus;
    }

    public BigDecimal getCompanyId() {
        return companyId;
    }

    public void setCompanyId(BigDecimal companyId) {
        this.companyId = companyId;
    }

    public String getAgentbatchinno() {
        return agentbatchinno;
    }

    public void setAgentbatchinno(String agentbatchinno) {
        this.agentbatchinno = agentbatchinno == null ? null : agentbatchinno.trim();
    }

    public String getAgentindate() {
        return agentindate;
    }

    public void setAgentindate(String agentindate) {
        this.agentindate = agentindate == null ? null : agentindate.trim();
    }
}