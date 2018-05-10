package xdt.dto;

/**
 * 调用讯联生成二维码返回
 * User: Jeff
 * Date: 15-6-19
 * Time: 下午4:38
 * To change this template use File | Settings | File Templates.
 */
public class XunlianGeneral2DecimalResponseDTO {

    private String retCode;// 信息编号 返回码

    private String retMessage;// 信息描述 返回信息

    private String payAmount; //支付金额

    private String orderNumber; //订单

    private String twoDecimal;//二维码信息
    
    private String serialNo;//批次号
    
    private String orderStatus;//讯联订单处理状态
    
    private String tradeTime;//讯联订单交易时间
    
    private String searchNum;//讯联检索参考号

    private String busInfo;//通道商户标号

    private String busPos;//通道pos终端号


    public String getRetCode() {
        return retCode;
    }

    public void setRetCode(String retCode) {
        this.retCode = retCode;
    }

    public String getRetMessage() {
        return retMessage;
    }

    public void setRetMessage(String retMessage) {
        this.retMessage = retMessage;
    }

    public String getPayAmount() {
        return payAmount;
    }

    public void setPayAmount(String payAmount) {
        this.payAmount = payAmount;
    }

    public String getOrderNumber() {
        return orderNumber;
    }

    public void setOrderNumber(String orderNumber) {
        this.orderNumber = orderNumber;
    }

    public String getTwoDecimal() {
        return twoDecimal;
    }

    public void setTwoDecimal(String twoDecimal) {
        this.twoDecimal = twoDecimal;
    }

	public String getSerialNo() {
		return serialNo;
	}

	public void setSerialNo(String serialNo) {
		this.serialNo = serialNo;
	}

	public String getOrderStatus() {
		return orderStatus;
	}

	public void setOrderStatus(String orderStatus) {
		this.orderStatus = orderStatus;
	}

	public String getTradeTime() {
		return tradeTime;
	}

	public void setTradeTime(String tradeTime) {
		this.tradeTime = tradeTime;
	}

	public String getSearchNum() {
		return searchNum;
	}

	public void setSearchNum(String searchNum) {
		this.searchNum = searchNum;
	}

    public String getBusInfo() {
        return busInfo;
    }

    public void setBusInfo(String busInfo) {
        this.busInfo = busInfo;
    }

    public String getBusPos() {
        return busPos;
    }

    public void setBusPos(String busPos) {
        this.busPos = busPos;
    }
}
