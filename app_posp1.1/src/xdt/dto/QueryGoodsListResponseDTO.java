package xdt.dto;

import xdt.util.PageView;

/**
 * 商品列表查看接口响应
 * 
 * @author lev12
 * 
 */
public class QueryGoodsListResponseDTO {

	private Integer retCode;// 操作返回代码

	private String retMessage;// 返回码信息 0成功1 失败100 系统异常

	private PageView pageView;// 商品分页列表

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

	public PageView getPageView() {
		return pageView;
	}

	public void setPageView(PageView pageView) {
		this.pageView = pageView;
	}

}
