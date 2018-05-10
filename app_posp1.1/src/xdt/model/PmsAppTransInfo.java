package xdt.model;

import java.math.BigDecimal;

/**
 * @author Jeff
 * app订单表
 */
public class PmsAppTransInfo implements java.io.Serializable{
	
    private static final long serialVersionUID = -5345229284210268952L;
    private BigDecimal id;

    private String paymenttype;//支付方式(汉字) 例如： 1 账号（余额）支付、2 百度支付、3 微信支付、4 支付宝支付、5刷卡支付、6移动和包支付、0其它  、11 恒丰快捷支付

    private String paymentcode; //支付方式 例如： 1 账号（余额）支付、2 百度支付、3 微信支付、4 支付宝支付、5刷卡支付、6移动和包支付、0其它 、11 恒丰快捷支付

    private String tradetype;//交易类型(汉字) 例如： 1 商户收款、2 转账汇款、3 信用卡还款、4手机充值、5 水煤电、 6 加油卡充值、7 提款（提现） 、 17 在线支付

    private String tradetypecode; //交易类型 例如： 1 商户收款、2 转账汇款、3 信用卡还款、4手机充值、5 水煤电、 6 加油卡充值、7 提款（提现）、 17 在线支付

    private String tradetime;//交易时间 例如：2015-05-07 11:23:36

    private String orderid;//交易号本地订单号

    private String phonenumbertype;//手机号类型    例如：联通、电信、移动   （充值业务使用

    private String payamount;//结算金额   按分为最小单位  例如：1元=100分   采用100  给商户的钱     商户收款时给商户记账时的金额  ，提现时表示给商户打款的钱（订单金额-手续费）

    private String prepaidphonenumber;//充值手机号

    private String amount;//充值面额 （充值业务使用)  按分为最小单位  例如：1元=100分   采用100

    private String bankno; //银行卡号 信用卡还款与转账必填

    private String bankcardname;//银行卡名称

    private String bankname;//银行名称

    private String mercname;//账户持卡人

    private String shortbankcardnumber;//银行卡尾号后四位

    private String payeename;//收款人

    private String reasonofpayment;//付款理由

    private String prepaidcomeoncardcompany;//石油集团名称

    private String comeonkaka;//加油卡卡号

    private String mercid;//商户id

    private String poundage;//手续费    按分为最小单位  例如：1元=100分   采用100

    private String factamount;//实际金额    按分为最小单位  例如：1元=100分   采用100  用户消费了多少钱（商户收款收了多钱和商户提现提了多钱）

    private String url;//路径

    private String responsestate; //请求第三方实时返回响应状态  交易成功    交易失败
    private String creditcardnumber; //刷卡银行卡号

    private String portorderid;//接口订单号

    private String finishtime;//订单完成时间

    private String orderamount;//订单金额  按分为最小单位  例如：1元=100分   采用100

    private String rate;//费率

    private String resultCode;//返回码(调接口上送完成是返回的)

    private String drawMoneyType; //提款方式          0 超级提款 1 普通提款

    private String thirdPartResultCode; //第三方回调返回码

    private String status; //交易状态码  0支付成功 1支付失败2 等待支付 3退款成功 4客户端支付成功，等待服务器调用支付  5第三方正在支付 6等待清算系统结算 9第三方撤销订单 100 系统异常

    private String personalPay; //个人付款        0 付款方  1 收款方

    private String brushType;//刷卡类型：1音频刷卡，2蓝牙刷卡

    private String snNO;//刷卡器设备号

    private String accountingFlag;// 订单处理成功(支付成功)后余额修改标记位    1已经修改过  其他没有修改

    private String serialNo;//批次号（讯联用）

    private String businessNum;//通道业务编码

    private String channelNum;//渠道号
    
    private String searchNum;//检索参考号（讯联用）

    private String oAgentNo; //欧单编号

    private String authPath; //图片认证路径
    private String altLat;//经纬度（逗号隔开）
    private String gpsAddress;//gps获取的地址信息(中文)
    
    private String SettlementState;//结算状态
	private String SettlementPeriod;//结算周期

    
    
    
    private String settlepoundage;//O单T0使用   清算附加费(手续费)
    private String totalpoundage;//O单T0使用   总手续费
    private String certNo; //证件号码
    private String token; //令牌
    
    
    public BigDecimal getId() {
        return id;
    }

    public void setId(BigDecimal id) {
        this.id = id;
    }

    public String getPaymenttype() {
        return paymenttype;
    }

    public void setPaymenttype(String paymenttype) {
        this.paymenttype = paymenttype;
    }

    public String getTradetype() {
        return tradetype;
    }

    public void setTradetype(String tradetype) {
        this.tradetype = tradetype;
    }

    public String getTradetime() {
        return tradetime;
    }

    public void setTradetime(String tradetime) {
        this.tradetime = tradetime;
    }

    public String getOrderid() {
        return orderid;
    }

    public void setOrderid(String orderid) {
        this.orderid = orderid;
    }

    public String getPhonenumbertype() {
        return phonenumbertype;
    }

    public void setPhonenumbertype(String phonenumbertype) {
        this.phonenumbertype = phonenumbertype;
    }

    public String getPayamount() {
        return payamount;
    }

    public void setPayamount(String payamount) {
        this.payamount = payamount;
    }

    public String getPrepaidphonenumber() {
        return prepaidphonenumber;
    }

    public void setPrepaidphonenumber(String prepaidphonenumber) {
        this.prepaidphonenumber = prepaidphonenumber;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getBankcardname() {
        return bankcardname;
    }

    public void setBankcardname(String bankcardname) {
        this.bankcardname = bankcardname;
    }

    public String getMercname() {
        return mercname;
    }

    public void setMercname(String mercname) {
        this.mercname = mercname;
    }

    public String getBankname() {
        return bankname;
    }

    public void setBankname(String bankname) {
        this.bankname = bankname;
    }

    public String getShortbankcardnumber() {
        return shortbankcardnumber;
    }

    public void setShortbankcardnumber(String shortbankcardnumber) {
        this.shortbankcardnumber = shortbankcardnumber;
    }

    public String getPayeename() {
        return payeename;
    }

    public void setPayeename(String payeename) {
        this.payeename = payeename;
    }

    public String getReasonofpayment() {
        return reasonofpayment;
    }

    public void setReasonofpayment(String reasonofpayment) {
        this.reasonofpayment = reasonofpayment;
    }

    public String getPrepaidcomeoncardcompany() {
        return prepaidcomeoncardcompany;
    }

    public void setPrepaidcomeoncardcompany(String prepaidcomeoncardcompany) {
        this.prepaidcomeoncardcompany = prepaidcomeoncardcompany;
    }

    public String getComeonkaka() {
        return comeonkaka;
    }

    public void setComeonkaka(String comeonkaka) {
        this.comeonkaka = comeonkaka;
    }

    public String getMercid() {
        return mercid;
    }

    public void setMercid(String mercid) {
        this.mercid = mercid;
    }

    public String getPoundage() {
        return poundage;
    }

    public void setPoundage(String poundage) {
        this.poundage = poundage;
    }

    public String getFactamount() {
        return factamount;
    }

    public void setFactamount(String factamount) {
        this.factamount = factamount;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getResponsestate() {
        return responsestate;
    }

    public void setResponsestate(String responsestate) {
        this.responsestate = responsestate;
    }

    public String getPaymentcode() {
		return paymentcode;
	}

	public void setPaymentcode(String paymentcode) {
		this.paymentcode = paymentcode;
	}

	public String getTradetypecode() {
		return tradetypecode;
	}

	public void setTradetypecode(String tradetypecode) {
		this.tradetypecode = tradetypecode;
	}

	public String getBankno() {
		return bankno;
	}

	public void setBankno(String bankno) {
		this.bankno = bankno;
	}

	public String getCreditcardnumber() {
        return creditcardnumber;
    }

    public void setCreditcardnumber(String creditcardnumber) {
        this.creditcardnumber = creditcardnumber;
    }

    public String getPortorderid() {
        return portorderid;
    }

    public void setPortorderid(String portorderid) {
        this.portorderid = portorderid;
    }

    public String getFinishtime() {
        return finishtime;
    }

    public void setFinishtime(String finishtime) {
        this.finishtime = finishtime;
    }

    public String getOrderamount() {
        return orderamount;
    }

    public void setOrderamount(String orderamount) {
        this.orderamount = orderamount;
    }

    public String getRate() {
        return rate;
    }

    public void setRate(String rate) {
        this.rate = rate;
    }

    public String getResultCode() {
        return resultCode;
    }

    public void setResultCode(String resultCode) {
        this.resultCode = resultCode;
    }

    public String getDrawMoneyType() {
        return drawMoneyType;
    }

    public void setDrawMoneyType(String drawMoneyType) {
        this.drawMoneyType = drawMoneyType;
    }

    public String getThirdPartResultCode() {
        return thirdPartResultCode;
    }

    public void setThirdPartResultCode(String thirdPartResultCode) {
        this.thirdPartResultCode = thirdPartResultCode;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getPersonalPay() {
        return personalPay;
    }

    public void setPersonalPay(String personalPay) {
        this.personalPay = personalPay;
    }

    public String getBrushType() {
		return brushType;
	}

	public void setBrushType(String brushType) {
		this.brushType = brushType;
	}

	public String getSnNO() {
		return snNO;
	}

	public void setSnNO(String snNO) {
		this.snNO = snNO;
	}


    public String getAccountingFlag() {
		return accountingFlag;
	}

	public void setAccountingFlag(String accountingFlag) {
		this.accountingFlag = accountingFlag;
	}

	public String getSerialNo() {
		return serialNo;
	}

	public void setSerialNo(String serialNo) {
		this.serialNo = serialNo;
	}

    public String getBusinessNum() {
        return businessNum;
    }

    public void setBusinessNum(String businessNum) {
        this.businessNum = businessNum;
    }

    public String getChannelNum() {
        return channelNum;
    }

    public void setChannelNum(String channelNum) {
        this.channelNum = channelNum;
    }

	public String getSearchNum() {
		return searchNum;
	}

	public void setSearchNum(String searchNum) {
		this.searchNum = searchNum;
	}

    public String getoAgentNo() {
        return oAgentNo;
    }

    public void setoAgentNo(String oAgentNo) {
        this.oAgentNo = oAgentNo;
    }

    public String getAuthPath() {
        return authPath;
    }

    public void setAuthPath(String authPath) {
        this.authPath = authPath;
    }

	public String getSettlepoundage() {
		return settlepoundage;
	}

	public void setSettlepoundage(String settlepoundage) {
		this.settlepoundage = settlepoundage;
	}

	public String getTotalpoundage() {
		return totalpoundage;
	}

	public void setTotalpoundage(String totalpoundage) {
		this.totalpoundage = totalpoundage;
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
	public String getSettlementState() {
		return SettlementState;
	}

	public void setSettlementState(String settlementState) {
		SettlementState = settlementState;
	}

	public String getSettlementPeriod() {
		return SettlementPeriod;
	}

	public void setSettlementPeriod(String settlementPeriod) {
		SettlementPeriod = settlementPeriod;
	}
	
	public String getCertNo() {
		return certNo;
	}

	public void setCertNo(String certNo) {
		this.certNo = certNo;
	}

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}

	@Override
	public String toString() {
		return "PmsAppTransInfo [id=" + id + ", paymenttype=" + paymenttype
				+ ", paymentcode=" + paymentcode + ", tradetype=" + tradetype
				+ ", tradetypecode=" + tradetypecode + ", tradetime="
				+ tradetime + ", orderid=" + orderid + ", phonenumbertype="
				+ phonenumbertype + ", payamount=" + payamount
				+ ", prepaidphonenumber=" + prepaidphonenumber + ", amount="
				+ amount + ", bankno=" + bankno + ", bankcardname="
				+ bankcardname + ", bankname=" + bankname + ", mercname="
				+ mercname + ", shortbankcardnumber=" + shortbankcardnumber
				+ ", payeename=" + payeename + ", reasonofpayment="
				+ reasonofpayment + ", prepaidcomeoncardcompany="
				+ prepaidcomeoncardcompany + ", comeonkaka=" + comeonkaka
				+ ", mercid=" + mercid + ", poundage=" + poundage
				+ ", factamount=" + factamount + ", url=" + url
				+ ", responsestate=" + responsestate + ", creditcardnumber="
				+ creditcardnumber + ", portorderid=" + portorderid
				+ ", finishtime=" + finishtime + ", orderamount=" + orderamount
				+ ", rate=" + rate + ", resultCode=" + resultCode
				+ ", drawMoneyType=" + drawMoneyType + ", thirdPartResultCode="
				+ thirdPartResultCode + ", status=" + status + ", personalPay="
				+ personalPay + ", brushType=" + brushType + ", snNO=" + snNO
				+ ", accountingFlag=" + accountingFlag + ", serialNo="
				+ serialNo + ", businessNum=" + businessNum + ", channelNum="
				+ channelNum + ", searchNum=" + searchNum + ", oAgentNo="
				+ oAgentNo + ", authPath=" + authPath + ", altLat=" + altLat
				+ ", gpsAddress=" + gpsAddress + ", SettlementState="
				+ SettlementState + ", SettlementPeriod=" + SettlementPeriod
				+ ", settlepoundage=" + settlepoundage + ", totalpoundage="
				+ totalpoundage + "]";
	}
    
}