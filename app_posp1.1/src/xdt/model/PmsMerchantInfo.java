package xdt.model;

import java.math.BigDecimal;

public class PmsMerchantInfo {
    private String id;//id

    private String mercId;//商户编号

    private String sellerNo;//销售员编号

    private String mercName;//工商注册名称

    private String shortname;//商户简称

    private String businessname;//营业名称

    private String mccCd;//行业编码

    private String address;//客户地址

    private String mainbusiness;//主营业务

    private String taxCertId;//税务登记表

    private String licenseissuingdate;//执照签发日期

    private String licenseduedate;//执照到期日期

    private BigDecimal registeredcapital;//注册资金

    private String registrationdeadline;//注册期限

    private String customertype;//客户类型(1:普通商户；  2：封顶商户  ；  3 ：手机用户)

    private String naturebusiness;//商户性质

    private String businesschannels;//经营渠道

    private String businesslocation;//经营地段

    private String businessarea;//经营区域

    private String businessareaof;//经营面积

    private String salesway;//销售方式

    private String registeredaddress;//注册地址

    private String busAddr;//营业地址

    private String startbusinessdate;//开始营业日期

    private String normalbusinessdate;//正常营业时间

    private String orgId;//组织机构代码

    private String bankname;//开户行名称

    private String settlementname;//结算账户名

    private String clrMerc;//结算账号

    private String banksysnumber;//开户行支付系统行号

    private String crpNm;//法人代表

    private String crpIdNo;//法人代表证件号

    private String mobilephone;//法人手机

    private String phone;//法人座机

    private String headquartersbank;//总行名称

    private String creationName;//创建人登录名

    private String creationdate;//创建时间

    private BigDecimal removetag;//删除标记0正常  1.已删除

    private String loginName;//商户登录名（同商户编号）

    private String agentNumber;//代理商编号

    private String password;//密码

    private String crpIdTyp;//法人证件类型

    private String retMessage;//打回原因

    private String premiumrate;

    private BigDecimal mercClear;//1.自清  2它清

    private String mercSts;//" 10.申请商户 20.送审商户. -1 受理商户 30.核实商户 40.审批商户 50.开通绑定 60. 正式商户  80. 注销商户
    
    private String kyaccount; //快易账户 
    
    private BigDecimal  clearType; //0:T+0;  1:T+1;  2:T+N
    
    private BigDecimal  business; //1:多多宝应用
    
    private String truemerdate; //打成正式商户时间
    
    private String checkmerdate; //打成核实商户时间
    
    private String companyId; //公司编号
    
    private String applydate; //打成申请商户时间
    
    private String generalName; //泛称
    
    private String zipCode;//邮编
    
    private String cardStartdate;//身份证开始时间
    
    private String cardEnddate;//身份证结束时间
    
    private String bankProvince;//商户收款开户银行省
    
    private String bankCity; //商户收款开户银行市
    
    private String businessrange; //商户营业范围
    
    private String taxregistcardenddate; //税务登记结束日期
    
    private String taxregistcard; //税务登记号
    
    private String email; //邮箱
    
    private BigDecimal status; //0:默认  1:app默认审核成功 2：人工审核成功

    private String externalId;//对外的商户id uuid

    private String oAgentNo;//欧单编号

    private String altLat;//经纬度，逗号隔开

    private String gpsAddress;//gps获取的地址信息(中文)

    private String settleRateType;//清分费率类型

    private String quickRateType;//快捷费率类型
    
    private String counter;//最低手续费
    
    private String type;//小微商户和大商户类型0小微商户 1大商户
    
    private String t0Status;//是否开通T+0（Y,N 暂时只能为N）
    
    private BigDecimal settleRate;//T+0 费率（百分比格式，0.5为千五）
    
    private BigDecimal fixedFee;//T+0 单笔加收费用

    private String isCapped;//是否封顶（Y,N）
    
    private String settleMode;//结算(T0_INSTANT实时，T0_BATCH批量,T0_HANDING手动,T1_AUTO自动) ,
    
    private String upperFee;//封顶值( 当IS_CAPPED为Y时，此字段起作用)
    
    private String accountType;//账户类型（PERSONAL-个体户 ，COMPANY-公户）
    
    private String payChannel;//支付通道
    
    private String position; //可用额度
    
    private String maxDaiFu;//代付最大金额
    
    private String minDaiFu;//代付最大金额
    
    private String poundage;//代付手续费
    
    private String openPay;//是否开通代付 0开通1未开通
    
    private String positionT1; //T1代付额度
    
    private String daikouMinPoundage; //5000以下的代扣手续费
    
    private String daikouMaxPoundage; //5000以上2万以下的代扣手续费
    
    private String authentication;//鉴权手续费
    
    private String poundageFree;//代付手续费率
    
    
    public String getPoundageFree() {
		return poundageFree;
	}

	public void setPoundageFree(String poundageFree) {
		this.poundageFree = poundageFree;
	}

	public String getOpenPay() {
		return openPay;
	}

	public void setOpenPay(String openPay) {
		this.openPay = openPay;
	}

	public String getPoundage() {
		return poundage;
	}

	public void setPoundage(String poundage) {
		this.poundage = poundage;
	}

	public String getMaxDaiFu() {
		return maxDaiFu;
	}

	public void setMaxDaiFu(String maxDaiFu) {
		this.maxDaiFu = maxDaiFu;
	}

	public String getMinDaiFu() {
		return minDaiFu;
	}

	public void setMinDaiFu(String minDaiFu) {
		this.minDaiFu = minDaiFu;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getT0Status() {
		return t0Status;
	}

	public void setT0Status(String t0Status) {
		this.t0Status = t0Status;
	}

	public BigDecimal getSettleRate() {
		return settleRate;
	}

	public void setSettleRate(BigDecimal settleRate) {
		this.settleRate = settleRate;
	}

	public BigDecimal getFixedFee() {
		return fixedFee;
	}

	public void setFixedFee(BigDecimal fixedFee) {
		this.fixedFee = fixedFee;
	}

	public String getIsCapped() {
		return isCapped;
	}

	public void setIsCapped(String isCapped) {
		this.isCapped = isCapped;
	}

	public String getSettleMode() {
		return settleMode;
	}

	public void setSettleMode(String settleMode) {
		this.settleMode = settleMode;
	}

	public String getUpperFee() {
		return upperFee;
	}

	public void setUpperFee(String upperFee) {
		this.upperFee = upperFee;
	}

	public String getAccountType() {
		return accountType;
	}

	public void setAccountType(String accountType) {
		this.accountType = accountType;
	}

	public String getPayChannel() {
		return payChannel;
	}

	public void setPayChannel(String payChannel) {
		this.payChannel = payChannel;
	}

	public BigDecimal getStatus() {
		return status;
	}

	public void setStatus(BigDecimal status) {
		this.status = status;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email == null ? null : email.trim();
	}

	public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id == null ? null : id.trim();
    }

    public String getMercId() {
        return mercId;
    }

    public void setMercId(String mercId) {
        this.mercId = mercId == null ? null : mercId.trim();
    }

    public String getSellerNo() {
        return sellerNo;
    }

    public void setSellerNo(String sellerNo) {
        this.sellerNo = sellerNo == null ? null : sellerNo.trim();
    }

    public String getMercName() {
        return mercName;
    }

    public void setMercName(String mercName) {
        this.mercName = mercName == null ? null : mercName.trim();
    }

    public String getShortname() {
        return shortname;
    }

    public void setShortname(String shortname) {
        this.shortname = shortname == null ? null : shortname.trim();
    }

    public String getBusinessname() {
        return businessname;
    }

    public void setBusinessname(String businessname) {
        this.businessname = businessname == null ? null : businessname.trim();
    }

    public String getMccCd() {
        return mccCd;
    }

    public void setMccCd(String mccCd) {
        this.mccCd = mccCd == null ? null : mccCd.trim();
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address == null ? null : address.trim();
    }

    public String getMainbusiness() {
        return mainbusiness;
    }

    public void setMainbusiness(String mainbusiness) {
        this.mainbusiness = mainbusiness == null ? null : mainbusiness.trim();
    }

    public String getTaxCertId() {
        return taxCertId;
    }

    public void setTaxCertId(String taxCertId) {
        this.taxCertId = taxCertId == null ? null : taxCertId.trim();
    }

    public String getLicenseissuingdate() {
        return licenseissuingdate;
    }

    public void setLicenseissuingdate(String licenseissuingdate) {
        this.licenseissuingdate = licenseissuingdate == null ? null : licenseissuingdate.trim();
    }

    public String getLicenseduedate() {
        return licenseduedate;
    }

    public void setLicenseduedate(String licenseduedate) {
        this.licenseduedate = licenseduedate == null ? null : licenseduedate.trim();
    }

    public BigDecimal getRegisteredcapital() {
        return registeredcapital;
    }

    public void setRegisteredcapital(BigDecimal registeredcapital) {
        this.registeredcapital = registeredcapital;
    }

    public String getRegistrationdeadline() {
        return registrationdeadline;
    }

    public void setRegistrationdeadline(String registrationdeadline) {
        this.registrationdeadline = registrationdeadline == null ? null : registrationdeadline.trim();
    }

    public String getCustomertype() {
        return customertype;
    }

    public void setCustomertype(String customertype) {
        this.customertype = customertype == null ? null : customertype.trim();
    }

    public String getNaturebusiness() {
        return naturebusiness;
    }

    public void setNaturebusiness(String naturebusiness) {
        this.naturebusiness = naturebusiness == null ? null : naturebusiness.trim();
    }

    public String getBusinesschannels() {
        return businesschannels;
    }

    public void setBusinesschannels(String businesschannels) {
        this.businesschannels = businesschannels == null ? null : businesschannels.trim();
    }

    public String getBusinesslocation() {
        return businesslocation;
    }

    public void setBusinesslocation(String businesslocation) {
        this.businesslocation = businesslocation == null ? null : businesslocation.trim();
    }

    public String getBusinessarea() {
        return businessarea;
    }

    public void setBusinessarea(String businessarea) {
        this.businessarea = businessarea == null ? null : businessarea.trim();
    }

    public String getBusinessareaof() {
        return businessareaof;
    }

    public void setBusinessareaof(String businessareaof) {
        this.businessareaof = businessareaof == null ? null : businessareaof.trim();
    }

    public String getSalesway() {
        return salesway;
    }

    public void setSalesway(String salesway) {
        this.salesway = salesway == null ? null : salesway.trim();
    }

    public String getRegisteredaddress() {
        return registeredaddress;
    }

    public void setRegisteredaddress(String registeredaddress) {
        this.registeredaddress = registeredaddress == null ? null : registeredaddress.trim();
    }

    public String getBusAddr() {
        return busAddr;
    }

    public void setBusAddr(String busAddr) {
        this.busAddr = busAddr == null ? null : busAddr.trim();
    }

    public String getStartbusinessdate() {
        return startbusinessdate;
    }

    public void setStartbusinessdate(String startbusinessdate) {
        this.startbusinessdate = startbusinessdate == null ? null : startbusinessdate.trim();
    }

    public String getNormalbusinessdate() {
        return normalbusinessdate;
    }

    public void setNormalbusinessdate(String normalbusinessdate) {
        this.normalbusinessdate = normalbusinessdate == null ? null : normalbusinessdate.trim();
    }

    public String getOrgId() {
        return orgId;
    }

    public void setOrgId(String orgId) {
        this.orgId = orgId == null ? null : orgId.trim();
    }

    public String getBankname() {
        return bankname;
    }

    public void setBankname(String bankname) {
        this.bankname = bankname == null ? null : bankname.trim();
    }

    public String getSettlementname() {
        return settlementname;
    }

    public void setSettlementname(String settlementname) {
        this.settlementname = settlementname == null ? null : settlementname.trim();
    }

    public String getClrMerc() {
        return clrMerc;
    }

    public void setClrMerc(String clrMerc) {
        this.clrMerc = clrMerc == null ? null : clrMerc.trim();
    }

    public String getBanksysnumber() {
        return banksysnumber;
    }

    public void setBanksysnumber(String banksysnumber) {
        this.banksysnumber = banksysnumber == null ? null : banksysnumber.trim();
    }

    public String getCrpNm() {
        return crpNm;
    }

    public void setCrpNm(String crpNm) {
        this.crpNm = crpNm == null ? null : crpNm.trim();
    }

    public String getCrpIdNo() {
        return crpIdNo;
    }

    public void setCrpIdNo(String crpIdNo) {
        this.crpIdNo = crpIdNo == null ? null : crpIdNo.trim();
    }

    public String getMobilephone() {
        return mobilephone;
    }

    public void setMobilephone(String mobilephone) {
        this.mobilephone = mobilephone == null ? null : mobilephone.trim();
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone == null ? null : phone.trim();
    }

    public String getHeadquartersbank() {
        return headquartersbank;
    }

    public void setHeadquartersbank(String headquartersbank) {
        this.headquartersbank = headquartersbank == null ? null : headquartersbank.trim();
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

    public String getLoginName() {
        return loginName;
    }

    public void setLoginName(String loginName) {
        this.loginName = loginName == null ? null : loginName.trim();
    }

    public String getAgentNumber() {
        return agentNumber;
    }

    public void setAgentNumber(String agentNumber) {
        this.agentNumber = agentNumber == null ? null : agentNumber.trim();
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password == null ? null : password.trim();;
    }

    public String getCrpIdTyp() {
        return crpIdTyp;
    }

    public void setCrpIdTyp(String crpIdTyp) {
        this.crpIdTyp = crpIdTyp == null ? null : crpIdTyp.trim();
    }

    public String getRetMessage() {
        return retMessage;
    }

    public void setRetMessage(String retMessage) {
        this.retMessage = retMessage == null ? null : retMessage.trim();
    }

    public String getPremiumrate() {
        return premiumrate;
    }

    public void setPremiumrate(String premiumrate) {
        this.premiumrate = premiumrate == null ? null : premiumrate.trim();
    }

    public BigDecimal getMercClear() {
        return mercClear;
    }

    public void setMercClear(BigDecimal mercClear) {
        this.mercClear = mercClear;
    }

    public String getMercSts() {
        return mercSts;
    }

    public void setMercSts(String mercSts) {
        this.mercSts = mercSts == null ? null : mercSts.trim();
    }

	public String getKyaccount() {
		return kyaccount;
	}

	public void setKyaccount(String kyaccount) {
		this.kyaccount = kyaccount ==null ? null : kyaccount.trim();
	}

	public BigDecimal getClearType() {
		return clearType;
	}

	public void setClearType(BigDecimal clearType) {
		this.clearType = clearType;
	}

	public BigDecimal getBusiness() {
		return business;
	}

	public void setBusiness(BigDecimal business) {
		this.business = business;
	}

	public String getTruemerdate() {
		return truemerdate;
	}

	public void setTruemerdate(String truemerdate) {
		this.truemerdate = truemerdate == null ? null : truemerdate.trim();
	}

	public String getCheckmerdate() {
		return checkmerdate;
	}

	public void setCheckmerdate(String checkmerdate) {
		this.checkmerdate = checkmerdate == null ? null : checkmerdate.trim();
	}

	public String getCompanyId() {
		return companyId;
	}

	public void setCompanyId(String companyId) {
		this.companyId = companyId == null ? null : companyId.trim();
	}

	public String getApplydate() {
		return applydate;
	}

	public void setApplydate(String applydate) {
		this.applydate = applydate ==null ? null : applydate.trim();
	}

	public String getGeneralName() {
		return generalName;
	}

	public void setGeneralName(String generalName) {
		this.generalName = generalName == null ? null : generalName.trim();
	}

	public String getZipCode() {
		return zipCode;
	}

	public void setZipCode(String zipCode) {
		this.zipCode = zipCode == null ? null : zipCode.trim();
	}

	public String getCardStartdate() {
		return cardStartdate;
	}

	public void setCardStartdate(String cardStartdate) {
		this.cardStartdate = cardStartdate == null ? null : cardStartdate.trim();
	}

	public String getCardEnddate() {
		return cardEnddate;
	}

	public void setCardEnddate(String cardEnddate) {
		this.cardEnddate = cardEnddate == null ? null : cardEnddate.trim();
	}

	public String getBankProvince() {
		return bankProvince;
	}

	public void setBankProvince(String bankProvince) {
		this.bankProvince = bankProvince == null ? null : bankProvince.trim();
	}

	public String getBankCity() {
		return bankCity;
	}

	public void setBankCity(String bankCity) {
		this.bankCity = bankCity == null ? null : bankCity.trim();
	}

	public String getBusinessrange() {
		return businessrange;
	}

	public void setBusinessrange(String businessrange) {
		this.businessrange = businessrange == null ? null : businessrange.trim();
	}

	public String getTaxregistcardenddate() {
		return taxregistcardenddate;
	}

	public void setTaxregistcardenddate(String taxregistcardenddate) {
		this.taxregistcardenddate = taxregistcardenddate == null ? null : taxregistcardenddate.trim();
	}

	public String getTaxregistcard() {
		return taxregistcard;
	}

	public void setTaxregistcard(String taxregistcard) {
		this.taxregistcard = taxregistcard == null ? null : taxregistcard.trim();
	}

    public String getExternalId() {
        return externalId;
    }

    public void setExternalId(String externalId) {
        this.externalId = externalId;
    }

    public String getoAgentNo() {
        return oAgentNo;
    }

    public void setoAgentNo(String oAgentNo) {
        this.oAgentNo = oAgentNo;
    }

    public String getAltLat() {
        return altLat;
    }

    public void setAltLat(String altLat) {
        this.altLat = altLat;
    }

    public String getGpsAddress() {
        return gpsAddress;
    }

    public void setGpsAddress(String gpsAddress) {
        this.gpsAddress = gpsAddress;
    }

    public String getSettleRateType() {
        return settleRateType;
    }

    public void setSettleRateType(String settleRateType) {
        this.settleRateType = settleRateType;
    }

	public String getQuickRateType() {
		return quickRateType;
	}

	public void setQuickRateType(String quickRateType) {
		this.quickRateType = quickRateType;
	}

	public String getCounter() {
		return counter;
	}

	public void setCounter(String counter) {
		this.counter = counter;
	}

	public String getPosition() {
		return position;
	}

	public void setPosition(String position) {
		this.position = position;
	}
	
	public String getDaikouMinPoundage() {
		return daikouMinPoundage;
	}

	public void setDaikouMinPoundage(String daikouMinPoundage) {
		this.daikouMinPoundage = daikouMinPoundage;
	}

	public String getDaikouMaxPoundage() {
		return daikouMaxPoundage;
	}

	public void setDaikouMaxPoundage(String daikouMaxPoundage) {
		this.daikouMaxPoundage = daikouMaxPoundage;
	}

	public String getPositionT1() {
		return positionT1;
	}

	public void setPositionT1(String positionT1) {
		this.positionT1 = positionT1;
	}

	public String getAuthentication() {
		return authentication;
	}

	public void setAuthentication(String authentication) {
		this.authentication = authentication;
	}
 
}