package xdt.dto;
/**
 * 手机充值产品查询请求接口
 * @author Jeff
 *
 */
public class PrepaidPhoneProductQueryRequestDTO {
	

    private String mobilePhone;//充值手机号

    private String price; //充值金额

    public String getMobilePhone() {
        return mobilePhone;
    }

    public void setMobilePhone(String mobilePhone) {
        this.mobilePhone = mobilePhone;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }
}
