package xdt.dto.nbs.register;

import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;

import xdt.dto.nbs.base.WechatRequestBase;
import xdt.quickpay.nbs.common.util.SignatureUtil;
import xdt.quickpay.nbs.common.util.StringUtil;

public class Register extends WechatRequestBase {
	// 业务类型
	private String serviceType;
	// 签名
	private String sign;
	// 代理商编号
	private String agentNum;

	private String merchantNumber;// 机构商商户编号
	// 代理商秘钥
	private String apiKey;
	// 下游商户
	private String outMchId;
	// 关注公众号
	private String appId;
	// 商户类型（PERSONAL-个人，ENTERPRISE-企业）
	private String customerType;
	// 经营行业 内容详见行业字典
	private String businessType;
	// 经营名称（支付成功显示）
	private String businessName;
	// 法人身份证
	private String legalId;
	// 法人名称
	private String legalName;
	// 联系人
	private String contact;
	// 联系人电话（手机号）
	private String contactPhone;
	// 联系人邮箱
	private String contactEmail;
	// 客服电话（座机号码）
	private String servicePhone;
	// 商户名称
	private String customerName;
	// 经营地址
	private String address;
	// 经营省
	private String provinceName;
	// 经营市
	private String cityName;
	// 经营区
	private String districtName;
	// 营业执照
	private String licenseNo;
	// 支付通道（WECHAT_OFFLINE-微信线下、WECHAT_APP微信APP支付）
	private String payChannel;
	// 交易费率（百分比格式，0.5为千五）
	private BigDecimal rate;
	// 是否开通T+0（Y,N 暂时只能为N）
	private String t0Status;
	// T+0 费率（百分比格式，0.5为千五）
	private BigDecimal settleRate;
	// T+0 单笔加收费用
	private BigDecimal fixedFee;
	// 是否封顶（Y,N）
	private String isCapped;
	// 结算(T0_INSTANT实时，T0_BATCH批量,T0_HANDING手动,T1_AUTO自动) ,
	private String settleMode;
	// 封顶值( 当IS_CAPPED为Y时，此字段起作用)
	private BigDecimal upperFee;
	// 账户类型（PERSONAL-个体户 ，COMPANY-公户）
	private String accountType;
	// 开户名
	private String accountName;
	// 银行卡号
	private String bankCard;
	// 开户行名称
	private String bankName;
	// 开户行省份
	private String province;
	// 开户行城市
	private String city;
	// 开户行地址
	private String bankAddress;
	// 联行号
	private String alliedBankNo;
	// 身份证正面(如XXX.jpg 或 dir/XXX.jpg,不支持中文命名)
	private String rightID;
	// 身份证反面(如XXX.jpg 或 dir/XXX.jpg,不支持中文命名)
	private String reservedID;
	// 手持身份证(如XXX.jpg 或 dir/XXX.jpg,不支持中文命名)
	private String IDWithHand;
	// 银行卡正面(如XXX.jpg 或 dir/XXX.jpg,不支持中文命名)
	private String rightBankCard;
	// 营业执照(如XXX.jpg 或 dir/XXX.jpg,不支持中文命名)
	private String licenseImage;
	// 门面照(如XXX.jpg 或 dir/XXX.jpg,不支持中文命名)
	private String doorHeadImage;
	// 开户许可证(如XXX.jpg 或 dir/XXX.jpg,不支持中文命名)
	private String accountLicence;
	// 查询条件类型（0-商户编号 或 1-下游商户号）
	private String queryType;
	// 商户编号(查询条件类型为0时有效)
	private String customerNum;
	// 对账单日期
	private String orderDate;
	// 结算日期
	private String checkDate;

	public Register() {
		super();
	}

	public Register(String key, String serviceType, String agentNum, String merchantNumber, String apiKey,
			String outMchId, String appId, String customerType, String businessType, String businessName,
			String legalId, String legalName, String contact, String contactPhone, String contactEmail,
			String servicePhone, String customerName, String address, String provinceName, String cityName,
			String districtName, String licenseNo, String payChannel, BigDecimal rate, String t0Status,
			BigDecimal settleRate, BigDecimal fixedFee, String isCapped, String settleMode, BigDecimal upperFee,
			String accountType, String accountName, String bankCard, String bankName, String province, String city,
			String bankAddress, String alliedBankNo, String rightID, String reservedID, String iDWithHand,
			String rightBankCard, String licenseImage, String doorHeadImage, String accountLicence, String queryType,
			String customerNum, String orderDate, String checkDate, Logger log) {
		setServiceType(serviceType);
		setAgentNum(agentNum);
		setMerchantNumber(merchantNumber);
		setApiKey(apiKey);
		setOutMchId(outMchId);
		setAppId(appId);
		setCustomerType(customerType);
		setBusinessType(businessType);
		setBusinessName(businessName);
		setLegalId(legalId);
		setLegalName(legalName);
		setContact(contact);
		setContactPhone(contactPhone);
		setContactEmail(contactEmail);
		setServicePhone(servicePhone);
		setCustomerName(customerName);
		setAddress(address);
		setProvinceName(provinceName);
		setCityName(cityName);
		setDistrictName(districtName);
		setLicenseNo(licenseNo);
		setPayChannel(payChannel);
		setRate(rate);
		setT0Status(t0Status);
		setSettleRate(settleRate);
		setFixedFee(fixedFee);
		setIsCapped(isCapped);
		setSettleMode(settleMode);
		setUpperFee(upperFee);
		setAccountType(accountType);
		setAccountName(accountName);
		setBankCard(bankCard);
		setBankName(bankName);
		setProvince(province);
		setCity(city);
		setBankAddress(bankAddress);
		setAlliedBankNo(alliedBankNo);
		setRightID(rightID);
		setReservedID(reservedID);
		setIDWithHand(iDWithHand);
		setRightBankCard(rightBankCard);
		setLicenseImage(licenseImage);
		setDoorHeadImage(doorHeadImage);
		setAccountLicence(accountLicence);
		setQueryType(queryType);
		setCustomerNum(customerNum);
		setOrderDate(orderDate);
		setCheckDate(checkDate);
		String sign = SignatureUtil.getSign(toMap(), key, log);
		setSign(sign);// 把签名数据设置到Sign这个属性中
	}

	/**
	 * 将对象转成Map
	 * 
	 * @return
	 * @author zhang.hui@pufubao.net
	 * @date 2017年3月2日 下午7:02:13
	 */
	public Map<String, Object> toMap() {
		Map<String, Object> map = new HashMap<String, Object>();
		Field[] fields = this.getClass().getDeclaredFields();
		for (Field field : fields) {
			Object obj;
			try {
				obj = field.get(this);
				if (obj != null && StringUtil.isNotBlank(String.valueOf(obj))) {
					map.put(field.getName(), obj);
				}
			} catch (IllegalArgumentException e) {
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			}
		}
		return map;
	}

	public String getServiceType() {
		return serviceType;
	}

	public void setServiceType(String serviceType) {
		this.serviceType = serviceType;
	}

	public String getSign() {
		return sign;
	}

	public void setSign(String sign) {
		this.sign = sign;
	}

	public String getAgentNum() {
		return agentNum;
	}

	public void setAgentNum(String agentNum) {
		this.agentNum = agentNum;
	}

	public String getMerchantNumber() {
		return merchantNumber;
	}

	public void setMerchantNumber(String merchantNumber) {
		this.merchantNumber = merchantNumber;
	}

	public String getApiKey() {
		return apiKey;
	}

	public void setApiKey(String apiKey) {
		this.apiKey = apiKey;
	}

	public String getOutMchId() {
		return outMchId;
	}

	public void setOutMchId(String outMchId) {
		this.outMchId = outMchId;
	}

	public String getAppId() {
		return appId;
	}

	public void setAppId(String appId) {
		this.appId = appId;
	}

	public String getCustomerType() {
		return customerType;
	}

	public void setCustomerType(String customerType) {
		this.customerType = customerType;
	}

	public String getBusinessType() {
		return businessType;
	}

	public void setBusinessType(String businessType) {
		this.businessType = businessType;
	}

	public String getBusinessName() {
		return businessName;
	}

	public void setBusinessName(String businessName) {
		this.businessName = businessName;
	}

	public String getLegalId() {
		return legalId;
	}

	public void setLegalId(String legalId) {
		this.legalId = legalId;
	}

	public String getLegalName() {
		return legalName;
	}

	public void setLegalName(String legalName) {
		this.legalName = legalName;
	}

	public String getContact() {
		return contact;
	}

	public void setContact(String contact) {
		this.contact = contact;
	}

	public String getContactPhone() {
		return contactPhone;
	}

	public void setContactPhone(String contactPhone) {
		this.contactPhone = contactPhone;
	}

	public String getContactEmail() {
		return contactEmail;
	}

	public void setContactEmail(String contactEmail) {
		this.contactEmail = contactEmail;
	}

	public String getServicePhone() {
		return servicePhone;
	}

	public void setServicePhone(String servicePhone) {
		this.servicePhone = servicePhone;
	}

	public String getCustomerName() {
		return customerName;
	}

	public void setCustomerName(String customerName) {
		this.customerName = customerName;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getProvinceName() {
		return provinceName;
	}

	public void setProvinceName(String provinceName) {
		this.provinceName = provinceName;
	}

	public String getCityName() {
		return cityName;
	}

	public void setCityName(String cityName) {
		this.cityName = cityName;
	}

	public String getDistrictName() {
		return districtName;
	}

	public void setDistrictName(String districtName) {
		this.districtName = districtName;
	}

	public String getLicenseNo() {
		return licenseNo;
	}

	public void setLicenseNo(String licenseNo) {
		this.licenseNo = licenseNo;
	}

	public String getPayChannel() {
		return payChannel;
	}

	public void setPayChannel(String payChannel) {
		this.payChannel = payChannel;
	}

	public BigDecimal getRate() {
		return rate;
	}

	public void setRate(BigDecimal rate) {
		this.rate = rate;
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

	public BigDecimal getUpperFee() {
		return upperFee;
	}

	public void setUpperFee(BigDecimal upperFee) {
		this.upperFee = upperFee;
	}

	public String getAccountType() {
		return accountType;
	}

	public void setAccountType(String accountType) {
		this.accountType = accountType;
	}

	public String getAccountName() {
		return accountName;
	}

	public void setAccountName(String accountName) {
		this.accountName = accountName;
	}

	public String getBankCard() {
		return bankCard;
	}

	public void setBankCard(String bankCard) {
		this.bankCard = bankCard;
	}

	public String getBankName() {
		return bankName;
	}

	public void setBankName(String bankName) {
		this.bankName = bankName;
	}

	public String getProvince() {
		return province;
	}

	public void setProvince(String province) {
		this.province = province;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public String getBankAddress() {
		return bankAddress;
	}

	public void setBankAddress(String bankAddress) {
		this.bankAddress = bankAddress;
	}

	public String getAlliedBankNo() {
		return alliedBankNo;
	}

	public void setAlliedBankNo(String alliedBankNo) {
		this.alliedBankNo = alliedBankNo;
	}

	public String getRightID() {
		return rightID;
	}

	public void setRightID(String rightID) {
		this.rightID = rightID;
	}

	public String getReservedID() {
		return reservedID;
	}

	public void setReservedID(String reservedID) {
		this.reservedID = reservedID;
	}

	public String getIDWithHand() {
		return IDWithHand;
	}

	public void setIDWithHand(String iDWithHand) {
		IDWithHand = iDWithHand;
	}

	public String getRightBankCard() {
		return rightBankCard;
	}

	public void setRightBankCard(String rightBankCard) {
		this.rightBankCard = rightBankCard;
	}

	public String getLicenseImage() {
		return licenseImage;
	}

	public void setLicenseImage(String licenseImage) {
		this.licenseImage = licenseImage;
	}

	public String getDoorHeadImage() {
		return doorHeadImage;
	}

	public void setDoorHeadImage(String doorHeadImage) {
		this.doorHeadImage = doorHeadImage;
	}

	public String getAccountLicence() {
		return accountLicence;
	}

	public void setAccountLicence(String accountLicence) {
		this.accountLicence = accountLicence;
	}

	public String getQueryType() {
		return queryType;
	}

	public void setQueryType(String queryType) {
		this.queryType = queryType;
	}

	public String getCustomerNum() {
		return customerNum;
	}

	public void setCustomerNum(String customerNum) {
		this.customerNum = customerNum;
	}

	public String getOrderDate() {
		return orderDate;
	}

	public void setOrderDate(String orderDate) {
		this.orderDate = orderDate;
	}

	public String getCheckDate() {
		return checkDate;
	}

	public void setCheckDate(String checkDate) {
		this.checkDate = checkDate;
	}

}
