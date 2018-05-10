package xdt.dto;

import xdt.model.PmsPhoneProducts;

import java.util.List;

/**
 * 手机充值产品查询响应接口
 * @author Jeff
 *
 */
public class PrepaidPhoneProductQueryResponseDTO {
	
	private String inprice; //价格

    private Integer retCode;//是否成功
    
    private String retMessage;//信息描述


    public String getInprice() {
        return inprice;
    }

    public void setInprice(String inprice) {
        this.inprice = inprice;
    }

    public Integer getRetCode() {
		return retCode;
	}

	public void setRetCode(Integer retCode) {
		this.retCode = retCode;
	}

	public String getRetMessage() {
		return retMessage;
	}

	public void setRetMessage(String retMessage) {
		this.retMessage = retMessage;
	}
}
