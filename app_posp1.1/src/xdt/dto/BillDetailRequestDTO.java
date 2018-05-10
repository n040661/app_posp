package xdt.dto;

/**
 * 单个账单详情相应请求
 * User: Jeff
 * Date: 15-5-22
 * Time: 下午8:55
 * To change this template use File | Settings | File Templates.
 */
public class BillDetailRequestDTO {
    String orderId; //订单号

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }
}
