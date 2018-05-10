package xdt.dto;
/**
 * 手机充值号段查询响应接口
 * @author xiaomei
 *
 */
public class PrepaidPhoneThemRoughlyQueryResponseDTO {
	
	private String isptype; //运营商名称 （移动、联通、电信）
	
	private String area;//地域
	
	private String detail; //描述
	
    private Integer retCode;//是否成功
    
    private String retMessage;//信息描述
    
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

	public String getIsptype() {
		return isptype;
	}

	public void setIsptype(String isptype) {
		this.isptype = isptype;
	}

    public String getArea() {
        return area;
    }

    public void setArea(String area) {
        this.area = area;
    }

    public String getDetail() {
		return detail;
	}

	public void setDetail(String detail) {
		this.detail = detail;
	}
}