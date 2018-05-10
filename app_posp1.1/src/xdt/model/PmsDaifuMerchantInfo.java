package xdt.model;
/**
* ********************************************************
* @ClassName: PmsDaifuMerchantInfo
* @Description: 代付信息表
* @author 用wzl写的自动生成
* @date 2017-04-12 上午 09:38:22 
*******************************************************
*/
public class PmsDaifuMerchantInfo {

    private String mercId;//下游商户号

    private String count;//代付总笔数

    private String amount;//代付总金额

    private String batchNo;//订单号

    private String cardno;//银行卡号

    private String realname;//开卡人姓名

    private String province;//省份

    private String city;//城市

    private String payamount;//交易金额

    private String identity;//客户标识

    private String pmsbankno;//联行号
    
    private String creationdate;//创建时间
    
    private String responsecode;//代付状态
    
    private String oagentno;//代理商
    
    private String payCounter;//代付手续费
    
    private String position; //余额
    
    private String transactionType;//标准快捷
    
    private String remarks;//备注
    private String recordDescription;//记录描述
    
    private String agentnumber;
    private String agentname;
    
    
    
    public String getAgentnumber() {
		return agentnumber;
	}

	public void setAgentnumber(String agentnumber) {
		this.agentnumber = agentnumber;
	}

	public String getAgentname() {
		return agentname;
	}

	public void setAgentname(String agentname) {
		this.agentname = agentname;
	}

	public String getRemarks() {
		return remarks;
	}

	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}

	public String getRecordDescription() {
		return recordDescription;
	}

	public void setRecordDescription(String recordDescription) {
		this.recordDescription = recordDescription;
	}

	public String getMercId() {
        return mercId;
    }

    public void setMercId(String mercId) {
        this.mercId = mercId == null ? null : mercId.trim();
    }

    public String getCount() {
        return count;
    }

    public void setCount(String count) {
        this.count = count == null ? null : count.trim();
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount == null ? null : amount.trim();
    }

    public String getBatchNo() {
        return batchNo;
    }

    public void setBatchNo(String batchNo) {
        this.batchNo = batchNo == null ? null : batchNo.trim();
    }

    public String getCardno() {
        return cardno;
    }

    public void setCardno(String cardno) {
        this.cardno = cardno == null ? null : cardno.trim();
    }

    public String getRealname() {
        return realname;
    }

    public void setRealname(String realname) {
        this.realname = realname == null ? null : realname.trim();
    }

    public String getProvince() {
        return province;
    }

    public void setProvince(String province) {
        this.province = province == null ? null : province.trim();
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city == null ? null : city.trim();
    }

    public String getPayamount() {
        return payamount;
    }

    public void setPayamount(String payamount) {
        this.payamount = payamount == null ? null : payamount.trim();
    }

    public String getIdentity() {
        return identity;
    }

    public void setIdentity(String identity) {
        this.identity = identity == null ? null : identity.trim();
    }

    public String getPmsbankno() {
        return pmsbankno;
    }

    public void setPmsbankno(String pmsbankno) {
        this.pmsbankno = pmsbankno == null ? null : pmsbankno.trim();
    }

	public String getCreationdate() {
		return creationdate;
	}

	public void setCreationdate(String creationdate) {
		this.creationdate = creationdate;
	}

	public String getResponsecode() {
		return responsecode;
	}

	public void setResponsecode(String responsecode) {
		this.responsecode = responsecode;
	}

	public String getOagentno() {
		return oagentno;
	}

	public void setOagentno(String oagentno) {
		this.oagentno = oagentno;
	}

	public String getPayCounter() {
		return payCounter;
	}

	public void setPayCounter(String payCounter) {
		this.payCounter = payCounter;
	}

	public String getPosition() {
		return position;
	}

	public void setPosition(String position) {
		this.position = position;
	}

	public String getTransactionType() {
		return transactionType;
	}

	public void setTransactionType(String transactionType) {
		this.transactionType = transactionType;
	}
	
    
}