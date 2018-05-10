package xdt.dto;

/**
 * 银行列表查询请求接口
 * @author lev12
 *
 */
public class SearchSupportBankListRequestDTO {

    private String pageNum; //当前页
    
    private String pageSize; //显示条数

	public String getPageNum() {
		return pageNum;
	}

	public void setPageNum(String pageNum) {
		this.pageNum = pageNum;
	}

	public String getPageSize() {
		return pageSize;
	}

	public void setPageSize(String pageSize) {
		this.pageSize = pageSize;
	}
}
