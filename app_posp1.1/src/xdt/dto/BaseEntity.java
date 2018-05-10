package xdt.dto;

public class BaseEntity {

	private String url;//主路径

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	@Override
	public String toString() {
		return "BaseEntity [url=" + url + "]";
		
		
	}
	
	
}
