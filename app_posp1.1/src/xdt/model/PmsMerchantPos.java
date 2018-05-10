package xdt.model;

import java.math.BigDecimal;

public class PmsMerchantPos {
	private BigDecimal id;// 主键

	private BigDecimal merchantid;// 商户id（pms_merchant_info的id）

	private BigDecimal posid;// posid(pos_info表主键，关联现实终端id)

	private String posbusinessno;// pos终端业编号

	private BigDecimal rate;// 佣金费率，以元为单位

	private Short ratetype;// 费率类型1：固定单笔费率 2：固定百分百费率 3：固定年费

	private Short rateflag;// 费率使用状态0：使用默认费率1：特殊情况费率

	private Short usestatus;// 0：未注册 1：已注册 2：已初始化 3：已开通 4：已注销 5：正常工作状态 6：未签到
	// 7：已锁定 8：已冻结 9：强制签到

	private String paramversion;// pos当前版本

	private String setupdate;// 安装日期

	private String startusedate;// 启动日期

	private String expiredate;// 到期日期

	private String authority;// 交易权限

	private String cursoftversion;// 当前软件版本

	private String updatesoftversion;// 待升级的软件版本

	private String kek;// kek，密钥索引依次对应（明文）

	private BigDecimal minamt;// 手续费最低金额，以元为单位

	private BigDecimal maxamt;// 手续费最高金额，以元为单位

	private BigDecimal departmentid;// 营业部id

	private String batno;// 批次号

	private String reservedprivate;// mac pin秘钥

	private String zhukek;// 秘钥 kek密文

	private String posname;// 设备名称

	public BigDecimal getId() {
		return id;
	}

	public void setId(BigDecimal id) {
		this.id = id;
	}

	public BigDecimal getMerchantid() {
		return merchantid;
	}

	public void setMerchantid(BigDecimal merchantid) {
		this.merchantid = merchantid;
	}

	public BigDecimal getPosid() {
		return posid;
	}

	public void setPosid(BigDecimal posid) {
		this.posid = posid;
	}

	public String getPosbusinessno() {
		return posbusinessno;
	}

	public void setPosbusinessno(String posbusinessno) {
		this.posbusinessno = posbusinessno == null ? null : posbusinessno
				.trim();
	}

	public BigDecimal getRate() {
		return rate;
	}

	public void setRate(BigDecimal rate) {
		this.rate = rate;
	}

	public Short getRatetype() {
		return ratetype;
	}

	public void setRatetype(Short ratetype) {
		this.ratetype = ratetype;
	}

	public Short getRateflag() {
		return rateflag;
	}

	public void setRateflag(Short rateflag) {
		this.rateflag = rateflag;
	}

	public Short getUsestatus() {
		return usestatus;
	}

	public void setUsestatus(Short usestatus) {
		this.usestatus = usestatus;
	}

	public String getParamversion() {
		return paramversion;
	}

	public void setParamversion(String paramversion) {
		this.paramversion = paramversion == null ? null : paramversion.trim();
	}

	public String getSetupdate() {
		return setupdate;
	}

	public void setSetupdate(String setupdate) {
		this.setupdate = setupdate == null ? null : setupdate.trim();
	}

	public String getStartusedate() {
		return startusedate;
	}

	public void setStartusedate(String startusedate) {
		this.startusedate = startusedate == null ? null : startusedate.trim();
	}

	public String getExpiredate() {
		return expiredate;
	}

	public void setExpiredate(String expiredate) {
		this.expiredate = expiredate == null ? null : expiredate.trim();
	}

	public String getAuthority() {
		return authority;
	}

	public void setAuthority(String authority) {
		this.authority = authority == null ? null : authority.trim();
	}

	public String getCursoftversion() {
		return cursoftversion;
	}

	public void setCursoftversion(String cursoftversion) {
		this.cursoftversion = cursoftversion == null ? null : cursoftversion
				.trim();
	}

	public String getUpdatesoftversion() {
		return updatesoftversion;
	}

	public void setUpdatesoftversion(String updatesoftversion) {
		this.updatesoftversion = updatesoftversion == null ? null
				: updatesoftversion.trim();
	}

	public String getKek() {
		return kek;
	}

	public void setKek(String kek) {
		this.kek = kek == null ? null : kek.trim();
	}

	public BigDecimal getMinamt() {
		return minamt;
	}

	public void setMinamt(BigDecimal minamt) {
		this.minamt = minamt;
	}

	public BigDecimal getMaxamt() {
		return maxamt;
	}

	public void setMaxamt(BigDecimal maxamt) {
		this.maxamt = maxamt;
	}

	public BigDecimal getDepartmentid() {
		return departmentid;
	}

	public void setDepartmentid(BigDecimal departmentid) {
		this.departmentid = departmentid;
	}

	public String getBatno() {
		return batno;
	}

	public void setBatno(String batno) {
		this.batno = batno == null ? null : batno.trim();
	}

	public String getReservedprivate() {
		return reservedprivate;
	}

	public void setReservedprivate(String reservedprivate) {
		this.reservedprivate = reservedprivate == null ? null : reservedprivate
				.trim();
	}

	public String getZhukek() {
		return zhukek;
	}

	public void setZhukek(String zhukek) {
		this.zhukek = zhukek == null ? null : zhukek.trim();
	}

	public String getPosname() {
		return posname;
	}

	public void setPosname(String posname) {
		this.posname = posname;
	}

}