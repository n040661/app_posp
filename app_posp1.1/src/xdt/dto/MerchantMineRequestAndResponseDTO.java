package xdt.dto;

import java.util.Map;

import xdt.model.PmsAppBusinessConfig;


/**
 * 商户个人账户信息
 * @author wumeng  20150525
 */
public class MerchantMineRequestAndResponseDTO {

	
	//请求使用
	private String mobilePhone;// 手机号
	
	
	
    //返回使用
	private String retCode ;//  返回码
	              
	private String retMessage;//  返回码信息      0查询成功      1 查询失败       100 系统异常
	              
	private String merchantName; //商户名
	              
	private String merchantNo;// 商户号
	              
	private String headImage;//商户头像图片
	private String rate ;//  商户收款费率
	
	//手机号和请求公用
	
	private String accBalanceAmt;//	账户余额
	
	private String attestationSign;//实民认证标记，1末认证，2已认证

    private String isRead;//是否有末读消息标记（0：有末读消息；1：已全部读过）
    
    private Map<String,PmsAppBusinessConfig> map; // 业务配置列表
    
	public Map<String, PmsAppBusinessConfig> getMap() {
		return map;
	}

	public void setMap(Map<String, PmsAppBusinessConfig> map) {
		this.map = map;
	}

    public String getRead() {
        return isRead;
    }

    public void setRead(String read) {
        isRead = read;
    }

    public String getMobilePhone() {
		return mobilePhone;
	}

	public void setMobilePhone(String mobilePhone) {
		this.mobilePhone = mobilePhone;
	}

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

	public String getMerchantName() {
		return merchantName;
	}

	public void setMerchantName(String merchantName) {
		this.merchantName = merchantName;
	}

	public String getMerchantNo() {
		return merchantNo;
	}

	public void setMerchantNo(String merchantNo) {
		this.merchantNo = merchantNo;
	}

	public String getHeadImage() {
		return headImage;
	}

	public void setHeadImage(String headImage) {
		this.headImage = headImage;
	}

	public String getAccBalanceAmt() {
		return accBalanceAmt;
	}

	public void setAccBalanceAmt(String accBalanceAmt) {
		this.accBalanceAmt = accBalanceAmt;
	}

	public String getAttestationSign() {
		return attestationSign;
	}

	public void setAttestationSign(String attestationSign) {
		this.attestationSign = attestationSign;
	}

	public String getRate() {
		return rate;
	}

	public void setRate(String rate) {
		this.rate = rate;
	}
    
	    
	
}   
                  