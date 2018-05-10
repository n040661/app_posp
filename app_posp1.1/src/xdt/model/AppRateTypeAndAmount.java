package xdt.model;


public class AppRateTypeAndAmount {

		
	    private String bottompoundage;
	  
	    private String rate;   //费率

	    private String topPoundage;   //封顶手续费（当is_top为1时有效）

	    private String isTop;        //是否是封顶的费率 1：封顶 0 ：普通

	    private String isThirdpart;   //是否第三方费率

	    private String remark;
	    private String mercId;     //商户编号

	    private String businesscode;  //业务编码    （ 1 商户收款、2 转账汇款、3 信用卡还款、4手机充值、5 水煤电、 6 加油卡充值、7 提款（提现））
	    
	    private String minAmount;  // 每笔最小金额 （分）

	    private String maxAmount;    // 每笔最大金额（分）

	    private String status;     // 业务状态 0 有效 1 无效

	    private String accountTime; //  到账时间

	    private String accountType;   //  0 t+0 1 t+1

	    private String numberoftimes;  // 操作次数

	    private String description;    // 描述

	    private String ratetype;    // 费率类型  关联 app_rate_config的ratetype字段

	    private String paymentcode; //支付方式 例如： 1 账号（余额）支付、2 百度支付、3 微信支付、4 支付宝支付、5刷卡支付、6移动和包支付、0其它

	    private String payStatus;  //支付方式状态 0 有效 1 无效
	    
	    private String message;
	    
		public String getRate() {
			return rate;
		}

		public void setRate(String rate) {
			this.rate = rate;
		}

		public String getTopPoundage() {
			return topPoundage;
		}

		public void setTopPoundage(String topPoundage) {
			this.topPoundage = topPoundage;
		}

		public String getIsTop() {
			return isTop;
		}

		public void setIsTop(String isTop) {
			this.isTop = isTop;
		}

		public String getIsThirdpart() {
			return isThirdpart;
		}

		public void setIsThirdpart(String isThirdpart) {
			this.isThirdpart = isThirdpart;
		}

		public String getRemark() {
			return remark;
		}

		public void setRemark(String remark) {
			this.remark = remark;
		}

		public String getMercId() {
			return mercId;
		}

		public void setMercId(String mercId) {
			this.mercId = mercId;
		}

		public String getBusinesscode() {
			return businesscode;
		}

		public void setBusinesscode(String businesscode) {
			this.businesscode = businesscode;
		}

		public String getMinAmount() {
			return minAmount;
		}

		public void setMinAmount(String minAmount) {
			this.minAmount = minAmount;
		}

		public String getMaxAmount() {
			return maxAmount;
		}

		public void setMaxAmount(String maxAmount) {
			this.maxAmount = maxAmount;
		}

		public String getStatus() {
			return status;
		}

		public void setStatus(String status) {
			this.status = status;
		}

		public String getAccountTime() {
			return accountTime;
		}

		public void setAccountTime(String accountTime) {
			this.accountTime = accountTime;
		}

		public String getAccountType() {
			return accountType;
		}

		public void setAccountType(String accountType) {
			this.accountType = accountType;
		}

		public String getNumberoftimes() {
			return numberoftimes;
		}

		public void setNumberoftimes(String numberoftimes) {
			this.numberoftimes = numberoftimes;
		}

		public String getDescription() {
			return description;
		}

		public void setDescription(String description) {
			this.description = description;
		}

		public String getRatetype() {
			return ratetype;
		}

		public void setRatetype(String ratetype) {
			this.ratetype = ratetype;
		}

		public String getPaymentcode() {
			return paymentcode;
		}

		public void setPaymentcode(String paymentcode) {
			this.paymentcode = paymentcode;
		}

		public String getPayStatus() {
			return payStatus;
		}

		public void setPayStatus(String payStatus) {
			this.payStatus = payStatus;
		}

		public String getMessage() {
			return message;
		}

		public void setMessage(String message) {
			this.message = message;
		}

		public String getBottompoundage() {
			return bottompoundage;
		}

		public void setBottompoundage(String bottompoundage) {
			this.bottompoundage = bottompoundage;
		}

}
