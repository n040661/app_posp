package xdt.dto;

/**
 * 手机充值生成订单返回类
 * User: Jeff
 * Date: 15-5-25
 * Time: 下午4:06
 * To change this template use File | Settings | File Templates.
 */
public class GeneralMobileOrderResponseDTO {

    private Integer retCode;// 操作返回代码，1成功,err_msg为空，其它数字具体错误在err_msg返回

    private String retMessage;// 错误描述，如请求得到正确返回，此处将为空

    private String orderNumber;// 订单号

    private String pageUrl;//返回的URL，供skd调用的

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

    public String getOrderNumber() {
        return orderNumber;
    }

    public void setOrderNumber(String orderNumber) {
        this.orderNumber = orderNumber;
    }

    public String getPageUrl() {
        return pageUrl;
    }

    public void setPageUrl(String pageUrl) {
        this.pageUrl = pageUrl;
    }
}
