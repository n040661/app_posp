package xdt.dto;

import xdt.model.TAccRate;

import java.util.ArrayList;
import java.util.List;


/**
 * 商户费用率查询（商品表 T_ACC_RATE）
 * @author p
 *
 */
public class TAccRateResponseDTO {
    private Integer retCode;// 信息编号
	
	private String retMessage;// 信息描述
	
	private List<TAccRate> li=new ArrayList<TAccRate>();

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

	public List<TAccRate> getLi() {
		return li;
	}

	public void setLi(List<TAccRate> li) {
		this.li = li;
	}
}
