package xdt.model;

import java.math.BigDecimal;

public class PmsMessage {

    public static final Integer REGISER=0;
    public static final Integer FINDPASS=1;
    public static final Integer UPPASS=2;
    public static final Integer ADDSELLER=3;
    public static final Integer UPSELLER=4;
    public static final Integer DRAWMONEY=5;//t+0提现使用
    public static final Integer QUICKPAYPRE = 6;//快捷支付预消费
    public static final Integer OTHRE=7;


    private BigDecimal id; //ID

    private String phoneNumber; //手机号

    private String context; //参数值

    private BigDecimal interfaceId; //暂时设置为1,发起请求平台编号

    private String response; //响应状态

    private String spnumber; //短信服务号

    private String sendtime; //接收端收到时间

    private String state; //状态值：S0S|发送成功,SNT|失败：发送超时,SNA|失败：发送未到达，SNB|失败：接收端网络忙，S0F|失败：提交运营商失败 其他错误描述|失败

    private String messagegb; //上行内容

    private String omessage; //对应的下行内容 (可为空)

    private String rectime; //回复时间

    private BigDecimal searchId; //检索发送的短信状态及回复内容
    
    private String reqtime; //请求时间
    
    private String requestNumber; //请求编号
    
    private BigDecimal failure; //失效 0 是 1 否

    private Integer msgType;//发送短信的类型 0:注册 1：找回没密码 2：修改密码 3：添加收银员 4：修改收银员 5：提现 6：快捷支付 7：其他
   
    private String oAgentNo; //欧单编号

    private String orderId;//对应订单号，快捷支付使用

	public BigDecimal getFailure() {
		return failure;
	}

	public void setFailure(BigDecimal failure) {
		this.failure = failure;
	}

	public BigDecimal getId() {
        return id;
    }

    public void setId(BigDecimal id) {
        this.id = id;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber == null ? null : phoneNumber.trim();
    }

    public String getContext() {
        return context;
    }

    public void setContext(String context) {
        this.context = context == null ? null : context.trim();
    }

    public BigDecimal getInterfaceId() {
        return interfaceId;
    }

    public void setInterfaceId(BigDecimal interfaceId) {
        this.interfaceId = interfaceId;
    }

    public String getResponse() {
        return response;
    }

    public void setResponse(String response) {
        this.response = response == null ? null : response.trim();
    }

    public String getSpnumber() {
        return spnumber;
    }

    public void setSpnumber(String spnumber) {
        this.spnumber = spnumber == null ? null : spnumber.trim();
    }

    public String getSendtime() {
        return sendtime;
    }

    public void setSendtime(String sendtime) {
        this.sendtime = sendtime == null ? null : sendtime.trim();
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state == null ? null : state.trim();
    }

    public String getMessagegb() {
        return messagegb;
    }

    public void setMessagegb(String messagegb) {
        this.messagegb = messagegb == null ? null : messagegb.trim();
    }

    public String getOmessage() {
        return omessage;
    }

    public void setOmessage(String omessage) {
        this.omessage = omessage == null ? null : omessage.trim();
    }

    public String getRectime() {
        return rectime;
    }

    public void setRectime(String rectime) {
        this.rectime = rectime == null ? null : rectime.trim();
    }

    public BigDecimal getSearchId() {
        return searchId;
    }

    public void setSearchId(BigDecimal searchId) {
        this.searchId = searchId;
    }

	public String getReqtime() {
		return reqtime;
	}

	public void setReqtime(String reqtime) {
		this.reqtime = reqtime == null ? null : reqtime.trim();
	}

	public String getRequestNumber() {
		return requestNumber;
	}

	public void setRequestNumber(String requestNumber) {
		this.requestNumber = requestNumber == null ? null : requestNumber.trim();
	}

    public Integer getMsgType() {
        return msgType;
    }

    public void setMsgType(Integer msgType) {
        this.msgType = msgType;
    }

	public String getoAgentNo() {
		return oAgentNo;
	}

	public void setoAgentNo(String oAgentNo) {
		this.oAgentNo = oAgentNo;
	}

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }
}