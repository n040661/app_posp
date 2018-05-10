package xdt.dto;

import java.util.List;

import xdt.model.Card;

/**
 * 水煤电商品信息查询接口响应
 * 
 * @author lev12
 * 
 */
public class QueryClassIdResponseDTO {

	private Integer retCode;// 操作返回代码，1成功,err_msg为空，其它数字具体错误在err_msg返回

	private String retMessage;// 错误描述，如请求得到正确返回，此处将为空

	private List<Card> cards;// 商品信息列表

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

	public List<Card> getCards() {
		return cards;
	}

	public void setCards(List<Card> cards) {
		this.cards = cards;
	}

}
