package xdt.dto;

/**
 * @author lev12
 * 
 */
public class PageViewRequestDTO {

	Integer pageNum;// 当前页
	Integer pageSize;// 每页显示的记录数量(默认每页10条)

	public Integer getPageNum() {
		return pageNum;
	}

	public void setPageNum(Integer pageNum) {
		this.pageNum = pageNum;
	}

	public Integer getPageSize() {
		return pageSize;
	}

	public void setPageSize(Integer pageSize) {
		this.pageSize = pageSize;
	}

}
