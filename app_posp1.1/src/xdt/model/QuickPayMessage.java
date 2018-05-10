package xdt.model;

/**
 * Created with IntelliJ IDEA.
 * User: Jeff
 * Date: 16-3-17
 * Time: 下午1:14
 * To change this template use File | Settings | File Templates.
 */
public class QuickPayMessage {
    String orderId;
    String message;

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
