package xdt.model;

/**
 * ********************************************************
 * 
 * @ClassName: PmsBusinessPos
 * @Description: 商户Pos表
 * @author 用wzl写的自动生成
 * @date 2014-06-17 下午 01:40:10
 ******************************************************* 
 */
public class PmsBusinessPos {

	private Integer id; // 主键id
	private String indate; // 入库时间
	private String batchoutno; // 出库批号
	private String posnum; // 业务终端编号
	private String batchinno; // 入库批号
	private String departmentnum; // 领用部门编号
	private String departmentname; // 领用部门
	private String status; // 库存状态:2已出库1:已入库
	private String businessnum; // 商户编号
	private String businessname; // 商户名称
	private String channelnum; // 通道编号
	private String channelname; // 通道名称
	private String outdate; // 出库时间
	private String personnum; // 领用人编号
	private String personname; // 领用人名称
	private String number; // 数量
	private String posstatus; // POS机的状态 1：未使用 2.已使用
	private String kek; // pos主密钥
	private String posopenstatus; // POSOPENSTATUS
    private String merchantId;//通道商户的主键id

	public String getPosopenstatus() {
		return posopenstatus;
	}

	public void setPosopenstatus(String posopenstatus) {
		this.posopenstatus = posopenstatus;
	}

	public String getPosstatus() {
		return posstatus;
	}

	public void setPosstatus(String posstatus) {
		this.posstatus = posstatus;
	}

	public String getKek() {
		return kek;
	}

	public void setKek(String kek) {
		this.kek = kek;
	}

	public String getDepartmentname() {
		return departmentname;
	}

	public void setDepartmentname(String departmentname) {
		this.departmentname = departmentname;
	}

	public String getBusinessname() {
		return businessname;
	}

	public void setBusinessname(String businessname) {
		this.businessname = businessname;
	}

	public String getChannelname() {
		return channelname;
	}

	public void setChannelname(String channelname) {
		this.channelname = channelname;
	}

	public String getPersonname() {
		return personname;
	}

	public void setPersonname(String personname) {
		this.personname = personname;
	}

	public String getNumber() {
		return number;
	}

	public void setNumber(String number) {
		this.number = number;
	}

	public Integer getId() {
		return this.id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getIndate() {
		return this.indate;
	}

	public void setIndate(String indate) {
		this.indate = indate;
	}

	public String getBatchoutno() {
		return this.batchoutno;
	}

	public void setBatchoutno(String batchoutno) {
		this.batchoutno = batchoutno;
	}

	public String getPosnum() {
		return this.posnum;
	}

	public void setPosnum(String posnum) {
		this.posnum = posnum;
	}

	public String getBatchinno() {
		return this.batchinno;
	}

	public void setBatchinno(String batchinno) {
		this.batchinno = batchinno;
	}

	public String getDepartmentnum() {
		return this.departmentnum;
	}

	public void setDepartmentnum(String departmentnum) {
		this.departmentnum = departmentnum;
	}

	public String getStatus() {
		return this.status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getBusinessnum() {
		return this.businessnum;
	}

	public void setBusinessnum(String businessnum) {
		this.businessnum = businessnum;
	}

	public String getChannelnum() {
		return this.channelnum;
	}

	public void setChannelnum(String channelnum) {
		this.channelnum = channelnum;
	}

	public String getOutdate() {
		return this.outdate;
	}

	public void setOutdate(String outdate) {
		this.outdate = outdate;
	}

	public String getPersonnum() {
		return this.personnum;
	}

	public void setPersonnum(String personnum) {
		this.personnum = personnum;
	}

    public String getMerchantId() {
        return merchantId;
    }

    public void setMerchantId(String merchantId) {
        this.merchantId = merchantId;
    }

}
