package xdt.model;

import java.math.BigDecimal;

public class PmsBusinessInfo {
    private BigDecimal id;

    private String businessNum;

    private String channelId;

    private String businessName;

    private String type;

    private String time;

    private String state;

    private String kuaiyiId;

    private String posId;

    private BigDecimal totalSum;

    private BigDecimal failures;

    private BigDecimal moneyStart;

    private BigDecimal moneyEnd;

    private String premiumerate;//通道费率
    
    private String poundage;//代付手续费
    
    private String city;
    
    private String province;
    
    private String payType;

    private String payTypeName;

    
    public String getPoundage() {
		return poundage;
	}

	public void setPoundage(String poundage) {
		this.poundage = poundage;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public String getProvince() {
		return province;
	}

	public void setProvince(String province) {
		this.province = province;
	}

	public String getPayType() {
		return payType;
	}

	public void setPayType(String payType) {
		this.payType = payType;
	}

	public String getPayTypeName() {
		return payTypeName;
	}

	public void setPayTypeName(String payTypeName) {
		this.payTypeName = payTypeName;
	}

	public BigDecimal getId() {
        return id;
    }

    public void setId(BigDecimal id) {
        this.id = id;
    }

    public String getBusinessNum() {
        return businessNum;
    }

    public void setBusinessNum(String businessNum) {
        this.businessNum = businessNum == null ? null : businessNum.trim();
    }

    public String getChannelId() {
        return channelId;
    }

    public void setChannelId(String channelId) {
        this.channelId = channelId;
    }

    public String getBusinessName() {
        return businessName;
    }

    public void setBusinessName(String businessName) {
        this.businessName = businessName == null ? null : businessName.trim();
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type == null ? null : type.trim();
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time == null ? null : time.trim();
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state == null ? null : state.trim();
    }

    public String getKuaiyiId() {
        return kuaiyiId;
    }

    public void setKuaiyiId(String kuaiyiId) {
        this.kuaiyiId = kuaiyiId == null ? null : kuaiyiId.trim();
    }

    public String getPosId() {
        return posId;
    }

    public void setPosId(String posId) {
        this.posId = posId == null ? null : posId.trim();
    }

    public BigDecimal getTotalSum() {
        return totalSum;
    }

    public void setTotalSum(BigDecimal totalSum) {
        this.totalSum = totalSum;
    }

    public BigDecimal getFailures() {
        return failures;
    }

    public void setFailures(BigDecimal failures) {
        this.failures = failures;
    }

    public BigDecimal getMoneyStart() {
        return moneyStart;
    }

    public void setMoneyStart(BigDecimal moneyStart) {
        this.moneyStart = moneyStart;
    }

    public BigDecimal getMoneyEnd() {
        return moneyEnd;
    }

    public void setMoneyEnd(BigDecimal moneyEnd) {
        this.moneyEnd = moneyEnd;
    }

    public String getPremiumerate() {
        return premiumerate;
    }

    public void setPremiumerate(String premiumerate) {
        this.premiumerate = premiumerate == null ? null : premiumerate.trim();
    }
}