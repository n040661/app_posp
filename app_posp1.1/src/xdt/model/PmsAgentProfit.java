package xdt.model;

import java.util.List;

public class PmsAgentProfit {

	 private String mercId;//商户号
	 private String mercName;//商户名称
	 private String agentName;//代理商号
	 private String agentNumber;//代理商名称
	 private String agentLevel;//代理商等级
	 private String businessNum;//通道商户号
	 private String businessName;//通道名称
	 private String tariffRate;//商户费率
	 private String standardRate;//通道费率
	 private String agentRate;//代理商费率
	 private String poundage;//通道代付手续费
	 private String mercPoundage;//商户代付手续费
	 private String agentPoundage;//一级代理商代付手续费
	 private String pospsn;//流水号
	 private String transamt;//交易金额
	 private String profitRatio;//协定比例
	 private String profit;//通道分润
	 private String profitOne;//一级代理商分润
	 private String endDate;//交易时间
	 private String tradeTypeCode;//业务类型
	 private String oAgentNo;//欧单编号
	public String getMercId() {
		return mercId;
	}
	public void setMercId(String mercId) {
		this.mercId = mercId;
	}
	public String getMercName() {
		return mercName;
	}
	public void setMercName(String mercName) {
		this.mercName = mercName;
	}
	public String getAgentName() {
		return agentName;
	}
	public void setAgentName(String agentName) {
		this.agentName = agentName;
	}
	public String getAgentNumber() {
		return agentNumber;
	}
	public void setAgentNumber(String agentNumber) {
		this.agentNumber = agentNumber;
	}
	public String getAgentLevel() {
		return agentLevel;
	}
	public void setAgentLevel(String agentLevel) {
		this.agentLevel = agentLevel;
	}
	public String getBusinessNum() {
		return businessNum;
	}
	public void setBusinessNum(String businessNum) {
		this.businessNum = businessNum;
	}
	public String getBusinessName() {
		return businessName;
	}
	public void setBusinessName(String businessName) {
		this.businessName = businessName;
	}
	public String getTariffRate() {
		return tariffRate;
	}
	public void setTariffRate(String tariffRate) {
		this.tariffRate = tariffRate;
	}
	public String getStandardRate() {
		return standardRate;
	}
	public void setStandardRate(String standardRate) {
		this.standardRate = standardRate;
	}
	public String getAgentRate() {
		return agentRate;
	}
	public void setAgentRate(String agentRate) {
		this.agentRate = agentRate;
	}
	public String getPoundage() {
		return poundage;
	}
	public void setPoundage(String poundage) {
		this.poundage = poundage;
	}
	public String getMercPoundage() {
		return mercPoundage;
	}
	public void setMercPoundage(String mercPoundage) {
		this.mercPoundage = mercPoundage;
	}
	public String getAgentPoundage() {
		return agentPoundage;
	}
	public void setAgentPoundage(String agentPoundage) {
		this.agentPoundage = agentPoundage;
	}
	public String getPospsn() {
		return pospsn;
	}
	public void setPospsn(String pospsn) {
		this.pospsn = pospsn;
	}
	public String getTransamt() {
		return transamt;
	}
	public void setTransamt(String transamt) {
		this.transamt = transamt;
	}
	public String getProfitRatio() {
		return profitRatio;
	}
	public void setProfitRatio(String profitRatio) {
		this.profitRatio = profitRatio;
	}
	public String getProfit() {
		return profit;
	}
	public void setProfit(String profit) {
		this.profit = profit;
	}
	public String getProfitOne() {
		return profitOne;
	}
	public void setProfitOne(String profitOne) {
		this.profitOne = profitOne;
	}
	public String getEndDate() {
		return endDate;
	}
	public void setEndDate(String endDate) {
		this.endDate = endDate;
	}
	public String getTradeTypeCode() {
		return tradeTypeCode;
	}
	public void setTradeTypeCode(String tradeTypeCode) {
		this.tradeTypeCode = tradeTypeCode;
	}
	public String getoAgentNo() {
		return oAgentNo;
	}
	public void setoAgentNo(String oAgentNo) {
		this.oAgentNo = oAgentNo;
	}
	@Override
	public String toString() {
		return "PmsAgentProfit [mercId=" + mercId + ", mercName=" + mercName + ", agentName=" + agentName
				+ ", agentNumber=" + agentNumber + ", agentLevel=" + agentLevel + ", businessNum=" + businessNum
				+ ", businessName=" + businessName + ", tariffRate=" + tariffRate + ", standardRate=" + standardRate
				+ ", agentRate=" + agentRate + ", poundage=" + poundage + ", mercPoundage=" + mercPoundage
				+ ", agentPoundage=" + agentPoundage + ", pospsn=" + pospsn + ", transamt=" + transamt
				+ ", profitRatio=" + profitRatio + ", profit=" + profit + ", profitOne=" + profitOne + ", endDate="
				+ endDate + ", tradeTypeCode=" + tradeTypeCode + ", oAgentNo=" + oAgentNo + "]";
	}
	 
	 
	 
}
