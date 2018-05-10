package xdt.dto;
/**
 * 银行卡历史记录列表请求接口
 * @author xiaomei
 */
public class BankCardHistoryRecordListRequestDTO {
	
    private Integer businessCode; //业务码  1:信用卡 2:提款
    
	public Integer getBusinessCode() {
		return businessCode;
	}

	public void setBusinessCode(Integer businessCode) {
		this.businessCode = businessCode;
	}

}
