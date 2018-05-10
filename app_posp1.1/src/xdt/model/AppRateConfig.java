package xdt.model;

import java.math.BigDecimal;

public class AppRateConfig {
    private BigDecimal id;      //主键

    private String rateType;      // 费率类型

    private String rate;         //费率

    private String topPoundage; //封顶手续费（当is_top为1时有效）

    private String isTop;       //是否是封顶的费率 1：封顶 0 ：普通

    private String isThirdpart;  //是否第三方费率

    private String remark;    //备注
    
    private String bottomPoundage;//最低手续费(转账汇款和信用卡还款时为附加费)
    
	private String isBottom;//是否最低  1最低   0不是最低
	private String oAgentNo;//O单编号
	

	public BigDecimal getId() {
        return id;
    }

    public void setId(BigDecimal id) {
        this.id = id;
    }

    public String getRateType() {
        return rateType;
    }

    public void setRateType(String rateType) {
        this.rateType = rateType == null ? null : rateType.trim();
    }

    public String getRate() {
        return rate;
    }

    public void setRate(String rate) {
        this.rate = rate == null ? null : rate.trim();
    }

    public String getTopPoundage() {
        return topPoundage;
    }

    public void setTopPoundage(String topPoundage) {
        this.topPoundage = topPoundage == null ? null : topPoundage.trim();
    }

    public String getIsTop() {
        return isTop;
    }

    public void setIsTop(String isTop) {
        this.isTop = isTop == null ? null : isTop.trim();
    }

    public String getIsThirdpart() {
        return isThirdpart;
    }

    public void setIsThirdpart(String isThirdpart) {
        this.isThirdpart = isThirdpart == null ? null : isThirdpart.trim();
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark == null ? null : remark.trim();
    }

	public String getBottomPoundage() {
		return bottomPoundage;
	}

	public void setBottomPoundage(String bottomPoundage) {
		this.bottomPoundage = bottomPoundage;
	}

	public String getIsBottom() {
		return isBottom;
	}

	public void setIsBottom(String isBottom) {
		this.isBottom = isBottom;
	}

	public String getoAgentNo() {
		return oAgentNo;
	}

	public void setoAgentNo(String oAgentNo) {
		this.oAgentNo = oAgentNo;
	}
    
}