package xdt.quickpay.ysb.model;

import java.io.Serializable;

public class CallBack implements Serializable{
	
	private static final long serialVersionUID = 1571781512832614652L;
	  private String accountId;
	  private String result_code;
	  private String result_msg;
	  private String amount;
	  private String orderId;
	  private String key;
	  private String mac;
	  
	  public CallBack() {}
	  
	  public CallBack(String resultCode, String resultMsg, String amount, String orderId, String key)
	  {
	    this.result_code = resultCode;
	    this.result_msg = resultMsg;
	    this.amount = amount;
	    this.orderId = orderId;
	    this.key = key;
	  }
	  
	  public String getAccountId()
	  {
	    return this.accountId;
	  }
	  
	  public void setAccountId(String accountId)
	  {
	    this.accountId = accountId;
	  }
	  
	  public String getResult_code()
	  {
	    return this.result_code;
	  }
	  
	  public void setResult_code(String result_code)
	  {
	    this.result_code = result_code;
	  }
	  
	  public String getResult_msg()
	  {
	    return this.result_msg;
	  }
	  
	  public void setResult_msg(String result_msg)
	  {
	    this.result_msg = result_msg;
	  }
	  
	  public String getAmount()
	  {
	    return this.amount;
	  }
	  
	  public void setAmount(String amount)
	  {
	    this.amount = amount;
	  }
	  
	  public String getOrderId()
	  {
	    return this.orderId;
	  }
	  
	  public void setOrderId(String orderId)
	  {
	    this.orderId = orderId;
	  }
	  
	  public String getKey()
	  {
	    return this.key;
	  }
	  
	  public void setKey(String key)
	  {
	    this.key = key;
	  }
	  
	  public String getMac()
	  {
	    return this.mac;
	  }
	  
	  public void setMac(String mac)
	  {
	    this.mac = mac;
	  }
	  
	  public String toString()
	  {
	    return "DcCallBack [resultCode=" + this.result_code + ", resultMsg=" + this.result_msg + ", amount=" + this.amount + ", orderId=" + this.orderId + ", key=" + this.key + ", mac=" + this.mac + "]";
	  }

}
